import AppLayout from '@/layout/AppLayout.vue';
import AdminLoginPage from '@/views/pages/auth/AdminLoginPage.vue';
import LoginPage from '@/views/pages/auth/LoginPage.vue';
import OAuth2Redirect from '@/views/pages/auth/OAuth2Redirect.vue';
import ResetPWPage from '@/views/pages/auth/ResetPWPage.vue';
import SignUpPage from '@/views/pages/auth/SignUpPage.vue';
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: AppLayout,
            children: [
                {
                    path: '/',
                    name: 'main',
                    component: () => import('@/views/pages/main/MainPage.vue')
                },
                {
                    path: '/employeeList',
                    name: 'employeeList',
                    component: () => import('@/views/pages/employeeList/EmployeeListPage.vue')
                },
                {
                    path: '/attendance',
                    name: 'attendance',
                    component: () => import('@/views/pages/attendance/Attendance.vue')
                },
                {
                    path: '/attendance-calendar',
                    name: 'attendance-calendar',
                    component: () => import('@/views/pages/attendance/attendance-calendar/AttendanceCalendar.vue')
                },
                {
                    path: '/education-calendar',
                    name: 'education-calendar',
                    component: () => import('@/views/pages/education/education-calendar/EducationCalendar.vue')
                },
                {
                    path: '/certification',
                    name: 'certification',
                    component: () => import('@/views/pages/education/certificate-management/CertificationList.vue')
                },
                {
                    path: '/feedback',
                    name: 'feedback',
                    component: () => import('@/views/pages/feedback/FeedBackPage.vue')
                },
                {
                    path: '/feedback-result',
                    name: 'feedbackRestul',
                    component: () => import('@/views/pages/feedback/FeedBackResultPage.vue')
                },
                {
                    path: '/apply-annual-leave',
                    name: 'apply-annual-leave',
                    component: () => import('@/views/pages/HQattendance/vacation/apply-annual-leave.vue')
                },
                {
                    path: '/approve-leave',
                    name: 'approve-leave',
                    component: () => import('@/views/pages/HQattendance/vacation/approve-leave.vue')
                },
                {
                    path: '/apply-overtime',
                    name: 'apply-overtime',
                    component: () => import('@/views/pages/HQattendance/overtime/apply-overtime.vue')
                },
                {
                    path: '/leave-status',
                    name: 'leave-status',
                    component: () => import('@/views/pages/HQattendance/attendanceStatus/leave-status.vue')
                },
                {
                  path: '/monthly-attendance-status',
                  name: 'monthly-attendance-status',
                  component: () => import('@/views/pages/HQattendance/attendanceStatus/monthly-attendance-status.vue')
                },
                {
                  path: '/salary-statement',
                  name: 'salary-statement',
                  component: () => import('@/views/pages/salary/salary-statement.vue')
                },
                {
                  path: '/retire-pay-inquiry',
                  name: 'retire-pay-inquiry',
                  component: () => import('@/views/pages/salary/salary-statement.vue')
                },
                {
                    path: '/update-emp-info',
                    name: 'update-emp-info',
                    component: () => import('@/views/pages/admin/UpdateEmpInfoPage.vue')
                }
            ]
        },
        {
            path: '/oauth2-jwt-header',
            component: OAuth2Redirect
        },
        {
            path: '/login',
            component: LoginPage
        },
        {
            path: '/admin-login',
            component: AdminLoginPage
        },
        {
            path: '/signup',
            component: SignUpPage
        },
        {
            path: '/reset-password',
            component: ResetPWPage
        },
    ]
});

export default router;
