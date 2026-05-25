<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <!-- 顶部：联系辅导员 -->
      <div class="px-2 pt-2 pb-2">
        <div class="p-3 rounded-xl flex items-center justify-between cursor-pointer transition-all"
             :class="activeId === 'chat' ? 'bg-primary/10 shadow-sm ring-1 ring-primary/20' : 'hover:bg-white hover:shadow-sm'"
             @click="selectChat">
          <div class="flex items-center gap-3">
            <div class="relative">
              <img :src="teacherAvatar" class="w-10 h-10 rounded-full border border-outline-variant/30 object-cover">
            </div>
            <div>
              <div class="font-bold text-sm text-on-surface">{{ teacherName }} (辅导员)</div>
            </div>
          </div>
          <div v-if="unreadChatCount > 0" class="min-w-[20px] h-5 px-1.5 bg-error text-white text-[11px] font-bold rounded-full flex items-center justify-center shadow-sm">
            {{ unreadChatCount }}
          </div>
        </div>
      </div>

      <div class="h-px bg-outline-variant/35"></div>

      <!-- 意见反馈入口 -->
      <div class="px-2 pt-2 pb-2 border-b border-outline-variant/35">
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
        <div class="flex items-center justify-between border-b border-outline-variant/35 flex-shrink-0">
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
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between gap-2 mb-1">
                  <h4 class="font-bold text-sm text-on-surface truncate flex-1" :class="{'font-black': !notif.isRead}">{{ notif.title }}</h4>
                  <span v-if="!notif.isRead" class="w-2 h-2 rounded-full bg-error flex-shrink-0"></span>
                </div>
                <div class="flex items-center justify-between text-xs">
                  <span class="text-secondary truncate pr-2">{{ notif.publisherName }}</span>
                  <span class="text-outline flex-shrink-0">{{ formatTimeShort(notif.publishTime) }}</span>
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

    <!-- 右侧面板 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col relative">
      <transition name="fade" mode="out-in">
        <!-- 聊天界面 -->
        <div v-if="activeId === 'chat'" key="chat" class="flex flex-col h-full w-full">
          <div class="px-6 py-4 border-b border-outline-variant/30 flex items-center bg-white/40">
            <div>
              <h3 class="font-bold text-base text-on-surface">{{ teacherName }}</h3>
              <p class="text-xs text-secondary mt-0.5">辅导员</p>
            </div>
          </div>

          <div class="flex-1 overflow-y-auto p-6 space-y-6 custom-scrollbar bg-surface-container-lowest/30" ref="chatScrollContainer">
            <template v-for="msg in chatMessages" :key="msg.id">
              <!-- 撤回提示 -->
              <div v-if="msg.isRecalled" class="flex justify-center my-2">
                <span class="px-3 py-1 bg-surface-container-low rounded-full text-[11px] text-secondary">
                  {{ msg.senderId === studentId ? '你' : teacherName }}撤回了一条消息
                </span>
              </div>
              <!-- 正常消息 -->
              <div v-else class="flex items-start gap-3" :class="msg.senderId === studentId ? 'flex-row-reverse' : ''" @contextmenu.prevent="openContextMenu($event, msg)">
                <div class="px-4 py-2.5 rounded-2xl shadow-sm max-w-[70%]"
                     :class="msg.senderId === studentId ? 'bg-primary text-on-primary-fixed rounded-tr-sm' : 'bg-white border border-outline-variant/15 rounded-tl-sm'">
                  <!-- 引用内容展示 -->
                  <div v-if="msg.quoteId && getQuotedMessage(msg.quoteId)" class="mb-2 p-2 bg-black/10 rounded-lg text-xs">
                    <div class="font-bold opacity-80 mb-1">{{ getQuotedSender(msg.quoteId) }}</div>
                    <div class="opacity-90 line-clamp-2">{{ getQuotedMessage(msg.quoteId)?.content || '消息已撤回' }}</div>
                  </div>
                  <p class="text-sm leading-relaxed" :class="msg.senderId === studentId ? '' : 'text-on-surface'">{{ msg.content }}</p>
                </div>
              </div>
            </template>
            <div v-if="chatMessages.length === 0" class="flex flex-col items-center justify-center h-full text-secondary py-20">
              <el-icon :size="48" class="mb-3 opacity-20"><ChatDotRound /></el-icon>
              <p class="text-sm">暂无消息</p>
            </div>
          </div>

          <!-- 底部输入框 -->
          <div class="p-4 border-t border-outline-variant/15 bg-white/40 flex flex-col gap-2">
            <!-- 引用预览 -->
            <div v-if="quotingMessage" class="flex items-center justify-between bg-surface-container-low pl-3 pr-2 py-1.5 rounded-lg mx-2">
              <div class="text-xs text-secondary truncate flex-1 pr-4">
                <span class="font-bold">{{ quotingMessage.senderId === studentId ? '我' : teacherName }}:</span> {{ quotingMessage.content }}
              </div>
              <el-icon class="cursor-pointer text-secondary hover:text-error transition-colors flex-shrink-0" @click="cancelQuote"><Close /></el-icon>
            </div>
            <div class="flex items-end gap-3 bg-white p-1.5 rounded-2xl border border-outline-variant/30 focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/20 transition-all">
              <textarea ref="chatInputRef" v-model="chatInput" rows="1" @keydown.enter.exact.prevent="sendChat" class="flex-1 resize-none outline-none text-sm px-2 py-1.5 bg-transparent custom-scrollbar max-h-32 min-h-[32px]" placeholder="发送消息给辅导员... (Enter发送，Shift+Enter换行)"></textarea>
              <div class="flex items-center gap-2 pb-0.5 pr-1">
                <button @click="ElMessage.info('图片发送功能开发中')" class="w-8 h-8 rounded-full hover:bg-surface-container flex items-center justify-center text-secondary transition-colors"><el-icon><Picture /></el-icon></button>
                <button @click="ElMessage.info('文件发送功能开发中')" class="w-8 h-8 rounded-full hover:bg-surface-container flex items-center justify-center text-secondary transition-colors"><el-icon><Paperclip /></el-icon></button>
                <button @click="sendChat" class="px-4 py-1.5 bg-primary text-on-primary-fixed font-bold text-sm rounded-xl hover:bg-primary-fixed transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1" :disabled="!chatInput.trim() || sendingChat">
                  <el-icon v-if="sendingChat" class="is-loading"><Loading /></el-icon>
                  <span>发送</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 通知详情界面 -->
        <div v-else-if="activeNotification" key="detail" class="flex flex-col h-full w-full overflow-y-auto custom-scrollbar">
          <div class="px-8 py-6 border-b border-outline-variant/30 bg-white/40">
            <div class="flex items-start justify-between gap-4 mb-4">
              <h2 class="text-xl font-bold text-on-surface leading-snug flex-1">{{ activeNotification.title }}</h2>
            </div>
            <div class="flex items-center gap-4 text-sm text-secondary">
              <span class="font-medium text-on-surface flex items-center gap-1.5">
                <el-icon><User /></el-icon> {{ activeNotification.publisherName }}
              </span>
              <span class="flex items-center gap-1.5">
                <el-icon><Clock /></el-icon> {{ formatTimeFull(activeNotification.publishTime) }}
              </span>
            </div>
          </div>

          <div class="p-8 pb-4 flex-1">
            <div class="prose prose-sm max-w-none text-on-surface whitespace-pre-wrap leading-relaxed">{{ activeNotification.content }}</div>
          </div>
        </div>

        <!-- 意见反馈板 -->
        <div v-else-if="activeId === 'feedback'" key="feedback" class="flex flex-col h-full w-full">
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

          <div class="flex-1 overflow-y-auto p-6 space-y-6 custom-scrollbar bg-surface-container-lowest/30">
            <div v-for="fb in feedbacks" :key="fb.id" class="bg-white rounded-2xl p-5 border border-outline-variant/15 shadow-sm">
              <div class="flex items-center justify-between mb-2">
                 <span class="font-bold text-sm text-on-surface">{{ fb.student }}</span>
                 <span class="text-xs text-outline">{{ fb.time }}</span>
              </div>
              <p class="text-sm text-on-surface mb-4">{{ fb.content }}</p>
              <div v-if="fb.reply" class="bg-surface-container-low rounded-xl p-4 relative">
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

          <div class="p-4 border-t border-outline-variant/15 bg-white/40">
            <div class="flex gap-3">
              <textarea v-model="feedbackInput" rows="1" class="flex-1 resize-none outline-none text-sm p-3 bg-white rounded-xl border border-outline-variant/30 focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/20 transition-all custom-scrollbar max-h-32 min-h-[44px]" placeholder="在此输入您的意见或建议..."></textarea>
              <button @click="submitFeedback" :disabled="!feedbackInput.trim() || submittingFeedback" class="px-6 py-2 bg-primary text-on-primary-fixed font-bold text-sm rounded-xl hover:bg-primary-fixed transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-1.5 h-[44px]">
                <el-icon v-if="submittingFeedback" class="is-loading"><Loading /></el-icon>
                <span>提交</span>
              </button>
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
          <li v-if="contextMenu.msg && contextMenu.msg.senderId === studentId && canRecall(contextMenu.msg)" class="px-4 py-2.5 hover:bg-surface-container cursor-pointer text-error flex items-center gap-2 transition-colors border-t border-outline-variant/15" @click="recallMessage(contextMenu.msg)">
            <el-icon><RefreshLeft /></el-icon>撤回
          </li>
        </ul>
      </teleport>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ChatDotRound, ChatLineSquare, EditPen, Close, User, Clock, Picture, Paperclip, RefreshLeft, Loading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const API = '/api/communication'
