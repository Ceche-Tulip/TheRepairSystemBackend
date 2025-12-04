package org.trs.therepairsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RepairRatingRequest {

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1星")
    @Max(value = 5, message = "评分最高为5星")
    private Integer rating;

    @Size(max = 500, message = "评价内容不能超过500字符")
    private String comment;
}