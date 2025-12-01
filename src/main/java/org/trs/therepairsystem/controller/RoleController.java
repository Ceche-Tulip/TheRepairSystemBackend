package org.trs.therepairsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.pojo.UserRole;
import org.trs.therepairsystem.service.RoleService;
import org.trs.therepairsystem.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    /** 获取全部角色 */
    @GetMapping
    public ResponseEntity<?> listRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    /** 创建角色（仅管理员可用） */
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody UserRole role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    /** 删除角色 */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok("角色已删除");
    }

    /** 设置用户角色（管理员操作） */
    @PostMapping("/assign/{userId}")
    public ResponseEntity<?> setUserRoles(
            @PathVariable Long userId,
            @RequestBody List<Integer> roleIds
    ) {
        userService.updateUserRoles(userId, roleIds);
        return ResponseEntity.ok("角色更新成功");
    }

    /** 查询用户角色 */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserRoles(userId));
    }
}

