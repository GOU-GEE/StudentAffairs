<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧学生列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <div class="px-3 pt-2 pb-2">
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-outline z-10 flex items-center justify-center"><el-icon :size="14"><Search /></el-icon></span>
          <input v-model="searchQuery" placeholder="搜索姓名或学号..."
                 class="w-full pl-9 pr-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-amber-500/50 focus:ring-2 focus:ring-amber-500/20 transition-all" />
        </div>
      </div>

      <div class="h-px bg-outline-variant/15"></div>

      <!-- 等级筛选 -->
      <div class="px-2 py-2 flex gap-1.5 flex-wrap">
        <button v-for="tab in levelTabs" :key="tab.key"
          @click="activeFilter = tab.key"
          class="px-3 py-1 rounded-lg text-xs font-semibold transition-colors"
          :class="activeFilter === tab.key ? 'bg-amber-500 text-white' : 'text-secondary hover:bg-surface-container-high'">
          {{ tab.label }} {{ tab.count }}
        </button>
      </div>

      <div class="h-px bg-outline-variant/15"></div>

      <!-- 学生列表 -->
      <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
        <div v-for="item in filteredStudents" :key="item.id"
             @click="selectStudent(item)"
             class="p-3 rounded-xl cursor-pointer transition-all border flex flex-col gap-1"
             :class="selectedId === item.id ? 'bg-white shadow-sm border-amber-500/30 ring-1 ring-amber-500/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
          <div class="flex items-center justify-between gap-2">
            <div class="font-bold text-sm text-on-surface truncate">{{ item.name }}</div>
            <span class="text-xs font-bold flex-shrink-0 px-2 py-0.5 rounded-full" :class="levelBadgeClass(item.level)">{{ item.level }}</span>
          </div>
          <div class="text-xs text-outline">{{ item.studentId }} · {{ item.college }}</div>
        </div>

        <div v-if="filteredStudents.length === 0" class="py-10 text-center text-secondary text-sm">
          暂无学生记录
        </div>
      </div>
    </div>

    <!-- 右侧详情面板 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col">
      <transition name="fade" mode="out-in">
        <!-- 空状态 -->
        <div v-if="!selectedId" key="empty" class="flex flex-col items-center justify-center h-full w-full text-secondary">
          <el-icon :size="64" class="mb-4 opacity-20"><Stamp /></el-icon>
          <p class="text-lg font-medium text-on-surface">困难认定管理</p>
          <p class="text-sm mt-2 opacity-80">选择左侧学生查看详细信息</p>
        </div>

        <!-- 详情面板 -->
        <div v-else key="detail" class="flex flex-col h-full w-full">
          <!-- 头部 -->
          <div class="px-6 py-4 border-b border-outline-variant/30 flex items-center justify-between bg-white/40">
            <div>
              <h3 class="font-bold text-base text-on-surface">{{ selectedStudent?.name }}</h3>
              <p class="text-xs text-secondary mt-0.5">{{ selectedStudent?.studentId }} · {{ selectedStudent?.college }}</p>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs font-bold px-2.5 py-1 rounded-full" :class="statusBadgeClass(selectedStudent?.status)">{{ selectedStudent?.status }}</span>
            </div>
          </div>

          <!-- 详情内容 -->
          <div class="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-5">
            <!-- 学院 + 班级 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">学院</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedStudent?.college || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">班级</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedStudent?.gradeClass || '--' }}</p>
              </div>
            </div>

            <!-- 困难等级 + 认定状态 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">困难等级</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedStudent?.level || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">认定状态</label>
                <p class="text-sm font-semibold" :class="selectedStudent?.status === '已认定' ? 'text-green-600' : 'text-amber-600'">{{ selectedStudent?.status || '--' }}</p>
              </div>
            </div>

            <!-- 家庭年收入 + 家庭人口 -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">家庭年收入（元）</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedStudent?.annualIncome || '--' }}</p>
              </div>
              <div>
                <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">家庭人口</label>
                <p class="text-sm font-semibold text-on-surface">{{ selectedStudent?.familySize || '--' }} 人</p>
              </div>
            </div>

            <!-- 家庭成员 -->
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">家庭成员</label>
              <p class="text-sm text-on-surface leading-relaxed">{{ selectedStudent?.familyMembers || '--' }}</p>
            </div>

            <!-- 困难原因 -->
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">困难原因</label>
              <p class="text-sm text-on-surface leading-relaxed">{{ selectedStudent?.reason || '--' }}</p>
            </div>

            <!-- 证明材料 -->
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">证明材料</label>
              <div class="border-2 border-dashed border-outline-variant/30 rounded-xl h-40 flex items-center justify-center bg-surface-container-lowest/50">
                <span class="text-sm text-outline">证明材料显示在这里</span>
              </div>
            </div>

            <!-- 审核意见 -->
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">审核意见（选填）</label>
              <textarea
                :value="reviewComment"
                @input="onCommentInput($event.target.value)"
                :disabled="selectedStudent?.status === '已认定'"
                rows="3"
                placeholder="可填写审核意见..."
                class="w-full px-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-amber-500/50 focus:ring-2 focus:ring-amber-500/20 transition-all resize-none disabled:bg-gray-50 disabled:text-gray-400"
              ></textarea>
            </div>

            <!-- 操作按钮 -->
            <div v-if="selectedStudent?.status !== '已认定'" class="flex justify-end gap-2">
              <button @click="handleConfirm('B档')"
                class="px-4 py-1.5 rounded-lg text-xs font-bold border border-amber-200 text-amber-700 hover:bg-amber-50 transition-colors">
                认定为B档
              </button>
              <button @click="handleConfirm('A档')"
                class="px-4 py-1.5 rounded-lg text-xs font-bold border border-red-200 text-red-600 hover:bg-red-50 transition-colors">
                认定为A档
              </button>
              <button @click="handleConfirm('C档')"
                class="px-4 py-1.5 rounded-lg text-xs font-bold bg-amber-500 text-white hover:bg-amber-600 transition-colors shadow-sm">
                认定为C档
              </button>
            </div>
            <div v-else class="flex justify-end gap-2">
              <button @click="handleReset"
                class="px-4 py-1.5 rounded-lg text-xs font-bold border border-gray-200 text-gray-500 hover:bg-gray-50 transition-colors">
                重新认定
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
import { Search, Stamp } from '@element-plus/icons-vue'

