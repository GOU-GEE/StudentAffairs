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
        <li v-for="nav in navItems" :key="nav.path">
          <router-link
            :to="nav.path"
            active-class="!text-error font-bold"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group"
          >
            <el-icon :size="20" class="group-hover:scale-110 transition-transform"><component :is="nav.icon" /></el-icon>
            {{ nav.label }}
          </router-link>
        </li>
      </ul>

      <div class="mt-auto px-4">
        <div class="bg-surface-container-low p-4 rounded-xl border border-outline-variant/15 flex flex-col gap-2">
           <div class="text-[0.6875rem] font-bold text-secondary uppercase">系统状态</div>
           <div class="flex items-center gap-2">
             <div class="w-2 h-2 rounded-full bg-green-500"></div>
             <span class="text-xs text-secondary font-medium">后端服务正常</span>
           </div>
           <div class="flex items-center gap-2">
             <div class="w-2 h-2 rounded-full bg-error animate-pulse"></div>
             <span class="text-xs text-secondary font-medium">超级管理员模式</span>
           </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="main-content-wrapper flex-1 ml-0 md:ml-64 flex flex-col h-screen overflow-hidden">
      <!-- Header -->
      <header class="flex items-center justify-between px-8 fixed top-0 left-0 md:left-64 right-0 z-40 h-14 bg-surface/70 backdrop-blur-xl border-b border-outline-variant/10 font-sans tracking-tight">
        <div class="flex items-center gap-6">
          <h2 class="text-2xl font-black text-on-surface tracking-tight">{{ route.meta.title || '' }}</h2>
          <div id="header-actions" class="flex items-center gap-3"></div>
        </div>

        <div class="flex items-center gap-4">
          <!-- 通知铃铛 -->
          <div class="relative flex items-center justify-center">
            <button @click.stop="notifOpen = !notifOpen" class="relative text-secondary hover:text-error transition-colors p-1.5 rounded-lg hover:bg-surface-container-low flex items-center justify-center">
              <el-icon :size="20"><Bell /></el-icon>
              <span v-if="unreadCount > 0" class="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 bg-error rounded-full text-white text-[10px] font-bold flex items-center justify-center px-1 border-2 border-surface">{{ unreadCount }}</span>
            </button>
            <transition name="notif-drop">
              <div v-if="notifOpen" class="absolute right-0 top-full mt-3 w-80 bg-surface/95 backdrop-blur-2xl rounded-2xl shadow-2xl border border-outline-variant/20 overflow-hidden z-50">
                <div class="absolute -top-2 right-3 w-4 h-4 bg-surface/95 border-l border-t border-outline-variant/20 rotate-45"></div>
                <div class="px-4 py-3.5 border-b border-outline-variant/10 flex items-center justify-between">
                  <span class="font-bold text-sm">系统消息</span>
                  <button @click="markAllRead" class="text-xs text-error hover:underline font-semibold flex items-center gap-1"><el-icon :size="12"><CircleCheck /></el-icon> 全部已读</button>
                </div>
                <div class="max-h-80 overflow-y-auto">
                  <div v-for="n in notifications.filter(x => !x.read)" :key="n.id" @click="toggleNotif(n)" class="px-4 py-3 border-b border-outline-variant/8 hover:bg-surface-container-low transition-colors cursor-pointer" :class="!n.read ? 'bg-red-50/50' : ''">
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
          <button @click="showSettings = true" class="text-secondary hover:text-error transition-colors p-1.5 rounded-lg hover:bg-surface-container-low flex items-center justify-center">
            <el-icon :size="20"><Setting /></el-icon>
          </button>
          <div class="flex items-center gap-2 cursor-pointer group">
            <div class="w-8 h-8 rounded-full bg-error text-on-error flex items-center justify-center font-bold text-sm shadow-md">管</div>
            <span class="text-sm font-semibold text-on-surface group-hover:text-error transition-colors">系统管理员</span>
          </div>
        </div>
      </header>

      <!-- Canvas -->
      <main class="flex-1 overflow-y-auto p-6 pt-16 lg:p-10 lg:pt-20 max-w-[1600px] w-full mx-auto">
        <router-view v-slot="{ Component }">
          <transition name="fade">
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
          <div class="w-48 bg-surface-container-lowest border-r border-outline-variant/15 p-4 flex flex-col">
            <p class="text-[0.6875rem] font-bold text-outline uppercase tracking-wider mb-3 px-2">账号</p>
            <button @click="settingsTab = 'password'"
                    class="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-sm text-left mb-1 transition-colors"
                    :class="settingsTab === 'password' ? 'bg-error text-on-error font-semibold' : 'text-secondary hover:bg-surface-container-low'">
              <el-icon :size="16"><Lock /></el-icon>
              密码修改
            </button>
            <div class="mt-auto">
              <button @click="logout"
                      class="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-sm text-left text-error hover:bg-error-container transition-colors">
                <el-icon :size="16"><SwitchButton /></el-icon>
                退出登录
              </button>
            </div>
          </div>
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
                        class="mt-2 px-6 py-2.5 bg-error text-on-error rounded-xl text-sm font-bold hover:bg-error/80 transition-colors shadow-md">
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
  User, Briefcase, Money, Timer, Reading, School, Bell, Setting, Close, Lock,
  SwitchButton, CircleCheck
} from '@element-plus/icons-vue'

const navItems = [
  { path: '/admin/admins', label: '管理员管理', icon: User },
  { path: '/admin/teachers', label: '辅导员管理', icon: Briefcase },
  { path: '/admin/financials', label: '资助中心管理', icon: Money },
  { path: '/admin/youths', label: '团委管理', icon: Timer },
  { path: '/admin/academics', label: '教务管理', icon: Reading },
  { path: '/admin/students', label: '学生管理', icon: School },
]

const route = useRoute()
const router = useRouter()

const showSettings = ref(false)
const settingsTab = ref('password')
const pwdForm = ref({ current: '', newPwd: '', confirm: '' })
const notifOpen = ref(false)

const notifications = ref([
  { id: 1, tag: '系统', tagStyle: 'bg-red-100 text-red-700', time: '05-16 09:00', title: '新账号注册待审核', content: '有 2 个新的教师账号注册申请待审核，请前往「账号管理」处理。', read: false, expanded: false, path: '/admin/accounts' },
  { id: 2, tag: '导入', tagStyle: 'bg-blue-100 text-blue-700', time: '05-15 14:30', title: '成绩批量导入完成', content: '上学期成绩数据已成功导入 156 条记录，0 条失败。', read: true, expanded: false, path: '/admin/grades' },
])
const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)
const toggleNotif = (n) => {
  if (!n.read) { n.read = true; }
  if (n.path) {
    router.push(n.path);
    notifOpen.value = false;
  }
}
const markAllRead = () => { notifications.value.forEach(n => n.read = true) }
const closeNotif = () => { notifOpen.value = false }
onMounted(() => document.addEventListener('click', closeNotif))
onUnmounted(() => document.removeEventListener('click', closeNotif))

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
