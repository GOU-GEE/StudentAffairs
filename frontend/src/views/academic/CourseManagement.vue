<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧专业列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <div class="px-3 pt-2 pb-2">
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-outline z-10 flex items-center justify-center"><el-icon :size="14"><Search /></el-icon></span>
          <input v-model="searchQuery" placeholder="搜索专业名称..."
                 class="w-full pl-9 pr-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-purple-500/50 focus:ring-2 focus:ring-purple-500/20 transition-all" />
        </div>
      </div>

      <div class="h-px bg-outline-variant/15"></div>

      <!-- 专业列表 -->
      <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
        <div v-for="major in filteredMajors" :key="major.id"
             @click="selectMajor(major)"
             class="p-3 rounded-xl cursor-pointer transition-all border"
             :class="selectedMajor?.id === major.id ? 'bg-white shadow-sm border-purple-500/30 ring-1 ring-purple-500/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
          <div class="font-bold text-sm text-on-surface truncate">{{ major.name }}</div>
          <div class="text-xs text-outline mt-0.5">{{ major.courses.length }} 门课程</div>
        </div>

        <div v-if="filteredMajors.length === 0" class="py-10 text-center text-secondary text-sm">
          暂无专业数据
        </div>
      </div>
    </div>

    <!-- 右侧课程管理 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col">
      <transition name="fade" mode="out-in">
        <!-- 空状态 -->
        <div v-if="!selectedMajor" key="empty" class="flex flex-col items-center justify-center h-full w-full text-secondary">
          <el-icon :size="64" class="mb-4 opacity-20"><Reading /></el-icon>
          <p class="text-lg font-medium text-on-surface">导入培养方案</p>
          <p class="text-sm mt-2 opacity-80">选择左侧专业查看课程列表</p>
        </div>

        <!-- 专业详情 -->
        <div v-else key="detail" class="flex flex-col h-full w-full">
          <!-- 头部 -->
          <div class="px-6 py-4 border-b border-outline-variant/30 flex items-center justify-between bg-white/40 flex-shrink-0">
            <div>
              <h3 class="font-bold text-base text-on-surface">{{ selectedMajor.name }}</h3>
              <p class="text-xs text-secondary mt-0.5">{{ selectedMajor.courses.length }} 门课程</p>
            </div>
          </div>

          <!-- 课程列表 -->
          <div class="flex-1 overflow-auto custom-scrollbar min-h-0">
            <!-- 表头 -->
            <div class="sticky top-0 z-10 bg-surface-container-lowest/90 backdrop-blur-sm border-b border-outline-variant/15 flex items-center px-6 py-3">
              <span class="flex-1 text-xs font-bold text-secondary uppercase tracking-wider">课程名称</span>
              <span class="w-24 text-center text-xs font-bold text-secondary uppercase tracking-wider">是否必修</span>
              <span class="w-20 text-center text-xs font-bold text-secondary uppercase tracking-wider">学分</span>
              <span class="w-40 text-center text-xs font-bold text-secondary uppercase tracking-wider">开课学期</span>
              <span class="w-20 text-center text-xs font-bold text-secondary uppercase tracking-wider">操作</span>
            </div>

            <!-- 已有课程行 -->
            <div v-for="course in selectedMajor.courses" :key="course.id"
              class="flex items-center px-6 py-3 border-b border-outline-variant/10 hover:bg-surface-container-lowest/50 transition-colors">
              <span class="flex-1 text-sm font-semibold text-on-surface truncate pr-4">{{ course.name }}</span>
              <span class="w-24 text-center">
                <span class="text-xs font-bold px-2 py-0.5 rounded-full"
                  :class="course.required ? 'bg-purple-100 text-purple-700' : 'bg-gray-100 text-gray-600'">
                  {{ course.required ? '必修' : '选修' }}
                </span>
              </span>
              <span class="w-20 text-center text-sm text-on-surface">{{ course.credit }}</span>
              <span class="w-40 text-center text-sm text-secondary">{{ course.semester }}</span>
              <span class="w-20 flex justify-center gap-2">
                <button @click="editCourseInline(course)" class="text-xs font-semibold text-purple-600 hover:text-purple-800">编辑</button>
                <button @click="deleteCourse(course)" class="text-xs font-semibold text-red-500 hover:text-red-700">删除</button>
              </span>
            </div>

            <!-- 内联编辑已有课程行 -->
            <div v-if="editingId && !isAdding" class="flex items-center gap-3 px-6 py-3 border-b border-outline-variant/10 bg-purple-50/30">
              <div class="flex-1"><el-input v-model="editForm.name" placeholder="课程名称" size="small" /></div>
              <div class="w-24"><el-select v-model="editForm.required" size="small" style="width:100%"><el-option label="必修" :value="true" /><el-option label="选修" :value="false" /></el-select></div>
              <div class="w-20"><el-input-number v-model="editForm.credit" :min="0.5" :max="10" :step="0.5" size="small" style="width:100%" /></div>
              <div class="w-40"><el-input v-model="editForm.semester" placeholder="如：2025-2026-1" size="small" /></div>
              <div class="w-20 flex justify-center gap-2">
                <button @click="saveEditInline" class="text-xs font-semibold text-purple-600 hover:text-purple-800">保存</button>
                <button @click="cancelEditInline" class="text-xs font-semibold text-gray-400 hover:text-gray-600">取消</button>
              </div>
            </div>

            <!-- 新增行 -->
            <div v-if="isAdding" class="flex items-center gap-3 px-6 py-3 border-b border-outline-variant/10 bg-purple-50/30">
              <div class="flex-1"><el-input v-model="newForm.name" placeholder="请输入课程名称" size="small" /></div>
              <div class="w-24"><el-select v-model="newForm.required" size="small" style="width:100%"><el-option label="必修" :value="true" /><el-option label="选修" :value="false" /></el-select></div>
              <div class="w-20"><el-input-number v-model="newForm.credit" :min="0.5" :max="10" :step="0.5" size="small" style="width:100%" /></div>
              <div class="w-40"><el-input v-model="newForm.semester" placeholder="如：2025-2026-1" size="small" /></div>
              <div class="w-20"></div>
            </div>
          </div>

          <!-- 底部操作 -->
          <div class="border-t border-outline-variant/20 bg-white/40 flex-shrink-0 px-6 py-4 flex justify-end">
            <button v-if="!isAdding" @click="startAdd"
              class="px-5 py-2.5 bg-purple-500 text-white rounded-xl text-sm font-bold hover:bg-purple-600 transition-colors shadow-sm flex items-center gap-2">
              <el-icon :size="16"><Plus /></el-icon>新建课程
            </button>
            <button v-else @click="finishAdd"
              class="px-5 py-2.5 bg-purple-500 text-white rounded-xl text-sm font-bold hover:bg-purple-600 transition-colors shadow-sm flex items-center gap-2">
              完成
            </button>
          </div>
        </div>
      </transition>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Reading, Plus } from '@element-plus/icons-vue'

