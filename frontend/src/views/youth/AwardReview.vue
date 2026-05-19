<template>
  <div class="h-full flex flex-col">
    <div class="flex items-center gap-3 mb-4">
      <button class="bg-emerald-500 text-white hover:bg-emerald-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Download /></el-icon>导出记录
      </button>
    </div>

    <!-- Filter Tabs -->
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <div class="flex gap-2">
        <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
          class="px-4 py-1.5 rounded-lg text-sm font-semibold transition-colors"
          :class="activeTab === tab.key ? 'bg-emerald-500 text-white' : 'text-secondary hover:bg-surface-container-high'">
          {{ tab.label }} ({{ tab.count }})
        </button>
      </div>
      <el-input v-model="searchQuery" placeholder="搜索学号、获奖名称..." class="max-w-xs ml-auto" clearable>
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <!-- Awards Table -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredAwards" style="width: 100%" height="100%" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="awardName" label="获奖名称" width="200" />
        <el-table-column label="级别" width="80">
          <template #default="{ row }">
            <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="levelStyle(row.level)">{{ row.level }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="类别" width="100" />
        <el-table-column prop="awardTime" label="获奖时间" width="110" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span class="text-xs font-bold" :class="row.status === 'PENDING' ? 'text-amber-600' : row.status === 'APPROVED' ? 'text-emerald-600' : 'text-red-500'">
              {{ ({ PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' })[row.status] }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <button v-if="row.status === 'PENDING'" @click="handleReview(row, 'APPROVED')" class="text-xs font-semibold text-emerald-600 hover:text-emerald-800 transition-colors mr-3">通过</button>
            <button v-if="row.status === 'PENDING'" @click="handleReview(row, 'REJECTED')" class="text-xs font-semibold text-red-500 hover:text-red-700 transition-colors mr-3">驳回</button>
            <button @click="viewDetail(row)" class="text-xs font-semibold text-secondary hover:text-emerald-600 transition-colors">详情</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Detail Drawer -->
    <el-drawer v-model="detailVisible" title="获奖详情" size="480px">
      <div v-if="detailAward" class="space-y-4 px-2">
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">基本信息</p>
          <p class="text-sm"><span class="text-secondary">学号：</span>{{ detailAward.studentId }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">姓名：</span>{{ detailAward.studentName }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">获奖名称：</span>{{ detailAward.awardName }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">级别：</span>{{ detailAward.level }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">类别：</span>{{ detailAward.category }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">时间：</span>{{ detailAward.awardTime }}</p>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">获奖简述</p>
          <p class="text-sm leading-relaxed">{{ detailAward.description }}</p>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">证明材料</p>
          <div class="flex items-center gap-2 text-sm text-secondary">
            <el-icon><Document /></el-icon> 获奖证书.jpg
            <button class="text-emerald-600 text-xs font-semibold ml-auto hover:underline">查看</button>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Search, Document } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/youth/awards'

const awards = ref([])

const fetchAwards = async () => {
  try {
    const res = await request.get(API)
    if (res.data.code === 200) {
      awards.value = res.data.data
    }
  } catch (e) {
    console.error('Failed to fetch awards', e)
  }
}

onMounted(() => {
  fetchAwards()
})

const searchQuery = ref('')
const activeTab = ref('all')
const detailVisible = ref(false)
const detailAward = ref(null)

const tabs = computed(() => [
  { key: 'all', label: '全部', count: awards.value.length },
  { key: 'PENDING', label: '待审核', count: awards.value.filter(a => a.status === 'PENDING').length },
  { key: 'APPROVED', label: '已通过', count: awards.value.filter(a => a.status === 'APPROVED').length },
  { key: 'REJECTED', label: '已驳回', count: awards.value.filter(a => a.status === 'REJECTED').length },
])

const filteredAwards = computed(() => {
  let list = awards.value
  if (activeTab.value !== 'all') list = list.filter(a => a.status === activeTab.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(a => a.studentId.includes(q) || a.studentName.includes(q) || a.awardName.includes(q))
  }
  return list
})

const levelStyle = (l) => ({
  '国家级': 'bg-red-100 text-red-700',
  '省级': 'bg-amber-100 text-amber-700',
  '市级': 'bg-blue-100 text-blue-700',
  '校级': 'bg-emerald-100 text-emerald-700',
  '院级': 'bg-gray-100 text-gray-600',
}[l] || '')

const handleReview = async (award, action) => {
  try {
    await request.put(`${API}/${award.id}/review`, { status: action })
    ElMessage.success(action === 'APPROVED' ? '已通过审核' : '已驳回')
    fetchAwards()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const viewDetail = (award) => {
  detailAward.value = award
  detailVisible.value = true
}
</script>
