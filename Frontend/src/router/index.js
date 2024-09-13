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
      ]
    },
    {
      path: '/oauth2-jwt-header',
      component: OAuth2Redirect,
    },
  ]
});

export default router;
