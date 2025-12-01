package org.trs.therepairsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.pojo.User;
import org.trs.therepairsystem.pojo.UserRole;
import org.trs.therepairsystem.pojo.dto.UserDTO;
import org.trs.therepairsystem.web.converter.UserConverter;
import org.trs.therepairsystem.web.dto.ChangePasswordRequest;
import org.trs.therepairsystem.service.UserService;
import org.trs.therepairsystem.web.annotation.CurrentUser;

import java.util.List;

//@Profile("legacy")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(UserConverter.toDTO(user));
    }

    @GetMapping
    public Page<UserDTO> listUsers(@RequestParam int page,
                                   @RequestParam int size) {
        return UserConverter.toDTOPage(userService.listUsers(page, size));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        User saved = userService.createUser(user);
        return ResponseEntity.ok(UserConverter.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(UserConverter.toDTO(updated));
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrent(@CurrentUser User currentUser) {
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(UserConverter.toDTO(currentUser));
    }

    // 其它接口（密码修改、角色更新等）可继续返回 void 或简单返回状态
}
