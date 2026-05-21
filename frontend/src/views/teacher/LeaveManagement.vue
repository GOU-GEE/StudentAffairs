<template>
  <div class="flex flex-col gap-6 h-[calc(100vh-100px)] min-h-[600px] pb-4">
    <!-- 主体：左右分栏两个独立的板块 -->
    <div class="flex-1 flex gap-6 min-h-0">
      
      <!-- 左栏板块 (占 58% 宽度) -->
      <div class="w-[58%] bg-white rounded-3xl border border-gray-200/60 shadow-sm overflow-hidden flex flex-col h-full">
        <!-- 列表顶部小工具栏：支持搜索和筛选 -->
        <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between flex-shrink-0 bg-gray-50/30">
          <div class="relative w-72 flex items-center">
            <span class="absolute left-3 flex items-center text-gray-400"><el-icon><Search /></el-icon></span>
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="搜索学号、姓名、目的地..." 
              class="w-full pl-9 pr-4 py-2 bg-white border border-gray-200 rounded-xl text-xs font-medium focus:outline-none focus:border-blue-500 transition-all"
            />
          </div>
          <!-- 快捷过滤 -->
          <div class="flex items-center gap-1">
            <button 
              v-for="filter in ['ALL', 'PENDING', 'APPROVED', 'REJECTED', 'RETURNED']" 
              :key="filter"
              @click="activeFilter = filter"
              class="px-2.5 py-1 text-[11px] font-bold rounded-lg transition-all"
              :class="activeFilter === filter ? 'bg-gray-900 text-white shadow-sm' : 'text-gray-400 hover:text-gray-700 bg-transparent'"
            >
              {{ {ALL: '全部', PENDING: '待审批', APPROVED: '已批准', REJECTED: '已驳回', RETURNED: '已销假'}[filter] }}
            </button>
          </div>
        </div>

        <!-- 申请列表区域 -->
        <div class="flex-1 overflow-y-auto p-6 space-y-3.5 custom-scrollbar min-h-0 bg-gray-50/10">
          <div 
            v-for="r in filteredLeaves" 
            :key="r.id"
            @click="selectLeave(r)"
            class="p-4 border rounded-2xl transition-all cursor-pointer bg-white relative flex flex-col"
            :class="selectedLeave?.id === r.id 
              ? 'border-blue-500 shadow-md ring-1 ring-blue-500/10' 
              : 'border-gray-100 hover:border-blue-300 hover:shadow-sm'"
          >
            <!-- 头部信息 -->
            <div class="flex items-start justify-between mb-2">
              <div class="flex items-center gap-2">
                <span class="w-8 h-8 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center font-bold text-xs">
                  {{ r.studentName ? r.studentName.substring(0, 1) : '学' }}
                </span>
                <div>
                  <span class="font-bold text-sm text-gray-900">{{ r.studentName }}</span>
                  <span class="text-[10px] font-bold text-gray-400 bg-gray-100 px-1.5 py-0.5 rounded ml-2">{{ r.studentId }}</span>
                </div>
              </div>
              <div class="flex items-center gap-2.5">
                <span class="text-xs text-gray-400 font-medium">{{ r.applyTime ? r.applyTime.substring(0, 10) : '' }}</span>
                <span class="px-2.5 py-0.5 rounded-full text-[10px] font-black border"
                      :class="statusBadgeStyle(r.status)">
                  {{ leaveStatusLabel(r.status) }}
                </span>
              </div>
            </div>

            <!-- 请假简要信息 -->
            <div class="mt-1 flex items-center justify-between text-xs text-gray-500">
              <span class="font-semibold text-gray-700 bg-blue-50/50 px-2 py-0.5 rounded-md border border-blue-100/30">
                {{ leaveTypeLabel(r.leaveType) }}
              </span>
              <span class="text-gray-400">
                {{ r.startDate }} 至 {{ r.endDate }}（共 {{ calcDays(r.startDate, r.endDate) }} 天）
              </span>
            </div>
            <p class="text-xs text-gray-400 leading-relaxed line-clamp-1 mt-2">目的地：{{ r.destination || '未填写' }}</p>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredLeaves.length === 0" class="h-full flex flex-col items-center justify-center text-gray-400 py-16">
            <span class="text-4xl mb-3">📋</span>
            <p class="text-xs font-semibold">暂无匹配的请假报备申请</p>
          </div>
        </div>
      </div>

      <!-- 右栏板块 (占 42% 宽度) -->
      <div class="w-[42%] bg-white rounded-3xl border border-gray-200/60 shadow-sm overflow-hidden flex flex-col h-full">
        <transition name="fade-slide" mode="out-in">
          <!-- 选中请假详情 -->
          <div v-if="selectedLeave" class="flex flex-col h-full min-h-0 bg-white" :key="selectedLeave.id">
            <!-- 详情滚动内容区 -->
            <div class="flex-1 overflow-y-auto px-6 py-6 space-y-5 custom-scrollbar min-h-0 bg-white text-sm text-gray-700 leading-relaxed">
              <!-- 第一行：第一列姓名，第二列学号 -->
              <div class="grid grid-cols-2 gap-4">
                <div>姓名：{{ selectedLeave.studentName }}</div>
                <div>学号：{{ selectedLeave.studentId }}</div>
              </div>
              
              <!-- 第二行：第一列专业，第二列班级 -->
              <div class="grid grid-cols-2 gap-4">
                <div>专业：{{ studentMap[selectedLeave.studentId]?.major || '计算机科学与技术' }}</div>
                <div>班级：{{ studentMap[selectedLeave.studentId]?.classGrade || '大一 (2023级) 软件工程12班' }}</div>
              </div>
              
              <!-- 第三行：一整行请假时间 -->
              <div>请假时间：{{ selectedLeave.startDate }} 至 {{ selectedLeave.endDate }}&nbsp;&nbsp;共 {{ calcDays(selectedLeave.startDate, selectedLeave.endDate) }} 天</div>
              
              <!-- 第四、五行：前往地点与紧急联系方式分列，换行显示实际数据 -->
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <div class="text-gray-400 text-xs font-semibold">前往地点：</div>
                  <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedLeave.destination || '未填写' }}</div>
                </div>
                <div>
                  <div class="text-gray-400 text-xs font-semibold">紧急联系方式：</div>
                  <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedLeave.contact || studentMap[selectedLeave.studentId]?.contact || '未填写' }}</div>
                </div>
              </div>
              
              <!-- 第六、七行：请假理由与实际理由内容 -->
              <div>
                <div class="text-gray-400 text-xs font-semibold">请假理由：</div>
                <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedLeave.reason || '未填写' }}</div>
              </div>
              
              <!-- 第八行：审批状态（带颜色） -->
              <div class="flex items-center gap-1.5">
                <span class="text-gray-400 text-xs font-semibold">审批状态：</span>
                <span class="font-bold text-sm" :class="{
                  PENDING: 'text-blue-500',
                  ON_LEAVE: 'text-green-500',
                  NOT_STARTED: 'text-yellow-500',
                  PENDING_RETURN: 'text-orange-500',
                  RETURNED: 'text-gray-400',
                  REJECTED: 'text-red-500'
                }[selectedLeave.status] || 'text-gray-500'">
                  {{ leaveStatusLabel(selectedLeave.status) }}
                </span>
              </div>

              <!-- 第九、十行：辅导员意见与意见输入框 -->
              <div class="space-y-1.5 pt-1">
                <div class="text-gray-400 text-xs font-semibold">辅导员意见：</div>
                <div class="border border-gray-200 rounded-xl overflow-hidden hover:border-gray-400 focus-within:border-blue-500 transition-colors bg-white">
                  <textarea 
                    v-model="reviewComment" 
                    class="w-full text-sm outline-none bg-transparent px-3 py-2.5 placeholder-gray-300 text-gray-700 resize-none disabled:bg-gray-50 disabled:text-gray-400/80" 
                    rows="3" 
                    placeholder="在此输入您的审批意见（通过或驳回时都会保存）..."
                    maxlength="200"
                    :disabled="selectedLeave.status !== 'PENDING'"
                  ></textarea>
                  <div v-if="selectedLeave.status === 'PENDING'" class="px-3 pb-2 text-right text-xs text-gray-300 select-none">
                    {{ reviewComment.length }} / 200
                  </div>
                </div>
              </div>

              <!-- 第十一行：追加的销假时间 -->
              <div v-if="selectedLeave.status === 'RETURNED' && selectedLeave.reviewTime">
                <div class="text-gray-400 text-xs font-semibold">销假时间：</div>
                <div class="text-gray-800 mt-1.5 font-medium">{{ selectedLeave.reviewTime.replace('T', ' ').substring(0, 16) }}</div>
              </div>
            </div>

            <!-- 审批操作按钮 -->
            <div v-if="selectedLeave.status === 'PENDING'" class="p-5 border-t border-gray-100 flex gap-4 bg-gray-50/50 flex-shrink-0">
              <button 
                @click="submitReview('REJECTED')" 
                :disabled="submitting"
                class="flex-1 py-2.5 bg-red-50 text-red-600 border border-red-200 rounded-xl font-bold text-sm hover:bg-red-100 transition-all flex items-center justify-center gap-1.5 disabled:opacity-40"
              >
                <el-icon><Close /></el-icon> 驳回
              </button>
              <button 
                @click="submitReview('APPROVED')" 
                :disabled="submitting"
                class="flex-1 py-2.5 bg-blue-600 text-white rounded-xl font-bold text-sm shadow-md hover:bg-blue-700 hover:shadow-lg transition-all flex items-center justify-center gap-1.5 disabled:opacity-40"
              >
                <el-icon><Check /></el-icon> 通过
              </button>
            </div>
          </div>

          <!-- 未选中状态 -->
          <div v-else class="h-full flex flex-col items-center justify-center text-gray-400/80 p-8">
            <el-icon class="text-5xl mb-4 text-gray-300"><Document /></el-icon>
            <p class="text-sm font-bold tracking-wide">选中请假申请后即可查看详情</p>
          </div>
        </transition>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Calendar, User, Location, EditPen, Close, Check, Document } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/applications'
