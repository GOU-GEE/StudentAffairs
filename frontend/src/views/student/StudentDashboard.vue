<template>
  <div class="h-full flex flex-col">
    <!-- Page Header -->
    <div class="mb-8">
      <h2 class="text-[2.5rem] leading-none font-bold tracking-tight text-on-surface mb-2">上午好，张小明！</h2>
      <p class="text-[0.875rem] text-secondary font-medium">今天是 {{ currentDate }}，距离期末考试还有 42 天。</p>
    </div>

    <!-- Bento Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 auto-rows-min">
      
      <!-- 1. Profile Info (JSONB Data Simulation) -->
      <div class="md:col-span-8 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] flex flex-col md:flex-row gap-6">
        <div class="w-32 h-32 rounded-2xl overflow-hidden flex-shrink-0 bg-surface-variant border border-outline-variant/30 relative">
          <img src="https://i.pravatar.cc/150?u=student1" class="w-full h-full object-cover">
          <div class="absolute bottom-0 w-full bg-surface-container-lowest/80 backdrop-blur py-1 text-center text-xs font-bold text-on-surface">学号: 202301042</div>
        </div>
        <div class="flex-1">
          <div class="flex justify-between items-start mb-4">
            <div>
              <h3 class="text-xl font-bold text-on-surface">张小明</h3>
              <p class="text-sm text-secondary">计算机学院 | 软件工程 | 软工2班</p>
            </div>
            <button @click="openProfile" class="bg-surface-container-high text-on-surface text-xs font-semibold px-3 py-1.5 rounded-md hover:bg-outline-variant/30 transition-colors">
              查看个人详情信息
            </button>
          </div>
          
          <!-- Simulated JSONB Unstructured Data -->
          <div class="grid grid-cols-2 gap-y-3 gap-x-6 text-sm">
            <div class="flex flex-col">
              <span class="text-xs text-outline font-bold uppercase tracking-wider mb-0.5">宿舍地址</span>
              <span class="text-on-surface font-medium">兰苑 3 栋 402</span>
            </div>
            <div class="flex flex-col">
              <span class="text-xs text-outline font-bold uppercase tracking-wider mb-0.5">联系电话</span>
              <span class="text-on-surface font-medium">138-xxxx-8888</span>
            </div>
            <div class="flex flex-col">
              <span class="text-xs text-outline font-bold uppercase tracking-wider mb-0.5">紧急联系人 (父母)</span>
              <span class="text-on-surface font-medium">张大明 (139-xxxx-9999)</span>
            </div>
            <div class="flex flex-col">
              <span class="text-xs text-outline font-bold uppercase tracking-wider mb-0.5">个人爱好 (扩展字段)</span>
              <span class="text-on-surface font-medium flex gap-1">
                <span class="bg-surface-container-high px-2 py-0.5 rounded text-xs">篮球</span>
                <span class="bg-surface-container-high px-2 py-0.5 rounded text-xs">算法竞赛</span>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 2. Academic Hub Widget -->
      <div class="md:col-span-4 bg-tertiary-container text-on-tertiary-container rounded-xl p-6 relative overflow-hidden shadow-[0_4px_12px_rgba(49,107,243,0.2)]">
        <div class="absolute -right-6 -top-6 opacity-20">
          <el-icon :size="120"><DataLine /></el-icon>
        </div>
        <div class="relative z-10 h-full flex flex-col justify-between">
          <div>
            <div class="flex items-center gap-2 mb-2">
              <el-icon><DataLine /></el-icon>
              <h3 class="font-bold text-sm uppercase tracking-wider">当前平均绩点 (GPA)</h3>
            </div>
            <p class="text-[3rem] font-bold leading-none">3.85</p>
            <p class="text-sm mt-1 text-on-tertiary-container/80">专业排名: 12 / 150 <span class="text-green-300 font-bold ml-1">↑ 2名</span></p>
          </div>
          <button class="bg-on-tertiary-container text-tertiary-container font-bold text-sm px-4 py-2 rounded-lg mt-6 w-max hover:bg-white transition-colors">
            查看完整成绩单
          </button>
        </div>
      </div>

      <!-- 3. Active Applications & Notifications -->
      <div class="md:col-span-6 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_2px_8px_rgba(25,28,30,0.02)] h-[320px] flex flex-col">
        <h3 class="text-lg font-semibold text-on-surface tracking-tight mb-4">我的申请状态</h3>
        <div class="flex-1 overflow-y-auto space-y-3 custom-scrollbar pr-2">
          
          <div class="p-4 rounded-xl border border-outline-variant/30 bg-surface flex justify-between items-center group hover:border-primary transition-colors">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-orange-100 text-orange-600 flex items-center justify-center">
                <el-icon><Money /></el-icon>
              </div>
              <div>
                <h4 class="font-bold text-sm text-on-surface">国家励志奖学金申请</h4>
                <p class="text-xs text-secondary mt-0.5">提交时间: 2023-10-15</p>
              </div>
            </div>
            <div class="text-right">
              <span class="text-[0.6875rem] font-bold bg-orange-100 text-orange-600 px-2 py-1 rounded border border-orange-200">学院初审中</span>
            </div>
          </div>

          <div class="p-4 rounded-xl border border-outline-variant/30 bg-surface flex justify-between items-center group hover:border-primary transition-colors">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-green-100 text-green-600 flex items-center justify-center">
                <el-icon><School /></el-icon>
              </div>
              <div>
                <h4 class="font-bold text-sm text-on-surface">周末外出报备</h4>
                <p class="text-xs text-secondary mt-0.5">提交时间: 2023-10-20</p>
              </div>
            </div>
            <div class="text-right">
              <span class="text-[0.6875rem] font-bold bg-green-100 text-green-700 px-2 py-1 rounded border border-green-200">辅导员已批阅</span>
            </div>
          </div>

        </div>
      </div>

      <!-- 4. AI Career Planner Interface -->
      <div class="md:col-span-6 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_12px_40px_rgba(25,28,30,0.06)] h-[320px] flex flex-col relative overflow-hidden">
        <div class="absolute -right-10 -bottom-10 w-40 h-40 bg-ai-primary/10 rounded-full blur-[40px] pointer-events-none"></div>
        <div class="flex justify-between items-start mb-6 relative z-10">
          <div>
            <div class="flex items-center gap-2 mb-1">
              <el-icon class="text-ai-primary animate-pulse"><Guide /></el-icon>
              <h3 class="text-lg font-semibold text-on-surface tracking-tight">AI 职业规划分析</h3>
            </div>
            <p class="text-xs text-secondary">根据你的学习轨迹与兴趣，大模型为你推演未来。</p>
          </div>
          <button class="text-ai-primary font-semibold text-sm hover:underline">重新评测</button>
        </div>
        
        <div class="flex-1 flex flex-col justify-center relative z-10">
          <div class="bg-surface border border-outline-variant/30 rounded-xl p-4 mb-4">
            <p class="text-sm text-on-surface leading-relaxed">
              基于你优秀的 <span class="font-bold">数据结构</span> (95分) 和 <span class="font-bold">算法设计</span> 成绩，以及对“篮球”的爱好。AI 建议你可以重点关注：
            </p>
            <div class="mt-3 flex gap-2 flex-wrap">
              <span class="px-3 py-1 bg-ai-primary/10 text-ai-primary border border-ai-primary/20 rounded-full text-xs font-bold">体育科技数据分析师</span>
              <span class="px-3 py-1 bg-surface-container-high text-on-surface border border-outline-variant/30 rounded-full text-xs font-medium">大厂后端研发</span>
              <span class="px-3 py-1 bg-surface-container-high text-on-surface border border-outline-variant/30 rounded-full text-xs font-medium">考研 (人工智能方向)</span>
            </div>
          </div>
          
          <button class="w-full py-3 bg-ai-primary text-white rounded-xl font-semibold text-sm hover:bg-ai-primary-light transition-colors flex items-center justify-center gap-2 shadow-md shadow-ai-primary/20">
            <el-icon><ChatDotRound /></el-icon>
            与 AI 规划师深度对话
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { User, Document, Warning, Trophy, Download, UploadFilled, MagicStick, Loading, Position, DataLine, Money, School, Guide, ChatDotRound } from '@element-plus/icons-vue'
import { ref, inject } from 'vue'

const openProfile = inject('openProfile')

const date = new Date()
const currentDate = `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
</script>
