<template>
  <div class="antialiased min-h-screen flex text-on-surface">
    <!-- 侧边栏 毛玻璃 -->
    <nav class="sidebar-glass hidden md:flex flex-col fixed left-0 top-0 h-full py-8 px-4 w-64 z-50 overflow-y-auto overscroll-contain custom-scrollbar">
      <div class="mb-12 px-2 flex items-center justify-center gap-3">
        <img src="/logosvg.png" alt="Logo" class="w-12 h-12 object-contain">
        <div class="flex flex-col items-center">
          <span class="text-[0.9rem] font-bold tracking-[0.2em] text-on-surface leading-tight text-center">西华师范大学</span>
          <span class="text-[1rem] font-black tracking-[0.15em] text-primary leading-tight mt-0.5 text-center">智慧学工系统</span>
        </div>
      </div>
      <ul class="flex flex-col gap-1 font-medium tracking-tight text-[0.875rem]">
        <li><router-link to="/student" exact exact-active-class="!text-primary font-bold" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-all"><el-icon :size="20"><House /></el-icon>主页</router-link></li>
        <li><router-link to="/student/academic" active-class="!text-primary font-bold" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-all"><el-icon :size="20"><Reading /></el-icon>学业与第二课堂</router-link></li>
        <!-- 我的申请展开菜单 -->
        <li>
          <button @click="appsExpanded = !appsExpanded"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all"
            :class="route.path.startsWith('/student/applications') ? '!text-primary font-bold' : 'text-secondary hover:bg-white/60'">
            <el-icon :size="20"><Document /></el-icon>
            <span class="flex-1 text-left">我的申请</span>
            <el-icon :size="14" class="transition-transform duration-200" :class="appsExpanded ? 'rotate-180' : ''"><ArrowDown /></el-icon>
          </button>
          <transition name="expand">
            <ul v-show="appsExpanded" class="ml-4 mt-1 flex flex-col gap-1">
              <li><router-link to="/student/applications/leave" active-class="!bg-gray-200/60 !text-gray-900 font-bold" class="flex items-center gap-3 px-4 py-2.5 rounded-xl text-secondary hover:bg-white/60 transition-all text-[0.8125rem]">请假报备</router-link></li>
              <li><router-link to="/student/applications/scholarship" active-class="!bg-gray-200/60 !text-gray-900 font-bold" class="flex items-center gap-3 px-4 py-2.5 rounded-xl text-secondary hover:bg-white/60 transition-all text-[0.8125rem]">奖助学金</router-link></li>
              <li><router-link to="/student/applications/work-study" active-class="!bg-gray-200/60 !text-gray-900 font-bold" class="flex items-center gap-3 px-4 py-2.5 rounded-xl text-secondary hover:bg-white/60 transition-all text-[0.8125rem]">勤工助学</router-link></li>
              <li><router-link to="/student/applications/safety-report" active-class="!bg-gray-200/60 !text-gray-900 font-bold" class="flex items-center gap-3 px-4 py-2.5 rounded-xl text-secondary hover:bg-white/60 transition-all text-[0.8125rem]">安全异常报备</router-link></li>
              <li><router-link to="/student/applications/awards" active-class="!bg-gray-200/60 !text-gray-900 font-bold" class="flex items-center gap-3 px-4 py-2.5 rounded-xl text-secondary hover:bg-white/60 transition-all text-[0.8125rem]">获奖情况录入</router-link></li>
              <li><router-link to="/student/applications/mid-term" active-class="!bg-gray-200/60 !text-gray-900 font-bold" class="flex items-center gap-3 px-4 py-2.5 rounded-xl text-secondary hover:bg-white/60 transition-all text-[0.8125rem]">中期鉴定填报</router-link></li>
            </ul>
          </transition>
        </li>
        <li><router-link to="/student/career-plan" active-class="!text-primary font-bold" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-all"><el-icon :size="20"><Guide /></el-icon>职业规划 (AI)</router-link></li>
        <li><router-link to="/student/campus-life" active-class="!text-primary font-bold" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-all"><el-icon :size="20"><School /></el-icon>通讯中心</router-link></li>
        <li><router-link to="/student/activity-center" active-class="!text-primary font-bold" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-all"><el-icon :size="20"><LocationInformation /></el-icon>活动大厅</router-link></li>
        <li><router-link to="/student/honors" active-class="!text-primary font-bold" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-all"><el-icon :size="20"><Medal /></el-icon>个人荣誉与记录</router-link></li>
        <li><router-link to="/student/profile" active-class="!text-primary font-bold" class="flex items-center gap-3 px-4 py-3 rounded-xl text-secondary hover:bg-white/60 transition-all"><el-icon :size="20"><UserFilled /></el-icon>个人信息</router-link></li>
      </ul>
    </nav>

    <!-- 主内容区 -->
    <div class="main-content-wrapper flex-1 ml-0 md:ml-64 flex flex-col min-h-screen overflow-x-auto">
      <!-- Header 毛玻璃 -->
      <header class="flex items-center justify-between px-8 fixed top-0 left-0 md:left-64 right-0 z-40 h-14 bg-white/40 backdrop-blur-xl border-b border-outline-variant/10 font-sans tracking-tight min-w-[900px]">
        <div class="flex items-center gap-6">
          <h2 class="text-2xl font-black text-on-surface tracking-tight whitespace-nowrap">
            {{ route.meta.title || '' }}
          </h2>
        </div>
        <!-- 右侧操作 -->
        <div class="flex items-center gap-4 relative">
          <!-- 通知铃铛 -->
          <div class="relative flex items-center justify-center">
            <button @click.stop="notifOpen = !notifOpen" class="relative text-secondary hover:text-primary transition-colors p-1.5 rounded-lg hover:bg-surface-container-low flex items-center justify-center">
              <el-icon :size="20"><Bell /></el-icon>
              <span v-if="unreadCount > 0" class="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 bg-error rounded-full text-white text-[10px] font-bold flex items-center justify-center px-1 border-2 border-surface">{{ unreadCount }}</span>
            </button>
            <!-- 通知下拉面板 -->
            <transition name="notif-drop">
              <div v-if="notifOpen" class="absolute right-0 top-full mt-3 w-80 bg-surface/95 backdrop-blur-2xl rounded-2xl shadow-2xl border border-outline-variant/20 overflow-hidden z-50">
                <!-- 小箭头 -->
                <div class="absolute -top-2 right-3 w-4 h-4 bg-surface/95 border-l border-t border-outline-variant/20 rotate-45"></div>
                <div class="px-4 py-3.5 border-b border-outline-variant/10 flex items-center justify-between">
                  <span class="font-bold text-sm text-on-surface">站内消息</span>
                  <button @click="markAllRead" class="text-xs text-primary hover:underline font-semibold flex items-center gap-1">
                    <el-icon :size="12"><CircleCheck /></el-icon> 全部已读
                  </button>
                </div>
                <div class="max-h-80 overflow-y-auto">
                  <div v-for="n in notifications" :key="n.id" @click="toggleNotif(n)"
                       class="px-4 py-3 border-b border-outline-variant/8 hover:bg-surface-container-low transition-colors cursor-pointer"
                       :class="!n.read ? 'bg-blue-50/50' : ''">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-[11px] font-bold px-1.5 py-0.5 rounded-md" :class="n.tagStyle">{{ n.tag }}</span>
                      <span class="text-xs text-outline ml-auto">{{ n.time }}</span>
                      <span v-if="!n.read" class="w-2 h-2 rounded-full bg-error flex-shrink-0"></span>
                    </div>
                    <p class="text-sm font-semibold text-on-surface leading-snug">{{ n.title }}</p>
                    <transition name="expand">
                      <p v-if="n.expanded" class="mt-2 text-xs text-secondary leading-relaxed">{{ n.content }}</p>
                    </transition>
                  </div>
                  <div v-if="notifications.length === 0" class="py-8 text-center text-secondary text-sm">暂无通知</div>
                </div>
              </div>
            </transition>
          </div>
          <!-- 设置 -->
          <button @click="showSettings = true" class="text-secondary hover:text-primary transition-colors p-1.5 rounded-lg hover:bg-surface-container-low flex items-center justify-center">
            <el-icon :size="20"><Setting /></el-icon>
          </button>
          <!-- 头像 -->
          <div class="flex items-center gap-2 cursor-pointer group">
            <img src="https://i.pravatar.cc/150?u=student1" class="w-8 h-8 rounded-full border border-outline-variant/30">
            <span class="text-sm font-semibold text-on-surface group-hover:text-primary transition-colors">张小明</span>
          </div>
        </div>
      </header>

      <!-- 主内容：档案页 or 路由页 -->
      <main class="flex-1 p-6 pt-16 lg:p-10 lg:pt-20 max-w-[1600px] w-full mx-auto relative z-10">
        <!-- 个人档案（全页展示） -->
        <transition name="fade">
          <div v-if="route.path === '/student/profile'" class="space-y-4 min-w-[900px]">
            <!-- Top Card: 参考图二，头像在左，信息在右 -->
            <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 p-6 shadow-sm hover:bg-white/50 transition-all">
              <div class="flex gap-5 items-center">
                <!-- 最左列：头像 + 在读标签，上下居中 -->
                <div class="flex flex-col items-center justify-center gap-2 flex-shrink-0 w-[6.5rem]">
                  <img src="https://i.pravatar.cc/150?u=student_profile" class="w-24 h-24 rounded-full object-cover shadow-md ring-2 ring-white">
                  <span class="px-2.5 py-0.5 bg-green-50 text-green-700 text-[0.65rem] font-bold rounded-full border border-green-100">在读</span>
                </div>

                <!-- 右侧内容区 -->
                <div class="flex-1 flex flex-col gap-3">
                  <!-- 第一行：姓名 + 三个按钮 -->
                  <div class="flex items-center justify-between">
                    <h3 class="text-2xl font-bold text-on-surface">{{ profileForm.name }}</h3>
                    <div class="flex gap-2">
                      <button @click="showPrivacy = !showPrivacy" class="px-3 py-1.5 border border-outline-variant/30 rounded-lg text-xs font-semibold text-secondary hover:bg-surface-container-low transition-colors flex items-center gap-1.5">
                        <el-icon><View v-if="!showPrivacy" /><Hide v-else /></el-icon>{{ showPrivacy ? '隐藏私密信息' : '展示私密信息' }}
                      </button>
                      <button @click="isEditingProfile = !isEditingProfile" :class="isEditingProfile ? 'bg-primary text-on-primary-fixed border-primary' : 'border-outline-variant/30 text-secondary hover:bg-surface-container-low'" class="px-3 py-1.5 border rounded-lg text-xs font-semibold transition-colors flex items-center gap-1.5">
                        <el-icon><EditPen /></el-icon>{{ isEditingProfile ? '保存信息' : '编辑信息' }}
                      </button>
                      <button @click="showPdfPreview = true" class="px-3 py-1.5 border border-outline-variant/30 rounded-lg text-xs font-semibold text-secondary hover:bg-surface-container-low transition-colors flex items-center gap-1.5">
                        <el-icon><Printer /></el-icon>打印/导出为PDF
                      </button>
                    </div>
                  </div>

                  <!-- 第二行：左侧竖排4项 + 右侧网格 -->
                  <div class="flex gap-0 border-t border-outline-variant/15 pt-3">
                    <!-- 左侧：学号/身份证/电话/邮箱 竖排 -->
                    <div class="flex flex-col gap-2.5 pr-6 border-r border-outline-variant/15 flex-shrink-0 w-64">
                      <div class="flex items-center gap-2">
                        <el-icon class="text-secondary text-sm flex-shrink-0"><User /></el-icon>
                        <span class="text-[0.65rem] text-secondary w-10 flex-shrink-0">学号</span>
                        <span class="text-sm font-bold">{{ profileForm.studentId }}</span>
                      </div>
                      <div class="flex items-center gap-2">
                        <el-icon class="text-secondary text-sm flex-shrink-0"><Postcard /></el-icon>
                        <span class="text-[0.65rem] text-secondary w-10 flex-shrink-0 leading-[1.1]">身份<br>证号</span>
                        <span class="text-sm font-bold">{{ showPrivacy ? profileForm.idCard : profileForm.idCardMasked }}</span>
                      </div>
                      <div class="flex items-center gap-2 group">
                        <el-icon @click="copyPhone(profileForm.phone)" class="text-secondary text-sm flex-shrink-0 cursor-pointer hover:text-primary transition-colors" title="点击复制"><Phone /></el-icon>
                        <span class="text-[0.65rem] text-secondary w-10 flex-shrink-0">电话</span>
                        <input v-if="isEditingProfile" v-model="profileForm.phone" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 flex-1 outline-none focus:border-primary">
                        <span v-else class="text-sm font-bold">{{ showPrivacy ? profileForm.phone : profileForm.phoneMasked }}</span>
                      </div>
                      <div class="flex items-center gap-2">
                        <el-icon class="text-secondary text-sm flex-shrink-0"><Message /></el-icon>
                        <span class="text-[0.65rem] text-secondary w-10 flex-shrink-0">邮箱</span>
                        <input v-if="isEditingProfile" v-model="profileForm.email" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 flex-1 outline-none focus:border-primary">
                        <span v-else class="text-sm font-bold truncate min-w-0 flex-1" :title="profileForm.email">{{ profileForm.email }}</span>
                      </div>
                    </div>

                    <!-- 右侧：学院/专业/年级班级/校区/宿舍/非集中住宿 网格（3列2行，内部分割线，无外边框） -->
                    <div class="flex-1 grid grid-cols-3 min-w-0">
                      <!-- 第一行 -->
                      <div class="flex items-start gap-2 py-2 px-3 border-b border-r border-outline-variant/15">
                        <el-icon class="text-secondary text-sm mt-0.5 flex-shrink-0"><School /></el-icon>
                        <div><span class="text-[0.6rem] text-secondary block mb-0.5">学院</span><span class="text-sm font-bold leading-snug">{{ profileForm.college }}</span></div>
                      </div>
                      <div class="flex items-start gap-2 py-2 px-3 border-b border-r border-outline-variant/15">
                        <el-icon class="text-secondary text-sm mt-0.5 flex-shrink-0"><Reading /></el-icon>
                        <div><span class="text-[0.6rem] text-secondary block mb-0.5">专业</span><span class="text-sm font-bold leading-snug">{{ profileForm.major }}</span></div>
                      </div>
                      <div class="flex items-start gap-2 py-2 px-3 border-b border-outline-variant/15">
                        <el-icon class="text-secondary text-sm mt-0.5 flex-shrink-0"><CollectionTag /></el-icon>
                        <div><span class="text-[0.6rem] text-secondary block mb-0.5">年级班级</span><span class="text-sm font-bold leading-snug">{{ profileForm.classGrade }}</span></div>
                      </div>
                      <!-- 第二行 -->
                      <div class="flex items-start gap-2 py-2 px-3 border-r border-outline-variant/15">
                        <el-icon class="text-secondary text-sm mt-0.5 flex-shrink-0"><Location /></el-icon>
                        <div><span class="text-[0.6rem] text-secondary block mb-0.5">校区</span><span class="text-sm font-bold leading-snug">{{ profileForm.campus }}</span></div>
                      </div>
                      <div class="flex items-start gap-2 py-2 px-3 border-r border-outline-variant/15">
                        <el-icon class="text-secondary text-sm mt-0.5 flex-shrink-0"><House /></el-icon>
                        <div class="flex-1"><span class="text-[0.6rem] text-secondary block mb-0.5">宿舍</span>
                          <input v-if="isEditingProfile" v-model="profileForm.dorm" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-full outline-none focus:border-primary">
                          <span v-else class="text-sm font-bold leading-snug">{{ profileForm.dorm }}</span>
                        </div>
                      </div>
                      <div class="flex items-start gap-2 py-2 px-3">
                        <el-icon class="text-secondary text-sm mt-0.5 flex-shrink-0"><MapLocation /></el-icon>
                        <div class="flex-1"><span class="text-[0.6rem] text-secondary block mb-0.5">非集中住宿地址</span>
                          <input v-if="isEditingProfile" v-model="profileForm.offCampus" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-full outline-none focus:border-primary">
                          <span v-else class="text-sm font-bold leading-snug">{{ profileForm.offCampus }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Bento Grid: 响应式设计（正常窗口 3 列 Bento 布局，压缩窗口 3 行堆叠布局） -->
            <div class="grid grid-cols-2 lg:grid-cols-3 gap-4 items-start">
              <!-- 健康与个人信息：压缩时全宽(2/2)，正常时占1列并跨2行 -->
              <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 p-5 shadow-sm col-span-2 lg:col-span-1 lg:row-span-2 flex flex-col h-full lg:h-[460px] hover:bg-white/50 transition-all">
                <h4 class="text-sm font-bold text-on-surface mb-4 flex-shrink-0">健康与个人信息</h4>
                <div class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-1 gap-4 overflow-y-auto pr-2 custom-scrollbar flex-1">
                  <div><span class="text-xs text-secondary block mb-1">病史</span>
                    <input v-if="isEditingProfile" v-model="profileForm.medical" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-full outline-none focus:border-primary">
                    <span v-else class="text-sm font-bold">{{ profileForm.medical }}</span>
                  </div>
                  <div><span class="text-xs text-secondary block mb-1">血型</span>
                    <input v-if="isEditingProfile" v-model="profileForm.blood" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-full outline-none focus:border-primary">
                    <span v-else class="text-sm font-bold">{{ profileForm.blood }}</span>
                  </div>
                  <div><span class="text-xs text-secondary block mb-1">性格特点</span>
                    <input v-if="isEditingProfile" v-model="profileForm.personality" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-full outline-none focus:border-primary">
                    <span v-else class="text-sm font-bold">{{ profileForm.personality }}</span>
                  </div>
                  <div class="md:col-span-1 lg:col-span-1"><span class="text-xs text-secondary block mb-1">兴趣爱好</span>
                    <input v-if="isEditingProfile" v-model="profileForm.hobbies" placeholder="用逗号分隔" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-full outline-none focus:border-primary">
                    <div v-else class="flex flex-wrap gap-1.5 mt-1">
                      <span v-for="h in profileForm.hobbies.split(/[,，]/)" :key="h" class="px-2.5 py-0.5 bg-blue-50 text-blue-600 rounded-full text-xs font-semibold">{{ h.trim() }}</span>
                    </div>
                  </div>
                  <div class="md:col-span-2 lg:col-span-1"><span class="text-xs text-secondary block mb-1">职业目标</span>
                    <textarea v-if="isEditingProfile" v-model="profileForm.goal" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-full outline-none focus:border-primary min-h-[60px]"></textarea>
                    <span v-else class="text-sm font-bold leading-relaxed">{{ profileForm.goal }}</span>
                  </div>
                </div>
              </div>

              <!-- 家庭情况 -->
              <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 p-5 shadow-sm flex flex-col h-[222px] col-span-1 hover:bg-white/50 transition-all">
                <h4 class="text-sm font-bold text-on-surface mb-4 flex-shrink-0">家庭情况</h4>
                <div class="space-y-3 overflow-y-auto pr-2 custom-scrollbar flex-1">
                  <div class="flex items-center justify-between p-2.5 bg-surface-container-lowest rounded-xl border border-outline-variant/20">
                    <div class="flex items-center gap-3"><span class="text-xs text-secondary w-8">父亲</span><span class="text-sm font-bold">张大国</span>
                      <input v-if="isEditingProfile" v-model="profileForm.fatherPhone" class="text-sm bg-surface-container-low px-1 rounded border border-outline-variant/30 w-28 outline-none focus:border-primary">
                      <span v-else class="text-sm">{{ showPrivacy ? profileForm.fatherPhone : profileForm.fatherPhoneMasked }}</span>
                    </div>
                    <el-icon @click="copyPhone(profileForm.fatherPhone)" class="text-outline hover:text-primary cursor-pointer transition-colors" title="点击复制"><Phone /></el-icon>
                  </div>
                  <div class="flex items-center justify-between p-2.5 bg-surface-container-lowest rounded-xl border border-outline-variant/20">
                    <div class="flex items-center gap-3"><span class="text-xs text-secondary w-8">母亲</span><span class="text-sm font-bold">李秀梅</span>
                      <input v-if="isEditingProfile" v-model="profileForm.motherPhone" class="text-sm bg-surface-container-low px-1 rounded border border-outline-variant/30 w-28 outline-none focus:border-primary">
                      <span v-else class="text-sm">{{ showPrivacy ? profileForm.motherPhone : profileForm.motherPhoneMasked }}</span>
                    </div>
                    <el-icon @click="copyPhone(profileForm.motherPhone)" class="text-outline hover:text-primary cursor-pointer transition-colors" title="点击复制"><Phone /></el-icon>
                  </div>
                </div>
              </div>

              <!-- 学业与班级信息 -->
              <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 p-5 shadow-sm flex flex-col h-[222px] col-span-1 hover:bg-white/50 transition-all">
                <h4 class="text-sm font-bold text-on-surface mb-4 flex-shrink-0">学业与班级信息</h4>
                <div class="space-y-3 text-sm overflow-y-auto pr-2 custom-scrollbar flex-1">
                  <div class="flex"><span class="text-secondary w-16">学院</span><span class="font-bold">计算机科学学院</span></div>
                  <div class="flex"><span class="text-secondary w-16">专业</span><span class="font-bold">计算机科学与技术</span></div>
                  <div class="flex"><span class="text-secondary w-16">年级班级</span><span class="font-bold">大一 (2023级) 软件工程12班</span></div>
                  <div class="flex items-center mt-1"><span class="text-secondary w-16">班主任</span><span class="font-bold">李老师</span><span class="ml-4 text-xs">{{ showPrivacy ? '15012347777' : '150 **** 7777' }}</span><el-icon @click="copyPhone('15012347777')" class="ml-auto text-outline hover:text-primary cursor-pointer transition-colors" title="点击复制"><Phone /></el-icon></div>
                </div>
              </div>

              <!-- 紧急联系人 -->
              <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 p-5 shadow-sm flex flex-col h-[222px] col-span-1 hover:bg-white/50 transition-all">
                <h4 class="text-sm font-bold text-on-surface mb-4 flex-shrink-0">紧急联系人 (至少2位)</h4>
                <div class="space-y-3 overflow-y-auto pr-2 custom-scrollbar flex-1">
                  <div class="flex flex-wrap items-center justify-between gap-2 p-2.5 bg-surface-container-lowest rounded-xl border border-outline-variant/20">
                    <div class="flex items-center gap-2"><span class="w-5 h-5 rounded-full bg-outline-variant/20 flex items-center justify-center text-xs font-bold text-secondary">1</span>
                      <input v-if="isEditingProfile" v-model="profileForm.emerg1Name" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-24 outline-none focus:border-primary">
                      <span v-else class="text-sm font-bold">{{ profileForm.emerg1Name }}</span>
                      <input v-if="isEditingProfile" v-model="profileForm.emerg1Phone" class="text-xs bg-surface-container-low px-1 rounded border border-outline-variant/30 w-24 outline-none focus:border-primary">
                      <span v-else class="text-xs">{{ showPrivacy ? profileForm.emerg1Phone : profileForm.emerg1PhoneMasked }}</span>
                    </div>
                    <div class="flex items-center gap-2"><el-icon @click="copyPhone(profileForm.emerg1Phone)" class="text-outline hover:text-primary cursor-pointer transition-colors" title="点击复制"><Phone /></el-icon></div>
                  </div>
                  <div class="flex flex-wrap items-center justify-between gap-2 p-2.5 bg-surface-container-lowest rounded-xl border border-outline-variant/20">
                    <div class="flex items-center gap-2"><span class="w-5 h-5 rounded-full bg-outline-variant/20 flex items-center justify-center text-xs font-bold text-secondary">2</span>
                      <input v-if="isEditingProfile" v-model="profileForm.emerg2Name" class="text-sm font-bold bg-surface-container-low px-1 rounded border border-outline-variant/30 w-24 outline-none focus:border-primary">
                      <span v-else class="text-sm font-bold">{{ profileForm.emerg2Name }}</span>
                      <input v-if="isEditingProfile" v-model="profileForm.emerg2Phone" class="text-xs bg-surface-container-low px-1 rounded border border-outline-variant/30 w-24 outline-none focus:border-primary">
                      <span v-else class="text-xs">{{ showPrivacy ? profileForm.emerg2Phone : profileForm.emerg2PhoneMasked }}</span>
                    </div>
                    <div class="flex items-center gap-2"><el-icon @click="copyPhone(profileForm.emerg2Phone)" class="text-outline hover:text-primary cursor-pointer transition-colors" title="点击复制"><Phone /></el-icon></div>
                  </div>
                </div>
              </div>

              <!-- 困难认定等级 -->
              <div class="bg-white/30 backdrop-blur-xl rounded-2xl border border-white/40 p-5 shadow-sm flex flex-col h-[222px] col-span-1 hover:bg-white/50 transition-all">
                <h4 class="text-sm font-bold text-on-surface mb-4 flex-shrink-0">困难认定等级</h4>
                <div class="space-y-4 overflow-y-auto pr-2 custom-scrollbar flex-1">
                  <div class="flex items-center gap-2 mb-3">
                    <span class="text-xs text-secondary">认定级别：</span>
                    <span class="px-3 py-0.5 bg-blue-50 text-blue-600 rounded-full text-xs font-bold border border-blue-100">等级 2</span>
                  </div>
                  <div>
                    <span class="text-xs text-secondary block mb-2">特殊人员证明材料</span>
                    <div class="flex items-center justify-between p-3 border border-outline-variant/30 rounded-xl hover:bg-surface-container-low cursor-pointer transition-colors group">
                      <div class="flex items-center gap-3">
                        <el-icon class="text-red-500 text-2xl"><Document /></el-icon>
                        <div><p class="text-sm font-bold text-on-surface">困难证明材料.pdf</p><p class="text-xs text-secondary">1.2MB</p></div>
                      </div>
                      <el-icon class="text-outline group-hover:text-primary"><Download /></el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </transition>

        <!-- 正常路由页面 -->
        <transition name="fade" mode="out-in">
          <div v-if="route.path !== '/student/profile'">
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" :key="route.fullPath" />
              </transition>
            </router-view>
          </div>
        </transition>
      </main>
    </div>

    <!-- PDF 预览与导出弹窗 -->
    <el-dialog v-model="showPdfPreview" title="个人档案预览" width="800px" align-center destroy-on-close class="pdf-dialog">
      <div id="pdf-printable-area" class="bg-white p-10 text-black mx-auto max-w-[210mm] min-h-[297mm] shadow-lg border border-gray-200">
        <!-- 头部 -->
        <div class="flex items-center gap-6 mb-8">
          <div class="w-24 h-32 bg-gray-200 rounded flex-shrink-0 flex items-center justify-center text-gray-400">照片</div>
          <div class="flex-1">
            <h2 class="text-3xl font-bold mb-4">{{ profileForm.name }}</h2>
            <div class="grid grid-cols-1 gap-y-2 text-sm text-gray-800">
              <p>学号: {{ profileForm.studentId }}</p>
              <p>{{ profileForm.college }} · {{ profileForm.major }}</p>
              <p>电话: {{ profileForm.phone }} &nbsp;|&nbsp; 邮箱: {{ profileForm.email }}</p>
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="mb-6">
          <h3 class="text-lg font-bold border-b border-gray-300 pb-2 mb-4">基本信息</h3>
          <div class="grid grid-cols-2 gap-y-4 text-sm">
            <div><span class="text-gray-500 inline-block w-20">身份证号</span>{{ showPrivacy ? profileForm.idCard : profileForm.idCardMasked }}</div>
            <div><span class="text-gray-500 inline-block w-20">年级班级</span>{{ profileForm.classGrade }}</div>
            <div><span class="text-gray-500 inline-block w-20">校区</span>{{ profileForm.campus }}</div>
            <div><span class="text-gray-500 inline-block w-20">宿舍</span>{{ profileForm.dorm }}</div>
            <div class="col-span-2"><span class="text-gray-500 inline-block w-20">非集中住宿</span>{{ profileForm.offCampus }}</div>
          </div>
        </div>

        <!-- 健康与个人情况 -->
        <div class="mb-6">
          <h3 class="text-lg font-bold border-b border-gray-300 pb-2 mb-4">健康与个人情况</h3>
          <div class="space-y-3 text-sm">
            <div class="flex"><span class="text-gray-500 w-20">病史</span><span class="flex-1">{{ profileForm.medical }}</span></div>
            <div class="flex"><span class="text-gray-500 w-20">血型</span><span class="flex-1">{{ profileForm.blood }}</span></div>
            <div class="flex items-start"><span class="text-gray-500 w-20">兴趣爱好</span>
              <div class="flex gap-2 flex-wrap flex-1">
                <span v-for="h in profileForm.hobbies.split(',')" :key="h" class="px-2 py-0.5 bg-gray-100 rounded text-xs">{{ h.trim() }}</span>
              </div>
            </div>
            <div class="flex"><span class="text-gray-500 w-20">性格特征</span><span class="flex-1">{{ profileForm.personality }}</span></div>
            <div class="flex"><span class="text-gray-500 w-20">职业目标</span><span class="flex-1">{{ profileForm.goal }}</span></div>
          </div>
        </div>

        <!-- 家庭情况 - 2列 -->
        <div class="mb-6">
          <h3 class="text-lg font-bold border-b border-gray-300 pb-2 mb-4">家庭情况</h3>
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div class="flex gap-2"><span class="text-gray-500 w-12">父亲</span><span>张大国 &nbsp; {{ showPrivacy ? profileForm.fatherPhone : profileForm.fatherPhoneMasked }}</span></div>
            <div class="flex gap-2"><span class="text-gray-500 w-12">母亲</span><span>李秀梅 &nbsp; {{ showPrivacy ? profileForm.motherPhone : profileForm.motherPhoneMasked }}</span></div>
          </div>
        </div>

        <!-- 紧急联系人 - 2列 -->
        <div class="mb-6">
          <h3 class="text-lg font-bold border-b border-gray-300 pb-2 mb-4">紧急联系人</h3>
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div class="flex gap-2 items-start">
              <span class="text-gray-500 w-6 flex-shrink-0">1</span>
              <span>{{ profileForm.emerg1Name }} &nbsp; {{ showPrivacy ? profileForm.emerg1Phone : profileForm.emerg1PhoneMasked }}</span>
            </div>
            <div class="flex gap-2 items-start">
              <span class="text-gray-500 w-6 flex-shrink-0">2</span>
              <span>{{ profileForm.emerg2Name }} &nbsp; {{ showPrivacy ? profileForm.emerg2Phone : profileForm.emerg2PhoneMasked }}</span>
            </div>
          </div>
        </div>

        <!-- 行政信息 -->
        <div class="mb-6">
          <h3 class="text-lg font-bold border-b border-gray-300 pb-2 mb-4">行政信息</h3>
          <div class="grid grid-cols-2 gap-3 text-sm">
            <div class="flex gap-3 items-center">
              <span class="text-gray-500 w-16 flex-shrink-0">班主任</span>
              <span>李老师</span>
            </div>
            <div class="flex gap-3 items-center">
              <span class="text-gray-500 w-16 flex-shrink-0">联系方式</span>
              <span>{{ showPrivacy ? '15012347777' : '150 **** 7777' }}</span>
            </div>
          </div>
        </div>

        <!-- 困难认定 -->
        <div class="mb-6">
          <h3 class="text-lg font-bold border-b border-gray-300 pb-2 mb-4">困难认定</h3>
          <div class="space-y-3 text-sm">
            <div class="flex"><span class="text-gray-500 w-20">等级</span><span class="flex-1">二级</span></div>
            <div class="flex"><span class="text-gray-500 w-20">证明材料</span><span class="flex-1">困难证明材料.pdf</span></div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPdfPreview = false">取消</el-button>
          <el-button type="primary" @click="handlePrint">
            打印 / 导出 PDF
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 设置弹窗 -->
    <div v-if="showSettings" class="fixed inset-0 z-[100] flex items-center justify-center" @click.self="showSettings = false">
      <div class="absolute inset-0 bg-black/40 backdrop-blur-sm"></div>
      <div class="relative bg-surface rounded-2xl shadow-2xl w-full max-w-2xl mx-4 overflow-hidden border border-outline-variant/20">
        <div class="flex items-center justify-between px-8 py-5 border-b border-outline-variant/15">
          <h2 class="text-xl font-bold tracking-tight">个人设置</h2>
          <button @click="showSettings = false" class="w-8 h-8 rounded-full bg-surface-container-high flex items-center justify-center text-secondary hover:bg-surface-container-highest transition-colors"><el-icon><Close /></el-icon></button>
        </div>
        <div class="flex min-h-[440px]">
          <div class="w-48 bg-surface-container-lowest border-r border-outline-variant/15 p-4 flex flex-col">
            <p class="text-[0.6875rem] font-bold text-outline uppercase tracking-wider mb-3 px-2">账号</p>
            <button @click="settingsTab='password'" class="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-sm text-left mb-1 transition-colors" :class="settingsTab==='password'?'bg-primary text-on-primary-fixed font-semibold':'text-secondary hover:bg-surface-container-low'">
              <el-icon :size="16"><Lock /></el-icon>密码修改
            </button>
            <div class="mt-auto">
              <button @click="logout" class="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-sm text-left text-error hover:bg-error-container transition-colors">
                <el-icon :size="16"><SwitchButton /></el-icon>退出登录
              </button>
            </div>
          </div>
          <div class="flex-1 p-8">
            <h3 class="text-lg font-bold mb-1">修改密码</h3>
            <p class="text-sm text-secondary mb-6">建议定期修改密码，确保账号安全。</p>
            <div class="space-y-4 max-w-sm">
              <div><label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">当前密码</label><el-input v-model="pwdForm.current" type="password" show-password placeholder="请输入当前密码" /></div>
              <div><label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">新密码</label><el-input v-model="pwdForm.newPwd" type="password" show-password placeholder="至少 8 位" /></div>
              <div><label class="text-xs font-bold text-secondary uppercase tracking-wider block mb-1.5">确认新密码</label><el-input v-model="pwdForm.confirm" type="password" show-password placeholder="再次输入新密码" /></div>
              <button @click="changePwd" class="px-6 py-2.5 bg-primary text-on-primary-fixed rounded-xl text-sm font-bold hover:bg-primary-fixed transition-colors shadow-md">确认修改</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Reading, Document, Guide, School, Bell, Setting, Close, Lock, SwitchButton, ArrowRightBold, Phone, CircleCheck, EditPen, ArrowDown, User, Postcard, Message, Location, House, CollectionTag, MapLocation, Download, Hide, View, Printer, LocationInformation, Medal } from '@element-plus/icons-vue'
