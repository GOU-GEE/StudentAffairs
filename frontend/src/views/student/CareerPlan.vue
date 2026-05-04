<template>
  <div class="h-full flex flex-col">

    <!-- Main Bento Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6">

      <!-- AI Career Card -->
      <div class="md:col-span-8 bg-surface-container-lowest rounded-xl p-7 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] relative overflow-hidden">
        <div class="absolute -right-16 -bottom-16 w-56 h-56 bg-ai-primary/8 rounded-full blur-[60px] pointer-events-none"></div>
        
        <div class="flex items-center gap-3 mb-6 relative z-10">
          <div class="w-10 h-10 rounded-xl bg-ai-primary text-white flex items-center justify-center shadow-lg shadow-ai-primary/30">
            <el-icon :size="20"><Guide /></el-icon>
          </div>
          <div>
            <h3 class="font-bold text-on-surface">AI 职业路径推演</h3>
            <p class="text-xs text-secondary">基于你的成绩、技能标签与兴趣分析</p>
          </div>
          <div class="ml-auto flex items-center gap-2 text-xs text-ai-primary font-bold">
            <span class="w-2 h-2 rounded-full bg-ai-primary animate-pulse"></span>
            AI 分析就绪
          </div>
        </div>

        <!-- Career Tracks -->
        <div class="space-y-4 relative z-10">
          <div v-for="(track, i) in careerTracks" :key="i"
               class="p-4 rounded-xl border cursor-pointer transition-all hover:scale-[1.01]"
               :class="selectedTrack === i ? 'border-ai-primary bg-ai-primary/5 shadow-md shadow-ai-primary/10' : 'border-outline-variant/20 bg-surface hover:border-ai-primary/40'"
               @click="selectedTrack = i">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-3">
                <span class="text-xl">{{ track.emoji }}</span>
                <div>
                  <h4 class="font-bold text-sm text-on-surface">{{ track.title }}</h4>
                  <p class="text-xs text-secondary">{{ track.subtitle }}</p>
                </div>
              </div>
              <div class="text-right">
                <div class="text-xs font-bold px-2.5 py-1 rounded-full"
                     :class="track.matchColor">匹配度 {{ track.match }}%</div>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <div class="flex-1 bg-surface-container-high h-1.5 rounded-full overflow-hidden">
                <div class="h-full rounded-full bg-ai-primary transition-all duration-700"
                     :style="`width: ${selectedTrack === i ? track.match : 0}%`"></div>
              </div>
            </div>
            <!-- Expanded Details -->
            <transition name="slide">
              <div v-if="selectedTrack === i" class="mt-4 pt-4 border-t border-outline-variant/20">
                <p class="text-sm text-secondary leading-relaxed mb-3">{{ track.description }}</p>
                <div class="flex flex-wrap gap-2">
                  <span v-for="skill in track.skills" :key="skill"
                        class="text-xs px-3 py-1.5 rounded-full bg-ai-primary/10 text-ai-primary border border-ai-primary/20 font-medium">
                    {{ skill }}
                  </span>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </div>

      <!-- Right Panel: Skills & Roadmap -->
      <div class="md:col-span-4 space-y-5">
        <!-- Skills Radar -->
        <div class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)]">
          <h3 class="font-semibold text-on-surface text-sm mb-4">当前技能评估</h3>
          <div class="space-y-3">
            <div v-for="(skill, i) in skills" :key="i">
              <div class="flex justify-between items-center mb-1">
                <span class="text-xs font-semibold text-secondary">{{ skill.name }}</span>
                <span class="text-xs font-bold text-on-surface">{{ skill.score }}/100</span>
              </div>
              <div class="w-full bg-surface-container-high h-2 rounded-full overflow-hidden">
                <div class="h-full rounded-full transition-all duration-1000"
                     :class="skill.color"
                     :style="`width: ${skill.score}%`"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Roadmap Timeline -->
        <div class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)]">
          <h3 class="font-semibold text-on-surface text-sm mb-4">
            {{ careerTracks[selectedTrack]?.title }} 成长路线图
          </h3>
          <div class="space-y-3">
            <div v-for="(step, i) in careerTracks[selectedTrack]?.roadmap" :key="i" class="flex gap-3">
              <div class="flex flex-col items-center">
                <div class="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0"
                     :class="step.done ? 'bg-ai-primary text-white' : 'bg-surface-container-high text-secondary border border-outline-variant/30'">
                  {{ step.done ? '✓' : i + 1 }}
                </div>
                <div class="w-0.5 flex-1 mt-1" :class="step.done ? 'bg-ai-primary/40' : 'bg-outline-variant/20'" v-if="i < careerTracks[selectedTrack]?.roadmap.length - 1"></div>
              </div>
              <div class="pb-3">
                <p class="text-sm font-semibold text-on-surface">{{ step.title }}</p>
                <p class="text-xs text-secondary mt-0.5">{{ step.detail }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Guide } from '@element-plus/icons-vue'

