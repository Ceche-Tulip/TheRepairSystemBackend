package org.trs.therepairsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trs.therepairsystem.dto.request.AIQuestionRequest;
import org.trs.therepairsystem.dto.response.AIAnswerResponse;
import org.trs.therepairsystem.service.AIService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/ask")
    public ResponseEntity<AIAnswerResponse> askAI(@RequestBody AIQuestionRequest request) {
        AIAnswerResponse response = aiService.askAI(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}
