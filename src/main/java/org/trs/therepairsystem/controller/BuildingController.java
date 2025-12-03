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
import org.trs.therepairsystem.entity.Building;
import org.trs.therepairsystem.service.BuildingService;
import org.trs.therepairsystem.web.converter.BuildingConverter;
import org.trs.therepairsystem.dto.request.building.BuildingCreateRequest;
import org.trs.therepairsystem.dto.response.BuildingDTO;
import org.trs.therepairsystem.dto.request.building.BuildingUpdateRequest;

import java.util.List;

/**
 * 楼栋管理控制器
 */
@RestController
@RequestMapping("/api/buildings")
@Tag(name = "楼栋管理", description = "楼栋CRUD操作，支持创建、查询、更新和删除楼栋")
@SecurityRequirement(name = "Bearer Authentication")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping
    @Operation(summary = "获取所有楼栋", description = "获取系统中所有楼栋列表，按名称排序")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证")
    })
    public List<BuildingDTO> getAllBuildings() {
        List<Building> buildings = buildingService.getAllBuildings();
        return BuildingConverter.toDTOList(buildings);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取楼栋", description = "分页查询楼栋列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证")
    })
    public Page<BuildingDTO> getBuildings(
            @Parameter(description = "页码（从0开始）", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Page<Building> buildingPage = buildingService.getBuildings(page, size);
        return BuildingConverter.toDTOPage(buildingPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取指定楼栋", description = "根据ID获取楼栋详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "404", description = "楼栋不存在")
    })
    public BuildingDTO getBuilding(
            @Parameter(description = "楼栋ID", example = "1")
            @PathVariable Long id) {
        Building building = buildingService.getBuildingById(id);
        return BuildingConverter.toDTO(building);
    }

    @PostMapping
    @Operation(summary = "创建楼栋", description = "创建新的楼栋，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数验证失败"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "409", description = "楼栋名称已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingDTO> createBuilding(
            @Parameter(description = "楼栋创建信息")
            @Valid @RequestBody BuildingCreateRequest request) {
        Building building = BuildingConverter.fromCreateRequest(request);
        Building created = buildingService.createBuilding(building);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BuildingConverter.toDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新楼栋", description = "更新指定楼栋信息，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "参数验证失败"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "楼栋不存在"),
        @ApiResponse(responseCode = "409", description = "楼栋名称已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public BuildingDTO updateBuilding(
            @Parameter(description = "楼栋ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "楼栋更新信息")
            @Valid @RequestBody BuildingUpdateRequest request) {
        
        Building existingBuilding = buildingService.getBuildingById(id);
        BuildingConverter.updateFromRequest(existingBuilding, request);
        
        Building updated = buildingService.updateBuilding(id, existingBuilding);
        return BuildingConverter.toDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除楼栋", description = "删除指定楼栋，需要管理员权限。删除楼栋时会级联删除该楼栋下的所有楼层")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未认证"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "楼栋不存在"),
        @ApiResponse(responseCode = "409", description = "楼栋已被引用，无法删除")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBuilding(
            @Parameter(description = "楼栋ID", example = "1")
            @PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索楼栋", description = "根据名称模糊搜索楼栋")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功"),
        @ApiResponse(responseCode = "401", description = "未认证")
    })
    public Page<BuildingDTO> searchBuildings(
            @Parameter(description = "搜索关键词", example = "教学楼")
            @RequestParam String keyword,
            @Parameter(description = "页码（从0开始）", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Page<Building> buildingPage = buildingService.searchBuildingsByName(keyword, page, size);
        return BuildingConverter.toDTOPage(buildingPage);
    }
}