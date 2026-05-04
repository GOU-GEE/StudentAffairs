<template>
  <div class="antialiased min-h-screen flex text-on-surface">
    <!-- 侧边栏 毛玻璃 -->
    <nav class="sidebar-glass hidden md:flex flex-col fixed left-0 top-0 h-full py-8 px-4 w-64 z-50">
      <div class="mb-12 px-4 flex items-center gap-3">
        <img src="/logosvg.png" alt="Logo" class="w-10 h-10 object-contain">
        <div class="flex flex-col text-left">
          <span class="text-[0.85rem] font-bold tracking-widest text-on-surface leading-tight">西华师范大学</span>
          <span class="text-[1rem] font-black tracking-[0.15em] text-primary leading-tight mt-0.5">智慧学工系统</span>
          <span class="text-[0.42rem] font-medium tracking-wider text-outline mt-1 whitespace-nowrap">CHINA WEST NORMAL UNIVERSITY STUDENT AFFAIRS</span>
        </div>
      </div>
      <ul class="flex flex-col gap-2 font-medium tracking-tight text-[0.875rem]">
        <li><router-link to="/student" exact-active-class="text-primary font-semibold border-r-2 border-primary bg-surface-container-highest" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-colors"><el-icon :size="20"><UserFilled /></el-icon>个人主页</router-link></li>
        <li><router-link to="/student/academic" active-class="text-primary font-semibold border-r-2 border-primary bg-surface-container-highest" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-colors"><el-icon :size="20"><Reading /></el-icon>学业与第二课堂</router-link></li>
        <li><router-link to="/student/applications" active-class="text-primary font-semibold border-r-2 border-primary bg-surface-container-highest" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-colors"><el-icon :size="20"><Document /></el-icon>我的申请</router-link></li>
        <li><router-link to="/student/career-plan" active-class="text-primary font-semibold border-r-2 border-primary bg-surface-container-highest" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-colors"><el-icon :size="20" class="text-ai-primary"><Guide /></el-icon><span class="text-ai-primary font-semibold">职业规划 (AI)</span></router-link></li>
        <li><router-link to="/student/campus-life" active-class="text-primary font-semibold border-r-2 border-primary bg-surface-container-highest" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-colors"><el-icon :size="20"><School /></el-icon>校园生活与安全</router-link></li>
      </ul>
      <!-- 个人信息入口 -->
      <div class="mt-auto px-4">
        <button @click="showProfile = !showProfile"
                class="w-full text-left p-4 rounded-xl border border-outline-variant/20 hover:bg-white/60 hover:border-primary/20 transition-all group"
                :class="showProfile ? 'bg-white/80 border-primary/30' : ''">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-primary text-on-primary-fixed flex items-center justify-center font-bold text-base shadow-sm flex-shrink-0">张</div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-bold text-on-surface truncate">张小明</p>
              <p class="text-[0.6875rem] text-secondary truncate">202301042 · 计科2301班</p>
            </div>
            <el-icon class="text-outline group-hover:text-primary transition-colors flex-shrink-0" :size="14"><ArrowRightBold /></el-icon>
          </div>
          <p class="text-[0.6875rem] text-outline mt-2">点击查看个人档案 →</p>
        </button>
      </div>
    </nav>

    <!-- 主内容区 -->
    <div class="flex-1 ml-0 md:ml-64 flex flex-col min-h-screen relative">
      <!-- Header 毛玻璃 -->
      <header class="flex items-center justify-between px-8 sticky top-0 z-40 h-14 bg-surface/75 backdrop-blur-xl border-b border-outline-variant/10 font-sans tracking-tight w-full">
        <div class="flex items-center gap-6">
          <h2 class="text-2xl font-black text-on-surface tracking-tight whitespace-nowrap">
            {{ showProfile ? '个人档案' : (route.meta.title || '') }}
          </h2>
          <!-- 申请页切换标签 -->
          <div v-if="route.name === 'StudentApplications' && !showProfile" class="flex items-center gap-1 bg-surface-container-low rounded-lg p-1">
            <button v-for="tab in appTabs" :key="tab.value" @click="switchTab(tab.value)"
                    class="px-3.5 py-1.5 rounded-md text-[0.8125rem] font-semibold transition-all"
                    :class="currentTab === tab.value ? 'bg-surface text-on-surface shadow-sm' : 'text-secondary hover:text-on-surface'">
              {{ tab.label }}
            </button>
          </div>
        </div>
        <!-- 右侧操作 -->
        <div class="flex items-center gap-4 relative">
          <!-- 通知铃铛 -->
          <div class="relative flex items-center justify-center">
            <button @click.stop="notifOpen = !notifOpen" class="relative text-secondary hover:text-primary transition-colors p-1.5 rounded-lg hover:bg-surface-container-low flex items-center justify-center">
              <el-icon :size="20"><Bell /></el-icon>
              <span v-if="unreadCount > 0" class="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 bg-error rounded-full text-white text-[10px] font-bold flex items-center justify-center px-1 border-2 border-surface">{{ unreadCount }}</span>
            </button>
            <!-- 通知下拉面板 -->
            <transition name="notif-drop">
              <div v-if="notifOpen" class="absolute right-0 top-full mt-3 w-80 bg-surface/95 backdrop-blur-2xl rounded-2xl shadow-2xl border border-outline-variant/20 overflow-hidden z-50">
                <!-- 小箭头 -->
                <div class="absolute -top-2 right-3 w-4 h-4 bg-surface/95 border-l border-t border-outline-variant/20 rotate-45"></div>
                <div class="px-4 py-3.5 border-b border-outline-variant/10 flex items-center justify-between">
                  <span class="font-bold text-sm text-on-surface">站内消息</span>
                  <button @click="markAllRead" class="text-xs text-primary hover:underline font-semibold flex items-center gap-1">
                    <el-icon :size="12"><CircleCheck /></el-icon> 全部已读
                  </button>
                </div>
                <div class="max-h-80 overflow-y-auto">
                  <div v-for="n in notifications" :key="n.id" @click="toggleNotif(n)"
                       class="px-4 py-3 border-b border-outline-variant/8 hover:bg-surface-container-low transition-colors cursor-pointer"
                       :class="!n.read ? 'bg-blue-50/50' : ''">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-[11px] font-bold px-1.5 py-0.5 rounded-md" :class="n.tagStyle">{{ n.tag }}</span>
                      <span class="text-xs text-outline ml-auto">{{ n.time }}</span>
                      <span v-if="!n.read" class="w-2 h-2 rounded-full bg-error flex-shrink-0"></span>
                    </div>
                    <p class="text-sm font-semibold text-on-surface leading-snug">{{ n.title }}</p>
                    <transition name="expand">
                      <p v-if="n.expanded" class="mt-2 text-xs text-secondary leading-relaxed">{{ n.content }}</p>
                    </transition>
                  </div>
                  <div v-if="notifications.length === 0" class="py-8 text-center text-secondary text-sm">暂无通知</div>
                </div>
              </div>
            </transition>
          </div>
          <!-- 设置 -->
          <button @click="showSettings = true" class="text-secondary hover:text-primary transition-colors p-1.5 rounded-lg hover:bg-surface-container-low flex items-center justify-center">
            <el-icon :size="20"><Setting /></el-icon>
          </button>
          <!-- 头像 -->
          <div class="flex items-center gap-2 cursor-pointer group">
            <img src="https://i.pravatar.cc/150?u=student1" class="w-8 h-8 rounded-full border border-outline-variant/30">
            <span class="text-sm font-semibold text-on-surface group-hover:text-primary transition-colors">张小明</span>
          </div>
        </div>
      </header>

      <!-- 主内容：档案页 or 路由页 -->
      <main class="flex-1 p-6 lg:p-10 max-w-[1400px] w-full mx-auto relative z-10">
        <!-- 个人档案（全页展示） -->
        <transition name="fade">
          <div v-if="showProfile" class="space-y-6">
            <!-- 基本身份 -->
            <div class="bg-surface/90 backdrop-blur-md rounded-2xl border border-outline-variant/15 p-6 shadow-sm grid grid-cols-1 md:grid-cols-3 gap-6">
              <div class="md:col-span-1">
                <div class="flex items-center justify-between mb-4">
                  <p class="text-[0.6875rem] font-bold text-outline uppercase tracking-widest">基本身份</p>
                  <span class="px-2.5 py-1 bg-green-50 text-green-700 text-xs font-bold rounded-full flex items-center gap-1 border border-green-100"><span class="w-1.5 h-1.5 rounded-full bg-green-500"></span>在籍</span>
                </div>
                <div class="flex items-center gap-4 mb-5">
                  <img src="https://i.pravatar.cc/150?u=student_profile" class="w-20 h-20 rounded-2xl object-cover shadow-sm ring-1 ring-outline-variant/20">
                  <div>
                    <h4 class="text-2xl font-bold text-on-surface mb-1">张小明</h4>
                    <p class="text-sm text-secondary">学号: 202301042</p>
                    <p class="text-xs text-outline">出生日期: 2004年3月15日</p>
                  </div>
                </div>
                <div class="divide-y divide-outline-variant/10 text-sm">
                  <div class="flex justify-between py-2.5"><span class="text-secondary">电子邮箱</span><span class="font-semibold text-on-surface">zhangxm@stu.university.edu</span></div>
                  <div class="flex justify-between py-2.5"><span class="text-secondary">联系电话</span><span class="font-semibold">138 **** 2024</span></div>
                  <div class="flex justify-between py-2.5"><span class="text-secondary">证件号码</span><span class="font-semibold">440*** ****0032</span></div>
                </div>
              </div>
              <!-- 学籍详情 -->
              <div class="md:col-span-2">
                <p class="text-[0.6875rem] font-bold text-outline uppercase tracking-widest mb-4">学籍详情</p>
                <div class="grid grid-cols-3 gap-3">
                  <div v-for="item in academicInfo" :key="item.label" class="p-3 bg-surface rounded-xl border border-outline-variant/10 shadow-sm">
                    <label class="text-xs text-secondary font-semibold block mb-1">{{ item.label }}</label>
                    <p class="text-sm font-bold text-on-surface">{{ item.value }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 健康与画像 + 家庭联系人 -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="bg-surface/90 backdrop-blur-md rounded-2xl border border-outline-variant/15 p-6 shadow-sm">
                <p class="text-[0.6875rem] font-bold text-outline uppercase tracking-widest mb-4">健康与个人画像</p>
                <div class="grid grid-cols-2 gap-4 text-sm">
                  <div class="p-3 rounded-xl bg-surface border border-outline-variant/10">
                    <p class="text-xs text-secondary font-semibold mb-1">过敏史</p><p class="font-bold">无已知过敏</p>
                  </div>
                  <div class="p-3 rounded-xl bg-surface border border-outline-variant/10">
                    <p class="text-xs text-secondary font-semibold mb-1">血型</p><p class="font-bold">A 型 (Rh+)</p>
                  </div>
                  <div class="p-3 rounded-xl bg-surface border border-outline-variant/10 col-span-2">
                    <p class="text-xs text-secondary font-semibold mb-1">职业目标</p>
                    <p class="font-medium leading-relaxed">致力于 AI / 大模型工程领域，目标进入头部互联网公司担任算法工程师。</p>
                  </div>
                  <div class="col-span-2">
                    <p class="text-xs text-secondary font-semibold mb-2">课外兴趣</p>
                    <div class="flex flex-wrap gap-1.5">
                      <span v-for="tag in ['篮球', '算法竞赛', 'GitHub 开源', '读书']" :key="tag" class="px-3 py-1 bg-surface border border-outline-variant/20 rounded-full text-xs font-semibold text-secondary">{{ tag }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="bg-surface/90 backdrop-blur-md rounded-2xl border border-outline-variant/15 p-6 shadow-sm">
                <p class="text-[0.6875rem] font-bold text-outline uppercase tracking-widest mb-4">家庭与紧急联系人</p>
                <div class="space-y-3">
                  <div class="p-4 bg-surface rounded-xl border border-outline-variant/10">
                    <div class="flex justify-between mb-2"><span class="text-sm font-bold">张大国</span><span class="text-xs px-2 py-0.5 bg-surface-container-high text-secondary rounded-full font-bold">父亲</span></div>
                    <p class="text-sm text-secondary flex items-center gap-2"><el-icon class="text-outline"><Phone /></el-icon>138 **** 1024</p>
                  </div>
                  <div class="p-4 bg-surface rounded-xl border border-outline-variant/10">
                    <div class="flex justify-between mb-2"><span class="text-sm font-bold">李秀梅</span><span class="text-xs px-2 py-0.5 bg-surface-container-high text-secondary rounded-full font-bold">母亲</span></div>
                    <p class="text-sm text-secondary flex items-center gap-2"><el-icon class="text-outline"><Phone /></el-icon>139 **** 8823</p>
                  </div>
                  <div class="p-4 bg-red-50 rounded-xl border border-red-100">
                    <div class="flex justify-between mb-1"><span class="text-sm font-bold">张大国</span><span class="text-xs px-2 py-0.5 bg-red-100 text-red-700 rounded-full font-bold">紧急联系人</span></div>
                    <p class="text-xs text-secondary mb-2">关系: 父亲</p>
                    <p class="text-sm font-bold text-error flex items-center gap-2"><el-icon><Phone /></el-icon>138 **** 1024</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </transition>

        <!-- 正常路由页面 -->
        <transition name="fade">
          <div v-if="!showProfile">
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" :key="route.fullPath" />
              </transition>
            </router-view>
          </div>
        </transition>
      </main>
    </div>

    <!-- 设置弹窗 -->
    <div v-if="showSettings" class="fixed inset-0 z-[100] flex items-center justify-center" @click.self="showSettings = false">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm"></div>
      <div class="relative bg-surface rounded-2xl shadow-2xl w-full max-w-2xl mx-4 overflow-hidden border border-outline-variant/20">
        <div class="flex items-center justify-between px-8 py-5 border-b border-outline-variant/15">
          <h2 class="text-xl font-bold tracking-tight">个人设置</h2>
          <button @click="showSettings = false" class="w-8 h-8 rounded-full bg-surface-container-high flex items-center justify-center text-secondary hover:bg-surface-container-highest transition-colors"><el-icon><Close /></el-icon></button>
        </div>
        <div class="flex min-h-[440px]">
          <div class="w-48 bg-surface-container-lowest border-r border-outline-variant/15 p-4 flex flex-col">
            <p class="text-[0.6875rem] font-bold text-outline uppercase tracking-wider mb-3 px-2">账号</p>
            <button @click="settingsTab='password'" class="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-sm text-left mb-1 transition-colors" :class="settingsTab==='password'?'bg-primary text-on-primary-fixed font-semibold':'text-secondary hover:bg-surface-container-low'">
              <el-icon :size="16"><Lock /></el-icon>密码修改
            </button>
            <div class="mt-auto">
              <button @click="logout" class="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-sm text-left text-error hover:bg-error-container transition-colors">
                <el-icon :size="16"><SwitchButton /></el-icon>退出登录
              </button>
            </div>
          </div>
          <div class="flex-1 p-8">
            <h3 class="text-lg font-bold mb-1">修改密码</h3>
            <p class="text-sm text-secondary mb-6">建议定期修改密码，确保账号安全。</p>
            <div class="space-y-4 max-w-sm">
              <div><label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">当前密码</label><el-input v-model="pwdForm.current" type="password" show-password placeholder="请输入当前密码" /></div>
              <div><label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">新密码</label><el-input v-model="pwdForm.newPwd" type="password" show-password placeholder="至少 8 位" /></div>
              <div><label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">确认新密码</label><el-input v-model="pwdForm.confirm" type="password" show-password placeholder="再次输入新密码" /></div>
              <button @click="changePwd" class="px-6 py-2.5 bg-primary text-on-primary-fixed rounded-xl text-sm font-bold hover:bg-primary-fixed transition-colors shadow-md">确认修改</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Reading, Document, Guide, School, Bell, Setting, Close, Lock, SwitchButton, ArrowRightBold, Phone, CircleCheck } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const showProfile = ref(false)
const showSettings = ref(false)
const settingsTab = ref('password')
const notifOpen = ref(false)
const pwdForm = ref({ current: '', newPwd: '', confirm: '' })

const academicInfo = [
  { label: '所属学院', value: '计算机科学学院' },
  { label: '主修专业', value: '计算机科学与技术' },
  { label: '当前年级', value: '大一 · 2023级' },
  { label: '学籍状态', value: '全日制在读' },
  { label: '所在校区', value: '南校区 3号宿舍楼' },
  { label: '生源地', value: '广东省广州市' },
]

const notifications = ref([
  { id: 1, tag: '审批结果', tagStyle: 'bg-green-100 text-green-700', time: '05-03 17:30', title: '您的请假申请已通过审批', content: '辅导员李老师已批准您 11月16日至11月17日的返乡假。请注意按时返校销假。', read: false, expanded: false, path: '/student/applications' },
  { id: 2, tag: '系统通知', tagStyle: 'bg-blue-100 text-blue-700', time: '05-03 09:00', title: '2024年度国家奖学金申请通道已开放', content: '本次申请截止日期为 2024年11月30日，请尽快前往「我的申请 → 奖助学金」提交材料。', read: false, expanded: false, path: '/student/applications?tab=scholarship' },
  { id: 3, tag: '校园公告', tagStyle: 'bg-orange-100 text-orange-700', time: '05-02 14:00', title: '【重要】本周五下午校园消防演练通知', content: '本周五（11月22日）下午 3:00 起，全校开展消防演练，请同学们配合有序疏散。', read: true, expanded: false, path: '/student/campus-life' },
])

const unreadCount = ref(notifications.value.filter(n => !n.read).length)

const toggleNotif = (n) => {
  n.expanded = !n.expanded
  if (!n.read) { n.read = true; unreadCount.value = notifications.value.filter(x => !x.read).length }
  if (n.path) {
    if (n.path.includes('?tab=')) {
      const tab = n.path.split('?tab=')[1];
      router.push({ path: n.path.split('?')[0], query: { tab } });
    } else {
      router.push(n.path);
    }
    notifOpen.value = false;
  }
}
const markAllRead = () => {
  notifications.value.forEach(n => n.read = true)
  unreadCount.value = 0
}

// 点击外部关闭通知
const closeNotif = () => { notifOpen.value = false }
onMounted(() => document.addEventListener('click', closeNotif))
onUnmounted(() => document.removeEventListener('click', closeNotif))

// 路由切换时关闭档案
watch(() => route.name, () => { showProfile.value = false })

const appTabs = [{ label: '📅 请假报备', value: 'leave' }, { label: '🏆 奖助学金', value: 'scholarship' }]
const currentTab = ref('leave')
const switchTab = (val) => {
  currentTab.value = val
  router.replace({ name: 'StudentApplications', query: { tab: val } })
}
watch(() => route.query.tab, (val) => { if (val) currentTab.value = val }, { immediate: true })

const changePwd = () => {
  if (!pwdForm.value.current || !pwdForm.value.newPwd || !pwdForm.value.confirm) { ElMessage.warning('请填写完整信息'); return }
  if (pwdForm.value.newPwd !== pwdForm.value.confirm) { ElMessage.error('两次密码不一致'); return }
  ElMessage.success('密码修改成功（演示模式）')
  showSettings.value = false
  pwdForm.value = { current: '', newPwd: '', confirm: '' }
}
const logout = () => { 
  localStorage.removeItem('userRole'); 
  window.location.href = '/login';
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
