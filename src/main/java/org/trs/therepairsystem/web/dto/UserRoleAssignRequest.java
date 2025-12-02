package org.trs.therepairsystem.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户角色分配请求")
public class UserRoleAssignRequest {
    
    @Schema(description = "要分配的角色ID列表", example = "[1, 2]", required = true)
    private List<Integer> roleIds;
}