package org.trs.therepairsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RepairOrderSubmitRequest {

    @NotNull(message = "建筑ID不能为空")
    private Long buildingId;

    @NotNull(message = "楼层ID不能为空") 
    private Long floorId;

    @NotNull(message = "故障类型ID不能为空")
    private Long faultTypeId;

    @NotBlank(message = "故障描述不能为空")
    @Size(min = 10, max = 1000, message = "故障描述长度必须在10-1000字符之间")
    private String description;
}