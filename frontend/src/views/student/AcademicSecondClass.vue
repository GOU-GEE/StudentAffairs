<template>
  <div class="h-full flex flex-col">
    <div class="mb-8">
      <h2 class="text-[2.5rem] leading-none font-bold tracking-tight text-on-surface mb-2">学业成绩</h2>
      <p class="text-[0.875rem] text-secondary font-medium">Academic Records & Radar Analysis</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-12 gap-6">

      <!-- GPA 卡片 -->
      <div class="md:col-span-4 bg-tertiary-container text-on-tertiary-container rounded-xl p-6 shadow-[0_4px_12px_rgba(49,107,243,0.2)] relative overflow-hidden">
        <div class="absolute -right-4 -top-4 opacity-20"><el-icon :size="100"><DataLine /></el-icon></div>
        <div class="relative z-10">
          <p class="text-sm font-bold uppercase tracking-wider mb-1 opacity-80">当前加权平均分</p>
          <p class="text-[3.5rem] font-bold leading-none">{{ gpa.toFixed(1) }}</p>
          <p class="text-sm mt-1 opacity-80">总学分: {{ totalCredits }} | {{ warningStatus }}</p>
        </div>
      </div>

      <!-- 雷达图 -->
      <div class="md:col-span-8 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] h-[280px] flex flex-col">
        <h3 class="text-base font-semibold text-on-surface tracking-tight mb-4">学科能力雷达图</h3>
        <div ref="radarChartRef" class="flex-1"></div>
      </div>

      <!-- 成绩明细 -->
      <div class="md:col-span-12 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_2px_8px_rgba(25,28,30,0.02)]">
        <h3 class="text-base font-semibold text-on-surface tracking-tight mb-4">本学期成绩明细</h3>
        <div class="space-y-3">
          <div v-for="(r, i) in records" :key="i"
               class="flex items-center gap-4 p-4 rounded-xl border bg-surface hover:bg-surface-container transition-colors"
               :class="r.score < 60 ? 'border-error/20 bg-error-container/10' : 'border-outline-variant/20'">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-semibold text-sm text-on-surface">{{ r.courseName }}</span>
                <span class="text-xs px-1.5 py-0.5 rounded bg-surface-container-high text-secondary border border-outline-variant/20">{{ r.courseType }}</span>
                <span class="text-xs text-outline">{{ r.credit }}学分</span>
              </div>
              <div class="flex items-center gap-2">
                <div class="flex-1 bg-surface-container-high h-2 rounded-full overflow-hidden max-w-[200px]">
                  <div class="h-full rounded-full transition-all"
                       :class="r.score >= 90 ? 'bg-green-500' : r.score >= 60 ? 'bg-primary' : 'bg-error'"
                       :style="`width: ${r.score}%`"></div>
                </div>
              </div>
            </div>
            <div class="text-right">
              <span class="text-2xl font-bold" :class="r.score < 60 ? 'text-error' : r.score >= 90 ? 'text-green-600' : 'text-on-surface'">
                {{ r.score }}
              </span>
              <p class="text-xs text-secondary">{{ r.semester }}</p>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { DataLine } from '@element-plus/icons-vue'
import axios from 'axios'
import * as echarts from 'echarts'

const radarChartRef = ref(null)
const records = ref([])
const gpa = ref(0)
const totalCredits = ref(0)
let radarChart = null

const warningStatus = computed(() => gpa.value < 65 ? '⚠️ 学业预警' : '✅ 学业正常')

const API = 'http://localhost:8080/api/academic'
const STUDENT_ID = '202301042' // 当前登录学生

const loadData = async () => {
  try {
    const [recordsRes, radarRes] = await Promise.all([
      axios.get(`${API}/student-records`, { params: { studentId: STUDENT_ID } }),
      axios.get(`${API}/student-radar`, { params: { studentId: STUDENT_ID } })
    ])

    if (recordsRes.data.code === 200) {
      records.value = recordsRes.data.data
    }

    if (radarRes.data.code === 200) {
      const d = radarRes.data.data
      gpa.value = d.gpa
      totalCredits.value = d.totalCredits
      await nextTick()
      renderRadar(d.subjects, d.scores)
    }
  } catch (e) {
    console.error('获取成绩失败，请确保后端已启动', e)
  }
}

const renderRadar = (subjects, scores) => {
  if (!radarChartRef.value) return
  if (!radarChart) radarChart = echarts.init(radarChartRef.value)
  
  radarChart.setOption({
    radar: {
      indicator: subjects.map(s => ({ name: s, max: 100 })),
      axisName: { color: '#49454f', fontSize: 11 },
      splitLine: { lineStyle: { color: '#cac4d030' } },
      splitArea: { show: false },
      axisLine: { lineStyle: { color: '#cac4d040' } }
    },
    series: [{
      type: 'radar',
      data: [{
        value: scores,
        name: '个人成绩',
        areaStyle: { color: 'rgba(49, 107, 243, 0.12)' },
        lineStyle: { color: '#316bf3', width: 2 },
        itemStyle: { color: '#316bf3' }
      }]
    }]
  })
}

onMounted(async () => {
  await loadData()
  window.addEventListener('resize', () => radarChart?.resize())
})
</script>
