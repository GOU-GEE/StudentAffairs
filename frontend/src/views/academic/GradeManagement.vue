<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="importDialogVisible = true" class="bg-purple-500 text-white hover:bg-purple-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><UploadFilled /></el-icon>批量导入成绩
      </button>
    </teleport>
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in statList" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span><h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <el-input v-model="searchQuery" placeholder="搜索学号、姓名、课程..." class="max-w-xs" clearable><template #prefix><el-icon><Search /></el-icon></template></el-input>
      <el-select v-model="filterSemester" placeholder="学期" class="w-40" clearable><el-option v-for="s in semesters" :key="s" :label="s" :value="s" /></el-select>
      <el-select v-model="filterClass" placeholder="班级" class="w-40" clearable><el-option v-for="c in classes" :key="c" :label="c" :value="c" /></el-select>
      <span class="text-xs text-secondary ml-auto">{{ filteredRecords.length }} 条</span>
    </div>
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredRecords" style="width: 100%" height="100%" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="courseName" label="课程" width="180" />
        <el-table-column prop="score" label="成绩" width="80"><template #default="{ row }"><span :class="row.score < 60 ? 'text-red-500 font-bold' : ''">{{ row.score }}</span></template></el-table-column>
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="操作" width="100" fixed="right"><template #default="{ row }"><button @click="editGrade(row)" class="text-xs font-semibold text-purple-600 hover:text-purple-800 transition-colors">修改</button></template></el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="importDialogVisible" title="批量导入成绩" width="480px" destroy-on-close>
      <div class="text-center py-8">
        <el-icon :size="56" color="#8b5cf6"><UploadFilled /></el-icon>
        <p class="text-sm text-secondary mt-4 mb-2">支持 Excel (.xlsx) 格式</p>
        <p class="text-xs text-outline mb-6">模板：学号、姓名、班级、课程、成绩、学分、学期</p>
        <div class="border-2 border-dashed border-outline-variant/40 rounded-xl p-8 hover:border-purple-500/40 transition-colors cursor-pointer bg-surface-container-low"><p class="text-sm text-secondary">拖拽文件或 <span class="text-purple-600 font-semibold">点击选择</span></p></div>
        <button @click="importDialogVisible = false; ElMessage.success('演示：成功导入 86 条成绩记录')" class="mt-6 px-6 py-2 bg-purple-500 text-white rounded-lg text-sm font-bold hover:bg-purple-600 transition-colors">确认导入</button>
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'; import { ElMessage } from 'element-plus'; import { UploadFilled, Search } from '@element-plus/icons-vue'
const records = ref([
  { id:1, studentId:'202301042', studentName:'张小明', className:'软工2班', courseName:'数据结构', score:95, credit:4, semester:'2024-2025-1' },
  { id:2, studentId:'202301042', studentName:'张小明', className:'软工2班', courseName:'算法设计', score:88, credit:3, semester:'2024-2025-1' },
  { id:3, studentId:'202301043', studentName:'李四', className:'软工2班', courseName:'数据结构', score:45, credit:4, semester:'2024-2025-1' },
  { id:4, studentId:'202301043', studentName:'李四', className:'软工2班', courseName:'算法设计', score:55, credit:3, semester:'2024-2025-1' },
  { id:5, studentId:'202301044', studentName:'王五', className:'软工1班', courseName:'数据结构', score:75, credit:4, semester:'2024-2025-1' },
  { id:6, studentId:'202301044', studentName:'王五', className:'软工1班', courseName:'线性代数', score:82, credit:3, semester:'2024-2025-1' },
])
const searchQuery = ref(''); const filterSemester = ref(''); const filterClass = ref(''); const importDialogVisible = ref(false)
const semesters = ['2024-2025-1','2024-2025-2','2025-2026-1']; const classes = ['软工1班','软工2班','计科3班']
const filteredRecords = computed(() => {
  let list = records.value
  if (searchQuery.value) { const q = searchQuery.value.toLowerCase(); list = list.filter(r => r.studentId.includes(q) || r.studentName.includes(q) || r.courseName.includes(q)) }
  if (filterSemester.value) list = list.filter(r => r.semester === filterSemester.value)
  if (filterClass.value) list = list.filter(r => r.className === filterClass.value)
  return list
})
const statList = computed(() => {
  const total = records.value.length; const failed = records.value.filter(r => r.score < 60).length
  const avg = total > 0 ? (records.value.reduce((a,b)=>a+b.score,0)/total).toFixed(1) : 0
  return [{ label:'成绩记录', value:total },{ label:'挂科人次', value:failed },{ label:'平均分', value:avg },{ label:'课程数', value:new Set(records.value.map(r=>r.courseName)).size }]
})
const editGrade = (row) => { ElMessage.success(`演示：修改 ${row.studentName} - ${row.courseName} 成绩`) }
</script>
