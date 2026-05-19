package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.security.JwtProvider;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtProvider jwtProvider;

    private static final Map<String, Map<String, String>> ACCOUNTS = new HashMap<>();

    static {
        addAccount("admin", "123456", "admin", "系统管理员", null);
        addAccount("t001", "123456", "teacher", "李辅导员", "T001");
        addAccount("f001", "123456", "financial", "资助管理中心", null);
        addAccount("y001", "123456", "youth", "第二课堂/团委", null);
        addAccount("a001", "123456", "academic", "教务管理", null);
        addAccount("s001", "123456", "student", "张小明", "202301042");
    }

    private static void addAccount(String username, String password, String role, String name, String userId) {
        Map<String, String> info = new HashMap<>();
        info.put("password", password);
        info.put("role", role);
        info.put("name", name);
        if (userId != null) info.put("userId", userId);
        ACCOUNTS.put(username, info);
    }

    public AuthController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "");

        if (username.isEmpty() || password.isEmpty()) {
            return Result.error(400, "账号和密码不能为空");
        }

        Map<String, String> account = ACCOUNTS.get(username);
        if (account == null || !account.get("password").equals(password)) {
            return Result.error(401, "账号或密码错误");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("role", account.get("role"));
        data.put("name", account.get("name"));
        String jwt = jwtProvider.generateToken(
            account.getOrDefault("userId", ""), username, account.get("role"));
        data.put("token", jwt);
        if (account.containsKey("userId")) data.put("userId", account.get("userId"));
        return Result.success(data);
    }
}
