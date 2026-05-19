<template>
  <div class="h-full flex flex-col">
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in stats" :key="idx" class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/15 hover:shadow-md transition-shadow">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-[2rem] font-bold text-on-surface mt-1">{{ stat.value }}</h3>
        <p class="text-xs text-secondary mt-1">{{ stat.desc }}</p>
      </div>
    </div>
    <div class="flex items-center gap-3 mb-4">
      <button class="bg-purple-500 text-white hover:bg-purple-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Download /></el-icon>导出报表
      </button>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 auto-rows-min">
      <div class="md:col-span-5 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">快捷操作</h3>
        <div class="grid grid-cols-2 gap-3">
          <button @click="router.push('/academic/grades')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15"><el-icon :size="28" color="#8b5cf6"><DataAnalysis /></el-icon><span class="text-sm font-semibold text-on-surface">成绩管理</span><span class="text-xs text-secondary">录入与导入</span></button>
          <button @click="router.push('/academic/warnings')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15"><el-icon :size="28" color="#8b5cf6"><Warning /></el-icon><span class="text-sm font-semibold text-on-surface">学业预警</span><span class="text-xs text-secondary">预警配置</span></button>
          <button @click="router.push('/academic/courses')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15"><el-icon :size="28" color="#8b5cf6"><Reading /></el-icon><span class="text-sm font-semibold text-on-surface">课程管理</span><span class="text-xs text-secondary">课程安排</span></button>
          <button class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15"><el-icon :size="28" color="#8b5cf6"><TrendCharts /></el-icon><span class="text-sm font-semibold text-on-surface">统计分析</span><span class="text-xs text-secondary">学业数据</span></button>
        </div>
      </div>
      <div class="md:col-span-7 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 flex flex-col h-[320px]">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">预警学生列表</h3>
        <div class="flex-1 overflow-y-auto pr-2 space-y-3 custom-scrollbar">
          <div v-for="(w, idx) in warningStudents" :key="idx" class="bg-surface rounded-xl p-4 border border-outline-variant/15 flex items-center gap-4">
            <div class="w-10 h-10 rounded-full bg-purple-100 flex items-center justify-center text-purple-600 font-bold text-sm">{{ w.name[0] }}</div>
            <div class="flex-1 min-w-0"><h4 class="font-semibold text-[0.875rem] text-on-surface">{{ w.name }}</h4><p class="text-xs text-secondary">{{ w.studentId }} | {{ w.className }} | 挂科 {{ w.failedCount }} 门</p></div>
            <span class="text-xs font-bold px-2 py-1 rounded-full" :class="w.level === '严重' ? 'bg-red-100 text-red-700' : 'bg-amber-100 text-amber-700'">{{ w.level }}预警</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Download, DataAnalysis, Warning, Reading, TrendCharts } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const API = '/api/academic'

const stats = ref([
  { label: '学生总数', value: '--', desc: '' },
  { label: '课程总数', value: '--', desc: '' },
  { label: '预警学生', value: '--', desc: '' },
  { label: '平均分', value: '--', desc: '' },
])
const warningStudents = ref([])

onMounted(async () => {
  try {
    const dashRes = await request.get(`${API}/dashboard`)
    if (dashRes.data.code === 200) {
      const d = dashRes.data.data
      stats.value = [
        { label: '学生总数', value: d.totalStudents, desc: '已录入成绩的学生' },
        { label: '课程总数', value: d.totalCourses || '--', desc: '' },
        { label: '预警学生', value: d.warningCount, desc: '' },
        { label: '平均分', value: d.avgScore, desc: '全体学生均分' },
      ]
      warningStudents.value = (d.recentWarnings || []).map(w => ({
        name: w.studentName,
        studentId: w.studentId,
        className: w.className,
        failedCount: '--',
        level: w.isWarning ? '一般' : '正常',
      }))
    }
  } catch (e) { console.error(e) }
})
</script>
