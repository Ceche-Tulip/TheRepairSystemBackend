package org.trs.therepairsystem.service;

import org.springframework.data.domain.Page;
import org.trs.therepairsystem.entity.Building;

import java.util.List;

/**
 * 楼栋业务逻辑接口
 */
public interface BuildingService {

    List<Building> getAllBuildings();
    
    Page<Building> getBuildings(int page, int size);
    
    Building getBuildingById(Long id);
    
    Building createBuilding(Building building);
    
    Building updateBuilding(Long id, Building updatedBuilding);
    
    void deleteBuilding(Long id);
    
    boolean existsByName(String name);
    
    Page<Building> searchBuildingsByName(String name, int page, int size);
    
    boolean canDelete(Long id);
}