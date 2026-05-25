<template>
  <div class="h-full flex flex-col">
    <!-- 底部主内容：左栏活动列表，右栏活动详情 -->
    <div class="grid grid-cols-1 xl:grid-cols-12 gap-6 items-stretch relative flex-1 min-h-0">
      <!-- 左侧：活动列表 -->
      <div class="xl:col-span-8 flex flex-col bg-white rounded-3xl px-6 pt-4 pb-6 border border-outline-variant/20 shadow-sm relative z-20">
        <!-- 导航 Tabs 与 搜索栏 -->
        <div class="flex items-center justify-between border-b border-outline-variant/20 pb-3 flex-shrink-0">
          <div class="flex items-center gap-2">
            <button v-for="tab in tabs" :key="tab.value"
              @click="activeTab = tab.value"
              class="px-3 py-2 text-sm font-semibold transition-colors rounded-lg"
              :class="activeTab === tab.value ? 'bg-surface-container-high text-on-surface' : 'text-secondary hover:bg-surface-container-low hover:text-on-surface'">
              {{ tab.label }}
            </button>
          </div>
          <div class="flex items-center gap-4">
            <div class="relative w-64 flex items-center">
              <span class="absolute left-3 flex items-center text-outline"><el-icon><Search /></el-icon></span>
              <input v-model="searchQuery" type="text" placeholder="搜索活动名称、关键字..." class="w-full pl-9 pr-3 py-2 bg-surface-container-lowest border border-outline-variant/30 rounded-xl text-xs font-medium focus:outline-none focus:border-emerald-500 transition-all">
            </div>
          </div>
        </div>

        <!-- 活动列表 (锁定高度刚好展示5条，移除横/纵滚动条，纯靠分页) -->
        <div class="flex flex-col gap-4 mt-4 h-[694px] overflow-hidden flex-shrink-0 pr-2">
          <div v-for="activity in paginatedActivities" :key="activity.id"
               @click="selectActivity(activity)"
               class="rounded-2xl p-3 border border-outline-variant/20 hover:shadow-md transition-shadow flex gap-4 cursor-pointer bg-surface-container-lowest"
               :class="selectedActivity?.id === activity.id ? 'border-emerald-500 shadow-md ring-1 ring-emerald-500/20' : 'hover:border-emerald-500/30 group/card'">

            <!-- 左侧图片 -->
            <div class="w-[100px] h-[100px] rounded-xl overflow-hidden flex-shrink-0 relative">
              <img :src="activity.image" class="w-full h-full object-cover group-hover/card:scale-105 transition-transform duration-300" :alt="activity.title">
              <div class="absolute top-1.5 left-1.5 px-1.5 py-0.5 rounded text-[10px] font-bold text-white shadow-sm" :class="statusConfig[activity.status].bg">
                {{ statusConfig[activity.status].label }}
              </div>
            </div>

            <!-- 中间内容 -->
            <div class="flex-1 flex flex-col justify-between py-0.5 min-w-0">
              <div>
                <h3 class="text-base font-bold text-on-surface tracking-tight truncate mb-2">{{ activity.title }}</h3>
                <p class="text-xs text-secondary leading-relaxed line-clamp-1 mb-2">{{ activity.desc }}</p>
                <div class="flex flex-wrap items-center gap-x-5 gap-y-2 text-xs text-secondary font-medium">
                  <span class="flex items-center gap-1.5 whitespace-nowrap"><el-icon><Clock /></el-icon> {{ activity.time }}</span>
                  <span class="flex items-center gap-1.5 whitespace-nowrap"><el-icon><Location /></el-icon> {{ activity.location }}</span>
                </div>
              </div>
            </div>

            <!-- 右侧操作 -->
            <div class="flex flex-col items-center justify-center border-l border-outline-variant/15 pl-5 min-w-[100px] flex-shrink-0">
              <span class="text-[11px] font-bold text-secondary mb-1">{{ activity.creditType || '活动学时' }}</span>
              <span class="text-[1.35rem] font-black leading-none mb-2 text-emerald-600">{{ activity.hours }} <span class="text-[10px] font-bold">学时</span></span>
              <button v-if="grantedIds.includes(activity.id)"
                class="w-full py-1.5 rounded-lg text-xs font-bold transition-all shadow-sm bg-gray-100 text-gray-400 cursor-default">
                已发放
              </button>
              <button v-else-if="activity.status !== 'completed'"
                disabled
                class="w-full py-1.5 rounded-lg text-xs font-bold transition-all shadow-sm bg-gray-100 text-gray-400 cursor-not-allowed">
                未结束
              </button>
              <button v-else @click.stop="grantHours(activity)" :disabled="submittingActivityId !== null"
                class="w-full py-1.5 rounded-lg text-xs font-bold transition-all shadow-sm bg-emerald-500 text-white hover:bg-emerald-600 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-1">
                <el-icon v-if="submittingActivityId === activity.id" class="is-loading"><Loading /></el-icon>
                <span>发放学时</span>
              </button>
            </div>
          </div>
          <div v-if="paginatedActivities.length === 0" class="py-20 text-center text-gray-400 text-sm">
            暂无活动记录
          </div>
        </div>

        <!-- 底部高保真统计及分页底栏 -->
        <div class="flex items-center justify-between border-t border-outline-variant/15 pt-4 mt-auto flex-shrink-0">
          <!-- 左下角：统计数量 -->
          <div class="text-xs font-semibold text-secondary flex items-center gap-1.5">
            <span class="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
            共 {{ filteredActivities.length }} 条活动
          </div>
          
          <!-- 右下角：页数按钮 -->
          <div class="flex items-center gap-1">
            <button 
              @click="currentPage > 1 && currentPage--" 
              :disabled="currentPage === 1"
              class="w-8 h-8 rounded-lg border border-outline-variant/20 flex items-center justify-center text-secondary hover:text-emerald-500 hover:border-emerald-500/30 transition-all disabled:opacity-40 disabled:pointer-events-none"
            >
              <el-icon><ArrowLeft /></el-icon>
            </button>
            
            <button 
              v-for="p in totalPages" 
              :key="p" 
              @click="currentPage = p"
              class="w-8 h-8 rounded-lg font-bold text-xs transition-all"
              :class="currentPage === p ? 'bg-emerald-500 text-white shadow-sm' : 'border border-outline-variant/20 text-secondary hover:text-emerald-500 hover:border-emerald-500/30 bg-transparent'"
            >
              {{ p }}
            </button>
            
            <button 
              @click="currentPage < totalPages && currentPage++" 
              :disabled="currentPage === totalPages"
              class="w-8 h-8 rounded-lg border border-outline-variant/20 flex items-center justify-center text-secondary hover:text-emerald-500 hover:border-emerald-500/30 transition-all disabled:opacity-40 disabled:pointer-events-none"
            >
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧区域 (固定高度与左侧对齐，带过渡动画) -->
      <div class="xl:col-span-4 relative h-full">
        <transition name="fade-slide" mode="out-in">
          <!-- 默认占位 -->
          <div v-if="!selectedActivity" class="bg-white rounded-3xl border border-outline-variant/20 shadow-sm p-8 flex flex-col items-center justify-center h-full min-h-[400px]">
            <div class="w-20 h-20 bg-emerald-100 rounded-2xl flex items-center justify-center mb-6">
              <el-icon :size="36" class="text-emerald-500"><Timer /></el-icon>
            </div>
            <h3 class="text-lg font-bold text-on-surface mb-2">第二课堂学时发放</h3>
            <p class="text-sm text-secondary text-center leading-relaxed max-w-[240px]">
              请在左侧选择一项已发布的活动，点击「发放学时」为学生录入第二课堂学时。
            </p>
          </div>

          <!-- 活动详情 -->
          <div v-else class="bg-white rounded-3xl border border-outline-variant/20 shadow-sm overflow-hidden flex flex-col h-full relative">
            <!-- 封面图 -->
            <div class="h-[180px] w-full relative bg-surface-container-low">
              <!-- 关闭按钮 -->
              <div class="absolute top-4 right-4 z-20 flex gap-2">
                <button @click="selectedActivity = null" class="w-8 h-8 flex items-center justify-center rounded-full bg-white/20 backdrop-blur-md text-white hover:bg-white/40 transition-colors shadow-sm">
                  <el-icon><Close /></el-icon>
                </button>
              </div>
              <img v-if="selectedActivity.image && !selectedActivity.image.includes('default')" :src="selectedActivity.image" class="w-full h-full object-cover">
              <div v-else class="w-full h-full bg-gradient-to-br from-emerald-600 to-teal-800"></div>
              <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
              <div class="absolute bottom-4 left-5 pr-5 flex items-center gap-3">
                <h3 class="text-white text-xl font-bold leading-tight drop-shadow-md">{{ selectedActivity.title }}</h3>
                <span class="px-2 py-0.5 rounded text-[10px] font-bold text-white shadow-sm" :class="statusConfig[selectedActivity.status].bg">
                  {{ statusConfig[selectedActivity.status].label }}
                </span>
              </div>
            </div>

            <!-- 详情内容区 -->
            <div class="p-6 flex flex-col gap-6 overflow-y-auto flex-1">
              <div class="grid grid-cols-2 gap-y-5 gap-x-6 text-base p-1">
                <div class="col-span-2 flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">报名时间</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.enrollTime || '--' }}</span>
                </div>
                <div class="col-span-2 flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">活动时间</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.timeDetail || selectedActivity.time || '--' }}</span>
                </div>
                <div class="flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">活动级别</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.level || '--' }}</span>
                </div>
                <div class="flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">参与范围</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.range || '--' }}</span>
                </div>
                <div class="flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">活动请假</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.leave || '--' }}</span>
                </div>
                <div class="flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">活动地点</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.location || '--' }}</span>
                </div>
                <div class="col-span-2 flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">学分设置</span>
                  <div class="flex items-center gap-2">
                    <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.creditType || '--' }}</span>
                    <span class="text-[13px] text-secondary font-medium ml-1">学时 {{ (selectedActivity.hours || 0).toFixed(1) }}</span>
                  </div>
                </div>
              </div>

              <div>
                <h4 class="text-[15px] font-bold text-on-surface mb-2 flex items-center gap-1.5"><div class="w-1.5 h-4 bg-emerald-500 rounded-full"></div>活动详情</h4>
                <p class="text-[14px] text-secondary leading-relaxed text-justify">{{ selectedActivity.desc }}</p>
              </div>
            </div>

            <!-- 底部操作栏 -->
            <div class="mt-auto p-5 bg-white border-t border-outline-variant/10 flex gap-4 items-center shadow-[0_-4px_20px_-10px_rgba(0,0,0,0.05)] z-20">
              <div class="flex flex-col items-center justify-center min-w-[70px]">
                <span class="text-secondary font-medium text-xs mb-0.5">已报</span>
                <span class="font-bold text-on-surface text-[14px]">{{ selectedActivity.participants || 0 }}人</span>
              </div>
              <button v-if="grantedIds.includes(selectedActivity.id)"
                class="flex-1 py-2.5 flex items-center justify-center rounded-xl font-bold text-[14px] shadow-sm transition-all bg-gray-200 text-gray-400 cursor-default">
                已发放学时
              </button>
              <button v-else-if="selectedActivity.status !== 'completed'"
                disabled
                class="flex-1 py-2.5 flex items-center justify-center rounded-xl font-bold text-[14px] shadow-sm transition-all bg-gray-200 text-gray-400 cursor-not-allowed">
                活动未结束
              </button>
              <button v-else @click="grantHours(selectedActivity)" :disabled="submittingActivityId !== null"
                class="flex-1 py-2.5 flex items-center justify-center rounded-xl font-bold text-[14px] shadow-sm transition-all bg-emerald-500 text-white hover:bg-emerald-600 disabled:opacity-50 disabled:cursor-not-allowed gap-1.5">
                <el-icon v-if="submittingActivityId === selectedActivity.id" class="is-loading"><Loading /></el-icon>
                <span>立即发放学时</span>
              </button>
            </div>
          </div>
        </transition>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Clock, Location, Timer, Close, ArrowLeft, ArrowRight, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/youth/activities'

