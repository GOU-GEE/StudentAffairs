<template>
  <div class="h-full flex flex-col">
    <div class="flex items-center gap-3 mb-4">
      <button @click="importDialogVisible = true" class="bg-surface-container-high text-on-surface hover:bg-outline-variant/30 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 border border-outline-variant/20 shadow-sm">
        <el-icon :size="14"><UploadFilled /></el-icon>批量导入
      </button>
      <button @click="openAddDialog" class="bg-error text-on-error hover:bg-error/80 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Plus /></el-icon>新增学生
      </button>
    </div>

    <!-- Search Bar -->
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <div class="flex-1 flex gap-3">
        <el-input v-model="searchQuery" placeholder="搜索学号、姓名..." class="max-w-xs" clearable @input="filterStudents">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filterCollege" placeholder="学院筛选" class="w-40" clearable @change="filterStudents">
          <el-option v-for="c in colleges" :key="c" :label="c" :value="c" />
        </el-select>
      </div>
      <span class="text-xs text-secondary">{{ filteredStudents.length }} 条记录</span>
    </div>

    <!-- Student Table -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredStudents" style="width: 100%" height="100%" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="college" label="学院" width="140" />
        <el-table-column prop="major" label="专业" width="160" />
        <el-table-column prop="gradeClass" label="年级班级" width="120" />
        <el-table-column label="联系方式" width="160">
          <template #default="{ row }">
            {{ row.extraInfo?.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <button @click="openEditDialog(row)" class="text-xs font-semibold text-primary hover:text-error transition-colors mr-3">编辑</button>
            <button @click="handleDelete(row)" class="text-xs font-semibold text-secondary hover:text-error transition-colors">删除</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学生信息' : '新增学生'" width="600px" destroy-on-close>
      <el-form :model="form" label-position="top" class="grid grid-cols-2 gap-x-6 gap-y-2">
        <el-form-item label="学号" required>
          <el-input v-model="form.studentId" placeholder="请输入学号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="form.college" placeholder="请输入学院" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="form.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="年级班级">
          <el-input v-model="form.gradeClass" placeholder="如：2024级1班" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" placeholder="18位身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="电话" class="col-span-2">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <button @click="dialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="handleSave" class="px-6 py-2 bg-error text-on-error rounded-lg text-sm font-bold hover:bg-error/80 transition-colors">保存</button>
      </template>
    </el-dialog>

    <!-- Import Dialog -->
    <el-dialog v-model="importDialogVisible" title="批量导入学生信息" width="480px" destroy-on-close>
      <div class="text-center py-8">
        <el-icon :size="56" color="var(--color-outline-variant)"><UploadFilled /></el-icon>
        <p class="text-sm text-secondary mt-4 mb-2">支持 Excel (.xlsx) 或 CSV 格式</p>
        <p class="text-xs text-outline mb-6">请按照模板格式填写：学号、姓名、学院、专业、年级班级、身份证号</p>
        <div class="border-2 border-dashed border-outline-variant/40 rounded-xl p-8 hover:border-error/40 transition-colors cursor-pointer bg-surface-container-low">
          <p class="text-sm text-secondary">拖拽文件到此处或 <span class="text-error font-semibold">点击选择文件</span></p>
        </div>
        <button @click="importDialogVisible = false; ElMessage.success('演示模式：模拟导入成功')" class="mt-6 px-6 py-2 bg-error text-on-error rounded-lg text-sm font-bold hover:bg-error/80 transition-colors">
          确认导入
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Plus, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/admin'

const students = ref([])
const searchQuery = ref('')
const filterCollege = ref('')
const dialogVisible = ref(false)
const importDialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const form = ref({ studentId: '', name: '', college: '', major: '', gradeClass: '', idCard: '', phone: '' })

const colleges = ref(['计算机学院', '数学与信息学院', '文学院', '外国语学院'])

const filteredStudents = computed(() => {
  let list = students.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(s => s.studentId.includes(q) || s.name.includes(q))
  }
  if (filterCollege.value) {
    list = list.filter(s => s.college === filterCollege.value)
  }
  return list
})

const fetchStudents = async () => {
  try {
    const res = await request.get(`${API}/students`)
    students.value = res.data.data || []
  } catch {
    // 降级 mock
    students.value = [
      { id: 1, studentId: '202301042', name: '张小明', college: '计算机学院', major: '软件工程', gradeClass: '2023级2班', idCard: '510000200103150000', extraInfo: { phone: '138****0001' } },
      { id: 2, studentId: '202301043', name: '李四', college: '计算机学院', major: '软件工程', gradeClass: '2023级2班', idCard: '510000200205200000', extraInfo: { phone: '159****0002' } },
      { id: 3, studentId: '202301044', name: '王五', college: '计算机学院', major: '计算机科学与技术', gradeClass: '2023级1班', idCard: '510000200308150000', extraInfo: { phone: '177****0003' } },
    ]
  }
}

const openAddDialog = () => {
  isEdit.value = false
  editingId.value = null
  form.value = { studentId: '', name: '', college: '', major: '', gradeClass: '', idCard: '', phone: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  editingId.value = row.id
  form.value = {
    studentId: row.studentId,
    name: row.name,
    college: row.college || '',
    major: row.major || '',
    gradeClass: row.gradeClass || '',
    idCard: row.idCard || '',
    phone: row.extraInfo?.phone || '',
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.studentId || !form.value.name) {
    ElMessage.warning('学号和姓名为必填项')
    return
  }
  const payload = {
    studentId: form.value.studentId,
    name: form.value.name,
    college: form.value.college,
    major: form.value.major,
    gradeClass: form.value.gradeClass,
    idCard: form.value.idCard,
    extraInfo: { phone: form.value.phone },
  }
  try {
    if (isEdit.value) {
      await request.put(`${API}/students/${editingId.value}`, payload)
      ElMessage.success('学生信息已更新')
    } else {
      await request.post(`${API}/students`, payload)
      ElMessage.success('学生信息已添加')
    }
    dialogVisible.value = false
    fetchStudents()
  } catch {
    ElMessage.success('演示模式：操作成功')
    dialogVisible.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学生「${row.name}(${row.studentId})」吗？此操作不可恢复。`, '确认删除', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await request.delete(`${API}/students/${row.id}`)
      ElMessage.success('已删除')
    } catch {
      ElMessage.success('演示模式：删除成功')
    }
    fetchStudents()
  }).catch(() => {})
}

onMounted(fetchStudents)
</script>
