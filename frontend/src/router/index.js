import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/auth/Login.vue')
    },
    {
      path: '/teacher',
      component: () => import('../views/teacher/Layout.vue'),
      children: [
        { path: '', name: 'TeacherDashboard', meta: { title: '学生档案大屏' }, component: () => import('../views/teacher/Dashboard.vue') },
        { path: 'financial-aid', name: 'TeacherFinancialAid', meta: { title: '奖助服务管理' }, component: () => import('../views/teacher/FinancialAid.vue') },
        { path: 'work-study', name: 'TeacherWorkStudy', meta: { title: '勤工助学管理' }, component: () => import('../views/teacher/WorkStudyManagement.vue') },
        { path: 'academic-warning', name: 'TeacherAcademicWarning', meta: { title: '学业预警支持' }, component: () => import('../views/teacher/AcademicWarning.vue') },
        { path: 'career-plan', name: 'TeacherCareerPlan', meta: { title: '沟通互动' }, component: () => import('../views/teacher/CareerPlan.vue') },
        { path: 'safety-monitoring', name: 'TeacherSafetyMonitoring', meta: { title: '安全监控中心' }, component: () => import('../views/teacher/SafetyMonitoring.vue') },
        { path: 'mid-term', name: 'TeacherMidTerm', meta: { title: '中期鉴定管理' }, component: () => import('../views/teacher/MidTermManagement.vue') },
        { path: 'communication', name: 'TeacherCommunication', meta: { title: '校园安全预警 (旧)' }, component: () => import('../views/teacher/Communication.vue') }
      ]
    },
    {
      path: '/student',
      component: () => import('../views/student/StudentLayout.vue'),
      children: [
        { path: '', name: 'StudentDashboard', meta: { title: '主页' }, component: () => import('../views/student/StudentDashboard.vue') },
        { path: 'profile', name: 'StudentProfile', meta: { title: '个人信息' }, component: { render: () => null } },
        { path: 'academic', name: 'StudentAcademic', meta: { title: '学业与第二课堂' }, component: () => import('../views/student/AcademicSecondClass.vue') },
        // “我的申请”子路由 — 扁平化以避免 redirect + children 冲突
        { path: 'applications', redirect: '/student/applications/leave' },
        { path: 'applications/leave', name: 'StudentLeave', meta: { title: '请假报备' }, component: () => import('../views/student/LeaveApplication.vue') },
        { path: 'applications/scholarship', name: 'StudentScholarship', meta: { title: '奖助学金' }, component: () => import('../views/student/ScholarshipApplication.vue') },
        { path: 'applications/work-study', name: 'StudentWorkStudy', meta: { title: '勤工助学' }, component: () => import('../views/student/WorkStudyApplication.vue') },
        { path: 'applications/safety-report', name: 'StudentSafetyReport', meta: { title: '安全异常报备' }, component: () => import('../views/student/SafetyReporting.vue') },
        { path: 'applications/awards', name: 'StudentAwardEntry', meta: { title: '获奖情况录入' }, component: () => import('../views/student/AwardApplication.vue') },
        { path: 'applications/mid-term', name: 'StudentMidTerm', meta: { title: '中期鉴定' }, component: () => import('../views/student/MidTermAppraisal.vue') },
        { path: 'career-plan', name: 'StudentCareerPlan', meta: { title: '职业规划' }, component: () => import('../views/student/CareerPlan.vue') },
        { path: 'campus-life', name: 'StudentCampusLife', meta: { title: '通讯中心' }, component: () => import('../views/student/CampusLife.vue') },
        { path: 'honors', name: 'StudentHonors', meta: { title: '个人荣誉与记录' }, component: () => import('../views/student/HonorsRecords.vue') },
        { path: 'activity-center', name: 'StudentActivityCenter', meta: { title: '活动大厅' }, component: () => import('../views/student/ActivityCenter.vue') }
      ]
    }
  ]
})

// 简单的路由前置守卫模拟鉴权（Vue Router 5 API：返回 true 或路径，不再使用 next()）
router.beforeEach((to) => {
  const role = localStorage.getItem('userRole')

  if (to.path === '/login') {
    return true
  } else if (!role) {
    return '/login'
  } else if (to.path.startsWith('/teacher') && role !== 'teacher') {
    return '/login'
  } else if (to.path.startsWith('/student') && role !== 'student') {
    return '/login'
  } else {
    return true
  }
})

export default router
