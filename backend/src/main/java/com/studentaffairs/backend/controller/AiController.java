package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.service.AiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // 允许前端跨域访问
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, Object> body) {
        String message = (String) body.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error(400, "消息内容不能为空");
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) body.get("history");
        @SuppressWarnings("unchecked")
        Map<String, Object> classStats = (Map<String, Object>) body.get("classStats");

        String response = aiService.generateAdvice(message, history, classStats);
        return Result.success(response);
    }

    // ==================== 生涯小助手多轮对话 ====================
    @PostMapping("/career/chat")
    public Result<String> careerChat(@RequestBody Map<String, Object> body) {
        String message = (String) body.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error(400, "消息内容不能为空");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) body.get("history");
        @SuppressWarnings("unchecked")
        Map<String, Double> hollandScores = (Map<String, Double>) body.get("hollandScores");
        String mbtiType = (String) body.get("mbtiType");
        @SuppressWarnings("unchecked")
        List<Double> valueScores = (List<Double>) body.get("valueScores");
        @SuppressWarnings("unchecked")
        List<Double> skillScores = (List<Double>) body.get("skillScores");
        @SuppressWarnings("unchecked")
        List<String> matchedJobs = (List<String>) body.get("matchedJobs");

        String response = aiService.generateCareerChat(
                message, history, hollandScores, mbtiType, valueScores, skillScores, matchedJobs
        );
        return Result.success(response);
    }

    // ==================== 一键生成深度生涯报告 ====================
    @PostMapping("/career/report")
    public Result<String> careerReport(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        Map<String, Double> hollandScores = (Map<String, Double>) body.get("hollandScores");
        String mbtiType = (String) body.get("mbtiType");
        @SuppressWarnings("unchecked")
        List<Double> valueScores = (List<Double>) body.get("valueScores");
        @SuppressWarnings("unchecked")
        List<Double> skillScores = (List<Double>) body.get("skillScores");
        @SuppressWarnings("unchecked")
        List<String> matchedJobs = (List<String>) body.get("matchedJobs");

        String response = aiService.generateCareerReport(
                hollandScores, mbtiType, valueScores, skillScores, matchedJobs
        );
        return Result.success(response);
    }
}
