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
          <div class="flex items-center gap-2"><el-icon><Clock /></el-icon> 2024-06-01 至 2024-06-05</div>
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
            <span class="text-xs font-bold text-secondary">共 {{ filteredEvents.length }} 条活动</span>
            <div class="relative w-64 flex items-center">
              <span class="absolute left-3 flex items-center text-outline"><el-icon><Search /></el-icon></span>
              <input v-model="searchQuery" @keyup.enter="$event.target.blur()" type="text" placeholder="搜索活动名称、关键字..." class="w-full pl-9 pr-9 py-2 bg-surface-container-lowest border border-outline-variant/30 rounded-xl text-xs font-medium focus:outline-none focus:border-primary transition-all">
              <button class="absolute right-2 flex items-center justify-center p-1 text-secondary hover:text-primary transition-colors"><el-icon><Filter /></el-icon></button>
            </div>
          </div>
        </div>

        <!-- 活动列表 (自适应高度，最大展示5条) -->
        <div class="flex flex-col gap-4 mt-4 overflow-y-auto custom-scrollbar pr-2 max-h-[694px] flex-shrink-0">
          <div v-for="activity in filteredEvents" :key="activity.id" 
               @click="selectActivity(activity, $event)"
               class="rounded-2xl p-3 border border-outline-variant/20 hover:shadow-md transition-shadow flex gap-4 cursor-pointer bg-surface-container-lowest relative"
               :class="selectedActivity?.id === activity.id ? 'border-primary shadow-md' : 'hover:border-primary/30 group/card'">
            
            <!-- 左侧图片 -->
            <div class="w-[100px] h-[100px] rounded-xl overflow-hidden flex-shrink-0 relative group">
              <img :src="activity.image" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" :alt="activity.title">
              <div class="absolute top-1.5 left-1.5 px-1.5 py-0.5 rounded text-[10px] font-bold text-white shadow-sm" :class="statusConfig[activity.status].bg">
                {{ statusConfig[activity.status].label }}
              </div>
            </div>
            
            <!-- 中间内容 -->
            <div class="flex-1 flex flex-col justify-between py-0.5 min-w-0">
              <div>
                <div class="flex items-center gap-3 mb-1">
                  <h3 class="text-base font-bold text-on-surface tracking-tight truncate">{{ activity.title }}</h3>
                  <div class="flex gap-1.5 flex-shrink-0">
                    <span v-for="tag in activity.tags" :key="tag" class="px-1.5 py-0.5 bg-surface-container-low text-secondary text-[10px] font-semibold rounded border border-outline-variant/20">{{ tag }}</span>
                  </div>
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
              <span class="text-[11px] font-bold text-secondary mb-1">{{ activity.tags[0] || '活动学时' }}</span>
              <span class="text-[1.35rem] font-black leading-none mb-2" :class="statusConfig[activity.status].textColor">{{ activity.hours }} <span class="text-[10px] font-bold">学时</span></span>
              <button class="w-full py-1.5 rounded-lg text-xs font-bold transition-all shadow-sm"
                :class="statusConfig[activity.status].btnClass">
                {{ statusConfig[activity.status].btnText }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧区域 (固定高度与左侧对齐，带过渡动画) -->
      <div class="xl:col-span-4 relative h-full">
        <transition name="fade-slide" mode="out-in">
          <!-- 默认：活动日历与我的活动 -->
          <div v-if="!selectedActivity" class="flex flex-col gap-6 h-full">
        <!-- 活动日历 -->
        <div class="bg-white rounded-3xl p-6 border border-outline-variant/20 shadow-sm cursor-pointer hover:shadow-md transition-shadow relative" @click="showCalendarDialog = true">
          <div class="absolute top-6 right-6 text-xs text-secondary flex items-center gap-1 opacity-60 hover:opacity-100 transition-opacity">
            <el-icon><ZoomIn /></el-icon> 放大
          </div>
          <h3 class="text-base font-bold text-on-surface mb-4">活动日历</h3>
          <!-- 简单日历头部 -->
          <div class="flex items-center justify-between mb-4">
            <button class="text-secondary hover:text-primary"><el-icon><ArrowLeft /></el-icon></button>
            <span class="text-sm font-bold">2024年5月</span>
            <button class="text-secondary hover:text-primary"><el-icon><ArrowRight /></el-icon></button>
          </div>
          <!-- 日历星期 -->
          <div class="grid grid-cols-7 text-center mb-2">
            <span v-for="day in ['日','一','二','三','四','五','六']" :key="day" class="text-xs font-semibold text-outline">{{ day }}</span>
          </div>
          <!-- 日历天数 (模拟数据) -->
          <div class="grid grid-cols-7 text-center gap-y-2 gap-x-1" style="grid-template-rows: repeat(5, minmax(0, 1fr));">
            <div v-for="d in 35" :key="d" class="relative flex flex-col items-center justify-start py-1 rounded-xl transition-colors h-[52px]"
              :class="getCalendarDayBg(d)">
              <span class="text-xs font-semibold flex items-center justify-center w-6 h-6 rounded-full transition-colors z-10"
                :class="getCalendarDayClass(d)">
                {{ getCalendarDayText(d) }}
              </span>
              <!-- 活动数量 -->
              <span v-if="getCalendarActivityCount(d) > 0" class="text-[10px] font-bold mt-0.5 z-10"
                :class="(d - 3) > 20 ? 'text-yellow-700/90' : 'text-outline'">
                {{ getCalendarActivityCount(d) }}项
              </span>
            </div>
          </div>
          
          <!-- 图例 -->
          <div class="flex items-center justify-between mt-5 pt-4 border-t border-outline-variant/15 px-1">
            <div class="flex items-center gap-1.5"><span class="w-1.5 h-1.5 rounded-full bg-blue-600"></span><span class="text-[10px] text-secondary font-medium">当前日</span></div>
            <div class="flex items-center gap-1.5"><span class="w-2 h-2 rounded bg-yellow-100 border border-yellow-200"></span><span class="text-[10px] text-secondary font-medium">即将举办</span></div>
            <div class="flex items-center gap-1.5"><span class="w-2 h-2 rounded-full bg-outline-variant"></span><span class="text-[10px] text-secondary font-medium">已参加</span></div>
          </div>
        </div>

        <!-- 我的活动 (自动填满高度并对齐) -->
        <div class="bg-white rounded-3xl p-6 border border-outline-variant/20 shadow-sm flex-1 flex flex-col min-h-0 cursor-pointer hover:shadow-md transition-shadow group/history" @click="showMyActivitiesDialog = true">
          <div class="flex items-center justify-between mb-5 flex-shrink-0">
            <h3 class="text-base font-bold text-on-surface">我的活动</h3>
            <button class="text-xs font-semibold text-secondary group-hover/history:text-primary transition-colors">全部 <el-icon class="align-middle"><ArrowRight /></el-icon></button>
          </div>
          
          <div class="flex-1 overflow-hidden flex flex-col gap-1">
            <div v-for="myEvent in myEvents.filter(e => e.status === 'completed').slice(0, 4)" :key="myEvent.id" class="flex justify-between items-center py-3 border-b border-outline-variant/10 last:border-0 group cursor-pointer">
              <span class="text-sm font-bold text-on-surface group-hover:text-primary transition-colors truncate pr-4">{{ myEvent.title }}</span>
              <span class="text-xs font-semibold text-secondary flex-shrink-0">{{ myEvent.dateShort }}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 右侧内容结束 -->

          <!-- 活动详情 -->
          <div v-else class="bg-white rounded-3xl border border-outline-variant/20 shadow-sm overflow-hidden flex flex-col h-full relative">
          <!-- 头部操作区 (含关闭按钮) -->
          <div class="absolute top-4 right-4 z-20 flex gap-2">
            <button class="w-8 h-8 flex items-center justify-center rounded-full bg-white/20 backdrop-blur-md text-white hover:bg-white/40 transition-colors shadow-sm">
              <el-icon><Share /></el-icon>
            </button>
            <button @click="closeDetail" class="w-8 h-8 flex items-center justify-center rounded-full bg-white/20 backdrop-blur-md text-white hover:bg-white/40 transition-colors shadow-sm">
              <el-icon><Close /></el-icon>
            </button>
          </div>
          
          <!-- 封面图 (如果选中项有真实封面则显示，此处模拟) -->
          <div class="h-[180px] w-full relative bg-surface-container-low">
            <img v-if="!selectedActivity.image.includes('default')" :src="selectedActivity.image" class="w-full h-full object-cover">
            <div v-else class="w-full h-full bg-gradient-to-br from-blue-600 to-indigo-800"></div>
            <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
            <div class="absolute bottom-4 left-5 pr-5">
               <h3 class="text-white text-xl font-bold leading-tight drop-shadow-md mb-2">{{ selectedActivity.title }}</h3>
               <div class="flex gap-2 flex-wrap">
                 <span v-for="tag in selectedActivity.tags" :key="tag" class="px-1.5 py-0.5 bg-white/20 backdrop-blur-sm text-white text-[10px] font-bold rounded shadow-sm">{{ tag }}</span>
               </div>
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
                <span class="text-secondary font-medium text-[13px] mb-1.5">报名人数</span>
                <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.enrollLimit || '不限' }}</span>
              </div>
              
              <div class="col-span-2 flex flex-col">
                <span class="text-secondary font-medium text-[13px] mb-1.5">学分设置</span>
                <div class="flex items-center gap-2">
                  <span class="font-bold text-on-surface text-[15px]">{{ selectedActivity.creditType || '文艺体育项目学分' }}</span>
                  <span class="text-[13px] text-secondary font-medium ml-1">学时{{ (selectedActivity.hours || 2.0).toFixed(2) }} 名额1011</span>
                </div>
              </div>
            </div>
            
            <!-- 活动简介 -->
            <div>
              <h4 class="text-[15px] font-bold text-on-surface mb-2 flex items-center gap-1.5"><div class="w-1.5 h-4 bg-primary rounded-full"></div>活动详情</h4>
              <p class="text-[14px] text-secondary leading-relaxed text-justify">{{ selectedActivity.desc }}本次活动旨在传承红色基因，通过诵读经典篇章为主题，通过朗诵红色经典文学作品，缅怀革命先烈，弘扬爱国精神，展现新时代大学生的精神风貌。</p>
            </div>
          </div>
          
          <!-- 底部操作栏 -->
          <div class="mt-auto p-5 bg-white border-t border-outline-variant/10 flex gap-4 items-center shadow-[0_-4px_20px_-10px_rgba(0,0,0,0.05)] z-20">
            <div class="flex flex-col items-center justify-center min-w-[70px]">
              <span class="text-secondary font-medium text-xs mb-0.5">已报/上限</span>
              <span class="font-bold text-on-surface text-[14px]">{{ parseInt(selectedActivity.participantText) || 0 }}/{{ selectedActivity.enrollLimit || '不限' }}</span>
            </div>
            <button class="flex-1 py-2.5 flex items-center justify-center rounded-xl font-bold text-[14px] shadow-sm transition-all"
              :class="statusConfig[selectedActivity.status].btnClass">
              {{ statusConfig[selectedActivity.status].btnText }}
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
          <button class="flex items-center justify-center w-8 h-8 text-secondary hover:text-primary rounded-xl hover:bg-surface-container-low transition-colors"><el-icon><ArrowLeft /></el-icon></button>
          <span class="text-xl font-bold text-on-surface">2024年5月</span>
          <button class="flex items-center justify-center w-8 h-8 text-secondary hover:text-primary rounded-xl hover:bg-surface-container-low transition-colors"><el-icon><ArrowRight /></el-icon></button>
        </div>
        <!-- 星期 -->
        <div class="grid grid-cols-7 text-center mb-2">
          <span v-for="day in ['日','一','二','三','四','五','六']" :key="day" class="text-xs font-semibold text-outline">{{ day }}</span>
        </div>
        <!-- 天数网格 (均分5行) -->
        <div class="flex-1 grid grid-cols-7 border-t border-l border-outline-variant/20 rounded-xl overflow-hidden min-h-0" style="grid-template-rows: repeat(5, minmax(0, 1fr));">
          <div v-for="d in 35" :key="d" class="p-2 border-b border-r border-outline-variant/20 flex flex-col relative overflow-hidden transition-colors group" :class="getCalendarDayBg(d)">
            
            <!-- 水印日期数字 -->
            <div class="absolute bottom-1 right-2 text-[80px] leading-none font-black z-0 pointer-events-none select-none tracking-tighter" :class="d - 3 === 20 ? 'text-primary/10' : 'text-outline/10'">
              {{ getCalendarDayText(d) }}
            </div>

            <!-- 活动列表 -->
            <div class="relative z-10 flex-1 flex flex-col gap-1 overflow-y-auto custom-scrollbar pr-1 min-h-0 mt-1">
              <div v-for="(act, idx) in getCalendarDayActivities(d)" :key="idx" 
                class="text-[11px] px-1 py-0.5 leading-tight text-on-surface/90 font-medium flex items-start" 
                :class="getCalendarDayActivities(d).length === 1 ? 'whitespace-normal break-words' : 'truncate'"
                :title="act">
                <span class="inline-block flex-shrink-0 w-1.5 h-1.5 rounded-full mr-1.5 mt-1" :class="(d - 3) > 20 ? 'bg-yellow-500' : ((d - 3) === 20 ? 'bg-blue-500' : 'bg-gray-400')"></span>
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
            <h3 class="text-lg font-bold text-on-surface">历史参加的活动</h3>
            <span class="text-sm font-bold text-secondary">共 {{ events.filter(e => e.status === 'completed').length }} 条记录</span>
          </div>
          <div class="flex-1 overflow-y-auto custom-scrollbar px-6 py-4 flex flex-col gap-3">
            <div v-for="event in events.filter(e => e.status === 'completed')" :key="event.id" 
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
                <span class="text-[11px] font-bold text-secondary mb-1">{{ event.tags[0] || '活动学时' }}</span>
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
              <div class="h-48 relative overflow-hidden flex-shrink-0">
                <img v-if="!selectedHistoryActivity.image.includes('default')" :src="selectedHistoryActivity.image" class="w-full h-full object-cover">
                <div v-else class="w-full h-full bg-gradient-to-br from-blue-600 to-indigo-800"></div>
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
                      <span class="text-[13px] text-secondary font-medium ml-1">学时{{ (selectedHistoryActivity.hours || 2.0).toFixed(2) }} 名额1011</span>
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
import { ref, computed } from 'vue'
import { ArrowRight, ArrowDown, Search, Filter, Clock, Location, ArrowLeft, Timer, ZoomIn, DataLine, Close, User, Star, Share } from '@element-plus/icons-vue'

