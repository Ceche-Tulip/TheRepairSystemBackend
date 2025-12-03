package org.trs.therepairsystem.dto.request.floor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 楼层更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新楼层请求")
public class FloorUpdateRequest {

    @Schema(description = "楼层号", example = "1")
    private Integer floorNo;

    @Size(min = 1, max = 100, message = "楼层名称长度必须在1-100字符之间")
    @Schema(description = "楼层名称", example = "一楼")
    private String name;
}