const teacherName = sessionStorage.getItem('userName') || '李辅导员'

const leaves = ref([])
const selectedLeave = ref(null)
const reviewComment = ref('')
const submitting = ref(false)
const searchQuery = ref('')
const activeFilter = ref('ALL') // ALL, PENDING, APPROVED, REJECTED, RETURNED

// 学生详细信息Mock Map
const studentMap = {
  '202301042': { major: '计算机科学与技术', classGrade: '大一 (2023级) 软件工程12班', contact: '13800138000' },
  '202301043': { major: '电子信息工程', classGrade: '大二 (2022级) 电工22-1班', contact: '13911112222' },
  '202301044': { major: '应用物理学', classGrade: '大三 (2021级) 物研21-2班', contact: '13733334444' },
  '202301045': { major: '汉语言文学', classGrade: '大一 (2023级) 汉语言23-3班', contact: '13655556666' }
}

const pendingCount = computed(() => leaves.value.filter(r => r.status === 'PENDING').length)
const approvedCount = computed(() => leaves.value.filter(r => r.status === 'APPROVED' || r.status === 'ON_LEAVE' || r.status === 'PENDING_RETURN' || r.status === 'RETURNED').length)

const filteredLeaves = computed(() => {
  return leaves.value.filter(r => {
    // 快捷状态过滤
    if (activeFilter.value === 'PENDING' && r.status !== 'PENDING') return false
    if (activeFilter.value === 'APPROVED' && r.status !== 'APPROVED' && r.status !== 'ON_LEAVE' && r.status !== 'PENDING_RETURN') return false
    if (activeFilter.value === 'REJECTED' && r.status !== 'REJECTED') return false
    if (activeFilter.value === 'RETURNED' && r.status !== 'RETURNED') return false

    // 关键词搜索过滤
    const query = searchQuery.value.trim().toLowerCase()
    if (!query) return true

    return (
      (r.studentName && r.studentName.toLowerCase().includes(query)) ||
      (r.studentId && r.studentId.toLowerCase().includes(query)) ||
      (r.destination && r.destination.toLowerCase().includes(query)) ||
      (r.reason && r.reason.toLowerCase().includes(query))
    )
  })
})

