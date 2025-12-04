package org.trs.therepairsystem.service;

import org.springframework.data.domain.Page;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.entity.UserRole;

import java.util.List;

public interface UserService {

    User getById(Long id);
    
    // 专门用于前端显示的方法，不包含敏感信息

    Page<User> listUsers(int page, int size);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    void updateUserRoles(Long userId, List<Integer> roleIds);

    List<UserRole> getUserRoles(Long userId);

    void changePassword(Long userId, String oldPassword, String newPassword);
    
    User findByUsername(String username);

//    public void assignRoles(Long userId, List<Integer> roleIds);
}


