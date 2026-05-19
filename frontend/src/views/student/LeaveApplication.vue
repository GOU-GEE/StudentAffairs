<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
    <!-- 左侧：填写请假申请 (col-span-2) -->
    <div class="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <!-- 卡片标题 -->
      <div class="flex items-center gap-3 px-6 pt-6 pb-4 border-b border-gray-100 flex-shrink-0">
        <h3 class="text-base font-bold text-gray-900">填写请假申请</h3>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5 space-y-5">
        <!-- 请假须知折叠面板 -->
        <div class="border border-red-200 rounded-xl overflow-hidden">
          <button
            @click="noticeExpanded = !noticeExpanded"
            class="w-full flex items-center justify-between px-4 py-3 bg-red-50 text-left"
          >
            <div class="flex items-center gap-2">
              <el-icon class="text-red-500" :size="16"><Warning /></el-icon>
              <span class="text-sm font-semibold text-red-600">请假须知 <span class="font-normal underline underline-offset-2">（请仔细阅读）</span></span>
            </div>
            <el-icon
              class="text-red-400 transition-transform duration-200"
              :class="noticeExpanded ? 'rotate-180' : ''"
              :size="14"
            ><ArrowDown /></el-icon>
          </button>
          <transition name="notice-expand">
            <div v-if="noticeExpanded" class="px-4 py-3 bg-white border-t border-red-100 space-y-1.5">
              <p v-for="item in noticeItems" :key="item" class="text-xs text-gray-600 flex items-start gap-2">
                <span class="text-red-400 mt-0.5 flex-shrink-0">•</span>{{ item }}
              </p>
            </div>
          </transition>
        </div>

        <!-- 请假类型 -->
        <div>
          <label class="text-sm font-semibold text-gray-700 block mb-2">请假类型</label>
          <div class="flex gap-2 flex-wrap">
            <button
              v-for="lt in leaveTypes"
              :key="lt.value"
              @click="leaveForm.leaveType = lt.value"
              class="px-4 py-2 rounded-lg text-sm font-medium border transition-all"
              :class="leaveForm.leaveType === lt.value
                ? 'bg-[#409eff] text-white border-[#409eff]'
                : 'bg-white text-gray-600 border-gray-200 hover:border-gray-400'"
            >
              {{ lt.label }}
            </button>
          </div>
        </div>

        <!-- 请假时间 -->
        <div>
          <label class="text-sm font-semibold text-gray-700 block mb-2">请假时间</label>
          <div class="flex items-center gap-3">
            <div class="flex-1 flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2.5 bg-white hover:border-gray-400 transition-colors">
              <el-date-picker
                v-model="leaveForm.startDate"
                type="date"
                placeholder="开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :style="{ border: 'none', boxShadow: 'none', padding: 0 }"
                class="leave-date-picker"
              />
            </div>
            <el-icon class="text-gray-400 flex-shrink-0" :size="14"><ArrowRight /></el-icon>
            <div class="flex-1 flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2.5 bg-white hover:border-gray-400 transition-colors">
              <el-date-picker
                v-model="leaveForm.endDate"
                type="date"
                placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                class="leave-date-picker"
              />
            </div>
            <span v-if="leaveDays > 0" class="text-sm font-bold text-gray-500 whitespace-nowrap flex-shrink-0">
              共 {{ leaveDays }} 天
            </span>
          </div>
        </div>

        <!-- 前往地点 -->
        <div>
          <label class="text-sm font-semibold text-gray-700 block mb-2">前往地点</label>
          <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2.5 bg-white hover:border-gray-400 focus-within:border-gray-500 transition-colors">
            <el-icon class="text-gray-400 flex-shrink-0" :size="15"><Location /></el-icon>
            <input
              v-model="leaveForm.destination"
              class="flex-1 text-sm outline-none bg-transparent placeholder-gray-300 text-gray-700"
              placeholder="如：广州市天河区·家庭住址"
            />
          </div>
        </div>

        <!-- 请假事由 -->
        <div>
          <label class="text-sm font-semibold text-gray-700 block mb-2">请假事由（详细说明）</label>
          <div class="border border-gray-200 rounded-xl overflow-hidden hover:border-gray-400 focus-within:border-gray-500 transition-colors">
            <textarea
              v-model="leaveForm.reason"
              class="w-full text-sm outline-none bg-white px-3 py-2.5 placeholder-gray-300 text-gray-700 resize-none"
              rows="4"
              placeholder="请详细说明请假原因，辅导员将依据此进行审批..."
              maxlength="200"
            ></textarea>
            <div class="px-3 pb-2 text-right text-xs text-gray-300">
              {{ leaveForm.reason.length }} / 200
            </div>
          </div>
        </div>

        <!-- 紧急联系方式 -->
        <div>
          <label class="text-sm font-semibold text-gray-700 block mb-2">紧急联系方式</label>
          <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2.5 bg-white hover:border-gray-400 focus-within:border-gray-500 transition-colors">
            <el-icon class="text-gray-400 flex-shrink-0" :size="15"><Phone /></el-icon>
            <input
              v-model="leaveForm.contact"
              class="flex-1 text-sm outline-none bg-transparent placeholder-gray-300 text-gray-700"
              placeholder="假期期间可联系到您的手机号"
            />
          </div>
        </div>
      </div>

        <div class="px-6 py-4 border-t border-gray-100 flex-shrink-0 flex items-center justify-between gap-4">
          <div class="flex items-start gap-2 p-3 bg-orange-50 border border-orange-100 rounded-xl flex-1">
            <el-icon class="text-orange-500 mt-0.5 flex-shrink-0" :size="15"><InfoFilled /></el-icon>
            <p class="text-xs text-orange-700 leading-relaxed">
              请假批准后方可离校，请确保返校后及时向辅导员进行<strong>销假</strong>。超时未归视为旷课处理。
            </p>
          </div>
          <button
            @click="submitLeave"
            :disabled="submitting"
            class="px-8 py-2.5 bg-[#409eff] text-white rounded-xl font-bold text-sm hover:bg-[#409eff]/80 transition-colors shadow-sm disabled:opacity-50 whitespace-nowrap"
          >
            {{ submitting ? '提交中...' : '提交请假申请' }}
          </button>
        </div>
      </div>

    <!-- 右侧：历史请假记录 -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <!-- 卡片标题 -->
      <div class="flex items-center gap-3 px-6 pt-6 pb-4 border-b border-gray-100 flex-shrink-0">
        <el-icon class="text-gray-400" :size="18"><Clock /></el-icon>
        <h3 class="text-base font-bold text-gray-900">历史请假记录</h3>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5">
          <div class="space-y-4">
            <div
              v-for="(r, i) in leaveHistory"
              :key="r.id"
              class="relative"
            >
              <!-- 卡片内容 -->
              <div
                class="border rounded-xl p-4 transition-all"
                :class="cardStyle(r.status)"
              >
                <div class="flex items-start justify-between gap-2">
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="font-bold text-sm text-gray-900">{{ leaveTypeLabel(r.leaveType) }}</span>
                    </div>
                    <p class="text-xs text-gray-500">
                      {{ r.startDate }} 至 {{ r.endDate }}
                      <span class="ml-1 text-gray-400">（共{{ calcDays(r.startDate, r.endDate) }}天）</span>
                    </p>
                    <p class="text-xs text-gray-500 mt-1">前往：{{ r.destination }}</p>
                    <p class="text-xs text-gray-500 mt-1">事由：{{ r.reason }}</p>
                    <div v-if="r.reviewComment" class="mt-2 pt-2 border-t border-gray-100">
                      <p class="text-xs text-gray-700">
                        <span class="font-semibold">辅导员批注：</span>{{ r.reviewComment }}
                      </p>
                    </div>
                  </div>
                  <!-- 状态徽标 -->
                  <span
                    class="flex-shrink-0 text-xs font-bold px-2.5 py-1 rounded-full border"
                    :class="statusBadgeStyle(r.status)"
                  >{{ leaveStatusLabel(r.status) }}</span>
                </div>

                <!-- 销假按钮 -->
                <button
                  v-if="r.status === 'PENDING_RETURN'"
                  @click="checkIn(r.id)"
                  class="mt-3 w-full py-2 border-2 border-orange-400 text-orange-600 rounded-lg text-xs font-bold hover:bg-orange-50 transition-colors"
                >
                  确认销假
                </button>
              </div>
            </div>
          </div>

        <!-- 查看更多 -->
        <button class="mt-5 w-full py-2.5 text-xs text-gray-400 hover:text-gray-600 transition-colors flex items-center justify-center gap-1">
          查看更多记录
          <el-icon :size="12"><ArrowDown /></el-icon>
        </button>

        <!-- 空状态 -->
        <div v-if="leaveHistory.length === 0" class="py-16 flex flex-col items-center text-gray-400">
          <span class="text-4xl mb-3">📋</span>
          <p class="text-sm">暂无请假记录</p>
        </div>
      </div>
    </div>

    <!-- 底部说明 -->
    <div class="lg:col-span-2 flex items-center justify-center gap-2 text-xs text-gray-400 pb-2">
      <el-icon :size="12"><Lock /></el-icon>
      本系统数据严格保密，仅用于请假审批流程
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import {
  EditPen, Warning, ArrowDown, ArrowRight, Calendar, Location,
  Phone, InfoFilled, Clock, Lock
} from '@element-plus/icons-vue'

