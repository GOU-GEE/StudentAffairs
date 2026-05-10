<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <!-- 顶部：联系班主任 -->
      <div class="px-2 pt-2 pb-2">
        <div class="p-3 rounded-xl flex items-center justify-between cursor-pointer transition-all"
             :class="activeId === 'chat' ? 'bg-primary/10 shadow-sm ring-1 ring-primary/20' : 'hover:bg-white hover:shadow-sm'"
             @click="selectChat">
          <div class="flex items-center gap-3">
            <div class="relative">
              <img src="https://i.pravatar.cc/150?u=teacher_li" class="w-10 h-10 rounded-full border border-outline-variant/30 object-cover">
            </div>
            <div>
              <div class="font-bold text-sm text-on-surface">李老师 (辅导员)</div>
            </div>
          </div>
          <div v-if="unreadChatCount > 0" class="min-w-[20px] h-5 px-1.5 bg-error text-white text-[11px] font-bold rounded-full flex items-center justify-center shadow-sm">
            {{ unreadChatCount }}
          </div>
        </div>
      </div>
      
      <div class="h-px bg-outline-variant/15"></div>

      <!-- 意见反馈入口 -->
      <div class="px-2 pt-2 pb-2 border-b border-outline-variant/15">
        <div class="px-2 py-3 rounded-xl flex items-center justify-between cursor-pointer transition-all min-h-[44px]"
             :class="activeId === 'feedback' ? 'bg-primary/10 shadow-sm ring-1 ring-primary/20' : 'hover:bg-white hover:shadow-sm'"
             @click="openFeedback">
          <div class="font-bold text-sm text-on-surface flex items-center h-5">
            意见反馈
          </div>
          <div v-if="feedbackStatus" class="h-5 text-[11px] px-2 rounded flex items-center justify-center font-bold leading-none"
               :class="feedbackStatus === '已回复' ? 'bg-green-100 text-green-700' : 'bg-orange-100 text-orange-700'">
            {{ feedbackStatus }}
          </div>
        </div>
      </div>

      <!-- 下部：通知列表 -->
      <div class="flex-1 flex flex-col min-h-0">
        <!-- Tabs & 统计 -->
        <div class="flex items-center justify-between border-b border-outline-variant/15 flex-shrink-0">
          <div class="flex items-center gap-5">
            <span class="text-sm font-bold text-on-surface px-4 py-3 border-r border-outline-variant/30">
              通知
            </span>
            <button v-for="tab in ['全部', '未读', '已读']" :key="tab"
                    @click="currentTab = tab"
                    class="py-3 text-sm font-bold border-b-2 transition-colors relative -mb-[1px]"
                    :class="currentTab === tab ? 'text-primary border-primary' : 'text-secondary border-transparent hover:text-on-surface'">
              {{ tab }}
            </button>
          </div>
          <span class="text-[11px] text-secondary pr-4 font-medium">共 {{ filteredNotifications.length }} 条{{ currentTab === '全部' ? '通知' : currentTab }}</span>
        </div>

        <!-- 列表 -->
        <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
          <div v-for="notif in filteredNotifications" :key="notif.id"
               @click="selectNotification(notif.id)"
               class="p-3 rounded-xl cursor-pointer transition-all border"
               :class="activeId === notif.id ? 'bg-white shadow-sm border-primary/30 ring-1 ring-primary/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
            <div class="flex items-start gap-3">
              <!-- 竖向文字标签 (等间距长方形) -->
              <div class="p-2.5 rounded-xl flex flex-col items-center justify-center flex-shrink-0 text-[11px] font-bold leading-tight"
                   :class="notif.type === 'GRADE' ? 'bg-blue-100 text-blue-700' : 'bg-purple-100 text-purple-700'">
                <span>{{ notif.type === 'GRADE' ? '年' : '班' }}</span>
                <span>{{ notif.type === 'GRADE' ? '级' : '级' }}</span>
              </div>
              
              <!-- 内容摘要 -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between gap-2 mb-1">
                  <h4 class="font-bold text-sm text-on-surface truncate flex-1" :class="{'font-black': !notif.isRead}">{{ notif.title }}</h4>
                  <span v-if="!notif.isRead" class="w-2 h-2 rounded-full bg-error flex-shrink-0"></span>
                </div>
                <div class="flex items-center justify-between text-xs">
                  <span class="text-secondary truncate pr-2">{{ notif.publisher }}</span>
                  <span class="text-outline flex-shrink-0">{{ notif.timeShort }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="filteredNotifications.length === 0" class="py-10 text-center text-secondary text-sm">
            暂无相关通知
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧面板 (动态渲染内容) -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col relative">
      <transition name="fade" mode="out-in">
        <!-- 聊天界面 -->
        <div v-if="activeId === 'chat'" key="chat" class="flex flex-col h-full w-full">
          <div class="px-6 py-4 border-b border-outline-variant/15 flex items-center gap-3 bg-white/40">
            <div class="relative">
              <img src="https://i.pravatar.cc/150?u=teacher_li" class="w-10 h-10 rounded-full border border-outline-variant/30 object-cover">
            </div>
            <div>
              <h3 class="font-bold text-base text-on-surface">李老师</h3>
              <p class="text-xs text-secondary mt-0.5">辅导员 · 软件工程专业</p>
            </div>
          </div>
          
          <div class="flex-1 overflow-y-auto p-6 space-y-6 custom-scrollbar bg-surface-container-lowest/30" ref="chatScrollContainer">
            <!-- Time separator (simplified for mock) -->
            <div class="flex justify-center"><span class="px-3 py-1 bg-surface-container-low rounded-full text-xs text-secondary font-medium">05-18 09:15</span></div>
            
            <template v-for="msg in chatMessages" :key="msg.id">
              <div v-if="msg.sender === 'system'" class="flex justify-center my-2">
                <span class="px-3 py-1 bg-surface-container-low rounded-full text-[11px] text-secondary">{{ msg.text }}</span>
              </div>
              <div v-else class="flex items-start gap-3" :class="msg.sender === 'student' ? 'flex-row-reverse' : ''" @contextmenu.prevent="openContextMenu($event, msg)">
                <img :src="msg.sender === 'student' ? 'https://i.pravatar.cc/150?u=student1' : 'https://i.pravatar.cc/150?u=teacher_li'" class="w-8 h-8 rounded-full border border-outline-variant/20 flex-shrink-0 object-cover">
                <div class="px-4 py-2.5 rounded-2xl shadow-sm max-w-[70%]"
                     :class="msg.sender === 'student' ? 'bg-primary text-on-primary-fixed rounded-tr-sm' : 'bg-white border border-outline-variant/15 rounded-tl-sm'">
                  <!-- 引用内容展示 -->
                  <div v-if="msg.quote" class="mb-2 p-2 bg-black/10 rounded-lg border-l-4 border-primary/60 text-xs">
                    <div class="font-bold opacity-80 mb-1">{{ msg.quote.sender === 'student' ? '我' : '李老师' }}</div>
                    <div class="opacity-90 line-clamp-2">{{ msg.quote.text }}</div>
                  </div>
                  <p class="text-sm leading-relaxed" :class="msg.sender === 'student' ? '' : 'text-on-surface'">{{ msg.text }}</p>
                </div>
              </div>
            </template>
          </div>
          
          <!-- 底部输入框 -->
          <div class="p-4 border-t border-outline-variant/15 bg-white/40 flex flex-col gap-2">
            <!-- 引用预览 -->
            <div v-if="quotingMessage" class="flex items-center justify-between bg-surface-container-low pl-3 pr-2 py-1.5 rounded-lg mx-2 relative">
              <div class="text-xs text-secondary truncate flex-1 pr-4">
                <span class="font-bold">{{ quotingMessage.sender === 'student' ? '我' : '李老师' }}:</span> {{ quotingMessage.text }}
              </div>
              <el-icon class="cursor-pointer text-secondary hover:text-error transition-colors flex-shrink-0" @click="cancelQuote"><Close /></el-icon>
            </div>
            <div class="flex items-end gap-3 bg-white p-1.5 rounded-2xl border border-outline-variant/30 focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/20 transition-all">
              <textarea ref="chatInputRef" v-model="chatInput" rows="1" @keydown.enter.exact.prevent="sendChat" class="flex-1 resize-none outline-none text-sm px-2 py-1.5 bg-transparent custom-scrollbar max-h-32 min-h-[32px]" placeholder="发送消息给李老师... (Enter发送，Shift+Enter换行)"></textarea>
              <div class="flex items-center gap-2 pb-0.5 pr-1">
                <button class="w-8 h-8 rounded-full hover:bg-surface-container flex items-center justify-center text-secondary transition-colors"><el-icon><Picture /></el-icon></button>
                <button class="w-8 h-8 rounded-full hover:bg-surface-container flex items-center justify-center text-secondary transition-colors"><el-icon><Paperclip /></el-icon></button>
                <button @click="sendChat" class="px-4 py-1.5 bg-primary text-on-primary-fixed font-bold text-sm rounded-xl hover:bg-primary-fixed transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed" :disabled="!chatInput.trim()">发送</button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 通知详情界面 -->
        <div v-else-if="activeNotification" key="detail" class="flex flex-col h-full w-full overflow-y-auto custom-scrollbar">
          <!-- 标题区 -->
          <div class="px-8 py-6 border-b border-outline-variant/15 bg-white/40">
            <div class="flex items-start justify-between gap-4 mb-4">
              <h2 class="text-xl font-bold text-on-surface leading-snug">{{ activeNotification.title }}</h2>
              <span class="px-2.5 py-1 text-xs font-bold rounded-full whitespace-nowrap"
                    :class="activeNotification.type === 'GRADE' ? 'bg-blue-100 text-blue-700 border border-blue-200' : 'bg-purple-100 text-purple-700 border border-purple-200'">
                {{ activeNotification.type === 'GRADE' ? '年级通知' : '班级通知' }}
              </span>
            </div>
            <div class="flex items-center gap-4 text-sm text-secondary">
              <span class="font-medium text-on-surface flex items-center gap-1.5">
                <el-icon><User /></el-icon> {{ activeNotification.publisher }}
              </span>
              <span class="flex items-center gap-1.5">
                <el-icon><Clock /></el-icon> {{ activeNotification.timeFull }}
              </span>
              <span class="text-outline">发送给 {{ activeNotification.type === 'GRADE' ? '全体学生' : '软件工程12班' }}</span>
            </div>
          </div>
          
          <!-- 正文区 -->
          <div class="p-8 pb-4 flex-1">
            <div class="prose prose-sm max-w-none text-on-surface whitespace-pre-wrap leading-relaxed" v-html="activeNotification.content"></div>
            
            <!-- 附件 -->
            <div v-if="activeNotification.attachments && activeNotification.attachments.length > 0" class="mt-10">
              <h4 class="font-bold text-sm text-on-surface mb-3 flex items-center gap-2">
                <el-icon><Paperclip /></el-icon> 附件 ({{ activeNotification.attachments.length }})
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div v-for="(file, i) in activeNotification.attachments" :key="i" class="flex items-center justify-between p-3 border border-outline-variant/30 rounded-xl hover:bg-white hover:shadow-sm cursor-pointer transition-all group">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-lg bg-surface-container-low flex items-center justify-center flex-shrink-0"
                         :class="file.name.endsWith('.pdf') ? 'text-red-500' : 'text-blue-500'">
                      <el-icon size="20"><Document /></el-icon>
                    </div>
                    <div class="min-w-0">
                      <p class="text-sm font-bold text-on-surface truncate">{{ file.name }}</p>
                      <p class="text-xs text-secondary mt-0.5">{{ file.size }}</p>
                    </div>
                  </div>
                  <el-icon class="text-outline group-hover:text-primary"><Download /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 意见反馈板 -->
        <div v-else-if="activeId === 'feedback'" key="feedback" class="flex flex-col h-full w-full">
          <!-- Header -->
          <div class="px-6 py-4 border-b border-outline-variant/15 flex items-center justify-between bg-white/40">
             <div class="flex items-center gap-3">
               <div class="w-10 h-10 rounded-full bg-orange-100 text-orange-600 flex items-center justify-center">
                 <el-icon :size="20"><EditPen /></el-icon>
               </div>
               <div>
                 <h3 class="font-bold text-on-surface">意见反馈板</h3>
                 <p class="text-xs text-secondary">查看历史反馈或提出您的宝贵意见</p>
               </div>
             </div>
             <div class="text-xs font-medium text-secondary">
               共 {{ feedbacks.length }} 条 · 已回复 <span class="text-green-600 font-bold">{{ repliedFeedbacksCount }}</span> 条
             </div>
          </div>
          
          <!-- 历史列表 -->
          <div class="flex-1 overflow-y-auto p-6 space-y-6 custom-scrollbar bg-surface-container-lowest/30">
            <div v-for="fb in feedbacks" :key="fb.id" class="bg-white rounded-2xl p-5 border border-outline-variant/15 shadow-sm">
              <div class="flex items-center justify-between mb-2">
                 <span class="font-bold text-sm text-on-surface">{{ fb.student }}</span>
                 <span class="text-xs text-outline">{{ fb.time }}</span>
              </div>
              <p class="text-sm text-on-surface mb-4">{{ fb.content }}</p>
              
              <!-- 官方回复 -->
              <div v-if="fb.reply" class="bg-surface-container-low rounded-xl p-4 relative">
                 <!-- 装饰用的小三角 -->
                 <div class="absolute -top-2 left-6 w-4 h-4 bg-surface-container-low rotate-45 rounded-sm"></div>
                 <div class="relative z-10 flex flex-col gap-1">
                   <div class="flex items-center justify-between">
                     <span class="text-xs font-bold text-primary">学校回复</span>
                     <span class="text-xs text-outline">{{ fb.replyTime }}</span>
                   </div>
                   <p class="text-sm text-on-surface">{{ fb.reply }}</p>
                 </div>
              </div>
              <div v-else class="text-xs text-secondary italic">等待学校回复...</div>
            </div>
            <div v-if="feedbacks.length === 0" class="py-10 text-center text-secondary text-sm">暂无历史反馈</div>
          </div>
          
          <!-- 底部输入框 -->
          <div class="p-4 border-t border-outline-variant/15 bg-white/40">
            <div class="flex gap-3">
              <textarea v-model="feedbackInput" rows="1" class="flex-1 resize-none outline-none text-sm p-3 bg-white rounded-xl border border-outline-variant/30 focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/20 transition-all custom-scrollbar max-h-32 min-h-[44px]" placeholder="在此输入您的意见或建议..."></textarea>
              <button @click="submitFeedback" class="px-6 py-2 bg-primary text-on-primary-fixed font-bold text-sm rounded-xl hover:bg-primary-fixed transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center h-[44px]" :disabled="!feedbackInput.trim()">提交</button>
            </div>
          </div>
        </div>

        <!-- 默认空状态 -->
        <div v-else key="empty" class="flex flex-col items-center justify-center h-full w-full text-secondary">
          <el-icon :size="64" class="mb-4 opacity-20"><ChatLineSquare /></el-icon>
          <p class="text-lg font-medium text-on-surface">欢迎来到通讯中心</p>
          <p class="text-sm mt-2 opacity-80">点击左侧联系人或通知查看详情</p>
        </div>
      </transition>
      
      <!-- 右键菜单 -->
      <teleport to="body">
        <ul v-if="contextMenu.visible"
            :style="{ top: contextMenu.y + 'px', left: contextMenu.x + 'px' }"
            class="fixed z-[9999] bg-white border border-outline-variant/30 rounded-xl shadow-lg min-w-[110px] text-sm overflow-hidden shadow-black/5 flex flex-col">
          <li class="px-4 py-2.5 hover:bg-surface-container cursor-pointer text-on-surface flex items-center gap-2 transition-colors" @click="quoteMessage(contextMenu.msg)">
            <el-icon><ChatDotRound /></el-icon>引用
          </li>
          <li v-if="contextMenu.msg && contextMenu.msg.sender === 'student'" class="px-4 py-2.5 hover:bg-surface-container cursor-pointer text-error flex items-center gap-2 transition-colors border-t border-outline-variant/15" @click="recallMessage(contextMenu.msg)">
            <el-icon><Notification /></el-icon>撤回
          </li>
        </ul>
      </teleport>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { Notification, ChatDotRound, Picture, Paperclip, Document, Download, User, Clock, ChatLineSquare, EditPen, Close } from '@element-plus/icons-vue'

const currentTab = ref('全部')
const activeId = ref(null)
const chatInput = ref('')
const replyInput = ref('')
const chatScrollContainer = ref(null)
const chatInputRef = ref(null)

// Mock Chat Data
const chatMessages = ref([
  { id: 1, sender: 'teacher', text: '张小明同学，关于你申请的奖学金材料，我发现缺少了上学期的成绩单复印件，请尽快补齐交到办公室。', time: '05-18 09:15', isRead: false },
  { id: 2, sender: 'teacher', text: '如果找不到原件，可以去教务处重新打印一份盖章。', time: '05-18 09:16', isRead: false },
  { id: 3, sender: 'student', text: '好的李老师，我明天上午就带过去给您。', time: '05-18 09:20', isRead: true }
])

const unreadChatCount = computed(() => chatMessages.value.filter(m => m.sender === 'teacher' && !m.isRead).length)

// Mock Feedback Data
const feedbacks = ref([
  { id: 1, student: '李四', time: '昨天 14:30', content: '学校南门的共享单车停放区太小了，经常停不下，建议扩建。', reply: '感谢建议，后勤处已将南门外区域纳入下个月的扩建规划。', replyTime: '昨天 16:00' },
  { id: 2, student: '王五', time: '05-18 09:00', content: '图书馆二楼的空调制冷效果不好，希望能报修。', reply: '已通知维修人员，预计本周五前完成检修。', replyTime: '05-18 11:30' },
  { id: 3, student: '赵六', time: '05-19 10:20', content: '希望食堂能增加一些清淡口味的窗口。', reply: null, replyTime: null }
])
const feedbackInput = ref('')

const repliedFeedbacksCount = computed(() => feedbacks.value.filter(f => f.reply).length)

const myFeedbacks = computed(() => feedbacks.value.filter(f => f.student === '我'))
const feedbackStatus = computed(() => {
  if (myFeedbacks.value.length === 0) return null
  const latest = myFeedbacks.value[0]
  return latest.reply ? '已回复' : '待回复'
})

// Mock Data
const notifications = ref([
  {
    id: 1,
    type: 'GRADE',
    title: '关于开展2024-2025学年国家奖学金评选工作的通知',
    publisher: '学生工作部',
    timeShort: '10:30',
    timeFull: '2024-05-20 10:30',
    isRead: false,
    content: `各学院、各班级：\n\n为激励我校学生勤奋学习、积极进取，德、智、体、美、劳全面发展，根据《教育部 财政部关于印发〈普通本科高校、高等职业学校国家奖学金管理暂行办法〉的通知》（教财〔2007〕90号）及省教育厅有关文件精神，现就做好我校2024-2025学年国家奖学金评审工作通知如下：\n\n一、评选对象\n具有中华人民共和国国籍，纳入全国普通高校招生计划的全日制在校二年级（含）以上（含二年级）特别优秀的本专科学生。\n\n二、评选条件\n1. 热爱社会主义祖国，拥护中国共产党的领导；\n2. 遵守宪法和法律，遵守学校规章制度；\n3. 诚实守信，道德品质优良；\n4. 在校期间学习成绩优异，社会实践、创新能力、综合素质等方面特别突出。\n\n请各班级积极组织申报，于规定时间内将材料上交至辅导员处。`,
    attachments: [
      { name: '国家奖学金申请表.docx', size: '25.6 KB' },
      { name: '国家奖学金评审细则.pdf', size: '152.3 KB' }
    ],
    replies: [
      { name: '李老师', avatar: 'https://i.pravatar.cc/150?u=teacher_li', role: '班主任', time: '05-20 11:20', content: '请各位同学认真阅读通知，有问题及时联系我。' },
      { name: '学习委员 - 王强', avatar: 'https://i.pravatar.cc/150?u=student3', role: '', time: '05-20 12:05', content: '请问申请表最晚什么时候交呢？' }
    ]
  },
  {
    id: 2,
    type: 'CLASS',
    title: '辅导员通知：本周班会安排',
    publisher: '李老师',
    timeShort: '09:15',
    timeFull: '2024-05-20 09:15',
    isRead: false,
    content: '同学们，本周日下午 3:00 在 2B-401 召开本学期第一次线下班会，主要讨论新学期的学业规划和班委改选事宜，请全体同学准时参加，不得无故缺席。\n\n如有特殊情况需要请假，请提前在系统中提交请假申请。',
    attachments: [],
    replies: []
  },
  {
    id: 3,
    type: 'GRADE',
    title: '图书馆关于数据库使用培训的通知',
    publisher: '图书馆',
    timeShort: '昨天',
    timeFull: '2024-05-19 14:00',
    isRead: true,
    content: '为了帮助同学们更好地利用图书馆数字资源，提高信息检索能力，图书馆将举办系列数据库使用培训讲座...\n\n地点：图书馆一楼报告厅\n时间：本周三下午 4:00',
    attachments: [],
    replies: []
  },
  {
    id: 4,
    type: 'CLASS',
    title: '关于2024年下半年勤工助学岗位招募的通知',
    publisher: '学生资助管理中心',
    timeShort: '05-18',
    timeFull: '2024-05-18 10:00',
    isRead: true,
    content: '本学期勤工助学岗位开始招募，有意向的同学请填写报名表交至办公室。',
    attachments: [],
    replies: []
  },
  {
    id: 5,
    type: 'CLASS',
    title: '宿舍安全检查通知',
    publisher: '后勤管理处',
    timeShort: '05-15',
    timeFull: '2024-05-15 09:00',
    isRead: true,
    content: '为消除宿舍安全隐患，营造安全舒适的住宿环境，后勤管理处将于本周四开展全校宿舍安全大检查，重点检查违章电器等。',
    attachments: [],
    replies: []
  }
])

const filteredNotifications = computed(() => {
  if (currentTab.value === '未读') return notifications.value.filter(n => !n.isRead)
  if (currentTab.value === '已读') return notifications.value.filter(n => n.isRead)
  return notifications.value
})

const activeNotification = computed(() => {
  return activeId.value !== 'chat' ? notifications.value.find(n => n.id === activeId.value) : null
})

const selectChat = async () => {
  activeId.value = 'chat'
  chatMessages.value.forEach(m => {
    if (m.sender === 'teacher') m.isRead = true
  })
  await nextTick()
  if (chatInputRef.value) {
    chatInputRef.value.focus()
  }
}

const selectNotification = (id) => {
  activeId.value = id
  const notif = notifications.value.find(n => n.id === id)
  if (notif && !notif.isRead) {
    notif.isRead = true
  }
}

const openFeedback = () => {
  activeId.value = 'feedback'
}

const submitFeedback = () => {
  if(!feedbackInput.value.trim()) return
  feedbacks.value.unshift({
    id: Date.now(),
    student: '我',
    time: '刚刚',
    content: feedbackInput.value,
    reply: null,
    replyTime: null
  })
  feedbackInput.value = ''
}

const sendChat = async () => {
  if (!chatInput.value.trim()) return
  chatMessages.value.push({
    id: Date.now(),
    sender: 'student',
    text: chatInput.value,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    isRead: true,
    quote: quotingMessage.value ? { ...quotingMessage.value } : null
  })
  chatInput.value = ''
  quotingMessage.value = null
  
  await nextTick()
  if (chatScrollContainer.value) {
    chatScrollContainer.value.scrollTop = chatScrollContainer.value.scrollHeight
  }
}

// Context Menu
const contextMenu = ref({ visible: false, x: 0, y: 0, msg: null })
const quotingMessage = ref(null)

const openContextMenu = (e, msg) => {
  contextMenu.value = {
    visible: true,
    x: e.clientX,
    y: e.clientY,
    msg
  }
}

const closeContextMenu = () => {
  contextMenu.value.visible = false
}

const quoteMessage = async (msg) => {
  quotingMessage.value = msg
  closeContextMenu()
  await nextTick()
  if (chatInputRef.value) {
    chatInputRef.value.focus()
  }
}

const cancelQuote = () => {
  quotingMessage.value = null
}

const recallMessage = (msg) => {
  const index = chatMessages.value.findIndex(m => m.id === msg.id)
  if (index !== -1) {
    chatMessages.value.splice(index, 1, {
      id: msg.id,
      sender: 'system',
      text: '你撤回了一条消息',
      time: msg.time
    })
  }
  closeContextMenu()
}

onMounted(() => {
  document.addEventListener('click', closeContextMenu)
})
onUnmounted(() => {
  document.removeEventListener('click', closeContextMenu)
})

// 默认选中第一条未读通知，如果没有未读则选中第一条
const firstUnread = notifications.value.find(n => !n.isRead)
if (firstUnread) {
  selectNotification(firstUnread.id)
} else if (notifications.value.length > 0) {
  selectNotification(notifications.value[0].id)
}

</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
