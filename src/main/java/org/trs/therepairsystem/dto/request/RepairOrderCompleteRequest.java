package org.trs.therepairsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RepairOrderCompleteRequest {

    @NotBlank(message = "维修信息不能为空")
    @Size(min = 10, max = 1000, message = "维修信息长度必须在10-1000字符之间")
    private String repairInfo;
}