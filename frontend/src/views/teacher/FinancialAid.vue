<template>
  <div class="h-full flex flex-col gap-4 min-h-0">
    <!-- 顶部合并卡片：统计 -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm px-6 py-4 flex items-center gap-6 flex-shrink-0">
      <div class="flex items-center gap-3 flex-shrink-0">
        <div class="relative w-14 h-14">
          <svg class="w-14 h-14 -rotate-90" viewBox="0 0 64 64">
            <circle cx="32" cy="32" r="26" fill="none" stroke="#f3f4f6" stroke-width="7"/>
            <circle cx="32" cy="32" r="26" fill="none" stroke="#1d4ed8" stroke-width="7"
              stroke-dasharray="163.4" :stroke-dashoffset="163.4 * (1 - reviewProgress / 100)"
              stroke-linecap="round" style="transition: stroke-dashoffset 0.6s ease"/>
          </svg>
          <div class="absolute inset-0 flex items-center justify-center">
            <span class="text-xs font-black text-blue-700">{{ reviewProgress }}%</span>
          </div>
        </div>
        <div>
          <p class="text-xs text-gray-400">辅导员审核进度</p>
          <p class="text-sm font-bold text-gray-700">已完成 {{ reviewed }}/{{ total }}</p>
        </div>
      </div>
      <div class="w-px h-10 bg-gray-100 flex-shrink-0"></div>
      <div class="flex items-center gap-6 flex-shrink-0">
        <div v-for="stat in stats" :key="stat.label" class="flex flex-col items-center gap-0.5">
          <span class="text-2xl font-black" :class="stat.color">{{ stat.value }}</span>
          <span class="text-xs text-gray-400">{{ stat.label }}</span>
        </div>
      </div>
      <div class="w-px h-10 bg-gray-100 flex-shrink-0"></div>
      <div class="flex items-center gap-4 flex-1 justify-end min-w-0">
        <span class="text-xs text-gray-400">本次奖学金评定批次</span>
        <span class="text-sm font-bold text-gray-900">2025-2026学年春季学期</span>
        <button class="flex-shrink-0 px-4 py-2 bg-gray-900 text-white rounded-xl text-sm font-semibold hover:bg-gray-700 transition-colors flex items-center gap-1.5">
          <el-icon :size="14"><Download /></el-icon>导出数据
        </button>
      </div>
    </div>

    <!-- 三栏主区域 -->
    <div class="flex-1 grid grid-cols-12 gap-4 min-h-0">
      <!-- 左栏：学生列表 -->
      <div class="col-span-3 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div class="px-4 pt-4 pb-3 border-b border-gray-100 flex-shrink-0">
          <div class="flex items-center justify-between mb-3">
            <p class="text-sm font-bold text-gray-900">申请学生列表</p>
            <span class="text-xs text-gray-400">共 {{ filteredStudents.length }} 条</span>
          </div>
          <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2 mb-2 hover:border-gray-400 focus-within:border-gray-500 transition-colors">
            <el-icon class="text-gray-300 flex-shrink-0" :size="13"><Search /></el-icon>
            <input v-model="searchText" class="flex-1 text-xs outline-none bg-transparent placeholder-gray-300 text-gray-700" placeholder="搜索学号/姓名" />
          </div>
          <div class="flex gap-1 flex-wrap">
            <button v-for="f in statusFilters" :key="f.value" @click="activeFilter = f.value"
                    class="px-2.5 py-1 rounded-lg text-xs font-medium transition-all"
                    :class="activeFilter === f.value ? 'bg-gray-900 text-white' : 'bg-gray-100 text-gray-500 hover:bg-gray-200'">
              {{ f.label }}
            </button>
          </div>
        </div>
        <div class="flex-1 min-h-0 overflow-y-auto px-3 py-3 space-y-2">
          <div v-for="stu in filteredStudents" :key="stu.id"
               @click="selectedStudent = stu"
               class="p-3 rounded-xl cursor-pointer transition-all border"
               :class="selectedStudent?.id === stu.id ? 'border-blue-200 bg-blue-50' : 'border-gray-100 hover:border-gray-200 hover:bg-gray-50'">
            <div class="flex items-center gap-2.5">
              <img :src="stu.avatar" class="w-8 h-8 rounded-full object-cover flex-shrink-0" />
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between gap-1">
                  <span class="text-sm font-bold text-gray-900 truncate">{{ stu.name }}</span>
                  <span class="text-xs font-bold flex-shrink-0" :class="statusColor(stu.status)">{{ statusLabel(stu.status) }}</span>
                </div>
                <p class="text-xs text-gray-400 truncate">学号：{{ stu.studentId }}</p>
                <p class="text-xs text-gray-400 truncate">{{ stu.className }}</p>
              </div>
            </div>
            <p class="text-xs text-gray-400 mt-1.5">提交：{{ stu.submitTime }}</p>
          </div>
          <div v-if="filteredStudents.length === 0" class="py-10 text-center text-gray-400 text-xs">暂无数据</div>
        </div>
      </div>

      <!-- 中栏：申请详情 -->
      <div class="col-span-6 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div v-if="selectedStudent" class="flex flex-col h-full overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-100 flex-shrink-0">
            <p class="text-sm font-bold text-gray-900">申请信息</p>
          </div>
          <div class="flex-1 overflow-y-auto px-6 py-4 space-y-5">
            <div>
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">基本信息</p>
              <div class="flex gap-0">
                <div class="w-2/5 space-y-3 pr-4 border-r border-gray-100">
                  <div class="flex items-start gap-2">
                    <span class="text-xs text-gray-400 w-16 pt-0.5">学号</span>
                    <span class="text-sm font-semibold text-gray-800">{{ selectedStudent.studentId }}</span>
                  </div>
                  <div class="flex items-start gap-2">
                    <span class="text-xs text-gray-400 w-16 pt-0.5">姓名</span>
                    <span class="text-sm font-semibold text-gray-800">{{ selectedStudent.name }}</span>
                  </div>
                  <div class="flex items-start gap-2">
                    <span class="text-xs text-gray-400 w-16 pt-0.5">班级</span>
                    <span class="text-sm font-semibold text-gray-800">{{ selectedStudent.className }}</span>
                  </div>
                </div>
                <div class="w-3/5 space-y-3 pl-4">
                  <div class="flex items-start gap-2">
                    <span class="text-xs text-gray-400 w-20 pt-0.5">申请类型</span>
                    <span class="text-sm font-semibold text-gray-800">{{ selectedStudent.typeLabel }}</span>
                  </div>
                  <div class="flex items-start gap-2">
                    <span class="text-xs text-gray-400 w-20 pt-0.5">GPA</span>
                    <span class="text-sm font-semibold text-gray-800">{{ selectedStudent.gpa }}</span>
                  </div>
                  <div class="flex items-start gap-2">
                    <span class="text-xs text-gray-400 w-20 pt-0.5">困难等级</span>
                    <span class="text-sm font-semibold text-gray-800">{{ selectedStudent.povertyLevel }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="pt-3 border-t border-gray-100">
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">申请理由</p>
              <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">{{ selectedStudent.reason }}</p>
            </div>
            <div v-if="selectedStudent.reviewComment" class="pt-3 border-t border-gray-100">
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">审核意见</p>
              <p class="text-sm text-gray-700">{{ selectedStudent.reviewComment }}</p>
              <p class="text-xs text-gray-400 mt-1">审核人: {{ selectedStudent.reviewerName }} · {{ selectedStudent.reviewTime }}</p>
            </div>
          </div>
        </div>
        <div v-else class="flex-1 flex flex-col items-center justify-center text-gray-300">
          <el-icon :size="48" class="mb-3"><User /></el-icon>
          <p class="text-sm">请从左侧选择学生查看申请详情</p>
        </div>
      </div>

      <!-- 右栏：审核操作 -->
      <div class="col-span-3 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div class="px-4 py-4 border-b border-gray-100 flex-shrink-0">
          <p class="text-sm font-bold text-gray-900">审核操作</p>
        </div>
        <div class="flex-1 overflow-y-auto px-4 py-4">
          <textarea v-model="reviewComment" placeholder="请输入审核意见..." maxlength="500"
                    class="w-full h-full border-none outline-none bg-transparent resize-none text-sm text-gray-700 placeholder-gray-300"></textarea>
        </div>
        <div class="px-4 py-4 border-t border-gray-100 flex-shrink-0 flex items-center gap-2">
          <button @click="review('REJECTED')"
                  class="flex-1 py-2.5 border-2 border-red-200 rounded-xl text-sm font-bold text-red-500 hover:bg-red-50 transition-colors"
                  :disabled="!selectedStudent || selectedStudent.status !== '待审核'">不通过</button>
          <button @click="review('APPROVED')"
                  class="flex-1 py-2.5 bg-gray-900 rounded-xl text-sm font-bold text-white hover:bg-gray-700 transition-colors"
                  :disabled="!selectedStudent || selectedStudent.status !== '待审核'">通过</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, User, Download } from '@element-plus/icons-vue'

