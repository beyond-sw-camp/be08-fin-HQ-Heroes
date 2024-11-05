<template>
    <div class="evaluation-detail">
        <div class="flex items-center justify-center">
            <h3>팀원 평가</h3>
        </div>

        <div class="employee-info">
            <!-- 프로필 이미지 -->
            <img :src="employeeProfileImageUrl" alt="Profile Image" class="profile-image" />

            <!-- 사원 정보 -->
            <div class="employee-details">
                <p><strong>이름:</strong> {{ employeeName }}</p>
                <p><strong>사번:</strong> {{ employeeId }}</p>
                <p><strong>부서:</strong>{{ employeeDept }}</p>
                <p><strong>팀명:</strong> {{ employeeTeam }}</p>
                <p><strong>직책:</strong> {{ employeePosition }}</p>
                <p><strong>직무:</strong> {{ employeejobRole }}</p>
            </div>
        </div>

        <hr class="divider" />

        <div class="evaluation-form">
            <h3 class="font-bold">평가 기준</h3>
            <div v-for="(criteria, index) in evaluationCriteriaList" :key="criteria.evaluationCriteriaId" class="evaluation-item">
                <h3>{{ index + 1 }}. {{ criteria.criteriaTitle }}</h3>

                <!-- 각 질문과 점수 선택을 같은 행에 배치 -->
                <div v-for="(question, questionIndex) in criteria.criteriaContent.split('#')" :key="questionIndex" class="evaluation-row">
                    <p class="question-text">{{ question }}</p>
                    <Select v-model="criteriaScores[index][questionIndex]" :options="scoreOptions" optionLabel="label" optionValue="value" placeholder="평가 등급" />
                </div>
                <hr class="divider" />
            </div>

            <div class="evaluation-item flex-col comment-item">
                <p>코멘트</p>
                <Textarea v-model="comments" rows="4" class="custom-textarea"></Textarea>
            </div>

            <div class="save-button-container">
                <!-- isEvaluationComplete가 true일 때만 버튼 활성화 -->
                <Button :disabled="!isEvaluationComplete" label="평가 저장" @click="saveEvaluation" class="p-button-rounded p-button-primary custom-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
import router from '@/router';
import Button from 'primevue/button';
import Select from 'primevue/select';
import Textarea from 'primevue/textarea';
import Swal from 'sweetalert2';
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { fetchGet, fetchPost, fetchPut } from '../auth/service/AuthApiService';

const route = useRoute();
const employeeId = route.params.employeeId;

const employeeName = ref('');
const employeePosition = ref('');
const employeejobRole = ref('');
const employeeTeam = ref('');
const employeeDept = ref('');
const employeeProfileImageUrl = ref('');
const comments = ref('');

// 평가 기준 항목 및 점수 관리 변수
const evaluationCriteriaList = ref([]);
const criteriaScores = ref([]); // 2D 배열로 각 질문의 점수를 저장
const score = ref(0); // 평가 점수의 평균을 저장

// 평가 점수 옵션
const scoreOptions = ref([
    { label: 'S (매우 우수)', value: 100 },
    { label: 'A+ (우수)', value: 95 },
    { label: 'A (좋음)', value: 90 },
    { label: 'B+ (보통)', value: 85 },
    { label: 'B (미흡)', value: 80 },
    { label: 'C+ (개선 필요)', value: 75 },
    { label: 'C (부족)', value: 70 }
]);

// 모든 필수 항목이 입력되었는지 확인하는 computed 속성
const isEvaluationComplete = computed(() => {
    return criteriaScores.value.every((questions) => questions.every((score) => score !== null));
});

// 직원 평가 데이터를 로드하는 함수
async function fetchEmployeeEvaluationData() {
    try {
        const employeeData = await fetchGet(`https://hq-heroes-api.com/api/v1/employee/employees/${employeeId}`);
        employeeName.value = employeeData.employeeName;
        employeePosition.value = employeeData.positionName || '팀원';
        employeeDept.value = employeeData.deptName;
        employeeTeam.value = employeeData.teamName;
        employeejobRole.value = employeeData.jobRoleName;
        employeeProfileImageUrl.value = employeeData.profileImageUrl;
        fetchEvaluationCriteria(employeeData.deptName);
    } catch (error) {
        console.error('직원 데이터를 가져오는 중 오류 발생:', error);
    }
}

