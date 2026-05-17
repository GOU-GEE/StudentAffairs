<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="openAddDialog" class="bg-emerald-500 text-white hover:bg-emerald-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Plus /></el-icon>新建评优项目
      </button>
    </teleport>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in statList" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 flex-1">
      <!-- Honors Projects -->
      <div class="bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">评优项目</h3>
        <div class="space-y-3">
          <div v-for="project in projects" :key="project.id" class="bg-surface rounded-xl p-4 border border-outline-variant/15">
            <div class="flex justify-between items-start mb-2">
              <h4 class="font-semibold text-on-surface">{{ project.name }}</h4>
              <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="project.status === '进行中' ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-500'">{{ project.status }}</span>
            </div>
            <p class="text-xs text-secondary mb-3">{{ project.description }}</p>
            <div class="flex items-center text-xs text-secondary gap-4">
              <span>名额：{{ project.quota }}人</span>
              <span>已报：{{ project.applied }}人</span>
              <span>截止：{{ project.deadline }}</span>
            </div>
            <div class="mt-3 flex gap-2">
              <button @click="viewCandidates(project)" class="text-xs font-semibold text-emerald-600 hover:text-emerald-800 transition-colors">查看候选</button>
              <button class="text-xs font-semibold text-secondary hover:text-emerald-600 transition-colors">编辑</button>
              <button @click="toggleProject(project)" class="text-xs font-semibold text-secondary hover:text-emerald-600 transition-colors">
                {{ project.status === '进行中' ? '关闭' : '开放' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Candidates List -->
      <div class="bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">
          {{ selectedProject ? `候选名单 - ${selectedProject.name}` : '请选择评优项目' }}
        </h3>
        <div v-if="selectedProject" class="space-y-2">
          <div v-for="c in candidates" :key="c.id" class="bg-surface rounded-xl p-4 border border-outline-variant/15 flex items-center gap-4">
            <div class="flex-1 min-w-0">
              <h4 class="font-semibold text-[0.875rem] text-on-surface">{{ c.studentName }}</h4>
              <p class="text-xs text-secondary">{{ c.studentId }} | {{ c.className }} | GPA {{ c.gpa }}</p>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="c.status === '已入选' ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700'">{{ c.status }}</span>
              <button v-if="c.status === '候选'" @click="selectCandidate(c)" class="text-xs font-semibold text-emerald-600 hover:text-emerald-800 transition-colors">入选</button>
            </div>
          </div>
        </div>
        <div v-else class="flex items-center justify-center h-48 text-secondary text-sm">
          点击左侧项目的「查看候选」查看候选人
        </div>
      </div>
    </div>

    <!-- Add Project Dialog -->
    <el-dialog v-model="dialogVisible" title="新建评优项目" width="480px" destroy-on-close>
      <el-form :model="form" label-position="top">
        <el-form-item label="项目名称" required>
          <el-input v-model="form.name" placeholder="如：三好学生、优秀学生干部" />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="评选条件和要求" />
        </el-form-item>
        <div class="grid grid-cols-2 gap-x-4">
          <el-form-item label="名额" required>
            <el-input-number v-model="form.quota" :min="1" :max="100" class="w-full" />
          </el-form-item>
          <el-form-item label="截止日期">
            <el-date-picker v-model="form.deadline" type="date" class="w-full" placeholder="选择日期" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <button @click="dialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="saveProject" class="px-6 py-2 bg-emerald-500 text-white rounded-lg text-sm font-bold hover:bg-emerald-600 transition-colors">创建</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const dialogVisible = ref(false)
const selectedProject = ref(null)
const form = ref({ name: '', description: '', quota: 10, deadline: '' })

const projects = ref([
  { id: 1, name: '三好学生', description: '综合表现优秀，无挂科记录，无违纪行为', quota: 10, applied: 25, deadline: '2026-06-01', status: '进行中' },
  { id: 2, name: '优秀学生干部', description: '担任学生干部满一年，工作表现突出', quota: 5, applied: 12, deadline: '2026-06-15', status: '进行中' },
  { id: 3, name: '优秀共青团员', description: '积极参加团组织活动，发挥模范带头作用', quota: 8, applied: 18, deadline: '2026-05-30', status: '进行中' },
])

const candidates = ref([
  { id: 1, studentId: '202301042', studentName: '张小明', className: '软工2班', gpa: 3.85, status: '候选' },
  { id: 2, studentId: '202301044', studentName: '王五', className: '软工1班', gpa: 3.52, status: '已入选' },
  { id: 3, studentId: '202301046', studentName: '孙七', className: '软工2班', gpa: 3.71, status: '候选' },
  { id: 4, studentId: '202301048', studentName: '刘芳', className: '文学院1班', gpa: 3.64, status: '候选' },
])

const statList = computed(() => [
  { label: '评优项目', value: projects.value.length },
  { label: '进行中', value: projects.value.filter(p => p.status === '进行中').length },
  { label: '候选总人数', value: candidates.value.length },
  { label: '已入选', value: candidates.value.filter(c => c.status === '已入选').length },
])

const openAddDialog = () => {
  form.value = { name: '', description: '', quota: 10, deadline: '' }
  dialogVisible.value = true
}

const saveProject = () => {
  if (!form.value.name) { ElMessage.warning('项目名称为必填'); return }
  ElMessage.success('评优项目已创建')
  dialogVisible.value = false
}

const viewCandidates = (project) => {
  selectedProject.value = project
}

const selectCandidate = (candidate) => {
  candidate.status = '已入选'
  ElMessage.success(`${candidate.studentName} 已入选`)
}

const toggleProject = (project) => {
  project.status = project.status === '进行中' ? '已关闭' : '进行中'
  ElMessage.success(`项目已${project.status === '进行中' ? '重新开放' : '关闭'}`)
}
</script>
