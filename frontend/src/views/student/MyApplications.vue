<template>
  <div class="my-applications h-full flex flex-col">
    <div class="flex items-center justify-between mb-6 flex-shrink-0">
      <div>
        <h2 class="text-xl font-bold text-on-surface">我的申请</h2>
        <p class="text-sm text-secondary mt-1">查看所有已提交的申请状态</p>
      </div>
    </div>

    <!-- Stats Summary -->
    <div class="grid grid-cols-4 gap-4 mb-6 flex-shrink-0">
      <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">全部申请</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ applications.length }}</h3>
      </div>
      <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">待审核</span>
        <h3 class="text-2xl font-bold text-amber-500 mt-1">{{ applications.filter(a => a.status === 'PENDING').length }}</h3>
      </div>
      <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">已通过</span>
        <h3 class="text-2xl font-bold text-green-600 mt-1">{{ applications.filter(a => a.status === 'APPROVED').length }}</h3>
      </div>
      <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">已驳回</span>
        <h3 class="text-2xl font-bold text-red-500 mt-1">{{ applications.filter(a => a.status === 'REJECTED').length }}</h3>
      </div>
    </div>

    <!-- Application List -->
    <div class="flex-1 overflow-y-auto space-y-3 pr-2 custom-scrollbar">
      <div v-if="applications.length === 0" class="text-center py-20">
        <el-icon :size="48" class="text-gray-300 mb-3"><Document /></el-icon>
        <p class="text-secondary">暂无申请记录</p>
        <p class="text-xs text-gray-400 mt-1">提交的申请将在这里显示</p>
      </div>

      <div
        v-for="app in applications"
        :key="app.id"
        class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/15 hover:shadow-md transition-shadow"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-2">
              <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="typeBadgeClass(app.type)">
                {{ typeLabel(app.type) }}
              </span>
              <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="statusBadgeClass(app.status)">
                {{ statusLabel(app.status) }}
              </span>
            </div>
            <h3 class="text-[0.9375rem] font-semibold text-on-surface mb-1">{{ app.title }}</h3>
            <p class="text-sm text-secondary leading-relaxed line-clamp-2">{{ app.reason }}</p>
            <div class="flex items-center gap-4 mt-3">
              <span class="text-xs text-gray-400 flex items-center gap-1">
                <el-icon :size="12"><Clock /></el-icon>
                {{ formatTime(app.applyTime) }}
              </span>
              <span v-if="app.reviewerName" class="text-xs text-gray-400 flex items-center gap-1">
                <el-icon :size="12"><User /></el-icon>
                审核人：{{ app.reviewerName }}
              </span>
            </div>
            <div v-if="app.reviewComment" class="mt-2 p-3 bg-surface rounded-lg border border-outline-variant/10">
              <p class="text-xs text-secondary">审核意见：{{ app.reviewComment }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Document, Clock, User } from '@element-plus/icons-vue'

const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'

const applications = ref([])

const typeLabel = (t) => ({
  SCHOLARSHIP: '奖学金',
  FINANCIAL_AID: '助学金',
  WORK_STUDY: '勤工助学',
  LEAVE: '请假',
  POVERTY: '困难认定'
}[t] || t)

const typeBadgeClass = (t) => ({
  SCHOLARSHIP: 'bg-blue-100 text-blue-700',
  FINANCIAL_AID: 'bg-purple-100 text-purple-700',
  WORK_STUDY: 'bg-green-100 text-green-700',
  LEAVE: 'bg-amber-100 text-amber-700',
  POVERTY: 'bg-orange-100 text-orange-700'
}[t] || 'bg-gray-100 text-gray-600')

const statusLabel = (s) => ({
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已驳回'
}[s] || s)

const statusBadgeClass = (s) => ({
  PENDING: 'bg-amber-100 text-amber-700',
  APPROVED: 'bg-green-100 text-green-700',
  REJECTED: 'bg-red-100 text-red-700'
}[s] || 'bg-gray-100 text-gray-600')

const formatTime = (t) => {
  if (!t) return '-'
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(async () => {
  try {
    const res = await request.get(`/api/applications/student/${STUDENT_ID}`)
    if (res.data.code === 200) {
      applications.value = res.data.data || []
    }
  } catch (e) {
    // keep empty on error
  }
})
</script>
