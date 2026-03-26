package org.trs.therepairsystem.service;

import org.springframework.web.multipart.MultipartFile;
import org.trs.therepairsystem.common.enums.AttachmentType;
import org.trs.therepairsystem.dto.response.RepairOrderAttachmentResponse;
import org.trs.therepairsystem.entity.RepairOrderAttachment;

import java.util.List;

public interface RepairOrderAttachmentService {

    RepairOrderAttachmentResponse uploadAttachment(Long requesterId,
                                                   boolean isAdmin,
                                                   Long orderId,
                                                   MultipartFile file,
                                                   AttachmentType attachmentType);

    List<RepairOrderAttachmentResponse> listAttachments(Long requesterId,
                                                        boolean isAdmin,
                                                        Long orderId);

    AttachmentDownloadInfo getAttachmentForDownload(Long requesterId,
                                                    boolean isAdmin,
                                                    Long orderId,
                                                    Long attachmentId);

    void deleteAttachment(Long requesterId,
                          boolean isAdmin,
                          Long orderId,
                          Long attachmentId);

    record AttachmentDownloadInfo(RepairOrderAttachment attachment, byte[] content) {
    }
}
