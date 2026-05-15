<template>
  <div class="space-y-6 animate-fade-in pb-10">
    <!-- 第一排：获奖情况 & 评奖评优情况 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 获奖情况 -->
      <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 shadow-sm hover:bg-white/50 transition-all p-6 flex flex-col">
        <div class="flex justify-between items-center mb-8 pb-4 border-b border-gray-100/30 gap-6">
          <div class="flex items-center gap-4 flex-shrink-0">
            <div class="w-10 h-10 rounded-xl bg-blue-50 text-blue-500 flex items-center justify-center text-xl shadow-sm flex-shrink-0">
              <el-icon><Medal /></el-icon>
            </div>
            <div class="flex items-center gap-3">
              <h3 class="text-lg font-bold text-gray-800 whitespace-nowrap">获奖情况</h3>
              <span class="text-xs font-bold text-blue-500 bg-blue-50/50 px-2.5 py-0.5 rounded-full border border-blue-100/50 whitespace-nowrap">累计获奖 8 项</span>
            </div>
          </div>
          <el-input v-model="searchAward" placeholder="搜索获奖记录..." class="flex-1 max-w-[240px] search-input" :prefix-icon="Search" />
        </div>

        <!-- 列表 -->
        <div class="space-y-8">
          <div v-for="award in filteredAwards" :key="award.id" class="group relative">
            <div class="flex justify-between items-start mb-2">
              <div class="flex items-center gap-2">
                <h4 class="text-sm font-bold text-gray-800 group-hover:text-blue-600 transition-colors">{{ award.title }}</h4>
                <span :class="award.tagClass" class="px-1.5 py-0.5 rounded text-[10px] font-bold">{{ award.level }}</span>
              </div>
              <span class="text-[10px] text-gray-400 font-medium">{{ award.date }}</span>
            </div>
            <p class="text-xs text-gray-500 leading-relaxed">{{ award.description }}</p>
          </div>
          <div v-if="filteredAwards.length === 0" class="py-10 text-center text-gray-400 text-xs font-medium">未找到相关记录</div>
        </div>

        <div class="mt-auto pt-8 text-center">
          <button class="text-xs text-gray-400 hover:text-gray-600 font-medium transition-colors">查看更多记录</button>
        </div>
      </div>

      <!-- 评奖评优情况 -->
      <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 shadow-sm hover:bg-white/50 transition-all p-6 flex flex-col">
        <div class="flex justify-between items-center mb-8 pb-4 border-b border-gray-100/30 gap-6">
          <div class="flex items-center gap-4 flex-shrink-0">
            <div class="w-10 h-10 rounded-xl bg-orange-50 text-orange-500 flex items-center justify-center text-xl shadow-sm flex-shrink-0">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="flex items-center gap-3">
              <h3 class="text-lg font-bold text-gray-800 whitespace-nowrap">评奖评优情况</h3>
              <span class="text-xs font-bold text-green-600 bg-green-50/50 px-2.5 py-0.5 rounded-full border border-green-100/50 whitespace-nowrap">累计评优 6 项</span>
            </div>
          </div>
          <el-input v-model="searchHonor" placeholder="搜索评优记录..." class="flex-1 max-w-[240px] search-input" :prefix-icon="Search" />
        </div>

        <!-- 列表 -->
        <div class="space-y-8">
          <div v-for="honor in filteredHonors" :key="honor.id" class="group relative">
            <div class="flex justify-between items-start mb-2">
              <div class="flex items-center gap-2">
                <h4 class="text-sm font-bold text-gray-800 group-hover:text-green-600 transition-colors">{{ honor.title }}</h4>
                <span class="bg-green-50 text-green-600 px-1.5 py-0.5 rounded text-[10px] font-bold border border-green-100">{{ honor.level }}</span>
              </div>
              <span class="text-[10px] text-gray-400 font-medium">{{ honor.date }}</span>
            </div>
            <p class="text-xs text-gray-500 leading-relaxed">{{ honor.description }}</p>
          </div>
          <div v-if="filteredHonors.length === 0" class="py-10 text-center text-gray-400 text-xs font-medium">未找到相关记录</div>
        </div>

        <div class="mt-auto pt-8 text-center">
          <button class="text-xs text-gray-400 hover:text-gray-600 font-medium transition-colors">查看更多记录</button>
        </div>
      </div>
    </div>

    <!-- 第二排：违规记录情况 -->
    <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 shadow-sm hover:bg-white/50 transition-all p-6">
      <div class="flex justify-between items-center mb-8 pb-4 border-b border-gray-100/30 gap-6">
        <div class="flex items-center gap-4 flex-shrink-0">
          <div class="w-10 h-10 rounded-xl bg-red-50 text-red-500 flex items-center justify-center text-xl shadow-sm flex-shrink-0">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="flex items-center gap-3">
            <h3 class="text-lg font-bold text-gray-800 whitespace-nowrap">违规记录情况</h3>
            <span class="text-xs font-bold text-red-500 bg-red-50/50 px-2.5 py-0.5 rounded-full border border-red-100/50 whitespace-nowrap">累计违规 2 次</span>
          </div>
        </div>
        <el-input v-model="searchViolation" placeholder="搜索违规记录..." class="flex-1 max-w-[320px] search-input" :prefix-icon="Search" />
      </div>

      <!-- 表格 -->
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="text-[10px] text-gray-400 uppercase tracking-wider border-b border-gray-100/50">
              <th class="pb-3 font-bold px-4">违规时间</th>
              <th class="pb-3 font-bold px-4">违规类型</th>
              <th class="pb-3 font-bold px-4">违规描述</th>
              <th class="pb-3 font-bold px-4">处理结果</th>
              <th class="pb-3 font-bold px-4">处理时间</th>
              <th class="pb-3 font-bold px-4">备注</th>
            </tr>
          </thead>
          <tbody class="text-xs">
            <tr v-for="violation in filteredViolations" :key="violation.id" class="hover:bg-white/20 transition-colors">
              <td class="py-4 px-4 text-gray-500 font-medium">{{ violation.date }}</td>
              <td class="py-4 px-4 text-gray-800 font-bold">{{ violation.type }}</td>
              <td class="py-4 px-4 text-gray-500 leading-relaxed max-w-xs">{{ violation.description }}</td>
              <td class="py-4 px-4">
                <span class="bg-green-50 text-green-600 px-2 py-1 rounded-md font-bold border border-green-100">已处理</span>
              </td>
              <td class="py-4 px-4 text-gray-500 font-medium">{{ violation.handledDate }}</td>
              <td class="py-4 px-4 text-gray-500">{{ violation.remark }}</td>
            </tr>
            <tr v-if="filteredViolations.length === 0">
              <td colspan="6" class="py-10 text-center text-gray-400 text-xs font-medium">未找到相关记录</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="mt-8 pt-4 border-t border-gray-100/50 text-center">
        <button class="text-xs text-gray-400 hover:text-gray-600 font-medium transition-colors">查看更多记录</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Medal, Trophy, Warning, ArrowRight, Stamp, InfoFilled, Search } from '@element-plus/icons-vue'

