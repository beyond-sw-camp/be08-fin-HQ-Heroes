<template>
    <Dialog 
        :visible.sync="localVisible" 
        header="사원 상세 정보" 
        :modal="true" 
        :closable="true" 
        @update:visible="handleVisibilityUpdate"
        :style="{ width: '500px', maxWidth: '90vw' }"
    >
        <div class="employee-details-container">
            <div class="employee-details">
                <p><strong>이름:</strong> {{ employee.employeeName }}</p>
                <p><strong>부서:</strong> {{ employee.deptName }}</p>
                <p><strong>팀:</strong> {{ employee.teamName }}</p>
                <p><strong>직책:</strong> {{ employee.positionName }}</p>
                <p><strong>사번:</strong> {{ employee.employeeId }}</p>
                <p><strong>입사일:</strong> {{ formatDate(new Date(employee.joinDate)) }}</p>
            </div>
            <div class="photo-upload-container">
                <img :src="photoUrl" alt="증명사진" class="employee-photo">
            </div>
        </div>
    </Dialog>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
    employee: Object,
    visible: Boolean
});

const emit = defineEmits(['update:visible']);

const localVisible = ref(props.visible);
const employee = ref(props.employee);
const photoUrl = ref('https://via.placeholder.com/150'); // 초기 이미지 URL

watch(
    () => props.visible,
    (newVal) => {
        localVisible.value = newVal;
    }
);

function handleVisibilityUpdate(newVal) {
    localVisible.value = newVal;
    emit('update:visible', newVal);
}

watch(
    () => props.employee,
    (newVal) => {
        employee.value = newVal;
    }
);

function formatDate(date) {
    if (!(date instanceof Date) || isNaN(date.getTime())) {
        return '';
    }
    return date.toLocaleDateString();
}
</script>

<style scoped>
.employee-details-container {
    display: flex;
    align-items: center; /* 세로 정렬 */
    padding: 1.5rem; /* 내부 여백 추가 */
    background-color: #ffffff; /* 배경 색상 */
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2); /* 그림자 추가 */
    border-radius: 8px; /* 모서리 둥글게 */
}

.employee-details {
    flex: 1; /* 남은 공간을 차지하게 함 */
}

.photo-upload-container {
    margin-left: 20px; /* 이미지와 텍스트 간격 */
}

.employee-photo {
    width: 150px;
    height: 150px;
    object-fit: cover; /* 이미지 비율 유지 */
    border-radius: 4px; /* 약간 둥글게 */
}

.employee-details p {
    margin: 0.5rem 0;
    font-size: 1rem;
    line-height: 1.6;
    color: #333;
}

strong {
    color: #2c3e50; /* 강조된 텍스트 색상 */
}
</style>
