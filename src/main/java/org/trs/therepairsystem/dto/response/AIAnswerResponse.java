package org.trs.therepairsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AI回答响应")
public class AIAnswerResponse {
    @Schema(description = "AI回答内容", example = "建议先检查排水管是否堵塞，并提交维修工单附上现场照片。")
    private String answer;
}
