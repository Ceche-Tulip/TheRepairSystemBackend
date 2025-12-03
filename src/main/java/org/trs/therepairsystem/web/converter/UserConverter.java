package org.trs.therepairsystem.web.converter;



import org.trs.therepairsystem.entity.User;
import org.trs.therepairsystem.dto.response.UserDTO;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public class UserConverter {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        return dto;
    }

    public static Page<UserDTO> toDTOPage(Page<User> page) {
        return page.map(UserConverter::toDTO);
    }
}
