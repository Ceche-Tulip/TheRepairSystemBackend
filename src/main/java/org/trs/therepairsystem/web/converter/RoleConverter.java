package org.trs.therepairsystem.web.converter;

import org.springframework.data.domain.Page;
import org.trs.therepairsystem.pojo.UserRole;
import org.trs.therepairsystem.pojo.dto.RoleDTO;

import java.util.List;
import java.util.stream.Collectors;

public class RoleConverter {

    public static RoleDTO toDTO(UserRole role) {
        if (role == null) return null;
        
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        return dto;
    }

    public static List<RoleDTO> toDTOList(List<UserRole> roles) {
        if (roles == null) return null;
        
        return roles.stream()
                .map(RoleConverter::toDTO)
                .collect(Collectors.toList());
    }

    public static UserRole toEntity(RoleDTO dto) {
        if (dto == null) return null;
        
        UserRole role = new UserRole();
        role.setId(dto.getId());
        role.setRoleName(dto.getRoleName());
        return role;
    }
}