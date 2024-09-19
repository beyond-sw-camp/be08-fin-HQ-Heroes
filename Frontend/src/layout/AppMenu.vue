<script setup>
import { ref } from 'vue';
import AppMenuItem from './AppMenuItem.vue';
const model = ref([
{
        label: '인사시스템',
        items: [
            { 
                label: '인사',
                icon: 'pi pi-fw pi-users',
                items: [
                    { label: '사람찾기', icon: 'pi pi-fw pi-search', to: '/find-people' },
                    { label: '내 프로파일', icon: 'pi pi-fw pi-user', to: '/profile' }
                ]
            },
            {
                label: '근태',
                icon: 'pi pi-fw pi-calendar',
                items: [
                    { label: '근태 캘린더', icon: 'pi pi-fw pi-calendar-plus', to: '/attendance-calendar' },
                    { 
                        label: '휴가',
                        icon: 'pi pi-fw pi-envelope',
                        items: [
                            { label: '연차 휴가 신청', icon: 'pi pi-fw pi-calendar-times', to: '/apply-annual-leave' },
                            { label: '비가동 신청', icon: 'pi pi-fw pi-calendar-minus', to: '/apply-non-operation-leave' },
                            { label: '휴가 승인', icon: 'pi pi-fw pi-check-square', to: '/approve-leave' }
                        ]
                    },
                    { 
                        label: '연장 근로',
                        icon: 'pi pi-fw pi-clock',
                        items: [
                            { label: '연장 근로 신청', icon: 'pi pi-fw pi-clock-plus', to: '/apply-overtime' }
                        ]
                    },
                    { 
                        label: '근태 현황',
                        icon: 'pi pi-fw pi-chart-line',
                        items: [
                            { label: '휴가 현황', icon: 'pi pi-fw pi-calendar-check', to: '/leave-status' },
                            { label: '일 근태 현황', icon: 'pi pi-fw pi-calendar-day', to: '/daily-attendance-status' },
                            { label: '월 근태 현황', icon: 'pi pi-fw pi-calendar-alt', to: '/monthly-attendance-status' }
                        ]
                    }
                ]
            },
            { 
                label: '급여',
                icon: 'pi pi-fw pi-wallet',
                items: [
                    { label: '급여 명세서', icon: 'pi pi-fw pi-dollar', to: '/salary-statement' }
                ]
            },
            { 
                label: '퇴직',
                icon: 'pi pi-fw pi-power-off',
                items: [
                    { label: '퇴직금 조회', icon: 'pi pi-fw pi-wallet', to: '/retirement-funds' }
                ]
            },
            { 
                label: '교육',
                icon: 'pi pi-fw pi-book',
                items: [
                    { label: '교육 현황 및 교육 신청', icon: 'pi pi-fw pi-calendar-plus', to: '/education-list' },
                    { label: '자격증 관리', icon: 'pi pi-fw pi-id-card', to: '/certificate-management' }
                ]
            },
            { 
                label: '평가',
                icon: 'pi pi-fw pi-chart-bar',
                items: [
                    { label: '평가 수행 결과', icon: 'pi pi-fw pi-chart-line', to: '/evaluation-results' },
                    { label: '다면 평가', icon: 'pi pi-fw pi-th-large', to: '/multifaceted-evaluation-template' }
                ]
            }
        ]
    },
    {
        label: 'Home',
        items: [{ label: 'Home', icon: 'pi pi-fw pi-home', to: '/' }]
    },
    {
        label: 'Pages',
        items: [
            { label: 'Attendance', icon: 'pi pi-fw pi-check-square', to: '/attendance' },
            { label: 'Student List', icon: 'pi pi-fw pi-list', to: '/studentlist' },
            { label: 'Calendar', icon: 'pi pi-fw pi-calendar', to: '/calendar' }
        ]
    },
    {
        label: 'Reference',
        items: [
            {
                label: 'Documentation',
                icon: 'pi pi-fw pi-book',
                url: 'https://primevue.org/setup/',
                target: '_blank'
            },
            {
                label: 'View Source',
                icon: 'pi pi-fw pi-github',
                url: 'https://github.com/beyond-sw-camp/be08-4th-1team',
                target: '_blank'
            }
        ]
    }
]);
</script>

<template>
    <ul class="layout-menu">
        <template v-for="(item, i) in model" :key="item">
            <app-menu-item v-if="!item.separator" :item="item" :index="i"></app-menu-item>
            <li v-if="item.separator" class="menu-separator"></li>
        </template>
    </ul>
</template>

<style lang="scss" scoped></style>