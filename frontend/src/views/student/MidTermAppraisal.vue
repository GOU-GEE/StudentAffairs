<template>
  <div class="mid-term-appraisal pb-20">
    <div class="grid grid-cols-1 xl:grid-cols-12 gap-8 items-start">
      
      <!-- Left Column: The Form -->
      <div class="xl:col-span-8 space-y-8">
      <!-- Left Column: The Form -->
      <div class="xl:col-span-8">
        <section class="glass-card p-8 rounded-3xl border border-white/20 shadow-xl space-y-8 relative pb-20">
          <!-- 1. 个人鉴定自述 (6 Questions) -->
          <div class="space-y-6">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-3">
                <h3 class="text-xl font-bold tracking-tight">个人鉴定自述</h3>
              </div>
              <div class="text-xs font-medium text-secondary">
                <span class="text-primary font-bold">{{ completedQuestions }}</span> / 6 已完成
              </div>
            </div>

            <div v-for="(q, index) in questions" :key="index" class="space-y-2">
              <div class="flex items-start gap-4">
                <div class="flex items-center gap-4 w-full">
                  <div class="w-6 h-6 rounded-full bg-surface-container-high flex items-center justify-center text-[10px] font-black shrink-0">0{{ index + 1 }}</div>
                  <p class="text-sm font-bold text-on-surface leading-snug">{{ q.title }}</p>
                </div>
              </div>
              <div class="ml-10">
                <el-input
                  v-model="answers[index]"
                  type="textarea"
                  :rows="3"
                  :placeholder="q.placeholder"
                  maxlength="300"
                  show-word-limit
                  :disabled="isSubmitted"
                  class="modern-textarea"
                />
              </div>
              <div v-if="index < questions.length - 1" class="h-px bg-outline-variant/10 ml-10"></div>
            </div>
          </div>

          <!-- 2. 自评等级区 -->
          <div class="space-y-6">
            <div class="flex items-center gap-3 mb-4">
              <h3 class="text-xl font-bold tracking-tight">综合表现自评</h3>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
              <div v-for="item in selfEvals" :key="item.key" class="space-y-3">
                <label class="text-xs font-bold text-secondary uppercase tracking-widest">{{ item.label }}</label>
                <el-select v-model="form[item.key]" placeholder="请选择等级" class="w-full" :disabled="isSubmitted">
                  <el-option label="优秀" value="优秀" />
                  <el-option label="良好" value="良好" />
                  <el-option label="合格" value="合格" />
                  <el-option label="待改进" value="待改进" />
                </el-select>
              </div>
            </div>
          </div>

          <!-- 3. 附件上传区 -->
          <div class="space-y-6">
            <div class="flex items-center gap-3 mb-4">
              <h3 class="text-xl font-bold tracking-tight">附件支撑材料</h3>
            </div>
            
            <el-upload
              action="http://localhost:8080/api/upload"
              :headers="{ Authorization: 'Bearer ' + sessionStorage.getItem('token') }"
              multiple
              :limit="5"
              :on-exceed="handleExceed"
              :on-success="handleUploadSuccess"
              :disabled="isSubmitted"
              class="midterm-uploader"
            >
              <div class="border-2 border-dashed border-outline-variant/30 rounded-2xl p-10 flex flex-col items-center justify-center gap-3 hover:border-primary/50 transition-colors cursor-pointer bg-surface/50">
                <el-icon class="text-4xl text-outline-variant"><UploadFilled /></el-icon>
                <div class="text-center">
                  <p class="text-sm font-bold">点击或拖拽文件上传</p>
                  <p class="text-xs text-outline mt-1">支持成绩单、获奖证书、支撑材料 (最多5个)</p>
                </div>
              </div>
            </el-upload>
          </div>

          <!-- Action Buttons (Positioned bottom-right) -->
          <div class="absolute bottom-8 right-8 flex items-center gap-4">
            <template v-if="!isSubmitted">
              <el-button size="large" round class="px-8" @click="saveDraft">保存草稿</el-button>
              <el-button type="primary" size="large" round class="px-10 shadow-lg shadow-[#409eff]/20 !bg-[#409eff] !border-[#409eff]" @click="submitForm">正式提交</el-button>
            </template>
            <div v-else class="flex items-center gap-2 text-green-600 font-bold bg-green-50 px-6 py-2 rounded-full border border-green-100">
              <el-icon><CircleCheckFilled /></el-icon> 已提交
            </div>
          </div>
        </section>
      </div>
      </div>

      <!-- Right Column: Status & Reference -->
      <div class="xl:col-span-4 space-y-6">
        <!-- 填报状态卡片 -->
        <div class="glass-card p-6 rounded-3xl border border-white/20 shadow-xl bg-gradient-to-br from-white/80 to-surface-container-low/50 sticky top-20">
          <h4 class="text-sm font-black uppercase tracking-widest text-secondary mb-6 flex items-center justify-between">
            填报进度与状态
            <span class="w-2 h-2 rounded-full" :class="statusColor"></span>
          </h4>
          
          <div class="space-y-6">
            <div class="flex items-start gap-4">
              <div class="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center shrink-0">
                <el-icon :size="16" class="text-primary"><Timer /></el-icon>
              </div>
              <div>
                <p class="text-xs font-bold text-outline uppercase">当前状态</p>
                <p class="text-sm font-black text-primary">{{ statusText }}</p>
              </div>
            </div>

            <div class="relative pl-4 border-l-2 border-outline-variant/30 space-y-6 py-2">
              <div v-for="(step, i) in workflow" :key="i" class="relative">
                <div class="absolute -left-[25px] top-0 w-4 h-4 rounded-full border-2 border-white flex items-center justify-center transition-colors shadow-sm"
                     :class="step.active ? 'bg-primary' : 'bg-outline-variant/50'">
                  <el-icon v-if="step.completed" :size="10" class="text-white"><Check /></el-icon>
                </div>
                <div class="pl-2">
                  <p class="text-xs font-bold" :class="step.active ? 'text-primary' : 'text-secondary'">{{ step.title }}</p>
                  <p class="text-[10px] text-outline">{{ step.desc }}</p>
                </div>
              </div>
            </div>

            <div class="h-px bg-outline-variant/10"></div>
            
            <!-- 参考数据概览 -->
            <div class="space-y-4">
              <p class="text-[0.65rem] font-black text-secondary uppercase tracking-widest">关联系统参考数据</p>
              <div class="grid grid-cols-2 gap-3">
                <div v-for="data in refData" :key="data.label" class="p-3 rounded-xl bg-surface/50 border border-outline-variant/5">
                  <div class="text-[0.6rem] font-bold text-outline uppercase tracking-tighter">{{ data.label }}</div>
                  <div class="text-xs font-black text-on-surface mt-0.5">{{ data.value }}</div>
                </div>
              </div>
              <p class="text-[10px] text-secondary italic leading-relaxed">提示：以上数据由系统自动同步，填写自述时请结合相关表现进行分析。</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  InfoFilled, UserFilled, Edit, StarFilled, UploadFilled,
  CircleCheckFilled, Timer, Check
} from '@element-plus/icons-vue'
import request from '@/utils/request'
const API = '/api/mid-term'
const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'

