<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
    <!-- 左侧：上报表单 -->
    <div class="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <!-- 头部选项卡 -->
      <div class="px-6 pt-6 border-b border-gray-100 flex-shrink-0">
        <div class="mb-6">
          <h3 class="text-base font-bold text-gray-900">安全与突发事件报备</h3>
          <p class="text-xs text-gray-400 mt-1">请按实、按时完成报备，确保校园生活安全有序</p>
        </div>
        
        <div class="flex gap-8">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            @click="activeTab = tab.id"
            class="pb-3 text-sm font-bold transition-all relative"
            :class="activeTab === tab.id ? 'text-red-600' : 'text-gray-400 hover:text-gray-600'"
          >
            {{ tab.name }}
            <div v-if="activeTab === tab.id" class="absolute bottom-0 left-0 right-0 h-0.5 bg-red-600 rounded-full"></div>
          </button>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-6">
        <!-- 1. 晚归登记 -->
        <div v-if="activeTab === 'late'" class="space-y-5 animate-in fade-in slide-in-from-bottom-2 duration-300">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="text-sm text-gray-700 font-medium block mb-1.5">预计回宿舍时间 <span class="text-red-500">*</span></label>
              <input 
                v-model="lateForm.time"
                placeholder="请输入时间，如 23:30"
                class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-red-500 hover:border-gray-400 transition-colors" 
              />
            </div>
            <div>
              <label class="text-sm text-gray-700 font-medium block mb-1.5">报备日期</label>
              <input :value="today" disabled class="w-full border border-gray-100 rounded-xl px-3 py-2.5 text-sm text-gray-400 bg-gray-50 outline-none" />
            </div>
          </div>
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">晚归原因 <span class="text-red-500">*</span></label>
            <textarea 
              v-model="lateForm.reason" 
              rows="4" 
              placeholder="请如实说明晚归原因（如：科研实验延时、社团活动、家庭突发状况等）"
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-red-500 hover:border-gray-400 transition-colors resize-none"
            ></textarea>
          </div>
        </div>


        <!-- 3. 异常上报 -->
        <div v-if="activeTab === 'emergency'" class="space-y-5 animate-in fade-in slide-in-from-bottom-2 duration-300">
          <div class="bg-red-50 border border-red-100 rounded-xl p-4 flex items-start gap-3 mb-2">
            <el-icon class="text-red-500 mt-0.5" :size="16"><Warning /></el-icon>
            <p class="text-xs text-red-700 leading-relaxed">
              <strong>提示：</strong> 若遇危及生命的极度紧急情况，请优先拨打校园 110 或 120。本系统用于事后上报或非即时性安全隐患反映。
            </p>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="text-sm text-gray-700 font-medium block mb-1.5">事件类型 <span class="text-red-500">*</span></label>
              <el-select v-model="emergencyForm.type" placeholder="请选择" class="w-full custom-el-select">
                <el-option label="校园安全隐患" value="safety" />
                <el-option label="个人健康突发" value="medical" />
                <el-option label="财产丢失/被盗" value="theft" />
                <el-option label="心理/情绪求助" value="mental" />
                <el-option label="其他异常" value="other" />
              </el-select>
            </div>
            <div>
              <label class="text-sm text-gray-700 font-medium block mb-1.5">发生地点 <span class="text-red-500">*</span></label>
              <input 
                v-model="emergencyForm.location"
                placeholder="如：西二教学楼 3 楼走廊"
                class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-red-500 hover:border-gray-400 transition-colors" 
              />
            </div>
          </div>
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">详情描述 <span class="text-red-500">*</span></label>
            <textarea 
              v-model="emergencyForm.desc" 
              rows="4" 
              placeholder="请详细描述异常情况，包括时间、人物及现状..."
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-red-500 hover:border-gray-400 transition-colors resize-none"
            ></textarea>
          </div>
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">现场照片 (可选)</label>
            <div class="flex gap-3">
              <div class="w-24 h-24 border-2 border-dashed border-gray-200 rounded-xl flex flex-col items-center justify-center text-gray-400 cursor-pointer hover:border-red-300 hover:text-red-400 transition-colors">
                <el-icon :size="24"><Camera /></el-icon>
                <span class="text-[10px] mt-1">上传照片</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部提交按钮 -->
      <div class="px-6 py-4 border-t border-gray-100 flex-shrink-0 flex items-center justify-end">
        <button 
          @click="submitReport"
          :disabled="submitting"
          class="px-8 py-3 bg-red-600 text-white rounded-xl font-bold text-sm hover:bg-red-700 transition-colors shadow-lg shadow-red-100 disabled:opacity-50 flex items-center gap-2"
        >
          {{ submitting ? '提交中...' : '提交报备' }}
        </button>
      </div>
    </div>

    <!-- 右侧：记录追踪 -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <div class="flex items-center justify-between px-6 pt-6 pb-4 border-b border-gray-100 flex-shrink-0">
        <div class="flex items-center gap-3">
          <el-icon class="text-gray-400" :size="18"><List /></el-icon>
          <h3 class="text-base font-bold text-gray-900">报备记录追踪</h3>
        </div>
        <button class="text-[11px] text-gray-400 hover:text-red-600">全部记录</button>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5">
        <div class="space-y-4">
          <div 
            v-for="item in records" 
            :key="item.id"
            class="p-4 border border-gray-100 rounded-xl bg-white hover:border-red-200 transition-all relative overflow-hidden group"
          >
            <div class="flex items-center justify-between mb-2">
              <span class="text-[10px] font-black text-gray-400 tracking-wider uppercase">{{ item.typeText }}</span>
              <span 
                class="text-[10px] font-bold px-2 py-0.5 rounded-full"
                :class="statusBadgeStyle(item.status)"
              >{{ statusLabel(item.status) }}</span>
            </div>
            <h4 class="text-sm font-bold text-gray-800 mb-1 group-hover:text-red-600 transition-colors">{{ item.title }}</h4>
            <div class="flex items-center gap-4 text-[10px] text-gray-400 mt-2">
              <span class="flex items-center gap-1"><el-icon><Calendar /></el-icon> {{ item.date }}</span>
              <span class="flex items-center gap-1"><el-icon><Clock /></el-icon> {{ item.statusTime }}</span>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="records.length === 0" class="py-20 flex flex-col items-center text-gray-400">
          <div class="text-5xl mb-4">📜</div>
          <p class="text-sm">暂无报备历史</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Warning, Camera, List, Calendar, Clock 
} from '@element-plus/icons-vue'

