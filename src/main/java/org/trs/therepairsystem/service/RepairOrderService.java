package org.trs.therepairsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.trs.therepairsystem.dto.request.*;
import org.trs.therepairsystem.dto.response.RepairOrderResponse;
import org.trs.therepairsystem.dto.response.RepairOrderStatsResponse;
import org.trs.therepairsystem.dto.response.EngineerResponse;
import org.trs.therepairsystem.entity.RepairOrder;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;

import java.util.List;

public interface RepairOrderService {

    /**
     * 用户提交维修工单
     */
    RepairOrderResponse submitOrder(Long userId, RepairOrderSubmitRequest request);

    /**
     * 保存为草稿
     */
    RepairOrderResponse saveDraft(Long userId, RepairOrderSubmitRequest request);

    /**
     * 提交草稿工单
     */
    RepairOrderResponse submitDraft(Long userId, Long orderId);

    /**
     * 修改草稿工单
     */
    RepairOrderResponse updateDraft(Long userId, Long orderId, RepairOrderSubmitRequest request);

    /**
     * 管理员分配工程师
     */
    RepairOrderResponse assignEngineer(Long adminId, Long orderId, RepairOrderAssignRequest request);

    /**
     * 自动分配工程师（根据故障类型和区域）
     */
    RepairOrderResponse autoAssignEngineer(Long adminId, Long orderId);

    /**
     * 工程师接受工单
     */
    RepairOrderResponse acceptOrder(Long engineerId, Long orderId);

    /**
     * 工程师完成工单
     */
    RepairOrderResponse completeOrder(Long engineerId, Long orderId, RepairOrderCompleteRequest request);

    /**
     * 用户关闭工单并评价
     */
    RepairOrderResponse closeOrderWithRating(Long userId, Long orderId, RepairRatingRequest ratingRequest);

    /**
     * 取消工单
     */
    RepairOrderResponse cancelOrder(Long userId, Long orderId);

    /**
     * 管理员强制取消工单
     */
    RepairOrderResponse adminCancelOrder(Long adminId, Long orderId);

    /**
     * 根据ID获取工单详情
     */
    RepairOrderResponse getOrderById(Long orderId);

    /**
     * 获取用户的工单列表
     */
    Page<RepairOrderResponse> getUserOrders(Long userId, RepairOrderStatus status, Pageable pageable);

    /**
     * 获取工程师的工单列表
     */
    Page<RepairOrderResponse> getEngineerOrders(Long engineerId, RepairOrderStatus status, Pageable pageable);

    /**
     * 管理员查询所有工单
     */
    Page<RepairOrderResponse> getAllOrders(RepairOrderQueryRequest request, Pageable pageable);

    /**
     * 管理员简化查询所有工单
     */
    Page<RepairOrderResponse> getAllOrdersSimple(RepairOrderStatus status, Long userId, Long engineerId, 
                                                Long buildingId, Long faultTypeId, Pageable pageable);

    /**
     * 获取待分配的工单列表
     */
    Page<RepairOrderResponse> getUnassignedOrders(Pageable pageable);

    /**
     * 获取可分配给指定故障类型和区域的工程师列表
     */
    List<EngineerResponse> getAvailableEngineers(Long faultTypeId, Long floorId);

    /**
     * 获取工单统计信息
     */
    RepairOrderStatsResponse getOrderStats();

    /**
     * 获取用户工单统计
     */
    RepairOrderStatsResponse getUserOrderStats(Long userId);

    /**
     * 获取工程师工单统计
     */
    RepairOrderStatsResponse getEngineerOrderStats(Long engineerId);

    /**
     * 更新工单状态（内部使用）
     */
    RepairOrder updateOrderStatus(Long orderId, RepairOrderStatus status);

    /**
     * 验证工单操作权限
     */
    boolean hasPermission(Long userId, Long orderId, String action);
}