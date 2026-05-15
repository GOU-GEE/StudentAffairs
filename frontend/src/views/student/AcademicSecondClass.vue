<template>
  <div class="h-full flex flex-col space-y-5 pb-8 relative z-10">
    <!-- Top Row -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-5">
      <!-- GPA Card with Warning integrated -->
      <div class="lg:col-span-4 bg-gradient-to-br from-[#407BFF] to-[#2B5CE6] rounded-2xl p-5 text-white shadow-lg relative overflow-hidden flex flex-col">
        <div class="absolute right-0 top-0 opacity-10 pointer-events-none">
          <svg class="w-32 h-32" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2L2 22h20L12 2zm0 4.5l6.5 13h-13L12 6.5z"/></svg>
        </div>
        
        <div class="relative z-10 flex-1">
          <div class="flex justify-between items-center mb-2">
            <span class="text-white/80 text-sm font-medium">当前 GPA</span>
            <span class="text-white/80 text-xs flex items-center cursor-pointer hover:text-white transition-colors">本学期 <el-icon class="ml-1"><ArrowRight /></el-icon></span>
          </div>
          <div class="flex items-end gap-3 mb-4">
            <span class="text-[3.25rem] font-bold leading-none tracking-tight">3.85</span>
            <div class="flex items-center text-xs bg-white/20 px-2 py-1 rounded-md backdrop-blur-sm mb-1">
              <el-icon class="mr-1 text-green-300"><Top /></el-icon>
              <span class="text-green-100 font-semibold">0.15</span>
              <span class="ml-1 text-white/90">较上学期</span>
            </div>
          </div>
          <div class="grid grid-cols-2 gap-4 text-sm mt-2 mb-6">
            <div>
              <div class="text-white/70 mb-1.5 text-xs">专业排名 <span class="text-white font-semibold text-sm ml-1">12 / 150</span></div>
              <div class="text-white/70 text-xs">已修学分 <span class="text-white font-semibold text-sm ml-1">86 学分</span></div>
            </div>
            <div>
               <div class="text-green-300 mb-1.5 text-xs flex items-center"><el-icon class="mr-0.5"><Top /></el-icon> 2 名</div>
               <div class="text-white/70 text-xs">培养方案完成度 <span class="text-[#FFD666] font-semibold text-sm ml-1">68%</span></div>
            </div>
          </div>

          <!-- Academic Warning Integrated -->
          <div class="mt-auto pt-4 border-t border-white/20">
            <div class="flex items-center gap-3">
              <div class="bg-white/20 p-2 rounded-xl backdrop-blur-sm">
                <el-icon class="text-2xl text-green-300"><CircleCheckFilled /></el-icon>
              </div>
              <div>
                <div class="text-sm font-bold text-white">学业预警：良好</div>
                <div class="text-white/70 text-xs mt-0.5">暂无风险，请继续保持！</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Radar Chart -->
      <div class="lg:col-span-3 bg-white/80 backdrop-blur-md rounded-2xl p-5 shadow-sm border border-gray-100 flex flex-col">
        <div class="flex justify-between items-center mb-2">
          <h3 class="text-base font-bold text-gray-800">学科能力雷达图</h3>
          <el-button size="small" class="!rounded-full text-xs" plain>详情</el-button>
        </div>
        <div class="flex-1 w-full" ref="radarChartRef"></div>
      </div>

      <!-- Trend Analysis (Moved from below) -->
      <div class="lg:col-span-5 bg-white/80 backdrop-blur-md rounded-2xl p-5 shadow-sm border border-gray-100 flex flex-col h-fit">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-base font-bold text-gray-800">学业趋势分析</h3>
          <el-select v-model="trendType" size="small" class="!w-24">
            <el-option label="GPA 趋势" value="gpa"></el-option>
            <el-option label="学分趋势" value="credits"></el-option>
            <el-option label="绩点排名" value="rank"></el-option>
            <el-option label="综测排名" value="overall"></el-option>
          </el-select>
        </div>
        <div class="h-48 w-full" ref="trendChartRef"></div>
      </div>
    </div>

    <!-- Bottom Row: Course Grades (Left) & Second Classroom (Right) -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-5">
      <!-- Left: Course Grade Details -->
      <div class="lg:col-span-6 bg-white/80 backdrop-blur-md rounded-2xl p-5 shadow-sm border border-gray-100 flex flex-col h-full">
        <h3 class="text-base font-bold text-gray-800 mb-4">课程成绩明细</h3>
        <el-table :data="tableData" style="width: 100%" class="flex-1" :header-cell-style="{background:'#F9FAFB', color:'#4B5563', fontWeight:'normal'}" :row-style="{height:'48px'}">
          <el-table-column prop="name" label="课程名称" show-overflow-tooltip>
             <template #default="scope">
                <span class="font-medium text-gray-800">{{ scope.row.name }}</span>
             </template>
          </el-table-column>
          <el-table-column prop="type" label="类别" width="90"></el-table-column>
          <el-table-column prop="score" label="成绩" width="70" align="center">
            <template #default="scope">
                <span class="font-semibold text-gray-800">{{ scope.row.score }}</span>
             </template>
          </el-table-column>
          <el-table-column prop="gpa" label="绩点" width="70" align="center"></el-table-column>
          <el-table-column prop="credits" label="学分" width="70" align="center"></el-table-column>
          <el-table-column prop="semester" label="开课学期" width="120" align="center"></el-table-column>
        </el-table>
        <div class="flex justify-between items-center mt-auto pt-4 border-t border-gray-100">
          <span class="text-sm text-gray-500">共 24 条</span>
          <el-pagination background layout="prev, pager, next" :total="24" :page-size="10" size="small" />
        </div>
      </div>

      <!-- Right: Second Classroom Stats & Archive -->
      <div class="lg:col-span-6 flex flex-col gap-5">
        <!-- Second Classroom Stats -->
        <div class="bg-white/80 backdrop-blur-md rounded-2xl p-5 shadow-sm border border-gray-100 flex flex-col">
          <div class="flex justify-between items-center mb-2">
            <h3 class="text-base font-bold text-gray-800">第二课堂统计</h3>
            <el-button size="small" class="!rounded-full text-xs" plain>详情</el-button>
          </div>
          <div class="flex-1 flex items-center h-40">
             <div class="w-1/2 h-full" ref="secondClassChartRef"></div>
             <div class="w-1/2 flex flex-col gap-3.5 pl-2">
                <div class="flex items-center justify-between text-sm">
                  <div class="flex items-center gap-2.5"><div class="w-2.5 h-2.5 rounded-sm bg-[#9E86FF]"></div><span class="text-gray-600">活动参与</span></div>
                  <span class="font-bold text-gray-800">18 <span class="text-xs font-normal text-gray-500">次</span></span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <div class="flex items-center gap-2.5"><div class="w-2.5 h-2.5 rounded-sm bg-[#52C41A]"></div><span class="text-gray-600">志愿服务</span></div>
                  <span class="font-bold text-gray-800">24 <span class="text-xs font-normal text-gray-500">小时</span></span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <div class="flex items-center gap-2.5"><div class="w-2.5 h-2.5 rounded-sm bg-[#FAAD14]"></div><span class="text-gray-600">社团活动</span></div>
                  <span class="font-bold text-gray-800">6 <span class="text-xs font-normal text-gray-500">次</span></span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <div class="flex items-center gap-2.5"><div class="w-2.5 h-2.5 rounded-sm bg-[#1890FF]"></div><span class="text-gray-600">技能培训</span></div>
                  <span class="font-bold text-gray-800">8 <span class="text-xs font-normal text-gray-500">小时</span></span>
                </div>
             </div>
          </div>
        </div>

        <!-- Second Classroom Archive (List UI) -->
        <div class="bg-white/90 backdrop-blur-md rounded-2xl p-6 shadow-sm border border-gray-100 flex flex-col flex-1">
          <div class="flex justify-between items-center mb-6">
            <div class="flex items-center gap-2.5">
              <div class="bg-blue-50 p-1.5 rounded-lg text-blue-500">
                <el-icon class="text-lg"><Reading /></el-icon>
              </div>
              <h3 class="text-base font-bold text-gray-800">学时分类完成情况</h3>
            </div>
            <div class="text-sm text-gray-500 flex items-center gap-4">
              <span>总计学时 <span class="font-bold text-gray-800 ml-1">116 / 144</span></span>
              <span>总体完成率 <span class="text-blue-600 font-bold text-lg ml-1">68%</span></span>
            </div>
          </div>

          <div class="flex text-xs text-gray-500 mb-3 px-2 font-medium">
            <div class="flex-1">学时类型</div>
            <div class="w-28 text-center">已获得 / 要求</div>
            <div class="w-16 text-center">完成率</div>
            <div class="w-20 text-center">状态</div>
          </div>

          <div class="flex flex-col gap-3 flex-1">
            <div v-for="item in archiveList" :key="item.name" class="flex items-center text-sm px-2 py-1.5 hover:bg-gray-50/50 rounded-lg transition-colors">
              <div class="flex-1 flex items-center gap-3">
                <span class="font-bold text-gray-800 w-20">{{ item.name }}</span>
                <div class="flex-1 max-w-[140px] h-2 bg-gray-100 rounded-full overflow-hidden mx-2">
                  <div class="h-full rounded-full bg-blue-400" :style="{ width: item.progress > 100 ? 100 + '%' : item.progress + '%' }"></div>
                </div>
              </div>
              <div class="w-28 text-center text-gray-700 font-semibold text-xs">{{ item.actual }} / {{ item.req }} 学时</div>
              <div class="w-16 text-center font-bold text-gray-800 text-xs">{{ item.progress }}%</div>
              <div class="w-20 flex justify-center">
                <span class="px-2.5 py-1 rounded text-xs font-bold" :class="item.statusClass">{{ item.status }}</span>
              </div>
            </div>
          </div>

          <div class="mt-5 bg-gray-50/80 rounded-xl p-3 flex items-center justify-center gap-2 text-xs font-medium text-gray-500 border border-gray-100">
            <el-icon class="text-sm text-gray-400"><DataLine /></el-icon>
            还需完成 12 学时即可达成培养方案要求，继续加油！
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ArrowRight, Top, DataLine, CircleCheckFilled, Trophy, Service, User, Opportunity, Monitor, Star, Reading, StarFilled, Basketball, Cpu, EditPen, Connection, Guide, Tools, Flag, Headset, Medal, Goods } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const radarChartRef = ref(null)
const secondClassChartRef = ref(null)
const trendChartRef = ref(null)
const distributionChartRef = ref(null)

