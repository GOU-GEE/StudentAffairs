<template>
  <div class="h-full flex flex-col">
    <!-- Bento Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 auto-rows-min">
      
      <!-- 1. Stats Overview -->
      <div class="md:col-span-12 grid grid-cols-1 md:grid-cols-4 gap-6">
        <div v-for="(stat, idx) in stats" :key="idx" class="bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_2px_8px_rgba(25,28,30,0.02)] hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="flex justify-between items-start mb-2">
            <span class="text-[0.875rem] font-semibold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
            <span class="text-xs px-2 py-0.5 rounded-full font-bold" :class="stat.trend > 0 ? 'text-green-600 bg-green-100' : 'text-error bg-error-container'">
              {{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%
            </span>
          </div>
          <h3 class="text-[2.5rem] font-bold text-on-surface mt-2">{{ stat.value }}</h3>
          
          <div class="absolute -right-4 -bottom-4 opacity-10 group-hover:scale-110 group-hover:opacity-20 transition-all duration-300">
             <el-icon :size="80" :color="stat.iconColor"><component :is="stat.icon" /></el-icon>
          </div>
        </div>
      </div>

      <div class="md:col-span-12 flex items-center gap-3 mb-4">
        <button @click="exportData" class="bg-surface-container-high text-on-surface hover:bg-outline-variant/30 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 border border-outline-variant/20 shadow-sm">
          <el-icon :size="14"><Download /></el-icon>导出报表
        </button>
        <button class="bg-primary text-on-primary-fixed hover:bg-primary-fixed transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
          <el-icon :size="14"><UploadFilled /></el-icon>导入数据
        </button>
      </div>

      <!-- 2. Focus Students (重点关注) -->
      <div class="md:col-span-8 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] flex flex-col h-[450px]">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-[1.375rem] font-semibold text-on-surface tracking-tight">近期关注学生</h3>
          <button class="text-[0.875rem] font-semibold text-secondary hover:text-primary transition-colors">查看全部</button>
        </div>
        
        <div class="flex-1 overflow-y-auto pr-2 space-y-3 custom-scrollbar">
          <div v-for="i in 5" :key="i" class="bg-surface rounded-xl p-4 cursor-pointer hover:bg-surface-container transition-colors border border-outline-variant/15 flex gap-4 items-center">
            <div class="relative">
              <div class="w-12 h-12 rounded-full overflow-hidden bg-surface-variant">
                <img :src="`https://i.pravatar.cc/150?u=${i}`" class="w-full h-full object-cover">
              </div>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <h4 class="font-semibold text-[0.875rem] text-on-surface">张三 {{ i }}</h4>
                <span class="text-[0.6875rem] font-bold uppercase tracking-wider text-secondary px-2 py-0.5 rounded bg-surface-container-high border border-outline-variant/30">软工2班</span>
              </div>
              <p class="text-[0.875rem] text-secondary truncate">学号: 2023000{{ i }} | 近期缺课次数异常</p>
            </div>
            <div>
              <span v-if="i === 1" class="text-[0.6875rem] font-bold px-2 py-1 rounded bg-error-container text-error uppercase border border-error/20">学业预警</span>
              <span v-else-if="i === 2" class="text-[0.6875rem] font-bold px-2 py-1 rounded bg-orange-100 text-orange-600 uppercase border border-orange-200">违纪记录</span>
              <span v-else class="text-[0.6875rem] font-bold px-2 py-1 rounded bg-blue-100 text-tertiary-container uppercase border border-blue-200">困难审核</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 3. AI Assistant Panel (AI 辅导员分身) -->
      <div class="md:col-span-4 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_12px_40px_rgba(25,28,30,0.06)] flex flex-col h-[450px] relative">
        <div class="absolute top-0 right-0 w-32 h-32 bg-ai-primary/10 rounded-full blur-[40px] pointer-events-none"></div>
        <div class="flex items-center gap-2 mb-6 relative z-10">
          <div class="w-8 h-8 rounded-lg bg-ai-primary text-white flex items-center justify-center shadow-lg shadow-ai-primary/30">
            <el-icon class="animate-pulse"><MagicStick /></el-icon>
          </div>
          <h3 class="text-[1.375rem] font-semibold text-on-surface tracking-tight">AI 辅导员分身</h3>
        </div>
        
        <div class="flex-1 overflow-y-auto space-y-4 pr-2 custom-scrollbar text-[0.875rem] relative z-10" ref="chatBox">
          <div v-for="(msg, idx) in chatMessages" :key="idx" 
               :class="['rounded-xl p-3 inline-block max-w-[85%] whitespace-pre-wrap leading-relaxed', 
                        msg.isUser ? 'bg-primary text-on-primary-fixed rounded-tr-none self-end ml-auto border border-primary' : 'bg-surface-container rounded-tl-none border border-outline-variant/20']">
            {{ msg.text }}
          </div>
          <div v-if="isTyping" class="bg-surface-container rounded-xl p-3 rounded-tl-none border border-outline-variant/20 inline-flex items-center gap-2">
            <el-icon class="animate-spin text-ai-primary"><Loading /></el-icon>
            <span class="text-secondary text-[0.75rem]">正在调用大模型生成...</span>
          </div>
        </div>

        <div class="mt-4 relative z-10">
          <input type="text" v-model="userInput" @keyup.enter="sendMessage" placeholder="输入指令 (如: 分析本班成绩分布)..." 
                 class="w-full bg-surface border border-outline-variant/30 rounded-xl px-4 py-3 pr-12 text-[0.875rem] focus:outline-none focus:ring-1 focus:ring-ai-primary focus:border-ai-primary transition-all text-on-surface placeholder-outline shadow-inner">
          <button @click="sendMessage" :disabled="isTyping || !userInput.trim()" 
                  class="absolute right-1.5 top-1.5 p-2 rounded-lg bg-ai-primary text-white hover:bg-ai-primary-light transition-colors disabled:opacity-50 shadow-md shadow-ai-primary/20">
            <el-icon><Position /></el-icon>
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { User, Document, Warning, Trophy, Download, UploadFilled, MagicStick, Loading, Position } from '@element-plus/icons-vue'
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { exportToCSV } from '@/utils/export'

const stats = ref([
  { label: 'Total Students', value: '2,405', icon: User, iconColor: '#316bf3', trend: 2.5 },
  { label: 'Weekly Incidents', value: '12', icon: Warning, iconColor: '#ba1a1a', trend: -15.2 },
  { label: 'Pending Approvals', value: '38', icon: Document, iconColor: '#f59e0b', trend: 8.1 },
  { label: 'Awards & Honors', value: '156', icon: Trophy, iconColor: '#10b981', trend: 12.5 },
])

const userInput = ref('')
const isTyping = ref(false)
const chatBox = ref(null)
const chatMessages = ref([
  { text: '您好，我是您的专属数字分身。系统检测到张三1近期存在挂科风险，是否需要我为您生成谈话大纲？', isUser: false }
])

const scrollToBottom = async () => {
  await nextTick()
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

const sendMessage = async () => {
  if (!userInput.value.trim() || isTyping.value) return
  
  const text = userInput.value
  chatMessages.value.push({ text, isUser: true })
  userInput.value = ''
  isTyping.value = true
  scrollToBottom()
  
  try {
    const response = await request.post('/api/ai/chat', { message: text })
    if (response.data.code === 200) {
      chatMessages.value.push({ text: response.data.data, isUser: false })
    } else {
      chatMessages.value.push({ text: 'AI 服务遇到问题：' + response.data.msg, isUser: false })
    }
  } catch (error) {
    chatMessages.value.push({ text: '网络请求失败，请确保后端服务已启动。', isUser: false })
  } finally {
    isTyping.value = false
    scrollToBottom()
  }
}

onMounted(async () => {
  try {
    const res = await request.get('/api/academic/dashboard')
    if (res.data.code === 200) {
      const d = res.data.data
      stats.value[0].value = String(d.totalStudents || 0)
      stats.value[2].value = String(d.warningCount || 0)
    }
  } catch(e) {}
})

const exportData = () => {
  exportToCSV('辅导员工作报表.csv',
    ['指标','数值','变化趋势'],
    stats.value.map(s => ({ '指标':s.label, '数值':s.value, '变化趋势':(s.trend > 0 ? '+' : '') + s.trend + '%' }))
  )
  ElMessage.success('导出成功')
}
</script>
