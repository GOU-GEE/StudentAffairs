package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.CareerAppointment;
import com.studentaffairs.backend.service.CareerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/career")
@CrossOrigin(origins = "*")
public class CareerController {

    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    // ==================== 职业库 ====================

    @GetMapping("/jobs")
    public Result<List<Map<String, Object>>> getJobLibrary() {
        return Result.success(careerService.getJobLibrary());
    }

    // ==================== 职业匹配 ====================

    @PostMapping("/match")
    public Result<List<Map<String, Object>>> matchCareers(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        Map<String, Double> hollandScores = (Map<String, Double>) body.get("hollandScores");
        String mbtiType = (String) body.get("mbtiType");
        @SuppressWarnings("unchecked")
        List<Double> valueScores = (List<Double>) body.get("valueScores");
        @SuppressWarnings("unchecked")
        List<Double> skillScores = (List<Double>) body.get("skillScores");

        return Result.success(careerService.matchCareers(hollandScores, mbtiType, valueScores, skillScores));
    }

    // ==================== 预约管理 ====================

    @PostMapping("/appointments")
    public Result<CareerAppointment> createAppointment(@RequestBody CareerAppointment appointment) {
        return Result.success(careerService.createAppointment(appointment));
    }

    @GetMapping("/appointments")
    public Result<List<CareerAppointment>> getStudentAppointments(@RequestParam String studentId) {
        return Result.success(careerService.getStudentAppointments(studentId));
    }

    @GetMapping("/appointments/teacher/{teacherId}")
    public Result<List<CareerAppointment>> getTeacherAppointments(@PathVariable String teacherId) {
        return Result.success(careerService.getTeacherAppointments(teacherId));
    }

    @PutMapping("/appointments/{id}/accept")
    public Result<CareerAppointment> acceptAppointment(@PathVariable Long id) {
        return careerService.acceptAppointment(id)
                .map(Result::success)
                .orElse(Result.error(404, "预约不存在"));
    }

    @PutMapping("/appointments/{id}/reject")
    public Result<CareerAppointment> rejectAppointment(@PathVariable Long id) {
        return careerService.rejectAppointment(id)
                .map(Result::success)
                .orElse(Result.error(404, "预约不存在"));
    }
}
