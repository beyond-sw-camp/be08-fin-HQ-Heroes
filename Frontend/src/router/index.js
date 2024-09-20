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
          path: '/employeeList',
          name: 'employeeList',
          component: () => import('@/views/pages/employeeList/EmployeeListPage.vue')
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
      ]
    },
    {
      path: '/oauth2-jwt-header',
      component: OAuth2Redirect,
    },
  ]
});

export default router;
