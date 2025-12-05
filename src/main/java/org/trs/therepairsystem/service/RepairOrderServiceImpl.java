package org.trs.therepairsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trs.therepairsystem.dto.request.*;
import org.trs.therepairsystem.dto.response.RepairOrderResponse;
import org.trs.therepairsystem.dto.response.RepairOrderStatsResponse;
import org.trs.therepairsystem.dto.response.EngineerResponse;
import org.trs.therepairsystem.entity.*;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;
import org.trs.therepairsystem.repository.*;
import org.trs.therepairsystem.common.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final FaultTypeRepository faultTypeRepository;
    private final EngineerAreaRelRepository engineerAreaRelRepository;
    private final EngineerFaultRelRepository engineerFaultRelRepository;
    private final RepairRatingRepository repairRatingRepository;

    @Override
    public RepairOrderResponse submitOrder(Long userId, RepairOrderSubmitRequest request) {
        // 验证用户存在
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));

        // 验证建筑
        Building building = buildingRepository.findById(request.getBuildingId())
            .orElseThrow(() -> new BusinessException("建筑不存在"));

        // 验证楼层
        Floor floor = floorRepository.findById(request.getFloorId())
            .orElseThrow(() -> new BusinessException("楼层不存在"));

        // 验证楼层属于指定建筑
        if (!floor.getBuilding().getId().equals(request.getBuildingId())) {
            throw new BusinessException("楼层不属于指定建筑");
        }

        // 验证故障类型
        FaultType faultType = faultTypeRepository.findById(request.getFaultTypeId())
            .orElseThrow(() -> new BusinessException("故障类型不存在"));

        // 创建维修工单
        RepairOrder order = RepairOrder.builder()
            .submitUser(user)
            .building(building)
            .floor(floor)
            .faultType(faultType)
            .description(request.getDescription())
            .status(RepairOrderStatus.SUBMITTED) // 提交后状态为已提交
            .createTime(LocalDateTime.now())
            .build();

        RepairOrder savedOrder = repairOrderRepository.save(order);
        
        log.info("用户 {} 提交了维修工单 {}", userId, savedOrder.getId());
        
        // 尝试自动分配工程师
        try {
            autoAssignEngineer(null, savedOrder.getId());
            log.info("工单 {} 自动分配成功", savedOrder.getId());
        } catch (Exception e) {
            log.warn("工单 {} 自动分配失败: {}, 等待管理员手动分配", savedOrder.getId(), e.getMessage());
        }
        
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse saveDraft(Long userId, RepairOrderSubmitRequest request) {
        // 验证用户存在
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));

        // 验证建筑
        Building building = buildingRepository.findById(request.getBuildingId())
            .orElseThrow(() -> new BusinessException("建筑不存在"));

        // 验证楼层
        Floor floor = floorRepository.findById(request.getFloorId())
            .orElseThrow(() -> new BusinessException("楼层不存在"));

        // 验证楼层属于指定建筑
        if (!floor.getBuilding().getId().equals(request.getBuildingId())) {
            throw new BusinessException("楼层不属于指定建筑");
        }

        // 验证故障类型
        FaultType faultType = faultTypeRepository.findById(request.getFaultTypeId())
            .orElseThrow(() -> new BusinessException("故障类型不存在"));

        // 创建维修工单草稿
        RepairOrder order = RepairOrder.builder()
            .submitUser(user)
            .building(building)
            .floor(floor)
            .faultType(faultType)
            .description(request.getDescription())
            .status(RepairOrderStatus.DRAFT) // 草稿状态
            .createTime(LocalDateTime.now())
            .build();

        RepairOrder savedOrder = repairOrderRepository.save(order);
        
        log.info("用户 {} 保存了维修工单草稿 {}", userId, savedOrder.getId());
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse submitDraft(Long userId, Long orderId) {
        // 查找工单
        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));
        
        // 验证工单属于当前用户
        if (!order.getSubmitUser().getId().equals(userId)) {
            throw new BusinessException("无权限操作此工单");
        }
        
        // 验证工单状态是草稿
        if (order.getStatus() != RepairOrderStatus.DRAFT) {
            throw new BusinessException("只能提交草稿状态的工单");
        }
        
        // 更新状态为待处理
        order.setStatus(RepairOrderStatus.PENDING);
        RepairOrder savedOrder = repairOrderRepository.save(order);
        
        log.info("用户 {} 提交了草稿工单 {}", userId, orderId);
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse assignEngineer(Long adminId, Long orderId, RepairOrderAssignRequest request) {
        // 验证管理员权限
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new BusinessException("管理员不存在"));

        // 验证工单存在
        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));

        // 验证工单状态
        if (!order.canAssignEngineer()) {
            throw new BusinessException("当前状态不允许分配工程师");
        }

        // 验证工程师
        User engineer = userRepository.findById(request.getEngineerId())
            .orElseThrow(() -> new BusinessException("工程师不存在"));

        // 验证工程师资质（可以处理该故障类型且负责该区域）
        boolean canHandleFaultType = engineerFaultRelRepository
            .existsByEngineerIdAndFaultTypeId(engineer.getId(), order.getFaultType().getId());
        boolean canHandleArea = engineerAreaRelRepository
            .existsByEngineerIdAndFloorId(engineer.getId(), order.getFloor().getId());

        if (!canHandleFaultType) {
            throw new BusinessException("工程师不具备处理该故障类型的资质");
        }
        if (!canHandleArea) {
            throw new BusinessException("工程师不负责该区域");
        }

        // 分配工程师
        order.setEngineer(engineer);
        order.setAdmin(admin);
        order.setStatus(RepairOrderStatus.PENDING); // 分配成功后状态变为待处理
        RepairOrder savedOrder = repairOrderRepository.save(order);

        log.info("管理员 {} 将工单 {} 分配给工程师 {}", adminId, orderId, request.getEngineerId());
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse autoAssignEngineer(Long adminId, Long orderId) {
        // adminId可以为null，表示系统自动调用
        User admin = null;
        if (adminId != null) {
            admin = userRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        }

        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));

        if (!order.canAssignEngineer()) {
            throw new BusinessException("当前状态不允许分配工程师");
        }

        // 查找可用工程师
        List<Long> availableEngineerIds = repairOrderRepository
            .findAvailableEngineerIds(order.getFaultType().getId(), order.getFloor().getId());

        if (availableEngineerIds.isEmpty()) {
            throw new BusinessException("没有可用的工程师处理此工单");
        }

        // 随机选择一个工程师（实际项目中可能需要更复杂的分配策略）
        Long selectedEngineerId = availableEngineerIds.get(
            new Random().nextInt(availableEngineerIds.size())
        );

        User engineer = userRepository.findById(selectedEngineerId)
            .orElseThrow(() -> new BusinessException("选中的工程师不存在"));

        order.setEngineer(engineer);
        order.setAdmin(admin);
        order.setStatus(RepairOrderStatus.PENDING); // 分配成功后状态变为待处理
        RepairOrder savedOrder = repairOrderRepository.save(order);

        if (adminId != null) {
            log.info("管理员 {} 将工单 {} 分配给工程师 {}", adminId, orderId, selectedEngineerId);
        } else {
            log.info("系统自动将工单 {} 分配给工程师 {}", orderId, selectedEngineerId);
        }
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse acceptOrder(Long engineerId, Long orderId) {
        User engineer = userRepository.findById(engineerId)
            .orElseThrow(() -> new BusinessException("工程师不存在"));

        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));

        // 验证权限
        if (!order.isAssignedTo(engineerId)) {
            throw new BusinessException("此工单未分配给您");
        }

        if (!order.canAccept()) {
            throw new BusinessException("当前状态不允许接受工单");
        }

        // 接受工单
        order.setStatus(RepairOrderStatus.IN_PROGRESS);
        order.setAcceptTime(LocalDateTime.now());
        RepairOrder savedOrder = repairOrderRepository.save(order);

        log.info("工程师 {} 接受了工单 {}", engineerId, orderId);
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse completeOrder(Long engineerId, Long orderId, RepairOrderCompleteRequest request) {
        User engineer = userRepository.findById(engineerId)
            .orElseThrow(() -> new BusinessException("工程师不存在"));

        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));

        if (!order.isAssignedTo(engineerId)) {
            throw new BusinessException("此工单未分配给您");
        }

        if (!order.canComplete()) {
            throw new BusinessException("当前状态不允许完成工单");
        }

        // 完成工单
        order.setStatus(RepairOrderStatus.COMPLETED);
        order.setRepairInfo(request.getRepairInfo());
        order.setFinishTime(LocalDateTime.now());
        RepairOrder savedOrder = repairOrderRepository.save(order);

        log.info("工程师 {} 完成了工单 {}", engineerId, orderId);
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse closeOrderWithRating(Long userId, Long orderId, RepairRatingRequest ratingRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));

        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));

        if (!order.isSubmittedBy(userId)) {
            throw new BusinessException("您没有权限关闭此工单");
        }

        if (order.getStatus() != RepairOrderStatus.COMPLETED) {
            throw new BusinessException("只有已完成的工单才能关闭");
        }

        // 关闭工单
        order.setStatus(RepairOrderStatus.CLOSED);
        RepairOrder savedOrder = repairOrderRepository.save(order);

        // 创建评价
        RepairRating rating = RepairRating.builder()
            .order(savedOrder)
            .rating(ratingRequest.getRating())
            .comment(ratingRequest.getComment())
            .createTime(LocalDateTime.now())
            .build();
        
        repairRatingRepository.save(rating);

        log.info("用户 {} 关闭了工单 {} 并提交了评价", userId, orderId);
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse cancelOrder(Long userId, Long orderId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));

        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));

        if (!order.isSubmittedBy(userId)) {
            throw new BusinessException("您没有权限取消此工单");
        }

        if (!order.canCancel()) {
            throw new BusinessException("当前状态不允许取消工单");
        }

        // 取消工单
        order.setStatus(RepairOrderStatus.CANCELLED);
        RepairOrder savedOrder = repairOrderRepository.save(order);

        log.info("用户 {} 取消了工单 {}", userId, orderId);
        return convertToResponse(savedOrder);
    }

    @Override
    public RepairOrderResponse adminCancelOrder(Long adminId, Long orderId) {
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new BusinessException("管理员不存在"));

        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));

        if (order.getStatus() == RepairOrderStatus.CLOSED || order.getStatus() == RepairOrderStatus.CANCELLED) {
            throw new BusinessException("工单已关闭或取消");
        }

        // 管理员强制取消
        order.setStatus(RepairOrderStatus.CANCELLED);
        order.setAdmin(admin);
        RepairOrder savedOrder = repairOrderRepository.save(order);

        log.info("管理员 {} 强制取消了工单 {}", adminId, orderId);
        return convertToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public RepairOrderResponse getOrderById(Long orderId) {
        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));
        return convertToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepairOrderResponse> getUserOrders(Long userId, RepairOrderStatus status, Pageable pageable) {
        Page<RepairOrder> orders = (status != null) ?
            repairOrderRepository.findByUserIdAndStatus(userId, status, pageable) :
            repairOrderRepository.findBySubmitUserIdOrderByCreateTimeDesc(userId, pageable);
        
        return orders.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepairOrderResponse> getEngineerOrders(Long engineerId, RepairOrderStatus status, Pageable pageable) {
        Page<RepairOrder> orders = (status != null) ?
            repairOrderRepository.findByEngineerIdAndStatus(engineerId, status, pageable) :
            repairOrderRepository.findByEngineerIdOrderByCreateTimeDesc(engineerId, pageable);
        
        return orders.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepairOrderResponse> getAllOrders(RepairOrderQueryRequest request, Pageable pageable) {
        return repairOrderRepository.findByConditions(
            request.getUserId(),
            request.getEngineerId(), 
            request.getStatus(),
            request.getBuildingId(),
            request.getFaultTypeId(),
            pageable
        ).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepairOrderResponse> getAllOrdersSimple(RepairOrderStatus status, Long userId, Long engineerId,
                                                      Long buildingId, Long faultTypeId, Pageable pageable) {
        return repairOrderRepository.findByConditions(
            userId, engineerId, status, buildingId, faultTypeId, pageable
        ).map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepairOrderResponse> getUnassignedOrders(Pageable pageable) {
        return repairOrderRepository.findUnassignedPendingOrders(pageable)
            .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EngineerResponse> getAvailableEngineers(Long faultTypeId, Long floorId) {
        List<Long> engineerIds = repairOrderRepository.findAvailableEngineerIds(faultTypeId, floorId);
        return engineerIds.stream()
            .map(id -> userRepository.findById(id).orElse(null))
            .filter(user -> user != null)
            .map(this::convertUserToEngineerResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RepairOrderStatsResponse getOrderStats() {
        return new RepairOrderStatsResponse(
            repairOrderRepository.count(),
            repairOrderRepository.countByStatus(RepairOrderStatus.DRAFT),
            repairOrderRepository.countByStatus(RepairOrderStatus.PENDING),
            repairOrderRepository.countByStatus(RepairOrderStatus.IN_PROGRESS),
            repairOrderRepository.countByStatus(RepairOrderStatus.COMPLETED),
            repairOrderRepository.countByStatus(RepairOrderStatus.CLOSED),
            repairOrderRepository.countByStatus(RepairOrderStatus.CANCELLED),
            repairOrderRepository.countTodayOrders(),
            repairOrderRepository.countTodayOrdersByStatus(RepairOrderStatus.COMPLETED)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RepairOrderStatsResponse getUserOrderStats(Long userId) {
        return new RepairOrderStatsResponse(
            (long) repairOrderRepository.findBySubmitUserIdOrderByCreateTimeDesc(userId, 
                org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements(),
            repairOrderRepository.countByUserIdAndStatus(userId, RepairOrderStatus.DRAFT),
            repairOrderRepository.countByUserIdAndStatus(userId, RepairOrderStatus.PENDING),
            repairOrderRepository.countByUserIdAndStatus(userId, RepairOrderStatus.IN_PROGRESS),
            repairOrderRepository.countByUserIdAndStatus(userId, RepairOrderStatus.COMPLETED),
            repairOrderRepository.countByUserIdAndStatus(userId, RepairOrderStatus.CLOSED),
            repairOrderRepository.countByUserIdAndStatus(userId, RepairOrderStatus.CANCELLED),
            0, 0 // 用户统计不需要今日数据
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RepairOrderStatsResponse getEngineerOrderStats(Long engineerId) {
        return new RepairOrderStatsResponse(
            (long) repairOrderRepository.findByEngineerIdOrderByCreateTimeDesc(engineerId, 
                org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements(),
            repairOrderRepository.countByEngineerIdAndStatus(engineerId, RepairOrderStatus.DRAFT),
            repairOrderRepository.countByEngineerIdAndStatus(engineerId, RepairOrderStatus.PENDING),
            repairOrderRepository.countByEngineerIdAndStatus(engineerId, RepairOrderStatus.IN_PROGRESS),
            repairOrderRepository.countByEngineerIdAndStatus(engineerId, RepairOrderStatus.COMPLETED),
            repairOrderRepository.countByEngineerIdAndStatus(engineerId, RepairOrderStatus.CLOSED),
            repairOrderRepository.countByEngineerIdAndStatus(engineerId, RepairOrderStatus.CANCELLED),
            0, 0 // 工程师统计不需要今日数据
        );
    }

    @Override
    public RepairOrder updateOrderStatus(Long orderId, RepairOrderStatus status) {
        RepairOrder order = repairOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("工单不存在"));
        
        order.setStatus(status);
        return repairOrderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, Long orderId, String action) {
        RepairOrder order = repairOrderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }

        switch (action) {
            case "view":
                return order.isSubmittedBy(userId) || 
                       (order.getEngineer() != null && order.isAssignedTo(userId));
            case "cancel":
                return order.isSubmittedBy(userId) && order.canCancel();
            case "close":
                return order.isSubmittedBy(userId) && order.getStatus() == RepairOrderStatus.COMPLETED;
            case "accept":
                return order.getEngineer() != null && order.isAssignedTo(userId) && order.canAccept();
            case "complete":
                return order.getEngineer() != null && order.isAssignedTo(userId) && order.canComplete();
            default:
                return false;
        }
    }

    private RepairOrderResponse convertToResponse(RepairOrder order) {
        return RepairOrderResponse.builder()
            .id(order.getId())
            .status(order.getStatus())
            .description(order.getDescription())
            .repairInfo(order.getRepairInfo())
            .createTime(order.getCreateTime())
            .acceptTime(order.getAcceptTime())
            .finishTime(order.getFinishTime())
            .submitUserId(order.getSubmitUser().getId())
            .submitUserName(order.getSubmitUser().getUsername())
            .buildingId(order.getBuilding().getId())
            .buildingName(order.getBuilding().getName())
            .floorId(order.getFloor().getId())
            .floorName(order.getFloor().getName())
            .faultTypeId(order.getFaultType().getId())
            .faultTypeName(order.getFaultType().getName())
            .adminId(order.getAdmin() != null ? order.getAdmin().getId() : null)
            .adminName(order.getAdmin() != null ? order.getAdmin().getUsername() : null)
            .engineerId(order.getEngineer() != null ? order.getEngineer().getId() : null)
            .engineerName(order.getEngineer() != null ? order.getEngineer().getUsername() : null)
            .canAssignEngineer(order.canAssignEngineer())
            .canAccept(order.canAccept())
            .canComplete(order.canComplete())
            .canCancel(order.canCancel())
            .build();
    }

    private EngineerResponse convertUserToEngineerResponse(User user) {
        return EngineerResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .phone(user.getPhone())
            .build();
    }
}