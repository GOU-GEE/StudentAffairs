<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
    <!-- 左侧：申请表单 -->
    <div class="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <!-- 头部标题 -->
      <div class="px-6 pt-6 pb-5 border-b border-gray-100 flex-shrink-0">
        <div>
          <h3 class="text-base font-bold text-gray-900">奖助学金申请</h3>
          <p class="text-xs text-gray-400 mt-0.5">请如实填写申请信息，材料将提交辅导员审核</p>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5">
        <!-- 申请信息标题 -->
        <div class="flex items-center gap-2 mb-4">
          <el-icon class="text-gray-400" :size="15"><Document /></el-icon>
          <span class="text-sm font-bold text-gray-700">申请信息</span>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-5">
          <!-- 申请奖助类别 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              申请奖助类别 <span class="text-red-500">*</span>
            </label>
            <div class="relative flex items-center">
              <select
                v-model="form.scholarType"
                class="w-full appearance-none border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors cursor-pointer pr-10 block"
              >
                <option value="" disabled>请选择奖助类型</option>
                <option value="nat_scholarship">国家奖学金（成绩全院前 0.5%）</option>
                <option value="nat_incentive">国家励志奖学金（成绩前 3%，有困难认定）</option>
                <option value="nat_aid">国家助学金（家庭经济困难）</option>
                <option value="school_scholarship">学校奖学金（成绩优秀）</option>
                <option value="school_aid">学校助学金（生活临时困难）</option>
              </select>
              <div class="absolute right-3 top-0 bottom-0 flex items-center pointer-events-none">
                <el-icon class="text-gray-400" :size="14"><ArrowDown /></el-icon>
              </div>
            </div>
          </div>

          <!-- 学号 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              学号 <span class="text-red-500">*</span>
            </label>
            <input
              value="202301042"
              disabled
              class="w-full border border-gray-100 rounded-xl px-3 py-2.5 text-sm text-gray-500 bg-gray-50 outline-none"
            />
          </div>

          <!-- 姓名 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              姓名 <span class="text-red-500">*</span>
            </label>
            <input
              value="张小明"
              disabled
              class="w-full border border-gray-100 rounded-xl px-3 py-2.5 text-sm text-gray-500 bg-gray-50 outline-none"
            />
          </div>

          <!-- 所在班级 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              所在班级 <span class="text-red-500">*</span>
            </label>
            <input
              value="计算机科学2301班"
              disabled
              class="w-full border border-gray-100 rounded-xl px-3 py-2.5 text-sm text-gray-500 bg-gray-50 outline-none"
            />
          </div>

          <!-- 家庭年收入 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              家庭年收入（元）<span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.familyIncome"
              placeholder="如：50000"
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors"
            />
          </div>

          <!-- 困难认定等级 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              困难认定等级 <span class="text-red-500">*</span>
            </label>
            <div class="relative flex items-center">
              <select
                v-model="form.povertyLevel"
                class="w-full appearance-none border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors cursor-pointer pr-10 block"
              >
                <option value="none">无困难认定</option>
                <option value="A">A 档（特困）</option>
                <option value="B">B 档（困难）</option>
                <option value="C">C 档（一般困难）</option>
              </select>
              <div class="absolute right-3 top-0 bottom-0 flex items-center pointer-events-none">
                <el-icon class="text-gray-400" :size="14"><ArrowDown /></el-icon>
              </div>
            </div>
          </div>

          <!-- 本学期加权平均成绩 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              本学期加权平均成绩 <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.gpa"
              placeholder="如：88.5"
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors"
            />
          </div>

          <!-- 专业排名 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">专业排名</label>
            <div class="flex items-center gap-2 h-[42px]">
              <span class="text-sm text-gray-400 flex-shrink-0">第</span>
              <input
                v-model="form.rank"
                placeholder="名次"
                class="w-20 border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors text-center"
              />
              <span class="text-sm text-gray-400 flex-shrink-0">名 / 共</span>
              <input
                v-model="form.total"
                placeholder="人数"
                class="w-20 border border-gray-200 rounded-xl px-3 py-2 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors text-center"
              />
              <span class="text-sm text-gray-400 flex-shrink-0">人</span>
            </div>
          </div>

          <!-- 在校获得荣誉 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              在校获得荣誉 <span class="text-red-500">*</span>
            </label>
            <textarea
              v-model="form.honors"
              rows="3"
              placeholder="如：2024年校级三好学生&#10;全国大学生数学竞赛二等奖"
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors resize-none"
            ></textarea>
          </div>

          <!-- 志愿服务时长 -->
          <div>
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              志愿服务时长 <span class="text-red-500">*</span>
            </label>
            <input
              v-model="form.volunteer"
              placeholder="如：累计志愿服务时长 32 小时"
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors"
            />
          </div>

          <!-- 个人申请陈述 -->
          <div class="md:col-span-2">
            <label class="text-sm text-gray-700 font-medium block mb-1.5">
              个人申请陈述 <span class="text-red-500">*</span>
            </label>
            <textarea
              v-model="form.statement"
              rows="4"
              placeholder="简述您的在校表现、申请理由..."
              class="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm text-gray-700 bg-white outline-none focus:border-gray-500 hover:border-gray-400 transition-colors resize-none"
            ></textarea>
          </div>
        </div>
      </div>

      <!-- 底部：提示 + 提交按钮 -->
      <div class="px-6 py-4 border-t border-gray-100 flex-shrink-0 flex items-center justify-between gap-4">
        <div class="flex items-center gap-2 text-xs text-gray-400">
          <el-icon :size="13"><InfoFilled /></el-icon>
          <span>申请材料将提交至辅导员进行初审，评审结果将在系统内通知</span>
        </div>
        <button
          @click="submitScholarship"
          :disabled="submitting"
          class="flex-shrink-0 px-6 py-2.5 bg-[#409eff] text-white rounded-xl font-bold text-sm hover:bg-[#409eff]/80 transition-colors shadow-sm disabled:opacity-50 flex items-center gap-2"
        >
          {{ submitting ? '提交中...' : '提交申请' }}
        </button>
      </div>
    </div>

    <!-- 右侧：历史申请记录 -->
    <div class="bg-white rounded-2xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <!-- 卡片标题 -->
      <div class="flex items-center gap-3 px-6 pt-6 pb-4 border-b border-gray-100 flex-shrink-0">
        <el-icon class="text-gray-400" :size="18"><Clock /></el-icon>
        <h3 class="text-base font-bold text-gray-900">历史申请记录</h3>
      </div>

      <div class="flex-1 overflow-y-auto px-6 py-5 custom-scrollbar">
        <div class="space-y-4">
          <div
            v-for="(item, i) in history"
            :key="item.id"
            @click="openDetail(item)"
            class="border rounded-xl p-4 cursor-pointer hover:shadow-sm transition-all group"
            :class="i === 0 ? 'border-amber-200 bg-amber-50/60' : 'border-gray-100 bg-white hover:border-gray-200'"
          >
            <div class="flex items-start gap-3">
              <div class="flex-1 min-w-0">
                <!-- 标题行 -->
                <div class="flex items-start justify-between gap-2 mb-1.5">
                  <h4 class="text-sm font-bold text-gray-900 leading-snug">{{ item.title }}</h4>
                  <span
                    class="flex-shrink-0 text-[10px] font-bold px-2 py-0.5 rounded-full"
                    :class="statusBadgeStyle(item.status)"
                  >{{ statusLabel(item.status) }}</span>
                </div>

                <!-- 申请时间 -->
                <div class="flex items-center gap-1.5 text-[11px] text-gray-400 mb-2">
                  <el-icon :size="11"><Calendar /></el-icon>
                  <span>申请时间：{{ item.applyTime }}</span>
                </div>

                <!-- 简述 -->
                <p class="text-[11px] text-gray-500 leading-relaxed line-clamp-2">{{ item.summary }}</p>

                <!-- 展开箭头 -->
                <div class="flex justify-end mt-1">
                  <el-icon class="text-gray-300 group-hover:text-gray-400 transition-colors" :size="14"><ArrowRight /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="history.length === 0" class="py-16 flex flex-col items-center text-gray-400">
          <span class="text-4xl mb-3">📋</span>
          <p class="text-sm">暂无申请记录</p>
        </div>
      </div>
    </div>

    <!-- 局部暗色遮罩层，仅覆盖内容区，避免导航栏变灰 -->
    <div v-if="showDetailDialog" class="fixed top-[56px] left-[256px] right-0 bottom-0 bg-black/45 z-[1000] transition-opacity duration-300" @click="showDetailDialog = false"></div>

    <!-- 奖学金详情滑出抽屉 (el-dialog) -->
    <el-dialog
      v-model="showDetailDialog"
      title=""
      modal-class="custom-leave-overlay"
      class="custom-leave-detail-dialog"
      :show-close="false"
      :modal="false"
      append-to-body
      lock-scroll
      destroy-on-close
    >
      <div v-if="selectedItem" class="flex flex-col h-full bg-white relative">
        <!-- 抽屉头部 -->
        <div class="px-6 py-5 border-b border-gray-100 flex items-center justify-between flex-shrink-0">
          <span class="text-base font-bold text-gray-900">详情信息</span>
          <button @click="showDetailDialog = false" class="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 text-gray-500 hover:text-gray-700 flex items-center justify-center transition-colors focus:outline-none">
            <el-icon :size="16"><Close /></el-icon>
          </button>
        </div>

        <!-- 滚动内容区 -->
        <div class="flex-1 overflow-y-auto px-8 py-6 space-y-5 custom-scrollbar text-sm text-gray-700 leading-relaxed bg-white">
          <!-- 第一行：一整行标题，字体加粗 -->
          <div class="text-sm font-bold text-gray-900 border-b border-gray-100 pb-2 mb-3">
            {{ selectedItem.title }}
          </div>

          <!-- 第二行：第一列姓名，第二列学号 -->
          <div class="grid grid-cols-2 gap-4">
            <div>姓名：{{ studentName }}</div>
            <div>学号：{{ studentId }}</div>
          </div>
          
          <!-- 第三行：所在班级，申请类别 -->
          <div class="grid grid-cols-2 gap-4">
            <div>班级：计算机科学2301班</div>
            <div>申请类别：{{ getScholarshipTypeLabel(selectedItem.detail.scholarType) || selectedItem.title }}</div>
          </div>
          
          <!-- 第四行：加权平均成绩，专业排名 -->
          <div class="grid grid-cols-2 gap-4">
            <div>平均成绩：{{ selectedItem.detail.gpa || '未填写' }}</div>
            <div>专业排名：{{ selectedItem.detail.rank ? `第 ${selectedItem.detail.rank} 名 / 共 ${selectedItem.detail.total} 人` : '未填写' }}</div>
          </div>

          <!-- 第五行：家庭年收入，困难等级 -->
          <div class="grid grid-cols-2 gap-4">
            <div>家庭年收入：{{ selectedItem.detail.familyIncome ? `${selectedItem.detail.familyIncome} 元` : '未填写' }}</div>
            <div>困难认定：{{ getPovertyLabel(selectedItem.detail.povertyLevel) }}</div>
          </div>

          <!-- 第六行：志愿服务时长 -->
          <div>
            <div class="text-gray-400 text-xs font-semibold">志愿服务情况：</div>
            <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedItem.detail.volunteer || '未填写' }}</div>
          </div>

          <!-- 第七行：在校获得荣誉 -->
          <div>
            <div class="text-gray-400 text-xs font-semibold">在校获得荣誉：</div>
            <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedItem.detail.honors || '未填写' }}</div>
          </div>

          <!-- 第八行：个人申请陈述 -->
          <div>
            <div class="text-gray-400 text-xs font-semibold">个人申请陈述：</div>
            <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedItem.detail.statement || '未填写' }}</div>
          </div>
          
          <!-- 第九行：审批状态 -->
          <div class="flex items-center gap-1.5">
            <span class="text-gray-400 text-xs font-semibold">审批状态：</span>
            <span class="font-bold text-sm" :class="{
              PENDING: 'text-orange-500',
              APPROVED: 'text-green-500',
              REJECTED: 'text-red-500'
            }[selectedItem.status] || 'text-gray-500'">
              {{ statusLabel(selectedItem.status) }}
            </span>
          </div>

          <!-- 第十行：意见 -->
          <div>
            <div class="text-gray-400 text-xs font-semibold">辅导员意见：</div>
            <div class="text-gray-800 mt-1.5 font-medium whitespace-pre-wrap">{{ selectedItem.reviewComment || '暂无辅导员意见' }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

const API = '/api/applications'
const studentId = sessionStorage.getItem('userId') || '202301042'
const studentName = '张小明'
import {
  Document, ArrowDown, ArrowRight, InfoFilled, Clock, Calendar, Close
} from '@element-plus/icons-vue'

// 表单数据
const form = ref({
  scholarType: '',
  gpa: '',
  rank: '',
  total: '',
  familyIncome: '',
  povertyLevel: 'none',
  honors: '',
  volunteer: '',
  statement: ''
})

const submitting = ref(false)

const history = ref([])
const showDetailDialog = ref(false)
const selectedItem = ref(null)

const openDetail = (item) => {
  selectedItem.value = item
  showDetailDialog.value = true
}

const getScholarshipTypeLabel = (type) => {
  const typeMap = {
    nat_scholarship: '国家奖学金',
    nat_incentive: '国家励志奖学金',
    nat_aid: '国家助学金',
    school_scholarship: '学校奖学金',
    school_aid: '学校助学金'
  }
  return typeMap[type] || type
}

const getPovertyLabel = (lvl) => {
  const povertyMap = {
    none: '无困难认定',
    A: 'A 档（特困）',
    B: 'B 档（困难）',
    C: 'C 档（一般困难）'
  }
  return povertyMap[lvl] || lvl || '无困难认定'
}

const loadHistory = async () => {
  try {
    const res = await request.get(`${API}/student/${studentId}`)
    if (res.data.code === 200) {
      history.value = res.data.data
        .filter(item => item.type === 'SCHOLARSHIP')
        .map(item => {
          let detail = {}
          try {
            detail = JSON.parse(item.reason)
          } catch (e) {
            detail = { statement: item.reason }
          }
          return {
            id: item.id,
            title: item.title,
            status: item.status,
            applyTime: item.applyTime ? item.applyTime.replace('T', ' ').substring(0, 19) : '',
            summary: detail.statement || item.reason,
            reviewComment: item.reviewComment,
            reviewerName: item.reviewerName,
            reviewTime: item.reviewTime,
            detail: detail
          }
        })
    }
  } catch (e) {
    console.error('Failed to load scholarship history', e)
  }
}

onMounted(() => {
  loadHistory()
})

const submitScholarship = async () => {
  if (!form.value.scholarType) {
    ElMessage.warning('请选择申请奖助类别'); return
  }
  if (!form.value.statement.trim()) {
    ElMessage.warning('请填写个人申请陈述'); return
  }
  submitting.value = true

  const typeMap = {
    nat_scholarship: '国家奖学金',
    nat_incentive: '国家励志奖学金',
    nat_aid: '国家助学金',
    school_scholarship: '学校奖学金',
    school_aid: '学校助学金'
  }

  try {
    const payload = {
      studentId,
      studentName,
      type: 'SCHOLARSHIP',
      title: `${new Date().getFullYear()}–${new Date().getFullYear()+1}学年${typeMap[form.value.scholarType]}申请`,
      reason: JSON.stringify(form.value)
    }
    const res = await request.post(API, payload)
    if (res.data.code === 200) {
      ElMessage.success('申请已提交，请耐心等待审核！')
      form.value = {
        scholarType: '', gpa: '', rank: '', total: '',
        familyIncome: '', povertyLevel: 'none',
        honors: '', volunteer: '', statement: ''
      }
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

const statusLabel = (s) => ({
  PENDING: '审核中',
  APPROVED: '已通过',
  REJECTED: '未通过'
}[s] || s)

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-orange-100 text-orange-600',
  APPROVED: 'bg-green-100 text-green-700',
  REJECTED: 'bg-red-100 text-red-600'
}[s] || 'bg-gray-100 text-gray-500')
</script>

<style>
/* 全局样式：用于被 Element Plus 渲染并挂载到 body 下的请假详情对话框 */
.custom-leave-overlay {
  background-color: transparent !important;
  z-index: 1001 !important; /* 保证其低于顶层状态栏 z-[9999]，避免覆盖状态栏 */
}

.custom-leave-detail-dialog {
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
  .custom-leave-detail-dialog {
    left: calc(50% + 128px) !important; /* 除去左边 256px 导航栏后，在剩下的页面中左右居中 */
    transform: translateX(-50%) !important;
    right: auto !important;
  }
}
@media (max-width: 767px) {
  .custom-leave-detail-dialog {
    left: 50% !important;
    transform: translateX(-50%) !important;
    right: auto !important;
    width: calc(100vw - 32px) !important;
  }
}
.custom-leave-detail-dialog .el-dialog__header {
  padding: 0 !important;
  margin: 0 !important;
  height: 0;
}
.custom-leave-detail-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  padding: 0 !important;
}
.custom-leave-detail-dialog .el-dialog__headerbtn {
  display: none !important; /* 隐藏默认关闭按钮 */
}
</style>
