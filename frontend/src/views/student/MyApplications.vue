<template>
  <div class="h-full">
    <!-- 请假报备 -->
    <div v-if="activeTab === 'leave'" class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 左: 请假申请表单 -->
      <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 p-6 shadow-sm">
        <h3 class="text-base font-bold text-on-surface mb-5">填写请假申请</h3>
        <div class="space-y-4">
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">请假类型</label>
            <div class="grid grid-cols-3 gap-2">
              <button v-for="lt in leaveTypes" :key="lt.value" @click="leaveForm.leaveType = lt.value"
                      class="py-2.5 rounded-xl text-xs font-bold border transition-all text-center"
                      :class="leaveForm.leaveType === lt.value ? 'bg-primary text-on-primary-fixed border-primary' : 'bg-surface border-outline-variant/30 text-secondary hover:border-primary/30'">
                {{ lt.label }}
              </button>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">开始日期</label>
              <el-date-picker v-model="leaveForm.startDate" type="date" placeholder="选择开始日期" style="width:100%" />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">结束日期</label>
              <el-date-picker v-model="leaveForm.endDate" type="date" placeholder="选择结束日期" style="width:100%" />
            </div>
          </div>

          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">前往地点</label>
            <el-input v-model="leaveForm.destination" placeholder="如: 广州南站 / 家庭住址" />
          </div>

          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">请假事由（详细）</label>
            <el-input v-model="leaveForm.reason" type="textarea" :rows="4" placeholder="请详细说明请假原因，辅导员将依据此进行审批..." />
          </div>

          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">紧急联系方式</label>
            <el-input v-model="leaveForm.contact" placeholder="假期期间可联系到您的手机号" />
          </div>

          <div class="bg-orange-50 border border-orange-200 rounded-xl p-3 flex gap-2">
            <el-icon class="text-orange-500 mt-0.5 flex-shrink-0"><Warning /></el-icon>
            <p class="text-xs text-orange-700 leading-relaxed">请假批准后方可离校，请确保返校后及时向辅导员进行<strong>销假</strong>。超时未归视为旷课处理。</p>
          </div>

          <button @click="submitLeave" :disabled="submitting"
                  class="w-full py-3 bg-primary text-on-primary-fixed rounded-xl font-bold text-sm hover:bg-primary-fixed transition-colors flex items-center justify-center gap-2 shadow-md disabled:opacity-50">
            {{ submitting ? '提交中...' : '提交请假申请' }}
          </button>
        </div>
      </div>

      <!-- 右: 历史请假记录 -->
      <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 p-6 shadow-sm flex flex-col max-h-[80vh] overflow-hidden">
        <h3 class="text-base font-bold text-on-surface mb-5 flex-shrink-0">历史请假记录</h3>
        <div class="flex-1 overflow-y-auto space-y-3 custom-scrollbar">
          <div v-for="(r, i) in leaveHistory" :key="i"
               class="p-4 rounded-xl border transition-colors"
               :class="r.status === 'ON_LEAVE' ? 'border-green-300 bg-green-50' : r.status === 'PENDING_RETURN' ? 'border-orange-300 bg-orange-50' : r.status === 'PENDING' ? 'border-blue-200 bg-blue-50' : 'border-outline-variant/20 bg-surface'">
            <div class="flex justify-between items-start mb-2">
              <div>
                <span class="text-sm font-bold text-on-surface">{{ leaveTypeLabel(r.leaveType) }}</span>
                <p class="text-xs text-secondary mt-0.5">{{ r.startDate }} 至 {{ r.endDate }}</p>
              </div>
              <span class="text-xs font-bold px-2.5 py-1 rounded-full border"
                    :class="leaveStatusStyle(r.status)">{{ leaveStatusLabel(r.status) }}</span>
            </div>
            <p class="text-xs text-secondary">前往: {{ r.destination }}</p>
            <p class="text-xs text-secondary mt-1 leading-relaxed">{{ r.reason }}</p>
            <div v-if="r.reviewComment" class="mt-2 pt-2 border-t border-outline-variant/20">
              <p class="text-xs text-on-surface font-medium">辅导员批注: <span class="text-secondary font-normal">{{ r.reviewComment }}</span></p>
            </div>
            <button v-if="r.status === 'PENDING_RETURN'" @click="checkIn(r.id)"
                    class="mt-3 w-full py-2 bg-orange-500 text-white rounded-lg text-xs font-bold hover:bg-orange-600 transition-colors">
              确认销假
            </button>
          </div>
          <div v-if="leaveHistory.length === 0" class="py-12 flex flex-col items-center text-secondary">
            <span class="text-4xl mb-2">📋</span>
            <p class="text-sm">暂无请假记录</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 奖助学金申请 -->
    <div v-else-if="activeTab === 'scholarship'" class="overflow-y-auto">
      <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 p-7 shadow-sm">
        <div class="flex items-center gap-3 mb-6">
          <div class="w-10 h-10 bg-yellow-100 rounded-xl flex items-center justify-center text-xl">🏆</div>
          <div>
            <h3 class="text-base font-bold text-on-surface">奖助学金申请</h3>
            <p class="text-sm text-secondary">请如实填写申请信息，材料将提交辅导员审核</p>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div class="space-y-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">申请奖助类别</label>
              <el-select v-model="scholarForm.scholarType" placeholder="请选择奖助类型" style="width:100%">
                <el-option label="国家奖学金（成绩全院前 0.5%）" value="nat_scholarship" />
                <el-option label="国家励志奖学金（成绩前 3%，有困难认定）" value="nat_incentive" />
                <el-option label="国家助学金（家庭经济困难）" value="nat_aid" />
                <el-option label="学校奖学金（成绩优秀）" value="school_scholarship" />
                <el-option label="学校助学金（生活临时困难）" value="school_aid" />
              </el-select>
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">学号</label>
              <el-input model-value="202301042" disabled />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">姓名</label>
              <el-input model-value="张小明" disabled />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">所在班级</label>
              <el-input model-value="计算机科学2301班" disabled />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">本学期加权平均成绩</label>
              <el-input v-model="scholarForm.gpa" placeholder="如: 88.5" />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">专业排名</label>
              <div class="flex gap-3 items-center">
                <el-input v-model="scholarForm.rank" placeholder="名" class="flex-1" />
                <span class="text-secondary text-sm flex-shrink-0">/ 共</span>
                <el-input v-model="scholarForm.total" placeholder="人" class="flex-1" />
              </div>
            </div>
          </div>

          <div class="space-y-4">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">家庭年收入（元）</label>
              <el-input v-model="scholarForm.familyIncome" placeholder="如: 50000" />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">困难认定等级</label>
              <el-select v-model="scholarForm.povertyLevel" style="width:100%">
                <el-option label="无困难认定" value="none" />
                <el-option label="A 档（特困）" value="A" />
                <el-option label="B 档（困难）" value="B" />
                <el-option label="C 档（一般困难）" value="C" />
              </el-select>
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">在校获得荣誉</label>
              <el-input v-model="scholarForm.honors" type="textarea" :rows="3" placeholder="如: 2024年校级三好学生&#10;全国大学生数学竞赛二等奖" />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">志愿服务时长</label>
              <el-input v-model="scholarForm.volunteer" placeholder="如: 累计志愿服务时长 32 小时" />
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">个人申请陈述</label>
              <el-input v-model="scholarForm.statement" type="textarea" :rows="4" placeholder="简述您的在校表现、申请理由..." />
            </div>
          </div>
        </div>

        <div class="mt-6 pt-5 border-t border-outline-variant/15 flex items-center justify-between">
          <div class="flex items-center gap-2 text-xs text-secondary">
            <el-icon><InfoFilled /></el-icon>
            申请材料将提交至辅导员进行初审，评审结果将在系统内通知
          </div>
          <button @click="submitScholarship" :disabled="submitting"
                  class="px-8 py-3 bg-primary text-on-primary-fixed rounded-xl font-bold text-sm hover:bg-primary-fixed transition-colors shadow-md disabled:opacity-50">
            {{ submitting ? '提交中...' : '🚀 提交申请' }}
          </button>
        </div>
      </div>

      <!-- 奖助历史记录（表单下方可滚动查看） -->
      <div class="mt-6 bg-surface-container-lowest rounded-xl border border-outline-variant/15 p-6 shadow-sm">
        <h4 class="text-sm font-bold text-on-surface mb-4">历史申请记录</h4>
        <div class="space-y-3">
          <div v-for="(app, i) in scholarHistory" :key="i"
               class="p-4 rounded-xl border border-outline-variant/20 bg-surface hover:border-primary/20 transition-colors">
            <div class="flex items-center justify-between mb-1.5">
              <h5 class="font-semibold text-sm text-on-surface">{{ app.title }}</h5>
              <span class="text-xs font-bold px-2.5 py-1 rounded-full border"
                    :class="statusStyle(app.status)">{{ statusLabel(app.status) }}</span>
            </div>
            <p class="text-xs text-secondary leading-relaxed">{{ app.reason }}</p>
            <div v-if="app.reviewComment" class="mt-2 p-2.5 bg-surface-container-low rounded-lg text-xs text-on-surface">
              <span class="font-bold">批注: </span>{{ app.reviewComment }}
            </div>
          </div>
          <div v-if="scholarHistory.length === 0" class="py-8 text-center text-secondary text-sm">暂无历史记录</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Warning, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const STUDENT_ID = '202301042'
