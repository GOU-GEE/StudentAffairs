<template>
  <div class="space-y-6 animate-fade-in pb-10">
    <!-- 第一排：获奖情况 & 评奖评优情况 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 获奖情况 -->
      <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 shadow-sm hover:bg-white/50 transition-all p-6 flex flex-col min-h-[520px]">
        <div class="flex justify-between items-center mb-6 pb-4 border-b border-gray-100/30 gap-6 flex-shrink-0">
          <div class="flex items-center gap-4 flex-shrink-0">
            <div class="w-10 h-10 rounded-xl bg-blue-50 text-blue-500 flex items-center justify-center text-xl shadow-sm flex-shrink-0">
              <el-icon><Medal /></el-icon>
            </div>
            <div class="flex items-center gap-3">
              <h3 class="text-lg font-bold text-gray-800 whitespace-nowrap">获奖情况</h3>
              <span class="text-xs font-bold text-blue-500 bg-blue-50/50 px-2.5 py-0.5 rounded-full border border-blue-100/50 whitespace-nowrap">累计获奖 {{ awards.length }} 项</span>
            </div>
          </div>
          <el-input v-model="searchAward" placeholder="搜索获奖记录..." class="flex-1 max-w-[240px] search-input" :prefix-icon="Search" />
        </div>

        <!-- 列表 -->
        <div class="space-y-4 flex-1 min-h-[300px] overflow-y-auto">
          <div v-for="award in paginatedAwards" :key="award.id" @click="showDetail(award, 'award')" class="group relative cursor-pointer p-3 rounded-xl hover:bg-white/50 border border-transparent hover:border-gray-200/40 transition-all">
            <div class="flex justify-between items-start mb-1.5">
              <div class="flex items-center gap-2">
                <h4 class="text-sm font-bold text-gray-800 group-hover:text-blue-600 transition-colors">{{ award.title }}</h4>
                <span :class="award.tagClass" class="px-1.5 py-0.5 rounded text-[10px] font-bold">{{ award.level }}</span>
              </div>
              <span class="text-[10px] text-gray-400 font-medium">{{ award.date }}</span>
            </div>
            <p class="text-xs text-gray-500 leading-relaxed truncate">{{ award.description }}</p>
          </div>
          <div v-if="filteredAwards.length === 0" class="py-16 text-center text-gray-400 text-xs font-medium">未找到相关记录</div>
        </div>

        <div class="flex-shrink-0 flex justify-end mt-4 pt-4 border-t border-gray-100/30">
          <el-pagination
            v-model:current-page="currentAwardPage"
            :page-size="awardPageSize"
            layout="prev, pager, next"
            :total="filteredAwards.length"
            small
            background
          />
        </div>
      </div>

      <!-- 评奖评优情况 -->
      <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 shadow-sm hover:bg-white/50 transition-all p-6 flex flex-col min-h-[520px]">
        <div class="flex justify-between items-center mb-6 pb-4 border-b border-gray-100/30 gap-6 flex-shrink-0">
          <div class="flex items-center gap-4 flex-shrink-0">
            <div class="w-10 h-10 rounded-xl bg-orange-50 text-orange-500 flex items-center justify-center text-xl shadow-sm flex-shrink-0">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="flex items-center gap-3">
              <h3 class="text-lg font-bold text-gray-800 whitespace-nowrap">评奖评优情况</h3>
              <span class="text-xs font-bold text-green-600 bg-green-50/50 px-2.5 py-0.5 rounded-full border border-green-100/50 whitespace-nowrap">累计评优 {{ honors.length }} 项</span>
            </div>
          </div>
          <el-input v-model="searchHonor" placeholder="搜索评优记录..." class="flex-1 max-w-[240px] search-input" :prefix-icon="Search" />
        </div>

        <!-- 列表 -->
        <div class="space-y-4 flex-1 min-h-[300px] overflow-y-auto">
          <div v-for="honor in paginatedHonors" :key="honor.id" @click="showDetail(honor, 'honor')" class="group relative cursor-pointer p-3 rounded-xl hover:bg-white/50 border border-transparent hover:border-gray-200/40 transition-all">
            <div class="flex justify-between items-start mb-1.5">
              <div class="flex items-center gap-2">
                <h4 class="text-sm font-bold text-gray-800 group-hover:text-green-600 transition-colors">{{ honor.title }}</h4>
                <span class="bg-green-50 text-green-600 px-1.5 py-0.5 rounded text-[10px] font-bold border border-green-100">{{ honor.level }}</span>
              </div>
              <span class="text-[10px] text-gray-400 font-medium">{{ honor.date }}</span>
            </div>
            <p class="text-xs text-gray-500 leading-relaxed truncate">{{ honor.description }}</p>
          </div>
          <div v-if="filteredHonors.length === 0" class="py-16 text-center text-gray-400 text-xs font-medium">未找到相关记录</div>
        </div>

        <div class="flex-shrink-0 flex justify-end mt-4 pt-4 border-t border-gray-100/30">
          <el-pagination
            v-model:current-page="currentHonorPage"
            :page-size="honorPageSize"
            layout="prev, pager, next"
            :total="filteredHonors.length"
            small
            background
          />
        </div>
      </div>
    </div>

    <!-- 第二排：违规记录情况 -->
    <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 shadow-sm hover:bg-white/50 transition-all p-6 flex flex-col">
      <div class="flex justify-between items-center mb-6 pb-4 border-b border-gray-100/30 gap-6 flex-shrink-0">
        <div class="flex items-center gap-4 flex-shrink-0">
          <div class="w-10 h-10 rounded-xl bg-red-50 text-red-500 flex items-center justify-center text-xl shadow-sm flex-shrink-0">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="flex items-center gap-3">
            <h3 class="text-lg font-bold text-gray-800 whitespace-nowrap">违规记录情况</h3>
            <span class="text-xs font-bold text-red-500 bg-red-50/50 px-2.5 py-0.5 rounded-full border border-red-100/50 whitespace-nowrap">累计违规 {{ violations.length }} 次</span>
          </div>
        </div>
        <el-input v-model="searchViolation" placeholder="搜索违规记录..." class="flex-1 max-w-[320px] search-input" :prefix-icon="Search" />
      </div>

      <!-- 表格 -->
      <div class="overflow-x-auto flex-1 min-h-[220px]">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="text-[10px] text-gray-400 uppercase tracking-wider border-b border-gray-100/50">
              <th class="pb-3 font-bold px-4">违规时间</th>
              <th class="pb-3 font-bold px-4">违规类型</th>
              <th class="pb-3 font-bold px-4">违规描述</th>
              <th class="pb-3 font-bold px-4">处理结果</th>
              <th class="pb-3 font-bold px-4">处理时间</th>
              <th class="pb-3 font-bold px-4">备注</th>
            </tr>
          </thead>
          <tbody class="text-xs">
            <tr v-for="violation in paginatedViolations" :key="violation.id" @click="showDetail(violation, 'violation')" class="hover:bg-white/40 cursor-pointer transition-colors">
              <td class="py-4 px-4 text-gray-500 font-medium">{{ violation.date }}</td>
              <td class="py-4 px-4 text-gray-800 font-bold">{{ violation.type }}</td>
              <td class="py-4 px-4 text-gray-500 leading-relaxed max-w-xs truncate">{{ violation.description }}</td>
              <td class="py-4 px-4">
                <span class="bg-green-50 text-green-600 px-2 py-1 rounded-md font-bold border border-green-100">已处理</span>
              </td>
              <td class="py-4 px-4 text-gray-500 font-medium">{{ violation.handledDate }}</td>
              <td class="py-4 px-4 text-gray-500 truncate max-w-[150px]">{{ violation.remark }}</td>
            </tr>
            <tr v-if="filteredViolations.length === 0">
              <td colspan="6" class="py-10 text-center text-gray-400 text-xs font-medium">未找到相关记录</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="flex-shrink-0 flex justify-end mt-4 pt-4 border-t border-gray-100/30">
        <el-pagination
          v-model:current-page="currentViolationPage"
          :page-size="violationPageSize"
          layout="prev, pager, next"
          :total="filteredViolations.length"
          small
          background
        />
      </div>
    </div>

    <!-- 局部暗色遮罩层，仅覆盖内容区，避免导航栏变灰 -->
    <div v-if="showDetailDialog" class="fixed top-[56px] left-[256px] right-0 bottom-0 bg-black/45 z-[1000] transition-opacity duration-300" @click="showDetailDialog = false"></div>

    <!-- 详情弹窗 (el-dialog) -->
    <el-dialog
      v-model="showDetailDialog"
      title=""
      modal-class="custom-award-overlay"
      class="custom-award-detail-dialog"
      :show-close="false"
      :modal="false"
      append-to-body
      lock-scroll
      destroy-on-close
    >
      <div v-if="selectedItem" class="flex flex-col h-full bg-white relative">
        <!-- 抽屉头部 -->
        <div class="px-6 py-5 border-b border-gray-100 flex items-center justify-between flex-shrink-0">
          <span class="text-base font-bold text-gray-900">
            {{ detailType === 'award' ? '获奖详情' : (detailType === 'honor' ? '荣誉详情' : '违规记录详情') }}
          </span>
          <button @click="showDetailDialog = false" class="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 text-gray-500 hover:text-gray-700 flex items-center justify-center transition-colors focus:outline-none">
            <el-icon :size="16"><Close /></el-icon>
          </button>
        </div>

        <!-- 滚动内容区 -->
        <div class="flex-1 overflow-y-auto px-8 py-6 space-y-5 custom-scrollbar text-sm text-gray-700 leading-relaxed bg-white">
          
          <!-- Award details -->
          <div v-if="detailType === 'award'" class="space-y-4">
            <div>
              <div class="text-gray-400 text-xs font-semibold">获奖名称：</div>
              <div class="text-gray-800 mt-1 font-bold text-base leading-snug">{{ selectedItem.title }}</div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <div class="text-gray-400 text-xs font-semibold">获奖时间：</div>
                <div class="text-gray-800 mt-1 font-medium">{{ selectedItem.date }}</div>
              </div>
              <div>
                <div class="text-gray-400 text-xs font-semibold">获奖级别：</div>
                <div class="text-gray-800 mt-1 font-medium">
                  <span class="px-2.5 py-0.5 bg-blue-50 text-blue-600 rounded-full text-xs font-semibold border border-blue-100/50">{{ selectedItem.level }}</span>
                </div>
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <div class="text-gray-400 text-xs font-semibold">获奖类别：</div>
                <div class="text-gray-800 mt-1 font-medium">
                  <span class="px-2.5 py-0.5 bg-purple-50 text-purple-600 rounded-full text-xs font-semibold border border-purple-100/50">{{ selectedItem.category }}</span>
                </div>
              </div>
              <div>
                <div class="text-gray-400 text-xs font-semibold">颁奖单位：</div>
                <div class="text-gray-800 mt-1 font-medium">{{ selectedItem.issuer || '暂无颁奖单位' }}</div>
              </div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">获奖简述：</div>
              <div class="text-gray-800 mt-1 font-medium whitespace-pre-wrap leading-relaxed">{{ selectedItem.description || '暂无描述' }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold mb-2">证明材料 (点击可放大)：</div>
              <div v-if="selectedItem.proofUrl" class="flex flex-wrap gap-2.5">
                <template v-for="url in (selectedItem.proofUrl || '').split(',')" :key="url">
                  <template v-if="url.trim()">
                    <!-- Image proof -->
                    <div v-if="isImageUrl(url.trim())" class="w-40 h-40 rounded-xl overflow-hidden border border-gray-100 bg-gray-50 flex items-center justify-center cursor-zoom-in hover:shadow-md transition-all">
                      <img :src="url.trim()" alt="证明材料" class="w-full h-full object-cover" @click="zoomedImageUrl = url.trim()">
                    </div>
                    <!-- PDF proof -->
                    <a v-else :href="url.trim()" target="_blank" class="flex items-center gap-3 p-3 border border-gray-200 rounded-xl hover:bg-gray-50 transition-colors w-full group">
                      <el-icon class="text-red-500 text-2xl"><Document /></el-icon>
                      <div class="flex-1 min-w-0">
                        <p class="text-sm font-bold text-gray-800 truncate">点击查看 PDF 证明材料</p>
                        <p class="text-xs text-gray-400">PDF 格式文件</p>
                      </div>
                      <el-icon class="text-gray-400 group-hover:text-primary"><Download /></el-icon>
                    </a>
                  </template>
                </template>
              </div>
              <div v-else class="text-gray-400 text-xs font-medium">无证明材料</div>
            </div>
            
            <!-- 审核状态 -->
            <div class="border-t border-gray-100 pt-4 mt-2">
              <div class="text-gray-400 text-xs font-semibold mb-1">审核状态：</div>
              <div class="flex items-center gap-2">
                <span class="text-xs font-bold px-2 py-0.5 rounded-full" :class="statusBadgeStyle(selectedItem.status)">{{ statusLabel(selectedItem.status) }}</span>
              </div>
            </div>
            
            <div v-if="selectedItem.reviewComment" class="p-3 bg-gray-50 rounded-xl border border-gray-100">
              <div class="text-gray-500 text-xs font-semibold">审核意见：</div>
              <div class="text-gray-700 mt-1 font-medium">{{ selectedItem.reviewComment }}</div>
            </div>
          </div>

          <!-- Honor details -->
          <div v-else-if="detailType === 'honor'" class="space-y-4">
            <div>
              <div class="text-gray-400 text-xs font-semibold">荣誉名称：</div>
              <div class="text-gray-800 mt-1 font-bold text-base leading-snug">{{ selectedItem.title }}</div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <div class="text-gray-400 text-xs font-semibold">截止日期/评选时间：</div>
                <div class="text-gray-800 mt-1 font-medium">{{ selectedItem.date }}</div>
              </div>
              <div>
                <div class="text-gray-400 text-xs font-semibold">荣誉级别：</div>
                <div class="text-gray-800 mt-1 font-medium">
                  <span class="px-2.5 py-0.5 bg-green-50 text-green-600 rounded-full text-xs font-semibold border border-green-100/50">{{ selectedItem.level }}</span>
                </div>
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <div class="text-gray-400 text-xs font-semibold">参评班级：</div>
                <div class="text-gray-800 mt-1 font-medium">{{ selectedItem.className }}</div>
              </div>
              <div>
                <div class="text-gray-400 text-xs font-semibold">学生 GPA：</div>
                <div class="text-gray-800 mt-1 font-medium font-mono text-emerald-600 font-bold">{{ selectedItem.gpa }}</div>
              </div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">入选状态：</div>
              <div class="mt-1">
                <span class="px-2.5 py-0.5 rounded-full text-xs font-bold" :class="selectedItem.status === '已入选' ? 'bg-emerald-50 text-emerald-600 border border-emerald-100' : 'bg-amber-50 text-amber-600 border border-amber-100'">
                  {{ selectedItem.status }}
                </span>
              </div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">荣誉项目说明：</div>
              <div class="text-gray-800 mt-1 font-medium whitespace-pre-wrap leading-relaxed">{{ selectedItem.projDesc || '暂无描述' }}</div>
            </div>
          </div>

          <!-- Violation details -->
          <div v-else-if="detailType === 'violation'" class="space-y-4">
            <div>
              <div class="text-gray-400 text-xs font-semibold">违规类型：</div>
              <div class="text-gray-800 mt-1 font-bold text-base leading-snug">{{ selectedItem.type }}</div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <div class="text-gray-400 text-xs font-semibold">违规时间：</div>
                <div class="text-gray-800 mt-1 font-medium">{{ selectedItem.date }}</div>
              </div>
              <div>
                <div class="text-gray-400 text-xs font-semibold">处理状态：</div>
                <div class="text-gray-800 mt-1 font-medium">
                  <span class="px-2.5 py-0.5 bg-green-50 text-green-600 rounded-full text-xs font-semibold border border-green-100">已处理</span>
                </div>
              </div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">处理时间：</div>
              <div class="text-gray-800 mt-1 font-medium">{{ selectedItem.handledDate }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">违规描述：</div>
              <div class="text-gray-800 mt-1 font-medium whitespace-pre-wrap leading-relaxed bg-red-50/20 p-3 rounded-xl border border-red-100/30 text-red-900">{{ selectedItem.description }}</div>
            </div>
            <div>
              <div class="text-gray-400 text-xs font-semibold">处理结果/备注：</div>
              <div class="text-gray-800 mt-1 font-medium whitespace-pre-wrap leading-relaxed">{{ selectedItem.remark }}</div>
            </div>
          </div>

        </div>
      </div>
    </el-dialog>

    <!-- 图片放大预览遮罩层 -->
    <teleport to="body">
      <div 
        v-if="zoomedImageUrl" 
        class="fixed top-[56px] left-0 md:left-[256px] right-0 bottom-0 bg-black/80 z-[99999] flex items-center justify-center transition-all duration-300 animate-in fade-in"
        @click="zoomedImageUrl = null"
      >
        <div class="relative max-w-[85vw] max-h-[75vh] flex items-center justify-center" @click.stop>
          <img :src="zoomedImageUrl" class="max-w-full max-h-[75vh] object-contain rounded-2xl shadow-2xl border border-white/10" />
          <button @click="zoomedImageUrl = null" class="absolute top-4 right-4 w-10 h-10 rounded-full bg-black/50 hover:bg-black/70 text-white flex items-center justify-center transition-colors">
            <el-icon :size="20"><Close /></el-icon>
          </button>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Medal, Trophy, Warning, ArrowRight, Stamp, InfoFilled, Search, Calendar, CollectionTag, Close, Download, Document } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

const API = '/api/youth'
const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'

const searchAward = ref('')
const searchHonor = ref('')
const searchViolation = ref('')

const awards = ref([])
const honors = ref([])
const projects = ref([])

const currentAwardPage = ref(1)
const awardPageSize = ref(5)

const currentHonorPage = ref(1)
const honorPageSize = ref(5)

const currentViolationPage = ref(1)
const violationPageSize = ref(5)

const showDetailDialog = ref(false)
const selectedItem = ref(null)
const detailType = ref('award') // 'award' | 'honor' | 'violation'
const zoomedImageUrl = ref(null)

const showDetail = (item, type) => {
  selectedItem.value = item
  detailType.value = type
  showDetailDialog.value = true
}

const isImageUrl = (url) => {
  if (!url) return false
  const cleanUrl = url.trim().split('?')[0]
  const ext = cleanUrl.substring(cleanUrl.lastIndexOf('.')).toLowerCase()
  return ['.jpg', '.jpeg', '.png', '.gif', '.webp'].some(x => ext === x)
}

const statusLabel = (s) => ({
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已驳回'
}[s] || s)

const statusBadgeStyle = (s) => ({
  PENDING: 'bg-blue-50 text-blue-600',
  APPROVED: 'bg-green-50 text-green-700 border border-green-100/50',
  REJECTED: 'bg-red-50 text-red-600 border border-red-100/50'
}[s] || 'bg-gray-100 text-gray-500')

watch(searchAward, () => { currentAwardPage.value = 1 })
watch(searchHonor, () => { currentHonorPage.value = 1 })
watch(searchViolation, () => { currentViolationPage.value = 1 })

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

const levelIconColor = (level) => {
  const m = {
    '国家级': 'text-red-400',
    '省级': 'text-orange-400',
    '市级': 'text-yellow-400',
    '校级': 'text-blue-400',
    '院级': 'text-green-400'
  }
  return m[level] || 'text-gray-400'
}

const loadData = async () => {
  try {
    const [aRes, pRes] = await Promise.all([
      request.get(`${API}/awards?studentId=${STUDENT_ID}`),
      request.get(`${API}/honors/projects`)
    ])
    if (pRes.data.code === 200) projects.value = pRes.data.data
    if (aRes.data.code === 200) {
      awards.value = aRes.data.data.map(a => ({
        id: a.id,
        title: a.awardName,
        level: a.level,
        date: a.awardTime,
        description: a.description || '',
        tagClass: levelTagClass(a.level),
        iconColor: levelIconColor(a.level),
        category: a.category || '学科竞赛',
        issuer: a.issuer || '',
        proofUrl: a.proofUrl || '',
        status: a.status || 'APPROVED',
        reviewComment: a.reviewComment || ''
      }))
    }
  } catch (e) { console.error(e) }

  try {
    const hRes = await request.get(`${API}/honors/candidates/by-student?studentId=${STUDENT_ID}`)
    if (hRes.data.code === 200) {
      const projectMap = {}
      projects.value.forEach(p => { projectMap[p.id] = p })
      honors.value = hRes.data.data.map(h => {
        const proj = projectMap[h.projectId] || {}
        return {
          id: h.id,
          title: proj.name || '评优项目',
          level: '校级',
          date: proj.deadline || '',
          description: `GPA ${h.gpa || '--'} | ${h.className || ''}`,
          gpa: h.gpa,
          className: h.className,
          status: h.status || '已入选',
          projDesc: proj.description || ''
        }
      })
    }
  } catch (e) { console.error(e) }
}

const violations = ref([
  {
    id: 1,
    date: '2024-03-15',
    type: '课堂纪律',
    description: '上课迟到\n第3-4节《高等数学》课程迟到',
    handledDate: '2024-03-16',
    remark: '给予口头批评教育'
  },
  {
    id: 2,
    date: '2023-11-28',
    type: '宿舍管理',
    description: '未按规定使用电器\n在宿舍使用大功率电器',
    handledDate: '2023-11-29',
    remark: '给予警告处分'
  }
])

const filteredAwards = computed(() => {
  return awards.value.filter(a =>
    a.title.includes(searchAward.value) ||
    (a.description || '').includes(searchAward.value) ||
    a.level.includes(searchAward.value)
  )
})

const paginatedAwards = computed(() => {
  const start = (currentAwardPage.value - 1) * awardPageSize.value
  return filteredAwards.value.slice(start, start + awardPageSize.value)
})

const filteredHonors = computed(() => {
  return honors.value.filter(h =>
    h.title.includes(searchHonor.value) ||
    (h.description || '').includes(searchHonor.value) ||
    h.level.includes(searchHonor.value)
  )
})

const paginatedHonors = computed(() => {
  const start = (currentHonorPage.value - 1) * honorPageSize.value
  return filteredHonors.value.slice(start, start + honorPageSize.value)
})

const filteredViolations = computed(() => {
  return violations.value.filter(v =>
    v.type.includes(searchViolation.value) ||
    v.description.includes(searchViolation.value) ||
    v.remark.includes(searchViolation.value)
  )
})

const paginatedViolations = computed(() => {
  const start = (currentViolationPage.value - 1) * violationPageSize.value
  return filteredViolations.value.slice(start, start + violationPageSize.value)
})

onMounted(loadData)
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

table th {
  white-space: nowrap;
}

:deep(.search-input .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.4) !important;
  box-shadow: none !important;
  border: 1px solid rgba(180, 180, 180, 0.4) !important;
  border-radius: 16px !important; /* macOS-style rounded-2xl */
  transition: all 0.3s ease;
}

