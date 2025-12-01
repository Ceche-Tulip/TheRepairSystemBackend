package org.trs.therepairsystem.web.converter;



import org.trs.therepairsystem.pojo.User;
import org.trs.therepairsystem.pojo.dto.UserDTO;
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