const activeTab = ref('ungranted')
const searchQuery = ref('')
const selectedActivity = ref(null)
const grantedIds = ref([])
const submittingActivityId = ref(null)

const currentPage = ref(1)
const pageSize = 5

// Watch tab and search query to reset pagination
watch([activeTab, searchQuery], () => {
  currentPage.value = 1
})

const tabs = [
  { label: '未发放', value: 'ungranted' },
  { label: '已发放', value: 'granted' },
]

const statusConfig = {
  'enrolling': { label: '报名中', bg: 'bg-blue-500' },
  'ongoing': { label: '进行中', bg: 'bg-green-500' },
  'completed': { label: '已完成', bg: 'bg-gray-400' },
}

const mapActivity = (a) => {
  const statusMap = { '报名中': 'enrolling', '进行中': 'ongoing', '已结束': 'completed' }
  const s = statusMap[a.status] || 'enrolling'
  return {
    id: a.id,
    status: s,
    title: a.title,
    image: a.coverImage || '',
    time: a.date || '',
    location: a.location || '',
    desc: a.description || '',
    hours: a.credits || 0,
    level: a.level,
    range: a.rangeName || '学校内',
    leave: a.leaveSupport,
    enrollLimit: a.enrollLimit,
    creditType: a.creditType,
    enrollTime: a.enrollTime,
    timeDetail: a.timeDetail,
    participants: a.participants || 0,
  }
}

