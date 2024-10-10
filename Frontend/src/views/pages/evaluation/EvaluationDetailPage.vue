<template>
    <div>
        <h2>{{ employeeName }}의 평가 상세</h2>
        <p>직급: {{ employeePosition }}</p>
        <p>사번: {{ employeeId }}</p>
        <p>팀명: {{ employeeTeam }}</p>
        <!-- Add any other employee details you want to display -->
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { fetchGet } from '../auth/service/AuthApiService'; // Adjust the path as needed

const route = useRoute();
const employeeId = route.params.employeeId; // Use employeeId from route parameters

const employeeName = ref('');
const employeePosition = ref('');
const employeeTeam = ref(''); // You can add more properties as needed

const API_URL = 'http://localhost:8080'; // Define your API URL

// 직원 평가 데이터를 로드하는 함수
async function fetchEmployeeEvaluationData() {
    try {
        const employeeData = await fetchGet(`${API_URL}/api/v1/employee/employees/${employeeId}`);
        employeeName.value = employeeData.employeeName; // Set employee name
        employeePosition.value = employeeData.positionName || '사원'; // Set position, default to '사원'
        employeeTeam.value = employeeData.teamName; // Set team name, or whatever you have in the API response
    } catch (error) {
        console.error('직원 데이터를 가져오는 중 오류 발생:', error);
    }
}

// 컴포넌트가 마운트될 때 직원 데이터를 가져옵니다.
onMounted(() => {
    fetchEmployeeEvaluationData();
});
</script>

<style scoped lang="scss">
/* Add any styles you need for this component */
</style>
