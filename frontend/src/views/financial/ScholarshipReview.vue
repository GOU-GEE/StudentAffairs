<template>
  <div class="h-full flex flex-col gap-4 min-h-0">

    <!-- 顶部合并卡片：进度 + 统计 + 批次信息 -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm px-6 py-4 flex items-center gap-6 flex-shrink-0">
      <!-- 圆形进度 -->
      <div class="flex items-center gap-3 flex-shrink-0">
        <div class="relative w-14 h-14">
          <svg class="w-14 h-14 -rotate-90" viewBox="0 0 64 64">
            <circle cx="32" cy="32" r="26" fill="none" stroke="#f3f4f6" stroke-width="7"/>
            <circle cx="32" cy="32" r="26" fill="none" stroke="#1d4ed8" stroke-width="7"
              stroke-dasharray="163.4"
              :stroke-dashoffset="163.4 * (1 - reviewProgress / 100)"
              stroke-linecap="round"
              style="transition: stroke-dashoffset 0.6s ease"
            />
          </svg>
          <div class="absolute inset-0 flex items-center justify-center">
            <span class="text-xs font-black text-blue-700">{{ reviewProgress }}%</span>
          </div>
        </div>
        <div>
          <p class="text-xs text-gray-400">资助中心审核进度</p>
          <p class="text-sm font-bold text-gray-700">已完成 {{ currentBatch.reviewed }}/{{ currentBatch.total }}</p>
        </div>
      </div>

      <div class="w-px h-10 bg-gray-100 flex-shrink-0"></div>

      <!-- 各项统计 -->
      <div class="flex items-center gap-6 flex-shrink-0">
        <div v-for="stat in stats" :key="stat.label" class="flex flex-col items-center gap-0.5">
          <span class="text-2xl font-black" :class="stat.color">{{ stat.value }}</span>
          <span class="text-xs text-gray-400">{{ stat.label }}</span>
        </div>
      </div>

      <div class="w-px h-10 bg-gray-100 flex-shrink-0"></div>

      <!-- 批次信息 + 按钮 -->
      <div class="flex items-center gap-4 flex-1 justify-end min-w-0">
        <div class="flex flex-col items-end min-w-0">
          <div class="flex items-center gap-2">
            <span class="text-sm font-bold text-gray-900 truncate">{{ currentBatch.title }}</span>
            <span class="text-xs font-bold px-2 py-0.5 rounded-full bg-green-100 text-green-700 flex-shrink-0">{{ currentBatch.status }}</span>
          </div>
          <div class="flex items-center gap-3 mt-0.5 text-xs text-gray-400">
            <span class="flex items-center gap-1 flex-shrink-0">
              <el-icon :size="11"><Calendar /></el-icon>
              {{ currentBatch.dateRange }}
            </span>
            <span class="flex-shrink-0">本批共 <b class="text-gray-600">{{ currentBatch.total }}</b> 人，已审核 <b class="text-gray-600">{{ currentBatch.reviewed }}</b>，待审核 <b class="text-orange-500">{{ currentBatch.pending }}</b></span>
          </div>
        </div>
        <el-dropdown trigger="click" @command="handleBatchChange">
          <button class="flex-shrink-0 px-4 py-2 border border-gray-200 rounded-xl text-sm text-gray-600 hover:bg-gray-50 transition-colors font-medium flex items-center gap-1">
            切换批次 <el-icon><ArrowDown /></el-icon>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="国家奖学金">2026年春季学期国家奖学金</el-dropdown-item>
              <el-dropdown-item command="国家励志奖学金">2026年春季学期国家励志奖学金</el-dropdown-item>
              <el-dropdown-item command="国家助学金">2026年春季学期国家助学金</el-dropdown-item>
              <el-dropdown-item divided command="校级奖学金">2026年春季学期校级奖学金</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <button class="flex-shrink-0 px-4 py-2 bg-gray-900 text-white rounded-xl text-sm font-semibold hover:bg-gray-700 transition-colors flex items-center gap-1.5">
          <el-icon :size="14"><Download /></el-icon>
          导出数据
        </button>
      </div>
    </div>

    <!-- 三栏主区域 -->
    <div class="flex-1 grid grid-cols-12 gap-4 min-h-0">

      <!-- 左栏：学生列表 (3/12) -->
      <div class="col-span-3 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div class="px-4 pt-4 pb-3 border-b border-gray-100 flex-shrink-0">
          <!-- 标题行：申请学生列表 + 数量 -->
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-1.5">
              <p class="text-sm font-bold text-gray-900">申请学生列表</p>
              <span class="text-xs text-gray-400 font-normal">共 {{ filteredStudents.length }} 条</span>
            </div>
          </div>
          <!-- 搜索框 -->
          <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2 mb-2 hover:border-gray-400 focus-within:border-gray-500 transition-colors">
            <el-icon class="text-gray-300 flex-shrink-0" :size="13"><Search /></el-icon>
            <input
              v-model="searchText"
              class="flex-1 text-xs outline-none bg-transparent placeholder-gray-300 text-gray-700"
              placeholder="搜索学号/姓名"
            />
          </div>
          <!-- 状态筛选 -->
          <div class="flex gap-1 flex-wrap">
            <button
              v-for="f in statusFilters"
              :key="f.value"
              @click="activeFilter = f.value"
              class="px-2.5 py-1 rounded-lg text-xs font-medium transition-all"
              :class="activeFilter === f.value ? 'bg-gray-900 text-white' : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
            >{{ f.label }}</button>
          </div>
        </div>

        <!-- 学生卡片列表（直接滚动，不分页） -->
        <div class="flex-1 min-h-0 overflow-y-auto px-3 py-3 space-y-2">
          <div
            v-for="stu in filteredStudents"
            :key="stu.id"
            @click="selectedStudent = stu; statementExpanded = false; honorsExpanded = false"
            class="p-3 rounded-xl cursor-pointer transition-all border"
            :class="selectedStudent?.id === stu.id
              ? 'border-blue-200 bg-blue-50'
              : 'border-gray-100 hover:border-gray-200 hover:bg-gray-50'"
          >
            <div class="flex items-center gap-2.5">
              <img :src="stu.avatar" class="w-8 h-8 rounded-full object-cover flex-shrink-0" />
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between gap-1">
                  <span class="text-sm font-bold text-gray-900 truncate">{{ stu.name }}</span>
                  <span class="text-xs font-bold flex-shrink-0" :class="statusColor(stu.status)">{{ statusLabel(stu.status) }}</span>
                </div>
                <p class="text-xs text-gray-400 truncate">学号：{{ stu.studentId }}</p>
                <p class="text-xs text-gray-400 truncate">{{ stu.class }}</p>
              </div>
            </div>
            <p class="text-xs text-gray-400 mt-1.5">提交：{{ stu.submitTime }}</p>
          </div>
          <div v-if="filteredStudents.length === 0" class="py-10 text-center text-gray-400 text-xs">暂无数据</div>
        </div>
      </div>

      <!-- 中栏：申请详情 (6/12) -->
      <div class="col-span-6 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div v-if="selectedStudent" class="flex flex-col h-full overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-100 flex-shrink-0">
            <p class="text-sm font-bold text-gray-900">申请信息</p>
          </div>

          <div class="flex-1 overflow-y-auto px-6 py-4 space-y-5">
            <!-- 基本信息 -->
            <div>
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">基本信息</p>

              <!-- 2/5 + 3/5 两列布局 -->
              <div class="flex gap-0">
                <!-- 左列 2/5 -->
                <div class="w-2/5 space-y-3 pr-4 border-r border-gray-100">
                  <div v-for="field in leftFields" :key="field.label" class="flex items-start gap-2">
                    <span class="text-xs text-gray-400 flex-shrink-0 w-16 pt-0.5">{{ field.label }}</span>
                    <span class="text-sm font-semibold text-gray-800 leading-snug">{{ field.value }}</span>
                  </div>
                </div>
                <!-- 右列 3/5 -->
                <div class="w-3/5 space-y-3 pl-4">
                  <div v-for="field in rightFields" :key="field.label" class="flex items-start gap-2">
                    <span class="text-xs text-gray-400 flex-shrink-0 w-20 pt-0.5">{{ field.label }}</span>
                    <span class="text-sm font-semibold text-gray-800 leading-snug">{{ field.value }}</span>
                  </div>
                </div>
              </div>

              <!-- 在校获得荣誉（单独一行，可展开） -->
              <div class="mt-3 pt-3 border-t border-gray-100">
                <p class="text-xs text-gray-400 mb-1">在校获得荣誉</p>
                <p
                  class="text-sm font-semibold text-gray-800 leading-relaxed transition-all"
                  :class="honorsExpanded ? '' : 'line-clamp-1'"
                >2024年校级三好学生 · 全国大学生数学竞赛二等奖 · 优秀学生干部</p>
                <button
                  @click="honorsExpanded = !honorsExpanded"
                  class="text-xs text-blue-600 hover:underline mt-0.5 flex items-center gap-0.5"
                >
                  {{ honorsExpanded ? '收起' : '展开' }}
                  <el-icon :size="11" :class="honorsExpanded ? 'rotate-180' : ''" class="transition-transform"><ArrowDown /></el-icon>
                </button>
              </div>

              <!-- 个人申请陈述（可展开） -->
              <div class="mt-3 pt-3 border-t border-gray-100">
                <p class="text-xs text-gray-400 mb-1">个人申请陈述</p>
                <p
                  class="text-sm text-gray-700 leading-relaxed transition-all"
                  :class="statementExpanded ? '' : 'line-clamp-2'"
                >我在思想、学习和生活中始终保持积极向上的态度，努力提升自我，积极参与各类社会实践与志愿服务活动。本学期加权平均成绩 88.5 分，专业排名第 10 名，获得校级三好学生称号，志愿服务累计 32 小时。特此申请国家励志奖学金，感谢老师的评审。</p>
                <button
                  @click="statementExpanded = !statementExpanded"
                  class="text-xs text-blue-600 mt-1 hover:underline flex items-center gap-0.5"
                >
                  {{ statementExpanded ? '收起' : '展开全部' }}
                  <el-icon :size="11" :class="statementExpanded ? 'rotate-180' : ''" class="transition-transform"><ArrowDown /></el-icon>
                </button>
              </div>
            </div>

            <!-- 申请材料（单行横向滚动） -->
            <div>
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">申请材料</p>
              <div class="flex gap-3 overflow-x-auto pb-1">
                <div
                  v-for="doc in documents"
                  :key="doc.name"
                  class="flex flex-col items-center gap-2 p-3 border border-gray-100 rounded-xl hover:border-blue-200 hover:bg-blue-50/30 cursor-pointer transition-all group flex-shrink-0 w-24"
                >
                  <div
                    class="w-10 h-12 rounded-lg flex items-center justify-center text-white flex-shrink-0"
                    :class="doc.color"
                  >
                    <el-icon :size="20"><Document /></el-icon>
                  </div>
                  <p class="text-xs text-gray-500 text-center leading-tight group-hover:text-blue-600 transition-colors">{{ doc.name }}</p>
                  <p class="text-xs text-gray-300">{{ doc.ext }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="flex-1 flex flex-col items-center justify-center text-gray-300">
          <el-icon :size="48" class="mb-3"><User /></el-icon>
          <p class="text-sm">请从左侧选择学生查看申请详情</p>
        </div>
      </div>

      <!-- 右栏：意见说明 (3/12) -->
      <div class="col-span-3 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div class="px-4 py-4 border-b border-gray-100 flex-shrink-0">
          <p class="text-sm font-bold text-gray-900">意见说明</p>
        </div>

        <div class="flex-1 overflow-y-auto px-4 py-4 space-y-4">
          <!-- 审核意见 -->
          <div class="h-full flex flex-col">
            <div class="flex-1 flex flex-col min-h-0">
              <textarea
                v-model="reviewComment"
                placeholder="请输入详细的审核意见说明..."
                maxlength="500"
                class="flex-1 w-full border-none px-0 py-1 text-sm text-gray-700 outline-none bg-transparent resize-none placeholder-gray-300"
              ></textarea>
              <div class="text-right text-xs text-gray-300 mt-1 flex-shrink-0">{{ reviewComment.length }} / 500</div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="px-4 py-4 border-t border-gray-100 flex-shrink-0 flex items-center gap-2">
          <button @click="handleSave" class="flex-1 py-2.5 border border-gray-200 rounded-xl text-sm font-semibold text-gray-600 hover:bg-gray-50 transition-colors">暂存</button>
          <button @click="handleReject" class="flex-1 py-2.5 border-2 border-red-200 rounded-xl text-sm font-bold text-red-500 hover:bg-red-50 transition-colors">不通过</button>
          <button @click="handlePass" class="flex-1 py-2.5 bg-gray-900 rounded-xl text-sm font-bold text-white hover:bg-gray-700 transition-colors">通过</button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, Download, Search, Document, User, ArrowDown } from '@element-plus/icons-vue'

