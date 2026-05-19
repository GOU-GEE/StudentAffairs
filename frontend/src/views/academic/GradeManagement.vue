<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧班级列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <div class="px-3 pt-2 pb-2">
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-outline z-10 flex items-center justify-center"><el-icon :size="14"><Search /></el-icon></span>
          <input v-model="searchQuery" placeholder="搜索班级名称..."
                 class="w-full pl-9 pr-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-purple-500/50 focus:ring-2 focus:ring-purple-500/20 transition-all" />
        </div>
      </div>

      <div class="h-px bg-outline-variant/15"></div>

      <!-- 班级列表 -->
      <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
        <div v-for="cls in filteredClasses" :key="cls.name"
             @click="selectClass(cls)"
             class="p-3 rounded-xl cursor-pointer transition-all border"
             :class="selectedClass?.name === cls.name ? 'bg-white shadow-sm border-purple-500/30 ring-1 ring-purple-500/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
          <div class="font-bold text-sm text-on-surface truncate">{{ cls.name }}</div>
          <div class="text-xs text-outline mt-0.5">{{ cls.students.length }} 名学生 · {{ cls.courses.length }} 门课程</div>
        </div>

        <div v-if="filteredClasses.length === 0" class="py-10 text-center text-secondary text-sm">
          暂无班级数据
        </div>
      </div>
    </div>

    <!-- 右侧内容区 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col">
      <transition name="fade" mode="out-in">
        <!-- 空状态 -->
        <div v-if="!selectedClass" key="empty" class="flex flex-col items-center justify-center h-full w-full text-secondary">
          <el-icon :size="64" class="mb-4 opacity-20"><DataAnalysis /></el-icon>
          <p class="text-lg font-medium text-on-surface">成绩管理</p>
          <p class="text-sm mt-2 opacity-80">选择左侧班级查看课程成绩</p>
        </div>

        <!-- 班级详情 -->
        <div v-else key="detail" class="flex flex-col h-full w-full">
          <!-- 头部 -->
          <div class="px-6 py-4 border-b border-outline-variant/30 flex items-center justify-between bg-white/40 flex-shrink-0">
            <div>
              <h3 class="font-bold text-base text-on-surface">{{ selectedClass.name }}</h3>
              <p class="text-xs text-secondary mt-0.5">{{ selectedClass.students.length }} 名学生 · {{ selectedClass.courses.length }} 门课程</p>
            </div>
          </div>

          <!-- 课程列表 -->
          <div class="flex-1 overflow-auto custom-scrollbar min-h-0 p-6">
            <p class="text-xs font-bold text-secondary uppercase tracking-wider mb-4">本班课程</p>
            <div class="grid grid-cols-3 gap-4">
              <div v-for="course in selectedClass.courses" :key="course"
                class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 hover:border-purple-300 hover:shadow-sm transition-all flex items-center justify-center">
                <span class="text-sm font-bold text-on-surface">{{ course }}</span>
              </div>
            </div>
          </div>

          <!-- 底部上传区 -->
          <div class="border-t border-outline-variant/20 bg-white/40 flex-shrink-0">
            <div class="px-6 py-4">
              <div class="border-2 border-dashed border-outline-variant/30 rounded-xl p-6 text-center hover:border-purple-500/40 transition-colors cursor-pointer bg-surface-container-lowest/30">
                <el-icon :size="36" class="text-outline mb-3"><UploadFilled /></el-icon>
                <p class="text-sm text-secondary font-medium">点击或拖拽上传 Excel 成绩文件</p>
                <p class="text-xs text-outline mt-1.5">支持 .xlsx / .xls 格式</p>
                <div class="mt-3 inline-flex items-center gap-1.5 bg-purple-50 text-purple-600 rounded-lg px-3 py-1.5 text-xs font-semibold">
                  <el-icon :size="14"><InfoFilled /></el-icon>
                  提示：Excel 列名需与上方课程名称完全一致（学号、姓名、{{ selectedClass.courses.join('、') }}）
                </div>
              </div>

              <div class="flex justify-end mt-4">
                <button @click="handleImport"
                  class="px-6 py-2.5 bg-purple-500 text-white rounded-xl text-sm font-bold hover:bg-purple-600 transition-colors shadow-sm flex items-center gap-2">
                  <el-icon :size="16"><Upload /></el-icon>确认导入
                </button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, DataAnalysis, UploadFilled, InfoFilled, Upload } from '@element-plus/icons-vue'

