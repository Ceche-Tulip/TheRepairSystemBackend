package org.trs.therepairsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairRatingResponse {

    private Long id;
    private Long orderId;
    private Long engineerId;
    private String engineerName;
    private Long userId;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
}