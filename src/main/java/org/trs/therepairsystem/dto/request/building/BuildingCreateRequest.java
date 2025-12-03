package org.trs.therepairsystem.dto.request.building;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 楼栋创建请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建楼栋请求")
public class BuildingCreateRequest {

    @NotBlank(message = "楼栋名称不能为空")
    @Size(min = 1, max = 100, message = "楼栋名称长度必须在1-100字符之间")
    @Schema(description = "楼栋名称", example = "教学楼A", required = true)
    private String name;
}