<template>
  <div class="flex flex-col gap-6 h-[calc(100vh-100px)] min-h-[600px] pb-4">
    <!-- 主体：左右分栏两个独立的板块 -->
    <div class="flex-1 flex gap-6 min-h-0">
      
      <!-- 左栏板块 (占 58% 宽度) -->
      <div class="w-[58%] bg-white rounded-3xl border border-gray-200/60 shadow-sm overflow-hidden flex flex-col h-full">
        <!-- 列表顶部小工具栏：支持搜索和筛选 -->
        <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between flex-shrink-0 bg-gray-50/30">
          <div class="relative w-72 flex items-center">
            <span class="absolute left-3 flex items-center text-gray-400"><el-icon><Search /></el-icon></span>
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="搜索学号、姓名、反馈内容..." 
              class="w-full pl-9 pr-4 py-2 bg-white border border-gray-200 rounded-xl text-xs font-medium focus:outline-none focus:border-blue-500 transition-all"
            />
          </div>
          <!-- 快捷过滤 -->
          <div class="flex items-center gap-1">
            <button 
              v-for="filter in ['ALL', 'PENDING', 'REPLIED']" 
              :key="filter"
              @click="activeFilter = filter"
              class="px-2.5 py-1 text-[11px] font-bold rounded-lg transition-all"
              :class="activeFilter === filter ? 'bg-gray-900 text-white shadow-sm' : 'text-gray-400 hover:text-gray-700 bg-transparent'"
            >
              {{ {ALL: '全部', PENDING: '待处理', REPLIED: '已处理'}[filter] }}
            </button>
          </div>
        </div>

        <!-- 反馈列表区域 -->
        <div class="flex-1 overflow-y-auto p-6 space-y-3.5 custom-scrollbar min-h-0">
          <div 
            v-for="fb in filteredFeedbacks" 
            :key="fb.id"
            @click="selectFeedback(fb)"
            class="p-4 border rounded-2xl transition-all cursor-pointer bg-white relative flex flex-col"
            :class="selectedFeedback?.id === fb.id 
              ? 'border-blue-500 shadow-md ring-1 ring-blue-500/10' 
              : 'border-gray-100 hover:border-blue-300 hover:shadow-sm'"
          >
            <!-- 头部信息 -->
            <div class="flex items-start justify-between mb-2">
              <div class="flex items-center gap-2">
                <span class="w-8 h-8 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center font-bold text-xs">
                  {{ fb.studentName ? fb.studentName.substring(0, 1) : '学' }}
                </span>
                <div>
                  <span class="font-bold text-sm text-gray-900">{{ fb.studentName }}</span>
                  <span class="text-[10px] font-bold text-gray-400 bg-gray-100 px-1.5 py-0.5 rounded ml-2">{{ fb.studentId }}</span>
                </div>
              </div>
              <div class="flex items-center gap-2.5">
                <span class="text-xs text-gray-400 font-medium">{{ formatTime(fb.createTime) }}</span>
                <span class="px-2.5 py-0.5 rounded-full text-[10px] font-black border"
                      :class="fb.status === 'REPLIED' ? 'bg-green-50 text-green-700 border-green-200' : 'bg-orange-50 text-orange-600 border-orange-200'">
                  {{ fb.status === 'REPLIED' ? '已回复' : '待回复' }}
                </span>
              </div>
            </div>

            <!-- 反馈截断内容 -->
            <p class="text-xs text-gray-500 leading-relaxed line-clamp-2 mt-1">{{ fb.content }}</p>
          </div>

          <!-- 搜索/列表为空状态 -->
          <div v-if="filteredFeedbacks.length === 0" class="h-full flex flex-col items-center justify-center text-gray-400 py-16">
            <span class="text-4xl mb-3">📬</span>
            <p class="text-xs font-semibold">暂无匹配的意见反馈记录</p>
          </div>
        </div>
      </div>

      <!-- 右栏板块 (占 42% 宽度) -->
      <div class="w-[42%] bg-white rounded-3xl border border-gray-200/60 shadow-sm overflow-hidden flex flex-col h-full">
        <transition name="fade-slide" mode="out-in">
          <!-- 选中反馈详情 -->
          <div v-if="selectedFeedback" class="flex flex-col h-full min-h-0 bg-white" :key="selectedFeedback.id">
            <!-- 1-4. 反馈信息滚动区 (反馈内容 + 处理状态 + 历史回复) -->
            <div class="overflow-y-auto px-6 pt-6 pb-3 space-y-4 custom-scrollbar min-h-0"
                 :class="selectedFeedback.status === 'REPLIED' ? 'flex-1' : 'flex-shrink-0 max-h-[55%]'">
              <!-- 1. “反馈内容”标题 -->
              <div class="text-sm font-bold text-gray-900 tracking-wide">反馈内容</div>
              
              <!-- 2. 反馈实体内容 (无底色灰色线框) -->
              <div class="border border-gray-200 rounded-xl bg-white p-3.5 text-sm text-gray-800 leading-relaxed whitespace-pre-wrap text-justify">
                {{ selectedFeedback.content }}
              </div>

              <!-- 3. 处理状态 (紧跟在“处理状态”字后面，非右对齐) -->
              <div class="flex items-center gap-2">
                <div class="text-sm font-bold text-gray-900 tracking-wide">处理状态：</div>
                <span class="flex items-center gap-1.5 font-bold text-sm" :class="selectedFeedback.status === 'REPLIED' ? 'text-green-600' : 'text-orange-500'">
                  <span class="inline-block w-2.5 h-2.5 rounded-full" :class="selectedFeedback.status === 'REPLIED' ? 'bg-green-500' : 'bg-orange-500'"></span>
                  {{ selectedFeedback.status === 'REPLIED' ? '已处理' : '未处理' }}
                </span>
              </div>

              <!-- 4. 教职工的回复 (如有回复显示，亦无底色灰色线框) -->
              <div v-if="selectedFeedback.status === 'REPLIED'" class="space-y-4">
                <div class="flex items-center justify-between">
                  <div class="text-sm font-bold text-gray-900 tracking-wide">教职工回复</div>
                  <span class="text-xs text-gray-400 font-medium">{{ formatTime(selectedFeedback.replyTime || selectedFeedback.updateTime) }}</span>
                </div>
                <div class="border border-gray-200 rounded-xl bg-white p-3.5 text-sm text-gray-800 leading-relaxed whitespace-pre-wrap text-justify">
                  {{ selectedFeedback.reply }}
                </div>
              </div>
            </div>

            <!-- 5. 教师回复输入框区域 (填充剩余的全部空白高度) -->
            <div v-if="selectedFeedback.status !== 'REPLIED'" class="flex-1 px-6 pb-4 flex flex-col min-h-0">
              <label class="text-[13px] font-bold text-gray-500 uppercase tracking-wider block mb-2">教师回复</label>
              <div class="flex-1 border border-gray-200 rounded-xl overflow-hidden hover:border-gray-400 focus-within:border-blue-500 transition-colors bg-white flex flex-col min-h-0">
                <textarea 
                  v-model="replyText" 
                  class="w-full flex-1 text-sm outline-none bg-transparent px-3 py-2.5 placeholder-gray-300 text-gray-700 resize-none disabled:bg-gray-50 disabled:text-gray-400/80" 
                  placeholder="在此输入您的回复内容..."
                  maxlength="1000"
                  :disabled="selectedFeedback.status === 'REPLIED'"
                ></textarea>
                <div class="px-3 pb-2 text-right text-xs text-gray-300 select-none bg-transparent">
                  {{ replyText.length }} / 1000
                </div>
              </div>
            </div>

            <!-- 6. 底部固定操作栏 ("请审慎输入答复意见。发送回复"移动到最底部) -->
            <div v-if="selectedFeedback.status !== 'REPLIED'" class="px-6 py-4 border-t border-gray-100 flex items-center justify-between bg-gray-50/30 flex-shrink-0">
              <span class="text-xs text-gray-400 font-medium">请审慎输入答复意见。发送回复</span>
              <button 
                @click="submitReply(selectedFeedback.id)" 
                :disabled="submitting || !replyText.trim() || selectedFeedback.status === 'REPLIED'"
                class="px-6 py-2.5 bg-blue-600 text-white rounded-xl font-bold text-sm shadow-md hover:bg-blue-700 hover:shadow-lg transition-all flex items-center justify-center gap-1.5 disabled:opacity-40 disabled:cursor-not-allowed"
              >
                <span>{{ submitting ? '发送中...' : '发送回复' }}</span>
              </button>
            </div>
          </div>

          <!-- 未选中反馈状态 -->
          <div v-else class="h-full flex flex-col items-center justify-center text-gray-400/80 p-8">
            <el-icon class="text-5xl mb-4 text-gray-300"><Document /></el-icon>
            <p class="text-sm font-bold tracking-wide">选中意见后即可查看详情</p>
          </div>
        </transition>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Clock, Document, Check } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/communication/feedbacks'

