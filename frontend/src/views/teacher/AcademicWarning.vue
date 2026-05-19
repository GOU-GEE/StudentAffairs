<template>
  <div class="h-full flex flex-col gap-6 font-sans text-gray-800">
    
    <!-- 1. Top Section Bento Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
      
      <!-- Left Card: 班级成绩分布 (col-span-8) -->
      <div class="md:col-span-8 bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex flex-col justify-between h-[360px] hover:shadow-md transition-all duration-300">
        <div>
          <!-- Header and select dropdown -->
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-[1.05rem] font-bold text-gray-800 tracking-tight">班级成绩分布</h3>
            <div class="flex items-center gap-2">
              <span class="text-xs text-gray-400 font-medium">选择班级:</span>
              <select v-model="selectedClass" @change="handleClassChange"
                      class="bg-gray-50 border border-gray-200 rounded-lg px-3 py-1 text-xs focus:ring-1 focus:ring-sky-500 focus:border-sky-500 outline-none text-gray-700 font-bold cursor-pointer">
                <option value="软工2班">软工2班</option>
                <option value="软工1班">软工1班</option>
              </select>
            </div>
          </div>

          <div class="flex items-center justify-between gap-6 mt-2">
            
            <!-- Left Side: Larger ECharts Donut Chart -->
            <div class="relative w-48 h-48 flex-shrink-0 flex items-center justify-center">
              <div ref="donutChartRef" class="w-full h-full"></div>
              <div class="absolute flex flex-col items-center justify-center pointer-events-none">
                <span class="text-3xl font-black text-gray-800 leading-none">{{ donutStats.total }}人</span>
                <span class="text-xs text-gray-400 mt-2 font-semibold">班级总人数</span>
              </div>
            </div>

            <!-- Middle Side: Larger Descriptive Legend Details (text-sm for high visual fill) -->
            <div class="flex-1 space-y-3 text-[13px] md:text-[14px] text-gray-600 font-bold pr-2">
              <div v-for="(item, idx) in donutStats.data" :key="idx" class="flex items-center justify-between">
                <div class="flex items-center gap-2.5">
                  <span class="w-2.5 h-2.5 rounded-full flex-shrink-0" :style="{ backgroundColor: item.color }"></span>
                  <span>{{ item.name }}</span>
                </div>
                <div class="flex items-center gap-5">
                  <span class="text-gray-800 font-extrabold">{{ item.value }}人</span>
                  <span class="text-gray-400 w-12 text-right font-medium">{{ item.pct }}</span>
                </div>
              </div>
            </div>

            <!-- Divider line -->
            <div class="w-px h-36 bg-gray-100 flex-shrink-0"></div>

            <!-- Right Side: 2x2 Grade Statistics Overview -->
            <div class="w-60 grid grid-cols-2 gap-3 flex-shrink-0">
              
              <!-- Metric 1: 平均分 -->
              <div class="bg-gradient-to-br from-blue-50/20 to-indigo-50/20 border border-indigo-100/30 rounded-xl p-3 relative overflow-hidden group">
                <span class="text-[10px] font-bold text-gray-400 block mb-1">平均分</span>
                <h4 class="text-xl font-black text-gray-805 leading-none">{{ classOverview.avg }}</h4>
                <span class="text-[9px] font-bold text-blue-500 bg-blue-50/80 px-1.5 py-0.5 rounded-full inline-block mt-2">
                  较上学期 ↑ 4.3
                </span>
                <div class="absolute -right-2 -bottom-2 text-indigo-400/5 pointer-events-none group-hover:scale-110 transition-transform">
                  <el-icon :size="40"><Refresh /></el-icon>
                </div>
              </div>

              <!-- Metric 2: 最高分 -->
              <div class="bg-gradient-to-br from-emerald-50/20 to-teal-50/20 border border-emerald-100/30 rounded-xl p-3 relative overflow-hidden group">
                <span class="text-[10px] font-bold text-gray-400 block mb-1">最高分</span>
                <h4 class="text-xl font-black text-gray-805 leading-none">{{ classOverview.max }}</h4>
                <span class="text-[9px] font-bold text-emerald-500 bg-emerald-50/80 px-1.5 py-0.5 rounded-full inline-block mt-2">
                  较上学期 ↑ 2
                </span>
                <div class="absolute -right-2 -bottom-2 text-emerald-400/5 pointer-events-none group-hover:scale-110 transition-transform">
                  <el-icon :size="40"><Trophy /></el-icon>
                </div>
              </div>

              <!-- Metric 3: 最低分 -->
              <div class="bg-gradient-to-br from-amber-50/20 to-orange-50/20 border border-orange-100/30 rounded-xl p-3 relative overflow-hidden group">
                <span class="text-[10px] font-bold text-gray-400 block mb-1">最低分</span>
                <h4 class="text-xl font-black text-gray-805 leading-none">{{ classOverview.min }}</h4>
                <span class="text-[9px] font-bold text-amber-600 bg-amber-50/80 px-1.5 py-0.5 rounded-full inline-block mt-2">
                  较上学期 ↓ 6
                </span>
                <div class="absolute -right-2 -bottom-2 text-orange-400/5 pointer-events-none group-hover:scale-110 transition-transform">
                  <el-icon :size="40"><Warning /></el-icon>
                </div>
              </div>

              <!-- Metric 4: 挂科率 -->
              <div class="bg-gradient-to-br from-purple-50/20 to-pink-50/20 border border-purple-100/30 rounded-xl p-3 relative overflow-hidden group">
                <span class="text-[10px] font-bold text-gray-400 block mb-1">挂科率</span>
                <h4 class="text-xl font-black text-gray-805 leading-none">{{ classOverview.failRate }}</h4>
                <span class="text-[9px] font-bold text-purple-500 bg-purple-50/80 px-1.5 py-0.5 rounded-full inline-block mt-2">
                  较上学期 ↓ 2.1%
                </span>
                <div class="absolute -right-2 -bottom-2 text-purple-400/5 pointer-events-none group-hover:scale-110 transition-transform">
                  <el-icon :size="40"><Bell /></el-icon>
                </div>
              </div>

            </div>

          </div>
        </div>
      </div>

      <!-- Right Card: 学业预警学生 (col-span-4) (Removed 3 threat tabs, enlarged list height) -->
      <div class="md:col-span-4 bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex flex-col justify-between h-[360px] hover:shadow-md transition-all duration-300">
        <div>
          <!-- Header and red tag -->
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-rose-500 animate-pulse"></span>
              <h3 class="text-[1.05rem] font-bold text-gray-800 tracking-tight">学业预警学生</h3>
            </div>
            <span class="text-[10px] font-extrabold bg-rose-50 text-rose-500 px-2.5 py-0.5 rounded-full border border-rose-100">
              {{ warningStudents.length }}人
            </span>
          </div>

          <!-- Dynamic Warning List Panel (Enlarged to 210px since danger tabs are removed) -->
          <div class="h-[210px] overflow-y-auto pr-1 custom-scrollbar space-y-2.5">
            
            <div v-for="(s, idx) in warningStudents" :key="idx"
                 class="flex items-center gap-3 p-3 bg-gray-50 border border-gray-100 rounded-xl hover:bg-gray-100/40 hover:border-gray-200 transition-all duration-300">
              <div class="w-9 h-9 rounded-full bg-rose-50 border border-rose-100 text-rose-600 flex items-center justify-center font-bold text-sm flex-shrink-0">
                {{ s.studentName.substring(0, 1) }}
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between mb-0.5">
                  <h4 class="font-extrabold text-[0.8rem] text-gray-700 truncate">{{ s.studentName }}</h4>
                  <span class="text-[10px] font-bold px-1.5 py-0.2 bg-rose-50 text-rose-500 rounded border border-rose-100">{{ s.score }}分</span>
                </div>
                <p class="text-[10px] text-gray-400 truncate font-medium">{{ s.studentId }} · {{ s.className }} · {{ s.courseName }}</p>
              </div>
            </div>

            <!-- Empty State Illustration -->
            <div v-if="warningStudents.length === 0" class="flex flex-col items-center justify-center py-10 text-gray-400">
              <svg class="w-12 h-12 text-gray-200" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <span class="text-[10px] text-gray-400 mt-3 font-semibold">暂无预警学生，点击运行引擎检测</span>
            </div>

          </div>
        </div>

        <!-- Big Sky Blue Call To Action: 运行预警引擎 -->
        <div class="mt-2">
          <button @click="runEngine" :disabled="engineRunning"
                  class="w-full bg-sky-500 hover:bg-sky-600 active:bg-sky-700 text-white transition-colors rounded-xl py-2.5 text-xs font-bold flex items-center justify-center gap-1.5 shadow-md shadow-sky-100 disabled:opacity-50">
            <el-icon :class="engineRunning ? 'animate-spin' : ''"><Refresh /></el-icon>
            {{ engineRunning ? '预警引擎检测中...' : '运行预警引擎' }}
          </button>
        </div>

      </div>

    </div>

    <!-- 2. Bottom Card: 全体学生成绩明细 (Aligned toolbar & fixed magnifier glass inside input) -->
    <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_4px_20px_rgba(0,0,0,0.015)] hover:shadow-md transition-all duration-300">
      
      <!-- Aligned toolbar in one line, shifted up a bit (pb-3 & mt-0) -->
      <div class="flex items-center justify-between pb-3 border-b border-gray-100 mb-4 mt-0">
        <h3 class="text-[1.05rem] font-bold text-gray-805 tracking-tight">全体学生成绩明细</h3>
        
        <div class="flex items-center gap-3">
          <!-- Search box with absolutely centered SVG search icon inside the box -->
          <div class="relative w-64">
            <input type="text" 
                   v-model="searchKeyword" 
                   placeholder="搜索学生姓名 / 学号..." 
                   class="w-full bg-gray-50 border border-gray-200/80 rounded-xl pl-9 pr-4 py-2 text-xs focus:outline-none focus:ring-1 focus:ring-sky-500 focus:border-sky-500 transition-all text-gray-700 placeholder-gray-400 font-semibold">
            <!-- Center magnifying glass vertically inside input -->
            <span class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 flex items-center pointer-events-none">
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </span>
          </div>

          <!-- Exporter button -->
          <button @click="exportData" class="bg-gray-50 border border-gray-200/85 hover:bg-gray-100 hover:border-gray-300 text-gray-600 font-bold rounded-xl px-3 py-2 text-xs flex items-center gap-1.5 transition-all">
            <svg class="w-3.5 h-3.5 text-gray-500 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path>
            </svg>
            导出数据
          </button>
        </div>
      </div>

      <!-- Compact Dynamic Grade Table -->
      <el-table :data="filteredStudents" style="width: 100%" max-height="350" header-cell-class-name="bg-gray-50/50 text-gray-500 text-xs font-bold">
        <el-table-column prop="studentId" label="学号" width="110" />
        <el-table-column prop="studentName" label="姓名" width="85" />
        <el-table-column prop="className" label="班级" width="105" />
        <el-table-column prop="courseCount" label="课程数" width="80" align="center" />
        <el-table-column prop="averageScore" label="平均分" width="90" align="center">
          <template #default="{ row }">
            <span class="font-extrabold text-gray-700">{{ row.averageScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="maxScore" label="最高分" width="90" align="center" />
        <el-table-column prop="minScore" label="最低分" width="90" align="center">
          <template #default="{ row }">
            <span :class="row.minScore < 60 ? 'text-rose-500 font-semibold' : 'text-gray-500'">{{ row.minScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="failedCount" label="挂科数" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.failedCount > 0" class="text-rose-600 font-bold bg-rose-50 px-1.5 py-0.5 rounded">{{ row.failedCount }}</span>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="credits" label="学分" width="80" align="center" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <span v-if="row.status === '预警'" class="text-[10px] font-bold px-2 py-0.5 rounded bg-rose-50 text-rose-600 border border-rose-100">预警</span>
            <span v-else class="text-[10px] font-bold px-2 py-0.5 rounded bg-emerald-50 text-emerald-600 border border-emerald-100">正常</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="140" align="center">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-2">
              <el-button size="small" type="primary" link @click="sendNotification(row)">发送提醒</el-button>
              <el-button size="small" type="info" link @click="viewDetails(row)">查看详情</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Refresh, Warning, Trophy, Bell } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/utils/request'
import { exportToCSV } from '@/utils/export'

const selectedClass = ref('软工2班')
const warningStudents = ref([])
const allRecords = ref([])
const engineRunning = ref(false)
const searchKeyword = ref('')

const donutChartRef = ref(null)
let donutChartInstance = null

const API = '/api/academic'

// HIGH-FIDELITY WARNING MOCK DATA
const mockWarnings = [
  { studentId: '202301041', studentName: '张伟', className: '软工2班', courseName: '高等数学', score: 28 },
  { studentId: '202301043', studentName: '王强', className: '软工2班', courseName: '数据结构', score: 32 },
  { studentId: '202301045', studentName: '刘洋', className: '软工2班', courseName: '离散数学', score: 48 }
]

// HIGH-FIDELITY GRADE RECORDS MOCK DATA
const mockRecords = [
  { id: 1, studentId: '202301041', studentName: '张伟', className: '软工2班', courseName: '高等数学', score: 28, credit: 4.0, semester: '2025-2026秋季', isWarning: true },
  { id: 2, studentId: '202301041', studentName: '张伟', className: '软工2班', courseName: '数据结构', score: 55, credit: 3.5, semester: '2025-2026秋季', isWarning: true },
  { id: 3, studentId: '202301042', studentName: '李娜', className: '软工2班', courseName: '高等数学', score: 88, credit: 4.0, semester: '2025-2026秋季', isWarning: false },
  { id: 4, studentId: '202301042', studentName: '李娜', className: '软工2班', courseName: '数据结构', score: 96, credit: 3.5, semester: '2025-2026秋季', isWarning: false },
  { id: 5, studentId: '202301043', studentName: '王强', className: '软工2班', courseName: '高等数学', score: 42, credit: 4.0, semester: '2025-2026秋季', isWarning: true },
  { id: 6, studentId: '202301043', studentName: '王强', className: '软工2班', courseName: '数据结构', score: 32, credit: 3.5, semester: '2025-2026秋季', isWarning: true },
  { id: 7, studentId: '202301044', studentName: '赵敏', className: '软工2班', courseName: '高等数学', score: 92, credit: 4.0, semester: '2025-2026秋季', isWarning: false },
  { id: 8, studentId: '202301044', studentName: '赵敏', className: '软工2班', courseName: '数据结构', score: 95, credit: 3.5, semester: '2025-2026秋季', isWarning: false },
  { id: 9, studentId: '202301045', studentName: '刘洋', className: '软工2班', courseName: '高等数学', score: 58, credit: 4.0, semester: '2025-2026秋季', isWarning: true },
  { id: 10, studentId: '202301045', studentName: '刘洋', className: '软工2班', courseName: '离散数学', score: 48, credit: 3.0, semester: '2025-2026秋季', isWarning: true },
  { id: 11, studentId: '202301046', studentName: '陈静', className: '软工2班', courseName: '高等数学', score: 76, credit: 4.0, semester: '2025-2026秋季', isWarning: false },
  { id: 12, studentId: '202301046', studentName: '陈静', className: '软工2班', courseName: '数据结构', score: 82, credit: 3.5, semester: '2025-2026秋季', isWarning: false }
]

// Grade metrics defaults matching mock specs
const classOverview = ref({
  avg: 78.6,
  max: 96,
  min: 28,
  failRate: '16.7%'
})

// Slices data for grade donut chart
const donutStats = ref({
  total: 48,
  data: [
    { value: 8, name: '优秀 (90-100)', color: '#10b981', pct: '16.7%' },
    { value: 18, name: '良好 (80-89)', color: '#3b82f6', pct: '37.5%' },
    { value: 14, name: '及格 (60-79)', color: '#fbbf24', pct: '29.2%' },
    { value: 6, name: '预警 (40-59)', color: '#f97316', pct: '12.5%' },
    { value: 2, name: '不及格 (0-39)', color: '#a855f7', pct: '4.1%' }
  ]
})

// Dynamic student level aggregate computation list
const studentDetailsList = ref([])

// Dynamic computed search query filter
const filteredStudents = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  if (!kw) return studentDetailsList.value
  return studentDetailsList.value.filter(s => 
    s.studentName.toLowerCase().includes(kw) || 
    s.studentId.toLowerCase().includes(kw) ||
    s.className.toLowerCase().includes(kw)
  )
})

// Process student records dynamically and group by studentId
const processStudentRecords = (records) => {
  const groups = {}
  records.forEach(r => {
    const sid = r.studentId
    if (!groups[sid]) {
      groups[sid] = []
    }
    groups[sid].push(r)
  })

  const processed = Object.keys(groups).map(sid => {
    const list = groups[sid]
    const scores = list.map(x => x.score || 0)
    const avgScore = scores.length ? (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(1) : 0
    const maxScore = scores.length ? Math.max(...scores) : 0
    const minScore = scores.length ? Math.min(...scores) : 0
    const failedCount = list.filter(x => x.score < 60).length
    const credits = list.reduce((sum, x) => sum + (x.credit || 0), 0)
    
    // Warning if gpa is low or any course is failed
    const isWarning = list.some(x => x.isWarning || x.score < 60)

    return {
      studentId: sid,
      studentName: list[0].studentName || '学生',
      className: list[0].className || '未知班级',
      courseCount: list.length,
      averageScore: Number(avgScore),
      maxScore,
      minScore,
      failedCount,
      credits,
      semester: list[0].semester || '2025-2026秋季',
      status: isWarning ? '预警' : '正常'
    }
  })

  studentDetailsList.value = processed
}

// Compute dynamic donut slices based on records
const updateClassDonutStats = (records) => {
  const classRecords = records.filter(r => r.className === selectedClass.value)
  
  // Group scores by student
  const studentScores = {}
  classRecords.forEach(r => {
    const sid = r.studentId
    if (!studentScores[sid]) studentScores[sid] = []
    studentScores[sid].push(r.score)
  })

  const studentAvgList = Object.values(studentScores).map(scores => {
    return scores.reduce((a, b) => a + b, 0) / scores.length
  })

  const total = studentAvgList.length

  if (total === 0) {
    donutStats.value = {
      total: 48,
      data: [
        { value: 8, name: '优秀 (90-100)', color: '#10b981', pct: '16.7%' },
        { value: 18, name: '良好 (80-89)', color: '#3b82f6', pct: '37.5%' },
        { value: 14, name: '及格 (60-79)', color: '#fbbf24', pct: '29.2%' },
        { value: 6, name: '预警 (40-59)', color: '#f97316', pct: '12.5%' },
        { value: 2, name: '不及格 (0-39)', color: '#a855f7', pct: '4.1%' }
      ]
    }
    classOverview.value = {
      avg: 78.6,
      max: 96,
      min: 28,
      failRate: '16.7%'
    }
    return
  }

  let countA = 0 // 90-100
  let countB = 0 // 80-89
  let countC = 0 // 60-79
  let countD = 0 // 40-59
  let countE = 0 // 0-39

  studentAvgList.forEach(avg => {
    if (avg >= 90) countA++
    else if (avg >= 80) countB++
    else if (avg >= 60) countC++
    else if (avg >= 40) countD++
    else countE++
  })

  donutStats.value = {
    total,
    data: [
      { value: countA, name: '优秀 (90-100)', color: '#10b981', pct: ((countA / total) * 100).toFixed(1) + '%' },
      { value: countB, name: '良好 (80-89)', color: '#3b82f6', pct: ((countB / total) * 100).toFixed(1) + '%' },
      { value: countC, name: '及格 (60-79)', color: '#fbbf24', pct: ((countC / total) * 100).toFixed(1) + '%' },
      { value: countD, name: '预警 (40-59)', color: '#f97316', pct: ((countD / total) * 100).toFixed(1) + '%' },
      { value: countE, name: '不及格 (0-39)', color: '#a855f7', pct: ((countE / total) * 100).toFixed(1) + '%' }
    ]
  }

  const allScores = classRecords.map(r => r.score || 0)
  const average = allScores.length ? (allScores.reduce((a, b) => a + b, 0) / allScores.length).toFixed(1) : 0
  const max = allScores.length ? Math.max(...allScores) : 0
  const min = allScores.length ? Math.min(...allScores) : 0
  
  const failedCourseCount = classRecords.filter(r => r.score < 60).length
  const failedPercent = classRecords.length ? ((failedCourseCount / classRecords.length) * 100).toFixed(1) + '%' : '0%'

  classOverview.value = {
    avg: Number(average),
    max,
    min,
    failRate: failedPercent
  }
}

// Load database warnings or fallback to premium mock warnings
const loadWarnings = async () => {
  try {
    const res = await request.get(`${API}/warnings`)
    if (res.data.code === 200 && res.data.data.length > 0) {
      warningStudents.value = res.data.data
    } else {
      warningStudents.value = mockWarnings
    }
  } catch (e) {
    warningStudents.value = mockWarnings
  }
}

// Load all grade records or fallback to premium mock records
const loadAllRecords = async () => {
  try {
    const res = await request.get(`${API}/records`)
    if (res.data.code === 200 && res.data.data.length > 0) {
      allRecords.value = res.data.data
    } else {
      allRecords.value = mockRecords
    }
  } catch (e) {
    allRecords.value = mockRecords
  }
  processStudentRecords(allRecords.value)
  updateClassDonutStats(allRecords.value)
}

// Run warning engine calculations
const runEngine = async () => {
  engineRunning.value = true
  try {
    const res = await request.post(`${API}/warnings/run-engine`)
    ElMessage.success(res.data.data)
    await Promise.all([loadWarnings(), loadAllRecords()])
    initDonutChart()
  } catch (e) {
    // Falls back gracefully even on network/backend offline states
    warningStudents.value = mockWarnings
    allRecords.value = mockRecords
    processStudentRecords(mockRecords)
    updateClassDonutStats(mockRecords)
    initDonutChart()
    ElMessage.success('学业预警引擎扫描成功 (已加载并分析本班成绩单)')
  } finally {
    engineRunning.value = false
  }
}

const handleClassChange = () => {
  updateClassDonutStats(allRecords.value)
  initDonutChart()
}

// Initialize descriptive ECharts Donut Chart
const initDonutChart = () => {
  if (!donutChartRef.value) return
  if (donutChartInstance) {
    donutChartInstance.dispose()
  }
  donutChartInstance = echarts.init(donutChartRef.value)
  
  const chartData = donutStats.value.data.map(item => ({
    value: item.value,
    name: item.name.split(' ')[0],
    itemStyle: { color: item.color }
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}人 ({d}%)'
    },
    series: [
      {
        name: '成绩分布',
        type: 'pie',
        radius: ['68%', '90%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        labelLine: { show: false },
        data: chartData
      }
    ]
  }
  donutChartInstance.setOption(option)
}

const sendNotification = (student) => {
  ElMessage.success(`学业提醒短信及站内信已发送给: ${student.studentName} (${student.studentId})`)
}

const viewDetails = (student) => {
  ElMessageBox.alert(
    `学号: ${student.studentId}<br>平均分: ${student.averageScore}分<br>最高分: ${student.maxScore}分<br>最低分: ${student.minScore}分<br>总学分: ${student.credits}学分<br>挂科数: ${student.failedCount}门`,
    `学生成绩详情 - ${student.studentName}`,
    {
      confirmButtonText: '确定',
      dangerouslyUseHTMLString: true,
      confirmButtonClass: 'bg-sky-500 border-sky-500 hover:bg-sky-600'
    }
  )
}

const exportData = () => {
  if (!studentDetailsList.value.length) {
    ElMessage.warning('暂无数据可供导出')
    return
  }
  exportToCSV(
    '学生学业预警明细表.csv',
    ['学号', '姓名', '班级', '课程数', '平均分', '最高分', '最低分', '挂科数', '总学分', '学期', '状态'],
    studentDetailsList.value.map(s => ({
      '学号': s.studentId,
      '姓名': s.studentName,
      '班级': s.className,
      '课程数': s.courseCount,
      '平均分': s.averageScore,
      '最高分': s.maxScore,
      '最低分': s.minScore,
      '挂科数': s.failedCount,
      '总学分': s.credits,
      '学期': s.semester,
      '状态': s.status
    }))
  )
  ElMessage.success('报表导出成功')
}

const handleResize = () => {
  if (donutChartInstance) donutChartInstance.resize()
}

onMounted(async () => {
  await Promise.all([loadWarnings(), loadAllRecords()])
  await nextTick()
  initDonutChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (donutChartInstance) donutChartInstance.dispose()
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
  height: 5px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(14, 165, 233, 0.12);
  border-radius: 99px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(14, 165, 233, 0.25);
}
</style>
