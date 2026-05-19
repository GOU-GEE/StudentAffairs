<template>
  <div class="h-full flex flex-col gap-4 min-h-0">
    <!-- 顶部统计卡片 -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm px-6 py-4 flex items-center gap-6 flex-shrink-0">
      <div class="flex items-center gap-6">
        <div v-for="stat in stats" :key="stat.label" class="flex flex-col items-center gap-0.5">
          <span class="text-2xl font-black" :class="stat.color">{{ stat.value }}</span>
          <span class="text-xs text-gray-400">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <!-- 三栏主区域 -->
    <div class="flex-1 grid grid-cols-12 gap-4 min-h-0">
      <!-- 左栏：学生列表 -->
      <div class="col-span-3 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div class="px-4 pt-4 pb-3 border-b border-gray-100 flex-shrink-0">
          <div class="flex items-center justify-between mb-3">
            <p class="text-sm font-bold text-gray-900">申请学生列表</p>
            <span class="text-xs text-gray-400">共 {{ filteredStudents.length }} 条</span>
          </div>
          <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2 mb-2 hover:border-gray-400 focus-within:border-gray-500 transition-colors">
            <el-icon class="text-gray-300 flex-shrink-0" :size="13"><Search /></el-icon>
            <input v-model="searchText" class="flex-1 text-xs outline-none bg-transparent placeholder-gray-300 text-gray-700" placeholder="搜索学号/姓名" />
          </div>
          <div class="flex gap-1 flex-wrap">
            <button v-for="f in statusFilters" :key="f.value" @click="activeFilter = f.value"
                    class="px-2.5 py-1 rounded-lg text-xs font-medium transition-all"
                    :class="activeFilter === f.value ? 'bg-gray-900 text-white' : 'bg-gray-100 text-gray-500 hover:bg-gray-200'">
              {{ f.label }}
            </button>
          </div>
        </div>

        <div class="flex-1 min-h-0 overflow-y-auto px-3 py-3 space-y-2">
          <div v-for="app in filteredStudents" :key="app.id"
               @click="selectedApp = app"
               class="p-3 rounded-xl cursor-pointer transition-all border"
               :class="selectedApp?.id === app.id ? 'border-blue-200 bg-blue-50' : 'border-gray-100 hover:border-gray-200 hover:bg-gray-50'">
            <div class="flex items-center justify-between gap-1">
              <span class="text-sm font-bold text-gray-900 truncate">{{ app.studentName }}</span>
              <span class="text-xs font-bold flex-shrink-0" :class="statusColor(app.status)">{{ statusLabel(app.status) }}</span>
            </div>
            <p class="text-xs text-gray-400">学号：{{ app.studentId }}</p>
            <p class="text-xs text-gray-400 mt-0.5">{{ app.title }}</p>
            <p class="text-xs text-gray-400">提交：{{ formatDate(app.applyTime) }}</p>
          </div>
          <div v-if="filteredStudents.length === 0" class="py-10 text-center text-gray-400 text-xs">暂无数据</div>
        </div>
      </div>

      <!-- 中栏：申请详情 -->
      <div class="col-span-6 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div v-if="selectedApp" class="flex flex-col h-full overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-100 flex-shrink-0">
            <p class="text-sm font-bold text-gray-900">申请详情</p>
          </div>
          <div class="flex-1 overflow-y-auto px-6 py-4 space-y-5">
            <div class="space-y-3">
              <div class="flex items-start gap-2">
                <span class="text-xs text-gray-400 w-16 pt-0.5">学号</span>
                <span class="text-sm font-semibold text-gray-800">{{ selectedApp.studentId }}</span>
              </div>
              <div class="flex items-start gap-2">
                <span class="text-xs text-gray-400 w-16 pt-0.5">姓名</span>
                <span class="text-sm font-semibold text-gray-800">{{ selectedApp.studentName }}</span>
              </div>
              <div class="flex items-start gap-2">
                <span class="text-xs text-gray-400 w-16 pt-0.5">申请类型</span>
                <span class="text-sm font-semibold text-gray-800">{{ typeLabel(selectedApp.type) }}</span>
              </div>
              <div class="flex items-start gap-2">
                <span class="text-xs text-gray-400 w-16 pt-0.5">标题</span>
                <span class="text-sm font-semibold text-gray-800">{{ selectedApp.title }}</span>
              </div>
              <div class="flex items-start gap-2">
                <span class="text-xs text-gray-400 w-16 pt-0.5">提交时间</span>
                <span class="text-sm font-semibold text-gray-800">{{ formatDate(selectedApp.applyTime) }}</span>
              </div>
            </div>

            <div class="pt-3 border-t border-gray-100">
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">申请理由</p>
              <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">{{ selectedApp.reason }}</p>
            </div>

            <div v-if="selectedApp.reviewComment" class="pt-3 border-t border-gray-100">
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">审核意见</p>
              <p class="text-sm text-gray-700">{{ selectedApp.reviewComment }}</p>
              <p class="text-xs text-gray-400 mt-1">审核人: {{ selectedApp.reviewerName }} · {{ formatDate(selectedApp.reviewTime) }}</p>
            </div>
          </div>
        </div>
        <div v-else class="flex-1 flex flex-col items-center justify-center text-gray-300">
          <el-icon :size="48" class="mb-3"><User /></el-icon>
          <p class="text-sm">请从左侧选择学生查看申请详情</p>
        </div>
      </div>

      <!-- 右栏：审核操作 -->
      <div class="col-span-3 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div class="px-4 py-4 border-b border-gray-100 flex-shrink-0">
          <p class="text-sm font-bold text-gray-900">审核操作</p>
        </div>
        <div class="flex-1 overflow-y-auto px-4 py-4">
          <textarea v-model="reviewComment" placeholder="请输入审核意见..." maxlength="500"
                    class="w-full h-full border-none outline-none bg-transparent resize-none text-sm text-gray-700 placeholder-gray-300"></textarea>
        </div>
        <div class="px-4 py-4 border-t border-gray-100 flex-shrink-0 flex items-center gap-2">
          <button @click="review('REJECTED')"
                  class="flex-1 py-2.5 border-2 border-red-200 rounded-xl text-sm font-bold text-red-500 hover:bg-red-50 transition-colors"
                  :disabled="!selectedApp || selectedApp.status !== 'PENDING'">不通过</button>
          <button @click="review('APPROVED')"
                  class="flex-1 py-2.5 bg-gray-900 rounded-xl text-sm font-bold text-white hover:bg-gray-700 transition-colors"
                  :disabled="!selectedApp || selectedApp.status !== 'PENDING'">通过</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, User } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/applications'

