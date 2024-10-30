import AppLayout from '@/layout/AppLayout.vue';
import { useAuthStore } from '@/stores/authStore';
import AdminLoginPage from '@/views/pages/auth/AdminLoginPage.vue';
import LoginPage from '@/views/pages/auth/LoginPage.vue';
import ResetPWPage from '@/views/pages/auth/ResetPWPage.vue';
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
                    path: '/profile',
                    name: 'profile',
                    component: () => import('@/views/pages/profile/ProfilePage.vue')
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
                    path: '/education-history',
                    name: 'education-history',
                    component: () => import('@/views/pages/education/education-management/EducationHistory.vue')
                },
                {
                    path: '/education-apply',
                    name: 'education-apply',
                    component: () => import('@/views/pages/education/education-management/EducationApply.vue')
                },
                {
                    path: '/education-apply/education-detail/:courseId',
                    name: 'educationDetail',
                    component: () => import('@/views/pages/education/education-management/EducationDetail.vue')
                },
                {
                    path: '/approve-education',
                    name: 'approve-education',
                    component: () => import('@/views/pages/education/education-management/ApproveEducation.vue')
                },
                {
                    path: '/certificate-management',
                    name: 'certification',
                    component: () => import('@/views/pages/education/certificate-management/CertificationList.vue')
                },
                {
                    path: '/evaluation',
                    name: 'evaluation',
                    component: () => import('@/views/pages/evaluation/EvaluationListPage.vue')
                },
                {
                    path: '/evaluation-detail/:employeeId',
                    name: 'evaluationDetail',
                    component: () => import('@/views/pages/evaluation/EvaluationDetailPage.vue')
                },
                {
                    path: '/evaluation-result',
                    name: 'evaluationRestul',
                    component: () => import('@/views/pages/evaluation/EvaluationResultPage.vue')
                },
                {
                    path: '/manage-evaluation-criteria',
                    name: 'manageEvaluationCriteria',
                    component: () => import('@/views/pages/admin/ManageEvaluationCriteriaPage.vue')
                },
                {
                    path: '/apply-vacation',
                    name: 'apply-vacation',
                    component: () => import('@/views/pages/HQattendance/vacation/apply-vacation.vue')
                },
                {
                    path: '/status-vacation',
                    name: 'status-vacation',
                    component: () => import('@/views/pages/HQattendance/vacation/status-vacation.vue')
                },
                {
                    path: '/approve-vacation',
                    name: 'approve-leave',
                    component: () => import('@/views/pages/HQattendance/vacation/approve-vacation.vue')
                },
                {
                    path: '/apply-overtime',
                    name: 'apply-overtime',
                    component: () => import('@/views/pages/HQattendance/overtime/apply-overtime.vue')
                },
                {
                    path: '/status-overtime',
                    name: 'status-overtime',
                    component: () => import('@/views/pages/HQattendance/overtime/status-overtime.vue')
                },
                {
                    path: '/approve-overtime',
                    name: 'approve-overtime',
                    component: () => import('@/views/pages/HQattendance/overtime/approve-overtime.vue')
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
                },
                {
                    path: '/write-notice',
                    name: 'write-notice',
                    component: () => import('@/views/pages/admin/NoticeWritePage.vue')
                },
                {
                    path: '/write-education',
                    name: 'write-education',
                    component: () => import('@/views/pages/admin/EducationWritePage.vue')
                },
                {
                    path: '/send-notifications', // 기존 send-notification에서 복수형으로 변경
                    name: 'send-notifications',
                    component: () => import('@/views/pages/notification/SendNotificationPage.vue')
                },
                {
                    path: '/manage-notices',
                    name: 'manage-notices',
                    component: () => import('@/views/pages/admin/ManageNoticePage.vue')
                },
                {
                    path: '/manage-education',
                    name: 'manage-education',
                    component: () => import('@/views/pages/admin/ManageEducationPage.vue')
                },
                {
                    path: '/manage-education/education-detail/:educationId',
                    name: 'manage-education-detail',
                    component: () => import('@/views/pages/admin/ManageEducationDetailPage.vue')
                },
                {
                    path: '/manage-certifications',
                    name: 'manage-certifications',
                    component: () => import('@/views/pages/admin/ManageCertificatePage.vue')
                },
                {
                    path: '/retirement-funds',
                    name: 'retirement-funds',
                    component: () => import('@/views/pages/retirement/retirement-funds.vue')
                },
                {
                    path: '/notice-update/:id',
                    name: 'notice-update',
                    component: () => import('@/views/pages/admin/NoticeUpdatePage.vue')
                },
                {
                    path: '/notice/:id',
                    name: 'notice-detail',
                    component: () => import('@/views/pages/admin/NoticeDetailPage.vue')
                },
                {
                    path: '/education-update/:id',
                    name: 'education-update',
                    component: () => import('@/views/pages/admin/EducationUpdatePage.vue')
                },
                {
                    path: '/signup',
                    name: 'sign-up',
                    component: () => import('@/views/pages/auth/SignUpPage.vue')
                }
            ]
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
            path: '/reset-password',
            component: ResetPWPage
        }
    ]
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    const publicPages = ['/login', '/signup', '/admin-login', '/reset-password'];
    const authRequired = !publicPages.includes(to.path);

    if (authRequired && !authStore.isLoggedIn) {
        return next('/login');
    }
    next();
});

export default router;