import request from '@/utils/request'

const STUDENT_ID = sessionStorage.getItem('userId') || '202301042'

const route = useRoute()
const router = useRouter()
const showSettings = ref(false)
const settingsTab = ref('password')
const notifOpen = ref(false)
const pwdForm = ref({ current: '', newPwd: '', confirm: '' })

// 我的申请子菜单展开状态
const appsExpanded = ref(false)
watch(() => route.path, (p) => {
  if (p.startsWith('/student/applications')) appsExpanded.value = true
}, { immediate: true })

// Route change fixes the sidebar active bug

// Profile interaction state
const isEditingProfile = ref(false)
const showPrivacy = ref(false)
const showPdfPreview = ref(false)

const handlePrint = () => {
  window.print()
}

const profileForm = ref({
  name: '张小明',
  studentId: STUDENT_ID,
  idCard: '440106200403150032',
  idCardMasked: '440**********0032',
  college: '计算机科学学院',
  major: '计算机科学与技术',
  classGrade: '大一 (2023级) 软件工程12班',
  campus: '南校区',
  phone: '13812342024',
  phoneMasked: '138 **** 2024',
  email: 'zhangxm@stu.university.edu',
  dorm: '3号楼 304 室',
  offCampus: '-',
  medical: '无已知过敏史',
  blood: 'A (Rh+)',
  hobbies: '篮球, 编程, 阅读',
  personality: '外向，善于分析',
  goal: '立志成为领先科技公司的 AI 工程师',
  fatherPhone: '13812341024',
  fatherPhoneMasked: '138 **** 1024',
  motherPhone: '13912348823',
  motherPhoneMasked: '139 **** 8823',
  emerg1Name: '张大国 (父亲)',
  emerg1Phone: '13812341024',
  emerg1PhoneMasked: '138 **** 1024',
  emerg2Name: '李老师 (老师)',
  emerg2Phone: '15012347777',
  emerg2PhoneMasked: '150 **** 7777',
})

