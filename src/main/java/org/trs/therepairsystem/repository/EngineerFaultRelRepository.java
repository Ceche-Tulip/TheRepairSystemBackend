package org.trs.therepairsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.trs.therepairsystem.entity.EngineerFaultRel;

import java.util.List;

public interface EngineerFaultRelRepository extends JpaRepository<EngineerFaultRel, Long> {
    
    /**
     * 查询工程师负责的所有故障类型
     */
    List<EngineerFaultRel> findByEngineerId(Long engineerId);
    
    /**
     * 查询故障类型分配给哪些工程师
     */
    List<EngineerFaultRel> findByFaultTypeId(Long faultTypeId);
    
    /**
     * 删除工程师的所有故障类型分配
     */
    void deleteByEngineerId(Long engineerId);
    
    /**
     * 删除特定的工程师-故障类型分配
     */
    void deleteByEngineerIdAndFaultTypeId(Long engineerId, Long faultTypeId);
    
    /**
     * 检查工程师是否已分配到某故障类型
     */
    boolean existsByEngineerIdAndFaultTypeId(Long engineerId, Long faultTypeId);
    
    /**
     * 查询工程师数量按故障类型分组
     */
    @Query("SELECT ft.id, COUNT(efr) FROM FaultType ft LEFT JOIN EngineerFaultRel efr ON ft.id = efr.faultType.id GROUP BY ft.id")
    List<Object[]> countEngineersByFaultType();
}