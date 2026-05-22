<template>
  <div class="space-y-6 min-w-[900px] pb-10">
    <!-- 顶部 Banner 卡片 -->
    <div class="relative w-full h-[220px] rounded-3xl overflow-hidden shadow-sm flex items-stretch">
      <!-- 右侧活动照片 -->
      <div class="absolute inset-0 w-full h-full">
        <!-- 使用了一张校园活动的图片 -->
        <img src="https://images.unsplash.com/photo-1523580494863-6f3031224c94?q=80&w=2070&auto=format&fit=crop" class="w-full h-full object-cover object-right" alt="Event Banner">
      </div>
      <!-- 左侧背景与模糊过渡：汲取图片偏蓝的颜色 -->
      <div class="absolute inset-0 bg-gradient-to-r from-[#1A365D] via-[#1A365D]/85 to-transparent"></div>
      
      <!-- 卡片内容 -->
      <div class="relative z-10 flex flex-col justify-start pt-6 pl-6 md:pt-7 md:pl-7 text-white w-full md:w-2/3 h-full">
        <h2 class="text-[1.8rem] font-bold mb-3 tracking-tight">第十七届电脑文化艺术节</h2>
        <p class="text-[0.85rem] text-blue-100/90 mb-4 max-w-lg leading-relaxed">
          感受科技魅力，展现创新风采。包含编程挑战赛、电竞友谊赛及前沿技术讲座等系列活动，期待你的参与！
        </p>
        
        <div class="flex items-center gap-6 text-sm text-blue-50/90 font-medium">
          <div class="flex items-center gap-2"><el-icon><Clock /></el-icon> 2026-06-01 至 2026-06-05</div>
          <div class="flex items-center gap-2"><el-icon><Location /></el-icon> 综合体育馆及各实验室</div>
        </div>
      </div>
    </div>

    <!-- 底部主内容：左栏活动列表，右栏日历与我的活动 -->
    <div id="activity-grid" class="grid grid-cols-1 xl:grid-cols-12 gap-6 items-stretch relative">
      <!-- 左侧内容 -->
      <div class="xl:col-span-8 flex flex-col bg-white rounded-3xl px-6 pt-4 pb-6 border border-outline-variant/20 shadow-sm relative z-20">
        <!-- 导航 Tabs 与 搜索栏 -->
        <div class="flex items-center justify-between border-b border-outline-variant/20 pb-3 flex-shrink-0">
          <div class="flex items-center gap-2">
            <button v-for="tab in tabs" :key="tab.value" 
              @click="activeTab = tab.value"
              class="px-3 py-2 text-sm font-semibold transition-colors rounded-lg"
              :class="activeTab === tab.value ? 'bg-surface-container-high text-on-surface' : 'text-secondary hover:bg-surface-container-low hover:text-on-surface'">
              {{ tab.label }}
            </button>
          </div>
          <!-- 搜索与筛选 -->
          <div class="flex items-center gap-4">
            <div class="relative w-64 flex items-center">
              <span class="absolute left-3 flex items-center text-outline"><el-icon><Search /></el-icon></span>
              <input v-model="searchQuery" @keyup.enter="$event.target.blur()" type="text" placeholder="搜索活动名称、关键字..." class="w-full pl-9 pr-9 py-2 bg-surface-container-lowest border border-outline-variant/30 rounded-xl text-xs font-medium focus:outline-none focus:border-primary transition-all">
              <button class="absolute right-2 flex items-center justify-center p-1 text-secondary hover:text-primary transition-colors"><el-icon><Filter /></el-icon></button>
            </div>
          </div>
        </div>

        <!-- 活动列表 (锁定高度刚好展示5条，移除横/纵滚动条，纯靠分页) -->
        <div class="flex flex-col gap-4 mt-4 h-[694px] overflow-hidden flex-shrink-0 pr-2">
          <div v-for="activity in paginatedEvents" :key="activity.id" 
               @click="selectActivity(activity, $event)"
               class="rounded-2xl p-3 border border-outline-variant/20 hover:shadow-md transition-shadow flex gap-4 cursor-pointer bg-surface-container-lowest relative"
               :class="selectedActivity?.id === activity.id ? 'border-primary shadow-md' : 'hover:border-primary/30 group/card'">
            
            <!-- 左侧图片 -->
            <div class="w-[100px] h-[100px] rounded-xl overflow-hidden flex-shrink-0 relative group bg-surface-container-low">
              <img v-if="activity.image && !imageErrors.has(activity.id)" 
                   :src="activity.image" 
                   @error="handleImageError(activity.id)" 
                   class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" 
                   :alt="activity.title">
              <div v-else 
                   class="w-full h-full bg-gradient-to-br from-blue-500 to-indigo-600 text-white flex items-center justify-center font-bold text-xs text-center p-2 leading-tight select-none">
                {{ activity.title ? activity.title.substring(0, 4) : '' }}
              </div>
              <div class="absolute top-1.5 left-1.5 px-1.5 py-0.5 rounded text-[10px] font-bold text-white shadow-sm" :class="getActivityState(activity).bg">
                {{ getActivityState(activity).label }}
              </div>
            </div>
            
            <!-- 中间内容 -->
            <div class="flex-1 flex flex-col justify-between py-0.5 min-w-0">
              <div>
                <div class="flex items-center gap-3 mb-1">
                  <h3 class="text-base font-bold text-on-surface tracking-tight truncate">{{ activity.title }}</h3>
                </div>
                <p class="text-xs text-secondary leading-relaxed line-clamp-1 mb-2">
                  {{ activity.desc }}
                </p>
                <div class="flex flex-wrap items-center gap-x-5 gap-y-2 text-xs text-secondary font-medium">
                  <span class="flex items-center gap-1.5 whitespace-nowrap"><el-icon><Clock /></el-icon> {{ activity.time }}</span>
                  <span class="flex items-center gap-1.5 whitespace-nowrap"><el-icon><Location /></el-icon> {{ activity.location || activity.tags.find(t => t.includes('活动')) }}</span>
                  <span class="flex items-center gap-1.5 whitespace-nowrap text-orange-600/80" v-if="activity.status === 'enrolling' || activity.status === 'waiting'">{{ activity.deadlineText }}: {{ activity.deadline }}</span>
                </div>
              </div>
            </div>
            
            <!-- 右侧操作 -->
            <div class="flex flex-col items-center justify-center border-l border-outline-variant/15 pl-5 min-w-[100px] flex-shrink-0">
              <span class="text-[11px] font-bold text-secondary mb-1">{{ activity.creditType || '活动学时' }}</span>
              <span class="text-[1.35rem] font-black leading-none mb-2" :class="getActivityState(activity).textColor">{{ activity.hours }} <span class="text-[10px] font-bold">学时</span></span>
              <button @click.stop="handleActivityAction(activity)" class="w-full py-1.5 rounded-lg text-xs font-bold transition-all shadow-sm"
                :class="getActivityState(activity).btnClass"
                :disabled="getActivityState(activity).disabled">
                {{ getActivityState(activity).btnText }}
              </button>
            </div>
          </div>
        </div>

        <!-- 底部高保真统计及分页底栏 -->
        <div class="flex items-center justify-between border-t border-outline-variant/15 pt-4 mt-auto flex-shrink-0">
          <!-- 左下角：统计数量 -->
          <div class="text-xs font-semibold text-secondary flex items-center gap-1.5">
            <span class="inline-block w-1.5 h-1.5 rounded-full bg-blue-500"></span>
            共 {{ filteredEvents.length }} 条活动
          </div>
          
          <!-- 右下角：页数按钮 -->
          <div class="flex items-center gap-1">
            <button 
              @click="currentPage > 1 && currentPage--" 
              :disabled="currentPage === 1"
              class="w-8 h-8 rounded-lg border border-outline-variant/20 flex items-center justify-center text-secondary hover:text-primary hover:border-primary/30 transition-all disabled:opacity-40 disabled:pointer-events-none"
            >
              <el-icon><ArrowLeft /></el-icon>
            </button>
            
            <button 
              v-for="p in totalPages" 
              :key="p" 
              @click="currentPage = p"
              class="w-8 h-8 rounded-lg font-bold text-xs transition-all"
              :class="currentPage === p ? 'bg-blue-600 text-white shadow-sm' : 'border border-outline-variant/20 text-secondary hover:text-primary hover:border-primary/30 bg-transparent'"
            >
              {{ p }}
            </button>
            
            <button 
              @click="currentPage < totalPages && currentPage++" 
              :disabled="currentPage === totalPages"
              class="w-8 h-8 rounded-lg border border-outline-variant/20 flex items-center justify-center text-secondary hover:text-primary hover:border-primary/30 transition-all disabled:opacity-40 disabled:pointer-events-none"
            >
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧区域 (固定高度与左侧对齐，带过渡动画) -->
      <div class="xl:col-span-4 relative h-full">
        <transition name="fade-slide" mode="out-in">
          <!-- 默认：活动日历与我的活动 -->
          <div v-if="!selectedActivity" class="flex flex-col gap-6 h-full">
            <!-- 活动日历 -->
            <div class="bg-white rounded-3xl p-6 border border-outline-variant/20 shadow-sm hover:shadow-md transition-shadow relative">
              <!-- 放大按钮：支持点击放大 -->
              <div @click="showCalendarDialog = true" class="absolute top-6 right-6 text-xs text-secondary flex items-center gap-1 opacity-60 hover:opacity-100 transition-opacity cursor-pointer z-10">
                <el-icon><ZoomIn /></el-icon> 放大
              </div>
              <h3 class="text-base font-bold text-on-surface mb-4">活动日历</h3>
              <!-- 简单日历头部 -->
              <div class="flex items-center justify-between mb-4">
                <button @click="prevMonth" class="text-secondary hover:text-primary"><el-icon><ArrowLeft /></el-icon></button>
                <span class="text-sm font-bold">{{ calendarMonthLabel }}</span>
                <button @click="nextMonth" class="text-secondary hover:text-primary"><el-icon><ArrowRight /></el-icon></button>
              </div>
              <!-- 日历星期 -->
              <div class="grid grid-cols-7 text-center mb-2">
                <span v-for="day in ['日','一','二','三','四','五','六']" :key="day" class="text-xs font-semibold text-outline">{{ day }}</span>
              </div>
              <!-- 日历天数 (动态计算) -->
              <div class="grid grid-cols-7 text-center gap-y-2 gap-x-1" style="grid-template-rows: repeat(5, minmax(0, 1fr));">
                <div v-for="d in 35" :key="d" @click="showCalendarDialog = true" class="relative flex flex-col items-center justify-start py-1 rounded-xl transition-colors h-[52px] cursor-pointer"
                  :class="getCalendarDayBg(d)">
                  <span class="text-xs font-semibold flex items-center justify-center w-6 h-6 rounded-full transition-colors z-10"
                    :class="getCalendarDayClass(d)">
                    {{ getCalendarDayText(d) }}
                  </span>
                  <!-- 活动数量 -->
                  <span v-if="getCalendarActivityCount(d) > 0" class="text-[10px] font-bold mt-0.5 z-10 text-outline">
                    {{ getCalendarActivityCount(d) }}项
                  </span>
                </div>
              </div>
              
              <!-- 图例 -->
              <div class="flex items-center justify-between mt-5 pt-4 border-t border-outline-variant/15 px-1">
                <div class="flex items-center gap-1.5"><span class="w-1.5 h-1.5 rounded-full bg-blue-600"></span><span class="text-[10px] text-secondary font-medium">当前日</span></div>
                <div class="flex items-center gap-1.5"><span class="w-2 h-2 rounded bg-yellow-100 border border-yellow-200"></span><span class="text-[10px] text-secondary font-medium">即将举办</span></div>
                <div class="flex items-center gap-1.5"><span class="w-2 h-2 rounded bg-surface-container border border-outline-variant/20"></span><span class="text-[10px] text-secondary font-medium">已举办</span></div>
              </div>
            </div>

            <!-- 我的活动 (自动填满高度并对齐) -->
            <div class="bg-white rounded-3xl p-6 border border-outline-variant/20 shadow-sm flex-1 flex flex-col min-h-0 cursor-pointer hover:shadow-md transition-shadow group/history" @click="showMyActivitiesDialog = true">
              <div class="flex items-center justify-between mb-5 flex-shrink-0">
                <h3 class="text-base font-bold text-on-surface">我的活动</h3>
                <button class="text-xs font-semibold text-secondary group-hover/history:text-primary transition-colors">全部 <el-icon class="align-middle"><ArrowRight /></el-icon></button>
              </div>
              
              <!-- 已报名的活动列表 -->
              <div class="flex-1 overflow-y-auto custom-scrollbar flex flex-col gap-1 pr-1 max-h-[220px]">
                <div v-for="myEvent in myEvents.slice(0, 5)" :key="myEvent.id" class="flex justify-between items-center py-3 border-b border-outline-variant/10 last:border-0 group cursor-pointer">
                  <span class="text-sm font-bold text-on-surface group-hover:text-primary transition-colors truncate pr-4">{{ myEvent.title }}</span>
                  <span class="text-xs font-semibold text-secondary flex-shrink-0">{{ myEvent.date }}</span>
                </div>
                <div v-if="myEvents.length === 0" class="py-8 text-center text-xs text-outline">
                  暂无已报名的活动
                </div>
              </div>
            </div>
          </div>
          <!-- 右侧内容结束 -->

          <!-- 活动详情 -->
          <div v-else class="bg-white rounded-3xl border border-outline-variant/20 shadow-sm overflow-hidden flex flex-col h-full relative">
            <!-- 头部操作区 (含关闭按钮) -->
            <div class="absolute top-4 right-4 z-20 flex gap-2">
              <button @click="shareActivity" class="w-8 h-8 flex items-center justify-center rounded-full bg-white/20 backdrop-blur-md text-white hover:bg-white/40 transition-colors shadow-sm">
                <el-icon><Share /></el-icon>
              </button>
              <button @click="closeDetail" class="w-8 h-8 flex items-center justify-center rounded-full bg-white/20 backdrop-blur-md text-white hover:bg-white/40 transition-colors shadow-sm">
                <el-icon><Close /></el-icon>
              </button>
            </div>
            
            <!-- 封面图 (如果选中项有真实封面则显示，此处模拟) -->
            <div class="h-[180px] w-full relative bg-surface-container-low">
              <img v-if="selectedActivity.image && !selectedActivity.image.includes('default') && !imageErrors.has(selectedActivity.id)" 
                   :src="selectedActivity.image" 
                   @error="handleImageError(selectedActivity.id)"
                   class="w-full h-full object-cover">
              <div v-else class="w-full h-full bg-gradient-to-br from-blue-600 to-indigo-800 flex items-center justify-center text-white font-black text-2xl tracking-widest select-none">
                {{ selectedActivity.title ? selectedActivity.title.substring(0, 4) : '' }}
              </div>
              <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
              <div class="absolute bottom-4 left-5 pr-5">
                 <h3 class="text-white text-xl font-bold leading-tight drop-shadow-md">{{ selectedActivity.title }}</h3>
              </div>
            </div>
            
            <!-- 详情内容区 -->
            <div class="p-6 flex flex-col gap-6">
              
              <!-- 基本信息 (按照要求详细展示) -->
              <div class="grid grid-cols-2 gap-y-5 gap-x-6 text-base p-1">
                <div class="col-span-2 flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">报名时间</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.enrollTime || '2026.05.07 06:00-2026.05.11 22:00' }}</span>
                </div>
                <div class="col-span-2 flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">活动时间</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.timeDetail || '2026.05.12 04:55-2026.05.12 12:55' }}</span>
                </div>
                
                <div class="flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">活动级别</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.level || '校级' }}</span>
                </div>
                <div class="flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">参与范围</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.range || '学校内' }}</span>
                </div>
                
                <div class="flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">活动请假</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.leave || '支持' }}</span>
                </div>
                <div class="flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">活动地点</span>
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.location || '学校内' }}</span>
                </div>
                
                <div class="col-span-2 flex flex-col">
                  <span class="text-secondary font-medium text-[13px] mb-1.5">学分设置</span>
                  <div class="flex items-center gap-2">
                    <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.creditType || '文艺体育项目学分' }}</span>
                    <span class="text-[13px] text-secondary font-medium ml-1">学时{{ (selectedActivity.hours || 2.0).toFixed(2) }} 名额{{ selectedActivity.maxParticipants }}</span>
                  </div>
                </div>
              </div>
              
              <!-- 活动简介 -->
              <div>
                <h4 class="text-[15px] font-bold text-on-surface mb-2 flex items-center gap-1.5"><div class="w-1.5 h-4 bg-primary rounded-full"></div>活动详情</h4>
                <p class="text-[14px] text-secondary leading-relaxed text-justify">{{ selectedActivity.desc }}</p>
              </div>
            </div>
            
            <!-- 底部操作栏 -->
            <div class="mt-auto p-5 bg-white border-t border-outline-variant/10 flex gap-4 items-center shadow-[0_-4px_20px_-10px_rgba(0,0,0,0.05)] z-20">
              <div class="flex flex-col items-center justify-center min-w-[70px]">
                <span class="text-secondary font-medium text-xs mb-0.5">已报/上限</span>
                <span class="font-bold text-on-surface text-[14px]">{{ selectedActivity.participants || 0 }}/{{ selectedActivity.enrollLimit || '不限' }}</span>
              </div>
              <button @click="handleActivityAction(selectedActivity)" class="flex-1 py-2.5 flex items-center justify-center rounded-xl font-bold text-[14px] shadow-sm transition-all"
                :class="getActivityState(selectedActivity).btnClass"
                :disabled="getActivityState(selectedActivity).disabled">
                {{ getActivityState(selectedActivity).btnText }}
              </button>
            </div>
          </div>
        </transition>
      </div>
    </div>
    
    <!-- 局部暗色遮罩层，仅覆盖内容区，避免导航栏变灰 -->
    <div v-if="showCalendarDialog || showMyActivitiesDialog" class="fixed top-[56px] left-[256px] right-0 bottom-0 bg-black/40 z-[1000] pointer-events-none transition-opacity duration-300"></div>

    <!-- 放大版日历弹窗 -->
    <el-dialog v-model="showCalendarDialog" title="" modal-class="custom-calendar-overlay" class="custom-calendar-dialog" :show-close="true" append-to-body lock-scroll destroy-on-close>
      <div class="p-6 pt-5 flex flex-col h-full bg-white">
        <!-- 头部 -->
        <div class="flex items-center justify-center gap-6 mb-4 mt-2 relative">
          <button @click="prevMonth" class="flex items-center justify-center w-8 h-8 text-secondary hover:text-primary rounded-xl hover:bg-surface-container-low transition-colors"><el-icon><ArrowLeft /></el-icon></button>
          <span class="text-xl font-bold text-on-surface">{{ calendarMonthLabel }}</span>
          <button @click="nextMonth" class="flex items-center justify-center w-8 h-8 text-secondary hover:text-primary rounded-xl hover:bg-surface-container-low transition-colors"><el-icon><ArrowRight /></el-icon></button>
        </div>
        <!-- 星期 -->
        <div class="grid grid-cols-7 text-center mb-2">
          <span v-for="day in ['日','一','二','三','四','五','六']" :key="day" class="text-xs font-semibold text-outline">{{ day }}</span>
        </div>
        <!-- 天数网格 (均分5行) -->
        <div class="flex-1 grid grid-cols-7 border-t border-l border-outline-variant/20 rounded-xl overflow-hidden min-h-0" style="grid-template-rows: repeat(5, minmax(0, 1fr));">
          <div v-for="d in 35" :key="d" class="p-2 border-b border-r border-outline-variant/20 flex flex-col relative overflow-hidden transition-colors group" :class="getCalendarDayBg(d)">
            
            <!-- 水印日期数字 -->
            <div class="absolute bottom-1 right-2 text-[80px] leading-none font-black z-0 pointer-events-none select-none tracking-tighter" :class="calendarCells[d-1]?.dateStr === '2026-05-21' ? 'text-primary/10' : 'text-outline/10'">
              {{ getCalendarDayText(d) }}
            </div>

            <!-- 活动列表 -->
            <div class="relative z-10 flex-1 flex flex-col gap-1 overflow-y-auto custom-scrollbar pr-1 min-h-0 mt-1">
              <div v-for="(act, idx) in getCalendarDayActivities(d)" :key="idx" 
                class="text-[11px] px-1 py-0.5 leading-tight text-on-surface/90 font-medium flex items-start" 
                :class="getCalendarDayActivities(d).length === 1 ? 'whitespace-normal break-words' : 'truncate'"
                :title="act">
                <span class="inline-block flex-shrink-0 w-1.5 h-1.5 rounded-full mr-1.5 mt-1" :class="calendarCells[d-1]?.dateStr > '2026-05-21' ? 'bg-yellow-500' : (calendarCells[d-1]?.dateStr === '2026-05-21' ? 'bg-blue-500' : 'bg-gray-400')"></span>
                <span :class="getCalendarDayActivities(d).length === 1 ? '' : 'truncate'">{{ act }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 放大版历史活动弹窗 -->
    <el-dialog v-model="showMyActivitiesDialog" title="" modal-class="custom-calendar-overlay" class="custom-calendar-dialog" :show-close="true" append-to-body lock-scroll destroy-on-close>
      <div class="flex h-full min-h-0 bg-white rounded-3xl overflow-hidden">
        <!-- 左侧：活动列表 -->
        <div class="w-[50%] flex flex-col h-full border-r border-outline-variant/20">
          <div class="px-8 py-5 flex items-center justify-between border-b border-outline-variant/10">
            <h3 class="text-lg font-bold text-on-surface">已报名的活动</h3>
            <span class="text-sm font-bold text-secondary">共 {{ myEvents.length }} 条记录</span>
          </div>
          <div class="flex-1 overflow-y-auto custom-scrollbar px-6 py-4 flex flex-col gap-3">
            <div v-for="event in myEvents" :key="event.id" 
              @click="selectedHistoryActivity = event"
              class="rounded-2xl px-5 py-4 border transition-all flex gap-4 justify-between items-center cursor-pointer bg-surface-container-lowest"
              :class="selectedHistoryActivity?.id === event.id ? 'border-primary shadow-md ring-1 ring-primary/20' : 'border-outline-variant/20 hover:shadow-md hover:border-primary/30 group/card'">
              
              <!-- 左侧信息 -->
              <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-3 mb-1">
                    <h3 class="text-[15px] font-bold text-on-surface tracking-tight truncate">{{ event.title }}</h3>
                  </div>
                  <div class="flex flex-wrap items-center gap-x-5 gap-y-2 text-xs text-secondary font-medium mt-2">
                    <span class="flex items-center gap-1.5 whitespace-nowrap"><el-icon><Clock /></el-icon> {{ event.time }}</span>
                    <span class="flex items-center gap-1.5 whitespace-nowrap"><el-icon><Location /></el-icon> {{ event.location || event.tags.find(t => t.includes('活动')) }}</span>
                  </div>
              </div>

              <!-- 右侧学时 -->
              <div class="flex flex-col items-end flex-shrink-0 min-w-[70px]">
                <span class="text-[11px] font-bold text-secondary mb-1">{{ event.creditType || '活动学时' }}</span>
                <span class="text-lg font-black text-on-surface">{{ event.hours }} <span class="text-[10px] font-bold text-secondary">学时</span></span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右侧：详情信息 -->
        <div class="w-[50%] flex flex-col h-full bg-surface-container-lowest/50">
          <div class="px-8 py-5 border-b border-outline-variant/10">
            <h3 class="text-lg font-bold text-on-surface">详情信息</h3>
          </div>
          <div class="flex-1 overflow-y-auto custom-scrollbar p-8">
            <div v-if="selectedHistoryActivity" class="bg-white rounded-3xl border border-outline-variant/20 shadow-sm overflow-hidden flex flex-col">
              <!-- 图片展示 -->
              <div class="h-48 relative overflow-hidden flex-shrink-0 bg-surface-container-low">
                <img v-if="selectedHistoryActivity.image && !selectedHistoryActivity.image.includes('default') && !imageErrors.has(selectedHistoryActivity.id)" 
                     :src="selectedHistoryActivity.image" 
                     @error="handleImageError(selectedHistoryActivity.id)"
                     class="w-full h-full object-cover">
                <div v-else class="w-full h-full bg-gradient-to-br from-blue-600 to-indigo-800 flex items-center justify-center text-white font-black text-2xl tracking-widest select-none">
                  {{ selectedHistoryActivity.title ? selectedHistoryActivity.title.substring(0, 4) : '' }}
                </div>
                <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
                <div class="absolute bottom-4 left-5">
                   <h3 class="text-white text-lg font-bold leading-tight drop-shadow-md mb-2">{{ selectedHistoryActivity.title }}</h3>
                   <div class="flex gap-2">
                     <span v-for="tag in selectedHistoryActivity.tags" :key="tag" class="px-1.5 py-0.5 bg-white/20 backdrop-blur-sm text-white text-[10px] font-bold rounded shadow-sm">{{ tag }}</span>
                   </div>
                </div>
              </div>

              <!-- 详细信息网格 -->
              <div class="p-6 flex flex-col gap-6">
                <div class="grid grid-cols-2 gap-y-5 gap-x-6 text-base p-1">
                  <div class="col-span-2 flex flex-col">
                    <span class="text-secondary font-medium text-[13px] mb-1.5">报名时间</span>
                    <span class="font-bold text-on-surface text-[15px]">{{ selectedHistoryActivity.enrollTime || '2026.05.07 06:00-2026.05.11 22:00' }}</span>
                  </div>
                  <div class="col-span-2 flex flex-col">
                    <span class="text-secondary font-medium text-[13px] mb-1.5">活动时间</span>
                    <span class="font-bold text-on-surface text-[15px]">{{ selectedHistoryActivity.timeDetail || '2026.05.12 04:55-2026.05.12 12:55' }}</span>
                  </div>
                  
                  <div class="flex flex-col">
                    <span class="text-secondary font-medium text-[13px] mb-1.5">活动级别</span>
                    <span class="font-bold text-on-surface text-[15px]">{{ selectedHistoryActivity.level || '校级' }}</span>
                  </div>
                  <div class="flex flex-col">
                    <span class="text-secondary font-medium text-[13px] mb-1.5">参与范围</span>
                    <span class="font-bold text-on-surface text-[15px]">{{ selectedHistoryActivity.range || '学校内' }}</span>
                  </div>
                  
                  <div class="flex flex-col">
                    <span class="text-secondary font-medium text-[13px] mb-1.5">活动请假</span>
                    <span class="font-bold text-on-surface text-[15px]">{{ selectedHistoryActivity.leave || '支持' }}</span>
                  </div>
                  <div class="flex flex-col">
                    <span class="text-secondary font-medium text-[13px] mb-1.5">报名人数</span>
                    <span class="font-bold text-on-surface text-[15px]">{{ selectedHistoryActivity.enrollLimit || '不限' }}</span>
                  </div>
                  
                  <div class="col-span-2 flex flex-col">
                    <span class="text-secondary font-medium text-[13px] mb-1.5">学分设置</span>
                    <div class="flex items-center gap-2">
                      <span class="font-bold text-on-surface text-[15px]">{{ selectedHistoryActivity.creditType || '文艺体育项目学分' }}</span>
                      <span class="text-[13px] text-secondary font-medium ml-1">学时{{ (selectedHistoryActivity.hours || 2.0).toFixed(2) }} 名额{{ selectedHistoryActivity.maxParticipants }}</span>
                    </div>
                  </div>
                </div>

                <div>
                  <h4 class="text-[15px] font-bold text-on-surface mb-2 flex items-center gap-1.5"><div class="w-1.5 h-4 bg-primary rounded-full"></div>活动详情</h4>
                  <p class="text-[14px] text-secondary leading-relaxed text-justify">{{ selectedHistoryActivity.desc || '本次活动旨在传承红色基因，缅怀革命先烈，弘扬爱国精神，展现新时代大学生的精神风貌。' }}</p>
                </div>
              </div>
            </div>
            <!-- 空状态 -->
            <div v-else class="h-full flex flex-col items-center justify-center text-secondary opacity-60">
               <el-icon class="text-5xl mb-4"><DataLine /></el-icon>
               <p class="text-sm font-medium">请在左侧选择活动查看详情</p>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>

/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  width: 100%;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>

<style>
.custom-calendar-overlay {
  background-color: transparent !important; /* 隐藏原生遮罩背景 */
}
.custom-calendar-dialog {
  position: fixed !important;
  top: 96px !important;
  bottom: 40px !important;
  left: 288px !important;
  right: 32px !important;
  width: auto !important;
  height: auto !important;
  margin: 0 !important;
  padding: 0 !important;
  display: flex;
  flex-direction: column;
  border-radius: 32px !important; /* 圆角处理 */
  overflow: hidden !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25) !important;
  overscroll-behavior: contain;
}
.custom-calendar-dialog .el-dialog__headerbtn {
  top: 22px;
  right: 25px;
  z-index: 100;
}
.custom-calendar-dialog .el-dialog__header {
  padding: 0 !important;
  margin: 0 !important;
  height: 0;
}
.custom-calendar-dialog .el-dialog__body {
  flex: 1;
  min-height: 0;
  padding: 0 !important;
}
</style>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowRight, ArrowDown, Search, Filter, Clock, Location, ArrowLeft, Timer, ZoomIn, DataLine, Close, User, Star, Share } from '@element-plus/icons-vue'
import request from '@/utils/request'

