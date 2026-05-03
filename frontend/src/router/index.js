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
        { path: '', name: 'TeacherDashboard', component: () => import('../views/teacher/Dashboard.vue') },
        { path: 'financial-aid', name: 'TeacherFinancialAid', component: () => import('../views/teacher/FinancialAid.vue') },
        { path: 'academic-warning', name: 'TeacherAcademicWarning', component: () => import('../views/teacher/AcademicWarning.vue') },
        { path: 'career-plan', name: 'TeacherCareerPlan', component: () => import('../views/teacher/CareerPlan.vue') },
        { path: 'communication', name: 'TeacherCommunication', component: () => import('../views/teacher/Communication.vue') }
      ]
    },
    {
      path: '/student',
      component: () => import('../views/student/StudentLayout.vue'),
      children: [
        { path: '', name: 'StudentDashboard', component: () => import('../views/student/StudentDashboard.vue') },
        { path: 'academic', name: 'StudentAcademic', component: () => import('../views/student/AcademicSecondClass.vue') },
        { path: 'applications', name: 'StudentApplications', component: () => import('../views/student/MyApplications.vue') },
        { path: 'career-plan', name: 'StudentCareerPlan', component: () => import('../views/student/CareerPlan.vue') },
        { path: 'campus-life', name: 'StudentCampusLife', component: () => import('../views/student/CampusLife.vue') }
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
