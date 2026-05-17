# 辅导员-学生私信与公告联动设计

## 概述
建立辅导员端和学生端的私信及公告联动功能。辅导员端"沟通互动"改名为"发布通知"，新增真正的"沟通互动"私信页面。学生端通讯中心接入后端公告和私信接口。

## 后端变更

### 1. Message 实体 (entity/Message.java)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| senderId | String | 发送者ID |
| senderRole | String | teacher/student |
| receiverId | String | 接收者ID |
| content | String (TEXT) | 消息内容 |
| quoteId | Long (nullable) | 引用消息ID |
| isRead | Boolean | 已读 |
| isRecalled | Boolean | 已撤回 |
| sentTime | LocalDateTime | 发送时间 |

### 2. MessageRepository
- `findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderBySentTimeAsc` — 会话消息
- `findDistinctSenderIdByReceiverId` / `findDistinctReceiverIdBySenderId` — 会话列表

### 3. StudentProfile 增加字段
- `teacherId` — 关联辅导员ID

### 4. CommunicationController 扩展
- `GET /api/communication/students?teacherId=X` — 辅导员获取班级学生列表
- `GET /api/communication/messages?userId=X&peerId=Y` — 获取会话消息
- `POST /api/communication/messages` — 发送消息
- `PUT /api/communication/messages/{id}/recall` — 撤回（校验2分钟时限）
- `GET /api/communication/contacts?userId=X&role=Y` — 获取会话联系人列表
- `PUT /api/communication/messages/{id}/read` — 标记已读

### 5. DataInitializer
- 预置学生-辅导员关联
- 预置样例公告数据
- 预置样例私信消息

## 前端变更

### 6. 路由 (router/index.js)
- `/teacher/career-plan` meta.title → "发布通知"
- 新增 `/teacher/communication` → "沟通互动" → `teacher/Communication.vue`

### 7. Teacher Layout.vue
- 侧边栏"沟通互动"标签 → "发布通知"（链接到 career-plan）
- 新增"沟通互动"入口（链接到 communication）

### 8. 新建 teacher/Communication.vue
- 左侧学生列表 + 搜索栏
- 右侧聊天界面（消息气泡、引用展示、撤回提示）
- 右键菜单：引用、撤回（2分钟内）
- 底部输入框 + 引用预览

### 9. 改造 student/CampusLife.vue
- 通知列表接 `GET /api/communication/announcements`
- 聊天消息接 `GET/POST /api/communication/messages`
- 撤回添加2分钟时限判断
- 联系人信息从后端获取

## 关键设计
- 撤回不删记录，设 isRecalled=true，双方显示"对方/你撤回了一条消息"
- 引用存 quoteId，前端根据 id 查找渲染引用气泡
- 消息按 sentTime ASC 排序
- 辅导员-学生为一对多关系（一个辅导员管一个班级）
