package com.studentaffairs.backend.config;

import com.studentaffairs.backend.entity.AcademicRecord;
import com.studentaffairs.backend.entity.Activity;
import com.studentaffairs.backend.entity.Announcement;
import com.studentaffairs.backend.entity.Course;
import com.studentaffairs.backend.entity.HonorCandidate;
import com.studentaffairs.backend.entity.HonorProject;
import com.studentaffairs.backend.entity.Message;
import com.studentaffairs.backend.entity.SafetyIncident;
import com.studentaffairs.backend.entity.SecondClassroomRecord;
import com.studentaffairs.backend.entity.StudentApplication;
import com.studentaffairs.backend.entity.StudentProfile;
import com.studentaffairs.backend.entity.WorkStudyJob;
import com.studentaffairs.backend.entity.YouthAward;
import com.studentaffairs.backend.repository.AcademicRecordRepository;
import com.studentaffairs.backend.repository.ActivityRepository;
import com.studentaffairs.backend.repository.AnnouncementRepository;
import com.studentaffairs.backend.repository.CourseRepository;
import com.studentaffairs.backend.repository.HonorCandidateRepository;
import com.studentaffairs.backend.repository.HonorProjectRepository;
import com.studentaffairs.backend.repository.MessageRepository;
import com.studentaffairs.backend.repository.SafetyIncidentRepository;
import com.studentaffairs.backend.repository.SecondClassroomRecordRepository;
import com.studentaffairs.backend.repository.StudentApplicationRepository;
import com.studentaffairs.backend.repository.StudentProfileRepository;
import com.studentaffairs.backend.repository.WorkStudyJobRepository;
import com.studentaffairs.backend.repository.YouthAwardRepository;
import com.studentaffairs.backend.entity.Feedback;
import com.studentaffairs.backend.repository.FeedbackRepository;
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
                                      MessageRepository messageRepository,
                                      ActivityRepository activityRepository,
                                      YouthAwardRepository youthAwardRepository,
                                      HonorProjectRepository honorProjectRepository,
                                      HonorCandidateRepository honorCandidateRepository,
                                      SecondClassroomRecordRepository secondClassroomRecordRepository,
                                      CourseRepository courseRepository,
                                      FeedbackRepository feedbackRepository) {
        return args -> {
            // 初始化勤工助学岗位
            if (jobRepository.count() == 0) {
                WorkStudyJob job1 = new WorkStudyJob();
                job1.setTitle("图书馆阅览室助理 (支持并发测试)");
                job1.setDepartment("图书馆");
                job1.setQuota(2);
                job1.setCurrentCount(0);
                job1.setStatus("ACTIVE");
                job1.setHourlyRate(20.0);
                job1.setLocation("校本部图书馆总馆");
                job1.setRequirements("负责图书整理、日常借还咨询与阅览室秩序维护，做事细心认真。");
                job1.setWorkTime("每周一至周五下午 14:00 - 18:00 分班轮值");

                WorkStudyJob job2 = new WorkStudyJob();
                job2.setTitle("计算机学院机房维护");
                job2.setDepartment("计算机学院");
                job2.setQuota(5);
                job2.setCurrentCount(3);
                job2.setStatus("ACTIVE");
                job2.setHourlyRate(22.5);
                job2.setLocation("理科楼 A 栋 3 楼机房");
                job2.setRequirements("协助系统管理员进行机房软硬件日常巡检、故障登记及基本维护，计算机相关专业优先。");
                job2.setWorkTime("每周六、周日全天灵活排班");

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
                // 张小明 2024-2025-2 学期
                records.add(createRecord("202301042", "张小明", "软工2班", "操作系统", "必修", 92f, 4f, "2024-2025-2", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "计算机网络", "必修", 88f, 3f, "2024-2025-2", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "高等数学(下)", "必修", 85f, 5f, "2024-2025-2", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "英语(三)", "必修", 79f, 3f, "2024-2025-2", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "计算机组成原理", "必修", 90f, 4f, "2024-2025-2", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "概率论与数理统计", "必修", 87f, 3f, "2024-2025-2", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "Java程序设计", "选修", 93f, 2f, "2024-2025-2", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "大学体育(三)", "必修", 90f, 1f, "2024-2025-2", false));
                // 张小明 2025-2026-1 学期
                records.add(createRecord("202301042", "张小明", "软工2班", "编译原理", "必修", 89f, 4f, "2025-2026-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "软件测试", "必修", 86f, 3f, "2025-2026-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "人工智能导论", "选修", 94f, 2f, "2025-2026-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "Web前端开发", "选修", 91f, 2f, "2025-2026-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "分布式系统", "必修", 83f, 3f, "2025-2026-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "马克思主义原理", "必修", 88f, 2f, "2025-2026-1", false));
                records.add(createRecord("202301042", "张小明", "软工2班", "大学体育(四)", "必修", 92f, 1f, "2025-2026-1", false));

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
            // 初始化课程演示数据
            if (courseRepository.count() == 0) {
                List<Course> courses = new ArrayList<>();
                courses.add(createCourse("CS201", "数据结构", "必修", 4f, 64, "2025-2026-1", "张教授", "2023级软工1班、2班"));
                courses.add(createCourse("CS202", "算法设计与分析", "必修", 3f, 48, "2025-2026-1", "李教授", "2023级软工1班、2班"));
                courses.add(createCourse("CS301", "软件工程", "必修", 3f, 48, "2025-2026-1", "王副教授", "2023级软工2班"));
                courses.add(createCourse("CS205", "数据库原理", "必修", 4f, 64, "2025-2026-1", "赵教授", "2023级软工1班、2班"));
                courses.add(createCourse("EN401", "大学英语(四)", "必修", 2f, 32, "2025-2026-1", "刘讲师", "2023级软工1班、2班"));
                courses.add(createCourse("CS250", "Python数据分析", "选修", 2f, 32, "2025-2026-1", "陈副教授", "2023级软工1班"));
                courseRepository.saveAll(courses);
                System.out.println("Initialized default Courses.");
            }

            initYouthData(activityRepository, youthAwardRepository, honorProjectRepository, honorCandidateRepository, secondClassroomRecordRepository);
            initFeedbacks(feedbackRepository);
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
        a2.setType("LEAVE"); a2.setTitle("周末外出报备（2026-05-16至05-17）");
        a2.setReason("{\"leaveType\":\"HOMETOWN\",\"startDate\":\"2026-05-16\",\"endDate\":\"2026-05-17\",\"destination\":\"四川省成都市·家庭住址\",\"reason\":\"本周末需返回家乡参加家庭事务，请批准2天外出申请，保证按时返校。\",\"contact\":\"13912345678\"}");
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

    private void initYouthData(ActivityRepository activityRepo,
                               YouthAwardRepository awardRepo,
                               HonorProjectRepository honorProjectRepo,
                               HonorCandidateRepository honorCandidateRepo,
                               SecondClassroomRecordRepository secondClassroomRepo) {
        if (activityRepo.count() < 12) {
            activityRepo.deleteAll();
            List<Activity> activities = new ArrayList<>();
            
            Activity act1 = new Activity();
            act1.setTitle("第十七届电脑文化艺术节闭幕式");
            act1.setDescription("校园文化艺术节闭幕式暨优秀作品展演，包含舞蹈、合唱、器乐及程序作品展览，展现大学生科技艺术风采。");
            act1.setDate("2026-06-15"); act1.setLocation("大学生活动中心大礼堂"); act1.setParticipants(120);
            act1.setMaxParticipants(500); act1.setStatus("报名中"); act1.setCredits(2.0);
            act1.setLevel("校级"); act1.setBgGradient("from-purple-500 to-pink-500");
            act1.setCoverImage("https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg");
            act1.setEnrollTime("2026-05-20 ~ 2026-06-10");
            act1.setTimeDetail("2026-06-15 18:30-21:00"); act1.setRangeName("全体学生");
            act1.setLeaveSupport("支持请假"); act1.setCreditType("文艺体育项目学分"); act1.setEnrollLimit("500人");
            activities.add(act1);

            Activity act2 = new Activity();
            act2.setTitle("软件工程专业编程马拉松");
            act2.setDescription("48小时极限编程挑战赛，自由组队（2-4人），在规定时间内完成指定方向的项目系统研发。");
            act2.setDate("2026-06-20"); act2.setLocation("计算机学院协同创新实验室"); act2.setParticipants(45);
            act2.setMaxParticipants(80); act2.setStatus("报名中"); act2.setCredits(3.0);
            act2.setLevel("院级"); act2.setBgGradient("from-blue-500 to-cyan-500");
            act2.setCoverImage("https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg");
            act2.setEnrollTime("2026-05-25 ~ 2026-06-18");
            act2.setTimeDetail("2026-06-20 08:00 - 2026-06-22 08:00"); act2.setRangeName("计算机学院");
            act2.setLeaveSupport("支持请假"); act2.setCreditType("创新创造项目学分"); act2.setEnrollLimit("80人");
            activities.add(act2);

            Activity act3 = new Activity();
            act3.setTitle("志愿者服务：社区义务支教");
            act3.setDescription("前往北湖社区小学开展义务支教活动，辅导困难家庭小学生课业，并策划开展趣味科学小实验。");
            act3.setDate("2026-05-18"); act3.setLocation("北湖社区小学教室"); act3.setParticipants(30);
            act3.setMaxParticipants(30); act3.setStatus("已结束"); act3.setCredits(1.5);
            act3.setLevel("院级"); act3.setBgGradient("from-green-500 to-teal-500");
            act3.setCoverImage("https://fuss10.elemecdn.com/1/34/19aa98b1fcb2781c4fba33d850549jpeg.jpeg");
            act3.setEnrollTime("2026-05-01 ~ 2026-05-15");
            act3.setTimeDetail("2026-05-18 14:00-17:00"); act3.setRangeName("计算机学院");
            act3.setLeaveSupport("不支持"); act3.setCreditType("志愿服务项目学分"); act3.setEnrollLimit("30人");
            activities.add(act3);

            Activity act4 = new Activity();
            act4.setTitle("毕业季摄影大赛");
            act4.setDescription("用镜头和快门记录最美校园瞬间。所有参赛照片经评审后将在校图书馆一楼大厅进行为期半个月的展览展示。");
            act4.setDate("2026-05-10"); act4.setLocation("全校范围"); act4.setParticipants(85);
            act4.setMaxParticipants(200); act4.setStatus("已结束"); act4.setCredits(1.0);
            act4.setLevel("校级"); act4.setBgGradient("from-orange-500 to-red-500");
            act4.setCoverImage("https://fuss10.elemecdn.com/9/bb/e27858e973f5d7d3904835f46abbdjpeg.jpeg");
            act4.setEnrollTime("2026-04-15 ~ 2026-05-08");
            act4.setTimeDetail("2026-05-10 09:00-18:00"); act4.setRangeName("全体学生");
            act4.setLeaveSupport("不需要"); act4.setCreditType("文艺体育项目学分"); act4.setEnrollLimit("200人");
            activities.add(act4);

            Activity act5 = new Activity();
            act5.setTitle("AI赋能未来技术前沿讲座");
            act5.setDescription("特邀人工智能领域知名教授，围绕大语言模型、深度学习及智能机器人产业应用展开学术前沿交流分享。");
            act5.setDate("2026-05-25"); act5.setLocation("学术报告厅"); act5.setParticipants(185);
            act5.setMaxParticipants(300); act5.setStatus("报名中"); act5.setCredits(1.5);
            act5.setLevel("校级"); act5.setBgGradient("from-indigo-500 to-purple-500");
            act5.setCoverImage("https://images.unsplash.com/photo-1591453089816-0fbb971b454c?q=80&w=200");
            act5.setEnrollTime("2026-05-15 ~ 2026-05-24");
            act5.setTimeDetail("2026-05-25 19:00-21:00"); act5.setRangeName("全体学生");
            act5.setLeaveSupport("支持请假"); act5.setCreditType("思想素质项目学分"); act5.setEnrollLimit("300人");
            activities.add(act5);

            Activity act6 = new Activity();
            act6.setTitle("心理健康与减压交流沙龙");
            act6.setDescription("邀请校心理咨询中心专业导师，通过心理小游戏与小组共创交流，帮助大家舒缓课业压力，建立积极心态。");
            act6.setDate("2026-05-28"); act6.setLocation("心理疏导中心多功能厅"); act6.setParticipants(25);
            act6.setMaxParticipants(40); act6.setStatus("报名中"); act6.setCredits(1.0);
            act6.setLevel("院级"); act6.setBgGradient("from-teal-400 to-emerald-500");
            act6.setCoverImage("https://images.unsplash.com/photo-1527192491265-7e15c55b1ed2?q=80&w=200");
            act6.setEnrollTime("2026-05-18 ~ 2026-05-27");
            act6.setTimeDetail("2026-05-28 15:00-17:00"); act6.setRangeName("计算机学院");
            act6.setLeaveSupport("不支持"); act6.setCreditType("思想素质项目学分"); act6.setEnrollLimit("40人");
            activities.add(act6);

            Activity act7 = new Activity();
            act7.setTitle("校园3V3篮球挑战赛");
            act7.setDescription("激情夏日，燃动球场！校园3V3街头篮球争霸赛，团队自由组合，赢取丰厚运动装备奖励及优胜奖杯。");
            act7.setDate("2026-05-30"); act7.setLocation("东区篮球场"); act7.setParticipants(48);
            act7.setMaxParticipants(64); act7.setStatus("报名中"); act7.setCredits(2.0);
            act7.setLevel("校级"); act7.setBgGradient("from-orange-400 to-amber-500");
            act7.setCoverImage("https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=200");
            act7.setEnrollTime("2026-05-20 ~ 2026-05-29");
            act7.setTimeDetail("2026-05-30 08:30-18:00"); act7.setRangeName("全体学生");
            act7.setLeaveSupport("不需要"); act7.setCreditType("文艺体育项目学分"); act7.setEnrollLimit("64人");
            activities.add(act7);

            Activity act8 = new Activity();
            act8.setTitle("英语角：跨文化交流研讨会");
            act8.setDescription("与来自各国的留学生 and 专业外教面对面探讨中西方文化差异，通过趣味话题PK锻炼口语和跨文化交际能力。");
            act8.setDate("2026-05-14"); act8.setLocation("外语沙龙大厅"); act8.setParticipants(38);
            act8.setMaxParticipants(60); act8.setStatus("已结束"); act8.setCredits(1.0);
            act8.setLevel("院级"); act8.setBgGradient("from-sky-400 to-blue-500");
            act8.setCoverImage("https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?q=80&w=200");
            act8.setEnrollTime("2026-05-01 ~ 2026-05-12");
            act8.setTimeDetail("2026-05-14 19:00-21:00"); act8.setRangeName("外国语学院");
            act8.setLeaveSupport("不支持"); act8.setCreditType("技能特长项目学分"); act8.setEnrollLimit("60人");
            activities.add(act8);

            Activity act9 = new Activity();
            act9.setTitle("环境美化绿植种植志愿活动");
            act9.setDescription("弘扬低碳环保精神，在校园绿化带开展初夏绿植亲手种植与挂牌科普志愿服务，美化校园生态环境。");
            act9.setDate("2026-05-12"); act9.setLocation("校园西区绿地"); act9.setParticipants(50);
            act9.setMaxParticipants(50); act9.setStatus("已结束"); act9.setCredits(1.5);
            act9.setLevel("校级"); act9.setBgGradient("from-emerald-500 to-green-600");
            act9.setCoverImage("https://images.unsplash.com/photo-1559027615-cd4487c6592a?q=80&w=200");
            act9.setEnrollTime("2026-05-01 ~ 2026-05-10");
            act9.setTimeDetail("2026-05-12 09:00-12:00"); act9.setRangeName("全体学生");
            act9.setLeaveSupport("支持请假"); act9.setCreditType("志愿服务项目学分"); act9.setEnrollLimit("50人");
            activities.add(act9);

            Activity act10 = new Activity();
            act10.setTitle("大学生创新创业大赛项目宣讲会");
            act10.setDescription("深入解读本届全国大学生“互联网+”、“挑战杯”等创新创业赛事最新政策与红头文件，梳理优秀商业计划书逻辑。");
            act10.setDate("2026-05-23"); act10.setLocation("图书馆报告厅"); act10.setParticipants(92);
            act10.setMaxParticipants(150); act10.setStatus("报名中"); act10.setCredits(1.5);
            act10.setLevel("校级"); act10.setBgGradient("from-pink-500 to-rose-600");
            act10.setCoverImage("https://images.unsplash.com/photo-1515187029135-18ee286d815b?q=80&w=200");
            act10.setEnrollTime("2026-05-12 ~ 2026-05-22");
            act10.setTimeDetail("2026-05-23 14:30-16:30"); act10.setRangeName("全体学生");
            act10.setLeaveSupport("不需要"); act10.setCreditType("创新创造项目学分"); act10.setEnrollLimit("150人");
            activities.add(act10);

            Activity act11 = new Activity();
            act11.setTitle("经典诗词诵读大赛");
            act11.setDescription("“雅言传承文明，经典浸润人生”，通过诵读红色经典和传统诗词，提升人文素养与经典赏析水平。");
            act11.setDate("2026-05-15"); act11.setLocation("博学楼学术多功能报告厅"); act11.setParticipants(60);
            act11.setMaxParticipants(100); act11.setStatus("已结束"); act11.setCredits(2.0);
            act11.setLevel("校级"); act11.setBgGradient("from-amber-500 to-yellow-600");
            act11.setCoverImage("https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?q=80&w=200");
            act11.setEnrollTime("2026-05-01 ~ 2026-05-12");
            act11.setTimeDetail("2026-05-15 14:00-17:00"); act11.setRangeName("全体学生");
            act11.setLeaveSupport("不需要"); act11.setCreditType("思想素质项目学分"); act11.setEnrollLimit("100人");
            activities.add(act11);

            Activity act12 = new Activity();
            act12.setTitle("金秋校园十佳歌手大决选");
            act12.setDescription("青春唱响，声动飞扬！本届十佳歌手总决赛正式开放门票和大众评委报名预约，见证巅峰之声。");
            act12.setDate("2026-10-18"); act12.setLocation("体育馆主场馆"); act12.setParticipants(320);
            act12.setMaxParticipants(800); act12.setStatus("报名中"); act12.setCredits(2.0);
            act12.setLevel("校级"); act12.setBgGradient("from-violet-500 to-fuchsia-500");
            act12.setCoverImage("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?q=80&w=200");
            act12.setEnrollTime("2026-10-01 ~ 2026-10-15");
            act12.setTimeDetail("2026-10-18 19:00-22:00"); act12.setRangeName("全体学生");
            act12.setLeaveSupport("不需要"); act12.setCreditType("文艺体育项目学分"); act12.setEnrollLimit("800人");
            activities.add(act12);

            activityRepo.saveAll(activities);
            System.out.println("Initialized default Activities (12 elements).");
        }

        if (awardRepo.count() == 0) {
            List<YouthAward> awards = new ArrayList<>();
            YouthAward aw1 = new YouthAward();
            aw1.setStudentId("202301042"); aw1.setStudentName("张小明");
            aw1.setAwardName("全国大学生数学建模竞赛一等奖"); aw1.setLevel("国家级"); aw1.setCategory("学科竞赛");
            aw1.setAwardTime("2024-03"); aw1.setStatus("APPROVED");
            aw1.setDescription("2023年全国大学生数学建模竞赛本科组一等奖，参赛论文《基于深度学习的城市交通流预测》。");
            awards.add(aw1);

            YouthAward aw2 = new YouthAward();
            aw2.setStudentId("202301043"); aw2.setStudentName("李四");
            aw2.setAwardName("蓝桥杯程序设计竞赛省二等奖"); aw2.setLevel("省部级"); aw2.setCategory("学科竞赛");
            aw2.setAwardTime("2024-04"); aw2.setStatus("APPROVED");
            aw2.setDescription("第十五届蓝桥杯全国软件和信息技术专业人才大赛四川赛区二等奖。");
            awards.add(aw2);

            YouthAward aw3 = new YouthAward();
            aw3.setStudentId("202301042"); aw3.setStudentName("张小明");
            aw3.setAwardName("校级优秀学生干部"); aw3.setLevel("校级"); aw3.setCategory("荣誉称号");
            aw3.setAwardTime("2024-05"); aw3.setStatus("PENDING");
            aw3.setDescription("2023-2024学年校级优秀学生干部评选。");
            awards.add(aw3);

            YouthAward aw4 = new YouthAward();
            aw4.setStudentId("202301044"); aw4.setStudentName("王五");
            aw4.setAwardName("校园足球联赛最佳射手"); aw4.setLevel("校级"); aw4.setCategory("文体活动");
            aw4.setAwardTime("2024-04"); aw4.setStatus("APPROVED");
            aw4.setDescription("2024年校园足球春季联赛最佳射手，赛季进球12个。");
            awards.add(aw4);

            YouthAward aw5 = new YouthAward();
            aw5.setStudentId("202301042"); aw5.setStudentName("张小明");
            aw5.setAwardName("校优秀志愿者"); aw5.setLevel("校级"); aw5.setCategory("荣誉称号");
            aw5.setAwardTime("2024-05"); aw5.setStatus("PENDING");
            aw5.setDescription("2023-2024学年志愿服务时长超100小时，获评校优秀志愿者。");
            awards.add(aw5);

            awardRepo.saveAll(awards);
            System.out.println("Initialized default YouthAwards.");
        }

        if (honorProjectRepo.count() == 0) {
            HonorProject hp1 = new HonorProject();
            hp1.setName("2024年度校级优秀毕业生评选"); hp1.setDescription("评选2024届校级优秀毕业生，要求在校期间综合表现优异，无违纪记录。");
            hp1.setQuota(10); hp1.setApplied(8); hp1.setDeadline("2024-06-30"); hp1.setStatus("进行中");
            honorProjectRepo.save(hp1);

            HonorProject hp2 = new HonorProject();
            hp2.setName("2023-2024学年三好学生评选"); hp2.setDescription("评选三好学生，要求德智体全面发展，学年GPA不低于3.5。");
            hp2.setQuota(20); hp2.setApplied(25); hp2.setDeadline("2024-07-15"); hp2.setStatus("进行中");
            honorProjectRepo.save(hp2);

            HonorProject hp3 = new HonorProject();
            hp3.setName("2023年度国家励志奖学金评定"); hp3.setDescription("面向家庭经济困难且品学兼优的学生，奖励标准为5000元/人。");
            hp3.setQuota(5); hp3.setApplied(12); hp3.setDeadline("2024-05-20"); hp3.setStatus("已关闭");
            honorProjectRepo.save(hp3);

            System.out.println("Initialized default HonorProjects.");

            // 为项目1添加候选人
            HonorCandidate hc1 = new HonorCandidate();
            hc1.setProjectId(hp1.getId()); hc1.setStudentId("202301042"); hc1.setStudentName("张小明");
            hc1.setClassName("2023级2班"); hc1.setGpa(3.85); hc1.setStatus("候选");
            honorCandidateRepo.save(hc1);

            HonorCandidate hc2 = new HonorCandidate();
            hc2.setProjectId(hp1.getId()); hc2.setStudentId("202301044"); hc2.setStudentName("王五");
            hc2.setClassName("2023级1班"); hc2.setGpa(3.52); hc2.setStatus("候选");
            honorCandidateRepo.save(hc2);

            // 为项目2添加候选人
            HonorCandidate hc3 = new HonorCandidate();
            hc3.setProjectId(hp2.getId()); hc3.setStudentId("202301042"); hc3.setStudentName("张小明");
            hc3.setClassName("2023级2班"); hc3.setGpa(3.85); hc3.setStatus("候选");
            honorCandidateRepo.save(hc3);

            HonorCandidate hc4 = new HonorCandidate();
            hc4.setProjectId(hp2.getId()); hc4.setStudentId("202301043"); hc4.setStudentName("李四");
            hc4.setClassName("2023级2班"); hc4.setGpa(2.75); hc4.setStatus("候选");
            honorCandidateRepo.save(hc4);

            System.out.println("Initialized default HonorCandidates.");
        }

        if (secondClassroomRepo.count() == 0) {
            List<SecondClassroomRecord> records = new ArrayList<>();
            String[][] data = {
                {"202301042", "张小明", "2023级2班", "0", "8", "志愿服务类 — 社区义务支教"},
                {"202301042", "张小明", "2023级2班", "1", "6", "创新创业类 — 大学生创业计划大赛"},
                {"202301042", "张小明", "2023级2班", "2", "4", "社会实践类 — 暑期三下乡"},
                {"202301042", "张小明", "2023级2班", "3", "12", "学术讲座类 — 参加学术报告累计"},
                {"202301043", "李四", "2023级2班", "0", "4", "志愿服务类 — 校园清洁志愿者"},
                {"202301043", "李四", "2023级2班", "4", "3", "文体活动类 — 院篮球赛"},
                {"202301044", "王五", "2023级1班", "0", "6", "志愿服务类 — 无偿献血活动"},
                {"202301044", "王五", "2023级1班", "3", "8", "学术讲座类 — 参加学术报告累计"},
                {"202301044", "王五", "2023级1班", "4", "10", "文体活动类 — 校足球联赛"},
                {"202301044", "王五", "2023级1班", "5", "4", "技能培训类 — Python高级应用培训"},
                {"202301042", "张小明", "2023级2班", "1", "6", "创新创业类 — 互联网+创新创业大赛"},
                {"202301042", "张小明", "2023级2班", "2", "8", "社会实践类 — 暑期企业实习"},
                {"202301042", "张小明", "2023级2班", "4", "8", "文体活动类 — 校运动会"},
                {"202301042", "张小明", "2023级2班", "5", "6", "技能培训类 — 大数据技术培训"},
            };
            for (String[] d : data) {
                SecondClassroomRecord r = new SecondClassroomRecord();
                r.setStudentId(d[0]); r.setStudentName(d[1]); r.setClassName(d[2]);
                r.setCategoryIndex(Integer.parseInt(d[3])); r.setHours(Integer.parseInt(d[4]));
                r.setReason(d[5]); r.setGrantTime(LocalDateTime.now().minusDays((long)(Math.random() * 30)));
                records.add(r);
            }
            secondClassroomRepo.saveAll(records);
            System.out.println("Initialized default SecondClassroomRecords.");
        }
    }

    private Course createCourse(String code, String name, String type, Float credit,
                                Integer hours, String semester, String teacher, String className) {
        Course c = new Course();
        c.setCode(code);
        c.setName(name);
        c.setType(type);
        c.setCredit(credit);
        c.setHours(hours);
        c.setSemester(semester);
        c.setTeacher(teacher);
        c.setClassName(className);
        return c;
    }

    private void initFeedbacks(FeedbackRepository feedbackRepo) {
        if (feedbackRepo.count() == 0) {
            List<Feedback> list = new ArrayList<>();

            Feedback f1 = new Feedback();
            f1.setStudentId("202301043");
            f1.setStudentName("李四");
            f1.setContent("学校南门的共享单车停放区太小了，经常停不下，建议扩建。");
            f1.setReply("感谢建议，后勤处已将南门外区域纳入下个月的扩建规划。");
            f1.setReplyTime(LocalDateTime.now().minusDays(1));
            f1.setCreateTime(LocalDateTime.now().minusDays(1).minusHours(1).minusMinutes(30));
            f1.setStatus("REPLIED");
            list.add(f1);

            Feedback f2 = new Feedback();
            f2.setStudentId("202301044");
            f2.setStudentName("王五");
            f2.setContent("图书馆二楼的空调制冷效果不好，希望能报修。");
            f2.setReply("已通知维修人员，预计本周五前完成检修。");
            f2.setReplyTime(LocalDateTime.now().minusDays(3));
            f2.setCreateTime(LocalDateTime.now().minusDays(3).minusHours(2).minusMinutes(30));
            f2.setStatus("REPLIED");
            list.add(f2);

            Feedback f3 = new Feedback();
            f3.setStudentId("202301045");
            f3.setStudentName("赵六");
            f3.setContent("希望食堂能增加一些清淡口味的窗口。");
            f3.setReply(null);
            f3.setReplyTime(null);
            f3.setCreateTime(LocalDateTime.now().minusHours(8));
            f3.setStatus("PENDING");
            list.add(f3);

            feedbackRepo.saveAll(list);
            System.out.println("Initialized default Feedbacks.");
        }
    }
}
