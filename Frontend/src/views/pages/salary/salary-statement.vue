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
      <div class="grid grid-cols-12 gap-4">
        <!-- 1월부터 12월까지 각 급여 카드 -->
        <div v-for="month in monthsList" :key="month.id" class="col-span-12 lg:col-span-6 xl:col-span-3">
          <div class="card mb-0 p-6 custom-box relative">
            <div class="text-left">
              <!-- 아이콘 박스 -->
              <div class="flex items-center dark:bg-blue-400/10 rounded-border icon-box mb-6">
                <i :class="month.icon" class="text-gray-400 !text-4xl"></i>
              </div>
              <!-- 텍스트 부분 (제목 및 카운트) -->
              <span class="block text-muted-color font-medium mb-4 text-base">{{ month.label }} 급여</span>
              <div class="text-surface-900 dark:text-surface-0 font-medium text-lg">
                총 지급액: {{ formatCurrency(month.totalPayment) }}
              </div>
              <div class="text-surface-900 dark:text-surface-0 font-medium text-lg">
                공제액: {{ formatCurrency(month.totalDeductions) }}
              </div>
              <div class="text-surface-900 dark:text-surface-0 font-medium text-lg">
                실지급액: {{ formatCurrency(month.netPayment) }}
              </div>
              <Button
                label="급여내역보기"
                class="p-button-secondary"
                style="margin-left:0.7rem; margin-top: 1rem; width: 95%;"
                @click="() => { showSalaryModal(month); }"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <Dialog
      header="급여 상세"
      v-model:visible="displayModal"
      :style="{ width: '50vw' }"
    >
      <div class="salary-modal">
        <!-- 선택한 급여의 상세 내역 표시 -->
        <h3>{{ selectedMonth?.label }} 급여 상세 내역</h3>
        <p>총 지급액: {{ formatCurrency(selectedMonth?.totalPayment) }}</p>
        <p>공제액: {{ formatCurrency(selectedMonth?.totalDeductions) }}</p>
        <p>실지급액: {{ formatCurrency(selectedMonth?.netPayment) }}</p>
      </div>
    </Dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Calendar from 'primevue/calendar';
import Button from 'primevue/button';

// 달력 관련 설정
const selectedDate = ref(new Date());
const displayModal = ref(false);
const selectedMonth = ref(null);
const currentYear = new Date().getFullYear();
const yearRange = `${1900}:${currentYear}`;

// 1월부터 12월까지의 급여 카드 리스트
const monthsList = ref([
  { id: 1, label: '1월', icon: 'pi pi-calendar', totalPayment: 3000000, totalDeductions: 500000, netPayment: 2500000 },
  { id: 2, label: '2월', icon: 'pi pi-calendar', totalPayment: 3100000, totalDeductions: 520000, netPayment: 2580000 },
  { id: 3, label: '3월', icon: 'pi pi-calendar', totalPayment: 3200000, totalDeductions: 540000, netPayment: 2660000 },
  { id: 4, label: '4월', icon: 'pi pi-calendar', totalPayment: 3300000, totalDeductions: 560000, netPayment: 2740000 },
  { id: 5, label: '5월', icon: 'pi pi-calendar', totalPayment: 3400000, totalDeductions: 580000, netPayment: 2820000 },
  { id: 6, label: '6월', icon: 'pi pi-calendar', totalPayment: 3500000, totalDeductions: 600000, netPayment: 2900000 },
  { id: 7, label: '7월', icon: 'pi pi-calendar', totalPayment: 3600000, totalDeductions: 620000, netPayment: 2980000 },
  { id: 8, label: '8월', icon: 'pi pi-calendar', totalPayment: 3700000, totalDeductions: 640000, netPayment: 3060000 },
  { id: 9, label: '9월', icon: 'pi pi-calendar', totalPayment: 3800000, totalDeductions: 660000, netPayment: 3140000 },
  { id: 10, label: '10월', icon: 'pi pi-calendar', totalPayment: 3900000, totalDeductions: 680000, netPayment: 3220000 },
  { id: 11, label: '11월', icon: 'pi pi-calendar', totalPayment: 4000000, totalDeductions: 700000, netPayment: 3300000 },
  { id: 12, label: '12월', icon: 'pi pi-calendar', totalPayment: 4100000, totalDeductions: 720000, netPayment: 3380000 },
]);

const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW'
  }).format(value);
};

// 선택된 급여 정보를 모달에 표시
const showSalaryModal = (selectedSalary) => {
  selectedMonth.value = selectedSalary;
  displayModal.value = true;
};

const updateSelectedYear = () => {
  console.log("Selected Year:", selectedDate.value.getFullYear());
};
</script>

<style scoped>
.salary-management {
  padding: 2rem;
}

.salary-cards {
  margin-top: 2rem;
}

.custom-box {
  border-radius: 10px;
  background-color: #ffffff;
}

.icon-box {
  width: 4rem;
  height: 4rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.5rem;
}

.text-muted-color {
  color: #6b7280;
}

.text-surface-900 {
  color: #1f2937;
}

.dark .text-surface-0 {
  color: #f9fafb;
}

.mb-6 {
  margin-bottom: 30px;
}

.text-left {
  text-align: left;
}

.salary-modal {
  padding: 1.5rem;
}
</style>
