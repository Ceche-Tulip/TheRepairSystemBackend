package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.trs.therepairsystem.common.enums.AttachmentType;
import org.trs.therepairsystem.dto.response.RepairOrderAttachmentResponse;
import org.trs.therepairsystem.security.CustomUserDetails;
import org.trs.therepairsystem.service.RepairOrderAttachmentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/repair-orders")
@RequiredArgsConstructor
@Tag(name = "工单附件管理", description = "工单图片附件上传、查询、下载、删除")
@SecurityRequirement(name = "Bearer Authentication")
public class RepairOrderAttachmentController {

    private final RepairOrderAttachmentService attachmentService;

    @PostMapping(value = "/{orderId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ENGINEER') or hasRole('ADMIN')")
    @Operation(summary = "上传工单附件", description = "上传工单图片附件。一个工单可有多个附件，也可以没有附件。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功"),
            @ApiResponse(responseCode = "400", description = "参数错误或文件校验失败"),
            @ApiResponse(responseCode = "403", description = "没有权限上传该工单附件"),
            @ApiResponse(responseCode = "404", description = "工单不存在")
    })
        public ResponseEntity<org.trs.therepairsystem.common.dto.ApiResponse<RepairOrderAttachmentResponse>> uploadAttachment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID", example = "1001") @PathVariable Long orderId,
            @Parameter(description = "附件文件") @RequestPart("file") MultipartFile file,
            @Parameter(description = "附件类型", example = "PROBLEM_PHOTO") @RequestParam AttachmentType attachmentType) {

        RepairOrderAttachmentResponse response = attachmentService.uploadAttachment(
                userDetails.getUserId(), isAdmin(userDetails), orderId, file, attachmentType);
                return ResponseEntity.ok(org.trs.therepairsystem.common.dto.ApiResponse.success(response));
    }

    @GetMapping("/{orderId}/attachments")
    @PreAuthorize("hasRole('USER') or hasRole('ENGINEER') or hasRole('ADMIN')")
    @Operation(summary = "查询工单附件列表", description = "查询指定工单的附件列表。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "403", description = "没有权限查看该工单附件"),
            @ApiResponse(responseCode = "404", description = "工单不存在")
    })
        public ResponseEntity<org.trs.therepairsystem.common.dto.ApiResponse<List<RepairOrderAttachmentResponse>>> listAttachments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID", example = "1001") @PathVariable Long orderId) {

        List<RepairOrderAttachmentResponse> response = attachmentService.listAttachments(
                userDetails.getUserId(), isAdmin(userDetails), orderId);
                return ResponseEntity.ok(org.trs.therepairsystem.common.dto.ApiResponse.success(response));
    }

    @GetMapping("/{orderId}/attachments/{attachmentId}/download")
    @PreAuthorize("hasRole('USER') or hasRole('ENGINEER') or hasRole('ADMIN')")
    @Operation(summary = "下载工单附件", description = "下载指定工单附件文件。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "下载成功", content = @Content(schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "403", description = "没有权限下载该工单附件"),
            @ApiResponse(responseCode = "404", description = "工单或附件不存在")
    })
    public ResponseEntity<InputStreamResource> downloadAttachment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID", example = "1001") @PathVariable Long orderId,
            @Parameter(description = "附件ID", example = "10") @PathVariable Long attachmentId) throws IOException {

        RepairOrderAttachmentService.AttachmentDownloadInfo info = attachmentService.getAttachmentForDownload(
                userDetails.getUserId(), isAdmin(userDetails), orderId, attachmentId);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (info.attachment().getContentType() != null) {
            mediaType = MediaType.parseMediaType(info.attachment().getContentType());
        }

        InputStreamResource resource = new InputStreamResource(new ByteArrayResource(info.content()).getInputStream());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + info.attachment().getOriginalFileName() + "\"")
                .contentLength(info.attachment().getFileSize())
                .body(resource);
    }

    @DeleteMapping("/{orderId}/attachments/{attachmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ENGINEER') or hasRole('ADMIN')")
    @Operation(summary = "删除工单附件", description = "删除指定工单附件。仅附件上传者本人或管理员可删除。")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "没有权限删除该附件"),
            @ApiResponse(responseCode = "404", description = "工单或附件不存在")
    })
        public ResponseEntity<org.trs.therepairsystem.common.dto.ApiResponse<Void>> deleteAttachment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID", example = "1001") @PathVariable Long orderId,
            @Parameter(description = "附件ID", example = "10") @PathVariable Long attachmentId) {

        attachmentService.deleteAttachment(userDetails.getUserId(), isAdmin(userDetails), orderId, attachmentId);
                return ResponseEntity.ok(org.trs.therepairsystem.common.dto.ApiResponse.success());
    }

    private boolean isAdmin(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
