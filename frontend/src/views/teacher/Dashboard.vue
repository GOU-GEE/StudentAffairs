<template>
  <div class="h-full flex flex-col gap-6 font-sans">
    
    <!-- 1. Top Five Stats Cards Row -->
    <div class="grid grid-cols-1 md:grid-cols-5 gap-6">
      
      <!-- Card 1: 所带学生 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100/80 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex items-center justify-between relative overflow-hidden group hover:shadow-md hover:-translate-y-0.5 transition-all duration-300">
        <div class="z-10">
          <span class="text-xs font-semibold text-gray-500 block mb-1">所带学生</span>
          <h3 class="text-3xl font-extrabold text-gray-800 leading-tight">{{ dashboardStats.totalStudents }}</h3>
          <div class="flex items-center gap-1.5 mt-2">
            <span class="text-[10px] font-bold text-indigo-600 bg-indigo-50 px-2 py-0.5 rounded-full">较上周 +5</span>
          </div>
        </div>
        <div class="w-11 h-11 rounded-xl bg-indigo-50 text-indigo-500 flex items-center justify-center z-10">
          <el-icon :size="20"><User /></el-icon>
        </div>
        <div class="absolute -right-4 -bottom-6 text-gray-100/40 pointer-events-none group-hover:scale-110 transition-transform duration-300">
          <el-icon :size="80"><User /></el-icon>
        </div>
      </div>

      <!-- Card 2: 请假申请 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100/80 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex items-center justify-between relative overflow-hidden group hover:shadow-md hover:-translate-y-0.5 transition-all duration-300">
        <div class="z-10">
          <span class="text-xs font-semibold text-gray-500 block mb-1">请假申请</span>
          <h3 class="text-3xl font-extrabold text-gray-800 leading-tight">{{ dashboardStats.pendingLeaves }}</h3>
          <div class="flex items-center gap-1.5 mt-2">
            <span class="text-[10px] font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full">待处理</span>
          </div>
        </div>
        <div class="w-11 h-11 rounded-xl bg-emerald-50 text-emerald-500 flex items-center justify-center z-10">
          <el-icon :size="20"><Document /></el-icon>
        </div>
        <div class="absolute -right-4 -bottom-6 text-gray-100/40 pointer-events-none group-hover:scale-110 transition-transform duration-300">
          <el-icon :size="80"><Document /></el-icon>
        </div>
      </div>

      <!-- Card 3: 预警学生 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100/80 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex items-center justify-between relative overflow-hidden group hover:shadow-md hover:-translate-y-0.5 transition-all duration-300">
        <div class="z-10">
          <span class="text-xs font-semibold text-gray-500 block mb-1">预警学生</span>
          <h3 class="text-3xl font-extrabold text-gray-800 leading-tight">{{ dashboardStats.warningCount }}</h3>
          <div class="flex items-center gap-1.5 mt-2">
            <span class="text-[10px] font-bold text-amber-600 bg-amber-50 px-2 py-0.5 rounded-full">待关注</span>
          </div>
        </div>
        <div class="w-11 h-11 rounded-xl bg-amber-50 text-amber-500 flex items-center justify-center z-10">
          <el-icon :size="20"><Warning /></el-icon>
        </div>
        <div class="absolute -right-4 -bottom-6 text-gray-100/40 pointer-events-none group-hover:scale-110 transition-transform duration-300">
          <el-icon :size="80"><Warning /></el-icon>
        </div>
      </div>

      <!-- Card 4: 待办事务 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100/80 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex items-center justify-between relative overflow-hidden group hover:shadow-md hover:-translate-y-0.5 transition-all duration-300">
        <div class="z-10">
          <span class="text-xs font-semibold text-gray-500 block mb-1">待办事务</span>
          <h3 class="text-3xl font-extrabold text-gray-800 leading-tight">15</h3>
          <div class="flex items-center gap-1.5 mt-2">
            <span class="text-[10px] font-bold text-sky-600 bg-sky-50 px-2 py-0.5 rounded-full">待处理</span>
          </div>
        </div>
        <div class="w-11 h-11 rounded-xl bg-sky-50 text-sky-500 flex items-center justify-center z-10">
          <el-icon :size="20"><Calendar /></el-icon>
        </div>
        <div class="absolute -right-4 -bottom-6 text-gray-100/40 pointer-events-none group-hover:scale-110 transition-transform duration-300">
          <el-icon :size="80"><Calendar /></el-icon>
        </div>
      </div>

      <!-- Card 5: 本周活动 -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100/80 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex items-center justify-between relative overflow-hidden group hover:shadow-md hover:-translate-y-0.5 transition-all duration-300">
        <div class="z-10">
          <span class="text-xs font-semibold text-gray-500 block mb-1">本周活动</span>
          <h3 class="text-3xl font-extrabold text-gray-800 leading-tight">3</h3>
          <div class="flex items-center gap-1.5 mt-2">
            <span class="text-[10px] font-bold text-purple-600 bg-purple-50 px-2 py-0.5 rounded-full">已发布</span>
          </div>
        </div>
        <div class="w-11 h-11 rounded-xl bg-purple-50 text-purple-500 flex items-center justify-center z-10">
          <el-icon :size="20"><Trophy /></el-icon>
        </div>
        <div class="absolute -right-4 -bottom-6 text-gray-100/40 pointer-events-none group-hover:scale-110 transition-transform duration-300">
          <el-icon :size="80"><Trophy /></el-icon>
        </div>
      </div>

    </div>

    <!-- 2. Bento Grid Layout -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
      
      <!-- Left side grid: 4 balanced cards (col-span-8) -->
      <div class="md:col-span-8 grid grid-cols-1 md:grid-cols-2 gap-6">
        
        <!-- Card 1: 重要提醒 -->
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex flex-col justify-between h-[250px] hover:shadow-md transition-all duration-300">
          <div>
            <div class="flex items-center gap-2 mb-4">
              <span class="w-2.5 h-2.5 rounded-full bg-amber-500 animate-pulse"></span>
              <h3 class="text-[1rem] font-bold text-gray-800 tracking-tight">重要提醒</h3>
            </div>
            
            <div class="space-y-3.5">
              <div class="flex items-center justify-between text-xs">
                <div class="flex items-center gap-2 text-gray-600">
                  <span class="w-1.5 h-1.5 rounded-full bg-amber-500"></span>
                  <span>有 3 名学生触发学业预警</span>
                </div>
                <span class="text-gray-400">10 分钟前</span>
              </div>
              
              <div class="flex items-center justify-between text-xs">
                <div class="flex items-center gap-2 text-gray-600">
                  <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                  <span>有 2 名学生提交了心理咨询预约</span>
                </div>
                <span class="text-gray-400">25 分钟前</span>
              </div>
              
              <div class="flex items-center justify-between text-xs">
                <div class="flex items-center gap-2 text-gray-600">
                  <span class="w-1.5 h-1.5 rounded-full bg-indigo-500"></span>
                  <span>春季运动会报名进行中</span>
                </div>
                <span class="text-gray-400">1 小时前</span>
              </div>
              
              <div class="flex items-center justify-between text-xs">
                <div class="flex items-center gap-2 text-gray-600">
                  <span class="w-1.5 h-1.5 rounded-full bg-rose-500"></span>
                  <span>毕业生就业材料提交截止提醒</span>
                </div>
                <span class="text-gray-400">3 小时前</span>
              </div>
            </div>
          </div>
          
          <button @click="navigateTo('/teacher/communication')" class="w-full text-center text-xs font-semibold text-indigo-600 hover:text-indigo-700 mt-2 flex items-center justify-center gap-1 group">
            查看全部提醒
            <el-icon class="group-hover:translate-x-0.5 transition-transform"><ArrowRight /></el-icon>
          </button>
        </div>

        <!-- Card 2: 学生状态概览 -->
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex flex-col justify-between h-[250px] hover:shadow-md transition-all duration-300">
          <div>
            <div class="flex items-center justify-between mb-2">
              <h3 class="text-[1rem] font-bold text-gray-800 tracking-tight">学生状态概览</h3>
              <el-dropdown trigger="click">
                <span class="text-xs text-gray-400 cursor-pointer flex items-center gap-0.5 hover:text-gray-600 transition-colors">
                  本学期 <el-icon :size="10"><ArrowRight class="rotate-90" /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>本学期</el-dropdown-item>
                    <el-dropdown-item>上学期</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <div class="flex items-center justify-between gap-4 mt-2">
              <!-- Center absolute text overlay wrapper -->
              <div class="relative w-32 h-32 flex-shrink-0 flex items-center justify-center">
                <div ref="statusChartRef" class="w-full h-full"></div>
                <div class="absolute flex flex-col items-center justify-center pointer-events-none">
                  <span class="text-xl font-extrabold text-gray-800 leading-none">{{ dashboardStats.totalStudents }}</span>
                  <span class="text-[9px] text-gray-400 mt-1 font-medium">总人数</span>
                </div>
              </div>

              <!-- Compact Legend Details -->
              <div class="flex-1 grid grid-cols-2 gap-x-2 gap-y-2 text-[10px] text-gray-500 font-medium">
                <div class="flex items-center gap-1">
                  <span class="w-2 h-2 rounded-full bg-blue-500 flex-shrink-0"></span>
                  <span class="truncate">正常</span>
                  <span class="text-gray-800 font-bold ml-auto">186人</span>
                </div>
                <div class="flex items-center gap-1">
                  <span class="text-[9px] bg-blue-50 text-blue-500 px-1 rounded font-semibold ml-auto">75.0%</span>
                </div>
                <div class="flex items-center gap-1">
                  <span class="w-2 h-2 rounded-full bg-orange-500 flex-shrink-0"></span>
                  <span class="truncate">学业预警</span>
                  <span class="text-gray-800 font-bold ml-auto">32人</span>
                </div>
                <div class="flex items-center gap-1">
                  <span class="text-[9px] bg-orange-50 text-orange-500 px-1 rounded font-semibold ml-auto">12.9%</span>
                </div>
                <div class="flex items-center gap-1">
                  <span class="w-2 h-2 rounded-full bg-purple-500 flex-shrink-0"></span>
                  <span class="truncate">心理关注</span>
                  <span class="text-gray-800 font-bold ml-auto">18人</span>
                </div>
                <div class="flex items-center gap-1">
                  <span class="text-[9px] bg-purple-50 text-purple-500 px-1 rounded font-semibold ml-auto">7.3%</span>
                </div>
                <div class="flex items-center gap-1">
                  <span class="w-2 h-2 rounded-full bg-rose-500 flex-shrink-0"></span>
                  <span class="truncate">经济困难</span>
                  <span class="text-gray-800 font-bold ml-auto">12人</span>
                </div>
                <div class="flex items-center gap-1">
                  <span class="text-[9px] bg-rose-50 text-rose-500 px-1 rounded font-semibold ml-auto">4.8%</span>
                </div>
              </div>
            </div>
          </div>

          <button @click="navigateTo('/teacher/academic-warning')" class="w-full text-center text-xs font-semibold text-indigo-600 hover:text-indigo-700 flex items-center justify-center gap-1 group">
            查看详情
            <el-icon class="group-hover:translate-x-0.5 transition-transform"><ArrowRight /></el-icon>
          </button>
        </div>

        <!-- Card 3: 预警情况分布 -->
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex flex-col justify-between h-[250px] hover:shadow-md transition-all duration-300">
          <div>
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-[1rem] font-bold text-gray-800 tracking-tight">预警情况分布</h3>
              <el-dropdown trigger="click">
                <span class="text-xs text-gray-400 cursor-pointer flex items-center gap-0.5 hover:text-gray-600 transition-colors">
                  本学期 <el-icon :size="10"><ArrowRight class="rotate-90" /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>本学期</el-dropdown-item>
                    <el-dropdown-item>上学期</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <!-- Custom Elegant Progress List -->
            <div class="space-y-3 mt-1.5">
              
              <!-- Progress 1: 学业预警 -->
              <div class="flex flex-col gap-1 text-[11px] font-medium text-gray-500">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-orange-500"></span>
                    <span>学业预警</span>
                  </div>
                  <span class="text-gray-800 font-bold">32 人</span>
                </div>
                <div class="w-full h-1.5 bg-gray-50 rounded-full overflow-hidden">
                  <div class="h-full bg-orange-500 rounded-full transition-all duration-500" style="width: 64%"></div>
                </div>
              </div>

              <!-- Progress 2: 心理预警 -->
              <div class="flex flex-col gap-1 text-[11px] font-medium text-gray-500">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-purple-500"></span>
                    <span>心理预警</span>
                  </div>
                  <span class="text-gray-800 font-bold">18 人</span>
                </div>
                <div class="w-full h-1.5 bg-gray-50 rounded-full overflow-hidden">
                  <div class="h-full bg-purple-500 rounded-full transition-all duration-500" style="width: 36%"></div>
                </div>
              </div>

              <!-- Progress 3: 违纪预警 -->
              <div class="flex flex-col gap-1 text-[11px] font-medium text-gray-500">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-rose-500"></span>
                    <span>违纪预警</span>
                  </div>
                  <span class="text-gray-800 font-bold">8 人</span>
                </div>
                <div class="w-full h-1.5 bg-gray-50 rounded-full overflow-hidden">
                  <div class="h-full bg-rose-500 rounded-full transition-all duration-500" style="width: 16%"></div>
                </div>
              </div>

              <!-- Progress 4: 就业预警 -->
              <div class="flex flex-col gap-1 text-[11px] font-medium text-gray-500">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-blue-500"></span>
                    <span>就业预警</span>
                  </div>
                  <span class="text-gray-800 font-bold">6 人</span>
                </div>
                <div class="w-full h-1.5 bg-gray-50 rounded-full overflow-hidden">
                  <div class="h-full bg-blue-500 rounded-full transition-all duration-500" style="width: 12%"></div>
                </div>
              </div>

            </div>
          </div>

          <button @click="navigateTo('/teacher/academic-warning')" class="w-full text-center text-xs font-semibold text-indigo-600 hover:text-indigo-700 flex items-center justify-center gap-1 group">
            查看预警详情
            <el-icon class="group-hover:translate-x-0.5 transition-transform"><ArrowRight /></el-icon>
          </button>
        </div>

        <!-- Card 4: 事务处理进度 -->
        <div class="bg-white rounded-2xl p-6 border border-gray-100 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex flex-col justify-between h-[250px] hover:shadow-md transition-all duration-300">
          <div>
            <div class="flex items-center justify-between mb-2">
              <h3 class="text-[1rem] font-bold text-gray-800 tracking-tight">事务处理进度</h3>
              <el-dropdown trigger="click">
                <span class="text-xs text-gray-400 cursor-pointer flex items-center gap-0.5 hover:text-gray-600 transition-colors">
                  本周 <el-icon :size="10"><ArrowRight class="rotate-90" /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>本周</el-dropdown-item>
                    <el-dropdown-item>本月</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <div class="flex items-center justify-between gap-4 mt-2">
              <!-- Gauge Center layout -->
              <div class="relative w-32 h-32 flex-shrink-0 flex items-center justify-center">
                <div ref="progressChartRef" class="w-full h-full"></div>
                <div class="absolute flex flex-col items-center justify-center pointer-events-none">
                  <span class="text-xl font-extrabold text-gray-800 leading-none">72%</span>
                  <span class="text-[9px] text-gray-400 mt-1 font-medium">已完成</span>
                </div>
              </div>

              <!-- Task stats list -->
              <div class="flex-1 space-y-2.5 text-[11px] text-gray-500 font-medium">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                    <span>已完成</span>
                  </div>
                  <span class="text-gray-800 font-bold">36 件</span>
                </div>
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-indigo-500"></span>
                    <span>进行中</span>
                  </div>
                  <span class="text-gray-800 font-bold">14 件</span>
                </div>
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-gray-300"></span>
                    <span>待处理</span>
                  </div>
                  <span class="text-gray-800 font-bold">10 件</span>
                </div>
              </div>
            </div>
          </div>

          <button @click="navigateTo('/teacher/financial-aid')" class="w-full text-center text-xs font-semibold text-indigo-600 hover:text-indigo-700 flex items-center justify-center gap-1 group">
            查看全部事务
            <el-icon class="group-hover:translate-x-0.5 transition-transform"><ArrowRight /></el-icon>
          </button>
        </div>

      </div>

      <!-- Right Column: AI Counselor Assistant (col-span-4) -->
      <div class="md:col-span-4 bg-gradient-to-br from-indigo-50/40 via-white to-purple-50/20 rounded-2xl p-6 border border-indigo-100/40 shadow-[0_4px_20px_rgba(0,0,0,0.015)] flex flex-col h-[524px] justify-between relative overflow-hidden group hover:shadow-md transition-all duration-300">
        <!-- Glow top corner decorator -->
        <div class="absolute -top-12 -right-12 w-32 h-32 bg-indigo-400/10 rounded-full blur-2xl pointer-events-none group-hover:scale-125 transition-transform duration-700"></div>

        <!-- Overlay when expanded -->
        <div v-if="aiChatVisible" class="absolute inset-0 bg-white/95 backdrop-blur-sm z-30 flex flex-col items-center justify-center p-6 text-center">
          <div class="w-12 h-12 rounded-full bg-indigo-50 text-indigo-600 flex items-center justify-center mb-3 shadow-sm border border-indigo-100/40 animate-bounce" style="animation-duration: 3s;">
            <el-icon :size="24"><MagicStick /></el-icon>
          </div>
          <h4 class="font-bold text-gray-800 text-sm mb-1">AI 助手已在大窗口展开</h4>
          <p class="text-xs text-gray-400 max-w-[200px] mb-4">为了提供更舒适的阅读与操作空间，对话已为您展开为大窗口模式。</p>
          <el-button type="primary" size="small" round class="!bg-indigo-600 !border-0 hover:!bg-indigo-700" @click="openLargeChat">
            打开对话窗口
          </el-button>
        </div>

        <div>
          <!-- Header and Custom AI Avatar -->
          <div class="flex items-start justify-between mb-4 relative z-10">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-indigo-600 text-white flex items-center justify-center shadow-md shadow-indigo-100">
                <el-icon class="animate-pulse"><MagicStick /></el-icon>
              </div>
              <h3 class="text-[1rem] font-bold text-gray-800 tracking-tight">AI 辅导员助手</h3>
            </div>
            
            <!-- Controls instead of Robot Icon -->
            <div class="flex items-center gap-1.5 z-10">
              <el-tooltip content="历史对话" placement="top">
                <el-button circle size="small" class="!bg-indigo-50 hover:!bg-indigo-100 !border-0 !text-indigo-600 flex items-center justify-center" @click="toggleHistory">
                  <el-icon><Clock /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="展开窗口" placement="top">
                <el-button circle size="small" class="!bg-indigo-50 hover:!bg-indigo-100 !border-0 !text-indigo-600 flex items-center justify-center" @click="openLargeChat">
                  <el-icon><FullScreen /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </div>

          <!-- Welcome Screen (Mutually Exclusive with Chat Stream) -->
          <div v-if="showWelcome && !showHistory" class="space-y-4 relative z-10">
            <div class="space-y-1">
              <h4 class="font-extrabold text-[0.875rem] text-gray-800">您好，我是您的 AI 助手</h4>
              <p class="text-[0.75rem] text-gray-400 leading-relaxed">
                我可以帮助分析学生数据、生成工作建议、解答业务问题，让您的工作更高效~
              </p>
            </div>
 
            <!-- Pre-filled prompt cards grid -->
            <div class="grid grid-cols-1 gap-2.5 max-h-[290px] overflow-y-auto pr-1.5 custom-scrollbar">
              <div v-for="(p, idx) in quickPrompts" :key="idx" 
                   @click="selectPrompt(p.prompt)"
                   class="bg-white/80 backdrop-blur-sm border rounded-xl p-3 cursor-pointer transition-all duration-300 flex items-center justify-between group/card shadow-[0_2px_10px_rgba(0,0,0,0.01)]"
                   :class="p.color">
                <div class="flex-1 min-w-0 pr-2">
                  <div class="flex items-center gap-1.5 mb-0.5">
                    <span class="w-1.5 h-1.5 rounded-full bg-indigo-500 flex-shrink-0"></span>
                    <h5 class="font-bold text-[0.75rem] text-gray-700 truncate">{{ p.title }}</h5>
                  </div>
                  <p class="text-[10px] text-gray-400 truncate">{{ p.desc }}</p>
                </div>
                <el-icon class="text-indigo-400 group-hover/card:translate-x-0.5 group-hover/card:text-indigo-600 transition-all" :size="12"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
 
          <!-- History List Container -->
          <div v-else-if="showWelcome && showHistory" class="space-y-3 relative z-10">
            <div class="flex items-center justify-between">
              <h4 class="font-bold text-[0.875rem] text-gray-800 flex items-center gap-1.5">
                <el-icon class="text-indigo-600"><Clock /></el-icon>
                历史对话记录
              </h4>
              <button 
                v-if="historyList.length > 0"
                @click="clearHistory" 
                class="text-[10px] text-rose-500 hover:text-rose-600 hover:underline font-medium"
              >
                清空记录
              </button>
            </div>
            
            <!-- History Items list -->
            <div class="grid grid-cols-1 gap-2 max-h-[290px] overflow-y-auto pr-1.5 custom-scrollbar">
              <div v-if="historyList.length === 0" class="flex flex-col items-center justify-center py-12 text-gray-400 text-xs">
                <el-icon :size="28" class="mb-2"><Clock /></el-icon>
                <span>暂无历史对话记录</span>
              </div>
              <div v-else v-for="h in historyList" :key="h.id" 
                   @click="selectHistoryItem(h)"
                   class="bg-white/80 hover:bg-indigo-50/20 border border-gray-100 hover:border-indigo-100 rounded-xl p-3 cursor-pointer transition-all duration-300 flex items-center justify-between group shadow-[0_2px_10px_rgba(0,0,0,0.005)]"
              >
                <div class="flex-1 min-w-0 pr-2">
                  <h5 class="font-bold text-[0.75rem] text-gray-700 truncate group-hover:text-indigo-600 transition-colors">{{ h.title }}</h5>
                  <p class="text-[9px] text-gray-400 mt-0.5">{{ h.time }}</p>
                </div>
                <el-icon class="text-indigo-400 group-hover:translate-x-0.5 group-hover:text-indigo-600 transition-all" :size="12"><ArrowRight /></el-icon>
              </div>
            </div>
            
            <button @click="showHistory = false" class="w-full text-center text-xs font-semibold text-indigo-600 hover:text-indigo-700 mt-2 flex items-center justify-center gap-1 group">
              返回快捷发送
              <el-icon class="group-hover:-translate-x-0.5 transition-transform rotate-180"><ArrowRight /></el-icon>
            </button>
          </div>

          <!-- Chat Console State -->
          <div v-else class="flex flex-col h-[380px] bg-white/60 border border-indigo-50/50 backdrop-blur-sm rounded-xl p-3 relative z-10">
            <div class="flex items-center justify-between border-b border-gray-100 pb-2 mb-2 flex-shrink-0">
              <span class="text-[10px] font-bold text-indigo-600">服务问答会话</span>
              <button @click="resetToWelcome" class="text-[9px] font-bold text-gray-400 hover:text-gray-600 hover:underline">返回首页</button>
            </div>
            
            <div class="flex-1 overflow-y-auto space-y-3.5 pr-1.5 custom-scrollbar" ref="chatBox">
              <div v-for="(msg, idx) in chatMessages" :key="idx" 
                   :class="['rounded-xl px-3 py-2 text-xs leading-relaxed max-w-[85%] whitespace-pre-wrap flex flex-col', 
                            msg.isUser ? 'bg-indigo-600 text-white rounded-tr-none ml-auto border border-indigo-600' : 'bg-gray-50 border border-gray-100 text-gray-700 rounded-tl-none']">
                {{ msg.text }}
              </div>
              
              <!-- Typing Status -->
              <div v-if="isTyping" class="bg-gray-50 border border-gray-100 rounded-xl px-3 py-2 rounded-tl-none inline-flex items-center gap-2 max-w-[80%]">
                <el-icon class="animate-spin text-indigo-500"><Loading /></el-icon>
                <span class="text-gray-400 text-[10px]">AI 助手正在为您分析并生成建议...</span>
              </div>
            </div>
          </div>

        </div>

        <!-- Chat Input Bar -->
        <div class="relative z-10 mt-3 pt-3 border-t border-gray-100/50">
          <input type="text" 
                 v-model="userInput" 
                 @keyup.enter="sendMessage" 
                 placeholder="输入您的指令或提问..." 
                 class="w-full bg-white border border-gray-200/80 rounded-xl pl-4 pr-12 py-3 text-xs focus:outline-none focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 transition-all text-gray-800 placeholder-gray-400 shadow-inner">
          <button @click="sendMessage" 
                  :disabled="isTyping || !userInput.trim()" 
                  class="absolute right-1.5 top-[18px] p-2 rounded-lg bg-indigo-600 text-white hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:bg-gray-300 shadow-md shadow-indigo-100 flex items-center justify-center">
            <el-icon :size="12"><Promotion /></el-icon>
          </button>
        </div>

      </div>

    </div>

    <!-- ==================== AI 辅导员浮动大助手 ==================== -->
    <div class="fixed bottom-8 right-8 z-[1050] flex flex-col items-end">
      <transition name="ai-chat-drop">
        <div v-if="aiChatVisible" class="bg-white border border-slate-200/80 rounded-3xl w-[600px] h-[calc(100vh-160px)] mb-4 flex flex-col overflow-hidden transition-all duration-300 max-w-[calc(100vw-2rem)] shadow-[0_10px_50px_rgba(0,0,0,0.12)]">
          <!-- Chat Header -->
          <div style="background-color: #4a89e247;" class="border-b border-slate-200/80 p-4 text-slate-800 flex items-center justify-between shadow-sm">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-slate-200/60 flex items-center justify-center border border-slate-300/40 text-slate-600 shadow-inner">
                <el-icon :size="20"><MagicStick /></el-icon>
              </div>
              <div>
                <h4 class="font-bold text-sm text-slate-800">AI 辅导员小助手</h4>
                <p class="text-[10px] text-slate-500">智慧学工 · 辅导员专属助手</p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <el-button circle size="small" class="!bg-slate-200/50 !border-0 !text-slate-600 hover:!bg-slate-200/80 hover:!text-slate-800" @click="resetChat">
                <el-icon><Refresh /></el-icon>
              </el-button>
              <el-button circle size="small" class="!bg-slate-200/50 !border-0 !text-slate-600 hover:!bg-slate-200/80 hover:!text-slate-800" @click="closeLargeChat">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </div>

          <!-- Chat Messages Container -->
          <div ref="largeChatScrollContainer" class="flex-1 overflow-y-auto p-4 space-y-4 custom-scrollbar bg-slate-50/50">
            <!-- Messages -->
            <div v-for="(msg, idx) in chatMessages" :key="idx" class="flex w-full" :class="msg.isUser ? 'justify-end' : 'justify-start'">
              <!-- Message Bubble -->
              <div 
                class="rounded-2xl px-4 py-2.5 text-[13px] shadow-sm leading-relaxed max-w-[85%]"
                :class="msg.isUser 
                  ? 'bg-sky-100 text-sky-900 rounded-tr-none border border-sky-200/50' 
                  : 'bg-white text-gray-800 border border-slate-200/60 rounded-tl-none font-normal'"
              >
                <div v-if="msg.isUser">{{ msg.text }}</div>
                <div v-else class="prose max-w-none space-y-2 text-justify" v-html="renderMarkdown(msg.text)"></div>
              </div>
            </div>

            <!-- Typing Loader -->
            <div v-if="isTyping" class="flex justify-start w-full">
              <div class="bg-white border border-slate-200/60 rounded-2xl rounded-tl-none px-4 py-2.5 text-xs text-secondary flex items-center gap-1.5 shadow-sm">
                <span>AI 助手正在为您分析并生成建议</span>
                <span class="flex gap-0.5 mt-0.5">
                  <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce" style="animation-delay: 0ms"></span>
                  <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce" style="animation-delay: 150ms"></span>
                  <span class="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-bounce" style="animation-delay: 300ms"></span>
                </span>
              </div>
            </div>
          </div>

          <!-- Input Area -->
          <div class="p-3 bg-white border-t border-slate-100 flex items-center gap-2 flex-shrink-0">
            <el-input 
              ref="largeChatInputRef"
              v-model="userInput" 
              placeholder="输入您的指令或提问..." 
              class="flex-1 custom-chat-input"
              @keyup.enter="sendMessage"
              size="default"
            />
            <el-button 
              type="primary" 
              circle 
              class="!bg-sky-500 !border-0 flex items-center justify-center hover:!bg-sky-600 shadow-sm transition-colors" 
              @click="sendMessage" 
              :disabled="isTyping"
              size="default"
            >
              <el-icon><Promotion /></el-icon>
            </el-button>
          </div>
        </div>
      </transition>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Document, Warning, Trophy, Download, UploadFilled, MagicStick, Loading, Position, Calendar, ArrowRight, Promotion, Close, Refresh, Clock, FullScreen } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const router = useRouter()

