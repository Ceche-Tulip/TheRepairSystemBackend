// java
package org.trs.therepairsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.entity.UserRole;
import org.trs.therepairsystem.entity.UserRoleRel;
import org.trs.therepairsystem.repository.UserRepository;
import org.trs.therepairsystem.repository.UserRoleRelRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserRoleRelRepository userRoleRelRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 直接从关系实体获取角色对象
        List<UserRoleRel> rels = userRoleRelRepository.findByUserId(user.getId());
        List<UserRole> roles = rels.stream()
                .map(UserRoleRel::getRole)
                .toList();

        // 提取角色名
        List<String> roleNames = roles.stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.toList());

        return new CustomUserDetails(user, roleNames);
    }
}
