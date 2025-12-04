package org.trs.therepairsystem.dto.request;

import lombok.Data;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;

@Data
public class RepairOrderQueryRequest {

    private RepairOrderStatus status;
    private Long userId;
    private Long engineerId;
    private Long buildingId;
    private Long faultTypeId;
    private String startTime; // yyyy-MM-dd HH:mm:ss
    private String endTime;   // yyyy-MM-dd HH:mm:ss
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createTime";
    private String sortDir = "DESC";
}