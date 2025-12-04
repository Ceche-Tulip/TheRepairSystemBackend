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
    private long pendingOrders;
    private long processingOrders;
    private long completedOrders;
    private long closedOrders;
    private long cancelledOrders;
    private long todayOrders;
    private long todayCompletedOrders;
}