const searchAward = ref('')
const searchHonor = ref('')
const searchViolation = ref('')

const awards = ref([
  {
    id: 1,
    title: '全国大学生数学建模竞赛省级一等奖',
    level: '省级',
    date: '2024-11',
    description: '2024年全国大学生数学建模竞赛四川省一等奖',
    iconColor: 'text-orange-400',
    tagClass: 'bg-orange-50 text-orange-600 border border-orange-100'
  },
  {
    id: 2,
    title: '西华师范大学优秀奖学金',
    level: '校级',
    date: '2024-10',
    description: '2023-2024学年第一学期优秀奖学金一等奖',
    iconColor: 'text-blue-400',
    tagClass: 'bg-blue-50 text-blue-600 border border-blue-100'
  },
  {
    id: 3,
    title: '“挑战杯”大学生创业计划竞赛校级铜奖',
    level: '校级',
    date: '2024-06',
    description: '第十四届“挑战杯”大学生创业计划竞赛校级铜奖',
    iconColor: 'text-orange-300',
    tagClass: 'bg-orange-50 text-orange-600 border border-orange-100'
  },
  {
    id: 4,
    title: '西华师范大学优秀学生干部',
    level: '校级',
    date: '2024-05',
    description: '2023-2024学年优秀学生干部',
    iconColor: 'text-orange-400',
    tagClass: 'bg-orange-50 text-orange-600 border border-orange-100'
  },
  {
    id: 5,
    title: '全国大学生英语竞赛三等奖',
    level: '国家级',
    date: '2024-04',
    description: '2024年全国大学生英语竞赛三等奖',
    iconColor: 'text-blue-300',
    tagClass: 'bg-red-50 text-red-600 border border-red-100'
  }
])

