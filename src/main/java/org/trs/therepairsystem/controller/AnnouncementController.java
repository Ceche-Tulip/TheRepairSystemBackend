package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.dto.request.announcement.AnnouncementCreateRequest;
import org.trs.therepairsystem.dto.request.announcement.AnnouncementUpdateRequest;
import org.trs.therepairsystem.dto.response.AnnouncementDTO;
import org.trs.therepairsystem.entity.Announcement;
import org.trs.therepairsystem.security.CustomUserDetails;
import org.trs.therepairsystem.service.AnnouncementService;
import org.trs.therepairsystem.web.converter.AnnouncementConverter;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "公告管理", description = "系统公告发布与管理")
@Validated
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @Operation(summary = "发布公告", description = "管理员发布系统公告")
    @PreAuthorize("hasRole('ADMIN')")
    public AnnouncementDTO createAnnouncement(
            @RequestBody @Validated AnnouncementCreateRequest request,
            Authentication authentication
    ) {
        // 从认证对象中获取用户ID
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long adminId = userDetails.getUser().getId();
        
        Announcement announcement = announcementService.createAnnouncement(request, adminId);
        return AnnouncementConverter.toDTO(announcement);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新公告", description = "管理员更新公告内容")
    @PreAuthorize("hasRole('ADMIN')")
    public AnnouncementDTO updateAnnouncement(
            @Parameter(description = "公告ID", example = "1") @PathVariable Long id,
            @RequestBody @Validated AnnouncementUpdateRequest request,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long adminId = userDetails.getUser().getId();
        
        Announcement announcement = announcementService.updateAnnouncement(id, request, adminId);
        return AnnouncementConverter.toDTO(announcement);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告", description = "管理员删除公告")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAnnouncement(
            @Parameter(description = "公告ID", example = "1") @PathVariable Long id
    ) {
        announcementService.deleteAnnouncement(id);
        return "公告删除成功";
    }

    @GetMapping
    @Operation(summary = "分页查询公告", description = "查询所有公告列表，支持分页")
    public Page<AnnouncementDTO> getAnnouncements(
            @Parameter(description = "页码", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return announcementService.getAnnouncements(pageable);
    }

    @GetMapping("/published")
    @Operation(summary = "查询已发布公告", description = "获取所有已发布状态的公告列表")
    public List<AnnouncementDTO> getPublishedAnnouncements() {
        return announcementService.getPublishedAnnouncements();
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "发布公告", description = "将草稿状态的公告发布")
    @PreAuthorize("hasRole('ADMIN')")
    public AnnouncementDTO publishAnnouncement(
            @Parameter(description = "公告ID", example = "1") @PathVariable Long id,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return announcementService.publishAnnouncement(id, userDetails.getUser().getId());
    }

    @PutMapping("/{id}/unpublish")
    @Operation(summary = "下架公告", description = "将已发布的公告下架")
    @PreAuthorize("hasRole('ADMIN')")
    public AnnouncementDTO unpublishAnnouncement(
            @Parameter(description = "公告ID", example = "1") @PathVariable Long id,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return announcementService.unpublishAnnouncement(id, userDetails.getUser().getId());
    }

    @GetMapping("/top")
    @Operation(summary = "查询置顶公告", description = "获取所有置顶公告")
    public List<AnnouncementDTO> getTopAnnouncements() {
        return announcementService.getTopAnnouncements();
    }

    @GetMapping("/latest")
    @Operation(summary = "查询最新公告", description = "获取最新的几条公告")
    public List<AnnouncementDTO> getLatestAnnouncements(
            @Parameter(description = "数量限制", example = "5") @RequestParam(defaultValue = "5") int limit
    ) {
        return announcementService.getLatestAnnouncements(limit);
    }
}