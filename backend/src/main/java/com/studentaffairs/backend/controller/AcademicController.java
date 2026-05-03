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
}
