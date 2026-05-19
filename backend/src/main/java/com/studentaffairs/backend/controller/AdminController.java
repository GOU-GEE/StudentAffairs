package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.AcademicRecord;
import com.studentaffairs.backend.entity.StudentProfile;
import com.studentaffairs.backend.repository.AcademicRecordRepository;
import com.studentaffairs.backend.repository.StudentProfileRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final StudentProfileRepository profileRepo;
    private final AcademicRecordRepository academicRepo;

    public AdminController(StudentProfileRepository profileRepo, AcademicRecordRepository academicRepo) {
        this.profileRepo = profileRepo;
        this.academicRepo = academicRepo;
    }

    // ==================== 学生档案管理 ====================

    /** 获取所有学生档案 */
    @GetMapping("/students")
    public Result<List<StudentProfile>> getAllStudents() {
        return Result.success(profileRepo.findAll());
    }

    /** 新增学生档案 */
    @PostMapping("/students")
    public Result<StudentProfile> addStudent(@RequestBody StudentProfile profile) {
        if (profile.getStudentId() == null || profile.getStudentId().isBlank()) {
            return Result.error(400, "学号不能为空");
        }
        if (profileRepo.existsByStudentId(profile.getStudentId())) {
            return Result.error(400, "该学号已存在");
        }
        return Result.success(profileRepo.save(profile));
    }

    /** 更新学生档案 */
    @PutMapping("/students/{id}")
    public Result<StudentProfile> updateStudent(@PathVariable Long id, @RequestBody StudentProfile updated) {
        return profileRepo.findById(id).map(p -> {
            if (updated.getName() != null) p.setName(updated.getName());
            if (updated.getAvatar() != null) p.setAvatar(updated.getAvatar());
            if (updated.getCollege() != null) p.setCollege(updated.getCollege());
            if (updated.getMajor() != null) p.setMajor(updated.getMajor());
            if (updated.getGradeClass() != null) p.setGradeClass(updated.getGradeClass());
            if (updated.getIdCard() != null) p.setIdCard(updated.getIdCard());
            if (updated.getExtraInfo() != null) p.setExtraInfo(updated.getExtraInfo());
            if (updated.getStudentId() != null && !updated.getStudentId().equals(p.getStudentId())) {
                if (profileRepo.existsByStudentId(updated.getStudentId())) {
                    // 不抛异常，仅忽略学号修改
                } else {
                    p.setStudentId(updated.getStudentId());
                }
            }
            return Result.success(profileRepo.save(p));
        }).orElse(Result.error(404, "学生档案不存在"));
    }

    /** 删除学生档案 */
    @DeleteMapping("/students/{id}")
    public Result<String> deleteStudent(@PathVariable Long id) {
        if (!profileRepo.existsById(id)) {
            return Result.error(404, "学生档案不存在");
        }
        profileRepo.deleteById(id);
        return Result.success("删除成功");
    }

    // ==================== 成绩管理 ====================

    /** 获取所有成绩记录 */
    @GetMapping("/grades")
    public Result<List<AcademicRecord>> getAllGrades() {
        return Result.success(academicRepo.findAll());
    }

    /** 模拟批量导入成绩 */
    @PostMapping("/grades/import")
    public Result<Map<String, Object>> importGrades(@RequestBody List<AcademicRecord> records) {
        int success = 0;
        int failed = 0;
        for (AcademicRecord r : records) {
            try {
                academicRepo.save(r);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", records.size());
        result.put("success", success);
        result.put("failed", failed);
        return Result.success(result);
    }

    // ==================== 账号管理（模拟） ====================

    /** 获取所有账号 */
    @GetMapping("/accounts")
    public Result<List<Map<String, Object>>> getAccounts() {
        List<Map<String, Object>> accounts = new ArrayList<>();

        Map<String, Object> a1 = new HashMap<>();
        a1.put("id", 1);
        a1.put("username", "admin");
        a1.put("role", "admin");
        a1.put("name", "系统管理员");
        a1.put("status", "启用");
        accounts.add(a1);

        Map<String, Object> a2 = new HashMap<>();
        a2.put("id", 2);
        a2.put("username", "teacher_li");
        a2.put("role", "teacher");
        a2.put("name", "李辅导员");
        a2.put("status", "启用");
        accounts.add(a2);

        Map<String, Object> a3 = new HashMap<>();
        a3.put("id", 3);
        a3.put("username", "teacher_wang");
        a3.put("role", "teacher");
        a3.put("name", "王老师");
        a3.put("status", "启用");
        accounts.add(a3);

        Map<String, Object> a4 = new HashMap<>();
        a4.put("id", 4);
        a4.put("username", "student_zhang");
        a4.put("role", "student");
        a4.put("name", "张小明");
        a4.put("status", "启用");
        accounts.add(a4);

        return Result.success(accounts);
    }

    /** 创建账号 */
    @PostMapping("/accounts")
    public Result<Map<String, Object>> createAccount(@RequestBody Map<String, Object> body) {
        Map<String, Object> account = new HashMap<>();
        account.put("id", System.currentTimeMillis() % 10000);
        account.put("username", body.getOrDefault("username", ""));
        account.put("role", body.getOrDefault("role", "teacher"));
        account.put("name", body.getOrDefault("name", ""));
        account.put("status", "启用");
        return Result.success(account);
    }

    // ==================== 困难认定管理 ====================

    /** 获取所有有困难认定数据的学生 */
    @GetMapping("/poverty-students")
    public Result<List<StudentProfile>> getPovertyStudents() {
        List<StudentProfile> all = profileRepo.findAll();
        List<StudentProfile> result = all.stream()
                .filter(p -> p.getExtraInfo() != null && p.getExtraInfo().containsKey("povertyLevel"))
                .collect(Collectors.toList());
        return Result.success(result);
    }
}
