package org.trs.therepairsystem.web.converter;

import org.trs.therepairsystem.dto.response.engineer.EngineerAreaDTO;
import org.trs.therepairsystem.entity.EngineerAreaRel;

public class EngineerAreaConverter {
    
    public static EngineerAreaDTO toDTO(EngineerAreaRel rel) {
        if (rel == null) {
            return null;
        }
        
        EngineerAreaDTO dto = new EngineerAreaDTO();
        dto.setId(rel.getId());
        dto.setEngineerId(rel.getEngineer().getId());
        dto.setEngineerName(rel.getEngineer().getRealName());
        dto.setEngineerUsername(rel.getEngineer().getUsername());
        dto.setFloorId(rel.getFloor().getId());
        dto.setFloorName(rel.getFloor().getName());
        dto.setFloorNo(rel.getFloor().getFloorNo());
        dto.setBuildingId(rel.getFloor().getBuilding().getId());
        dto.setBuildingName(rel.getFloor().getBuilding().getName());
        
        return dto;
    }
}