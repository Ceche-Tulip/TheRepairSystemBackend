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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.dto.request.floor.FloorCreateRequest;
import org.trs.therepairsystem.dto.request.floor.FloorUpdateRequest;
import org.trs.therepairsystem.dto.response.FloorDTO;
import org.trs.therepairsystem.entity.Building;
import org.trs.therepairsystem.entity.Floor;
import org.trs.therepairsystem.service.BuildingService;
import org.trs.therepairsystem.service.FloorService;
import org.trs.therepairsystem.web.converter.FloorConverter;

import java.util.List;

@RestController
@RequestMapping("/api/floors")
@Tag(name = "楼层管理", description = "楼层相关的CRUD操作")
@SecurityRequirement(name = "Bearer Authentication")
public class FloorController {

    @Autowired
    private FloorService floorService;

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/{id}")
    @Operation(summary = "获取楼层信息", description = "根据ID获取单个楼层的详细信息")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "楼层不存在")
    })
    public FloorDTO getFloor(@Parameter(description = "楼层ID", example = "1") @PathVariable Long id) {
        Floor floor = floorService.getById(id);
        if (floor == null) {
            throw new RuntimeException("楼层不存在");
        }
        return FloorConverter.toDTO(floor);
    }

    @GetMapping
    @Operation(summary = "分页获取楼层列表", description = "分页查询楼层信息")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功")
    })
    public Page<FloorDTO> listFloors(
            @Parameter(description = "页码", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "楼栋ID，可选") @RequestParam(required = false) Long buildingId) {
        
        if (buildingId != null) {
            return FloorConverter.toDTOPage(floorService.listFloorsByBuildingId(buildingId, page, size));
        } else {
            return FloorConverter.toDTOPage(floorService.listFloors(page, size));
        }
    }

    @GetMapping("/building/{buildingId}")
    @Operation(summary = "获取指定楼栋的楼层列表", description = "获取指定楼栋下的所有楼层")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功")
    })
    public List<FloorDTO> getFloorsByBuilding(
            @Parameter(description = "楼栋ID", example = "1") @PathVariable Long buildingId) {
        List<Floor> floors = floorService.getFloorsByBuildingId(buildingId);
        return FloorConverter.toDTOList(floors);
    }

    @PostMapping
    @Operation(summary = "创建楼层", description = "创建新的楼层")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "创建成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "楼层号已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public FloorDTO createFloor(@Valid @RequestBody FloorCreateRequest request) {
        // 查找楼栋
        Building building = buildingService.getBuildingById(request.getBuildingId());
        if (building == null) {
            throw new RuntimeException("楼栋不存在");
        }

        // 创建楼层
        Floor floor = new Floor();
        floor.setBuilding(building);
        floor.setFloorNo(request.getFloorNo());
        floor.setName(request.getName().trim());

        Floor savedFloor = floorService.createFloor(floor);
        return FloorConverter.toDTO(savedFloor);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新楼层信息", description = "更新楼层的基本信息")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "楼层不存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "楼层号已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public FloorDTO updateFloor(
            @Parameter(description = "楼层ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody FloorUpdateRequest request) {
        
        Floor floorToUpdate = new Floor();
        floorToUpdate.setFloorNo(request.getFloorNo());
        floorToUpdate.setName(request.getName());

        Floor updatedFloor = floorService.updateFloor(id, floorToUpdate);
        return FloorConverter.toDTO(updatedFloor);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除楼层", description = "删除指定楼层")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "删除成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "楼层不存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFloor(@Parameter(description = "楼层ID", example = "1") @PathVariable Long id) {
        floorService.deleteFloor(id);
        return "删除成功";
    }

    @GetMapping("/check-floor-no/{buildingId}/{floorNo}")
    @Operation(summary = "检查楼层号是否存在", description = "检查指定楼栋中楼层号是否已被使用")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "检查完成")
    })
    public boolean checkFloorNoExists(
            @Parameter(description = "楼栋ID", example = "1") @PathVariable Long buildingId,
            @Parameter(description = "楼层号", example = "1") @PathVariable Integer floorNo) {
        return floorService.isFloorNoExistsInBuilding(buildingId, floorNo);
    }

    @GetMapping("/count/{buildingId}")
    @Operation(summary = "获取楼栋楼层数量", description = "获取指定楼栋的楼层总数")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功")
    })
    public long countFloorsByBuilding(
            @Parameter(description = "楼栋ID", example = "1") @PathVariable Long buildingId) {
        return floorService.countFloorsByBuildingId(buildingId);
    }
}