const activeTab = ref('all')
const showCalendarDialog = ref(false)
const showMyActivitiesDialog = ref(false)

const searchQuery = ref('')
const filteredEvents = computed(() => {
  return events.value.filter(activity => {
    const matchTab = activeTab.value === 'all' || activity.status === activeTab.value
    const matchSearch = !searchQuery.value || 
      activity.title.includes(searchQuery.value) || 
      activity.desc.includes(searchQuery.value) || 
      activity.tags.some(tag => tag.includes(searchQuery.value))
    return matchTab && matchSearch
  })
})

const selectedActivity = ref(null)
const selectedHistoryActivity = ref(null)
const detailOffsetTop = ref(0)

const selectActivity = (event, domEvent) => {
  selectedActivity.value = event
  const container = document.getElementById('activity-grid')
  const itemRect = domEvent.currentTarget.getBoundingClientRect()
  const containerRect = container.getBoundingClientRect()
  // Calculate dynamic offset so the detail panel top aligns precisely with the clicked card
  // Min limit to 0 to prevent overflowing upwards if there are weird paddings
  detailOffsetTop.value = Math.max(0, itemRect.top - containerRect.top)
}

const closeDetail = () => {
  selectedActivity.value = null
}

const tabs = [
  { label: '全部活动', value: 'all' },
  { label: '报名中', value: 'enrolling' },
  { label: '待参加', value: 'waiting' },
  { label: '进行中', value: 'ongoing' },
  { label: '已完成', value: 'completed' },
]

