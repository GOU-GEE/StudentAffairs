<template>
  <div class="h-full flex flex-col bg-transparent pt-4">
    <!-- Bento Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 auto-rows-min pb-6">
      
      <!-- 1. Profile Info -->
      <div class="md:col-span-5 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col justify-between hover:bg-white/50 transition-all">
        <div class="flex gap-6 mb-4">
          <div class="w-24 h-24 rounded-full overflow-hidden flex-shrink-0 bg-gray-100 border border-gray-200">
            <img src="https://i.pravatar.cc/150?u=student1" class="w-full h-full object-cover">
          </div>
          <div class="flex-1">
            <div class="flex justify-between items-center mb-2">
              <div class="flex items-baseline gap-2">
                <h3 class="text-xl font-bold text-gray-800">张小明</h3>
              </div>
              <button @click="openProfile" class="text-blue-500 text-xs flex items-center hover:text-blue-600 bg-blue-50 px-2 py-1 rounded-full">
                查看完整信息 <el-icon class="ml-1"><ArrowRight /></el-icon>
              </button>
            </div>
            <div class="text-sm text-gray-500 mb-2 font-medium">学号: 202301042</div>
            <p class="text-sm text-gray-600 mb-3">计算机学院 | 软件工程 | 软工2班</p>
            <div class="text-xs text-gray-500 flex flex-wrap gap-x-4 gap-y-1.5 items-center">
              <span class="flex items-center gap-1"><el-icon><Location /></el-icon> 兰苑3栋 402</span>
              <span class="flex items-center gap-1"><el-icon><Phone /></el-icon> 138-xxxx-8888</span>
            </div>
          </div>
        </div>
        <div class="pt-4 border-t border-gray-100 flex-1 flex flex-col min-h-0">
          <div class="text-xs font-bold text-gray-800 mb-2.5 flex items-center justify-between">
            <span class="flex items-center gap-1">
              <el-icon class="text-blue-500"><Medal /></el-icon>
              个人荣誉与奖项
            </span>
            <button @click="router.push('/student/honors')" class="text-[10px] text-blue-500 hover:text-blue-600 font-medium">查看全部</button>
          </div>
          <div class="grid grid-cols-2 gap-x-4 gap-y-2 flex-1 overflow-y-auto pr-1 custom-scrollbar">
            <div v-for="award in awards.slice(0, 6)" :key="award.id" class="text-xs flex items-center justify-between py-1 border-b border-gray-50 last:border-0 hover:bg-gray-50/50 rounded transition-colors px-1">
              <span class="text-gray-700 font-medium truncate pr-2" :title="award.awardName">{{ award.awardName }}</span>
              <span :class="levelTagClass(award.level)" class="px-1.5 py-0.5 rounded text-[8px] font-bold flex-shrink-0">{{ award.level }}</span>
            </div>
            <div v-if="awards.length === 0" class="col-span-2 text-center text-gray-400 text-xs py-6">暂无获奖记录</div>
          </div>
        </div>
      </div>

      <!-- 2. Academic Growth Data -->
      <div class="md:col-span-7 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col justify-between hover:bg-white/50 transition-all">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-base font-bold text-gray-800">本学期学业数据</h3>
          <button @click="router.push('/student/academic')" class="text-blue-500 text-xs hover:text-blue-600 font-medium bg-blue-50/50 px-3 py-1 rounded-full border border-blue-100/50 transition-all flex items-center gap-1">
            查看详细成绩信息 <el-icon><ArrowRight /></el-icon>
          </button>
        </div>
        
        <div class="grid grid-cols-4 gap-2 mb-4 items-start">
          <div class="p-3">
            <div class="text-sm text-blue-500 font-bold mb-1">GPA</div>
            <div class="text-[2.8rem] font-bold text-blue-600 leading-none mb-1">{{ academicStats[selectedSemester].gpa }}</div>
            <div class="text-xs text-gray-500 mb-0.5">专业排名 {{ academicStats[selectedSemester].rank }}</div>
            <div class="text-xs text-green-500 font-bold">↑ 2名</div>
          </div>
          <div class="p-3">
            <div class="text-sm text-gray-500 font-bold mb-1">第二课堂</div>
            <div class="text-[2.8rem] font-bold text-blue-500 leading-none mb-1">{{ academicStats[selectedSemester].secondClass }} <span class="text-sm font-normal text-gray-500">学时</span></div>
            <div class="text-xs text-blue-500">完成度 {{ academicStats[selectedSemester].secondClassRate }}%</div>
          </div>
          <div class="p-3">
            <div class="text-sm text-gray-500 font-bold mb-1">获奖次数</div>
            <div class="text-[2.8rem] font-bold text-blue-500 leading-none mb-1">{{ academicStats[selectedSemester].awards }} <span class="text-sm font-normal text-gray-500">次</span></div>
            <div class="text-xs text-gray-500">校级以上 <span class="text-blue-500">{{ academicStats[selectedSemester].schoolAwards }}</span> 次</div>
          </div>
          <div class="p-3 rounded-xl transition-all" :class="failCount >= 1 ? 'bg-red-50/50 border border-red-100' : ''">
            <div class="text-sm text-gray-500 font-bold mb-1">挂科预警</div>
            <div class="text-[2.8rem] font-bold leading-none mb-1" :class="failCount >= 1 ? 'text-red-600' : 'text-gray-800'">{{ failCount }} <span class="text-sm font-normal text-gray-500">门</span></div>
            <div class="text-sm font-bold" :class="failCount >= 1 ? 'text-red-500' : 'text-green-500'">{{ failCount >= 1 ? '需补考/重修' : '安全' }}</div>
          </div>
        </div>

        <div class="grid grid-cols-5 gap-2 border-t border-gray-100 pt-3 mt-auto">
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">必修课完成率</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].requiredRate }}%</div>
            <div class="text-xs text-green-500 font-medium">优秀</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">选修课学分</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].electiveCredits }}</div>
            <div class="text-xs text-green-500 font-medium">已获得</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">综合测评排名</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].comprehensiveRank }}<span class="text-xs font-normal text-gray-500">/150</span></div>
            <div class="text-xs text-green-500 font-medium">优秀</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">体测成绩</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].physicalScore }}<span class="text-xs font-normal text-gray-500">分</span></div>
            <div class="text-xs text-blue-500 font-medium">良好</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">英语水平</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].englishLevel }}</div>
            <div class="text-xs text-green-500 font-medium">优秀</div>
          </div>
        </div>
      </div>

      <!-- 3. Dynamic & Tasks -->
      <div class="md:col-span-4 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col justify-between h-[360px] hover:bg-white/50 transition-all">
        <h3 class="text-base font-bold text-gray-800 mb-4">进展与待办</h3>
        <div class="flex-1 flex flex-col justify-between">
          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-orange-50 text-orange-500 flex items-center justify-center text-lg">
                <el-icon><Medal /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">国家励志奖学金申请</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-12</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-blue-50 text-blue-600 px-2.5 py-1 rounded-full border border-blue-100">已开始</span>
          </div>

          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-blue-50 text-blue-500 flex items-center justify-center text-lg">
                <el-icon><EditPen /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">中期鉴定填写</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-15</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-red-50 text-red-600 px-2.5 py-1 rounded-full border border-red-100">待完成</span>
          </div>

          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-green-50 text-green-500 flex items-center justify-center text-lg">
                <el-icon><Warning /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">安全知识考试</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-10</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-red-50 text-red-600 px-2.5 py-1 rounded-full border border-red-100">待完成</span>
          </div>

          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-purple-50 text-purple-500 flex items-center justify-center text-lg">
                <el-icon><Stamp /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">第二课堂学时认证</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-08</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-blue-50 text-blue-600 px-2.5 py-1 rounded-full border border-blue-100">已开始</span>
          </div>

          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-pink-50 text-pink-500 flex items-center justify-center text-lg">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">辅导员消息回复</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-06</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-blue-50 text-blue-600 px-2.5 py-1 rounded-full border border-blue-100">已开始</span>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-50 text-center">
          <el-popover placement="top" :width="500" trigger="click">
            <template #reference>
              <button class="text-blue-500 text-xs hover:text-blue-600 font-medium">查看全部待办 (6) <el-icon class="align-middle"><ArrowRight /></el-icon></button>
            </template>
            <div class="p-4">
              <div class="text-base font-bold text-gray-800 mb-4 px-1">任务中心详情</div>
              <div class="grid grid-cols-2 gap-4 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
                <div class="p-3 bg-orange-50/30 rounded-xl border border-orange-100 hover:bg-orange-50/50 transition-all cursor-pointer group">
                  <div class="flex items-center gap-2 mb-2">
                    <el-icon class="text-orange-500"><Medal /></el-icon>
                    <span class="text-xs font-bold text-gray-800">奖学金申请</span>
                    <span class="ml-auto text-[10px] text-orange-600 font-medium">待提交</span>
                  </div>
                  <p class="text-[11px] text-gray-500 leading-relaxed mb-2">国家励志奖学金申请即将截止，请尽快完成材料上传。</p>
                  <div class="text-[10px] text-gray-400">截止时间: 2026-05-12</div>
                </div>
                
                <div class="p-3 bg-blue-50/30 rounded-xl border border-blue-100 hover:bg-blue-50/50 transition-all cursor-pointer group" @click="router.push('/student/profile')">
                  <div class="flex items-center gap-2 mb-2">
                    <el-icon class="text-blue-500"><EditPen /></el-icon>
                    <span class="text-xs font-bold text-gray-800">中期鉴定</span>
                    <span class="ml-auto text-[10px] text-blue-600 font-medium">进行中</span>
                  </div>
                  <p class="text-[11px] text-gray-500 leading-relaxed mb-2">请完成个人学年总结并提交辅导员审核。</p>
                  <div class="text-[10px] text-gray-400">截止时间: 2026-05-15</div>
                </div>

                <div class="p-3 bg-red-50/30 rounded-xl border border-red-100 hover:bg-red-50/50 transition-all cursor-pointer group">
                  <div class="flex items-center gap-2 mb-2">
                    <el-icon class="text-red-500"><Warning /></el-icon>
                    <span class="text-xs font-bold text-gray-800">安全知识考试</span>
                    <span class="ml-auto text-[10px] text-red-600 font-medium">未开始</span>
                  </div>
                  <p class="text-[11px] text-gray-500 leading-relaxed mb-2">全校性安全意识测评，人人必做。</p>
                  <div class="text-[10px] text-gray-400">截止时间: 2026-05-10</div>
                </div>

                <div class="p-3 bg-purple-50/30 rounded-xl border border-purple-100 hover:bg-purple-50/50 transition-all cursor-pointer group">
                  <div class="flex items-center gap-2 mb-2">
                    <el-icon class="text-purple-500"><Stamp /></el-icon>
                    <span class="text-xs font-bold text-gray-800">学时认证</span>
                    <span class="ml-auto text-[10px] text-purple-600 font-medium">待补正</span>
                  </div>
                  <p class="text-[11px] text-gray-500 leading-relaxed mb-2">您的第二课堂学时认证申请被退回，请修改。</p>
                  <div class="text-[10px] text-gray-400">更新时间: 2026-05-08</div>
                </div>
              </div>
              <div class="mt-4 pt-3 border-t border-gray-100 flex justify-between items-center">
                <span class="text-[11px] text-gray-400">当前共有 6 项待办任务</span>
                <button class="text-xs text-blue-500 font-bold hover:underline" @click="router.push('/student/applications')">进入任务中心详情页</button>
              </div>
            </div>
          </el-popover>
        </div>
      </div>

      <!-- 4. Quick Access -->
      <div class="md:col-span-4 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col h-[360px] hover:bg-white/50 transition-all">
        <h3 class="text-base font-bold text-gray-800 mb-8">快捷入口</h3>
        <div class="grid grid-cols-2 gap-3 flex-1">
          <div @click="router.push('/student/applications/leave')" class="bg-blue-50/50 p-3 rounded-xl border border-blue-100/50 flex flex-col justify-between cursor-pointer hover:bg-blue-100/50 transition-all group">
            <el-icon class="text-blue-500 text-xl"><Calendar /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">请假报备</div>
              <div class="text-[10px] text-gray-400 group-hover:text-blue-500 transition-colors">在线审批</div>
            </div>
          </div>
          <div @click="router.push('/student/applications/scholarship')" class="bg-orange-50/50 p-3 rounded-xl border border-orange-100/50 flex flex-col justify-between cursor-pointer hover:bg-orange-100/50 transition-all group">
            <el-icon class="text-orange-500 text-xl"><Trophy /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">奖助学金</div>
              <div class="text-[10px] text-gray-400 group-hover:text-orange-500 transition-colors">资助在线申请</div>
            </div>
          </div>
          <div @click="router.push('/student/academic')" class="bg-green-50/50 p-3 rounded-xl border border-green-100/50 flex flex-col justify-between cursor-pointer hover:bg-green-100/50 transition-all group">
            <el-icon class="text-green-500 text-xl"><Reading /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">学业与第二课堂</div>
              <div class="text-[10px] text-gray-400 group-hover:text-green-500 transition-colors">成绩与素质分</div>
            </div>
          </div>
          <div @click="router.push('/student/career-plan')" class="bg-purple-50/50 p-3 rounded-xl border border-purple-100/50 flex flex-col justify-between cursor-pointer hover:bg-purple-100/50 transition-all group">
            <el-icon class="text-purple-500 text-xl"><MagicStick /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">职业规划</div>
              <div class="text-[10px] text-purple-500 font-bold">AI 分身已就绪</div>
            </div>
          </div>
          <div @click="router.push('/student/campus-life')" class="bg-gray-50/50 p-3 rounded-xl border border-gray-100/50 flex flex-col justify-between cursor-pointer hover:bg-gray-100/50 transition-all group col-span-2 flex-row items-center gap-4">
            <el-icon class="text-gray-500 text-xl"><School /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">通知与沟通</div>
              <div class="text-[10px] text-gray-400 group-hover:text-gray-600 transition-colors">查寝、安全公告、生活服务</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 5. Notifications -->
      <div class="md:col-span-4 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col h-[360px] hover:bg-white/50 transition-all">
        <div class="flex justify-between items-center mb-4 flex-shrink-0">
          <h3 class="text-base font-bold text-gray-800">通知公告</h3>
          <button @click="router.push('/student/campus-life')" class="text-blue-500 text-xs hover:text-blue-600 font-medium">更多 <el-icon class="align-middle"><ArrowRight /></el-icon></button>
        </div>
        <div class="flex-1 overflow-y-auto space-y-3.5 pr-1 custom-scrollbar">
          <div v-for="notif in notifications.slice(0, 6)" :key="notif.id" @click="goNotification(notif.id)" class="flex justify-between items-center group cursor-pointer py-1.5 border-b border-gray-50 last:border-0 hover:bg-white/40 px-1 rounded transition-all">
            <div class="flex items-center gap-2 truncate pr-2 flex-1">
              <span :class="getTypeTagClass(notif.type)" class="px-1.5 py-0.5 rounded text-[10px] flex-shrink-0 font-medium">{{ getTypeName(notif.type) }}</span>
              <span class="text-sm text-gray-700 truncate group-hover:text-blue-500 flex-1">{{ notif.title }}</span>
            </div>
            <span class="text-xs text-gray-400 flex-shrink-0">{{ formatDate(notif.publishTime) }}</span>
          </div>
          <div v-if="notifications.length === 0" class="h-full flex flex-col items-center justify-center text-gray-400 text-xs py-10">
            <el-icon class="text-2xl mb-2 opacity-55"><School /></el-icon>
            暂无通知公告
          </div>
        </div>
      </div>



    </div>
  </div>
