<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
    <!-- 左侧：发布岗位 & 岗位管理 -->
    <div class="lg:col-span-2 flex flex-col gap-6">
      <!-- 1. 发布新岗位 -->
      <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="px-8 py-5 border-b border-gray-100 flex items-center justify-between bg-blue-50/20">
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 bg-blue-600 text-white rounded-lg flex items-center justify-center text-sm">+</div>
            <h3 class="text-base font-bold text-gray-900">发布新勤工助学岗位</h3>
          </div>
          <span class="text-xs text-gray-400">目前已有 {{ jobs.length }} 个岗位</span>
        </div>
        <div class="p-8">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-4 mb-6">
            <div>
              <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5 ml-1">岗位名称</label>
              <el-input v-model="jobForm.title" placeholder="如：图书馆资源整理员" />
            </div>
            <div>
              <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5 ml-1">所属部门</label>
              <el-input v-model="jobForm.department" placeholder="如：校图书馆" />
            </div>
            <div>
              <label class="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1.5 ml-1">招聘人数</label>
              <el-input-number v-model="jobForm.quota" :min="1" :max="50" class="!w-full" />
            </div>
          </div>
          <div class="flex justify-end">
            <el-button type="primary" class="!px-10 !py-6 !rounded-2xl shadow-lg shadow-blue-100 font-bold" @click="publishJob">确认发布岗位</el-button>
          </div>
        </div>
      </div>

      <!-- 2. 在招岗位列表 -->
      <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden flex-1">
        <div class="px-8 py-5 border-b border-gray-100 flex items-center justify-between">
          <h3 class="text-base font-bold text-gray-900">岗位管理</h3>
          <div class="flex gap-2">
            <el-tag size="small" type="success">运行中: {{ runningCount }}</el-tag>
            <el-tag size="small" type="info">已满: {{ fullCount }}</el-tag>
          </div>
        </div>
        <div class="p-4">
          <el-table :data="jobs" style="width: 100%" size="small">
            <el-table-column prop="title" label="岗位名称" width="160" />
            <el-table-column prop="department" label="部门" />
            <el-table-column label="申请/名额">
              <template #default="{ row }">
                <span :class="row.currentCount >= row.quota ? 'text-red-500 font-bold' : 'text-blue-600'">{{ row.currentCount }} / {{ row.quota }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <span class="text-[11px] px-2 py-0.5 rounded font-bold"
                      :class="row.status === 'FULL' ? 'bg-red-50 text-red-600' : 'bg-green-50 text-green-600'">
                  {{ row.status === 'FULL' ? '已满' : '开放中' }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- 右侧：学生申请审批 -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden flex flex-col">
      <div class="px-6 py-5 border-b border-gray-100">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-base font-bold text-gray-900">待审核申请</h3>
          <span class="text-[10px] font-black bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">{{ applications.length }} 份</span>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto p-4 space-y-3">
        <div
          v-for="app in applications"
          :key="app.id"
          @click="selectedApp = app"
          class="p-4 rounded-2xl border transition-all cursor-pointer group"
          :class="selectedApp?.id === app.id ? 'border-blue-500 bg-blue-50/30' : 'border-gray-50 bg-gray-50/30 hover:border-blue-200'"
        >
          <div class="flex justify-between items-start mb-2">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold text-xs">{{ app.studentId.substring(0,1) }}</div>
              <div>
                <h4 class="text-sm font-bold text-gray-900">{{ app.studentId }}</h4>
                <p class="text-[10px] text-gray-400">岗位ID: {{ app.jobId }}</p>
              </div>
            </div>
            <span class="text-[10px] font-bold px-2 py-0.5 rounded-full"
                  :class="app.status === 'PENDING' ? 'bg-orange-50 text-orange-600' : app.status === 'APPROVED' ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'">
              {{ app.status === 'PENDING' ? '待审' : app.status === 'APPROVED' ? '通过' : '驳回' }}
            </span>
          </div>
          <div v-if="selectedApp?.id === app.id && app.status === 'PENDING'" class="animate-in fade-in duration-200 flex gap-2 mt-3">
            <el-button type="primary" size="small" class="flex-1 !rounded-lg" @click.stop="review(app, 'APPROVED')">通过</el-button>
            <el-button type="danger" size="small" plain class="flex-1 !rounded-lg" @click.stop="review(app, 'REJECTED')">驳回</el-button>
          </div>
        </div>
        <div v-if="applications.length === 0" class="py-10 text-center text-gray-400 text-sm">
          暂无申请
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const API = '/api/financial-aid'

const jobForm = ref({ title: '', department: '', quota: 5 })
const jobs = ref([])
const applications = ref([])
const selectedApp = ref(null)

const runningCount = computed(() => jobs.value.filter(j => j.status === 'ACTIVE').length)
const fullCount = computed(() => jobs.value.filter(j => j.status === 'FULL').length)

const loadData = async () => {
  try {
    const [jobRes, appRes] = await Promise.all([
      request.get(`${API}/jobs`),
      request.get(`${API}/applications`)
    ])
    if (jobRes.data.code === 200) jobs.value = jobRes.data.data
    if (appRes.data.code === 200) applications.value = appRes.data.data
  } catch (e) { console.error(e) }
}

const publishJob = async () => {
  if (!jobForm.value.title || !jobForm.value.department) {
    ElMessage.warning('请填写岗位名称和部门')
    return
  }
  try {
    await request.post(`${API}/jobs`, jobForm.value)
    ElMessage.success('岗位发布成功')
    jobForm.value = { title: '', department: '', quota: 5 }
    await loadData()
  } catch (e) { ElMessage.error('发布失败') }
}

const review = async (app, status) => {
  try {
    await request.put(`${API}/applications/${app.id}/review`, { status })
    ElMessage.success(status === 'APPROVED' ? '已通过' : '已驳回')
    selectedApp.value = null
    await loadData()
  } catch (e) { ElMessage.error('操作失败') }
}

onMounted(loadData)
</script>
