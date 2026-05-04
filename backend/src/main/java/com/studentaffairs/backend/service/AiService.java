package com.studentaffairs.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.model:qwen2.5:latest}")
    private String model;

    private final RestTemplate restTemplate;

    public AiService() {
        this.restTemplate = new RestTemplate();
    }

    public String generateAdvice(String prompt) {
        // 构建 Ollama 标准 /api/chat 请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("stream", false); // 关闭流式输出，等待完整响应

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "你是智慧学工系统的专属「AI 辅导员分身」。" +
                "你的职责是根据辅导员或学生的指令，提供专业的学业分析、生涯规划建议和心理支持。" +
                "请用温暖、专业且简洁的语气回答，适当使用 Markdown 格式（如 **加粗** 和分点列举）让回复更易读。");

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        requestBody.put("messages", List.of(systemMessage, userMessage));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            // 本地 Ollama 通常无需鉴权，如果远程部署需要 Bearer Token 可在此处加上

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // 解析 Ollama /api/chat 响应格式
            // 响应结构: { "message": { "role": "assistant", "content": "..." } }
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("message")) {
                Map<String, Object> message = (Map<String, Object>) body.get("message");
                return (String) message.get("content");
            }
            return "AI 响应解析失败，请检查 Ollama 服务状态。";
        } catch (Exception e) {
            e.printStackTrace();
            return "AI 服务调用失败: " + e.getMessage() +
                   "\n请确认：1) Ollama 本地服务已启动 (ollama serve) 2) 模型已下载 (ollama pull " + model + ")";
        }
    }
}
