<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
    <!-- 左侧：申请表单 (col-span-2) -->
    <div class="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <!-- 头部标题 -->
      <div class="px-6 pt-6 pb-5 border-b border-gray-100 flex-shrink-0">
        <div>
          <h3 class="text-base font-bold text-gray-900">勤工助学岗位申请</h3>
          <p class="text-xs text-gray-400 mt-0.5">申请校内勤工助学岗位，通过实践锻炼能力并获得相应补助</p>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5 space-y-6">
        <!-- 岗位选择 -->
        <div>
          <div class="mb-4">
            <span class="text-sm font-bold text-gray-700">可选岗位广场</span>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
            <div 
              v-for="job in availableJobs" 
              :key="job.id"
              @click="form.selectedJob = job.id"
              class="p-4 border rounded-xl cursor-pointer transition-all hover:shadow-md relative overflow-hidden group"
              :class="form.selectedJob === job.id ? 'border-[#409eff] bg-[#409eff]/10' : 'border-gray-100 bg-white hover:border-[#409eff]/50'"
            >
              <div v-if="form.selectedJob === job.id" class="absolute top-0 right-0 p-1 bg-[#409eff] text-white rounded-bl-lg">
                <el-icon :size="12"><Check /></el-icon>
              </div>
              <h4 class="text-sm font-bold text-gray-900 mb-1 group-hover:text-[#409eff] transition-colors">{{ job.name }}</h4>
              <p class="text-[11px] text-gray-500 line-clamp-1 mb-2">{{ job.department }}</p>
              <div class="flex items-center justify-between">
                <span class="text-xs font-black text-blue-600">{{ job.salary }}</span>
                <span class="text-[10px] px-1.5 py-0.5 rounded bg-gray-100 text-gray-500">{{ job.type }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 个人申请资料 -->
        <div class="space-y-4">
          <div class="mb-4">
            <span class="text-sm font-bold text-gray-700">申请详情填写</span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-x-5 gap-y-4">
            <!-- 姓名/学号 (只读) -->
            <div>
              <label class="text-sm text-gray-700 font-medium block mb-1.5">姓名</label>
              <input value="张小明" disabled class="w-full border border-gray-100 rounded-xl px-3 py-2.5 text-sm text-gray-400 bg-gray-50 outline-none" />
            </div>
            <div>
              <label class="text-sm text-gray-700 font-medium block mb-1.5">学号</label>
              <input value="202301042" disabled class="w-full border border-gray-100 rounded-xl px-3 py-2.5 text-sm text-gray-400 bg-gray-50 outline-none" />
            </div>

            <!-- 每周可用时长 -->
            <div>
              <label class="text-sm text-gray-700 font-medium block mb-1.5">
                每周预计可用时长 (小时) <span class="text-red-500">*</span>
              </label>
              <div class="relative flex items-center">
                <input 
                  v-model="form.availableHours" 
                  type="number"
                  placeholder="如：12"
                  class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-blue-500 hover:border-gray-400 transition-colors" 
                />
                <span class="absolute right-4 text-xs text-gray-400">小时/周</span>
              </div>
            </div>

            <!-- 困难认定等级 -->
            <div>
              <label class="text-sm text-gray-700 font-medium block mb-1.5">困难认定等级</label>
              <input value="A 档 (特困)" disabled class="w-full border border-gray-100 rounded-xl px-3 py-2.5 text-sm text-blue-600 font-bold bg-blue-50/30 outline-none" />
            </div>
          </div>

          <!-- 相关经验 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">相关经验 / 技能说明</label>
            <textarea 
              v-model="form.experience" 
              rows="3" 
              placeholder="请简述您与该岗位相关的经验，如：熟练使用Office、曾任班委等..."
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-blue-500 hover:border-gray-400 transition-colors resize-none"
            ></textarea>
          </div>

          <!-- 申请理由 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              个人申请陈述 (不少于50字) <span class="text-red-500">*</span>
            </label>
            <textarea 
              v-model="form.statement" 
              rows="5" 
              placeholder="请说明您申请该岗位的动机、家庭情况简述及您的工作态度承诺..."
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-blue-500 hover:border-gray-400 transition-colors resize-none"
            ></textarea>
          </div>
        </div>
      </div>

      <!-- 底部提交栏 -->
      <div class="px-6 py-4 border-t border-gray-100 flex-shrink-0 flex items-center justify-between gap-4">
        <div class="flex items-center gap-2 text-xs text-gray-400">
          <el-icon :size="13"><InfoFilled /></el-icon>
          <span>优先考虑家庭经济困难认定学生，录用后需按时参加岗前培训</span>
        </div>
        <button 
          @click="submitApplication"
          :disabled="submitting"
          class="flex-shrink-0 px-8 py-3 bg-[#409eff] text-white rounded-xl font-bold text-sm hover:bg-[#409eff]/80 transition-colors shadow-lg shadow-[#409eff]/10 disabled:opacity-50 flex items-center gap-2"
        >
          {{ submitting ? '提交中...' : '提交岗位申请' }}
        </button>
      </div>
    </div>

    <!-- 右侧：我的申请记录 (col-span-1) -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <div class="flex items-center gap-3 px-6 pt-6 pb-4 border-b border-gray-100 flex-shrink-0">
        <el-icon class="text-gray-400" :size="18"><Clock /></el-icon>
        <h3 class="text-base font-bold text-gray-900">我的投递记录</h3>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5">
        <div class="space-y-4">
          <div 
            v-for="item in history" 
            :key="item.id"
            class="p-4 border rounded-xl border-gray-100 bg-white hover:border-blue-200 transition-all group cursor-pointer"
          >
            <div class="flex justify-between items-start mb-2">
              <h4 class="text-sm font-bold text-gray-900">{{ item.jobName }}</h4>
              <span 
                class="text-[10px] font-bold px-2 py-0.5 rounded-full"
                :class="statusBadgeStyle(item.status)"
              >{{ statusLabel(item.status) }}</span>
            </div>
            <p class="text-xs text-gray-400 mb-3">{{ item.department }}</p>
            <div class="flex items-center justify-between text-[10px] text-gray-400">
              <span class="flex items-center gap-1"><el-icon><Calendar /></el-icon> {{ item.applyTime }}</span>
              <span @click.stop="ElMessage.info('详情功能开发中')" class="text-blue-600 group-hover:underline cursor-pointer">详情 ></span>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="history.length === 0" class="py-20 flex flex-col items-center text-gray-400">
          <div class="text-5xl mb-4">📪</div>
          <p class="text-sm">暂无投递记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const API = '/api/applications'
const JOBS_API = '/api/financial-aid/jobs'
const studentId = sessionStorage.getItem('userId') || '202301042'
const studentName = '张小明'
import { 
  Document, Check, InfoFilled, Clock, Calendar, ArrowRight 
} from '@element-plus/icons-vue'

const availableJobs = ref([])

const loadJobs = async () => {
  try {
    const res = await request.get(JOBS_API)
    if (res.data.code === 200) {
      availableJobs.value = res.data.data.map(job => ({
        id: job.id,
        name: job.title,
        department: job.department,
        salary: job.hourlyRate ? `${job.hourlyRate}元/时` : '面议',
        type: job.status === 'FULL' ? '已满' : '可申请',
        quota: job.quota,
        currentCount: job.currentCount || 0,
        location: job.location,
        requirements: job.requirements,
        workTime: job.workTime
      }))
    }
  } catch (e) {
    console.error('加载岗位列表失败', e)
    // fallback to default jobs
    availableJobs.value = [
      { id: 1, name: '图书馆管理员', department: '校图书馆·咨询台', salary: '18.5元/时', type: '文化服务' },
      { id: 2, name: '多媒体教室维护', department: '信息技术中心', salary: '20元/时', type: '技术支撑' },
      { id: 3, name: '学工办助理', department: '学生事务管理处', salary: '19元/时', type: '行政辅助' },
      { id: 4, name: '勤工助学基地助理', department: '学生处', salary: '18元/时', type: '行政辅助' }
    ]
  }
}

const form = ref({
  selectedJob: null,
  availableHours: '',
  experience: '',
  statement: ''
})

const submitting = ref(false)

const history = ref([])

const loadHistory = async () => {
  try {
    const res = await request.get(`${API}/student/${studentId}`)
    if (res.data.code === 200) {
      history.value = res.data.data
        .filter(item => item.type === 'WORK_STUDY')
        .map(item => {
          let detail = {}
          try { detail = JSON.parse(item.reason) } catch (e) {}
          return {
            id: item.id,
            jobName: detail.jobName || item.title,
            department: detail.department || '',
            status: item.status,
            applyTime: item.applyTime ? item.applyTime.replace('T', ' ').substring(0, 10) : ''
          }
        })
    }
  } catch (e) {
    console.error('Failed to load work study history', e)
  }
}

onMounted(() => {
  loadJobs()
  loadHistory()
})

const statusLabel = (s) => ({
  PENDING: '审核中',
  APPROVED: '已录用',
  REJECTED: '未录用'
}[s] || s)

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-orange-50 text-orange-600',
  APPROVED: 'bg-green-50 text-green-700',
  REJECTED: 'bg-red-50 text-red-600'
}[s] || 'bg-gray-100 text-gray-500')

const submitApplication = async () => {
  if (!form.value.selectedJob) { ElMessage.warning('请选择想要申请的岗位'); return }
  if (!form.value.availableHours) { ElMessage.warning('请填写每周可用时长'); return }
  if (form.value.statement.length < 50) { ElMessage.warning('申请陈述请不少于50字'); return }

  submitting.value = true
  
  const job = availableJobs.find(j => j.id === form.value.selectedJob)
  
  try {
    const payload = {
      studentId,
      studentName,
      type: 'WORK_STUDY',
      title: `勤工助学申请 - ${job.name}`,
      reason: JSON.stringify({
        jobName: job.name,
        department: job.department,
        availableHours: form.value.availableHours,
        experience: form.value.experience,
        statement: form.value.statement
      })
    }
    const res = await request.post(API, payload)
    if (res.data.code === 200) {
      ElMessage.success('您的岗位申请已成功提交，请关注审核通知！')
      form.value = { selectedJob: null, availableHours: '', experience: '', statement: '' }
      loadHistory()
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

<style scoped>
/* 隐藏数字输入框箭头 */
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>
