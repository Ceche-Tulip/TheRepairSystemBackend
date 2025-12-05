package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.dto.request.*;
import org.trs.therepairsystem.dto.response.EngineerResponse;
import org.trs.therepairsystem.dto.response.RepairOrderResponse;
import org.trs.therepairsystem.dto.response.RepairOrderStatsResponse;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;
import org.trs.therepairsystem.security.CustomUserDetails;
import org.trs.therepairsystem.service.RepairOrderService;
import org.trs.therepairsystem.common.dto.ApiResponse;

import java.util.List;

@Tag(name = "维修工单管理", description = "维修工单相关操作接口")
@RestController
@RequestMapping("/api/repair-orders")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    @Operation(summary = "用户提交维修工单", description = "普通用户提交新的维修工单，状态变为已提交，等待系统自动分配或管理员手动分配工程师")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "提交成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/submit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> submitOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RepairOrderSubmitRequest request) {
        
        RepairOrderResponse response = repairOrderService.submitOrder(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "保存为草稿", description = "用户保存维修工单为草稿状态，可稍后编辑")
    @PostMapping("/draft")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> saveDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RepairOrderSubmitRequest request) {
        
        RepairOrderResponse response = repairOrderService.saveDraft(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "提交草稿工单", description = "将已保存的草稿工单提交，状态变为已提交并尝试自动分配工程师，如果自动分配失败则等待管理员手动分配")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "提交成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "工单状态不允许提交或请求参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "没有权限操作此工单"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "工单不存在")
    })
    @PutMapping("/{orderId}/submit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> submitDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId) {
        
        RepairOrderResponse response = repairOrderService.submitDraft(userDetails.getUserId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "修改草稿工单", description = "用户可以修改自己创建的草稿状态工单的内容，包括建筑、楼层、故障类型和描述")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "工单状态不允许修改或请求参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "没有权限操作此工单"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "工单不存在")
    })
    @PutMapping("/{orderId}/draft")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> updateDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId,
            @Valid @RequestBody RepairOrderSubmitRequest request) {
        
        RepairOrderResponse response = repairOrderService.updateDraft(userDetails.getUserId(), orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "管理员分配工程师", description = "管理员手动为工单分配指定工程师，分配成功后工单状态变为待处理")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "分配成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误或工单状态不允许分配"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "工单或工程师不存在")
    })
    @PutMapping("/{orderId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> assignEngineer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId,
            @Valid @RequestBody RepairOrderAssignRequest request) {
        
        RepairOrderResponse response = repairOrderService.assignEngineer(
            userDetails.getUserId(), orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "自动分配工程师", description = "系统根据故障类型和区域自动分配工程师。如果没有符合条件的工程师，分配失败，需要管理员手动分配。分配成功后工单状态变为待处理。")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "分配成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "无符合条件的工程师或工单状态不允许分配"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "工单不存在")
    })
    @PutMapping("/{orderId}/auto-assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> autoAssignEngineer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId) {
        
        RepairOrderResponse response = repairOrderService.autoAssignEngineer(
            userDetails.getUserId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "工程师接受工单", description = "工程师接受分配给自己的工单")
    @PutMapping("/{orderId}/accept")
    @PreAuthorize("hasRole('ENGINEER')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> acceptOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId) {
        
        RepairOrderResponse response = repairOrderService.acceptOrder(
            userDetails.getUserId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "工程师完成工单", description = "工程师标记工单为已完成并填写维修信息")
    @PutMapping("/{orderId}/complete")
    @PreAuthorize("hasRole('ENGINEER')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> completeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId,
            @Valid @RequestBody RepairOrderCompleteRequest request) {
        
        RepairOrderResponse response = repairOrderService.completeOrder(
            userDetails.getUserId(), orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "用户关闭工单并评价", description = "用户对已完成的工单进行确认关闭并提交评价")
    @PutMapping("/{orderId}/close")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> closeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId,
            @Valid @RequestBody RepairRatingRequest ratingRequest) {
        
        RepairOrderResponse response = repairOrderService.closeOrderWithRating(
            userDetails.getUserId(), orderId, ratingRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "用户取消工单", description = "用户取消自己提交的工单。可取消状态：待提交、已提交、待处理。一旦工程师开始处理则不可取消。")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "取消成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "工单状态不允许取消"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "没有权限取消此工单"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "工单不存在")
    })
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId) {
        
        RepairOrderResponse response = repairOrderService.cancelOrder(
            userDetails.getUserId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "管理员强制取消工单", description = "管理员可以强制取消任意状态的工单，包括已在处理中的工单。")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "取消成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "工单不存在")
    })
    @PutMapping("/{orderId}/admin-cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> adminCancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "工单ID") @PathVariable Long orderId) {
        
        RepairOrderResponse response = repairOrderService.adminCancelOrder(
            userDetails.getUserId(), orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "查询工单详情", description = "根据工单ID查询详细信息")
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ENGINEER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> getOrderById(
            @Parameter(description = "工单ID") @PathVariable Long orderId) {
        
        RepairOrderResponse response = repairOrderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取用户工单列表", description = "查询当前用户的工单列表")
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RepairOrderResponse>>> getUserOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "状态筛选") @RequestParam(required = false) RepairOrderStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<RepairOrderResponse> response = repairOrderService.getUserOrders(
            userDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取工程师工单列表", description = "查询分配给当前工程师的工单列表")
    @GetMapping("/engineer/my")
    @PreAuthorize("hasRole('ENGINEER')")
    public ResponseEntity<ApiResponse<Page<RepairOrderResponse>>> getEngineerOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "状态筛选") @RequestParam(required = false) RepairOrderStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<RepairOrderResponse> response = repairOrderService.getEngineerOrders(
            userDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "管理员查询所有工单", description = "管理员查询系统中所有工单，支持多种条件筛选，所有参数都是可选的")
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RepairOrderResponse>>> getAllOrders(
            @Parameter(description = "状态筛选") @RequestParam(required = false) RepairOrderStatus status,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "工程师ID") @RequestParam(required = false) Long engineerId,
            @Parameter(description = "建筑ID") @RequestParam(required = false) Long buildingId,
            @Parameter(description = "故障类型ID") @RequestParam(required = false) Long faultTypeId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        
        // 确保分页参数有效
        if (size <= 0) size = 10;
        if (page < 0) page = 0;
        
        // 确保排序字段不为空
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "createTime";
        }
        
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir) ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<RepairOrderResponse> response = repairOrderService.getAllOrdersSimple(
            status, userId, engineerId, buildingId, faultTypeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取待分配工单", description = "管理员查看未分配工程师的待处理工单")
    @GetMapping("/admin/unassigned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RepairOrderResponse>>> getUnassignedOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").ascending());
        Page<RepairOrderResponse> response = repairOrderService.getUnassignedOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取可用工程师列表", description = "根据故障类型和区域获取可分配的工程师列表")
    @GetMapping("/available-engineers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EngineerResponse>>> getAvailableEngineers(
            @Parameter(description = "故障类型ID") @RequestParam Long faultTypeId,
            @Parameter(description = "楼层ID") @RequestParam Long floorId) {
        
        List<EngineerResponse> response = repairOrderService.getAvailableEngineers(
            faultTypeId, floorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取工单统计信息", description = "获取系统工单的统计数据")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderStatsResponse>> getOrderStats() {
        RepairOrderStatsResponse response = repairOrderService.getOrderStats();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取用户工单统计", description = "获取当前用户的工单统计数据")
    @GetMapping("/stats/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RepairOrderStatsResponse>> getUserOrderStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        RepairOrderStatsResponse response = repairOrderService.getUserOrderStats(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取工程师工单统计", description = "获取当前工程师的工单统计数据")
    @GetMapping("/stats/engineer")
    @PreAuthorize("hasRole('ENGINEER')")
    public ResponseEntity<ApiResponse<RepairOrderStatsResponse>> getEngineerOrderStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        RepairOrderStatsResponse response = repairOrderService.getEngineerOrderStats(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}