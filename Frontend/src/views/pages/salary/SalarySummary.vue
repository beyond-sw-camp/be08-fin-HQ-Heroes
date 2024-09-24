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
            <Card>
              <template #header>
                <h3>{{ month.label }} 급여</h3>
              </template>
              <div class="month-header" :class="month.statusClass">
                <p>{{ month.date }} 지급</p>
                <p>대상 {{ month.employeeCount }}명</p>
              </div>
              <div class="salary-details">
                <p>지급총액: {{ formatCurrency(month.totalPayment) }} 원</p>
                <p>공제총액: {{ formatCurrency(month.totalDeductions) }} 원</p>
                <p>실지급액: {{ formatCurrency(month.netPayment) }} 원</p>
              </div>
              <Button
                label="급여입력보기"
                class="p-button-secondary"
                @click="() => { showSalaryModal(month); }"
              />
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
                <p>지급총액: {{ formatCurrency(month.totalPayment) }} 원</p>
                <p>공제총액: {{ formatCurrency(month.totalDeductions) }} 원</p>
                <p>실지급액: {{ formatCurrency(month.netPayment) }} 원</p>
              </div>
              <Button label="급여입력보기" class="p-button-secondary" disabled />
            </Card>
          </div>
        </div>
      </div>
  
      <SalaryDetailsModal 
        :displayModal="displayModal" 
        :formatCurrency="formatCurrency" 
        :sendPayStatement="sendPayStatement" 
        @update:displayModal="displayModal = $event" 
        :selectedMonth="selectedMonth" 
      />
    </div>
  </template>
  
  <script setup>
  import { ref, computed, watch } from 'vue';
  import Card from 'primevue/card';
  import Button from 'primevue/button';
  import Calendar from 'primevue/calendar';
  import SalaryDetailsModal from '@/views/pages/salary/SalaryDetails.vue';
  
  const selectedDate = ref(new Date());
  const displayModal = ref(false);
  const selectedMonth = ref(null);
  
  const salaryMonths = ref([
    { id: 1, label: '1월', date: '2024-01-31', employeeCount: 10, totalPayment: 3000000, totalDeductions: 500000, netPayment: 2500000, statusClass: 'completed', year: 2024 },
    { id: 2, label: '2월', date: '2024-02-29', employeeCount: 10, totalPayment: 3100000, totalDeductions: 520000, netPayment: 2580000, statusClass: 'completed', year: 2024 },
    { id: 3, label: '1월', date: '2023-01-31', employeeCount: 8, totalPayment: 2800000, totalDeductions: 450000, netPayment: 2350000, statusClass: 'completed', year: 2023 },
    { id: 4, label: '2월', date: '2023-02-28', employeeCount: 8, totalPayment: 2900000, totalDeductions: 470000, netPayment: 2430000, statusClass: 'completed', year: 2023 }
  ]);
  
  const placeholderMonths = ref([
    { id: 1, label: '1월', date: '2024-01-31', employeeCount: 0, totalPayment: 0, totalDeductions: 0, netPayment: 0 },
    { id: 2, label: '2월', date: '2024-02-29', employeeCount: 0, totalPayment: 0, totalDeductions: 0, netPayment: 0 }
  ]);
  
  const currentYear = new Date().getFullYear();
  const yearRange = `${1900}:${currentYear}`;
  
  const filteredSalaryMonths = computed(() => {
    const selectedYear = selectedDate.value.getFullYear();
    const filteredMonths = salaryMonths.value.filter(month => month.year === selectedYear);
    
    console.log("Filtered Months:", filteredMonths);
    return filteredMonths;
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
  </script>
  
  <style scoped>
  .salary-management {
    padding: 2rem;
  }
  
  .header h2 {
    margin-bottom: 0.5rem;
  }
  
  .salary-cards {
    display: flex;
    justify-content: space-around;
    margin-top: 2rem;
  }
  
  .month-card {
    width: 30%;
  }
  
  .completed {
    background-color: #dff0d8;
  }
  
  .pending {
    background-color: #f2dede;
  }
  </style>
  