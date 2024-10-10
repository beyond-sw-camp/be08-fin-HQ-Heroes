<template>
  <div class="evaluation-detail">
    <h2>평가</h2>

    <div class="employee-info">
      <p><strong>이름:</strong> {{ employeeName }}</p>
      <p><strong>직급:</strong> {{ employeePosition }}</p>
      <p><strong>사번:</strong> {{ employeeId }}</p>
      <p><strong>팀명:</strong> {{ employeeTeam }}</p>
    </div>

    <hr class="divider" />

    <div class="evaluation-form">
      <h2>평가 기준 항목</h2>
      <div
        v-for="(criteria, index) in evaluationCriteriaList"
        :key="criteria.evaluationCriteriaId"
        class="evaluation-item"
      >
        <h3>{{ criteria.criteriaTitle }}</h3>

        <!-- 각 질문과 점수 선택을 같은 행에 배치 -->
        <div
          v-for="(question, questionIndex) in criteria.criteriaContent.split('#')"
          :key="questionIndex"
          class="evaluation-row"
        >
          <p class="question-text">{{ question }}</p>
          <Dropdown
            v-model="criteriaScores[index][questionIndex]"
            :options="scoreOptions"
            placeholder="점수 선택"
            class="custom-dropdown"
          />
        </div>
      </div>

      <hr class="divider" />

      <div class="evaluation-item flex-col comment-item">
        <p>코멘트</p>
        <Textarea v-model="comments" rows="4" class="custom-textarea"></Textarea>
      </div>

      <div class="save-button-container">
        <Button
          label="평가 저장"
          @click="saveEvaluation"
          class="p-button-rounded p-button-primary custom-button"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { fetchGet, fetchPost } from "../auth/service/AuthApiService";
import Dropdown from "primevue/dropdown";
import Button from "primevue/button";
import Textarea from "primevue/textarea";

const route = useRoute();
const employeeId = route.params.employeeId;

// 상태 관리 변수
const employeeName = ref("");
const employeePosition = ref("");
const employeeTeam = ref("");
const employeeDept = ref("");
const comments = ref(""); // 평가 코멘트 추가

// 평가 기준 항목 및 점수 관리 변수
const evaluationCriteriaList = ref([]);
const criteriaScores = ref([]); // 2D array to hold scores for each question

// 평가 점수 옵션
const scoreOptions = ref([
  { label: "S (매우 우수)", value: 100 },
  { label: "A+ (우수)", value: 95 },
  { label: "A (좋음)", value: 90 },
  { label: "B+ (보통)", value: 85 },
  { label: "B (미흡)", value: 80 },
  { label: "C+ (개선 필요)", value: 75 },
  { label: "C (부족)", value: 70 },
]);

// 직원 평가 데이터를 로드하는 함수
async function fetchEmployeeEvaluationData() {
  try {
    const employeeData = await fetchGet(
      `http://localhost:8080/api/v1/employee/employees/${employeeId}`
    );
    employeeName.value = employeeData.employeeName;
    employeePosition.value = employeeData.positionName || "사원";
    employeeDept.value = employeeData.deptName;
    employeeTeam.value = employeeData.teamName;

    // Fetch evaluation criteria based on department
    fetchEvaluationCriteria(employeeData.deptName);
  } catch (error) {
    console.error("직원 데이터를 가져오는 중 오류 발생:", error);
  }
}

// Fetch evaluation criteria by department
async function fetchEvaluationCriteria(deptName) {
  try {
    const criteriaData = await fetchGet(
      `http://localhost:8080/api/v1/evaluation-criteria/by-department?deptName=${deptName}`
    );
    evaluationCriteriaList.value = criteriaData;

    // Initialize criteriaScores array
    criteriaScores.value = criteriaData.map((criteria) => {
      const subQuestions = criteria.criteriaContent.split("#");
      return new Array(subQuestions.length).fill(null); // Initialize scores for each sub-question
    });
  } catch (error) {
    console.error("평가 기준 데이터를 가져오는 중 오류 발생:", error);
  }
}

// 평가 데이터를 저장하는 함수
async function saveEvaluation() {
  try {
    const evaluationData = {
      employeeId: String(employeeId),
      evaluatorId: String(1),
      evaluationFormId: 1,
      scores: criteriaScores.value, // 2D array of scores
      comments: comments.value,
    };

    const response = await fetchPost(
      "http://localhost:8080/api/v1/evaluation-service/evaluation",
      evaluationData
    );
    if (response) {
      alert("평가가 성공적으로 저장되었습니다.");
    }
  } catch (error) {
    console.error("평가 데이터를 저장하는 중 오류 발생:", error);
    alert("평가 저장에 실패했습니다.");
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
  background-color: #f8f9fa;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  font-family: "Arial", sans-serif;
}

h2 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
}

.employee-info p {
  font-size: 1.1rem;
  color: #555;
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

h4 {
  font-size: 1.2rem;
  color: #333;
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
  font-size: 1rem;
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
