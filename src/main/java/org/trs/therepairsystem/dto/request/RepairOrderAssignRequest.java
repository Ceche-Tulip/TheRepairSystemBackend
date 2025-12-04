package org.trs.therepairsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepairOrderAssignRequest {

    @NotNull(message = "工程师ID不能为空")
    private Long engineerId;
}