let charts = []

const trendType = ref('gpa')

const archiveList = [
  { name: '思想素质', req: '32', actual: '32', progress: 100, iconBg: 'bg-blue-50', iconColor: 'text-blue-500', barColor: 'bg-blue-500', icon: 'Flag', status: '已达标', statusClass: 'bg-green-100 text-green-600' },
  { name: '文艺体育', req: '32', actual: '24', progress: 75, iconBg: 'bg-pink-50', iconColor: 'text-pink-500', barColor: 'bg-pink-500', icon: 'Headset', status: '进行中', statusClass: 'bg-blue-100 text-blue-600' },
  { name: '创新创造', req: '16', actual: '12', progress: 75, iconBg: 'bg-purple-50', iconColor: 'text-purple-500', barColor: 'bg-purple-500', icon: 'Opportunity', status: '进行中', statusClass: 'bg-blue-100 text-blue-600' },
  { name: '志愿服务', req: '16', actual: '24', progress: 150, iconBg: 'bg-orange-50', iconColor: 'text-orange-500', barColor: 'bg-orange-500', icon: 'Medal', status: '已达标', statusClass: 'bg-green-100 text-green-600' },
  { name: '劳动教育', req: '32', actual: '16', progress: 50, iconBg: 'bg-green-50', iconColor: 'text-green-500', barColor: 'bg-green-500', icon: 'Goods', status: '进行中', statusClass: 'bg-blue-100 text-blue-600' },
  { name: '技能特长', req: '16', actual: '8', progress: 50, iconBg: 'bg-yellow-50', iconColor: 'text-yellow-500', barColor: 'bg-yellow-500', icon: 'Star', status: '待加强', statusClass: 'bg-orange-100 text-orange-600' }
]

