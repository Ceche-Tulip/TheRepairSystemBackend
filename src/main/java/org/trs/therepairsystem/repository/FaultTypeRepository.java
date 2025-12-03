package org.trs.therepairsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trs.therepairsystem.entity.FaultType;

import java.util.List;
import java.util.Optional;

/**
 * 故障类型数据访问层
 */
@Repository
public interface FaultTypeRepository extends JpaRepository<FaultType, Long> {

    /**
     * 根据名称查找故障类型（用于唯一性检查）
     */
    Optional<FaultType> findByName(String name);

    /**
     * 检查指定名称的故障类型是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查除指定ID外是否存在相同名称的故障类型（用于更新时的唯一性检查）
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * 根据名称模糊查询故障类型
     */
    @Query("SELECT ft FROM FaultType ft WHERE ft.name LIKE %:name%")
    Page<FaultType> findByNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * 根据名称模糊查询故障类型（返回列表）
     */
    List<FaultType> findByNameContainingIgnoreCase(String name);

    /**
     * 获取所有故障类型，按名称排序
     */
    List<FaultType> findAllByOrderByName();

    /**
     * 分页获取所有故障类型，按名称排序
     */
    Page<FaultType> findAllByOrderByName(Pageable pageable);

    /**
     * 统计故障类型总数
     */
    @Query("SELECT COUNT(ft) FROM FaultType ft")
    long countAll();

    /**
     * 检查故障类型是否被报修单引用（用于删除前检查）
     * 注意：这需要在报修单表创建后才能使用
     */
    @Query("SELECT COUNT(ro) > 0 FROM RepairOrder ro WHERE ro.faultType.id = :faultTypeId")
    boolean isReferencedByRepairOrders(@Param("faultTypeId") Long faultTypeId);
}