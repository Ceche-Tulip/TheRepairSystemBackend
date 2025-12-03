package org.trs.therepairsystem.web.converter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.trs.therepairsystem.entity.Building;
import org.trs.therepairsystem.dto.request.building.BuildingCreateRequest;
import org.trs.therepairsystem.dto.response.BuildingDTO;
import org.trs.therepairsystem.dto.request.building.BuildingUpdateRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 楼栋转换器
 */
public class BuildingConverter {

    public static BuildingDTO toDTO(Building building) {
        if (building == null) {
            return null;
        }
        return new BuildingDTO(building.getId(), building.getName());
    }

    public static List<BuildingDTO> toDTOList(List<Building> buildings) {
        if (buildings == null) {
            return null;
        }
        return buildings.stream()
                .map(BuildingConverter::toDTO)
                .collect(Collectors.toList());
    }

    public static Page<BuildingDTO> toDTOPage(Page<Building> buildingPage) {
        if (buildingPage == null) {
            return null;
        }
        List<BuildingDTO> dtoList = buildingPage.getContent().stream()
                .map(BuildingConverter::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, buildingPage.getPageable(), buildingPage.getTotalElements());
    }

    public static Building fromCreateRequest(BuildingCreateRequest request) {
        if (request == null) {
            return null;
        }
        Building building = new Building();
        building.setName(request.getName());
        return building;
    }

    public static void updateFromRequest(Building building, BuildingUpdateRequest request) {
        if (building == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            building.setName(request.getName());
        }
    }

    public static Building toEntity(BuildingDTO dto) {
        if (dto == null) {
            return null;
        }
        Building building = new Building();
        building.setId(dto.getId());
        building.setName(dto.getName());
        return building;
    }
}