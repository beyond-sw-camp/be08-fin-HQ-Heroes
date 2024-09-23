<template>
    <div class="grid grid-cols-12 gap-8">
        <div v-for="(item, index) in itemsList" :key="index" class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0 p-6 custom-box">
                <div class="text-left">
                    <!-- 아이콘 박스 -->
                    <div class="flex items-center bg-blue-100 dark:bg-blue-400/10 rounded-border icon-box mb-6">
                        <i :class="item.icon" class="text-blue-500 !text-3xl"></i>
                    </div>
                    <!-- 텍스트 부분 (제목 및 카운트) -->
                    <span class="block text-muted-color font-medium mb-4 text-base">{{ item.title }}</span> <!-- 제목 글자 크기를 줄임 -->
                    <div class="text-surface-900 dark:text-surface-0 font-medium text-lg">{{ item.count }}</div> <!-- 카운트 글자 크기를 줄임 -->
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

// 아이템 리스트 (카운트 및 아이콘 포함)
const itemsList = ref([
    { title: '연차', count: '-2일', icon: 'pi pi-check-circle' },
    { title: '가족돌봄', count: '신청 시 1일 지급', icon: 'pi pi-users' },
    { title: '난임 치료', count: '매년 3일 지급', icon: 'pi pi-lock' },
    { title: '결혼 - 본인', count: '신청 시 2일 지급', icon: 'pi pi-ring' },
    { title: '결혼 - 자녀', count: '신청 시 1일 지급', icon: 'pi pi-child' },
    { title: '리프레시', count: '3년 근속 시 30일 지급', icon: 'pi pi-calendar' },
    { title: '병가', count: '연차 소진 시 60일', icon: 'pi pi-bandage' },
    { title: '비상', count: '신청 시 1일 지급', icon: 'pi pi-exclamation-triangle' },
]);
</script>

<style scoped>
.grid {
    margin-top: 20px;
}

.custom-box {
    border-radius: 16px; /* 박스 둥글게 */
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1); /* 박스 그림자 */
    background-color: #f9f9f9; /* 박스 배경 */
}

.icon-box {
    width: 4rem;  /* 아이콘 박스 크기 */
    height: 4rem;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #ebf8ff; /* 아이콘 박스 배경 */
    border-radius: 0.5rem; /* 네모 박스 둥근 모서리 */
}

.text-muted-color {
    color: #6b7280; /* 회색 텍스트 색상 */
}

.text-surface-900 {
    color: #1f2937; /* 기본 텍스트 색상 */
}

.dark .text-surface-0 {
    color: #f9fafb; /* 다크 모드 텍스트 색상 */
}

/* 아이콘과 텍스트 사이의 간격을 더 넓게 설정 */
.mb-6 {
    margin-bottom: 30px; /* 아이콘과 텍스트 사이 간격 */
}

.text-left {
    text-align: left; /* 왼쪽 정렬 */
}
</style>
