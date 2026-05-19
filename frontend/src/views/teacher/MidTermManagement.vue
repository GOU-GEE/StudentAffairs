<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧学生列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <div class="px-3 pt-2 pb-2">
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-outline z-10"><el-icon :size="14"><Search /></el-icon></span>
          <input v-model="searchQuery" placeholder="搜索学生姓名或学号..."
                 class="w-full pl-9 pr-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-primary/50 focus:ring-2 focus:ring-primary/20 transition-all" />
        </div>
      </div>
      <div class="h-px bg-outline-variant/15"></div>
      <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
        <div v-for="student in filteredStudents" :key="student.studentId"
             @click="selectStudent(student)"
             class="p-3 rounded-xl cursor-pointer transition-all border flex items-center gap-3"
             :class="activeStudentId === student.studentId ? 'bg-white shadow-sm border-primary/30 ring-1 ring-primary/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
          <div class="w-10 h-10 rounded-full bg-purple-100 flex items-center justify-center text-purple-600 font-bold text-sm flex-shrink-0">
            {{ student.name ? student.name[0] : '?' }}
          </div>
          <div class="flex-1 min-w-0">
            <div class="font-bold text-sm text-on-surface truncate">{{ student.name }}</div>
            <div class="text-xs text-outline truncate">{{ student.studentId }} · {{ student.className || student.major || '' }}</div>
          </div>
          <div class="flex-shrink-0">
            <span v-if="student.hasSubmitted" class="text-xs font-bold px-2 py-0.5 rounded-full bg-green-100 text-green-700">已提交</span>
            <span v-else class="text-xs font-bold px-2 py-0.5 rounded-full bg-gray-100 text-gray-500">未提交</span>
          </div>
        </div>
        <div v-if="filteredStudents.length === 0" class="py-10 text-center text-secondary text-sm">暂无学生</div>
      </div>
    </div>

    <!-- 右侧详情面板 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col">
      <div v-if="activeAppraisal" class="flex flex-col h-full">
        <div class="px-6 py-4 border-b border-outline-variant/30 bg-white/40">
          <h3 class="font-bold text-base text-on-surface">{{ activeAppraisal.studentName }}</h3>
          <p class="text-xs text-secondary mt-0.5">{{ activeAppraisal.studentId }} · {{ activeAppraisal.className }} · {{ formatTime(activeAppraisal.submitTime) }}</p>
        </div>
        <div class="flex-1 overflow-y-auto p-6 space-y-4 custom-scrollbar">
          <div class="bg-surface-container-low rounded-xl p-4">
            <p class="text-xs font-bold text-secondary uppercase mb-3">自评等级</p>
            <div class="grid grid-cols-3 gap-4 text-sm">
              <div><span class="text-secondary">思想表现：</span><span class="font-bold" :class="scoreColor(activeAppraisal.thoughtPerformance)">{{ activeAppraisal.thoughtPerformance || '-' }}</span></div>
              <div><span class="text-secondary">学业成绩：</span><span class="font-bold" :class="scoreColor(activeAppraisal.academicPerformance)">{{ activeAppraisal.academicPerformance || '-' }}</span></div>
              <div><span class="text-secondary">综合表现：</span><span class="font-bold" :class="scoreColor(activeAppraisal.overallPerformance)">{{ activeAppraisal.overallPerformance || '-' }}</span></div>
            </div>
          </div>
          <div class="bg-surface-container-low rounded-xl p-4">
            <p class="text-xs font-bold text-secondary uppercase mb-3">自我鉴定</p>
            <p class="text-sm leading-relaxed whitespace-pre-wrap">{{ activeAppraisal.selfAssessment || '暂无内容' }}</p>
          </div>
        </div>
      </div>
      <div v-else class="flex flex-col items-center justify-center h-full text-secondary">
        <el-icon :size="64" class="mb-4 opacity-20"><DocumentChecked /></el-icon>
        <p class="text-lg font-medium text-on-surface">中期鉴定收集</p>
        <p class="text-sm mt-2 opacity-80">选择左侧学生查看鉴定详情</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, DocumentChecked } from '@element-plus/icons-vue'
import request from '@/utils/request'

const MIDTERM_API = '/api/mid-term'
const COMM_API = '/api/communication'
const teacherId = sessionStorage.getItem('userId') || 'T001'

const searchQuery = ref('')
const activeStudentId = ref(null)
const students = ref([])
const allAppraisals = ref([])

const loadStudents = async () => {
  try {
    const res = await request.get(`${COMM_API}/students`, { params: { teacherId } })
    if (res.data.code === 200) students.value = res.data.data
  } catch (e) { console.error(e) }
}

const loadAppraisals = async () => {
  try {
    const res = await request.get(`${MIDTERM_API}/all`)
    if (res.data.code === 200) allAppraisals.value = res.data.data
  } catch (e) { console.error(e) }
}

const filteredStudents = computed(() => {
  let list = students.value.map(s => {
    const appraisal = allAppraisals.value.find(a => a.studentId === s.studentId)
    return { ...s, hasSubmitted: !!appraisal, appraisalId: appraisal?.id }
  })
  if (!searchQuery.value) return list
  const q = searchQuery.value.toLowerCase()
  return list.filter(s => s.name.toLowerCase().includes(q) || s.studentId.toLowerCase().includes(q))
})

const activeAppraisal = computed(() => {
  return allAppraisals.value.find(a => a.studentId === activeStudentId.value) || null
})

const selectStudent = (student) => {
  activeStudentId.value = student.studentId
}

const formatTime = (s) => {
  if (!s) return ''
  return new Date(s).toLocaleString('zh-CN', { year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit' })
}

const scoreColor = (s) => ({ '优秀':'text-green-600','良好':'text-blue-600','合格':'text-amber-600','待改进':'text-red-500' }[s]||'')

onMounted(async () => {
  await Promise.all([loadStudents(), loadAppraisals()])
})
</script>
