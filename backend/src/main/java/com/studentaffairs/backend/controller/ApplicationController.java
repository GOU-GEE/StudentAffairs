package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.StudentApplication;
import com.studentaffairs.backend.entity.Notification;
import com.studentaffairs.backend.repository.StudentApplicationRepository;
import com.studentaffairs.backend.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final StudentApplicationRepository repository;
    private final NotificationRepository notificationRepository;

    public ApplicationController(StudentApplicationRepository repository,
                                 NotificationRepository notificationRepository) {
        this.repository = repository;
        this.notificationRepository = notificationRepository;
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

    /** 辅导员审批或学生销假（状态机流转：PENDING -> APPROVED/REJECTED，或者 APPROVED -> RETURNED）*/
    @PutMapping("/{id}/review")
    public Result<String> review(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return repository.findById(id).map(app -> {
            String newStatus = body.get("status");
            if (!"APPROVED".equals(newStatus) && !"REJECTED".equals(newStatus) && 
                !"RETURNED".equals(newStatus) && !"COUNSELOR_APPROVED".equals(newStatus) && 
                !"COUNSELOR_REJECTED".equals(newStatus)) {
                return Result.<String>error(400, "无效的审批状态");
            }
            app.setStatus(newStatus);
            if (body.containsKey("comment")) {
                String commentText = body.get("comment");
                if ("APPROVED".equals(newStatus) || "REJECTED".equals(newStatus)) {
                    String prevComment = app.getReviewComment();
                    if (prevComment != null && !prevComment.isEmpty()) {
                        // Avoid double prepending if already prepended
                        if (prevComment.contains("[学院意见]")) {
                            app.setReviewComment(commentText);
                        } else {
                            app.setReviewComment(prevComment + "\n[学院意见]: " + commentText);
                        }
                    } else {
                        app.setReviewComment("[学院意见]: " + commentText);
                    }
                } else if ("COUNSELOR_APPROVED".equals(newStatus) || "COUNSELOR_REJECTED".equals(newStatus)) {
                    app.setReviewComment("[辅导员意见]: " + commentText);
                } else {
                    app.setReviewComment(commentText);
                }
            }
            if (body.containsKey("reviewerName")) {
                app.setReviewerName(body.get("reviewerName"));
            } else if (!"RETURNED".equals(newStatus)) {
                app.setReviewerName("COUNSELOR_APPROVED".equals(newStatus) || "COUNSELOR_REJECTED".equals(newStatus) ? "李辅导员" : "资助管理中心");
            }
            app.setReviewTime(LocalDateTime.now());
            repository.save(app);

            // Create notification for student
            try {
                Notification notif = new Notification();
                notif.setUserId(app.getStudentId());
                notif.setPath("/student/applications/scholarship");
                notif.setIsRead(false);
                notif.setCreateTime(LocalDateTime.now());

                if ("COUNSELOR_APPROVED".equals(newStatus)) {
                    notif.setTitle("您的奖助服务申请已通过辅导员初审");
                    notif.setContent("您的【" + app.getTitle() + "】申请已通过辅导员初审，已推送至资助管理中心进行最终审核。评语：" + app.getReviewComment());
                    notif.setTag("已通过");
                    notif.setTagStyle("bg-green-50 text-green-600 border border-green-100/50");
                } else if ("COUNSELOR_REJECTED".equals(newStatus)) {
                    notif.setTitle("您的奖助服务申请未通过辅导员审核");
                    notif.setContent("您的【" + app.getTitle() + "】申请未通过辅导员初审。评语：" + app.getReviewComment());
                    notif.setTag("已驳回");
                    notif.setTagStyle("bg-red-50 text-red-600 border border-red-100/50");
                } else if ("APPROVED".equals(newStatus)) {
                    notif.setTitle("您的奖助服务申请已通过学院终审");
                    notif.setContent("恭喜！您的【" + app.getTitle() + "】申请已通过资助管理中心最终审核。评语：" + app.getReviewComment());
                    notif.setTag("已通过");
                    notif.setTagStyle("bg-green-50 text-green-600 border border-green-100/50");
                } else if ("REJECTED".equals(newStatus)) {
                    notif.setTitle("您的奖助服务申请未通过学院终审");
                    notif.setContent("很遗憾，您的【" + app.getTitle() + "】申请未通过资助管理中心最终审核。评语：" + app.getReviewComment());
                    notif.setTag("已驳回");
                    notif.setTagStyle("bg-red-50 text-red-600 border border-red-100/50");
                }

                if (notif.getTitle() != null) {
                    notificationRepository.save(notif);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String message = "操作成功";
            if ("COUNSELOR_APPROVED".equals(newStatus) || "APPROVED".equals(newStatus)) {
                message = "申请已批准";
            } else if ("COUNSELOR_REJECTED".equals(newStatus) || "REJECTED".equals(newStatus)) {
                message = "申请已驳回";
            } else if ("RETURNED".equals(newStatus)) {
                message = "已完成销假";
            }
            return Result.<String>success(message);
        }).orElse(Result.<String>error(404, "申请不存在"));
    }
}
