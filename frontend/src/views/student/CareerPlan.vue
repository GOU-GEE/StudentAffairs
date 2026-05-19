<template>
  <div class="career-plan">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- Tab 1: 生涯工具 -->
      <el-tab-pane label="生涯工具" name="tools">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 p-4">
          <div v-for="q in questionnaires" :key="q.key" class="bg-white rounded-2xl p-6 border hover:shadow-lg transition-all cursor-pointer" @click="startQuestionnaire(q.key)">
            <div class="flex items-center gap-3 mb-3">
              <div class="w-12 h-12 rounded-xl flex items-center justify-center text-white text-xl" :class="q.bg">{{ q.icon }}</div>
              <div>
                <h3 class="font-bold text-lg">{{ q.title }}</h3>
                <p class="text-xs text-secondary">{{ q.count }}题 · 约{{ q.time }}分钟</p>
              </div>
            </div>
            <p class="text-sm text-secondary">{{ q.desc }}</p>
            <div class="mt-3 flex items-center gap-2">
              <span v-if="results[q.key]" class="text-xs font-bold px-2 py-1 rounded-full bg-green-100 text-green-700">已完成</span>
              <span v-else class="text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-500">未开始</span>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab 2: 测评结果 -->
      <el-tab-pane label="测评结果" name="results">
        <div class="p-4 space-y-6">
          <div v-if="!matchResults.length" class="text-center py-20 text-secondary">
            <el-icon :size="64" class="opacity-20 mb-3"><TrendCharts /></el-icon>
            <p>请先在"生涯工具"中完成至少一份问卷</p>
          </div>
          <template v-else>
            <div class="bg-white rounded-2xl p-6 border">
              <h3 class="font-bold text-lg mb-4">你的测评概要</h3>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div v-if="results.holland" class="p-3 rounded-xl bg-purple-50">
                  <p class="text-xs text-secondary">霍兰德类型</p>
                  <p class="font-bold text-lg">{{ hollandTop3 }}</p>
                </div>
                <div v-if="results.mbti" class="p-3 rounded-xl bg-blue-50">
                  <p class="text-xs text-secondary">MBTI 类型</p>
                  <p class="font-bold text-lg">{{ mbtiType }}</p>
                </div>
                <div v-if="results.values" class="p-3 rounded-xl bg-green-50">
                  <p class="text-xs text-secondary">核心价值观</p>
                  <p class="font-bold text-lg">{{ topValue }}</p>
                </div>
                <div v-if="results.skills" class="p-3 rounded-xl bg-amber-50">
                  <p class="text-xs text-secondary">最强能力</p>
                  <p class="font-bold text-lg">{{ topSkill }}</p>
                </div>
              </div>
              <button @click="runMatching" class="mt-4 px-6 py-2.5 bg-purple-500 text-white rounded-xl text-sm font-bold hover:bg-purple-600 transition-colors" :disabled="matching">
                {{ matching ? '匹配中...' : '根据成绩问卷寻得最佳匹配' }}
              </button>
            </div>
            <div v-if="matchResults.length" class="space-y-4">
              <h3 class="font-bold text-lg">TOP 3 职业匹配</h3>
              <div v-for="(m, idx) in matchResults" :key="idx" class="bg-white rounded-2xl p-6 border hover:shadow-md transition-all">
                <div class="flex items-center justify-between mb-3">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-purple-500 text-white flex items-center justify-center font-bold">{{ idx + 1 }}</div>
                    <div>
                      <h4 class="font-bold text-lg">{{ m.title }}</h4>
                      <p class="text-xs text-secondary">{{ m.education }} · {{ m.salary }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <div class="text-2xl font-black text-purple-600">{{ m.matchScore }}%</div>
                    <div class="text-xs text-secondary">匹配度</div>
                  </div>
                </div>
                <p class="text-sm text-secondary mb-3">{{ m.description }}</p>
                <div class="grid grid-cols-4 gap-2 text-xs">
                  <div class="p-2 rounded-lg bg-purple-50 text-center"><span class="text-secondary">霍兰德</span><br><span class="font-bold">{{ (m.hollandScore*100).toFixed(0) }}%</span></div>
                  <div class="p-2 rounded-lg bg-blue-50 text-center"><span class="text-secondary">MBTI</span><br><span class="font-bold">{{ (m.mbtiScore*100).toFixed(0) }}%</span></div>
                  <div class="p-2 rounded-lg bg-green-50 text-center"><span class="text-secondary">价值观</span><br><span class="font-bold">{{ (m.valueScore*100).toFixed(0) }}%</span></div>
                  <div class="p-2 rounded-lg bg-amber-50 text-center"><span class="text-secondary">能力</span><br><span class="font-bold">{{ (m.skillScore*100).toFixed(0) }}%</span></div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </el-tab-pane>

      <!-- Tab 3: 预约咨询 -->
      <el-tab-pane label="预约咨询" name="appointment">
        <div class="max-w-lg mx-auto p-4 space-y-6">
          <div class="bg-white rounded-2xl p-6 border space-y-4">
            <h3 class="font-bold text-lg">预约职业规划咨询</h3>
            <div>
              <label class="text-xs font-bold text-secondary uppercase block mb-1.5">辅导员</label>
              <el-select v-model="apptForm.teacherId" class="w-full" placeholder="选择辅导员">
                <el-option label="李老师" value="T001" />
              </el-select>
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase block mb-1.5">预约时间</label>
              <el-select v-model="apptForm.appointmentTime" class="w-full" placeholder="选择时间段">
                <el-option label="周一 14:00-15:00" value="周一 14:00-15:00" />
                <el-option label="周二 10:00-11:00" value="周二 10:00-11:00" />
                <el-option label="周三 15:00-16:00" value="周三 15:00-16:00" />
                <el-option label="周四 09:00-10:00" value="周四 09:00-10:00" />
                <el-option label="周五 16:00-17:00" value="周五 16:00-17:00" />
              </el-select>
            </div>
            <div>
              <label class="text-xs font-bold text-secondary uppercase block mb-1.5">咨询原因</label>
              <el-input v-model="apptForm.reason" type="textarea" :rows="4" placeholder="请简要描述你想咨询的问题..." />
            </div>
            <button @click="submitAppointment" class="w-full py-2.5 bg-purple-500 text-white rounded-xl font-bold hover:bg-purple-600 transition-colors" :disabled="submittingAppt">
              {{ submittingAppt ? '提交中...' : '提交预约' }}
            </button>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab 4: 我的预约 -->
      <el-tab-pane label="我的预约" name="myAppointments">
        <div class="p-4">
          <div v-if="!appointments.length" class="text-center py-20 text-secondary">
            <el-icon :size="64" class="opacity-20 mb-3"><Calendar /></el-icon>
            <p>暂无预约记录</p>
          </div>
          <div v-else class="space-y-3">
            <div v-for="a in appointments" :key="a.id" class="bg-white rounded-xl p-4 border flex items-center justify-between">
              <div>
                <p class="font-bold text-sm">{{ a.appointmentTime }}</p>
                <p class="text-xs text-secondary mt-1">{{ a.reason }}</p>
              </div>
              <span class="text-xs font-bold px-3 py-1 rounded-full" :class="statusClass(a.status)">{{ statusText(a.status) }}</span>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab 5: 职业库 -->
      <el-tab-pane label="职业库" name="jobLibrary">
        <div class="p-4">
          <el-input v-model="jobSearch" placeholder="搜索职业..." class="max-w-xs mb-4" clearable />
          <div class="space-y-3">
            <el-collapse v-model="expandedJobs">
              <el-collapse-item v-for="(job, idx) in filteredJobs" :key="idx" :name="idx">
                <template #title>
                  <div class="flex items-center gap-3">
                    <span class="text-xs font-bold px-2 py-0.5 rounded bg-purple-100 text-purple-700">{{ job.hollandCode }}</span>
                    <span class="font-bold">{{ job.title }}</span>
                    <span class="text-xs text-secondary">{{ job.salary }}</span>
                  </div>
                </template>
                <div class="space-y-2 text-sm pl-4">
                  <p>{{ job.description }}</p>
                  <p><span class="text-secondary">学历要求：</span>{{ job.education }}</p>
                  <p><span class="text-secondary">霍兰德码：</span>{{ job.hollandCode }}</p>
                  <p><span class="text-secondary">MBTI推荐：</span>{{ (job.mbtiTypes || []).join(', ') }}</p>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 问卷答题弹窗 -->
    <el-dialog v-model="quizVisible" :title="currentQuiz?.title" width="700px" destroy-on-close :close-on-click-modal="false">
      <div v-if="currentQuiz" class="space-y-4 max-h-[60vh] overflow-y-auto">
        <div v-for="(q, idx) in currentQuiz.questions" :key="idx" class="p-3 rounded-xl bg-gray-50">
          <p class="text-sm font-medium mb-2">{{ idx + 1 }}. {{ q.text }}</p>
          <el-radio-group v-model="quizAnswers[idx]" class="flex gap-2">
            <el-radio :value="1">非常不符合</el-radio>
            <el-radio :value="2">比较不符合</el-radio>
            <el-radio :value="3">一般</el-radio>
            <el-radio :value="4">比较符合</el-radio>
            <el-radio :value="5">非常符合</el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <button @click="quizVisible = false" class="px-4 py-2 text-secondary text-sm mr-3">取消</button>
        <button @click="submitQuiz" class="px-6 py-2 bg-purple-500 text-white rounded-lg text-sm font-bold hover:bg-purple-600 transition-colors">提交</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { TrendCharts, Calendar } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/career'
const STUDENT_ID = '202301042'
const STUDENT_NAME = '张小明'

const activeTab = ref('tools')
const matching = ref(false)
const submittingAppt = ref(false)
const matchResults = ref([])
const appointments = ref([])
const jobSearch = ref('')
const jobLibrary = ref([])
const expandedJobs = ref([])

// ==================== 四份问卷定义 ====================

const HOLLAND_QUESTIONS = [
  // R - 现实型 (10题)
  { type: 'R', text: '我喜欢操作机器或使用工具' },
  { type: 'R', text: '我喜欢修理或组装物品' },
  { type: 'R', text: '我喜欢户外工作和体力活动' },
  { type: 'R', text: '我喜欢动手制作东西' },
  { type: 'R', text: '我对机械原理感兴趣' },
  { type: 'R', text: '我喜欢种植花草或照料动物' },
  { type: 'R', text: '我喜欢使用电脑软件进行工程设计' },
  { type: 'R', text: '我对交通工具的构造感兴趣' },
  { type: 'R', text: '我喜欢运动或体育训练' },
  { type: 'R', text: '我喜欢按照说明书组装产品' },
  // I - 研究型 (10题)
  { type: 'I', text: '我喜欢进行科学实验和研究' },
  { type: 'I', text: '我喜欢解决复杂的数学问题' },
  { type: 'I', text: '我喜欢阅读科学类书籍和文章' },
  { type: 'I', text: '我对自然现象的原理感到好奇' },
  { type: 'I', text: '我喜欢分析数据和发现规律' },
  { type: 'I', text: '我喜欢独立思考问题' },
  { type: 'I', text: '我对医学或生物学研究感兴趣' },
  { type: 'I', text: '我喜欢使用显微镜或精密仪器' },
  { type: 'I', text: '我对人工智能和新技术充满好奇' },
  { type: 'I', text: '我喜欢深入探究事物的本质' },
  // A - 艺术型 (10题)
  { type: 'A', text: '我喜欢绘画、雕塑或视觉艺术' },
  { type: 'A', text: '我喜欢音乐创作或演奏乐器' },
  { type: 'A', text: '我喜欢写作诗歌、小说或散文' },
  { type: 'A', text: '我喜欢设计新颖的物品或图形' },
  { type: 'A', text: '我喜欢摄影或摄像创作' },
  { type: 'A', text: '我对建筑美学和室内设计感兴趣' },
  { type: 'A', text: '我喜欢表演或戏剧艺术' },
  { type: 'A', text: '我享受自由创作的过程' },
  { type: 'A', text: '我喜欢参观美术馆或艺术展览' },
  { type: 'A', text: '我对服装搭配和时尚设计有独特见解' },
  // S - 社会型 (10题)
  { type: 'S', text: '我喜欢帮助他人解决困难' },
  { type: 'S', text: '我喜欢教学或培训工作' },
  { type: 'S', text: '我乐于参与志愿服务活动' },
  { type: 'S', text: '我善于倾听和理解他人的感受' },
  { type: 'S', text: '我喜欢与人合作完成工作' },
  { type: 'S', text: '我关注社会问题和弱势群体' },
  { type: 'S', text: '我享受在团队中沟通协调' },
  { type: 'S', text: '我对心理咨询或辅导工作感兴趣' },
  { type: 'S', text: '我喜欢组织和参与集体活动' },
  { type: 'S', text: '我认为帮助他人成长很有意义' },
  // E - 企业型 (10题)
  { type: 'E', text: '我喜欢领导团队完成目标' },
  { type: 'E', text: '我善于说服他人接受我的观点' },
  { type: 'E', text: '我对商业策划和创业感兴趣' },
  { type: 'E', text: '我喜欢在竞争中取胜' },
  { type: 'E', text: '我善于在公开场合演讲或展示' },
  { type: 'E', text: '我喜欢管理和组织资源' },
  { type: 'E', text: '我对市场营销和销售工作感兴趣' },
  { type: 'E', text: '我敢于承担风险和做出决策' },
  { type: 'E', text: '我喜欢参与政治或公共事务' },
  { type: 'E', text: '我追求权力和影响力' },
  // C - 常规型 (10题)
  { type: 'C', text: '我喜欢按照既定的流程和规范工作' },
  { type: 'C', text: '我善于整理文件和管理数据' },
  { type: 'C', text: '我对财务会计工作感兴趣' },
  { type: 'C', text: '我注重工作中的细节和准确性' },
  { type: 'C', text: '我喜欢有明确规则和标准的工作环境' },
  { type: 'C', text: '我善于使用办公软件处理文档' },
  { type: 'C', text: '我喜欢制定计划和安排日程' },
  { type: 'C', text: '我偏好稳定和可预测的工作节奏' },
  { type: 'C', text: '我对档案管理和图书分类感兴趣' },
  { type: 'C', text: '我遵守规章制度并按时完成任务' },
]

const MBTI_QUESTIONS = [
  // E/I 外向/内向 (12题)
  { dim: 'EI', text: '我喜欢参加社交聚会和集体活动', score: 1 },
  { dim: 'EI', text: '我更愿意通过说来表达自己的想法', score: 1 },
  { dim: 'EI', text: '我认识很多人，交际圈很广', score: 1 },
  { dim: 'EI', text: '我在人群中感到精力充沛', score: 1 },
  { dim: 'EI', text: '我倾向于先行动再思考', score: 1 },
  { dim: 'EI', text: '我喜欢成为众人关注的焦点', score: 1 },
  { dim: 'EI', text: '我更喜欢独处或与少数好友相处', score: -1 },
  { dim: 'EI', text: '我倾向于通过写来表达自己的想法', score: -1 },
  { dim: 'EI', text: '我有少数几个深交的朋友', score: -1 },
  { dim: 'EI', text: '社交活动后我需要独处来恢复精力', score: -1 },
  { dim: 'EI', text: '我倾向于先思考再行动', score: -1 },
  { dim: 'EI', text: '我更喜欢在幕后工作而非台前', score: -1 },
  // S/N 感觉/直觉 (12题)
  { dim: 'SN', text: '我更关注具体的事实和细节', score: 1 },
  { dim: 'SN', text: '我喜欢按照已有的方法做事', score: 1 },
  { dim: 'SN', text: '我更相信实际经验而非理论推测', score: 1 },
  { dim: 'SN', text: '我注重当下实际而非未来可能', score: 1 },
  { dim: 'SN', text: '我喜欢循序渐进地学习新知识', score: 1 },
  { dim: 'SN', text: '我更喜欢处理实际问题而非抽象概念', score: 1 },
  { dim: 'SN', text: '我关注整体图景和未来可能性', score: -1 },
  { dim: 'SN', text: '我喜欢尝试新的方法和创意', score: -1 },
  { dim: 'SN', text: '我依赖直觉和灵感做决策', score: -1 },
  { dim: 'SN', text: '我关注未来可能性而非现状', score: -1 },
  { dim: 'SN', text: '我喜欢跳跃式思维和联想', score: -1 },
  { dim: 'SN', text: '我更喜欢探讨理论和抽象概念', score: -1 },
  // T/F 思考/情感 (12题)
  { dim: 'TF', text: '我做决策时主要依据逻辑和客观分析', score: 1 },
  { dim: 'TF', text: '我认为公平正义比人情更重要', score: 1 },
  { dim: 'TF', text: '我能够冷静地指出他人的错误', score: 1 },
  { dim: 'TF', text: '我倾向于分析问题的利弊得失', score: 1 },
  { dim: 'TF', text: '我认为规则应该一视同仁', score: 1 },
  { dim: 'TF', text: '我在争论中关注谁更有道理', score: 1 },
  { dim: 'TF', text: '我做决策时会考虑对他人的影响', score: -1 },
  { dim: 'TF', text: '我认为和谐的人际关系很重要', score: -1 },
  { dim: 'TF', text: '我倾向于避免冲突和伤害他人感情', score: -1 },
  { dim: 'TF', text: '我更容易被感人的故事打动', score: -1 },
  { dim: 'TF', text: '我认为个别情况可以特殊处理', score: -1 },
  { dim: 'TF', text: '我在争论中关注谁的感受更需要照顾', score: -1 },
  // J/P 判断/感知 (12题)
  { dim: 'JP', text: '我更喜欢事先做好计划和安排', score: 1 },
  { dim: 'JP', text: '我喜欢在规定期限内完成任务', score: 1 },
  { dim: 'JP', text: '我倾向于做出明确决定而非保持开放', score: 1 },
  { dim: 'JP', text: '我喜欢井井有条的生活和工作环境', score: 1 },
  { dim: 'JP', text: '我会列清单并按计划执行', score: 1 },
  { dim: 'JP', text: '我对拖延和临时变更感到不适', score: 1 },
  { dim: 'JP', text: '我更喜欢灵活应变而非按计划行事', score: -1 },
  { dim: 'JP', text: '我享受在最后期限前冲刺完成工作', score: -1 },
  { dim: 'JP', text: '我倾向于保持选择开放而非早早决定', score: -1 },
  { dim: 'JP', text: '我适应多变和即兴的工作环境', score: -1 },
  { dim: 'JP', text: '我经常随心所欲地做事情', score: -1 },
  { dim: 'JP', text: '我认为计划赶不上变化', score: -1 },
]

const VALUES_QUESTIONS = [
  { dim: 'achievement', text: '我希望在工作中不断获得成就感' },
  { dim: 'achievement', text: '我渴望在工作中脱颖而出' },
  { dim: 'achievement', text: '我认为事业成功是人生的重要目标' },
  { dim: 'independence', text: '我希望在工作中拥有自主决策权' },
  { dim: 'independence', text: '我偏好不受过多约束的工作方式' },
  { dim: 'independence', text: '我想按自己的节奏和方式工作' },
  { dim: 'stability', text: '我重视工作的稳定性和安全感' },
  { dim: 'stability', text: '我希望有确定的收入和福利保障' },
  { dim: 'stability', text: '我偏好可预测的工作内容和环境' },
  { dim: 'stability', text: '我不喜欢频繁更换工作' },
  { dim: 'relationships', text: '我重视与同事的良好关系' },
  { dim: 'relationships', text: '我希望在友好和谐的氛围中工作' },
  { dim: 'relationships', text: '我认为团队合作比个人表现更重要' },
  { dim: 'relationships', text: '我享受在工作中帮助和支持他人' },
  { dim: 'contribution', text: '我希望工作能对社会有积极影响' },
  { dim: 'contribution', text: '我认为工作应该创造社会价值' },
  { dim: 'contribution', text: '我倾向于选择有意义或有使命感的职业' },
  { dim: 'contribution', text: '我关注环保、公益等社会议题' },
  { dim: 'innovation', text: '我渴望在工作中发挥创造力' },
  { dim: 'innovation', text: '我喜欢挑战传统和尝试新方法' },
  { dim: 'innovation', text: '我享受创新和突破带来的快乐' },
  { dim: 'innovation', text: '我对新兴技术和趋势充满兴趣' },
  { dim: 'reward', text: '我认为高薪酬是选择工作的重要因素' },
  { dim: 'reward', text: '我追求优厚的物质回报' },
  { dim: 'reward', text: '我希望薪资能匹配我的能力贡献' },
  { dim: 'environment', text: '我重视舒适的工作环境' },
  { dim: 'environment', text: '我希望有良好的办公条件和设施' },
  { dim: 'environment', text: '工作地点和通勤便利对我很重要' },
  { dim: 'environment', text: '我喜欢轻松自由的办公氛围' },
  { dim: 'environment', text: '我希望工作地点离家近或交通方便' },
]

const SKILLS_QUESTIONS = [
  { dim: 'verbal', text: '我善于用语言清晰表达自己的想法' },
  { dim: 'verbal', text: '我擅长写作和文字处理' },
  { dim: 'verbal', text: '我能在公开场合流利地演讲' },
  { dim: 'verbal', text: '我阅读和理解复杂文本的能力很强' },
  { dim: 'math', text: '我擅长数学计算和逻辑推理' },
  { dim: 'math', text: '我能快速理解和分析数据' },
  { dim: 'math', text: '我善于发现数字中的规律和异常' },
  { dim: 'math', text: '我对统计和概率有很好的直觉' },
  { dim: 'spatial', text: '我善于想象三维空间中的物体' },
  { dim: 'spatial', text: '我方向感很好，不易迷路' },
  { dim: 'spatial', text: '我能根据平面图想象出立体效果' },
  { dim: 'interpersonal', text: '我善于与人沟通和交流' },
  { dim: 'interpersonal', text: '我能快速与陌生人建立联系' },
  { dim: 'interpersonal', text: '我善于调解人际冲突' },
  { dim: 'interpersonal', text: '我能敏锐察觉他人的情绪变化' },
  { dim: 'organization', text: '我善于规划和管理项目进度' },
  { dim: 'organization', text: '我能够有效协调多方资源完成任务' },
  { dim: 'organization', text: '我擅长制定和执行计划' },
  { dim: 'organization', text: '我能同时处理多个任务并保持条理' },
  { dim: 'hands-on', text: '我动手操作能力很强' },
  { dim: 'hands-on', text: '我善于使用各种工具和器械' },
  { dim: 'hands-on', text: '我能快速掌握操作类技能' },
  { dim: 'creativity', text: '我经常产生新颖的创意和想法' },
  { dim: 'creativity', text: '我善于从不同角度思考问题' },
  { dim: 'creativity', text: '我喜欢设计独特的产品或方案' },
  { dim: 'creativity', text: '我能将看似无关的事物联系起来' },
  { dim: 'learning', text: '我学习新知识的速度很快' },
  { dim: 'learning', text: '我能快速适应新的工作内容和环境' },
  { dim: 'learning', text: '我善于总结经验和举一反三' },
  { dim: 'learning', text: '我持续关注行业动态和新技术' },
]

const questionnaires = [
  { key: 'holland', title: '霍兰德职业兴趣测评', icon: '\u{1F52C}', bg: 'bg-purple-500', count: 60, time: '15-20', desc: '基于RIASEC理论，评估你在现实型、研究型、艺术型、社会型、企业型、常规型六个维度的兴趣倾向，是应用最广泛的职业测评工具。' },
  { key: 'mbti', title: 'MBTI职业性格测评', icon: '\u{1F9E0}', bg: 'bg-blue-500', count: 48, time: '12-15', desc: '从外向/内向、感觉/直觉、思考/情感、判断/感知四个维度分析你的性格类型，帮助你找到与性格匹配的职业方向。' },
  { key: 'values', title: '职业价值观测评', icon: '\u{1F48E}', bg: 'bg-green-500', count: 30, time: '8-10', desc: '评估你在成就感、独立性、稳定性、人际关系、社会贡献等8个维度上的职业价值观偏好，明确什么对你最重要。' },
  { key: 'skills', title: '能力倾向自评', icon: '\u{26A1}', bg: 'bg-amber-500', count: 30, time: '8-10', desc: '自评语言表达、数理逻辑、空间想象、人际沟通等8项核心能力的当前水平，发现优势领域和成长空间。' },
]

// ==================== 状态管理 ====================

const results = ref({})
const quizVisible = ref(false)
const currentQuizKey = ref('')
const currentQuiz = ref(null)
const quizAnswers = ref([])

const QUESTION_MAP = { holland: HOLLAND_QUESTIONS, mbti: MBTI_QUESTIONS, values: VALUES_QUESTIONS, skills: SKILLS_QUESTIONS }
const VALUE_DIMS = ['achievement','independence','stability','relationships','contribution','innovation','reward','environment']
const VALUE_LABELS = { achievement:'成就感', independence:'独立性', stability:'稳定性', relationships:'人际关系', contribution:'社会贡献', innovation:'创新创造', reward:'物质报酬', environment:'工作环境' }
const SKILL_DIMS = ['verbal','math','spatial','interpersonal','organization','hands-on','creativity','learning']
const SKILL_LABELS = { verbal:'语言表达', math:'数理逻辑', spatial:'空间想象', interpersonal:'人际沟通', organization:'组织管理', 'hands-on':'动手操作', creativity:'创意设计', learning:'学习能力' }

// ==================== 问卷逻辑 ====================

const startQuestionnaire = (key) => {
  currentQuizKey.value = key
  const questions = QUESTION_MAP[key]
  currentQuiz.value = { title: questionnaires.find(q => q.key === key).title, questions }
  quizAnswers.value = new Array(questions.length).fill(3)
  quizVisible.value = true
}

const submitQuiz = () => {
  const key = currentQuizKey.value
  const questions = QUESTION_MAP[key]
  if (quizAnswers.value.some(a => !a)) {
    ElMessage.warning('请完成所有题目')
    return
  }
  // 计算得分
  if (key === 'holland') {
    const dims = ['R','I','A','S','E','C']
    const scores = {}
    dims.forEach(d => {
      const dimQuestions = questions.filter(q => q.type === d)
      const total = dimQuestions.reduce((sum, q, i) => {
        const idx = questions.indexOf(q)
        return sum + quizAnswers.value[idx]
      }, 0)
      scores[d] = Math.round(total / (dimQuestions.length * 5) * 1000) / 1000
    })
    results.value.holland = scores
  } else if (key === 'mbti') {
    const eiScore = questions.filter(q => q.dim === 'EI').reduce((sum, q, i) => sum + quizAnswers.value[questions.indexOf(q)] * (q.score > 0 ? 1 : -1) * (q.score > 0 ? 1 : -1) * (1 - (q.score > 0 ? 0 : 1) * 2), 0)
    const snScore = questions.filter(q => q.dim === 'SN').reduce((sum, q, i) => sum + quizAnswers.value[questions.indexOf(q)] * (q.score > 0 ? 1 : -1), 0)
    const tfScore = questions.filter(q => q.dim === 'TF').reduce((sum, q, i) => sum + quizAnswers.value[questions.indexOf(q)] * (q.score > 0 ? 1 : -1), 0)
    const jpScore = questions.filter(q => q.dim === 'JP').reduce((sum, q, i) => sum + quizAnswers.value[questions.indexOf(q)] * (q.score > 0 ? 1 : -1), 0)

    // Simplified MBTI calculation: positive score = first letter, negative = second
    const type = (eiScore > 0 ? 'E' : 'I') + (snScore > 0 ? 'S' : 'N') + (tfScore > 0 ? 'T' : 'F') + (jpScore > 0 ? 'J' : 'P')
    results.value.mbti = type

    // Also normalize EI/SN/TF/JP for scoring
    results.value.mbtiScores = { EI: Math.abs(eiScore) / (12 * 5), SN: Math.abs(snScore) / (12 * 5), TF: Math.abs(tfScore) / (12 * 5), JP: Math.abs(jpScore) / (12 * 5) }
  } else if (key === 'values') {
    const dims = VALUE_DIMS
    const scores = {}
    dims.forEach(d => {
      const dimQuestions = questions.filter(q => q.dim === d)
      const total = dimQuestions.reduce((sum, q, i) => sum + quizAnswers.value[questions.indexOf(q)], 0)
      scores[d] = Math.round(total / (dimQuestions.length * 5) * 1000) / 1000
    })
    results.value.values = scores
  } else if (key === 'skills') {
    const dims = SKILL_DIMS
    const scores = {}
    dims.forEach(d => {
      const dimQuestions = questions.filter(q => q.dim === d)
      const total = dimQuestions.reduce((sum, q, i) => sum + quizAnswers.value[questions.indexOf(q)], 0)
      scores[d] = Math.round(total / (dimQuestions.length * 5) * 1000) / 1000
    })
    results.value.skills = scores
  }
  ElMessage.success(`${questionnaires.find(q => q.key === key).title} 完成！`)
  quizVisible.value = false
  matchResults.value = []
}

// ==================== 匹配 ====================

const runMatching = async () => {
  if (!results.value.holland || !results.value.mbti || !results.value.values || !results.value.skills) {
    ElMessage.warning('请先完成所有四份问卷')
    return
  }
  matching.value = true
  try {
    const res = await axios.post(`${API}/match`, {
      hollandScores: results.value.holland,
      mbtiType: results.value.mbti,
      valueScores: VALUE_DIMS.map(d => results.value.values[d]),
      skillScores: SKILL_DIMS.map(d => results.value.skills[d]),
    })
    if (res.data.code === 200) {
      matchResults.value = res.data.data
      ElMessage.success('匹配完成！查看你的 TOP 3 职业推荐')
      activeTab.value = 'results'
    }
  } catch (e) { ElMessage.error('匹配失败，请稍后再试') }
  matching.value = false
}

// ==================== 预约 ====================

const apptForm = ref({ teacherId: 'T001', appointmentTime: '', reason: '' })

const submitAppointment = async () => {
  if (!apptForm.value.appointmentTime || !apptForm.value.reason) {
    ElMessage.warning('请填写预约时间和咨询原因')
    return
  }
  submittingAppt.value = true
  try {
    const res = await axios.post(`${API}/appointments`, {
      studentId: STUDENT_ID,
      studentName: STUDENT_NAME,
      teacherId: apptForm.value.teacherId,
      appointmentTime: apptForm.value.appointmentTime,
      reason: apptForm.value.reason,
    })
    if (res.data.code === 200) {
      ElMessage.success('预约已提交，等待辅导员确认')
      apptForm.value = { teacherId: 'T001', appointmentTime: '', reason: '' }
      loadAppointments()
      activeTab.value = 'myAppointments'
    }
  } catch (e) { ElMessage.error('提交失败') }
  submittingAppt.value = false
}

const loadAppointments = async () => {
  try {
    const res = await axios.get(`${API}/appointments?studentId=${STUDENT_ID}`)
    if (res.data.code === 200) appointments.value = res.data.data
  } catch (e) { console.error(e) }
}

const statusClass = (s) => ({ PENDING: 'bg-amber-100 text-amber-700', ACCEPTED: 'bg-green-100 text-green-700', REJECTED: 'bg-red-100 text-red-700' }[s] || '')
const statusText = (s) => ({ PENDING: '待确认', ACCEPTED: '已接受', REJECTED: '已拒绝' }[s] || s)

// ==================== 职业库 ====================

const loadJobLibrary = async () => {
  try {
    const res = await axios.get(`${API}/jobs`)
    if (res.data.code === 200) jobLibrary.value = res.data.data
  } catch (e) { console.error(e) }
}

const filteredJobs = computed(() => {
  if (!jobSearch.value) return jobLibrary.value
  const q = jobSearch.value.toLowerCase()
  return jobLibrary.value.filter(j => j.title.toLowerCase().includes(q))
})

// ==================== 计算属性 ====================

const hollandTop3 = computed(() => {
  if (!results.value.holland) return ''
  const order = ['R','I','A','S','E','C']
  const sorted = order.sort((a,b) => (results.value.holland[b]||0) - (results.value.holland[a]||0))
  return sorted.slice(0,3).join('')
})

const mbtiType = computed(() => results.value.mbti || '')

const topValue = computed(() => {
  if (!results.value.values) return ''
  const sorted = VALUE_DIMS.sort((a,b) => (results.value.values[b]||0) - (results.value.values[a]||0))
  return VALUE_LABELS[sorted[0]] || ''
})

const topSkill = computed(() => {
  if (!results.value.skills) return ''
  const sorted = SKILL_DIMS.sort((a,b) => (results.value.skills[b]||0) - (results.value.skills[a]||0))
  return SKILL_LABELS[sorted[0]] || ''
})

onMounted(() => {
  loadAppointments()
  loadJobLibrary()
})
</script>

<style scoped>
.career-plan :deep(.el-tabs__content) { padding: 0; }
.career-plan :deep(.el-tab-pane) { min-height: 400px; }
</style>
