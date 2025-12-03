package org.trs.therepairsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.trs.therepairsystem.entity.Building;
import org.trs.therepairsystem.repository.BuildingRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BuildingServiceImpl implements BuildingService {

    private static final Logger logger = LoggerFactory.getLogger(BuildingServiceImpl.class);

    @Autowired
    private BuildingRepository buildingRepository;

    @Override
    public List<Building> getAllBuildings() {
        return buildingRepository.findAllByOrderByName();
    }

    @Override
    public Page<Building> getBuildings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return buildingRepository.findAllByOrderByName(pageable);
    }

    @Override
    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("楼栋不存在，ID: " + id));
    }

    @Override
    @Transactional
    public Building createBuilding(Building building) {
        validateBuilding(building);
        if (buildingRepository.existsByName(building.getName())) {
            throw new DataIntegrityViolationException("楼栋名称已存在: " + building.getName());
        }
        return buildingRepository.save(building);
    }

    @Override
    @Transactional
    public Building updateBuilding(Long id, Building updatedBuilding) {
        Building existingBuilding = getBuildingById(id);
        validateBuilding(updatedBuilding);
        
        if (!existingBuilding.getName().equals(updatedBuilding.getName()) &&
            buildingRepository.existsByName(updatedBuilding.getName())) {
            throw new DataIntegrityViolationException("楼栋名称已存在: " + updatedBuilding.getName());
        }
        
        existingBuilding.setName(updatedBuilding.getName());
        return buildingRepository.save(existingBuilding);
    }

    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        Building building = getBuildingById(id);
        if (!canDelete(id)) {
            throw new DataIntegrityViolationException("无法删除楼栋，已被引用");
        }
        buildingRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return buildingRepository.existsByName(name);
    }

    @Override
    public Page<Building> searchBuildingsByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return buildingRepository.findByNameContaining(name, pageable);
    }

    @Override
    public boolean canDelete(Long id) {
        return true; // 暂时返回true，等Floor和RepairOrder创建后再完善
    }

    private void validateBuilding(Building building) {
        if (building == null) {
            throw new IllegalArgumentException("楼栋不能为空");
        }
        if (!StringUtils.hasText(building.getName())) {
            throw new IllegalArgumentException("楼栋名称不能为空");
        }
        if (building.getName().length() > 100) {
            throw new IllegalArgumentException("楼栋名称长度不能超过100字符");
        }
    }
}