const feedbacks = ref([])
const selectedFeedback = ref(null)
const replyText = ref('')
const submitting = ref(false)
const searchQuery = ref('')
const activeFilter = ref('ALL') // ALL, PENDING, REPLIED

const repliedCount = computed(() => feedbacks.value.filter(f => f.status === 'REPLIED').length)
const pendingCount = computed(() => feedbacks.value.filter(f => f.status !== 'REPLIED').length)

const filteredFeedbacks = computed(() => {
  return feedbacks.value.filter(fb => {
    // 快捷状态过滤
    if (activeFilter.value === 'PENDING' && fb.status === 'REPLIED') return false
    if (activeFilter.value === 'REPLIED' && fb.status !== 'REPLIED') return false

    // 关键词搜索过滤
    const query = searchQuery.value.trim().toLowerCase()
    if (!query) return true

    return (
      (fb.studentName && fb.studentName.toLowerCase().includes(query)) ||
      (fb.studentId && fb.studentId.toLowerCase().includes(query)) ||
      (fb.content && fb.content.toLowerCase().includes(query))
    )
  })
})

const loadFeedbacks = async () => {
  try {
    const res = await request.get(API)
    if (res.data.code === 200) {
      feedbacks.value = res.data.data || []
      // 保持之前的选中状态，或者若已更新则刷新详情引用
      if (selectedFeedback.value) {
        const updated = feedbacks.value.find(f => f.id === selectedFeedback.value.id)
        if (updated) {
          selectedFeedback.value = updated
        }
      }
    }
  } catch (e) {
    console.error('加载意见反馈失败', e)
  }
}

