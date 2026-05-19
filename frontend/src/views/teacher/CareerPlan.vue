<template>
  <div class="h-full flex flex-col">
    <div class="flex-1 grid grid-cols-1 md:grid-cols-12 gap-6 overflow-hidden">
      <!-- 左侧：发布通知 / 公告详情 -->
      <div class="md:col-span-7 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] flex flex-col overflow-hidden">
        <!-- 发布通知模式 -->
        <template v-if="!viewingAnnouncement">
          <h3 class="text-lg font-semibold text-on-surface tracking-tight mb-5">发布通知</h3>
          <div class="space-y-4 flex-1">
            <div>
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">标题</label>
              <el-input v-model="form.title" placeholder="请输入通知标题" />
            </div>
            <div class="flex-1">
              <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">内容</label>
              <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入通知内容..." />
            </div>
            <div class="flex items-center gap-2">
              <el-switch v-model="form.isPinned" />
              <span class="text-sm text-secondary">置顶显示</span>
            </div>
          </div>
          <div class="flex justify-end mt-6">
            <button @click="publishAnnouncement"
                    class="px-8 py-2.5 bg-primary text-on-primary-fixed rounded-xl font-bold text-sm hover:bg-primary-fixed transition-colors flex items-center gap-2 shadow-md">
              <el-icon><Check /></el-icon>
              发送
            </button>
          </div>
        </template>

        <!-- 公告详情模式 -->
        <template v-else>
          <div class="flex items-start justify-between mb-4">
            <h2 class="text-xl font-bold text-on-surface leading-snug flex-1 pr-4">{{ viewingAnnouncement.title }}</h2>
            <button @click="viewingAnnouncement = null"
                    class="w-8 h-8 rounded-full bg-surface-container-high hover:bg-error-container hover:text-error flex items-center justify-center flex-shrink-0 transition-colors"
                    title="关闭详情">
              <el-icon :size="18"><Close /></el-icon>
            </button>
          </div>
          <p class="text-xs text-outline mb-6">{{ formatFullDate(viewingAnnouncement.publishTime) }}</p>
          <div class="flex-1 overflow-y-auto custom-scrollbar">
            <p class="text-sm text-on-surface leading-relaxed whitespace-pre-wrap">{{ viewingAnnouncement.content }}</p>
          </div>
        </template>
      </div>

      <!-- 右侧：已发布公告历史 -->
      <div class="md:col-span-5 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] overflow-hidden flex flex-col">
        <h3 class="text-lg font-semibold text-on-surface tracking-tight mb-5">已发布公告</h3>
        <div class="flex-1 overflow-y-auto space-y-3 custom-scrollbar pr-2">
          <div v-for="a in announcements" :key="a.id"
               @click="viewAnnouncement(a)"
               class="p-4 bg-surface rounded-xl border hover:border-primary/20 transition-colors group cursor-pointer"
               :class="a.isPinned ? 'border-primary/30 ring-1 ring-primary/10' : 'border-outline-variant/20'">
            <div class="flex items-start justify-between gap-2 mb-1.5">
              <div class="flex items-center gap-2 min-w-0">
                <el-icon v-if="a.isPinned" class="text-primary text-sm flex-shrink-0"><Top /></el-icon>
                <h4 class="font-bold text-sm text-on-surface truncate">{{ a.title }}</h4>
              </div>
              <div class="flex items-center gap-1.5 flex-shrink-0">
                <span class="text-[11px] text-outline">{{ formatDate(a.publishTime) }}</span>
                <button @click.stop="deleteAnnouncement(a.id)"
                        class="opacity-0 group-hover:opacity-100 text-error hover:text-error/80 transition-all p-0.5"
                        title="删除">
                  <el-icon :size="14"><Delete /></el-icon>
                </button>
              </div>
            </div>
            <p class="text-xs text-secondary leading-relaxed line-clamp-2">{{ a.content }}</p>
          </div>
          <div v-if="announcements.length === 0" class="py-16 flex flex-col items-center text-secondary">
            <el-icon :size="48" class="mb-2 opacity-30"><Bell /></el-icon>
            <p class="text-sm">暂无已发布的公告</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Check, Bell, Delete, Top, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const API = '/api/communication'
const publisherId = sessionStorage.getItem('userId') || 'T001'
const publisherName = sessionStorage.getItem('userName') || '李老师'

const announcements = ref([])
const form = ref({ title: '', content: '', isPinned: false })
const viewingAnnouncement = ref(null)

const loadAnnouncements = async () => {
  try {
    const res = await request.get(`${API}/announcements`, { params: { publisherId } })
    if (res.data.code === 200) announcements.value = res.data.data
  } catch (e) { console.error(e) }
}

const viewAnnouncement = (a) => {
  viewingAnnouncement.value = a
}

const publishAnnouncement = async () => {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  try {
    await request.post(`${API}/announcements`, {
      title: form.value.title,
      content: form.value.content,
      publisherName,
      publisherId,
      type: 'NOTICE',
      isPinned: form.value.isPinned
    })
    ElMessage.success('通知已发布')
    form.value = { title: '', content: '', isPinned: false }
    await loadAnnouncements()
  } catch (e) { ElMessage.error('发布失败，请确保后端服务已启动') }
}

const deleteAnnouncement = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条公告吗？删除后学生端将不可见。', '确认删除', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    if (viewingAnnouncement.value?.id === id) viewingAnnouncement.value = null
    await request.delete(`${API}/announcements/${id}`)
    ElMessage.success('公告已删除')
    await loadAnnouncements()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const formatDate = (s) => {
  if (!s) return ''
  const d = new Date(s)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  if (diff < 86400000) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const formatFullDate = (s) => {
  if (!s) return ''
  const d = new Date(s)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

onMounted(loadAnnouncements)
</script>