const API = '/api/youth/activities'
const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'

const activeTab = ref('all')
const showCalendarDialog = ref(false)
const showMyActivitiesDialog = ref(false)
const calendarMonth = ref(new Date(2026, 4)) // May 2026

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = 5

// Watch tab and search query to reset pagination
watch([activeTab, searchQuery], () => {
  currentPage.value = 1
})

const filteredEvents = computed(() => {
  const list = events.value.filter(activity => {
    const isEnrolled = enrolledActivityIds.value.has(activity.id)
    const isPast = activity.date < '2026-05-21'
    
    let matchTab = true
    if (activeTab.value === 'enrolling') {
      matchTab = !isEnrolled && !isPast && (activity.participants < activity.maxParticipants)
    } else if (activeTab.value === 'enrolled') {
      matchTab = isEnrolled
    } else if (activeTab.value === 'completed') {
      matchTab = isPast
    }
    
    const matchSearch = !searchQuery.value ||
      activity.title.includes(searchQuery.value) ||
      activity.desc.includes(searchQuery.value) ||
      activity.tags.some(tag => tag.includes(searchQuery.value))
    return matchTab && matchSearch
  })
  return list.sort((a, b) => b.id - a.id)
})

const totalPages = computed(() => {
  return Math.ceil(filteredEvents.value.length / pageSize) || 1
})

