# 教务端模块接通 — 设计文档

**日期:** 2026-05-18
**状态:** 已确认

## 1. 概述

将教务端（/academic）5个全 mock 页面全部对接真实后端 API，新增 Course 和 MidTermAppraisal 两个实体及其配套接口，同时为辅导员端新增中期鉴定收集页面。

## 2. 架构决策

采用**分离式 Controller**：保持 AcademicController 不变（预警+成绩CRUD+统计），新增 CourseController 和 MidTermController，各自独立管理职责。

## 3. 实体设计

### 3.1 Course（课程）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 自增主键 |
| code | String(50) | 课程代码，如 CS201 |
| name | String(100) | 课程名称 |
| type | String(20) | 必修/选修 |
| credit | Float | 学分 |
| hours | Integer | 学时 |
| semester | String(20) | 开课学期，如 2025-2026-1 |
| teacher | String(50) | 任课教师 |
| className | String(100) | 授课班级 |

### 3.2 MidTermAppraisal（中期鉴定）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 自增主键 |
| studentId | String(50) | 学号 |
| studentName | String(50) | 姓名 |
| className | String(50) | 班级 |
| thoughtPerformance | String(20) | 思想表现（优秀/良好/合格/待改进） |
| academicPerformance | String(20) | 学业成绩 |
| overallPerformance | String(20) | 综合表现 |
| selfAssessment | Text | 自我鉴定内容 |
| submitTime | LocalDateTime | 提交时间 |

无审核状态字段，无审核意见字段。学生提交后仅供辅导员查看，辅导员自行联系学生沟通。

## 4. API 设计

### 4.1 AcademicController（修改现有）

| 方法 | 端点 | 说明 | 状态 |
|------|------|------|------|
| GET | /api/academic/warnings | 预警列表 | 已有 |
| POST | /api/academic/warnings/run-engine | 运行预警引擎 | 已有 |
| GET | /api/academic/class-distribution?className= | 班级成绩分布 | 已有 |
| GET | /api/academic/student-records?studentId= | 学生成绩明细 | 已有 |
| GET | /api/academic/dashboard | Dashboard 统计 | **新增** |
| POST | /api/academic/records | 新增成绩 | **新增** |
| PUT | /api/academic/records/{id} | 修改成绩 | **新增** |
| DELETE | /api/academic/records/{id} | 删除成绩 | **新增** |

Dashboard 返回格式：
```json
{
  "totalStudents": 3560,
  "totalCourses": 48,
  "warningCount": 8,
  "avgGpa": 3.05,
  "recentWarnings": [...]
}
```

### 4.2 CourseController（新增）

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | /api/courses | 列表（支持 type/semester 可选筛选） |
| POST | /api/courses | 新增 |
| PUT | /api/courses/{id} | 编辑 |
| DELETE | /api/courses/{id} | 删除 |

### 4.3 MidTermController（新增）

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | /api/mid-term | 学生提交鉴定 |
| GET | /api/mid-term?studentId= | 查询学生自己的鉴定 |
| GET | /api/mid-term/all | 教务/辅导员查看全部 |
| GET | /api/mid-term/{id} | 单条详情 |

## 5. 前端改动

### 5.1 教务端 5 页面（全部由 mock 数据改为 API 调用）

| 页面 | API 对接 |
|------|---------|
| Dashboard.vue | GET /api/academic/dashboard + GET /api/academic/warnings |
| GradeManagement.vue | GET/POST/PUT/DELETE /api/academic/records |
| AcademicWarning.vue | GET /api/academic/warnings + POST /api/academic/warnings/run-engine |
| CourseManagement.vue | GET/POST/PUT/DELETE /api/courses |
| MidTermManagement.vue | GET /api/mid-term/all + GET /api/mid-term/{id} |

前端使用 axios 直接调用 `http://localhost:8080`，与项目中 Communication.vue 等已对接页面保持一致。

### 5.2 学生端中期鉴定提交

MidTermAppraisal.vue 对接 POST /api/mid-term（提交）和 GET /api/mid-term?studentId=（查看自己已提交的）。

### 5.3 辅导员端中期鉴定收集（新增）

参照 Communication.vue 布局：
- 左侧（w-80）：学生列表，标注已提交/未提交状态，支持搜索
- 右侧（flex-1）：固定窗口中显示选中学生的鉴定详情（自评等级+自我鉴定），内容区可滚动

## 6. 文件清单

### 新增后端文件（7个）
- `entity/Course.java`
- `repository/CourseRepository.java`
- `service/CourseService.java`
- `controller/CourseController.java`
- `entity/MidTermAppraisal.java`
- `repository/MidTermAppraisalRepository.java`
- `controller/MidTermController.java`

### 新增前端文件（1个）
- `views/teacher/MidTermManagement.vue`（如不存在则新建）

### 修改后端文件（3个）
- `controller/AcademicController.java` — 增加dashboard + 成绩CRUD
- `service/AcademicService.java` — 增加统计与CRUD方法
- `repository/AcademicRecordRepository.java` — 增加统计查询

### 修改前端文件（6个）
- `views/academic/Dashboard.vue`
- `views/academic/GradeManagement.vue`
- `views/academic/AcademicWarning.vue`
- `views/academic/CourseManagement.vue`
- `views/academic/MidTermManagement.vue`
- `views/student/MidTermAppraisal.vue`

### 修改配置文件（1个）
- `config/DataInitializer.java` — 增加 Course 种子数据

## 7. 不在范围内

- 批量导入成绩（保留前端 UI，后端不做实现）
- Spring Security + JWT 认证（属于任务3）
- 学生端个人中心其他页面接通（属于任务2）
