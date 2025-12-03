package org.trs.therepairsystem.dto.request.floor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 楼层创建请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建楼层请求")
public class FloorCreateRequest {

    @NotNull(message = "楼栋ID不能为空")
    @Schema(description = "楼栋ID", example = "1", required = true)
    private Long buildingId;

    @NotNull(message = "楼层号不能为空")
    @Schema(description = "楼层号", example = "1", required = true)
    private Integer floorNo;

    @NotBlank(message = "楼层名称不能为空")
    @Size(min = 1, max = 100, message = "楼层名称长度必须在1-100字符之间")
    @Schema(description = "楼层名称", example = "一楼", required = true)
    private String name;
}