const paginatedEvents = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredEvents.value.slice(start, start + pageSize)
})

const selectedActivity = ref(null)
const selectedHistoryActivity = ref(null)
const detailOffsetTop = ref(0)
const enrolledActivityIds = ref(new Set())
const imageErrors = ref(new Set())

// Persistent enrolled activities
const loadEnrolledActivities = () => {
  const stored = localStorage.getItem('enrolled_activities_202301042')
  if (stored) {
    enrolledActivityIds.value = new Set(JSON.parse(stored))
  } else {
    // Pre-seed some default enrolled IDs for demonstration (3, 4, 5, 11)
    const initial = [3, 4, 5, 11]
    enrolledActivityIds.value = new Set(initial)
    localStorage.setItem('enrolled_activities_202301042', JSON.stringify(initial))
  }
}

const saveEnrolledActivities = () => {
  localStorage.setItem('enrolled_activities_202301042', JSON.stringify(Array.from(enrolledActivityIds.value)))
}

const handleImageError = (id) => {
  imageErrors.value.add(id)
  imageErrors.value = new Set(imageErrors.value)
}

// Unified Activity State Calculations
const getActivityState = (activity) => {
  if (!activity) {
    return {
      label: '--',
      bg: 'bg-gray-400',
      textColor: 'text-gray-500',
      btnText: '--',
      btnClass: 'bg-gray-200 text-gray-400 cursor-not-allowed',
      disabled: true
    }
  }
  
  const isEnrolled = enrolledActivityIds.value.has(activity.id)
  const isPast = activity.date < '2026-05-21'
  
  if (isEnrolled) {
    if (isPast) {
      return {
        label: '已结束',
        bg: 'bg-gray-400',
        textColor: 'text-gray-500',
        btnText: '已结束',
        btnClass: 'bg-gray-200 text-gray-400 cursor-not-allowed',
        disabled: true
      }
    } else {
      return {
        label: '已报名',
        bg: 'bg-emerald-500',
        textColor: 'text-emerald-600',
        btnText: '已报名',
        btnClass: 'bg-emerald-50 text-emerald-600 border border-emerald-200 cursor-not-allowed',
        disabled: true
      }
    }
  } else {
    if (isPast) {
      return {
        label: '已结束',
        bg: 'bg-gray-400',
        textColor: 'text-gray-500',
        btnText: '已结束',
        btnClass: 'bg-gray-200 text-gray-400 cursor-not-allowed',
        disabled: true
      }
    } else {
      if (activity.participants >= activity.maxParticipants) {
        return {
          label: '已满员',
          bg: 'bg-orange-500',
          textColor: 'text-orange-600',
          btnText: '名额已满',
          btnClass: 'bg-gray-200 text-gray-400 cursor-not-allowed',
          disabled: true
        }
      } else {
        return {
          label: '报名中',
          bg: 'bg-blue-500',
          textColor: 'text-blue-600',
          btnText: '立即报名',
          btnClass: 'bg-blue-600 text-white hover:bg-blue-700',
          disabled: false
        }
      }
    }
  }
}