// Mock 数据
const MOCK_MAJORS = [
  {
    id: 1,
    name: '计算机科学与技术',
    courses: [
      { id: 101, name: '高等数学', required: true, credit: 5, semester: '2025-2026-1' },
      { id: 102, name: '线性代数', required: true, credit: 3, semester: '2025-2026-1' },
      { id: 103, name: '离散数学', required: true, credit: 4, semester: '2025-2026-1' },
      { id: 104, name: '数据结构', required: true, credit: 4, semester: '2025-2026-1' },
      { id: 105, name: '操作系统', required: true, credit: 4, semester: '2025-2026-2' },
      { id: 106, name: '数据库原理', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 107, name: '计算机网络', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 108, name: '计算机组成原理', required: true, credit: 4, semester: '2025-2026-2' },
      { id: 109, name: '编译原理', required: false, credit: 3, semester: '2026-2027-1' },
      { id: 110, name: '软件工程', required: false, credit: 2, semester: '2026-2027-1' },
    ],
  },
  {
    id: 2,
    name: '软件工程',
    courses: [
      { id: 201, name: 'Java程序设计', required: true, credit: 4, semester: '2025-2026-1' },
      { id: 202, name: '软件工程导论', required: true, credit: 3, semester: '2025-2026-1' },
      { id: 203, name: 'Web前端开发', required: true, credit: 3, semester: '2025-2026-1' },
      { id: 204, name: 'Linux系统管理', required: true, credit: 2, semester: '2025-2026-2' },
      { id: 205, name: '软件测试', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 206, name: '设计模式', required: false, credit: 2, semester: '2026-2027-1' },
      { id: 207, name: '移动应用开发', required: false, credit: 3, semester: '2026-2027-1' },
    ],
  },
  {
    id: 3,
    name: '人工智能',
    courses: [
      { id: 301, name: '机器学习', required: true, credit: 4, semester: '2025-2026-1' },
      { id: 302, name: '深度学习', required: true, credit: 4, semester: '2025-2026-2' },
      { id: 303, name: '自然语言处理', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 304, name: '计算机视觉', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 305, name: 'Python编程', required: true, credit: 3, semester: '2025-2026-1' },
      { id: 306, name: '数据挖掘', required: false, credit: 2, semester: '2026-2027-1' },
      { id: 307, name: '强化学习', required: false, credit: 2, semester: '2026-2027-1' },
      { id: 308, name: '模式识别', required: false, credit: 2, semester: '2026-2027-1' },
    ],
  },
  {
    id: 4,
    name: '信息管理与信息系统',
    courses: [
      { id: 401, name: '管理学原理', required: true, credit: 3, semester: '2025-2026-1' },
      { id: 402, name: '管理信息系统', required: true, credit: 4, semester: '2025-2026-2' },
      { id: 403, name: '数据分析与可视化', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 404, name: 'ERP系统', required: true, credit: 3, semester: '2026-2027-1' },
      { id: 405, name: 'Python编程', required: true, credit: 3, semester: '2025-2026-1' },
      { id: 406, name: '运筹学', required: false, credit: 2, semester: '2026-2027-1' },
      { id: 407, name: '电子商务', required: false, credit: 2, semester: '2026-2027-1' },
    ],
  },
  {
    id: 5,
    name: '数据科学与大数据技术',
    courses: [
      { id: 501, name: '大数据技术基础', required: true, credit: 4, semester: '2025-2026-1' },
      { id: 502, name: 'Hadoop生态系统', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 503, name: 'Spark编程', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 504, name: '数据仓库', required: true, credit: 3, semester: '2025-2026-2' },
      { id: 505, name: 'Python数据分析', required: true, credit: 3, semester: '2025-2026-1' },
      { id: 506, name: 'NoSQL数据库', required: false, credit: 2, semester: '2026-2027-1' },
      { id: 507, name: '数据可视化', required: false, credit: 2, semester: '2026-2027-1' },
      { id: 508, name: '机器学习实战', required: false, credit: 3, semester: '2026-2027-1' },
    ],
  },
]