const tabs = [
  { id: 'late', name: '晚归登记' },
  { id: 'emergency', name: '异常情况上报' }
]
const activeTab = ref('late')
const today = new Date().toISOString().split('T')[0]
const submitting = ref(false)

const lateForm = ref({ time: '', reason: '' })
const outForm = ref({ destination: '', range: [], reason: '' })
const emergencyForm = ref({ type: '', location: '', desc: '' })

const records = ref([
  { id: 1, type: 'late', typeText: '晚归登记', title: '预计23:30返回 (图书馆闭馆延时)', status: 'APPROVED', date: '2025-05-10', statusTime: '已确认' },
  { id: 2, type: 'out', typeText: '外出报备', title: '成都市武侯区 (周末返家)', status: 'PENDING', date: '2025-05-12', statusTime: '待审批' },
  { id: 3, type: 'emergency', typeText: '异常上报', title: '西二教 304 教室窗户无法关闭', status: 'COMPLETED', date: '2025-04-28', statusTime: '已处理' }
])

const statusLabel = (s) => ({
  PENDING: '审核中',
  APPROVED: '已通过',
  REJECTED: '未通过',
  COMPLETED: '已处理'
}[s] || s)

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-orange-50 text-orange-600',
  APPROVED: 'bg-green-50 text-green-700',
  REJECTED: 'bg-red-50 text-red-600',
  COMPLETED: 'bg-blue-50 text-blue-600'
}[s] || 'bg-gray-100 text-gray-500')

const submitReport = async () => {
  // 基础校验
  if (activeTab.value === 'late' && (!lateForm.value.time || !lateForm.value.reason)) {
    ElMessage.warning('请填写完整晚归登记信息'); return
  }
  if (activeTab.value === 'out' && (!outForm.value.destination || !outForm.value.range || outForm.value.range.length === 0)) {
    ElMessage.warning('请填写完整外出报备信息'); return
  }
  if (activeTab.value === 'emergency' && (!emergencyForm.value.type || !emergencyForm.value.desc)) {
    ElMessage.warning('请选择异常类型并描述详情'); return
  }

  submitting.value = true
  await new Promise(r => setTimeout(r, 1000))

  let title = ''
  if (activeTab.value === 'late') title = `预计 ${lateForm.value.time} 返回 (${lateForm.value.reason.substring(0, 10)}...)`
  else if (activeTab.value === 'out') title = `${outForm.value.destination} (${outForm.value.reason.substring(0, 10)}...)`
  else title = `${emergencyForm.value.location} - ${emergencyForm.value.desc.substring(0, 10)}...`

  const newRecord = {
    id: Date.now(),
    type: activeTab.value,
    typeText: tabs.find(t => t.id === activeTab.value).name,
    title,
    status: activeTab.value === 'emergency' ? 'PENDING' : 'APPROVED',
    date: today,
    statusTime: activeTab.value === 'emergency' ? '待处理' : '已确认',
    student: '张小明',
    dorm: '南区 8 栋 304'
  }

  records.value.unshift(newRecord)
  
  // Persist for teacher
  const allReports = JSON.parse(localStorage.getItem('safety_reports') || '[]')
  allReports.unshift(newRecord)
  localStorage.setItem('safety_reports', JSON.stringify(allReports))

  ElMessage.success('报备信息已成功提交！')
  submitting.value = false
  
  // 重置表单
  lateForm.value = { time: '', reason: '' }
  outForm.value = { destination: '', range: [], reason: '' }
  emergencyForm.value = { type: '', location: '', desc: '' }
}
</script>

<style>
/* 适配 Element Plus 内部样式 */
.custom-el-date.el-range-editor.el-input__inner {
  border-radius: 0.75rem !important;
  padding: 10px 15px !important;
  height: auto !important;
  border-color: #e5e7eb !important;
}
.custom-el-input .el-input__inner {
  border-radius: 0.75rem !important;
  padding: 22px 15px !important;
  border-color: #e5e7eb !important;
}
.custom-el-select .el-input__inner {
  border-radius: 0.75rem !important;
  padding: 22px 15px !important;
}
</style>
