package org.trs.therepairsystem.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户信息更新请求 - 用于更新用户的非敏感信息")
public class UserUpdateRequest {
    
    @Size(min = 2, max = 10, message = "真实姓名长度必须在2-10字符之间")
    @Schema(description = "真实姓名 - 用户的真实姓名", example = "李四")
    private String realName;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号 - 手机号必须唯一，不能与其他用户重复", example = "13900139000")
    private String phone;
}