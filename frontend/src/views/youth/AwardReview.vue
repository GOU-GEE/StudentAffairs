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
          <div class="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-5">
            <!-- 获奖名称 + 颁奖单位 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">获奖名称</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedApp?.awardName || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">颁奖单位</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedApp?.issuer || '--' }}</p>
              </div>
            </div>

            <!-- 获奖时间 + 获奖级别 + 获奖类别 -->
            <div class="grid grid-cols-3 gap-4">
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">获奖时间</label>
                <p class="text-sm font-semibold text-on-surface">{{ formatMonth(selectedApp?.awardTime) || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">获奖级别</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedApp?.level || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">获奖类别</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedApp?.category || '--' }}</p>
              </div>
            </div>

            <!-- 获奖简述 -->
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">获奖简述</label>
              <p class="text-sm text-on-surface leading-relaxed">{{ selectedApp?.description || '--' }}</p>
            </div>

            <!-- 证明材料 -->
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">证明材料</label>
              <div class="border-2 border-dashed border-outline-variant/30 rounded-xl h-48 flex items-center justify-center bg-surface-container-lowest/50">
                <span class="text-sm text-outline">获奖材料显示在这里</span>
              </div>
            </div>

            <!-- 审核意见 -->
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">审核意见（选填）</label>
              <textarea
                :value="reviewComment"
                @input="onCommentInput($event.target.value)"
                :disabled="selectedApp?.status !== 'PENDING'"
                rows="3"
                placeholder="可填写审核意见..."
                class="w-full px-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-emerald-500/50 focus:ring-2 focus:ring-emerald-500/20 transition-all resize-none disabled:bg-gray-50 disabled:text-gray-400"
              ></textarea>
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Medal } from '@element-plus/icons-vue'

// Mock 数据
const MOCK_APPLICATIONS = [
  {
    id: 1,
    studentId: '202301001',
    studentName: '张小明',
    awardName: '2024年全国大学生数学建模竞赛省级一等奖',
    awardTime: '2024-09',
    level: '省级',
    category: '学科竞赛',
    issuer: '教育部高等教育司',
    description: '参赛作品《基于深度学习的城市交通流量预测模型》，团队排名第1，获省级一等奖。',
    status: 'PENDING',
    reviewComment: '',
    hasProof: true,
    proofName: '数学建模获奖证书.jpg',
  },
  {
    id: 2,
    studentId: '202301002',
    studentName: '李小丽',
    awardName: '蓝桥杯全国软件和信息技术专业人才大赛省级二等奖',
    awardTime: '2024-06',
    level: '省级',
    category: '学科竞赛',
    issuer: '工业和信息化部人才交流中心',
    description: 'Java软件开发大学A组，个人参赛，成绩排名全省前10%。',
    status: 'PENDING',
    reviewComment: '',
    hasProof: true,
    proofName: '蓝桥杯获奖证书.pdf',
  },
  {
    id: 3,
    studentId: '202301003',
    studentName: '王五',
    awardName: '全国大学生英语竞赛国家级三等奖',
    awardTime: '2024-05',
    level: '国家级',
    category: '学科竞赛',
    issuer: '教育部高等教育司',
    description: 'C类（非英语专业本科生），初赛全校排名第3，决赛获国家级三等奖。',
    status: 'PENDING',
    reviewComment: '',
    hasProof: true,
    proofName: '英语竞赛证书.jpg',
  },
  {
    id: 4,
    studentId: '202301004',
    studentName: '赵六',
    awardName: '校园程序设计大赛校级一等奖',
    awardTime: '2024-04',
    level: '校级',
    category: '学科竞赛',
    issuer: '西华师范大学',
    description: '独立完成算法竞赛题目，总成绩排名第1。',
    status: 'PENDING',
    reviewComment: '',
    hasProof: false,
    proofName: '',
  },
  {
    id: 5,
    studentId: '202301005',
    studentName: '周燕',
    awardName: '全国大学生创新创业训练计划项目国家级立项',
    awardTime: '2024-10',
    level: '国家级',
    category: '创新创业',
    issuer: '教育部',
    description: '项目《基于物联网的智慧校园垃圾分类系统》获国家级立项，项目负责人。',
    status: 'APPROVED',
    reviewComment: '材料齐全，项目有实际应用价值，予以通过。',
    hasProof: true,
    proofName: '大创立项通知书.pdf',
  },
  {
    id: 6,
    studentId: '202301006',
    studentName: '钱七',
    awardName: '四川省大学生艺术节舞蹈类省级二等奖',
    awardTime: '2024-07',
    level: '省级',
    category: '文体活动',
    issuer: '四川省教育厅',
    description: '群舞《蜀韵》获舞蹈类甲组省级二等奖，团队共12人。',
    status: 'APPROVED',
    reviewComment: '艺术类获奖，予以通过。',
    hasProof: true,
    proofName: '艺术节获奖证书.jpg',
  },
  {
    id: 7,
    studentId: '202301007',
    studentName: '孙八',
    awardName: '社区志愿服务优秀志愿者校级表彰',
    awardTime: '2024-03',
    level: '校级',
    category: '志愿服务',
    issuer: '西华师范大学团委',
    description: '2023-2024年度累计志愿服务时长120小时，获评优秀志愿者。',
    status: 'REJECTED',
    reviewComment: '志愿服务类奖项请走第二课堂学时认定流程，不在此处审核。',
    hasProof: true,
    proofName: '志愿证明.pdf',
  },
  {
    id: 8,
    studentId: '202301008',
    studentName: '吴十',
    awardName: 'ACM-ICPC国际大学生程序设计竞赛亚洲区域赛银奖',
    awardTime: '2024-11',
    level: '国家级',
    category: '学科竞赛',
    issuer: 'ACM中国理事会',
    description: '团队3人参赛，获亚洲区域赛（成都站）银奖，队内排名第2。',
    status: 'PENDING',
    reviewComment: '',
    hasProof: true,
    proofName: 'ACM获奖证书.pdf',
  },
]

const applications = ref([...MOCK_APPLICATIONS])
const searchQuery = ref('')
const selectedId = ref(null)
const activeFilter = ref('all')
const reviewComment = ref('')

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
  const [y, m] = val.split('-')
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

const handleReview = (status) => {
  const app = applications.value.find(a => a.id === selectedId.value)
  if (!app) return
  app.reviewComment = reviewComment.value || (status === 'APPROVED' ? '审核通过。' : '材料不符合要求，请重新提交。')
  app.status = status
  ElMessage.success(status === 'APPROVED' ? '已通过审核' : '已驳回申请')
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
