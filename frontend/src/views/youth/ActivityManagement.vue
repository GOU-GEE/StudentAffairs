<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button @click="openAddDialog" class="bg-emerald-500 text-white hover:bg-emerald-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Plus /></el-icon>发布活动
      </button>
    </teleport>

    <!-- Tabs -->
    <div class="bg-surface-container-lowest rounded-xl p-4 border border-outline-variant/15 mb-6 flex items-center gap-4">
      <div class="flex gap-2">
        <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
          class="px-4 py-1.5 rounded-lg text-sm font-semibold transition-colors"
          :class="activeTab === tab.key ? 'bg-emerald-500 text-white' : 'text-secondary hover:bg-surface-container-high'">
          {{ tab.label }}
        </button>
      </div>
      <el-input v-model="searchQuery" placeholder="搜索活动名称..." class="max-w-xs ml-auto" clearable>
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
    </div>

    <!-- Activity Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 flex-1 content-start">
      <div v-for="act in filteredActivities" :key="act.id" class="bg-surface-container-lowest rounded-xl border border-outline-variant/15 overflow-hidden hover:shadow-md transition-shadow">
        <div class="h-36 bg-gradient-to-br" :class="act.bgGradient"></div>
        <div class="p-4">
          <div class="flex items-start justify-between mb-2">
            <h4 class="font-semibold text-on-surface">{{ act.title }}</h4>
            <span class="text-xs font-bold px-2 py-0.5 rounded-full shrink-0" :class="statusClass(act.status)">{{ act.status }}</span>
          </div>
          <p class="text-xs text-secondary mb-3 line-clamp-2">{{ act.description }}</p>
          <div class="flex items-center text-xs text-secondary gap-3">
            <span class="flex items-center gap-1"><el-icon :size="12"><Calendar /></el-icon>{{ act.date }}</span>
            <span class="flex items-center gap-1"><el-icon :size="12"><Location /></el-icon>{{ act.location }}</span>
          </div>
          <div class="flex justify-between items-center mt-3 pt-3 border-t border-outline-variant/10">
            <span class="text-xs text-secondary">{{ act.participants }}/{{ act.maxParticipants }} 人报名</span>
            <div class="flex gap-2">
              <button @click="openEditDialog(act)" class="text-xs font-semibold text-secondary hover:text-emerald-600 transition-colors">编辑</button>
              <button @click="toggleStatus(act)" class="text-xs font-semibold text-secondary hover:text-emerald-600 transition-colors">
                {{ act.status === '已结束' ? '重新开放' : '关闭' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="editingAct ? '编辑活动' : '发布新活动'" width="560px" destroy-on-close>
      <el-form :model="form" label-position="top">
        <el-form-item label="活动名称" required>
          <el-input v-model="form.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入活动描述" />
        </el-form-item>
        <div class="grid grid-cols-2 gap-x-4">
          <el-form-item label="活动日期">
            <el-date-picker v-model="form.date" type="date" class="w-full" placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="地点">
            <el-input v-model="form.location" placeholder="活动地点" />
          </el-form-item>
        </div>
        <div class="grid grid-cols-3 gap-x-4">
          <el-form-item label="最大报名人数">
            <el-input-number v-model="form.maxParticipants" :min="1" :max="500" class="w-full" />
          </el-form-item>
          <el-form-item label="学分数">
            <el-input-number v-model="form.credits" :min="0" :max="10" :precision="1" class="w-full" />
          </el-form-item>
          <el-form-item label="活动级别">
            <el-select v-model="form.level" class="w-full">
              <el-option label="校级" value="校级" />
              <el-option label="院级" value="院级" />
              <el-option label="班级" value="班级" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <button @click="dialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
        <button @click="saveActivity" class="px-6 py-2 bg-emerald-500 text-white rounded-lg text-sm font-bold hover:bg-emerald-600 transition-colors">{{ editingAct ? '保存' : '发布' }}</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, Calendar, Location } from '@element-plus/icons-vue'

const searchQuery = ref('')
const activeTab = ref('全部')
const dialogVisible = ref(false)
const editingAct = ref(null)
const form = ref({ title: '', description: '', date: '', location: '', maxParticipants: 50, credits: 1, level: '校级' })

const tabs = [
  { key: '全部', label: '全部' },
  { key: '报名中', label: '报名中' },
  { key: '进行中', label: '进行中' },
  { key: '已结束', label: '已结束' },
]

const activities = ref([
  { id: 1, title: '第十七届电脑文化艺术节', description: '面向全校学生的电脑文化艺术节，包含程序设计竞赛、网页设计、数字艺术等多个比赛项目。', date: '2026-05-20', location: '北湖校区体育馆', participants: 156, maxParticipants: 200, status: '报名中', credits: 2, level: '校级', bgGradient: 'from-emerald-400 to-teal-500' },
  { id: 2, title: 'AI赋能未来主题讲座', description: '邀请知名AI企业技术专家来校讲座，分享人工智能最新发展趋势与应用场景。', date: '2026-05-18', location: '学术报告厅', participants: 89, maxParticipants: 120, status: '进行中', credits: 0.5, level: '校级', bgGradient: 'from-blue-400 to-indigo-500' },
  { id: 3, title: '红色经典诵读比赛', description: '为弘扬爱国主义精神，举办红色经典文学作品诵读比赛。', date: '2026-05-12', location: '文科楼报告厅', participants: 45, maxParticipants: 50, status: '已结束', credits: 1, level: '院级', bgGradient: 'from-red-400 to-rose-500' },
  { id: 4, title: '3V3班级篮球赛', description: '各班级组队参加3V3篮球比赛，增强班级凝聚力和体育精神。', date: '2026-05-10', location: '北湖校区篮球场', participants: 120, maxParticipants: 120, status: '已结束', credits: 1.5, level: '校级', bgGradient: 'from-orange-400 to-amber-500' },
  { id: 5, title: '敬老院志愿服务', description: '组织学生前往敬老院开展志愿服务活动，传递爱心关怀。', date: '2026-05-25', location: '顺庆区敬老院', participants: 12, maxParticipants: 30, status: '报名中', credits: 2, level: '院级', bgGradient: 'from-purple-400 to-violet-500' },
  { id: 6, title: '青年志愿者培训营', description: '面向新注册志愿者的培训活动，强化志愿服务意识和基本技能。', date: '2026-05-28', location: '学工处会议室', participants: 0, maxParticipants: 40, status: '报名中', credits: 1, level: '校级', bgGradient: 'from-teal-400 to-cyan-500' },
])

const filteredActivities = computed(() => {
  let list = activities.value
  if (activeTab.value !== '全部') list = list.filter(a => a.status === activeTab.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(a => a.title.includes(q))
  }
  return list
})

const statusClass = (s) => ({
  '报名中': 'bg-emerald-100 text-emerald-700',
  '进行中': 'bg-blue-100 text-blue-700',
  '已结束': 'bg-gray-100 text-gray-500',
}[s] || '')

const openAddDialog = () => {
  editingAct.value = null
  form.value = { title: '', description: '', date: '', location: '', maxParticipants: 50, credits: 1, level: '校级' }
  dialogVisible.value = true
}

const openEditDialog = (act) => {
  editingAct.value = act
  form.value = { ...act }
  dialogVisible.value = true
}

const saveActivity = () => {
  if (!form.value.title) { ElMessage.warning('活动名称为必填'); return }
  ElMessage.success(editingAct.value ? '活动已更新' : '活动已发布')
  dialogVisible.value = false
}

const toggleStatus = (act) => {
  act.status = act.status === '已结束' ? '报名中' : '已结束'
  ElMessage.success(`活动已${act.status === '已结束' ? '关闭' : '重新开放'}`)
}
</script>
