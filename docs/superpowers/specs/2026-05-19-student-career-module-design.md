# 学生端个人中心 + 职业规划模块 — 设计文档

**日期:** 2026-05-19

## 1. 概述

接通学生端6个"我的"页面，重点实现职业规划模块（含预约咨询、四份测评问卷、职业匹配算法）。同时为 YouthController 增加 studentId 筛选参数使第二课堂/获奖/荣誉页面可查询学生个人数据。

## 2. 学生端页面对接方案

| 页面 | 后端接口 | 改动 |
|------|---------|------|
| AcademicSecondClass | GET /api/youth/second-classroom/records?studentId= | YouthController 加参数 |
| AwardApplication | GET /api/youth/awards?studentId= + POST /api/youth/awards | YouthController 加筛选+提交 |
| HonorsRecords | GET /api/youth/honors/candidates?studentId= | HonorCandidateRepository 加查询 |
| MidTermAppraisal | 已在任务1完成 | — |
| StudentDashboard | 聚合多个接口 | 前端聚合统计数据 |
| CareerPlan | 见下文 | 全新实现 |

## 3. 职业规划模块

### 3.1 预约咨询

**CareerAppointment 实体：**
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 自增 |
| studentId | String | 学号 |
| studentName | String | 姓名 |
| teacherId | String | 辅导员ID |
| appointmentTime | String | 预约时间段 |
| reason | String | 咨询原因 |
| status | String | PENDING/ACCEPTED/REJECTED |
| createTime | LocalDateTime | 创建时间 |

**端点：**
- POST /api/career/appointments — 学生提交预约
- GET /api/career/appointments?studentId= — 学生查看自己的预约
- GET /api/career/appointments/teacher/{teacherId} — 辅导员查看预约
- PUT /api/career/appointments/{id}/accept — 辅导员接受
- PUT /api/career/appointments/{id}/reject — 辅导员拒绝

**辅导员端：** 在 Teacher Layout 增加"职业规划预约"导航，新页面类似中期鉴定收集页（左侧预约列表 + 右侧详情）。

### 3.2 四份测评问卷（前端静态数据）

问卷题目以 JS 常量形式存储在前端，不走数据库。每份问卷独立 Tab 展示。

**问卷1：霍兰德职业兴趣测评（60题，5分制）**
- R现实型(10题)：操作机器、修理物品、户外工作等
- I研究型(10题)：科学探究、数学分析、阅读专业书籍等
- A艺术型(10题)：绘画写作、音乐创作、设计创意等
- S社会型(10题)：帮助他人、教学活动、志愿服务等
- E企业型(10题)：领导团队、说服他人、商业策划等
- C常规型(10题)：整理文件、遵循流程、数据处理等

**问卷2：MBTI职业性格测评（48题，5分制）**
- E/I 外向/内向(12题)
- S/N 感觉/直觉(12题)
- T/F 思考/情感(12题)
- J/P 判断/感知(12题)

**问卷3：职业价值观测评（30题，5分制）**
8个维度：成就感、独立性、稳定性、人际关系、社会贡献、创新创造、物质报酬、工作环境

**问卷4：能力倾向自评（30题，5分制）**
8个维度：语言表达、数理逻辑、空间想象、人际沟通、组织管理、动手操作、创意设计、学习能力

### 3.3 职业匹配算法

**职业画像库（约30个职业）：** 每个职业预定义特征向量。

匹配计算（加权）：
- 霍兰德一致性（40%）— O*NET 标准三字母码匹配
- MBTI 匹配（20%）— 类型相似度
- 价值观匹配（20%）— 向量余弦相似度
- 能力匹配（20%）— 向量余弦相似度

结果：返回匹配分最高的前3个职业，显示职业名称、匹配度百分比、推荐理由。

### 3.4 CareerPlan.vue 页面结构

5 个 Tab：
1. **生涯工具** — 四份问卷入口卡片
2. **测评结果** — 雷达图 + TOP 3 职业匹配
3. **预约咨询** — 选择时间 + 填写原因 + 提交
4. **我的预约** — 预约列表及状态
5. **职业库** — 30个职业的详细介绍

## 4. 文件清单

### 后端新增（4个）
- entity/CareerAppointment.java
- repository/CareerAppointmentRepository.java
- service/CareerService.java（含匹配算法 + 职业画像数据）
- controller/CareerController.java

### 后端修改（3个）
- YouthController.java — 加 studentId 筛选参数
- HonorCandidateRepository.java — 加 findByStudentId
- YouthAwardRepository.java — 加 findByStudentId

### 前端修改（4个）
- CareerPlan.vue — 全新问卷+预约+匹配（约800行）
- AcademicSecondClass.vue — 对接 API
- AwardApplication.vue — 对接 API
- HonorsRecords.vue — 对接 API

### 前端新增（1个）
- teacher/CareerAppointment.vue — 辅导员预约管理（左右分栏）

### 路由 + 导航（2个）
- router/index.js — + teacher career 路由
- teacher/Layout.vue — + 职业规划预约导航
