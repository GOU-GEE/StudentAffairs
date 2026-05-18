package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.MidTermAppraisal;
import com.studentaffairs.backend.repository.MidTermAppraisalRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mid-term")
@CrossOrigin(origins = "*")
public class MidTermController {

    private final MidTermAppraisalRepository repository;

    public MidTermController(MidTermAppraisalRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Result<MidTermAppraisal> submit(@RequestBody MidTermAppraisal appraisal) {
        appraisal.setSubmitTime(LocalDateTime.now());
        return Result.success(repository.save(appraisal));
    }

    @GetMapping
    public Result<List<MidTermAppraisal>> getByStudent(@RequestParam String studentId) {
        return Result.success(repository.findByStudentId(studentId));
    }

    @GetMapping("/all")
    public Result<List<MidTermAppraisal>> getAll() {
        return Result.success(repository.findAllByOrderBySubmitTimeDesc());
    }

    @GetMapping("/{id}")
    public Result<MidTermAppraisal> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "记录不存在"));
    }
}
