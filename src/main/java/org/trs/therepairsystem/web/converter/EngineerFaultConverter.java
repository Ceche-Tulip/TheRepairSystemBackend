package org.trs.therepairsystem.web.converter;

import org.trs.therepairsystem.dto.response.engineer.EngineerFaultDTO;
import org.trs.therepairsystem.entity.EngineerFaultRel;

public class EngineerFaultConverter {
    
    public static EngineerFaultDTO toDTO(EngineerFaultRel rel) {
        if (rel == null) {
            return null;
        }
        
        EngineerFaultDTO dto = new EngineerFaultDTO();
        dto.setId(rel.getId());
        dto.setEngineerId(rel.getEngineer().getId());
        dto.setEngineerName(rel.getEngineer().getRealName());
        dto.setEngineerUsername(rel.getEngineer().getUsername());
        dto.setFaultTypeId(rel.getFaultType().getId());
        dto.setFaultTypeName(rel.getFaultType().getName());
        dto.setFaultTypeColor(rel.getFaultType().getColor());
        dto.setFaultTypeDescription(rel.getFaultType().getDescription());
        
        return dto;
    }
}