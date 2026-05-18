package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.MidTermAppraisal;
import com.studentaffairs.backend.service.MidTermService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mid-term")
@CrossOrigin(origins = "*")
public class MidTermController {

    private final MidTermService midTermService;

    public MidTermController(MidTermService midTermService) {
        this.midTermService = midTermService;
    }

    @PostMapping
    public Result<MidTermAppraisal> submit(@RequestBody MidTermAppraisal appraisal) {
        return Result.success(midTermService.submit(appraisal));
    }

    @GetMapping
    public Result<List<MidTermAppraisal>> getByStudent(@RequestParam String studentId) {
        return Result.success(midTermService.getByStudent(studentId));
    }

    @GetMapping("/all")
    public Result<List<MidTermAppraisal>> getAll() {
        return Result.success(midTermService.getAll());
    }

    @GetMapping("/{id}")
    public Result<MidTermAppraisal> getById(@PathVariable Long id) {
        return midTermService.getById(id)
                .map(Result::success)
                .orElse(Result.error(404, "记录不存在"));
    }
}
