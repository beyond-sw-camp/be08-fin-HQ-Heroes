<template>
  <div class="container">
    <header>
      <div class="title-font"><h1>급여관리</h1></div>
      <div class="year-selector">
        <label for="year">년도 선택:</label>
        <select v-model="selectedYear" id="year" @change="filterSalaries">
          <option v-for="year in years" :key="year">{{ year }}</option>
        </select>
      </div>
    </header>

    <div class="salary-summary">
      <div 
        class="salary-card" 
        v-for="(salary, index) in filteredSalaries" 
        :key="index" 
        @click="selectSalary(index)"
        :class="{ selected: selectedSalaryIndex === index }"
      >
        <h3>{{ salary.month }}월</h3>
        <p>지급일: {{ salary.paymentDate }}</p>
        <p>대상인원: {{ salary.targetCount }}명</p>
        <p>지급액: {{ salary.paymentAmount }}원</p>
        <p>공제액: {{ salary.deductionAmount }}원</p>
        <p>실지급액: {{ salary.netPaymentAmount }}원</p>
        <button class="view-details-button">급여입력보기</button>
      </div>
    </div>

    <div v-if="selectedSalary" class="salary-details">
      <h3>{{ selectedSalary.month }}월 급여 세부내역</h3>
      <p>기본급: {{ selectedSalary.paymentAmount }}원</p>
      <p>지급총액: {{ selectedSalary.netPaymentAmount }}원</p>
      <p>공제내역:</p>
      <ul>
        <li>국민연금: {{ selectedSalary.deductionBreakdown.nationalPension }}원</li>
        <li>건강보험: {{ selectedSalary.deductionBreakdown.healthInsurance }}원</li>
        <li>고용보험: {{ selectedSalary.deductionBreakdown.employmentInsurance }}원</li>
        <li>소득세: {{ selectedSalary.deductionBreakdown.incomeTax }}원</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const selectedYear = ref(new Date().getFullYear());
const selectedSalaryIndex = ref(null);
const selectedSalary = ref(null);
const years = generateYears(1900);
const salaries = ref([
  { month: 1, year: 2023, paymentDate: "2023.01.01", targetCount: 3, paymentAmount: 106000000, deductionAmount: 43587980, netPaymentAmount: 62412020, deductionBreakdown: { nationalPension: 15750, healthInsurance: 9980, employmentInsurance: 954000, incomeTax: 38733710 }},
  { month: 2, year: 2023, paymentDate: "2023.02.01", targetCount: 3, paymentAmount: 106000000, deductionAmount: 43587980, netPaymentAmount: 62412020, deductionBreakdown: { nationalPension: 15750, healthInsurance: 9980, employmentInsurance: 954000, incomeTax: 38733710 }},
  { month: 3, year: 2023, paymentDate: "2023.03.01", targetCount: 3, paymentAmount: 106000000, deductionAmount: 43587980, netPaymentAmount: 62412020, deductionBreakdown: { nationalPension: 15750, healthInsurance: 9980, employmentInsurance: 954000, incomeTax: 38733710 }},
  { month: 1, year: 2024, paymentDate: "2024.01.01", targetCount: 3, paymentAmount: 110000000, deductionAmount: 45000000, netPaymentAmount: 65000000, deductionBreakdown: { nationalPension: 16000, healthInsurance: 10000, employmentInsurance: 970000, incomeTax: 39000000 }},
]);
const filteredSalaries = ref([]);

onMounted(() => {
  filterSalaries();
});

function generateYears(startYear) {
  const currentYear = new Date().getFullYear();
  const years = [];
  for (let year = startYear; year <= currentYear; year++) {
    years.push(year);
  }
  return years;
}

function filterSalaries() {
  filteredSalaries.value = salaries.value.filter(salary => salary.year === selectedYear.value);
  selectedSalaryIndex.value = null;
  selectedSalary.value = null;
}

function selectSalary(index) {
  selectedSalaryIndex.value = index;
  selectedSalary.value = filteredSalaries.value[index];
}
</script>

<style scoped>
.container {
  padding: 20px;
}

.title-font {
  font-size: large;
  font-weight: bolder;
}
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.year-selector {
  display: flex;
  align-items: center;
}
select {
  margin-left: 10px;
  padding: 5px;
}
.salary-summary {
  display: flex;
  justify-content: space-between;
}
.salary-card {
  border: 1px solid #ddd;
  padding: 20px;
  cursor: pointer;
  flex-basis: 30%;
  transition: transform 0.2s ease-in-out;
}
.salary-card:hover {
  transform: scale(1.05);
}
.selected {
  border: 2px solid #007bff;
}
.salary-details {
  margin-top: 20px;
  border: 1px solid #ddd;
  padding: 20px;
}
button {
  margin-top: 10px;
  padding: 5px 10px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}
button:hover {
  background-color: #0056b3;
}
</style>
