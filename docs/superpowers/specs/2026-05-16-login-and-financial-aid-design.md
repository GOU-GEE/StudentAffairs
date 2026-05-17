# 登录改造 + 资助管理中心 设计文档

## 1. 登录系统改造

### 1.1 后端 AuthController

新增 `POST /api/auth/login`，接收 `{username, password}`，返回 `{code, msg, data: {role, name, token}}`。

预设账号（内存 Map）：

| 账号 | 密码 | 角色 | 姓名 |
|------|------|------|------|
| admin | 123456 | admin | 系统管理员 |
| t001 | 123456 | teacher | 李辅导员 |
| s001 | 123456 | student | 张小明 |

token 为 `"mock-token-" + username`，后续可换 JWT。

### 1.2 前端 Login.vue

- 删除三个角色选择按钮
- 改为账号+密码表单（Element Plus el-input + el-button）
- 提交时 POST /api/auth/login
- 成功后 localStorage 存入 userRole、userName、token
- 根据 role 跳转：admin→/admin, teacher→/teacher, student→/student
- 失败时显示错误提示

### 1.3 路由守卫

保持不变。已有 student/teacher/admin 三重守卫。

## 2. 资助管理中心（Financial Aid Center）

### 2.1 角色定位

负责：奖助学金审批、勤工助学岗位管理、困难认定管理。

配色主题：暖橙/金色（#f59e0b 系），区别于管理红色、教师蓝色。

### 2.2 路由

```js
{
  path: '/financial',
  component: () => import('../views/financial/Layout.vue'),
  children: [
    { path: '', name: 'FinancialDashboard', meta: { title: '资助管理首页' }, component: ... },
    { path: 'scholarships', name: 'FinancialScholarships', meta: { title: '奖助学金审批' }, component: ... },
    { path: 'work-study', name: 'FinancialWorkStudy', meta: { title: '勤工助学管理' }, component: ... },
    { path: 'poverty', name: 'FinancialPoverty', meta: { title: '困难认定管理' }, component: ... },
  ]
}
```

路由守卫新增：`/financial` 需 role === 'financial'。

### 2.3 侧边栏菜单

| 路由 | 图标 | 标签 |
|------|------|------|
| `/financial` | HomeFilled | 资助管理首页 |
| `/financial/scholarships` | Trophy | 奖助学金审批 |
| `/financial/work-study` | Briefcase | 勤工助学管理 |
| `/financial/poverty` | Stamp | 困难认定管理 |

### 2.4 新增文件清单

| 文件 | 说明 |
|------|------|
| `backend/.../controller/AuthController.java` | 登录 API |
| `frontend/src/views/auth/Login.vue` | 改为账号密码表单 |
| `frontend/src/views/financial/Layout.vue` | 资助管理布局 |
| `frontend/src/views/financial/Dashboard.vue` | 概览统计 |
| `frontend/src/views/financial/ScholarshipReview.vue` | 奖助学金审批 |
| `frontend/src/views/financial/WorkStudyManagement.vue` | 勤工助学管理 |
| `frontend/src/views/financial/PovertyManagement.vue` | 困难认定管理 |

### 2.5 修改文件清单

| 文件 | 改动 |
|------|------|
| `frontend/src/router/index.js` | 新增 /financial 路由组 + 守卫 |
| `frontend/src/views/auth/Login.vue` | 完全重写为表单登录 |

### 2.6 后端 API（复用+扩展）

- 复用 `ApplicationController`：`GET /api/applications/pending`（待审批奖学金申请）、`PUT /api/applications/{id}/review`（审批）
- 复用 `FinancialAidController`：`GET /api/financial-aid/jobs`（岗位列表）、`POST /api/financial-aid/apply`（申请岗位）
- 新增 `AuthController`：`POST /api/auth/login`

## 3. 验证清单

1. 登录页显示账号密码表单（无角色按钮）
2. admin/123456 → /admin，t001/123456 → /teacher，s001/123456 → /student
3. 错误密码提示"账号或密码错误"
4. financial 角色可访问 /financial/*，其他角色被拦截
5. 资助管理首页显示统计卡片
6. 奖助学金审批页可查看待审申请并进行通过/驳回
7. 勤工助学管理页可发布岗位、查看申请
8. 困难认定管理页可查看困难学生列表
