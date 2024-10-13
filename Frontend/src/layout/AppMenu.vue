<script setup>
import { ref } from 'vue';
import AppMenuItem from './AppMenuItem.vue';
const model = ref([
    {
        label: '관리자',
        items: [
            {
                label: '관리자 기능',
                items: [
                    { label: '사원 정보 수정', icon: 'pi pi-fw pi-user-edit', to: '/update-emp-info' },
                    { label: '공지 사항 작성', icon: 'pi pi-fw pi-user-edit', to: '/write-notice' },
                    { label: '알림 발송', icon: 'pi pi-fw pi-bell', to: '/send-notifications' },
                    { label: '공지 사항 관리', icon: 'pi pi-fw pi-file', to: '/manage-notices' },
                    { label: '교육 관리', icon: 'pi pi-fw pi-book', to: '/manage-education' },
                    { label: '자격증 관리', icon: 'pi pi-fw pi-credit-card', to: '/manage-certifications' },
                    { label: '평가 기준 관리', icon: 'pi pi-fw pi-chart-bar', to: '/manage-evaluation-criteria' }
                ]
            }
        ]
    },
    {
        label: '인사시스템',
        items: [
            { label: 'Home', icon: 'pi pi-fw pi-home', to: '/' },
            {
                label: '인사',
                icon: 'pi pi-fw pi-users',
                items: [
                    { label: '사원 찾기', icon: 'pi pi-fw pi-search', to: '/employeeList' },
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
                            { label: '휴가 신청', icon: 'pi pi-fw pi-calendar-times', to: '/apply-vacation' },
                            { label: '휴가 승인', icon: 'pi pi-fw pi-check-square', to: '/approve-vacation' }
                        ]
                    },
                    { label: '연장 근로 신청', icon: 'pi pi-fw pi-stopwatch', to: '/apply-overtime' },
                    { label: '월 근태 현황', icon: 'pi pi-fw pi-chart-line', to: '/monthly-attendance-status' }
                ]
            },
            {
                label: '급여',
                icon: 'pi pi-fw pi-wallet',
                items: [{ label: '급여 명세서', icon: 'pi pi-fw pi-dollar', to: '/salary-statement' }]
            },
            {
                label: '퇴직',
                icon: 'pi pi-fw pi-power-off',
                items: [{ label: '퇴직금 조회', icon: 'pi pi-fw pi-wallet', to: '/retirement-funds' }]
            },
            {
                label: '교육',
                icon: 'pi pi-fw pi-book',
                items: [
                    { label: '교육 신청', icon: 'pi pi-fw pi-calendar-plus', to: '/education-apply' },
                    { label: '교육 이력', icon: 'pi pi-fw pi-calendar-minus', to: '/education-history' },
                    { label: '자격증 관리', icon: 'pi pi-fw pi-id-card', to: '/certificate-management' }
                ]
            },
            {
                label: '평가',
                icon: 'pi pi-fw pi-chart-bar',
                items: [
                    { label: '평가 수행 결과', icon: 'pi pi-fw pi-chart-line', to: '/evaluation-result' },
                    { label: '팀원 평가', icon: 'pi pi-fw pi-th-large', to: '/evaluation' }
                ]
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
