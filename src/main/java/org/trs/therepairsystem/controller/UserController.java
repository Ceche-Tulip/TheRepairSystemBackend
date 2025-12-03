package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.entity.UserRole;
import org.trs.therepairsystem.dto.response.UserDTO;
import org.trs.therepairsystem.web.converter.UserConverter;
import org.trs.therepairsystem.dto.request.auth.ChangePasswordRequest;
import org.trs.therepairsystem.dto.request.user.UserCreateRequest;
import org.trs.therepairsystem.dto.request.user.UserUpdateRequest;
import org.trs.therepairsystem.service.UserService;
import org.trs.therepairsystem.web.annotation.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户CRUD操作、密码管理等")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据ID获取单个用户的详细信息")
    public UserDTO getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return UserConverter.toDTO(user);
    }

    @GetMapping
    @Operation(summary = "分页获取用户列表", description = "分页查询所有用户信息")
    public Page<UserDTO> listUsers(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return UserConverter.toDTOPage(userService.listUsers(page, size));
    }

    @PostMapping
    @Operation(summary = "创建新用户", description = "管理员创建新用户账号")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "用户创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO createUser(@RequestBody UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        
        User saved = userService.createUser(user);
        return UserConverter.toDTO(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "更新用户的基本信息（不包含密码）")
    public UserDTO updateUser(@PathVariable Long id,
                             @RequestBody UserUpdateRequest request) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (request.getRealName() != null) {
            existingUser.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            existingUser.setPhone(request.getPhone());
        }
        
        User updated = userService.updateUser(id, existingUser);
        return UserConverter.toDTO(updated);
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息，无需请求体")
    public UserDTO getCurrent(@Parameter(hidden = true) @CurrentUser User currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return UserConverter.toDTO(currentUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "管理员删除指定用户（将同时删除角色关联）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码，仅需提供旧密码和新密码")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, 
                                               @RequestBody ChangePasswordRequest request,
                                               @Parameter(hidden = true) @CurrentUser User currentUser) {
        // 只能修改自己的密码或管理员修改
        if (!id.equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
