<template>
  <div class="h-full flex flex-col">
    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div v-for="(stat, idx) in stats" :key="idx" class="bg-surface-container-lowest rounded-xl p-5 border border-outline-variant/15 hover:shadow-md transition-shadow">
        <div class="flex justify-between items-start mb-2">
          <span class="text-xs font-bold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        </div>
        <h3 class="text-[2rem] font-bold text-on-surface">{{ stat.value }}</h3>
        <p class="text-xs text-secondary mt-1">{{ stat.desc }}</p>
      </div>
    </div>

    <div class="flex items-center gap-3 mb-4">
      <button class="bg-amber-500 text-white hover:bg-amber-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Download /></el-icon>导出报表
      </button>
    </div>

    <!-- Quick Actions + Recent -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 auto-rows-min">
      <div class="md:col-span-5 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">快捷操作</h3>
        <div class="grid grid-cols-2 gap-3">
          <button @click="$router.push('/financial/scholarships')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="#f59e0b"><Trophy /></el-icon>
            <span class="text-sm font-semibold text-on-surface">奖学金审批</span>
            <span class="text-xs text-secondary">查看待审申请</span>
          </button>
          <button @click="$router.push('/financial/work-study')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="#f59e0b"><Briefcase /></el-icon>
            <span class="text-sm font-semibold text-on-surface">岗位管理</span>
            <span class="text-xs text-secondary">勤工助学岗位</span>
          </button>
          <button @click="$router.push('/financial/poverty')" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="#f59e0b"><Stamp /></el-icon>
            <span class="text-sm font-semibold text-on-surface">困难认定</span>
            <span class="text-xs text-secondary">学生困难等级</span>
          </button>
          <button class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="#f59e0b"><DataAnalysis /></el-icon>
            <span class="text-sm font-semibold text-on-surface">数据统计</span>
            <span class="text-xs text-secondary">资助情况分析</span>
          </button>
        </div>
      </div>

      <div class="md:col-span-7 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 flex flex-col h-[320px]">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">近期审批记录</h3>
        <div class="flex-1 overflow-y-auto pr-2 space-y-3 custom-scrollbar">
          <div v-for="(item, idx) in recentReviews" :key="idx" class="bg-surface rounded-xl p-4 border border-outline-variant/15 flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg flex items-center justify-center" :class="item.result === '通过' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'">
              <el-icon :size="20"><component :is="item.result === '通过' ? CircleCheck : CircleClose" /></el-icon>
            </div>
            <div class="flex-1 min-w-0">
              <h4 class="font-semibold text-[0.875rem] text-on-surface">{{ item.title }}</h4>
              <p class="text-xs text-secondary">{{ item.student }} | {{ item.time }}</p>
            </div>
            <span class="text-xs font-bold px-2 py-1 rounded-full" :class="item.result === '通过' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'">{{ item.result }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Download, Trophy, Briefcase, Stamp, DataAnalysis, CircleCheck, CircleClose } from '@element-plus/icons-vue'

const stats = [
  { label: '待审批申请', value: 3, desc: '奖学金2项 + 助学金1项' },
  { label: '勤工助学岗位', value: 5, desc: '活跃岗位3个 + 已满2个' },
  { label: '困难认定学生', value: 128, desc: 'A档32人 + B档58人 + C档38人' },
  { label: '本月发放金额', value: '¥8.5万', desc: '奖学金5.2万 + 助学金3.3万' },
]

const recentReviews = [
  { title: '国家励志奖学金申请', student: '张小明 (202301042)', time: '2026-05-15 14:30', result: '通过' },
  { title: '国家助学金申请', student: '李四 (202301043)', time: '2026-05-14 10:00', result: '驳回' },
  { title: '校级奖学金申请', student: '王五 (202301044)', time: '2026-05-13 16:00', result: '通过' },
  { title: '勤工助学岗位申请', student: '赵六 (202301045)', time: '2026-05-12 09:30', result: '通过' },
]
</script>