</template>

<script setup>
import { User, Document, Warning, Trophy, Download, UploadFilled, MagicStick, Loading, Position, Money, School, Guide, ChatDotRound, Calendar, ArrowRight, Location, Phone, ArrowDown, EditPen, Reading, CircleCheck, Medal, Stamp } from '@element-plus/icons-vue'
import { ref, inject, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const failCount = ref(0)
const selectedSemester = ref('大二下学期')

const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'

const stats = ref({ gpa: '--', totalCredits: '--', classroomHours: '--', applicationCount: '--', awards: '--' })
const notifications = ref([])
const awards = ref([])

const loadStats = async () => {
  try {
    const [academicRes, classroomRes, appRes] = await Promise.all([
      request.get(`/api/academic/student-records?studentId=${STUDENT_ID}`),
      request.get(`/api/youth/second-classroom/records?studentId=${STUDENT_ID}`),
      request.get(`/api/applications/student/${STUDENT_ID}`)
    ])
    if (academicRes.data.code === 200) {
      const records = academicRes.data.data
      const totalCredit = records.reduce((s, r) => s + (r.credit || 0), 0)
      const weightedSum = records.reduce((s, r) => s + ((r.score || 0) * (r.credit || 0)), 0)
      stats.value.gpa = totalCredit > 0 ? (weightedSum / totalCredit).toFixed(2) : '--'
      stats.value.totalCredits = totalCredit
    }
    if (classroomRes.data.code === 200) {
      stats.value.classroomHours = classroomRes.data.data.reduce((s, r) => s + (r.hours || 0), 0)
    }
    if (appRes.data.code === 200) {
      stats.value.applicationCount = appRes.data.data.length
    }
  } catch (e) { console.error(e) }
}

const loadNotifications = async () => {
  try {
    const res = await request.get('/api/communication/announcements')
    if (res.data.code === 200) {
      notifications.value = res.data.data
    }
  } catch (e) { console.error('加载通知失败', e) }
}

const loadAwards = async () => {
  try {
    const res = await request.get(`/api/youth/awards?studentId=${STUDENT_ID}`)
    if (res.data.code === 200) {
      const sorted = res.data.data.sort((a, b) => {
        const dateA = new Date(a.awardTime || 0)
        const dateB = new Date(b.awardTime || 0)
        return dateB - dateA
      })
      awards.value = sorted
    }
  } catch (e) { console.error('加载荣誉失败', e) }
}

const academicStats = ref({
  '大二下学期': {
    gpa: '3.85',
    rank: '12/150',
    secondClass: '32',
    secondClassRate: '80',
    awards: '6',
    schoolAwards: '4',
    requiredRate: '95',
    electiveCredits: '16',
    comprehensiveRank: '12',
    physicalScore: '90',
    englishLevel: '四级 586'
  },
  '大二上学期': {
    gpa: '3.72',
    rank: '25/150',
    secondClass: '28',
    secondClassRate: '70',
    awards: '4',
    schoolAwards: '2',
    requiredRate: '92',
    electiveCredits: '12',
    comprehensiveRank: '20',
    physicalScore: '88',
    englishLevel: '四级 586'
  }
})

const levelTagClass = (level) => {
  const m = {
    '国家级': 'bg-red-50 text-red-600 border border-red-100',
    '省级': 'bg-orange-50 text-orange-600 border border-orange-100',
    '市级': 'bg-yellow-50 text-yellow-600 border border-yellow-100',
    '校级': 'bg-blue-50 text-blue-600 border border-blue-100',
    '院级': 'bg-green-50 text-green-600 border border-green-100'
  }
  return m[level] || 'bg-gray-50 text-gray-500'
}

const getTypeName = (type) => {
  const m = {
    'NOTICE': '学校',
    'URGENT': '紧急',
    'EVENT': '活动'
  }
  return m[type] || '通知'
}

const getTypeTagClass = (type) => {
  const m = {
    'NOTICE': 'bg-blue-50 text-blue-500',
    'URGENT': 'bg-red-50 text-red-500',
    'EVENT': 'bg-green-50 text-green-500'
  }
  return m[type] || 'bg-gray-50 text-gray-500'
}

const formatDate = (s) => {
  if (!s) return ''
  const d = new Date(s)
  return `${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const goNotification = (id) => {
  router.push({ path: '/student/campus-life', query: { notifId: id } })
}

const openProfile = inject('openProfile', () => {
  router.push('/student/profile')
})

onMounted(async () => {
  await Promise.all([loadStats(), loadNotifications(), loadAwards()])
})
</script>

<style scoped>
.notification-popover {
  transition: opacity 0.1s ease-out, transform 0.1s ease-out !important;
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(6px); }

/* Custom premium scrollbar for lists */
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
  height: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.08);
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
</style>
