package org.trs.therepairsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairOrderStatsResponse {

    private long totalOrders;
    private long draftOrders;
    /**
     * 已提交（用户已提交、管理员还没派单）。
     * 这个状态之前漏统计，导致 totalOrders 与各状态之和对不上。
     */
    private long submittedOrders;
    private long pendingOrders;
    private long processingOrders;
    private long completedOrders;
    private long closedOrders;
    private long cancelledOrders;
    private long todayOrders;
    private long todayCompletedOrders;
}