const selectActivity = (event, domEvent) => {
  selectedActivity.value = event
  const container = document.getElementById('activity-grid')
  const itemRect = domEvent.currentTarget.getBoundingClientRect()
  const containerRect = container.getBoundingClientRect()
  detailOffsetTop.value = Math.max(0, itemRect.top - containerRect.top)
}

const closeDetail = () => {
  selectedActivity.value = null
}

const handleActivityAction = async (activity) => {
  if (!activity || !activity.id) return
  const state = getActivityState(activity)
  if (state.disabled) return
  
  try {
    const res = await request.post(`${API}/${activity.id}/enroll?studentId=${STUDENT_ID}`)
    if (res.data.code === 200) {
      ElMessage.success('报名成功！')
      activity.participants = (activity.participants || 0) + 1
      enrolledActivityIds.value.add(activity.id)
      saveEnrolledActivities()
    } else {
      ElMessage.warning(res.data.msg || '报名失败')
    }
  } catch (e) { ElMessage.error('报名请求失败') }
}

const shareActivity = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('活动链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.info('分享功能：复制当前页面链接即可分享')
  })
}

const prevMonth = () => {
  const d = new Date(calendarMonth.value)
  d.setMonth(d.getMonth() - 1)
  calendarMonth.value = d
}

const nextMonth = () => {
  const d = new Date(calendarMonth.value)
  d.setMonth(d.getMonth() + 1)
  calendarMonth.value = d
}

