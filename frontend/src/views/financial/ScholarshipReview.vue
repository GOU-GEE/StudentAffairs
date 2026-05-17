<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button class="bg-amber-500 text-white hover:bg-amber-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Download /></el-icon>导出数据
      </button>
    </teleport>

    <!-- Filter Tabs + Search -->
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <div class="flex gap-2">
        <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
                class="px-4 py-1.5 rounded-lg text-sm font-semibold transition-colors"
                :class="activeTab === tab.key ? 'bg-amber-500 text-white' : 'text-secondary hover:bg-surface-container-high'">
          {{ tab.label }} ({{ tab.count }})
        </button>
      </div>
      <el-input v-model="searchQuery" placeholder="搜索学号、姓名..." class="max-w-xs ml-auto" clearable>
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <!-- Application List -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredApplications" style="width: 100%" height="100%" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column label="类型" width="160">
          <template #default="{ row }">
            <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="typeStyle(row.type)">{{ row.type }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="申请标题" min-width="200" />
        <el-table-column prop="applyTime" label="申请时间" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="text-xs font-bold" :class="statusStyle(row.status)">{{ statusLabel(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <button v-if="row.status === 'PENDING'" @click="openReview(row, 'APPROVED')" class="text-xs font-semibold text-green-600 hover:text-green-800 transition-colors mr-3">通过</button>
            <button v-if="row.status === 'PENDING'" @click="openReview(row, 'REJECTED')" class="text-xs font-semibold text-red-500 hover:text-red-700 transition-colors mr-3">驳回</button>
            <button @click="viewDetail(row)" class="text-xs font-semibold text-secondary hover:text-amber-600 transition-colors">详情</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Review Dialog -->
    <el-dialog v-model="reviewDialogVisible" title="审批意见" width="480px" destroy-on-close>
      <div class="space-y-4">
        <div class="bg-surface-container-low rounded-xl p-4 text-sm">
          <p><span class="font-bold text-secondary">申请人：</span>{{ reviewingApp?.studentName }} ({{ reviewingApp?.studentId }})</p>
          <p class="mt-1"><span class="font-bold text-secondary">申请类型：</span>{{ reviewingApp?.type }}</p>
          <p class="mt-1"><span class="font-bold text-secondary">标题：</span>{{ reviewingApp?.title }}</p>
        </div>
        <el-input v-model="reviewComment" type="textarea" :rows="3" placeholder="请输入审批意见（可选）" />
      </div>
      <template #footer>
        <button @click="reviewDialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="confirmReview" class="px-6 py-2 bg-amber-500 text-white rounded-lg text-sm font-bold hover:bg-amber-600 transition-colors">
          确认{{ reviewAction === 'APPROVED' ? '通过' : '驳回' }}
        </button>
      </template>
    </el-dialog>

    <!-- Detail Drawer -->
    <el-drawer v-model="detailVisible" title="申请详情" size="480px">
      <div v-if="detailApp" class="space-y-4 px-2">
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">基本信息</p>
          <p class="text-sm"><span class="text-secondary">学号：</span>{{ detailApp.studentId }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">姓名：</span>{{ detailApp.studentName }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">类型：</span>{{ detailApp.type }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">申请时间：</span>{{ detailApp.applyTime }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">状态：</span>{{ statusLabel(detailApp.status) }}</p>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">申请事由</p>
          <p class="text-sm leading-relaxed">{{ detailApp.reason }}</p>
        </div>
        <div v-if="detailApp.reviewComment" class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">审批意见</p>
          <p class="text-sm">{{ detailApp.reviewComment }}</p>
          <p class="text-xs text-outline mt-1">{{ detailApp.reviewerName }} · {{ detailApp.reviewTime }}</p>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/applications'

const applications = ref([])
const searchQuery = ref('')
const activeTab = ref('all')
const reviewDialogVisible = ref(false)
const detailVisible = ref(false)
const reviewingApp = ref(null)
const reviewAction = ref('')
const reviewComment = ref('')
const detailApp = ref(null)

const tabs = computed(() => [
  { key: 'all', label: '全部', count: applications.value.length },
  { key: 'PENDING', label: '待审批', count: applications.value.filter(a => a.status === 'PENDING').length },
  { key: 'APPROVED', label: '已通过', count: applications.value.filter(a => a.status === 'APPROVED').length },
  { key: 'REJECTED', label: '已驳回', count: applications.value.filter(a => a.status === 'REJECTED').length },
])

const filteredApplications = computed(() => {
  let list = applications.value
  if (activeTab.value !== 'all') list = list.filter(a => a.status === activeTab.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(a => a.studentId.includes(q) || a.studentName.includes(q))
  }
  return list
})

const typeStyle = (t) => ({
  'SCHOLARSHIP': 'bg-amber-100 text-amber-700',
  'FINANCIAL_AID': 'bg-blue-100 text-blue-700',
})[t] || 'bg-gray-100 text-gray-600'

const statusStyle = (s) => ({
  'PENDING': 'text-amber-600',
  'APPROVED': 'text-green-600',
  'REJECTED': 'text-red-500',
})[s] || ''

const statusLabel = (s) => ({ PENDING: '待审批', APPROVED: '已通过', REJECTED: '已驳回' }[s] || s)

const fetchApplications = async () => {
  try {
    const res = await axios.get(`${API}/all`)
    applications.value = (res.data.data || []).filter(a =>
      a.type === 'SCHOLARSHIP' || a.type === 'FINANCIAL_AID'
    )
  } catch {
    applications.value = [
      { id: 1, studentId: '202301042', studentName: '张小明', type: 'SCHOLARSHIP', title: '2024-2025学年国家励志奖学金申请', reason: '本学期学习成绩优异，GPA 3.85，专业排名前10%，积极参加社会实践活动，特申请国家励志奖学金。', status: 'PENDING', applyTime: '2026-05-12 10:00', reviewComment: null, reviewerName: null, reviewTime: null },
      { id: 2, studentId: '202301043', studentName: '李四', type: 'FINANCIAL_AID', title: '国家助学金申请（困难认定C档）', reason: '家庭经济困难，父母务农，年收入不足3万元，已提交困难认定材料，特申请国家助学金。', status: 'PENDING', applyTime: '2026-05-11 15:30', reviewComment: null, reviewerName: null, reviewTime: null },
      { id: 3, studentId: '202301044', studentName: '王五', type: 'SCHOLARSHIP', title: '校级优秀奖学金申请', reason: 'GPA 3.5以上，无违纪记录，积极参加校园活动。', status: 'APPROVED', applyTime: '2026-05-08 09:00', reviewComment: '同意申请', reviewerName: '李辅导员', reviewTime: '2026-05-10 14:00' },
    ]
  }
}

const openReview = (app, action) => {
  reviewingApp.value = app
  reviewAction.value = action
  reviewComment.value = ''
  reviewDialogVisible.value = true
}

const confirmReview = async () => {
  try {
    await axios.put(`${API}/${reviewingApp.value.id}/review`, {
      status: reviewAction.value,
      comment: reviewComment.value,
      reviewerName: '资助管理中心',
    })
    ElMessage.success(reviewAction.value === 'APPROVED' ? '已通过' : '已驳回')
    reviewDialogVisible.value = false
    fetchApplications()
  } catch {
    ElMessage.success(`演示模式：${reviewAction.value === 'APPROVED' ? '已通过' : '已驳回'}`)
    reviewDialogVisible.value = false
  }
}

const viewDetail = (app) => {
  detailApp.value = app
  detailVisible.value = true
}

onMounted(fetchApplications)
</script>
