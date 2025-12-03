package org.trs.therepairsystem.service;

import org.springframework.data.domain.Page;
import org.trs.therepairsystem.entity.Floor;

import java.util.List;

public interface FloorService {

    /**
     * 根据ID获取楼层
     */
    Floor getById(Long id);

    /**
     * 创建楼层
     */
    Floor createFloor(Floor floor);

    /**
     * 更新楼层
     */
    Floor updateFloor(Long id, Floor floor);

    /**
     * 删除楼层
     */
    void deleteFloor(Long id);

    /**
     * 根据楼栋ID获取楼层列表
     */
    List<Floor> getFloorsByBuildingId(Long buildingId);

    /**
     * 分页获取楼层列表
     */
    Page<Floor> listFloors(int page, int size);

    /**
     * 分页获取指定楼栋的楼层列表
     */
    Page<Floor> listFloorsByBuildingId(Long buildingId, int page, int size);

    /**
     * 检查楼栋中楼层号是否已存在
     */
    boolean isFloorNoExistsInBuilding(Long buildingId, Integer floorNo);

    /**
     * 获取楼栋的楼层数量
     */
    long countFloorsByBuildingId(Long buildingId);
}