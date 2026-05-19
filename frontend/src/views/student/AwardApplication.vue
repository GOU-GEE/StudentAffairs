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
            <div class="flex items-center gap-2 border border-gray-200 rounded-xl px-3 py-2.5 bg-white hover:border-gray-400 transition-colors">
              <el-icon class="text-gray-400" :size="15"><Calendar /></el-icon>
              <el-date-picker
                v-model="form.awardTime"
                type="month"
                placeholder="选择年月"
                format="YYYY-MM"
                value-format="YYYY-MM"
                class="award-date-picker flex-1"
                :style="{ width: '100%' }"
              />
            </div>
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
            <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
              <!-- 已上传预览 -->
              <div
                v-for="(file, index) in uploadedFiles"
                :key="index"
                class="relative aspect-square rounded-xl border border-gray-200 overflow-hidden group"
              >
                <img :src="file.url" class="w-full h-full object-cover" />
                <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
                  <el-icon class="text-white cursor-pointer hover:text-red-400" @click="removeFile(index)" :size="20"><Delete /></el-icon>
                </div>
              </div>

              <!-- 上传按钮 -->
              <button
                v-if="uploadedFiles.length < 5"
                @click="triggerUpload"
                class="aspect-square rounded-xl border-2 border-dashed border-gray-200 hover:border-blue-400 hover:bg-blue-50 transition-all flex flex-col items-center justify-center gap-2 text-gray-400 hover:text-blue-500"
              >
                <el-icon :size="24"><Plus /></el-icon>
                <span class="text-xs font-medium">点击上传</span>
              </button>
            </div>
            <p class="text-[11px] text-gray-400 mt-3 flex items-center gap-1.5">
              <el-icon><InfoFilled /></el-icon>
              支持 JPG, PNG, PDF 格式，单文件不超过 5MB，最多上传 5 份
            </p>
            <input
              type="file"
              ref="fileInput"
              class="hidden"
              accept="image/*,.pdf"
              multiple
              @change="handleFileChange"
            />
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
            class="border rounded-xl p-4 hover:shadow-sm transition-all group"
            :class="item.status === 'PENDING' ? 'border-blue-100 bg-blue-50/30' : 'border-gray-100 bg-white'"
          >
            <div class="flex items-start gap-3">
              <div class="flex-1 min-w-0">
                <div class="flex items-start justify-between gap-2 mb-1">
                  <h4 class="text-sm font-bold text-gray-900 leading-snug truncate">{{ item.awardName }}</h4>
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

                <p v-if="item.reviewComment" class="mt-2 text-[11px] p-2 bg-gray-50 rounded-lg text-gray-600">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Document, Medal, Calendar, ArrowDown, OfficeBuilding,
  Plus, Delete, InfoFilled, Clock, CollectionTag
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/youth'
const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'
const STUDENT_NAME = '张小明'

const history = ref([])
const form = ref({
  awardName: '',
  awardTime: '',
  level: '校级',
  category: '学科竞赛',
  issuer: '',
  description: ''
})

const uploadedFiles = ref([])
const fileInput = ref(null)
const submitting = ref(false)

const loadAwards = async () => {
  try {
    const res = await request.get(`${API}/awards?studentId=${STUDENT_ID}`)
    if (res.data.code === 200) history.value = res.data.data
  } catch (e) { console.error(e) }
}

const triggerUpload = () => {
  fileInput.value.click()
}

const handleFileChange = (e) => {
  const files = Array.from(e.target.files)
  if (uploadedFiles.value.length + files.length > 5) {
    ElMessage.warning('最多只能上传 5 份材料')
    return
  }

  files.forEach(file => {
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.warning(`文件 ${file.name} 超过 5MB`)
      return
    }
    const reader = new FileReader()
    reader.onload = (e) => {
      uploadedFiles.value.push({
        name: file.name,
        url: e.target.result,
        file: file
      })
    }
    reader.readAsDataURL(file)
  })
}

const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1)
}

const submitAward = async () => {
  if (!form.value.awardName.trim()) { ElMessage.warning('请填写获奖名称'); return }
  if (!form.value.awardTime) { ElMessage.warning('请选择获奖时间'); return }
  if (!form.value.level) { ElMessage.warning('请选择获奖级别'); return }
  if (!form.value.category) { ElMessage.warning('请选择获奖类别'); return }
  if (uploadedFiles.value.length === 0) { ElMessage.warning('请上传获奖证明材料'); return }

  submitting.value = true
  try {
    const res = await request.post(`${API}/awards`, {
      studentId: STUDENT_ID,
      studentName: STUDENT_NAME,
      awardName: form.value.awardName,
      level: form.value.level,
      category: form.value.category,
      awardTime: form.value.awardTime,
      description: form.value.description,
      status: 'PENDING'
    })
    if (res.data.code === 200) {
      ElMessage.success('录入申请已提交，请等待辅导员审核')
      loadAwards()
      form.value = { awardName: '', awardTime: '', level: '校级', category: '学科竞赛', issuer: '', description: '' }
      uploadedFiles.value = []
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
  APPROVED: 'bg-green-50 text-green-700',
  REJECTED: 'bg-red-50 text-red-600'
}[s] || 'bg-gray-100 text-gray-500')

onMounted(loadAwards)
</script>

<style scoped>
/* 覆盖 el-date-picker 样式 */
:deep(.award-date-picker .el-input__wrapper) {
  box-shadow: none !important;
  padding: 0;
  background: transparent;
}
:deep(.award-date-picker .el-input__inner) {
  font-size: 0.875rem;
  color: #374151;
}
</style>
