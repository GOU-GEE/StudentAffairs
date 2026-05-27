<template>
  <div class="h-full flex flex-col space-y-6">
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 flex-1 min-h-0">

      <!-- Left Column: Main Content -->
      <div class="md:col-span-8 flex flex-col gap-6 overflow-y-auto no-scrollbar">

        <!-- AI Banner -->
        <div class="relative overflow-hidden rounded-2xl p-8 text-white shadow-lg flex items-stretch min-h-[260px]">
          <div class="absolute inset-0 w-full h-full">
            <img src="@/assets/career_bg.png" class="w-full h-full object-cover object-right" alt="AI Career Banner">
          </div>
          <div class="absolute inset-0 bg-gradient-to-r from-[#6366F1] via-[#6366F1]/85 to-transparent"></div>
          <div class="relative z-10 w-full flex flex-col justify-between">
            <div class="max-w-md">
              <h3 class="text-3xl font-bold mb-3 tracking-tight">AI 助力你的职业未来</h3>
              <p class="text-white/90 text-sm leading-relaxed mb-6">
                通过测评探索自我，获取个性化建议，精准匹配职业机会，为你的职业发展保驾护航。
              </p>
              <div class="flex flex-wrap gap-3">
                <el-button color="white" round class="!text-indigo-600 font-bold px-6 shadow-md border-0 group" @click="showAssessmentDialog = true">
                  开始生涯测评
                  <el-icon class="ml-2 transition-transform group-hover:translate-x-1"><ArrowRight /></el-icon>
                </el-button>
                <el-button color="rgba(255,255,255,0.2)" round class="!text-white font-bold px-6 border border-white/30 backdrop-blur-sm shadow-md hover:!bg-white/30 group" @click="openAiChat">
                  <el-icon class="mr-2"><ChatDotRound /></el-icon>
                  咨询 AI 导师
                </el-button>
              </div>
            </div>
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
          <div class="bg-white rounded-2xl border border-outline-variant/20 shadow-sm flex flex-col flex-1 overflow-hidden">
            <div class="flex items-center justify-between px-6 py-4 border-b border-outline-variant/15">
              <h3 class="text-[17px] font-bold text-on-surface flex items-center gap-2">
                <div class="w-1.5 h-4 bg-primary rounded-full"></div> 根据成绩问卷寻得最佳匹配
              </h3>
              <el-link type="primary" :underline="false" class="text-sm flex items-center gap-1" @click="runMatching" :disabled="matching">
                {{ matching ? '匹配中...' : '重新匹配' }} <el-icon><Refresh /></el-icon>
              </el-link>
            </div>
            <div class="flex flex-1">
              <div v-if="matchResults.length === 0" class="flex-1 flex items-center justify-center text-secondary py-16">
                <div class="text-center">
                  <el-icon :size="48" class="opacity-20 mb-2"><TrendCharts /></el-icon>
                  <p class="text-sm">请先在右侧"生涯测评工具"中完成测评，然后点击"重新匹配"</p>
                </div>
              </div>
              <div v-else class="flex flex-col flex-1" v-for="(job, idx) in matchResults" :key="idx">
                <div class="p-6 flex flex-col h-full hover:bg-surface-container-lowest transition-colors relative" :class="idx !== 0 ? 'border-l border-outline-variant/20' : ''">
                  <div class="flex items-start justify-between mb-4">
                    <h4 class="font-bold text-on-surface text-[17px] tracking-tight">{{ job.title }}</h4>
                    <span class="text-[11px] bg-green-50 text-green-600 px-2 py-0.5 rounded font-bold border border-green-200 shadow-sm flex-shrink-0">匹配度 {{ job.matchScore }}%</span>
                  </div>
                  <div class="pt-2 flex-1 flex flex-col">
                    <div class="text-[12px] font-bold text-primary mb-2 flex items-center gap-1.5">
                      <el-icon><Aim /></el-icon> 推荐理由
                    </div>
                    <p class="text-[13px] text-secondary leading-relaxed text-justify">{{ job.description }}</p>
                    <div class="mt-3 flex items-center gap-2 text-xs text-secondary">
                      <span class="px-2 py-0.5 rounded bg-purple-50 text-purple-700">{{ job.hollandCode }}</span>
                      <span>{{ job.education }}</span>
                      <span class="text-primary font-bold">{{ job.salary }}</span>
                    </div>
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
          <div v-if="appointments.length > 0" class="relative z-10 mb-4 space-y-1 max-h-32 overflow-y-auto custom-scrollbar">
            <div v-for="a in appointments.slice(0,3)" :key="a.id" class="text-xs flex items-center justify-between bg-white/60 rounded-lg px-3 py-1.5">
              <span class="text-secondary">{{ a.appointmentTime }}</span>
              <span :class="a.status === 'ACCEPTED' ? 'text-green-600' : a.status === 'REJECTED' ? 'text-red-500' : 'text-amber-600'" class="font-bold">{{ statusText(a.status) }}</span>
            </div>
          </div>
          <div class="relative z-10 flex items-center justify-between mt-6">
            <el-button type="primary" class="!rounded-lg shadow-sm shadow-blue-500/20" @click="showAppointmentForm = true">
              立即预约 <el-icon class="ml-1"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- Career Assessment Tools -->
        <div class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/20 shadow-sm">
          <div class="flex items-center justify-between mb-5">
            <h3 class="font-bold text-on-surface">生涯测评工具</h3>
            <el-link type="primary" :underline="false" class="text-[13px] font-semibold" @click="showAssessmentDialog = true">
              查看详细测评结果 <el-icon class="ml-1"><ArrowRight /></el-icon>
            </el-link>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div v-for="(tool, idx) in assessmentTools" :key="idx"
                 class="border border-outline-variant/20 rounded-xl p-4 hover:shadow-md transition-all flex flex-col bg-white">
              <h4 class="font-bold text-sm text-on-surface mb-2">{{ tool.title }}</h4>
              <p class="text-[11px] text-secondary mb-4 line-clamp-2 flex-1">{{ tool.desc }}</p>
              <div class="flex items-center justify-between mt-auto">
                <span class="text-[10px] text-secondary">约{{ tool.time }}分钟</span>
                <el-button :type="tool.btnType" size="small" class="!rounded-lg px-3 text-xs h-7" @click="startQuestionnaire(tool.key)">
                  {{ assessmentResults[tool.key] ? '重测' : '测评' }}
                </el-button>
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
        <div class="flex items-center justify-between mb-2">
          <h3 class="text-2xl font-bold text-on-surface flex items-center gap-3">
            <el-icon class="text-primary"><Document /></el-icon>
            生涯详细测评结果
          </h3>
        </div>
        
        <el-tabs v-model="activeReportTab" class="flex-1 flex flex-col min-h-0 custom-report-tabs">
          <el-tab-pane label="维度得分与测评原图" name="raw" class="h-full flex flex-col min-h-0 pt-2">
            <div class="flex-1 grid grid-cols-2 grid-rows-2 min-h-0 bg-white rounded-3xl border border-outline-variant/30 shadow-sm overflow-hidden">
              <div v-for="(tool, idx) in assessmentTools" :key="idx" class="p-8 flex flex-col relative transition-all hover:bg-surface-container-lowest/50"
                   :class="{
                     'border-b border-outline-variant/30': idx < 2,
                     'border-r border-outline-variant/30': idx % 2 === 0
                   }">
                <div class="mb-4 pb-3 flex items-center justify-between">
                  <h4 class="font-bold text-lg text-on-surface">{{ tool.title }}</h4>
                  <el-button size="small" class="!bg-surface-container-low !border-0 !text-secondary hover:!bg-surface-container hover:!text-on-surface transition-colors" @click="startQuestionnaire(tool.key)">重新测试</el-button>
                </div>
                <div class="flex-1 overflow-y-auto custom-scrollbar pr-2">
                  <div class="relative mt-1">
                    <template v-if="assessmentResults[tool.key]">
                      <div class="text-xl font-bold text-primary mb-3">{{ getResultSummary(tool.key) }}</div>
                      <div class="text-[14px] text-secondary leading-relaxed">{{ getResultDetail(tool.key) }}</div>
                    </template>
                    <div v-else class="text-secondary text-sm flex items-center gap-2">
                      <el-icon><Clock /></el-icon> 尚未测评，点击右上角"重新测试"开始
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="AI 个人深度生涯报告" name="ai" class="h-full flex flex-col min-h-0 pt-2">
            <div class="flex-1 flex flex-col h-full bg-white rounded-3xl border border-outline-variant/30 shadow-sm overflow-hidden p-6">
              
              <!-- Loading Skeleton -->
              <div v-if="generatingReport" class="flex-1 flex flex-col justify-center items-center py-20 px-8">
                <div class="w-16 h-16 rounded-full bg-indigo-50 flex items-center justify-center text-indigo-600 mb-6 animate-pulse relative">
                  <div class="absolute inset-0 rounded-full border-2 border-indigo-600 border-t-transparent animate-spin"></div>
                  <el-icon :size="32" class="text-indigo-600"><Cpu /></el-icon>
                </div>
                <h4 class="text-lg font-bold text-on-surface mb-2">AI 生涯导师正在深度剖析中...</h4>
                <p class="text-xs text-secondary max-w-sm text-center leading-relaxed">
                  正在提取并融合您的霍兰德职业兴趣维度、MBTI性格类型、核心能力素质以及职业价值观数据，为您定制大一至大四生涯黄金路线图。请稍候（约需 3-5 秒）...
                </p>
              </div>

              <!-- Not generated yet screen -->
              <div v-else-if="!reportContent" class="flex-1 flex flex-col justify-center items-center py-16 px-8 text-center">
                <div class="w-20 h-20 bg-indigo-50 rounded-full flex items-center justify-center text-indigo-600 mb-6 shadow-inner">
                  <el-icon :size="40" class="text-indigo-600"><Cpu /></el-icon>
                </div>
                <h4 class="text-lg font-bold text-on-surface mb-2">生成您的独家 AI 深度生涯报告</h4>
                <p class="text-sm text-secondary mb-6 max-w-md leading-relaxed">
                  深度交叉融合霍兰德兴趣与MBTI人格模型，科学输出 SWOT 优劣势态势分析、推荐职业剖析、大一至大四行动路线图与竞争力提升指南。
                </p>
                <el-button 
                  type="primary" 
                  size="large"
                  class="!rounded-2xl !bg-sky-500 hover:!bg-sky-600 !border-0 font-bold px-8 shadow-lg shadow-sky-500/20 text-white" 
                  @click="generateAiReport"
                  :disabled="!hasAllAssessments"
                >
                  一键生成 AI 生涯报告
                </el-button>
                <p v-if="!hasAllAssessments" class="text-xs text-red-500 mt-4 font-semibold">
                  * 请先在主页面右侧“生涯测评工具”中完成全部四项测评后再生成
                </p>
              </div>

              <!-- Report Content Display -->
              <div v-else class="flex-1 flex flex-col min-h-0">
                <div class="flex items-center justify-between border-b border-outline-variant/15 pb-4 mb-4 flex-shrink-0">
                  <div class="flex items-center gap-2">
                    <span class="w-2.5 h-2.5 bg-indigo-600 rounded-full animate-ping"></span>
                    <span class="text-sm font-bold text-indigo-600">智慧学工系统 · 生涯深度分析报告</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <el-button size="small" class="!rounded-lg" @click="copyReport">
                      <el-icon class="mr-1"><DocumentCopy /></el-icon> 复制报告
                    </el-button>
                    <el-button type="primary" size="small" class="!rounded-lg !bg-indigo-600 !border-0" @click="generateAiReport">
                      <el-icon class="mr-1"><Refresh /></el-icon> 重新生成
                    </el-button>
                  </div>
                </div>
                <!-- Scrollable Report Text -->
                <div class="flex-1 overflow-y-auto pr-2 custom-scrollbar text-justify text-on-surface">
                  <div class="prose max-w-none text-sm leading-relaxed space-y-4" v-html="renderMarkdown(reportContent)"></div>
                </div>
              </div>
              
            </div>
          </el-tab-pane>
        </el-tabs>
        
      </div>
    </el-dialog>

    <!-- 问卷答题弹窗 -->
    <el-dialog v-model="quizVisible" :title="currentQuizTitle" width="700px" destroy-on-close :close-on-click-modal="false" modal-class="custom-quiz-overlay" class="custom-quiz-dialog">
      <div class="space-y-4 max-h-[60vh] overflow-y-auto custom-scrollbar">
        <div v-for="(q, idx) in currentQuestions" :key="idx" class="p-3 rounded-xl bg-gray-50">
          <p class="text-sm font-medium mb-2">{{ idx + 1 }}. {{ q.text }}</p>
          <el-radio-group v-model="quizAnswers[idx]" class="flex flex-wrap gap-2">
            <el-radio :value="1">非常不符合</el-radio>
            <el-radio :value="2">比较不符合</el-radio>
            <el-radio :value="3">一般</el-radio>
            <el-radio :value="4">比较符合</el-radio>
            <el-radio :value="5">非常符合</el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <button @click="quizVisible = false" class="px-4 py-2 text-secondary text-sm mr-3">取消</button>
        <button @click="submitQuiz" class="px-6 py-2 bg-purple-500 text-white rounded-lg text-sm font-bold hover:bg-purple-600 transition-colors">提交</button>
      </template>
    </el-dialog>

    <!-- 预约表单弹窗 -->
    <el-dialog v-model="showAppointmentForm" title="预约职业规划咨询" width="500px" destroy-on-close>
      <div class="space-y-4">
        <div>
          <label class="text-xs font-bold text-secondary uppercase block mb-1.5">辅导员</label>
          <el-select v-model="apptForm.teacherId" class="w-full">
            <el-option label="李老师" value="T001" />
          </el-select>
        </div>
        <div>
          <label class="text-xs font-bold text-secondary uppercase block mb-1.5">预约时间</label>
          <el-select v-model="apptForm.appointmentTime" class="w-full" placeholder="选择时间段">
            <el-option label="周一 14:00-15:00" value="周一 14:00-15:00" />
            <el-option label="周二 10:00-11:00" value="周二 10:00-11:00" />
            <el-option label="周三 15:00-16:00" value="周三 15:00-16:00" />
            <el-option label="周四 09:00-10:00" value="周四 09:00-10:00" />
            <el-option label="周五 16:00-17:00" value="周五 16:00-17:00" />
          </el-select>
        </div>
        <div>
          <label class="text-xs font-bold text-secondary uppercase block mb-1.5">咨询原因</label>
          <el-input v-model="apptForm.reason" type="textarea" :rows="4" placeholder="请简要描述你想咨询的问题..." />
        </div>
      </div>
      <template #footer>
        <button @click="showAppointmentForm = false" class="px-4 py-2 text-secondary text-sm mr-3">取消</button>
        <button @click="submitAppointment" class="px-6 py-2 bg-blue-500 text-white rounded-lg text-sm font-bold hover:bg-blue-600 transition-colors" :disabled="submittingAppt">
          {{ submittingAppt ? '提交中...' : '确认预约' }}
        </button>
      </template>
    </el-dialog>

    <!-- ==================== AI 生涯规划浮动小助手 ==================== -->
    <div class="fixed bottom-8 right-8 z-[1050] flex flex-col items-end">
      <!-- Chat Panel -->
      <transition name="ai-chat-drop">
        <div v-if="aiChatVisible" class="bg-white border border-outline-variant/30 rounded-3xl w-[600px] h-[calc(100vh-160px)] mb-4 flex flex-col overflow-hidden transition-all duration-300 max-w-[calc(100vw-2rem)] ai-chat-panel">
          <!-- Chat Header -->
          <div style="background-color: #4a89e247;" class="border-b border-slate-200/80 p-4 text-slate-800 flex items-center justify-between shadow-sm">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-slate-200/60 flex items-center justify-center border border-slate-300/40 text-slate-600 shadow-inner">
                <el-icon :size="20"><Cpu /></el-icon>
              </div>
              <div>
                <h4 class="font-bold text-sm text-slate-800">AI 生涯规划导师</h4>
                <p class="text-[10px] text-slate-500">智慧学工 · 生涯规划专属助手</p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <el-button circle size="small" class="!bg-slate-200/50 !border-0 !text-slate-600 hover:!bg-slate-200/80 hover:!text-slate-800" @click="resetChat">
                <el-icon><Refresh /></el-icon>
              </el-button>
              <el-button circle size="small" class="!bg-slate-200/50 !border-0 !text-slate-600 hover:!bg-slate-200/80 hover:!text-slate-800" @click="aiChatVisible = false">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </div>

          <!-- Chat Messages Container -->
          <div ref="chatScrollContainer" class="flex-1 overflow-y-auto p-4 space-y-4 custom-scrollbar bg-slate-50/50">
            <!-- Messages -->
            <div v-for="(msg, idx) in chatMessages" :key="idx" class="flex w-full" :class="msg.role === 'user' ? 'justify-end' : 'justify-start'">
              <!-- Message Bubble -->
              <div 
                class="rounded-2xl px-4 py-2.5 text-[13px] shadow-sm leading-relaxed max-w-[85%]"
                :class="msg.role === 'user' 
                  ? 'bg-sky-100 text-sky-900 rounded-tr-none border border-sky-200/50' 
                  : 'bg-white text-gray-800 border border-slate-200/60 rounded-tl-none font-normal'"
              >
                <div v-if="msg.role === 'user'">{{ msg.content }}</div>
                <div v-else class="prose max-w-none space-y-2 text-justify" v-html="renderMarkdown(msg.content)"></div>
              </div>
            </div>

            <!-- Typing Loader -->
            <div v-if="chatLoading" class="flex justify-start w-full">
              <div class="bg-white border border-slate-200/60 rounded-2xl rounded-tl-none px-4 py-2.5 text-xs text-secondary flex items-center gap-1.5 shadow-sm">
                <span>AI 导师正在规划中</span>
                <span class="flex gap-0.5 mt-0.5">
                  <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce" style="animation-delay: 0ms"></span>
                  <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce" style="animation-delay: 150ms"></span>
                  <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce" style="animation-delay: 300ms"></span>
                </span>
              </div>
            </div>
          </div>

          <!-- Prompt Chips Area -->
          <div class="px-3 py-1.5 bg-slate-50 border-t border-slate-100 flex gap-2 overflow-x-auto no-scrollbar">
            <button 
              v-for="(chip, cIdx) in promptChips" 
              :key="cIdx" 
              class="text-[11px] bg-white hover:bg-indigo-50 hover:text-indigo-600 text-secondary border border-slate-200 hover:border-indigo-200 rounded-full px-2.5 py-1 whitespace-nowrap transition-all shadow-sm flex-shrink-0"
              @click="sendChipPrompt(chip)"
              :disabled="chatLoading"
            >
              {{ chip }}
            </button>
          </div>

          <!-- Input Area -->
          <div class="p-3 bg-white border-t border-slate-100 flex items-center gap-2 flex-shrink-0">
            <el-input 
              ref="chatInputRef"
              v-model="chatInput" 
              placeholder="输入生涯问题，如：我如何考取大三的证书？" 
              class="flex-1 custom-chat-input"
              @keyup.enter="sendChat"
              size="default"
            />
            <el-button 
              type="primary" 
              circle 
              class="!bg-sky-500 !border-0 flex items-center justify-center hover:!bg-sky-600 shadow-sm transition-colors" 
              @click="sendChat" 
              :disabled="chatLoading"
              size="default"
            >
              <el-icon><Promotion /></el-icon>
            </el-button>
          </div>
        </div>
      </transition>

      <!-- Floating Ball -->
      <button 
        class="w-14 h-14 rounded-full bg-gradient-to-tr from-sky-400 via-sky-400 to-blue-500 text-white flex items-center justify-center shadow-2xl hover:scale-105 active:scale-95 transition-all duration-300 relative group border-2 border-white/20"
        @click="toggleChat"
      >
        <div class="absolute inset-0 rounded-full bg-gradient-to-tr from-sky-400 to-blue-400 blur-md opacity-40 group-hover:opacity-80 transition-opacity"></div>
        <el-icon :size="24" class="relative z-10 animate-pulse"><Cpu /></el-icon>
        <span class="absolute -top-1 -right-1 bg-red-500 text-white text-[9px] font-bold px-1.5 py-0.5 rounded-full border border-white z-20 shadow">AI</span>
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowRight, Document, Refresh, Calendar, Clock, Aim, TrendCharts, Cpu, ChatDotRound, Close, Promotion, Loading, DocumentCopy } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/career'
const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'
const STUDENT_NAME = '张小明'

