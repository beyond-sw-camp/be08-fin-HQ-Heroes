<template>
    <Dialog 
        :visible.sync="localVisible" 
        header="사원 상세 정보" 
        :modal="true" 
        :closable="true" 
        @update:visible="handleVisibilityUpdate"
        :style="{ width: '500px', maxWidth: '90vw' }"
    >
        <div v-if="employee" class="employee-details">
            <p><strong>이름:</strong> {{ employee.employeeName }}</p>
            <p><strong>부서:</strong> {{ employee.department }}</p>
            <p><strong>팀:</strong> {{ employee.teamName }}</p>
            <p><strong>직책:</strong> {{ employee.position }}</p>
            <p><strong>사번:</strong> {{ employee.employeeId }}</p>
            <p><strong>입사일:</strong> {{ formatDate(new Date(employee.hireDate)) }}</p>
            <p><strong>칭찬 횟수:</strong> {{ employee.praiseCount }}</p>
            <Button     
                label="칭찬하기" 
                @click="praiseEmployee" 
                :disabled="hasPraised"
                class="p-button-success"
            />
        </div>
        <div v-else>
            <p>사원 정보가 없습니다.</p>
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
const hasPraised = ref(false); // 칭찬 여부 상태

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
    () => props.employee,
    (newVal) => {
        employee.value = newVal;
        hasPraised.value = false; // 직원 정보 변경 시 칭찬 초기화
    }
);

function formatDate(date) {
    if (!(date instanceof Date) || isNaN(date.getTime())) {
        return '';
    }
    return date.toLocaleDateString();
}

function praiseEmployee() {
    if (employee.value) {
        employee.value.praiseCount++;
        hasPraised.value = true; // 칭찬한 상태로 변경
    }
}
</script>

<style scoped>
.employee-details {
    padding: 1.5rem;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
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

.p-button-success {
    margin-top: 1rem;
    background-color: #6366F1;
    border-color: #6366F1;
}
</style>
