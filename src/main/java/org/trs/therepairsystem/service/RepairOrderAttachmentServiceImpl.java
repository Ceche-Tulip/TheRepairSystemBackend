package org.trs.therepairsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.trs.therepairsystem.common.enums.AttachmentType;
import org.trs.therepairsystem.common.enums.StorageProvider;
import org.trs.therepairsystem.common.exception.BusinessException;
import org.trs.therepairsystem.config.AttachmentStorageProperties;
import org.trs.therepairsystem.dto.response.RepairOrderAttachmentResponse;
import org.trs.therepairsystem.entity.RepairOrder;
import org.trs.therepairsystem.entity.RepairOrderAttachment;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.repository.RepairOrderAttachmentRepository;
import org.trs.therepairsystem.repository.RepairOrderRepository;
import org.trs.therepairsystem.repository.UserRepository;
import org.trs.therepairsystem.service.storage.AttachmentStorageService;

import org.trs.therepairsystem.common.enums.RepairOrderStatus;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RepairOrderAttachmentServiceImpl implements RepairOrderAttachmentService {

    private final RepairOrderAttachmentRepository attachmentRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final UserRepository userRepository;
    private final AttachmentStorageProperties storageProperties;
    private final List<AttachmentStorageService> storageServices;

    private Map<StorageProvider, AttachmentStorageService> storageServiceByProvider;

    @jakarta.annotation.PostConstruct
    void initStorageServices() {
        Map<StorageProvider, AttachmentStorageService> map = new EnumMap<>(StorageProvider.class);
        for (AttachmentStorageService service : storageServices) {
            map.put(service.provider(), service);
        }
        this.storageServiceByProvider = map;
    }



    @Override
    public RepairOrderAttachmentResponse uploadAttachment(Long requesterId,
                                                          boolean isAdmin,
                                                          Long orderId,
                                                          MultipartFile file,
                                                          AttachmentType attachmentType) {
        RepairOrder order = assertCanAccessOrder(requesterId, isAdmin, orderId);
        
        // 校验状态与类型权限（在文件验证前进行）
        String userRole = getUserRoleForOrder(requesterId, order, isAdmin);
        if (!canUploadByStatusAndType(userRole, order.getStatus(), attachmentType)) {
            throw new BusinessException("STATE_VIOLATION",
                String.format("用户在工单状态 %s 下禁止上传 %s 类型附件", 
                    order.getStatus(), attachmentType));
        }
        
        User uploader = userRepository.findById(requesterId)
                .orElseThrow(() -> new BusinessException("上传用户不存在"));

        validateUpload(file, attachmentType);

        String originalFileName = file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename();
        String ext = getSafeFileExtension(originalFileName);
        String generatedFileName = UUID.randomUUID() + ext;

        String objectKey = String.format("repair-orders/%d/%s/%s",
                orderId,
                attachmentType.name().toLowerCase(Locale.ROOT),
                generatedFileName);

        StorageProvider activeProvider = storageProperties.getStorageProvider();
        resolveStorageService(activeProvider).upload(objectKey, file);

        RepairOrderAttachment attachment = RepairOrderAttachment.builder()
                .order(order)
                .uploadedBy(uploader)
                .attachmentType(attachmentType)
            .storageProvider(activeProvider)
                .originalFileName(originalFileName)
                .objectKey(objectKey)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadTime(LocalDateTime.now())
                .build();

        RepairOrderAttachment saved = attachmentRepository.save(attachment);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrderAttachmentResponse> listAttachments(Long requesterId,
                                                               boolean isAdmin,
                                                               Long orderId) {
        assertCanAccessOrder(requesterId, isAdmin, orderId);
        return attachmentRepository.findByOrderIdOrderByUploadTimeDesc(orderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentDownloadInfo getAttachmentForDownload(Long requesterId,
                                                           boolean isAdmin,
                                                           Long orderId,
                                                           Long attachmentId) {
        assertCanAccessOrder(requesterId, isAdmin, orderId);

        RepairOrderAttachment attachment = attachmentRepository.findByIdAndOrderId(attachmentId, orderId)
                .orElseThrow(() -> new BusinessException("附件不存在"));

        byte[] content = resolveStorageService(attachment.getStorageProvider()).download(attachment.getObjectKey());

        return new AttachmentDownloadInfo(attachment, content);
    }

    @Override
    public void deleteAttachment(Long requesterId,
                                 boolean isAdmin,
                                 Long orderId,
                                 Long attachmentId) {
        RepairOrderAttachment attachment = attachmentRepository.findByIdAndOrderId(attachmentId, orderId)
                .orElseThrow(() -> new BusinessException("附件不存在"));

        RepairOrder order = attachment.getOrder();
        assertCanAccessOrder(requesterId, isAdmin, orderId);

        if (!isAdmin && !attachment.getUploadedBy().getId().equals(requesterId)) {
            throw new BusinessException("没有权限删除该附件");
        }

        // 校验状态权限（在删除前进行）
        String userRole = getUserRoleForOrder(requesterId, order, isAdmin);
        if (!canDeleteByStatus(userRole, order.getStatus())) {
            throw new BusinessException("STATE_VIOLATION",
                String.format("用户在工单状态 %s 下禁止删除附件", order.getStatus()));
        }

        resolveStorageService(attachment.getStorageProvider()).delete(attachment.getObjectKey());

        attachmentRepository.delete(attachment);
    }

    private void validateUpload(MultipartFile file, AttachmentType attachmentType) {
        if (attachmentType == null) {
            throw new BusinessException("附件类型不能为空");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > storageProperties.getMaxFileSizeBytes()) {
            throw new BusinessException("附件大小超出限制");
        }
        String contentType = file.getContentType();
        if (contentType == null || storageProperties.getAllowedContentTypes().stream().noneMatch(allowed -> allowed.trim().equalsIgnoreCase(contentType))) {
            throw new BusinessException("不支持的文件类型，仅支持 jpeg/png/webp");
        }
    }

    private RepairOrder assertCanAccessOrder(Long requesterId, boolean isAdmin, Long orderId) {
        RepairOrder order = repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("工单不存在"));

        if (isAdmin) {
            return order;
        }

        boolean isSubmitter = order.getSubmitUser() != null && requesterId.equals(order.getSubmitUser().getId());
        boolean isEngineer = order.getEngineer() != null && requesterId.equals(order.getEngineer().getId());

        if (!isSubmitter && !isEngineer) {
            throw new BusinessException("没有权限访问该工单附件");
        }

        return order;
    }

    private AttachmentStorageService resolveStorageService(StorageProvider provider) {
        AttachmentStorageService storageService = storageServiceByProvider.get(provider);
        if (storageService == null) {
            throw new BusinessException("未找到存储实现: " + provider);
        }
        return storageService;
    }

    private String getSafeFileExtension(String fileName) {
        String safe = fileName.replace("\\", "_").replace("/", "_");
        int dotIndex = safe.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == safe.length() - 1) {
            return "";
        }
        String ext = safe.substring(dotIndex).toLowerCase(Locale.ROOT);
        if (ext.length() > 10) {
            return "";
        }
        return ext;
    }

    private RepairOrderAttachmentResponse toResponse(RepairOrderAttachment attachment) {
        return RepairOrderAttachmentResponse.builder()
                .id(attachment.getId())
                .orderId(attachment.getOrder().getId())
                .attachmentType(attachment.getAttachmentType())
                .originalFileName(attachment.getOriginalFileName())
                .contentType(attachment.getContentType())
                .fileSize(attachment.getFileSize())
                .uploadedByUserId(attachment.getUploadedBy().getId())
                .uploadTime(attachment.getUploadTime())
                .storageProvider(attachment.getStorageProvider())
                .downloadUrl(String.format("/api/repair-orders/%d/attachments/%d/download",
                        attachment.getOrder().getId(),
                        attachment.getId()))
                .build();
    }

    /**
     * 根据用户与工单关系确定用户角色
     *
     * @param requesterId 请求用户ID
     * @param order 工单对象
     * @param isAdmin 是否管理员
     * @return 用户对本工单的角色：ADMIN / USER / ENGINEER
     */
    private String getUserRoleForOrder(Long requesterId, RepairOrder order, boolean isAdmin) {
        if (isAdmin) {
            return "ADMIN";
        }

        if (order.getSubmitUser() != null && requesterId.equals(order.getSubmitUser().getId())) {
            return "USER";
        }

        if (order.getEngineer() != null && requesterId.equals(order.getEngineer().getId())) {
            return "ENGINEER";
        }

        // 不应能到达此处，因为 assertCanAccessOrder 已校验
        return "UNKNOWN";
    }

    /**
     * 校验指定用户是否可在当前工单状态下上传特定类型的附件
     *
     * @param roleType 用户角色（ADMIN/USER/ENGINEER）
     * @param orderStatus 工单当前状态
     * @param attachmentType 待上传的附件类型
     * @return true 允许，false 拒绝
     */
    private boolean canUploadByStatusAndType(String roleType, RepairOrderStatus orderStatus, AttachmentType attachmentType) {
        // 管理员全权限
        if ("ADMIN".equals(roleType)) {
            return true;
        }

        // 用户规则：DRAFT ~ PENDING 可上传 PROBLEM_PHOTO 和 BEFORE_AFTER
        if ("USER".equals(roleType)) {
            if (attachmentType == AttachmentType.REPAIR_PROOF) {
                return false; // 用户不能上传维修证据
            }
            // 用户在 DRAFT/SUBMITTED/PENDING 阶段可上传问题描述和对比图
            return orderStatus.getCode() <= RepairOrderStatus.PENDING.getCode();
        }

        // 工程师规则：IN_PROGRESS ~ COMPLETED 可上传 REPAIR_PROOF 和 BEFORE_AFTER
        if ("ENGINEER".equals(roleType)) {
            if (attachmentType == AttachmentType.PROBLEM_PHOTO) {
                return false; // 工程师不能上传问题照片
            }
            // 工程师在 IN_PROGRESS/COMPLETED 阶段可上传维修证据和对比图
            return orderStatus.getCode() >= RepairOrderStatus.IN_PROGRESS.getCode()
                    && orderStatus.getCode() <= RepairOrderStatus.COMPLETED.getCode();
        }

        return false;
    }

    /**
     * 校验指定用户是否可在当前工单状态下删除附件
     * 前置条件：已验证用户是附件上传者或管理员
     *
     * @param roleType 用户角色（ADMIN/USER/ENGINEER）
     * @param orderStatus 工单当前状态
     * @return true 允许，false 拒绝
     */
    private boolean canDeleteByStatus(String roleType, RepairOrderStatus orderStatus) {
        // 管理员全权限
        if ("ADMIN".equals(roleType)) {
            return true;
        }

        // 用户只能在接单前（DRAFT ~ PENDING）删除自己的附件
        if ("USER".equals(roleType)) {
            return orderStatus.getCode() <= RepairOrderStatus.PENDING.getCode();
        }

        // 工程师只能在接单后的工作阶段（IN_PROGRESS ~ COMPLETED）删除自己的附件
        if ("ENGINEER".equals(roleType)) {
            return orderStatus.getCode() >= RepairOrderStatus.IN_PROGRESS.getCode()
                    && orderStatus.getCode() <= RepairOrderStatus.COMPLETED.getCode();
        }

        return false;
    }
}

