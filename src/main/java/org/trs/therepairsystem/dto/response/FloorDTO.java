package org.trs.therepairsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 楼层DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "楼层信息")
public class FloorDTO {

    @Schema(description = "楼层ID", example = "1")
    private Long id;

    @Schema(description = "楼栋信息")
    private BuildingDTO building;

    @Schema(description = "楼层号", example = "1")
    private Integer floorNo;

    @Schema(description = "楼层名称", example = "一楼")
    private String name;
}