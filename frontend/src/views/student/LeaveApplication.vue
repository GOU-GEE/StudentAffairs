<template>
  <div class="flex flex-col gap-4 pb-6">
    <!-- 主体 Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 items-stretch">
      <!-- 左侧：填写请假申请 (col-span-2) -->
      <div class="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col">
        <!-- 卡片标题 -->
        <div class="flex items-center gap-3 px-6 pt-6 pb-4 border-b border-gray-100 flex-shrink-0">
          <h3 class="text-base font-bold text-gray-900">填写请假申请</h3>
        </div>

        <div class="px-6 py-5 space-y-5">
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
            <label class="text-sm font-semibold text-gray-700 block mb-2">紧急联系方式 <span class="text-red-500">*</span></label>
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
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col h-full overflow-hidden">
        <!-- 卡片标题 -->
        <div class="flex items-center gap-3 px-6 pt-6 pb-4 border-b border-gray-100 flex-shrink-0">
          <el-icon class="text-gray-400" :size="18"><Clock /></el-icon>
          <h3 class="text-base font-bold text-gray-900">历史请假记录</h3>
        </div>

        <div class="flex-1 overflow-y-auto px-6 py-5 custom-scrollbar">
          <div class="space-y-4">
            <div
              v-for="(r, i) in leaveHistory"
              :key="r.id"
              class="relative"
            >
              <!-- 卡片内容 -->
              <div
                class="border rounded-xl p-4 transition-all cursor-pointer hover:shadow-md hover:border-[#409eff]/40"
                :class="cardStyle(r.status)"
                @click="openDetail(r)"
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
                    <div v-if="r.status === 'RETURNED' && r.reviewTime" class="mt-2 pt-1.5 border-t border-gray-100 flex items-center justify-between text-[11px] text-gray-400">
                      <span>销假时间：</span>
                      <span class="font-medium text-gray-500">{{ formatDateTime(r.reviewTime) }}</span>
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
                  @click.stop="checkIn(r.id)"
                  class="mt-3 w-full py-2 border-2 border-orange-400 text-orange-600 rounded-lg text-xs font-bold hover:bg-orange-50 transition-colors"
                >
                  确认销假
                </button>
              </div>
            </div>
          </div>

          <p v-if="leaveHistory.length === 0" class="text-center text-sm text-gray-400 py-8">暂无请假记录</p>
        </div>
      </div>
    </div>

    <!-- 局部暗色遮罩层，仅覆盖内容区，避免导航栏变灰 -->
    <div v-if="showDetailDialog" class="fixed top-[56px] left-[256px] right-0 bottom-0 bg-black/45 z-[1000] transition-opacity duration-300" @click="showDetailDialog = false"></div>

    <!-- 请假详情滑出抽屉 (el-dialog) -->
    <el-dialog
      v-model="showDetailDialog"
      title=""
      modal-class="custom-leave-overlay"
      class="custom-leave-detail-dialog"
      :show-close="false"
      :modal="false"
      append-to-body
      lock-scroll
      destroy-on-close
    >
      <div v-if="selectedLeave" class="flex flex-col h-full bg-white relative">
        <!-- 抽屉头部 -->
        <div class="px-6 py-5 border-b border-gray-100 flex items-center justify-between flex-shrink-0">
          <span class="text-base font-bold text-gray-900">详情信息</span>
          <button @click="showDetailDialog = false" class="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 text-gray-500 hover:text-gray-700 flex items-center justify-center transition-colors focus:outline-none">
            <el-icon :size="16"><Close /></el-icon>
          </button>
        </div>

        <!-- 滚动内容区 -->
        <div class="flex-1 overflow-y-auto px-8 py-6 space-y-5 custom-scrollbar text-sm text-gray-700 leading-relaxed bg-white">
          <!-- 第一行：第一列姓名，第二列学号 -->
          <div class="grid grid-cols-2 gap-4">
            <div>姓名：{{ studentName }}</div>
            <div>学号：{{ studentId }}</div>
          </div>
          
          <!-- 第二行：第一列专业，第二列班级 -->
          <div class="grid grid-cols-2 gap-4">
            <div>专业：{{ major }}</div>
            <div>班级：{{ classGrade }}</div>
          </div>
          
          <!-- 第三行：一整行请假时间 -->
          <div>请假时间：{{ selectedLeave.startDate }} 至 {{ selectedLeave.endDate }}&nbsp;&nbsp;共 {{ calcDays(selectedLeave.startDate, selectedLeave.endDate) }} 天</div>
          
          <!-- 第四、五行：前往地点与紧急联系方式分列，换行显示实际数据 -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <div class="text-gray-400 text-xs font-semibold">前往地点：</div>
              <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedLeave.destination || '未填写' }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">紧急联系方式：</div>
              <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedLeave.contact || '未填写' }}</div>
            </div>
          </div>
          
          <!-- 第六、七行：请假理由与实际理由内容 -->
          <div>
            <div class="text-gray-400 text-xs font-semibold">请假理由：</div>
            <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedLeave.reason || '未填写' }}</div>
          </div>
          
          <!-- 第八行：审批状态（带颜色） -->
          <div class="flex items-center gap-1.5">
            <span class="text-gray-400 text-xs font-semibold">审批状态：</span>
            <span class="font-bold text-sm" :class="{
              PENDING: 'text-blue-500',
              ON_LEAVE: 'text-green-500',
              NOT_STARTED: 'text-yellow-500',
              PENDING_RETURN: 'text-orange-500',
              RETURNED: 'text-gray-400',
              REJECTED: 'text-red-500'
            }[selectedLeave.status] || 'text-gray-500'">
              {{ leaveStatusLabel(selectedLeave.status) }}
            </span>
          </div>

          <!-- 第九、十行：辅导员意见与确切的意见 -->
          <div>
            <div class="text-gray-400 text-xs font-semibold">辅导员意见：</div>
            <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedLeave.reviewComment || '暂无辅导员意见' }}</div>
          </div>

          <!-- 第十一行：追加的销假时间 -->
          <div v-if="selectedLeave.status === 'RETURNED' && selectedLeave.reviewTime">
            <div class="text-gray-400 text-xs font-semibold">销假时间：</div>
            <div class="text-gray-800 mt-1.5 font-medium">{{ formatDateTime(selectedLeave.reviewTime) }}</div>
          </div>
        </div>

        <!-- 抽屉底部操作栏 (确认销假) -->
        <div v-if="selectedLeave.status === 'PENDING_RETURN'" class="p-5 border-t border-gray-100 bg-orange-50/20 flex-shrink-0">
          <button
            @click="checkInFromDetail(selectedLeave.id)"
            class="w-full py-3 bg-gradient-to-r from-orange-500 to-amber-500 text-white rounded-xl font-bold text-sm shadow-md hover:shadow-lg hover:from-orange-600 hover:to-amber-600 transition-all flex items-center justify-center gap-2"
          >
            <el-icon><Check /></el-icon> 确认销假
          </button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import {
  EditPen, Warning, ArrowDown, ArrowRight, Calendar, Location,
  Phone, InfoFilled, Clock, Lock, Document, User, Check, Close
} from '@element-plus/icons-vue'