// Mock 数据
const MOCK_STUDENTS = [
  {
    id: 1,
    studentId: '202301001',
    name: '张小明',
    college: '计算机科学学院',
    gradeClass: '计算机科学2301班',
    level: 'A档',
    status: '已认定',
    annualIncome: '12,000',
    familySize: '6',
    familyMembers: '父亲（务农）、母亲（体弱多病，无劳动能力）、奶奶（78岁）、弟弟（高中在读）、妹妹（小学在读）',
    reason: '家庭位于偏远山区，父亲务农收入微薄，母亲长期患病需药物治疗，家庭负担沉重。奶奶年迈行动不便需人照顾，弟弟妹妹均在上学，家庭经济压力极大。该生在校勤工助学，表现良好。',
  },
  {
    id: 2,
    studentId: '202301015',
    name: '李小丽',
    college: '软件工程学院',
    gradeClass: '软件工程2302班',
    level: 'B档',
    status: '已认定',
    annualIncome: '22,000',
    familySize: '4',
    familyMembers: '父亲（务工）、母亲（务农）、妹妹（初中在读）',
    reason: '父母均为普通劳动者，收入不稳定。妹妹就读初中，家庭开支较大。家庭位于农村，无其他经济来源。该生成绩优异，积极参加勤工助学。',
  },
  {
    id: 3,
    studentId: '202301042',
    name: '王五',
    college: '电子信息工程学院',
    gradeClass: '电子信息工程2301班',
    level: 'C档',
    status: '已认定',
    annualIncome: '35,000',
    familySize: '3',
    familyMembers: '父亲（个体经营户）、母亲（家庭主妇）',
    reason: '家庭收入来源单一，父亲经营小店收入不稳定。近年受经营环境影响较大，家庭经济状况有所下降。母亲在家照顾家庭，无收入。',
  },
  {
    id: 4,
    studentId: '202301056',
    name: '赵六',
    college: '数学与信息学院',
    gradeClass: '信息管理与信息系统2301班',
    level: '待认定',
    status: '待认定',
    annualIncome: '18,000',
    familySize: '5',
    familyMembers: '父亲（建筑工人）、母亲（无业）、爷爷（72岁）、弟弟（高中在读）',
    reason: '父亲从事建筑行业临工，工作不稳定，收入随季节波动大。母亲因身体原因无法从事体力劳动，爷爷年迈需要照顾。弟弟就读高中，教育支出较大。家庭住房为老旧平房，条件较差。',
  },
  {
    id: 5,
    studentId: '202301078',
    name: '周燕',
    college: '计算机科学学院',
    gradeClass: '人工智能2301班',
    level: '待认定',
    status: '待认定',
    annualIncome: '8,000',
    familySize: '7',
    familyMembers: '父亲（务农）、母亲（务农）、奶奶（76岁）、弟弟（高中）、妹妹（初中）、妹妹（小学）',
    reason: '来自偏远山区农村家庭，家庭人口多，劳动力少。父母务农收入极低，无其他经济来源。家中三位弟妹均在上学，教育支出负担重。奶奶年迈体弱需人照料。该生为低保家庭。',
  },
  {
    id: 6,
    studentId: '202301089',
    name: '孙八',
    college: '数据科学与大数据技术学院',
    gradeClass: '数据科学与大数据技术2301班',
    level: '待认定',
    status: '待认定',
    annualIncome: '15,000',
    familySize: '4',
    familyMembers: '母亲（务农）、本人（学生）、弟弟（小学）、妹妹（幼儿园）',
    reason: '单亲家庭，父亲三年前因病去世，家庭失去主要经济来源。母亲独自抚养三个孩子，务农收入有限。弟弟妹妹年幼需要照顾，家庭经济状况十分困难。',
  },
]