const handleBatchChange = (command) => {
  currentBatch.value.title = `2026年春季学期${command}申请`
}

// 当前批次信息
const currentBatch = ref({
  title: '2026年春季学期国家励志奖学金申请（普通班）',
  status: '进行中',
  dateRange: '2025-03-29 ~ 2026-04-09',
  total: 128,
  reviewed: 36,
  pending: 92
})

const reviewProgress = computed(() =>
  Math.round((currentBatch.value.reviewed / currentBatch.value.total) * 100)
)

const stats = computed(() => [
  { label: '已审核', value: currentBatch.value.reviewed, color: 'text-gray-900' },
  { label: '待审核', value: currentBatch.value.pending, color: 'text-orange-500' },
  { label: '已通过', value: 24, color: 'text-green-600' },
  { label: '未通过', value: 12, color: 'text-red-500' }
])

// 状态筛选
const statusFilters = [
  { label: '全部', value: 'all' },
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '未通过', value: 'REJECTED' }
]
const activeFilter = ref('all')
const searchText = ref('')

// Mock 学生列表
const students = ref([
  { id: 1, name: '张小明', studentId: '202301042', class: '计算机科学2301班', submitTime: '2026-04-02 14:32', status: 'PENDING', avatar: 'https://i.pravatar.cc/150?u=1' },
  { id: 2, name: '李思远', studentId: '202301057', class: '计算机科学2301班', submitTime: '2026-04-02 14:28', status: 'PENDING', avatar: 'https://i.pravatar.cc/150?u=2' },
  { id: 3, name: '王佳怡', studentId: '202301088', class: '软件工程2302班', submitTime: '2026-04-02 14:20', status: 'PENDING', avatar: 'https://i.pravatar.cc/150?u=3' },
  { id: 4, name: '陈浩宇', studentId: '202301075', class: '计算机科学2301班', submitTime: '2026-04-01 11:15', status: 'APPROVED', avatar: 'https://i.pravatar.cc/150?u=4' },
  { id: 5, name: '刘雨欣', studentId: '202301086', class: '软件工程2302班', submitTime: '2026-04-01 10:42', status: 'REJECTED', avatar: 'https://i.pravatar.cc/150?u=5' },
  { id: 6, name: '赵明轩', studentId: '202301093', class: '计算机科学2302班', submitTime: '2026-04-01 09:30', status: 'PENDING', avatar: 'https://i.pravatar.cc/150?u=6' },
  { id: 7, name: '吴晓彤', studentId: '202301101', class: '软件工程2301班', submitTime: '2026-03-31 16:45', status: 'PENDING', avatar: 'https://i.pravatar.cc/150?u=7' }
])

