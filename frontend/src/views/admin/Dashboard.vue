<template>
  <div class="h-full flex flex-col">
    <!-- Stats Overview -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
      <div v-for="(stat, idx) in stats" :key="idx" class="bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_2px_8px_rgba(25,28,30,0.02)] hover:shadow-md transition-shadow relative overflow-hidden group">
        <div class="flex justify-between items-start mb-2">
          <span class="text-[0.875rem] font-semibold text-secondary uppercase tracking-wider">{{ stat.label }}</span>
        </div>
        <h3 class="text-[2.5rem] font-bold text-on-surface mt-2">{{ stat.value }}</h3>
        <div class="absolute -right-4 -bottom-4 opacity-10 group-hover:scale-110 group-hover:opacity-20 transition-all duration-300">
          <el-icon :size="80" :color="stat.iconColor"><component :is="stat.icon" /></el-icon>
        </div>
      </div>
    </div>

    <!-- Quick Actions + Recent Import -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 auto-rows-min">
      <!-- Quick Actions -->
      <div class="md:col-span-5 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)]">
        <h3 class="text-[1.125rem] font-semibold text-on-surface mb-4">快捷操作</h3>
        <div class="grid grid-cols-2 gap-3">
          <router-link to="/admin/students" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="var(--color-error)"><User /></el-icon>
            <span class="text-sm font-semibold text-on-surface">导入学生信息</span>
            <span class="text-xs text-secondary">批量导入学生档案</span>
          </router-link>
          <router-link to="/admin/grades" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="var(--color-error)"><DataAnalysis /></el-icon>
            <span class="text-sm font-semibold text-on-surface">导入成绩数据</span>
            <span class="text-xs text-secondary">批量导入学业成绩</span>
          </router-link>
          <router-link to="/admin/accounts" class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="var(--color-error)"><Avatar /></el-icon>
            <span class="text-sm font-semibold text-on-surface">账号管理</span>
            <span class="text-xs text-secondary">创建与管理账号</span>
          </router-link>
          <button class="flex flex-col items-center gap-2 p-4 rounded-xl bg-surface hover:bg-surface-container-high transition-colors border border-outline-variant/15 text-left">
            <el-icon :size="28" color="var(--color-error)"><Setting /></el-icon>
            <span class="text-sm font-semibold text-on-surface">系统配置</span>
            <span class="text-xs text-secondary">全局参数与权限</span>
          </button>
        </div>
      </div>

      <!-- Recent Imports -->
      <div class="md:col-span-7 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] flex flex-col h-[320px]">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-[1.125rem] font-semibold text-on-surface tracking-tight">近期数据导入记录</h3>
        </div>
        <div class="flex-1 overflow-y-auto pr-2 space-y-3 custom-scrollbar">
          <div v-for="(item, idx) in importHistory" :key="idx" class="bg-surface rounded-xl p-4 border border-outline-variant/15 flex items-center gap-4">
            <div class="w-10 h-10 rounded-lg flex items-center justify-center" :class="item.status === '成功' ? 'bg-green-100 text-green-600' : 'bg-orange-100 text-orange-600'">
              <el-icon :size="20"><component :is="item.icon" /></el-icon>
            </div>
            <div class="flex-1 min-w-0">
              <h4 class="font-semibold text-[0.875rem] text-on-surface">{{ item.title }}</h4>
              <p class="text-xs text-secondary">操作人: {{ item.operator }} | {{ item.time }}</p>
            </div>
            <div class="text-right">
              <span class="text-[0.75rem] font-bold px-2 py-1 rounded-full" :class="item.status === '成功' ? 'text-green-600 bg-green-100' : 'text-orange-600 bg-orange-100'">{{ item.status }}</span>
              <p class="text-xs text-secondary mt-1">{{ item.count }} 条记录</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Download, Setting, User, DataAnalysis, Avatar, Document, UploadFilled } from '@element-plus/icons-vue'

const stats = [
  { label: '学生总数', value: 3560, icon: User, iconColor: 'var(--color-error)' },
  { label: '教师/辅导员', value: 48, icon: Avatar, iconColor: 'var(--color-error)' },
  { label: '本月导入记录', value: 12, icon: DataAnalysis, iconColor: 'var(--color-error)' },
  { label: '系统账号数', value: 5, icon: Setting, iconColor: 'var(--color-error)' },
]

const importHistory = [
  { title: '2024级新生信息导入', operator: '系统管理员', time: '2026-05-15 14:30', status: '成功', count: 1250, icon: Document },
  { title: '2024-2025-1学期成绩导入', operator: '系统管理员', time: '2026-05-10 09:15', status: '成功', count: 4560, icon: DataAnalysis },
  { title: '2023级学生档案更新', operator: '系统管理员', time: '2026-05-08 16:45', status: '部分失败', count: '320/356', icon: UploadFilled },
  { title: '困难认定数据导入', operator: '系统管理员', time: '2026-05-05 11:00', status: '成功', count: 180, icon: Document },
]
</script>
