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
              <label for="lateTime" class="text-sm text-gray-700 font-medium block mb-1.5">预计回宿舍时间 <span class="text-red-500">*</span></label>
              <input 
                id="lateTime"
                name="lateTime"
                v-model="lateForm.time"
                placeholder="请输入时间，如 23:30"
                class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-red-500 hover:border-gray-400 transition-colors" 
              />
            </div>
            <div>
              <label for="lateDate" class="text-sm text-gray-700 font-medium block mb-1.5">报备日期</label>
              <input id="lateDate" name="lateDate" :value="today" disabled class="w-full border border-gray-100 rounded-xl px-3 py-2.5 text-sm text-gray-400 bg-gray-50 outline-none" />
            </div>
          </div>
          <div>
            <label for="lateReason" class="text-sm text-gray-700 font-medium block mb-1.5">晚归原因 <span class="text-red-500">*</span></label>
            <textarea 
              id="lateReason"
              name="lateReason"
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
              <label for="emergencyType" class="text-sm text-gray-700 font-medium block mb-1.5">事件类型 <span class="text-red-500">*</span></label>
              <el-select id="emergencyType" name="emergencyType" v-model="emergencyForm.type" placeholder="请选择" class="w-full custom-el-select">
                <el-option label="校园安全隐患" value="safety" />
                <el-option label="个人健康突发" value="medical" />
                <el-option label="财产丢失/被盗" value="theft" />
                <el-option label="心理/情绪求助" value="mental" />
                <el-option label="其他异常" value="other" />
              </el-select>
            </div>
            <div>
              <label for="emergencyLocation" class="text-sm text-gray-700 font-medium block mb-1.5">发生地点 <span class="text-red-500">*</span></label>
              <input 
                id="emergencyLocation"
                name="emergencyLocation"
                v-model="emergencyForm.location"
                placeholder="如：西二教学楼 3 楼走廊"
                class="w-full h-11 border border-gray-200 rounded-xl px-3 text-sm text-gray-700 bg-white outline-none focus:border-red-500 hover:border-gray-400 transition-colors" 
              />
            </div>
          </div>
          <div>
            <label for="emergencyDesc" class="text-sm text-gray-700 font-medium block mb-1.5">详情描述 <span class="text-red-500">*</span></label>
            <textarea 
              id="emergencyDesc"
              name="emergencyDesc"
              v-model="emergencyForm.desc" 
              rows="4" 
              placeholder="请详细描述异常情况，包括时间、人物及现状..."
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-red-500 hover:border-gray-400 transition-colors resize-none"
            ></textarea>
          </div>
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">现场照片 (可选)</label>
            <div class="flex gap-3">
              <el-upload
                ref="safetyUploadRef"
                action="/api/upload"
                :headers="uploadHeaders"
                :limit="3"
                list-type="picture-card"
                accept="image/*"
                :on-success="handlePhotoSuccess"
                class="safety-uploader"
              >
                <el-icon :size="24"><Camera /></el-icon>
                <span class="text-[10px] mt-1">上传照片</span>
              </el-upload>
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
        <button @click="router.push('/student/applications')" class="text-[11px] text-gray-400 hover:text-red-600">全部记录</button>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5">
        <div class="space-y-4">
          <div 
            v-for="item in records" 
            :key="item.id"
            @click="selectIncident(item)"
            class="p-4 border border-gray-100 rounded-xl bg-white hover:border-red-200 transition-all relative overflow-hidden group cursor-pointer"
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

    <!-- 局部暗色遮罩层，仅覆盖内容区，避免导航栏变灰 -->
    <div v-if="showDetailDialog" class="fixed top-[56px] left-[256px] right-0 bottom-0 bg-black/45 z-[1000] transition-opacity duration-300" @click="showDetailDialog = false"></div>

    <!-- 请假详情滑出抽屉 (el-dialog) -->
    <el-dialog
      v-model="showDetailDialog"
      title=""
      modal-class="custom-safety-overlay"
      class="custom-safety-detail-dialog"
      :show-close="false"
      :modal="false"
      append-to-body
      lock-scroll
      destroy-on-close
    >
      <div v-if="selectedIncident" class="flex flex-col h-full bg-white relative">
        <!-- 抽屉头部 -->
        <div class="px-6 py-5 border-b border-gray-100 flex items-center justify-between flex-shrink-0">
          <span class="text-base font-bold text-gray-900">详情信息</span>
          <button @click="showDetailDialog = false" class="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 text-gray-500 hover:text-gray-700 flex items-center justify-center transition-colors focus:outline-none">
            <el-icon :size="16"><Close /></el-icon>
          </button>
        </div>

        <!-- 滚动内容区 -->
        <div class="flex-1 overflow-y-auto px-8 py-6 space-y-5 custom-scrollbar text-sm text-gray-700 leading-relaxed bg-white">
          <div v-if="selectedIncident.type === 'late'" class="space-y-4">
            <div>
              <div class="text-gray-400 text-xs font-semibold">标题：</div>
              <div class="text-gray-800 mt-1 font-bold text-base">晚归登记</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">预计回宿舍时间：</div>
              <div class="text-gray-800 mt-1 font-medium">{{ selectedIncident.detail.time || '未填写' }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">晚归原因：</div>
              <div class="text-gray-800 mt-1 font-medium whitespace-pre-wrap leading-relaxed">{{ selectedIncident.detail.reason || '未填写' }}</div>
            </div>
          </div>
          <div v-else class="space-y-4">
            <div>
              <div class="text-gray-400 text-xs font-semibold">事件类型：</div>
              <div class="text-gray-800 mt-1 font-bold text-base">
                {{ {
                  safety: '校园安全隐患',
                  medical: '个人健康突发',
                  theft: '财产丢失/被盗',
                  mental: '心理/情绪求助',
                  other: '其他异常'
                }[selectedIncident.detail.type] || selectedIncident.typeText }}
              </div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">发生地点：</div>
              <div class="text-gray-800 mt-1 font-medium">{{ selectedIncident.detail.location || '未填写' }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">详情描述：</div>
              <div class="text-gray-800 mt-1 font-medium whitespace-pre-wrap leading-relaxed">{{ selectedIncident.detail.desc || '未填写' }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold mb-2">现场照片 (点击可放大)：</div>
              <div v-if="selectedIncident.photoUrl" class="w-40 h-40 rounded-xl overflow-hidden border border-gray-100 bg-gray-50 flex items-center justify-center cursor-zoom-in hover:shadow-md transition-all">
                <img :src="selectedIncident.photoUrl" alt="现场照片" class="w-full h-full object-cover" @click="zoomedImageUrl = selectedIncident.photoUrl">
              </div>
              <div v-else class="text-gray-400 text-xs font-medium">无照片</div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 图片放大预览遮罩层 -->
    <teleport to="body">
      <div 
        v-if="zoomedImageUrl" 
        class="fixed top-[56px] left-0 md:left-[256px] right-0 bottom-0 bg-black/80 z-[99999] flex items-center justify-center transition-all duration-300 animate-in fade-in"
        @click="zoomedImageUrl = null"
      >
        <div class="relative max-w-[85vw] max-h-[75vh] flex items-center justify-center" @click.stop>
          <img :src="zoomedImageUrl" class="max-w-full max-h-[75vh] object-contain rounded-2xl shadow-2xl border border-white/10" />
          <button @click="zoomedImageUrl = null" class="absolute top-4 right-4 w-10 h-10 rounded-full bg-black/50 hover:bg-black/70 text-white flex items-center justify-center transition-colors">
            <el-icon :size="20"><Close /></el-icon>
          </button>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import {
  Warning, Camera, List, Calendar, Clock, Close
} from '@element-plus/icons-vue'

const router = useRouter()
const API = '/api/safety/incidents'
const studentId = sessionStorage.getItem('userId') || '202301042'
const studentName = '张小明'
const uploadHeaders = { Authorization: 'Bearer ' + sessionStorage.getItem('token') }

const handlePhotoSuccess = (response) => {
  if (response.code === 200 && response.data) {
    ElMessage.success('照片上传成功')
    emergencyForm.value.photoUrl = response.data.url
  }
}

const tabs = [
  { id: 'late', name: '晚归登记' },
  { id: 'emergency', name: '异常情况上报' }
]
const activeTab = ref('late')
const today = new Date().toISOString().split('T')[0]
const submitting = ref(false)

const safetyUploadRef = ref(null)
const lateForm = ref({ time: '', reason: '' })
const outForm = ref({ destination: '', range: [], reason: '' })
const emergencyForm = ref({ type: '', location: '', desc: '', photoUrl: '' })

const records = ref([])
const showDetailDialog = ref(false)
const selectedIncident = ref(null)
const zoomedImageUrl = ref(null)

const selectIncident = (item) => {
  let detail = {}
  try {
    detail = JSON.parse(item.rawDescription)
  } catch (e) {
    detail = { reason: item.rawDescription, desc: item.rawDescription }
  }
  selectedIncident.value = {
    ...item,
    detail
  }
  showDetailDialog.value = true
}

const loadHistory = async () => {
  try {
    const res = await request.get(API)
    if (res.data.code === 200) {
      records.value = res.data.data
        .filter(item => item.studentId === studentId)
        .map(item => {
          let detail = {}
          try { detail = JSON.parse(item.description) } catch (e) {}
          
          return {
            id: item.id,
            type: item.type === 'LATE' ? 'late' : 'emergency',
            typeText: item.type === 'LATE' ? '晚归登记' : '异常上报',
            title: detail.title || item.description,
            status: item.status,
            date: item.reportTime ? item.reportTime.replace('T', ' ').substring(0, 10) : '',
            statusTime: item.status === 'OPEN' ? '待处理' : (item.status === 'PROCESSING' ? '处理中' : '已关闭'),
            rawDescription: item.description,
            photoUrl: detail.photoUrl || ''
          }
        })
    }
  } catch (e) {
    console.error('Failed to load safety history', e)
  }
}

onMounted(() => {
  loadHistory()
})

const statusLabel = (s) => {
  const map = {
    OPEN: '待处理',
    PROCESSING: '处理中',
    CLOSED: '已处理',
    open: '待处理',
    processing: '处理中',
    closed: '已处理'
  }
  return map[s] || s
}

const statusBadgeStyle = (s) => {
  const map = {
    OPEN: 'bg-orange-50 text-orange-600 border border-orange-100/50',
    PROCESSING: 'bg-blue-50 text-blue-600 border border-blue-100/50',
    CLOSED: 'bg-green-50 text-green-700 border border-green-100/50',
    open: 'bg-orange-50 text-orange-600 border border-orange-100/50',
    processing: 'bg-blue-50 text-blue-600 border border-blue-100/50',
    closed: 'bg-green-50 text-green-700 border border-green-100/50'
  }
  return map[s] || 'bg-gray-100 text-gray-500'
}

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

  let title = ''
  if (activeTab.value === 'late') title = `预计 ${lateForm.value.time} 返回 (${lateForm.value.reason.substring(0, 10)}...)`
  else if (activeTab.value === 'out') title = `${outForm.value.destination} (${outForm.value.reason.substring(0, 10)}...)`
  else title = `${emergencyForm.value.location} - ${emergencyForm.value.desc.substring(0, 10)}...`

  try {
    const payload = {
      studentId,
      studentName,
      type: activeTab.value === 'late' ? 'LATE' : emergencyForm.value.type || 'OTHER',
      level: activeTab.value === 'emergency' ? 'HIGH' : 'LOW',
      description: JSON.stringify({
        title,
        ...lateForm.value,
        ...outForm.value,
        ...emergencyForm.value
      })
    }
    const res = await request.post(API, payload)
    if (res.data.code === 200) {
      ElMessage.success('报备信息已成功提交！')
      loadHistory()
      lateForm.value = { time: '', reason: '' }
      outForm.value = { destination: '', range: [], reason: '' }
      emergencyForm.value = { type: '', location: '', desc: '', photoUrl: '' }
      safetyUploadRef.value?.clearFiles()
    } else {
      ElMessage.error(res.data.msg || '提交失败')
    }
  } catch (e) {
    ElMessage.error('请求异常，请稍后重试')
  } finally {
    submitting.value = false
  }
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
.custom-el-select {
  height: 44px !important;
}
.custom-el-select .el-select__wrapper {
  height: 44px !important;
  border-radius: 0.75rem !important;
  border: 1px solid #e5e7eb !important;
  box-shadow: none !important;
  padding: 0 12px !important;
  background-color: #fff !important;
  transition: border-color 0.2s, box-shadow 0.2s !important;
}
.custom-el-select .el-select__wrapper:hover {
  border-color: #9ca3af !important;
}
.custom-el-select .el-select__wrapper.is-focused {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 1px rgba(239, 68, 68, 0.2) !important;
}

/* 全局样式：用于被 Element Plus 渲染并挂载到 body 下的安全详情对话框 */
.custom-safety-overlay {
  background-color: transparent !important;
  z-index: 1001 !important; /* 保证其低于顶层状态栏 z-[9999]，避免覆盖状态栏 */
}

.custom-safety-detail-dialog {
  position: fixed !important;
  top: 96px !important;
  bottom: 40px !important;
  width: 520px !important;
  margin: 0 !important;
  padding: 0 !important;
  display: flex;
  flex-direction: column;
  border-radius: 32px !important; /* 看板圆角处理 */
  overflow: hidden !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;
  overscroll-behavior: contain;
  z-index: 1001 !important;
}

@media (min-width: 768px) {
  .custom-safety-detail-dialog {
    left: calc(50% + 128px) !important; /* 除去左边 256px 导航栏后，在剩下的页面中左右居中 */
    transform: translateX(-50%) !important;
    right: auto !important;
  }
}
@media (max-width: 767px) {
  .custom-safety-detail-dialog {
    left: 50% !important;
    transform: translateX(-50%) !important;
    right: auto !important;
    width: calc(100vw - 32px) !important;
    margin: 0 auto !important;
  }
}
.custom-safety-detail-dialog .el-dialog__header {
  padding: 0 !important;
  margin: 0 !important;
  height: 0;
}
.custom-safety-detail-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  padding: 0 !important;
}
.custom-safety-detail-dialog .el-dialog__headerbtn {
  display: none !important; /* 隐藏默认关闭按钮 */
}
</style>
