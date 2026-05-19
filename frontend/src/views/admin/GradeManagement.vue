<template>
  <div class="h-full flex flex-col">
    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in stats" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>

    <div class="flex items-center gap-3 mb-4">
      <button @click="importDialogVisible = true" class="bg-surface-container-high text-on-surface hover:bg-outline-variant/30 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 border border-outline-variant/20 shadow-sm">
        <el-icon :size="14"><Download /></el-icon>导出成绩
      </button>
      <button @click="importDialogVisible = true" class="bg-error text-on-error hover:bg-error/80 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><UploadFilled /></el-icon>批量导入成绩
      </button>
    </div>

    <!-- Search -->
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <el-input v-model="searchQuery" placeholder="搜索学号、姓名、课程..." class="max-w-xs" clearable>
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="filterSemester" placeholder="学期筛选" class="w-40" clearable>
        <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
      </el-select>
      <span class="text-xs text-secondary ml-auto">{{ filteredRecords.length }} 条记录</span>
    </div>

    <!-- Grade Table -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredRecords" style="width: 100%" height="100%" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="courseName" label="课程名称" width="180" />
        <el-table-column prop="courseType" label="类型" width="80" />
        <el-table-column prop="score" label="成绩" width="80">
          <template #default="{ row }">
            <span :class="row.score < 60 ? 'text-error font-bold' : 'text-on-surface'">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="预警" width="70">
          <template #default="{ row }">
            <span v-if="row.warning" class="text-[0.6875rem] font-bold px-2 py-0.5 rounded bg-error-container text-error">预警</span>
            <span v-else class="text-xs text-outline">-</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Import Dialog -->
    <el-dialog v-model="importDialogVisible" title="批量导入成绩" width="480px" destroy-on-close>
      <div class="text-center py-8">
        <el-icon :size="56" color="var(--color-outline-variant)"><UploadFilled /></el-icon>
        <p class="text-sm text-secondary mt-4 mb-2">支持 Excel (.xlsx) 格式</p>
        <p class="text-xs text-outline mb-6">请按照模板格式填写：学号、姓名、班级、课程名称、课程类型、成绩、学分、学期</p>
        <div class="border-2 border-dashed border-outline-variant/40 rounded-xl p-8 hover:border-error/40 transition-colors cursor-pointer bg-surface-container-low">
          <p class="text-sm text-secondary">拖拽文件到此处或 <span class="text-error font-semibold">点击选择文件</span></p>
        </div>
        <div class="mt-6 p-3 bg-surface-container-low rounded-lg text-left">
          <p class="text-xs font-bold text-secondary mb-2">导入说明</p>
          <ul class="text-xs text-secondary space-y-1">
            <li>· 系统将自动校验学号是否存在</li>
            <li>· 成绩低于60分将自动标记为预警</li>
            <li>· 重复记录将被跳过</li>
          </ul>
        </div>
        <button @click="importDialogVisible = false; ElMessage.success('演示模式：模拟导入 45 条成绩记录')" class="mt-6 px-6 py-2 bg-error text-on-error rounded-lg text-sm font-bold hover:bg-error/80 transition-colors">
          确认导入
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Download, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/admin'

const records = ref([])
const searchQuery = ref('')
const filterSemester = ref('')
const importDialogVisible = ref(false)
const semesters = ref(['2024-2025-1', '2024-2025-2', '2025-2026-1'])

const filteredRecords = computed(() => {
  let list = records.value
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(r => r.studentId.includes(q) || r.studentName.includes(q) || r.courseName.includes(q))
  }
  if (filterSemester.value) {
    list = list.filter(r => r.semester === filterSemester.value)
  }
  return list
})

const stats = computed(() => {
  const total = records.value.length
  const warning = records.value.filter(r => r.warning).length
  const avgScore = total > 0 ? (records.value.reduce((a, b) => a + b.score, 0) / total).toFixed(1) : 0
  const courses = new Set(records.value.map(r => r.courseName)).size
  return [
    { label: '成绩记录总数', value: total },
    { label: '预警记录数', value: warning },
    { label: '平均分', value: avgScore },
    { label: '课程数', value: courses },
  ]
})

onMounted(async () => {
  try {
    const res = await request.get(`${API}/grades`)
    records.value = res.data.data || []
  } catch {
    // 降级 mock
    records.value = [
      { id: 1, studentId: '202301042', studentName: '张小明', className: '软工2班', courseName: '数据结构', courseType: '必修', score: 95, credit: 4, semester: '2024-2025-1', warning: false },
      { id: 2, studentId: '202301042', studentName: '张小明', className: '软工2班', courseName: '算法设计与分析', courseType: '必修', score: 88, credit: 3, semester: '2024-2025-1', warning: false },
      { id: 3, studentId: '202301042', studentName: '张小明', className: '软工2班', courseName: '软件工程', courseType: '必修', score: 91, credit: 3, semester: '2024-2025-1', warning: false },
      { id: 4, studentId: '202301043', studentName: '李四', className: '软工2班', courseName: '数据结构', courseType: '必修', score: 45, credit: 4, semester: '2024-2025-1', warning: true },
      { id: 5, studentId: '202301043', studentName: '李四', className: '软工2班', courseName: '算法设计与分析', courseType: '必修', score: 55, credit: 3, semester: '2024-2025-1', warning: true },
      { id: 6, studentId: '202301044', studentName: '王五', className: '软工2班', courseName: '数据结构', courseType: '必修', score: 75, credit: 4, semester: '2024-2025-1', warning: false },
    ]
  }
})
</script>