// 状态配置项，统一样式
const statusConfig = {
  'enrolling': { label: '报名中', bg: 'bg-blue-500', textSmall: 'text-blue-500', textColor: 'text-blue-600', btnClass: 'bg-blue-600 text-white hover:bg-blue-700', btnText: '立即报名' },
  'waiting': { label: '待参加', bg: 'bg-orange-500', textSmall: 'text-orange-500', textColor: 'text-orange-500', btnClass: 'bg-orange-50 text-orange-600 border border-orange-200 hover:bg-orange-100', btnText: '查看详情' },
  'ongoing': { label: '进行中', bg: 'bg-green-500', textSmall: 'text-green-500', textColor: 'text-green-500', btnClass: 'bg-green-50 text-green-600 border border-green-200 hover:bg-green-100', btnText: '签到打卡' },
  'completed': { label: '已完成', bg: 'bg-gray-400', textSmall: 'text-gray-500', textColor: 'text-gray-500', btnClass: 'bg-surface-container-low text-secondary border border-outline-variant/30 hover:bg-surface-container', btnText: '查看证书' },
}

// 模拟活动数据
const events = ref([
  {
    id: 1,
    status: 'enrolling',
    title: '红色经典诵读比赛',
    image: '/default-event.png',
    tags: ['思想政治', '校级', '线下活动'],
    time: '2024-05-25 14:00',
    location: '行政楼报告厅',
    desc: '传承红色基因，弘扬爱国精神，展现新时代大学生风采。',
    deadlineText: '报名截止',
    deadline: '2024-05-23',
    hours: 2,
    participantText: '234 人已报名'
  },
  {
    id: 2,
    status: 'enrolling',
    title: 'AI赋能未来：大学生科技创新讲座',
    image: '/default-event.png',
    tags: ['学术科技', '校级', '线上活动'],
    time: '2024-05-28 19:00',
    location: '腾讯会议',
    desc: '邀请行业专家分享AI前沿技术与应用，探索科技创新之路。',
    deadlineText: '报名截止',
    deadline: '2024-05-27',
    hours: 1,
    participantText: '166 人已报名'
  },
  {
    id: 3,
    status: 'waiting',
    title: '校园3V3篮球赛',
    image: 'https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=600&auto=format&fit=crop',
    tags: ['文体艺术', '院级', '线下活动'],
    time: '2024-05-30 09:00',
    location: '西区篮球场',
    desc: '挥洒汗水，燃烧青春，团队协作，勇夺冠军！',
    deadlineText: '活动时间',
    deadline: '05-30至06-02',
    hours: 3,
    participantText: '已报名'
  },
  {
    id: 4,
    status: 'ongoing',
    title: '校园环境美化志愿服务活动',
    image: 'https://images.unsplash.com/photo-1618477461853-cf6ed80faba5?q=80&w=600&auto=format&fit=crop',
    tags: ['志愿公益', '校级', '线下活动'],
    time: '2024-05-18 08:30',
    location: '图书馆周边',
    desc: '美化校园环境，创建绿色校园，从我做起。',
    deadlineText: '活动时间',
    deadline: '05-18',
    hours: 1,
    participantText: '已签到'
  },
  {
    id: 5,
    status: 'completed',
    title: '走进乡村：社会实践调研活动',
    image: 'https://images.unsplash.com/photo-1596484552834-6a58f850e0a1?q=80&w=600&auto=format&fit=crop',
    tags: ['社会实践', '校级', '线下活动'],
    time: '2024-04-28 08:00',
    location: '南充市某村',
    desc: '深入乡村开展调研，了解乡村发展现状与需求。',
    deadlineText: '活动时间',
    deadline: '04-28至04-30',
    hours: 4,
    participantText: '已完成'
  },
  {
    id: 6,
    status: 'enrolling',
    title: '青年志愿者招募培训',
    image: '/default-event.png',
    tags: ['志愿公益', '校级', '线上活动'],
    time: '2024-06-01 09:00',
    location: '腾讯会议',
    desc: '培养新时代青年志愿服务精神。',
    deadlineText: '报名截止',
    deadline: '2024-05-30',
    hours: 1,
    participantText: '80 人已报名',
    level: '院级',
    range: '全院学生',
    leave: '支持',
    enrollLimit: '200 人',
    creditType: '志愿公益项目学分',
    enrollTime: '2026.05.20 08:00-2026.05.30 18:00',
    timeDetail: '2026.06.01 09:00-2026.06.01 11:30'
  }
])

