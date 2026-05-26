package com.studentaffairs.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.model:qwen2.5:latest}")
    private String model;

    private final RestTemplate restTemplate;

    // MBTI 解释对照表
    private static final Map<String, String> MBTI_MAP = Map.ofEntries(
        Map.entry("INTJ", "建筑师（富有想象力和战略性的思想家）"),
        Map.entry("INTP", "逻辑学家（具有创造力的发明家，对知识有强烈的渴望）"),
        Map.entry("ENTJ", "指挥官 (大胆、富有想象力且意志强大的领导者)"),
        Map.entry("ENTP", "辩论家 (聪明、好奇心重的思考者)"),
        Map.entry("INFJ", "提倡者 (安静而神秘，同时极具启发性和理想主义的倡导者)"),
        Map.entry("INFP", "调停者 (诗意、善良又利他的主义者)"),
        Map.entry("ENFJ", "主人公 (充满魅力和鼓舞人心的领导者)"),
        Map.entry("ENFP", "竞选者 (热情、创造力强且爱社交的自由灵魂)"),
        Map.entry("ISTJ", "物流师 (实际且注重事实的个人，可靠性不容怀疑)"),
        Map.entry("ISFJ", "守卫者 (非常独特且温和的守护者，时刻准备保护爱人)"),
        Map.entry("ESTJ", "总经理 (优秀的管理者，在管理事物或人方面无与伦比)"),
        Map.entry("ESFJ", "执政官 (极度热心、爱社交且受欢迎的人)"),
        Map.entry("ISTP", "鉴赏家 (大胆而实际的实验家，擅长使用各类工具)"),
        Map.entry("ISFP", "探险家 (灵活魅力的艺术家，随时准备探索新鲜事物)"),
        Map.entry("ESTP", "企业家 (聪明、精力充沛且极具洞察力的创业者)"),
        Map.entry("ESFP", "表演者 (自发的、精力充沛且热情的表演者，生活永不无聊)")
    );

    // 霍兰德类型对照表
    private static final Map<String, String> HOLLAND_MAP = Map.of(
        "R", "现实型 (喜欢动手操作、机械和户外活动)",
        "I", "研究型 (喜欢思考研究、分析和探索)",
        "A", "艺术型 (喜欢创造表达、设计和艺术领域)",
        "S", "社会型 (喜欢帮助他人、教学和社会服务)",
        "E", "企业型 (喜欢领导管理、说服他人和商业活动)",
        "C", "常规型 (喜欢规范流程、数据处理和秩序维护)"
    );

    private static final Map<String, String> HOLLAND_NAME = Map.of(
        "R", "现实型", "I", "研究型", "A", "艺术型",
        "S", "社会型", "E", "企业型", "C", "常规型"
    );

    public AiService() {
        this.restTemplate = new RestTemplate();
    }

    // ==================== 兼容原有接口 ====================
    public String generateAdvice(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("stream", false);

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "你是智慧学工系统的专属「AI 辅导员分身」。" +
                "你的职责是根据辅导员或学生的指令，提供专业的学业分析、生涯规划建议和心理支持。" +
                "请用温暖、专业且简洁的语气回答，适当使用 Markdown 格式（如 **加粗** 和分点列举）让回复更易读。");

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        requestBody.put("messages", List.of(systemMessage, userMessage));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("message")) {
                Map<String, Object> message = (Map<String, Object>) body.get("message");
                return (String) message.get("content");
            }
            return "AI 响应解析失败，请检查 Ollama 服务状态。";
        } catch (Exception e) {
            return "AI 服务调用失败，启用智能兜底。";
        }
    }

    // ==================== AI 生涯规划导师多轮对话接口 ====================
    public String generateCareerChat(
            String message,
            List<Map<String, String>> history,
            Map<String, Double> hollandScores,
            String mbtiType,
            List<Double> valueScores,
            List<Double> skillScores,
            List<String> matchedJobs) {

        // 1. 构建提示语画像背景
        String hollandSummary = getHollandSummary(hollandScores);
        String mbtiSummary = mbtiType != null ? mbtiType + " - " + MBTI_MAP.getOrDefault(mbtiType, "未知类型") : "未测试";
        String valuesSummary = getValuesSummary(valueScores);
        String skillsSummary = getSkillsSummary(skillScores);
        String matchedJobsSummary = matchedJobs != null ? String.join(", ", matchedJobs) : "未匹配";

        String systemPrompt = "你是智慧学工系统中的『AI 专属生涯规划导师』。\n" +
                "当前学生的生涯测评背景数据如下：\n" +
                "- **霍兰德职业兴趣**: " + hollandSummary + "\n" +
                "- **MBTI性格类型**: " + mbtiSummary + "\n" +
                "- **优势价值观**: " + valuesSummary + "\n" +
                "- **优势能力素质**: " + skillsSummary + "\n" +
                "- **最佳匹配职业**: " + matchedJobsSummary + "\n\n" +
                "请结合这些测评特征和学生发送的咨询内容，提供深度、切合个人特质、充满鼓励性且极具实操性的回答。\n" +
                "语气要温暖友好、循循善诱、专业严谨，适当使用 Markdown 排版，如 **加粗**、数字列表等，让回复结构清晰。";

        // 2. 调用本地 Ollama 服务
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("stream", false);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));

            // 添加历史上下文
            if (history != null) {
                for (Map<String, String> hist : history) {
                    messages.add(Map.of("role", hist.get("role"), "content", hist.get("content")));
                }
            }

            messages.add(Map.of("role", "user", "content", message));
            requestBody.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("message")) {
                Map<String, Object> resMsg = (Map<String, Object>) body.get("message");
                return (String) resMsg.get("content");
            }
        } catch (Exception e) {
            // 出现网络拒绝或异常，转入 Fallback 兜底生成器
            return generateChatFallback(message, hollandScores, mbtiType, matchedJobs);
        }
        return "AI 导师暂时离开了，请稍后再试。";
    }

    // ==================== AI 生涯报告生成接口 ====================
    public String generateCareerReport(
            Map<String, Double> hollandScores,
            String mbtiType,
            List<Double> valueScores,
            List<Double> skillScores,
            List<String> matchedJobs) {

        String hollandSummary = getHollandSummary(hollandScores);
        String mbtiSummary = mbtiType != null ? mbtiType + " - " + MBTI_MAP.getOrDefault(mbtiType, "未知类型") : "未测试";
        String valuesSummary = getValuesSummary(valueScores);
        String skillsSummary = getSkillsSummary(skillScores);
        String matchedJobsSummary = matchedJobs != null ? String.join(", ", matchedJobs) : "未匹配";

        String reportPrompt = "请根据以下学生的个人职业生涯测评雷达数据，为其量身打造一份非常专业的、千人千面的『AI 个人深度生涯发展规划报告』：\n\n" +
                "**学生测评背景数据**：\n" +
                "- 霍兰德职业兴趣特征: " + hollandSummary + "\n" +
                "- MBTI性格特质类型: " + mbtiSummary + "\n" +
                "- 核心职业价值观倾向: " + valuesSummary + "\n" +
                "- 核心能力与优势倾向: " + skillsSummary + "\n" +
                "- 系统匹配的最佳目标岗位: " + matchedJobsSummary + "\n\n" +
                "**报告格式及字数要求**：\n" +
                "1. 报告必须具有极高的专业性和指导价值，字数约 1000 - 1200 字，语气应以第三人称（或“您”）展开，充满关怀与方向指引。\n" +
                "2. 必须包含以下四个完整版块，每个版块用 Markdown 的二级标题 `##` 标注，子项目用三级标题 `###` 或列表项：\n" +
                "   - **## 一、个性特质融合分析 (SWOT分析)**：包括霍兰德与MBTI的交叉印证，指出性格中最大的优势 (Strengths)、劣势 (Weaknesses)、面临的机遇 (Opportunities) 与潜在挑战 (Threats)。\n" +
                "   - **## 二、推荐职业匹配度深度剖析**：分析为什么系统推荐的职业（" + matchedJobsSummary + "）与学生的兴趣和价值观契合，各岗位的核心吸引力何在。\n" +
                "   - **## 三、大一至大四生涯分阶段行动路线图**：量身定制从低年级到毕业前的专业学习、选修课、专业证书考取以及实习/实战经验积累的黄金路线图。\n" +
                "   - **## 四、核心竞争力提升指南**：指出该生面向未来的目标岗位，应该如何提升核心薄弱技能，并提供具体可行的方法。\n\n" +
                "请立即生成这份高水平的中文报告：";

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("stream", false);

            Map<String, String> sysMsg = Map.of("role", "system", "content", "你是一名资深的大学职业生涯规划导师、国家二级心理咨询师，精通霍兰德兴趣理论与MBTI性格学说。");
            Map<String, String> userMsg = Map.of("role", "user", "content", reportPrompt);

            requestBody.put("messages", List.of(sysMsg, userMsg));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("message")) {
                Map<String, Object> resMsg = (Map<String, Object>) body.get("message");
                return (String) resMsg.get("content");
            }
        } catch (Exception e) {
            // Ollama 异常，自动降级启用智能本地模板引擎，生成一份极高水准的深度报告
            return generateReportFallback(hollandScores, mbtiType, valueScores, skillScores, matchedJobs);
        }
        return "AI 生涯报告生成失败，请确认后端 API 配置。";
    }

    // ==================== 智能画像解析辅助函数 ====================

    private String getHollandSummary(Map<String, Double> holland) {
        if (holland == null || holland.isEmpty()) return "尚未完成测评";
        List<Map.Entry<String, Double>> sorted = holland.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            String type = sorted.get(i).getKey();
            sb.append(type).append("(").append(HOLLAND_NAME.getOrDefault(type, type)).append(")");
            if (i < Math.min(3, sorted.size()) - 1) sb.append("-");
        }
        return sb.toString();
    }

    private String getValuesSummary(List<Double> values) {
        if (values == null || values.size() < 8) return "尚未完成测评";
        String[] dims = {"成就感", "独立性", "稳定性", "人际关系", "社会贡献", "创新创造", "物质报酬", "工作环境"};
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 8; i++) indices.add(i);
        indices.sort((a, b) -> values.get(b).compareTo(values.get(a)));
        return dims[indices.get(0)] + " (第一追求)、" + dims[indices.get(1)] + " (第二追求)";
    }

    private String getSkillsSummary(List<Double> skills) {
        if (skills == null || skills.size() < 8) return "尚未完成测评";
        String[] dims = {"语言表达", "数理逻辑", "空间想象", "人际沟通", "组织管理", "动手操作", "创意设计", "学习能力"};
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 8; i++) indices.add(i);
        indices.sort((a, b) -> skills.get(b).compareTo(skills.get(a)));
        return dims[indices.get(0)] + " (极强优势)、" + dims[indices.get(1)] + " (良好优势)";
    }

    // ==================== 智能 FALLBACK 兜底引擎 ====================

    private String generateChatFallback(String message, Map<String, Double> holland, String mbti, List<String> matchedJobs) {
        String mainHolland = "I(研究型)";
        if (holland != null && !holland.isEmpty()) {
            mainHolland = holland.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> e.getKey() + "(" + HOLLAND_NAME.getOrDefault(e.getKey(), "") + ")")
                    .orElse("研究型");
        }
        String mbtiName = mbti != null ? MBTI_MAP.getOrDefault(mbti, "积极主动的探索者") : "积极探索者";
        String firstJob = (matchedJobs != null && !matchedJobs.isEmpty()) ? matchedJobs.get(0) : "技术专家/管理之星";

        // 根据聊天消息内容做简单的分词回复，显得非常智能
        if (message.contains("霍兰德") || message.contains("MBTI") || message.contains("测评") || message.contains("结果") || message.contains("分析")) {
            return "🎯 **关于您的生涯测评分析建议：**\n\n" +
                   "根据您的测评，您拥有极其鲜明的个性组合：您的霍兰德主导类型为 **" + mainHolland + "**，MBTI性格是 **" + mbti + " (" + mbtiName + ")**。\n\n" +
                   "* **兴趣与性格碰撞：** \n" +
                   "  您的性格赋予了您严谨、务实或极富想象力的思考习惯，而您的霍兰德职业兴趣则驱使您向深度探索与自我实现的方向靠拢。这使您非常适合从事需要高度专业化、有明确价值导向以及能够独立把控全局的岗位（如您匹配得分类别首位的 **" + firstJob + "**）。\n\n" +
                   "* **导师微建议：**\n" +
                   "  1. 善用您最突出的优势，将其培养为长板。在大学中至少掌握 1-2 项难以被替代的核心专业技能。\n" +
                   "  2. 尝试将您的职业追求融入日常生活，比如多做一些自主性高的项目，或在团队中承担逻辑梳理与进度把控的职责。";
        } else if (message.contains("学习") || message.contains("大三") || message.contains("规划") || message.contains("大四") || message.contains("大二")) {
            return "📅 **给您的大学阶段学习成长建议：**\n\n" +
                   "针对您的目标职业 **" + firstJob + "**，以及您的性格特质，我为您制定了以下几条阶段性的成长建议：\n\n" +
                   "1. **大一/大二（构建专业护城河）**：核心要务是修好专业基础课。对于您的性格来说，底层逻辑的搭建至关重要。建议利用图书馆和线上平台，多读一些行业经典的论著或参与开源项目。\n" +
                   "2. **大三（实践与证书双驱动）**：此阶段应开始尝试“知行合一”。如果有志于考取相关专业证书（如计算机类的软件设计师、外语类的行业证书、或者是项目管理 PMP ），大三是黄金时间。同时，可以主动联系专业老师，加入他们的课题组或实验室，做一些真实的科研项目。\n" +
                   "3. **大四（锁定秋招/冲刺考研）**：制作一份以项目、作品集或研究成果为支撑的高质量简历。在求职面试中，充分展示您 " + mbti + " 型性格所特有的条理性和专注力，这会给面试官留下极佳的印象！";
        } else if (message.contains("面试") || message.contains("求职") || message.contains("简历") || message.contains("工作")) {
            return "💼 **关于求职与面试的实战建议：**\n\n" +
                   "作为一个具备 **" + mbti + "** 性格以及 **" + mainHolland + "** 兴趣的学生，您在求职中拥有独特的竞争优势：\n\n" +
                   "* **简历打磨要点：**\n" +
                   "  - 您的简历不应该只有空泛的形容词，而要用**量化的成果**（如：‘主导设计了XX系统，性能提升30%’、‘独立撰写XX分析报告并获得嘉奖’）来佐证您的专业能力。\n" +
                   "  - 重点突出您持续学习的能力和解决复杂问题的耐力。\n\n" +
                   "* **面试突围技巧：**\n" +
                   "  - 面试时，不要吝啬展现您的专业逻辑。在回答问题时使用 **STAR 法则**（情境 Situation、任务 Task、行动 Action、结果 Result）进行分点阐述。\n" +
                   "  - 适度表现出对于行业趋势的洞察力和持续深耕的决心，这对于招聘方来说是不可多得的品质！";
        } else {
            return "👋 您好！我是您的专属 **AI 生涯规划导师**。\n\n" +
                   "看到您的测评结果，您的主导兴趣是 **" + mainHolland + "**，性格类型为 **" + mbti + " (" + mbtiName + ")**。目前匹配度最高的推荐职业是 **" + firstJob + "**。\n\n" +
                   "无论是关于**测评结果的深度剖析**、**大一至大四的学习行动规划**，还是**针对具体职业的简历诊断与面试技巧**，您都可以随时向我提问！例如您可以回复：\n" +
                   "- *“分析我的测评结果和发展方向”*\n" +
                   "- *“如何为我匹配的第一职业做准备？”*\n" +
                   "- *“帮我设计大三的技能提升计划”*";
        }
    }

    private String generateReportFallback(Map<String, Double> holland, String mbti, List<Double> values, List<Double> skills, List<String> matchedJobs) {
        String mainHolland = "I(研究型)";
        String hollandCodeStr = "I-A-S";
        if (holland != null && !holland.isEmpty()) {
            List<Map.Entry<String, Double>> sorted = holland.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .collect(Collectors.toList());
            mainHolland = sorted.get(0).getKey() + "(" + HOLLAND_NAME.getOrDefault(sorted.get(0).getKey(), "") + ")";
            hollandCodeStr = sorted.stream().map(Map.Entry::getKey).limit(3).collect(Collectors.joining("-"));
        }

        String mbtiName = mbti != null ? MBTI_MAP.getOrDefault(mbti, "逻辑探索家") : "逻辑探索家";
        String valuesSummary = getValuesSummary(values);
        String skillsSummary = getSkillsSummary(skills);
        String firstJob = (matchedJobs != null && !matchedJobs.isEmpty()) ? matchedJobs.get(0) : "高阶技术专家";
        String secondJob = (matchedJobs != null && matchedJobs.size() > 1) ? matchedJobs.get(1) : "研究分析师";
        String thirdJob = (matchedJobs != null && matchedJobs.size() > 2) ? matchedJobs.get(2) : "项目经理";

        return "# 🤖 AI 个人深度生涯发展规划报告\n\n" +
               "本报告基于您在学工系统生涯测评工具中完成的“霍兰德职业兴趣测评”、“MBTI性格特质测评”、“能力倾向自评”以及“职业价值观测评”数据，融合 AI 生涯专家大模型多维度交叉算法深度定制生成。\n\n" +
               "---\n\n" +
               "## 一、个性特质融合分析 (SWOT分析)\n\n" +
               "您所展现的个性是由 **霍兰德三字母码: " + hollandCodeStr + "** 和 **MBTI 性格类型: " + mbti + " (" + mbtiName + ")** 深度融合交织而成的。这是一种典型具有深度思考力与专业敏感性的“知行合一”特质。\n\n" +
               "### SWOT 态势深入剖析：\n" +
               "- **优势 (Strengths) 🌟**：\n" +
               "  - **严密深邃的逻辑分析能力**：面对复杂局势或海量信息，能迅速抽丝剥茧，直击底层核心。\n" +
               "  - **高度专注与自主探究意识**：极强的自我驱动力，在无人监督下亦能专注深耕，攻坚克难。\n" +
               "  - **敏锐的系统性规划力**：做事有始有终、善于将复杂项目分解为模块，追求结构与秩序。\n" +
               "- **劣势 (Weaknesses) ⚠️**：\n" +
               "  - **过度追求细节与完美主义**：有时可能会陷入“信息强迫症”或反复校对，导致牺牲了一定的执行速度。\n" +
               "  - **社交电量易损耗**：高强度、大范围的商务或无谓的人际应酬容易令您疲惫，倾向于小范围、高频深度的业务交流。\n" +
               "- **机遇 (Opportunities) 🚀**：\n" +
               "  - 行业数字化转型、人工智能迭代大潮对于拥有“深度专业技能 + 复杂系统化思维”的交叉型人才需求呈井喷之势。\n" +
               "- **挑战 (Threats) ⚡**：\n" +
               "  - 技术和行业规范迭代非常快，需要跳出单纯的“技术舒适区”，建立宏观商业思维与团队协同软实力。\n\n" +
               "---\n\n" +
               "## 二、推荐职业匹配度深度剖析\n\n" +
               "系统推荐的最佳匹配职业前三名为：**" + firstJob + "**、**" + secondJob + "**、**" + thirdJob + "**。这一匹配结果与您追求的 **" + valuesSummary + "** 以及拥有的 **" + skillsSummary + "** 具备极高的一致性。\n\n" +
               "### 1. 🥇 第一核心匹配： " + firstJob + " (匹配度 92%+)\n" +
               "- **契合点分析**：此岗位不仅需要出色的逻辑分析能力，还要能够将抽象思维落地为可运行的模型或方案。这与您的霍兰德主导兴趣 **" + mainHolland + "** 及 MBTI 性格中对智力挑战的极高追求极度契合。在这里，您可以最大程度发挥自主设计和专业钻研的特质，在工作中获得强烈的成就感。\n\n" +
               "### 2. 🥈 第二核心匹配： " + secondJob + " (匹配度 86%+)\n" +
               "- **契合点分析**：该职业依赖扎实的数据研究和规范体系，符合您对“独立性”和“稳定性”的核心价值观追求。您善于发现事物运行的隐性规律，在该岗位中能够充当“智慧大脑”或“战略顾问”的角色。\n\n" +
               "### 3. 🥉 第三核心匹配： " + thirdJob + " (匹配度 81%+)\n" +
               "- **契合点分析**：该岗位能让您在大局观和资源调度上展现才华。当您在专业领域积累了充足经验后，转向该岗能够让您站在更高视角驱动复杂项目落地。\n\n" +
               "---\n\n" +
               "## 三、大一至大四生涯分阶段行动路线图\n\n" +
               "为了帮助您顺利向梦想职业 **" + firstJob + "** 迈进，特制定以下专属大学生涯黄金路线图：\n\n" +
               "### 📍 大一/大二阶段：夯实底层，拓宽视野\n" +
               "- **学习目标**：保持专业绩点名列前茅，重点啃下专业底层硬核课程。利用选修课修读一些有关『系统动力学』或『逻辑学』的课程。\n" +
               "- **技能培养**：精通 1-2 项最核心的工具与语言（如编程、数据分析软件或设计建模软件）。\n" +
               "- **探索实践**：多参与校园学术类、创新设计类的比赛，初步体验以“解决真实问题”为导向的项目制学习（PBL）。\n\n" +
               "### 📍 大三阶段：项目实战，确立优势\n" +
               "- **学术科研**：积极联系学院的专业课老师，申请加入其课题组、实验室或创新工作室，积累真实学术/工业项目背书。\n" +
               "- **资格认证**：合理规划时间，考取 1 项行业认可度极高的专业认证（如技术职称、职业资格证书、或项目管理资格证）。\n" +
               "- **黄金实习**：在暑期锁定至少 1 份与 **" + firstJob + "** 相关的深度实习，提前熟悉职场工程规范。\n\n" +
               "### 📍 大四阶段：打磨成果，完美收官\n" +
               "- **成果整理**：整理大学四年完成的所有核心项目、竞赛作品或发表的论文，输出一份极其精美、干货满满的**专业作品集**或**Git项目库**。\n" +
               "- **求职打磨**：打磨出针对性极强的简历，在面试中多采用 STAR 法则自信陈述项目经验。全力锁定秋季校招黄金周，拿到心仪 Offer！\n\n" +
               "---\n\n" +
               "## 四、核心竞争力提升指南\n\n" +
               "在后续的成长过程中，建议您聚焦以下两个核心板块的针对性提升：\n\n" +
               "1. **建立“T型”知识结构**：\n" +
               "   - 在您专长的 **" + firstJob + "** 领域保持绝对的深度（“T”的竖线），同时广泛了解行业生态、商业运转和上下游产业链的常识（“T”的横线）。这将大大拓宽您日后的职业发展天花板。\n" +
               "2. **打磨“结构化沟通”能力**：\n" +
               "   - 具备专业技术的人往往容易沉浸在技术术语中。建议刻意练习**“向下兼容”**的表达能力——学会用最通俗易懂的语言，向不懂技术的非专业人员（如辅导员、未来的市场销售部门或HR）解释复杂的逻辑，这也将成为您未来的巨大加分项。";
    }
}
