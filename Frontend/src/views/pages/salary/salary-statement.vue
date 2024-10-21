<template>
  <div class="card salary-management-wrapper" :class="{ blur: isBlurred }">
    <div class="salary-management">
      <div class="header">
        <label class="text-2xl font-bold text-gray-800">급여 관리</label>
      </div>

      <div class="year-selection">
        <Calendar
          v-model="selectedDate"
          :showIcon="true"
          :yearRange="yearRange"
          view="year"
          dateFormat="yy"
          @update:model-value="updateSelectedYear"
          placeholder="년도 선택"
          class="calendar"
        />
      </div>

      <div class="salary-cards">
        <div class="grid grid-cols-12 gap-6">
          <div v-if="monthsList.length === 0" class="col-span-12">
            <p class="text-center text-gray-500">급여 데이터가 없습니다.</p>
          </div>
          <div v-for="month in monthsList" :key="month.id" class="col-span-12 lg:col-span-6 xl:col-span-4">
            <div class="card p-4 custom-box relative transition-transform duration-200 hover:scale-105">
              <div class="text-left">
                <div class="flex items-center justify-between mb-4">
                  <i :class="month.icon" class="text-gray-600 text-3xl"></i>
                </div>
                <span class="block text-muted-color font-medium text-lg">{{ getMonthLabel(new Date(month.salaryMonth).getMonth()) }} 급여</span>
                <div class="status-badge" :class="month.status === 'PAID' ? 'PAID' : 'PENDING'">
                  {{ month.status === 'PAID' ? '지급' : '미지급' }}
                </div>
                <div class="text-surface-900 font-medium text-lg mt-2">
                  <div>총 급여 : {{ formatCurrency(month.preTaxTotal) }}</div>
                  <div>공제액 : {{ formatCurrency(calculateTotalDeductions(month)) }}</div>
                  <div>실지급액 : {{ formatCurrency(month.preTaxTotal - calculateTotalDeductions(month)) }}</div>
                </div>
                <Button
                  label="급여내역보기"
                  class="p-button-secondary mt-4"
                  style="width: 100%;"
                  @click="showSalaryModal(month)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <Dialog
        :header="salaryDialogHeader"
        v-model:visible="displayModal"
        :style="{ width: '70vw', marginLeft: '15%', marginTop: '5%' }"
        class="salary-dialog"
        @hide="closeSalaryModal"
        :draggable="false"
      >
        <div class="salary-modal">
          <div class="salary-details">
            <div class="left-panel">
              <h3 class="text-xl font-semibold text-gray-800">급여 상세 정보</h3>
              <div class="info-item">
                <span>총 근무시간 :</span>
                <span>{{ selectedMonth?.totalWorkHours }} 시간</span>
              </div>
              <div class="info-item">
                <span>시급 :</span>
                <span>{{ formatCurrency(selectedMonth?.baseSalary) }}</span>
              </div>
              <div v-if="selectedMonth?.salaryMonth && (getMonthLabel(selectedMonth.salaryMonth) === '1월' || getMonthLabel(selectedMonth.salaryMonth) === '7월')" class="info-item">
                <span>성과급 :</span>
                <span>{{ formatCurrency(selectedMonth?.performanceBonus || 0) }}</span>
              </div>
              <div class="total-deductions font-semibold mt-4">
                <span>급여 합계 :</span>
                <span>{{ formatCurrency(selectedMonth?.totalWorkHours * selectedMonth?.baseSalary + (selectedMonth?.bonus || 0)) }}</span>
              </div>
            </div>

            <div class="right-panel">
              <h3 class="text-xl font-semibold text-gray-800">공제액</h3>
              <div v-if="deductions.length">
                <div v-for="deduction in deductions" :key="deduction.type" class="deduction-item">
                  <span>{{ deduction.type }} :</span>
                  <span>{{ formatCurrency(deduction.amount) }}</span>
                </div>
              </div>
              <div v-else>
                <p>급여 데이터가 없습니다.</p>
              </div>
              <div class="total-deductions font-semibold mt-4">
                <span>공제액 합계:</span>
                <span>{{ formatCurrency(totalDeductions) }}</span>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <Button label="닫기" class="p-button-text" @click="closeSalaryModal" />
          </div>
        </div>
      </Dialog>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { fetchTotalWorkHours } from '@/views/pages/salary/workService';
import Button from 'primevue/button';
import Calendar from 'primevue/calendar';
import { onMounted, ref } from 'vue';
import { fetchbaseSalary, fetchBonus, fetchSalary } from './salaryService';
const authStore = useAuthStore();

const selectedDate = ref(new Date());
const selectedYear = ref(selectedDate.value.getFullYear());
const displayModal = ref(false);
const selectedMonth = ref({});
const deductions = ref([]);
const totalDeductions = ref(0);
const currentYear = new Date().getFullYear();
const yearRange = `${1900}:${currentYear}`;
const monthsList = ref([]);
const salaryDialogHeader = ref("급여 내역");
const isBlurred = ref(false);

