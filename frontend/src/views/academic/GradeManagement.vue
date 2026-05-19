<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="openAddDialog" class="bg-purple-500 text-white hover:bg-purple-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md mr-2">
        <el-icon :size="14"><Plus /></el-icon>新增成绩
      </button>
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
        <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <button @click="editGrade(row)" class="text-xs font-semibold text-purple-600 hover:text-purple-800 mr-3">修改</button>
          <button @click="deleteGrade(row)" class="text-xs font-semibold text-red-500 hover:text-red-700">删除</button>
        </template>
      </el-table-column>
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
    <el-dialog v-model="editDialogVisible" :title="editingRecord ? '修改成绩' : '新增成绩'" width="520px" destroy-on-close>
      <el-form :model="form" label-position="top">
        <div class="grid grid-cols-2 gap-x-4">
          <el-form-item label="学号" required><el-input v-model="form.studentId" /></el-form-item>
          <el-form-item label="姓名" required><el-input v-model="form.studentName" /></el-form-item>
        </div>
        <el-form-item label="班级"><el-input v-model="form.className" /></el-form-item>
        <div class="grid grid-cols-2 gap-x-4">
          <el-form-item label="课程" required><el-input v-model="form.courseName" /></el-form-item>
          <el-form-item label="类型"><el-select v-model="form.courseType" class="w-full"><el-option label="必修" value="必修" /><el-option label="选修" value="选修" /></el-select></el-form-item>
        </div>
        <div class="grid grid-cols-3 gap-x-4">
          <el-form-item label="成绩"><el-input-number v-model="form.score" :min="0" :max="100" class="w-full" /></el-form-item>
          <el-form-item label="学分"><el-input-number v-model="form.credit" :min="0" :step="0.5" class="w-full" /></el-form-item>
          <el-form-item label="学期"><el-input v-model="form.semester" /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <button @click="editDialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="saveGrade" class="px-6 py-2 bg-purple-500 text-white rounded-lg text-sm font-bold hover:bg-purple-600 transition-colors">保存</button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Search, Plus } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/academic'

const records = ref([])
const searchQuery = ref('')
const filterSemester = ref('')
const filterClass = ref('')
const importDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editingRecord = ref(null)
const form = ref({ studentId:'', studentName:'', className:'', courseName:'', courseType:'必修', score:0, credit:3, semester:'2024-2025-1' })

const loadRecords = async () => {
  try {
    const res = await axios.get(`${API}/records`)
    if (res.data.code === 200) records.value = res.data.data
  } catch (e) { console.error(e) }
}

const semesters = ['2024-2025-1','2024-2025-2','2025-2026-1']
const classes = computed(() => [...new Set(records.value.map(r => r.className))])

const filteredRecords = computed(() => {
  let list = records.value
  if (searchQuery.value) { const q = searchQuery.value.toLowerCase(); list = list.filter(r => r.studentId.includes(q) || r.studentName.includes(q) || r.courseName.includes(q)) }
  if (filterSemester.value) list = list.filter(r => r.semester === filterSemester.value)
  if (filterClass.value) list = list.filter(r => r.className === filterClass.value)
  return list
})

const statList = computed(() => {
  const total = records.value.length
  const failed = records.value.filter(r => r.score < 60).length
  const avg = total > 0 ? (records.value.reduce((a,b)=>a+b.score,0)/total).toFixed(1) : 0
  return [
    { label:'成绩记录', value:total },
    { label:'挂科人次', value:failed },
    { label:'平均分', value:avg },
    { label:'课程数', value:new Set(records.value.map(r=>r.courseName)).size }
  ]
})

const openAddDialog = () => {
  editingRecord.value = null
  form.value = { studentId:'', studentName:'', className:'', courseName:'', courseType:'必修', score:0, credit:3, semester:'2024-2025-1' }
  editDialogVisible.value = true
}

const editGrade = (row) => {
  editingRecord.value = row
  form.value = { ...row }
  editDialogVisible.value = true
}

const saveGrade = async () => {
  if (!form.value.studentId || !form.value.studentName || !form.value.courseName) {
    ElMessage.warning('请填写必要字段'); return
  }
  try {
    if (editingRecord.value) {
      const res = await axios.put(`${API}/records/${editingRecord.value.id}`, form.value)
      if (res.data.code === 200) { ElMessage.success('已更新'); loadRecords() }
    } else {
      const res = await axios.post(`${API}/records`, form.value)
      if (res.data.code === 200) { ElMessage.success('已添加'); loadRecords() }
    }
    editDialogVisible.value = false
  } catch (e) { ElMessage.error('操作失败') }
}

const deleteGrade = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除 ${row.studentName} 的 ${row.courseName} 成绩？`, '确认删除', { confirmButtonText:'删除', cancelButtonText:'取消', type:'warning' })
    const res = await axios.delete(`${API}/records/${row.id}`)
    if (res.data.code === 200) { ElMessage.success('已删除'); loadRecords() }
  } catch (e) {}
}

onMounted(loadRecords)
</script>