const myEvents = ref([
  {
    id: 101,
    status: 'waiting',
    title: '校园3V3篮球赛',
    image: 'https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=300&auto=format&fit=crop',
    timeShort: '09:00',
    dateShort: '05-30',
    location: '西区篮球场',
    hours: 3
  },
  {
    id: 102,
    status: 'ongoing',
    title: '校园环境美化志愿服务活动',
    image: 'https://images.unsplash.com/photo-1618477461853-cf6ed80faba5?q=80&w=300&auto=format&fit=crop',
    timeShort: '08:30',
    dateShort: '05-18',
    location: '图书馆周边',
    hours: 1
  },
  {
    id: 103,
    status: 'completed',
    title: '走进乡村：社会实践调研活动',
    image: 'https://images.unsplash.com/photo-1596484552834-6a58f850e0a1?q=80&w=300&auto=format&fit=crop',
    timeShort: '08:00',
    dateShort: '04-28',
    location: '南充市某村',
    hours: 4
  },
  {
    id: 104,
    status: 'completed',
    title: '春季田径运动会',
    image: 'https://images.unsplash.com/photo-1546519638-68e109498ffc?q=80&w=300&auto=format&fit=crop',
    timeShort: '08:00',
    dateShort: '04-15',
    location: '田径场',
    hours: 8
  },
  {
    id: 105,
    status: 'completed',
    title: '大学生英语演讲比赛',
    image: 'https://images.unsplash.com/photo-1618477461853-cf6ed80faba5?q=80&w=300&auto=format&fit=crop',
    timeShort: '14:00',
    dateShort: '03-22',
    location: '报告厅',
    hours: 2
  },
  {
    id: 106,
    status: 'completed',
    title: '编程马拉松：校园黑客松',
    image: 'https://images.unsplash.com/photo-1596484552834-6a58f850e0a1?q=80&w=300&auto=format&fit=crop',
    timeShort: '09:00',
    dateShort: '03-10',
    location: '图书馆',
    hours: 12
  }
])

