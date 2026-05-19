package com.studentaffairs.backend.service;

import com.studentaffairs.backend.entity.CareerAppointment;
import com.studentaffairs.backend.repository.CareerAppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CareerService {

    private final CareerAppointmentRepository appointmentRepository;

    public CareerService(CareerAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // ==================== 预约管理 ====================

    public CareerAppointment createAppointment(CareerAppointment appointment) {
        appointment.setStatus("PENDING");
        appointment.setCreateTime(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    public List<CareerAppointment> getStudentAppointments(String studentId) {
        return appointmentRepository.findByStudentIdOrderByCreateTimeDesc(studentId);
    }

    public List<CareerAppointment> getTeacherAppointments(String teacherId) {
        return appointmentRepository.findByTeacherIdOrderByCreateTimeDesc(teacherId);
    }

    public Optional<CareerAppointment> acceptAppointment(Long id) {
        return appointmentRepository.findById(id).map(a -> {
            a.setStatus("ACCEPTED");
            return appointmentRepository.save(a);
        });
    }

    public Optional<CareerAppointment> rejectAppointment(Long id) {
        return appointmentRepository.findById(id).map(a -> {
            a.setStatus("REJECTED");
            return appointmentRepository.save(a);
        });
    }

    // ==================== 职业画像库（30个职业） ====================

    private static final List<Map<String, Object>> JOB_LIBRARY = List.of(
        // IT/互联网
        createJob("软件工程师", "IRA", List.of("INTJ","INTP"),
            List.of(0.7,0.5,0.6,0.4,0.3,0.9,0.8,0.5), List.of(0.5,0.9,0.6,0.4,0.3,0.5,0.8,0.7),
            "设计、开发和维护软件系统，编写高质量代码，参与技术架构设计", "本科及以上，计算机相关专业", "15-50K"),
        createJob("数据分析师", "ICR", List.of("INTJ","ISTJ"),
            List.of(0.6,0.4,0.7,0.3,0.4,0.8,0.6,0.5), List.of(0.5,0.9,0.4,0.3,0.3,0.3,0.6,0.8),
            "收集、处理和分析数据，为业务决策提供数据支持", "本科及以上，统计学/数学/计算机", "12-35K"),
        createJob("AI算法工程师", "IRA", List.of("INTP","INTJ"),
            List.of(0.5,0.3,0.5,0.3,0.2,0.9,0.7,0.6), List.of(0.4,0.95,0.5,0.3,0.2,0.4,0.9,0.8),
            "研发机器学习模型和深度学习算法，解决复杂AI问题", "硕士及以上，AI/计算机", "25-80K"),
        createJob("产品经理", "EAS", List.of("ENTJ","ENFP"),
            List.of(0.8,0.5,0.7,0.8,0.9,0.7,0.6,0.4), List.of(0.7,0.5,0.5,0.9,0.8,0.3,0.6,0.5),
            "定义产品方向，协调设计、研发、测试团队推动产品落地", "本科及以上，不限专业", "18-45K"),
        createJob("UI/UX设计师", "ARI", List.of("INFP","ENFP"),
            List.of(0.6,0.4,0.5,0.6,0.7,0.8,0.5,0.3), List.of(0.6,0.4,0.8,0.5,0.3,0.4,0.9,0.4),
            "设计产品界面和用户体验，进行用户研究和原型设计", "本科及以上，设计相关专业", "12-30K"),

        // 金融/商业
        createJob("金融分析师", "ICE", List.of("INTJ","ISTJ"),
            List.of(0.7,0.5,0.8,0.4,0.6,0.6,0.7,0.3), List.of(0.6,0.8,0.3,0.4,0.5,0.2,0.4,0.7),
            "分析金融市场数据，撰写研究报告，提供投资建议", "本科及以上，金融/经济", "15-40K"),
        createJob("会计师", "CEI", List.of("ISTJ","ESTJ"),
            List.of(0.8,0.6,0.9,0.4,0.4,0.4,0.5,0.3), List.of(0.6,0.7,0.3,0.4,0.6,0.2,0.3,0.6),
            "负责财务报表编制、审计、税务筹划等财会工作", "本科及以上，会计/财务", "8-25K"),
        createJob("管理咨询顾问", "EAS", List.of("ENTJ","ESTJ"),
            List.of(0.9,0.6,0.7,0.7,0.9,0.7,0.8,0.4), List.of(0.8,0.7,0.5,0.8,0.9,0.3,0.7,0.6),
            "为企业提供战略、运营、组织等方面的管理咨询服务", "本科及以上，MBA优先", "20-60K"),
        createJob("市场营销经理", "EAS", List.of("ENTJ","ESTP"),
            List.of(0.8,0.6,0.6,0.9,0.9,0.7,0.5,0.4), List.of(0.8,0.5,0.4,0.9,0.7,0.2,0.5,0.5),
            "制定营销策略，管理品牌推广和市场活动", "本科及以上，市场营销", "15-35K"),

        // 教育/科研
        createJob("大学教授/讲师", "SIA", List.of("INFJ","INTJ"),
            List.of(0.6,0.5,0.5,0.8,0.7,0.8,0.4,0.6), List.of(0.7,0.6,0.4,0.8,0.5,0.3,0.6,0.7),
            "从事高等教育教学与科研工作，指导学生，发表学术论文", "博士，相关学科", "10-25K"),
        createJob("中学教师", "SAI", List.of("ENFJ","INFJ"),
            List.of(0.5,0.4,0.6,0.9,0.8,0.5,0.3,0.5), List.of(0.6,0.4,0.3,0.9,0.4,0.2,0.4,0.5),
            "承担学科教学任务，进行班级管理和学生辅导", "本科及以上，师范专业", "5-15K"),
        createJob("培训师", "SEA", List.of("ENFJ","ENTJ"),
            List.of(0.7,0.5,0.5,0.9,0.8,0.6,0.5,0.5), List.of(0.8,0.5,0.4,0.9,0.6,0.2,0.5,0.6),
            "设计和交付企业培训课程，提升员工职业技能", "本科及以上", "12-30K"),
        createJob("科研人员", "IRA", List.of("INTP","INTJ"),
            List.of(0.4,0.4,0.5,0.3,0.3,0.9,0.5,0.5), List.of(0.4,0.9,0.5,0.3,0.3,0.6,0.7,0.8),
            "在科研机构从事基础或应用研究，发表高水平论文", "博士，理工科", "12-30K"),

        // 医疗/健康
        createJob("临床医生", "ISR", List.of("ISTJ","INFJ"),
            List.of(0.6,0.5,0.7,0.8,0.5,0.5,0.4,0.6), List.of(0.6,0.7,0.6,0.7,0.5,0.7,0.5,0.6),
            "诊断和治疗疾病，进行临床手术和患者管理", "硕士及以上，临床医学", "15-40K"),
        createJob("心理咨询师", "SAI", List.of("INFJ","INFP"),
            List.of(0.5,0.4,0.5,0.9,0.7,0.4,0.3,0.6), List.of(0.7,0.5,0.4,0.9,0.4,0.2,0.4,0.6),
            "提供心理咨询和治疗服务，帮助来访者解决心理问题", "硕士及以上，心理学", "8-20K"),
        createJob("护士/护理", "SRI", List.of("ISFJ","ESFJ"),
            List.of(0.5,0.4,0.7,0.9,0.5,0.3,0.3,0.5), List.of(0.5,0.5,0.4,0.8,0.6,0.6,0.3,0.5),
            "执行医嘱，进行患者护理和健康教育", "大专及以上，护理学", "5-15K"),

        // 工程/制造
        createJob("机械工程师", "RIE", List.of("ISTJ","ISTP"),
            List.of(0.6,0.3,0.6,0.4,0.5,0.7,0.5,0.4), List.of(0.5,0.7,0.7,0.3,0.4,0.8,0.6,0.5),
            "设计、开发和测试机械设备和系统", "本科及以上，机械工程", "10-28K"),
        createJob("电气工程师", "RIE", List.of("ISTJ","INTJ"),
            List.of(0.5,0.4,0.6,0.3,0.4,0.7,0.5,0.4), List.of(0.4,0.8,0.6,0.3,0.3,0.7,0.6,0.5),
            "设计、开发电力系统和电气设备", "本科及以上，电气工程", "12-30K"),
        createJob("建筑设计师", "ARI", List.of("INTJ","ENTJ"),
            List.of(0.6,0.4,0.5,0.4,0.6,0.8,0.5,0.3), List.of(0.5,0.6,0.9,0.4,0.5,0.4,0.8,0.4),
            "进行建筑方案设计，绘制施工图纸，协调项目施工", "本科及以上，建筑学", "12-35K"),

        // 创意/艺术
        createJob("平面设计师", "ARI", List.of("INFP","ISFP"),
            List.of(0.5,0.3,0.4,0.5,0.7,0.8,0.4,0.2), List.of(0.5,0.3,0.8,0.4,0.3,0.4,0.9,0.3),
            "设计海报、画册、品牌VI等视觉作品", "本科及以上，视觉传达", "7-20K"),
        createJob("影视导演", "AES", List.of("ENFJ","ENTP"),
            List.of(0.7,0.4,0.5,0.7,0.8,0.8,0.5,0.3), List.of(0.7,0.4,0.6,0.8,0.7,0.3,0.8,0.4),
            "负责影视作品的创意构思、镜头语言设计和团队调度", "本科及以上，导演/影视", "10-40K"),
        createJob("作家/编辑", "AIS", List.of("INFP","INFJ"),
            List.of(0.5,0.4,0.5,0.5,0.4,0.7,0.3,0.4), List.of(0.8,0.4,0.4,0.4,0.3,0.3,0.7,0.5),
            "进行文字创作、内容编辑和出版策划", "本科及以上，文学/新闻", "6-20K"),

        // 公共服务
        createJob("公务员", "CSE", List.of("ISTJ","ESTJ"),
            List.of(0.7,0.5,0.8,0.6,0.5,0.4,0.5,0.4), List.of(0.6,0.5,0.3,0.6,0.7,0.2,0.3,0.5),
            "执行国家行政管理工作，制定和实施公共政策", "本科及以上", "6-20K"),
        createJob("律师", "EIC", List.of("ENTJ","INTJ"),
            List.of(0.7,0.6,0.7,0.5,0.8,0.5,0.6,0.3), List.of(0.8,0.7,0.4,0.7,0.6,0.2,0.5,0.6),
            "提供法律咨询和代理，起草法律文件，出庭辩护", "硕士及以上，法学", "15-50K"),
        createJob("社会工作者", "SAE", List.of("ENFJ","INFJ"),
            List.of(0.5,0.4,0.5,0.9,0.7,0.4,0.3,0.5), List.of(0.5,0.3,0.3,0.9,0.5,0.2,0.3,0.4),
            "为弱势群体提供社会服务和支持，协调社区资源", "本科及以上，社会工作", "5-12K"),
        createJob("记者", "ASE", List.of("ENFP","ENTP"),
            List.of(0.7,0.4,0.5,0.7,0.6,0.6,0.4,0.4), List.of(0.8,0.4,0.4,0.8,0.5,0.2,0.5,0.6),
            "采访新闻事件，撰写和发布新闻报道", "本科及以上，新闻学", "7-18K"),

        // 企业/管理
        createJob("人力资源经理", "ESC", List.of("ENFJ","ESTJ"),
            List.of(0.7,0.5,0.7,0.9,0.7,0.4,0.5,0.4), List.of(0.6,0.4,0.3,0.9,0.7,0.2,0.3,0.5),
            "负责招聘、培训、绩效、薪酬等HR全盘工作", "本科及以上，人力资源", "12-30K"),
        createJob("项目经理", "EAS", List.of("ENTJ","ESTJ"),
            List.of(0.8,0.6,0.6,0.7,0.8,0.5,0.6,0.4), List.of(0.6,0.5,0.4,0.7,0.9,0.3,0.5,0.5),
            "规划、执行和交付项目，管理团队和资源", "本科及以上，PMP优先", "15-40K"),
        createJob("创业者", "EAS", List.of("ENTP","ENTJ"),
            List.of(0.8,0.5,0.5,0.7,0.9,0.7,0.7,0.4), List.of(0.6,0.5,0.5,0.7,0.8,0.4,0.6,0.5),
            "自主创业，识别商业机会，组建团队，运营企业", "不限", "不定")
    );

    private static Map<String, Object> createJob(String title, String holland, List<String> mbtiTypes,
            List<Double> valueWeights, List<Double> skillWeights, String desc, String edu, String salary) {
        Map<String, Object> job = new LinkedHashMap<>();
        job.put("title", title);
        job.put("hollandCode", holland);
        job.put("mbtiTypes", mbtiTypes);
        job.put("valueWeights", valueWeights);
        job.put("skillWeights", skillWeights);
        job.put("description", desc);
        job.put("education", edu);
        job.put("salary", salary);
        return job;
    }

    public List<Map<String, Object>> getJobLibrary() {
        return JOB_LIBRARY;
    }

    // ==================== 匹配算法 ====================

    /**
     * 根据四份问卷结果匹配最佳3个职业
     * @param hollandScores [R,I,A,S,E,C] 6维得分，每维0-1归一化
     * @param mbtiType 如 "INTJ"
     * @param valueScores [成就感,独立性,稳定性,人际关系,社会贡献,创新创造,物质报酬,工作环境] 8维
     * @param skillScores [语言,数理,空间,人际,组织,动手,创意,学习] 8维
     */
    public List<Map<String, Object>> matchCareers(
            Map<String, Double> hollandScores,
            String mbtiType,
            List<Double> valueScores,
            List<Double> skillScores) {

        String[] hollandOrder = {"R","I","A","S","E","C"};

        // 计算学生霍兰德前三类型
        List<Map.Entry<String, Double>> sorted = new ArrayList<>();
        for (String key : hollandOrder) {
            sorted.add(new AbstractMap.SimpleEntry<>(key, hollandScores.getOrDefault(key, 0.0)));
        }
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        String top3Holland = sorted.get(0).getKey() + sorted.get(1).getKey() + sorted.get(2).getKey();

        List<Map<String, Object>> results = new ArrayList<>();

        for (Map<String, Object> job : JOB_LIBRARY) {
            String jobHolland = (String) job.get("hollandCode");

            // 1. 霍兰德匹配 (40%) - 计算三字母命中率
            double hollandScore = 0;
            for (int i = 0; i < 3; i++) {
                char studentType = top3Holland.charAt(i);
                int posInJob = jobHolland.indexOf(studentType);
                if (posInJob >= 0) {
                    hollandScore += (1.0 - posInJob * 0.2) * (1.0 - i * 0.15);
                }
            }
            hollandScore = Math.min(1.0, hollandScore / 2.5);

            // 2. MBTI 匹配 (20%)
            @SuppressWarnings("unchecked")
            List<String> jobMbti = (List<String>) job.get("mbtiTypes");
            double mbtiScore = jobMbti.contains(mbtiType) ? 1.0 :
                jobMbti.stream().anyMatch(t -> t.substring(0,2).equals(mbtiType.substring(0,2))) ? 0.5 : 0.2;

            // 3. 价值观匹配 (20%) - 余弦相似度
            @SuppressWarnings("unchecked")
            List<Double> jobValues = (List<Double>) job.get("valueWeights");
            double valueScore = cosineSimilarity(valueScores, jobValues);

            // 4. 能力匹配 (20%) - 余弦相似度
            @SuppressWarnings("unchecked")
            List<Double> jobSkills = (List<Double>) job.get("skillWeights");
            double skillScore = cosineSimilarity(skillScores, jobSkills);

            // 加权综合
            double totalScore = hollandScore * 0.40 + mbtiScore * 0.20 + valueScore * 0.20 + skillScore * 0.20;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("title", job.get("title"));
            result.put("matchScore", Math.round(totalScore * 1000.0) / 10.0); // 百分比，如 85.3
            result.put("hollandCode", jobHolland);
            result.put("description", job.get("description"));
            result.put("education", job.get("education"));
            result.put("salary", job.get("salary"));
            result.put("hollandScore", Math.round(hollandScore * 100.0) / 100.0);
            result.put("mbtiScore", Math.round(mbtiScore * 100.0) / 100.0);
            result.put("valueScore", Math.round(valueScore * 100.0) / 100.0);
            result.put("skillScore", Math.round(skillScore * 100.0) / 100.0);
            results.add(result);
        }

        // 按总分降序，返回 TOP 3
        results.sort((a, b) -> Double.compare((Double) b.get("matchScore"), (Double) a.get("matchScore")));
        return results.subList(0, Math.min(3, results.size()));
    }

    private double cosineSimilarity(List<Double> a, List<Double> b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