const studentId = sessionStorage.getItem('userId') || '202301042'
const studentName = sessionStorage.getItem('userName') || '张小明'
const teacherId = 'T001'
const teacherName = '李老师'
const teacherAvatar = '/avatar-placeholder.png'
const studentAvatar = '/avatar-placeholder.png'

const currentTab = ref('全部')
const activeId = ref(null)
const chatInput = ref('')
const feedbackInput = ref('')
const chatScrollContainer = ref(null)
const chatInputRef = ref(null)
const submittingFeedback = ref(false)
const sendingChat = ref(false)

// 聊天消息（从后端加载）
const chatMessages = ref([])
// 通知列表（从后端加载）
const notifications = ref([])
// 已读通知ID集合（本地跟踪）
const readNotifIds = ref(new Set())

// 反馈（从后端动态加载）
const feedbacks = ref([])

const loadFeedbacks = async () => {
  try {
    const res = await request.get(`${API}/feedbacks`, { params: { studentId } })
    if (res.data.code === 200) {
      feedbacks.value = res.data.data.map(fb => ({
        id: fb.id,
        student: fb.studentId === studentId ? '我' : (fb.studentName || '学生'),
        time: formatTimeFull(fb.createTime),
        content: fb.content,
        reply: fb.reply,
        replyTime: fb.replyTime ? formatTimeFull(fb.replyTime) : null
      }))
    }
  } catch (e) {
    console.error('加载意见反馈失败', e)
  }
}

