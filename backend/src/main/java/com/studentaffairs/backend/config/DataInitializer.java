package com.studentaffairs.backend.config;

import com.studentaffairs.backend.entity.AcademicRecord;
import com.studentaffairs.backend.entity.Announcement;
import com.studentaffairs.backend.entity.Message;
import com.studentaffairs.backend.entity.SafetyIncident;
import com.studentaffairs.backend.entity.StudentApplication;
import com.studentaffairs.backend.entity.StudentProfile;
import com.studentaffairs.backend.entity.WorkStudyJob;
import com.studentaffairs.backend.repository.AcademicRecordRepository;
import com.studentaffairs.backend.repository.AnnouncementRepository;
import com.studentaffairs.backend.repository.MessageRepository;
import com.studentaffairs.backend.repository.SafetyIncidentRepository;
import com.studentaffairs.backend.repository.StudentApplicationRepository;
import com.studentaffairs.backend.repository.StudentProfileRepository;
import com.studentaffairs.backend.repository.WorkStudyJobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(WorkStudyJobRepository jobRepository,
                                      AcademicRecordRepository academicRepository,
                                      SafetyIncidentRepository safetyRepository,
                                      AnnouncementRepository announcementRepository,
                                      StudentApplicationRepository applicationRepository,
                                      StudentProfileRepository profileRepository,
                                      MessageRepository messageRepository) {
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

            initStudentProfiles(profileRepository);
            initSafety(safetyRepository);
            initAnnouncements(announcementRepository);
            initApplications(applicationRepository);
            initMessages(messageRepository);
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

        Announcement a4 = new Announcement();
        a4.setTitle("关于开展2024-2025学年国家奖学金评选工作的通知");
        a4.setContent("各学院、各班级：\n\n为激励我校学生勤奋学习、积极进取，德、智、体、美、劳全面发展，根据《教育部 财政部关于印发〈普通本科高校、高等职业学校国家奖学金管理暂行办法〉的通知》文件精神，现就做好我校2024-2025学年国家奖学金评审工作通知如下：\n\n一、评选对象\n具有中华人民共和国国籍，纳入全国普通高校招生计划的全日制在校二年级（含）以上特别优秀的本专科学生。\n\n二、评选条件\n1. 热爱社会主义祖国，拥护中国共产党的领导；\n2. 遵守宪法和法律，遵守学校规章制度；\n3. 诚实守信，道德品质优良；\n4. 在校期间学习成绩优异，社会实践、创新能力、综合素质等方面特别突出。\n\n请各班级积极组织申报，于规定时间内将材料上交至辅导员处。");
        a4.setPublisherName("学生工作部"); a4.setType("URGENT");
        a4.setPublishTime(LocalDateTime.now().minusDays(1).minusHours(3)); a4.setIsPinned(true);
        list.add(a4);

        Announcement a5 = new Announcement();
        a5.setTitle("辅导员通知：本周班会安排");
        a5.setContent("同学们，本周日下午3:00在2B-401召开本学期第一次线下班会，主要讨论新学期的学业规划和班委改选事宜，请全体同学准时参加，不得无故缺席。\n\n如有特殊情况需要请假，请提前在系统中提交请假申请。");
        a5.setPublisherName("李老师"); a5.setPublisherId("T001"); a5.setType("NOTICE");
        a5.setPublishTime(LocalDateTime.now().minusHours(5)); a5.setIsPinned(false);
        list.add(a5);

        Announcement a6 = new Announcement();
        a6.setTitle("图书馆关于数据库使用培训的通知");
        a6.setContent("为了帮助同学们更好地利用图书馆数字资源，提高信息检索能力，图书馆将举办系列数据库使用培训讲座。\n\n地点：图书馆一楼报告厅\n时间：本周三下午4:00");
        a6.setPublisherName("图书馆"); a6.setType("EVENT");
        a6.setPublishTime(LocalDateTime.now().minusDays(2)); a6.setIsPinned(false);
        list.add(a6);

        Announcement a7 = new Announcement();
        a7.setTitle("关于2024年下半年勤工助学岗位招募的通知");
        a7.setContent("本学期勤工助学岗位开始招募，有意向的同学请填写报名表交至办公室。岗位包括：图书馆助理、实验室管理员、行政助理等。");
        a7.setPublisherName("学生资助管理中心"); a7.setType("NOTICE");
        a7.setPublishTime(LocalDateTime.now().minusDays(4)); a7.setIsPinned(false);
        list.add(a7);

        Announcement a8 = new Announcement();
        a8.setTitle("宿舍安全检查通知");
        a8.setContent("为消除宿舍安全隐患，营造安全舒适的住宿环境，后勤管理处将于本周四开展全校宿舍安全大检查，重点检查违章电器、私拉电线等。请各位同学提前自查整改。");
        a8.setPublisherName("后勤管理处"); a8.setType("NOTICE");
        a8.setPublishTime(LocalDateTime.now().minusDays(5)); a8.setIsPinned(false);
        list.add(a8);

        Announcement a9 = new Announcement();
        a9.setTitle("关于期末考试的注意事项");
        a9.setContent("期末考试将于下月中旬开始，请各位同学合理安排复习时间，严格遵守考试纪律。不得携带手机、电子设备等进入考场，一经发现按作弊处理。");
        a9.setPublisherName("李老师"); a9.setPublisherId("T001"); a9.setType("URGENT");
        a9.setPublishTime(LocalDateTime.now().minusDays(1)); a9.setIsPinned(true);
        list.add(a9);

        Announcement a10 = new Announcement();
        a10.setTitle("软件工程专业实习动员大会通知");
        a10.setContent("定于下周一下午2点在学术报告厅召开实习动员大会，请软件工程专业全体大三学生准时参加，届时将公布实习单位和分配方案。");
        a10.setPublisherName("李老师"); a10.setPublisherId("T001"); a10.setType("NOTICE");
        a10.setPublishTime(LocalDateTime.now().minusDays(7)); a10.setIsPinned(false);
        list.add(a10);

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

    private void initStudentProfiles(StudentProfileRepository repo) {
        if (repo.count() > 0) return;
        List<StudentProfile> list = new ArrayList<>();

        StudentProfile p1 = new StudentProfile();
        p1.setStudentId("202301042");
        p1.setName("张小明");
        p1.setTeacherId("T001");
        p1.setAvatar("https://i.pravatar.cc/150?u=student1");
        p1.setCollege("计算机学院");
        p1.setMajor("软件工程");
        p1.setGradeClass("2023级2班");
        p1.setIdCard("510000200103150000");
        Map<String, Object> extra1 = new HashMap<>();
        extra1.put("phone", "138****0001");
        extra1.put("email", "zhangxiaoming@stu.example.com");
        extra1.put("campus", "北湖校区");
        extra1.put("dorm", "A3-402");
        extra1.put("bloodType", "O型");
        extra1.put("politicalStatus", "中共党员");
        extra1.put("hobbies", List.of("篮球", "ACM竞赛", "阅读"));
        extra1.put("careerGoal", "算法工程师");
        extra1.put("povertyLevel", "困难生");
        extra1.put("fatherName", "张建国");
        extra1.put("fatherPhone", "139****8888");
        extra1.put("motherName", "李秀兰");
        extra1.put("motherPhone", "136****9999");
        extra1.put("emergencyContact1", "张建国 139****8888");
        extra1.put("emergencyContact2", "李明 138****7777");
        p1.setExtraInfo(extra1);
        list.add(p1);

        StudentProfile p2 = new StudentProfile();
        p2.setStudentId("202301043");
        p2.setName("李四");
        p2.setTeacherId("T001");
        p2.setAvatar("https://i.pravatar.cc/150?u=student2");
        p2.setCollege("计算机学院");
        p2.setMajor("软件工程");
        p2.setGradeClass("2023级2班");
        p2.setIdCard("510000200205200000");
        Map<String, Object> extra2 = new HashMap<>();
        extra2.put("phone", "159****0002");
        extra2.put("email", "lisi@stu.example.com");
        extra2.put("campus", "北湖校区");
        extra2.put("dorm", "A3-405");
        extra2.put("bloodType", "A型");
        extra2.put("politicalStatus", "共青团员");
        extra2.put("hobbies", List.of("游戏", "动漫"));
        extra2.put("careerGoal", "前端开发");
        extra2.put("povertyLevel", "一般困难");
        extra2.put("fatherName", "李大强");
        extra2.put("fatherPhone", "158****6666");
        extra2.put("motherName", "王芳");
        extra2.put("motherPhone", "158****5555");
        extra2.put("emergencyContact1", "李大强 158****6666");
        p2.setExtraInfo(extra2);
        list.add(p2);

        StudentProfile p3 = new StudentProfile();
        p3.setStudentId("202301044");
        p3.setName("王五");
        p3.setTeacherId("T001");
        p3.setAvatar("https://i.pravatar.cc/150?u=student3");
        p3.setCollege("计算机学院");
        p3.setMajor("计算机科学与技术");
        p3.setGradeClass("2023级1班");
        p3.setIdCard("510000200308150000");
        Map<String, Object> extra3 = new HashMap<>();
        extra3.put("phone", "177****0003");
        extra3.put("email", "wangwu@stu.example.com");
        extra3.put("campus", "华凤校区");
        extra3.put("dorm", "B1-208");
        extra3.put("bloodType", "B型");
        extra3.put("politicalStatus", "共青团员");
        extra3.put("hobbies", List.of("足球", "摄影", "跑步"));
        extra3.put("careerGoal", "全栈开发工程师");
        extra3.put("povertyLevel", "无");
        extra3.put("fatherName", "王建国");
        extra3.put("fatherPhone", "177****3333");
        extra3.put("motherName", "赵丽");
        extra3.put("motherPhone", "177****4444");
        extra3.put("emergencyContact1", "王建国 177****3333");
        p3.setExtraInfo(extra3);
        list.add(p3);

        repo.saveAll(list);
        System.out.println("Initialized default StudentProfiles.");
    }

    private void initMessages(MessageRepository repo) {
        if (repo.count() > 0) return;
        List<Message> list = new ArrayList<>();

        // 辅导员发给张小明
        Message m1 = new Message();
        m1.setSenderId("T001"); m1.setSenderRole("teacher");
        m1.setReceiverId("202301042");
        m1.setContent("张小明同学，关于你申请的奖学金材料，我发现缺少了上学期的成绩单复印件，请尽快补齐交到办公室。");
        m1.setIsRead(true); m1.setIsRecalled(false);
        m1.setSentTime(LocalDateTime.now().minusDays(2).minusHours(1));
        list.add(m1);

        Message m2 = new Message();
        m2.setSenderId("T001"); m2.setSenderRole("teacher");
        m2.setReceiverId("202301042");
        m2.setContent("如果找不到原件，可以去教务处重新打印一份盖章。");
        m2.setIsRead(true); m2.setIsRecalled(false);
        m2.setSentTime(LocalDateTime.now().minusDays(2).minusHours(1).plusMinutes(1));
        list.add(m2);

        // 张小明回复
        Message m3 = new Message();
        m3.setSenderId("202301042"); m3.setSenderRole("student");
        m3.setReceiverId("T001");
        m3.setContent("好的李老师，我明天上午就带过去给您。");
        m3.setIsRead(true); m3.setIsRecalled(false);
        m3.setSentTime(LocalDateTime.now().minusDays(2));
        list.add(m3);

        // 辅导员发给李四
        Message m4 = new Message();
        m4.setSenderId("T001"); m4.setSenderRole("teacher");
        m4.setReceiverId("202301043");
        m4.setContent("李四同学，最近你的出勤率有些低，请注意按时上课。如果身体不适，请及时请假。");
        m4.setIsRead(false); m4.setIsRecalled(false);
        m4.setSentTime(LocalDateTime.now().minusHours(3));
        list.add(m4);

        // 辅导员发给王五
        Message m5 = new Message();
        m5.setSenderId("T001"); m5.setSenderRole("teacher");
        m5.setReceiverId("202301044");
        m5.setContent("王五，你参与的机房维护勤工助学岗位表现不错，继续加油！");
        m5.setIsRead(false); m5.setIsRecalled(false);
        m5.setSentTime(LocalDateTime.now().minusHours(1));
        list.add(m5);

        repo.saveAll(list);
        System.out.println("Initialized default Messages.");
    }
}
