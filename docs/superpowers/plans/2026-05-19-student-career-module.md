# 学生端个人中心 + 职业规划 — 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development

**Goal:** 接通学生端6个页面，重点实现职业规划模块（四份测评问卷 + 职业匹配算法 + 预约咨询）

**Architecture:** YouthController 加 studentId 筛选参数；新建 CareerController 管理预约和匹配；问卷以 JS 常量存前端；匹配算法在 CareerService 中。

**Tech Stack:** Spring Boot 3 + JPA (Backend), Vue 3 + Element Plus + Axios (Frontend)

---

### Task 1: YouthController 增加 studentId 筛选参数

**Files:**
- Modify: `backend/src/main/java/com/studentaffairs/backend/controller/YouthController.java`
- Modify: `backend/src/main/java/com/studentaffairs/backend/repository/YouthAwardRepository.java`
- Modify: `backend/src/main/java/com/studentaffairs/backend/repository/HonorCandidateRepository.java`

- [ ] **Step 1: 给 YouthAwardRepository 增加 findByStudentId**

```java
List<YouthAward> findByStudentId(String studentId);
```

- [ ] **Step 2: 给 HonorCandidateRepository 增加 findByStudentId**

```java
List<HonorCandidate> findByStudentId(String studentId);
```

- [ ] **Step 3: 修改 YouthController 的 awards 和 second-classroom/records 端点支持 ?studentId= 可选参数**

在 `getAllAwards()` 加 `@RequestParam(required = false) String studentId`，有值时调用 `awardRepository.findByStudentId(studentId)`。
在 `getAllRecords()` 同样处理。
增加 `GET /honors/candidates/by-student?studentId=` 端点。

- [ ] **Step 4: 编译验证**

```bash
cd backend && ./mvnw compile -q
```

- [ ] **Step 5: Commit**

---

### Task 2: 创建 CareerAppointment 实体 + Repository

**Files:**
- Create: `backend/src/main/java/com/studentaffairs/backend/entity/CareerAppointment.java`
- Create: `backend/src/main/java/com/studentaffairs/backend/repository/CareerAppointmentRepository.java`

Entity 字段：id, studentId, studentName, teacherId, appointmentTime(String), reason(String), status(String: PENDING/ACCEPTED/REJECTED), createTime(LocalDateTime)

Repository 方法：findByStudentId, findByTeacherIdOrderByCreateTimeDesc, findAllByOrderByCreateTimeDesc

- [ ] **Step 1: 创建文件并编译**
- [ ] **Step 2: Commit**

---

### Task 3: 创建 CareerService（含职业画像库 + 匹配算法）

**Files:**
- Create: `backend/src/main/java/com/studentaffairs/backend/service/CareerService.java`

职业画像库包含约 30 个职业，每个预定义：
- hollandCode（三字母：R/I/A/S/E/C）
- mbtiTypes（1-2个推荐类型）
- valueWeights（8维价值观权重）
- skillWeights（8维能力权重）
- description（职业描述）
- education（学历要求）
- salary（薪资范围）

匹配算法：
1. 霍兰德匹配：计算学生前三类型与职业三字母码的命中率
2. MBTI 匹配：学生类型是否在职业推荐列表中
3. 价值观/能力：余弦相似度
4. 加权综合：40% + 20% + 20% + 20%

端点需要的辅助方法：
- `getJobLibrary()` — 返回全部职业
- `matchCareers(hollandScores, mbtiType, valueScores, skillScores)` — 返回 TOP 3
- `getJobDetail(int index)` — 返回单个职业详情

- [ ] **Step 1: 编写完整的 CareerService（带30个职业数据）**
- [ ] **Step 2: 编译**
- [ ] **Step 3: Commit**

---

### Task 4: 创建 CareerController

**Files:**
- Create: `backend/src/main/java/com/studentaffairs/backend/controller/CareerController.java`

端点：
- GET /api/career/jobs — 职业库列表
- POST /api/career/match — 提交测评结果，返回TOP3
- POST /api/career/appointments — 学生提交预约
- GET /api/career/appointments?studentId= — 学生查看预约
- GET /api/career/appointments/teacher/{teacherId} — 辅导员查看预约
- PUT /api/career/appointments/{id}/accept — 接受
- PUT /api/career/appointments/{id}/reject — 拒绝

- [ ] **Step 1: 编写 Controller**
- [ ] **Step 2: 编译并通过 curl 验证**
- [ ] **Step 3: Commit**

---

### Task 5: 重写 CareerPlan.vue（核心前端工作）

**Files:**
- Modify: `frontend/src/views/student/CareerPlan.vue`

5 个 Tab 页签结构：
1. **生涯工具** — 4 张问卷入口卡片，点击进入答题
2. **测评结果** — 雷达图(ECharts) + TOP 3 职业匹配卡片
3. **预约咨询** — 表单选择时间+填写原因+提交
4. **我的预约** — 预约列表（状态标签：待确认/已接受/已拒绝）
5. **职业库** — 30 个职业的折叠面板展示

每份问卷独立子组件（或内联），168 道题全部以 JS 常量定义。提交时计算各维度得分，调用 POST /api/career/match 获取结果。

问卷数据结构示例：
```javascript
const hollandQuestions = [
  { id: 1, type: 'R', text: '我喜欢操作机器或工具' },
  { id: 2, type: 'I', text: '我喜欢进行科学实验和研究' },
  // ... 60题
]
```

- [ ] **Step 1: 编写完整的 CareerPlan.vue（含所有问卷、Tab、API调用）**
- [ ] **Step 2: 前端构建验证**
- [ ] **Step 3: Commit**

---

### Task 6: 对接 AcademicSecondClass.vue 和 AwardApplication.vue 和 HonorsRecords.vue

**Files:**
- Modify: `frontend/src/views/student/AcademicSecondClass.vue`
- Modify: `frontend/src/views/student/AwardApplication.vue`
- Modify: `frontend/src/views/student/HonorsRecords.vue`

每个页面：替换 mock 数据为 axios API 调用，使用 `studentId=202301042` 作为当前登录学生（后续 JWT 替换）。

- [ ] **Step 1: 改造 3 个页面的 script 部分**
- [ ] **Step 2: 前端构建验证**
- [ ] **Step 3: Commit**

---

### Task 7: 改造 StudentDashboard.vue 聚合真实数据

**Files:**
- Modify: `frontend/src/views/student/StudentDashboard.vue`

从多个 API 并行获取数据：
- GET /api/academic/student-records?studentId= → GPA、课程数
- GET /api/youth/second-classroom/records?studentId= → 第二课堂学时
- GET /api/applications/student/{studentId} → 申请数量

- [ ] **Step 1: 改造 Dashboard script**
- [ ] **Step 2: 构建验证**
- [ ] **Step 3: Commit**

---

### Task 8: 新建辅导员端职业规划预约管理页

**Files:**
- Create: `frontend/src/views/teacher/CareerAppointment.vue`

左右分栏布局（参照 MidTermManagement.vue）：
- 左侧：预约列表（筛选：待确认/已接受/已拒绝）
- 右侧：选中预约详情 + 接受/拒绝按钮

- [ ] **Step 1: 编写页面**
- [ ] **Step 2: 添加路由和导航（teacher Layout + router）**
- [ ] **Step 3: 构建验证**
- [ ] **Step 4: Commit**

---

### Task 9: 集成测试

启动前后端，验证所有页面数据来自 API。

- [ ] **Step 1: 启动后端，curl 验证新接口**
- [ ] **Step 2: 启动前端，浏览器验证各页面**
- [ ] **Step 3: Commit 最终修复**
