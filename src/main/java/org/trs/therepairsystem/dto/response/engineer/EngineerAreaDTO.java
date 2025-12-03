package org.trs.therepairsystem.dto.response.engineer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "工程师区域分配信息")
public class EngineerAreaDTO {
    
    @Schema(description = "分配ID", example = "1")
    private Long id;
    
    @Schema(description = "工程师ID", example = "2")
    private Long engineerId;
    
    @Schema(description = "工程师姓名", example = "张三")
    private String engineerName;
    
    @Schema(description = "工程师用户名", example = "engineer1")
    private String engineerUsername;
    
    @Schema(description = "楼层ID", example = "1")
    private Long floorId;
    
    @Schema(description = "楼层名称", example = "一楼")
    private String floorName;
    
    @Schema(description = "楼层号", example = "1")
    private Integer floorNo;
    
    @Schema(description = "所属楼栋ID", example = "1")
    private Long buildingId;
    
    @Schema(description = "所属楼栋名称", example = "教学楿A")
    private String buildingName;
}