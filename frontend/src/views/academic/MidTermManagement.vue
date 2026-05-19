<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <!-- No batch actions needed - view only -->
    </teleport>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in stats" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>

    <!-- Filter -->
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <el-input v-model="searchQuery" placeholder="搜索姓名、学号..." class="max-w-xs" clearable>
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <span class="text-xs text-secondary ml-auto">{{ filteredList.length }} 条</span>
    </div>

    <!-- Table -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredList" style="width:100%" height="100%" stripe>
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column label="思想表现" width="90"><template #default="{ row }"><span :class="scoreColor(row.thoughtPerformance)">{{ row.thoughtPerformance }}</span></template></el-table-column>
        <el-table-column label="学业成绩" width="90"><template #default="{ row }"><span :class="scoreColor(row.academicPerformance)">{{ row.academicPerformance }}</span></template></el-table-column>
        <el-table-column label="综合表现" width="90"><template #default="{ row }"><span :class="scoreColor(row.overallPerformance)">{{ row.overallPerformance }}</span></template></el-table-column>
        <el-table-column label="提交时间" width="160">
          <template #default="{ row }">
            <span class="text-xs text-secondary">{{ row.submitTime ? new Date(row.submitTime).toLocaleString('zh-CN') : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <button @click="viewDetail(row)" class="text-xs font-semibold text-purple-600 hover:text-purple-800">查看</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Detail Drawer -->
    <el-drawer v-model="detailVisible" title="中期鉴定详情" size="520px">
      <div v-if="detail" class="space-y-4 px-2">
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">基本信息</p>
          <p class="text-sm"><span class="text-secondary">姓名：</span>{{ detail.studentName }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">学号：</span>{{ detail.studentId }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">班级：</span>{{ detail.className }}</p>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">自我鉴定</p>
          <p class="text-sm leading-relaxed">{{ detail.selfAssessment || '无' }}</p>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">自评等级</p>
          <div class="flex gap-4 text-sm"><span>思想表现：{{ detail.thoughtPerformance || '-' }}</span><span>学业成绩：{{ detail.academicPerformance || '-' }}</span><span>综合表现：{{ detail.overallPerformance || '-' }}</span></div>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">提交时间</p>
          <p class="text-sm">{{ detail.submitTime ? new Date(detail.submitTime).toLocaleString('zh-CN') : '未提交' }}</p>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { DocumentChecked, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/mid-term'

const searchQuery = ref('')
const detailVisible = ref(false)
const detail = ref(null)
const list = ref([])

const loadList = async () => {
  try {
    const res = await axios.get(`${API}/all`)
    if (res.data.code === 200) list.value = res.data.data
  } catch (e) { console.error(e) }
}

const filteredList = computed(() => {
  let l = list.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    l = l.filter(x => x.studentName.toLowerCase().includes(q) || x.studentId.toLowerCase().includes(q))
  }
  return l
})

const stats = computed(() => [
  { label:'总提交数', value:list.value.length },
  { label:'今日提交', value:list.value.filter(x => {
    const today = new Date().toDateString()
    return x.submitTime && new Date(x.submitTime).toDateString() === today
  }).length },
  { label:'涉及班级', value:new Set(list.value.map(x=>x.className)).size },
  { label:'涉及学生', value:new Set(list.value.map(x=>x.studentId)).size },
])

const scoreColor = (s) => ({ '优秀':'text-green-600','良好':'text-blue-600','合格':'text-amber-600','待改进':'text-red-500' }[s]||'')
const viewDetail = (row) => { detail.value = row; detailVisible.value = true }

onMounted(loadList)
</script>