const calendarMonthLabel = computed(() => {
  const d = calendarMonth.value
  return `${d.getFullYear()}年${d.getMonth() + 1}月`
})

const tabs = [
  { label: '全部活动', value: 'all' },
  { label: '报名中', value: 'enrolling' },
  { label: '已报名', value: 'enrolled' },
  { label: '已结束', value: 'completed' },
]

const mapActivity = (a) => {
  const statusMap = { '报名中': 'enrolling', '进行中': 'ongoing', '已结束': 'completed' }
  const s = statusMap[a.status] || 'enrolling'
  const image = a.coverImage || ''
  const tags = [a.level || '校级', a.location || ''].filter(Boolean)
  return {
    id: a.id,
    status: s,
    title: a.title,
    image,
    tags,
    date: a.date || '',
    time: a.timeDetail || a.date || '',
    location: a.location || '',
    desc: a.description || '',
    deadlineText: a.status === '报名中' ? '报名截止' : '活动时间',
    deadline: a.enrollTime || a.date || '',
    hours: a.credits || 0,
    participantText: `${a.participants || 0} 人已报名`,
    participants: a.participants || 0,
    maxParticipants: a.maxParticipants || 9999,
    level: a.level,
    range: a.rangeName || '学校内',
    leave: a.leaveSupport,
    enrollLimit: a.enrollLimit,
    creditType: a.creditType,
    enrollTime: a.enrollTime,
    timeDetail: a.timeDetail,
  }
}

