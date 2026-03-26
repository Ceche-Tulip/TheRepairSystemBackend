package org.trs.therepairsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.dto.request.AIQuestionRequest;
import org.trs.therepairsystem.dto.response.AIAnswerResponse;
import org.trs.therepairsystem.service.AIService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI助手", description = "AI问答辅助接口")
@SecurityRequirement(name = "Bearer Authentication")
public class AIController {

    private final AIService aiService;

    @PostMapping("/ask")
    @Operation(summary = "AI提问", description = "用户提交问题并获取AI回答")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "提问成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证或Token失效"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "500", description = "AI服务调用失败")
    })
    public ResponseEntity<AIAnswerResponse> askAI(
            @Parameter(description = "AI提问请求")
            @Valid @RequestBody AIQuestionRequest request) {
        AIAnswerResponse response = aiService.askAI(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}
