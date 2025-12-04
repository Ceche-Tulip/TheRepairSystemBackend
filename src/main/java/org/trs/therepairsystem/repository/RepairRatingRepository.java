package org.trs.therepairsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trs.therepairsystem.entity.RepairRating;

import java.util.Optional;

@Repository
public interface RepairRatingRepository extends JpaRepository<RepairRating, Long> {

    // 根据工单ID查找评价
    Optional<RepairRating> findByOrderId(Long orderId);

    // 检查工单是否已有评价
    boolean existsByOrderId(Long orderId);

    // 查询平均评分
    @Query("SELECT AVG(r.rating) FROM RepairRating r")
    Double getAverageRating();

    // 查询工程师的平均评分
    @Query("SELECT AVG(r.rating) FROM RepairRating r WHERE r.order.engineer.id = :engineerId")
    Double findAverageRatingByEngineerId(@Param("engineerId") Long engineerId);

    // 根据工程师ID查询评价数量
    @Query("SELECT COUNT(r) FROM RepairRating r WHERE r.order.engineer.id = :engineerId")
    Long countByEngineerId(@Param("engineerId") Long engineerId);

    // 根据工程师ID和评分查询数量
    @Query("SELECT COUNT(r) FROM RepairRating r WHERE r.order.engineer.id = :engineerId AND r.rating = :rating")
    Long countByEngineerIdAndRating(@Param("engineerId") Long engineerId, @Param("rating") Integer rating);

    // 根据工程师ID分页查询评价
    @Query("SELECT r FROM RepairRating r WHERE r.order.engineer.id = :engineerId")
    Page<RepairRating> findByEngineerId(@Param("engineerId") Long engineerId, Pageable pageable);

    // 根据用户ID分页查询评价（通过工单的提交用户）
    @Query("SELECT r FROM RepairRating r WHERE r.order.submitUser.id = :userId")
    Page<RepairRating> findByRepairOrderSubmitUserId(@Param("userId") Long userId, Pageable pageable);

    // 根据评分范围查询
    Page<RepairRating> findByRatingBetween(Integer minRating, Integer maxRating, Pageable pageable);

    // 根据最低评分查询
    Page<RepairRating> findByRatingGreaterThanEqual(Integer minRating, Pageable pageable);

    // 根据最高评分查询
    Page<RepairRating> findByRatingLessThanEqual(Integer maxRating, Pageable pageable);
}