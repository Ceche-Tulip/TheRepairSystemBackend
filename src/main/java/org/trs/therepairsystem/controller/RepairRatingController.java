package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.common.dto.ApiResponse;
import org.trs.therepairsystem.dto.response.RepairRatingResponse;
import org.trs.therepairsystem.security.CustomUserDetails;
import org.trs.therepairsystem.service.RepairRatingService;

@Tag(name = "维修评价管理", description = "维修工单评价相关接口")
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RepairRatingController {

    private final RepairRatingService repairRatingService;

    @Operation(summary = "获取工单评价", description = "根据工单ID获取评价详情")
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ENGINEER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairRatingResponse>> getRatingByOrderId(
            @Parameter(description = "工单ID") @PathVariable Long orderId) {
        
        RepairRatingResponse response = repairRatingService.getRatingByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取工程师评价统计", description = "获取指定工程师的评价统计信息")
    @GetMapping("/engineer/{engineerId}/stats")
    @PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getEngineerRatingStats(
            @Parameter(description = "工程师ID") @PathVariable Long engineerId) {
        
        Object stats = repairRatingService.getEngineerRatingStats(engineerId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(summary = "获取工程师评价列表", description = "获取指定工程师的所有评价")
    @GetMapping("/engineer/{engineerId}")
    @PreAuthorize("hasRole('ENGINEER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RepairRatingResponse>>> getEngineerRatings(
            @Parameter(description = "工程师ID") @PathVariable Long engineerId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int size) {
        
        // 确保分页参数有效
        if (size <= 0) size = 10;
        if (page < 0) page = 0;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<RepairRatingResponse> response = repairRatingService.getEngineerRatings(engineerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取我的评价列表", description = "用户查看自己提交的评价")
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('ENGINEER')")
    public ResponseEntity<ApiResponse<Page<RepairRatingResponse>>> getMyRatings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int size) {
        
        // 确保分页参数有效
        if (size <= 0) size = 10;
        if (page < 0) page = 0;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<RepairRatingResponse> response = repairRatingService.getUserRatings(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取所有评价", description = "管理员查看所有评价")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RepairRatingResponse>>> getAllRatings(
            @Parameter(description = "最低评分筛选") @RequestParam(required = false) Integer minRating,
            @Parameter(description = "最高评分筛选") @RequestParam(required = false) Integer maxRating,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int size) {
        
        // 确保分页参数有效
        if (size <= 0) size = 10;
        if (page < 0) page = 0;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<RepairRatingResponse> response = repairRatingService.getAllRatings(minRating, maxRating, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}