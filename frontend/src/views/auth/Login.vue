<template>
  <div class="min-h-screen bg-surface flex flex-col items-center justify-center relative overflow-hidden">
    <div class="absolute inset-0 bg-[radial-gradient(var(--color-outline-variant)_1px,transparent_1px)] [background-size:24px_24px] opacity-50"></div>
    
    <div class="z-10 w-full max-w-md bg-surface-container-lowest p-10 rounded-2xl border border-outline-variant/20 shadow-xl shadow-outline-variant/10">
      <div class="flex items-center gap-4 mb-10 justify-center">
        <div class="w-12 h-12 rounded-xl bg-primary text-on-primary-fixed flex items-center justify-center font-bold text-2xl tracking-tighter shadow-md">S</div>
        <div>
          <h1 class="text-2xl font-bold tracking-tighter text-primary leading-none">智慧学工</h1>
          <p class="text-[0.6875rem] uppercase tracking-wider text-outline mt-1 font-semibold">综合信息系统 V2.0</p>
        </div>
      </div>

      <div class="space-y-6">
        <div>
          <h2 class="text-xl font-bold text-on-surface mb-2 text-center">系统登录</h2>
          <p class="text-sm text-secondary text-center">请选择您的身份进入系统</p>
        </div>
        
        <div class="grid grid-cols-1 gap-4 mt-8">
          <button @click="login('student')" class="group relative flex items-center justify-between p-4 rounded-xl border border-outline-variant/30 bg-surface-container-lowest hover:bg-surface-container-low transition-all overflow-hidden">
            <div class="absolute inset-0 bg-gradient-to-r from-ai-primary/10 to-transparent translate-x-[-100%] group-hover:translate-x-0 transition-transform duration-300"></div>
            <div class="flex items-center gap-4 relative z-10">
              <div class="w-10 h-10 rounded-lg bg-surface-container-high flex items-center justify-center text-primary group-hover:bg-primary group-hover:text-on-primary-fixed transition-colors">
                <el-icon><User /></el-icon>
              </div>
              <div class="text-left">
                <h3 class="font-bold text-on-surface">我是学生</h3>
                <p class="text-xs text-secondary mt-0.5">访问个人主页与学业支持</p>
              </div>
            </div>
            <el-icon class="text-secondary group-hover:text-primary relative z-10"><ArrowRight /></el-icon>
          </button>

          <button @click="login('teacher')" class="group relative flex items-center justify-between p-4 rounded-xl border border-outline-variant/30 bg-surface-container-lowest hover:bg-surface-container-low transition-all overflow-hidden">
            <div class="absolute inset-0 bg-gradient-to-r from-tertiary-container/10 to-transparent translate-x-[-100%] group-hover:translate-x-0 transition-transform duration-300"></div>
            <div class="flex items-center gap-4 relative z-10">
              <div class="w-10 h-10 rounded-lg bg-surface-container-high flex items-center justify-center text-primary group-hover:bg-primary group-hover:text-on-primary-fixed transition-colors">
                <el-icon><Service /></el-icon>
              </div>
              <div class="text-left">
                <h3 class="font-bold text-on-surface">我是教师 / 辅导员</h3>
                <p class="text-xs text-secondary mt-0.5">进入管理大屏与全局监控</p>
              </div>
            </div>
            <el-icon class="text-secondary group-hover:text-primary relative z-10"><ArrowRight /></el-icon>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { User, Service, ArrowRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const login = (role) => {
  // 模拟登录态存储
  localStorage.setItem('userRole', role)
  if (role === 'student') {
    router.push('/student')
  } else {
    router.push('/teacher')
  }
}
</script>
