<template>
  <div class="h-full flex flex-col">

    <!-- Pinned Announcement -->
    <div v-if="pinned" class="mb-5 p-5 bg-primary text-on-primary-fixed rounded-xl border border-primary flex gap-4 items-start shadow-[0_8px_20px_rgba(49,107,243,0.2)]">
      <div class="w-10 h-10 rounded-lg bg-on-primary-fixed/10 flex items-center justify-center flex-shrink-0">
        <el-icon :size="20"><StarFilled /></el-icon>
      </div>
      <div>
        <p class="text-xs font-bold uppercase tracking-wider opacity-70 mb-1">置顶公告</p>
        <h3 class="font-bold text-base">{{ pinned.title }}</h3>
        <p class="text-sm opacity-80 mt-1 leading-relaxed">{{ pinned.content }}</p>
        <p class="text-xs opacity-60 mt-2">{{ pinned.publisherName }} · {{ formatDate(pinned.publishTime) }}</p>
      </div>
    </div>

    <!-- Other Announcements -->
    <div class="flex-1 overflow-y-auto space-y-3 custom-scrollbar">
      <div v-for="(a, i) in nonPinned" :key="i"
           class="p-5 bg-surface-container-lowest rounded-xl border border-outline-variant/15 hover:border-primary/20 transition-colors">
        <div class="flex items-center gap-2 mb-2">
          <span class="text-xs px-2 py-0.5 rounded-full font-bold"
                :class="typeStyle(a.type)">{{ typeLabel(a.type) }}</span>
          <h4 class="font-semibold text-sm text-on-surface">{{ a.title }}</h4>
          <span class="ml-auto text-xs text-outline flex-shrink-0">{{ formatDate(a.publishTime) }}</span>
        </div>
        <p class="text-sm text-secondary leading-relaxed">{{ a.content }}</p>
        <p class="text-xs text-outline mt-2">发布人: {{ a.publisherName }}</p>
      </div>

      <div v-if="announcements.length === 0" class="py-16 flex flex-col items-center text-secondary">
        <el-icon :size="48" class="mb-2 opacity-30"><Bell /></el-icon>
        <p>暂无通知</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { StarFilled, Bell } from '@element-plus/icons-vue'
import axios from 'axios'

const announcements = ref([])

const pinned = computed(() => announcements.value.find(a => a.isPinned))
const nonPinned = computed(() => announcements.value.filter(a => !a.isPinned))

const loadData = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/communication/announcements')
    if (res.data.code === 200) announcements.value = res.data.data
  } catch (e) { console.error(e) }
}

const typeStyle = (t) => ({ URGENT: 'bg-error-container text-error border border-error/20', EVENT: 'bg-blue-100 text-tertiary-container border border-blue-200', NOTICE: 'bg-surface-container-high text-secondary border border-outline-variant/30' }[t])
const typeLabel = (t) => ({ URGENT: '紧急', EVENT: '活动', NOTICE: '通知' }[t] || t)
const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

onMounted(loadData)
</script>
