package org.trs.therepairsystem.dto.request.announcement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "公告更新请求")
public class AnnouncementUpdateRequest {
    
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题长度不能超过200字符")
    @Schema(description = "公告标题", example = "系统维护通知", required = true)
    private String title;
    
    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "公告内容", example = "系统将于明日进行维护，期间服务可能中断", required = true)
    private String content;
    
    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop = false;
}