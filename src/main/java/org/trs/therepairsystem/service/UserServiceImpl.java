package org.trs.therepairsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.trs.therepairsystem.pojo.User;
import org.trs.therepairsystem.pojo.UserRole;
import org.trs.therepairsystem.pojo.UserRoleRel;
import org.trs.therepairsystem.repository.RoleRepository;
import org.trs.therepairsystem.repository.UserRepository;
import org.trs.therepairsystem.repository.UserRoleRelRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRelRepository userRoleRelRepository;

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Page<User> listUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public User createUser(User user) {
        user.setPassword(encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        user.setId(id);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        userRoleRelRepository.deleteByUserId(id);
    }

    @Override
    public void updateUserRoles(Long userId, List<Integer> roleIds) {
        // 删除旧关系（按需要保留）
        userRoleRelRepository.deleteByUserId(userId);

        // 加载用户实体
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));

        // 加载角色实体
        List<UserRole> roles = roleRepository.findAllById(roleIds);

        // 使用 (Long id, User user, UserRole role) 构建关系
        List<UserRoleRel> rels = roles.stream()
                .map(role -> new UserRoleRel(null, user, role))
                .collect(Collectors.toList());

        userRoleRelRepository.saveAll(rels);
    }

    @Override
    public List<UserRole> getUserRoles(Long userId) {
        List<UserRoleRel> rels = userRoleRelRepository.findByUserId(userId);

        // 从关系实体中取出角色对象
        return rels.stream()
                .map(UserRoleRel::getRole)
                .collect(Collectors.toList());
    }

    @Override
    public boolean login(String username, String password) {
        return userRepository.findByUsername(username)
                .map(u -> matches(password, u.getPassword()))
                .orElse(false);
    }

    @Override
    public void resetPassword(Long userId) {
        User user = getById(userId);
        user.setPassword(encode("123456"));
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        user.setPassword(encode(newPassword));
        userRepository.save(user);
    }

    private String encode(String raw) {
        return new BCryptPasswordEncoder().encode(raw);
    }

    private boolean matches(String raw, String encoded) {
        return new BCryptPasswordEncoder().matches(raw, encoded);
    }
}

