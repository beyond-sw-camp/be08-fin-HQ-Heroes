<template>
  <div class="evaluation-detail">
    <h2>평가</h2>
    <div class="employee-info">
      <p><strong>이름 : </strong>{{ employeeName }}</p>
      <p><strong>직급 : </strong> {{ employeePosition }}</p>
      <p><strong>사번 : </strong> {{ employeeId }}</p>
      <p><strong>팀명 : </strong> {{ employeeTeam }}</p>
    </div>

    <div class="evaluation-form">
      <h3>1. 서비스 레벨 평가</h3>
      <div class="evaluation-item">
        <p>서비스 개선 및 최적화에 기여한 정도는 어느 정도였습니까?</p>
        <Dropdown v-model="serviceLevelScore" :options="scoreOptions" placeholder="평가 선택" class="dropdown-right" />
      </div>
      <div class="evaluation-item">
        <p>장애 발생 시 신속하게 대응하고 복구할 수 있었습니까?</p>
        <Dropdown v-model="incidentResponseScore" :options="scoreOptions" placeholder="평가 선택" class="dropdown-right" />
      </div>

      <h3>2. 팀 목표 및 개인 과제 평가</h3>
      <div class="evaluation-item">
        <p>팀 목표 달성에 얼마나 기여했습니까?</p>
        <Dropdown v-model="teamGoalScore" :options="scoreOptions" placeholder="평가 선택" class="dropdown-right" />
      </div>
      <div class="evaluation-item">
        <p>개인에게 주어진 과제를 성공적으로 이행했습니까?</p>
        <Dropdown v-model="personalTaskScore" :options="scoreOptions" placeholder="평가 선택" class="dropdown-right" />
      </div>

      <h3>3. 일정 계획 평가</h3>
      <div class="evaluation-item">
        <p>프로젝트 또는 과제의 마감 기한을 준수했습니까?</p>
        <Dropdown v-model="deadlineScore" :options="scoreOptions" placeholder="평가 선택" class="dropdown-right" />
      </div>
      <div class="evaluation-item">
        <p>예상치 못한 일정 변화에 유연하게 대처할 수 있었습니까?</p>
        <Dropdown v-model="flexibilityScore" :options="scoreOptions" placeholder="평가 선택" class="dropdown-right" />
      </div>

      <!-- 추가적으로 평가 코멘트를 입력받을 수 있도록 Textarea 추가 -->
      <div class="evaluation-item">
        <p>평가 코멘트:</p>
        <textarea v-model="comments" rows="4" class="comments-textarea"></textarea>
      </div>

      <div class="save-button-container">
        <Button label="평가 저장" @click="saveEvaluation" class="p-button-rounded p-button-primary" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { fetchGet, fetchPost } from '../auth/service/AuthApiService';
import Dropdown from 'primevue/dropdown';
import Button from 'primevue/button';

const route = useRoute();
const employeeId = route.params.employeeId;

// 상태 관리 변수
const employeeName = ref('');
const employeePosition = ref('');
const employeeTeam = ref('');
const comments = ref(''); // 평가 코멘트 추가

// 평가 항목들
const serviceLevelScore = ref(null);
const incidentResponseScore = ref(null);
const teamGoalScore = ref(null);
const personalTaskScore = ref(null);
const deadlineScore = ref(null);
const flexibilityScore = ref(null);

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

// 직원 평가 데이터를 로드하는 함수
async function fetchEmployeeEvaluationData() {
  try {
    const employeeData = await fetchGet(`http://localhost:8080/api/v1/employee/employees/${employeeId}`);
    employeeName.value = employeeData.employeeName;
    employeePosition.value = employeeData.positionName || '사원';
    employeeTeam.value = employeeData.teamName;
  } catch (error) {
    console.error('직원 데이터를 가져오는 중 오류 발생:', error);
  }
}

async function saveEvaluation() {
  try {
    // 평가 데이터를 POST 요청으로 전송
    const evaluationData = {
      employeeId: String(employeeId),  // String으로 변환
      evaluatorId: String(1),  // 평가자 ID를 String으로 변환
      evaluationFormId: 1,  // 평가 양식 ID
      score: calculateTotalScore(),  // 총합 점수
      comments: comments.value  // 코멘트
    };

    // 백엔드로 데이터 전송
    const response = await fetchPost('http://localhost:8080/api/v1/evaluation-service/evaluation', evaluationData);
    if (response) {
      alert('평가가 성공적으로 저장되었습니다.');
    }
  } catch (error) {
    console.error('평가 데이터를 저장하는 중 오류 발생:', error);
    alert('평가 저장에 실패했습니다.');
  }
}


// 점수 합산 및 계산 로직
function calculateTotalScore() {
  const scores = [
    serviceLevelScore.value,
    incidentResponseScore.value,
    teamGoalScore.value,
    personalTaskScore.value,
    deadlineScore.value,
    flexibilityScore.value
  ];
  
  // 선택되지 않은 항목은 0으로 처리하여 합산
  const totalScore = scores.reduce((acc, score) => acc + (score || 0), 0);
  return totalScore;
}

// 컴포넌트가 마운트되면 데이터 로드
onMounted(() => {
  fetchEmployeeEvaluationData();
});
</script>

<style scoped>
.evaluation-detail {
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 1.75rem;
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
}

.employee-info p {
  font-size: 1rem;
  color: #555;
  margin-bottom: 0.5rem;
}

.evaluation-form {
  margin-top: 2rem;
}

h3 {
  font-size: 1.5rem;
  color: #444;
  margin-bottom: 1rem;
}

.evaluation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.evaluation-item p {
  font-size: 1rem;
  color: #333;
  margin-right: 1rem;
  flex: 1;
}

.dropdown-right {
  flex: 0.5;
  min-width: 150px;
}

.comments-textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.save-button-container {
  text-align: center;
  margin-top: 2rem;
}
</style>