// Stats state with fallbacks
const dashboardStats = ref({
  totalStudents: 248,
  pendingLeaves: 8,
  warningCount: 12
})

// Quick prompts configured exactly from user mock
const quickPrompts = [
  {
    title: '学生画像分析',
    desc: '多维度分析学生特点、行为偏好、成长轨迹',
    prompt: '请帮我分析本班学生的画像特征，包括行为偏好与成长轨迹。',
    color: 'border-indigo-100/50 hover:bg-indigo-50/15'
  },
  {
    title: '学业预警分析',
    desc: '智能识别学业风险，提供干预建议',
    prompt: '如何对本班级中触发学业预警的学生进行干预？请提供建议。',
    color: 'border-blue-100/50 hover:bg-blue-50/15'
  },
  {
    title: '谈心谈话建议',
    desc: '提供谈心话题建议和沟通策略',
    prompt: '请为挂科较多的学生提供谈心谈话的主题建议与沟通策略。',
    color: 'border-purple-100/50 hover:bg-purple-50/15'
  },
  {
    title: '活动方案生成',
    desc: '根据需求生成活动方案和流程',
    prompt: '请帮我生成一份针对大二学年的心理主题班会活动策划方案。',
    color: 'border-pink-100/50 hover:bg-pink-50/15'
  }
]

