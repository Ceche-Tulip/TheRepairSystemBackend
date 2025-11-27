package org.trs.therepairsystem.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.pojo.User;
import org.trs.therepairsystem.web.dto.LoginRequest;
import org.trs.therepairsystem.repository.UserRepository;
import org.trs.therepairsystem.security.JwtUtil;
import org.trs.therepairsystem.web.dto.AuthResponse;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // 认证成功，从 DB 拉用户获取 id
        User user = userRepository.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), new HashMap<>());

        return new AuthResponse(token, "Bearer", jwtUtil.getUserIdFromToken(token), user.getUsername());
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        // 简单逻辑：密码加密后保存
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