const badgeList = [
  { name: 'ACM成员', icon: 'Monitor', gradient: 'bg-gradient-to-br from-indigo-400 to-indigo-600' },
  { name: '志愿先锋', icon: 'Star', gradient: 'bg-gradient-to-br from-red-400 to-red-600' },
  { name: '学习标兵', icon: 'Reading', gradient: 'bg-gradient-to-br from-blue-400 to-blue-600' },
  { name: '优秀学生', icon: 'StarFilled', gradient: 'bg-gradient-to-br from-yellow-400 to-yellow-600' },
  { name: '篮球队员', icon: 'Basketball', gradient: 'bg-gradient-to-br from-green-400 to-green-600' }
]

const tableData = ref([
  { name: '数据结构', type: '专业必修', score: 95, gpa: 4.0, credits: 4, semester: '2024-2025-2', status: '优秀' },
  { name: '操作系统', type: '专业必修', score: 92, gpa: 4.0, credits: 4, semester: '2024-2025-2', status: '优秀' },
  { name: '计算机网络', type: '专业必修', score: 88, gpa: 3.7, credits: 3, semester: '2024-2025-2', status: '良好' },
  { name: '高等数学（下）', type: '公共基础', score: 85, gpa: 3.3, credits: 5, semester: '2024-2025-2', status: '良好' },
  { name: '英语（四）', type: '公共基础', score: 79, gpa: 2.7, credits: 3, semester: '2024-2025-2', status: '中等' }
])

