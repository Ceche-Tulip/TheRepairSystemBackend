package org.trs.therepairsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trs.therepairsystem.entity.RepairOrder;
import org.trs.therepairsystem.common.enums.RepairOrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {

    // 基础查询方法
    List<RepairOrder> findByStatusOrderByCreateTimeDesc(RepairOrderStatus status);
    
    Page<RepairOrder> findByStatusOrderByCreateTimeDesc(RepairOrderStatus status, Pageable pageable);

    // 用户相关查询
    Page<RepairOrder> findBySubmitUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.submitUser.id = :userId AND ro.status = :status ORDER BY ro.createTime DESC")
    Page<RepairOrder> findByUserIdAndStatus(@Param("userId") Long userId, 
                                           @Param("status") RepairOrderStatus status, 
                                           Pageable pageable);

    // 工程师相关查询
    Page<RepairOrder> findByEngineerIdOrderByCreateTimeDesc(Long engineerId, Pageable pageable);
    
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.engineer.id = :engineerId AND ro.status = :status ORDER BY ro.createTime DESC")
    Page<RepairOrder> findByEngineerIdAndStatus(@Param("engineerId") Long engineerId, 
                                               @Param("status") RepairOrderStatus status, 
                                               Pageable pageable);

    // 待分配的工单（未分配工程师的 SUBMITTED 状态）
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.status = 'SUBMITTED' AND ro.engineer IS NULL ORDER BY ro.createTime ASC")
    Page<RepairOrder> findUnassignedPendingOrders(Pageable pageable);

    // 根据故障类型查询
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.faultType.id = :faultTypeId ORDER BY ro.createTime DESC")
    Page<RepairOrder> findByFaultTypeId(@Param("faultTypeId") Long faultTypeId, Pageable pageable);

    // 根据建筑和楼层查询
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.building.id = :buildingId AND ro.floor.id = :floorId ORDER BY ro.createTime DESC")
    Page<RepairOrder> findByBuildingAndFloor(@Param("buildingId") Long buildingId, 
                                            @Param("floorId") Long floorId, 
                                            Pageable pageable);

    // 工程师在指定故障类型范围内的工单
    @Query("""
        SELECT ro FROM RepairOrder ro 
        WHERE ro.engineer.id = :engineerId 
        AND ro.faultType.id IN (
            SELECT efr.faultType.id FROM EngineerFaultRel efr 
            WHERE efr.engineer.id = :engineerId
        )
        ORDER BY ro.createTime DESC
        """)
    Page<RepairOrder> findByEngineerInAssignedFaultTypes(@Param("engineerId") Long engineerId, 
                                                        Pageable pageable);

    // 工程师在指定区域范围内的工单
    @Query("""
        SELECT ro FROM RepairOrder ro 
        WHERE ro.engineer.id = :engineerId 
        AND ro.floor.id IN (
            SELECT ear.floor.id FROM EngineerAreaRel ear 
            WHERE ear.engineer.id = :engineerId
        )
        ORDER BY ro.createTime DESC
        """)
    Page<RepairOrder> findByEngineerInAssignedAreas(@Param("engineerId") Long engineerId, 
                                                   Pageable pageable);

    // 管理员查询 - 所有工单按状态和时间
    @Query("SELECT ro FROM RepairOrder ro WHERE (:status IS NULL OR ro.status = :status) ORDER BY ro.createTime DESC")
    Page<RepairOrder> findAllByStatusOptional(@Param("status") RepairOrderStatus status, 
                                             Pageable pageable);

    // 时间范围查询
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.createTime BETWEEN :startTime AND :endTime ORDER BY ro.createTime DESC")
    Page<RepairOrder> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                             @Param("endTime") LocalDateTime endTime, 
                                             Pageable pageable);

    // 统计查询
    @Query("SELECT COUNT(ro) FROM RepairOrder ro WHERE ro.status = :status")
    long countByStatus(@Param("status") RepairOrderStatus status);

    @Query("SELECT COUNT(ro) FROM RepairOrder ro WHERE ro.submitUser.id = :userId AND ro.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") RepairOrderStatus status);

    @Query("SELECT COUNT(ro) FROM RepairOrder ro WHERE ro.engineer.id = :engineerId AND ro.status = :status")
    long countByEngineerIdAndStatus(@Param("engineerId") Long engineerId, @Param("status") RepairOrderStatus status);

    // 今日统计
    @Query("SELECT COUNT(ro) FROM RepairOrder ro WHERE DATE(ro.createTime) = CURRENT_DATE")
    long countTodayOrders();

    @Query("SELECT COUNT(ro) FROM RepairOrder ro WHERE DATE(ro.createTime) = CURRENT_DATE AND ro.status = :status")
    long countTodayOrdersByStatus(@Param("status") RepairOrderStatus status);

    // 可分配的工程师查询辅助（根据故障类型和区域）
    @Query("""
        SELECT DISTINCT u.id FROM User u 
        INNER JOIN UserRoleRel urr ON u.id = urr.user.id
        INNER JOIN UserRole ur ON urr.role.id = ur.id
        WHERE ur.roleName = 'ENGINEER'
        AND u.id IN (
            SELECT efr.engineer.id FROM EngineerFaultRel efr 
            WHERE efr.faultType.id = :faultTypeId
        )
        AND u.id IN (
            SELECT ear.engineer.id FROM EngineerAreaRel ear 
            WHERE ear.floor.id = :floorId
        )
        """)
    List<Long> findAvailableEngineerIds(@Param("faultTypeId") Long faultTypeId, 
                                       @Param("floorId") Long floorId);

    // 复杂条件查询
    @Query("""
        SELECT ro FROM RepairOrder ro 
        WHERE (:userId IS NULL OR ro.submitUser.id = :userId)
        AND (:engineerId IS NULL OR ro.engineer.id = :engineerId)
        AND (:status IS NULL OR ro.status = :status)
        AND (:buildingId IS NULL OR ro.building.id = :buildingId)
        AND (:faultTypeId IS NULL OR ro.faultType.id = :faultTypeId)
        ORDER BY ro.createTime DESC
        """)
    Page<RepairOrder> findByConditions(@Param("userId") Long userId,
                                      @Param("engineerId") Long engineerId,
                                      @Param("status") RepairOrderStatus status,
                                      @Param("buildingId") Long buildingId,
                                      @Param("faultTypeId") Long faultTypeId,
                                      Pageable pageable);
}