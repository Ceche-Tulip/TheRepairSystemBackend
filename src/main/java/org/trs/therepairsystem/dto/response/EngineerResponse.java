package org.trs.therepairsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngineerResponse {

    private Long id;
    private String username;
    private String realName;
    private String phone;
}