// 简单的日历逻辑模拟
const calendarActivities = {
  12: ['校园歌手大赛'],
  14: ['心理健康讲座'],
  18: ['环境美化志愿服务'],
  22: ['创新创业大赛宣讲会'],
  25: ['红色经典诵读比赛', '志愿者培训会'],
  28: ['AI赋能未来讲座'],
  30: ['校园3V3篮球赛', '英语角活动', '读书分享会']
}

const getCalendarDayText = (d) => {
  // 假设前3天是上个月
  if (d <= 3) return 27 + d;
  // 假设当前月有31天
  if (d <= 34) return d - 3;
  // 下个月
  return 1;
}

const getCalendarDayClass = (d) => {
  if (d <= 3 || d > 34) return 'text-outline bg-transparent'; // 其他月
  if (d - 3 === 20) return 'bg-blue-600 text-white'; // 选中天 (当前日，假设20日)
  return 'text-on-surface bg-transparent'; // 本月普通天
}

const getCalendarActivityCount = (d) => {
  if (d <= 3 || d > 34) return 0;
  const day = d - 3;
  return calendarActivities[day]?.length || 0;
}

const getCalendarDayActivities = (d) => {
  if (d <= 3 || d > 34) return [];
  const day = d - 3;
  return calendarActivities[day] || [];
}

const getCalendarDayBg = (d) => {
  if (d <= 3 || d > 34) return 'bg-surface-container-lowest/50';
  const day = d - 3;
  if (day === 20) return 'bg-blue-50/30'; // 当前日微微偏蓝
  if (day > 20 && getCalendarActivityCount(d) > 0) return 'bg-yellow-100/60'; // 未来且有活动，浅黄色
  if (day < 20 && getCalendarActivityCount(d) > 0) return 'bg-surface-container/60'; // 过去且有活动，浅灰色
  return 'bg-transparent';
}
</script>
