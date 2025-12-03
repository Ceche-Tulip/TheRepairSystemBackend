package org.trs.therepairsystem.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户创建/更新请求")
public class UserCreateRequest {
    
    @Schema(description = "用户名", example = "testuser", required = true)
    private String username;
    
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
    
    @Schema(description = "真实姓名", example = "张三", required = true)
    private String realName;
    
    @Schema(description = "手机号", example = "13800138000", required = true)
    private String phone;
}