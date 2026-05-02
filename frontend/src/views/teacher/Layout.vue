<template>
  <div class="antialiased min-h-screen flex text-on-surface bg-surface">
    <!-- SideNavBar -->
    <nav class="hidden md:flex flex-col fixed left-0 top-0 h-full py-8 px-4 w-64 bg-surface border-r border-outline-variant/15 z-50">
      <div class="mb-12 px-4 flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-primary text-on-primary-fixed flex items-center justify-center font-bold text-xl tracking-tighter shadow-md">S</div>
        <div>
          <h1 class="text-xl font-bold tracking-tighter text-primary leading-none">智慧学工</h1>
          <p class="text-[0.6875rem] uppercase tracking-wider text-outline mt-1 font-semibold">综合信息系统 V2.0</p>
        </div>
      </div>
      <ul class="flex flex-col gap-2 font-medium tracking-tight text-[0.875rem]">
        <li>
          <router-link to="/" class="flex items-center gap-3 px-4 py-3 rounded-xl text-primary font-semibold border-r-2 border-primary bg-surface-container-highest transition-colors scale-98">
            <el-icon :size="20"><DataBoard /></el-icon>
            学生档案大屏
          </router-link>
        </li>
        <li>
          <a class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-surface-container-low transition-colors scale-98" href="#">
            <el-icon :size="20"><Money /></el-icon>
            奖助服务管理
          </a>
        </li>
        <li>
          <a class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-surface-container-low transition-colors scale-98" href="#">
            <el-icon :size="20"><TrendCharts /></el-icon>
            学业预警支持
          </a>
        </li>
        <li>
          <a class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-surface-container-low transition-colors scale-98 relative group" href="#">
            <el-icon :size="20" class="text-ai-primary"><Guide /></el-icon>
            <span class="text-ai-primary font-semibold">AI 职业规划</span>
            <span class="absolute right-3 flex h-2 w-2">
              <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-ai-primary opacity-75"></span>
              <span class="relative inline-flex rounded-full h-2 w-2 bg-ai-primary"></span>
            </span>
          </a>
        </li>
        <li>
          <a class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-surface-container-low transition-colors scale-98" href="#">
            <el-icon :size="20"><ChatLineRound /></el-icon>
            沟通互动
          </a>
        </li>
      </ul>

      <div class="mt-auto px-4">
        <div class="bg-surface-container-low p-4 rounded-xl border border-outline-variant/15 flex flex-col gap-2">
           <div class="text-[0.6875rem] font-bold text-secondary uppercase">系统状态</div>
           <div class="flex items-center gap-2">
             <div class="w-2 h-2 rounded-full bg-green-500"></div>
             <span class="text-xs text-secondary font-medium">后端服务正常</span>
           </div>
           <div class="flex items-center gap-2">
             <div class="w-2 h-2 rounded-full bg-ai-primary animate-pulse"></div>
             <span class="text-xs text-secondary font-medium">大模型 API 已连接</span>
           </div>
        </div>
      </div>
    </nav>

    <!-- Main Content Wrapper -->
    <div class="flex-1 ml-0 md:ml-64 flex flex-col min-h-screen relative">
      <!-- TopNavBar -->
      <header class="flex justify-between items-center px-8 sticky top-0 z-40 h-16 bg-surface/80 backdrop-blur-xl border-b border-outline-variant/15 font-sans text-sm tracking-tight w-full">
        <div class="flex items-center gap-4 flex-1">
          <div class="relative w-80 focus-within:ring-1 ring-primary transition-all rounded-md bg-surface-container-low hidden md:flex items-center px-3 py-1.5 border border-outline-variant/20">
            <el-icon class="text-outline mr-2"><Search /></el-icon>
            <input class="bg-transparent border-none outline-none text-sm w-full placeholder-outline text-on-surface p-0 focus:ring-0" placeholder="全局搜索学生 / 班级 / AI 指令..." type="text"/>
          </div>
        </div>
        <div class="flex items-center gap-5">
          <div class="hidden md:flex items-center gap-2 text-xs font-semibold tracking-wider text-outline uppercase border border-outline-variant/30 rounded px-2 py-1 bg-surface-container-lowest">
            Command + K
          </div>
          <button class="text-secondary hover:text-primary transition-opacity">
            <el-icon :size="20"><Bell /></el-icon>
          </button>
          <div class="w-8 h-8 rounded-full bg-surface-container-high overflow-hidden shadow-[0_4px_12px_rgba(25,28,30,0.06)] border border-outline-variant/30 flex items-center justify-center font-bold text-xs text-primary">
            李
          </div>
        </div>
      </header>

      <!-- Canvas -->
      <main class="flex-1 p-6 lg:p-10 max-w-[1600px] w-full mx-auto">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { DataBoard, Money, TrendCharts, Guide, ChatLineRound, Search, Bell } from '@element-plus/icons-vue'
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(5px);
}
</style>
