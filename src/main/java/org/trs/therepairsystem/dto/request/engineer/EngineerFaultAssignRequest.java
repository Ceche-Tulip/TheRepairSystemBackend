package org.trs.therepairsystem.dto.request.engineer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "工程师故障类型分配请求")
public class EngineerFaultAssignRequest {
    
    @NotNull(message = "工程师ID不能为空")
    @Schema(description = "工程师用户ID", example = "2", required = true)
    private Long engineerId;
    
    @NotEmpty(message = "故障类型ID列表不能为空")
    @Schema(description = "要分配的故障类型ID列表", example = "[1, 2, 3]", required = true)
    private List<Long> faultTypeIds;
}