// Context Menu
const contextMenu = ref({ visible: false, x: 0, y: 0, msg: null })
const quotingMessage = ref(null)

const unreadChatCount = computed(() => chatMessages.value.filter(m => m.senderId !== studentId && !m.isRead && !m.isRecalled).length)

const repliedFeedbacksCount = computed(() => feedbacks.value.filter(f => f.reply).length)

const myFeedbacks = computed(() => feedbacks.value.filter(f => f.student === '我'))
const feedbackStatus = computed(() => {
  if (myFeedbacks.value.length === 0) return null
  const latest = myFeedbacks.value[0]
  return latest.reply ? '已回复' : '待回复'
})

const filteredNotifications = computed(() => {
  if (currentTab.value === '未读') return notifications.value.filter(n => !n.isRead)
  if (currentTab.value === '已读') return notifications.value.filter(n => n.isRead)
  return notifications.value
})

const activeNotification = computed(() => {
  if (activeId.value === 'chat' || activeId.value === 'feedback' || !activeId.value) return null
  return notifications.value.find(n => n.id === activeId.value)
})

// 通知数据加载
const loadNotifications = async () => {
  try {
    const res = await request.get(`${API}/announcements`)
    if (res.data.code === 200) {
      notifications.value = res.data.data.map(n => ({
        ...n,
        isRead: readNotifIds.value.has(n.id)
      }))
    }
  } catch (e) { console.error('加载通知失败', e) }
}

// 聊天消息加载
const loadChatMessages = async () => {
  try {
    const res = await request.get(`${API}/messages`, { params: { userId: studentId, peerId: teacherId } })
    if (res.data.code === 200) {
      chatMessages.value = res.data.data
    }
  } catch (e) { console.error('加载消息失败', e) }
}

const selectChat = async () => {
  activeId.value = 'chat'
  await loadChatMessages()
  await nextTick()
  if (chatInputRef.value) chatInputRef.value.focus()
}

const selectNotification = (id) => {
  activeId.value = id
  readNotifIds.value.add(id)
  const notif = notifications.value.find(n => n.id === id)
  if (notif) notif.isRead = true
}