const submitting = ref(false)
const allApplications = ref([])

// 从 URL query 读取当前 tab（由 Layout 控制）
const activeTab = computed(() => route.query.tab || 'leave')

// ----- 请假 -----
const leaveTypes = [
  { label: '事假', value: 'PERSONAL' },
  { label: '病假', value: 'SICK' },
  { label: '公假', value: 'OFFICIAL' },
  { label: '家庭急事', value: 'FAMILY' },
  { label: '返乡', value: 'HOMETOWN' },
  { label: '其他', value: 'OTHER' }
]

const leaveForm = ref({ leaveType: 'PERSONAL', startDate: '', endDate: '', destination: '', reason: '', contact: '' })

const leaveHistory = ref([
  { id: 1, leaveType: 'HOMETOWN', startDate: '2024-11-16', endDate: '2024-11-17', destination: '广州市天河区 · 家庭住址', reason: '周末返乡，参加家庭事务', status: 'PENDING_RETURN', reviewComment: '同意，注意安全，按时返校' },
  { id: 2, leaveType: 'SICK', startDate: '2024-10-20', endDate: '2024-10-21', destination: '校医院 / 宿舍休养', reason: '发烧感冒，已就医，医嘱休息2天', status: 'RETURNED', reviewComment: '注意保暖，好好休息' }
])