:deep(.search-input .el-input__wrapper.is-focus) {
  background-color: rgba(255, 255, 255, 0.8) !important;
  border-color: #409eff !important;
  box-shadow: 0 0 0 1px #409eff !important;
}

/* 隐藏滚动条 */
.overflow-x-auto::-webkit-scrollbar {
  height: 4px;
}
.overflow-x-auto::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.05);
  border-radius: 10px;
}
</style>

<style>
/* 全局样式：用于被 Element Plus 渲染并挂载到 body 下的荣誉/获奖详情对话框 */
.custom-award-overlay {
  background-color: transparent !important;
  z-index: 1001 !important; /* 保证其低于顶层状态栏 z-[9999]，避免覆盖状态栏 */
}

.custom-award-detail-dialog {
  position: fixed !important;
  top: 96px !important;
  bottom: 40px !important;
  width: 520px !important;
  margin: 0 !important;
  padding: 0 !important;
  display: flex;
  flex-direction: column;
  border-radius: 32px !important; /* 看板圆角处理 */
  overflow: hidden !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;
  overscroll-behavior: contain;
  z-index: 1001 !important;
}

@media (min-width: 768px) {
  .custom-award-detail-dialog {
    left: calc(50% + 128px) !important; /* 除去左边 256px 导航栏后，在剩下的页面中左右居中 */
    transform: translateX(-50%) !important;
    right: auto !important;
  }
}
@media (max-width: 767px) {
  .custom-award-detail-dialog {
    left: 50% !important;
    transform: translateX(-50%) !important;
    right: auto !important;
    width: calc(100vw - 32px) !important;
    margin: 0 auto !important;
  }
}
.custom-award-detail-dialog .el-dialog__header {
  padding: 0 !important;
  margin: 0 !important;
  height: 0;
}
.custom-award-detail-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  padding: 0 !important;
}
.custom-award-detail-dialog .el-dialog__headerbtn {
  display: none !important; /* 隐藏默认关闭按钮 */
}
</style>
