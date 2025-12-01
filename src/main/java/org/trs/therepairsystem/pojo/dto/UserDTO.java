package org.trs.therepairsystem.pojo.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String realName;
    private String phone;

}
