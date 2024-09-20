import AppLayout from '@/layout/AppLayout.vue';
import OAuth2Redirect from '@/views/pages/auth/OAuth2Redirect.vue';
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
          path: '/studentlist',
          name: 'studentlist',
          component: () => import('@/views/pages/studentlist/StudentListPage.vue')
        },
        {
          path: '/calendar',
          name: 'calendar',
          component: () => import('@/views/pages/calendar/CalendarPage.vue')
        },
        {
          path: '/attendance',
          name: 'attendance',
          component: () => import('@/views/pages/attendance/Attendance.vue')
        },
        {
          path: '/feedback',
          name: 'feedback',
          component: () => import('@/views/pages/feedback/FeedBack.vue')
        },
        {
          path: '/apply-annual-leave',
          name: 'apply-annual-leave',
          component: () => import('@/views/pages/vacation/apply-annual-leave.vue')
          },
          {
          path: '/approve-leave',
          name: 'approve-leave',
          component: () => import('@/views/pages/vacation/approve-leave.vue')
          },
          {
          path: '/apply-overtime',
          name: 'apply-overtime',
          component: () => import('@/views/pages/overtime/apply-overtime.vue')
          },
          {
          path: '/leave-status',
          name: 'leave-status',
          component: () => import('@/views/pages/AttendanceStatus/leave-status.vue')
          },
          {
          path: '/monthly-attendance-status',
          name: 'monthly-attendance-status',
          component: () => import('@/views/pages/AttendanceStatus/monthly-attendance-status.vue')
          },
      ]
    },
    {
      path: '/oauth2-jwt-header',
      component: OAuth2Redirect,
    },
  ]
});

export default router;
