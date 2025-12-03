package org.trs.therepairsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.trs.therepairsystem.entity.Floor;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    /**
     * 根据楼栋ID查询楼层
     */
    List<Floor> findByBuildingId(Long buildingId);

    /**
     * 根据楼栋ID分页查询楼层
     */
    Page<Floor> findByBuildingId(Long buildingId, Pageable pageable);

    /**
     * 检查楼栋中是否已存在指定楼层号
     */
    boolean existsByBuildingIdAndFloorNo(Long buildingId, Integer floorNo);

    /**
     * 根据楼栋ID和楼层号查询楼层
     */
    Floor findByBuildingIdAndFloorNo(Long buildingId, Integer floorNo);

    /**
     * 查询指定楼栋的楼层数量
     */
    @Query("SELECT COUNT(f) FROM Floor f WHERE f.building.id = :buildingId")
    long countByBuildingId(@Param("buildingId") Long buildingId);
}