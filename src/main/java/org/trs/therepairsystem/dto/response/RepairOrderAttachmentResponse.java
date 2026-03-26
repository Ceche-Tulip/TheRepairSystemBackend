package org.trs.therepairsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.trs.therepairsystem.common.enums.AttachmentType;
import org.trs.therepairsystem.common.enums.StorageProvider;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "工单附件响应")
public class RepairOrderAttachmentResponse {

    @Schema(description = "附件ID", example = "1")
    private Long id;

    @Schema(description = "工单ID", example = "1001")
    private Long orderId;

    @Schema(description = "附件类型", example = "PROBLEM_PHOTO")
    private AttachmentType attachmentType;

    @Schema(description = "原始文件名", example = "problem.jpg")
    private String originalFileName;

    @Schema(description = "文件内容类型", example = "image/jpeg")
    private String contentType;

    @Schema(description = "文件大小(字节)", example = "204800")
    private Long fileSize;

    @Schema(description = "上传人ID", example = "2")
    private Long uploadedByUserId;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "存储提供方", example = "LOCAL")
    private StorageProvider storageProvider;

    @Schema(description = "下载地址", example = "/api/repair-orders/1001/attachments/1/download")
    private String downloadUrl;
}
