package org.trs.therepairsystem.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息响应对象")
public class UserDTO {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "testuser")
    private String username;
    
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}