// Chat states
const userInput = ref('')
const isTyping = ref(false)
const chatBox = ref(null)
const showWelcome = ref(true)
const chatMessages = ref([
  { text: '您好，我是您的 AI 助理。我可以帮您分析本班学生特征、生成学业干预方案、或者为您草拟谈心谈话指南。您可以在左侧快捷菜单点选，或在此直接输入您的问题。', isUser: false }
])

const aiChatVisible = ref(false)
const largeChatScrollContainer = ref(null)
const largeChatInputRef = ref(null)

const currentConversationId = ref(null)
const showHistory = ref(false)
const historyList = ref([])

const updateHistoryList = () => {
  try {
    const s = localStorage.getItem('teacher_ai_chat_history')
    if (s) {
      historyList.value = JSON.parse(s)
    } else {
      historyList.value = []
    }
  } catch(e) {
    historyList.value = []
  }
}

const toggleHistory = () => {
  if (!showHistory.value) {
    updateHistoryList()
  }
  showHistory.value = !showHistory.value
}

const clearHistory = () => {
  localStorage.removeItem('teacher_ai_chat_history')
  historyList.value = []
  ElMessage.success('历史记录已清空')
}

const selectHistoryItem = (h) => {
  currentConversationId.value = h.id
  chatMessages.value = JSON.parse(JSON.stringify(h.messages))
  showWelcome.value = false
  showHistory.value = false
  aiChatVisible.value = true
  scrollLargeChatToBottom()
  nextTick(() => {
    largeChatInputRef.value?.focus()
  })
}

