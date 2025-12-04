package org.trs.therepairsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.entity.UserRole;
import org.trs.therepairsystem.entity.UserRoleRel;
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
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        // 获取现有用户信息
        User existingUser = getById(id);
        
        // 检查用户名是否重复（如果要更新用户名）
        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new RuntimeException("用户名已存在");
            }
            existingUser.setUsername(user.getUsername());
        }
        
        // 检查手机号是否重复（如果要更新手机号）
        if (user.getPhone() != null && !user.getPhone().equals(existingUser.getPhone())) {
            if (userRepository.findByPhone(user.getPhone()).isPresent()) {
                throw new RuntimeException("手机号已存在");
            }
            existingUser.setPhone(user.getPhone());
        }
        
        // 更新其他非敏感字段
        if (user.getRealName() != null) {
            existingUser.setRealName(user.getRealName());
        }
        
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // 先删除用户角色关联，再删除用户（避免外键约束问题）
        userRoleRelRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
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
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        user.setPassword(encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void assignRoles(Long userId, List<Integer> roleIds) {
//        // 删除旧关系
//        userRoleRelRepository.deleteByUserId(userId);
//
//        // 加载用户实体
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
//
//        // 保存新关系（按每个 roleId 查到对应的 UserRole 实体）
//        for (Integer roleId : roleIds) {
//            UserRole role = roleRepository.findById(roleId)
//                    .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + roleId));
//            userRoleRelRepository.save(new UserRoleRel(null, user, role));
//        }
//    }

    private String encode(String raw) {
        return passwordEncoder.encode(raw);
    }

    private boolean matches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }
}

