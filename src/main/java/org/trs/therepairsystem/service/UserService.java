package org.trs.therepairsystem.service;

import org.springframework.data.domain.Page;
import org.trs.therepairsystem.pojo.User;
import org.trs.therepairsystem.pojo.UserRole;

import java.util.List;

public interface UserService {

    User getById(Long id);

    Page<User> listUsers(int page, int size);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    void updateUserRoles(Long userId, List<Integer> roleIds);

    List<UserRole> getUserRoles(Long userId);

    boolean login(String username, String password);

    void resetPassword(Long userId);

    void changePassword(Long userId, String oldPassword, String newPassword);
}


