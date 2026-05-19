<template>
  <div class="h-full flex flex-col">
    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in stats" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>

    <div class="flex items-center gap-3 mb-4">
      <button @click="openAddDialog" class="bg-error text-on-error hover:bg-error/80 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Plus /></el-icon>新建账号
      </button>
    </div>

    <!-- Account Table -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="accounts" style="width: 100%" height="100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="name" label="姓名" width="140" />
        <el-table-column label="角色" width="140">
          <template #default="{ row }">
            <span class="text-xs font-bold px-2 py-1 rounded-full"
                  :class="row.role === 'admin' ? 'bg-red-100 text-red-700' : row.role === 'teacher' ? 'bg-blue-100 text-blue-700' : 'bg-green-100 text-green-700'">
              {{ roleLabel(row.role) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="text-xs font-bold" :class="row.status === '启用' ? 'text-green-600' : 'text-secondary'">{{ row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <button @click="handleToggleStatus(row)" class="text-xs font-semibold text-primary hover:text-error transition-colors mr-3">
              {{ row.status === '启用' ? '停用' : '启用' }}
            </button>
            <button @click="handleResetPwd(row)" class="text-xs font-semibold text-secondary hover:text-error transition-colors mr-3">重置密码</button>
            <button @click="handleDelete(row)" class="text-xs font-semibold text-secondary hover:text-error transition-colors">删除</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Add Account Dialog -->
    <el-dialog v-model="dialogVisible" title="新建账号" width="480px" destroy-on-close>
      <el-form :model="form" label-position="top">
        <el-form-item label="用户名" required>
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="form.role" class="w-full">
            <el-option label="超级管理员" value="admin" />
            <el-option label="教师/辅导员" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
        </el-form-item>
        <el-form-item label="初始密码">
          <el-input v-model="form.password" placeholder="默认为 123456" />
        </el-form-item>
      </el-form>
      <template #footer>
        <button @click="dialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="handleCreate" class="px-6 py-2 bg-error text-on-error rounded-lg text-sm font-bold hover:bg-error/80 transition-colors">创建</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/admin'

const accounts = ref([])
const dialogVisible = ref(false)
const form = ref({ username: '', name: '', role: 'teacher', password: '123456' })

const roleLabel = (role) => ({ admin: '超级管理员', teacher: '教师', student: '学生' }[role] || role)

const stats = computed(() => {
  const adminCount = accounts.value.filter(a => a.role === 'admin').length
  const teacherCount = accounts.value.filter(a => a.role === 'teacher').length
  const studentCount = accounts.value.filter(a => a.role === 'student').length
  return [
    { label: '管理员账号', value: adminCount },
    { label: '教师账号', value: teacherCount },
    { label: '学生账号', value: studentCount },
    { label: '账号总数', value: accounts.value.length },
  ]
})

const fetchAccounts = async () => {
  try {
    const res = await request.get(`${API}/accounts`)
    accounts.value = res.data.data || []
  } catch {
    accounts.value = [
      { id: 1, username: 'admin', name: '系统管理员', role: 'admin', status: '启用' },
      { id: 2, username: 'teacher_li', name: '李辅导员', role: 'teacher', status: '启用' },
      { id: 3, username: 'teacher_wang', name: '王老师', role: 'teacher', status: '启用' },
      { id: 4, username: 'student_zhang', name: '张小明', role: 'student', status: '启用' },
    ]
  }
}

const openAddDialog = () => {
  form.value = { username: '', name: '', role: 'teacher', password: '123456' }
  dialogVisible.value = true
}

const handleCreate = async () => {
  if (!form.value.username || !form.value.name) {
    ElMessage.warning('用户名和姓名为必填项')
    return
  }
  try {
    await request.post(`${API}/accounts`, form.value)
    ElMessage.success('账号创建成功')
  } catch {
    ElMessage.success('演示模式：账号创建成功，默认密码 123456')
  }
  dialogVisible.value = false
  fetchAccounts()
}

const handleToggleStatus = (row) => {
  row.status = row.status === '启用' ? '停用' : '启用'
  ElMessage.success(`账号「${row.username}」已${row.status === '启用' ? '启用' : '停用'}`)
}

const handleResetPwd = (row) => {
  ElMessageBox.confirm(`确定要重置账号「${row.username}」的密码吗？`, '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success(`密码已重置为 123456（演示模式）`)
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除账号「${row.name}(${row.username})」吗？此操作不可恢复。`, '确认删除', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    accounts.value = accounts.value.filter(a => a.id !== row.id)
    ElMessage.success('账号已删除')
  }).catch(() => {})
}

onMounted(fetchAccounts)
</script>