const API = '/api/applications'
const studentId = sessionStorage.getItem('userId') || '202301042'
const studentName = '张小明'

// 请假须知
const noticeExpanded = ref(true)
const noticeItems = [
  '必须按时返校销假，逾期未归按旷课处理',
  '外出途中注意安全，加强自我保护',
  '遵守学校纪律要求，愿意承担相关责任',
  '与学校保持联系，确保按时返校'
]

// 请假类型
const leaveTypes = [
  { label: '事假', value: 'PERSONAL' },
  { label: '病假', value: 'SICK' },
  { label: '公假', value: 'OFFICIAL' },
  { label: '家庭急事', value: 'FAMILY' },
  { label: '返乡', value: 'HOMETOWN' },
  { label: '其他', value: 'OTHER' }
]

const leaveForm = ref({
  leaveType: 'PERSONAL',
  startDate: '',
  endDate: '',
  destination: '',
  reason: '',
  contact: ''
})

const submitting = ref(false)

// 计算请假天数
const leaveDays = computed(() => {
  if (!leaveForm.value.startDate || !leaveForm.value.endDate) return 0
  const diff = new Date(leaveForm.value.endDate) - new Date(leaveForm.value.startDate)
  return Math.max(1, Math.round(diff / 86400000) + 1)
})