const events = ref([])

const loadActivities = async () => {
  try {
    const res = await request.get(API)
    if (res.data.code === 200) {
      events.value = res.data.data.map(mapActivity)
    }
  } catch (e) { console.error('加载活动失败', e) }
}

onMounted(() => {
  loadActivities()
  loadEnrolledActivities()
})

const myEvents = computed(() => {
  return events.value.filter(e => enrolledActivityIds.value.has(e.id))
})

// Dynamic Calendar Grid Calculations (35 Cells Grid)
const calendarCells = computed(() => {
  const year = calendarMonth.value.getFullYear()
  const month = calendarMonth.value.getMonth()
  
  // First day of the calendarMonth
  const firstDay = new Date(year, month, 1)
  const startDayOfWeek = firstDay.getDay() // 0 = Sun, 1 = Mon, ..., 6 = Sat
  
  const cells = []
  for (let i = 0; i < 35; i++) {
    const date = new Date(year, month, 1 - startDayOfWeek + i)
    const yStr = date.getFullYear()
    const mStr = String(date.getMonth() + 1).padStart(2, '0')
    const dStr = String(date.getDate()).padStart(2, '0')
    cells.push({
      date,
      dayNum: date.getDate(),
      isCurrentMonth: date.getMonth() === month,
      dateStr: `${yStr}-${mStr}-${dStr}`
    })
  }
  return cells
})

