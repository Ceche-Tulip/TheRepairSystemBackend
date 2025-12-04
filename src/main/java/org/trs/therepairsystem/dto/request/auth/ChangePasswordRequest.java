package org.trs.therepairsystem.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改密码请求 - 用于用户修改自己的登录密码")
public class ChangePasswordRequest {
    
    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码 - 必须与当前密码匹配", example = "oldpassword123", required = true)
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, message = "新密码长度至少8位")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "新密码必须至少8位，包含字母和数字")
    @Schema(description = "新密码 - 建议8位以上，包含字母和数字", example = "newpassword123", required = true)
    private String newPassword;
}