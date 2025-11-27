package org.trs.therepairsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.pojo.User;
import org.trs.therepairsystem.pojo.UserRole;
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
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public Page<User> listUsers(@RequestParam int page,
                                @RequestParam int size) {
        return userService.listUsers(page, size);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/roles")
    public List<UserRole> getUserRoles(@PathVariable Long id) {
        return userService.getUserRoles(id);
    }

    @PostMapping("/{id}/roles")
    public void updateUserRoles(@PathVariable Long id,
                                @RequestBody List<Integer> roleIds) {
        userService.updateUserRoles(id, roleIds);
    }

    @PutMapping("/{id}/password")
    public void resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
    }

    @PutMapping("/password")
    public void changePassword(@RequestBody ChangePasswordRequest req) {
        userService.changePassword(req.getUserId(),
                req.getOldPassword(), req.getNewPassword());
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrent(@CurrentUser User currentUser) {
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(currentUser);
    }
}
