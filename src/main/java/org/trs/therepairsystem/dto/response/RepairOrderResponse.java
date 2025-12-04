package org.trs.therepairsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    // 业务状态标识
    private Boolean canAssignEngineer;
    private Boolean canAccept;
    private Boolean canComplete;
    private Boolean canCancel;
}