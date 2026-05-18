package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.AcademicRecord;
import com.studentaffairs.backend.service.AcademicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academic")
@CrossOrigin(origins = "*")
public class AcademicController {

    private final AcademicService academicService;

    public AcademicController(AcademicService academicService) {
        this.academicService = academicService;
    }

    /** 获取预警学生列表（教师端） */
    @GetMapping("/warnings")
    public Result<List<AcademicRecord>> getWarnings() {
        return Result.success(academicService.getWarningRecords());
    }

    /** 手动触发预警引擎（也可改为定时任务） */
    @PostMapping("/warnings/run-engine")
    public Result<String> runWarningEngine() {
        int count = academicService.runWarningEngine();
        return Result.success("预警引擎运行完毕，共识别 " + count + " 名预警学生");
    }

    /** 获取班级成绩分布（教师端图表） */
    @GetMapping("/class-distribution")
    public Result<Map<String, Object>> getClassDistribution(@RequestParam String className) {
        return Result.success(academicService.getClassScoreDistribution(className));
    }

    /** 获取学生个人雷达图数据（学生端） */
    @GetMapping("/student-radar")
    public Result<Map<String, Object>> getStudentRadar(@RequestParam String studentId) {
        return Result.success(academicService.getStudentRadarData(studentId));
    }

    /** 获取学生个人成绩明细（学生端） */
    @GetMapping("/student-records")
    public Result<List<AcademicRecord>> getStudentRecords(@RequestParam String studentId) {
        return Result.success(academicService.getStudentRecords(studentId));
    }

    /** Dashboard 统计 */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(academicService.getDashboardStats());
    }

    /** 获取全部成绩记录 */
    @GetMapping("/records")
    public Result<List<AcademicRecord>> getAllRecords() {
        return Result.success(academicService.getAllRecords());
    }

    /** 新增成绩 */
    @PostMapping("/records")
    public Result<AcademicRecord> createRecord(@RequestBody AcademicRecord record) {
        return Result.success(academicService.createRecord(record));
    }

    /** 修改成绩 */
    @PutMapping("/records/{id}")
    public Result<AcademicRecord> updateRecord(@PathVariable Long id, @RequestBody AcademicRecord record) {
        return academicService.updateRecord(id, record)
                .map(Result::success)
                .orElse(Result.error(404, "成绩记录不存在"));
    }

    /** 删除成绩 */
    @DeleteMapping("/records/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        if (academicService.deleteRecord(id)) {
            return Result.success(null);
        }
        return Result.error(404, "成绩记录不存在");
    }
}
