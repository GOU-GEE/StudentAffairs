package com.studentaffairs.backend;

import com.studentaffairs.backend.controller.YouthController;
import com.studentaffairs.backend.entity.Activity;
import com.studentaffairs.backend.entity.ActivityEnrollment;
import com.studentaffairs.backend.entity.SecondClassroomRecord;
import com.studentaffairs.backend.entity.YouthAward;
import com.studentaffairs.backend.repository.ActivityEnrollmentRepository;
import com.studentaffairs.backend.repository.ActivityRepository;
import com.studentaffairs.backend.repository.SecondClassroomRecordRepository;
import com.studentaffairs.backend.repository.YouthAwardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class YouthControllerIntegrationTest {

    @Autowired
    private YouthController youthController;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityEnrollmentRepository enrollmentRepository;

    @Autowired
    private SecondClassroomRecordRepository recordRepository;

    @Autowired
    private YouthAwardRepository awardRepository;

    private Activity testActivity;

    @BeforeEach
    public void setup() {
        enrollmentRepository.deleteAll();
        recordRepository.deleteAll();

        // Create a test activity
        Activity activity = new Activity();
        activity.setTitle("测试创新创造活动");
        activity.setDescription("一个用于集成测试的活动");
        activity.setDate("2026-05-25");
        activity.setLocation("测试多功能厅");
        activity.setParticipants(0);
        activity.setMaxParticipants(10);
        activity.setStatus("报名中");
        activity.setCreditType("创新创造项目学分"); // Should map to categoryIndex = 1
        testActivity = activityRepository.save(activity);
    }

    @Test
    public void testEnrollActivitySuccess() {
        // Enroll student A
        Map<String, Object> result = youthController.enrollActivity(testActivity.getId(), "202301042");
        assertEquals(200, result.get("code"));

        // Verify database enrollment
        List<ActivityEnrollment> enrollments = enrollmentRepository.findByActivityId(testActivity.getId());
        assertEquals(1, enrollments.size());
        assertEquals("202301042", enrollments.get(0).getStudentId());

        // Verify updated activity count in database
        Activity updated = activityRepository.findById(testActivity.getId()).orElseThrow();
        assertEquals(1, updated.getParticipants());
    }

    @Test
    public void testEnrollActivityDuplicate() {
        // Enroll once
        Map<String, Object> result1 = youthController.enrollActivity(testActivity.getId(), "202301042");
        assertEquals(200, result1.get("code"));

        // Enroll second time
        Map<String, Object> result2 = youthController.enrollActivity(testActivity.getId(), "202301042");
        assertEquals(400, result2.get("code"));
        assertTrue(((String) result2.get("msg")).contains("您已报名该活动"));

        // Count should remain 1
        assertEquals(1, enrollmentRepository.countByActivityId(testActivity.getId()));
    }

    @Test
    public void testGrantHoursBatchNotEnded() {
        // Activity status is "报名中" by default in setup()
        Map<String, Object> req = new HashMap<>();
        req.put("activityId", testActivity.getId());
        req.put("hours", 2);
        req.put("reason", "参与测试活动");

        Map<String, Object> result = youthController.grantHoursBatch(req);
        assertEquals(400, result.get("code"));
        assertEquals("活动未结束，无法发放学时", result.get("msg"));
    }

    @Test
    public void testGrantHoursBatchEmptyParticipants() {
        // Set status to "已结束" so we bypass status check
        testActivity.setStatus("已结束");
        activityRepository.save(testActivity);

        // Grant hours for testActivity when no one has registered
        Map<String, Object> req = new HashMap<>();
        req.put("activityId", testActivity.getId());
        req.put("hours", 2);
        req.put("reason", "参与测试活动");

        Map<String, Object> result = youthController.grantHoursBatch(req);
        assertEquals(400, result.get("code"));
        assertEquals("无人可发放", result.get("msg"));
    }

    @Test
    public void testGrantHoursBatchSuccessAndIdempotent() {
        // 1. Enroll two students
        youthController.enrollActivity(testActivity.getId(), "202301042");
        youthController.enrollActivity(testActivity.getId(), "202301043");

        // Set status to "已结束" so we can grant hours
        testActivity.setStatus("已结束");
        activityRepository.save(testActivity);

        // 2. Grant hours
        Map<String, Object> req = new HashMap<>();
        req.put("activityId", testActivity.getId());
        req.put("hours", 3);
        req.put("reason", "参与活动\"测试创新创造活动\"获得学时");

        Map<String, Object> result = youthController.grantHoursBatch(req);
        assertEquals(200, result.get("code"));
        assertEquals("学时发放成功", result.get("data"));

        // Verify second classroom records
        List<SecondClassroomRecord> records = recordRepository.findAll();
        assertEquals(2, records.size());

        // Both should have mapped categoryIndex = 1 ("创新创造项目学分") and 3 hours
        for (SecondClassroomRecord r : records) {
            assertEquals(1, r.getCategoryIndex());
            assertEquals(3, r.getHours());
            assertTrue(r.getStudentId().equals("202301042") || r.getStudentId().equals("202301043"));
            assertEquals("参与活动\"测试创新创造活动\"获得学时", r.getReason());
        }

        // 3. Grant hours again (testing idempotency)
        Map<String, Object> result2 = youthController.grantHoursBatch(req);
        assertEquals(200, result2.get("code"));

        // Total records should STILL be 2, not 4
        assertEquals(2, recordRepository.count());
    }

    @Test
    public void testReviewAwardAndGrantSecondClassroomCredits() {
        // Clean database
        awardRepository.deleteAll();
        recordRepository.deleteAll();

        // 1. Create and submit a pending YouthAward
        YouthAward award = new YouthAward();
        award.setStudentId("202301042");
        award.setStudentName("张小明");
        award.setAwardName("全国智能车竞赛一等奖");
        award.setLevel("国家级");
        award.setCategory("学科竞赛");
        award.setAwardTime("2026-05");
        award.setStatus("PENDING");
        YouthAward savedAward = awardRepository.save(award);

        // 2. Review and approve the award via controller
        Map<String, String> reviewReq = new HashMap<>();
        reviewReq.put("status", "APPROVED");
        reviewReq.put("reviewComment", "优秀！准予认定");

        Map<String, Object> reviewResult = youthController.reviewAward(savedAward.getId(), reviewReq);
        assertEquals(200, reviewResult.get("code"));

        // Verify status in DB is updated
        YouthAward updatedAward = awardRepository.findById(savedAward.getId()).orElseThrow();
        assertEquals("APPROVED", updatedAward.getStatus());
        assertEquals("优秀！准予认定", updatedAward.getReviewComment());

        // 3. Verify automatic SecondClassroomRecord creation
        List<SecondClassroomRecord> records = recordRepository.findByStudentId("202301042");
        assertEquals(1, records.size());

        SecondClassroomRecord r = records.get(0);
        assertEquals(1, r.getCategoryIndex()); // 学科竞赛 -> 1 (创新创造)
        assertEquals(8, r.getHours()); // 国家级 -> 8
        assertEquals("《全国智能车竞赛一等奖》获奖认定(国家级)", r.getReason());
        assertEquals("张小明", r.getStudentName());
        assertEquals("2023级2班", r.getClassName()); // Resolved from profile

        // 4. Test idempotency: call reviewAward again with APPROVED
        Map<String, Object> reviewResult2 = youthController.reviewAward(savedAward.getId(), reviewReq);
        assertEquals(200, reviewResult2.get("code"));

        // Count should STILL be 1, not 2
        assertEquals(1, recordRepository.count());
    }
}
