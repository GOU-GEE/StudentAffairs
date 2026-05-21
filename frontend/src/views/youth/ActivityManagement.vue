<template>
  <div class="h-full flex flex-col">
    <div class="flex-1 grid grid-cols-1 md:grid-cols-12 gap-6 overflow-hidden">
      <!-- 左侧：发布活动表单 -->
      <div class="md:col-span-7 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] flex flex-col h-full overflow-y-auto custom-scrollbar">
        <div class="flex items-center justify-between mb-5">
          <h3 class="text-lg font-semibold text-on-surface tracking-tight">
            {{ isEditing ? '编辑活动' : '发布活动' }}
          </h3>
        </div>
        <div class="space-y-4 flex-1">
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">标题</label>
            <el-input v-model="form.title" placeholder="例如：红色经典诵读比赛" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">报名时间</label>
              <el-date-picker v-model="enrollTimeRange" type="datetimerange" range-separator="至"
                start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY/MM/DD HH:mm"
                value-format="YYYY-MM-DD HH:mm" class="!w-full" />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">活动时间</label>
              <el-date-picker v-model="activityTimeRange" type="datetimerange" range-separator="至"
                start-placeholder="开始时间" end-placeholder="结束时间" format="YYYY/MM/DD HH:mm"
                value-format="YYYY-MM-DD HH:mm" class="!w-full" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">活动级别</label>
              <el-select v-model="form.level" class="w-full">
                <el-option label="校级" value="校级" />
                <el-option label="院级" value="院级" />
                <el-option label="班级" value="班级" />
              </el-select>
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">参与范围</label>
              <el-input v-model="form.rangeName" placeholder="学校内" />
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">活动请假</label>
              <el-select v-model="form.leaveSupport" class="w-full">
                <el-option label="支持" value="支持" />
                <el-option label="不支持" value="不支持" />
              </el-select>
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">活动地点</label>
              <el-input v-model="form.location" placeholder="例如：学校内" />
            </div>
          </div>
          <!-- 学时类型 / 学时 / 人数上限 三栏 -->
          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">学时类型</label>
              <el-select v-model="form.creditType" class="w-full">
                <el-option label="思想素质" value="思想素质" />
                <el-option label="文艺体育" value="文艺体育" />
                <el-option label="创新创造" value="创新创造" />
                <el-option label="志愿服务" value="志愿服务" />
                <el-option label="劳动教育" value="劳动教育" />
                <el-option label="技能特长" value="技能特长" />
              </el-select>
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">学时</label>
              <el-input-number v-model="form.credits" :min="0" :max="10" :step="1" class="w-full" />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">人数上限</label>
              <div class="flex items-center gap-2">
                <el-input v-model="enrollNumber" type="number" :min="1" :max="9999"
                          :class="{ 'opacity-40 pointer-events-none': isUnlimited }"
                          @focus="isUnlimited = false"
                          class="flex-1" />
                <span class="text-xs text-outline flex-shrink-0">或者</span>
                <button @click="isUnlimited = !isUnlimited"
                        class="px-3 py-1.5 rounded-lg text-xs font-bold transition-all border flex-shrink-0"
                        :class="isUnlimited ? 'bg-emerald-400 text-white border-emerald-400 shadow-sm' : 'bg-gray-100 text-gray-400 border-gray-200'">
                  不限
                </button>
              </div>
            </div>
          </div>
          <!-- 活动详情 + 封面上传 左右排列 -->
          <div class="flex gap-4">
            <div class="flex-1">
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">活动详情</label>
              <el-input v-model="form.description" type="textarea" :rows="6" placeholder="请输入活动详情..." class="custom-textarea !h-[140px]" />
            </div>
            <div class="flex-shrink-0 flex flex-col" style="width: 140px;">
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">封面（选择性上传）</label>
              <el-upload
                action="/api/upload"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleCoverUploadSuccess"
                accept="image/*"
                class="cover-uploader"
              >
                <div class="w-full h-[140px] rounded-xl border-2 border-dashed border-outline-variant/40 hover:border-emerald-400/60 cursor-pointer transition-colors flex flex-col items-center justify-center gap-2 bg-surface-container-low overflow-hidden relative group">
                  <img v-if="form.coverImage" :src="form.coverImage" class="absolute inset-0 w-full h-full object-cover rounded-xl group-hover:brightness-50 transition-all" />
                  <div v-if="form.coverImage" class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                    <span class="text-white font-bold text-xs bg-black/50 px-2 py-1 rounded-lg">重新上传</span>
                  </div>
                  <template v-if="!form.coverImage">
                    <el-icon :size="28" class="text-outline"><Upload /></el-icon>
                    <span class="text-xs text-outline font-medium">点击上传图片</span>
                  </template>
                </div>
              </el-upload>
              <button v-if="form.coverImage" @click="form.coverImage = ''" class="text-xs text-error hover:underline mt-1.5 text-center">清除封面</button>
            </div>
          </div>
        </div>

        <!-- 底部发布按钮区域 (从右上角移动到右下角) -->
        <div class="flex justify-end pt-4 mt-6 border-t border-outline-variant/10 gap-3">
          <button v-if="isEditing" @click="cancelEdit"
                  class="px-5 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-500 rounded-xl font-bold text-sm transition-colors">
            取消编辑
          </button>
          <button @click="publishActivity"
                  class="px-8 py-2.5 bg-emerald-400 text-white rounded-xl font-bold text-sm hover:bg-emerald-500 transition-colors flex items-center gap-2 shadow-md">
            <el-icon><Check /></el-icon>
            {{ isEditing ? '保存修改' : '发布' }}
          </button>
        </div>
      </div>

      <!-- 右侧：已发布活动历史 -->
      <div class="md:col-span-5 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] overflow-hidden flex flex-col h-full">
        <h3 class="text-lg font-semibold text-on-surface tracking-tight mb-5">已发布活动</h3>
        <div class="flex-1 overflow-y-auto space-y-3 custom-scrollbar pr-2">
          <div v-for="a in activities" :key="a.id"
               class="p-4 bg-surface rounded-xl border border-outline-variant/20 hover:border-emerald-500/30 transition-colors cursor-pointer group">
            <div class="flex items-start justify-between gap-2 mb-1.5">
              <div class="flex items-center gap-2 min-w-0">
                <h4 class="font-bold text-sm text-on-surface truncate">{{ a.title }}</h4>
              </div>
              <div class="flex items-center gap-1.5 flex-shrink-0">
                <span class="text-[11px] font-bold px-2 py-0.5 rounded-full" :class="statusClass(a.status)">{{ a.status || '报名中' }}</span>
              </div>
            </div>
            <p class="text-xs text-secondary leading-relaxed line-clamp-2 mb-2">{{ a.description }}</p>
            <div class="flex justify-between items-center text-xs text-outline">
              <span>{{ a.location }}</span>
              <div class="flex gap-2">
                <button @click.stop="startEdit(a)" class="opacity-0 group-hover:opacity-100 font-bold text-blue-600 hover:text-blue-700 transition-colors mr-3">
                  编辑
                </button>
                <button @click.stop="deleteActivity(a)" class="opacity-0 group-hover:opacity-100 font-bold text-red-500 hover:text-red-600 transition-colors">
                  删除
                </button>
              </div>
            </div>
          </div>
          <div v-if="activities.length === 0" class="py-16 flex flex-col items-center text-secondary">
            <el-icon :size="48" class="mb-2 opacity-30"><Calendar /></el-icon>
            <p class="text-sm">暂无已发布的活动</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { Check, Calendar, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const API = '/api/youth/activities'
const uploadHeaders = { Authorization: 'Bearer ' + sessionStorage.getItem('token') }

const activities = ref([])
const isEditing = ref(false)
const editingId = ref(null)

const defaultForm = () => ({
  coverImage: '',
  title: '',
  enrollTime: '',
  timeDetail: '',
  level: '校级',
  rangeName: '',
  leaveSupport: '支持',
  location: '',
  creditType: '思想素质',
  credits: 2,
  enrollLimit: '不限',
  description: '',
  bgGradient: ''
})

const form = ref(defaultForm())
const enrollTimeRange = ref([])
const activityTimeRange = ref([])
const isUnlimited = ref(false)
const enrollNumber = ref(200)

watch(enrollTimeRange, (val) => {
  if (val && val.length === 2) {
    form.value.enrollTime = val[0] + ' ~ ' + val[1]
  } else {
    form.value.enrollTime = ''
  }
})

watch(activityTimeRange, (val) => {
  if (val && val.length === 2) {
    form.value.timeDetail = val[0] + ' ~ ' + val[1]
  } else {
    form.value.timeDetail = ''
  }
})

const loadActivities = async () => {
  try {
    const res = await request.get(API)
    if (res.data.code === 200) {
      activities.value = res.data.data.sort((a, b) => b.id - a.id)
    }
  } catch (e) {
    console.error('Failed to load activities', e)
  }
}

const publishActivity = async () => {
  if (!form.value.title) {
    ElMessage.warning('活动标题为必填')
    return
  }

  // 封面非必选，若未上传封面，则填入后端服务器上的默认图片
  if (!form.value.coverImage) {
    form.value.coverImage = '/uploads/default-cover.jpg'
  }

  const gradients = ['from-emerald-400 to-teal-500', 'from-blue-400 to-indigo-500', 'from-purple-400 to-violet-500', 'from-orange-400 to-amber-500']
  if (!form.value.bgGradient) {
    form.value.bgGradient = gradients[Math.floor(Math.random() * gradients.length)]
  }

  form.value.enrollLimit = isUnlimited.value ? '不限' : String(enrollNumber.value)
  form.value.maxParticipants = isUnlimited.value ? 9999 : enrollNumber.value
  if (!form.value.rangeName) form.value.rangeName = '学校内'

  if (form.value.timeDetail && form.value.timeDetail.length >= 10) {
    form.value.date = form.value.timeDetail.substring(0, 10)
  } else {
    form.value.date = ''
  }
  form.value.status = '报名中'

  try {
    let res
    if (isEditing.value) {
      res = await request.put(`${API}/${editingId.value}`, form.value)
    } else {
      res = await request.post(API, form.value)
    }

    if (res.data.code === 200) {
      ElMessage.success(isEditing.value ? '活动已修改' : '活动已发布')
      cancelEdit()
      await loadActivities()
    } else {
      ElMessage.warning(res.data.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error(isEditing.value ? '修改失败' : '发布失败')
  }
}

const startEdit = (a) => {
  isEditing.value = true
  editingId.value = a.id

  // 回填活动数据，若图片是默认图片，则不用回填，显示为清除状态
  const isDefaultImage = a.coverImage && a.coverImage.includes('default-cover.jpg')
  form.value = {
    coverImage: isDefaultImage ? '' : (a.coverImage || ''),
    title: a.title || '',
    enrollTime: a.enrollTime || '',
    timeDetail: a.timeDetail || '',
    level: a.level || '校级',
    rangeName: a.rangeName || '学校内',
    leaveSupport: a.leaveSupport || '支持',
    location: a.location || '',
    creditType: a.creditType || '思想素质',
    credits: a.credits || 2,
    enrollLimit: a.enrollLimit || '不限',
    description: a.description || '',
    bgGradient: a.bgGradient || ''
  }

  isUnlimited.value = (a.enrollLimit === '不限')
  enrollNumber.value = isUnlimited.value ? 200 : Number(a.enrollLimit || 200)

  if (a.enrollTime && a.enrollTime.includes(' ~ ')) {
    enrollTimeRange.value = a.enrollTime.split(' ~ ')
  } else {
    enrollTimeRange.value = []
  }

  if (a.timeDetail && a.timeDetail.includes(' ~ ')) {
    activityTimeRange.value = a.timeDetail.split(' ~ ')
  } else {
    activityTimeRange.value = []
  }
}

const cancelEdit = () => {
  isEditing.value = false
  editingId.value = null
  form.value = defaultForm()
  enrollTimeRange.value = []
  activityTimeRange.value = []
  isUnlimited.value = false
  enrollNumber.value = 200
}

const deleteActivity = async (a) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除活动“${a.title}”吗？此操作无法撤销。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        buttonSize: 'default'
      }
    )

    const res = await request.delete(`${API}/${a.id}`)
    if (res.data.code === 200) {
      ElMessage.success('活动已删除')
      if (isEditing.value && editingId.value === a.id) {
        cancelEdit()
      }
      await loadActivities()
    } else {
      ElMessage.warning(res.data.msg || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
      ElMessage.error('删除失败')
    }
  }
}

const statusClass = (s) => ({
  '报名中': 'bg-emerald-100 text-emerald-700',
  '进行中': 'bg-blue-100 text-blue-700',
  '已结束': 'bg-gray-100 text-gray-500',
}[s] || 'bg-emerald-100 text-emerald-700')

onMounted(loadActivities)
</script>

<style scoped>
:deep(.custom-textarea .el-textarea__inner) {
  height: 140px !important;
  min-height: 140px !important;
  max-height: 140px !important;
  resize: none;
}
:deep(.cover-uploader) {
  width: 100%;
}
:deep(.cover-uploader .el-upload) {
  width: 100%;
  display: block;
}
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
  border-radius: 99px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
</style>