const getMonthLabel = (monthIndex) => {
  const monthNames = [
    '1월', '2월', '3월', '4월', '5월', '6월',
    '7월', '8월', '9월', '10월', '11월', '12월'
  ];
  return monthNames[monthIndex];
};

// 화폐 포맷팅 함수
const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

// 공제액 합계 계산 함수
const calculateTotalDeductions = (month) => {
  return (
    month.nationalPension +
    month.healthInsurance +
    month.longTermCare +
    month.employmentInsurance +
    month.incomeTax +
    month.localIncomeTax
  );
};

// 선택한 연도에 따라 월별 데이터 가져오기
const updateSelectedYear = async (date) => {
  selectedYear.value = date.getFullYear();
  await loadMonthlyData();
};

// 월별 데이터 가져오기 함수
const loadMonthlyData = async () => {
  try {
    const salaryData = await fetchSalary(authStore.loginUserId);
    if (salaryData && salaryData.length > 0) {
      const filteredData = salaryData.filter((item) => {
        const salaryYear = new Date(item.salaryMonth).getFullYear();
        return salaryYear === selectedYear.value;
      });

      // 필터링된 데이터를 월별 내림차순으로 정렬
      monthsList.value = filteredData.sort(
        (a, b) => new Date(b.salaryMonth) - new Date(a.salaryMonth)
      );
    } else {
      monthsList.value = [];
    }
  } catch (error) {
    console.error("Error loading salary data:", error);
  }
};

// 급여 상세 모달 열기
const showSalaryModal = async (month) => {
  selectedMonth.value = month;
  salaryDialogHeader.value = `${authStore.employeeData.employeeName}님의 급여 내역`;

  const baseSalary = await fetchbaseSalary(authStore.loginUserId); // 급여 세부 정보 가져오기
  selectedMonth.value.baseSalary = baseSalary;

  // 총 근무 시간 가져오기
  const monthNumber = new Date(month.salaryMonth).getMonth() + 1; // 월은 1부터 시작하므로 +1
  const totalWorkHours = await fetchTotalWorkHours(selectedYear.value, monthNumber);
  selectedMonth.value.totalWorkHours = totalWorkHours; // 총 근무 시간을 selectedMonth에 저장

  // 성과급 가져오기
  const performanceBonus = await fetchBonus(authStore.loginUserId);
  selectedMonth.value.performanceBonus = performanceBonus;
  
  await fetchDeductionsData(month.salaryMonth);

  displayModal.value = true;
  isBlurred.value = true;
};

// 공제 데이터 가져오기 함수
const fetchDeductionsData = async (salaryMonth) => {
  deductions.value = [
    { type: '국민연금', amount: selectedMonth.value.nationalPension },
    { type: '건강보험', amount: selectedMonth.value.healthInsurance },
    { type: '장기요양보험', amount: selectedMonth.value.longTermCare },
    { type: '고용보험', amount: selectedMonth.value.employmentInsurance },
    { type: '소득세', amount: selectedMonth.value.incomeTax },
    { type: '지방소득세', amount: selectedMonth.value.localIncomeTax },
  ];

  totalDeductions.value = calculateTotalDeductions(selectedMonth.value); // 공제액 합계
};

// 모달 닫기 함수
const closeSalaryModal = () => {
  displayModal.value = false;
  isBlurred.value = false;
};

// 컴포넌트 마운트 시 초기 데이터 로드
onMounted(async () => {
  await loadMonthlyData(); // 마운트 시 월별 데이터 로드
});
</script>

<style scoped>
.salary-management {
  padding: 1rem;
}

.header {
  margin-bottom: 2rem;
}

.year-selection {
  margin-bottom: 2rem;
}

.salary-cards {
  margin-bottom: 2rem;
}

.custom-box {
  border-radius: 1rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.custom-box:hover {
  transform: translateY(-5px);
}

.status-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  padding: 0.5rem 1rem;
  border-radius: 1rem;
  font-weight: bold;
  color: white;
}

.PAID {
  background-color: #6366f1;
}

.PENDING {
  background-color: #e0e7ff;
}

.salary-modal {
  display: flex;
  flex-direction: column;
}

.salary-details {
  display: flex;
  padding: 1rem;
}

.left-panel,
.right-panel {
  width: 50%;
  padding: 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  background: #f9fafb;
  margin: 0.5rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
}

.deduction-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
}

.total-deductions {
  display: flex;
  justify-content: space-between;
  padding: 1rem 0;
  font-weight: bold;
  font-size: 1.1rem;
  border-top: 2px solid #e5e7eb;
  margin-top: 1rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}
</style>