const getCalendarDayText = (d) => {
  const cell = calendarCells.value[d - 1]
  return cell ? cell.dayNum : ''
}

const getCalendarDayClass = (d) => {
  const cell = calendarCells.value[d - 1]
  if (!cell) return ''
  if (!cell.isCurrentMonth) return 'text-outline bg-transparent'
  if (cell.dateStr === '2026-05-21') return 'bg-blue-600 text-white'
  return 'text-on-surface bg-transparent'
}

const getCalendarDayActivities = (d) => {
  const cell = calendarCells.value[d - 1]
  if (!cell) return []
  return events.value
    .filter(e => e.date === cell.dateStr)
    .map(e => e.title)
}

const getCalendarActivityCount = (d) => {
  return getCalendarDayActivities(d).length
}

const getCalendarDayBg = (d) => {
  const cell = calendarCells.value[d - 1]
  if (!cell) return 'bg-transparent'
  if (!cell.isCurrentMonth) return 'bg-surface-container-lowest/50'
  
  if (cell.dateStr === '2026-05-21') return 'bg-blue-50/30'
  
  const count = getCalendarActivityCount(d)
  if (count > 0) {
    if (cell.dateStr > '2026-05-21') {
      return 'bg-yellow-100/60'
    } else {
      return 'bg-surface-container/60'
    }
  }
  return 'bg-transparent'
}
</script>
