<template>
  <div class="h-full flex flex-col bg-transparent">
    <!-- Page Header -->
    <div class="mb-6 flex justify-between items-end">
      <div>
        <h2 class="text-[2rem] font-bold text-gray-800 mb-2">上午好，张小明！👋</h2>
        <div class="flex items-center text-sm text-gray-500 gap-4">
          <span class="flex items-center gap-1"><el-icon><Calendar /></el-icon> 今天是 {{ currentDate }}，星期三</span>
          <span class="flex items-center gap-1"><el-icon><Calendar /></el-icon> 距离英语四级还有 <span class="text-blue-500 font-bold mx-1">18</span> 天</span>
        </div>
      </div>
      <div class="flex gap-4">
        <!-- 待办任务 Popover -->
        <el-popover placement="bottom-end" :width="300" trigger="click" popper-class="notification-popover" :hide-after="0" :show-after="0" transition="el-zoom-in-top">
          <template #reference>
            <div class="flex items-center gap-2 bg-white/40 backdrop-blur-md px-4 py-2 rounded-lg border border-white/40 shadow-sm transition-all hover:bg-white/60 cursor-pointer group">
              <el-icon class="text-orange-500 text-lg group-hover:scale-110 transition-transform"><Document /></el-icon>
              <div><div class="text-xl font-bold text-gray-800 leading-none">1</div><div class="text-[10px] text-gray-500 mt-1">待办任务</div></div>
            </div>
          </template>
          <div class="p-2">
            <div class="flex justify-between items-center mb-3 px-2">
              <span class="font-bold text-sm text-gray-800">待办任务</span>
              <span class="text-[10px] text-blue-500 cursor-pointer">全部忽略</span>
            </div>
            <div class="space-y-1">
              <div class="p-2 hover:bg-gray-50 rounded-lg cursor-pointer transition-colors border-b border-gray-50 last:border-0" @click="router.push('/student/applications/scholarship')">
                <div class="flex justify-between mb-1">
                  <span class="text-[10px] font-bold text-orange-500 bg-orange-50 px-1.5 py-0.5 rounded">重要</span>
                  <span class="text-[10px] text-gray-400">刚刚</span>
                </div>
                <div class="text-xs text-gray-700 font-medium truncate">国家励志奖学金申请即将截止</div>
              </div>
            </div>
            <div class="mt-3 pt-2 border-t border-gray-50 text-center">
              <button class="text-xs text-blue-500 font-medium" @click="router.push('/student/applications')">进入任务中心</button>
            </div>
          </div>
        </el-popover>

        <!-- 安全预警 Popover -->
        <el-popover placement="bottom-end" :width="300" trigger="click" popper-class="notification-popover" :hide-after="0" :show-after="0" transition="el-zoom-in-top">
          <template #reference>
            <div class="flex items-center gap-2 bg-white/40 backdrop-blur-md px-4 py-2 rounded-lg border border-white/40 shadow-sm transition-all hover:bg-white/60 cursor-pointer group">
              <el-icon class="text-green-500 text-lg group-hover:scale-110 transition-transform"><CircleCheck /></el-icon>
              <div><div class="text-xl font-bold text-gray-800 leading-none">0</div><div class="text-[10px] text-gray-500 mt-1">安全预警</div></div>
            </div>
          </template>
          <div class="p-4 text-center">
            <el-icon class="text-4xl text-green-500 mb-2"><CircleCheck /></el-icon>
            <p class="text-sm text-gray-600 font-medium">当前校园环境安全</p>
            <p class="text-[10px] text-gray-400 mt-1">暂无异常预警信息</p>
          </div>
        </el-popover>

        <!-- 材料待提交 Popover -->
        <el-popover placement="bottom-end" :width="300" trigger="click" popper-class="notification-popover" :hide-after="0" :show-after="0" transition="el-zoom-in-top">
          <template #reference>
            <div class="flex items-center gap-2 bg-white/40 backdrop-blur-md px-4 py-2 rounded-lg border border-white/40 shadow-sm transition-all hover:bg-white/60 cursor-pointer group">
              <el-icon class="text-purple-500 text-lg group-hover:scale-110 transition-transform"><UploadFilled /></el-icon>
              <div><div class="text-xl font-bold text-gray-800 leading-none">2</div><div class="text-[10px] text-gray-500 mt-1">材料待提交</div></div>
            </div>
          </template>
          <div class="p-2">
            <div class="flex justify-between items-center mb-3 px-2">
              <span class="font-bold text-sm text-gray-800">待提交材料</span>
            </div>
            <div class="space-y-2">
              <div class="p-2 hover:bg-gray-50 rounded-lg cursor-pointer transition-colors border border-gray-100 shadow-sm" @click="router.push('/student/profile')">
                <div class="text-xs text-gray-800 font-bold mb-1">生源地贷款确认书</div>
                <div class="flex justify-between items-center">
                  <span class="text-[10px] text-gray-400">截止日期: 05-15</span>
                  <span class="text-[10px] text-purple-500 font-bold">去上传 ></span>
                </div>
              </div>
              <div class="p-2 hover:bg-gray-50 rounded-lg cursor-pointer transition-colors border border-gray-100 shadow-sm" @click="router.push('/student/profile')">
                <div class="text-xs text-gray-800 font-bold mb-1">学籍信息核对单</div>
                <div class="flex justify-between items-center">
                  <span class="text-[10px] text-gray-400">截止日期: 05-20</span>
                  <span class="text-[10px] text-purple-500 font-bold">去确认 ></span>
                </div>
              </div>
            </div>
          </div>
        </el-popover>
      </div>
    </div>

    <!-- Bento Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-6 auto-rows-min pb-6">
      
      <!-- 1. Profile Info -->
      <div class="md:col-span-5 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col justify-between hover:bg-white/50 transition-all">
        <div class="flex gap-6 mb-4">
          <div class="w-24 h-24 rounded-full overflow-hidden flex-shrink-0 bg-gray-100 border border-gray-200">
            <img src="https://i.pravatar.cc/150?u=student1" class="w-full h-full object-cover">
          </div>
          <div class="flex-1">
            <div class="flex justify-between items-center mb-2">
              <div class="flex items-baseline gap-2">
                <h3 class="text-xl font-bold text-gray-800">张小明</h3>
              </div>
              <button @click="openProfile" class="text-blue-500 text-xs flex items-center hover:text-blue-600 bg-blue-50 px-2 py-1 rounded-full">
                查看完整信息 <el-icon class="ml-1"><ArrowRight /></el-icon>
              </button>
            </div>
            <div class="text-sm text-gray-500 mb-2 font-medium">学号: 202301042</div>
            <p class="text-sm text-gray-600 mb-3">计算机学院 | 软件工程 | 软工2班</p>
            <div class="text-xs text-gray-500 flex flex-col gap-1.5">
              <span class="flex items-center gap-1"><el-icon><Location /></el-icon> 兰苑3栋 402</span>
              <span class="flex items-center gap-1"><el-icon><Phone /></el-icon> 138-xxxx-8888</span>
            </div>
          </div>
        </div>
        <div class="flex gap-2 flex-wrap pt-4 border-t border-gray-50">
          <span class="px-2 py-1 bg-red-50 text-red-600 rounded text-[10px] font-medium border border-red-100">中共党员</span>
          <span class="px-2 py-1 bg-blue-50 text-blue-600 rounded text-[10px] font-medium border border-blue-100">困难生</span>
          <span class="px-2 py-1 bg-green-50 text-green-600 rounded text-[10px] font-medium border border-green-100">篮球队</span>
          <span class="px-2 py-1 bg-purple-50 text-purple-600 rounded text-[10px] font-medium border border-purple-100">ACM成员</span>
          <span class="px-2 py-1 bg-orange-50 text-orange-600 rounded text-[10px] font-medium border border-orange-100">2025届</span>
        </div>
      </div>

      <!-- 2. Academic Growth Data -->
      <div class="md:col-span-7 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col justify-between hover:bg-white/50 transition-all">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-base font-bold text-gray-800">本学期学业数据</h3>
          <button @click="router.push('/student/academic')" class="text-blue-500 text-xs hover:text-blue-600 font-medium bg-blue-50/50 px-3 py-1 rounded-full border border-blue-100/50 transition-all flex items-center gap-1">
            查看详细成绩信息 <el-icon><ArrowRight /></el-icon>
          </button>
        </div>
        
        <div class="grid grid-cols-4 gap-2 mb-4 items-start">
          <div class="p-3">
            <div class="text-sm text-blue-500 font-bold mb-1">GPA</div>
            <div class="text-[2.8rem] font-bold text-blue-600 leading-none mb-1">{{ academicStats[selectedSemester].gpa }}</div>
            <div class="text-xs text-gray-500 mb-0.5">专业排名 {{ academicStats[selectedSemester].rank }}</div>
            <div class="text-xs text-green-500 font-bold">↑ 2名</div>
          </div>
          <div class="p-3">
            <div class="text-sm text-gray-500 font-bold mb-1">第二课堂</div>
            <div class="text-[2.8rem] font-bold text-blue-500 leading-none mb-1">{{ academicStats[selectedSemester].secondClass }} <span class="text-sm font-normal text-gray-500">学时</span></div>
            <div class="text-xs text-blue-500">完成度 {{ academicStats[selectedSemester].secondClassRate }}%</div>
          </div>
          <div class="p-3">
            <div class="text-sm text-gray-500 font-bold mb-1">获奖次数</div>
            <div class="text-[2.8rem] font-bold text-blue-500 leading-none mb-1">{{ academicStats[selectedSemester].awards }} <span class="text-sm font-normal text-gray-500">次</span></div>
            <div class="text-xs text-gray-500">校级以上 <span class="text-blue-500">{{ academicStats[selectedSemester].schoolAwards }}</span> 次</div>
          </div>
          <div class="p-3 rounded-xl transition-all" :class="failCount >= 1 ? 'bg-red-50/50 border border-red-100' : ''">
            <div class="text-sm text-gray-500 font-bold mb-1">挂科预警</div>
            <div class="text-[2.8rem] font-bold leading-none mb-1" :class="failCount >= 1 ? 'text-red-600' : 'text-gray-800'">{{ failCount }} <span class="text-sm font-normal text-gray-500">门</span></div>
            <div class="text-sm font-bold" :class="failCount >= 1 ? 'text-red-500' : 'text-green-500'">{{ failCount >= 1 ? '需补考/重修' : '安全' }}</div>
          </div>
        </div>

        <div class="grid grid-cols-5 gap-2 border-t border-gray-100 pt-3 mt-auto">
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">必修课完成率</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].requiredRate }}%</div>
            <div class="text-xs text-green-500 font-medium">优秀</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">选修课学分</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].electiveCredits }}</div>
            <div class="text-xs text-green-500 font-medium">已获得</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">综合测评排名</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].comprehensiveRank }}<span class="text-xs font-normal text-gray-500">/150</span></div>
            <div class="text-xs text-green-500 font-medium">优秀</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">体测成绩</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].physicalScore }}<span class="text-xs font-normal text-gray-500">分</span></div>
            <div class="text-xs text-blue-500 font-medium">良好</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500 mb-1">英语水平</div>
            <div class="text-lg font-bold text-gray-800">{{ academicStats[selectedSemester].englishLevel }}</div>
            <div class="text-xs text-green-500 font-medium">优秀</div>
          </div>
        </div>
      </div>

      <!-- 3. Dynamic & Tasks -->
      <div class="md:col-span-4 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col justify-between h-[360px] hover:bg-white/50 transition-all">
        <h3 class="text-base font-bold text-gray-800 mb-4">进展与待办</h3>
        <div class="flex-1 flex flex-col justify-between">
          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-orange-50 text-orange-500 flex items-center justify-center text-lg">
                <el-icon><Medal /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">国家励志奖学金申请</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-12</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-blue-50 text-blue-600 px-2.5 py-1 rounded-full border border-blue-100">已开始</span>
          </div>

          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-blue-50 text-blue-500 flex items-center justify-center text-lg">
                <el-icon><EditPen /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">中期鉴定填写</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-15</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-red-50 text-red-600 px-2.5 py-1 rounded-full border border-red-100">待完成</span>
          </div>

          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-green-50 text-green-500 flex items-center justify-center text-lg">
                <el-icon><Warning /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">安全知识考试</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-10</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-red-50 text-red-600 px-2.5 py-1 rounded-full border border-red-100">待完成</span>
          </div>

          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-purple-50 text-purple-500 flex items-center justify-center text-lg">
                <el-icon><Stamp /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">第二课堂学时认证</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-08</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-blue-50 text-blue-600 px-2.5 py-1 rounded-full border border-blue-100">已开始</span>
          </div>

          <div class="flex justify-between items-center">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-pink-50 text-pink-500 flex items-center justify-center text-lg">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div>
                <h4 class="font-medium text-sm text-gray-800">辅导员消息回复</h4>
                <p class="text-[10px] text-gray-400 mt-0.5">提交时间: 2026-05-06</p>
              </div>
            </div>
            <span class="text-[10px] font-bold bg-blue-50 text-blue-600 px-2.5 py-1 rounded-full border border-blue-100">已开始</span>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-50 text-center">
          <el-popover placement="top" :width="500" trigger="click">
            <template #reference>
              <button class="text-blue-500 text-xs hover:text-blue-600 font-medium">查看全部待办 (6) <el-icon class="align-middle"><ArrowRight /></el-icon></button>
            </template>
            <div class="p-4">
              <div class="text-base font-bold text-gray-800 mb-4 px-1">任务中心详情</div>
              <div class="grid grid-cols-2 gap-4 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
                <div class="p-3 bg-orange-50/30 rounded-xl border border-orange-100 hover:bg-orange-50/50 transition-all cursor-pointer group">
                  <div class="flex items-center gap-2 mb-2">
                    <el-icon class="text-orange-500"><Medal /></el-icon>
                    <span class="text-xs font-bold text-gray-800">奖学金申请</span>
                    <span class="ml-auto text-[10px] text-orange-600 font-medium">待提交</span>
                  </div>
                  <p class="text-[11px] text-gray-500 leading-relaxed mb-2">国家励志奖学金申请即将截止，请尽快完成材料上传。</p>
                  <div class="text-[10px] text-gray-400">截止时间: 2026-05-12</div>
                </div>
                
                <div class="p-3 bg-blue-50/30 rounded-xl border border-blue-100 hover:bg-blue-50/50 transition-all cursor-pointer group" @click="router.push('/student/profile')">
                  <div class="flex items-center gap-2 mb-2">
                    <el-icon class="text-blue-500"><EditPen /></el-icon>
                    <span class="text-xs font-bold text-gray-800">中期鉴定</span>
                    <span class="ml-auto text-[10px] text-blue-600 font-medium">进行中</span>
                  </div>
                  <p class="text-[11px] text-gray-500 leading-relaxed mb-2">请完成个人学年总结并提交辅导员审核。</p>
                  <div class="text-[10px] text-gray-400">截止时间: 2026-05-15</div>
                </div>

                <div class="p-3 bg-red-50/30 rounded-xl border border-red-100 hover:bg-red-50/50 transition-all cursor-pointer group">
                  <div class="flex items-center gap-2 mb-2">
                    <el-icon class="text-red-500"><Warning /></el-icon>
                    <span class="text-xs font-bold text-gray-800">安全知识考试</span>
                    <span class="ml-auto text-[10px] text-red-600 font-medium">未开始</span>
                  </div>
                  <p class="text-[11px] text-gray-500 leading-relaxed mb-2">全校性安全意识测评，人人必做。</p>
                  <div class="text-[10px] text-gray-400">截止时间: 2026-05-10</div>
                </div>

                <div class="p-3 bg-purple-50/30 rounded-xl border border-purple-100 hover:bg-purple-50/50 transition-all cursor-pointer group">
                  <div class="flex items-center gap-2 mb-2">
                    <el-icon class="text-purple-500"><Stamp /></el-icon>
                    <span class="text-xs font-bold text-gray-800">学时认证</span>
                    <span class="ml-auto text-[10px] text-purple-600 font-medium">待补正</span>
                  </div>
                  <p class="text-[11px] text-gray-500 leading-relaxed mb-2">您的第二课堂学时认证申请被退回，请修改。</p>
                  <div class="text-[10px] text-gray-400">更新时间: 2026-05-08</div>
                </div>
              </div>
              <div class="mt-4 pt-3 border-t border-gray-100 flex justify-between items-center">
                <span class="text-[11px] text-gray-400">当前共有 6 项待办任务</span>
                <button class="text-xs text-blue-500 font-bold hover:underline" @click="router.push('/student/applications')">进入任务中心详情页</button>
              </div>
            </div>
          </el-popover>
        </div>
      </div>

      <!-- 4. Quick Access -->
      <div class="md:col-span-4 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col h-[360px] hover:bg-white/50 transition-all">
        <h3 class="text-base font-bold text-gray-800 mb-8">快捷入口</h3>
        <div class="grid grid-cols-2 gap-3 flex-1">
          <div @click="router.push('/student/applications/leave')" class="bg-blue-50/50 p-3 rounded-xl border border-blue-100/50 flex flex-col justify-between cursor-pointer hover:bg-blue-100/50 transition-all group">
            <el-icon class="text-blue-500 text-xl"><Calendar /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">请假报备</div>
              <div class="text-[10px] text-gray-400 group-hover:text-blue-500 transition-colors">在线审批</div>
            </div>
          </div>
          <div @click="router.push('/student/applications/scholarship')" class="bg-orange-50/50 p-3 rounded-xl border border-orange-100/50 flex flex-col justify-between cursor-pointer hover:bg-orange-100/50 transition-all group">
            <el-icon class="text-orange-500 text-xl"><Trophy /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">奖助学金</div>
              <div class="text-[10px] text-gray-400 group-hover:text-orange-500 transition-colors">资助在线申请</div>
            </div>
          </div>
          <div @click="router.push('/student/academic')" class="bg-green-50/50 p-3 rounded-xl border border-green-100/50 flex flex-col justify-between cursor-pointer hover:bg-green-100/50 transition-all group">
            <el-icon class="text-green-500 text-xl"><Reading /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">学业与第二课堂</div>
              <div class="text-[10px] text-gray-400 group-hover:text-green-500 transition-colors">成绩与素质分</div>
            </div>
          </div>
          <div @click="router.push('/student/career-plan')" class="bg-purple-50/50 p-3 rounded-xl border border-purple-100/50 flex flex-col justify-between cursor-pointer hover:bg-purple-100/50 transition-all group">
            <el-icon class="text-purple-500 text-xl"><MagicStick /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">职业规划</div>
              <div class="text-[10px] text-purple-500 font-bold">AI 分身已就绪</div>
            </div>
          </div>
          <div @click="router.push('/student/campus-life')" class="bg-gray-50/50 p-3 rounded-xl border border-gray-100/50 flex flex-col justify-between cursor-pointer hover:bg-gray-100/50 transition-all group col-span-2 flex-row items-center gap-4">
            <el-icon class="text-gray-500 text-xl"><School /></el-icon>
            <div>
              <div class="text-xs font-bold text-gray-800 mb-0.5">通知与沟通</div>
              <div class="text-[10px] text-gray-400 group-hover:text-gray-600 transition-colors">查寝、安全公告、生活服务</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 5. Notifications -->
      <div class="md:col-span-4 bg-white/30 backdrop-blur-xl rounded-xl p-6 shadow-sm border border-white/40 flex flex-col h-[360px] hover:bg-white/50 transition-all">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-base font-bold text-gray-800">学校通知</h3>
          <button @click="router.push('/student/campus-life')" class="text-blue-500 text-xs hover:text-blue-600 font-medium">更多 <el-icon class="align-middle"><ArrowRight /></el-icon></button>
        </div>
        <div class="space-y-3 mb-6">
          <div class="flex justify-between items-center group cursor-pointer">
            <div class="flex items-center gap-2 truncate pr-2">
              <span class="px-1.5 py-0.5 bg-blue-50 text-blue-500 rounded text-[10px] flex-shrink-0">学校</span>
              <span class="text-sm text-gray-700 truncate group-hover:text-blue-500">关于五一劳动节放假安排的通知</span>
            </div>
            <span class="text-xs text-gray-400 flex-shrink-0">05-06</span>
          </div>
          <div class="flex justify-between items-center group cursor-pointer">
            <div class="flex items-center gap-2 truncate pr-2">
              <span class="px-1.5 py-0.5 bg-blue-50 text-blue-500 rounded text-[10px] flex-shrink-0">学校</span>
              <span class="text-sm text-gray-700 truncate group-hover:text-blue-500">2025年国家奖学金评审结果公示</span>
            </div>
            <span class="text-xs text-gray-400 flex-shrink-0">05-05</span>
          </div>
          <div class="flex justify-between items-center group cursor-pointer">
            <div class="flex items-center gap-2 truncate pr-2">
              <span class="px-1.5 py-0.5 bg-blue-50 text-blue-500 rounded text-[10px] flex-shrink-0">学校</span>
              <span class="text-sm text-gray-700 truncate group-hover:text-blue-500">大学英语四、六级考试报名通知</span>
            </div>
            <span class="text-xs text-gray-400 flex-shrink-0">05-04</span>
          </div>
          <div class="flex justify-between items-center group cursor-pointer">
            <div class="flex items-center gap-2 truncate pr-2">
              <span class="px-1.5 py-0.5 bg-blue-50 text-blue-500 rounded text-[10px] flex-shrink-0">学校</span>
              <span class="text-sm text-gray-700 truncate group-hover:text-blue-500">图书馆座位预约系统上线通知</span>
            </div>
            <span class="text-xs text-gray-400 flex-shrink-0">05-03</span>
          </div>
        </div>

        <div class="flex justify-between items-center mb-4">
          <h3 class="text-base font-bold text-gray-800">班级通知</h3>
          <button @click="router.push('/student/campus-life')" class="text-blue-500 text-xs hover:text-blue-600 font-medium">更多 <el-icon class="align-middle"><ArrowRight /></el-icon></button>
        </div>
        <div class="space-y-3 pb-4">
          <div class="flex justify-between items-center group cursor-pointer" @click="router.push('/student/campus-life')">
            <div class="flex items-center gap-2 truncate pr-2">
              <span class="px-1.5 py-0.5 bg-green-50 text-green-500 rounded text-[10px] flex-shrink-0">班级</span>
              <span class="text-sm text-gray-700 truncate group-hover:text-blue-500 font-medium">本周日晚点名提醒</span>
            </div>
            <span class="text-xs text-gray-400 flex-shrink-0">05-06</span>
          </div>
          <div class="flex justify-between items-center group cursor-pointer" @click="router.push('/student/campus-life')">
            <div class="flex items-center gap-2 truncate pr-2">
              <span class="px-1.5 py-0.5 bg-green-50 text-green-500 rounded text-[10px] flex-shrink-0">班级</span>
              <span class="text-sm text-gray-700 truncate group-hover:text-blue-500 font-medium">毕业生信息采集工作通知</span>
            </div>
            <span class="text-xs text-gray-400 flex-shrink-0">05-05</span>
          </div>
          <div class="flex justify-between items-center group cursor-pointer" @click="router.push('/student/campus-life')">
            <div class="flex items-center gap-2 truncate pr-2">
              <span class="px-1.5 py-0.5 bg-green-50 text-green-500 rounded text-[10px] flex-shrink-0">班级</span>
              <span class="text-sm text-gray-700 truncate group-hover:text-blue-500 font-medium">团课学习第二阶段开始</span>
            </div>
            <span class="text-xs text-gray-400 flex-shrink-0">05-04</span>
          </div>
        </div>
      </div>



    </div>
  </div>
