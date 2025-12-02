package org.trs.therepairsystem.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改密码请求")
public class ChangePasswordRequest {
    
    @Schema(description = "旧密码", example = "oldpassword123", required = true)
    private String oldPassword;
    
    @Schema(description = "新密码", example = "newpassword123", required = true)
    private String newPassword;
}

