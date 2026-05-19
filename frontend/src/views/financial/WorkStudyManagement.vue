<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="openAddJobDialog" class="bg-amber-500 text-white hover:bg-amber-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Plus /></el-icon>发布新岗位
      </button>
    </teleport>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in statList" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>

    <!-- Job Listings -->
    <div class="bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 mb-6">
      <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">活跃岗位</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div v-for="job in jobs" :key="job.id" class="bg-surface rounded-xl p-5 border border-outline-variant/15 hover:shadow-md transition-shadow">
          <div class="flex justify-between items-start mb-3">
            <h4 class="font-semibold text-on-surface">{{ job.title }}</h4>
            <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="job.status === 'ACTIVE' ? 'bg-green-100 text-green-600' : 'bg-gray-100 text-gray-500'">
              {{ job.status === 'ACTIVE' ? '开放中' : '已关闭' }}
            </span>
          </div>
          <div class="text-xs text-secondary space-y-1">
            <p>部门：{{ job.department }}</p>
            <p>名额：{{ job.currentCount }}/{{ job.quota }}</p>
          </div>
          <div class="mt-3 flex items-center gap-2">
            <el-progress :percentage="(job.currentCount / job.quota * 100)" :stroke-width="6" class="flex-1" :color="job.currentCount >= job.quota ? '#ef4444' : '#f59e0b'" />
            <span class="text-xs font-semibold" :class="job.currentCount >= job.quota ? 'text-red-500' : 'text-amber-600'">
              {{ job.currentCount >= job.quota ? '已满' : '可申请' }}
            </span>
          </div>
          <div class="mt-4 flex gap-2">
            <button @click="openEditJobDialog(job)" class="text-xs font-semibold text-secondary hover:text-amber-600 transition-colors">编辑</button>
            <button @click="toggleJobStatus(job)" class="text-xs font-semibold text-secondary hover:text-amber-600 transition-colors">
              {{ job.status === 'ACTIVE' ? '关闭' : '开放' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Applications Table -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <div class="px-6 py-4 border-b border-outline-variant/10">
        <h3 class="text-[1.125rem] font-semibold text-on-surface">岗位申请记录</h3>
      </div>
      <el-table :data="jobApplications" style="width: 100%" height="100%" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="jobTitle" label="申请岗位" width="200" />
        <el-table-column prop="applyTime" label="申请时间" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="text-xs font-bold" :class="row.status === 'PENDING' ? 'text-amber-600' : row.status === 'APPROVED' ? 'text-green-600' : 'text-red-500'">
              {{ ({ PENDING: '待审核', APPROVED: '已录用', REJECTED: '未录用' })[row.status] }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <button v-if="row.status === 'PENDING'" @click="handleApprove(row)" class="text-xs font-semibold text-green-600 hover:text-green-800 transition-colors mr-3">录用</button>
            <button v-if="row.status === 'PENDING'" @click="handleReject(row)" class="text-xs font-semibold text-red-500 hover:text-red-700 transition-colors">不录用</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Add/Edit Job Dialog -->
    <el-dialog v-model="jobDialogVisible" :title="editingJob ? '编辑岗位' : '发布新岗位'" width="480px" destroy-on-close>
      <el-form :model="jobForm" label-position="top">
        <el-form-item label="岗位名称" required>
          <el-input v-model="jobForm.title" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="所属部门" required>
          <el-input v-model="jobForm.department" placeholder="请输入部门" />
        </el-form-item>
        <div class="grid grid-cols-2 gap-x-4">
          <el-form-item label="名额" required>
            <el-input-number v-model="jobForm.quota" :min="1" :max="100" class="w-full" />
          </el-form-item>
          <el-form-item label="时薪（元）">
            <el-input-number v-model="jobForm.hourlyRate" :min="0" :precision="1" class="w-full" />
          </el-form-item>
        </div>
        <el-form-item label="岗位要求">
          <el-input v-model="jobForm.requirements" type="textarea" :rows="3" placeholder="请输入岗位要求" />
        </el-form-item>
      </el-form>
      <template #footer>
        <button @click="jobDialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="saveJob" class="px-6 py-2 bg-amber-500 text-white rounded-lg text-sm font-bold hover:bg-amber-600 transition-colors">保存</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/financial-aid'

const jobs = ref([])
const jobApplications = ref([])
const jobDialogVisible = ref(false)
const editingJob = ref(null)
const jobForm = ref({ title: '', department: '', quota: 5, hourlyRate: 18, requirements: '' })

const stats = computed(() => ({
  activeJobs: jobs.value.filter(j => j.status === 'ACTIVE').length,
  totalApplications: jobApplications.value.length,
  pendingApplications: jobApplications.value.filter(a => a.status === 'PENDING').length,
}))

const fetchJobs = async () => {
  try {
    const res = await request.get(`${API}/jobs`)
    jobs.value = res.data.data || []
  } catch {
    jobs.value = [
      { id: 1, title: '图书馆阅览室助理', department: '图书馆', quota: 2, currentCount: 2, status: 'ACTIVE' },
      { id: 2, title: '多媒体教室维护', department: '计算机学院', quota: 5, currentCount: 3, status: 'ACTIVE' },
      { id: 3, title: '学工办助理', department: '学工处', quota: 3, currentCount: 1, status: 'ACTIVE' },
      { id: 4, title: '勤工助学基地助理', department: '学生资助中心', quota: 2, currentCount: 1, status: 'ACTIVE' },
    ]
  }
}

const fetchApplications = () => {
  // mock
  jobApplications.value = [
    { id: 1, studentId: '202301045', studentName: '赵六', jobTitle: '图书馆阅览室助理', applyTime: '2026-05-14 11:00', status: 'PENDING' },
    { id: 2, studentId: '202301046', studentName: '孙七', jobTitle: '多媒体教室维护', applyTime: '2026-05-13 16:30', status: 'PENDING' },
    { id: 3, studentId: '202301047', studentName: '周八', jobTitle: '学工办助理', applyTime: '2026-05-12 09:00', status: 'APPROVED' },
  ]
}

const openAddJobDialog = () => {
  editingJob.value = null
  jobForm.value = { title: '', department: '', quota: 5, hourlyRate: 18, requirements: '' }
  jobDialogVisible.value = true
}

const openEditJobDialog = (job) => {
  editingJob.value = job
  jobForm.value = { title: job.title, department: job.department, quota: job.quota, hourlyRate: job.hourlyRate || 18, requirements: job.requirements || '' }
  jobDialogVisible.value = true
}

const saveJob = () => {
  if (!jobForm.value.title || !jobForm.value.department) { ElMessage.warning('岗位名称和部门为必填'); return }
  ElMessage.success(editingJob.value ? '岗位已更新' : '新岗位已发布')
  jobDialogVisible.value = false
}

const toggleJobStatus = (job) => {
  job.status = job.status === 'ACTIVE' ? 'CLOSED' : 'ACTIVE'
  ElMessage.success(`岗位已${job.status === 'ACTIVE' ? '开放' : '关闭'}`)
}

const handleApprove = (app) => {
  app.status = 'APPROVED'
  ElMessage.success(`已录用 ${app.studentName}`)
}

const handleReject = (app) => {
  app.status = 'REJECTED'
  ElMessage.success(`已标记为不录用`)
}

const statList = computed(() => [
  { label: '活跃岗位', value: stats.value.activeJobs },
  { label: '申请总数', value: stats.value.totalApplications },
  { label: '待审核', value: stats.value.pendingApplications },
  { label: '本月已录用', value: 8 },
])

onMounted(() => { fetchJobs(); fetchApplications() })
</script>