const checkIn = (id) => {
  const item = leaveHistory.value.find(r => r.id === id)
  if (item) { item.status = 'RETURNED'; ElMessage.success('销假成功，欢迎回来！') }
}

const submitLeave = async () => {
  if (!leaveForm.value.startDate || !leaveForm.value.endDate || !leaveForm.value.reason) {
    ElMessage.warning('请填写完整的请假信息'); return
  }
  submitting.value = true
  try {
    await axios.post('http://localhost:8080/api/applications', {
      studentId: STUDENT_ID, studentName: '张小明', type: 'LEAVE',
      title: `${leaveTypeLabel(leaveForm.value.leaveType)}申请（${leaveForm.value.startDate} 至 ${leaveForm.value.endDate}）`,
      reason: `前往: ${leaveForm.value.destination}｜联系: ${leaveForm.value.contact}｜${leaveForm.value.reason}`
    })
    ElMessage.success('请假申请已提交，请等待辅导员审批')
    leaveHistory.value.unshift({ id: Date.now(), leaveType: leaveForm.value.leaveType, startDate: leaveForm.value.startDate, endDate: leaveForm.value.endDate, destination: leaveForm.value.destination, reason: leaveForm.value.reason, status: 'PENDING', reviewComment: '' })
    leaveForm.value = { leaveType: 'PERSONAL', startDate: '', endDate: '', destination: '', reason: '', contact: '' }
  } catch (e) { ElMessage.error('提交失败，请确保后端服务已启动') }
  finally { submitting.value = false }
}

