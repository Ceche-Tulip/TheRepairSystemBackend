package org.trs.therepairsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 故障类型DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "故障类型信息")
public class FaultTypeDTO {

    @Schema(description = "故障类型ID", example = "1")
    private Long id;

    @Schema(description = "故障类型名称", example = "空调故障")
    private String name;

    @Schema(description = "显示颜色", example = "#FF5722")
    private String color;

    @Schema(description = "故障类型描述", example = "包括空调不制冷、不制热、噪音过大等问题")
    private String description;
}