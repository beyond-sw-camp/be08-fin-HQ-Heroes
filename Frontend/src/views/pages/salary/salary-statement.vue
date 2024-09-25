<template>
  <div class="salary-management">
    <div class="header">
      <h2>급여관리</h2>
      <p>급여와 내역을 확인한 후 신규급여를 생성할 수 있습니다.</p>
    </div>

    <div class="year-selection">
      <Calendar
        v-model="selectedDate"
        :showIcon="true"
        :yearRange="yearRange"
        view="year"
        dateFormat="yy"
        @input="updateSelectedYear"
        placeholder="년도 선택"
      />
    </div>

    <div class="salary-cards">
      <div v-if="filteredSalaryMonths.length > 0">
        <div v-for="month in filteredSalaryMonths" :key="month.id" class="month-card">
          <Card style="justify-content: space-between;">
            <template #header>
              <h3 style="margin-left: 1rem; margin-top: 1rem; margin-bottom: 1rem; font-size: large;">{{ month.label }} 급여</h3>
              <div class="month-header" :class="month.statusClass">
                <p>{{ month.date }} 지급</p>
                <p>대상 {{ month.employeeCount }}명</p>
              </div>
              <div class="salary-details">
                <p>지급총액 : {{ formatCurrency(month.totalPayment) }} 원</p>
                <p>공제총액 : {{ formatCurrency(month.totalDeductions) }} 원</p>
                <p>실지급액 : {{ formatCurrency(month.netPayment) }} 원</p>
              </div>
              <Button
              label="급여입력보기"
              class="p-button-secondary"
              style="margin-left:0.7rem; margin-top: 1rem; width: 95%;"
              @click="() => { showSalaryModal(month); }"
              />
            </template>
          </Card>
        </div>
      </div>

      <div v-else>
        <div class="month-card" v-for="month in placeholderMonths" :key="month.id">
          <Card>
            <template #header>
              <h3>{{ month.label }} 급여</h3>
            </template>
            <div class="month-header">
              <p>{{ month.date }} 지급</p>
              <p>대상 {{ month.employeeCount }}명</p>
            </div>
            <div class="salary-details">
              <p>지급총액 : {{ formatCurrency(month.totalPayment) }} 원</p>
              <p>공제총액 : {{ formatCurrency(month.totalDeductions) }} 원</p>
              <p>실지급액 : {{ formatCurrency(month.netPayment) }} 원</p>
            </div>
            <Button label="급여입력보기" class="p-button-secondary" disabled />
          </Card>
        </div>
      </div>
    </div>

    <Dialog 
      header="급여 상세" 
      v-model:visible="displayModal" 
      :style="{ width: '50vw' }"
    >
      <div class="salary-modal">
        <div class="modal-header">
          <h2>이달의 총 급여액</h2>
          <p>급여지급일 : {{ selectedMonth.date }}</p>
          <div class="summary-details">
            <div>
              <span>총급여액 : </span>{{ formatCurrency(selectedMonth.totalPayment) }} 원
            </div>
            <div>
              <span>공제액 : </span>{{ formatCurrency(selectedMonth.totalDeductions) }} 원
            </div>
            <div>
              <span>실지급액 : </span>{{ formatCurrency(selectedMonth.netPayment) }} 원
            </div>
          </div>
        </div>

        <div class="work-time">
          <h4>근무시간</h4>
          <p>근무시간 마감기간 : {{ selectedMonth.date }}</p>
          <p>일반근로 : {{ selectedMonth.normalWorkHours }} 시간</p>
          <p>연장근로 : {{ selectedMonth.extraWorkHours }} 시간</p>
          <p>야간근로 : {{ selectedMonth.nightWorkHours }} 시간</p>
        </div>

        <div class="salary-details-section">
          <div class="payment-info">
            <h4>지급내역</h4>
            <p>기준급: {{ formatCurrency(selectedMonth.baseSalary) }} 원</p>
            <p>총 지급액: {{ formatCurrency(selectedMonth.totalPayment) }} 원</p>
          </div>

          <div class="deduction-info">
            <h4>공제내역</h4>
            <p>국민연금: {{ formatCurrency(selectedMonth.nationalPension) }} 원</p>
            <p>건강보험: {{ formatCurrency(selectedMonth.healthInsurance) }} 원</p>
            <p>고용보험: {{ formatCurrency(selectedMonth.employmentInsurance) }} 원</p>
            <p>장기요양보험료: {{ formatCurrency(selectedMonth.longTermCareInsurance) }} 원</p>
            <p>소득세: {{ formatCurrency(selectedMonth.incomeTax) }} 원</p>
            <p>지방소득세: {{ formatCurrency(selectedMonth.localIncomeTax) }} 원</p>
          </div>
        </div>

        <Button label="급여명세서 보내기" class="p-button-primary" @click="sendPayStatement" />
      </div>
    </Dialog>

  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import Card from 'primevue/card';
