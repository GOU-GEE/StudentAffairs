package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.StudentApplication;
import com.studentaffairs.backend.repository.StudentApplicationRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final StudentApplicationRepository repository;

    public ApplicationController(StudentApplicationRepository repository) {
        this.repository = repository;
    }

    /** 学生查询自己的所有申请 */
    @GetMapping("/student/{studentId}")
    public Result<List<StudentApplication>> getByStudent(@PathVariable String studentId) {
        return Result.success(repository.findByStudentIdOrderByApplyTimeDesc(studentId));
    }

    /** 教师查询所有待审批的申请 */
    @GetMapping("/pending")
    public Result<List<StudentApplication>> getPending() {
        return Result.success(repository.findByStatusOrderByApplyTimeDesc("PENDING"));
    }

    /** 教师查询所有申请 */
    @GetMapping("/all")
    public Result<List<StudentApplication>> getAll() {
        return Result.success(repository.findAllByOrderByApplyTimeDesc());
    }

    /** 学生提交新申请 */
    @PostMapping
    public Result<StudentApplication> submit(@RequestBody StudentApplication application) {
        application.setStatus("PENDING");
        application.setApplyTime(LocalDateTime.now());
        return Result.success(repository.save(application));
    }

    /** 辅导员审批（状态机流转：PENDING -> APPROVED/REJECTED）*/
    @PutMapping("/{id}/review")
    public Result<String> review(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return repository.findById(id).map(app -> {
            String newStatus = body.get("status");
            if (!"APPROVED".equals(newStatus) && !"REJECTED".equals(newStatus)) {
                return Result.<String>error(400, "无效的审批状态");
            }
            app.setStatus(newStatus);
            app.setReviewComment(body.get("comment"));
            app.setReviewerName(body.getOrDefault("reviewerName", "辅导员"));
            app.setReviewTime(LocalDateTime.now());
            repository.save(app);
            return Result.<String>success("APPROVED".equals(newStatus) ? "申请已批准" : "申请已驳回");
        }).orElse(Result.<String>error(404, "申请不存在"));
    }
}