const loadLeaves = async () => {
  try {
    const res = await request.get(`${API}/all`)
    if (res.data.code === 200) {
      leaves.value = (res.data.data || [])
        .filter(item => item.type === 'LEAVE')
        .map(item => {
          let detail = {}
          try { detail = JSON.parse(item.reason) } catch (e) {}
          return {
            id: item.id,
            studentId: item.studentId,
            studentName: item.studentName,
            applyTime: item.applyTime,
            leaveType: detail.leaveType || 'OTHER',
            startDate: detail.startDate || '',
            endDate: detail.endDate || '',
            destination: detail.destination || '',
            reason: detail.reason || item.reason,
            contact: detail.contact || '',
            status: item.status,
            reviewComment: item.reviewComment,
            reviewerName: item.reviewerName,
            reviewTime: item.reviewTime
          }
        })
      
      // 刷新当前选中的假单
      if (selectedLeave.value) {
        const updated = leaves.value.find(l => l.id === selectedLeave.value.id)
        if (updated) selectedLeave.value = updated
      }
    }
  } catch (e) {
    console.error('加载请假申请列表失败', e)
  }
}

const selectLeave = (r) => {
  selectedLeave.value = r
  reviewComment.value = r.reviewComment || ''
}

const submitReview = async (newStatus) => {
  if (!selectedLeave.value) return
  submitting.value = true
  try {
    const payload = {
      status: newStatus,
      comment: reviewComment.value.trim(),
      reviewerName: teacherName
    }
    const res = await request.put(`${API}/${selectedLeave.value.id}/review`, payload)
    if (res.data.code === 200) {
      ElMessage.success(newStatus === 'APPROVED' ? '请假已批准' : '申请已驳回')
      await loadLeaves()
    } else {
      ElMessage.warning(res.data.msg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('审批请求失败，请检查网络')
  } finally {
    submitting.value = false
  }
}

const calcDays = (start, end) => {
  if (!start || !end) return 0
  const diff = new Date(end) - new Date(start)
  return Math.max(1, Math.round(diff / 86400000) + 1)
}

const leaveTypeLabel = (t) => ({
  PERSONAL: '事假', SICK: '病假', OFFICIAL: '公假',
  FAMILY: '家庭急事', HOMETOWN: '返乡', OTHER: '其他'
}[t] || t)

const leaveStatusLabel = (s) => ({
  PENDING: '待审批', APPROVED: '已批准', ON_LEAVE: '休假中',
  PENDING_RETURN: '待销假', RETURNED: '已销假', REJECTED: '已驳回'
}[s] || s)

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-blue-50 text-blue-600 border-blue-200',
  APPROVED: 'bg-green-50 text-green-700 border-green-300',
  ON_LEAVE: 'bg-green-50 text-green-700 border-green-300',
  PENDING_RETURN: 'bg-orange-50 text-orange-600 border-orange-300',
  RETURNED: 'bg-gray-100 text-gray-500 border-gray-200',
  REJECTED: 'bg-red-50 text-red-600 border-red-200'
}[s] || 'bg-gray-100 text-gray-500 border-gray-200')

onMounted(loadLeaves)
</script>

<style scoped>
/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(15px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-15px);
}
</style>
