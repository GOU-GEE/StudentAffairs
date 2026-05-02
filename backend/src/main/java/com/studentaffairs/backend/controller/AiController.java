package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.service.AiService;
import org.springframework.web.bind.annotation.*;

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
    public Result<String> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error(400, "Message cannot be empty");
        }
        
        // 构建提示词上下文，注入辅导员系统设定的 System Prompt
        String systemPrompt = "你现在是智慧学工系统中的'AI 辅导员分身'。你的任务是根据辅导员的指令，分析学生的学业情况并给出建议。请用专业、简洁的语气回答：\n\n";
        String fullPrompt = systemPrompt + message;

        String response = aiService.generateAdvice(fullPrompt);
        return Result.success(response);
    }
}
