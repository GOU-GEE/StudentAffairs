<template>
  <div class="flex flex-col gap-6 h-full">
    <!-- 顶部统计卡片 -->
    <!-- 顶部统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg relative overflow-hidden group hover:-translate-y-1 transition-all duration-300">
        <div class="absolute top-0 right-0 w-24 h-24 bg-orange-500/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 bg-orange-100 text-orange-600 rounded-2xl flex items-center justify-center shadow-inner"><el-icon :size="24"><Timer /></el-icon></div>
          <span class="text-xs font-black text-secondary uppercase tracking-widest">今日预计晚归</span>
        </div>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-black text-on-surface">42</span>
          <span class="text-xs text-secondary font-bold">人登记</span>
        </div>
      </div>
      <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg relative overflow-hidden group hover:-translate-y-1 transition-all duration-300">
        <div class="absolute top-0 right-0 w-24 h-24 bg-red-500/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 bg-red-100 text-red-600 rounded-2xl flex items-center justify-center shadow-inner"><el-icon :size="24"><Warning /></el-icon></div>
          <span class="text-xs font-black text-secondary uppercase tracking-widest">异常未归预警</span>
        </div>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-black text-red-600">3</span>
          <span class="text-xs text-secondary font-bold">名异常</span>
        </div>
      </div>
      <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg relative overflow-hidden group hover:-translate-y-1 transition-all duration-300">
        <div class="absolute top-0 right-0 w-24 h-24 bg-blue-500/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 bg-blue-100 text-blue-600 rounded-2xl flex items-center justify-center shadow-inner"><el-icon :size="24"><Bell /></el-icon></div>
          <span class="text-xs font-black text-secondary uppercase tracking-widest">待处置事件</span>
        </div>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-black text-on-surface">12</span>
          <span class="text-xs text-secondary font-bold">件上报</span>
        </div>
      </div>
      <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg relative overflow-hidden group hover:-translate-y-1 transition-all duration-300">
        <div class="absolute top-0 right-0 w-24 h-24 bg-green-500/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 bg-green-100 text-green-600 rounded-2xl flex items-center justify-center shadow-inner"><el-icon :size="24"><CircleCheck /></el-icon></div>
          <span class="text-xs font-black text-secondary uppercase tracking-widest">校园整体状态</span>
        </div>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-black text-green-600">良好</span>
          <span class="text-xs text-secondary font-bold">受控中</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区：选项卡 -->
    <div class="flex-1 bg-white rounded-3xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <div class="px-8 pt-6 border-b border-gray-100 flex-shrink-0">
        <div class="flex items-center justify-between mb-6">
          <div class="flex gap-8">
            <button 
              v-for="tab in tabs" 
              :key="tab.id"
              @click="activeTab = tab.id"
              class="pb-3 text-sm font-bold transition-all relative"
              :class="activeTab === tab.id ? 'text-gray-900' : 'text-gray-400 hover:text-gray-600'"
            >
              {{ tab.name }}
              <div v-if="activeTab === tab.id" class="absolute bottom-0 left-0 right-0 h-1 bg-red-600 rounded-full"></div>
            </button>
          </div>
          <div class="flex items-center gap-2">
            <el-button size="small" :icon="Search">搜索</el-button>
            <el-button size="small" type="primary" :icon="Download">导出报表</el-button>
          </div>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto p-8">
        <!-- 1. 晚归监控表格 -->
        <div v-if="activeTab === 'late'" class="animate-in fade-in duration-300">
          <el-table :data="lateRecords" style="width: 100%" class="custom-table">
            <el-table-column prop="student" label="学生姓名" width="120">
              <template #default="{ row }">
                <div class="flex items-center gap-2">
                  <div class="w-7 h-7 rounded-full bg-gray-100 flex items-center justify-center text-[10px] font-bold">{{ row.student.substring(0,1) }}</div>
                  <span class="font-bold text-gray-700">{{ row.student }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="dorm" label="宿舍楼/房号" />
            <el-table-column prop="expectedTime" label="预计回寝时间" />
            <el-table-column prop="reason" label="晚归原因" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <span class="px-2 py-0.5 rounded-full text-[10px] font-black" :class="statusBadge(row.status)">
                  {{ statusLabel(row.status) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <div class="flex gap-2">
                  <el-button size="small" plain @click="contactStudent(row)">联系</el-button>
                  <el-button size="small" type="primary" plain @click="markReturned(row)">确认回寝</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 2. 突发事件处置 -->
        <div v-if="activeTab === 'emergency'" class="grid grid-cols-1 md:grid-cols-2 gap-4 animate-in fade-in duration-300">
          <div v-for="item in emergencies" :key="item.id" class="p-5 border border-gray-100 rounded-2xl hover:border-red-200 transition-all group">
            <div class="flex justify-between items-start mb-3">
              <div class="flex items-center gap-2">
                <span class="px-2 py-0.5 bg-red-50 text-red-600 rounded text-[10px] font-bold">{{ item.type }}</span>
                <span class="text-xs text-gray-400">{{ item.time }}</span>
              </div>
              <span class="text-[10px] font-black px-2 py-0.5 rounded-full" :class="item.status === 'PENDING' ? 'bg-orange-50 text-orange-600' : 'bg-blue-50 text-blue-600'">
                {{ item.status === 'PENDING' ? '待处置' : '处理中' }}
              </span>
            </div>
            <h4 class="text-sm font-bold text-gray-900 mb-2">{{ item.title }}</h4>
            <div class="flex items-center gap-4 text-xs text-gray-500 mb-4">
              <span class="flex items-center gap-1"><el-icon><User /></el-icon> {{ item.student }}</span>
              <span class="flex items-center gap-1"><el-icon><Location /></el-icon> {{ item.location }}</span>
            </div>
            <div class="flex gap-2">
              <el-button size="small" class="w-full" type="danger" plain @click="handleEmergency(item)">立即处理</el-button>
              <el-button size="small" class="w-full">转发职能部门</el-button>
            </div>
          </div>
        </div>

        <!-- 3. 隐患管理 -->
        <div v-if="activeTab === 'hazard'" class="space-y-4 animate-in fade-in duration-300">
          <div v-for="h in hazards" :key="h.id" class="flex items-center gap-6 p-4 border border-gray-100 rounded-2xl hover:bg-gray-50/50 transition-all">
            <div class="w-20 h-20 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0">
              <img :src="h.image" class="w-full h-full object-cover" />
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-3 mb-1">
                <span class="text-xs font-bold text-red-600">{{ h.level }}</span>
                <h4 class="text-sm font-bold text-gray-900 truncate">{{ h.title }}</h4>
              </div>
              <p class="text-xs text-gray-500 mb-2">{{ h.location }} · 上报于 {{ h.time }}</p>
              <div class="flex items-center gap-2">
                <span class="text-[10px] text-gray-400">已指派：后勤维修中心</span>
                <span class="text-[10px] px-1.5 py-0.5 rounded bg-blue-50 text-blue-600 font-bold">待维修</span>
              </div>
            </div>
            <div class="flex-shrink-0">
              <el-button size="small" plain :icon="Edit">更新状态</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Timer, Warning, Bell, CircleCheck, Search, Download, User, Location, Edit 
} from '@element-plus/icons-vue'

const tabs = [
  { id: 'late', name: '晚归监控' },
  { id: 'emergency', name: '突发事件处置' },
  { id: 'hazard', name: '隐患管理' }
]
const activeTab = ref('late')

const lateRecords = ref([
  { student: '张小明', dorm: '南区 8 栋 304', expectedTime: '23:30', reason: '校图书馆闭馆延时', status: 'LATE' },
  { student: '李红', dorm: '西区 2 栋 102', expectedTime: '22:45', reason: '社团彩排', status: 'RETURNED' },
  { student: '王伟', dorm: '南区 4 栋 501', expectedTime: '23:15', reason: '实验数据采集', status: 'LATE' },
  { student: '刘芳', dorm: '东区 1 栋 404', expectedTime: '23:00', reason: '家庭紧急情况', status: 'WARNING' }
])

const emergencies = ref([
  { id: 1, type: '医疗突发', title: '学生宿舍内突发急性腹痛', student: '赵四', location: '南区 5 栋 202', time: '10:15', status: 'PENDING' },
  { id: 2, type: '校园安全', title: '西区食堂门口发生口角纠纷', student: '匿名上报', location: '西区食堂', time: '11:30', status: 'PROCESSING' }
])

const hazards = ref([
  { id: 1, level: '高危', title: '三教 2 楼走廊吊顶脱落', location: '第三教学楼', time: '05-11 14:00', image: 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?q=80&w=200&auto=format&fit=crop' },
  { id: 2, level: '一般', title: '篮球场北侧围栏破损', location: '西区操场', time: '05-10 09:20', image: 'https://images.unsplash.com/photo-1544919396-1055745f448c?q=80&w=200&auto=format&fit=crop' }
])

onMounted(() => {
  const allReports = JSON.parse(localStorage.getItem('safety_reports') || '[]')
  allReports.forEach(report => {
    if (report.type === 'late') {
      lateRecords.value.unshift({
        student: report.student,
        dorm: report.dorm,
        expectedTime: report.title.match(/预计 (.*) 返回/)?.[1] || '待定',
        reason: report.title,
        status: 'LATE'
      })
    } else if (report.type === 'emergency') {
      emergencies.value.unshift({
        id: report.id,
        type: '实时报备',
        title: report.title,
        student: report.student,
        location: report.title.split(' - ')[0] || '未知',
        time: '刚刚',
        status: 'PENDING'
      })
    }
  })
})

const statusLabel = (s) => ({
  LATE: '待回寝',
  RETURNED: '已回寝',
  WARNING: '异常预警'
}[s] || s)

const statusBadge = (s) => ({
  LATE: 'bg-orange-50 text-orange-600',
  RETURNED: 'bg-green-50 text-green-700',
  WARNING: 'bg-red-50 text-red-600 animate-pulse'
}[s] || 'bg-gray-100 text-gray-500')

const contactStudent = (row) => {
  ElMessage.info(`正在拨打 ${row.student} 的电话...`)
}

const markReturned = (row) => {
  row.status = 'RETURNED'
  ElMessage.success(`${row.student} 已确认回寝`)
}

const handleEmergency = (item) => {
  ElMessageBox.prompt('请输入初步处置意见', '处理事件', {
    confirmButtonText: '提交',
    cancelButtonText: '取消'
  }).then(({ value }) => {
    item.status = 'PROCESSING'
    ElMessage.success('处置意见已提交，状态更新为：处理中')
  })
}
</script>

<style scoped>
.custom-table {
  --el-table-border-color: #f3f4f6;
  --el-table-header-bg-color: #f9fafb;
}
</style>