const copyPhone = (phone) => {
  navigator.clipboard.writeText(phone).then(() => {
    ElMessage.success('复制成功: ' + phone)
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

const notifications = ref([])
const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

const fetchNotifications = async () => {
  try {
    const res = await request.get('/api/communication/announcements')
    if (res.data.code === 200) {
      const readIds = JSON.parse(localStorage.getItem('student_read_notifs') || '[]')
      notifications.value = res.data.data.map(a => {
        const d = new Date(a.publishTime)
        const timeStr = d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
        return {
          id: a.id,
          tag: a.type === 'NOTICE' ? '辅导员通知' : '系统公告',
          tagStyle: a.type === 'NOTICE' ? 'bg-orange-100 text-orange-700' : 'bg-blue-100 text-blue-700',
          time: timeStr,
          title: a.title,
          content: a.content,
          read: readIds.includes(a.id),
          expanded: false,
          path: `/student/campus-life?notifId=${a.id}`
        }
      })
    }
  } catch (e) { console.error('Failed to load notifications', e) }
}

const toggleNotif = (n) => {
  n.expanded = !n.expanded
  if (!n.read) {
    n.read = true
    const readIds = JSON.parse(localStorage.getItem('student_read_notifs') || '[]')
    if (!readIds.includes(n.id)) {
      readIds.push(n.id)
      localStorage.setItem('student_read_notifs', JSON.stringify(readIds))
    }
  }
  if (n.path) {
    if (n.path.includes('?tab=')) {
      const tab = n.path.split('?tab=')[1];
      router.push({ path: n.path.split('?')[0], query: { tab } });
    } else {
      router.push(n.path);
    }
    notifOpen.value = false;
  }
}
const markAllRead = () => {
  notifications.value.forEach(n => n.read = true)
  const allIds = notifications.value.map(n => n.id)
  localStorage.setItem('student_read_notifs', JSON.stringify(allIds))
}

// 点击外部关闭通知
const closeNotif = () => { notifOpen.value = false }
onMounted(() => {
  document.addEventListener('click', closeNotif)
  fetchNotifications()
  // Poll for new notifications
  setInterval(fetchNotifications, 30000)
})
onUnmounted(() => document.removeEventListener('click', closeNotif))

// 路由切换时关闭档案

// 安全删除已废弃的tab逻辑

const changePwd = async () => {
  if (!pwdForm.value.current || !pwdForm.value.newPwd || !pwdForm.value.confirm) { ElMessage.warning('请填写完整的密码信息'); return }
  if (pwdForm.value.newPwd !== pwdForm.value.confirm) { ElMessage.error('两次输入的新密码不一致'); return }
  if (pwdForm.value.newPwd.length < 6) { ElMessage.error('新密码至少需要6位'); return }
  try {
    const res = await request.put('/api/auth/change-password', {
      username: sessionStorage.getItem('userName') || '',
      oldPassword: pwdForm.value.current,
      newPassword: pwdForm.value.newPwd
    })
    if (res.data.code === 200) {
      ElMessage.success('密码修改成功')
      showSettings.value = false
      pwdForm.value = { current: '', newPwd: '', confirm: '' }
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (e) { ElMessage.error('修改失败，请稍后再试') }
}
const logout = () => { 
  sessionStorage.removeItem('userRole'); 
  window.location.href = '/login';
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(6px); }
.notif-drop-enter-active { transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1); }
.notif-drop-leave-active { transition: all 0.15s ease; }
.notif-drop-enter-from, .notif-drop-leave-to { opacity: 0; transform: translateY(-8px) scale(0.96); }
.expand-enter-active, .expand-leave-active { transition: all 0.2s ease; overflow: hidden; }
.expand-enter-from, .expand-leave-to { max-height: 0; opacity: 0; }
.expand-enter-to, .expand-leave-from { max-height: 200px; opacity: 1; }

@media print {
  body * {
    visibility: hidden;
  }
  #pdf-printable-area, #pdf-printable-area * {
    visibility: visible;
  }
  #pdf-printable-area {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    margin: 0;
    padding: 0;
    box-shadow: none;
    border: none;
  }
  /* Hide the rest of the dialog ui */
  .el-dialog__header, .el-dialog__footer {
    display: none !important;
  }
}
</style>
