<template>
  <div class="antialiased min-h-screen flex text-on-surface bg-surface">
    <!-- SideNavBar (Student Version) -->
    <nav class="hidden md:flex flex-col fixed left-0 top-0 h-full py-8 px-4 w-64 bg-surface border-r border-outline-variant/15 z-50">
      <div class="mb-12 px-4 flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-primary text-on-primary-fixed flex items-center justify-center font-bold text-xl tracking-tighter shadow-md">S</div>
        <div>
          <h1 class="text-xl font-bold tracking-tighter text-primary leading-none">学生空间</h1>
          <p class="text-[0.6875rem] uppercase tracking-wider text-outline mt-1 font-semibold">Student Portal</p>
        </div>
      </div>
      <ul class="flex flex-col gap-2 font-medium tracking-tight text-[0.875rem]">
        <li>
          <router-link to="/student" class="flex items-center gap-3 px-4 py-3 rounded-xl text-primary font-semibold border-r-2 border-primary bg-surface-container-highest transition-colors scale-98">
            <el-icon :size="20"><UserFilled /></el-icon>
            个人主页
          </router-link>
        </li>
        <li>
          <a class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-surface-container-low transition-colors scale-98" href="#">
            <el-icon :size="20"><Reading /></el-icon>
            学业与第二课堂
          </a>
        </li>
        <li>
          <a class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-surface-container-low transition-colors scale-98" href="#">
            <el-icon :size="20"><Document /></el-icon>
            我的申请 (奖助/请假)
          </a>
        </li>
        <li>
          <a class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-surface-container-low transition-colors scale-98 relative group" href="#">
            <el-icon :size="20" class="text-ai-primary"><Guide /></el-icon>
            <span class="text-ai-primary font-semibold">职业规划 (AI 分析)</span>
          </a>
        </li>
        <li>
          <a class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-surface-container-low transition-colors scale-98" href="#">
            <el-icon :size="20"><School /></el-icon>
            校园生活与安全
          </a>
        </li>
      </ul>

      <div class="mt-auto px-4">
        <div class="flex items-center gap-3 p-3 bg-surface-container-low rounded-xl border border-outline-variant/15">
          <img src="https://i.pravatar.cc/150?u=student1" alt="avatar" class="w-10 h-10 rounded-lg border border-outline-variant/30">
          <div>
            <p class="text-sm font-bold text-on-surface">张小明</p>
            <p class="text-[0.6875rem] text-secondary">计算机科学与技术</p>
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
            <input class="bg-transparent border-none outline-none text-sm w-full placeholder-outline text-on-surface p-0 focus:ring-0" placeholder="搜索课程 / 活动 / 政策..." type="text"/>
          </div>
        </div>
        <div class="flex items-center gap-5">
          <button class="text-secondary hover:text-primary transition-opacity relative">
            <el-icon :size="20"><Bell /></el-icon>
            <span class="absolute -top-1 -right-1 w-2.5 h-2.5 bg-error rounded-full border-2 border-surface"></span>
          </button>
          <button @click="$router.push('/login')" class="text-secondary hover:text-primary transition-opacity flex items-center gap-1 text-xs font-semibold uppercase">
            <el-icon><SwitchButton /></el-icon> 退出
          </button>
        </div>
      </header>

      <!-- Canvas -->
      <main class="flex-1 p-6 lg:p-10 max-w-[1400px] w-full mx-auto">
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
import { UserFilled, Reading, Document, Guide, School, Search, Bell, SwitchButton } from '@element-plus/icons-vue'
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
