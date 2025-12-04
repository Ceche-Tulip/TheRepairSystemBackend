package org.trs.therepairsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.trs.therepairsystem.dto.response.RepairRatingResponse;

/**
 * 维修评价服务接口
 */
public interface RepairRatingService {

    /**
     * 根据工单ID获取评价
     */
    RepairRatingResponse getRatingByOrderId(Long orderId);

    /**
     * 获取工程师评价统计
     */
    Object getEngineerRatingStats(Long engineerId);

    /**
     * 获取工程师的所有评价
     */
    Page<RepairRatingResponse> getEngineerRatings(Long engineerId, Pageable pageable);

    /**
     * 获取用户提交的所有评价
     */
    Page<RepairRatingResponse> getUserRatings(Long userId, Pageable pageable);

    /**
     * 获取所有评价（管理员）
     */
    Page<RepairRatingResponse> getAllRatings(Integer minRating, Integer maxRating, Pageable pageable);
}