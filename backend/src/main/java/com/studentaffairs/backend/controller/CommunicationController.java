package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.Announcement;
import com.studentaffairs.backend.entity.Feedback;
import com.studentaffairs.backend.entity.Message;
import com.studentaffairs.backend.entity.StudentProfile;
import com.studentaffairs.backend.entity.Notification;
import com.studentaffairs.backend.repository.AnnouncementRepository;
import com.studentaffairs.backend.repository.FeedbackRepository;
import com.studentaffairs.backend.repository.MessageRepository;
import com.studentaffairs.backend.repository.StudentProfileRepository;
import com.studentaffairs.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/communication")
@CrossOrigin(origins = "*")
public class CommunicationController {

    private final AnnouncementRepository announcementRepo;
    private final MessageRepository messageRepo;
    private final StudentProfileRepository studentProfileRepo;
    private final FeedbackRepository feedbackRepo;

    @Autowired
    private NotificationRepository notificationRepo;


    public CommunicationController(AnnouncementRepository announcementRepo,
                                   MessageRepository messageRepo,
                                   StudentProfileRepository studentProfileRepo,
                                   FeedbackRepository feedbackRepo) {
        this.announcementRepo = announcementRepo;
        this.messageRepo = messageRepo;
        this.studentProfileRepo = studentProfileRepo;
        this.feedbackRepo = feedbackRepo;
    }

    // ==================== 公告 ====================

    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements(@RequestParam(required = false) String publisherId) {
        if (publisherId != null && !publisherId.isEmpty()) {
            return Result.success(announcementRepo.findByPublisherIdOrderByIsPinnedDescPublishTimeDesc(publisherId));
        }
        return Result.success(announcementRepo.findByOrderByIsPinnedDescPublishTimeDesc());
    }

    @PostMapping("/announcements")
    public Result<Announcement> publish(@RequestBody Announcement announcement) {
        announcement.setPublishTime(LocalDateTime.now());
        if (announcement.getIsPinned() == null) announcement.setIsPinned(false);
        return Result.success(announcementRepo.save(announcement));
    }