const initCharts = () => {
  // 1. Radar Chart
  if (radarChartRef.value) {
    const radarChart = echarts.init(radarChartRef.value)
    radarChart.setOption({
      grid: { top: 10, bottom: 10 },
      radar: {
        indicator: [
          { name: '数学能力', max: 100 },
          { name: '编程能力', max: 100 },
          { name: '英语能力', max: 100 },
          { name: '实践能力', max: 100 },
          { name: '创新能力', max: 100 },
          { name: '团队协作', max: 100 }
        ],
        radius: '65%',
        axisName: { color: '#4B5563', fontSize: 11 },
        splitLine: { lineStyle: { color: '#E5E7EB' } },
        splitArea: { show: false },
        axisLine: { lineStyle: { color: '#E5E7EB' } }
      },
      series: [{
        type: 'radar',
        data: [{
          value: [85, 90, 78, 82, 82, 80],
          name: '能力值',
          areaStyle: { color: 'rgba(24, 144, 255, 0.15)' },
          lineStyle: { color: '#1890FF', width: 2 },
          itemStyle: { color: '#1890FF' },
          label: {
            show: true,
            color: '#1890FF',
            fontSize: 10,
            formatter: (params) => params.value
          }
        }]
      }]
    })
    charts.push(radarChart)
  }

  // 2. Second Classroom Donut
  if (secondClassChartRef.value) {
    const donutChart = echarts.init(secondClassChartRef.value)
    donutChart.setOption({
      series: [{
        type: 'pie',
        radius: ['60%', '80%'],
        center: ['50%', '50%'],
        avoidLabelOverlap: false,
        label: { show: false },
        data: [
          { value: 18, name: '活动参与', itemStyle: { color: '#9E86FF' } },
          { value: 24, name: '志愿服务', itemStyle: { color: '#52C41A' } },
          { value: 6, name: '社团活动', itemStyle: { color: '#FAAD14' } },
          { value: 8, name: '技能培训', itemStyle: { color: '#1890FF' } },
          { value: 4, name: '剩余目标', itemStyle: { color: '#F3F4F6' } }
        ]
      }],
      graphic: [{
        type: 'text',
        left: 'center',
        top: 'center',
        style: {
          text: '{val|32}\n{lbl|总学时}',
          textAlign: 'center',
          rich: {
            val: { fontSize: 28, fontWeight: 'bold', color: '#1F2937', lineHeight: 32 },
            lbl: { fontSize: 12, color: '#6B7280', lineHeight: 16 }
          }
        }
      }]
    })
    charts.push(donutChart)
  }

  // 3. Trend Line Chart
  if (trendChartRef.value) {
    const trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      grid: { left: 30, right: 30, top: 30, bottom: 25 },
      xAxis: {
        type: 'category',
        data: ['大一上', '大一下', '大二上', '大二下(本学期)'],
        axisLine: { lineStyle: { color: '#E5E7EB' } },
        axisLabel: { color: '#6B7280', fontSize: 12 },
        axisTick: { show: false }
      },
      yAxis: {
        type: 'value',
        min: 0,
        max: 4.0,
        splitLine: { lineStyle: { type: 'dashed', color: '#F3F4F6' } },
        axisLabel: { color: '#6B7280' }
      },
      series: [{
        data: [3.21, 3.42, 3.62, 3.85],
        type: 'line',
        smooth: false,
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: { color: '#1890FF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(24,144,255,0.2)' },
            { offset: 1, color: 'rgba(24,144,255,0)' }
          ])
        },
        label: {
          show: true,
          position: 'top',
          color: '#1F2937',
          fontWeight: 'bold'
        }
      }],
      tooltip: {
        trigger: 'axis',
        formatter: '{b}: {c}'
      }
    })
    charts.push(trendChart)
  }
}

const handleResize = () => {
  charts.forEach(chart => chart.resize())
}

onMounted(() => {
  nextTick(() => {
    initCharts()
    window.addEventListener('resize', handleResize)
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(chart => chart.dispose())
})
</script>