const searchText = ref('')
const activeFilter = ref('all')
const selectedStudent = ref(null)
const reviewComment = ref('')

// Mock 学生申请数据
const students = ref([
  { id:1, name:'张小明', studentId:'202301042', className:'软工2班', avatar:'https://i.pravatar.cc/150?u=1', status:'待审核', typeLabel:'国家励志奖学金', gpa:'3.85', povertyLevel:'困难生', submitTime:'2026-05-15 14:30', reason:'本学年学习成绩优异，GPA 3.85，名列专业前10%。家庭经济困难，父母务农年收入不足3万元，已通过困难认定。在校期间积极参加ACM竞赛和志愿服务活动，获得省级二等奖。特申请国家励志奖学金，以缓解家庭经济压力，专心学业。', reviewComment:'', reviewerName:'', reviewTime:'' },
  { id:2, name:'李四', studentId:'202301043', className:'软工2班', avatar:'https://i.pravatar.cc/150?u=2', status:'待审核', typeLabel:'国家助学金', gpa:'2.75', povertyLevel:'一般困难', submitTime:'2026-05-14 10:15', reason:'家庭经济来源单一，父亲患病需长期服药，医疗支出较大。本人学习成绩中等但态度端正，本学期无挂科记录。希望通过国家助学金减轻家庭负担，保证正常学业不中断。', reviewComment:'', reviewerName:'', reviewTime:'' },
  { id:3, name:'王五', studentId:'202301044', className:'软工1班', avatar:'https://i.pravatar.cc/150?u=3', status:'已通过', typeLabel:'校级奖学金', gpa:'3.52', povertyLevel:'无', submitTime:'2026-05-12 09:00', reason:'本学年积极参加校园足球联赛并获得最佳射手荣誉，学业成绩稳步提升，各科均分从上学期的72分提升至本学期80分。特申请校级奖学金以资鼓励。', reviewComment:'该生进步明显，综合素质突出，同意推荐。', reviewerName:'李辅导员', reviewTime:'2026-05-13 16:30' },
  { id:4, name:'赵六', studentId:'202301045', className:'计科3班', avatar:'https://i.pravatar.cc/150?u=4', status:'已通过', typeLabel:'国家助学金', gpa:'3.12', povertyLevel:'困难生', submitTime:'2026-05-11 15:20', reason:'单亲家庭，母亲一人抚养本人和妹妹，家庭收入微薄。本人努力学习，课余时间勤工助学补贴家用。希望能够获得国家助学金支持。', reviewComment:'情况属实，家庭确有困难。', reviewerName:'李辅导员', reviewTime:'2026-05-12 10:00' },
  { id:5, name:'钱七', studentId:'202301050', className:'软工1班', avatar:'https://i.pravatar.cc/150?u=5', status:'未通过', typeLabel:'国家励志奖学金', gpa:'2.95', povertyLevel:'困难生', submitTime:'2026-05-10 11:45', reason:'本学年学习努力，但在数据结构课程中挂科，目前正在补考准备中。家庭经济困难属实，希望能给予资助。', reviewComment:'该生有挂科记录，不符合国家励志奖学金评选条件，建议转申国家助学金。', reviewerName:'李辅导员', reviewTime:'2026-05-11 14:20' },
  { id:6, name:'孙八', studentId:'202301046', className:'计科3班', avatar:'https://i.pravatar.cc/150?u=6', status:'待审核', typeLabel:'校级奖学金', gpa:'3.68', povertyLevel:'无', submitTime:'2026-05-16 08:30', reason:'本学年在蓝桥杯程序设计竞赛中获得省级二等奖，学习成绩稳定在专业前15%。积极参与学院实验室科研项目，完成了基于深度学习的校园导航系统。特申请校级奖学金。', reviewComment:'', reviewerName:'', reviewTime:'' },
])

