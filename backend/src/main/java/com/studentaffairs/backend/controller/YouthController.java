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

    @Autowired
    private ActivityEnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private NotificationRepository notificationRepository;


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

    @GetMapping("/activities/enrolled")
    public Map<String, Object> getEnrolledActivities(@RequestParam String studentId) {
        List<ActivityEnrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<Long> ids = enrollments.stream().map(ActivityEnrollment::getActivityId).toList();
        return success(ids);
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
        
        activity.setCoverImage(req.getCoverImage());
        activity.setEnrollTime(req.getEnrollTime());
        activity.setTimeDetail(req.getTimeDetail());
        activity.setRangeName(req.getRangeName());
        activity.setLeaveSupport(req.getLeaveSupport());
        activity.setCreditType(req.getCreditType());
        activity.setEnrollLimit(req.getEnrollLimit());
        
        if (req.getStatus() != null) {
            activity.setStatus(req.getStatus());
        }
        
        return success(activityRepository.save(activity));
    }

    @DeleteMapping("/activities/{id}")
    public Map<String, Object> deleteActivity(@PathVariable Long id) {
        activityRepository.deleteById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "Activity deleted successfully");
        return map;
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
    public Map<String, Object> enrollActivity(@PathVariable Long id, @RequestParam(required = false) String studentId) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        Map<String, Object> error = new HashMap<>();
        if (!"报名中".equals(activity.getStatus())) {
            error.put("code", 400); error.put("msg", "该活动不在报名阶段");
            return error;
        }
        
        String finalStudentId = (studentId == null || studentId.trim().isEmpty()) ? "202301042" : studentId;
        
        if (enrollmentRepository.existsByActivityIdAndStudentId(id, finalStudentId)) {
            error.put("code", 400); error.put("msg", "您已报名该活动");
            return error;
        }
        
        int max = activity.getMaxParticipants() != null ? activity.getMaxParticipants() : 9999;
        int current = enrollmentRepository.countByActivityId(id);
        if (current >= max) {
            error.put("code", 400); error.put("msg", "名额已满");
            return error;
        }
        
        ActivityEnrollment enrollment = new ActivityEnrollment();
        enrollment.setActivityId(id);
        enrollment.setStudentId(finalStudentId);
        enrollment.setEnrollTime(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
        
        int realParticipants = enrollmentRepository.countByActivityId(id);
        activity.setParticipants(realParticipants);
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
        
        String studentName = award.getStudentName();
        if (studentName == null || studentName.isEmpty()) {
            studentName = studentProfileRepository.findByStudentId(award.getStudentId())
                .map(StudentProfile::getName)
                .orElse("学生");
            award.setStudentName(studentName);
        }
        
        YouthAward saved = awardRepository.save(award);
        
        // Generate notification for the youth group
        Notification notif = new Notification();
        notif.setUserId("youth");
        notif.setTitle("新获奖审核申请");
        notif.setContent("学生 " + saved.getStudentName() + " (" + saved.getStudentId() + ") 提交了获奖情况录入申请：《" + saved.getAwardName() + "》，请尽快审核。");
        notif.setTag("待审核");
        notif.setTagStyle("bg-orange-50 text-orange-600 border-orange-100");
        notif.setPath("/youth/awards");
        notif.setIsRead(false);
        notif.setCreateTime(LocalDateTime.now());
        notificationRepository.save(notif);
        
        return success(saved);
    }

    @PutMapping("/awards/{id}/review")
    public Map<String, Object> reviewAward(@PathVariable Long id, @RequestBody Map<String, String> body) {
        YouthAward award = awardRepository.findById(id).orElseThrow();
        String status = body.get("status");
        String comment = body.get("reviewComment");
        if (comment == null) {
            comment = body.get("comment");
        }
        award.setStatus(status);
        award.setReviewComment(comment);
        YouthAward saved = awardRepository.save(award);

        // Generate notification for the student
        Notification notif = new Notification();
        notif.setUserId(saved.getStudentId());
        String statusZh = "APPROVED".equals(status) ? "已通过" : "已驳回";
        notif.setTitle("获奖申请审核已处理");
        notif.setContent("您的获奖申请《" + saved.getAwardName() + "》已被审核，状态更新为【" + statusZh + "】。" 
            + (comment != null && !comment.trim().isEmpty() ? " 审核意见：" + comment : ""));
        notif.setTag(statusZh);
        if ("APPROVED".equals(status)) {
            notif.setTagStyle("bg-green-50 text-green-600 border-green-100/50");
        } else {
            notif.setTagStyle("bg-red-50 text-red-600 border-red-100/50");
        }
        notif.setPath("/student/applications/awards");
        notif.setIsRead(false);
        notif.setCreateTime(LocalDateTime.now());
        notificationRepository.save(notif);

        // Automatically grant Second Classroom credits/hours if approved
        if ("APPROVED".equals(status)) {
            StudentProfile profile = studentProfileRepository.findByStudentId(saved.getStudentId()).orElse(null);
            String className = profile != null ? profile.getGradeClass() : "2023级2班";
            String studentName = profile != null ? profile.getName() : saved.getStudentName();

            int categoryIndex = mapAwardCategoryToCategoryIndex(saved.getCategory());
            int hours = mapAwardLevelToHours(saved.getLevel());
            String reason = "《" + saved.getAwardName() + "》获奖认定(" + saved.getLevel() + ")";

            // Check if already exists to prevent duplicate addition
            boolean alreadyExists = false;
            List<SecondClassroomRecord> existing = recordRepository.findByStudentId(saved.getStudentId());
            for (SecondClassroomRecord r : existing) {
                if (reason.equals(r.getReason()) && r.getHours().equals(hours) && r.getCategoryIndex().equals(categoryIndex)) {
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                SecondClassroomRecord record = new SecondClassroomRecord();
                record.setStudentId(saved.getStudentId());
                record.setStudentName(studentName);
                record.setClassName(className);
                record.setCategoryIndex(categoryIndex);
                record.setHours(hours);
                record.setReason(reason);
                record.setGrantTime(LocalDateTime.now());
                recordRepository.save(record);
            }
        }

        return success(saved);
    }

    private int mapAwardCategoryToCategoryIndex(String category) {
        if (category == null) return 5; // Default to 技能特长 (index 5)
        switch (category) {
            case "学科竞赛":
            case "学术科研":
                return 1; // 创新创造
            case "社会实践":
                return 2; // 社会实践/劳动教育
            case "荣誉称号":
                return 3; // 思想素质
            case "文体活动":
                return 4; // 文艺体育
            case "其他":
            default:
                return 5; // 技能特长
        }
    }

    private int mapAwardLevelToHours(String level) {
        if (level == null) return 2;
        switch (level) {
            case "国家级":
                return 8;
            case "省级":
            case "省部级":
                return 6;
            case "市级":
                return 4;
            case "校级":
                return 3;
            case "院级":
            default:
                return 2;
        }
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

    @PostMapping("/second-classroom/grant-batch")
    public Map<String, Object> grantHoursBatch(@RequestBody Map<String, Object> req) {
        Long activityId = ((Number) req.get("activityId")).longValue();
        Integer hours = ((Number) req.getOrDefault("hours", 2)).intValue();
        String reason = (String) req.get("reason");
        
        Activity activity = activityRepository.findById(activityId).orElseThrow();
        
        if (!"已结束".equals(activity.getStatus())) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("msg", "活动未结束，无法发放学时");
            return error;
        }
        
        List<ActivityEnrollment> enrollments = enrollmentRepository.findByActivityId(activityId);
        
        if (enrollments.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("msg", "无人可发放");
            return error;
        }
        
        int categoryIndex = mapCreditTypeToCategory(activity.getCreditType());
        
        for (ActivityEnrollment enrollment : enrollments) {
            String studentId = enrollment.getStudentId();
            StudentProfile profile = studentProfileRepository.findByStudentId(studentId).orElse(null);
            String studentName = profile != null ? profile.getName() : "张小明";
            String className = profile != null ? profile.getGradeClass() : "2023级2班";
            
            String finalReason = (reason == null || reason.isEmpty()) ? "参与活动\"" + activity.getTitle() + "\"获得学时" : reason;
            
            boolean alreadyGranted = false;
            List<SecondClassroomRecord> existing = recordRepository.findByStudentId(studentId);
            for (SecondClassroomRecord r : existing) {
                if (finalReason.equals(r.getReason()) && r.getHours().equals(hours) && r.getCategoryIndex().equals(categoryIndex)) {
                    alreadyGranted = true;
                    break;
                }
            }
            
            if (!alreadyGranted) {
                SecondClassroomRecord record = new SecondClassroomRecord();
                record.setStudentId(studentId);
                record.setStudentName(studentName);
                record.setClassName(className);
                record.setCategoryIndex(categoryIndex);
                record.setHours(hours);
                record.setReason(finalReason);
                record.setGrantTime(LocalDateTime.now());
                recordRepository.save(record);
            }
        }
        
        return success("学时发放成功");
    }

    private int mapCreditTypeToCategory(String creditType) {
        if (creditType == null) return 0;
        if (creditType.contains("志愿服务")) return 0;
        if (creditType.contains("创新创造") || creditType.contains("创新创业")) return 1;
        if (creditType.contains("社会实践") || creditType.contains("劳动教育")) return 2;
        if (creditType.contains("思想素质") || creditType.contains("学术讲座")) return 3;
        if (creditType.contains("文艺体育") || creditType.contains("文体活动")) return 4;
        if (creditType.contains("技能特长") || creditType.contains("技能培训")) return 5;
        return 0;
    }
}
