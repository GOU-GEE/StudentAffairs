<template>
  <div class="h-full flex flex-col space-y-6">
    <!-- Main Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 flex-1 min-h-0">
      
      <!-- Left Column: Main Content -->
      <div class="md:col-span-8 flex flex-col gap-6 overflow-y-auto no-scrollbar">
        
        <!-- AI Banner -->
        <div class="relative overflow-hidden rounded-2xl p-8 text-white shadow-lg flex items-stretch min-h-[260px]">
          <!-- Background Image -->
          <div class="absolute inset-0 w-full h-full">
            <img src="@/assets/career_bg.png" class="w-full h-full object-cover object-right" alt="AI Career Banner">
          </div>
          <!-- Gradient Overlay: Left blurry/solid to right transparent -->
          <div class="absolute inset-0 bg-gradient-to-r from-[#6366F1] via-[#6366F1]/85 to-transparent"></div>

          <div class="relative z-10 w-full flex flex-col justify-between">
            <div class="max-w-md">
              <h3 class="text-3xl font-bold mb-3 tracking-tight">AI 助力你的职业未来</h3>
              <p class="text-white/90 text-sm leading-relaxed mb-6">
                通过测评探索自我，获取个性化建议，精准匹配职业机会，为你的职业发展保驾护航。
              </p>
              <el-button color="white" round class="!text-indigo-600 font-bold px-6 shadow-md border-0 group" @click="showAssessmentDialog = true">
                开始生涯测评
                <el-icon class="ml-2 transition-transform group-hover:translate-x-1"><ArrowRight /></el-icon>
              </el-button>
            </div>

            <!-- Stats Bar -->
            <div class="flex items-center gap-x-12 p-4 bg-white/10 backdrop-blur-md rounded-xl border border-white/20 w-max mt-6">
              <div v-for="(stat, idx) in stats" :key="idx" class="flex flex-col items-start">
                <span class="text-[13px] text-white/80 mb-1 leading-none">{{ stat.label }}</span>
                <span class="text-[22px] font-bold leading-none">{{ stat.value }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Recommended Positions -->
        <div class="mb-6 flex-1 flex flex-col min-h-0">
          
          <!-- 3 Columns Layout Wrapped in a single box with title integrated -->
          <div class="bg-white rounded-2xl border border-outline-variant/20 shadow-sm flex flex-col flex-1 overflow-hidden">
            <!-- Integrated Title -->
            <div class="flex items-center justify-between px-6 py-4 border-b border-outline-variant/15">
              <h3 class="text-[17px] font-bold text-on-surface flex items-center gap-2">
                <div class="w-1.5 h-4 bg-primary rounded-full"></div> 根据成绩问卷寻得最佳匹配
              </h3>
              <el-link type="primary" :underline="false" class="text-sm flex items-center gap-1">换一批 <el-icon><Refresh /></el-icon></el-link>
            </div>

            <!-- The Grid -->
            <div class="flex flex-1">
              <div class="flex flex-col flex-1" v-for="(job, idx) in recommendedJobs" :key="idx">
                <div class="p-6 flex flex-col h-full hover:bg-surface-container-lowest transition-colors relative" :class="idx !== 0 ? 'border-l border-outline-variant/20' : ''">
                  <!-- Top: Job Direction -->
                  <div class="flex items-start justify-between mb-4">
                    <h4 class="font-bold text-on-surface text-[17px] tracking-tight">{{ job.title }}</h4>
                    <span class="text-[11px] bg-green-50 text-green-600 px-2 py-0.5 rounded font-bold border border-green-200 shadow-sm flex-shrink-0">匹配度 {{ job.match }}%</span>
                  </div>
                  
                  <!-- Reason for Recommendation -->
                  <div class="pt-2 flex-1 flex flex-col">
                    <div class="text-[12px] font-bold text-primary mb-2 flex items-center gap-1.5">
                      <el-icon><Aim /></el-icon> 推荐理由
                    </div>
                    <p class="text-[13px] text-secondary leading-relaxed text-justify">
                      {{ job.reason }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>

      </div>

      <!-- Right Column: Sidebar -->
      <div class="md:col-span-4 flex flex-col gap-6 overflow-y-auto no-scrollbar pb-6">

        <!-- Employment Counseling Appointment -->
        <div class="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl p-5 border border-blue-100/50 shadow-sm relative overflow-hidden group">
          <div class="absolute -right-6 -bottom-6 w-24 h-24 bg-blue-500/10 rounded-full blur-xl transition-transform group-hover:scale-150"></div>
          
          <div class="flex items-start justify-between mb-4 relative z-10">
            <div>
              <h3 class="font-bold text-on-surface text-lg mb-1">就业咨询预约</h3>
              <p class="text-xs text-secondary">一对一职业规划咨询，解决求职困惑</p>
            </div>
            <div class="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center text-blue-600 shadow-sm">
              <el-icon :size="20"><Calendar /></el-icon>
            </div>
          </div>
          
          <div class="relative z-10 flex items-center justify-between mt-6">
            <el-button type="primary" class="!rounded-lg shadow-sm shadow-blue-500/20">
              立即预约 <el-icon class="ml-1"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- Career Assessment Tools (Moved to sidebar) -->
        <div class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/20 shadow-sm">
          <div class="flex items-center justify-between mb-5">
            <h3 class="font-bold text-on-surface">生涯测评工具</h3>
            <el-link type="primary" :underline="false" class="text-[13px] font-semibold" @click="showAssessmentDialog = true">
              查看详细测评结果 <el-icon class="ml-1"><ArrowRight /></el-icon>
            </el-link>
          </div>
          
          <!-- 2x2 Grid -->
          <div class="grid grid-cols-2 gap-4">
            <div v-for="(tool, idx) in assessmentTools" :key="idx" 
                 class="border border-outline-variant/20 rounded-xl p-4 hover:shadow-md transition-all flex flex-col bg-white">
              <h4 class="font-bold text-sm text-on-surface mb-2">{{ tool.title }}</h4>
              <p class="text-[11px] text-secondary mb-4 line-clamp-2 flex-1">{{ tool.desc }}</p>
              <div class="flex items-center justify-between mt-auto">
                <span class="text-[10px] text-secondary">约{{ tool.time }}分钟</span>
                <el-button plain :type="tool.btnType" size="small" class="!rounded-lg px-3 text-xs h-7">测评</el-button>
              </div>
            </div>
          </div>
        </div>

      </div>

    </div>

    <!-- 局部暗色遮罩层 -->
    <div v-if="showAssessmentDialog" class="fixed top-[56px] left-[256px] right-0 bottom-0 bg-black/40 z-[1000] pointer-events-none transition-opacity duration-300"></div>

    <!-- 详细测评结果弹窗 -->
    <el-dialog v-model="showAssessmentDialog" title="" modal-class="custom-calendar-overlay" class="custom-calendar-dialog" :show-close="true" append-to-body lock-scroll destroy-on-close>
      <div class="flex flex-col h-full bg-surface-container-lowest overflow-hidden p-8">
        <!-- 头部 -->
        <div class="flex items-center justify-between mb-8">
          <h3 class="text-2xl font-bold text-on-surface flex items-center gap-3">
            <el-icon class="text-primary"><Document /></el-icon>
            生涯详细测评结果
          </h3>
        </div>
        
        <!-- 十字格网格 -->
        <div class="flex-1 grid grid-cols-2 grid-rows-2 min-h-0 bg-white rounded-3xl border border-outline-variant/30 shadow-sm overflow-hidden">
          <div v-for="(tool, idx) in assessmentTools" :key="idx" class="p-8 flex flex-col relative transition-all hover:bg-surface-container-lowest/50"
               :class="{
                 'border-b border-outline-variant/30': idx < 2,
                 'border-r border-outline-variant/30': idx % 2 === 0
               }">
            <!-- 小标题 和 重新测试按钮 -->
            <div class="mb-4 pb-3 flex items-center justify-between">
              <h4 class="font-bold text-lg text-on-surface">{{ tool.title }}</h4>
              <el-button size="small" class="!bg-surface-container-low !border-0 !text-secondary hover:!bg-surface-container hover:!text-on-surface transition-colors">重新测试</el-button>
            </div>
            
            <!-- 测试记录 (无蓝色块包裹) -->
            <div class="flex-1 overflow-y-auto custom-scrollbar pr-2">
              <div class="relative mt-1">
                <div class="text-xl font-bold text-primary mb-3">{{ assessmentHistory[idx][0].result }}</div>
                <div class="text-[14px] text-secondary leading-relaxed" v-if="assessmentHistory[idx][0].detail">{{ assessmentHistory[idx][0].detail }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  ArrowRight, ArrowDown, Monitor, Key, Ticket, Document, Star, StarFilled,
  OfficeBuilding, Location, Refresh, Calendar, DataLine, Help, Magnet, Clock, Aim
} from '@element-plus/icons-vue'

const showAssessmentDialog = ref(false)

const stats = ref([
  { label: '测评次数', value: '3次' },
  { label: '预约咨询', value: '1次' },
])

const assessmentTools = ref([
  { title: '职业兴趣测评', desc: '探索你的兴趣类型，发掘适合的职业方向', time: '20', icon: 'Star', iconBgClass: 'bg-blue-50', iconTextClass: 'text-blue-500', btnType: 'primary' },
  { title: '性格特质测评', desc: '了解你的性格优势，适合的工作环境', time: '15', icon: 'Magnet', iconBgClass: 'bg-green-50', iconTextClass: 'text-green-500', btnType: 'success' },
  { title: '能力倾向测评', desc: '评估你的能力水平，找到优势领域', time: '25', icon: 'DataLine', iconBgClass: 'bg-purple-50', iconTextClass: 'text-purple-500', btnType: 'warning' },
  { title: '职业价值观测评', desc: '明确你的职业价值观，帮助职业决策', time: '15', icon: 'Help', iconBgClass: 'bg-orange-50', iconTextClass: 'text-orange-500', btnType: 'danger' },
])

const recommendedJobs = ref([
  { 
    title: '算法研究方向', 
    match: 92,
    reason: '结合您的性格特质测试（INTJ），该方向高度契合您出色的逻辑分析和独立思考能力，算法研究需要深入探索未知领域。'
  },
  { 
    title: '软件工程与系统架构', 
    match: 89,
    reason: '您的能力倾向测评在系统架构方面得分较高（95分），在复杂系统中能发挥出极强的工程思维和构建能力。'
  },
  { 
    title: '产品设计与规划', 
    match: 86,
    reason: '您的职业价值观倾向于成就感导向，产品方向能给您提供广阔的发展平台和用户价值验证的巨大成就感。'
  }
])

const assessmentHistory = ref([
  // 职业兴趣测评
  [
    { date: '2026-05-10 14:30', result: '研究型 (I) / 艺术型 (A)', detail: '倾向于从事需要深度思考和创新的工作，适合科研、设计等领域。' },
    { date: '2025-10-15 09:15', result: '研究型 (I) / 实际型 (R)', detail: '具有较强的逻辑思维和动手能力，喜欢探索事物运作原理。' }
  ],
  // 性格特质测评
  [
    { date: '2026-05-12 10:00', result: 'INTJ (建筑师)', detail: '富有想象力和战略性，对一切事物皆有计划，适合高阶管理或架构师角色。' },
    { date: '2025-11-20 16:40', result: 'INTP (逻辑学家)', detail: '具有创造力，对知识有着永不满足的渴望，适合独立研究和开发。' }
  ],
  // 能力倾向测评
  [
    { date: '2026-05-14 08:50', result: '逻辑推理能力突出 (95分)', detail: '在复杂系统分析、算法设计方面具有显著优势。' },
    { date: '2025-09-05 11:20', result: '逻辑推理 (88分) / 空间想象 (82分)', detail: '具备良好的抽象思维能力和空间构建能力。' }
  ],
  // 职业价值观测评
  [
    { date: '2026-05-15 09:30', result: '成就感导向 / 智力刺激', detail: '期望在工作中解决复杂问题并获得成就认可，注重自我价值实现。' },
    { date: '2025-12-01 15:10', result: '成就感导向 / 独立性', detail: '喜欢自主安排工作，追求专业领域的突破，不喜被过度微观管理。' }
  ]
])
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--el-border-color-lighter);
  border-radius: 4px;
}
.custom-scrollbar:hover::-webkit-scrollbar-thumb {
  background: var(--el-border-color);
}
</style>

<style>
.custom-calendar-overlay {
  background-color: transparent !important; /* 隐藏原生遮罩背景 */
}
.custom-calendar-dialog {
  position: fixed !important;
  top: 96px !important;
  bottom: 40px !important;
  left: 288px !important;
  right: 32px !important;
  width: auto !important;
  height: auto !important;
  margin: 0 !important;
  padding: 0 !important;
  display: flex;
  flex-direction: column;
  border-radius: 32px !important; /* 圆角处理 */
  overflow: hidden !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;
  overscroll-behavior: contain;
}
.custom-calendar-dialog .el-dialog__headerbtn {
  top: 22px;
  right: 25px;
  z-index: 100;
}
.custom-calendar-dialog .el-dialog__header {
  padding: 0 !important;
  margin: 0 !important;
  height: 0;
}
.custom-calendar-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  padding: 0 !important;
}
</style>
