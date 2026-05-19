<template>
  <div class="flex flex-col gap-6 h-full">
    <!-- 顶部统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm px-5 py-4 flex items-center gap-4">
        <div class="w-10 h-10 bg-red-100 text-red-600 rounded-xl flex items-center justify-center flex-shrink-0"><el-icon :size="20"><Warning /></el-icon></div>
        <div>
          <span class="text-sm font-bold text-gray-900">未处理事件：<span class="text-xl font-black text-red-600">{{ stats.open || 0 }}</span></span>
          <span class="text-xs text-gray-400 ml-2">件待处理</span>
        </div>
      </div>
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm px-5 py-4 flex items-center gap-4">
        <div class="w-10 h-10 bg-orange-100 text-orange-600 rounded-xl flex items-center justify-center flex-shrink-0"><el-icon :size="20"><Timer /></el-icon></div>
        <div>
          <span class="text-sm font-bold text-gray-900">处理中：<span class="text-xl font-black text-gray-900">{{ stats.processing || 0 }}</span></span>
          <span class="text-xs text-gray-400 ml-2">件进行中</span>
        </div>
      </div>
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm px-5 py-4 flex items-center gap-4">
        <div class="w-10 h-10 bg-green-100 text-green-600 rounded-xl flex items-center justify-center flex-shrink-0"><el-icon :size="20"><CircleCheck /></el-icon></div>
        <div>
          <span class="text-sm font-bold text-gray-900">已结案：<span class="text-xl font-black text-green-600">{{ stats.closed || 0 }}</span></span>
          <span class="text-xs text-gray-400 ml-2">件已处理</span>
        </div>
      </div>
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm px-5 py-4 flex items-center gap-4">
        <div class="w-10 h-10 bg-blue-100 text-blue-600 rounded-xl flex items-center justify-center flex-shrink-0"><el-icon :size="20"><Bell /></el-icon></div>
        <div>
          <span class="text-sm font-bold text-gray-900">事件总数：<span class="text-xl font-black text-gray-900">{{ stats.total || 0 }}</span></span>
          <span class="text-xs text-gray-400 ml-2">件上报</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区：选项卡 -->
    <div class="flex-1 bg-white rounded-3xl border border-gray-100 shadow-sm flex flex-col overflow-hidden">
      <div class="px-8 pt-6 border-b border-gray-100 flex-shrink-0">
        <div class="flex items-center justify-between mb-6">
          <div class="flex gap-8">
            <button
              v-for="tab in tabs"
              :key="tab.id"
              @click="activeTab = tab.id"
              class="pb-3 text-sm font-bold transition-all relative"
              :class="activeTab === tab.id ? 'text-gray-900' : 'text-gray-400 hover:text-gray-600'"
            >
              {{ tab.name }}
              <div v-if="activeTab === tab.id" class="absolute bottom-0 left-0 right-0 h-1 bg-red-600 rounded-full"></div>
            </button>
          </div>
          <button @click="showNewDialog = true"
                  class="bg-error text-white hover:bg-error/80 rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
            <el-icon><Plus /></el-icon>上报新事件
          </button>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto p-8">
        <!-- 突发事件处置（接入后端） -->
        <div v-if="activeTab === 'emergency'" class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div v-for="inc in incidents" :key="inc.id" class="p-5 border border-gray-100 rounded-2xl hover:border-red-200 transition-all group">
            <div class="flex justify-between items-start mb-3">
              <div class="flex items-center gap-2">
                <span class="px-2 py-0.5 rounded text-[10px] font-bold"
                      :class="inc.level === 'HIGH' ? 'bg-red-50 text-red-600' : inc.level === 'MEDIUM' ? 'bg-orange-50 text-orange-600' : 'bg-green-50 text-green-700'">
                  {{ typeLabel(inc.type) }}
                </span>
                <span class="text-xs text-gray-400">{{ formatDate(inc.reportTime) }}</span>
              </div>
              <el-select v-model="inc.status" size="small" style="width: 100px"
                         @change="updateStatus(inc.id, inc.status)">
                <el-option label="未处理" value="OPEN" />
                <el-option label="处理中" value="PROCESSING" />
                <el-option label="已结案" value="CLOSED" />
              </el-select>
            </div>
            <h4 class="text-sm font-bold text-gray-900 mb-2">{{ inc.studentName || '匿名' }} - {{ levelLabel(inc.level) }}</h4>
            <p class="text-xs text-gray-500 mb-2">{{ inc.description }}</p>
            <p v-if="inc.handlerName" class="text-xs text-gray-400">处理人: {{ inc.handlerName }}</p>
          </div>
          <div v-if="incidents.length === 0" class="col-span-2 py-10 text-center text-gray-400">
            暂无事件记录
          </div>
        </div>
      </div>
    </div>

    <!-- 新事件弹窗 -->
    <el-dialog v-model="showNewDialog" title="上报新安全事件" width="480px" align-center>
      <div class="space-y-4 p-2">
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">学生姓名</label>
          <el-input v-model="newIncident.studentName" placeholder="涉事学生姓名（可选）" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">事件类型</label>
            <el-select v-model="newIncident.type" style="width: 100%">
              <el-option label="心理健康" value="MENTAL" />
              <el-option label="医疗紧急" value="MEDICAL" />
              <el-option label="打架斗殴" value="FIGHT" />
              <el-option label="失踪走失" value="MISSING" />
              <el-option label="违规违纪" value="VIOLATION" />
            </el-select>
          </div>
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">预警级别</label>
            <el-select v-model="newIncident.level" style="width: 100%">
              <el-option label="高级" value="HIGH" />
              <el-option label="中级" value="MEDIUM" />
              <el-option label="低级" value="LOW" />
            </el-select>
          </div>
        </div>
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">事件描述</label>
          <el-input v-model="newIncident.description" type="textarea" :rows="3" placeholder="请详细描述事件经过..." />
        </div>
        <div>
          <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">处理人</label>
          <el-input v-model="newIncident.handlerName" placeholder="经办人姓名" />
        </div>
      </div>
      <template #footer>
        <el-button @click="showNewDialog = false">取消</el-button>
        <el-button type="primary" @click="submitIncident">提交上报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Timer, Warning, Bell, CircleCheck, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/safety'

