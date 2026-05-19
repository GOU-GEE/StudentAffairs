<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧学生列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <!-- 搜索栏 -->
      <div class="px-3 pt-2 pb-2">
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-outline z-10 flex items-center justify-center"><el-icon :size="14"><Search /></el-icon></span>
          <input v-model="searchQuery" placeholder="搜索学生姓名或学号..."
                 class="w-full pl-9 pr-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-primary/50 focus:ring-2 focus:ring-primary/20 transition-all" />
        </div>
      </div>

      <div class="h-px bg-outline-variant/15"></div>

      <!-- 学生列表 -->
      <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
        <div v-for="student in filteredStudents" :key="student.studentId"
             @click="selectStudent(student)"
             class="p-3 rounded-xl cursor-pointer transition-all border flex items-center gap-3"
             :class="activeStudentId === student.studentId ? 'bg-white shadow-sm border-primary/30 ring-1 ring-primary/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
          <div class="relative flex-shrink-0">
            <img :src="student.avatar || 'https://i.pravatar.cc/150?u=default'" class="w-10 h-10 rounded-full border border-outline-variant/30 object-cover" />
            <div v-if="getUnreadCount(student.studentId) > 0"
                 class="absolute -top-1 -right-1 min-w-[18px] h-[18px] bg-error text-white text-[10px] font-bold rounded-full flex items-center justify-center px-1">
              {{ getUnreadCount(student.studentId) }}
            </div>
          </div>
          <div class="flex-1 min-w-0">
            <div class="font-bold text-sm text-on-surface truncate">{{ student.name }}</div>
            <div class="text-xs text-outline truncate">{{ student.studentId }} · {{ student.major || '' }}</div>
          </div>
          <div v-if="getLastTime(student.studentId)" class="text-[10px] text-outline flex-shrink-0">
            {{ getLastTime(student.studentId) }}
          </div>
        </div>

        <div v-if="filteredStudents.length === 0" class="py-10 text-center text-secondary text-sm">
          暂无学生
        </div>
      </div>
    </div>

    <!-- 右侧聊天面板 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col relative"
         @click="closeContextMenu">
      <transition name="fade" mode="out-in">
        <!-- 聊天界面 -->
        <div v-if="activeStudentId" key="chat" class="flex flex-col h-full w-full">
          <!-- 聊天头部 -->
          <div class="px-6 py-4 border-b border-outline-variant/30 flex items-center bg-white/40">
            <div>
              <h3 class="font-bold text-base text-on-surface">{{ activeStudent?.name }}</h3>
              <p class="text-xs text-secondary mt-0.5">{{ activeStudent?.studentId }} · {{ activeStudent?.major || '' }}</p>
            </div>
          </div>

          <!-- 消息列表 -->
          <div class="flex-1 overflow-y-auto p-6 space-y-6 custom-scrollbar bg-surface-container-lowest/30" ref="chatScrollContainer">
            <template v-for="msg in currentMessages" :key="msg.id">
              <!-- 撤回的系统提示 -->
              <div v-if="msg.isRecalled" class="flex justify-center my-2">
                <span class="px-3 py-1 bg-surface-container-low rounded-full text-[11px] text-secondary">
                  {{ msg.senderId === teacherId ? '你' : (activeStudent?.name || '对方') }}撤回了一条消息
                </span>
              </div>
              <!-- 正常消息 -->
              <div v-else class="flex items-start gap-3"
                   :class="msg.senderId === teacherId ? 'flex-row-reverse' : ''"
                   @contextmenu.prevent="openContextMenu($event, msg)">
                <div class="px-4 py-2.5 rounded-2xl shadow-sm max-w-[70%]"
                     :class="msg.senderId === teacherId ? 'bg-primary text-on-primary-fixed rounded-tr-sm' : 'bg-white border border-outline-variant/15 rounded-tl-sm'">
                  <!-- 引用内容展示 -->
                  <div v-if="msg.quoteId && getQuotedMessage(msg.quoteId)" class="mb-2 p-2 bg-black/10 rounded-lg text-xs">
                    <div class="font-bold opacity-80 mb-1">{{ getQuotedSender(msg.quoteId) }}</div>
                    <div class="opacity-90 line-clamp-2">{{ getQuotedMessage(msg.quoteId)?.content || '消息已撤回' }}</div>
                  </div>
                  <p class="text-sm leading-relaxed" :class="msg.senderId === teacherId ? '' : 'text-on-surface'">{{ msg.content }}</p>
                </div>
              </div>
            </template>
            <div v-if="currentMessages.length === 0" class="flex flex-col items-center justify-center h-full text-secondary py-20">
              <el-icon :size="48" class="mb-3 opacity-20"><ChatDotRound /></el-icon>
              <p class="text-sm">暂无消息，发送第一条消息吧</p>
            </div>
          </div>

          <!-- 底部输入框 -->
          <div class="p-4 border-t border-outline-variant/15 bg-white/40 flex flex-col gap-2">
            <!-- 引用预览 -->
            <div v-if="quotingMessage" class="flex items-center justify-between bg-surface-container-low pl-3 pr-2 py-1.5 rounded-lg mx-2">
              <div class="text-xs text-secondary truncate flex-1 pr-4">
                <span class="font-bold">{{ quotingMessage.senderId === teacherId ? '你' : (activeStudent?.name || '对方') }}:</span> {{ quotingMessage.content }}
              </div>
              <el-icon class="cursor-pointer text-secondary hover:text-error transition-colors flex-shrink-0" @click="cancelQuote"><Close /></el-icon>
            </div>
            <div class="flex items-end gap-3 bg-white p-1.5 rounded-2xl border border-outline-variant/30 focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/20 transition-all">
              <textarea ref="chatInputRef" v-model="chatInput" rows="1"
                        @keydown.enter.exact.prevent="sendMessage"
                        class="flex-1 resize-none outline-none text-sm px-2 py-1.5 bg-transparent custom-scrollbar max-h-32 min-h-[32px]"
                        placeholder="输入消息... (Enter发送，Shift+Enter换行)"></textarea>
              <button @click="sendMessage"
                      class="px-4 py-1.5 bg-primary text-on-primary-fixed font-bold text-sm rounded-xl hover:bg-primary-fixed transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
                      :disabled="!chatInput.trim()">发送</button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else key="empty" class="flex flex-col items-center justify-center h-full w-full text-secondary">
          <el-icon :size="64" class="mb-4 opacity-20"><ChatLineSquare /></el-icon>
          <p class="text-lg font-medium text-on-surface">沟通互动</p>
          <p class="text-sm mt-2 opacity-80">选择左侧学生开始私信</p>
        </div>
      </transition>

      <!-- 右键菜单 -->
      <teleport to="body">
        <ul v-if="contextMenu.visible"
            :style="{ top: contextMenu.y + 'px', left: contextMenu.x + 'px' }"
            class="fixed z-[9999] bg-white border border-outline-variant/30 rounded-xl shadow-lg min-w-[110px] text-sm overflow-hidden shadow-black/5 flex flex-col">
          <li class="px-4 py-2.5 hover:bg-surface-container cursor-pointer text-on-surface flex items-center gap-2 transition-colors"
              @click="quoteMessage(contextMenu.msg)">
            <el-icon><ChatDotRound /></el-icon>引用
          </li>
          <li v-if="contextMenu.msg && contextMenu.msg.senderId === teacherId && canRecall(contextMenu.msg)"
              class="px-4 py-2.5 hover:bg-surface-container cursor-pointer text-error flex items-center gap-2 transition-colors border-t border-outline-variant/15"
              @click="recallMessage(contextMenu.msg)">
            <el-icon><RefreshLeft /></el-icon>撤回
          </li>
        </ul>
      </teleport>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { Search, ChatDotRound, ChatLineSquare, Close, RefreshLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const API = '/api/communication'
const teacherId = sessionStorage.getItem('userId') || 'T001'

const searchQuery = ref('')
const activeStudentId = ref(null)
const students = ref([])
const allMessages = ref([])
const chatInput = ref('')
const chatScrollContainer = ref(null)
const chatInputRef = ref(null)

// Context Menu
const contextMenu = ref({ visible: false, x: 0, y: 0, msg: null })
const quotingMessage = ref(null)

const loadStudents = async () => {
  try {
    const res = await request.get(`${API}/students`, { params: { teacherId } })
    if (res.data.code === 200) students.value = res.data.data
  } catch (e) { console.error(e) }
}

const loadMessages = async (studentId) => {
  try {
    const res = await request.get(`${API}/messages`, { params: { userId: teacherId, peerId: studentId } })
    if (res.data.code === 200) {
      allMessages.value = res.data.data
    }
  } catch (e) { console.error(e) }
}

const loadUnreadCounts = async () => {
  try {
    const res = await request.get(`${API}/contacts`, { params: { userId: teacherId, role: 'teacher' } })
    if (res.data.code === 200) {
      const contacts = res.data.data
      contacts.forEach(c => {
        const s = students.value.find(s => s.studentId === c.id)
        if (s) s._unread = c.unread
      })
    }
  } catch (e) { console.error(e) }
}

const selectStudent = async (student) => {
  activeStudentId.value = student.studentId
  quotingMessage.value = null
  await loadMessages(student.studentId)
  await nextTick()
  setTimeout(() => scrollToBottom(), 300)
  if (chatInputRef.value) chatInputRef.value.focus()
}

const sendMessage = async () => {
  if (!chatInput.value.trim() || !activeStudentId.value) return
  const msg = {
    senderId: teacherId,
    senderRole: 'teacher',
    receiverId: activeStudentId.value,
    content: chatInput.value.trim(),
    quoteId: quotingMessage.value?.id || null
  }
  try {
    const res = await request.post(`${API}/messages`, msg)
    if (res.data.code === 200) {
      allMessages.value.push(res.data.data)
      chatInput.value = ''
      quotingMessage.value = null
      await nextTick()
      scrollToBottom()
    }
  } catch (e) { ElMessage.error('发送失败') }
}

const recallMessage = async (msg) => {
  try {
    const res = await request.put(`${API}/messages/${msg.id}/recall`)
    if (res.data.code === 200) {
      const found = allMessages.value.find(m => m.id === msg.id)
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

const canRecall = (msg) => {
  if (!msg || !msg.sentTime) return false
  const sent = new Date(msg.sentTime)
  const now = new Date()
  return (now.getTime() - sent.getTime()) < 2 * 60 * 1000
}

const getQuotedMessage = (quoteId) => {
  return allMessages.value.find(m => m.id === quoteId && !m.isRecalled) || null
}

const getQuotedSender = (quoteId) => {
  const msg = allMessages.value.find(m => m.id === quoteId)
  if (!msg) return ''
  return msg.senderId === teacherId ? '你' : (activeStudent?.value?.name || '学生')
}

const getUnreadCount = (studentId) => {
  return allMessages.value.filter(m => m.senderId === studentId && !m.isRead && !m.isRecalled).length
}

const getLastTime = (studentId) => {
  const msgs = allMessages.value.filter(m =>
    (m.senderId === studentId || m.receiverId === studentId) && !m.isRecalled
  )
  if (msgs.length === 0) return ''
  const last = msgs[msgs.length - 1]
  return formatTimeShort(last.sentTime)
}

const filteredStudents = computed(() => {
  if (!searchQuery.value) return students.value
  const q = searchQuery.value.toLowerCase()
  return students.value.filter(s =>
    s.name.toLowerCase().includes(q) || s.studentId.toLowerCase().includes(q)
  )
})

const activeStudent = computed(() => {
  return students.value.find(s => s.studentId === activeStudentId.value) || null
})

const currentMessages = computed(() => {
  if (!activeStudentId.value) return []
  return allMessages.value.filter(m =>
    (m.senderId === teacherId && m.receiverId === activeStudentId.value) ||
    (m.senderId === activeStudentId.value && m.receiverId === teacherId)
  )
})

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

const scrollToBottom = () => {
  if (chatScrollContainer.value) {
    chatScrollContainer.value.scrollTop = chatScrollContainer.value.scrollHeight
  }
}

onMounted(async () => {
  await loadStudents()
  await loadUnreadCounts()
  document.addEventListener('click', closeContextMenu)

  // 处理从通知铃铛跳转过来的 studentId，延迟确保DOM就绪
  const targetStudentId = route.query.studentId
  if (targetStudentId) {
    await nextTick()
    const s = students.value.find(s => s.studentId === targetStudentId)
    if (s) await selectStudent(s)
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
