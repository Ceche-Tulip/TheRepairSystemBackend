package org.trs.therepairsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "维修工单响应对象")
public class RepairOrderResponse {

    private Long id;
    private RepairOrderStatus status;
    private String description;
    private String repairInfo;
    private LocalDateTime createTime;
    private LocalDateTime acceptTime;
    private LocalDateTime finishTime;

    // 提交用户信息
    private Long submitUserId;
    private String submitUserName;
    @Schema(description = "报修人联系电话。默认可能为脱敏值；管理员、本人、同工单协作者可见明文", example = "138****8000")
    private String submitUserPhone;

    // 建筑和楼层信息
    private Long buildingId;
    private String buildingName;
    private Long floorId;
    private String floorName;

    // 故障类型信息
    private Long faultTypeId;
    private String faultTypeName;

    // 管理员信息（可为空）
    private Long adminId;
    private String adminName;

    // 工程师信息（可为空）
    private Long engineerId;
    private String engineerName;
    @Schema(description = "工程师联系电话。默认可能为脱敏值；管理员、本人、同工单协作者可见明文", example = "139****9000")
    private String engineerPhone;

    // 业务状态标识
    private Boolean canAssignEngineer;
    private Boolean canAccept;
    private Boolean canComplete;
    private Boolean canCancel;
}