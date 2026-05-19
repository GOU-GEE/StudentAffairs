<template>
  <div class="h-full flex flex-col gap-4 min-h-0">
    <!-- 顶部一行：总数 + 搜索栏 + 上传按钮 + 提示 -->
    <div class="flex items-center gap-3 flex-shrink-0">
      <h3 class="text-sm font-bold text-on-surface flex-shrink-0">共 <span class="text-xl font-black text-error">{{ users.length }}</span> 人</h3>
      <div class="relative w-56">
        <span class="absolute left-3 top-1/2 -translate-y-1/2 text-outline z-10 flex items-center justify-center"><el-icon :size="14"><Search /></el-icon></span>
        <input v-model="searchQuery" :placeholder="`搜索${idLabel}、姓名...`"
               class="w-full pl-9 pr-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-error/50 focus:ring-2 focus:ring-error/20 transition-all" />
      </div>
      <button @click="handleUpload"
        class="border border-red-400 text-red-500 bg-gray-50 hover:bg-red-50 transition-colors rounded-md px-3 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 flex-shrink-0">
        <el-icon :size="14"><Upload /></el-icon>上传Excel导入
      </button>
      <span class="text-xs text-outline flex-shrink-0">仅包含{{ idLabel === '教职工号' ? '姓名和教职工号' : '姓名和学号' }}字段</span>
    </div>

    <!-- 用户列表 -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1 flex flex-col min-h-0">
      <!-- 表头 -->
      <div class="flex items-center px-8 py-3 border-b border-outline-variant/15 bg-surface-container-lowest/50 flex-shrink-0 gap-8">
        <span class="flex-1 text-xs font-bold text-secondary uppercase tracking-wider">{{ idLabel }}</span>
        <span class="flex-1 text-xs font-bold text-secondary uppercase tracking-wider">姓名</span>
        <span class="flex-1 text-xs font-bold text-secondary uppercase tracking-wider">密码</span>
        <span class="w-40 text-center text-xs font-bold text-secondary uppercase tracking-wider flex-shrink-0">操作</span>
      </div>

      <!-- 列表 -->
      <div class="flex-1 overflow-y-auto custom-scrollbar">
        <div v-for="user in filteredUsers" :key="user.id"
          class="flex items-center px-8 py-3.5 border-b border-outline-variant/10 hover:bg-surface-container-lowest/50 transition-colors gap-8">
          <span class="flex-1 text-sm text-on-surface truncate">{{ user.userId }}</span>
          <span class="flex-1 text-sm font-semibold text-on-surface truncate">{{ user.name }}</span>
          <span class="flex-1 text-sm text-secondary truncate flex items-center gap-2">
            {{ visiblePasswords.has(user.id) ? user.password : user.maskedPassword }}
            <button @click="togglePassword(user.id)" class="text-outline hover:text-on-surface transition-colors flex-shrink-0">
              <el-icon :size="14"><component :is="visiblePasswords.has(user.id) ? View : Hide" /></el-icon>
            </button>
          </span>
          <span class="w-40 flex justify-center gap-3 flex-shrink-0">
            <button @click="resetPassword(user)" class="text-xs font-semibold text-error hover:text-error/80 transition-colors">重置密码</button>
            <button @click="deleteUser(user)" class="text-xs font-semibold text-gray-400 hover:text-red-500 transition-colors">删除</button>
          </span>
        </div>

        <div v-if="filteredUsers.length === 0" class="py-16 text-center text-secondary text-sm">
          暂无数据
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Search, View, Hide } from '@element-plus/icons-vue'

const route = useRoute()
const roleKey = route.meta.roleKey
const idLabel = route.meta.idLabel || '账号'

// Mock 数据
const MOCK_DATA = {
  admin: [
    { id: 1, userId: 'admin', name: '系统管理员', password: 'admin123', maskedPassword: '******' },
    { id: 2, userId: 'a002', name: '李管理', password: '123456', maskedPassword: '******' },
  ],
  teacher: [
    { id: 1, userId: 'T001', name: '王辅导员', password: '123456', maskedPassword: '******' },
    { id: 2, userId: 'T002', name: '李辅导员', password: '123456', maskedPassword: '******' },
    { id: 3, userId: 'T003', name: '张辅导员', password: '123456', maskedPassword: '******' },
    { id: 4, userId: 'T004', name: '赵辅导员', password: '123456', maskedPassword: '******' },
  ],
  financial: [
    { id: 1, userId: 'f001', name: '资助中心管理员', password: '123456', maskedPassword: '******' },
    { id: 2, userId: 'f002', name: '陈老师', password: '123456', maskedPassword: '******' },
  ],
  youth: [
    { id: 1, userId: 'y001', name: '第二课堂/团委', password: '123456', maskedPassword: '******' },
    { id: 2, userId: 'y002', name: '刘团委', password: '123456', maskedPassword: '******' },
  ],
  academic: [
    { id: 1, userId: 'a001', name: '教务管理', password: '123456', maskedPassword: '******' },
    { id: 2, userId: 'a002', name: '周教务', password: '123456', maskedPassword: '******' },
  ],
  student: [
    { id: 1, userId: '202301042', name: '张小明', password: '123456', maskedPassword: '******' },
    { id: 2, userId: '202301015', name: '李小丽', password: '123456', maskedPassword: '******' },
    { id: 3, userId: '202301078', name: '王五', password: '123456', maskedPassword: '******' },
    { id: 4, userId: '202301056', name: '赵六', password: '123456', maskedPassword: '******' },
    { id: 5, userId: '202301032', name: '周燕', password: '123456', maskedPassword: '******' },
    { id: 6, userId: '202301089', name: '孙八', password: '123456', maskedPassword: '******' },
  ],
}

const users = ref([...MOCK_DATA[roleKey] || []])
const searchQuery = ref('')
const visiblePasswords = ref(new Set())

const togglePassword = (userId) => {
  const s = new Set(visiblePasswords.value)
  if (s.has(userId)) s.delete(userId)
  else s.add(userId)
  visiblePasswords.value = s
}

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  const q = searchQuery.value.toLowerCase()
  return users.value.filter(u =>
    u.userId.toLowerCase().includes(q) ||
    u.name.toLowerCase().includes(q)
  )
})

const handleUpload = () => {
  const hintField = idLabel === '教职工号' ? '姓名和教职工号' : '姓名和学号'
  ElMessage.info(`请上传Excel文件，必须包含字段：${hintField}。格式：第一列${idLabel}，第二列姓名，第三列密码`)
}

const resetPassword = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定重置「${user.name}」（${user.userId}）的密码吗？密码将恢复为默认密码 123456。`,
      '确认重置密码',
      { confirmButtonText: '确认重置', cancelButtonText: '取消', type: 'warning' }
    )
    user.password = '123456'
    user.maskedPassword = '******'
    ElMessage.success(`已重置 ${user.name} 的密码为 123456`)
  } catch (e) {}
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定删除「${user.name}」（${user.userId}）吗？此操作不可恢复。`,
      '确认删除',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    const idx = users.value.findIndex(u => u.id === user.id)
    if (idx >= 0) users.value.splice(idx, 1)
    ElMessage.success('已删除')
  } catch (e) {}
}
</script>