const statusFilters = [
  { label: '全部', value: 'all' },
  { label: '待审核', value: '待审核' },
  { label: '已通过', value: '已通过' },
  { label: '未通过', value: '未通过' },
]

const filteredStudents = computed(() => {
  let list = students.value
  if (activeFilter.value !== 'all') list = list.filter(s => s.status === activeFilter.value)
  if (searchText.value) {
    const q = searchText.value.toLowerCase()
    list = list.filter(s => s.name.includes(q) || s.studentId.includes(q))
  }
  return list
})

const total = computed(() => students.value.length)
const reviewed = computed(() => students.value.filter(s => s.status !== '待审核').length)
const reviewProgress = computed(() => total.value > 0 ? Math.round((reviewed.value / total.value) * 100) : 0)

const stats = computed(() => [
  { label: '全部', value: total.value, color: 'text-gray-900' },
  { label: '待审核', value: students.value.filter(s => s.status === '待审核').length, color: 'text-orange-500' },
  { label: '已通过', value: students.value.filter(s => s.status === '已通过').length, color: 'text-green-600' },
  { label: '未通过', value: students.value.filter(s => s.status === '未通过').length, color: 'text-red-500' },
])

const review = (status) => {
  if (!selectedStudent.value) { ElMessage.warning('请先选择学生'); return }
  selectedStudent.value.status = status === 'APPROVED' ? '已通过' : '未通过'
  selectedStudent.value.reviewComment = reviewComment.value || (status === 'APPROVED' ? '同意申请' : '不符合条件')
  selectedStudent.value.reviewerName = sessionStorage.getItem('userName') || '李辅导员'
  selectedStudent.value.reviewTime = new Date().toLocaleDateString('zh-CN', { month:'short', day:'numeric', hour:'2-digit', minute:'2-digit' })
  ElMessage.success(status === 'APPROVED' ? '已通过' : '已驳回')
  reviewComment.value = ''
}

const statusLabel = (s) => s
const statusColor = (s) => ({ '待审核':'text-orange-500', '已通过':'text-green-600', '未通过':'text-red-500' }[s] || 'text-gray-400')
</script>
