package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.entity.UserRole;
import org.trs.therepairsystem.dto.response.RoleDTO;
import org.trs.therepairsystem.service.RoleService;
import org.trs.therepairsystem.service.UserService;
import org.trs.therepairsystem.web.converter.RoleConverter;
import org.trs.therepairsystem.dto.request.role.RoleCreateRequest;
import org.trs.therepairsystem.dto.request.user.UserRoleAssignRequest;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "角色管理", description = "角色CRUD操作、用户角色分配等")
@SecurityRequirement(name = "Bearer Authentication")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "获取所有角色", description = "查询系统中的所有角色信息，所有认证用户都可访问")
    public List<RoleDTO> listRoles() {
        List<UserRole> roles = roleService.getAllRoles();
        return RoleConverter.toDTOList(roles);
    }

    @PostMapping
    @Operation(summary = "创建新角色", description = "管理员创建新的角色。角色名称必须唯一。")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "角色创建成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "角色名称已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public RoleDTO createRole(@RequestBody RoleCreateRequest request) {
        UserRole role = new UserRole();
        role.setRoleName(request.getRoleName());
        
        UserRole savedRole = roleService.createRole(role);
        return RoleConverter.toDTO(savedRole);
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "更新角色信息", description = "管理员更新指定角色的信息。角色名称必须唯一。")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "角色更新成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "角色不存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "角色名称已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public RoleDTO updateRole(@PathVariable Integer roleId,
                             @RequestBody RoleCreateRequest request) {
        UserRole existingRole = roleService.getById(roleId);
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }
        
        UserRole roleToUpdate = new UserRole();
        roleToUpdate.setRoleName(request.getRoleName());
        UserRole updatedRole = roleService.updateRole(roleId, roleToUpdate);
        return RoleConverter.toDTO(updatedRole);
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色", description = "管理员删除指定角色。注意：删除前请确保没有用户使用此角色。")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "删除成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "角色不存在"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "角色正在被使用，无法删除")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@Parameter(description = "角色ID", example = "1") @PathVariable Integer roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/assign")
    @Operation(summary = "分配用户角色", description = "管理员为指定用户分配角色")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignUserRoles(
            @Parameter(description = "用户ID", example = "1") @PathVariable Long userId,
            @RequestBody UserRoleAssignRequest request
    ) {
        userService.updateUserRoles(userId, request.getRoleIds());
        return "角色分配成功";
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "查询用户角色", description = "查询指定用户拥有的所有角色")
    public List<RoleDTO> getUserRoles(
            @Parameter(description = "用户ID", example = "1") @PathVariable Long userId
    ) {
        List<UserRole> userRoles = userService.getUserRoles(userId);
        return RoleConverter.toDTOList(userRoles);
    }
}