const basicInfo = {
  '姓名': '张小明',
  '学号': '202301042',
  '学院': '计算机学院',
  '专业': '软件工程',
  '年级班级': '2023级04班',
  '入学时间': '2023-09-01',
  '学制': '4年',
  '学业阶段': '本科在读'
}

const defaultQuestions = [
  { title: '思想品德、遵纪守法、思政学习、志愿服务及违纪情况自述', placeholder: '请描述你的思想政治觉悟，遵纪守法情况，是否有参加志愿服务或违纪记录...' },
  { title: '课程学习、成绩排名、挂科情况、考证备考、专业能力提升自述', placeholder: '总结你的学业成绩，包括专业课掌握程度，未来的考证或考研计划...' },
  { title: '第二课堂、社团实践、志愿活动、学科竞赛、创新创业及获奖情况', placeholder: '列举你的实践成果，参加的竞赛名称及获得奖项，社团贡献等...' },
  { title: '身心健康、人际交往、集体生活、日常作风表现自述', placeholder: '描述你的心理状态，与同学相处是否和谐，日常作息是否自律...' },
  { title: '自身学习生活存在的不足与问题反思', placeholder: '客观分析当前面临的瓶颈，如效率不高、拖延等问题...' },
  { title: '下一阶段改进措施、学习目标及升学 / 就业 / 科研发展规划', placeholder: '明确下一步的努力方向，具体的改进计划和长远职业目标...' }
]

const questions = ref([...defaultQuestions])
const answers = ref(new Array(defaultQuestions.length).fill(''))

const form = ref({
  thoughtEval: '',
  academicEval: '',
  comprehensiveEval: ''
})

