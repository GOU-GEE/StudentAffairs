package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error(400, "文件为空");

        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String newName = UUID.randomUUID().toString() + ext;
            Path target = Paths.get(UPLOAD_DIR, newName);
            Files.write(target, file.getBytes());

            Map<String, String> data = new HashMap<>();
            data.put("url", "/uploads/" + newName);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }
}