import Button from 'primevue/button';
import Calendar from 'primevue/calendar';

const selectedDate = ref(new Date());
const displayModal = ref(false);
const selectedMonth = ref(null);

const salaryMonths = ref([
  { id: 1, label: '1월', date: '2024-01-31', employeeCount: 10, totalPayment: 3000000, totalDeductions: 500000, netPayment: 2500000, baseSalary: 2000000, normalWorkHours: 160, extraWorkHours: 10, nightWorkHours: 5, nationalPension: 30000, healthInsurance: 15000, employmentInsurance: 5000, longTermCareInsurance: 2000, incomeTax: 50000, localIncomeTax: 5000, year: 2024 },
  { id: 2, label: '2월', date: '2024-02-29', employeeCount: 10, totalPayment: 3100000, totalDeductions: 520000, netPayment: 2580000, baseSalary: 2000000, normalWorkHours: 160, extraWorkHours: 12, nightWorkHours: 6, nationalPension: 31000, healthInsurance: 15000, employmentInsurance: 5000, longTermCareInsurance: 2000, incomeTax: 60000, localIncomeTax: 6000, year: 2024 },
]);

const placeholderMonths = ref([
  { id: 1, label: '1월', date: '2024-01-31', employeeCount: 0, totalPayment: 0, totalDeductions: 0, netPayment: 0 },
  { id: 2, label: '2월', date: '2024-02-29', employeeCount: 0, totalPayment: 0, totalDeductions: 0, netPayment: 0 }
]);

const currentYear = new Date().getFullYear();
const yearRange = `${1900}:${currentYear}`;

const filteredSalaryMonths = computed(() => {
  const selectedYear = selectedDate.value.getFullYear();
  return salaryMonths.value.filter(month => month.year === selectedYear);
});

watch(selectedDate, () => {
  console.log("Selected Year:", selectedDate.value.getFullYear());
});

const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW'
  }).format(value);
};

const showSalaryModal = (selectedSalary) => {
  selectedMonth.value = selectedSalary;
  displayModal.value = true;
};

const sendPayStatement = () => {
  alert('급여명세서를 보냈습니다.');
};

const updateSelectedYear = () => {
  console.log("Selected Year:", selectedDate.value.getFullYear());
};

const updateDisplayModal = (value) => {
  displayModal.value = value;
};
</script>

<style scoped>
.salary-management {
  padding: 2rem;
}

.header h2 {
  margin-bottom: 0.5rem;
}

.salary-cards {
  /* display: flex; */
  justify-content: space-between;
  width: auto;
  margin-top: 2rem;
}

.month-card {
  width: 35%;
  margin-bottom: 1rem;
}

.month-header {
  margin-left: 1rem;
  margin-bottom: 1rem;
}

.pending {
  background-color: #f2dede;
}

.salary-modal {
  padding: 1.5rem;
}

.salary-details {
  margin-left: 1rem;
}

.modal-header {
  background-color: #e6f7ff;
  padding: 1rem;
  margin-bottom: 1rem;
}

.summary-details {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
}

.work-time,
.salary-details-section {
  margin-top: 1rem;
}

.salary-details-section {
  display: flex;
  justify-content: space-between;
}

.payment-info, .deduction-info {
  width: 48%;
}
</style>
