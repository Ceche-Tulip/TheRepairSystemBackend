package org.trs.therepairsystem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.trs.therepairsystem.dto.response.AIAnswerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${siliconflow.api-url}")
    private String apiUrl;

    @Value("${siliconflow.api-key}")
    private String apiKey;

    @Value("${siliconflow.model}")
    private String model;

    @Override
    public AIAnswerResponse askAI(String question) {
        // 提取关键词
        List<String> keywords = extractKeywords(question);

        // 构造prompt
        String prompt = buildPrompt(question, keywords);

        // 调用API
        String response = callSiliconFlowAPI(prompt);

        AIAnswerResponse answerResponse = new AIAnswerResponse();
        answerResponse.setAnswer(response);
        return answerResponse;
    }

    private List<String> extractKeywords(String question) {
        // 简单关键词提取：匹配常见词，如工单、故障、提交、状态等
        Pattern pattern = Pattern.compile("(工单|故障|提交|状态|维修|查询|登录|注册)");
        Matcher matcher = pattern.matcher(question);
        return matcher.results()
                .map(match -> match.group())
                .distinct()
                .toList();
    }

    private String buildPrompt(String question, List<String> keywords) {
        String keywordsStr = keywords.isEmpty() ? "无" : String.join(", ", keywords);
        return String.format("根据关键词[%s]，提供系统操作指南和故障自查步骤。问题：%s", keywordsStr, question);
    }

    private String callSiliconFlowAPI(String prompt) {
        try {
            // 构造请求体
            String requestBody = String.format("{\n" +
                    "    \"model\": \"%s\",\n" +
                    "    \"messages\": [\n" +
                    "        {\n" +
                    "            \"role\": \"system\",\n" +
                    "            \"content\": \"你是一个维修工单系统的AI助手。根据用户问题，提供系统操作指南和故障自查步骤。回答应简洁、实用，并基于关键词如工单、故障、提交、状态等。\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"role\": \"user\",\n" +
                    "            \"content\": \"%s\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}", model, prompt.replace("\"", "\\\""));

            // 调用API
            Mono<String> responseMono = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class);

            String responseJson = responseMono.block();
            log.info("SiliconFlow API response: {}", responseJson);

            // 解析回答
            JsonNode root = objectMapper.readTree(responseJson);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("Error calling SiliconFlow API", e);
            return "抱歉，AI服务暂时不可用，请稍后重试。";
        }
    }
}
