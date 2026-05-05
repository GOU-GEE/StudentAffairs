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
        { path: 'academic-warning', name: 'TeacherAcademicWarning', meta: { title: '学业预警支持' }, component: () => import('../views/teacher/AcademicWarning.vue') },
        { path: 'career-plan', name: 'TeacherCareerPlan', meta: { title: '沟通互动' }, component: () => import('../views/teacher/CareerPlan.vue') },
        { path: 'communication', name: 'TeacherCommunication', meta: { title: '校园安全预警' }, component: () => import('../views/teacher/Communication.vue') }
      ]
    },
    {
      path: '/student',
      component: () => import('../views/student/StudentLayout.vue'),
      children: [
        { path: '', name: 'StudentDashboard', meta: { title: '主页' }, component: () => import('../views/student/StudentDashboard.vue') },
        { path: 'profile', name: 'StudentProfile', meta: { title: '个人信息' }, component: { template: '<div></div>' } },
        { path: 'academic', name: 'StudentAcademic', meta: { title: '学业成绩' }, component: () => import('../views/student/AcademicSecondClass.vue') },
        { path: 'applications', name: 'StudentApplications', meta: { title: '我的申请' }, component: () => import('../views/student/MyApplications.vue') },
        { path: 'career-plan', name: 'StudentCareerPlan', meta: { title: '职业规划' }, component: () => import('../views/student/CareerPlan.vue') },
        { path: 'campus-life', name: 'StudentCampusLife', meta: { title: '校园通知' }, component: () => import('../views/student/CampusLife.vue') }
      ]
    }
  ]
})

// 简单的路由前置守卫模拟鉴权
router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('userRole')
  
  if (to.path === '/login') {
    next()
  } else if (!role) {
    next('/login')
  } else if (to.path.startsWith('/teacher') && role !== 'teacher') {
    next('/login')
  } else if (to.path.startsWith('/student') && role !== 'student') {
    next('/login')
  } else {
    next()
  }
})

export default router