const activities = ref([])

const loadActivities = async () => {
  try {
    const res = await request.get(API)
    if (res.data.code === 200) {
      activities.value = res.data.data.map(mapActivity)
      
      // Update the selectedActivity details with the latest participants count
      if (selectedActivity.value) {
        const found = activities.value.find(act => act.id === selectedActivity.value.id)
        if (found) {
          selectedActivity.value = found
        }
      }
    }
  } catch (e) { console.error('加载活动失败', e) }
}

onMounted(loadActivities)

const filteredActivities = computed(() => {
  return activities.value.filter(a => {
    const isGranted = grantedIds.value.includes(a.id)
    const matchTab = activeTab.value === 'ungranted' ? !isGranted : isGranted
    const matchSearch = !searchQuery.value ||
      a.title.includes(searchQuery.value) ||
      a.desc.includes(searchQuery.value)
    return matchTab && matchSearch
  })
})

const totalPages = computed(() => {
  return Math.ceil(filteredActivities.value.length / pageSize) || 1
})

const paginatedActivities = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredActivities.value.slice(start, start + pageSize)
})

const selectActivity = (activity) => {
  selectedActivity.value = activity
}

const grantHours = async (activity) => {
  if (submittingActivityId.value !== null) return
  if (activity.status !== 'completed') {
    ElMessage.warning('活动未结束，无法发放学时')
    return
  }
  if (activity.participants === 0) {
    ElMessage.warning('无人可发放')
    return
  }
  submittingActivityId.value = activity.id
  try {
    const res = await request.post('/api/youth/second-classroom/grant-batch', {
      activityId: activity.id,
      hours: activity.hours || 2,
      reason: `参与活动"${activity.title}"获得学时`
    })
    if (res.data.code === 200) {
      if (!grantedIds.value.includes(activity.id)) {
        grantedIds.value.push(activity.id)
      }
      ElMessage.success(`已为 ${activity.participants || 0} 名报名学生发放 ${activity.hours || 0} 学时`)
      loadActivities()
    } else {
      ElMessage.warning(res.data.msg || '发放失败')
    }
  } catch (e) {
    console.error('发放学时失败', e)
    const errorMsg = e.response?.data?.msg || '发放学时失败'
    ElMessage.error(errorMsg)
  } finally {
    submittingActivityId.value = null
  }
}
</script>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  width: 100%;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
