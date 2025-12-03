package org.trs.therepairsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trs.therepairsystem.entity.Building;
import org.trs.therepairsystem.entity.Floor;
import org.trs.therepairsystem.repository.FloorRepository;
import org.trs.therepairsystem.repository.BuildingRepository;

import java.util.List;

@Service
@Transactional
public class FloorServiceImpl implements FloorService {

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Override
    @Transactional(readOnly = true)
    public Floor getById(Long id) {
        return floorRepository.findById(id).orElse(null);
    }

    @Override
    public Floor createFloor(Floor floor) {
        // 验证楼栋是否存在
        Building building = buildingRepository.findById(floor.getBuilding().getId()).orElse(null);
        if (building == null) {
            throw new RuntimeException("楼栋不存在");
        }

        // 检查楼层号是否已存在
        if (floorRepository.existsByBuildingIdAndFloorNo(building.getId(), floor.getFloorNo())) {
            throw new RuntimeException("该楼栋中楼层号已存在");
        }

        floor.setBuilding(building);
        return floorRepository.save(floor);
    }

    @Override
    public Floor updateFloor(Long id, Floor floor) {
        Floor existingFloor = getById(id);
        if (existingFloor == null) {
            throw new RuntimeException("楼层不存在");
        }

        // 如果更新了楼层号，检查是否冲突
        if (!existingFloor.getFloorNo().equals(floor.getFloorNo())) {
            if (floorRepository.existsByBuildingIdAndFloorNo(existingFloor.getBuilding().getId(), floor.getFloorNo())) {
                throw new RuntimeException("该楼栋中楼层号已存在");
            }
        }

        // 更新字段
        if (floor.getFloorNo() != null) {
            existingFloor.setFloorNo(floor.getFloorNo());
        }
        if (floor.getName() != null && !floor.getName().trim().isEmpty()) {
            existingFloor.setName(floor.getName().trim());
        }

        return floorRepository.save(existingFloor);
    }

    @Override
    public void deleteFloor(Long id) {
        Floor floor = getById(id);
        if (floor == null) {
            throw new RuntimeException("楼层不存在");
        }
        floorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Floor> getFloorsByBuildingId(Long buildingId) {
        return floorRepository.findByBuildingId(buildingId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Floor> listFloors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("building.id").and(Sort.by("floorNo")));
        return floorRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Floor> listFloorsByBuildingId(Long buildingId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("floorNo"));
        return floorRepository.findByBuildingId(buildingId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFloorNoExistsInBuilding(Long buildingId, Integer floorNo) {
        return floorRepository.existsByBuildingIdAndFloorNo(buildingId, floorNo);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFloorsByBuildingId(Long buildingId) {
        return floorRepository.countByBuildingId(buildingId);
    }
}