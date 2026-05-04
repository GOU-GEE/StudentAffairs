<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="runEngine" :disabled="engineRunning"
              class="bg-error text-white hover:bg-error/80 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md disabled:opacity-50">
        <el-icon :class="engineRunning ? 'animate-spin' : ''"><Refresh /></el-icon>
        {{ engineRunning ? '引擎运行中...' : '运行预警引擎' }}
      </button>
    </teleport>

    <!-- Bento Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6">
      
      <!-- 成绩分布柱状图 -->
      <div class="md:col-span-7 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] h-[380px] flex flex-col">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold text-on-surface tracking-tight">班级成绩分布</h3>
          <select v-model="selectedClass" @change="loadDistribution"
                  class="bg-surface-container-low border border-outline-variant/30 rounded-lg px-3 py-1.5 text-sm focus:ring-1 focus:ring-primary focus:border-primary outline-none text-on-surface">
            <option value="软工2班">软工2班</option>
          </select>
        </div>
        <div ref="barChartRef" class="flex-1"></div>
      </div>

      <!-- 预警引擎结果 -->
      <div class="md:col-span-5 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] h-[380px] flex flex-col">
        <div class="flex items-center gap-2 mb-4">
          <span class="w-2.5 h-2.5 rounded-full bg-error animate-pulse"></span>
          <h3 class="text-lg font-semibold text-on-surface tracking-tight">学业预警学生</h3>
          <span class="ml-auto text-xs font-bold bg-error-container text-error px-2.5 py-1 rounded-full border border-error/20">
            {{ warningStudents.length }} 人
          </span>
        </div>
        <div class="flex-1 overflow-y-auto space-y-3 custom-scrollbar pr-2">
          <div v-for="(s, idx) in warningStudents" :key="idx"
               class="flex items-center gap-4 p-3 bg-surface rounded-xl border border-error/15 hover:border-error/30 transition-colors">
            <img :src="`https://i.pravatar.cc/150?u=${s.studentId}`" class="w-10 h-10 rounded-full border border-outline-variant/30">
            <div class="flex-1 min-w-0">
              <h4 class="font-bold text-sm text-on-surface">{{ s.studentName }}</h4>
              <p class="text-xs text-secondary truncate">{{ s.studentId }} · {{ s.className }} · {{ s.courseName }}</p>
            </div>
            <div class="text-right flex-shrink-0">
              <span class="font-bold text-error text-sm">{{ s.score }}分</span>
              <p class="text-xs text-secondary">{{ s.courseName }}</p>
            </div>
          </div>
          <div v-if="warningStudents.length === 0" class="py-12 flex flex-col items-center text-secondary">
            <el-icon :size="40" class="mb-2 opacity-30"><CircleCheck /></el-icon>
            <p class="text-sm">暂无预警学生，点击运行引擎检测</p>
          </div>
        </div>
      </div>

      <!-- 成绩明细列表 -->
      <div class="md:col-span-12 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_2px_8px_rgba(25,28,30,0.02)]">
        <h3 class="text-lg font-semibold text-on-surface tracking-tight mb-4">全体学生成绩明细</h3>
        <el-table :data="allRecords" style="width: 100%" max-height="280">
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="studentName" label="姓名" width="90" />
          <el-table-column prop="className" label="班级" width="100" />
          <el-table-column prop="courseName" label="课程" min-width="150" />
          <el-table-column prop="score" label="成绩" width="100">
            <template #default="{ row }">
              <span :class="row.score < 60 ? 'text-error font-bold' : row.score >= 90 ? 'text-green-600 font-bold' : 'text-on-surface'">
                {{ row.score }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="credit" label="学分" width="80" />
          <el-table-column prop="semester" label="学期" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <span v-if="row.isWarning" class="text-[0.6875rem] font-bold px-2 py-1 rounded bg-error-container text-error border border-error/20">预警</span>
              <span v-else class="text-[0.6875rem] font-bold px-2 py-1 rounded bg-green-100 text-green-700 border border-green-200">正常</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { Refresh, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import * as echarts from 'echarts'

const barChartRef = ref(null)
const selectedClass = ref('软工2班')
const warningStudents = ref([])
const allRecords = ref([])
const engineRunning = ref(false)
let barChart = null

const API = 'http://localhost:8080/api/academic'

const loadDistribution = async () => {
  try {
    const res = await axios.get(`${API}/class-distribution`, { params: { className: selectedClass.value } })
    if (res.data.code === 200) renderBarChart(res.data.data)
  } catch (e) { console.error(e) }
}

const loadWarnings = async () => {
  try {
    const res = await axios.get(`${API}/warnings`)
    if (res.data.code === 200) warningStudents.value = res.data.data
  } catch (e) { console.error(e) }
}

const loadAllRecords = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/academic/student-records', { params: { studentId: '202301043' } })
    const res2 = await axios.get('http://localhost:8080/api/academic/student-records', { params: { studentId: '202301042' } })
    const res3 = await axios.get('http://localhost:8080/api/academic/student-records', { params: { studentId: '202301044' } })
    if (res.data.code === 200) allRecords.value = [...res.data.data, ...res2.data.data, ...res3.data.data]
  } catch (e) { console.error(e) }
}

const runEngine = async () => {
  engineRunning.value = true
  try {
    const res = await axios.post(`${API}/warnings/run-engine`)
    ElMessage.success(res.data.data)
    await loadWarnings()
  } catch (e) {
    ElMessage.error('引擎运行失败，请确保后端已启动')
  } finally {
    engineRunning.value = false
  }
}

const renderBarChart = (data) => {
  if (!barChartRef.value) return
  if (!barChart) barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    grid: { left: 16, right: 16, top: 16, bottom: 20, containLabel: true },
    xAxis: {
      type: 'category',
      data: data.labels,
      axisLabel: { fontSize: 11, color: '#79747e' },
      axisLine: { lineStyle: { color: '#cac4d0' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { fontSize: 11, color: '#79747e' },
      splitLine: { lineStyle: { color: '#cac4d020' } }
    },
    series: [{
      data: data.data,
      type: 'bar',
      barMaxWidth: 48,
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: (params) => {
          const colors = ['#ba1a1a', '#f59e0b', '#316bf3', '#10b981', '#6c5dd3']
          return colors[params.dataIndex]
        }
      },
      label: { show: true, position: 'top', fontSize: 12, fontWeight: 'bold', color: '#191c1e' }
    }]
  })
}

onMounted(async () => {
  await Promise.all([loadWarnings(), loadAllRecords()])
  await nextTick()
  await loadDistribution()
  window.addEventListener('resize', () => barChart?.resize())
})
</script>