const majors = ref([...MOCK_MAJORS])
const searchQuery = ref('')
const selectedMajor = ref(null)
const isAdding = ref(false)
const editingId = ref(null)
const newForm = ref({ name: '', required: true, credit: 3, semester: '' })
const editForm = ref({ name: '', required: true, credit: 3, semester: '' })
let nextId = 900

const filteredMajors = computed(() => {
  if (!searchQuery.value) return majors.value
  const q = searchQuery.value.toLowerCase()
  return majors.value.filter(m => m.name.toLowerCase().includes(q))
})

const selectMajor = (major) => {
  selectedMajor.value = major
  isAdding.value = false
  editingId.value = null
}

const startAdd = () => {
  isAdding.value = true
  editingId.value = null
  newForm.value = { name: '', required: true, credit: 3, semester: '' }
}

const finishAdd = () => {
  if (!newForm.value.name.trim()) { ElMessage.warning('请填写课程名称'); return }
  if (!newForm.value.semester.trim()) { ElMessage.warning('请填写开课学期'); return }
  const course = {
    id: nextId++,
    name: newForm.value.name.trim(),
    required: newForm.value.required,
    credit: newForm.value.credit,
    semester: newForm.value.semester.trim(),
  }
  selectedMajor.value.courses.push(course)
  ElMessage.success('课程已添加')
  isAdding.value = false
}

const editCourseInline = (course) => {
  editingId.value = course.id
  isAdding.value = false
  editForm.value = { name: course.name, required: course.required, credit: course.credit, semester: course.semester }
}

const saveEditInline = () => {
  if (!editForm.value.name.trim()) { ElMessage.warning('请填写课程名称'); return }
  if (!editForm.value.semester.trim()) { ElMessage.warning('请填写开课学期'); return }
  const course = selectedMajor.value.courses.find(c => c.id === editingId.value)
  if (course) {
    course.name = editForm.value.name.trim()
    course.required = editForm.value.required
    course.credit = editForm.value.credit
    course.semester = editForm.value.semester.trim()
    ElMessage.success('课程已更新')
  }
  editingId.value = null
}

const cancelEditInline = () => {
  editingId.value = null
}

const deleteCourse = async (course) => {
  try {
    await ElMessageBox.confirm(`确定删除课程「${course.name}」？`, '确认删除', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
    const idx = selectedMajor.value.courses.findIndex(c => c.id === course.id)
    if (idx >= 0) selectedMajor.value.courses.splice(idx, 1)
    ElMessage.success('已删除')
  } catch (e) {}
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
