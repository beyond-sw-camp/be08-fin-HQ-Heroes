<template>
    <div class="card mb-0 bg-blue-50 mt-2">
        <div class="flex justify-between mb-0">
            <div>
                <span class="block text-muted-color font-medium mb-4">인사팀 OO님</span>
                <div class="text-surface-900 dark:text-surface-0 font-medium text-l">{{ currentDay }}</div>
                <div class="text-surface-900 dark:text-surface-0 font-medium text-">09 : 00 ~ 18 : 00</div>
            </div>
            <div class="flex items-center justify-center bg-blue-500 rounded-full" style="width: 2.5rem; height: 2.5rem">
                <i class="pi pi-check-circle text-white !text-xl"></i>
            </div>
        </div>
        <button class="mt-3 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">출근하기</button>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const currentDay = ref('');

const getKoreanDate = () => {
    const date = new Date();
    date.setHours(date.getHours() + 9); // UTC+9 for KST
    const dayNames = ['일', '월', '화', '수', '목', '금', '토'];
    const day = dayNames[date.getDay()];
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const dayOfMonth = date.getDate();
    currentDay.value = `${year}/${month}/${dayOfMonth}(${day})`;
};

onMounted(() => {
    getKoreanDate();
});
</script>