const selectFeedback = (fb) => {
  selectedFeedback.value = fb
  // 高保真细节：如果已经是回复状态，把历史回复预填入输入框，方便编辑或参考；否则清空输入框
  replyText.value = fb.reply || ''
}

const submitReply = async (id) => {
  if (!replyText.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  submitting.value = true
  try {
    const res = await request.put(`${API}/${id}/reply`, { reply: replyText.value })
    if (res.data.code === 200) {
      ElMessage.success('回复发送成功')
      await loadFeedbacks()
      // 更新当前选中的反馈缓存中的数据
      if (selectedFeedback.value && selectedFeedback.value.id === id) {
        const refreshed = feedbacks.value.find(f => f.id === id)
        if (refreshed) selectedFeedback.value = refreshed
      }
    } else {
      ElMessage.warning(res.data.msg || '发送回复失败')
    }
  } catch (e) {
    ElMessage.error('回复发送失败，请检查网络连接')
  } finally {
    submitting.value = false
  }
}

const formatTime = (s) => {
  if (!s) return ''
  try {
    const d = new Date(s)
    if (isNaN(d.getTime())) {
      return String(s).replace('T', ' ').substring(0, 16)
    }
    const pad = (n) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
  } catch (e) {
    return String(s)
  }
}

onMounted(loadFeedbacks)
</script>

<style scoped>
/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(15px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-15px);
}
</style>
