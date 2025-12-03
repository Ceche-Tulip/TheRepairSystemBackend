package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.dto.request.engineer.EngineerAreaAssignRequest;
import org.trs.therepairsystem.dto.request.engineer.EngineerFaultAssignRequest;
import org.trs.therepairsystem.dto.response.UserDTO;
import org.trs.therepairsystem.dto.response.engineer.EngineerAreaDTO;
import org.trs.therepairsystem.dto.response.engineer.EngineerFaultDTO;
import org.trs.therepairsystem.service.EngineerAssignService;

import java.util.List;

@RestController
@RequestMapping("/api/engineers")
@RequiredArgsConstructor
@Tag(name = "工程师分配管理", description = "工程师负责区域和故障类型分配管理")
@Validated
public class EngineerAssignController {

    private final EngineerAssignService engineerAssignService;

    // ========== 工程师用户管理 ==========

    @GetMapping
    @Operation(summary = "获取工程师用户列表", description = "获取所有具有工程师角色的用户列表，用于分配管理")
    public List<UserDTO> getEngineerUsers() {
        return engineerAssignService.getAllEngineers();
    }

    // ========== 区域分配管理 ==========

    @PostMapping("/areas/assign")
    @Operation(summary = "分配工程师负责区域", description = "管理员为工程师分配负责的楼层区域")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignEngineerToAreas(@RequestBody @Validated EngineerAreaAssignRequest request) {
        engineerAssignService.assignEngineerToAreas(request.getEngineerId(), request.getFloorIds());
        return "区域分配成功";
    }

    @DeleteMapping("/{engineerId}/areas/{floorId}")
    @Operation(summary = "移除工程师区域分配", description = "管理员移除工程师对特定楼层的负责权限")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeEngineerFromArea(
            @Parameter(description = "工程师用户ID", example = "2") @PathVariable Long engineerId,
            @Parameter(description = "楼层ID", example = "1") @PathVariable Long floorId
    ) {
        engineerAssignService.removeEngineerFromArea(engineerId, floorId);
        return "区域分配移除成功";
    }

    @GetMapping("/{engineerId}/areas")
    @Operation(summary = "查询工程师负责区域", description = "查询指定工程师负责的所有楼层区域")
    public List<EngineerAreaDTO> getEngineerAreas(
            @Parameter(description = "工程师用户ID", example = "2") @PathVariable Long engineerId
    ) {
        return engineerAssignService.getEngineerAreas(engineerId);
    }

    @GetMapping("/areas/floor/{floorId}")
    @Operation(summary = "查询楼层负责工程师", description = "查询负责指定楼层的所有工程师")
    public List<EngineerAreaDTO> getFloorEngineers(
            @Parameter(description = "楼层ID", example = "1") @PathVariable Long floorId
    ) {
        return engineerAssignService.getFloorEngineers(floorId);
    }

    @GetMapping("/areas")
    @Operation(summary = "查询所有区域分配", description = "管理员查询所有工程师的区域分配情况")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EngineerAreaDTO> getAllAreaAssignments() {
        return engineerAssignService.getAllAreaAssignments();
    }

    // ========== 故障类型分配管理 ==========

    @PostMapping("/faults/assign")
    @Operation(summary = "分配工程师故障类型", description = "管理员为工程师分配负责的故障类型")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignEngineerToFaultTypes(@RequestBody @Validated EngineerFaultAssignRequest request) {
        engineerAssignService.assignEngineerToFaultTypes(request.getEngineerId(), request.getFaultTypeIds());
        return "故障类型分配成功";
    }

    @DeleteMapping("/{engineerId}/faults/{faultTypeId}")
    @Operation(summary = "移除工程师故障类型分配", description = "管理员移除工程师对特定故障类型的负责权限")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeEngineerFromFaultType(
            @Parameter(description = "工程师用户ID", example = "2") @PathVariable Long engineerId,
            @Parameter(description = "故障类型ID", example = "1") @PathVariable Long faultTypeId
    ) {
        engineerAssignService.removeEngineerFromFaultType(engineerId, faultTypeId);
        return "故障类型分配移除成功";
    }

    @GetMapping("/{engineerId}/faults")
    @Operation(summary = "查询工程师负责故障类型", description = "查询指定工程师负责的所有故障类型")
    public List<EngineerFaultDTO> getEngineerFaultTypes(
            @Parameter(description = "工程师用户ID", example = "2") @PathVariable Long engineerId
    ) {
        return engineerAssignService.getEngineerFaultTypes(engineerId);
    }

    @GetMapping("/faults/type/{faultTypeId}")
    @Operation(summary = "查询故障类型负责工程师", description = "查询负责指定故障类型的所有工程师")
    public List<EngineerFaultDTO> getFaultTypeEngineers(
            @Parameter(description = "故障类型ID", example = "1") @PathVariable Long faultTypeId
    ) {
        return engineerAssignService.getFaultTypeEngineers(faultTypeId);
    }

    @GetMapping("/faults")
    @Operation(summary = "查询所有故障类型分配", description = "管理员查询所有工程师的故障类型分配情况")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EngineerFaultDTO> getAllFaultTypeAssignments() {
        return engineerAssignService.getAllFaultTypeAssignments();
    }
}