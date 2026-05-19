<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="openBatchDialog" class="bg-emerald-500 text-white hover:bg-emerald-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><UploadFilled /></el-icon>批量发放学时
      </button>
    </teleport>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in statsRecord" :key="idx" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-2xl font-bold text-on-surface mt-1">{{ stat.value }}</h3>
      </div>
    </div>

    <!-- Category Progress -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
      <div v-for="cat in categories" :key="cat.name" class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 flex items-center gap-4">
        <div class="w-12 h-12 rounded-xl flex items-center justify-center" :class="cat.bgClass">
          <el-icon :size="24" :color="cat.color"><component :is="cat.icon" /></el-icon>
        </div>
        <div class="flex-1">
          <div class="flex justify-between mb-1">
            <span class="text-sm font-semibold text-on-surface">{{ cat.name }}</span>
            <span class="text-xs text-secondary">{{ cat.distributed }}/{{ cat.total }} 学时</span>
          </div>
          <el-progress :percentage="cat.percent" :stroke-width="8" :color="cat.color" />
        </div>
      </div>
    </div>

    <!-- Student Hours Table -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <div class="px-6 py-4 border-b border-outline-variant/10 flex items-center gap-4">
        <h3 class="text-[1.125rem] font-semibold text-on-surface">学生学时记录</h3>
        <el-input v-model="searchQuery" placeholder="搜索学号、姓名..." class="max-w-xs" clearable>
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
      <el-table :data="filteredRecords" style="width: 100%" height="100%" stripe>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column label="思想素质" width="90"><template #default="{ row }"><span>{{ row.hours[0] }}</span></template></el-table-column>
        <el-table-column label="文艺体育" width="90"><template #default="{ row }"><span>{{ row.hours[1] }}</span></template></el-table-column>
        <el-table-column label="创新创造" width="90"><template #default="{ row }"><span>{{ row.hours[2] }}</span></template></el-table-column>
        <el-table-column label="志愿服务" width="90"><template #default="{ row }"><span>{{ row.hours[3] }}</span></template></el-table-column>
        <el-table-column label="劳动教育" width="90"><template #default="{ row }"><span>{{ row.hours[4] }}</span></template></el-table-column>
        <el-table-column label="技能特长" width="90"><template #default="{ row }"><span>{{ row.hours[5] }}</span></template></el-table-column>
        <el-table-column label="合计" width="80"><template #default="{ row }"><span class="font-bold text-emerald-600">{{ row.hours.reduce((a,b) => a+b, 0) }}</span></template></el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <button @click="openGrantDialog(row)" class="text-xs font-semibold text-emerald-600 hover:text-emerald-800 transition-colors">发放学时</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Grant Dialog -->
    <el-dialog v-model="grantDialogVisible" title="发放第二课堂学时" width="480px" destroy-on-close>
      <div class="space-y-4">
        <div class="bg-surface-container-low rounded-xl p-3 text-sm">
          <span class="font-bold text-secondary">学生：</span>{{ grantStudent?.studentName }} ({{ grantStudent?.studentId }})
        </div>
        <el-form label-position="top">
          <el-form-item label="学时类别" required>
            <el-select v-model="grantForm.category" class="w-full">
              <el-option v-for="(cat, idx) in categoryNames" :key="idx" :label="cat" :value="idx" />
            </el-select>
          </el-form-item>
          <el-form-item label="学时数" required>
            <el-input-number v-model="grantForm.hours" :min="1" :max="50" class="w-full" />
          </el-form-item>
          <el-form-item label="发放说明">
            <el-input v-model="grantForm.reason" type="textarea" :rows="2" placeholder="如：参与校园志愿服务4小时" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <button @click="grantDialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="confirmGrant" class="px-6 py-2 bg-emerald-500 text-white rounded-lg text-sm font-bold hover:bg-emerald-600 transition-colors">确认发放</button>
      </template>
    </el-dialog>

    <!-- Batch Dialog -->
    <el-dialog v-model="batchDialogVisible" title="批量发放学时" width="480px" destroy-on-close>
      <div class="text-center py-8">
        <el-icon :size="56" color="#10b981"><UploadFilled /></el-icon>
        <p class="text-sm text-secondary mt-4 mb-2">支持 Excel (.xlsx) 格式</p>
        <p class="text-xs text-outline mb-6">模板格式：学号、姓名、学时类别、学时数、说明</p>
        <div class="border-2 border-dashed border-outline-variant/40 rounded-xl p-8 hover:border-emerald-500/40 transition-colors cursor-pointer bg-surface-container-low">
          <p class="text-sm text-secondary">拖拽文件或 <span class="text-emerald-600 font-semibold">点击选择</span></p>
        </div>
        <button @click="batchDialogVisible = false; ElMessage.success('演示：成功为 12 名学生发放 48 学时')" class="mt-6 px-6 py-2 bg-emerald-500 text-white rounded-lg text-sm font-bold hover:bg-emerald-600 transition-colors">确认导入</button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Search, Star, Picture, Sunny, Help, School, EditPen } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/youth/second-classroom'