const openFeedback = () => {
  activeId.value = 'feedback'
}

const submitFeedback = async () => {
  if (submittingFeedback.value) return
  if (!feedbackInput.value.trim()) return
  submittingFeedback.value = true
  try {
    const res = await request.post(`${API}/feedbacks`, {
      studentId,
      studentName,
      content: feedbackInput.value
    })
    if (res.data.code === 200) {
      feedbackInput.value = ''
      ElMessage.success('反馈已提交，感谢您的宝贵意见！')
      await loadFeedbacks()
    } else {
      ElMessage.warning(res.data.msg || '提交失败')
    }
  } catch (e) {
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    submittingFeedback.value = false
  }
}

const sendChat = async () => {
  if (sendingChat.value) return
  if (!chatInput.value.trim()) return
  const msg = {
    senderId: studentId,
    senderRole: 'student',
    receiverId: teacherId,
    content: chatInput.value.trim(),
    quoteId: quotingMessage.value?.id || null
  }
  sendingChat.value = true
  try {
    const res = await request.post(`${API}/messages`, msg)
    if (res.data.code === 200) {
      chatMessages.value.push(res.data.data)
      chatInput.value = ''
      quotingMessage.value = null
      await nextTick()
      if (chatScrollContainer.value) {
        chatScrollContainer.value.scrollTop = chatScrollContainer.value.scrollHeight
      }
    }
  } catch (e) { ElMessage.error('发送失败') }
  finally {
    sendingChat.value = false
  }
}

const canRecall = (msg) => {
  if (!msg || !msg.sentTime) return false
  const sent = new Date(msg.sentTime)
  const now = new Date()
  return (now.getTime() - sent.getTime()) < 2 * 60 * 1000
}

const recallMessage = async (msg) => {
  try {
    const res = await request.put(`${API}/messages/${msg.id}/recall`)
    if (res.data.code === 200) {
      const found = chatMessages.value.find(m => m.id === msg.id)
      if (found) {
        found.isRecalled = true
        found.content = ''
      }
      ElMessage.success('消息已撤回')
    } else {
      ElMessage.warning(res.data.msg)
    }
  } catch (e) { ElMessage.error('撤回失败') }
  closeContextMenu()
}

const getQuotedMessage = (quoteId) => {
  return chatMessages.value.find(m => m.id === quoteId && !m.isRecalled) || null
}

const getQuotedSender = (quoteId) => {
  const msg = chatMessages.value.find(m => m.id === quoteId)
  if (!msg) return ''
  return msg.senderId === studentId ? '我' : teacherName
}

const openContextMenu = (e, msg) => {
  contextMenu.value = { visible: true, x: e.clientX, y: e.clientY, msg }
}

const closeContextMenu = () => {
  contextMenu.value.visible = false
}

const quoteMessage = async (msg) => {
  quotingMessage.value = msg
  closeContextMenu()
  await nextTick()
  if (chatInputRef.value) chatInputRef.value.focus()
}

const cancelQuote = () => {
  quotingMessage.value = null
}


const formatTimeShort = (s) => {
  if (!s) return ''
  const d = new Date(s)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  if (diff < 86400000) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const formatTimeFull = (s) => {
  if (!s) return ''
  const d = new Date(s)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

onMounted(async () => {
  await Promise.all([loadNotifications(), loadChatMessages(), loadFeedbacks()])
  document.addEventListener('click', closeContextMenu)

  if (route.query.select === 'chat') {
    selectChat()
  } else if (route.query.notifId) {
    const targetId = parseInt(route.query.notifId)
    if (notifications.value.find(n => n.id === targetId)) {
      selectNotification(targetId)
    }
  } else {
    // 默认选中第一条未读通知
    const firstUnread = notifications.value.find(n => !n.isRead)
    if (firstUnread) {
      selectNotification(firstUnread.id)
    } else if (notifications.value.length > 0) {
      selectNotification(notifications.value[0].id)
    }
  }
})

watch(() => route.query.notifId, (newId) => {
  if (newId) {
    const targetId = parseInt(newId)
    if (notifications.value.find(n => n.id === targetId)) {
      selectNotification(targetId)
    }
  }
})

watch(() => route.query.select, (newSelect) => {
  if (newSelect === 'chat') {
    selectChat()
  }
})

onUnmounted(() => {
  document.removeEventListener('click', closeContextMenu)
})
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
