package org.trs.therepairsystem.dto.request.faulttype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 故障类型创建请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建故障类型请求")
public class FaultTypeCreateRequest {

    @NotBlank(message = "故障类型名称不能为空")
    @Size(min = 1, max = 50, message = "故障类型名称长度必须在1-50字符之间")
    @Schema(description = "故障类型名称", example = "空调故障", required = true)
    private String name;

    @NotBlank(message = "显示颜色不能为空")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "颜色格式必须为十六进制颜色代码，如#FF5722")
    @Schema(description = "显示颜色（十六进制颜色代码）", example = "#FF5722", required = true)
    private String color;

    @Size(max = 200, message = "描述长度不能超过200字符")
    @Schema(description = "故障类型描述", example = "包括空调不制冷、不制热、噪音过大等问题")
    private String description;
}