// Fetch evaluation criteria by department
async function fetchEvaluationCriteria(deptName) {
    try {
        const criteriaData = await fetchGet(`https://hq-heroes-api.com/api/v1/evaluation-criteria/by-department?deptName=${deptName}`);
        evaluationCriteriaList.value = criteriaData;

        criteriaScores.value = criteriaData.map((criteria) => {
            const subQuestions = criteria.criteriaContent.split('#');
            return new Array(subQuestions.length).fill(null);
        });
    } catch (error) {
        console.error('평가 기준 데이터를 가져오는 중 오류 발생:', error);
    }
}

// 평균 점수를 계산하는 함수
function calculateAverageScore() {
    let totalScore = 0;
    let scoreCount = 0;

    // 모든 점수를 합산하고 개수를 계산
    criteriaScores.value.forEach((questions) => {
        questions.forEach((score) => {
            if (score !== null) {
                totalScore += score;
                scoreCount++;
            }
        });
    });

    // 평균 점수 계산
    score.value = scoreCount > 0 ? totalScore / scoreCount : 0;
    score.value = Math.round(score.value);
}

// 한국 시간 기준으로 상반기인지 하반기인지 확인하는 함수
function getKoreanEvaluationPeriod() {
    const now = new Date();
    const koreanTime = new Date(now.setHours(now.getHours() + 9)); // UTC+9 한국 시간으로 변환
    const month = koreanTime.getMonth() + 1; // 월은 0부터 시작하므로 +1
    return month <= 6 ? '상반기' : '하반기';
}

// 평가 저장/수정 함수
async function saveEvaluation() {
    try {
        // 평균 점수 계산
        calculateAverageScore();

        // 평가 데이터 생성
        const evaluationData = {
            employeeId: String(employeeId),
            evaluatorId: window.localStorage.getItem('employeeId'),
            comments: comments.value,
            score: score.value
        };

        // 상반기/하반기 기준 설정
        const evaluationPeriod = getKoreanEvaluationPeriod();

        // 기존 평가 기록 조회 API 호출
        const existingEvaluations = await fetchGet(`https://hq-heroes-api.com/api/v1/evaluation-service/evaluations/existing?employeeId=${evaluationData.employeeId}&evaluatorId=${evaluationData.evaluatorId}&period=${evaluationPeriod}`);

        if (existingEvaluations.length > 0) {
            // 기존 기록이 있을 경우 수정(평가 ID를 이용해 PUT 요청)
            const existingEvaluationId = existingEvaluations[0].evaluationId;
            const response = await fetchPut(`https://hq-heroes-api.com/api/v1/evaluation-service/evaluation/${existingEvaluationId}`, evaluationData, 'PUT');
            if (response) {
                await Swal.fire({
                    title: '평가를 수정하였습니다.',
                    icon: 'success'
                });
                router.go(-1);
            }
        } else {
            // 기존 기록이 없을 경우 새 평가 생성
            const response = await fetchPost('https://hq-heroes-api.com/api/v1/evaluation-service/evaluation', evaluationData, 'POST');
            if (response) {
                await Swal.fire({
                    title: '평가를 완료하였습니다.',
                    icon: 'success'
                });
                router.go(-1);
            }
        }
    } catch (error) {
        await Swal.fire({
            title: '평가 저장중 오류가 발생하였습니다.',
            icon: 'error'
        });
    }
}

onMounted(() => {
    fetchEmployeeEvaluationData();
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

h2 {
    font-size: 2rem;
    color: #333;
    margin-bottom: 1.5rem;
    text-align: center;
}

.employee-info {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6rem; /* 이미지와 텍스트 사이의 간격 */
}

.profile-image {
    width: 100%;
    max-width: 180px; /* 최대 너비를 제한 */
    height: 100%;
    max-height: 180px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #ddd;
}

.employee-details {
    font-size: 1.1rem;
    color: #555;
}

.employee-info p {
    margin-bottom: 0.7rem;
}

.divider {
    border: 0;
    height: 1px;
    background-color: #ddd;
    margin: 2rem 0;
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
    align-items: center;
    margin-bottom: 1rem;
}

.question-text {
    font-size: 1.05rem;
    color: #333;
    flex: 1;
}

.custom-dropdown {
    flex: 0.3;
    min-width: 180px;
    border-radius: 8px;
    border: 1px solid #ccc;
    padding: 0.5rem;
}

.comment-item p {
    font-size: 1.1rem;
    margin-bottom: 0.5rem;
}

.custom-textarea {
    width: 100%;
    padding: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 1rem;
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