// Mock 数据
const MOCK_CLASSES = [
  {
    name: '计算机科学2301班',
    courses: ['高等数学', '线性代数', '离散数学', '数据结构', '操作系统', '数据库原理', '计算机网络', '计算机组成原理', '编译原理', '软件工程'],
    students: [
      { studentId: '202301001', name: '张小明', scores: {} },
      { studentId: '202301002', name: '李小丽', scores: {} },
      { studentId: '202301003', name: '王五', scores: {} },
      { studentId: '202301004', name: '赵六', scores: {} },
      { studentId: '202301005', name: '周燕', scores: {} },
      { studentId: '202301006', name: '吴九', scores: {} },
    ],
  },
  {
    name: '软件工程2302班',
    courses: ['Java程序设计', '软件工程', 'Web前端开发', 'Linux系统管理', 'Python数据分析', '软件测试', '设计模式', '移动应用开发', '云计算基础', 'DevOps实践'],
    students: [
      { studentId: '202302001', name: '钱七', scores: {} },
      { studentId: '202302002', name: '孙八', scores: {} },
      { studentId: '202302003', name: '吴十', scores: {} },
      { studentId: '202302004', name: '郑十一', scores: {} },
    ],
  },
  {
    name: '人工智能2301班',
    courses: ['机器学习', '深度学习', '自然语言处理', '计算机视觉', 'Python编程', '数据挖掘', '强化学习', '知识图谱', '模式识别', '神经网络与深度学习'],
    students: [
      { studentId: '202303001', name: '陈十二', scores: {} },
      { studentId: '202303002', name: '刘十三', scores: {} },
      { studentId: '202303003', name: '黄十四', scores: {} },
    ],
  },
  {
    name: '信息管理与信息系统2301班',
    courses: ['管理学原理', '管理信息系统', '数据分析与可视化', 'ERP系统', 'Python编程', '运筹学', '电子商务', '信息系统安全', '数据仓库与数据挖掘', 'IT项目管理'],
    students: [
      { studentId: '202304001', name: '杨十五', scores: {} },
      { studentId: '202304002', name: '朱十六', scores: {} },
      { studentId: '202304003', name: '马十七', scores: {} },
    ],
  },
  {
    name: '数据科学与大数据技术2301班',
    courses: ['大数据技术基础', 'Hadoop生态系统', 'Spark编程', '数据仓库', 'Python数据分析', '数据可视化', '流式计算', 'NoSQL数据库', '机器学习实战', '数据采集与清洗'],
    students: [
      { studentId: '202305001', name: '胡十八', scores: {} },
      { studentId: '202305002', name: '高十九', scores: {} },
      { studentId: '202305003', name: '林二十', scores: {} },
      { studentId: '202305004', name: '何二一', scores: {} },
    ],
  },
]

const classes = ref([...MOCK_CLASSES])
const searchQuery = ref('')
const selectedClass = ref(null)

const filteredClasses = computed(() => {
  if (!searchQuery.value) return classes.value
  const q = searchQuery.value.toLowerCase()
  return classes.value.filter(c => c.name.toLowerCase().includes(q))
})

const selectClass = (cls) => {
  selectedClass.value = cls
}

const scoreColor = (score) => {
  if (score == null) return 'text-gray-400'
  if (score >= 90) return 'text-green-600'
  if (score >= 80) return 'text-blue-600'
  if (score >= 70) return 'text-amber-600'
  if (score >= 60) return 'text-orange-500'
  return 'text-red-500'
}

const handleImport = () => {
  if (!selectedClass.value) {
    ElMessage.warning('请先选择班级')
    return
  }
  ElMessage.success(`已成功导入 ${selectedClass.value.name} 的成绩数据`)
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
