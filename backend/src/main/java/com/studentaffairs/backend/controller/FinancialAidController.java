package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.WorkStudyApplication;
import com.studentaffairs.backend.entity.WorkStudyJob;
import com.studentaffairs.backend.service.FinancialAidService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/financial-aid")
@CrossOrigin(origins = "*")
public class FinancialAidController {

    private final FinancialAidService financialAidService;

    public FinancialAidController(FinancialAidService financialAidService) {
        this.financialAidService = financialAidService;
    }

    @GetMapping("/jobs")
    public Result<List<WorkStudyJob>> getJobs() {
        return Result.success(financialAidService.getAllJobs());
    }

    @PostMapping("/apply")
    public Result<String> applyJob(@RequestBody Map<String, Object> payload) {
        Long jobId = Long.valueOf(payload.get("jobId").toString());
        String studentId = payload.get("studentId").toString();

        try {
            financialAidService.applyForJob(jobId, studentId);
            return Result.success("申请成功，已进入审核流程");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/jobs")
    public Result<WorkStudyJob> createJob(@RequestBody WorkStudyJob job) {
        return Result.success(financialAidService.createJob(job));
    }

    @GetMapping("/applications")
    public Result<List<WorkStudyApplication>> getApplications() {
        return Result.success(financialAidService.getAllApplications());
    }

    @PutMapping("/applications/{id}/review")
    public Result<String> reviewApplication(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        try {
            financialAidService.reviewApplication(id, status);
            return Result.success("审核完成");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(financialAidService.getDashboardStats());
    }
}
