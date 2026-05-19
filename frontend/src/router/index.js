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
        { path: 'career-plan', name: 'TeacherCareerPlan', meta: { title: '发布通知' }, component: () => import('../views/teacher/CareerPlan.vue') },
        { path: 'safety-monitoring', name: 'TeacherSafetyMonitoring', meta: { title: '安全监控中心' }, component: () => import('../views/teacher/SafetyMonitoring.vue') },
        { path: 'communication', name: 'TeacherCommunication', meta: { title: '沟通互动' }, component: () => import('../views/teacher/Communication.vue') },
        { path: 'mid-term', name: 'TeacherMidTerm', meta: { title: '中期鉴定收集' }, component: () => import('../views/teacher/MidTermManagement.vue') },
        { path: 'career-appointments', name: 'TeacherCareerAppointments', meta: { title: '职业规划预约' }, component: () => import('../views/teacher/CareerAppointment.vue') }
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
    },
    {
      path: '/admin',
      component: () => import('../views/admin/Layout.vue'),
      children: [
        { path: '', name: 'AdminDashboard', meta: { title: '管理后台首页' }, component: () => import('../views/admin/Dashboard.vue') },
        { path: 'students', name: 'AdminStudents', meta: { title: '学生信息管理' }, component: () => import('../views/admin/StudentManagement.vue') },
        { path: 'grades', name: 'AdminGrades', meta: { title: '成绩信息管理' }, component: () => import('../views/admin/GradeManagement.vue') },
        { path: 'accounts', name: 'AdminAccounts', meta: { title: '账号管理' }, component: () => import('../views/admin/AccountManagement.vue') }
      ]
    },
    {
      path: '/financial',
      component: () => import('../views/financial/Layout.vue'),
      children: [
        { path: '', name: 'FinancialDashboard', meta: { title: '资助管理首页' }, component: () => import('../views/financial/Dashboard.vue') },
        { path: 'scholarships', name: 'FinancialScholarships', meta: { title: '奖助学金审批' }, component: () => import('../views/financial/ScholarshipReview.vue') },
        { path: 'work-study', name: 'FinancialWorkStudy', meta: { title: '勤工助学管理' }, component: () => import('../views/financial/WorkStudyManagement.vue') },
        { path: 'poverty', name: 'FinancialPoverty', meta: { title: '困难认定管理' }, component: () => import('../views/financial/PovertyManagement.vue') }
      ]
    },
    {
      path: '/youth',
      component: () => import('../views/youth/Layout.vue'),
      children: [
        { path: '', name: 'YouthDashboard', meta: { title: '团委管理首页' }, component: () => import('../views/youth/Dashboard.vue') },
        { path: 'second-classroom', name: 'YouthSecondClassroom', meta: { title: '第二课堂学时' }, component: () => import('../views/youth/SecondClassroom.vue') },
        { path: 'activities', name: 'YouthActivities', meta: { title: '活动发布管理' }, component: () => import('../views/youth/ActivityManagement.vue') },
        { path: 'awards', name: 'YouthAwards', meta: { title: '获奖情况审核' }, component: () => import('../views/youth/AwardReview.vue') },
        { path: 'honors', name: 'YouthHonors', meta: { title: '评优评奖管理' }, component: () => import('../views/youth/HonorsManagement.vue') }
      ]
    },
    {
      path: '/academic',
      component: () => import('../views/academic/Layout.vue'),
      children: [
        { path: '', name: 'AcademicDashboard', meta: { title: '教务管理首页' }, component: () => import('../views/academic/Dashboard.vue') },
        { path: 'grades', name: 'AcademicGrades', meta: { title: '成绩管理' }, component: () => import('../views/academic/GradeManagement.vue') },
        { path: 'warnings', name: 'AcademicWarnings', meta: { title: '学业预警' }, component: () => import('../views/academic/AcademicWarning.vue') },
        { path: 'mid-term', name: 'AcademicMidTerm', meta: { title: '中期鉴定管理' }, component: () => import('../views/academic/MidTermManagement.vue') },
        { path: 'courses', name: 'AcademicCourses', meta: { title: '课程管理' }, component: () => import('../views/academic/CourseManagement.vue') }
      ]
    }
  ]
})

// 简单的路由前置守卫模拟鉴权（Vue Router 5 API：返回 true 或路径，不再使用 next()）
router.beforeEach((to) => {
  const role = sessionStorage.getItem('userRole')
  const token = sessionStorage.getItem('token')

  // token 过期或缺失但仍有 role 残留 — 清理并跳转登录
  if (!token && role) {
    sessionStorage.clear()
    return '/login'
  }

  if (to.path === '/login') {
    return true
  } else if (!role) {
    return '/login'
  } else if (to.path.startsWith('/teacher') && role !== 'teacher') {
    return '/login'
  } else if (to.path.startsWith('/student') && role !== 'student') {
    return '/login'
  } else if (to.path.startsWith('/admin') && role !== 'admin') {
    return '/login'
  } else if (to.path.startsWith('/financial') && role !== 'financial') {
    return '/login'
  } else if (to.path.startsWith('/youth') && role !== 'youth') {
    return '/login'
  } else if (to.path.startsWith('/academic') && role !== 'academic') {
    return '/login'
  } else {
    return true
  }
})

export default router
