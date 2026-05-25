<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧预约列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <div class="p-3 border-b border-outline-variant/15 flex items-center gap-2">
        <el-select v-model="filterStatus" placeholder="状态筛选" class="flex-1" clearable @change="loadAppointments">
          <el-option label="待确认" value="PENDING" />
          <el-option label="已接受" value="ACCEPTED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
      </div>
      <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
        <div v-for="a in filteredAppointments" :key="a.id"
             @click="selectAppointment(a)"
             class="p-3 rounded-xl cursor-pointer transition-all border"
             :class="activeId === a.id ? 'bg-white shadow-sm border-primary/30 ring-1 ring-primary/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
          <div class="flex items-center justify-between mb-1">
            <span class="font-bold text-sm text-on-surface">{{ a.studentName }}</span>
            <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="statusClass(a.status)">{{ statusText(a.status) }}</span>
          </div>
          <p class="text-xs text-secondary truncate">{{ a.appointmentTime }}</p>
        </div>
        <div v-if="filteredAppointments.length === 0" class="py-10 text-center text-secondary text-sm">暂无预约</div>
      </div>
    </div>

    <!-- 右侧详情面板 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col">
      <div v-if="active" class="flex flex-col h-full">
        <div class="px-6 py-4 border-b border-outline-variant/30 bg-white/40 flex items-center justify-between">
          <div>
            <h3 class="font-bold text-base text-on-surface">{{ active.studentName }}</h3>
            <p class="text-xs text-secondary mt-0.5">学号：{{ active.studentId }} · {{ active.appointmentTime }}</p>
          </div>
          <div class="flex items-center gap-2">
            <button v-if="active.status === 'PENDING'" @click="handleAccept" :disabled="submitting" class="px-4 py-2 bg-green-500 text-white rounded-lg text-sm font-bold hover:bg-green-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1.5">
              <el-icon v-if="submitting && submittingType === 'ACCEPT'" class="is-loading"><Loading /></el-icon>
              <span>接受预约</span>
            </button>
            <button v-if="active.status === 'PENDING'" @click="handleReject" :disabled="submitting" class="px-4 py-2 bg-red-500 text-white rounded-lg text-sm font-bold hover:bg-red-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1.5">
              <el-icon v-if="submitting && submittingType === 'REJECT'" class="is-loading"><Loading /></el-icon>
              <span>拒绝</span>
            </button>
            <span v-if="active.status === 'ACCEPTED'" class="text-sm font-bold text-green-600">已接受</span>
            <span v-if="active.status === 'REJECTED'" class="text-sm font-bold text-red-500">已拒绝</span>
          </div>
        </div>
        <div class="flex-1 overflow-y-auto p-6 space-y-4 custom-scrollbar">
          <div class="bg-surface-container-low rounded-xl p-4">
            <p class="text-xs font-bold text-secondary uppercase mb-2">预约信息</p>
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div><span class="text-secondary">学生姓名：</span>{{ active.studentName }}</div>
              <div><span class="text-secondary">学号：</span>{{ active.studentId }}</div>
              <div><span class="text-secondary">预约时间：</span>{{ active.appointmentTime }}</div>
              <div><span class="text-secondary">提交时间：</span>{{ formatTime(active.createTime) }}</div>
            </div>
          </div>
          <div class="bg-surface-container-low rounded-xl p-4">
            <p class="text-xs font-bold text-secondary uppercase mb-2">咨询原因</p>
            <p class="text-sm leading-relaxed whitespace-pre-wrap">{{ active.reason || '未填写' }}</p>
          </div>
        </div>
      </div>
      <div v-else class="flex flex-col items-center justify-center h-full text-secondary">
        <el-icon :size="64" class="mb-4 opacity-20"><Calendar /></el-icon>
        <p class="text-lg font-medium text-on-surface">职业规划预约管理</p>
        <p class="text-sm mt-2 opacity-80">选择左侧预约查看详情</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/career'
const TEACHER_ID = sessionStorage.getItem('userId') || 'T001'

const filterStatus = ref('')
const activeId = ref(null)
const appointments = ref([])
const submitting = ref(false)
const submittingType = ref('')

const loadAppointments = async () => {
  try {
    const res = await request.get(`${API}/appointments/teacher/${TEACHER_ID}`)
    if (res.data.code === 200) appointments.value = res.data.data
  } catch (e) { console.error(e) }
}

const filteredAppointments = computed(() => {
  if (!filterStatus.value) return appointments.value
  return appointments.value.filter(a => a.status === filterStatus.value)
})

const active = computed(() => appointments.value.find(a => a.id === activeId.value) || null)

const selectAppointment = (a) => { activeId.value = a.id }

const handleAccept = async () => {
  if (submitting.value) return
  submitting.value = true
  submittingType.value = 'ACCEPT'
  try {
    const res = await request.put(`${API}/appointments/${activeId.value}/accept`)
    if (res.data.code === 200) { ElMessage.success('已接受预约'); loadAppointments() }
  } catch (e) { ElMessage.error('操作失败') }
  finally {
    submitting.value = false
    submittingType.value = ''
  }
}

const handleReject = async () => {
  if (submitting.value) return
  submitting.value = true
  submittingType.value = 'REJECT'
  try {
    const res = await request.put(`${API}/appointments/${activeId.value}/reject`)
    if (res.data.code === 200) { ElMessage.success('已拒绝预约'); loadAppointments() }
  } catch (e) { ElMessage.error('操作失败') }
  finally {
    submitting.value = false
    submittingType.value = ''
  }
}

const formatTime = (s) => {
  if (!s) return ''
  return new Date(s).toLocaleString('zh-CN', { year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit' })
}

const statusClass = (s) => ({ PENDING: 'bg-amber-100 text-amber-700', ACCEPTED: 'bg-green-100 text-green-700', REJECTED: 'bg-red-100 text-red-700' }[s]||'')
const statusText = (s) => ({ PENDING: '待确认', ACCEPTED: '已接受', REJECTED: '已拒绝' }[s]||s)

onMounted(loadAppointments)
</script>