const calcDays = (start, end) => {
  if (!start || !end) return 0
  const diff = new Date(end) - new Date(start)
  return Math.max(1, Math.round(diff / 86400000) + 1)
}

const leaveHistory = ref([])

const loadHistory = async () => {
  try {
    const res = await request.get(`${API}/student/${studentId}`)
    if (res.data.code === 200) {
      leaveHistory.value = res.data.data
        .filter(item => item.type === 'LEAVE')
        .map(item => {
          let detail = {}
          try { detail = JSON.parse(item.reason) } catch (e) {}
          return {
            id: item.id,
            leaveType: detail.leaveType || 'OTHER',
            startDate: detail.startDate || '',
            endDate: detail.endDate || '',
            destination: detail.destination || '',
            reason: detail.reason || item.reason,
            status: item.status === 'APPROVED' ? 'ON_LEAVE' : item.status, // Map APPROVED to ON_LEAVE for frontend logic
            reviewComment: item.reviewComment
          }
        })
    }
  } catch (e) {
    console.error('Failed to load leave history', e)
  }
}

onMounted(() => {
  loadHistory()
})

const checkIn = (id) => {
  const item = leaveHistory.value.find(r => r.id === id)
  if (item) {
    item.status = 'RETURNED'
    ElMessage.success('销假成功，欢迎回来！')
  }
}

