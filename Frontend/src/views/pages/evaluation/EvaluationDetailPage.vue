<template>
  <div>
      <h2>{{ employeeName }}의 평가 상세</h2>
      <p>직급: {{ employeePosition }}</p>
      <p>서비스 레벨 평가: {{ serviceLevel }}</p>
      <p>팀 기여도: {{ teamContribution }}</p>
      <p>일정 계획 평가: {{ scheduleAdherence }}</p>
      <!-- 평가 상세 정보 추가 -->
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const employeeId = route.params.employeeId;

// 평가 데이터 (API 또는 임시 데이터로부터 로드)
const employeeName = ref('');
const employeePosition = ref('');
const serviceLevel = ref('');
const teamContribution = ref('');
const scheduleAdherence = ref('');

// 임시 데이터 예시
const evaluations = [
  { employeeId: 1, employeeName: '홍길동', employeePosition: '대리', serviceLevel: 'S', teamContribution: 'A', scheduleAdherence: 'B+' },
  { employeeId: 2, employeeName: '이순신', employeePosition: '팀장', serviceLevel: 'A+', teamContribution: 'A', scheduleAdherence: 'A+' }
];

// 평가 데이터를 불러오는 함수
onMounted(() => {
  const evaluation = evaluations.find(e => e.employeeId == employeeId);
  if (evaluation) {
      employeeName.value = evaluation.employeeName;
      employeePosition.value = evaluation.employeePosition;
      serviceLevel.value = evaluation.serviceLevel;
      teamContribution.value = evaluation.teamContribution;
      scheduleAdherence.value = evaluation.scheduleAdherence;
  }
});
</script>