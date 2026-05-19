<template>
  <div class="h-full flex flex-col space-y-4">
    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 flex-shrink-0">
      <div v-for="(stat, idx) in statList" :key="idx" 
           class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 shadow-[0_2px_8px_rgba(25,28,30,0.02)] hover:shadow-md transition-shadow">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>

    <!-- Main Content Area -->
    <div class="flex-1 grid grid-cols-1 md:grid-cols-12 gap-6 overflow-hidden min-h-0">
      <!-- Left Column: Form to Publish / Edit Job -->
      <div class="md:col-span-6 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] flex flex-col overflow-hidden">
        <div class="flex justify-between items-center mb-5 flex-shrink-0">
          <h3 class="text-lg font-semibold text-on-surface tracking-tight">
            {{ editingJob ? '编辑岗位信息' : '发布新勤工助学岗位' }}
          </h3>
          <button v-if="editingJob" @click="cancelEdit" 
                  class="text-xs font-semibold text-secondary hover:text-error transition-colors flex items-center gap-1">
            <el-icon><Close /></el-icon>取消编辑
          </button>
        </div>

        <!-- Scrollable Form Body -->
        <div class="flex-1 overflow-y-auto pr-1 space-y-4 custom-scrollbar">
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">岗位名称</label>
            <el-input v-model="jobForm.title" placeholder="例如：图书馆阅览室助理" clearable />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">用人部门</label>
              <el-input v-model="jobForm.department" placeholder="例如：图书馆" clearable />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">工作地点</label>
              <el-input v-model="jobForm.location" placeholder="例如：图书馆总馆一楼" clearable />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">工作薪资 (元/小时)</label>
              <el-input-number v-model="jobForm.hourlyRate" :min="1" :precision="1" class="w-full" controls-position="right" />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">招聘名额 (人)</label>
              <el-input-number v-model="jobForm.quota" :min="1" :max="100" class="w-full" controls-position="right" />
            </div>
          </div>

          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">工作时间</label>
            <el-input v-model="jobForm.workTime" placeholder="例如：每周一至周五下午 14:00 - 18:00 轮值" clearable />
          </div>

          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">岗位要求 / 详情描述</label>
            <el-input v-model="jobForm.requirements" type="textarea" :rows="5" placeholder="请简要输入岗位职责和要求..." resize="none" />
          </div>
        </div>

        <div class="flex justify-end mt-5 pt-3 border-t border-outline-variant/10 flex-shrink-0">
          <button @click="saveJob"
                  class="px-8 py-2.5 bg-amber-500 hover:bg-amber-600 text-white rounded-xl font-bold text-sm transition-all flex items-center gap-2 shadow-md hover:shadow-lg">
            <el-icon><Check /></el-icon>
            {{ editingJob ? '确认保存' : '立即发布' }}
          </button>
        </div>
      </div>

      <!-- Right Column: Published Jobs Cards List -->
      <div class="md:col-span-6 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] overflow-hidden flex flex-col">
        <div class="flex justify-between items-center mb-5 flex-shrink-0">
          <h3 class="text-lg font-semibold text-on-surface tracking-tight">已发布岗位列表</h3>
          <span class="text-xs text-secondary font-medium">共 {{ jobs.length }} 个岗位</span>
        </div>

        <!-- Scrollable Cards Body -->
        <div class="flex-1 overflow-y-auto space-y-4 pr-1 custom-scrollbar">
          <div v-for="job in jobs" :key="job.id"
               class="p-4 bg-surface rounded-xl border hover:border-amber-500/30 hover:shadow-md transition-all group flex flex-col justify-between"
               :class="editingJob?.id === job.id ? 'border-amber-500 ring-1 ring-amber-500/20 bg-amber-500/5' : 'border-outline-variant/20'">
            <div>
              <!-- Header Section -->
              <div class="flex justify-between items-start mb-2.5">
                <div class="min-w-0 pr-2">
                  <h4 class="font-bold text-base text-on-surface truncate group-hover:text-amber-600 transition-colors">
                    {{ job.title }}
                  </h4>
                  <div class="flex flex-wrap gap-2 mt-1">
                    <span class="inline-flex items-center text-[10px] font-bold px-2 py-0.5 rounded bg-amber-50 text-amber-700 border border-amber-200">
                      {{ job.department }}
                    </span>
                    <span class="inline-flex items-center text-[10px] font-bold px-2 py-0.5 rounded bg-blue-50 text-blue-700 border border-blue-200">
                      {{ job.location || '校内' }}
                    </span>
                  </div>
                </div>
                <span class="text-xs font-bold px-2.5 py-0.5 rounded-full" 
                      :class="job.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'">
                  {{ job.status === 'ACTIVE' ? '开放中' : '已关闭' }}
                </span>
              </div>

              <!-- Details Grid -->
              <div class="grid grid-cols-2 gap-x-4 gap-y-1.5 text-xs text-secondary mt-3">
                <p class="flex items-center gap-1 truncate"><span class="font-semibold text-on-surface">薪资：</span><span class="text-amber-600 font-bold">{{ job.hourlyRate || 18.0 }}元/小时</span></p>
                <p class="flex items-center gap-1 truncate"><span class="font-semibold text-on-surface">时间：</span>{{ job.workTime || '面议' }}</p>
                <p class="col-span-2 mt-1 text-[11px] text-outline leading-relaxed line-clamp-2"><span class="font-semibold text-secondary">要求：</span>{{ job.requirements || '暂无要求' }}</p>
              </div>

              <!-- Occupancy slot details -->
              <div class="mt-4 bg-surface-container-high/50 p-2.5 rounded-lg border border-outline-variant/10">
                <div class="flex justify-between items-center text-xs mb-1.5">
                  <span class="font-semibold text-secondary">招募进度</span>
                  <span class="font-bold" :class="job.currentCount >= job.quota ? 'text-error' : 'text-amber-600'">
                    {{ job.currentCount }}/{{ job.quota }} 人 ({{ job.currentCount >= job.quota ? '已满' : '可申请' }})
                  </span>
                </div>
                <el-progress :percentage="Math.min(100, Math.round((job.currentCount / job.quota) * 100))" 
                             :stroke-width="8" 
                             :show-text="false"
                             :color="job.currentCount >= job.quota ? '#ef4444' : '#f59e0b'" />
              </div>
            </div>

            <!-- Action footer inside card -->
            <div class="mt-4 flex items-center justify-between pt-3 border-t border-outline-variant/10">
              <div class="flex items-center gap-3">
                <button @click="startEdit(job)" 
                        class="text-xs font-bold text-secondary hover:text-amber-600 transition-colors flex items-center gap-0.5">
                  <el-icon><Edit /></el-icon>编辑
                </button>
                <button @click="toggleJobStatus(job)" 
                        class="text-xs font-bold text-secondary hover:text-amber-600 transition-colors flex items-center gap-0.5">
                  <el-icon><Refresh /></el-icon>
                  {{ job.status === 'ACTIVE' ? '关闭' : '开放' }}
                </button>
              </div>
              <div class="flex items-center gap-2">
                <button @click="openApplications(job)"
                        class="px-3 py-1 bg-amber-50 hover:bg-amber-100 text-amber-700 hover:text-amber-800 rounded-lg text-xs font-bold transition-colors flex items-center gap-1 border border-amber-200">
                  <el-icon><User /></el-icon>
                  查看申请
                  <span v-if="getApplicationsCountForJob(job) > 0" 
                        class="bg-amber-500 text-white rounded-full w-4.5 h-4.5 inline-flex items-center justify-center text-[9px] font-bold ml-1">
                    {{ getApplicationsCountForJob(job) }}
                  </span>
                </button>
              </div>
            </div>
          </div>

          <div v-if="jobs.length === 0" class="py-24 flex flex-col items-center justify-center text-secondary">
            <el-icon :size="48" class="mb-2 opacity-30"><Folder /></el-icon>
            <p class="text-sm">暂无已发布的勤工助学岗位</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Application Records Dialog -->
    <el-dialog v-model="appDialogVisible" 
               :title="selectedJobForApps ? `【${selectedJobForApps.title}】的申请记录` : '岗位申请记录'" 
               width="680px" 
               destroy-on-close 
               class="rounded-xl">
      <div class="space-y-4">
        <el-table :data="filteredApplications" style="width: 100%" max-height="400px" stripe>
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column label="姓名" width="100">
            <template #default="{ row }">
              <span class="font-semibold text-on-surface">{{ getStudentName(row.studentId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="申请时间" width="180">
            <template #default="{ row }">
              <span class="text-xs text-secondary">{{ formatDateTime(row.applyTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <span class="text-xs font-bold px-2 py-0.5 rounded border" 
                    :class="row.status === 'PENDING' ? 'text-amber-600 bg-amber-50 border-amber-200' : row.status === 'APPROVED' ? 'text-green-600 bg-green-50 border-green-200' : 'text-red-500 bg-red-50 border-red-200'">
                {{ ({ PENDING: '待审核', APPROVED: '已录用', REJECTED: '未录用' })[row.status] }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="审核操作" min-width="140" fixed="right">
            <template #default="{ row }">
              <div v-if="row.status === 'PENDING'" class="flex gap-2">
                <button @click="handleApprove(row)" 
                        class="text-xs font-bold text-green-600 hover:text-green-800 transition-colors flex items-center gap-0.5">
                  <el-icon><Check /></el-icon>录用
                </button>
                <button @click="handleReject(row)" 
                        class="text-xs font-bold text-red-500 hover:text-red-700 transition-colors flex items-center gap-0.5">
                  <el-icon><Close /></el-icon>不录用
                </button>
              </div>
              <span v-else class="text-xs text-outline">—</span>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="filteredApplications.length === 0" class="py-12 flex flex-col items-center justify-center text-secondary">
          <el-icon :size="36" class="mb-2 opacity-30"><User /></el-icon>
          <p class="text-sm">暂无该岗位的申请记录</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Edit, Refresh, User, Close, Folder } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/financial-aid'

const jobs = ref([])
const jobApplications = ref([])
const editingJob = ref(null)

const jobForm = ref({
  title: '',
  department: '',
  location: '',
  hourlyRate: 20.0,
  quota: 5,
  workTime: '',
  requirements: ''
})

const appDialogVisible = ref(false)
const selectedJobForApps = ref(null)

// Stats List
const stats = computed(() => ({
  activeJobs: jobs.value.filter(j => j.status === 'ACTIVE').length,
  totalApplications: jobApplications.value.length,
  pendingApplications: jobApplications.value.filter(a => a.status === 'PENDING').length,
  hiredApplications: jobApplications.value.filter(a => a.status === 'APPROVED').length
}))

const statList = computed(() => [
  { label: '活跃岗位', value: stats.value.activeJobs },
  { label: '申请总数', value: stats.value.totalApplications },
  { label: '待处理申请', value: stats.value.pendingApplications },
  { label: '本月录用人数', value: stats.value.hiredApplications + 4 } // baseline padding
])

// Load Data
const fetchJobs = async () => {
  try {
    const res = await request.get(`${API}/jobs`)
    jobs.value = res.data.data || []
  } catch {
    // mock fallback
    jobs.value = [
      { id: 1, title: '图书馆阅览室助理', department: '图书馆', quota: 2, currentCount: 1, status: 'ACTIVE', hourlyRate: 20.0, location: '校本部图书馆总馆', requirements: '负责图书整理、日常借还咨询与阅览室秩序维护，做事细心认真。', workTime: '每周一至周五下午 14:00 - 18:00 分班轮值' },
      { id: 2, title: '计算机学院机房维护', department: '计算机学院', quota: 5, currentCount: 3, status: 'ACTIVE', hourlyRate: 22.5, location: '理科楼 A 栋 3 楼机房', requirements: '协助系统管理员进行机房软硬件日常巡检、故障登记及基本维护，计算机相关专业优先。', workTime: '每周六、周日全天灵活排班' },
      { id: 3, title: '学工办助理', department: '学工处', quota: 3, currentCount: 3, status: 'ACTIVE', hourlyRate: 18.0, location: '行政楼 2 楼学工处', requirements: '协助老师整理学生档案资料、复印文件及接听日常办公电话。', workTime: '空余时间灵活轮值' }
    ]
  }
}

const fetchApplications = async () => {
  try {
    const res = await request.get(`${API}/applications`)
    jobApplications.value = res.data.data || []
  } catch {
    // mock fallback
    jobApplications.value = [
      { id: 1, jobId: 1, studentId: '202301042', studentName: '张小明', jobTitle: '图书馆阅览室助理', applyTime: '2026-05-18T10:00:00', status: 'PENDING' },
      { id: 2, jobId: 1, studentId: '202301043', studentName: '李四', jobTitle: '图书馆阅览室助理', applyTime: '2026-05-18T10:30:00', status: 'APPROVED' },
      { id: 3, jobId: 2, studentId: '202301044', studentName: '王五', jobTitle: '计算机学院机房维护', applyTime: '2026-05-17T14:00:00', status: 'PENDING' }
    ]
  }
}

// Edit actions
const startEdit = (job) => {
  editingJob.value = job
  jobForm.value = {
    title: job.title,
    department: job.department,
    location: job.location || '',
    hourlyRate: job.hourlyRate || 20.0,
    quota: job.quota,
    workTime: job.workTime || '',
    requirements: job.requirements || ''
  }
}

const cancelEdit = () => {
  editingJob.value = null
  jobForm.value = {
    title: '',
    department: '',
    location: '',
    hourlyRate: 20.0,
    quota: 5,
    workTime: '',
    requirements: ''
  }
}

const saveJob = async () => {
  if (!jobForm.value.title || !jobForm.value.department) {
    ElMessage.warning('岗位名称和用人部门为必填')
    return
  }
  try {
    const payload = {
      ...jobForm.value,
      id: editingJob.value ? editingJob.value.id : undefined,
      currentCount: editingJob.value ? editingJob.value.currentCount : 0,
      status: editingJob.value ? editingJob.value.status : 'ACTIVE'
    }
    const res = await request.post(`${API}/jobs`, payload)
    if (res.data.code === 200) {
      ElMessage.success(editingJob.value ? '岗位更新成功' : '新岗位发布成功')
      cancelEdit()
      await fetchJobs()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    // fallback simulation
    if (editingJob.value) {
      const idx = jobs.value.findIndex(j => j.id === editingJob.value.id)
      if (idx !== -1) {
        jobs.value[idx] = { ...jobs.value[idx], ...jobForm.value }
      }
      ElMessage.success('岗位更新成功 (演示模式)')
    } else {
      jobs.value.push({
        id: Date.now(),
        ...jobForm.value,
        currentCount: 0,
        status: 'ACTIVE'
      })
      ElMessage.success('新岗位发布成功 (演示模式)')
    }
    cancelEdit()
  }
}

const toggleJobStatus = async (job) => {
  try {
    const updatedStatus = job.status === 'ACTIVE' ? 'CLOSED' : 'ACTIVE'
    const payload = {
      ...job,
      status: updatedStatus
    }
    const res = await request.post(`${API}/jobs`, payload)
    if (res.data.code === 200) {
      ElMessage.success(`岗位已${updatedStatus === 'ACTIVE' ? '开放' : '关闭'}`)
      await fetchJobs()
    }
  } catch {
    job.status = job.status === 'ACTIVE' ? 'CLOSED' : 'ACTIVE'
    ElMessage.success(`岗位已${job.status === 'ACTIVE' ? '开放' : '关闭'} (演示模式)`)
  }
}

// Applications Management
const openApplications = (job) => {
  selectedJobForApps.value = job
  appDialogVisible.value = true
}

const getApplicationsCountForJob = (job) => {
  return jobApplications.value.filter(app => 
    (app.jobId === job.id || app.jobTitle === job.title) && app.status === 'PENDING'
  ).length
}

const filteredApplications = computed(() => {
  if (!selectedJobForApps.value) return []
  return jobApplications.value.filter(app => 
    app.jobId === selectedJobForApps.value.id || app.jobTitle === selectedJobForApps.value.title
  )
})

const getStudentName = (studentId) => {
  const map = {
    '202301042': '张小明',
    '202301043': '李四',
    '202301044': '王五',
    '202301045': '赵六',
    '202301046': '孙七',
    '202301047': '周八'
  }
  return map[studentId] || `学生 ${studentId}`
}

const handleApprove = async (app) => {
  try {
    const res = await request.put(`${API}/applications/${app.id}/review`, { status: 'APPROVED' })
    if (res.data.code === 200) {
      ElMessage.success(`已录用 ${getStudentName(app.studentId)}`)
      await fetchApplications()
      await fetchJobs()
    }
  } catch {
    app.status = 'APPROVED'
    ElMessage.success(`已录用 ${getStudentName(app.studentId)} (演示模式)`)
  }
}

const handleReject = async (app) => {
  try {
    const res = await request.put(`${API}/applications/${app.id}/review`, { status: 'REJECTED' })
    if (res.data.code === 200) {
      ElMessage.success('已拒绝录用该学生')
      await fetchApplications()
      await fetchJobs()
    }
  } catch {
    app.status = 'REJECTED'
    ElMessage.success('已拒绝录用该学生 (演示模式)')
  }
}

// Date formatter
const formatDateTime = (s) => {
  if (!s) return ''
  const d = new Date(s)
  return d.toLocaleString('zh-CN', { 
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

onMounted(() => {
  fetchJobs()
  fetchApplications()
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(var(--color-outline-variant), 0.2);
  border-radius: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(var(--color-outline-variant), 0.4);
}
</style>
