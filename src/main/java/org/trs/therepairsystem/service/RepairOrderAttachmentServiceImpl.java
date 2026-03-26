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

        assertCanAccessOrder(requesterId, isAdmin, orderId);

        if (!isAdmin && !attachment.getUploadedBy().getId().equals(requesterId)) {
            throw new BusinessException("没有权限删除该附件");
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
}
