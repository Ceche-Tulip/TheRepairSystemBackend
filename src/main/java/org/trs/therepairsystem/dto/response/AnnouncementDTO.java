package org.trs.therepairsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.trs.therepairsystem.common.enums.AnnouncementStatus;

import java.time.LocalDateTime;

@Data
@Schema(description = "公告信息")
public class AnnouncementDTO {
    
    @Schema(description = "公告ID", example = "1")
    private Long id;
    
    @Schema(description = "公告标题", example = "系统维护通知")
    private String title;
    
    @Schema(description = "公告内容", example = "系统将于明日进行维护，期间服务可能中断")
    private String content;
    
    @Schema(description = "发布管理员ID", example = "1")
    private Long adminId;
    
    @Schema(description = "发布管理员姓名", example = "系统管理员")
    private String adminName;
    
    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop;
    
    @Schema(description = "公告状态", example = "PUBLISHED")
    private AnnouncementStatus status;
    
    @Schema(description = "状态描述", example = "已发布")
    private String statusDescription;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}