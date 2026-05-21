<template>
  <div class="flex flex-col gap-6 h-[calc(100vh-100px)] min-h-[600px] pb-4">
    <!-- 主要内容区：包含顶部筛选和突发事件处置 -->
    <div class="flex-1 bg-white rounded-3xl border border-gray-200 shadow-sm flex flex-col overflow-hidden h-full">
      <!-- 顶部操作与筛选栏 -->
      <div class="px-8 py-6 flex-shrink-0 border-b border-gray-100 flex items-center justify-between bg-gray-50/20">
        <div class="flex items-center gap-8">
          <!-- 调大字号，从 text-2xl 改为 text-3xl，无红色线条 -->
          <h2 class="text-3xl font-black text-gray-900 tracking-tight">突发事件处置</h2>
          
          <!-- 统计与筛选按钮，移动至标题右边，点击可切换筛选 -->
          <div class="flex items-center gap-3">
            <button 
              @click="statusFilter = 'UNHANDLED'"
              class="px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-2 border"
              :class="statusFilter === 'UNHANDLED' 
                ? 'bg-red-50 text-red-600 border-red-200 shadow-sm shadow-red-50' 
                : 'bg-white text-gray-400 border-gray-200 hover:border-gray-300'"
            >
              <span class="w-1.5 h-1.5 rounded-full bg-red-500"></span>
              未处理：{{ unhandledCount }}
            </button>
            <button 
              @click="statusFilter = 'CLOSED'"
              class="px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-2 border"
              :class="statusFilter === 'CLOSED' 
                ? 'bg-green-50 text-green-700 border-green-200 shadow-sm shadow-green-50' 
                : 'bg-white text-gray-400 border-gray-200 hover:border-gray-300'"
            >
              <span class="w-1.5 h-1.5 rounded-full bg-green-500"></span>
              已处理：{{ closedCount }}
            </button>
            <button 
              @click="statusFilter = 'ALL'"
              class="px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-2 border"
              :class="statusFilter === 'ALL' 
                ? 'bg-gray-100 text-gray-800 border-gray-300' 
                : 'bg-white text-gray-400 border-gray-200 hover:border-gray-300'"
            >
              <span class="w-1.5 h-1.5 rounded-full bg-gray-400"></span>
              事件总数：{{ totalCount }}
            </button>
          </div>
        </div>
      </div>

      <!-- 突发事件处置列表 -->
      <div class="flex-1 overflow-y-auto p-8 custom-scrollbar">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div v-for="inc in filteredIncidents" :key="inc.id" 
               @click="selectIncident(inc)"
               class="p-5 border border-gray-150 rounded-2xl hover:border-red-200 hover:shadow-md transition-all group cursor-pointer bg-white flex flex-col justify-between">
            <div>
              <div class="flex justify-between items-start mb-3">
                <div class="flex items-center gap-2">
                  <span class="px-2.5 py-0.5 rounded-full text-[10px] font-extrabold"
                        :class="inc.level === 'HIGH' ? 'bg-red-50 text-red-600 border border-red-100' : 'bg-blue-50 text-blue-600 border border-blue-100'">
                    {{ typeLabel(inc.type) }}
                  </span>
                  <span class="text-xs text-gray-400 font-medium">{{ formatDate(inc.reportTime) }}</span>
                </div>
                
                <!-- 列表里每条记录右上角只显示未处理和已处理状态，删除下拉框 -->
                <span 
                  class="text-[10px] font-black px-2.5 py-0.5 rounded-full border"
                  :class="(inc.status === 'CLOSED' || inc.status === 'closed') ? 'bg-green-50 text-green-700 border-green-200' : 'bg-orange-50 text-orange-600 border-orange-200'"
                >
                  {{ (inc.status === 'CLOSED' || inc.status === 'closed') ? '已处理' : '未处理' }}
                </span>
              </div>
              <h4 class="text-sm font-bold text-gray-900 mb-2 group-hover:text-red-600 transition-colors">
                {{ inc.studentName || '学生' }} <span class="text-xs text-gray-400 font-bold ml-1.5">({{ inc.studentId }})</span>
              </h4>
              <p class="text-xs text-gray-500 mb-4 leading-relaxed line-clamp-2">{{ formatDescription(inc.description) }}</p>
            </div>
            
            <div v-if="inc.handlerName" class="text-xs text-gray-400 font-semibold border-t border-gray-100 pt-2.5 mt-2 flex items-center justify-between">
              <span>处理人: {{ inc.handlerName }}</span>
              <span class="text-[10px] text-gray-300 font-medium">点击查看详情 &rarr;</span>
            </div>
            <div v-else class="text-xs text-gray-300 font-medium border-t border-gray-100 pt-2.5 mt-2 text-right">
              点击查看详情 &rarr;
            </div>
          </div>
          <div v-if="filteredIncidents.length === 0" class="col-span-2 py-24 flex flex-col items-center justify-center text-gray-400">
            <span class="text-5xl mb-4">📜</span>
            <p class="text-sm font-bold">暂无匹配的事件记录</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 局部暗色遮罩层，仅覆盖内容区，避免导航栏变灰 -->
    <div v-if="showDetailDialog" class="fixed top-[56px] left-[256px] right-0 bottom-0 bg-black/45 z-[1000] transition-opacity duration-300" @click="showDetailDialog = false"></div>

    <!-- 详情弹窗 (el-dialog) -->
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

        <!-- 辅导员已处理按钮 -->
        <div v-if="selectedIncident.status !== 'CLOSED' && selectedIncident.status !== 'closed'" class="p-5 border-t border-gray-100 bg-orange-50/20 flex-shrink-0 flex justify-end">
          <button
            @click="markAsProcessed(selectedIncident.id)"
            class="px-6 py-2.5 bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl font-bold text-sm shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2"
          >
            <el-icon><Check /></el-icon> 已处理
          </button>
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning, Close, Check } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/safety'