const isSubmitted = ref(false)
const submitting = ref(false)
const completedQuestions = computed(() => answers.value.filter(a => a && a.length >= 100).length)

const statusText = computed(() => isSubmitted.value ? '学院初审中' : '草稿待提交')
const statusColor = computed(() => isSubmitted.value ? 'bg-orange-500 animate-pulse' : 'bg-secondary')

const selfEvals = [
  { label: '思想表现自评', key: 'thoughtEval' },
  { label: '学业成绩自评', key: 'academicEval' },
  { label: '综合表现自评', key: 'comprehensiveEval' }
]

const workflow = [
  { title: '填报提交', desc: '学生完成填报并正式提交', active: !isSubmitted.value, completed: isSubmitted.value },
  { title: '辅导员审核', desc: '辅导员进行初次评议', active: isSubmitted.value, completed: false },
  { title: '导师/管理员终审', desc: '最终鉴定结论下达', active: false, completed: false }
]

const refData = [
  { label: 'GPA 绩点', value: '3.82 (Top 5%)' },
  { label: '第二课堂积分', value: '42.5 分' },
  { label: '违纪记录', value: '无违纪现象' },
  { label: '奖项荣誉', value: '国家励志奖学金等 4 项' }
]

const saveDraft = () => {
  localStorage.setItem('midterm_draft', JSON.stringify({ answers: answers.value, form: form.value }))
  ElMessage.success('草稿已保存至本地')
}

const submitForm = () => {
  if (completedQuestions.value < questions.value.length) {
    ElMessage.error(`请确保所有主观题（共 ${questions.value.length} 道）均不少于 100 字后再提交`)
    return
  }
  if (!form.value.thoughtEval || !form.value.academicEval || !form.value.comprehensiveEval) {
    ElMessage.error('请完成所有自评等级选择')
    return
  }

  ElMessageBox.confirm('正式提交后将无法修改，是否确认提交？', '提交确认', {
    confirmButtonText: '确认提交',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    submitting.value = true
    try {
      const payload = {
        studentId: STUDENT_ID,
        studentName: '张小明',
        className: '软工2班',
        thoughtPerformance: form.value.thoughtEval,
        academicPerformance: form.value.academicEval,
        overallPerformance: form.value.comprehensiveEval,
        selfAssessment: questions.value.map((q, i) => `${q.title}: ${answers.value[i]}`).join('\n\n'),
      }
      const res = await request.post(API, payload)
      if (res.data.code === 200) {
        ElMessage.success('中期鉴定已成功提交！')
        isSubmitted.value = true
        localStorage.setItem('midterm_submitted', 'true')
      } else {
        ElMessage.error(res.data.msg || '提交失败')
      }
    } catch (e) {
      ElMessage.error('提交失败，请检查网络连接')
    }
    submitting.value = false
  })
}

onMounted(() => {
  // 加载问卷配置
  const savedQuestions = localStorage.getItem('midterm_questions')
  if (savedQuestions) {
    questions.value = JSON.parse(savedQuestions)
  }
  
  // 初始化答案数组
  answers.value = new Array(questions.value.length).fill('')

  const draft = localStorage.getItem('midterm_draft')
  if (draft) {
    const parsed = JSON.parse(draft)
    // 只有在问题数量一致时才恢复答案，或者按需部分恢复
    if (parsed.answers && parsed.answers.length === answers.value.length) {
      answers.value = parsed.answers
    }
    if (parsed.form) {
      form.value = parsed.form
    }
  }
  if (localStorage.getItem('midterm_submitted') === 'true') {
    isSubmitted.value = true
  }
  const loadExisting = async () => {
    try {
      const res = await request.get(`${API}?studentId=${STUDENT_ID}`)
      if (res.data.code === 200 && res.data.data.length > 0) {
        isSubmitted.value = true
        localStorage.setItem('midterm_submitted', 'true')
      }
    } catch (e) {}
  }
  loadExisting()
})

const uploadedFiles = ref([])
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    uploadedFiles.value.push(response.data.url)
  }
}
const handleExceed = () => ElMessage.warning('最多上传 5 个附件')

</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
}

.modern-textarea :deep(.el-textarea__inner) {
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 1rem;
  font-size: 0.875rem;
  line-height: 1.6;
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);
}

.modern-textarea :deep(.el-textarea__inner:focus) {
  background: white;
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 4px var(--el-color-primary-light-8);
}

.midterm-uploader :deep(.el-upload-list) {
  margin-top: 1rem;
}
</style>