const router = useRouter()
const API = '/api/applications'
const studentId = sessionStorage.getItem('userId') || '202301042'
const studentName = '张小明'
const major = '计算机科学与技术'
const classGrade = '大一 (2023级) 软件工程12班'

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

// 详情弹窗控制
const showDetailDialog = ref(false)
const selectedLeave = ref(null)

const openDetail = (r) => {
  selectedLeave.value = r
  showDetailDialog.value = true
}

const checkInFromDetail = async (id) => {
  await checkIn(id)
}

const getTodayStr = () => {
  const d = new Date()
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatDateTime = (t) => {
  if (!t) return ''
  try {
    if (Array.isArray(t)) {
      const year = t[0]
      const month = String(t[1]).padStart(2, '0')
      const day = String(t[2]).padStart(2, '0')
      const hour = String(t[3] || 0).padStart(2, '0')
      const minute = String(t[4] || 0).padStart(2, '0')
      const second = String(t[5] || 0).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    }
    const d = new Date(t)
    if (isNaN(d.getTime())) {
      return String(t).replace('T', ' ').substring(0, 19)
    }
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hour = String(d.getHours()).padStart(2, '0')
    const minute = String(d.getMinutes()).padStart(2, '0')
    const second = String(d.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`
  } catch (e) {
    return String(t)
  }
}

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
          const today = getTodayStr()
          let status = item.status
          if (item.status === 'APPROVED') {
            if (detail.startDate && today < detail.startDate) {
              status = 'NOT_STARTED'
            } else if (detail.startDate && today >= detail.startDate) {
              status = 'PENDING_RETURN'
            } else {
              status = 'ON_LEAVE'
            }
          }
          return {
            id: item.id,
            leaveType: detail.leaveType || 'OTHER',
            startDate: detail.startDate || '',
            endDate: detail.endDate || '',
            destination: detail.destination || '',
            reason: detail.reason || item.reason,
            contact: detail.contact || '',
            status: status,
            rawStatus: item.status,
            reviewComment: item.reviewComment,
            reviewerName: item.reviewerName || '辅导员',
            reviewTime: item.reviewTime
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

const checkIn = async (id) => {
  try {
    const res = await request.put(`${API}/${id}/review`, { status: 'RETURNED' })
    if (res.data.code === 200) {
      ElMessage.success('销假成功，欢迎回来！')
      await loadHistory()
      if (selectedLeave.value && selectedLeave.value.id === id) {
        selectedLeave.value = leaveHistory.value.find(r => r.id === id) || selectedLeave.value
      }
    } else {
      ElMessage.warning(res.data.msg || '销假失败')
    }
  } catch (e) {
    ElMessage.error('销假请求异常')
  }
}

const submitLeave = async () => {
  if (submitting.value) return
  if (!leaveForm.value.startDate || !leaveForm.value.endDate) {
    ElMessage.warning('请选择请假时间'); return
  }
  if (new Date(leaveForm.value.endDate) < new Date(leaveForm.value.startDate)) {
    ElMessage.warning('结束日期不能早于开始日期，请重新选择'); return
  }
  if (!leaveForm.value.contact || !leaveForm.value.contact.trim()) {
    ElMessage.warning('请填写紧急联系方式'); return
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
  PENDING: '审核中', ON_LEAVE: '休假中', NOT_STARTED: '未开始',
  PENDING_RETURN: '待销假', RETURNED: '已销假', REJECTED: '已驳回'
}[s] || s)

const dotStyle = (s) => ({
  PENDING: 'border-blue-300 bg-white',
  ON_LEAVE: 'border-green-400 bg-white',
  NOT_STARTED: 'border-yellow-400 bg-white',
  PENDING_RETURN: 'border-orange-400 bg-white',
  RETURNED: 'border-gray-300 bg-white',
  REJECTED: 'border-red-300 bg-white'
}[s] || 'border-gray-300 bg-white')

const dotInnerStyle = (s) => ({
  PENDING: 'bg-blue-400',
  ON_LEAVE: 'bg-green-500',
  NOT_STARTED: 'bg-yellow-500',
  PENDING_RETURN: 'bg-orange-500',
  RETURNED: 'bg-gray-400',
  REJECTED: 'bg-red-400'
}[s] || 'bg-gray-400')

const cardStyle = (s) => ({
  PENDING: 'border-blue-100 bg-blue-50/40',
  ON_LEAVE: 'border-green-100 bg-green-50/40',
  NOT_STARTED: 'border-yellow-100 bg-yellow-50/40',
  PENDING_RETURN: 'border-orange-100 bg-orange-50/40',
  RETURNED: 'border-gray-100 bg-white',
  REJECTED: 'border-red-100 bg-red-50/40'
}[s] || 'border-gray-100 bg-white')

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-blue-50 text-blue-600 border-blue-200',
  ON_LEAVE: 'bg-green-50 text-green-700 border-green-300',
  NOT_STARTED: 'bg-yellow-50 text-yellow-600 border-yellow-200',
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

<style>
/* 全局样式：用于被 Element Plus 渲染并挂载到 body 下的请假详情对话框 */
.custom-leave-overlay {
  background-color: transparent !important;
  z-index: 1001 !important; /* 保证其低于顶层状态栏 z-[9999]，避免覆盖状态栏 */
}

.custom-leave-detail-dialog {
  position: fixed !important;
  top: 96px !important;
  bottom: 40px !important;
  width: 520px !important;
  margin: 0 !important;
  padding: 0 !important;
  display: flex;
  flex-direction: column;
  border-radius: 32px !important; /* 看板圆角处理 */
  overflow: hidden !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;
  overscroll-behavior: contain;
  z-index: 1001 !important;
}

@media (min-width: 768px) {
  .custom-leave-detail-dialog {
    left: calc(50% + 128px) !important; /* 除去左边 256px 导航栏后，在剩下的页面中左右居中 */
    transform: translateX(-50%) !important;
    right: auto !important;
  }
}
@media (max-width: 767px) {
  .custom-leave-detail-dialog {
    left: 50% !important;
    transform: translateX(-50%) !important;
    right: auto !important;
    width: calc(100vw - 32px) !important;
  }
}
.custom-leave-detail-dialog .el-dialog__header {
  padding: 0 !important;
  margin: 0 !important;
  height: 0;
}
.custom-leave-detail-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  padding: 0 !important;
}
.custom-leave-detail-dialog .el-dialog__headerbtn {
  display: none !important; /* 隐藏默认关闭按钮 */
}
</style>