const selectedTrack = ref(0)

const skills = ref([
  { name: '数据结构与算法', score: 92, color: 'bg-ai-primary' },
  { name: '工程实践能力', score: 78, color: 'bg-green-500' },
  { name: '英语与国际化', score: 72, color: 'bg-blue-400' },
  { name: '团队协作与领导力', score: 65, color: 'bg-orange-400' },
  { name: '产品与商业思维', score: 50, color: 'bg-purple-400' }
])

const careerTracks = ref([
  {
    emoji: '🤖', title: 'AI / 大模型研发工程师', subtitle: '人工智能 · 研究型',
    match: 88,
    matchColor: 'bg-ai-primary/15 text-ai-primary',
    description: '你在数据结构（95分）和算法（88分）上的卓越表现，高度符合大模型研发岗位对理论基础的要求。建议加深 PyTorch、Transformers 和 CUDA 的学习。',
    skills: ['PyTorch', 'Transformer', 'CUDA', '数学基础', '分布式训练'],
    roadmap: [
      { title: '夯实数学基础', detail: '线性代数、概率论、最优化理论', done: true },
      { title: '掌握深度学习框架', detail: 'PyTorch 实践，复现经典论文', done: true },
      { title: '参与开源项目', detail: 'GitHub 贡献，积累工程经验', done: false },
      { title: '算法/大厂实习', detail: '字节 / 百度 / 华为暑期实习', done: false },
      { title: '研究生深造', detail: '推免目标：清/北/交大 AI 方向', done: false }
    ]
  },
  {
    emoji: '⚙️', title: '后端架构师 / 平台研发', subtitle: '分布式系统 · 高并发',
    match: 82,
    matchColor: 'bg-green-100 text-green-700',
    description: '你的数据库（86分）和软件工程（91分）成绩显示出扎实的系统设计功底。Java 和 Go 的深入学习将帮助你进入大厂基础架构团队。',
    skills: ['Java/Go', 'Kafka', 'Redis', 'MySQL 调优', '微服务'],
    roadmap: [
      { title: '精通 Java 生态', detail: 'JVM 调优、Spring 核心原理', done: true },
      { title: '分布式系统实践', detail: 'Redis 集群、Kafka 消息队列', done: false },
      { title: '系统设计强化', detail: '刷 System Design Primer', done: false },
      { title: '后端校招冲刺', detail: '目标：字节/腾讯/阿里后端', done: false },
      { title: '技术管理进阶', detail: '带领小团队完成复杂项目', done: false }
    ]
  },
  {
    emoji: '🏀', title: '体育科技 / 数据分析师', subtitle: 'AI + 运动科学 · 新兴赛道',
    match: 71,
    matchColor: 'bg-blue-100 text-blue-700',
    description: '你热爱篮球，同时具备强大的数据分析能力。这让你在体育科技赛道（如 NBA 数据公司、运动智能硬件厂商）中具备独特竞争力。',
    skills: ['Python', '数据可视化', '运动生物力学', '机器学习', '商业分析'],
    roadmap: [
      { title: '数据分析基础', detail: 'Python、Pandas、Matplotlib', done: true },
      { title: '体育数据项目', detail: '搭建篮球数据可视化平台', done: false },
      { title: '跨学科拓展', detail: '辅修运动科学或生物力学', done: false },
      { title: '行业实习', detail: '体育媒体、运动品牌数据岗', done: false },
      { title: '创业或加入独角兽', detail: '运动科技领域初创公司', done: false }
    ]
  }
])
</script>

<style scoped>
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}
.slide-enter-from,
.slide-leave-to {
  max-height: 0;
  opacity: 0;
}
.slide-enter-to,
.slide-leave-from {
  max-height: 400px;
  opacity: 1;
}
</style>