const incidents = ref([])
const statusFilter = ref('UNHANDLED') // UNHANDLED, CLOSED, ALL
const showDetailDialog = ref(false)
const selectedIncident = ref(null)
const zoomedImageUrl = ref(null)

// Mock 数据：API无数据或报错时使用
const MOCK_INCIDENTS = [
  { id: 101, studentId: '202301042', studentName: '张小明', type: 'LATE', level: 'LOW', description: '{"title":"预计 23:30 返回","time":"23:30","reason":"因图书馆学习过晚，错过了最晚一班校车，需步行回宿舍。"}', status: 'OPEN', reportTime: '2026-05-21 21:15', handlerName: '' },
  { id: 102, studentId: '202301045', studentName: '王五', type: 'safety', level: 'HIGH', description: '{"title":"西二教学楼 3 楼走廊 - 发现消火栓门破裂...","type":"safety","location":"西二教学楼 3 楼走廊","desc":"发现消火栓玻璃门完全破碎，内部管线裸露，存在安全隐患，请及时派人修缮。","photoUrl":""}', status: 'OPEN', reportTime: '2026-05-21 15:51', handlerName: '' },
  { id: 103, studentId: '202301048', studentName: '李四', type: 'medical', level: 'HIGH', description: '{"title":"东区体育馆 - 打篮球时扭伤脚踝...","type":"medical","location":"东区体育馆","desc":"体育馆内打篮球时扭伤脚踝，疼痛剧烈无法站立，已由同学送至校医院拍片。","photoUrl":""}', status: 'CLOSED', reportTime: '2026-05-20 14:20', handlerName: '李辅导员' }
]

const unhandledCount = computed(() => incidents.value.filter(inc => inc.status === 'OPEN' || inc.status === 'PROCESSING' || inc.status === 'open' || inc.status === 'processing').length)
const closedCount = computed(() => incidents.value.filter(inc => inc.status === 'CLOSED' || inc.status === 'closed').length)
const totalCount = computed(() => incidents.value.length)

