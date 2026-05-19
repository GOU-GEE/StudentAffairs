package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.entity.*;
import com.studentaffairs.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/youth")
@CrossOrigin(origins = "*")
public class YouthController {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private YouthAwardRepository awardRepository;

    @Autowired
    private HonorProjectRepository honorProjectRepository;

    @Autowired
    private HonorCandidateRepository honorCandidateRepository;

    @Autowired
    private SecondClassroomRecordRepository recordRepository;

    private Map<String, Object> success(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "Success");
        map.put("data", data);
        return map;
    }

    // ================= Activities =================
    @GetMapping("/activities")
    public Map<String, Object> getAllActivities() {
        return success(activityRepository.findAll());
    }

    @PostMapping("/activities")
    public Map<String, Object> createActivity(@RequestBody Activity activity) {
        activity.setParticipants(0);
        if (activity.getStatus() == null) activity.setStatus("报名中");
        return success(activityRepository.save(activity));
    }

    @PutMapping("/activities/{id}")
    public Map<String, Object> updateActivity(@PathVariable Long id, @RequestBody Activity req) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        activity.setTitle(req.getTitle());
        activity.setDescription(req.getDescription());
        activity.setDate(req.getDate());
        activity.setLocation(req.getLocation());
        activity.setMaxParticipants(req.getMaxParticipants());
        activity.setCredits(req.getCredits());
        activity.setLevel(req.getLevel());
        return success(activityRepository.save(activity));
    }

    @PutMapping("/activities/{id}/toggle-status")
    public Map<String, Object> toggleActivityStatus(@PathVariable Long id) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        if ("已结束".equals(activity.getStatus())) {
            activity.setStatus("报名中");
        } else {
            activity.setStatus("已结束");
        }
        return success(activityRepository.save(activity));
    }

    @PostMapping("/activities/{id}/enroll")
    public Map<String, Object> enrollActivity(@PathVariable Long id) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        Map<String, Object> error = new HashMap<>();
        if (!"报名中".equals(activity.getStatus())) {
            error.put("code", 400); error.put("msg", "该活动不在报名阶段");
            return error;
        }
        int max = activity.getMaxParticipants() != null ? activity.getMaxParticipants() : 9999;
        int current = activity.getParticipants() != null ? activity.getParticipants() : 0;
        if (current >= max) {
            error.put("code", 400); error.put("msg", "名额已满");
            return error;
        }
        activity.setParticipants(current + 1);
        activityRepository.save(activity);
        return success(activity);
    }

    // ================= Awards =================
    @GetMapping("/awards")
    public Map<String, Object> getAllAwards(@RequestParam(required = false) String studentId) {
        if (studentId != null && !studentId.isEmpty()) {
            return success(awardRepository.findByStudentId(studentId));
        }
        return success(awardRepository.findAll());
    }

    @PostMapping("/awards")
    public Map<String, Object> createAward(@RequestBody YouthAward award) {
        if (award.getStatus() == null) award.setStatus("PENDING");
        return success(awardRepository.save(award));
    }

    @PutMapping("/awards/{id}/review")
    public Map<String, Object> reviewAward(@PathVariable Long id, @RequestBody Map<String, String> body) {
        YouthAward award = awardRepository.findById(id).orElseThrow();
        award.setStatus(body.get("status"));
        return success(awardRepository.save(award));
    }

    // ================= Honors Projects =================
    @GetMapping("/honors/projects")
    public Map<String, Object> getHonorProjects() {
        return success(honorProjectRepository.findAll());
    }

    @PostMapping("/honors/projects")
    public Map<String, Object> createHonorProject(@RequestBody HonorProject project) {
        project.setApplied(0);
        if (project.getStatus() == null) project.setStatus("进行中");
        return success(honorProjectRepository.save(project));
    }

    @PutMapping("/honors/projects/{id}/toggle")
    public Map<String, Object> toggleHonorProject(@PathVariable Long id) {
        HonorProject project = honorProjectRepository.findById(id).orElseThrow();
        if ("进行中".equals(project.getStatus())) {
            project.setStatus("已关闭");
        } else {
            project.setStatus("进行中");
        }
        return success(honorProjectRepository.save(project));
    }

    // ================= Honors Candidates =================
    @GetMapping("/honors/projects/{projectId}/candidates")
    public Map<String, Object> getHonorCandidates(@PathVariable Long projectId) {
        return success(honorCandidateRepository.findByProjectId(projectId));
    }

    @GetMapping("/honors/candidates/by-student")
    public Map<String, Object> getCandidatesByStudent(@RequestParam String studentId) {
        return success(honorCandidateRepository.findByStudentId(studentId));
    }

    @PutMapping("/honors/candidates/{id}/select")
    public Map<String, Object> selectCandidate(@PathVariable Long id) {
        HonorCandidate candidate = honorCandidateRepository.findById(id).orElseThrow();
        candidate.setStatus("已入选");
        return success(honorCandidateRepository.save(candidate));
    }

    // ================= Second Classroom =================
    @GetMapping("/second-classroom/records")
    public Map<String, Object> getAllRecords(@RequestParam(required = false) String studentId) {
        if (studentId != null && !studentId.isEmpty()) {
            return success(recordRepository.findByStudentId(studentId));
        }
        return success(recordRepository.findAll());
    }

    @PostMapping("/second-classroom/grant")
    public Map<String, Object> grantHours(@RequestBody SecondClassroomRecord record) {
        record.setGrantTime(LocalDateTime.now());
        return success(recordRepository.save(record));
    }
}
