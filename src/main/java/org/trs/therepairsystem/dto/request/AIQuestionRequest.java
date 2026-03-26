package org.trs.therepairsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "AI提问请求")
public class AIQuestionRequest {
    @NotBlank(message = "问题内容不能为空")
    @Schema(description = "用户的问题", example = "如何处理教室空调漏水？")
    private String question;
}
