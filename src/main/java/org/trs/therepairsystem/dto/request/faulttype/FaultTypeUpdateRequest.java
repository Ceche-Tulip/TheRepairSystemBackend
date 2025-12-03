package org.trs.therepairsystem.dto.request.faulttype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 故障类型更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新故障类型请求")
public class FaultTypeUpdateRequest {

    @Size(min = 1, max = 50, message = "故障类型名称长度必须在1-50字符之间")
    @Schema(description = "故障类型名称", example = "空调故障")
    private String name;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "颜色格式必须为十六进制颜色代码，如#FF5722")
    @Schema(description = "显示颜色（十六进制颜色代码）", example = "#FF5722")
    private String color;

    @Size(max = 200, message = "描述长度不能超过200字符")
    @Schema(description = "故障类型描述", example = "包括空调不制冷、不制热、噪音过大等问题")
    private String description;
}