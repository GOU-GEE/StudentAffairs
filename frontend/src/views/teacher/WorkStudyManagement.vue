<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
    <!-- 左侧：发布岗位 & 岗位管理 (col-span-2) -->
    <div class="lg:col-span-2 flex flex-col gap-6">
      <!-- 1. 发布新岗位 -->
      <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-8 py-5 border-b border-gray-100 flex items-center justify-between bg-blue-50/20">
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 bg-blue-600 text-white rounded-lg flex items-center justify-center text-sm">➕</div>
            <h3 class="text-base font-bold text-gray-900">发布新勤工助学岗位</h3>
          </div>
          <span class="text-xs text-gray-400">目前已有 12 个活跃岗位</span>
        </div>
        <div class="p-8">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4 mb-6">
            <div>
              <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5 ml-1">岗位名称</label>
              <el-input v-model="jobForm.name" placeholder="如：图书馆资源整理员" />
            </div>
            <div>
              <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5 ml-1">所属部门</label>
              <el-select v-model="jobForm.dept" placeholder="选择部门" class="w-full">
                <el-option label="校图书馆" value="library" />
                <el-option label="学生处" value="student_office" />
                <el-option label="后勤保障处" value="logistics" />
                <el-option label="信息技术中心" value="it_center" />
              </el-select>
            </div>
            <div>
              <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5 ml-1">薪资标准 (元/小时)</label>
              <el-input-number v-model="jobForm.salary" :min="18" :max="35" class="!w-full" />
            </div>
            <div>
              <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5 ml-1">招聘人数</label>
              <el-input-number v-model="jobForm.slots" :min="1" :max="50" class="!w-full" />
            </div>
          </div>
          <div class="mb-6">
            <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5 ml-1">任职要求</label>
            <textarea v-model="jobForm.req" rows="3" placeholder="请简述岗位技能要求、工作时间段要求等..." class="w-full border border-gray-200 rounded-xl px-4 py-3 text-sm focus:border-blue-500 outline-none resize-none"></textarea>
          </div>
          <div class="flex justify-end">
            <el-button type="primary" class="!px-10 !py-6 !rounded-2xl shadow-lg shadow-blue-100 font-bold" @click="publishJob">确认发布岗位</el-button>
          </div>
        </div>
      </div>

      <!-- 2. 在招岗位列表 -->
      <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden flex-1">
        <div class="px-8 py-5 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-base font-bold text-gray-900">活跃岗位管理</h3>
          <div class="flex gap-2">
            <el-tag size="small" type="success">运行中: 8</el-tag>
            <el-tag size="small" type="info">已截止: 4</el-tag>
          </div>
        </div>
        <div class="p-4">
          <el-table :data="activeJobs" style="width: 100%" size="small">
            <el-table-column prop="name" label="岗位名称" width="160" font-bold />
            <el-table-column prop="dept" label="部门" />
            <el-table-column prop="applied" label="申请/名额">
              <template #default="{ row }">
                <span :class="row.applied >= row.slots ? 'text-red-500 font-bold' : 'text-blue-600'">{{ row.applied }} / {{ row.slots }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-switch v-model="row.active" size="small" active-text="开放" inactive-text="关闭" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="primary" link size="small">编辑</el-button>
                <el-button type="danger" link size="small">关闭</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- 右侧：学生申请审批 (col-span-1) -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden flex flex-col">
      <div class="px-6 py-5 border-b border-gray-100">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-base font-bold text-gray-900">待审核申请</h3>
          <span class="text-[10px] font-black bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">24 份新申请</span>
        </div>
        <el-input :prefix-icon="Search" placeholder="搜索学生姓名..." size="small" />
      </div>

      <div class="flex-1 overflow-y-auto p-4 space-y-3">
        <div 
          v-for="app in pendingApps" 
          :key="app.id"
          @click="selectedApp = app"
          class="p-4 rounded-2xl border transition-all cursor-pointer group"
          :class="selectedApp?.id === app.id ? 'border-blue-500 bg-blue-50/30' : 'border-gray-50 bg-gray-50/30 hover:border-blue-200'"
        >
          <div class="flex justify-between items-start mb-2">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold text-xs">{{ app.student.substring(0,1) }}</div>
              <div>
                <h4 class="text-sm font-bold text-gray-900">{{ app.student }}</h4>
                <p class="text-[10px] text-gray-400">{{ app.major }}</p>
              </div>
            </div>
            <span class="text-[10px] font-bold text-gray-400">{{ app.time }}</span>
          </div>
          <div class="bg-white p-2 rounded-lg border border-gray-100 mb-3">
            <p class="text-[11px] text-gray-500 font-bold mb-0.5 italic">申请岗位：</p>
            <p class="text-xs text-blue-600 font-bold">{{ app.job }}</p>
          </div>
          <div v-if="selectedApp?.id === app.id" class="animate-in fade-in zoom-in-95 duration-200">
             <p class="text-xs text-gray-600 leading-relaxed mb-4 border-l-2 border-blue-200 pl-3">“{{ app.motivation }}”</p>
             <div class="flex gap-2">
               <el-button type="primary" size="small" class="flex-1 !rounded-lg" @click.stop="approve(app)">通过</el-button>
               <el-button type="danger" size="small" plain class="flex-1 !rounded-lg" @click.stop="reject(app)">驳回</el-button>
             </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const jobForm = ref({
  name: '',
  dept: '',
  salary: 20,
  slots: 5,
  req: ''
})

const activeJobs = ref([
  { id: 1, name: '图书馆上架员', dept: '校图书馆', applied: 12, slots: 5, active: true },
  { id: 2, name: '学工助理', dept: '学生处', applied: 8, slots: 3, active: true },
  { id: 3, name: '机房值班员', dept: '信息中心', applied: 20, slots: 10, active: true },
  { id: 4, name: '绿化维护', dept: '后勤中心', applied: 2, slots: 15, active: false }
])

const pendingApps = ref([
  { id: 101, student: '张小明', major: '计算机科学', job: '图书馆上架员', time: '14:20', motivation: '我性格耐心，对图书馆排布熟悉，且这学期课余时间稳定...', status: 'PENDING' },
  { id: 102, student: '李红', major: '汉语言文学', job: '学工助理', time: '15:10', motivation: '曾担任班长，具备较强的沟通和公文处理能力，希望能减轻家庭负担...', status: 'PENDING' },
  { id: 103, student: '王伟', major: '体育教育', job: '机房值班员', time: '昨天', motivation: '对电脑基础维护有一定了解，希望能利用课余时间勤工俭学...', status: 'PENDING' }
])

onMounted(() => {
  const allApps = JSON.parse(localStorage.getItem('work_study_apps') || '[]')
  allApps.forEach(app => {
    // Avoid duplicates if needed, but for demo just unshift
    if (!pendingApps.value.some(p => p.id === app.id)) {
      pendingApps.value.unshift({
        id: app.id,
        student: app.student,
        major: app.major,
        job: app.jobName,
        time: '刚刚',
        motivation: app.motivation,
        status: 'PENDING'
      })
    }
  })
})

const selectedApp = ref(null)

const publishJob = () => {
  if (!jobForm.value.name || !jobForm.value.dept) { ElMessage.warning('请填写岗位名称和部门'); return }
  ElMessage.success('岗位发布成功！')
  activeJobs.value.unshift({
    id: Date.now(),
    name: jobForm.value.name,
    dept: jobForm.value.dept,
    applied: 0,
    slots: jobForm.value.slots,
    active: true
  })
  jobForm.value = { name: '', dept: '', salary: 20, slots: 5, req: '' }
}

const approve = (app) => {
  ElMessage.success(`${app.student} 的申请已通过并录用`)
  pendingApps.value = pendingApps.value.filter(a => a.id !== app.id)
  selectedApp.value = null
}

const reject = (app) => {
  ElMessage.info(`已驳回 ${app.student} 的申请`)
  pendingApps.value = pendingApps.value.filter(a => a.id !== app.id)
  selectedApp.value = null
}
</script>

<style scoped>
:deep(.el-input__wrapper), :deep(.el-select__wrapper), :deep(.el-input-number) {
  border-radius: 0.75rem !important;
  box-shadow: 0 0 0 1px #e5e7eb inset !important;
}
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #3b82f6 inset !important;
}
</style>
