<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button class="bg-emerald-500 text-white hover:bg-emerald-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Download /></el-icon>导出报表
      </button>
    </teleport>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in stats" :key="idx" class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/15 hover:shadow-md transition-shadow">
        <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        <h3 class="text-[2rem] font-bold text-on-surface mt-1">{{ stat.value }}</h3>
        <p class="text-xs text-secondary mt-1">{{ stat.desc }}</p>
      </div>
    </div>

    <!-- Quick Actions + Recent -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 auto-rows-min">
      <div class="md:col-span-5 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">快捷操作</h3>
        <div class="grid grid-cols-2 gap-3">
          <button @click="$router.push('/youth/second-classroom')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="#10b981"><Timer /></el-icon>
            <span class="text-sm font-semibold text-on-surface">发放学时</span>
            <span class="text-xs text-secondary">第二课堂学时管理</span>
          </button>
          <button @click="$router.push('/youth/activities')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="#10b981"><Calendar /></el-icon>
            <span class="text-sm font-semibold text-on-surface">发布活动</span>
            <span class="text-xs text-secondary">校园活动管理</span>
          </button>
          <button @click="$router.push('/youth/awards')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="#10b981"><Medal /></el-icon>
            <span class="text-sm font-semibold text-on-surface">审核获奖</span>
            <span class="text-xs text-secondary">学生获奖记录</span>
          </button>
          <button @click="$router.push('/youth/honors')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="#10b981"><Trophy /></el-icon>
            <span class="text-sm font-semibold text-on-surface">评优评奖</span>
            <span class="text-xs text-secondary">荣誉评选管理</span>
          </button>
        </div>
      </div>

      <div class="md:col-span-7 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 flex flex-col h-[320px]">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">近期活动参与统计</h3>
        <div class="space-y-3">
          <div v-for="(item, idx) in recentActivities" :key="idx" class="bg-surface rounded-xl p-4 border border-outline-variant/15 flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg flex items-center justify-center bg-emerald-100 text-emerald-600">
              <el-icon :size="20"><Calendar /></el-icon>
            </div>
            <div class="flex-1 min-w-0">
              <h4 class="font-semibold text-[0.875rem] text-on-surface">{{ item.title }}</h4>
              <p class="text-xs text-secondary">{{ item.time }} | {{ item.participants }}人参与</p>
            </div>
            <span class="text-xs font-bold px-2 py-1 rounded-full" :class="item.status === '进行中' ? 'bg-emerald-100 text-emerald-600' : 'bg-gray-100 text-gray-500'">{{ item.status }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Download, Timer, Calendar, Medal, Trophy } from '@element-plus/icons-vue'

const stats = [
  { label: '总学时数', value: '4,280', desc: '已发放 3,850 + 待发放 430' },
  { label: '活跃活动', value: 6, desc: '进行中 2 个 + 报名中 4 个' },
  { label: '待审获奖', value: 5, desc: '学科竞赛 2 + 文体 3' },
  { label: '评优项目', value: 3, desc: '三好学生 / 优干 / 优秀团员' },
]

const recentActivities = [
  { title: '第十七届电脑文化艺术节', time: '2026-05-20', participants: 156, status: '报名中' },
  { title: 'AI赋能未来主题讲座', time: '2026-05-18', participants: 89, status: '进行中' },
  { title: '红色经典诵读比赛', time: '2026-05-12', participants: 45, status: '已结束' },
  { title: '3V3 班级篮球赛', time: '2026-05-10', participants: 120, status: '已结束' },
]
</script>
