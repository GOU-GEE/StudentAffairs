<template>
  <div class="h-full flex flex-col">
    <div class="mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <h2 class="text-[2.5rem] leading-none font-bold tracking-tight text-on-surface mb-2">我的申请</h2>
        <p class="text-[0.875rem] text-secondary font-medium">My Applications & Approval Status</p>
      </div>
      <button @click="showDialog = true"
              class="bg-primary text-on-primary-fixed hover:bg-primary-fixed rounded-md px-5 py-2.5 text-[0.875rem] font-semibold flex items-center gap-2 shadow-md transition-colors">
        <el-icon><Plus /></el-icon>
        提交新申请
      </button>
    </div>

    <!-- Stats Row -->
    <div class="grid grid-cols-3 gap-4 mb-6">
      <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 text-center">
        <p class="text-2xl font-bold text-on-surface">{{ applications.length }}</p>
        <p class="text-xs text-secondary font-semibold uppercase tracking-wider mt-1">总申请数</p>
      </div>
      <div class="bg-orange-50 rounded-xl p-4 border border-orange-200 text-center">
        <p class="text-2xl font-bold text-orange-600">{{ pendingCount }}</p>
        <p class="text-xs text-orange-600 font-semibold uppercase tracking-wider mt-1">审核中</p>
      </div>
      <div class="bg-green-50 rounded-xl p-4 border border-green-200 text-center">
        <p class="text-2xl font-bold text-green-600">{{ approvedCount }}</p>
        <p class="text-xs text-green-600 font-semibold uppercase tracking-wider mt-1">已批准</p>
      </div>
    </div>

    <!-- Application List -->
    <div class="flex-1 overflow-y-auto space-y-4 custom-scrollbar">
      <div v-for="(app, i) in applications" :key="i"
           class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/15 hover:border-primary/20 transition-all group">
        <div class="flex items-start justify-between gap-3 mb-3 flex-wrap">
          <div class="flex items-center gap-2 flex-wrap">
            <div class="w-9 h-9 rounded-lg flex items-center justify-center flex-shrink-0"
                 :class="typeIconBg(app.type)">
              <el-icon :class="typeIconColor(app.type)"><component :is="typeIcon(app.type)" /></el-icon>
            </div>
            <div>
              <h4 class="font-bold text-sm text-on-surface">{{ app.title }}</h4>
              <p class="text-xs text-secondary">{{ typeLabel(app.type) }} · 提交于 {{ formatDate(app.applyTime) }}</p>
            </div>
          </div>
          <!-- Status Badge -->
          <div class="flex items-center gap-2">
            <span class="text-xs font-bold px-3 py-1 rounded-full border"
                  :class="statusStyle(app.status)">
              <span class="inline-block w-1.5 h-1.5 rounded-full mr-1.5 align-middle"
                    :class="app.status === 'PENDING' ? 'bg-orange-400' : app.status === 'APPROVED' ? 'bg-green-500' : 'bg-error'"></span>
              {{ statusLabel(app.status) }}
            </span>
          </div>
        </div>

        <!-- Reason -->
        <p class="text-sm text-secondary leading-relaxed pl-11 border-l-2 border-outline-variant/30 ml-4">{{ app.reason }}</p>

        <!-- Review Comment (if exists) -->
        <div v-if="app.reviewComment" class="mt-3 pl-11 ml-4">
          <div class="bg-surface p-3 rounded-lg border border-outline-variant/20 flex items-start gap-2">
            <el-icon class="text-secondary mt-0.5 flex-shrink-0"><Comment /></el-icon>
            <div>
              <p class="text-xs font-bold text-secondary mb-1">{{ app.reviewerName }} 的批注 · {{ formatDate(app.reviewTime) }}</p>
              <p class="text-sm text-on-surface">{{ app.reviewComment }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="applications.length === 0" class="py-16 flex flex-col items-center text-secondary">
        <el-icon :size="48" class="mb-2 opacity-30"><Document /></el-icon>
        <p class="text-sm">暂无申请记录，点击上方按钮发起申请</p>
      </div>
    </div>

    <!-- New Application Dialog -->
    <el-dialog v-model="showDialog" title="提交新申请" width="500px" align-center>
      <div class="space-y-4 p-2">
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">申请类型</label>
          <div class="grid grid-cols-2 gap-2">
            <button v-for="t in applicationTypes" :key="t.value" @click="form.type = t.value"
                    class="py-3 rounded-xl text-sm font-bold border transition-all flex items-center justify-center gap-1.5"
                    :class="form.type === t.value ? 'bg-primary text-on-primary-fixed border-primary shadow-md' : 'bg-surface border-outline-variant/30 text-secondary hover:border-primary/30'">
              {{ t.label }}
            </button>
          </div>
        </div>
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">申请标题</label>
          <el-input v-model="form.title" placeholder="请简要描述您的申请内容" />
        </div>
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">申请理由</label>
          <el-input v-model="form.reason" type="textarea" :rows="5" placeholder="详细说明您的申请理由，这将影响辅导员的审批决定..." />
        </div>
      </div>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitApplication" :loading="submitting">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, Document, Trophy, Money, Calendar, Comment } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const STUDENT_ID = '202301042'
const applications = ref([])
const showDialog = ref(false)
const submitting = ref(false)
const form = ref({ type: 'SCHOLARSHIP', title: '', reason: '' })

const applicationTypes = [
  { label: '🏆 奖学金申请', value: 'SCHOLARSHIP' },
  { label: '💰 助学金申请', value: 'FINANCIAL_AID' },
  { label: '📋 请假报备', value: 'LEAVE' },
  { label: '🎗️ 困难认定', value: 'POVERTY' }
]

const pendingCount = computed(() => applications.value.filter(a => a.status === 'PENDING').length)
const approvedCount = computed(() => applications.value.filter(a => a.status === 'APPROVED').length)

const loadData = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/applications/student/${STUDENT_ID}`)
    if (res.data.code === 200) applications.value = res.data.data
  } catch (e) { console.error(e) }
}

const submitApplication = async () => {
  if (!form.value.title || !form.value.reason) {
    ElMessage.warning('请填写完整的申请标题和理由')
    return
  }
  submitting.value = true
  try {
    await axios.post('http://localhost:8080/api/applications', {
      ...form.value,
      studentId: STUDENT_ID,
      studentName: '张小明'
    })
    ElMessage.success('申请已成功提交，请耐心等待辅导员审批')
    showDialog.value = false
    form.value = { type: 'SCHOLARSHIP', title: '', reason: '' }
    await loadData()
  } catch (e) {
    ElMessage.error('提交失败，请确保后端服务已启动')
  } finally {
    submitting.value = false
  }
}

const typeLabel = (t) => ({ SCHOLARSHIP: '奖学金', FINANCIAL_AID: '助学金', LEAVE: '请假报备', POVERTY: '困难认定', WORK_STUDY: '勤工助学' }[t] || t)
const typeIcon = (t) => ({ SCHOLARSHIP: 'Trophy', FINANCIAL_AID: 'Money', LEAVE: 'Calendar', POVERTY: 'Document', WORK_STUDY: 'Document' }[t] || 'Document')
const typeIconBg = (t) => ({ SCHOLARSHIP: 'bg-yellow-100', FINANCIAL_AID: 'bg-green-100', LEAVE: 'bg-blue-100', POVERTY: 'bg-orange-100', WORK_STUDY: 'bg-purple-100' }[t] || 'bg-surface-container-high')
const typeIconColor = (t) => ({ SCHOLARSHIP: 'text-yellow-600', FINANCIAL_AID: 'text-green-600', LEAVE: 'text-blue-600', POVERTY: 'text-orange-600', WORK_STUDY: 'text-purple-600' }[t] || 'text-secondary')
const statusLabel = (s) => ({ PENDING: '审核中', APPROVED: '已批准', REJECTED: '已驳回' }[s] || s)
const statusStyle = (s) => ({ PENDING: 'bg-orange-50 text-orange-600 border-orange-200', APPROVED: 'bg-green-50 text-green-700 border-green-200', REJECTED: 'bg-error-container text-error border-error/20' }[s])
const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

onMounted(loadData)
</script>