const honors = ref([
  {
    id: 1,
    title: '西华师范大学三好学生',
    level: '校级',
    date: '2024-10',
    description: '2023-2024学年三好学生'
  },
  {
    id: 2,
    title: '西华师范大学优秀共青团员',
    level: '校级',
    date: '2024-05',
    description: '2023-2024学年优秀共青团员'
  },
  {
    id: 3,
    title: '西华师范大学优秀志愿者',
    level: '校级',
    date: '2024-03',
    description: '2023年度优秀志愿者'
  },
  {
    id: 4,
    title: '西华师范大学文明寝室',
    level: '校级',
    date: '2023-11',
    description: '2023-2024学年文明寝室'
  },
  {
    id: 5,
    title: '西华师范大学社会实践先进个人',
    level: '校级',
    date: '2023-07',
    description: '2023年暑期社会实践先进个人'
  }
])

const violations = ref([
  {
    id: 1,
    date: '2024-03-15',
    type: '课堂纪律',
    description: '上课迟到\n第3-4节《高等数学》课程迟到',
    handledDate: '2024-03-16',
    remark: '给予口头批评教育'
  },
  {
    id: 2,
    date: '2023-11-28',
    type: '宿舍管理',
    description: '未按规定使用电器\n在宿舍使用大功率电器',
    handledDate: '2023-11-29',
    remark: '给予警告处分'
  }
])

const filteredAwards = computed(() => {
  return awards.value.filter(a => 
    a.title.includes(searchAward.value) || 
    a.description.includes(searchAward.value) ||
    a.level.includes(searchAward.value)
  )
})

const filteredHonors = computed(() => {
  return honors.value.filter(h => 
    h.title.includes(searchHonor.value) || 
    h.description.includes(searchHonor.value) ||
    h.level.includes(searchHonor.value)
  )
})

const filteredViolations = computed(() => {
  return violations.value.filter(v => 
    v.type.includes(searchViolation.value) || 
    v.description.includes(searchViolation.value) ||
    v.remark.includes(searchViolation.value)
  )
})
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

table th {
  white-space: nowrap;
}

:deep(.search-input .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.4) !important;
  box-shadow: none !important;
  border: 1px solid rgba(180, 180, 180, 0.4) !important;
  border-radius: 16px !important; /* macOS-style rounded-2xl */
  transition: all 0.3s ease;
}

:deep(.search-input .el-input__wrapper.is-focus) {
  background-color: rgba(255, 255, 255, 0.8) !important;
  border-color: #409eff !important;
  box-shadow: 0 0 0 1px #409eff !important;
}

/* 隐藏滚动条 */
.overflow-x-auto::-webkit-scrollbar {
  height: 4px;
}
.overflow-x-auto::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.05);
  border-radius: 10px;
}
</style>
