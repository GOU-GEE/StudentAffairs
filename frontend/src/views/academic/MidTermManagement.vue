<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="batchReview" class="bg-purple-500 text-white hover:bg-purple-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><DocumentChecked /></el-icon>批量审核
      </button>
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
      <el-select v-model="statusFilter" placeholder="状态" class="w-32" clearable>
        <el-option label="待审核" value="待审核" /><el-option label="已通过" value="已通过" /><el-option label="待修改" value="待修改" />
      </el-select>
      <span class="text-xs text-secondary ml-auto">{{ filteredList.length }} 条</span>
    </div>

    <!-- Table -->
    <div class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden flex-1">
      <el-table :data="filteredList" style="width:100%" height="100%" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column label="思想表现" width="90"><template #default="{ row }"><span :class="scoreColor(row.scores[0])">{{ row.scores[0] }}</span></template></el-table-column>
        <el-table-column label="学业成绩" width="90"><template #default="{ row }"><span :class="scoreColor(row.scores[1])">{{ row.scores[1] }}</span></template></el-table-column>
        <el-table-column label="综合表现" width="90"><template #default="{ row }"><span :class="scoreColor(row.scores[2])">{{ row.scores[2] }}</span></template></el-table-column>
        <el-table-column label="状态" width="90"><template #default="{ row }"><span class="text-xs font-bold" :class="statusClass(row.status)">{{ row.status }}</span></template></el-table-column>
        <el-table-column label="提交时间" width="140" prop="submitTime" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <button @click="viewDetail(row)" class="text-xs font-semibold text-purple-600 hover:text-purple-800 mr-3">查看</button>
            <button v-if="row.status==='待审核'" @click="approve(row)" class="text-xs font-semibold text-green-600 hover:text-green-800 mr-2">通过</button>
            <button v-if="row.status==='待审核'" @click="reject(row)" class="text-xs font-semibold text-red-500 hover:text-red-700">退回</button>
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
          <p class="text-sm leading-relaxed">{{ detail.content }}</p>
        </div>
        <div class="bg-surface-container-low rounded-xl p-4">
          <p class="text-xs font-bold text-secondary uppercase mb-2">自评等级</p>
          <div class="flex gap-4 text-sm"><span>思想表现：{{ detail.scores[0] }}</span><span>学业成绩：{{ detail.scores[1] }}</span><span>综合表现：{{ detail.scores[2] }}</span></div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'; import { ElMessage } from 'element-plus'; import { DocumentChecked, Search } from '@element-plus/icons-vue'

const searchQuery = ref(''); const statusFilter = ref(''); const detailVisible = ref(false); const detail = ref(null); const selectedRows = ref([])

const list = ref([
  { id:1, studentName:'张小明', studentId:'202301042', className:'软工2班', scores:['优秀','优秀','优秀'], status:'已通过', submitTime:'2026-05-10', content:'本学期在思想方面积极向党组织靠拢，严格遵守校规校纪...在学习方面认真完成课程任务，GPA保持在3.85...在第二课堂方面参加了ACM竞赛训练和志愿服务活动...' },
  { id:2, studentName:'李四', studentId:'202301043', className:'软工2班', scores:['良好','合格','合格'], status:'待审核', submitTime:'2026-05-12', content:'本学期思想认识有所提高，基本做到遵纪守法...学习方面因部分课程基础薄弱，存在挂科情况，正在努力追赶...' },
  { id:3, studentName:'王五', studentId:'202301044', className:'软工1班', scores:['良好','优秀','良好'], status:'待审核', submitTime:'2026-05-11', content:'本学期思想政治表现良好，积极参加团组织活动...学业成绩稳步提升，各科成绩均达到70分以上...参加了校园篮球赛和敬老院志愿服务...' },
  { id:4, studentName:'赵六', studentId:'202301045', className:'软工1班', scores:['优秀','良好','优秀'], status:'待修改', submitTime:'2026-05-08', content:'自述内容不够详实，需补充志愿服务和社会实践方面的具体经历。' },
])

const filteredList = computed(() => {
  let l = list.value
  if (searchQuery.value) { const q = searchQuery.value.toLowerCase(); l = l.filter(x => x.studentName.includes(q) || x.studentId.includes(q)) }
  if (statusFilter.value) l = l.filter(x => x.status === statusFilter.value)
  return l
})

const stats = computed(() => [
  { label:'总提交数', value:list.value.length },
  { label:'待审核', value:list.value.filter(x=>x.status==='待审核').length },
  { label:'已通过', value:list.value.filter(x=>x.status==='已通过').length },
  { label:'待修改', value:list.value.filter(x=>x.status==='待修改').length },
])

const scoreColor = (s) => ({ '优秀':'text-green-600','良好':'text-blue-600','合格':'text-amber-600','待改进':'text-red-500' }[s]||'')
const statusClass = (s) => ({ '待审核':'text-amber-600','已通过':'text-green-600','待修改':'text-red-500' }[s]||'')

const handleSelectionChange = (rows) => { selectedRows.value = rows }
const approve = (row) => { row.status = '已通过'; ElMessage.success(`已通过 ${row.studentName} 的中期鉴定`) }
const reject = (row) => { row.status = '待修改'; ElMessage.info(`已退回 ${row.studentName} 的中期鉴定`) }
const batchReview = () => {
  if (selectedRows.value.length === 0) { ElMessage.warning('请先选择记录'); return }
  selectedRows.value.forEach(r => { if (r.status === '待审核') r.status = '已通过' })
  ElMessage.success(`已批量通过 ${selectedRows.value.length} 条记录`)
}
const viewDetail = (row) => { detail.value = row; detailVisible.value = true }
</script>
