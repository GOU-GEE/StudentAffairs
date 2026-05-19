<template>
  <div class="h-full flex flex-col">
    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in statList" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>

    <div class="flex items-center gap-3 mb-4">
      <button class="bg-amber-500 text-white hover:bg-amber-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Download /></el-icon>导出名单
      </button>
    </div>

    <!-- Filter + Table -->
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <div class="flex gap-2">
        <button v-for="level in ['全部', 'A档', 'B档', 'C档']" :key="level" @click="filterLevel = level"
                class="px-4 py-1.5 rounded-lg text-sm font-semibold transition-colors"
                :class="filterLevel === level ? 'bg-amber-500 text-white' : 'text-secondary hover:bg-surface-container-high'">
          {{ level }}
        </button>
      </div>
      <el-input v-model="searchQuery" placeholder="搜索学号、姓名..." class="max-w-xs ml-auto" clearable>
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <!-- Student List -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredStudents" style="width: 100%" height="100%" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="college" label="学院" width="140" />
        <el-table-column prop="gradeClass" label="班级" width="120" />
        <el-table-column label="困难等级" width="100">
          <template #default="{ row }">
            <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="levelStyle(row.level)">{{ row.level }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="困难原因" min-width="160" />
        <el-table-column prop="annualIncome" label="家庭年收入" width="110" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <span class="text-xs font-bold" :class="row.status === '已认定' ? 'text-green-600' : 'text-amber-600'">{{ row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <button @click="viewDetail(row)" class="text-xs font-semibold text-secondary hover:text-amber-600 transition-colors mr-3">详情</button>
            <button v-if="row.status === '待认定'" @click="approveLevel(row)" class="text-xs font-semibold text-green-600 hover:text-green-800 transition-colors">认定</button>
            <button v-if="row.status === '已认定'" @click="changeLevel(row)" class="text-xs font-semibold text-secondary hover:text-amber-600 transition-colors">调整</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Detail Drawer -->
    <el-drawer v-model="detailVisible" title="困难学生详情" size="480px">
      <div v-if="detailStudent" class="space-y-4 px-2">
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">基本信息</p>
          <p class="text-sm"><span class="text-secondary">学号：</span>{{ detailStudent.studentId }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">姓名：</span>{{ detailStudent.name }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">学院：</span>{{ detailStudent.college }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">班级：</span>{{ detailStudent.gradeClass }}</p>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">困难信息</p>
          <p class="text-sm"><span class="text-secondary">等级：</span>{{ detailStudent.level }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">年收入：</span>{{ detailStudent.annualIncome }}</p>
          <p class="text-sm mt-1"><span class="text-secondary">人口：</span>{{ detailStudent.familySize }}人</p>
          <p class="text-sm mt-1"><span class="text-secondary">原因：</span>{{ detailStudent.reason }}</p>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">证明材料</p>
          <div class="flex items-center gap-2 text-sm text-secondary">
            <el-icon><Document /></el-icon> 困难认定申请表.pdf
            <button class="text-amber-600 text-xs font-semibold ml-auto hover:underline">下载</button>
          </div>
          <div class="flex items-center gap-2 text-sm text-secondary mt-2">
            <el-icon><Document /></el-icon> 家庭经济情况调查表.pdf
            <button class="text-amber-600 text-xs font-semibold ml-auto hover:underline">下载</button>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- Change Level Dialog -->
    <el-dialog v-model="levelDialogVisible" title="调整困难等级" width="400px" destroy-on-close>
      <el-select v-model="newLevel" class="w-full">
        <el-option label="A档（特别困难）" value="A档" />
        <el-option label="B档（比较困难）" value="B档" />
        <el-option label="C档（一般困难）" value="C档" />
      </el-select>
      <template #footer>
        <button @click="levelDialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="confirmChangeLevel" class="px-6 py-2 bg-amber-500 text-white rounded-lg text-sm font-bold hover:bg-amber-600 transition-colors">确认</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Search, Document } from '@element-plus/icons-vue'

const students = ref([
  { id: 1, studentId: '202301042', name: '张小明', college: '计算机学院', gradeClass: '2023级2班', level: 'A档', reason: '父母务农，家庭年收入不足1万元，父亲患病需长期用药', annualIncome: '0.8万', familySize: 5, status: '已认定' },
  { id: 2, studentId: '202301043', name: '李四', college: '计算机学院', gradeClass: '2023级2班', level: 'C档', reason: '单亲家庭，母亲一人抚养兄妹二人', annualIncome: '2.5万', familySize: 3, status: '已认定' },
  { id: 3, studentId: '202301048', name: '刘芳', college: '文学院', gradeClass: '2023级1班', level: 'B档', reason: '家庭经济来源单一，父亲打零工收入不稳定', annualIncome: '1.6万', familySize: 4, status: '已认定' },
  { id: 4, studentId: '202301049', name: '陈强', college: '数学与信息学院', gradeClass: '2024级3班', level: 'A档', reason: '建档立卡贫困户，父母均为残疾人', annualIncome: '0.5万', familySize: 4, status: '已认定' },
  { id: 5, studentId: '202401001', name: '新申请学生', college: '计算机学院', gradeClass: '2024级1班', level: '待认定', reason: '家庭突遭变故，经济状况急剧恶化', annualIncome: '待核实', familySize: 3, status: '待认定' },
])

const searchQuery = ref('')
const filterLevel = ref('全部')
const detailVisible = ref(false)
const detailStudent = ref(null)
const levelDialogVisible = ref(false)
const changingStudent = ref(null)
const newLevel = ref('')

const filteredStudents = computed(() => {
  let list = students.value
  if (filterLevel.value !== '全部') list = list.filter(s => s.level === filterLevel.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(s => s.studentId.includes(q) || s.name.includes(q))
  }
  return list
})

const levelStyle = (l) => ({
  'A档': 'bg-red-100 text-red-700',
  'B档': 'bg-amber-100 text-amber-700',
  'C档': 'bg-blue-100 text-blue-700',
  '待认定': 'bg-gray-100 text-gray-600',
})[l] || ''

const statList = computed(() => {
  const a = students.value.filter(s => s.level === 'A档').length
  const b = students.value.filter(s => s.level === 'B档').length
  const c = students.value.filter(s => s.level === 'C档').length
  const pending = students.value.filter(s => s.status === '待认定').length
  return [
    { label: '困难学生总数', value: students.value.length },
    { label: 'A档（特别困难）', value: a },
    { label: 'B档（比较困难）', value: b },
    { label: 'C档/待认定', value: c + pending },
  ]
})

const viewDetail = (student) => {
  detailStudent.value = student
  detailVisible.value = true
}

const approveLevel = (student) => {
  newLevel.value = 'C档'
  changingStudent.value = student
  levelDialogVisible.value = true
}

const changeLevel = (student) => {
  newLevel.value = student.level
  changingStudent.value = student
  levelDialogVisible.value = true
}

const confirmChangeLevel = () => {
  if (changingStudent.value) {
    changingStudent.value.level = newLevel.value
    changingStudent.value.status = '已认定'
    ElMessage.success('困难等级已更新')
  }
  levelDialogVisible.value = false
}
</script>
