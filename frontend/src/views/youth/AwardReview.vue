<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧学生申请列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <div class="px-3 pt-2 pb-2">
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-outline z-10 flex items-center justify-center"><el-icon :size="14"><Search /></el-icon></span>
          <input v-model="searchQuery" placeholder="搜索获奖名称或姓名..."
                 class="w-full pl-9 pr-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all" />
        </div>
      </div>

      <div class="h-px bg-outline-variant/15"></div>

      <!-- 状态筛选 -->
      <div class="px-2 py-2 flex gap-1.5 flex-wrap">
        <button v-for="tab in statusTabs" :key="tab.key"
          @click="activeFilter = tab.key"
          class="px-3 py-1 rounded-lg text-xs font-semibold transition-colors"
          :class="activeFilter === tab.key ? 'bg-emerald-500 text-white' : 'text-secondary hover:bg-surface-container-high'">
          {{ tab.label }} {{ tab.count }}
        </button>
      </div>

      <div class="h-px bg-outline-variant/15"></div>

      <!-- 申请列表 -->
      <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
        <div v-for="item in filteredApplications" :key="item.id"
             @click="selectApplication(item)"
             class="p-3 rounded-xl cursor-pointer transition-all border flex flex-col gap-1"
             :class="selectedId === item.id ? 'bg-white shadow-sm border-emerald-500/30 ring-1 ring-emerald-500/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
          <div class="font-bold text-sm text-on-surface truncate">{{ item.awardName }}</div>
          <div class="text-xs text-outline">{{ item.studentName }} · {{ item.studentId }}</div>
        </div>

        <div v-if="filteredApplications.length === 0" class="py-10 text-center text-secondary text-sm">
          暂无申请记录
        </div>
      </div>
    </div>

    <!-- 右侧详情面板 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col">
      <transition name="fade" mode="out-in">
        <!-- 空状态 -->
        <div v-if="!selectedId" key="empty" class="flex flex-col items-center justify-center h-full w-full text-secondary">
          <el-icon :size="64" class="mb-4 opacity-20"><Medal /></el-icon>
          <p class="text-lg font-medium text-on-surface">获奖情况审核</p>
          <p class="text-sm mt-2 opacity-80">选择左侧申请查看详情</p>
        </div>

        <!-- 详情面板 -->
        <div v-else key="detail" class="flex flex-col h-full w-full">
          <!-- 头部 -->
          <div class="px-6 py-4 border-b border-outline-variant/30 flex items-center bg-white/40">
            <div>
              <h3 class="font-bold text-base text-on-surface">{{ selectedApp?.studentName }}</h3>
              <p class="text-xs text-secondary mt-0.5">{{ selectedApp?.studentId }}</p>
            </div>
          </div>

          <!-- 详情内容（只读展示） -->
          <div class="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-5 bg-white/20">
            <!-- 获奖名称 + 颁奖单位 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-secondary block mb-1.5">获奖名称</label>
                <p class="text-sm font-semibold text-on-surface leading-snug">{{ selectedApp?.awardName || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary block mb-1.5">颁奖单位</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedApp?.issuer || '--' }}</p>
              </div>
            </div>

            <!-- 获奖时间 + 获奖级别 + 获奖类别 -->
            <div class="grid grid-cols-3 gap-4">
              <div>
                <label class="text-xs font-bold text-secondary block mb-1.5">获奖时间</label>
                <p class="text-sm font-semibold text-on-surface">{{ formatMonth(selectedApp?.awardTime) || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary block mb-1.5">获奖级别</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedApp?.level || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary block mb-1.5">获奖类别</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedApp?.category || '--' }}</p>
              </div>
            </div>

            <!-- 获奖简述 -->
            <div>
              <label class="text-xs font-bold text-secondary block mb-1.5">获奖简述</label>
              <p class="text-sm text-on-surface leading-relaxed whitespace-pre-wrap">{{ selectedApp?.description || '--' }}</p>
            </div>

            <!-- 证明材料 -->
            <div>
              <label class="text-xs font-bold text-secondary block mb-1.5">证明材料</label>
              <div v-if="selectedApp?.proofUrl" class="flex flex-wrap gap-2.5">
                <template v-for="url in (selectedApp.proofUrl || '').split(',')" :key="url">
                  <template v-if="url.trim()">
                    <!-- Image thumbnail -->
                    <div v-if="isImageUrl(url.trim())" class="w-40 h-40 rounded-xl overflow-hidden border border-outline-variant/30 bg-surface-container-lowest/50 flex items-center justify-center cursor-zoom-in hover:shadow-md transition-all">
                      <img :src="url.trim()" alt="证明材料" class="w-full h-full object-cover" @click="zoomedImageUrl = url.trim()">
                    </div>
                    <!-- PDF link -->
                    <a v-else :href="url.trim()" target="_blank" class="flex items-center gap-3 p-3 border border-outline-variant/30 rounded-xl hover:bg-surface-container-low transition-colors w-full group">
                      <el-icon class="text-red-500 text-2xl"><Document /></el-icon>
                      <div class="flex-1 min-w-0">
                        <p class="text-sm font-bold text-gray-800 truncate">证明材料.pdf</p>
                        <p class="text-xs text-gray-400">PDF 格式文件 (点击查看)</p>
                      </div>
                      <el-icon class="text-gray-400 group-hover:text-primary"><Download /></el-icon>
                    </a>
                  </template>
                </template>
              </div>
              <div v-else class="border border-dashed border-outline-variant/30 rounded-xl p-6 flex items-center justify-center bg-surface-container-lowest/50">
                <span class="text-sm text-outline">未上传证明材料</span>
              </div>
            </div>

            <!-- 审核意见 -->
            <div>
              <label class="text-xs font-bold text-secondary block mb-1.5">审核意见（选填）</label>
              <textarea
                :value="reviewComment"
                @input="onCommentInput($event.target.value)"
                :disabled="selectedApp?.status !== 'PENDING'"
                rows="3"
                placeholder="可填写审核意见..."
                class="w-full px-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all resize-none disabled:bg-gray-50 disabled:text-gray-400"
              ></textarea>
            </div>

            <!-- 状态标签 (非 PENDING 状态显示) -->
            <div v-if="selectedApp?.status !== 'PENDING'" class="flex items-center gap-2 pt-2">
              <span class="text-xs text-secondary font-bold">处理结果：</span>
              <span class="text-xs font-bold px-3 py-0.5 rounded-full" :class="statusBadgeStyle(selectedApp.status)">{{ statusLabel(selectedApp.status) }}</span>
            </div>

            <!-- 通过 / 驳回 -->
            <div v-if="selectedApp?.status === 'PENDING'" class="flex justify-end gap-2">
              <button @click="handleReview('REJECTED')"
                class="px-4 py-1.5 rounded-lg text-xs font-bold border border-red-200 text-red-600 hover:bg-red-50 transition-colors">
                驳回
              </button>
              <button @click="handleReview('APPROVED')"
                class="px-4 py-1.5 rounded-lg text-xs font-bold bg-emerald-500 text-white hover:bg-emerald-600 transition-colors shadow-sm">
                通过
              </button>
            </div>
          </div>
        </div>
      </transition>
    </div>

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
import { Search, Medal, Document, Download, Close } from '@element-plus/icons-vue'
import request from '@/utils/request'

const applications = ref([])
const searchQuery = ref('')
const selectedId = ref(null)
const activeFilter = ref('all')
const reviewComment = ref('')
const zoomedImageUrl = ref(null)

const statusTabs = computed(() => [
  { key: 'all', label: '全部', count: applications.value.length },
  { key: 'PENDING', label: '待审核', count: applications.value.filter(a => a.status === 'PENDING').length },
  { key: 'APPROVED', label: '已通过', count: applications.value.filter(a => a.status === 'APPROVED').length },
  { key: 'REJECTED', label: '已驳回', count: applications.value.filter(a => a.status === 'REJECTED').length },
])

const filteredApplications = computed(() => {
  let list = applications.value
  if (activeFilter.value !== 'all') {
    list = list.filter(a => a.status === activeFilter.value)
  }
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(a =>
      a.awardName.toLowerCase().includes(q) ||
      a.studentName.toLowerCase().includes(q) ||
      a.studentId.toLowerCase().includes(q)
    )
  }
  return list
})

const selectedApp = computed(() => {
  return applications.value.find(a => a.id === selectedId.value) || null
})

const formatMonth = (val) => {
  if (!val) return ''
  const parts = val.split('-')
  if (parts.length < 2) return val
  const [y, m] = parts
  return `${y}年${m}月`
}

const selectApplication = (item) => {
  selectedId.value = item.id
  reviewComment.value = item.reviewComment || ''
}

const onCommentInput = (val) => {
  reviewComment.value = val
  const app = applications.value.find(a => a.id === selectedId.value)
  if (app) app.reviewComment = val
}

const isImageUrl = (url) => {
  if (!url) return false
  const cleanUrl = url.trim().split('?')[0]
  const ext = cleanUrl.substring(cleanUrl.lastIndexOf('.')).toLowerCase()
  return ['.jpg', '.jpeg', '.png', '.gif', '.webp'].some(x => ext === x)
}

const loadAwards = async () => {
  try {
    const res = await request.get('/api/youth/awards')
    if (res.data.code === 200 && Array.isArray(res.data.data)) {
      applications.value = res.data.data.sort((a, b) => b.id - a.id)
    }
  } catch (e) {
    console.error('Failed to load awards', e)
  }
}

const handleReview = async (status) => {
  if (!selectedId.value) return
  const comment = reviewComment.value || (status === 'APPROVED' ? '审核通过。' : '材料不符合要求，请重新提交。')
  try {
    const res = await request.put(`/api/youth/awards/${selectedId.value}/review`, {
      status: status,
      reviewComment: comment
    })
    if (res.data.code === 200) {
      ElMessage.success(status === 'APPROVED' ? '已通过审核' : '已驳回申请')
      await loadAwards()
      const app = applications.value.find(a => a.id === selectedId.value)
      if (app) {
        reviewComment.value = app.reviewComment || ''
      } else {
        selectedId.value = null
      }
    }
  } catch (e) {
    ElMessage.error('审核提交失败')
  }
}

const statusLabel = (s) => ({
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已驳回'
}[s] || s)

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-orange-50 text-orange-600 border border-orange-100/50',
  APPROVED: 'bg-emerald-50 text-emerald-700 border border-emerald-100/50',
  REJECTED: 'bg-red-50 text-red-600 border border-red-100/50'
}[s] || 'bg-gray-100 text-gray-500')

onMounted(loadAwards)
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
