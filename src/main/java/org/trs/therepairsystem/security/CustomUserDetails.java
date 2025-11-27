// java
package org.trs.therepairsystem.security;

import lombok.Getter;
import org.trs.therepairsystem.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CustomUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final User user;

    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(User user, List<String> roles) {
        this.user = Objects.requireNonNull(user, "user 不能为 null");
        // 防御式处理 roles，过滤 null/空串，去重并构建不可变集合
        this.authorities = Collections.unmodifiableList(
                (roles == null ? List.<String>of() : roles).stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(r -> !r.isEmpty())
                        .distinct()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // 已不可变，可直接返回
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 可选辅助方法
    public Long getUserId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 可根据业务扩展
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 可根据业务扩展
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 可根据业务扩展
    }

    @Override
    public boolean isEnabled() {
        return true; // 可根据业务扩展
    }
}
