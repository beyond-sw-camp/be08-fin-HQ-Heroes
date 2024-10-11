<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">평가 결과 목록</div>

    <!-- DataTable for listing evaluations -->
    <DataTable :value="evaluations" :paginator="true" :rows="10" removableSort dataKey="evaluationId"
               :rowHover="true" selectionMode="single" @row-click="showEvaluationDetails" :metaKeySelection="false">
      <template #empty>No evaluations found.</template>

      <Column field="evaluationId" sortable header="평가 번호" style="min-width: 8rem">
        <template #body="{ data }">{{ data.evaluationId }}</template>
      </Column>
      <Column field="evaluatorId" sortable header="평가자 ID" style="min-width: 12rem">
        <template #body="{ data }">{{ data.evaluatorId }}</template>
      </Column>
      <Column field="employeeId" sortable header="피평가자 ID" style="min-width: 12rem">
        <template #body="{ data }">{{ data.employeeId }}</template>
      </Column>
      <Column field="score" sortable header="평가 점수" style="min-width: 8rem">
        <template #body="{ data }">{{ data.score }}</template>
      </Column>
      <Column field="createdAt" sortable header="평가일" dataType="date" style="min-width: 10rem">
        <template #body="{ data }">{{ formatDate(new Date(data.createdAt)) }}</template>
      </Column>
    </DataTable>

    <!-- Dialog for showing selected evaluation details -->
    <Dialog v-model:visible="displayDialog" modal="true" header="평가 상세"
            :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="true">
      <div v-if="selectedEvaluation">
        <h3>평가자 정보</h3>
        <p><strong>평가자 ID:</strong> {{ selectedEvaluation.evaluatorId }}</p>
        <p><strong>피평가자 ID:</strong> {{ selectedEvaluation.employeeId }}</p>
        <p><strong>평가 점수:</strong> {{ selectedEvaluation.score }}</p>
        <p><strong>평가일:</strong> {{ formatDate(selectedEvaluation.createdAt) }}</p>
        <p><strong>코멘트:</strong> {{ selectedEvaluation.comments }}</p>
      </div>

      <template #footer>
        <Button label="닫기" icon="pi pi-times" class="p-button-text p-button-plain" @click="displayDialog = false" />
      </template>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import router from '@/router';
import { fetchGet } from '../auth/service/AuthApiService';

// State variables
const evaluations = ref([]);  // 전체 평가 리스트
const selectedEvaluation = ref(null);  // 선택된 평가 정보
const displayDialog = ref(false);  // Dialog 표시 여부

// 사원 ID로 평가 목록 가져오기 (로그인된 사용자)
async function fetchEvaluationsByEmployeeId() {
  try {
    const evaluationData = await fetchGet('http://localhost:8080/api/v1/evaluation-service/evaluations/by-employeeId', router.push, router.currentRoute.value);
    evaluations.value = evaluationData || [];
  } catch (error) {
    console.error('사원 ID로 평가 데이터를 가져오는 중 오류 발생:', error);
    evaluations.value = [];
  }
}

// 평가 상세 보기 함수
function showEvaluationDetails(event) {
  const evaluationId = event.data.evaluationId;
  fetchEvaluationById(evaluationId);
}

// 평가 ID로 평가 상세 정보 가져오기
async function fetchEvaluationById(evaluationId) {
  try {
    const evaluation = await fetchGet(`http://localhost:8080/api/v1/evaluation-service/evaluation/${evaluationId}`, router.push, router.currentRoute.value);
    if (evaluation) {
      selectedEvaluation.value = evaluation;
      displayDialog.value = true;
    }
  } catch (error) {
    console.error('평가 데이터를 가져오는 중 오류 발생:', error);
  }
}

// 날짜 포맷팅 함수
function formatDate(date) {
  if (!(date instanceof Date)) {
    // If date is a string, convert it to a Date object
    date = new Date(date);
  }
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
}

// 컴포넌트가 마운트될 때 평가 데이터 가져오기 (전체 목록)
onBeforeMount(() => {
  fetchEvaluationsByEmployeeId();
});
</script>


<style scoped lang="scss">
.card {
  padding: 20px;
}

:deep(.p-datatable) {
  font-weight: bold;
}
</style>