const submitLeave = async () => {
  if (!leaveForm.value.startDate || !leaveForm.value.endDate) {
    ElMessage.warning('请选择请假时间'); return
  }
  if (!leaveForm.value.reason.trim()) {
    ElMessage.warning('请填写请假事由'); return
  }
  submitting.value = true
  try {
    const payload = {
      studentId,
      studentName,
      type: 'LEAVE',
      title: `请假申请 - ${leaveTypeLabel(leaveForm.value.leaveType)}`,
      reason: JSON.stringify({
        leaveType: leaveForm.value.leaveType,
        startDate: leaveForm.value.startDate,
        endDate: leaveForm.value.endDate,
        destination: leaveForm.value.destination,
        reason: leaveForm.value.reason,
        contact: leaveForm.value.contact
      })
    }
    const res = await request.post(API, payload)
    if (res.data.code === 200) {
      ElMessage.success('请假申请已提交，请等待辅导员审批')
      leaveForm.value = { leaveType: 'PERSONAL', startDate: '', endDate: '', destination: '', reason: '', contact: '' }
      loadHistory()
    } else {
      ElMessage.error(res.data.msg || '提交失败')
    }
  } catch (e) {
    ElMessage.error('请求异常，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 样式方法
const leaveTypeLabel = (t) => ({
  PERSONAL: '事假', SICK: '病假', OFFICIAL: '公假',
  FAMILY: '家庭急事', HOMETOWN: '返乡', OTHER: '其他'
}[t] || t)

const leaveStatusLabel = (s) => ({
  PENDING: '审核中', ON_LEAVE: '休假中',
  PENDING_RETURN: '待销假', RETURNED: '已销假', REJECTED: '已驳回'
}[s] || s)

const dotStyle = (s) => ({
  PENDING: 'border-blue-300 bg-white',
  ON_LEAVE: 'border-green-400 bg-white',
  PENDING_RETURN: 'border-orange-400 bg-white',
  RETURNED: 'border-gray-300 bg-white',
  REJECTED: 'border-red-300 bg-white'
}[s] || 'border-gray-300 bg-white')

const dotInnerStyle = (s) => ({
  PENDING: 'bg-blue-400',
  ON_LEAVE: 'bg-green-500',
  PENDING_RETURN: 'bg-orange-500',
  RETURNED: 'bg-gray-400',
  REJECTED: 'bg-red-400'
}[s] || 'bg-gray-400')

const cardStyle = (s) => ({
  PENDING: 'border-blue-100 bg-blue-50/40',
  ON_LEAVE: 'border-green-100 bg-green-50/40',
  PENDING_RETURN: 'border-orange-100 bg-orange-50/40',
  RETURNED: 'border-gray-100 bg-white',
  REJECTED: 'border-red-100 bg-red-50/40'
}[s] || 'border-gray-100 bg-white')

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-blue-50 text-blue-600 border-blue-200',
  ON_LEAVE: 'bg-green-50 text-green-700 border-green-300',
  PENDING_RETURN: 'bg-orange-50 text-orange-600 border-orange-300',
  RETURNED: 'bg-gray-100 text-gray-500 border-gray-200',
  REJECTED: 'bg-red-50 text-red-600 border-red-200'
}[s] || 'bg-gray-100 text-gray-500 border-gray-200')
</script>

<style scoped>
.notice-expand-enter-active,
.notice-expand-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}
.notice-expand-enter-from,
.notice-expand-leave-to {
  max-height: 0;
  opacity: 0;
}
.notice-expand-enter-to,
.notice-expand-leave-from {
  max-height: 300px;
  opacity: 1;
}

/* 覆盖 el-date-picker 边框样式 */
:deep(.leave-date-picker .el-input__wrapper) {
  box-shadow: none !important;
  padding: 0;
  background: transparent;
}
:deep(.leave-date-picker .el-input__inner) {
  font-size: 0.875rem;
  color: #374151;
}
</style>
