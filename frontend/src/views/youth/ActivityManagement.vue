<template>
  <div class="h-full flex flex-col">
    <div class="flex-1 grid grid-cols-1 md:grid-cols-12 gap-6 overflow-hidden">
      <!-- 左侧：发布活动表单 -->
      <div class="md:col-span-7 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] flex flex-col">
        <div class="flex items-center justify-between mb-5">
          <h3 class="text-lg font-semibold text-on-surface tracking-tight">发布活动</h3>
          <button @click="publishActivity"
                  class="px-8 py-2 bg-emerald-400 text-white rounded-xl font-bold text-sm hover:bg-emerald-500 transition-colors flex items-center gap-2 shadow-md">
            <el-icon><Check /></el-icon>
            发布
          </button>
        </div>
        <div class="space-y-4">
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
              <el-input v-model="form.description" type="textarea" :rows="6" placeholder="请输入活动详情..." class="!h-[140px]" />
            </div>
            <div class="flex-shrink-0 flex flex-col" style="width: 140px;">
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">封面</label>
              <el-upload
                action="http://localhost:8080/api/upload"
                :headers="{ Authorization: 'Bearer ' + sessionStorage.getItem('token') }"
                :show-file-list="false"
                :on-success="handleCoverUploadSuccess"
                accept="image/*"
                class="cover-uploader"
              >
                <div class="w-full aspect-square rounded-xl border-2 border-dashed border-outline-variant/40 hover:border-emerald-400/60 cursor-pointer transition-colors flex flex-col items-center justify-center gap-2 bg-surface-container-low overflow-hidden relative group">
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
      </div>

      <!-- 右侧：已发布活动历史 -->
      <div class="md:col-span-5 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] overflow-hidden flex flex-col">
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
                <button @click="toggleStatus(a)" class="opacity-0 group-hover:opacity-100 font-semibold text-secondary hover:text-emerald-600 transition-colors">
                  {{ a.status === '已结束' ? '重新开放' : '关闭' }}
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
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const API = '/api/youth/activities'

const activities = ref([])

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
  description: ''
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
      activities.value = res.data.data
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

  if (!form.value.coverImage) {
    form.value.coverImage = ''
  }

  const gradients = ['from-emerald-400 to-teal-500', 'from-blue-400 to-indigo-500', 'from-purple-400 to-violet-500', 'from-orange-400 to-amber-500']
  form.value.bgGradient = gradients[Math.floor(Math.random() * gradients.length)]

  form.value.enrollLimit = isUnlimited.value ? '不限' : String(enrollNumber.value)
  form.value.maxParticipants = isUnlimited.value ? 9999 : enrollNumber.value
  if (!form.value.rangeName) form.value.rangeName = '学校内'

  try {
    const res = await request.post(API, form.value)
    if (res.data.code === 200) {
      ElMessage.success('活动已发布')
      form.value = defaultForm()
      enrollTimeRange.value = []
      activityTimeRange.value = []
      isUnlimited.value = false
      enrollNumber.value = 200
      await loadActivities()
    }
  } catch (e) {
    ElMessage.error('发布失败')
  }
}

const toggleStatus = async (act) => {
  try {
    await request.put(`${API}/${act.id}/toggle-status`)
    ElMessage.success(`活动已${act.status === '已结束' ? '重新开放' : '关闭'}`)
    loadActivities()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleCoverUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.coverImage = 'http://localhost:8080' + response.data.url
    ElMessage.success('封面上传成功')
  }
}

const statusClass = (s) => ({
  '报名中': 'bg-emerald-100 text-emerald-700',
  '进行中': 'bg-blue-100 text-blue-700',
  '已结束': 'bg-gray-100 text-gray-500',
}[s] || 'bg-emerald-100 text-emerald-700')

onMounted(loadActivities)
</script>
