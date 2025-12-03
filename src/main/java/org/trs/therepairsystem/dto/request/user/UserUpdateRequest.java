package org.trs.therepairsystem.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息更新请求")
public class UserUpdateRequest {
    
    @Schema(description = "真实姓名", example = "李四")
    private String realName;
    
    @Schema(description = "手机号", example = "13900139000")
    private String phone;
}