    @DeleteMapping("/announcements/{id}")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        announcementRepo.deleteById(id);
        return Result.success("公告已删除");
    }

    // ==================== 学生列表 ====================

    @GetMapping("/students")
    public Result<List<StudentProfile>> getStudents(@RequestParam String teacherId) {
        return Result.success(studentProfileRepo.findByTeacherId(teacherId));
    }

    // ==================== 私信 ====================

    @GetMapping("/messages")
    public Result<List<Message>> getMessages(@RequestParam String userId,
                                             @RequestParam String peerId) {
        List<Message> messages = messageRepo.findConversation(userId, peerId);
        // 标记对方发来的未读消息为已读
        for (Message m : messages) {
            if (m.getReceiverId().equals(userId) && !m.getIsRead()) {
                m.setIsRead(true);
                messageRepo.save(m);
            }
        }
        return Result.success(messages);
    }

    @PostMapping("/messages")
    public Result<Message> sendMessage(@RequestBody Message message) {
        message.setSentTime(LocalDateTime.now());
        if (message.getIsRead() == null) message.setIsRead(false);
        if (message.getIsRecalled() == null) message.setIsRecalled(false);
        return Result.success(messageRepo.save(message));
    }

    @PutMapping("/messages/{id}/recall")
    public Result<Message> recallMessage(@PathVariable Long id) {
        Optional<Message> opt = messageRepo.findById(id);
        if (opt.isEmpty()) return Result.error(404, "消息不存在");
        Message msg = opt.get();
        // 2 分钟内可撤回
        if (msg.getSentTime().plusMinutes(2).isBefore(LocalDateTime.now())) {
            return Result.error(400, "超过2分钟，无法撤回");
        }
        msg.setIsRecalled(true);
        msg.setContent("");
        return Result.success(messageRepo.save(msg));
    }

    @PutMapping("/messages/{id}/read")
    public Result<Message> markRead(@PathVariable Long id) {
        Optional<Message> opt = messageRepo.findById(id);
        if (opt.isEmpty()) return Result.error(404, "消息不存在");
        Message msg = opt.get();
        msg.setIsRead(true);
        return Result.success(messageRepo.save(msg));
    }

    // ==================== 联系人列表 ====================

    @GetMapping("/contacts")
    public Result<List<Map<String, Object>>> getContacts(@RequestParam String userId,
                                                         @RequestParam String role) {
        Set<String> peerIds = new LinkedHashSet<>();
        if ("teacher".equals(role)) {
            peerIds.addAll(messageRepo.findStudentReceiversBySenderId(userId));
            peerIds.addAll(messageRepo.findStudentSendersByReceiverId(userId));
        } else {
            peerIds.addAll(messageRepo.findStudentSendersByReceiverId(userId));
            // student only chats with their teacher, get from student profile
            Optional<StudentProfile> profile = studentProfileRepo.findByStudentId(userId);
            if (profile.isPresent() && profile.get().getTeacherId() != null) {
                peerIds.add(profile.get().getTeacherId());
            }
        }

        List<Object[]> unreadCounts = messageRepo.countUnreadBySender(userId);
        Map<String, Long> unreadMap = new HashMap<>();
        for (Object[] row : unreadCounts) {
            unreadMap.put((String) row[0], (Long) row[1]);
        }

        List<Map<String, Object>> contacts = new ArrayList<>();
        for (String peerId : peerIds) {
            Map<String, Object> contact = new HashMap<>();
            contact.put("id", peerId);
            if ("teacher".equals(role)) {
                Optional<StudentProfile> sp = studentProfileRepo.findByStudentId(peerId);
                contact.put("name", sp.map(StudentProfile::getName).orElse(peerId));
                contact.put("avatar", sp.map(StudentProfile::getAvatar).orElse(""));
            } else {
                contact.put("name", "李老师");
                contact.put("avatar", "https://i.pravatar.cc/150?u=teacher_li");
            }
            contact.put("unread", unreadMap.getOrDefault(peerId, 0L));
            contacts.add(contact);
        }
        return Result.success(contacts);
    }

    // ==================== 意见反馈 ====================

    @GetMapping("/feedbacks")
    public Result<List<Feedback>> getFeedbacks(@RequestParam(required = false) String studentId) {
        if (studentId != null && !studentId.isEmpty()) {
            return Result.success(feedbackRepo.findByStudentIdOrderByCreateTimeDesc(studentId));
        }
        return Result.success(feedbackRepo.findByOrderByCreateTimeDesc());
    }

    @PostMapping("/feedbacks")
    public Result<Feedback> submitFeedback(@RequestBody Feedback feedback) {
        feedback.setCreateTime(LocalDateTime.now());
        if (feedback.getStatus() == null) feedback.setStatus("PENDING");
        return Result.success(feedbackRepo.save(feedback));
    }

    @PutMapping("/feedbacks/{id}/reply")
    public Result<Feedback> replyFeedback(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Feedback feedback = feedbackRepo.findById(id).orElse(null);
        if (feedback == null) return Result.error(404, "反馈不存在");
        feedback.setReply(body.get("reply"));
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus("REPLIED");
        return Result.success(feedbackRepo.save(feedback));
    }

    // ==================== 通知 ====================

    @GetMapping("/notifications")
    public Result<List<Notification>> getNotifications(@RequestParam String userId, @RequestParam(required = false) Boolean unreadOnly) {
        if (Boolean.TRUE.equals(unreadOnly)) {
            return Result.success(notificationRepo.findByUserIdAndIsReadOrderByCreateTimeDesc(userId, false));
        }
        return Result.success(notificationRepo.findByUserIdOrderByCreateTimeDesc(userId));
    }

    @PutMapping("/notifications/{id}/read")
    public Result<Notification> markNotificationRead(@PathVariable Long id) {
        Optional<Notification> opt = notificationRepo.findById(id);
        if (opt.isEmpty()) return Result.error(404, "通知不存在");
        Notification notif = opt.get();
        notif.setIsRead(true);
        return Result.success(notificationRepo.save(notif));
    }

    @PutMapping("/notifications/read-all")
    public Result<String> markAllNotificationsRead(@RequestParam String userId) {
        List<Notification> list = notificationRepo.findByUserIdAndIsReadOrderByCreateTimeDesc(userId, false);
        for (Notification notif : list) {
            notif.setIsRead(true);
            notificationRepo.save(notif);
        }
        return Result.success("所有通知已标记为已读");
    }
}
