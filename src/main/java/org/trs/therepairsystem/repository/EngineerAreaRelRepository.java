package org.trs.therepairsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.trs.therepairsystem.entity.EngineerAreaRel;

import java.util.List;

public interface EngineerAreaRelRepository extends JpaRepository<EngineerAreaRel, Long> {
    
    /**
     * 查询工程师负责的所有区域
     */
    List<EngineerAreaRel> findByEngineerId(Long engineerId);
    
    /**
     * 查询楼层分配给哪些工程师
     */
    List<EngineerAreaRel> findByFloorId(Long floorId);
    
    /**
     * 删除工程师的所有区域分配
     */
    void deleteByEngineerId(Long engineerId);
    
    /**
     * 删除特定的工程师-楼层分配
     */
    void deleteByEngineerIdAndFloorId(Long engineerId, Long floorId);
    
    /**
     * 检查工程师是否已分配到某楼层
     */
    boolean existsByEngineerIdAndFloorId(Long engineerId, Long floorId);
    
    /**
     * 查询工程师数量按楼层分组
     */
    @Query("SELECT f.id, COUNT(ear) FROM Floor f LEFT JOIN EngineerAreaRel ear ON f.id = ear.floor.id GROUP BY f.id")
    List<Object[]> countEngineersByFloor();
}