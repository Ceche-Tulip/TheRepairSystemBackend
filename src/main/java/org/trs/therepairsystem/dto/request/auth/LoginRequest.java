package org.trs.therepairsystem.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录请求")
public class LoginRequest {
    
    @Schema(description = "用户名", example = "testuser", required = true)
    private String username;
    
    @Schema(description = "密码", example = "123456", required = true)
    private String password;
}