// ----- 奖助学金 -----
const scholarForm = ref({ scholarType: '', gpa: '', rank: '', total: '', familyIncome: '', povertyLevel: 'none', honors: '', volunteer: '', statement: '' })
const scholarHistory = computed(() => allApplications.value.filter(a => a.type === 'SCHOLARSHIP' || a.type === 'FINANCIAL_AID'))

const submitScholarship = async () => {
  if (!scholarForm.value.scholarType || !scholarForm.value.statement) {
    ElMessage.warning('请选择奖助类别并填写个人陈述'); return
  }
  submitting.value = true
  try {
    const typeLabels = { nat_scholarship: '国家奖学金', nat_incentive: '国家励志奖学金', nat_aid: '国家助学金', school_scholarship: '学校奖学金', school_aid: '学校助学金' }
    await axios.post('http://localhost:8080/api/applications', {
      studentId: STUDENT_ID, studentName: '张小明', type: 'SCHOLARSHIP',
      title: `${typeLabels[scholarForm.value.scholarType]}申请`,
      reason: `GPA: ${scholarForm.value.gpa}，排名第${scholarForm.value.rank}/${scholarForm.value.total}。${scholarForm.value.statement}`
    })
    ElMessage.success('申请已提交，请耐心等待审核！')
    await loadData()
    scholarForm.value = { scholarType: '', gpa: '', rank: '', total: '', familyIncome: '', povertyLevel: 'none', honors: '', volunteer: '', statement: '' }
  } catch (e) { ElMessage.error('提交失败，请确保后端服务已启动') }
  finally { submitting.value = false }
}

const loadData = async () => {
  try {
    const res = await axios.get(`http://localhost:8080/api/applications/student/${STUDENT_ID}`)
    if (res.data.code === 200) allApplications.value = res.data.data
  } catch (e) { console.error(e) }
}

const leaveTypeLabel = (t) => ({ PERSONAL: '事假', SICK: '病假', OFFICIAL: '公假', FAMILY: '家庭急事', HOMETOWN: '返乡', OTHER: '其他' }[t] || t)
const leaveStatusLabel = (s) => ({ PENDING: '审核中', ON_LEAVE: '休假中', PENDING_RETURN: '待销假', RETURNED: '已销假', REJECTED: '已驳回' }[s] || s)
const leaveStatusStyle = (s) => ({ PENDING: 'bg-blue-50 text-blue-600 border-blue-200', ON_LEAVE: 'bg-green-50 text-green-700 border-green-300', PENDING_RETURN: 'bg-orange-50 text-orange-600 border-orange-300', RETURNED: 'bg-surface-container-high text-secondary border-outline-variant/30', REJECTED: 'bg-error-container text-error border-error/20' }[s] || '')
const statusLabel = (s) => ({ PENDING: '审核中', APPROVED: '已批准', REJECTED: '已驳回' }[s] || s)
const statusStyle = (s) => ({ PENDING: 'bg-orange-50 text-orange-600 border-orange-200', APPROVED: 'bg-green-50 text-green-700 border-green-200', REJECTED: 'bg-error-container text-error border-error/20' }[s])

onMounted(loadData)
</script>
