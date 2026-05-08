<template>
  <div class="antialiased min-h-screen flex text-on-surface">
    <!-- 侧边栏 毛玻璃 -->
    <nav class="sidebar-glass hidden md:flex flex-col fixed left-0 top-0 h-full py-8 px-4 w-64 z-50">
      <div class="mb-12 px-2 flex items-center justify-center gap-3">
        <img src="/logosvg.png" alt="Logo" class="w-12 h-12 object-contain">
        <div class="flex flex-col items-center">
          <span class="text-[0.9rem] font-bold tracking-[0.2em] text-on-surface leading-tight text-center">西华师范大学</span>
          <span class="text-[1rem] font-black tracking-[0.15em] text-primary leading-tight mt-0.5 text-center">智慧学工系统</span>
        </div>
      </div>
      <ul class="flex flex-col gap-2 font-medium tracking-tight text-[0.875rem]">
        <li>
          <button
            @click="navigateTo('/teacher')"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all"
            :class="route.path === '/teacher' ? 'sidebar-active text-primary font-semibold' : 'text-secondary hover:bg-surface-container-low'"
          >
            <el-icon :size="20"><DataBoard /></el-icon>
            学生档案大屏
          </button>
        </li>
        <li>
          <button
            @click="navigateTo('/teacher/financial-aid')"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all"
            :class="route.path === '/teacher/financial-aid' ? 'sidebar-active text-primary font-semibold' : 'text-secondary hover:bg-surface-container-low'"
          >
            <el-icon :size="20"><Money /></el-icon>
            奖助服务管理
          </button>
        </li>
        <li>
          <button
            @click="navigateTo('/teacher/academic-warning')"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all"
            :class="route.path === '/teacher/academic-warning' ? 'sidebar-active text-primary font-semibold' : 'text-secondary hover:bg-surface-container-low'"
          >
            <el-icon :size="20"><TrendCharts /></el-icon>
            学业预警支持
          </button>
        </li>
        <li>
          <button
            @click="navigateTo('/teacher/career-plan')"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all"
            :class="route.path === '/teacher/career-plan' ? 'sidebar-active text-primary font-semibold' : 'text-secondary hover:bg-surface-container-low'"
          >
            <el-icon :size="20"><ChatLineRound /></el-icon>
            沟通互动
          </button>
        </li>
        <li>
          <button
            @click="navigateTo('/teacher/communication')"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all"
            :class="route.path === '/teacher/communication' ? 'sidebar-active text-primary font-semibold' : 'text-secondary hover:bg-surface-container-low'"
          >
            <el-icon :size="20"><Warning /></el-icon>
            校园安全预警
          </button>
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
             <div class="w-2 h-2 rounded-full bg-ai-primary animate-pulse"></div>
             <span class="text-xs text-secondary font-medium">Ollama AI 就绪</span>
           </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="main-content-wrapper flex-1 ml-0 md:ml-64 flex flex-col h-screen overflow-hidden">
      <!-- Header: 毛玻璃 + 路由标题 + 右侧操作 -->
      <header class="flex items-center justify-between px-8 fixed top-0 left-0 md:left-64 right-0 z-40 h-14 bg-surface/70 backdrop-blur-xl border-b border-outline-variant/10 font-sans tracking-tight">
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
                  <div v-for="n in notifications" :key="n.id" @click="toggleNotif(n)" class="px-4 py-3 border-b border-outline-variant/8 hover:bg-surface-container-low transition-colors cursor-pointer" :class="!n.read ? 'bg-blue-50/50' : ''">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-[11px] font-bold px-1.5 py-0.5 rounded-md" :class="n.tagStyle">{{ n.tag }}</span>
                      <span class="text-xs text-outline ml-auto">{{ n.time }}</span>
                      <span v-if="!n.read" class="w-2 h-2 rounded-full bg-error flex-shrink-0"></span>
                    </div>
                    <p class="text-sm font-semibold text-on-surface">{{ n.title }}</p>
                    <transition name="expand"><p v-if="n.expanded" class="mt-2 text-xs text-secondary leading-relaxed">{{ n.content }}</p></transition>
                  </div>
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
        <router-view :key="route.path + '_' + refreshKey" />
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataBoard, Money, TrendCharts, ChatLineRound, Warning, Bell, Setting, Close, Lock, SwitchButton, CircleCheck } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const refreshKey = ref(0)

const navigateTo = async (path) => {
  if (route.path !== path) {
    await router.push(path)
  }
  refreshKey.value++
}
const showSettings = ref(false)
const settingsTab = ref('password')
const pwdForm = ref({ current: '', newPwd: '', confirm: '' })
const notifOpen = ref(false)

const notifications = ref([
  { id: 1, tag: '申请待审', tagStyle: 'bg-orange-100 text-orange-700', time: '05-03 16:20', title: '张小明 提交了请假申请，待审批', content: '申请人: 张小明（202301042）｜类型: 返乡假｜时间: 11月16日至11月17日。请及时处理。', read: false, expanded: false, path: '/teacher' },
  { id: 2, tag: '系统', tagStyle: 'bg-blue-100 text-blue-700', time: '05-03 09:00', title: '学业预警引擎已完成本期检测', content: '本次检测发现 3 名学生触发学业预警条件，请前往「学业预警支持」查看详情。', read: true, expanded: false, path: '/teacher/academic-warning' },
])
const unreadCount = ref(notifications.value.filter(n => !n.read).length)
const toggleNotif = (n) => { 
  n.expanded = !n.expanded; 
  if (!n.read) { n.read = true; unreadCount.value = notifications.value.filter(x => !x.read).length; }
  if (n.path) {
    router.push(n.path);
    notifOpen.value = false;
  }
}
const markAllRead = () => { notifications.value.forEach(n => n.read = true); unreadCount.value = 0 }
const closeNotif = () => { notifOpen.value = false }
onMounted(() => document.addEventListener('click', closeNotif))
onUnmounted(() => document.removeEventListener('click', closeNotif))

const changePwd = () => {
  if (!pwdForm.value.current || !pwdForm.value.newPwd || !pwdForm.value.confirm) { ElMessage.warning('请填写完整的密码信息'); return }
  if (pwdForm.value.newPwd !== pwdForm.value.confirm) { ElMessage.error('两次输入的新密码不一致'); return }
  if (pwdForm.value.newPwd.length < 8) { ElMessage.error('新密码至少需要 8 位'); return }
  ElMessage.success('密码修改成功（演示模式）')
  showSettings.value = false
  pwdForm.value = { current: '', newPwd: '', confirm: '' }
}

const logout = () => {
  localStorage.removeItem('userRole')
  window.location.href = '/login'
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(5px); }
.notif-drop-enter-active { transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1); }
.notif-drop-leave-active { transition: all 0.15s ease; }
.notif-drop-enter-from, .notif-drop-leave-to { opacity: 0; transform: translateY(-8px) scale(0.96); }
.expand-enter-active, .expand-leave-active { transition: all 0.2s ease; overflow: hidden; }
.expand-enter-from, .expand-leave-to { max-height: 0; opacity: 0; }
.expand-enter-to, .expand-leave-from { max-height: 200px; opacity: 1; }
</style>
