<template>
  <div class="antialiased min-h-screen flex text-on-surface">
    <!-- 侧边栏 毛玻璃 -->
    <nav class="sidebar-glass hidden md:flex flex-col fixed left-0 top-0 h-full py-8 px-4 w-64 z-50 overflow-y-auto overscroll-contain custom-scrollbar">
      <div class="mb-12 px-2 flex items-center justify-center gap-3">
        <img src="/logosvg.png" alt="Logo" class="w-12 h-12 object-contain group-hover:rotate-12 transition-transform duration-500">
        <div class="flex flex-col items-center">
          <span class="text-[0.9rem] font-bold tracking-[0.2em] text-on-surface leading-tight text-center">西华师范大学</span>
          <span class="text-[1.1rem] font-black tracking-[0.15em] text-gradient leading-tight mt-0.5 text-center">智慧学工系统</span>
        </div>
      </div>
      <ul class="flex flex-col gap-2 font-medium tracking-tight text-[0.875rem]">
        <li>
          <router-link
            to="/teacher"
            exact-active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><Monitor /></el-icon>
            学生档案大屏
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/financial-aid"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><Trophy /></el-icon>
            奖助服务管理
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/academic-warning"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><TrendCharts /></el-icon>
            学业预警支持
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/safety-monitoring"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><Warning /></el-icon>
            安全监控中心
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/career-plan"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><EditPen /></el-icon>
            发布通知
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/communication"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><ChatLineRound /></el-icon>
            沟通互动
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/mid-term"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><DocumentChecked /></el-icon>
            中期鉴定收集
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/career-appointments"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><Calendar /></el-icon>
            职业规划预约
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/feedbacks"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><EditPen /></el-icon>
            意见反馈管理
          </router-link>
        </li>
        <li>
          <router-link
            to="/teacher/leaves"
            active-class="!text-primary font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><Calendar /></el-icon>
            请假报备管理
          </router-link>
        </li>
      </ul>
      
    </nav>

    <!-- Main Content -->
    <div class="main-content-wrapper flex-1 ml-0 md:ml-64 flex flex-col h-screen overflow-hidden">
      <!-- Header: 毛玻璃 + 路由标题 + 右侧操作 -->
      <header class="flex items-center justify-between px-8 fixed top-0 left-0 md:left-64 right-0 z-[9999] h-14 bg-surface/70 backdrop-blur-xl border-b border-outline-variant/10 font-sans tracking-tight">
        <!-- 页面标题 & 页面特定操作(通过 teleport 注入) -->
        <div class="flex items-center gap-6">
          <h2 class="text-2xl font-black text-on-surface tracking-tight">{{ route.meta.title || '' }}</h2>
          <div id="header-actions" class="flex items-center gap-3"></div>
        </div>

        <!-- 右侧操作区 -->
        <div class="flex items-center gap-4">
          <!-- 通知铃铛 -->
          <div class="relative flex items-center justify-center">
            <button @click.stop="notifOpen = !notifOpen" class="relative text-secondary hover:text-primary transition-colors p-1.5 rounded-lg hover:bg-surface-container-low flex items-center justify-center">
              <el-icon :size="20"><Bell /></el-icon>
              <span v-if="unreadCount > 0" class="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 bg-error rounded-full text-white text-[10px] font-bold flex items-center justify-center px-1 border-2 border-surface">{{ unreadCount }}</span>
            </button>
            <transition name="notif-drop">
              <div v-if="notifOpen" class="absolute right-0 top-full mt-3 w-80 bg-surface/95 backdrop-blur-2xl rounded-2xl shadow-2xl border border-outline-variant/20 overflow-hidden z-50">
                <div class="absolute -top-2 right-3 w-4 h-4 bg-surface/95 border-l border-t border-outline-variant/20 rotate-45"></div>
                <div class="px-4 py-3.5 border-b border-outline-variant/10 flex items-center justify-between">
                  <span class="font-bold text-sm">站内消息</span>
                  <button @click="markAllRead" class="text-xs text-primary hover:underline font-semibold flex items-center gap-1"><el-icon :size="12"><CircleCheck /></el-icon> 全部已读</button>
                </div>
                <div class="max-h-80 overflow-y-auto">
                  <div v-for="n in notifications.filter(x => !x.read)" :key="n.id" @click="toggleNotif(n)" class="px-4 py-3 border-b border-outline-variant/8 hover:bg-surface-container-low transition-colors cursor-pointer" :class="!n.read ? 'bg-blue-50/50' : ''">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-[11px] font-bold px-1.5 py-0.5 rounded-md" :class="n.tagStyle">{{ n.tag }}</span>
                      <span class="text-xs text-outline ml-auto">{{ n.time }}</span>
                      <span v-if="!n.read" class="w-2 h-2 rounded-full bg-error flex-shrink-0"></span>
                    </div>
                    <p class="text-sm font-semibold text-on-surface">{{ n.title }}</p>
                  </div>
                  <div v-if="notifications.filter(x => !x.read).length === 0" class="py-8 text-center text-secondary text-sm">暂无通知</div>
                </div>
              </div>
            </transition>
          </div>
          <!-- 设置 -->
          <button @click="showSettings = true" class="text-secondary hover:text-primary transition-colors p-1.5 rounded-lg hover:bg-surface-container-low flex items-center justify-center">
            <el-icon :size="20"><Setting /></el-icon>
          </button>
          <div class="flex items-center gap-2 cursor-pointer group">
            <div class="w-8 h-8 rounded-full bg-primary text-on-primary-fixed flex items-center justify-center font-bold text-sm shadow-md">李</div>
            <span class="text-sm font-semibold text-on-surface group-hover:text-primary transition-colors">李辅导员</span>
          </div>
        </div>
      </header>

      <!-- Canvas -->
      <main class="flex-1 overflow-y-auto p-6 pt-16 lg:p-10 lg:pt-20 max-w-[1600px] w-full mx-auto">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="route.fullPath" />
          </transition>
        </router-view>
      </main>
    </div>

    <!-- 设置弹窗 -->
    <div v-if="showSettings" class="fixed inset-0 z-[100] flex items-center justify-center" @click.self="showSettings = false">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm"></div>
      <div class="relative bg-surface rounded-2xl shadow-2xl w-full max-w-2xl mx-4 overflow-hidden border border-outline-variant/20">
        <div class="flex items-center justify-between px-8 py-5 border-b border-outline-variant/15">
          <h2 class="text-xl font-bold text-on-surface tracking-tight">系统设置</h2>
          <button @click="showSettings = false" class="w-8 h-8 rounded-full bg-surface-container-high flex items-center justify-center text-secondary hover:bg-surface-container-highest transition-colors">
            <el-icon><Close /></el-icon>
          </button>
        </div>
        <div class="flex min-h-[460px]">
          <!-- 设置导航 -->
          <div class="w-48 bg-surface-container-lowest border-r border-outline-variant/15 p-4 flex flex-col">
            <p class="text-[0.6875rem] font-bold text-outline uppercase tracking-wider mb-3 px-2">账号</p>
            <button @click="settingsTab = 'password'"
                    class="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-sm text-left mb-1 transition-colors"
                    :class="settingsTab === 'password' ? 'bg-primary text-on-primary-fixed font-semibold' : 'text-secondary hover:bg-surface-container-low'">
              <el-icon :size="16"><Lock /></el-icon>
              密码修改
            </button>
            <!-- 退出按钮放在设置导航最下方 -->
            <div class="mt-auto">
              <button @click="logout"
                      class="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-sm text-left text-error hover:bg-error-container transition-colors">
                <el-icon :size="16"><SwitchButton /></el-icon>
                退出登录
              </button>
            </div>
          </div>
          <!-- 设置内容 -->
          <div class="flex-1 p-8">
            <div v-if="settingsTab === 'password'">
              <h3 class="text-lg font-bold text-on-surface mb-1">修改密码</h3>
              <p class="text-sm text-secondary mb-6">建议定期修改密码，确保账号安全。</p>
              <div class="space-y-4 max-w-sm">
                <div>
                  <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">当前密码</label>
                  <el-input v-model="pwdForm.current" type="password" show-password placeholder="请输入当前密码" />
                </div>
                <div>
                  <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">新密码</label>
                  <el-input v-model="pwdForm.newPwd" type="password" show-password placeholder="至少 8 位，含字母和数字" />
                </div>
                <div>
                  <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">确认新密码</label>
                  <el-input v-model="pwdForm.confirm" type="password" show-password placeholder="再次输入新密码" />
                </div>
                <button @click="changePwd"
                        class="mt-2 px-6 py-2.5 bg-primary text-on-primary-fixed rounded-xl text-sm font-bold hover:bg-primary-fixed transition-colors shadow-md">
                  确认修改
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import {
  Warning, Trophy, Bell, Setting, Close, Lock,
  SwitchButton, CircleCheck, TrendCharts,
  Monitor, ChatLineRound, EditPen, DocumentChecked, Calendar
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const API = '/api/communication'
const teacherId = sessionStorage.getItem('userId') || 'T001'

const showSettings = ref(false)
const settingsTab = ref('password')
const pwdForm = ref({ current: '', newPwd: '', confirm: '' })
const notifOpen = ref(false)

const staticNotifications = ref([
  { id: 'static-1', tag: '申请待审', tagStyle: 'bg-orange-100 text-orange-700', time: '05-03 16:20', title: '张小明 提交了请假申请，待审批', content: '申请人: 张小明（202301042）｜类型: 返乡假｜时间: 11月16日至11月17日。请及时处理。', read: false, expanded: false, path: '/teacher/leaves' },
  { id: 'static-2', tag: '系统', tagStyle: 'bg-blue-100 text-blue-700', time: '05-03 09:00', title: '学业预警引擎已完成本期检测', content: '本次检测发现 3 名学生触发学业预警条件，请前往「学业预警支持」查看详情。', read: true, expanded: false, path: '/teacher/academic-warning' },
])
const msgNotifications = ref([])
const systemNotifications = ref([])

const notifications = computed(() => {
  return [...msgNotifications.value, ...systemNotifications.value, ...staticNotifications.value]
})
const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

const getReadNotifsFromStorage = () => {
  try {
    const val = localStorage.getItem('teacher_read_notifs')
    return val ? JSON.parse(val) : []
  } catch (e) {
    return []
  }
}

const saveReadNotifToStorage = (id) => {
  try {
    const readList = getReadNotifsFromStorage()
    if (!readList.includes(id)) {
      readList.push(id)
      localStorage.setItem('teacher_read_notifs', JSON.stringify(readList))
    }
  } catch (e) {}
}

const loadStaticNotifications = () => {
  const readList = getReadNotifsFromStorage()
  staticNotifications.value.forEach(n => {
    if (readList.includes(n.id)) {
      n.read = true
    }
  })
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  try {
    const date = new Date(dateTimeStr)
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    const hh = String(date.getHours()).padStart(2, '0')
    const mm = String(date.getMinutes()).padStart(2, '0')
    return `${m}-${d} ${hh}:${mm}`
  } catch (e) {
    return dateTimeStr
  }
}

const fetchMessageAlerts = async () => {
  try {
    const res = await request.get(`${API}/contacts`, { params: { userId: teacherId, role: 'teacher' } })
    if (res.data.code === 200) {
      const contacts = res.data.data
      const alerts = []
      for (const c of contacts) {
        if (c.unread > 0) {
          alerts.push({
            id: 'msg-' + c.id,
            tag: '私信',
            tagStyle: 'bg-green-100 text-green-700',
            time: '',
            title: `${c.name} 发来 ${c.unread} 条新消息`,
            content: '',
            read: false,
            expanded: false,
            path: `/teacher/communication?studentId=${c.id}`,
            unreadCount: c.unread
          })
        }
      }
      // 保留已读状态：之前标记为已读的且未读消息数量未发生变化的保持已读
      const oldMap = new Map(msgNotifications.value.map(n => [n.id, n]))
      for (const a of alerts) {
        const old = oldMap.get(a.id)
        if (old && old.read && old.unreadCount === a.unreadCount) {
          a.read = true
        }
      }
      msgNotifications.value = alerts
    }
  } catch (e) { /* ignore polling errors */ }
}

const fetchSystemAlerts = async () => {
  try {
    const readList = getReadNotifsFromStorage()
    const alerts = []

    // 1. Fetch pending leaves
    const leaveRes = await request.get('/api/applications/pending')
    if (leaveRes.data.code === 200) {
      const pendingApps = leaveRes.data.data
      for (const app of pendingApps) {
        if (app.type === 'LEAVE') {
          const alertId = `leave-${app.id}`
          alerts.push({
            id: alertId,
            tag: '申请待审',
            tagStyle: 'bg-orange-100 text-orange-700',
            time: formatDateTime(app.applyTime),
            title: `${app.studentName} 提交了请假申请，待审批`,
            content: `申请人: ${app.studentName}（${app.studentId}）｜类型: ${app.title}｜时间/原因: ${app.reason}。请及时处理。`,
            read: readList.includes(alertId),
            expanded: false,
            path: '/teacher/leaves'
          })
        }
      }
    }

    // 2. Fetch safety incidents
    const safetyRes = await request.get('/api/safety/incidents')
    if (safetyRes.data.code === 200) {
      const activeIncidents = safetyRes.data.data.filter(i => i.status === 'OPEN' || i.status === 'PROCESSING')
      for (const incident of activeIncidents) {
        const alertId = `safety-${incident.id}`
        let descText = incident.description
        try {
          const parsed = JSON.parse(incident.description)
          descText = parsed.reason || parsed.desc || parsed.title || incident.description
        } catch (e) {}

        const isLate = incident.type === 'LATE'
        alerts.push({
          id: alertId,
          tag: isLate ? '晚归报备' : '安全异常',
          tagStyle: isLate ? 'bg-amber-100 text-amber-700' : 'bg-red-100 text-red-700',
          time: formatDateTime(incident.reportTime),
          title: isLate ? `${incident.studentName} 提交了晚归报备` : `${incident.studentName} 上报了安全异常`,
          content: `上报人: ${incident.studentName}（${incident.studentId}）｜详情: ${descText}`,
          read: readList.includes(alertId),
          expanded: false,
          path: '/teacher/safety-monitoring'
        })
      }
    }

    // 3. Fetch career appointments
    const careerRes = await request.get(`/api/career/appointments/teacher/${teacherId}`)
    if (careerRes.data.code === 200) {
      const pendingAppointments = careerRes.data.data.filter(a => a.status === 'PENDING')
      for (const app of pendingAppointments) {
        const alertId = `career-${app.id}`
        alerts.push({
          id: alertId,
          tag: '规划预约',
          tagStyle: 'bg-purple-100 text-purple-700',
          time: formatDateTime(app.createTime),
          title: `${app.studentName} 预约了职业规划申请`,
          content: `学生: ${app.studentName}（${app.studentId}）｜预约时间: ${app.appointmentTime}｜原因: ${app.reason}`,
          read: readList.includes(alertId),
          expanded: false,
          path: '/teacher/career-appointments'
        })
      }
    }

    systemNotifications.value = alerts
  } catch (e) { /* ignore polling errors */ }
}

const fetchAllAlerts = () => {
  fetchMessageAlerts()
  fetchSystemAlerts()
}

let pollTimer = null

const toggleNotif = (n) => {
  if (!n.read) {
    n.read = true
    if (!n.id.startsWith('msg-')) {
      saveReadNotifToStorage(n.id)
    }
  }
  if (n.path) {
    router.push(n.path)
    notifOpen.value = false
  }
}
const markAllRead = () => {
  notifications.value.forEach(n => {
    n.read = true
    if (!n.id.startsWith('msg-')) {
      saveReadNotifToStorage(n.id)
    }
  })
}
const closeNotif = () => { notifOpen.value = false }

onMounted(() => {
  document.addEventListener('click', closeNotif)
  loadStaticNotifications()
  fetchAllAlerts()
  pollTimer = setInterval(fetchAllAlerts, 10000)
})
onUnmounted(() => {
  document.removeEventListener('click', closeNotif)
  if (pollTimer) clearInterval(pollTimer)
})

const changePwd = async () => {
  if (!pwdForm.value.current || !pwdForm.value.newPwd || !pwdForm.value.confirm) { ElMessage.warning('请填写完整的密码信息'); return }
  if (pwdForm.value.newPwd !== pwdForm.value.confirm) { ElMessage.error('两次输入的新密码不一致'); return }
  if (pwdForm.value.newPwd.length < 6) { ElMessage.error('新密码至少需要6位'); return }
  try {
    const res = await request.put('/api/auth/change-password', {
      username: sessionStorage.getItem('userName') || '',
      oldPassword: pwdForm.value.current,
      newPassword: pwdForm.value.newPwd
    })
    if (res.data.code === 200) {
      ElMessage.success('密码修改成功')
      showSettings.value = false
      pwdForm.value = { current: '', newPwd: '', confirm: '' }
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) { ElMessage.error('修改失败，请稍后再试') }
}

const logout = () => {
  sessionStorage.removeItem('userRole')
  window.location.href = '/login'
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(6px); }
.notif-drop-enter-active { transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1); }
.notif-drop-leave-active { transition: all 0.15s ease; }
.notif-drop-enter-from, .notif-drop-leave-to { opacity: 0; transform: translateY(-8px) scale(0.96); }
.expand-enter-active, .expand-leave-active { transition: all 0.2s ease; overflow: hidden; }
.expand-enter-from, .expand-leave-to { max-height: 0; opacity: 0; }
.expand-enter-to, .expand-leave-from { max-height: 200px; opacity: 1; }
</style>