const filteredIncidents = computed(() => {
  if (statusFilter.value === 'UNHANDLED') {
    return incidents.value.filter(inc => inc.status === 'OPEN' || inc.status === 'PROCESSING' || inc.status === 'open' || inc.status === 'processing')
  } else if (statusFilter.value === 'CLOSED') {
    return incidents.value.filter(inc => inc.status === 'CLOSED' || inc.status === 'closed')
  }
  return incidents.value
})

const loadData = async () => {
  try {
    const res = await request.get(`${API}/incidents`)
    if (res.data.code === 200 && res.data.data?.length > 0) {
      incidents.value = res.data.data
    } else {
      incidents.value = MOCK_INCIDENTS
    }
  } catch (e) {
    console.error('加载突发事件失败，使用 Mock 数据', e)
    incidents.value = MOCK_INCIDENTS
  }
}

const selectIncident = (item) => {
  let detail = {}
  try {
    detail = JSON.parse(item.description)
  } catch (e) {
    detail = { reason: item.description, desc: item.description }
  }
  selectedIncident.value = {
    ...item,
    type: item.type === 'LATE' ? 'late' : 'emergency',
    typeText: item.type === 'LATE' ? '晚归登记' : '异常上报',
    detail,
    photoUrl: detail.photoUrl || ''
  }
  showDetailDialog.value = true
}

const markAsProcessed = async (id) => {
  try {
    const res = await request.put(`${API}/incidents/${id}/status`, { status: 'CLOSED' })
    if (res.data.code === 200) {
      ElMessage.success('事件已标记为已处理')
      showDetailDialog.value = false
      await loadData()
    } else {
      ElMessage.warning(res.data.msg || '操作失败')
    }
  } catch (e) {
    // 兼容Mock数据就地修改状态，以便在前端演示
    const localItem = incidents.value.find(inc => inc.id === id)
    if (localItem) {
      localItem.status = 'CLOSED'
      localItem.handlerName = '李辅导员'
      ElMessage.success('本地 Mock 数据已标记为已处理')
      showDetailDialog.value = false
    } else {
      ElMessage.error('更新状态异常，请稍后重试')
    }
  }
}

const typeLabel = (t) => ({ MENTAL: '心理健康', MEDICAL: '医疗紧急', FIGHT: '打架斗殴', MISSING: '失踪走失', VIOLATION: '违规违纪', LATE: '晚归登记', safety: '校园安全隐患', theft: '财产丢失/被盗', mental: '心理/情绪求助', other: '其他异常' }[t] || t)
const levelLabel = (l) => l === 'HIGH' ? '高级预警' : l === 'MEDIUM' ? '中级预警' : '低级预警'

const formatDate = (s) => {
  if (!s) return ''
  const d = new Date(s)
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const formatDescription = (desc) => {
  if (!desc) return ''
  try {
    const parsed = JSON.parse(desc)
    if (parsed.title && Object.keys(parsed).length <= 10) {
      const parts = []
      if (parsed.title) parts.push(parsed.title)
      if (parsed.location && parsed.location !== 'undefined') parts.push(`地点: ${parsed.location}`)
      if (parsed.desc && parsed.desc !== 'undefined') parts.push(`描述: ${parsed.desc}`)
      if (parsed.reason && parsed.reason !== 'undefined') parts.push(`原因: ${parsed.reason}`)
      if (parsed.time && parsed.time !== 'undefined') parts.push(`时间: ${parsed.time}`)
      return parts.length > 0 ? parts.join(' | ') : desc
    }
    return desc
  } catch (e) {
    return desc
  }
}

onMounted(loadData)
</script>

<style scoped>
/* 滚动条美化 */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 99px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
</style>

<style>
/* 全局样式：用于被 Element Plus 渲染并挂载到 body 下的突发事件详情对话框 */
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
