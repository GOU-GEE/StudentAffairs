<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="openAddDialog" class="bg-purple-500 text-white hover:bg-purple-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Plus /></el-icon>新增课程
      </button>
    </teleport>
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in statList" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span><h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <el-input v-model="searchQuery" placeholder="搜索课程名称..." class="max-w-xs" clearable><template #prefix><el-icon><Search /></el-icon></template></el-input>
      <el-select v-model="filterType" placeholder="课程类型" class="w-32" clearable><el-option label="必修" value="必修" /><el-option label="选修" value="选修" /></el-select>
      <el-select v-model="filterSemester" placeholder="学期" class="w-40" clearable><el-option v-for="s in semesters" :key="s" :label="s" :value="s" /></el-select>
    </div>
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredCourses" style="width:100%" height="100%" stripe>
        <el-table-column prop="code" label="课程代码" width="120" />
        <el-table-column prop="name" label="课程名称" width="200" />
        <el-table-column label="类型" width="80"><template #default="{ row }"><span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="row.type==='必修'?'bg-purple-100 text-purple-700':'bg-gray-100 text-gray-600'">{{ row.type }}</span></template></el-table-column>
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="hours" label="学时" width="70" />
        <el-table-column prop="semester" label="开课学期" width="120" />
        <el-table-column prop="teacher" label="任课教师" width="120" />
        <el-table-column prop="className" label="授课班级" min-width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }"><button @click="editCourse(row)" class="text-xs font-semibold text-purple-600 hover:text-purple-800 mr-3">编辑</button><button @click="deleteCourse(row)" class="text-xs font-semibold text-red-500 hover:text-red-700">删除</button></template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="dialogVisible" :title="editingCourse ? '编辑课程' : '新增课程'" width="520px" destroy-on-close>
      <el-form :model="form" label-position="top">
        <el-form-item label="课程代码" required><el-input v-model="form.code" placeholder="如：CS201" /></el-form-item>
        <el-form-item label="课程名称" required><el-input v-model="form.name" placeholder="请输入课程名称" /></el-form-item>
        <div class="grid grid-cols-3 gap-x-4">
          <el-form-item label="类型"><el-select v-model="form.type" class="w-full"><el-option label="必修" value="必修" /><el-option label="选修" value="选修" /></el-select></el-form-item>
          <el-form-item label="学分"><el-input-number v-model="form.credit" :min="0.5" :max="10" :step="0.5" class="w-full" /></el-form-item>
          <el-form-item label="学时"><el-input-number v-model="form.hours" :min="16" :max="96" :step="16" class="w-full" /></el-form-item>
        </div>
        <el-form-item label="开课学期"><el-input v-model="form.semester" placeholder="如：2025-2026-1" /></el-form-item>
        <div class="grid grid-cols-2 gap-x-4">
          <el-form-item label="任课教师"><el-input v-model="form.teacher" placeholder="教师姓名" /></el-form-item>
          <el-form-item label="授课班级"><el-input v-model="form.className" placeholder="如：2023级软工1班、2班" /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <button @click="dialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="saveCourse" class="px-6 py-2 bg-purple-500 text-white rounded-lg text-sm font-bold hover:bg-purple-600 transition-colors">保存</button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/courses'

const courses = ref([])
const searchQuery = ref('')
const filterType = ref('')
const filterSemester = ref('')
const dialogVisible = ref(false)
const editingCourse = ref(null)
const form = ref({ code:'', name:'', type:'必修', credit:3, hours:48, semester:'2025-2026-1', teacher:'', className:'' })

const loadCourses = async () => {
  try {
    const params = {}
    if (filterType.value) params.type = filterType.value
    if (filterSemester.value) params.semester = filterSemester.value
    const res = await axios.get(API, { params })
    if (res.data.code === 200) courses.value = res.data.data
  } catch (e) { console.error(e) }
}

const semesters = ['2024-2025-2','2025-2026-1','2025-2026-2']

const filteredCourses = computed(() => {
  let list = courses.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(c => c.name.toLowerCase().includes(q) || c.code.toLowerCase().includes(q))
  }
  return list
})

const statList = computed(() => {
  const required = courses.value.filter(c => c.type === '必修').length
  const elective = courses.value.filter(c => c.type === '选修').length
  return [
    { label:'课程总数', value:courses.value.length },
    { label:'必修课', value:required },
    { label:'选修课', value:elective },
    { label:'总学分', value:courses.value.reduce((a,c)=>a+c.credit,0) }
  ]
})

const openAddDialog = () => {
  editingCourse.value = null
  form.value = { code:'', name:'', type:'必修', credit:3, hours:48, semester:'2025-2026-1', teacher:'', className:'' }
  dialogVisible.value = true
}

const editCourse = (row) => {
  editingCourse.value = row
  form.value = { ...row }
  dialogVisible.value = true
}

const saveCourse = async () => {
  if (!form.value.code || !form.value.name) { ElMessage.warning('课程代码和名称为必填'); return }
  try {
    if (editingCourse.value) {
      const res = await axios.put(`${API}/${editingCourse.value.id}`, form.value)
      if (res.data.code === 200) { ElMessage.success('课程已更新'); loadCourses() }
      else { ElMessage.error(res.data.msg) }
    } else {
      const res = await axios.post(API, form.value)
      if (res.data.code === 200) { ElMessage.success('课程已添加'); loadCourses() }
    }
    dialogVisible.value = false
  } catch (e) { ElMessage.error('操作失败') }
}

const deleteCourse = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除课程「${row.name}」？`, '确认删除', { confirmButtonText:'删除', cancelButtonText:'取消', type:'warning' })
    const res = await axios.delete(`${API}/${row.id}`)
    if (res.data.code === 200) { ElMessage.success('已删除'); loadCourses() }
  } catch (e) {}
}

watch([filterType, filterSemester], () => loadCourses())

onMounted(loadCourses)
</script>