const searchQuery = ref('')
const grantDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const grantStudent = ref(null)
const grantForm = ref({ category: 3, hours: 2, reason: '' })
const categoryNames = ['思想素质', '文艺体育', '创新创造', '志愿服务', '劳动教育', '技能特长']

const categories = [
  { name: '思想素质', icon: Star, color: '#f59e0b', bgClass: 'bg-amber-100', total: 120, distributed: 85 },
  { name: '文艺体育', icon: Picture, color: '#8b5cf6', bgClass: 'bg-purple-100', total: 100, distributed: 62 },
  { name: '创新创造', icon: Sunny, color: '#3b82f6', bgClass: 'bg-blue-100', total: 80, distributed: 45 },
  { name: '志愿服务', icon: Help, color: '#10b981', bgClass: 'bg-emerald-100', total: 160, distributed: 120 },
  { name: '劳动教育', icon: School, color: '#ef4444', bgClass: 'bg-red-100', total: 100, distributed: 48 },
  { name: '技能特长', icon: EditPen, color: '#06b6d4', bgClass: 'bg-cyan-100', total: 60, distributed: 25 },
].map(c => ({ ...c, percent: Math.round(c.distributed / c.total * 100) }))

const students = ref([])
const rawRecords = ref([])

const fetchRecords = async () => {
  try {
    const res = await request.get(`${API}/records`)
    if (res.data.code === 200) {
      rawRecords.value = res.data.data
      aggregateStudents(res.data.data)
    }
  } catch (e) {
    console.error('Failed to fetch records', e)
  }
}

const aggregateStudents = (records) => {
  const map = {}
  records.forEach(r => {
    if (!map[r.studentId]) {
      map[r.studentId] = { studentId: r.studentId, studentName: r.studentName, className: r.className, hours: [0, 0, 0, 0, 0, 0] }
    }
    map[r.studentId].hours[r.categoryIndex] += r.hours
  })
  students.value = Object.values(map)
}

onMounted(() => {
  fetchRecords()
})

const filteredRecords = computed(() => {
  if (!searchQuery.value) return students.value
  const q = searchQuery.value.toLowerCase()
  return students.value.filter(s => s.studentId.includes(q) || s.studentName.includes(q))
})

const statsRecord = computed(() => {
  const total = students.value.reduce((a, s) => a + s.hours.reduce((x, y) => x + y, 0), 0)
  return [
    { label: '已发放总学时', value: total },
    { label: '覆盖学生数', value: students.value.length },
    { label: '学时类别', value: '6类' },
    { label: '本月新增', value: 128 },
  ]
})

const openGrantDialog = (student) => {
  grantStudent.value = student
  grantForm.value = { category: 3, hours: 2, reason: '' }
  grantDialogVisible.value = true
}

const confirmGrant = async () => {
  if (grantStudent.value) {
    try {
      const payload = {
        studentId: grantStudent.value.studentId,
        studentName: grantStudent.value.studentName,
        className: grantStudent.value.className,
        categoryIndex: grantForm.value.category,
        hours: grantForm.value.hours,
        reason: grantForm.value.reason
      }
      await request.post(`${API}/grant`, payload)
      ElMessage.success(`已为 ${grantStudent.value.studentName} 发放 ${grantForm.value.hours} 学时`)
      fetchRecords()
      grantDialogVisible.value = false
    } catch (e) {
      ElMessage.error('发放失败')
    }
  }
}
</script>