</template>

<script setup>
import { User, Document, Warning, Trophy, Download, UploadFilled, MagicStick, Loading, Position, Money, School, Guide, ChatDotRound, Calendar, ArrowRight, Location, Phone, ArrowDown, EditPen, Reading, CircleCheck, Medal, Stamp } from '@element-plus/icons-vue'
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const failCount = ref(0) // 设置为 0 显示安全，设置为 >=1 显示红色警示
const selectedSemester = ref('大二下学期')

const academicStats = ref({
  '大二下学期': {
    gpa: '3.85',
    rank: '12/150',
    secondClass: '32',
    secondClassRate: '80',
    awards: '6',
    schoolAwards: '4',
    requiredRate: '95',
    electiveCredits: '16',
    comprehensiveRank: '12',
    physicalScore: '90',
    englishLevel: '四级 586'
  },
  '大二上学期': {
    gpa: '3.72',
    rank: '25/150',
    secondClass: '28',
    secondClassRate: '70',
    awards: '4',
    schoolAwards: '2',
    requiredRate: '92',
    electiveCredits: '12',
    comprehensiveRank: '20',
    physicalScore: '88',
    englishLevel: '四级 586'
  }
})

const openProfile = inject('openProfile', () => {
  router.push('/student/profile')
})

const date = new Date()
const currentDate = `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
</script>

<style scoped>
.notification-popover {
  transition: opacity 0.1s ease-out, transform 0.1s ease-out !important;
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(6px); }

</style>
