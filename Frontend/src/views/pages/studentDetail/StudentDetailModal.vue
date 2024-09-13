<template>
    <Dialog 
        :visible.sync="localVisible" 
        header="학생 상세 정보" 
        :modal="true" 
        :closable="true" 
        @update:visible="handleVisibilityUpdate"
        :style="{ width: '500px', maxWidth: '90vw' }"
    >
        <div v-if="student" class="student-details">
            <p><strong>이름:</strong> {{ student.studentName }}</p>
            <p><strong>개강일:</strong> {{ formatDate(new Date(student.openDt)) }}</p>
            <p><strong>강사명:</strong> {{ student.teacher }}</p>
            <!--
            <p><strong>상태:</strong> {{ student.status }}</p>
            <p><strong>출석률:</strong> {{ student.activity }}%</p>
            -->
        </div>
        <div v-else>
            <p>학생 정보가 없습니다.</p>
        </div>
    </Dialog>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
    student: Object,
    visible: Boolean
});

const emit = defineEmits(['update:visible']);

const localVisible = ref(props.visible);
const student = ref(props.student);

// 부모에서 받은 visible 상태를 로컬 상태로 반영
watch(
    () => props.visible,
    (newVal) => {
        localVisible.value = newVal;
    }
);

// 로컬 visible 상태가 변경되면 부모 컴포넌트에 반영
function handleVisibilityUpdate(newVal) {
    localVisible.value = newVal;
    emit('update:visible', newVal);
}

watch(
    () => props.student,
    (newVal) => {
        console.log('학생 데이터:', newVal);
        student.value = newVal;
    }
);

function formatDate(date) {
    if (!(date instanceof Date) || isNaN(date.getTime())) {
        return ''; // 유효하지 않은 날짜일 경우 빈 문자열 반환
    }
    return date.toLocaleDateString(); // 또는 다른 형식의 날짜 문자열로 변경 가능
}

</script>

<style scoped>
.student-details {
    padding: 1rem;
}

.student-details p {
    margin: 0.5rem 0;
    font-size: 1rem;
    line-height: 1.5;
}

.student-details p:not(:last-child) {
    margin-bottom: 1rem;
}

.student-details strong {
    font-weight: 600;
}
</style>
