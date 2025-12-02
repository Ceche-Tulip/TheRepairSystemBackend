package org.trs.therepairsystem.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色信息响应对象")
public class RoleDTO {
    
    @Schema(description = "角色ID", example = "1")
    private Integer id;
    
    @Schema(description = "角色名称", example = "ADMIN")
    private String roleName;
}
