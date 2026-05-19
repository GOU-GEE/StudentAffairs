<template>
  <div class="flex flex-col gap-6 h-full">
    <!-- 顶部统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg relative overflow-hidden group hover:-translate-y-1 transition-all duration-300">
        <div class="absolute top-0 right-0 w-24 h-24 bg-red-500/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 bg-red-100 text-red-600 rounded-2xl flex items-center justify-center shadow-inner"><el-icon :size="24"><Warning /></el-icon></div>
          <span class="text-xs font-black text-secondary uppercase tracking-widest">未处理事件</span>
        </div>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-black text-red-600">{{ stats.open || 0 }}</span>
          <span class="text-xs text-secondary font-bold">件待处理</span>
        </div>
      </div>
      <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg relative overflow-hidden group hover:-translate-y-1 transition-all duration-300">
        <div class="absolute top-0 right-0 w-24 h-24 bg-orange-500/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 bg-orange-100 text-orange-600 rounded-2xl flex items-center justify-center shadow-inner"><el-icon :size="24"><Timer /></el-icon></div>
          <span class="text-xs font-black text-secondary uppercase tracking-widest">处理中</span>
        </div>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-black text-on-surface">{{ stats.processing || 0 }}</span>
          <span class="text-xs text-secondary font-bold">件进行中</span>
        </div>
      </div>
      <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg relative overflow-hidden group hover:-translate-y-1 transition-all duration-300">
        <div class="absolute top-0 right-0 w-24 h-24 bg-green-500/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 bg-green-100 text-green-600 rounded-2xl flex items-center justify-center shadow-inner"><el-icon :size="24"><CircleCheck /></el-icon></div>
          <span class="text-xs font-black text-secondary uppercase tracking-widest">已结案</span>
        </div>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-black text-green-600">{{ stats.closed || 0 }}</span>
          <span class="text-xs text-secondary font-bold">件已处理</span>
        </div>
      </div>
      <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg relative overflow-hidden group hover:-translate-y-1 transition-all duration-300">
        <div class="absolute top-0 right-0 w-24 h-24 bg-blue-500/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 bg-blue-100 text-blue-600 rounded-2xl flex items-center justify-center shadow-inner"><el-icon :size="24"><Bell /></el-icon></div>
          <span class="text-xs font-black text-secondary uppercase tracking-widest">事件总数</span>
        </div>
        <div class="flex items-baseline gap-2">
          <span class="text-3xl font-black text-on-surface">{{ stats.total || 0 }}</span>
          <span class="text-xs text-secondary font-bold">件上报</span>
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
          <button @click="showNewDialog = true"
                  class="bg-error text-white hover:bg-error/80 rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
            <el-icon><Plus /></el-icon>上报新事件
          </button>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto p-8">
        <!-- 突发事件处置（接入后端） -->
        <div v-if="activeTab === 'emergency'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div v-for="inc in incidents" :key="inc.id" class="p-5 border border-gray-100 rounded-2xl hover:border-red-200 transition-all group">
            <div class="flex justify-between items-start mb-3">
              <div class="flex items-center gap-2">
                <span class="px-2 py-0.5 rounded text-[10px] font-bold"
                      :class="inc.level === 'HIGH' ? 'bg-red-50 text-red-600' : inc.level === 'MEDIUM' ? 'bg-orange-50 text-orange-600' : 'bg-green-50 text-green-700'">
                  {{ typeLabel(inc.type) }}
                </span>
                <span class="text-xs text-gray-400">{{ formatDate(inc.reportTime) }}</span>
              </div>
              <el-select v-model="inc.status" size="small" style="width: 100px"
                         @change="updateStatus(inc.id, inc.status)">
                <el-option label="未处理" value="OPEN" />
                <el-option label="处理中" value="PROCESSING" />
                <el-option label="已结案" value="CLOSED" />
              </el-select>
            </div>
            <h4 class="text-sm font-bold text-gray-900 mb-2">{{ inc.studentName || '匿名' }} - {{ levelLabel(inc.level) }}</h4>
            <p class="text-xs text-gray-500 mb-2">{{ inc.description }}</p>
            <p v-if="inc.handlerName" class="text-xs text-gray-400">处理人: {{ inc.handlerName }}</p>
          </div>
          <div v-if="incidents.length === 0" class="col-span-2 py-10 text-center text-gray-400">
            暂无事件记录
          </div>
        </div>
      </div>
    </div>

    <!-- 新事件弹窗 -->
    <el-dialog v-model="showNewDialog" title="上报新安全事件" width="480px" align-center>
      <div class="space-y-4 p-2">
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">学生姓名</label>
          <el-input v-model="newIncident.studentName" placeholder="涉事学生姓名（可选）" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">事件类型</label>
            <el-select v-model="newIncident.type" style="width: 100%">
              <el-option label="心理健康" value="MENTAL" />
              <el-option label="医疗紧急" value="MEDICAL" />
              <el-option label="打架斗殴" value="FIGHT" />
              <el-option label="失踪走失" value="MISSING" />
              <el-option label="违规违纪" value="VIOLATION" />
            </el-select>
          </div>
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">预警级别</label>
            <el-select v-model="newIncident.level" style="width: 100%">
              <el-option label="高级" value="HIGH" />
              <el-option label="中级" value="MEDIUM" />
              <el-option label="低级" value="LOW" />
            </el-select>
          </div>
        </div>
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">事件描述</label>
          <el-input v-model="newIncident.description" type="textarea" :rows="3" placeholder="请详细描述事件经过..." />
        </div>
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">处理人</label>
          <el-input v-model="newIncident.handlerName" placeholder="经办人姓名" />
        </div>
      </div>
      <template #footer>
        <el-button @click="showNewDialog = false">取消</el-button>
        <el-button type="primary" @click="submitIncident">提交上报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Timer, Warning, Bell, CircleCheck, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/safety'

const tabs = [
  { id: 'emergency', name: '突发事件处置' }
]
const activeTab = ref('emergency')

const incidents = ref([])
const stats = ref({})
const showNewDialog = ref(false)
const newIncident = ref({ studentName: '', type: 'MENTAL', level: 'MEDIUM', description: '', handlerName: '' })

const loadData = async () => {
  try {
    const [incRes, statRes] = await Promise.all([
      request.get(`${API}/incidents`),
      request.get(`${API}/stats`)
    ])
    if (incRes.data.code === 200) incidents.value = incRes.data.data
    if (statRes.data.code === 200) stats.value = statRes.data.data
  } catch (e) { console.error(e) }
}

const updateStatus = async (id, status) => {
  try {
    await request.put(`${API}/incidents/${id}/status`, { status })
    ElMessage.success('状态已更新')
    await loadData()
  } catch (e) { ElMessage.error('更新失败') }
}

const submitIncident = async () => {
  try {
    await request.post(`${API}/incidents`, newIncident.value)
    ElMessage.success('事件已上报')
    showNewDialog.value = false
    newIncident.value = { studentName: '', type: 'MENTAL', level: 'MEDIUM', description: '', handlerName: '' }
    await loadData()
  } catch (e) { ElMessage.error('上报失败') }
}

const levelLabel = (l) => l === 'HIGH' ? '高级预警' : l === 'MEDIUM' ? '中级预警' : '低级预警'
const typeLabel = (t) => ({ MENTAL: '心理健康', MEDICAL: '医疗紧急', FIGHT: '打架斗殴', MISSING: '失踪走失', VIOLATION: '违规违纪' }[t] || t)
const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

onMounted(loadData)
</script>
