package com.studentaffairs.backend.config;

import com.studentaffairs.backend.entity.AcademicRecord;
import com.studentaffairs.backend.entity.Announcement;
import com.studentaffairs.backend.entity.SafetyIncident;
import com.studentaffairs.backend.entity.StudentApplication;
import com.studentaffairs.backend.entity.WorkStudyJob;
import com.studentaffairs.backend.repository.AcademicRecordRepository;
import com.studentaffairs.backend.repository.AnnouncementRepository;
import com.studentaffairs.backend.repository.SafetyIncidentRepository;
import com.studentaffairs.backend.repository.StudentApplicationRepository;
import com.studentaffairs.backend.repository.WorkStudyJobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(WorkStudyJobRepository jobRepository,
                                      AcademicRecordRepository academicRepository,
                                      SafetyIncidentRepository safetyRepository,
                                      AnnouncementRepository announcementRepository,
                                      StudentApplicationRepository applicationRepository) {
        return args -> {
            // 初始化勤工助学岗位
            if (jobRepository.count() == 0) {
                WorkStudyJob job1 = new WorkStudyJob();
                job1.setTitle("图书馆阅览室助理 (支持并发测试)");
                job1.setDepartment("图书馆");
                job1.setQuota(2);
                job1.setCurrentCount(0);
                job1.setStatus("ACTIVE");

                WorkStudyJob job2 = new WorkStudyJob();
                job2.setTitle("计算机学院机房维护");
                job2.setDepartment("计算机学院");
                job2.setQuota(5);
                job2.setCurrentCount(3);
                job2.setStatus("ACTIVE");

                jobRepository.saveAll(List.of(job1, job2));
                System.out.println("Initialized default WorkStudyJobs.");
            }

            // 初始化成绩演示数据
            if (academicRepository.count() == 0) {
                List<AcademicRecord> records = new ArrayList<>();
                
                // 学生1: 张小明 (202301042) - 优等生
                records.add(createRecord("202301042", "张小明", "软工2班", "数据结构", "必修", 95f, 4f, "2024-2025-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "算法设计与分析", "必修", 88f, 3f, "2024-2025-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "软件工程", "必修", 91f, 3f, "2024-2025-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "数据库原理", "必修", 86f, 4f, "2024-2025-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "英语(四)", "必修", 82f, 2f, "2024-2025-1", false));

                // 学生2: 李四 (202301043) - 预警学生（有挂科）
                records.add(createRecord("202301043", "李四", "软工2班", "数据结构", "必修", 45f, 4f, "2024-2025-1", true));
                records.add(createRecord("202301043", "李四", "软工2班", "算法设计与分析", "必修", 55f, 3f, "2024-2025-1", true));
                records.add(createRecord("202301043", "李四", "软工2班", "软件工程", "必修", 62f, 3f, "2024-2025-1", true));
                records.add(createRecord("202301043", "李四", "软工2班", "数据库原理", "必修", 58f, 4f, "2024-2025-1", true));
                records.add(createRecord("202301043", "李四", "软工2班", "英语(四)", "必修", 60f, 2f, "2024-2025-1", true));

                // 学生3: 王五 (202301044) - 中等
                records.add(createRecord("202301044", "王五", "软工2班", "数据结构", "必修", 75f, 4f, "2024-2025-1", false));
                records.add(createRecord("202301044", "王五", "软工2班", "算法设计与分析", "必修", 72f, 3f, "2024-2025-1", false));
                records.add(createRecord("202301044", "王五", "软工2班", "软件工程", "必修", 78f, 3f, "2024-2025-1", false));
                records.add(createRecord("202301044", "王五", "软工2班", "数据库原理", "必修", 80f, 4f, "2024-2025-1", false));
                records.add(createRecord("202301044", "王五", "软工2班", "英语(四)", "必修", 74f, 2f, "2024-2025-1", false));

                academicRepository.saveAll(records);
                System.out.println("Initialized default AcademicRecords.");
            }

            initSafety(safetyRepository);
            initAnnouncements(announcementRepository);
            initApplications(applicationRepository);
        };
    }

    private AcademicRecord createRecord(String studentId, String studentName, String className,
                                         String courseName, String courseType,
                                         Float score, Float credit, String semester, Boolean isWarning) {
        AcademicRecord r = new AcademicRecord();
        r.setStudentId(studentId);
        r.setStudentName(studentName);
        r.setClassName(className);
        r.setCourseName(courseName);
        r.setCourseType(courseType);
        r.setScore(score);
        r.setCredit(credit);
        r.setSemester(semester);
        r.setIsWarning(isWarning);
        return r;
    }

    private void initSafety(SafetyIncidentRepository repo) {
        if (repo.count() > 0) return;
        List<SafetyIncident> incidents = new ArrayList<>();

        SafetyIncident i1 = new SafetyIncident();
        i1.setStudentId("202301043"); i1.setStudentName("李四");
        i1.setType("MENTAL"); i1.setLevel("HIGH");
        i1.setDescription("学生近期情绪低落，多次旷课，疑似存在心理健康问题，辅导员已约谈。");
        i1.setStatus("PROCESSING"); i1.setReportTime(LocalDateTime.now().minusDays(2));
        i1.setHandlerName("王辅导员");
        incidents.add(i1);

        SafetyIncident i2 = new SafetyIncident();
        i2.setStudentId("202301044"); i2.setStudentName("王五");
        i2.setType("VIOLATION"); i2.setLevel("MEDIUM");
        i2.setDescription("宿舍违规使用大功率电器，已进行警告处理并没收设备。");
        i2.setStatus("CLOSED"); i2.setReportTime(LocalDateTime.now().minusDays(5));
        i2.setHandlerName("宿管处");
        incidents.add(i2);

        SafetyIncident i3 = new SafetyIncident();
        i3.setStudentId(null); i3.setStudentName("未知");
        i3.setType("MEDICAL"); i3.setLevel("HIGH");
        i3.setDescription("图书馆2楼有学生晕倒，已联系校医院并送医处理，家长已通知。");
        i3.setStatus("CLOSED"); i3.setReportTime(LocalDateTime.now().minusDays(8));
        i3.setHandlerName("校医院");
        incidents.add(i3);

        repo.saveAll(incidents);
        System.out.println("Initialized default SafetyIncidents.");
    }

    private void initAnnouncements(AnnouncementRepository repo) {
        if (repo.count() > 0) return;
        List<Announcement> list = new ArrayList<>();

        Announcement a1 = new Announcement();
        a1.setTitle("【重要】2024-2025学年第二学期奖学金评定通知");
        a1.setContent("各位同学，根据学院规定，本学期奖学金评定工作即将启动。请符合条件的同学于5月20日前登录系统提交申请材料，逾期不再受理。评定结果将于6月10日公示。");
        a1.setPublisherName("学工处"); a1.setType("URGENT");
        a1.setPublishTime(LocalDateTime.now().minusHours(2)); a1.setIsPinned(true);
        list.add(a1);

        Announcement a2 = new Announcement();
        a2.setTitle("五月份团建活动通知 | 计科院户外拓展");
        a2.setContent("定于本月25日（周六）组织全体同学参加户外拓展活动，地点：南山郊野公园。请各班班长统计参与人数，费用由学院承担。");
        a2.setPublisherName("团委"); a2.setType("EVENT");
        a2.setPublishTime(LocalDateTime.now().minusDays(1)); a2.setIsPinned(false);
        list.add(a2);

        Announcement a3 = new Announcement();
        a3.setTitle("关于规范宿舍用电安全的通知");
        a3.setContent("近期宿舍违规用电现象频发，请全体同学严格遵守宿舍管理规定，不得使用大功率电器（热水壶、电饭锅、电吹风除外）。违者将按校规处理。");
        a3.setPublisherName("宿管处"); a3.setType("NOTICE");
        a3.setPublishTime(LocalDateTime.now().minusDays(3)); a3.setIsPinned(false);
        list.add(a3);

        repo.saveAll(list);
        System.out.println("Initialized default Announcements.");
    }

    private void initApplications(StudentApplicationRepository repo) {
        if (repo.count() > 0) return;
        List<StudentApplication> list = new ArrayList<>();

        StudentApplication a1 = new StudentApplication();
        a1.setStudentId("202301042"); a1.setStudentName("张小明");
        a1.setType("SCHOLARSHIP"); a1.setTitle("2024-2025学年国家励志奖学金申请");
        a1.setReason("本学期学习成绩优异，GPA 3.85，专业排名前10%，积极参加社会实践活动，特申请国家励志奖学金。");
        a1.setStatus("PENDING"); a1.setApplyTime(LocalDateTime.now().minusDays(3));
        list.add(a1);

        StudentApplication a2 = new StudentApplication();
        a2.setStudentId("202301042"); a2.setStudentName("张小明");
        a2.setType("LEAVE"); a2.setTitle("周末外出报备（2024-11-16至11-17）");
        a2.setReason("本周末需返回家乡参加家庭事务，请批准2天外出申请，保证按时返校。");
        a2.setStatus("APPROVED"); a2.setApplyTime(LocalDateTime.now().minusDays(10));
        a2.setReviewComment("同意，注意安全"); a2.setReviewerName("李辅导员");
        a2.setReviewTime(LocalDateTime.now().minusDays(9));
        list.add(a2);

        StudentApplication a3 = new StudentApplication();
        a3.setStudentId("202301043"); a3.setStudentName("李四");
        a3.setType("FINANCIAL_AID"); a3.setTitle("国家助学金申请（困难认定C档）");
        a3.setReason("家庭经济困难，父母务农，年收入不足3万元，已提交困难认定材料，特申请国家助学金。");
        a3.setStatus("PENDING"); a3.setApplyTime(LocalDateTime.now().minusDays(5));
        list.add(a3);

        repo.saveAll(list);
        System.out.println("Initialized default StudentApplications.");
    }
}
