<template>
    <div class="evaluation-detail">
        <h3>부서 선택</h3>
        <!-- 부서 선택 -->
        <Select class="mb-5" v-model="selectedDepartment" :options="departments" optionLabel="deptName" placeholder="부서를 선택하세요" @change="fetchEvaluationCriteria" />

        <!-- 평가 기준 수정 폼 -->
        <div v-if="evaluationCriteriaList.length > 0" class="evaluation-form">
            <h3>평가 기준 수정</h3>

            <!-- 기준 수정 -->
            <div v-for="(criteria, index) in evaluationCriteriaList" :key="criteria.evaluationCriteriaId" class="evaluation-item">
                <!-- 평가 기준 제목 (수정 불가) -->
                <h3>{{ index + 1 }}. {{ criteria.criteriaTitle }}</h3>

                <!-- 평가 기준 질문 수정 -->
                <div v-for="(question, questionIndex) in criteria.criteriaContent.split('#')" :key="questionIndex" class="evaluation-row">
                    <label :for="'question_' + questionIndex">질문 {{ questionIndex + 1 }}:</label>
                    <InputText :id="'question_' + questionIndex" v-model="criteriaQuestions[index][questionIndex]" class="w-full mb-2" placeholder="질문을 입력하세요" />
                </div>
            </div>

            <!-- 저장 버튼 -->
            <div class="save-button-container">
                <Button label="평가 기준 저장" @click="saveEvaluationCriteria" class="p-button-rounded p-button-primary custom-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Select from 'primevue/select';
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { fetchGet, fetchPut } from '../auth/service/AuthApiService';

// 선택된 부서 정보
const selectedDepartment = ref(null);

// 부서 목록과 평가 기준 데이터
const departments = ref([]);
const evaluationCriteriaList = ref([]);
const updateEvaluationCriteriaList = ref({
    criteriaContent: '',
    criteriaTitle: '',
    deptId: null
});
const criteriaQuestions = ref([]); // 각 평가 항목의 질문을 저장할 배열

// 부서 목록을 가져오는 함수
async function fetchDepartments() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/departments');
        departments.value = response;
    } catch (error) {
        console.error('부서 목록을 가져오는 중 오류 발생:', error);
    }
}

// 평가 기준을 부서별로 가져오는 함수
async function fetchEvaluationCriteria() {
    if (!selectedDepartment.value) return;

    try {
        const response = await fetchGet(`https://hq-heroes-api.com/api/v1/evaluation-criteria/by-department?deptName=${selectedDepartment.value.deptName}`);
        evaluationCriteriaList.value = response;

        // 각 평가 항목의 질문을 기준으로 데이터를 초기화
        criteriaQuestions.value = evaluationCriteriaList.value.map((criteria) => criteria.criteriaContent.split('#'));
    } catch (error) {
        console.error('평가 기준을 가져오는 중 오류 발생:', error);
    }
}

// 수정된 평가 기준을 저장하는 함수
async function saveEvaluationCriteria() {
    try {
        // 각 평가 항목에 대한 수정된 질문 내용을 다시 합쳐서 저장
        evaluationCriteriaList.value.forEach(async (criteria, index) => {
            criteria.criteriaContent = criteriaQuestions.value[index].join('#');

            updateEvaluationCriteriaList.value.criteriaContent = criteria.criteriaContent;
            updateEvaluationCriteriaList.value.criteriaTitle = criteria.criteriaTitle;
            updateEvaluationCriteriaList.value.deptId = criteria.deptId;

            // PUT 요청 URL에 평가 기준 ID를 추가하여 각 항목별로 수정
            const url = `https://hq-heroes-api.com/api/v1/evaluation-criteria/${criteria.evaluationCriteriaId}`;

            // 수정된 평가 기준 데이터를 저장하는 API 호출
            const response = await fetchPut(url, updateEvaluationCriteriaList.value, {
                withCredentials: true,
                headers: {
                    access: window.localStorage.getItem('access'),
                    'Content-Type': 'application/json'
                }
            });
        });

        await Swal.fire({
            title: '평가 기준이 저장되었습니다.',
            icon: 'success'
        });
    } catch (error) {
        await Swal.fire({
            title: '평가 기준이 저장 중 오류가 발생하였습니다.',
            icon: 'error'
        });
    }
}

// 컴포넌트 마운트 시 부서 목록을 불러옵니다.
onMounted(() => {
    fetchDepartments();
});
</script>

<style scoped>
.evaluation-detail {
    padding: 2rem;
    max-width: 900px;
    margin: 0 auto;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    font-family: 'Arial', sans-serif;
}

h3 {
    font-size: 1.5rem;
    color: #444;
    margin-bottom: 1rem;
}

.evaluation-item {
    margin-bottom: 2rem;
}

.evaluation-row {
    display: flex;
    flex-direction: column;
    margin-bottom: 1rem;
}

.custom-button {
    padding: 0.75rem 2rem;
    font-size: 1rem;
    border-radius: 8px;
}

.save-button-container {
    text-align: center;
    margin-top: 2.5rem;
}
</style>