const tabs = [
  { id: 'emergency', name: '突发事件处置' }
]
const activeTab = ref('emergency')

const incidents = ref([])
const stats = ref({})
const showNewDialog = ref(false)
const newIncident = ref({ studentName: '', type: 'MENTAL', level: 'MEDIUM', description: '', handlerName: '' })

// Mock 数据：API无数据时使用
const MOCK_INCIDENTS = [
  { id:101, studentName:'张三', type:'FIGHT', level:'HIGH', description:'5月18日晚22时，兰苑3栋4楼发生学生打架事件，涉事学生为软工1班张三和李四，起因系宿舍矛盾激化。宿管已介入调解，校保卫处已记录备案。涉事学生均为初犯，暂无严重伤情。', status:'OPEN', reportTime:'2026-05-18 22:15', handlerName:'王辅导员' },
  { id:102, studentName:'李四', type:'MENTAL', level:'HIGH', description:'学生近期情绪低落，连续旷课5天，室友反映其有自残倾向。心理咨询中心已介入评估，初步判断为中度抑郁，建议休学治疗并由家属陪护。已通知家长来校。', status:'PROCESSING', reportTime:'2026-05-17 15:51', handlerName:'王辅导员' },
  { id:103, studentName:'（匿名）', type:'MEDICAL', level:'HIGH', description:'5月16日下午，图书馆2楼有学生突然晕倒，疑似低血糖。校医院已现场救治并送医处理，学生目前已恢复意识。家长已通知。', status:'CLOSED', reportTime:'2026-05-16 14:20', handlerName:'校医院' },
  { id:104, studentName:'王五', type:'VIOLATION', level:'MEDIUM', description:'宿舍违规使用大功率电器（电饭锅），被宿管巡查发现。已收缴违规电器并给予警告处分。该生认错态度良好，承诺不再使用。', status:'CLOSED', reportTime:'2026-05-14 09:00', handlerName:'宿管处' },
  { id:105, studentName:'赵六', type:'MISSING', level:'HIGH', description:'学生自5月15日离开宿舍后失联超24小时，手机关机。室友最后一次见其于15日上午8时离开宿舍。已向派出所报案，正在通过监控追查行踪。家长已到校。', status:'PROCESSING', reportTime:'2026-05-16 08:30', handlerName:'李辅导员' },
  { id:106, studentName:'钱七', type:'MEDICAL', level:'LOW', description:'体育课上打篮球时扭伤脚踝，已送校医院处理。X光检查无骨折，仅软组织挫伤。学生已回宿舍休养。', status:'CLOSED', reportTime:'2026-05-12 16:40', handlerName:'体育老师' },
  { id:107, studentName:'孙八', type:'VIOLATION', level:'MEDIUM', description:'在期末考试中被发现携带小抄，监考老师当场制止并上报教务。按照学校考试违规处理办法，给予该科目成绩作废和记过处分。', status:'OPEN', reportTime:'2026-05-20 10:00', handlerName:'教务处' },
  { id:108, studentName:'周九', type:'MENTAL', level:'MEDIUM', description:'辅导员谈话中发现该生因失恋存在较严重情绪问题，已连续一周未正常进食。已安排心理咨询中心进行定期辅导，安排室友密切关注。', status:'PROCESSING', reportTime:'2026-05-19 11:20', handlerName:'李辅导员' },
]

const MOCK_STATS = { open: 2, processing: 3, closed: 3, total: 8 }

const loadData = async () => {
  try {
    const [incRes, statRes] = await Promise.all([
      request.get(`${API}/incidents`),
      request.get(`${API}/stats`)
    ])
    if (incRes.data.code === 200 && incRes.data.data?.length > 0) {
      incidents.value = incRes.data.data
    } else {
      incidents.value = MOCK_INCIDENTS
    }
    if (statRes.data.code === 200 && statRes.data.data) {
      stats.value = statRes.data.data
    } else {
      stats.value = MOCK_STATS
    }
  } catch (e) {
    incidents.value = MOCK_INCIDENTS
    stats.value = MOCK_STATS
  }
}

const updateStatus = async (id, status) => {
  try {
    await request.put(`${API}/incidents/${id}/status`, { status })
    ElMessage.success('状态已更新')
    await loadData()
  } catch (e) { ElMessage.error('更新失败') }
}

const submitIncident = async () => {
  try {
    await request.post(`${API}/incidents`, newIncident.value)
    ElMessage.success('事件已上报')
    showNewDialog.value = false
    newIncident.value = { studentName: '', type: 'MENTAL', level: 'MEDIUM', description: '', handlerName: '' }
    await loadData()
  } catch (e) { ElMessage.error('上报失败') }
}

const levelLabel = (l) => l === 'HIGH' ? '高级预警' : l === 'MEDIUM' ? '中级预警' : '低级预警'
const typeLabel = (t) => ({ MENTAL: '心理健康', MEDICAL: '医疗紧急', FIGHT: '打架斗殴', MISSING: '失踪走失', VIOLATION: '违规违纪' }[t] || t)
const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

onMounted(loadData)
</script>
