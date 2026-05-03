<template>
  <div class="h-full flex flex-col">
    <div class="mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <h2 class="text-[3.5rem] leading-none font-bold tracking-[-0.02em] text-on-surface mb-2">校园安全预警</h2>
        <p class="text-[0.875rem] text-secondary font-medium">Campus Safety Monitoring & Incident Management</p>
      </div>
      <button @click="showNewDialog = true"
              class="bg-error text-white hover:bg-error/80 rounded-md px-5 py-2.5 text-[0.875rem] font-semibold flex items-center gap-2 shadow-md">
        <el-icon><Plus /></el-icon>
        上报新事件
      </button>
    </div>

    <!-- Stats Row -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/15 text-center">
        <p class="text-3xl font-bold text-on-surface">{{ stats.total || 0 }}</p>
        <p class="text-xs text-secondary font-semibold uppercase tracking-wider mt-1">事件总数</p>
      </div>
      <div class="bg-error-container rounded-xl p-5 border border-error/15 text-center">
        <p class="text-3xl font-bold text-error">{{ stats.open || 0 }}</p>
        <p class="text-xs text-error font-semibold uppercase tracking-wider mt-1">未处理</p>
      </div>
      <div class="bg-orange-50 rounded-xl p-5 border border-orange-200 text-center">
        <p class="text-3xl font-bold text-orange-600">{{ stats.processing || 0 }}</p>
        <p class="text-xs text-orange-600 font-semibold uppercase tracking-wider mt-1">处理中</p>
      </div>
      <div class="bg-green-50 rounded-xl p-5 border border-green-200 text-center">
        <p class="text-3xl font-bold text-green-600">{{ stats.closed || 0 }}</p>
        <p class="text-xs text-green-600 font-semibold uppercase tracking-wider mt-1">已结案</p>
      </div>
    </div>

    <!-- Incident Timeline -->
    <div class="flex-1 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] overflow-hidden flex flex-col">
      <h3 class="text-lg font-semibold text-on-surface tracking-tight mb-5">事件台账 (按时间降序)</h3>
      <div class="flex-1 overflow-y-auto space-y-4 custom-scrollbar pr-2">
        <div v-for="(inc, i) in incidents" :key="i" class="flex gap-4 group">
          <!-- Timeline dot -->
          <div class="flex flex-col items-center">
            <div class="w-4 h-4 rounded-full border-2 mt-1 flex-shrink-0"
                 :class="inc.level === 'HIGH' ? 'bg-error border-error' : inc.level === 'MEDIUM' ? 'bg-orange-400 border-orange-400' : 'bg-green-500 border-green-500'"></div>
            <div class="w-0.5 bg-outline-variant/20 flex-1 mt-1" v-if="i < incidents.length - 1"></div>
          </div>
          <!-- Card -->
          <div class="flex-1 mb-4 bg-surface border rounded-xl p-4 group-hover:border-primary/30 transition-colors"
               :class="inc.level === 'HIGH' ? 'border-error/20' : 'border-outline-variant/20'">
            <div class="flex justify-between items-start gap-2 mb-2 flex-wrap">
              <div class="flex items-center gap-2 flex-wrap">
                <span class="font-bold text-sm text-on-surface">{{ inc.studentName || '匿名' }}</span>
                <span class="text-xs px-2 py-0.5 rounded-full font-bold"
                      :class="levelStyle(inc.level)">{{ levelLabel(inc.level) }}</span>
                <span class="text-xs px-2 py-0.5 rounded font-semibold bg-surface-container-high text-secondary border border-outline-variant/20">{{ typeLabel(inc.type) }}</span>
              </div>
              <div class="flex items-center gap-2">
                <span class="text-xs text-outline">{{ formatDate(inc.reportTime) }}</span>
                <el-select v-model="inc.status" size="small" style="width: 100px"
                           @change="updateStatus(inc.id, inc.status)">
                  <el-option label="未处理" value="OPEN" />
                  <el-option label="处理中" value="PROCESSING" />
                  <el-option label="已结案" value="CLOSED" />
                </el-select>
              </div>
            </div>
            <p class="text-sm text-secondary leading-relaxed">{{ inc.description }}</p>
            <p v-if="inc.handlerName" class="text-xs text-outline mt-2">处理人: {{ inc.handlerName }}</p>
          </div>
        </div>
        <div v-if="incidents.length === 0" class="py-16 flex flex-col items-center text-secondary">
          <el-icon :size="48" class="mb-2 opacity-30"><CircleCheck /></el-icon>
          <p>暂无事件记录</p>
        </div>
      </div>
    </div>

    <!-- New Incident Dialog -->
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
              <el-option label="🔴 高级" value="HIGH" />
              <el-option label="🟡 中级" value="MEDIUM" />
              <el-option label="🟢 低级" value="LOW" />
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
import { Plus, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const incidents = ref([])
const stats = ref({})
const showNewDialog = ref(false)
const newIncident = ref({ studentName: '', type: 'MENTAL', level: 'MEDIUM', description: '', handlerName: '' })

const API = 'http://localhost:8080/api/safety'

const loadData = async () => {
  try {
    const [incRes, statRes] = await Promise.all([
      axios.get(`${API}/incidents`),
      axios.get(`${API}/stats`)
    ])
    if (incRes.data.code === 200) incidents.value = incRes.data.data
    if (statRes.data.code === 200) stats.value = statRes.data.data
  } catch (e) { console.error(e) }
}

const updateStatus = async (id, status) => {
  try {
    await axios.put(`${API}/incidents/${id}/status`, { status })
    ElMessage.success('状态已更新')
    await loadData()
  } catch (e) { ElMessage.error('更新失败') }
}

const submitIncident = async () => {
  try {
    await axios.post(`${API}/incidents`, newIncident.value)
    ElMessage.success('事件已上报')
    showNewDialog.value = false
    newIncident.value = { studentName: '', type: 'MENTAL', level: 'MEDIUM', description: '', handlerName: '' }
    await loadData()
  } catch (e) { ElMessage.error('上报失败') }
}

const levelStyle = (l) => l === 'HIGH' ? 'bg-error-container text-error border border-error/20' : l === 'MEDIUM' ? 'bg-orange-100 text-orange-600 border border-orange-200' : 'bg-green-100 text-green-700 border border-green-200'
const levelLabel = (l) => l === 'HIGH' ? '高级预警' : l === 'MEDIUM' ? '中级预警' : '低级预警'
const typeLabel = (t) => ({ MENTAL: '心理健康', MEDICAL: '医疗紧急', FIGHT: '打架斗殴', MISSING: '失踪走失', VIOLATION: '违规违纪' }[t] || t)
const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

onMounted(loadData)
</script>
