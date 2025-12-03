package org.trs.therepairsystem.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.entity.UserRole;
import org.trs.therepairsystem.entity.UserRoleRel;
import org.trs.therepairsystem.dto.response.UserDTO;
import org.trs.therepairsystem.repository.RoleRepository;
import org.trs.therepairsystem.repository.UserRoleRelRepository;
import org.trs.therepairsystem.dto.request.auth.LoginRequest;
import org.trs.therepairsystem.repository.UserRepository;
import org.trs.therepairsystem.security.JwtUtil;
import org.trs.therepairsystem.dto.response.AuthResponse;
import org.trs.therepairsystem.dto.request.user.UserCreateRequest;
import org.trs.therepairsystem.common.dto.ApiResponse;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户注册、登录相关接口")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserRoleRelRepository userRoleRelRepository;
    @Autowired private RoleRepository roleRepository;


    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录验证，返回JWT Token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
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
    @Operation(summary = "用户注册", description = "创建新用户账号，自动分配普通用户角色")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "注册成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数验证失败"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @Transactional
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody UserCreateRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.conflict("用户名已存在"));
        }

        // 密码格式校验：至少8位，包含字母和数字
        String rawPassword = request.getPassword() == null ? "" : request.getPassword();
        java.util.regex.Pattern pwdPattern = java.util.regex.Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
        if (!pwdPattern.matcher(rawPassword).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("密码格式不符合要求：至少8位，必须包含字母和数字"));
        }

        // 手机号格式校验（中国手机号示例）匹配一个以 1 开头，第二位是 3-9 的数字，后面紧跟 9 个任意数字的
        String phone = request.getPhone() == null ? "" : request.getPhone().trim();
        java.util.regex.Pattern phonePattern = java.util.regex.Pattern.compile("^1[3-9]\\d{9}$");
        if (!phonePattern.matcher(phone).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("手机号格式不正确"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);

        UserDTO dto = new UserDTO();
        dto.setId(saved.getId());
        dto.setUsername(saved.getUsername());
        dto.setRealName(saved.getRealName());
        dto.setPhone(saved.getPhone());

        // 默认赋予普通用户角色（ROLE_USER = 1）
        UserRole defaultRole = roleRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("默认角色不存在"));

        UserRoleRel rel = new UserRoleRel(null, saved, defaultRole);
        userRoleRelRepository.save(rel);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("用户注册成功", dto));
    }

//    @PostMapping("/register")
//    public User register(@RequestBody User user) {
//        // 简单逻辑：密码加密后保存
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
}