const applications = ref([])
const selectedApp = ref(null)
const reviewComment = ref('')
const searchText = ref('')
const activeFilter = ref('all')

const statusFilters = [
  { label: '全部', value: 'all' },
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '未通过', value: 'REJECTED' }
]

const stats = computed(() => {
  const all = applications.value
  const pending = all.filter(a => a.status === 'PENDING').length
  const approved = all.filter(a => a.status === 'APPROVED').length
  const rejected = all.filter(a => a.status === 'REJECTED').length
  return [
    { label: '全部', value: all.length, color: 'text-gray-900' },
    { label: '待审核', value: pending, color: 'text-orange-500' },
    { label: '已通过', value: approved, color: 'text-green-600' },
    { label: '未通过', value: rejected, color: 'text-red-500' }
  ]
})

const filteredStudents = computed(() => {
  // 只显示奖助学金相关申请
  const aidApps = applications.value.filter(a =>
    ['SCHOLARSHIP', 'FINANCIAL_AID'].includes(a.type)
  )
  return aidApps.filter(a => {
    const matchFilter = activeFilter.value === 'all' || a.status === activeFilter.value
    const matchSearch = !searchText.value ||
      a.studentName.includes(searchText.value) ||
      a.studentId.includes(searchText.value)
    return matchFilter && matchSearch
  })
})

const loadData = async () => {
  try {
    const res = await request.get(`${API}/all`)
    if (res.data.code === 200) applications.value = res.data.data
  } catch (e) { console.error(e) }
}

const review = async (status) => {
  if (!selectedApp.value) { ElMessage.warning('请先选择学生'); return }
  try {
    await request.put(`${API}/${selectedApp.value.id}/review`, {
      status,
      comment: reviewComment.value,
      reviewerName: sessionStorage.getItem('userName') || '李辅导员'
    })
    ElMessage.success(status === 'APPROVED' ? '已通过' : '已驳回')
    reviewComment.value = ''
    await loadData()
    selectedApp.value = applications.value.find(a => a.id === selectedApp.value?.id) || null
  } catch (e) { ElMessage.error('操作失败') }
}

const statusLabel = (s) => ({ PENDING: '待审核', APPROVED: '已通过', REJECTED: '未通过' }[s] || s)
const statusColor = (s) => ({ PENDING: 'text-orange-500', APPROVED: 'text-green-600', REJECTED: 'text-red-500' }[s] || 'text-gray-400')
const typeLabel = (t) => ({ SCHOLARSHIP: '奖学金', FINANCIAL_AID: '助学金', LEAVE: '请假', WORK_STUDY: '勤工助学' }[t] || t)
const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

onMounted(loadData)
</script>
