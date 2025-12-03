package org.trs.therepairsystem.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色创建请求")
public class RoleCreateRequest {
    
    @Schema(description = "角色名称", example = "ENGINEER", required = true)
    private String roleName;
}