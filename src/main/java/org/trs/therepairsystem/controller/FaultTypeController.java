package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.entity.FaultType;
import org.trs.therepairsystem.service.FaultTypeService;
import org.trs.therepairsystem.web.converter.FaultTypeConverter;
import org.trs.therepairsystem.dto.request.faulttype.FaultTypeCreateRequest;
import org.trs.therepairsystem.dto.response.FaultTypeDTO;
import org.trs.therepairsystem.dto.request.faulttype.FaultTypeUpdateRequest;

import java.util.List;

/**
 * 故障类型管理控制器
 */
@RestController
@RequestMapping("/api/fault-types")
@Tag(name = "故障类型管理", description = "故障类型CRUD操作，支持创建、查询、更新和删除故障类型")
@SecurityRequirement(name = "Bearer Authentication")
public class FaultTypeController {

    @Autowired
    private FaultTypeService faultTypeService;

    @GetMapping
    @Operation(summary = "获取所有故障类型", description = "获取系统中所有故障类型列表，按名称排序")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证")
    })
    public List<FaultTypeDTO> getAllFaultTypes() {
        List<FaultType> faultTypes = faultTypeService.getAllFaultTypes();
        return FaultTypeConverter.toDTOList(faultTypes);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取故障类型", description = "分页查询故障类型列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证")
    })
    public Page<FaultTypeDTO> getFaultTypes(
            @Parameter(description = "页码（从0开始）", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Page<FaultType> faultTypePage = faultTypeService.getFaultTypes(page, size);
        return FaultTypeConverter.toDTOPage(faultTypePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取指定故障类型", description = "根据ID获取故障类型详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "404", description = "故障类型不存在")
    })
    public FaultTypeDTO getFaultType(
            @Parameter(description = "故障类型ID", example = "1")
            @PathVariable Long id) {
        FaultType faultType = faultTypeService.getFaultTypeById(id);
        return FaultTypeConverter.toDTO(faultType);
    }

    @PostMapping
    @Operation(summary = "创建故障类型", description = "创建新的故障类型，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数验证失败"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "409", description = "故障类型名称已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FaultTypeDTO> createFaultType(
            @Parameter(description = "故障类型创建信息")
            @Valid @RequestBody FaultTypeCreateRequest request) {
        FaultType faultType = FaultTypeConverter.fromCreateRequest(request);
        FaultType created = faultTypeService.createFaultType(faultType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FaultTypeConverter.toDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新故障类型", description = "更新指定故障类型信息，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "参数验证失败"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "故障类型不存在"),
        @ApiResponse(responseCode = "409", description = "故障类型名称已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public FaultTypeDTO updateFaultType(
            @Parameter(description = "故障类型ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "故障类型更新信息")
            @Valid @RequestBody FaultTypeUpdateRequest request) {
        
        // 获取现有故障类型并应用更新
        FaultType existingFaultType = faultTypeService.getFaultTypeById(id);
        FaultTypeConverter.updateFromRequest(existingFaultType, request);
        
        // 更新故障类型
        FaultType updated = faultTypeService.updateFaultType(id, existingFaultType);
        return FaultTypeConverter.toDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除故障类型", description = "删除指定故障类型，需要管理员权限。如果故障类型已被报修单引用则无法删除")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "故障类型不存在"),
        @ApiResponse(responseCode = "409", description = "故障类型已被引用，无法删除")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFaultType(
            @Parameter(description = "故障类型ID", example = "1")
            @PathVariable Long id) {
        faultTypeService.deleteFaultType(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索故障类型", description = "根据名称模糊搜索故障类型")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功"),
        @ApiResponse(responseCode = "401", description = "未认证")
    })
    public Page<FaultTypeDTO> searchFaultTypes(
            @Parameter(description = "搜索关键词", example = "空调")
            @RequestParam String keyword,
            @Parameter(description = "页码（从0开始）", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Page<FaultType> faultTypePage = faultTypeService.searchFaultTypesByName(keyword, page, size);
        return FaultTypeConverter.toDTOPage(faultTypePage);
    }

    @GetMapping("/{id}/can-delete")
    @Operation(summary = "检查是否可删除", description = "检查指定故障类型是否可以被删除（未被报修单引用）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "检查完成"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "404", description = "故障类型不存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public boolean canDeleteFaultType(
            @Parameter(description = "故障类型ID", example = "1")
            @PathVariable Long id) {
        // 先检查故障类型是否存在
        faultTypeService.getFaultTypeById(id);
        return faultTypeService.canDelete(id);
    }
}