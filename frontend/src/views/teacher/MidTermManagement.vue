<template>
  <div class="mid-term-management space-y-6">
    <!-- Top Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div v-for="stat in stats" :key="stat.label" class="glass-card p-6 rounded-3xl border border-white/20 shadow-lg">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-2xl flex items-center justify-center text-xl" :class="stat.bg">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div>
            <p class="text-xs font-bold text-secondary uppercase tracking-widest">{{ stat.label }}</p>
            <p class="text-2xl font-black text-on-surface">{{ stat.value }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Management Area -->
    <div class="glass-card rounded-3xl border border-white/20 shadow-xl overflow-hidden bg-white/60">
      <div class="p-6 border-b border-outline-variant/10 flex flex-wrap items-center justify-between gap-4">
        <div class="flex items-center gap-4 flex-1">
          <el-input
            v-model="searchQuery"
            placeholder="搜索姓名、学号、班级..."
            class="max-w-xs modern-input"
            clearable
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-select v-model="statusFilter" placeholder="状态筛选" class="w-36">
            <el-option label="全部状态" value="all" />
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </div>
        
        <div class="flex items-center gap-3">
          <el-button type="primary" plain round @click="showDesign = true">
            <el-icon class="mr-1"><Edit /></el-icon> 设计问卷
          </el-button>
          <el-button type="primary" plain round @click="batchAudit">批量审核</el-button>
          <el-button type="primary" round @click="exportExcel">
            <el-icon class="mr-1"><Download /></el-icon> 导出数据
          </el-button>
          <el-button round @click="showConfig = true">
            <el-icon class="mr-1"><Setting /></el-icon> 配置规则
          </el-button>
        </div>
      </div>

      <!-- Table -->
      <el-table :data="filteredList" class="midterm-table" style="width: 100%">
        <el-table-column type="selection" width="55" />
        <el-table-column label="学生信息" width="220">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center font-bold text-primary text-xs">
                {{ row.name.charAt(0) }}
              </div>
              <div>
                <div class="font-bold text-sm">{{ row.name }}</div>
                <div class="text-[10px] text-outline uppercase tracking-wider">{{ row.studentId }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="class" label="班级" />
        <el-table-column label="自评/绩点" width="150">
          <template #default="{ row }">
            <div class="space-y-1">
              <el-tag size="small" effect="plain" type="info">{{ row.selfEval }}</el-tag>
              <div class="text-[10px] text-secondary font-bold">GPA: {{ row.gpa }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column label="审核状态" width="120">
          <template #default="{ row }">
            <div class="flex items-center gap-1.5">
              <div class="w-1.5 h-1.5 rounded-full" :class="statusBadge(row.status)"></div>
              <span class="text-xs font-bold">{{ statusLabel(row.status) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">查看鉴定</el-button>
            <el-dropdown trigger="click">
              <el-button link type="primary" class="ml-2">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>导出 PDF</el-dropdown-item>
                  <el-dropdown-item divided class="text-error">驳回重填</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="p-6 border-t border-outline-variant/10 flex justify-center">
        <el-pagination background layout="prev, pager, next" :total="100" />
      </div>
    </div>

    <!-- Detail Modal -->
    <el-dialog
      v-model="showDetail"
      title="中期鉴定详情与审核"
      width="800px"
      destroy-on-close
      class="glass-dialog"
    >
      <div v-if="selectedStudent" class="space-y-8 p-2">
        <!-- Student Info Header -->
        <div class="flex items-center justify-between bg-surface-container-low/50 p-6 rounded-2xl">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-2xl bg-primary text-white flex items-center justify-center text-xl font-bold">
              {{ selectedStudent.name.charAt(0) }}
            </div>
            <div>
              <h3 class="text-xl font-black text-on-surface">{{ selectedStudent.name }}</h3>
              <p class="text-xs text-secondary font-medium">{{ selectedStudent.studentId }} | {{ selectedStudent.class }}</p>
            </div>
          </div>
          <div class="text-right">
            <p class="text-[0.65rem] font-bold text-outline uppercase tracking-widest">自评结论</p>
            <p class="text-lg font-black text-primary">{{ selectedStudent.selfEval }}</p>
          </div>
        </div>

        <!-- Sections from Student Form -->
        <div class="space-y-6">
          <div v-for="(val, label) in sampleAnswers" :key="label" class="space-y-2">
            <h4 class="text-xs font-bold text-secondary uppercase tracking-widest flex items-center gap-2">
              <div class="w-1 h-3 bg-primary/40 rounded-full"></div>
              {{ label }}
            </h4>
            <div class="p-4 rounded-xl bg-surface border border-outline-variant/10 text-sm leading-relaxed text-on-surface shadow-sm">
              {{ val }}
            </div>
          </div>
        </div>

        <!-- Audit Form -->
        <div class="bg-surface-container-high/30 p-8 rounded-3xl border border-primary/10">
          <h4 class="text-sm font-bold text-primary uppercase tracking-widest mb-6">审核评定</h4>
          <div class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div class="space-y-2">
                <label class="text-[0.65rem] font-bold text-secondary uppercase">评定等级</label>
                <el-select v-model="auditForm.grade" placeholder="请选择评定等级" class="w-full">
                  <el-option label="优秀" value="优秀" />
                  <el-option label="良好" value="良好" />
                  <el-option label="合格" value="合格" />
                  <el-option label="待改进" value="待改进" />
                </el-select>
              </div>
              <div class="space-y-2">
                <label class="text-[0.65rem] font-bold text-secondary uppercase">鉴定结论</label>
                <el-radio-group v-model="auditForm.pass">
                  <el-radio-button label="true">鉴定合格</el-radio-button>
                  <el-radio-button label="false">退回重填</el-radio-button>
                </el-radio-group>
              </div>
            </div>
            <div class="space-y-2">
              <label class="text-[0.65rem] font-bold text-secondary uppercase tracking-widest">评议意见</label>
              <el-input v-model="auditForm.comment" type="textarea" :rows="4" placeholder="请填写辅导员/班主任鉴定意见..." />
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="px-4 pb-4 flex items-center justify-between w-full">
          <p class="text-[10px] text-outline italic">该鉴定记录将作为学期综合素质评价的重要支撑材料存入档案。</p>
          <div class="flex items-center gap-3">
            <el-button round @click="showDetail = false">取消</el-button>
            <el-button type="primary" round class="px-8 shadow-lg shadow-primary/20" @click="submitAudit">提交审核结论</el-button>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- Config Modal -->
    <el-dialog v-model="showConfig" title="中期鉴定规则配置" width="500px">
      <div class="space-y-6">
        <div class="space-y-4">
          <h5 class="text-xs font-bold text-secondary uppercase">填报限制</h5>
          <div class="flex items-center justify-between">
            <span class="text-sm">最少填报字数</span>
            <el-input-number v-model="config.minWords" :min="10" :max="1000" />
          </div>
          <div class="flex items-center justify-between">
            <span class="text-sm">最多填报字数</span>
            <el-input-number v-model="config.maxWords" :min="100" :max="5000" />
          </div>
        </div>
        <div class="space-y-4">
          <h5 class="text-xs font-bold text-secondary uppercase">审核节点</h5>
          <el-checkbox-group v-model="config.nodes">
            <el-checkbox label="counselor">辅导员/班主任初审</el-checkbox>
            <el-checkbox label="mentor">专业导师审核 (研究生)</el-checkbox>
            <el-checkbox label="admin">学院管理员终审</el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="showConfig = false">保存配置</el-button>
      </template>
    </el-dialog>

    <!-- Questionnaire Design Modal -->
    <el-dialog v-model="showDesign" title="中期鉴定问卷设计" width="700px" class="glass-dialog">
      <div class="space-y-6">
        <div class="bg-blue-50 p-4 rounded-xl border border-blue-100 flex gap-3">
          <el-icon class="text-blue-500 mt-1"><InfoFilled /></el-icon>
          <p class="text-xs text-blue-700 leading-relaxed">
            您可以在此自定义中期鉴定表单的主观题部分。发布的问卷将实时对全校学生生效。
            默认包含 6 个核心维度。
          </p>
        </div>

        <div class="space-y-4 max-h-[50vh] overflow-y-auto pr-2 custom-scrollbar">
          <div v-for="(q, idx) in tempQuestions" :key="idx" class="p-4 rounded-2xl bg-surface border border-outline-variant/10 relative group">
            <div class="flex items-center justify-between mb-3">
              <span class="text-[10px] font-black bg-surface-container-high px-2 py-0.5 rounded text-secondary uppercase">问题 {{ idx + 1 }}</span>
              <button @click="removeQuestion(idx)" class="text-error opacity-0 group-hover:opacity-100 transition-opacity p-1 hover:bg-error-container rounded">
                <el-icon><Delete /></el-icon>
              </button>
            </div>
            <div class="space-y-3">
              <el-input v-model="q.title" placeholder="问题标题 (如: 思想表现自述)" />
              <el-input v-model="q.placeholder" type="textarea" :rows="2" placeholder="填写提示词..." />
            </div>
          </div>
        </div>

        <el-button class="w-full border-dashed" @click="addQuestion">
          <el-icon class="mr-1"><Plus /></el-icon> 添加新问题
        </el-button>
      </div>
      <template #footer>
        <div class="flex items-center justify-between w-full px-4 pb-4">
          <span class="text-xs text-secondary italic">提示：发布后建议不要轻易删除已有问题，以免影响已提交数据的显示。</span>
          <div class="flex items-center gap-3">
            <el-button round @click="showDesign = false">取消</el-button>
            <el-button type="primary" round class="px-8 shadow-lg shadow-primary/20" @click="publishQuestions">确认并发布问卷</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  UserFilled, List, CircleCheck, Warning, Search, Download, Setting,
  Management, Timer, Edit, Plus, Delete, InfoFilled
} from '@element-plus/icons-vue'

const stats = [
  { label: '应填报人数', value: '425', icon: 'UserFilled', bg: 'bg-blue-50 text-blue-600' },
  { label: '已提交', value: '382', icon: 'CircleCheck', bg: 'bg-green-50 text-green-600' },
  { label: '待审核', value: '142', icon: 'Timer', bg: 'bg-orange-50 text-orange-600' },
  { label: '审核完成率', value: '62.8%', icon: 'TrendCharts', bg: 'bg-primary/5 text-primary' }
]

const searchQuery = ref('')
const statusFilter = ref('all')
const showDetail = ref(false)
const showConfig = ref(false)
const showDesign = ref(false)
const selectedStudent = ref(null)

const auditForm = ref({
  grade: '',
  pass: 'true',
  comment: ''
})

const config = ref({
  minWords: 100,
  maxWords: 300,
  nodes: ['counselor', 'admin']
})

// 问卷设计逻辑
const defaultQuestions = [
  { title: '思想品德、遵纪守法、思政学习、志愿服务及违纪情况自述', placeholder: '请描述你的思想政治觉悟，遵纪守法情况，是否有参加志愿服务或违纪记录...' },
  { title: '课程学习、成绩排名、挂科情况、考证备考、专业能力提升自述', placeholder: '总结你的学业成绩，包括专业课掌握程度，未来的考证或考研计划...' },
  { title: '第二课堂、社团实践、志愿活动、学科竞赛、创新创业及获奖情况', placeholder: '列举你的实践成果，参加的竞赛名称及获得奖项，社团贡献等...' },
  { title: '身心健康、人际交往、集体生活、日常作风表现自述', placeholder: '描述你的心理状态，与同学相处是否和谐，日常作息是否自律...' },
  { title: '自身学习生活存在的不足与问题反思', placeholder: '客观分析当前面临的瓶颈，如效率不高、拖延等问题...' },
  { title: '下一阶段改进措施、学习目标及升学 / 就业 / 科研发展规划', placeholder: '明确下一步的努力方向，具体的改进计划和长远职业目标...' }
]

const tempQuestions = ref([])

const addQuestion = () => {
  tempQuestions.value.push({ title: '', placeholder: '' })
}

const removeQuestion = (idx) => {
  tempQuestions.value.splice(idx, 1)
}

const publishQuestions = () => {
  if (tempQuestions.value.some(q => !q.title)) {
    ElMessage.warning('所有问题的标题均不能为空')
    return
  }
  ElMessageBox.confirm('发布新问卷将对所有未提交的学生生效，是否确认发布？', '发布确认', {
    confirmButtonText: '确认发布',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.setItem('midterm_questions', JSON.stringify(tempQuestions.value))
    ElMessage.success('问卷已成功发布')
    showDesign.value = false
  })
}

onMounted(() => {
  const saved = localStorage.getItem('midterm_questions')
  tempQuestions.value = saved ? JSON.parse(saved) : JSON.parse(JSON.stringify(defaultQuestions))
  
  // Load real submissions
  const subs = JSON.parse(localStorage.getItem('midterm_submissions') || '[]')
  subs.forEach(s => {
    // Basic mapping for the table
    const record = {
      id: s.id,
      name: s.name,
      studentId: s.studentId,
      class: s.class,
      selfEval: s.selfEval.thoughtEval || '良好', // Using thoughtEval as a summary for now
      gpa: s.gpa,
      submitTime: s.submitTime,
      status: s.status,
      answers: s.answers
    }
    if (!studentList.value.some(x => x.id === s.id)) {
      studentList.value.unshift(record)
    }
  })
})

const studentList = ref([
  { name: '张小明', studentId: '202301042', class: '2023级软件工程04班', selfEval: '优秀', gpa: '3.82', submitTime: '2026-05-01 14:20', status: 'pending' },
  { name: '李芳', studentId: '202301058', class: '2023级软件工程04班', selfEval: '良好', gpa: '3.65', submitTime: '2026-05-01 10:15', status: 'approved' },
  { name: '王伟', studentId: '202301012', class: '2023级软件工程01班', selfEval: '合格', gpa: '3.20', submitTime: '2026-05-02 09:30', status: 'pending' },
  { name: '赵静', studentId: '202301089', class: '2023级软件工程02班', selfEval: '待改进', gpa: '2.85', submitTime: '2026-05-01 16:45', status: 'rejected' },
  { name: '刘洋', studentId: '202301033', class: '2023级软件工程03班', selfEval: '优秀', gpa: '3.91', submitTime: '2026-05-03 11:20', status: 'pending' }
])

const sampleAnswers = {
  '思想表现': '本学期我积极参加学院组织的思政课学习，坚持利用学习强国APP进行理论学习。严格遵守学校各项规章制度，无违纪行为。',
  '学业情况': '在课程学习方面，我保持了良好的学习习惯，各科成绩均在85分以上，特别是数据结构与算法课程获得了92分。',
  '社会实践': '担任班级体育委员，策划并组织了班级羽毛球赛。参加了学校红十字会的志愿服务活动，服务时长累计20小时。',
  '身心健康': '坚持每周进行三次以上体育锻炼，身心状态良好。与宿舍同学关系融洽，积极参加寝室文化建设。',
  '不足反思': '虽然学习成绩尚可，但在代码工程实践能力方面还有待提高，遇到复杂算法逻辑时思维还不够严密。',
  '未来规划': '下阶段我计划重点攻克专业领域的实践项目，争取在下学期的学科竞赛中获得奖项，并开始准备英语六级考试。'
}

const filteredList = computed(() => {
  return studentList.value.filter(s => {
    const matchesSearch = s.name.includes(searchQuery.value) || s.studentId.includes(searchQuery.value) || s.class.includes(searchQuery.value)
    const matchesStatus = statusFilter.value === 'all' || s.status === statusFilter.value
    return matchesSearch && matchesStatus
  })
})

const statusLabel = (s) => ({ pending: '待审核', approved: '已通过', rejected: '已驳回' }[s])
const statusBadge = (s) => ({ pending: 'bg-orange-500', approved: 'bg-green-500', rejected: 'bg-error' }[s])

const viewDetail = (row) => {
  selectedStudent.value = row
  showDetail.value = true
}

const submitAudit = () => {
  if (!auditForm.value.grade || !auditForm.value.comment) {
    ElMessage.warning('请填写完整的审核意见和等级')
    return
  }
  ElMessage.success('审核结论提交成功')
  showDetail.value = false
  // Update state in demo
  if (selectedStudent.value) {
    selectedStudent.value.status = auditForm.value.pass === 'true' ? 'approved' : 'rejected'
  }
}

const batchAudit = () => ElMessage.info('批量审核功能开发中...')
const exportExcel = () => ElMessage.success('正在生成并下载 Excel 报表...')

</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
}

.midterm-table :deep(.el-table__header-wrapper) th {
  background: rgba(0, 0, 0, 0.02);
  color: var(--color-secondary);
  font-weight: 800;
  text-transform: uppercase;
  font-size: 0.7rem;
  letter-spacing: 0.05em;
}

.glass-dialog :deep(.el-dialog) {
  border-radius: 2rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(40px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}
</style>
