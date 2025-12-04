package org.trs.therepairsystem.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户创建/更新请求 - 所有字段均为必填")
public class UserCreateRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20字符之间")
    @Schema(description = "用户名 - 3-20字符，必须唯一", example = "testuser", required = true)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度至少8位")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "密码必须至少8位，包含字母和数字")
    @Schema(description = "密码 - 至少8位，必须包含字母和数字", example = "password123", required = true)
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 10, message = "真实姓名长度必须在2-10字符之间")
    @Schema(description = "真实姓名 - 用户的真实姓名", example = "张三", required = true)
    private String realName;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号 - 中国大陆手机号格式，必须唯一", example = "13800138000", required = true)
    private String phone;
}