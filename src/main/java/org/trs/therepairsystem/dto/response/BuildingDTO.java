package org.trs.therepairsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 楼栋DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "楼栋信息")
public class BuildingDTO {

    @Schema(description = "楼栋ID", example = "1")
    private Long id;

    @Schema(description = "楼栋名称", example = "教学楼A")
    private String name;
}