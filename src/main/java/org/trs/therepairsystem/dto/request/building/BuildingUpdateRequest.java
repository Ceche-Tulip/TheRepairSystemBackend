package org.trs.therepairsystem.dto.request.building;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 楼栋更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新楼栋请求")
public class BuildingUpdateRequest {

    @Size(min = 1, max = 100, message = "楼栋名称长度必须在1-100字符之间")
    @Schema(description = "楼栋名称", example = "教学楼A")
    private String name;
}