package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
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
}
