<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
    <!-- 左侧：录入表单 -->
    <div class="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <!-- 头部标题 -->
      <div class="px-6 pt-6 pb-5 border-b border-gray-100 flex-shrink-0">
        <div>
          <h3 class="text-base font-bold text-gray-900">获奖情况录入</h3>
          <p class="text-xs text-gray-400 mt-0.5">上传您的获奖证明材料，提交至辅导员审核后将计入个人档案</p>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5">
        <!-- 获奖基本信息 -->
        <div class="flex items-center gap-2 mb-4">
          <el-icon class="text-gray-400" :size="15"><Document /></el-icon>
          <span class="text-sm font-bold text-gray-700">获奖基本信息</span>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-5">
          <!-- 获奖名称 -->
          <div class="md:col-span-2">
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              获奖名称 <span class="text-red-500">*</span>
            </label>
            <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2.5 bg-white hover:border-gray-400 focus-within:border-gray-500 transition-colors">
              <el-icon class="text-gray-400 flex-shrink-0" :size="15"><Medal /></el-icon>
              <input
                v-model="form.awardName"
                class="flex-1 text-sm outline-none bg-transparent placeholder-gray-300 text-gray-700"
                placeholder="如：2024年全国大学生数学建模竞赛省级一等奖"
              />
            </div>
          </div>

          <!-- 获奖时间 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              获奖时间 <span class="text-red-500">*</span>
            </label>
            <el-date-picker
              v-model="form.awardTime"
              type="month"
              placeholder="选择年月"
              format="YYYY-MM"
              value-format="YYYY-MM"
              class="award-date-picker w-full"
            />
          </div>

          <!-- 获奖级别 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              获奖级别 <span class="text-red-500">*</span>
            </label>
            <div class="relative flex items-center">
              <select
                v-model="form.level"
                class="w-full appearance-none border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors cursor-pointer pr-10 block"
              >
                <option value="" disabled>请选择获奖级别</option>
                <option value="国家级">国家级</option>
                <option value="省级">省级</option>
                <option value="市级">市级</option>
                <option value="校级">校级</option>
                <option value="院级">院级</option>
              </select>
              <div class="absolute right-3 top-0 bottom-0 flex items-center pointer-events-none">
                <el-icon class="text-gray-400" :size="14"><ArrowDown /></el-icon>
              </div>
            </div>
          </div>

          <!-- 获奖类别 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              获奖类别 <span class="text-red-500">*</span>
            </label>
            <div class="relative flex items-center">
              <select
                v-model="form.category"
                class="w-full appearance-none border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors cursor-pointer pr-10 block"
              >
                <option value="" disabled>请选择获奖类别</option>
                <option value="学科竞赛">学科竞赛</option>
                <option value="文体活动">文体活动</option>
                <option value="社会实践">社会实践</option>
                <option value="荣誉称号">荣誉称号</option>
                <option value="学术科研">学术科研</option>
                <option value="其他">其他</option>
              </select>
              <div class="absolute right-3 top-0 bottom-0 flex items-center pointer-events-none">
                <el-icon class="text-gray-400" :size="14"><ArrowDown /></el-icon>
              </div>
            </div>
          </div>

          <!-- 颁奖单位 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">颁奖单位</label>
            <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2.5 bg-white hover:border-gray-400 focus-within:border-gray-500 transition-colors">
              <el-icon class="text-gray-400 flex-shrink-0" :size="15"><OfficeBuilding /></el-icon>
              <input
                v-model="form.issuer"
                class="flex-1 text-sm outline-none bg-transparent placeholder-gray-300 text-gray-700"
                placeholder="如：教育部、西华师范大学"
              />
            </div>
          </div>

          <!-- 获奖简述 -->
          <div class="md:col-span-2">
            <label class="text-sm text-gray-700 font-medium block mb-1.5">获奖简述</label>
            <textarea
              v-model="form.description"
              rows="3"
              placeholder="简要说明获奖背景、排名或项目内容..."
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors resize-none"
            ></textarea>
          </div>

          <!-- 证明材料上传 -->
          <div class="md:col-span-2 mt-2">
            <label class="text-sm text-gray-700 font-medium block mb-2">
              证明材料（证书照片/扫描件）<span class="text-red-500">*</span>
            </label>
            <el-upload
              action="/api/upload"
              :headers="uploadHeaders"
              v-model:file-list="fileList"
              list-type="picture-card"
              :limit="5"
              :on-exceed="handleExceed"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              accept="image/*,.pdf"
            >
              <el-icon><Plus /></el-icon>
              <template #file="{ file }">
                <div class="relative w-full h-full flex items-center justify-center bg-gray-50 border border-gray-100 rounded-xl overflow-hidden group">
                  <!-- Check if it is a PDF file -->
                  <template v-if="file.name.toLowerCase().endsWith('.pdf') || (file.url && file.url.toLowerCase().split('?')[0].endsWith('.pdf'))">
                    <div class="flex flex-col items-center justify-center p-2 text-center h-full w-full bg-red-50/10">
                      <div class="w-10 h-10 rounded-lg bg-red-50 flex items-center justify-center text-red-500 mb-1.5 shadow-sm">
                        <el-icon :size="22"><Document /></el-icon>
                      </div>
                      <span class="text-[10px] text-gray-500 font-bold px-1 truncate w-full" :title="file.name">{{ file.name }}</span>
                    </div>
                  </template>
                  <!-- Otherwise, treat as an image -->
                  <template v-else>
                    <img class="w-full h-full object-cover" :src="file.url" alt="证明材料" />
                  </template>
                  
                  <!-- Hover Action Mask Overlay -->
                  <div class="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 flex items-center justify-center gap-4 transition-opacity duration-200 z-10">
                    <!-- Zoom button for images only -->
                    <span v-if="!file.name.toLowerCase().endsWith('.pdf') && !(file.url && file.url.toLowerCase().split('?')[0].endsWith('.pdf'))" 
                          class="text-white text-lg cursor-pointer hover:text-blue-400 transition-colors" 
                          @click="zoomedImageUrl = file.url">
                      <el-icon><ZoomIn /></el-icon>
                    </span>
                    <!-- Delete button -->
                    <span class="text-white text-lg cursor-pointer hover:text-red-400 transition-colors" 
                          @click="handleRemoveFile(file)">
                      <el-icon><Delete /></el-icon>
                    </span>
                  </div>
                </div>
              </template>
            </el-upload>
            <p class="text-[11px] text-gray-400 mt-3 flex items-center gap-1.5">
              <el-icon><InfoFilled /></el-icon>
              支持 JPG, PNG, PDF 格式，单文件不超过 5MB，最多上传 5 份
            </p>
          </div>
        </div>
      </div>

      <!-- 底部：提示 + 提交按钮 -->
      <div class="px-6 py-4 border-t border-gray-100 flex-shrink-0 flex items-center justify-between gap-4">
        <div class="flex items-center gap-2 text-xs text-gray-400">
          <el-icon :size="13"><InfoFilled /></el-icon>
          <span>提交后请等待辅导员审核，审核通过后将正式同步至个人荣誉档案</span>
        </div>
        <button
          @click="submitAward"
          :disabled="submitting"
          class="flex-shrink-0 px-8 py-2.5 bg-[#409eff] text-white rounded-xl font-bold text-sm hover:bg-[#409eff]/80 transition-colors shadow-sm disabled:opacity-50 flex items-center gap-2"
        >
          {{ submitting ? '正在提交...' : '提交录入' }}
        </button>
      </div>
    </div>

    <!-- 右侧：录入历史 -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <!-- 卡片标题 -->
      <div class="flex items-center gap-3 px-6 pt-6 pb-4 border-b border-gray-100 flex-shrink-0">
        <el-icon class="text-gray-400" :size="18"><Clock /></el-icon>
        <h3 class="text-base font-bold text-gray-900">录入历史</h3>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5">
        <div class="space-y-4">
          <div
            v-for="(item, i) in history"
            :key="item.id"
            @click="selectAwardItem(item)"
            class="border rounded-xl p-4 hover:shadow-sm hover:border-blue-200 transition-all group cursor-pointer"
            :class="item.status === 'PENDING' ? 'border-blue-100 bg-blue-50/30' : 'border-gray-100 bg-white'"
          >
            <div class="flex items-start gap-3">
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between gap-2 mb-1">
                  <h4 class="text-sm font-bold text-gray-900 leading-snug truncate group-hover:text-blue-500 transition-colors">{{ item.awardName }}</h4>
                  <span
                    class="flex-shrink-0 text-[10px] font-bold px-2 py-0.5 rounded-full"
                    :class="statusBadgeStyle(item.status)"
                  >{{ statusLabel(item.status) }}</span>
                </div>

                <div class="flex flex-col gap-1 text-[11px] text-gray-400">
                  <div class="flex items-center gap-1">
                    <el-icon :size="10"><Calendar /></el-icon>
                    <span>获奖时间：{{ item.awardTime }}</span>
                  </div>
                  <div class="flex items-center gap-1">
                    <el-icon :size="10"><CollectionTag /></el-icon>
                    <span>级别：{{ item.level }}</span>
                  </div>
                </div>

                <p v-if="item.reviewComment" class="mt-2 text-[11px] p-2 bg-gray-50 rounded-lg text-gray-600 truncate">
                  <span class="font-bold">审核意见：</span>{{ item.reviewComment }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div v-if="history.length === 0" class="py-16 flex flex-col items-center text-gray-400">
          <span class="text-4xl mb-3">📜</span>
          <p class="text-sm">暂无录入记录</p>
        </div>
      </div>
    </div>

    <!-- 局部暗色遮罩层，仅覆盖内容区，避免导航栏变灰 -->
    <div v-if="showDetailDialog" class="fixed top-[56px] left-[256px] right-0 bottom-0 bg-black/45 z-[1000] transition-opacity duration-300" @click="showDetailDialog = false"></div>

    <!-- 获奖详情滑出抽屉 (el-dialog) -->
    <el-dialog
      v-model="showDetailDialog"
      title=""
      modal-class="custom-award-overlay"
      class="custom-award-detail-dialog"
      :show-close="false"
      :modal="false"
      append-to-body
      lock-scroll
      destroy-on-close
    >
      <div v-if="selectedAward" class="flex flex-col h-full bg-white relative">
        <!-- 抽屉头部 -->
        <div class="px-6 py-5 border-b border-gray-100 flex items-center justify-between flex-shrink-0">
          <span class="text-base font-bold text-gray-900">详情信息</span>
          <button @click="showDetailDialog = false" class="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 text-gray-500 hover:text-gray-700 flex items-center justify-center transition-colors focus:outline-none">
            <el-icon :size="16"><Close /></el-icon>
          </button>
        </div>

        <!-- 滚动内容区 -->
        <div class="flex-1 overflow-y-auto px-8 py-6 space-y-5 custom-scrollbar text-sm text-gray-700 leading-relaxed bg-white">
          <div class="space-y-4">
            <div>
              <div class="text-gray-400 text-xs font-semibold">获奖名称：</div>
              <div class="text-gray-800 mt-1 font-bold text-base leading-snug">{{ selectedAward.awardName }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">获奖时间：</div>
              <div class="text-gray-800 mt-1 font-medium">{{ selectedAward.awardTime }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">获奖级别：</div>
              <div class="text-gray-800 mt-1 font-medium">
                <span class="px-2.5 py-0.5 bg-blue-50 text-blue-600 rounded-full text-xs font-semibold border border-blue-100/50">{{ selectedAward.level }}</span>
              </div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">获奖类别：</div>
              <div class="text-gray-800 mt-1 font-medium">
                <span class="px-2.5 py-0.5 bg-purple-50 text-purple-600 rounded-full text-xs font-semibold border border-purple-100/50">{{ selectedAward.category }}</span>
              </div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">颁奖单位：</div>
              <div class="text-gray-800 mt-1 font-medium">{{ selectedAward.issuer || '暂无颁奖单位' }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">获奖简述：</div>
              <div class="text-gray-800 mt-1 font-medium whitespace-pre-wrap leading-relaxed">{{ selectedAward.description || '暂无描述' }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold mb-2">证明材料 (点击可放大)：</div>
              <div v-if="selectedAward.proofUrl" class="flex flex-wrap gap-2.5">
                <template v-for="url in (selectedAward.proofUrl || '').split(',')" :key="url">
                  <template v-if="url.trim()">
                    <!-- Image proof -->
                    <div v-if="isImageUrl(url.trim())" class="w-40 h-40 rounded-xl overflow-hidden border border-gray-100 bg-gray-50 flex items-center justify-center cursor-zoom-in hover:shadow-md transition-all">
                      <img :src="url.trim()" alt="证明材料" class="w-full h-full object-cover" @click="zoomedImageUrl = url.trim()">
                    </div>
                    <!-- PDF proof -->
                    <a v-else :href="url.trim()" target="_blank" class="flex items-center gap-3 p-3 border border-gray-200 rounded-xl hover:bg-gray-50 transition-colors w-full group">
                      <el-icon class="text-red-500 text-2xl"><Document /></el-icon>
                      <div class="flex-1 min-w-0">
                        <p class="text-sm font-bold text-gray-800 truncate">点击查看 PDF 证明材料</p>
                        <p class="text-xs text-gray-400">PDF 格式文件</p>
                      </div>
                      <el-icon class="text-gray-400 group-hover:text-primary"><Download /></el-icon>
                    </a>
                  </template>
                </template>
              </div>
              <div v-else class="text-gray-400 text-xs font-medium">无证明材料</div>
            </div>
            
            <!-- 审核状态与意见 -->
            <div class="border-t border-gray-100 pt-4 mt-2">
              <div class="text-gray-400 text-xs font-semibold mb-1">审核状态：</div>
              <div class="flex items-center gap-2">
                <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="statusBadgeStyle(selectedAward.status)">{{ statusLabel(selectedAward.status) }}</span>
              </div>
            </div>
            
            <div v-if="selectedAward.reviewComment" class="p-3 bg-gray-50 rounded-xl border border-gray-100">
              <div class="text-gray-500 text-xs font-semibold">审核意见：</div>
              <div class="text-gray-700 mt-1 font-medium">{{ selectedAward.reviewComment }}</div>
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
import { ElMessage } from 'element-plus'
import {
  Document, Medal, Calendar, ArrowDown, OfficeBuilding,
  Plus, InfoFilled, Clock, CollectionTag, Close, Download,
  Delete, ZoomIn
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/youth'
const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'
const STUDENT_NAME = '张小明'
const uploadHeaders = { Authorization: 'Bearer ' + sessionStorage.getItem('token') }

const history = ref([])
const form = ref({
  awardName: '',
  awardTime: '',
  level: '校级',
  category: '学科竞赛',
  issuer: '',
  description: ''
})

const fileList = ref([])
const submitting = ref(false)

const showDetailDialog = ref(false)
const selectedAward = ref(null)
const zoomedImageUrl = ref(null)

const selectAwardItem = (item) => {
  selectedAward.value = item
  showDetailDialog.value = true
}

const isImageUrl = (url) => {
  if (!url) return false
  const cleanUrl = url.trim().split('?')[0]
  const ext = cleanUrl.substring(cleanUrl.lastIndexOf('.')).toLowerCase()
  return ['.jpg', '.jpeg', '.png', '.gif', '.webp'].some(x => ext === x)
}

const loadAwards = async () => {
  try {
    const res = await request.get(`${API}/awards?studentId=${STUDENT_ID}`)
    if (res.data.code === 200 && Array.isArray(res.data.data)) {
      // Sort by id descending so the latest submissions are at the top
      history.value = res.data.data.sort((a, b) => b.id - a.id)
    }
  } catch (e) { console.error(e) }
}

const handleExceed = () => ElMessage.warning('最多只能上传 5 份材料')

const handleUploadSuccess = (response, uploadFile, uploadFiles) => {
  if (response.code === 200) {
    ElMessage.success('材料上传成功')
    uploadFile.url = response.data.url
    uploadFile.uploadedUrl = response.data.url
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const handleRemove = (uploadFile, uploadFiles) => {
  ElMessage.info('已移除一份材料')
}

const handleRemoveFile = (file) => {
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index !== -1) {
    fileList.value.splice(index, 1)
    ElMessage.info('已移除一份材料')
  }
}

const extractFileUrl = (f) => {
  if (!f) return ''
  if (f.uploadedUrl) return f.uploadedUrl
  if (f.response) {
    let resp = f.response
    if (typeof resp === 'string') {
      try { resp = JSON.parse(resp) } catch (e) {}
    }
    if (resp && typeof resp === 'object') {
      if (resp.code === 200 && resp.data && resp.data.url) return resp.data.url
      if (resp.data && typeof resp.data === 'string') return resp.data
      if (resp.url) return resp.url
      if (resp.data && resp.data.url) return resp.data.url
    }
  }
  if (f.url && !f.url.startsWith('blob:') && !f.url.startsWith('data:')) {
    if (f.url.includes('/api/uploads/') || f.url.startsWith('http') || f.url.startsWith('/uploads/')) {
      return f.url
    }
  }
  return ''
}

const submitAward = async () => {
  if (submitting.value) return
  if (!form.value.awardName.trim()) { ElMessage.warning('请填写获奖名称'); return }
  if (!form.value.awardTime) { ElMessage.warning('请选择获奖时间'); return }
  if (!form.value.level) { ElMessage.warning('请选择获奖级别'); return }
  if (!form.value.category) { ElMessage.warning('请选择获奖类别'); return }
  
  // Extract and filter uploaded URLs cleanly
  const uploadedUrls = fileList.value
    .map(extractFileUrl)
    .filter(url => url !== '')

  if (uploadedUrls.length === 0) {
    ElMessage.warning('请上传并等待证明材料上传完成')
    return
  }

  submitting.value = true
  const proofUrl = uploadedUrls.join(',')
  try {
    const res = await request.post(`${API}/awards`, {
      studentId: STUDENT_ID,
      studentName: STUDENT_NAME,
      awardName: form.value.awardName,
      level: form.value.level,
      category: form.value.category,
      awardTime: form.value.awardTime,
      issuer: form.value.issuer,
      proofUrl: proofUrl,
      description: form.value.description,
      status: 'PENDING'
    })
    if (res.data.code === 200) {
      ElMessage.success('录入申请已提交，请等待辅导员审核')
      loadAwards()
      form.value = { awardName: '', awardTime: '', level: '校级', category: '学科竞赛', issuer: '', description: '' }
      fileList.value = []
    }
  } catch (e) { ElMessage.error('提交失败') }
  submitting.value = false
}

const statusLabel = (s) => ({
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已驳回'
}[s] || s)

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-blue-50 text-blue-600',
  APPROVED: 'bg-green-50 text-green-700 border border-green-100/50',
  REJECTED: 'bg-red-50 text-red-600 border border-red-100/50'
}[s] || 'bg-gray-100 text-gray-500')

onMounted(loadAwards)
</script>

<style scoped>
/* 覆盖 el-date-picker 样式 */
:deep(.award-date-picker.el-date-editor.el-input) {
  height: 44px;
  width: 100%;
}
:deep(.award-date-picker .el-input__wrapper) {
  border: 1px solid #e5e7eb !important; /* border-gray-200 */
  box-shadow: none !important;
  border-radius: 0.75rem !important; /* rounded-xl */
  padding-left: 0.75rem !important;
  padding-right: 0.75rem !important;
  background-color: #ffffff;
  transition: border-color 0.2s;
  height: 44px !important;
  box-sizing: border-box;
}
:deep(.award-date-picker .el-input__wrapper:hover) {
  border-color: #9ca3af !important; /* border-gray-400 */
}
:deep(.award-date-picker .el-input__wrapper.is-active),
:deep(.award-date-picker .el-input__wrapper.is-focus) {
  border-color: #3b82f6 !important; /* focus border blue-500 */
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.2) !important;
}
:deep(.award-date-picker .el-input__inner) {
  font-size: 0.875rem;
  color: #374151;
  font-family: inherit;
}
</style>

<style>
/* 全局样式：用于被 Element Plus 渲染并挂载到 body 下的获奖详情对话框 */
.custom-award-overlay {
  background-color: transparent !important;
  z-index: 1001 !important; /* 保证其低于顶层状态栏 z-[9999]，避免覆盖状态栏 */
}

.custom-award-detail-dialog {
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
  .custom-award-detail-dialog {
    left: calc(50% + 128px) !important; /* 除去左边 256px 导航栏后，在剩下的页面中左右居中 */
    transform: translateX(-50%) !important;
    right: auto !important;
  }
}
@media (max-width: 767px) {
  .custom-award-detail-dialog {
    left: 50% !important;
    transform: translateX(-50%) !important;
    right: auto !important;
    width: calc(100vw - 32px) !important;
    margin: 0 auto !important;
  }
}
.custom-award-detail-dialog .el-dialog__header {
  padding: 0 !important;
  margin: 0 !important;
  height: 0;
}
.custom-award-detail-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  padding: 0 !important;
}
.custom-award-detail-dialog .el-dialog__headerbtn {
  display: none !important; /* 隐藏默认关闭按钮 */
}
</style>