const selectedStudent = ref(students.value[0])
const statementExpanded = ref(false)
const honorsExpanded = ref(false)

const filteredStudents = computed(() => {
  return students.value.filter(s => {
    const matchFilter = activeFilter.value === 'all' || s.status === activeFilter.value
    const matchSearch = !searchText.value ||
      s.name.includes(searchText.value) ||
      s.studentId.includes(searchText.value)
    return matchFilter && matchSearch
  })
})

// 基本信息：左列（2/5）
const leftFields = computed(() => {
  if (!selectedStudent.value) return []
  return [
    { label: '学号', value: selectedStudent.value.studentId },
    { label: '姓名', value: selectedStudent.value.name },
    { label: '性别', value: '男' },
    { label: '所在班级', value: selectedStudent.value.class },
    { label: '专业排名', value: '10 / 68' }
  ]
})

// 基本信息：右列（3/5）
const rightFields = computed(() => {
  if (!selectedStudent.value) return []
  return [
    { label: '困难认定等级', value: '无困难认定' },
    { label: '本学期加权平均', value: '88.5' },
    { label: '志愿服务时长', value: '32 小时' },
    { label: '家庭年收入（元）', value: '50,000' }
  ]
})

// 申请材料
const documents = ref([
  { name: '申请表', ext: '.pdf', color: 'bg-red-400' },
  { name: '成绩单', ext: '.jpg', color: 'bg-blue-400' },
  { name: '荣誉证书', ext: '.pdf', color: 'bg-red-400' },
  { name: '志愿服务证明', ext: '.jpg', color: 'bg-green-400' },
  { name: '家庭经济情况说明', ext: '.pdf', color: 'bg-red-400' }
])



const reviewDecision = ref('pass')
const reviewComment = ref('')

const statusLabel = (s) => ({ PENDING: '待审核', APPROVED: '已通过', REJECTED: '未通过' }[s] || s)
const statusColor = (s) => ({
  PENDING: 'text-orange-500',
  APPROVED: 'text-green-600',
  REJECTED: 'text-red-500'
}[s] || 'text-gray-400')

const handlePass = () => {
  if (!selectedStudent.value) { ElMessage.warning('请先选择学生'); return }
  selectedStudent.value.status = 'APPROVED'
  currentBatch.value.reviewed++
  currentBatch.value.pending--
  ElMessage.success(`已通过 ${selectedStudent.value.name} 的申请`)
  reviewComment.value = ''
}

const handleReject = () => {
  if (!selectedStudent.value) { ElMessage.warning('请先选择学生'); return }
  selectedStudent.value.status = 'REJECTED'
  currentBatch.value.reviewed++
  currentBatch.value.pending--
  ElMessage.warning(`已驳回 ${selectedStudent.value.name} 的申请`)
  reviewComment.value = ''
}

const handleSave = () => {
  ElMessage.success('已暂存审核意见')
}
</script>