const saveCurrentConversationToHistory = () => {
  if (chatMessages.value.length <= 1) return
  if (!currentConversationId.value) {
    currentConversationId.value = Date.now().toString()
  }
  
  let history = []
  try {
    const s = localStorage.getItem('teacher_ai_chat_history')
    if (s) history = JSON.parse(s)
  } catch(e) {}
  
  const existingIdx = history.findIndex(h => h.id === currentConversationId.value)
  const firstUserMsg = chatMessages.value.find(m => m.isUser)?.text || '新对话'
  const title = firstUserMsg.length > 18 ? firstUserMsg.substring(0, 18) + '...' : firstUserMsg
  const timeStr = new Date().toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  
  const entry = {
    id: currentConversationId.value,
    title,
    time: timeStr,
    messages: JSON.parse(JSON.stringify(chatMessages.value))
  }
  
  if (existingIdx !== -1) {
    history[existingIdx] = entry
  } else {
    history.unshift(entry)
    if (history.length > 10) history.pop()
  }
  localStorage.setItem('teacher_ai_chat_history', JSON.stringify(history))
}

// ==================== AI Markdown 渲染器 ====================
const renderMarkdown = (text) => {
  if (!text) return ''
  let html = text
  // 基础转义转 HTML 展现以获得专业格式
  html = html.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  
  // H2-H4 格式化
  html = html.replace(/^#\s+(.*?)$/gm, '<h2 class="text-base font-bold text-indigo-700 mt-4 mb-2 border-b border-indigo-100 pb-1.5 flex items-center">$1</h2>')
  html = html.replace(/^##\s+(.*?)$/gm, '<h3 class="text-sm font-bold text-indigo-600 mt-3 mb-1.5 pb-0.5 border-b border-slate-100 flex items-center">$1</h3>')
  html = html.replace(/^###\s+(.*?)$/gm, '<h4 class="text-xs font-bold text-gray-800 mt-2.5 mb-1 flex items-center">$1</h4>')
  
  // 加粗
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong class="font-bold text-indigo-900">$1</strong>')
  
  // 无序列表与有序列表格式化
  html = html.replace(/^(?:\*|-)\s+(.*?)$/gm, '<div class="flex items-start gap-1.5 my-1 text-[13px] text-gray-600"><span class="text-indigo-500 mt-0.5">•</span><span>$1</span></div>')
  html = html.replace(/^\d+\.\s+(.*?)$/gm, '<div class="flex items-start gap-1.5 my-1 text-[13px] text-gray-600"><span class="text-indigo-500 font-bold text-xs mt-0.5">$0</span><span>$1</span></div>')
  // 修复上一个替换中 $0 包含数字点字符的问题，这里直接给个清爽的行
  html = html.replace(/<span class="text-indigo-500 font-bold text-xs mt-0.5">\d+\.\s+<\/span>/g, '<span class="text-indigo-500 font-bold text-[11px] mt-0.5">▷</span>')
  
  // 段落空行
  html = html.replace(/\n/g, '<br/>')
  return html
}

const scrollLargeChatToBottom = async () => {
  await nextTick()
  if (largeChatScrollContainer.value) {
    largeChatScrollContainer.value.scrollTop = largeChatScrollContainer.value.scrollHeight
  }
}

const openLargeChat = () => {
  aiChatVisible.value = true
  scrollLargeChatToBottom()
  nextTick(() => {
    largeChatInputRef.value?.focus()
  })
}

const resetChat = () => {
  saveCurrentConversationToHistory()
  chatMessages.value = [
    { text: '您好，我是您的 AI 助理。我可以帮您分析本班学生特征、生成学业干预方案、或者为您草拟谈心谈话指南。您可以在左侧快捷菜单点选，或在此直接输入您的问题。', isUser: false }
  ]
  currentConversationId.value = null
}

const closeLargeChat = () => {
  saveCurrentConversationToHistory()
  aiChatVisible.value = false
  showWelcome.value = true
  showHistory.value = false
  currentConversationId.value = null
  chatMessages.value = [
    { text: '您好，我是您的 AI 助理。我可以帮您分析本班学生特征、生成学业干预方案、或者为您草拟谈心谈话指南。您可以在左侧快捷菜单点选，或在此直接输入您的问题。', isUser: false }
  ]
}

// Chart DOM refs
const statusChartRef = ref(null)
const progressChartRef = ref(null)

let statusChartInstance = null
let progressChartInstance = null

// Navigation router helper
const navigateTo = (path) => {
  router.push(path)
}

// Pre-fill prompt click
const selectPrompt = (promptText) => {
  userInput.value = promptText
  showWelcome.value = false
  aiChatVisible.value = true
  sendMessage()
}

// Reset chat pane
const resetToWelcome = () => {
  showWelcome.value = true
}

const scrollToBottom = async () => {
  await nextTick()
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

// AI Message Submission
const sendMessage = async () => {
  if (!userInput.value.trim() || isTyping.value) return
  
  const text = userInput.value
  showWelcome.value = false
  aiChatVisible.value = true
  
  if (!currentConversationId.value) {
    currentConversationId.value = Date.now().toString()
  }
  
  chatMessages.value.push({ text, isUser: true })
  userInput.value = ''
  isTyping.value = true
  scrollToBottom()
  scrollLargeChatToBottom()
  
  try {
    const response = await request.post('/api/ai/chat', { message: text }, {
      timeout: 60000
    })
    if (response.data.code === 200) {
      chatMessages.value.push({ text: response.data.data, isUser: false })
      saveCurrentConversationToHistory()
    } else {
      chatMessages.value.push({ text: 'AI 服务遇到问题：' + response.data.msg, isUser: false })
    }
  } catch (error) {
    chatMessages.value.push({ text: '网络请求超时，请确保后端服务正常运行中。', isUser: false })
  } finally {
    isTyping.value = false
    scrollToBottom()
    scrollLargeChatToBottom()
    nextTick(() => {
      largeChatInputRef.value?.focus()
    })
  }
}

// Donut chart generator
const initStatusChart = () => {
  if (!statusChartRef.value) return
  if (statusChartInstance) {
    statusChartInstance.dispose()
  }
  statusChartInstance = echarts.init(statusChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}人 ({d}%)'
    },
    series: [
      {
        name: '学生状态',
        type: 'pie',
        radius: ['68%', '90%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        labelLine: { show: false },
        data: [
          { value: 186, name: '正常', itemStyle: { color: '#3b82f6' } },
          { value: 32, name: '学业预警', itemStyle: { color: '#f97316' } },
          { value: 18, name: '心理关注', itemStyle: { color: '#a855f7' } },
          { value: 12, name: '经济困难', itemStyle: { color: '#f43f5e' } }
        ]
      }
    ]
  }
  statusChartInstance.setOption(option)
}

// Semi-donut Progress chart generator
const initProgressChart = () => {
  if (!progressChartRef.value) return
  if (progressChartInstance) {
    progressChartInstance.dispose()
  }
  progressChartInstance = echarts.init(progressChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}件 ({d}%)'
    },
    series: [
      {
        name: '事务进度',
        type: 'pie',
        radius: ['68%', '90%'],
        startAngle: 180, // Half circle
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        labelLine: { show: false },
        data: [
          { value: 36, name: '已完成', itemStyle: { color: '#10b981' } },
          { value: 14, name: '进行中', itemStyle: { color: '#3b82f6' } },
          { value: 10, name: '待处理', itemStyle: { color: '#e2e8f0' } }
        ]
      }
    ]
  }
  progressChartInstance.setOption(option)
}

// Handle resizing dynamically
const handleResize = () => {
  if (statusChartInstance) statusChartInstance.resize()
  if (progressChartInstance) progressChartInstance.resize()
}

// Load real statistics
const loadDashboardStats = async () => {
  try {
    const [academicRes, applicationsRes] = await Promise.all([
      request.get('/api/academic/dashboard'),
      request.get('/api/applications/pending')
    ])
    if (academicRes.data.code === 200) {
      const d = academicRes.data.data
      dashboardStats.value.totalStudents = d.totalStudents || 248
      dashboardStats.value.warningCount = d.warningCount || 12
    }
    if (applicationsRes.data.code === 200) {
      dashboardStats.value.pendingLeaves = applicationsRes.data.data.length || 8
    }
  } catch (e) {
    console.warn('Dashboard stats fallback utilized successfully')
  }
}

onMounted(async () => {
  await loadDashboardStats()
  initStatusChart()
  initProgressChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (statusChartInstance) statusChartInstance.dispose()
  if (progressChartInstance) progressChartInstance.dispose()
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
  height: 5px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(99, 102, 241, 0.15);
  border-radius: 99px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(99, 102, 241, 0.3);
}
</style>

<style>
/* ==================== AI 浮动大窗口定制过渡动画与全局覆盖 ==================== */
.ai-chat-drop-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.ai-chat-drop-leave-active {
  transition: all 0.2s ease;
}
.ai-chat-drop-enter-from,
.ai-chat-drop-leave-to {
  opacity: 0;
  transform: translateY(16px) scale(0.95);
  transform-origin: bottom right;
}

.custom-chat-input .el-input__wrapper {
  border-radius: 24px !important;
  box-shadow: 0 0 0 1px var(--el-border-color) inset !important;
  padding-left: 16px !important;
  padding-right: 16px !important;
}
.custom-chat-input .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #38BDF8 inset !important;
}
</style>
