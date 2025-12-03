package org.trs.therepairsystem.web.converter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.trs.therepairsystem.dto.response.FloorDTO;
import org.trs.therepairsystem.entity.Floor;

import java.util.List;
import java.util.stream.Collectors;

public class FloorConverter {

    public static FloorDTO toDTO(Floor floor) {
        if (floor == null) {
            return null;
        }

        FloorDTO dto = new FloorDTO();
        dto.setId(floor.getId());
        dto.setFloorNo(floor.getFloorNo());
        dto.setName(floor.getName());
        
        // 设置楼栋信息
        if (floor.getBuilding() != null) {
            dto.setBuilding(BuildingConverter.toDTO(floor.getBuilding()));
        }

        return dto;
    }

    public static List<FloorDTO> toDTOList(List<Floor> floors) {
        if (floors == null) {
            return null;
        }
        return floors.stream()
                .map(FloorConverter::toDTO)
                .collect(Collectors.toList());
    }

    public static Page<FloorDTO> toDTOPage(Page<Floor> floorPage) {
        if (floorPage == null) {
            return null;
        }
        List<FloorDTO> dtoList = toDTOList(floorPage.getContent());
        return new PageImpl<>(dtoList, floorPage.getPageable(), floorPage.getTotalElements());
    }

    public static Floor toEntity(FloorDTO dto) {
        if (dto == null) {
            return null;
        }

        Floor floor = new Floor();
        floor.setId(dto.getId());
        floor.setFloorNo(dto.getFloorNo());
        floor.setName(dto.getName());
        
        // 注意：楼栋信息需要单独设置
        if (dto.getBuilding() != null) {
            floor.setBuilding(BuildingConverter.toEntity(dto.getBuilding()));
        }

        return floor;
    }
}