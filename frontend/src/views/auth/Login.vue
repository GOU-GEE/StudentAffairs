<template>
  <div class="min-h-screen flex flex-col items-center justify-center relative overflow-hidden">
    <div class="z-10 w-full max-w-md bg-surface-container-lowest p-10 rounded-2xl border border-outline-variant/20 shadow-xl shadow-outline-variant/10">
      <div class="flex flex-col items-center justify-center mb-10">
        <div class="flex items-center gap-4 justify-center">
          <img src="/logosvg.png" alt="Logo" class="w-14 h-14 object-contain">
          <div class="flex flex-col items-center">
            <span class="text-[1.1rem] font-bold tracking-[0.15em] text-on-surface">西华师范大学</span>
            <span class="text-[1.3rem] font-black tracking-[0.1em] text-primary mt-0.5">智慧学工系统</span>
          </div>
        </div>
        <span class="text-[0.55rem] font-medium tracking-[0.05em] text-outline mt-2 text-center">CHINA WEST NORMAL UNIVERSITY STUDENT AFFAIRS</span>
      </div>

      <div class="space-y-6">
        <div>
          <h2 class="text-xl font-bold text-on-surface mb-2 text-center">系统登录</h2>
          <p class="text-sm text-secondary text-center">请输入账号密码进入系统</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-4 mt-6">
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">账号</label>
            <el-input
              v-model="username"
              placeholder="请输入账号"
              :prefix-icon="User"
              size="large"
              clearable
            />
          </div>
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">密码</label>
            <el-input
              v-model="password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </div>
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3 bg-primary text-on-primary-fixed rounded-xl text-sm font-bold hover:bg-primary-fixed transition-colors shadow-md disabled:opacity-50 disabled:cursor-not-allowed mt-2"
          >
            <span v-if="loading" class="flex items-center justify-center gap-2">
              <el-icon class="animate-spin"><Loading /></el-icon>登录中...
            </span>
            <span v-else>登 录</span>
          </button>
        </form>

        <p class="text-xs text-outline text-center mt-4">系统将根据账号自动识别角色并跳转至对应工作台</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { User, Lock, Loading } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()

const username = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!username.value.trim() || !password.value) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value.trim(),
      password: password.value,
    })

    if (res.data.code === 200) {
      const { role, name, token, userId } = res.data.data
      sessionStorage.setItem('userRole', role)
      sessionStorage.setItem('userName', name)
      sessionStorage.setItem('token', token)
      if (userId) sessionStorage.setItem('userId', userId)
      ElMessage.success(`欢迎回来，${name}`)

      if (role === 'student') {
        router.push('/student')
      } else if (role === 'admin') {
        router.push('/admin')
      } else if (role === 'financial') {
        router.push('/financial')
      } else if (role === 'youth') {
        router.push('/youth')
      } else if (role === 'academic') {
        router.push('/academic')
      } else {
        window.location.href = '/teacher'
      }
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch {
    ElMessage.error('无法连接服务器，请检查后端是否启动')
  } finally {
    loading.value = false
  }
}
</script>