const students = ref([...MOCK_STUDENTS])
const searchQuery = ref('')
const activeFilter = ref('all')
const selectedId = ref(null)
const reviewComment = ref('')

const levelTabs = computed(() => [
  { key: 'all', label: '全部', count: students.value.length },
  { key: 'A档', label: 'A档', count: students.value.filter(s => s.level === 'A档').length },
  { key: 'B档', label: 'B档', count: students.value.filter(s => s.level === 'B档').length },
  { key: 'C档', label: 'C档', count: students.value.filter(s => s.level === 'C档').length },
])

const filteredStudents = computed(() => {
  let list = students.value
  if (activeFilter.value !== 'all') list = list.filter(s => s.level === activeFilter.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(s => s.name.includes(q) || s.studentId.includes(q))
  }
  return list
})

const selectedStudent = computed(() => {
  return students.value.find(s => s.id === selectedId.value) || null
})

const levelBadgeClass = (l) => ({
  'A档': 'bg-red-100 text-red-700',
  'B档': 'bg-amber-100 text-amber-700',
  'C档': 'bg-blue-100 text-blue-700',
  '待认定': 'bg-gray-100 text-gray-600',
}[l] || '')

const statusBadgeClass = (s) => ({
  '已认定': 'bg-green-100 text-green-700',
  '待认定': 'bg-amber-100 text-amber-700',
}[s] || '')

const selectStudent = (stu) => {
  selectedId.value = stu.id
  reviewComment.value = ''
}

const onCommentInput = (val) => {
  reviewComment.value = val
}

const handleConfirm = (level) => {
  const s = students.value.find(s => s.id === selectedId.value)
  if (!s) return
  s.level = level
  s.status = '已认定'
  ElMessage.success(`已认定 ${s.name} 为 ${level}`)
}

const handleReset = () => {
  const s = students.value.find(s => s.id === selectedId.value)
  if (!s) return
  s.status = '待认定'
  s.level = '待认定'
  reviewComment.value = ''
  ElMessage.success(`已重置 ${s.name} 的认定状态`)
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