// ==================== AI 职业规划模块状态 ====================
const aiChatVisible = ref(false)
const chatInput = ref('')
const chatMessages = ref([])
const chatLoading = ref(false)
const chatInputRef = ref(null)
const chatScrollContainer = ref(null)

const generatingReport = ref(false)
const reportContent = ref('')
const activeReportTab = ref('raw')

const promptChips = ref([
  '分析我的职业兴趣与性格',
  '如何成为优秀的软件工程师？',
  '推荐大三该修读的技能与证书',
  '如何做一份高价值的求职简历？'
])

const hasAllAssessments = computed(() => {
  const r = assessmentResults.value
  return !!(r.holland && r.mbti && r.values && r.skills)
})

// ==================== AI Markdown 渲染器 ====================
const renderMarkdown = (text) => {
  if (!text) return ''
  let html = text
  // 基础转义转 HTML 展现以获得专业格式
  html = html.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  
  // H2-H4 格式化
  html = html.replace(/^#\s+(.*?)$/gm, '<h2 class="text-base font-bold text-indigo-700 mt-4 mb-2 border-b border-indigo-100 pb-1.5 flex items-center">$1</h2>')
  html = html.replace(/^##\s+(.*?)$/gm, '<h3 class="text-sm font-bold text-indigo-600 mt-3 mb-1.5 pb-0.5 border-b border-slate-100 flex items-center">$1</h3>')
  html = html.replace(/^###\s+(.*?)$/gm, '<h4 class="text-xs font-bold text-gray-800 mt-2.5 mb-1 flex items-center">$1</h4>')
  
  // 加粗
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong class="font-bold text-indigo-900">$1</strong>')
  
  // 无序列表与有序列表格式化
  html = html.replace(/^(?:\*|-)\s+(.*?)$/gm, '<div class="flex items-start gap-1.5 my-1 text-[13px] text-gray-600"><span class="text-indigo-500 mt-0.5">•</span><span>$1</span></div>')
  html = html.replace(/^\d+\.\s+(.*?)$/gm, '<div class="flex items-start gap-1.5 my-1 text-[13px] text-gray-600"><span class="text-indigo-500 font-bold text-xs mt-0.5">$0</span><span>$1</span></div>')
  // 修复上一个替换中 $0 包含数字点字符的问题，这里直接给个清爽的行
  html = html.replace(/<span class="text-indigo-500 font-bold text-xs mt-0.5">\d+\.\s+<\/span>/g, '<span class="text-indigo-500 font-bold text-[11px] mt-0.5">▷</span>')
  
  // 段落空行
  html = html.replace(/\n/g, '<br/>')
  return html
}

// ==================== 问卷题目 ====================
const HOLLAND_QUESTIONS = [
  { type:'R',text:'我喜欢操作机器或使用工具' },{ type:'R',text:'我喜欢修理或组装物品' },{ type:'R',text:'我喜欢户外工作和体力活动' },{ type:'R',text:'我喜欢动手制作东西' },{ type:'R',text:'我对机械原理感兴趣' },{ type:'R',text:'我喜欢种植花草或照料动物' },{ type:'R',text:'我喜欢使用电脑软件进行工程设计' },{ type:'R',text:'我对交通工具的构造感兴趣' },{ type:'R',text:'我喜欢运动或体育训练' },{ type:'R',text:'我喜欢按照说明书组装产品' },
  { type:'I',text:'我喜欢进行科学实验和研究' },{ type:'I',text:'我喜欢解决复杂的数学问题' },{ type:'I',text:'我喜欢阅读科学类书籍和文章' },{ type:'I',text:'我对自然现象的原理感到好奇' },{ type:'I',text:'我喜欢分析数据和发现规律' },{ type:'I',text:'我喜欢独立思考问题' },{ type:'I',text:'我对医学或生物学研究感兴趣' },{ type:'I',text:'我喜欢使用显微镜或精密仪器' },{ type:'I',text:'我对人工智能和新技术充满好奇' },{ type:'I',text:'我喜欢深入探究事物的本质' },
  { type:'A',text:'我喜欢绘画、雕塑或视觉艺术' },{ type:'A',text:'我喜欢音乐创作或演奏乐器' },{ type:'A',text:'我喜欢写作诗歌、小说或散文' },{ type:'A',text:'我喜欢设计新颖的物品或图形' },{ type:'A',text:'我喜欢摄影或摄像创作' },{ type:'A',text:'我对建筑美学和室内设计感兴趣' },{ type:'A',text:'我喜欢表演或戏剧艺术' },{ type:'A',text:'我享受自由创作的过程' },{ type:'A',text:'我喜欢参观美术馆或艺术展览' },{ type:'A',text:'我对服装搭配和时尚设计有独特见解' },
  { type:'S',text:'我喜欢帮助他人解决困难' },{ type:'S',text:'我喜欢教学或培训工作' },{ type:'S',text:'我乐于参与志愿服务活动' },{ type:'S',text:'我善于倾听和理解他人的感受' },{ type:'S',text:'我喜欢与人合作完成工作' },{ type:'S',text:'我关注社会问题和弱势群体' },{ type:'S',text:'我享受在团队中沟通协调' },{ type:'S',text:'我对心理咨询或辅导工作感兴趣' },{ type:'S',text:'我喜欢组织和参与集体活动' },{ type:'S',text:'我认为帮助他人成长很有意义' },
  { type:'E',text:'我喜欢领导团队完成目标' },{ type:'E',text:'我善于说服他人接受我的观点' },{ type:'E',text:'我对商业策划和创业感兴趣' },{ type:'E',text:'我喜欢在竞争中取胜' },{ type:'E',text:'我善于在公开场合演讲或展示' },{ type:'E',text:'我喜欢管理和组织资源' },{ type:'E',text:'我对市场营销和销售工作感兴趣' },{ type:'E',text:'我敢于承担风险和做出决策' },{ type:'E',text:'我喜欢参与政治或公共事务' },{ type:'E',text:'我追求权力和影响力' },
  { type:'C',text:'我喜欢按照既定的流程和规范工作' },{ type:'C',text:'我善于整理文件和管理数据' },{ type:'C',text:'我对财务会计工作感兴趣' },{ type:'C',text:'我注重工作中的细节和准确性' },{ type:'C',text:'我喜欢有明确规则和标准的工作环境' },{ type:'C',text:'我善于使用办公软件处理文档' },{ type:'C',text:'我喜欢制定计划和安排日程' },{ type:'C',text:'我偏好稳定和可预测的工作节奏' },{ type:'C',text:'我对档案管理和图书分类感兴趣' },{ type:'C',text:'我遵守规章制度并按时完成任务' },
]

const MBTI_QUESTIONS = [
  { dim:'EI',text:'我喜欢参加社交聚会和集体活动',score:1 },{ dim:'EI',text:'我更愿意通过说来表达自己的想法',score:1 },{ dim:'EI',text:'我认识很多人，交际圈很广',score:1 },{ dim:'EI',text:'我在人群中感到精力充沛',score:1 },{ dim:'EI',text:'我倾向于先行动再思考',score:1 },{ dim:'EI',text:'我喜欢成为众人关注的焦点',score:1 },
  { dim:'EI',text:'我更喜欢独处或与少数好友相处',score:-1 },{ dim:'EI',text:'我倾向于通过写来表达自己的想法',score:-1 },{ dim:'EI',text:'我有少数几个深交的朋友',score:-1 },{ dim:'EI',text:'社交活动后我需要独处来恢复精力',score:-1 },{ dim:'EI',text:'我倾向于先思考再行动',score:-1 },{ dim:'EI',text:'我更喜欢在幕后工作而非台前',score:-1 },
  { dim:'SN',text:'我更关注具体的事实和细节',score:1 },{ dim:'SN',text:'我喜欢按照已有的方法做事',score:1 },{ dim:'SN',text:'我更相信实际经验而非理论推测',score:1 },{ dim:'SN',text:'我注重当下实际而非未来可能',score:1 },{ dim:'SN',text:'我喜欢循序渐进地学习新知识',score:1 },{ dim:'SN',text:'我更喜欢处理实际问题而非抽象概念',score:1 },
  { dim:'SN',text:'我关注整体图景和未来可能性',score:-1 },{ dim:'SN',text:'我喜欢尝试新的方法和创意',score:-1 },{ dim:'SN',text:'我依赖直觉和灵感做决策',score:-1 },{ dim:'SN',text:'我关注未来可能性而非现状',score:-1 },{ dim:'SN',text:'我喜欢跳跃式思维和联想',score:-1 },{ dim:'SN',text:'我更喜欢探讨理论和抽象概念',score:-1 },
  { dim:'TF',text:'我做决策时主要依据逻辑和客观分析',score:1 },{ dim:'TF',text:'我认为公平正义比人情更重要',score:1 },{ dim:'TF',text:'我能够冷静地指出他人的错误',score:1 },{ dim:'TF',text:'我倾向于分析问题的利弊得失',score:1 },{ dim:'TF',text:'我认为规则应该一视同仁',score:1 },{ dim:'TF',text:'我在争论中关注谁更有道理',score:1 },
  { dim:'TF',text:'我做决策时会考虑对他人的影响',score:-1 },{ dim:'TF',text:'我认为和谐的人际关系很重要',score:-1 },{ dim:'TF',text:'我倾向于避免冲突和伤害他人感情',score:-1 },{ dim:'TF',text:'我更容易被感人的故事打动',score:-1 },{ dim:'TF',text:'我认为个别情况可以特殊处理',score:-1 },{ dim:'TF',text:'我在争论中关注谁的感受更需要照顾',score:-1 },
  { dim:'JP',text:'我更喜欢事先做好计划和安排',score:1 },{ dim:'JP',text:'我喜欢在规定期限内完成任务',score:1 },{ dim:'JP',text:'我倾向于做出明确决定而非保持开放',score:1 },{ dim:'JP',text:'我喜欢井井有条的生活和工作环境',score:1 },{ dim:'JP',text:'我会列清单并按计划执行',score:1 },{ dim:'JP',text:'我对拖延和临时变更感到不适',score:1 },
  { dim:'JP',text:'我更喜欢灵活应变而非按计划行事',score:-1 },{ dim:'JP',text:'我享受在最后期限前冲刺完成工作',score:-1 },{ dim:'JP',text:'我倾向于保持选择开放而非早早决定',score:-1 },{ dim:'JP',text:'我适应多变和即兴的工作环境',score:-1 },{ dim:'JP',text:'我经常随心所欲地做事情',score:-1 },{ dim:'JP',text:'我认为计划赶不上变化',score:-1 },
]

const VALUES_QUESTIONS = [
  { dim:'achievement',text:'我希望在工作中不断获得成就感' },{ dim:'achievement',text:'我渴望在工作中脱颖而出' },{ dim:'achievement',text:'我认为事业成功是人生的重要目标' },
  { dim:'independence',text:'我希望在工作中拥有自主决策权' },{ dim:'independence',text:'我偏好不受过多约束的工作方式' },{ dim:'independence',text:'我想按自己的节奏和方式工作' },
  { dim:'stability',text:'我重视工作的稳定性和安全感' },{ dim:'stability',text:'我希望有确定的收入和福利保障' },{ dim:'stability',text:'我偏好可预测的工作内容和环境' },{ dim:'stability',text:'我不喜欢频繁更换工作' },
  { dim:'relationships',text:'我重视与同事的良好关系' },{ dim:'relationships',text:'我希望在友好和谐的氛围中工作' },{ dim:'relationships',text:'我认为团队合作比个人表现更重要' },{ dim:'relationships',text:'我享受在工作中帮助和支持他人' },
  { dim:'contribution',text:'我希望工作能对社会有积极影响' },{ dim:'contribution',text:'我认为工作应该创造社会价值' },{ dim:'contribution',text:'我倾向于选择有意义或有使命感的职业' },{ dim:'contribution',text:'我关注环保、公益等社会议题' },
  { dim:'innovation',text:'我渴望在工作中发挥创造力' },{ dim:'innovation',text:'我喜欢挑战传统和尝试新方法' },{ dim:'innovation',text:'我享受创新和突破带来的快乐' },{ dim:'innovation',text:'我对新兴技术和趋势充满兴趣' },
  { dim:'reward',text:'我认为高薪酬是选择工作的重要因素' },{ dim:'reward',text:'我追求优厚的物质回报' },{ dim:'reward',text:'我希望薪资能匹配我的能力贡献' },
  { dim:'environment',text:'我重视舒适的工作环境' },{ dim:'environment',text:'我希望有良好的办公条件和设施' },{ dim:'environment',text:'工作地点和通勤便利对我很重要' },{ dim:'environment',text:'我喜欢轻松自由的办公氛围' },{ dim:'environment',text:'我希望工作地点离家近或交通方便' },
]

const SKILLS_QUESTIONS = [
  { dim:'verbal',text:'我善于用语言清晰表达自己的想法' },{ dim:'verbal',text:'我擅长写作和文字处理' },{ dim:'verbal',text:'我能在公开场合流利地演讲' },{ dim:'verbal',text:'我阅读和理解复杂文本的能力很强' },
  { dim:'math',text:'我擅长数学计算和逻辑推理' },{ dim:'math',text:'我能快速理解和分析数据' },{ dim:'math',text:'我善于发现数字中的规律和异常' },{ dim:'math',text:'我对统计和概率有很好的直觉' },
  { dim:'spatial',text:'我善于想象三维空间中的物体' },{ dim:'spatial',text:'我方向感很好，不易迷路' },{ dim:'spatial',text:'我能根据平面图想象出立体效果' },
  { dim:'interpersonal',text:'我善于与人沟通和交流' },{ dim:'interpersonal',text:'我能快速与陌生人建立联系' },{ dim:'interpersonal',text:'我善于调解人际冲突' },{ dim:'interpersonal',text:'我能敏锐察觉他人的情绪变化' },
  { dim:'organization',text:'我善于规划和管理项目进度' },{ dim:'organization',text:'我能够有效协调多方资源完成任务' },{ dim:'organization',text:'我擅长制定和执行计划' },{ dim:'organization',text:'我能同时处理多个任务并保持条理' },
  { dim:'hands-on',text:'我动手操作能力很强' },{ dim:'hands-on',text:'我善于使用各种工具和器械' },{ dim:'hands-on',text:'我能快速掌握操作类技能' },
  { dim:'creativity',text:'我经常产生新颖的创意和想法' },{ dim:'creativity',text:'我善于从不同角度思考问题' },{ dim:'creativity',text:'我喜欢设计独特的产品或方案' },{ dim:'creativity',text:'我能将看似无关的事物联系起来' },
  { dim:'learning',text:'我学习新知识的速度很快' },{ dim:'learning',text:'我能快速适应新的工作内容和环境' },{ dim:'learning',text:'我善于总结经验和举一反三' },{ dim:'learning',text:'我持续关注行业动态和新技术' },
]

const QMAP = { holland:HOLLAND_QUESTIONS, mbti:MBTI_QUESTIONS, values:VALUES_QUESTIONS, skills:SKILLS_QUESTIONS }
const VDS = ['achievement','independence','stability','relationships','contribution','innovation','reward','environment']
const SDS = ['verbal','math','spatial','interpersonal','organization','hands-on','creativity','learning']
const VLB = { achievement:'成就感',independence:'独立性',stability:'稳定性',relationships:'人际关系',contribution:'社会贡献',innovation:'创新创造',reward:'物质报酬',environment:'工作环境' }
const SLB = { verbal:'语言表达',math:'数理逻辑',spatial:'空间想象',interpersonal:'人际沟通',organization:'组织管理','hands-on':'动手操作',creativity:'创意设计',learning:'学习能力' }

// ==================== 状态 ====================
const showAssessmentDialog = ref(false)
const showAppointmentForm = ref(false)
const matching = ref(false)
const submittingAppt = ref(false)
const matchResults = ref([])
const appointments = ref([])
const assessmentResults = ref({})

const assessmentTools = ref([
  { key:'holland',title:'职业兴趣测评',desc:'探索你的兴趣类型，发掘适合的职业方向',time:'20',btnType:'primary' },
  { key:'mbti',title:'性格特质测评',desc:'了解你的性格优势，适合的工作环境',time:'15',btnType:'success' },
  { key:'skills',title:'能力倾向测评',desc:'评估你的能力水平，找到优势领域',time:'25',btnType:'warning' },
  { key:'values',title:'职业价值观测评',desc:'明确你的职业价值观，帮助职业决策',time:'15',btnType:'danger' },
])

const toolTitles = { holland:'职业兴趣测评',mbti:'性格特质测评',skills:'能力倾向测评',values:'职业价值观测评' }
const stats = ref([{ label:'测评次数',value:'0次' },{ label:'预约咨询',value:'0次' }])

// ==================== 问卷 ====================
const quizVisible = ref(false)
const currentQuizKey = ref('')
const currentQuizTitle = ref('')
const currentQuestions = ref([])
const quizAnswers = ref([])

const startQuestionnaire = (key) => {
  currentQuizKey.value = key
  currentQuizTitle.value = toolTitles[key]
  currentQuestions.value = QMAP[key]
  quizAnswers.value = new Array(QMAP[key].length).fill(3)
  quizVisible.value = true
}

const submitQuiz = () => {
  const key = currentQuizKey.value
  const qs = QMAP[key]
  if (quizAnswers.value.some(a => !a)) { ElMessage.warning('请完成所有题目'); return }
  if (key === 'holland') {
    const scores = {}; ['R','I','A','S','E','C'].forEach(d => { const dq=qs.filter(q=>q.type===d); scores[d]=Math.round(dq.reduce((s,q,i)=>s+quizAnswers.value[qs.indexOf(q)],0)/(dq.length*5)*1000)/1000 })
    assessmentResults.value.holland = scores
  } else if (key === 'mbti') {
    const ei=qs.filter(q=>q.dim==='EI').reduce((s,q,i)=>s+quizAnswers.value[qs.indexOf(q)]*q.score,0)
    const sn=qs.filter(q=>q.dim==='SN').reduce((s,q,i)=>s+quizAnswers.value[qs.indexOf(q)]*q.score,0)
    const tf=qs.filter(q=>q.dim==='TF').reduce((s,q,i)=>s+quizAnswers.value[qs.indexOf(q)]*q.score,0)
    const jp=qs.filter(q=>q.dim==='JP').reduce((s,q,i)=>s+quizAnswers.value[qs.indexOf(q)]*q.score,0)
    assessmentResults.value.mbti = (ei>0?'E':'I')+(sn>0?'S':'N')+(tf>0?'T':'F')+(jp>0?'J':'P')
  } else if (key === 'values') {
    const scores = {}; VDS.forEach(d=>{ const dq=qs.filter(q=>q.dim===d); scores[d]=Math.round(dq.reduce((s,q,i)=>s+quizAnswers.value[qs.indexOf(q)],0)/(dq.length*5)*1000)/1000 })
    assessmentResults.value.values = scores
  } else if (key === 'skills') {
    const scores = {}; SDS.forEach(d=>{ const dq=qs.filter(q=>q.dim===d); scores[d]=Math.round(dq.reduce((s,q,i)=>s+quizAnswers.value[qs.indexOf(q)],0)/(dq.length*5)*1000)/1000 })
    assessmentResults.value.skills = scores
  }
  updateStats(); quizVisible.value = false; matchResults.value = []
  ElMessage.success(`${toolTitles[key]} 完成！`)
  localStorage.setItem('career_assessment_results', JSON.stringify(assessmentResults.value))
}

// ==================== 结果展示 ====================
const getResultSummary = (key) => {
  const r = assessmentResults.value
  if (key==='holland'&&r.holland) { const o=['R','I','A','S','E','C'].sort((a,b)=>(r.holland[b]||0)-(r.holland[a]||0)); const n={R:'现实型',I:'研究型',A:'艺术型',S:'社会型',E:'企业型',C:'常规型'}; return `${n[o[0]]} / ${n[o[1]]}` }
  if (key==='mbti'&&r.mbti) { const n={INTJ:'建筑师',INTP:'逻辑学家',ENTJ:'指挥官',ENTP:'辩论家',INFJ:'提倡者',INFP:'调停者',ENFJ:'主人公',ENFP:'竞选者',ISTJ:'物流师',ISFJ:'守卫者',ESTJ:'总经理',ESFJ:'执政官',ISTP:'鉴赏家',ISFP:'探险家',ESTP:'企业家',ESFP:'表演者'}; return `${r.mbti} (${n[r.mbti]||''})` }
  if (key==='skills'&&r.skills) { const o=SDS.sort((a,b)=>(r.skills[b]||0)-(r.skills[a]||0)); return `${SLB[o[0]]}突出 (${Math.round((r.skills[o[0]]||0)*100)}分)` }
  if (key==='values'&&r.values) { const o=VDS.sort((a,b)=>(r.values[b]||0)-(r.values[a]||0)); return `${VLB[o[0]]}导向 / ${VLB[o[1]]||''}` }
  return ''
}
const getResultDetail = (key) => {
  const r = assessmentResults.value
  if (key==='holland'&&r.holland) { const o=['R','I','A','S','E','C'].sort((a,b)=>(r.holland[b]||0)-(r.holland[a]||0)); const n={R:'现实型',I:'研究型',A:'艺术型',S:'社会型',E:'企业型',C:'常规型'}; const d={R:'喜欢动手操作、机械和户外活动',I:'喜欢思考研究、分析和探索',A:'喜欢创造表达、设计和艺术',S:'喜欢帮助他人、教学和社会服务',E:'喜欢领导管理、说服和商业活动',C:'喜欢规范流程、数据和秩序'}; return `倾向于${n[o[0]]}类型的职业方向。${d[o[0]]}，适合${n[o[0]]}导向的岗位。` }
  if (key==='mbti'&&r.mbti) return `富有想象力和战略性思维，对事物有自己的规划。具备独立思考和深度分析的能力。`
  if (key==='skills'&&r.skills) { const o=SDS.sort((a,b)=>(r.skills[b]||0)-(r.skills[a]||0)); return `在${SLB[o[0]]}方面具有显著优势，在${SLB[o[1]]}方面表现良好。建议在职业选择中充分发挥这些核心能力。` }
  if (key==='values'&&r.values) { const o=VDS.sort((a,b)=>(r.values[b]||0)-(r.values[a]||0)); return `期望在工作中获得${VLB[o[0]]}和${VLB[o[1]]}，注重自我价值实现，适合能够发挥自主性和创造力的职业。` }
  return ''
}

// ==================== 匹配 ====================
const runMatching = async () => {
  const r = assessmentResults.value
  if (!r.holland||!r.mbti||!r.values||!r.skills) { ElMessage.warning('请先在右侧"生涯测评工具"中完成全部四项测评'); return }
  matching.value = true
  try {
    const res = await request.post(`${API}/match`, {
      hollandScores:r.holland, mbtiType:r.mbti,
      valueScores:VDS.map(d=>r.values[d]), skillScores:SDS.map(d=>r.skills[d]),
    })
    if (res.data.code===200) { matchResults.value=res.data.data; ElMessage.success('匹配完成！') }
  } catch(e) { ElMessage.error('匹配失败') }
  matching.value = false
}

// ==================== 预约 ====================
const apptForm = ref({ teacherId:'T001',appointmentTime:'',reason:'' })
const loadAppointments = async () => {
  try { const res=await request.get(`${API}/appointments?studentId=${STUDENT_ID}`); if(res.data.code===200) appointments.value=res.data.data; updateStats() } catch(e){}
}
const submitAppointment = async () => {
  if (submittingAppt.value) return
  if(!apptForm.value.appointmentTime||!apptForm.value.reason){ ElMessage.warning('请填写预约时间和咨询原因'); return }
  submittingAppt.value=true
  try {
    const res=await request.post(`${API}/appointments`,{ studentId:STUDENT_ID,studentName:STUDENT_NAME,teacherId:apptForm.value.teacherId,appointmentTime:apptForm.value.appointmentTime,reason:apptForm.value.reason })
    if(res.data.code===200){ ElMessage.success('预约已提交'); showAppointmentForm.value=false; apptForm.value={teacherId:'T001',appointmentTime:'',reason:''}; loadAppointments() }
  } catch(e){ ElMessage.error('提交失败') }
  submittingAppt.value=false
}
const statusText = (s)=>({PENDING:'待确认',ACCEPTED:'已接受',REJECTED:'已拒绝'}[s]||s)
const updateStats = ()=>{
  const r=assessmentResults.value; const c=[r.holland,r.mbti,r.values,r.skills].filter(Boolean).length
  stats.value=[{ label:'测评次数',value:c+'次' },{ label:'预约咨询',value:appointments.value.length+'次' }]
}

// ==================== AI 对话交互逻辑 ====================
const toggleChat = () => {
  aiChatVisible.value = !aiChatVisible.value
  if (aiChatVisible.value && chatMessages.value.length === 0) {
    initChat()
  }
}

const openAiChat = () => {
  aiChatVisible.value = true
  if (chatMessages.value.length === 0) {
    initChat()
  }
}

const initChat = () => {
  chatMessages.value = [
    {
      role: 'assistant',
      content: '👋 您好，张小明！我是您的专属 **AI 生涯规划导师**。\n\n我已经同步了您的生涯测评底册。我们可以根据您的霍兰德职业兴趣和性格特质，深度定制您的大学成长行动计划。您可以随时向我咨询测评解读、能力提升路径、或求职面试方面的技巧。'
    }
  ]
}

const resetChat = () => {
  initChat()
}

const scrollToBottom = () => {
  setTimeout(() => {
    if (chatScrollContainer.value) {
      chatScrollContainer.value.scrollTop = chatScrollContainer.value.scrollHeight
    }
  }, 120)
}

const sendChipPrompt = (promptText) => {
  chatInput.value = promptText
  sendChat()
}

const sendChat = async () => {
  if (chatLoading.value) return
  const text = chatInput.value.trim()
  if (!text) return

  // 添加用户消息
  chatMessages.value.push({ role: 'user', content: text })
  chatInput.value = ''
  scrollToBottom()
  
  // 保持输入框获取焦点
  chatInputRef.value?.focus()
  
  chatLoading.value = true
  
  try {
    const r = assessmentResults.value
    const matchedJobsList = matchResults.value.map(j => j.title)
    const historyList = chatMessages.value.slice(1, -1).map(m => ({
      role: m.role,
      content: m.content
    })).slice(-6)

    const res = await request.post('/api/ai/career/chat', {
      message: text,
      history: historyList,
      hollandScores: r.holland || null,
      mbtiType: r.mbti || null,
      valueScores: r.values ? VDS.map(d => r.values[d]) : null,
      skillScores: r.skills ? SDS.map(d => r.skills[d]) : null,
      matchedJobs: matchedJobsList.length > 0 ? matchedJobsList : null
    }, {
      timeout: 60000
    })

    if (res.data.code === 200) {
      chatMessages.value.push({ role: 'assistant', content: res.data.data })
    } else {
      chatMessages.value.push({ role: 'assistant', content: 'AI 导师思路有些卡壳，请您再说一遍。' })
    }
  } catch (e) {
    chatMessages.value.push({ role: 'assistant', content: '连接服务失败，请检查网络或后端配置状态。' })
  }
  
  chatLoading.value = false
  scrollToBottom()
  
  // 保持输入框获取焦点
  chatInputRef.value?.focus()
}

// ==================== AI 报告生成逻辑 ====================
const generateAiReport = async () => {
  if (generatingReport.value) return
  generatingReport.value = true
  activeReportTab.value = 'ai'

  try {
    const r = assessmentResults.value
    const matchedJobsList = matchResults.value.map(j => j.title)

    const res = await request.post('/api/ai/career/report', {
      hollandScores: r.holland || null,
      mbtiType: r.mbti || null,
      valueScores: r.values ? VDS.map(d => r.values[d]) : null,
      skillScores: r.skills ? SDS.map(d => r.skills[d]) : null,
      matchedJobs: matchedJobsList.length > 0 ? matchedJobsList : null
    }, {
      timeout: 120000
    })

    if (res.data.code === 200) {
      reportContent.value = res.data.data
      localStorage.setItem('career_ai_report_content', reportContent.value)
      ElMessage.success('AI 深度生涯规划报告生成成功！')
    } else {
      ElMessage.error('生成报告失败：' + res.data.message)
    }
  } catch (e) {
    ElMessage.error('服务请求超时，生成报告失败')
  }

  generatingReport.value = false
}

const copyReport = () => {
  if (!reportContent.value) return
  navigator.clipboard.writeText(reportContent.value).then(() => {
    ElMessage.success('报告内容已复制到剪贴板！')
  }).catch(() => {
    ElMessage.error('复制失败，请手动选择复制。')
  })
}

onMounted(()=>{
  loadAppointments()
  const s=localStorage.getItem('career_assessment_results')
  if(s){ try{ assessmentResults.value=JSON.parse(s); updateStats() } catch(e){} }
  const savedReport = localStorage.getItem('career_ai_report_content')
  if(savedReport) { reportContent.value = savedReport }
})
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar{display:none}
.no-scrollbar{-ms-overflow-style:none;scrollbar-width:none}
.custom-scrollbar::-webkit-scrollbar{width:4px}
.custom-scrollbar::-webkit-scrollbar-track{background:transparent}
.custom-scrollbar::-webkit-scrollbar-thumb{background:var(--el-border-color-lighter);border-radius:4px}
.custom-scrollbar:hover::-webkit-scrollbar-thumb{background:var(--el-border-color)}

/* ==================== AI 职业规划定制样式 ==================== */
.custom-report-tabs .el-tabs__header {
  margin-bottom: 4px !important;
}
.custom-report-tabs .el-tabs__item {
  font-size: 14px !important;
  font-weight: 600 !important;
  color: var(--el-text-color-secondary) !important;
  padding: 0 16px !important;
}
.custom-report-tabs .el-tabs__item.is-active {
  color: #4f46e5 !important;
}
.custom-report-tabs .el-tabs__active-bar {
  background-color: #4f46e5 !important;
  height: 3px !important;
  border-radius: 99px;
}
.prose h2, .prose h3, .prose h4 {
  font-family: inherit;
}
.no-scrollbar::-webkit-scrollbar {
  display: none !important;
}

/* ==================== AI 浮动窗口定制过渡动画 ==================== */
.ai-chat-drop-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.ai-chat-drop-leave-active {
  transition: all 0.2s ease;
}
.ai-chat-drop-enter-from,
.ai-chat-drop-leave-to {
  opacity: 0;
  transform: translateY(16px) scale(0.95);
  transform-origin: bottom right;
}
</style>

<style>
/* ==================== AI 职业规划全局样式覆盖 ==================== */
.custom-chat-input .el-input__wrapper {
  border-radius: 24px !important;
  box-shadow: 0 0 0 1px var(--el-border-color) inset !important;
  padding-left: 16px !important;
  padding-right: 16px !important;
}
.custom-chat-input .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1.5px #0ea5e9 inset !important;
}
.ai-chat-panel {
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.16), 0 8px 20px rgba(15, 23, 42, 0.08) !important;
}

.custom-calendar-overlay{background-color:transparent!important}
.custom-calendar-dialog{position:fixed!important;top:96px!important;bottom:40px!important;left:288px!important;right:32px!important;width:auto!important;height:auto!important;margin:0!important;padding:0!important;display:flex;flex-direction:column;border-radius:32px!important;overflow:hidden!important;box-shadow:0 25px 50px -12px rgba(0,0,0,0.25)!important;overscroll-behavior:contain}
.custom-calendar-dialog .el-dialog__headerbtn{top:22px;right:25px;z-index:100}
.custom-calendar-dialog .el-dialog__header{padding:0!important;margin:0!important;height:0}
.custom-calendar-dialog .el-dialog__body{flex:1;min-height:0;padding:0!important}

@media (min-width: 768px) {
  .custom-quiz-overlay,
  .custom-quiz-overlay .el-overlay-dialog {
    left: 256px !important;
    width: calc(100% - 256px) !important;
  }
}

.custom-quiz-dialog {
  border-radius: 20px !important;
  overflow: hidden !important;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25) !important;
}
</style>
