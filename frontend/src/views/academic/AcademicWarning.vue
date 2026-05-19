<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="runEngine" class="bg-purple-500 text-white hover:bg-purple-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><VideoPlay /></el-icon>运行预警引擎
      </button>
    </teleport>
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in statList" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span><h3 class="text-2xl font-bold" :class="stat.color">{{ stat.value }}</h3>
      </div>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 flex-1">
      <div class="md:col-span-2 bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden">
        <div class="px-6 py-4 border-b border-outline-variant/10 flex justify-between items-center"><h3 class="text-[1.125rem] font-semibold text-on-surface">预警学生列表</h3><el-input v-model="searchQuery" placeholder="搜索..." class="max-w-[180px]" clearable><template #prefix><el-icon><Search /></el-icon></template></el-input></div>
        <el-table :data="filteredWarnings" style="width:100%" height="100%" stripe>
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="studentName" label="姓名" width="100" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column prop="failedCount" label="挂科数" width="80"><template #default="{ row }"><span class="font-bold text-red-500">{{ row.failedCount }}</span></template></el-table-column>
          <el-table-column prop="gpa" label="GPA" width="80" />
          <el-table-column label="预警等级" width="90"><template #default="{ row }"><span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="row.level==='严重'?'bg-red-100 text-red-700':'bg-amber-100 text-amber-700'">{{ row.level }}</span></template></el-table-column>
          <el-table-column label="操作" width="90"><template #default="{ row }"><button @click="viewDetail(row)" class="text-xs font-semibold text-purple-600 hover:text-purple-800">详情</button></template></el-table-column>
        </el-table>
      </div>
      <div class="bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">预警配置</h3>
        <div class="space-y-4">
          <div><label class="text-xs font-bold text-secondary uppercase block mb-1.5">挂科数阈值</label><el-input-number v-model="config.failThreshold" :min="1" :max="10" class="w-full" /></div>
          <div><label class="text-xs font-bold text-secondary uppercase block mb-1.5">GPA 下限</label><el-input-number v-model="config.gpaThreshold" :min="0" :max="4" :step="0.1" :precision="1" class="w-full" /></div>
          <div><label class="text-xs font-bold text-secondary uppercase block mb-1.5">连续下滑学期数</label><el-input-number v-model="config.declineSemesters" :min="1" :max="4" class="w-full" /></div>
          <div class="pt-2 space-y-2">
            <label class="flex items-center gap-2 text-sm"><input type="checkbox" v-model="config.autoNotify" class="rounded" /><span>自动通知辅导员</span></label>
            <label class="flex items-center gap-2 text-sm"><input type="checkbox" v-model="config.autoEmail" class="rounded" /><span>发送邮件通知学生</span></label>
          </div>
          <button @click="saveConfig" class="w-full mt-4 px-4 py-2.5 bg-purple-500 text-white rounded-xl text-sm font-bold hover:bg-purple-600 transition-colors">保存配置</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoPlay, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/academic'

const searchQuery = ref('')
const warnings = ref([])
const config = ref({ failThreshold: 2, gpaThreshold: 2.0, declineSemesters: 2, autoNotify: true, autoEmail: false })

const loadWarnings = async () => {
  try {
    const res = await axios.get(`${API}/warnings`)
    if (res.data.code === 200) {
      const all = res.data.data
      const byStudent = {}
      all.forEach(r => {
        if (!byStudent[r.studentId]) {
          byStudent[r.studentId] = {
            id: r.id,
            studentId: r.studentId,
            studentName: r.studentName,
            className: r.className,
            failedCount: 0,
            failedCourses: [],
            totalScore: 0,
            totalCredit: 0,
          }
        }
        const s = byStudent[r.studentId]
        s.totalScore += r.score * r.credit
        s.totalCredit += r.credit
        if (r.score < 60) { s.failedCount++; s.failedCourses.push(r.courseName + r.score + '分') }
      })
      warnings.value = Object.values(byStudent).map(s => ({
        id: s.id,
        studentId: s.studentId,
        studentName: s.studentName,
        className: s.className,
        failedCount: s.failedCount,
        gpa: s.totalCredit > 0 ? (s.totalScore / s.totalCredit).toFixed(1) : 0,
        level: s.failedCount >= 2 ? '严重' : '一般',
        reason: s.failedCourses.join('、'),
      }))
    }
  } catch (e) { console.error(e) }
}

const filteredWarnings = computed(() => {
  if (!searchQuery.value) return warnings.value
  const q = searchQuery.value.toLowerCase()
  return warnings.value.filter(w => w.studentId.includes(q) || w.studentName.includes(q))
})

const statList = computed(() => [
  { label:'预警学生', value:warnings.value.length, color:'text-red-500' },
  { label:'严重预警', value:warnings.value.filter(w=>w.level==='严重').length, color:'text-red-500' },
  { label:'一般预警', value:warnings.value.filter(w=>w.level==='一般').length, color:'text-amber-600' },
  { label:'挂科总人次', value:warnings.value.reduce((a,w)=>a+w.failedCount,0), color:'text-green-600' },
])

const runEngine = async () => {
  try {
    const res = await axios.post(`${API}/warnings/run-engine`)
    if (res.data.code === 200) {
      ElMessage.success(res.data.data)
      loadWarnings()
    }
  } catch (e) { ElMessage.error('预警引擎运行失败') }
}

const saveConfig = () => { ElMessage.success('预警配置已保存') }
const viewDetail = (row) => { ElMessage.info(`${row.studentName}：${row.reason}`) }

onMounted(loadWarnings)
</script>
