<script setup>
import { ref, onBeforeMount } from 'vue';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';

// 평가 결과 리스트
const evaluations = ref([
  {
    evaluationId: 1,
    evaluatorName: '홍길동',
    evaluatorPosition: '대리',
    evaluationType: '팀',
    score: 4.5,
    comments: '팀워크가 뛰어나며 책임감이 강하다.',
    createdAt: new Date('2023-08-15')
  },
  {
    evaluationId: 2,
    evaluatorName: '이순신',
    evaluatorPosition: '팀장',
    evaluationType: '리더',
    score: 4.8,
    comments: '리더십과 문제 해결 능력이 우수하다.',
    createdAt: new Date('2023-07-20')
  }
]);

// 선택된 평가 상세 정보
const selectedEvaluation = ref(null);
const displayDialog = ref(false);

// 평가 세부 사항 보여주기
function showEvaluationDetails(event) {
  selectedEvaluation.value = event.data;
  displayDialog.value = true;
}

// 날짜 포맷팅 함수 (yyyy/mm/dd 형식)
function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}/${month}/${day}`;
}
</script>

<template>
  <div class="card">
    <h2 class="font-semibold text-xl mb-4">평가 결과 목록</h2>
    <DataTable :value="evaluations" removableSort :paginator="true" :rows="10" dataKey="evaluationId" :rowHover="true"
      selectionMode="single" @row-click="showEvaluationDetails" :metaKeySelection="false" @rowSelect="onRowSelect"
      @rowUnselect="onRowUnselect">
      <Column field="evaluationId" sortable header="평가 번호" />
      <Column field="evaluatorName" sortable header="평가자 이름" />
      <Column field="evaluatorPosition" sortable header="평가자 직급" />
      <Column field="evaluationType" sortable header="평가 유형" />
      <Column field="score" sortable header="평가 점수" />
      <Column field="createdAt" sortable header="평가일" :body="(data) => formatDate(data.createdAt)" />
    </DataTable>

    <!-- 평가 상세 정보 모달 -->
    <Dialog v-model:visible="displayDialog" modal="true" header="평가 상세" :style="{ width: '50vw', borderRadius: '12px' }"
      :draggable="false" :closable="true">
      <div v-if="selectedEvaluation">
        <h3>평가자 정보</h3>
        <p><strong>이름:</strong> {{ selectedEvaluation.evaluatorName }}</p>
        <p><strong>직급:</strong> {{ selectedEvaluation.evaluatorPosition }}</p>
        <p><strong>평가 유형:</strong> {{ selectedEvaluation.evaluationType }}</p>
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
