package org.trs.therepairsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.trs.therepairsystem.common.exception.BusinessException;
import org.trs.therepairsystem.dto.response.RepairRatingResponse;
import org.trs.therepairsystem.entity.RepairRating;
import org.trs.therepairsystem.repository.RepairRatingRepository;
import org.trs.therepairsystem.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairRatingServiceImpl implements RepairRatingService {

    private final RepairRatingRepository repairRatingRepository;
    private final UserRepository userRepository;

    @Override
    public RepairRatingResponse getRatingByOrderId(Long orderId) {
        RepairRating rating = repairRatingRepository.findByOrderId(orderId)
            .orElseThrow(() -> new BusinessException("该工单暂无评价"));
        
        return convertToResponse(rating);
    }

    @Override
    public Object getEngineerRatingStats(Long engineerId) {
        // 验证工程师存在
        if (!userRepository.existsById(engineerId)) {
            throw new BusinessException("工程师不存在");
        }

        // 获取统计数据
        Long totalCount = repairRatingRepository.countByEngineerId(engineerId);
        Double averageRating = repairRatingRepository.findAverageRatingByEngineerId(engineerId);
        
        // 获取各评分的数量统计
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("averageRating", averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0);
        
        // 各评分数量统计
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = repairRatingRepository.countByEngineerIdAndRating(engineerId, i);
            ratingDistribution.put(i, count);
        }
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }

    @Override
    public Page<RepairRatingResponse> getEngineerRatings(Long engineerId, Pageable pageable) {
        // 验证工程师存在
        if (!userRepository.existsById(engineerId)) {
            throw new BusinessException("工程师不存在");
        }

        Page<RepairRating> ratings = repairRatingRepository.findByEngineerId(engineerId, pageable);
        return ratings.map(this::convertToResponse);
    }

    @Override
    public Page<RepairRatingResponse> getUserRatings(Long userId, Pageable pageable) {
        // 验证用户存在
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }

        Page<RepairRating> ratings = repairRatingRepository.findByRepairOrderSubmitUserId(userId, pageable);
        return ratings.map(this::convertToResponse);
    }

    @Override
    public Page<RepairRatingResponse> getAllRatings(Integer minRating, Integer maxRating, Pageable pageable) {
        Page<RepairRating> ratings;
        
        if (minRating != null && maxRating != null) {
            ratings = repairRatingRepository.findByRatingBetween(minRating, maxRating, pageable);
        } else if (minRating != null) {
            ratings = repairRatingRepository.findByRatingGreaterThanEqual(minRating, pageable);
        } else if (maxRating != null) {
            ratings = repairRatingRepository.findByRatingLessThanEqual(maxRating, pageable);
        } else {
            ratings = repairRatingRepository.findAll(pageable);
        }
        
        return ratings.map(this::convertToResponse);
    }

    private RepairRatingResponse convertToResponse(RepairRating rating) {
        return RepairRatingResponse.builder()
            .id(rating.getId())
            .orderId(rating.getOrder().getId())
            .engineerId(rating.getOrder().getEngineer().getId())
            .engineerName(rating.getOrder().getEngineer().getRealName())
            .userId(rating.getOrder().getSubmitUser().getId())
            .userName(rating.getOrder().getSubmitUser().getRealName())
            .rating(rating.getRating())
            .comment(rating.getComment())
            .createTime(rating.getCreateTime())
            .build();
    }
}