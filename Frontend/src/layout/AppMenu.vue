<script setup>
import { fetchGet } from '@/views/pages/auth/service/AuthApiService';
import { computed, onMounted, ref } from 'vue';
import AppMenuItem from './AppMenuItem.vue';

const model = ref([
    {
        label: '관리자',
        items: [
            {
                label: '관리자 기능',
                icon: 'pi pi-key',
                items: [
                    { label: '사원 등록', icon: 'pi pi-fw pi-user-plus', to: '/signup' },
                    { label: '사원 정보 수정', icon: 'pi pi-fw pi-user-edit', to: '/update-emp-info' },
                    { label: '교육 관리', icon: 'pi pi-fw pi-book', to: '/manage-education' },
                    { label: '교육/자격증 승인', icon: 'pi pi-fw pi-calendar-minus', to: '/approve-education' },
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
            { label: '공지사항', icon: 'pi pi-fw pi-file', to: '/manage-notices' },
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
                            { label: '휴가 신청 현황', icon: 'pi pi-fw pi-calendar-times', to: '/status-vacation' },
                            { label: '휴가 결재', icon: 'pi pi-fw pi-check-square', to: '/approve-vacation' }
                        ]
                    },
                    {
                        label: '연장 근로',
                        icon: 'pi pi-fw pi-stopwatch',
                        items: [
                            { label: '연장 근로 신청', icon: 'pi pi-fw pi-calendar-times', to: '/apply-overtime' },
                            { label: '연장 근로 신청 현황', icon: 'pi pi-fw pi-calendar-times', to: '/status-overtime' },
                            { label: '연장 근로 결재', icon: 'pi pi-fw pi-check-square', to: '/approve-overtime' }
                        ]
                    },
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
                    { label: '자격증 목록', icon: 'pi pi-fw pi-id-card', to: '/certificate-management' }
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
const role = ref('');
const positionId = ref(0);

// 사용자 role과 positionId를 가져오는 함수
const fetchUserRoleAndPosition = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/role-check');
        role.value = response.role;
        positionId.value = response.positionId;
    } catch (error) {
        console.error('Error fetching role and position:', error);
    }
};

// 메뉴 항목을 필터링하는 함수
const isMenuItemVisible = (item) => {
    // 관리자 메뉴: 관리자만 접근 가능
    if (item.label === '관리자' && role.value !== 'ROLE_ADMIN') {
        return false;
    }

    // // 휴가 결재 및 연장 근로 결재 항목은 ROLE_ADMIN이거나 positionId가 1 이상인 경우만 표시
    // if (item.label === '휴가 결재' || item.label === '연장 근로 결재') {
    //     return positionId.value >= 1 || role.value === 'ROLE_ADMIN';
    // }

    // // 하위 항목이 있을 경우, 하위 항목도 필터링
    // if (item.items && item.items.length) {
    //     item.items = item.items.filter(isMenuItemVisible); // 재귀 호출로 하위 항목 필터링
    //     return item.items.length > 0; // 필터링된 하위 항목이 남아있는 경우만 부모 항목 표시
    // }

    return true;
};
// 재귀적으로 메뉴 항목을 필터링하는 함수
const filterMenuItems = (items) => {
    return items
        .filter((item) => {
            if (item.label === '관리자') {
                return role.value === 'ROLE_ADMIN';
            }
            // '휴가 결재', '연장 근로 결재', '팀원 평가' 항목에 대한 특정 조건 추가
            if (item.label === '휴가 결재' || item.label === '연장 근로 결재' || item.label === '팀원 평가') {
                return positionId.value === 1;
            }

            if (item.label === '평가 수행 결과') {
                return positionId.value === 2;
            }

            return true;
        })
        .map((item) => {
            if (item.items) {
                return { ...item, items: filterMenuItems(item.items) };
            }
            return item;
        })
        .filter((item) => !item.items || item.items.length > 0); // 하위 항목이 없는 경우 항목 숨기기
};

// 계산된 속성으로 필터링된 메뉴 항목 반환
const filteredModel = computed(() => filterMenuItems(model.value));

// 컴포넌트가 마운트될 때 role과 positionId를 가져옴
onMounted(() => {
    fetchUserRoleAndPosition();
});
</script>

<template>
    <ul class="layout-menu">
        <template v-for="(item, i) in filteredModel" :key="item.label">
            <app-menu-item v-if="!item.separator" :item="item" :index="i"></app-menu-item>
            <li v-if="item.separator" class="menu-separator"></li>
        </template>
    </ul>
</template>

<style lang="scss" scoped></style>
