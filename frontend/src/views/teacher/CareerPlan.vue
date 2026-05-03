<template>
  <div class="h-full flex flex-col">
    <div class="mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <h2 class="text-[3.5rem] leading-none font-bold tracking-[-0.02em] text-on-surface mb-2">沟通互动</h2>
        <p class="text-[0.875rem] text-secondary font-medium">Announcements & Communication Center</p>
      </div>
      <button @click="showPublishDialog = true"
              class="bg-primary text-on-primary-fixed hover:bg-primary-fixed rounded-md px-5 py-2.5 text-[0.875rem] font-semibold flex items-center gap-2 shadow-md">
        <el-icon><EditPen /></el-icon>
        发布公告
      </button>
    </div>

    <!-- Announcement Feed -->
    <div class="flex-1 grid grid-cols-1 md:grid-cols-12 gap-6 overflow-hidden">
      <div class="md:col-span-8 flex flex-col bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] overflow-hidden">
        <h3 class="text-lg font-semibold text-on-surface tracking-tight mb-5">已发布公告</h3>
        <div class="flex-1 overflow-y-auto space-y-4 custom-scrollbar pr-2">
          <div v-for="(a, i) in announcements" :key="i"
               class="p-5 bg-surface rounded-xl border hover:border-primary/20 transition-colors group"
               :class="a.isPinned ? 'border-primary/30 ring-1 ring-primary/10' : 'border-outline-variant/20'">
            <div class="flex items-start justify-between gap-2 mb-2 flex-wrap">
              <div class="flex items-center gap-2 flex-wrap">
                <el-icon v-if="a.isPinned" class="text-primary text-sm"><StarFilled /></el-icon>
                <span class="text-xs px-2 py-0.5 rounded-full font-bold"
                      :class="typeStyle(a.type)">{{ typeLabel(a.type) }}</span>
                <h4 class="font-bold text-sm text-on-surface">{{ a.title }}</h4>
              </div>
              <div class="flex items-center gap-2">
                <span class="text-xs text-outline">{{ formatDate(a.publishTime) }}</span>
                <button @click="deleteAnnouncement(a.id)"
                        class="opacity-0 group-hover:opacity-100 text-error hover:text-error/80 transition-all text-xs font-semibold">删除</button>
              </div>
            </div>
            <p class="text-sm text-secondary leading-relaxed">{{ a.content }}</p>
            <p class="text-xs text-outline mt-2">发布人: {{ a.publisherName }}</p>
          </div>
          <div v-if="announcements.length === 0" class="py-12 text-center text-secondary">
            <el-icon :size="48" class="mb-2 opacity-30"><Bell /></el-icon>
            <p>暂无公告</p>
          </div>
        </div>
      </div>

      <!-- Quick Compose Panel -->
      <div class="md:col-span-4 bg-surface-container-lowest rounded-xl p-6 border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] flex flex-col">
        <h3 class="text-lg font-semibold text-on-surface tracking-tight mb-5">快速发布</h3>
        <div class="space-y-4 flex-1">
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">公告类型</label>
            <div class="grid grid-cols-3 gap-2">
              <button v-for="t in types" :key="t.value" @click="quickForm.type = t.value"
                      class="py-2 rounded-lg text-xs font-bold border transition-colors"
                      :class="quickForm.type === t.value ? 'bg-primary text-on-primary-fixed border-primary' : 'bg-surface border-outline-variant/30 text-secondary hover:border-primary/30'">
                {{ t.label }}
              </button>
            </div>
          </div>
          <div>
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">标题</label>
            <el-input v-model="quickForm.title" placeholder="公告标题" />
          </div>
          <div class="flex-1">
            <label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">内容</label>
            <el-input v-model="quickForm.content" type="textarea" :rows="6" placeholder="请输入公告内容..." />
          </div>
          <div class="flex items-center gap-2">
            <el-switch v-model="quickForm.isPinned" />
            <span class="text-sm text-secondary">置顶显示</span>
          </div>
        </div>
        <button @click="publishAnnouncement"
                class="mt-6 w-full py-3 bg-primary text-on-primary-fixed rounded-xl font-bold text-sm hover:bg-primary-fixed transition-colors flex items-center justify-center gap-2 shadow-md">
          <el-icon><Check /></el-icon>
          立即发布给全体学生
        </button>
      </div>
    </div>

    <el-dialog v-model="showPublishDialog" title="发布公告" width="500px" align-center>
      <div class="space-y-4 p-2">
        <el-input v-model="quickForm.title" placeholder="公告标题" />
        <el-select v-model="quickForm.type" style="width: 100%">
          <el-option label="普通通知" value="NOTICE" />
          <el-option label="紧急通知" value="URGENT" />
          <el-option label="活动通知" value="EVENT" />
        </el-select>
        <el-input v-model="quickForm.content" type="textarea" :rows="5" placeholder="公告内容..." />
        <div class="flex items-center gap-2">
          <el-switch v-model="quickForm.isPinned" />
          <span class="text-sm">置顶显示</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPublishDialog = false">取消</el-button>
        <el-button type="primary" @click="publishAnnouncement">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { EditPen, StarFilled, Bell, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const announcements = ref([])
const showPublishDialog = ref(false)
const quickForm = ref({ title: '', content: '', type: 'NOTICE', publisherName: '辅导员', isPinned: false })
const types = [{ label: '通知', value: 'NOTICE' }, { label: '紧急', value: 'URGENT' }, { label: '活动', value: 'EVENT' }]

const API = 'http://localhost:8080/api/communication'

const loadData = async () => {
  try {
    const res = await axios.get(`${API}/announcements`)
    if (res.data.code === 200) announcements.value = res.data.data
  } catch (e) { console.error(e) }
}

const publishAnnouncement = async () => {
  if (!quickForm.value.title || !quickForm.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  try {
    await axios.post(`${API}/announcements`, quickForm.value)
    ElMessage.success('公告已发布')
    showPublishDialog.value = false
    quickForm.value = { title: '', content: '', type: 'NOTICE', publisherName: '辅导员', isPinned: false }
    await loadData()
  } catch (e) { ElMessage.error('发布失败，请确保后端服务已启动') }
}

const deleteAnnouncement = async (id) => {
  try {
    await axios.delete(`${API}/announcements/${id}`)
    ElMessage.success('公告已删除')
    await loadData()
  } catch (e) { ElMessage.error('删除失败') }
}

const typeStyle = (t) => ({ URGENT: 'bg-error-container text-error border border-error/20', EVENT: 'bg-blue-100 text-tertiary-container border border-blue-200', NOTICE: 'bg-surface-container-high text-secondary border border-outline-variant/30' }[t])
const typeLabel = (t) => ({ URGENT: '紧急', EVENT: '活动', NOTICE: '通知' }[t] || t)
const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

onMounted(loadData)
</script>
