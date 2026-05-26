<template>
  <div class="h-full flex flex-col gap-4 min-h-0">
    <!-- 顶部：批次信息 -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm px-6 py-4 flex items-center gap-6 flex-shrink-0">
      <div class="flex items-center gap-3">
        <div class="text-sm font-bold text-gray-900">{{ currentBatch.title }}</div>
        <span class="text-xs font-bold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700">{{ currentBatch.status }}</span>
      </div>
      <div class="flex items-center gap-3 text-xs text-gray-400">
        <span>{{ currentBatch.dateRange }}</span>
        <span>共 <b class="text-gray-600">{{ batchStats.total }}</b> 人，已审核 <b class="text-gray-600">{{ batchStats.reviewed }}</b>，待审核 <b class="text-orange-500">{{ batchStats.pending }}</b></span>
      </div>
      <div class="flex-1"></div>
      <el-dropdown trigger="click" @command="handleBatchChange" :teleported="false">
        <button class="px-4 py-2 border border-gray-200 rounded-xl text-sm text-gray-600 hover:bg-gray-50 transition-colors font-medium flex items-center gap-1">
          切换批次 <el-icon><ArrowDown /></el-icon>
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="国家奖学金">国家奖学金</el-dropdown-item>
            <el-dropdown-item command="国家励志奖学金">国家励志奖学金</el-dropdown-item>
            <el-dropdown-item command="国家助学金">国家助学金</el-dropdown-item>
            <el-dropdown-item command="校级奖学金">校级奖学金</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 三栏主区域 -->
    <div class="flex-1 grid grid-cols-12 gap-4 min-h-0">
      <!-- 左栏：学生列表 (3/12) -->
      <div class="col-span-3 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div class="px-4 pt-4 pb-3 border-b border-gray-100 flex-shrink-0">
          <div class="flex items-center justify-between mb-3">
            <p class="text-sm font-bold text-gray-900">申请学生列表</p>
            <span class="text-xs text-gray-400">共 {{ filteredStudents.length }} 条</span>
          </div>
          <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2 mb-2">
            <el-icon class="text-gray-300 flex-shrink-0" :size="13"><Search /></el-icon>
            <input v-model="searchText" class="flex-1 text-xs outline-none bg-transparent placeholder-gray-300 text-gray-700" placeholder="搜索学号/姓名" />
          </div>
          <div class="flex gap-1 flex-wrap">
            <button v-for="f in statusFilters" :key="f.value" @click="activeFilter = f.value"
              class="px-2.5 py-1 rounded-lg text-xs font-medium transition-all"
              :class="activeFilter === f.value ? 'bg-amber-500 text-white' : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
            >{{ f.label }}</button>
          </div>
        </div>
        <div class="flex-1 min-h-0 overflow-y-auto px-3 py-3 space-y-2">
          <div v-for="stu in filteredStudents" :key="stu.id"
            @click="selectStudent(stu)"
            class="p-3 rounded-xl cursor-pointer transition-all border"
            :class="selectedStudent?.id === stu.id ? 'border-amber-200 bg-amber-50' : 'border-gray-100 hover:border-gray-200 hover:bg-gray-50'">
            <div class="flex items-center gap-2.5">
              <div class="w-8 h-8 rounded-full bg-amber-500 text-white flex items-center justify-center font-bold text-sm flex-shrink-0">{{ stu.name.charAt(0) }}</div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between gap-1">
                  <span class="text-sm font-bold text-gray-900 truncate">{{ stu.name }}</span>
                  <span class="text-xs font-bold flex-shrink-0" :class="statusColor(stu.status)">{{ statusLabel(stu.status) }}</span>
                </div>
                <p class="text-xs text-gray-400 truncate">{{ stu.studentId }} · {{ stu.className }}</p>
              </div>
            </div>
          </div>
          <div v-if="filteredStudents.length === 0" class="py-10 text-center text-gray-400 text-xs">暂无数据</div>
        </div>
      </div>

      <!-- 中栏：申请详情 (6/12) -->
      <div class="col-span-6 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div v-if="selectedStudent" class="flex flex-col h-full overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-100 flex-shrink-0 flex items-center justify-between">
            <p class="text-sm font-bold text-gray-900">申请信息</p>
            <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="statusBadgeClass(selectedStudent.status)">{{ statusLabel(selectedStudent.status) }}</span>
          </div>

          <div class="flex-1 overflow-y-auto px-6 py-4 space-y-4">
            <!-- 申请奖助类别 -->
            <div>
              <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">申请奖助类别</label>
              <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.aidType }}</p>
            </div>

            <!-- 姓名 + 学号 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">姓名</label>
                <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.name }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">学号</label>
                <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.studentId }}</p>
              </div>
            </div>

            <!-- 所在班级 + 困难认定等级 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">所在班级</label>
                <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.className }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">困难认定等级</label>
                <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.povertyLevel }}</p>
              </div>
            </div>

            <!-- 家庭年收入 + 本学期加权平均成绩 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">家庭年收入（元）</label>
                <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.annualIncome }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">本学期加权平均成绩</label>
                <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.avgScore }}</p>
              </div>
            </div>

            <!-- 专业排名 + 志愿服务时长 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">专业排名</label>
                <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.rank }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5">志愿服务时长</label>
                <p class="text-sm font-semibold text-gray-800">{{ selectedStudent.volunteerHours }}</p>
              </div>
            </div>

            <!-- 在校获得荣誉 -->
            <div class="pt-2 border-t border-gray-100">
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">在校获得荣誉</p>
              <p class="text-sm text-gray-700 leading-relaxed" :class="honorsExpanded ? '' : 'line-clamp-2'">{{ selectedStudent.honors || '暂无' }}</p>
              <button v-if="selectedStudent.honors && selectedStudent.honors.length > 60"
                @click="honorsExpanded = !honorsExpanded"
                class="text-xs text-amber-600 hover:underline mt-1 flex items-center gap-0.5">
                {{ honorsExpanded ? '收起' : '展开' }}
                <el-icon :size="11" :class="honorsExpanded ? 'rotate-180' : ''" class="transition-transform"><ArrowDown /></el-icon>
              </button>
            </div>

            <!-- 个人申请陈述 -->
            <div class="pt-2 border-t border-gray-100">
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">个人申请陈述</p>
              <p class="text-sm text-gray-700 leading-relaxed" :class="statementExpanded ? '' : 'line-clamp-3'">{{ selectedStudent.statement }}</p>
              <button @click="statementExpanded = !statementExpanded"
                class="text-xs text-amber-600 hover:underline mt-1 flex items-center gap-0.5">
                {{ statementExpanded ? '收起' : '展开全部' }}
                <el-icon :size="11" :class="statementExpanded ? 'rotate-180' : ''" class="transition-transform"><ArrowDown /></el-icon>
              </button>
            </div>
          </div>
        </div>

        <div v-else class="flex-1 flex flex-col items-center justify-center text-gray-300">
          <el-icon :size="48" class="mb-3"><User /></el-icon>
          <p class="text-sm">请从左侧选择学生查看申请详情</p>
        </div>
      </div>

      <!-- 右栏：审核操作 (3/12) -->
      <div class="col-span-3 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
        <div class="px-4 py-4 border-b border-gray-100 flex-shrink-0">
          <p class="text-sm font-bold text-gray-900">审核意见</p>
        </div>
        <div class="flex-1 overflow-y-auto px-4 py-4">
          <textarea v-model="reviewComment" placeholder="请输入审核意见..." maxlength="500"
            :disabled="selectedStudent?.status !== 'PENDING'"
            class="w-full h-full border-none px-0 py-1 text-sm text-gray-700 outline-none bg-transparent resize-none placeholder-gray-300 disabled:text-gray-400 disabled:bg-transparent"
          ></textarea>
        </div>
        <div class="px-4 py-4 border-t border-gray-100 flex-shrink-0 flex items-center gap-2" v-if="selectedStudent?.status === 'PENDING'">
          <button @click="handleReject" class="flex-1 py-2.5 border-2 border-red-200 rounded-xl text-sm font-bold text-red-500 hover:bg-red-50 transition-colors">不通过</button>
          <button @click="handlePass" class="flex-1 py-2.5 bg-amber-500 rounded-xl text-sm font-bold text-white hover:bg-amber-600 transition-colors">通过</button>
        </div>
        <div v-else-if="selectedStudent" class="px-4 py-4 border-t border-gray-100 flex-shrink-0 text-center text-sm font-bold" :class="selectedStudent.status === 'APPROVED' ? 'text-green-600' : 'text-red-500'">
          {{ selectedStudent.status === 'APPROVED' ? '已通过' : '已驳回' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, User, ArrowDown } from '@element-plus/icons-vue'
import request from '@/utils/request'

// Mock 数据
const MOCK_STUDENTS = [
  {
    id: 1, name: '张小明', studentId: '202301042', className: '计算机科学2301班',
    aidType: '国家励志奖学金', povertyLevel: '特别困难', annualIncome: '18,000',
    avgScore: '88.5', rank: '10/150', volunteerHours: '32小时',
    honors: '2024年校级三好学生 · 全国大学生数学竞赛二等奖 · 优秀学生干部 · 2023年优秀共青团员',
    statement: '我在思想、学习和生活中始终保持积极向上的态度，努力提升自我，积极参与各类社会实践与志愿服务活动。本学期加权平均成绩88.5分，专业排名第10名，获得校级三好学生称号，志愿服务累计32小时。家庭经济困难，父亲务农，母亲身体不好需要长期服药，弟弟还在读高中。特此申请国家励志奖学金，感谢老师的评审。',
    status: 'PENDING',
  },
  {
    id: 2, name: '李小丽', studentId: '202301015', className: '软件工程2302班',
    aidType: '国家奖学金', povertyLevel: '一般困难', annualIncome: '25,000',
    avgScore: '92.3', rank: '1/150', volunteerHours: '48小时',
    honors: '蓝桥杯省级一等奖 · 校级优秀学生标兵 · 优秀班干部 · 英语六级证书',
    statement: '本人学习刻苦，成绩优异，大一至大二学年连续保持专业第一名。积极参加各类学科竞赛，获得蓝桥杯省级一等奖。同时注重全面发展，担任班级学习委员，积极组织学习交流活动。家庭收入较低，父母均为普通工人。希望获得国家奖学金以减轻家庭负担，更加专注于学业。',
    status: 'PENDING',
  },
  {
    id: 3, name: '王五', studentId: '202301078', className: '计算机科学2301班',
    aidType: '国家助学金', povertyLevel: '特别困难', annualIncome: '12,000',
    avgScore: '76.8', rank: '78/150', volunteerHours: '56小时',
    honors: '优秀志愿者 · 校园服务之星',
    statement: '家庭经济条件较为困难，父母离异，跟随母亲生活，母亲在工厂做临时工，收入不稳定。虽然学习成绩中等，但我一直在努力提升，本学期成绩较上学期有明显进步。同时积极参与志愿服务，累计56小时。希望申请国家助学金缓解经济压力。',
    status: 'PENDING',
  },
  {
    id: 4, name: '赵六', studentId: '202301056', className: '信息管理与信息系统2301班',
    aidType: '国家励志奖学金', povertyLevel: '一般困难', annualIncome: '22,000',
    avgScore: '86.2', rank: '15/120', volunteerHours: '24小时',
    honors: '校优秀学生干部 · 征文比赛二等奖',
    statement: '我来自农村家庭，家庭收入主要靠父亲外出打工。在校期间学习认真，成绩名列前茅，同时担任学生会干部，组织过多场校园活动。希望申请国家励志奖学金以减轻家庭经济压力。',
    status: 'APPROVED',
  },
  {
    id: 5, name: '周燕', studentId: '202301032', className: '人工智能2301班',
    aidType: '校级奖学金', povertyLevel: '不困难', annualIncome: '60,000',
    avgScore: '90.1', rank: '3/80', volunteerHours: '16小时',
    honors: 'ACM-ICPC省级银奖 · 校级三好学生 · 优秀共青团员',
    statement: '学习认真刻苦，专业成绩班级排名前三。积极参加ACM竞赛并获得省级银奖。父母均为普通职工，收入尚可但需同时供养我和妹妹上大学。申请校级奖学金以减轻家庭负担。',
    status: 'APPROVED',
  },
  {
    id: 6, name: '孙八', studentId: '202301089', className: '数据科学与大数据技术2301班',
    aidType: '国家助学金', povertyLevel: '特别困难', annualIncome: '8,000',
    avgScore: '72.5', rank: '95/140', volunteerHours: '68小时',
    honors: '优秀志愿者 · 勤工助学先进个人',
    statement: '来自偏远山区，家庭为低保户。父母年迈多病，家中还有一个弟弟在读初中。在校期间积极参加勤工助学和志愿服务活动，被评为勤工助学先进个人。因基础薄弱成绩有待提高，但一直在努力追赶。特申请国家助学金。',
    status: 'REJECTED',
  },
]

const students = ref([])
const selectedStudent = ref(null)
const searchText = ref('')
const activeFilter = ref('all')
const reviewComment = ref('')
const statementExpanded = ref(false)
const honorsExpanded = ref(false)

const currentBatch = ref({
  title: '国家励志奖学金',
  status: '进行中',
  dateRange: '2026-03-29 ~ 2026-04-09',
})

const getScholarshipTypeLabel = (type) => {
  const typeMap = {
    nat_scholarship: '国家奖学金',
    nat_incentive: '国家励志奖学金',
    nat_aid: '国家助学金',
    school_scholarship: '学校奖学金',
    school_aid: '学校助学金'
  }
  return typeMap[type] || type
}

const loadCenterData = async () => {
  try {
    const res = await request.get('/api/applications/all')
    if (res.data.code === 200 && Array.isArray(res.data.data)) {
      const rawList = res.data.data.filter(item => 
        item.type === 'SCHOLARSHIP' && 
        (item.status === 'COUNSELOR_APPROVED' || item.status === 'APPROVED' || item.status === 'REJECTED')
      )
      
      const mappedList = rawList.map(item => {
        let detail = {}
        try {
          detail = JSON.parse(item.reason)
        } catch (e) {
          detail = { statement: item.reason, honors: '', povertyLevel: 'none', gpa: '3.0' }
        }
        
        const povertyMap = { none: '无', A: '特别困难', B: '困难', C: '一般困难' }
        const povLabel = povertyMap[detail.povertyLevel] || detail.povertyLevel || '无困难认定'
        
        let uiStatus = 'PENDING'
        if (item.status === 'APPROVED') {
          uiStatus = 'APPROVED'
        } else if (item.status === 'REJECTED') {
          uiStatus = 'REJECTED'
        }
        
        return {
          id: item.id,
          name: item.studentName,
          studentId: item.studentId,
          className: detail.classGrade || '软工2班',
          aidType: getScholarshipTypeLabel(detail.scholarType) || item.title,
          povertyLevel: povLabel,
          annualIncome: detail.familyIncome || '未填写',
          avgScore: detail.gpa || '3.0',
          rank: detail.rank ? `${detail.rank}/${detail.total || 150}` : '未填写',
          volunteerHours: detail.volunteer || '未填写',
          honors: detail.honors || '暂无',
          statement: detail.statement || item.reason,
          status: uiStatus,
          rawStatus: item.status,
          reviewComment: item.reviewComment,
          rawItem: item
        }
      })
      
      students.value = mappedList
      
      if (selectedStudent.value) {
        selectedStudent.value = students.value.find(s => s.id === selectedStudent.value.id) || null
      }
    }
  } catch (e) {
    console.error('Failed to load center data', e)
  }
}

onMounted(() => {
  loadCenterData()
})

const batchStats = computed(() => {
  const total = filteredStudents.value.length
  const reviewed = filteredStudents.value.filter(s => s.status !== 'PENDING').length
  const pending = filteredStudents.value.filter(s => s.status === 'PENDING').length
  return { total, reviewed, pending }
})

const statusFilters = [
  { label: '全部', value: 'all' },
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
]

const filteredStudents = computed(() => {
  let list = students.value
  const batchTitle = currentBatch.value.title
  
  // Filter by active batch
  list = list.filter(s => s.aidType.includes(batchTitle) || s.rawItem.title.includes(batchTitle))
  
  if (activeFilter.value !== 'all') list = list.filter(s => s.status === activeFilter.value)
  if (searchText.value) {
    const q = searchText.value.toLowerCase()
    list = list.filter(s => s.name.includes(q) || s.studentId.includes(q))
  }
  return list
})

const statusLabel = (s) => ({ PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }[s] || s)
const statusColor = (s) => ({ PENDING: 'text-orange-500', APPROVED: 'text-green-600', REJECTED: 'text-red-500' }[s] || 'text-gray-400')
const statusBadgeClass = (s) => ({
  PENDING: 'bg-orange-100 text-orange-700',
  APPROVED: 'bg-green-100 text-green-700',
  REJECTED: 'bg-red-100 text-red-700',
}[s] || '')

const selectStudent = (stu) => {
  selectedStudent.value = stu
  statementExpanded.value = false
  honorsExpanded.value = false
  reviewComment.value = ''
}

const handlePass = async () => {
  if (!selectedStudent.value) return
  try {
    const res = await request.put(`/api/applications/${selectedStudent.value.id}/review`, {
      status: 'APPROVED',
      comment: reviewComment.value || '准予通过，表现优异。',
      reviewerName: '学院资助管理中心'
    })
    
    if (res.data.code === 200) {
      ElMessage.success(`已通过 ${selectedStudent.value.name} 的最终审核`)
      reviewComment.value = ''
      await loadCenterData()
    } else {
      ElMessage.error(res.data.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('网络请求异常')
  }
}

const handleReject = async () => {
  if (!selectedStudent.value) return
  try {
    const res = await request.put(`/api/applications/${selectedStudent.value.id}/review`, {
      status: 'REJECTED',
      comment: reviewComment.value || '不符合最终资助评定标准。',
      reviewerName: '学院资助管理中心'
    })
    
    if (res.data.code === 200) {
      ElMessage.warning(`已驳回 ${selectedStudent.value.name} 的最终申请`)
      reviewComment.value = ''
      await loadCenterData()
    } else {
      ElMessage.error(res.data.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('网络请求异常')
  }
}

const handleBatchChange = (cmd) => {
  currentBatch.value.title = cmd
  selectedStudent.value = null
  activeFilter.value = 'all'
  searchText.value = ''
  ElMessage.success(`已切换至${cmd}`)
}
</script>
