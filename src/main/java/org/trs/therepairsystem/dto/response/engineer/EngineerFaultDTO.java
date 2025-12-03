package org.trs.therepairsystem.dto.response.engineer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "工程师故障类型分配信息")
public class EngineerFaultDTO {
    
    @Schema(description = "分配ID", example = "1")
    private Long id;
    
    @Schema(description = "工程师ID", example = "2")
    private Long engineerId;
    
    @Schema(description = "工程师姓名", example = "张三")
    private String engineerName;
    
    @Schema(description = "工程师用户名", example = "engineer1")
    private String engineerUsername;
    
    @Schema(description = "故障类型ID", example = "1")
    private Long faultTypeId;
    
    @Schema(description = "故障类型名称", example = "空调故障")
    private String faultTypeName;
    
    @Schema(description = "故障类型颜色", example = "#FF5722")
    private String faultTypeColor;
    
    @Schema(description = "故障类型描述", example = "空调不制冷、不制热、噪音过大、滤网更换等")
    private String faultTypeDescription;
}