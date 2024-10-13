<template>
  <div class="card">
    <div class="salary-management">
      <div class="header">
        <label class="text-2xl font-bold text-gray-800">급여 관리</label>
        <p class="text-gray-600 mt-2">급여와 내역을 확인한 후 신규 급여를 생성할 수 있습니다.</p>
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
          <div v-for="month in monthsList" :key="month.id" class="col-span-12 lg:col-span-6 xl:col-span-4">
            <div class="card mb-0 p-4 custom-box relative transition-transform duration-200 hover:scale-105">
              <div class="text-left">
                <div class="flex items-center justify-between mb-4">
                  <i :class="month.icon" class="text-gray-600 text-3xl"></i>
                </div>
                <span class="block text-muted-color font-medium text-lg">{{ getMonthLabel(month.salaryMonth) }} 급여</span>
                <div class="status-badge" :class="month.status ? 'paid' : 'pending'">
                  {{ month.status ? '지급' : '미지급' }}
                </div>

                <div class="text-surface-900 font-medium text-lg mt-2">
                  <div>총 지급액 : {{ formatCurrency(month.totalSalary) }}</div>
                  <div>공제액 : {{ formatCurrency(month.totalDeductions) }}</div>
                  <div>실지급액 : {{ formatCurrency(month.netPayment) }}</div>
                </div>
                <Button
                  label="급여내역보기"
                  class="p-button-secondary mt-4"
                  style="width: 100%;"
                  @click="() => { showSalaryModal(month); }"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <Dialog
        header="급여 내역"
        v-model:visible="displayModal"
        :style="{ width: '70vw', marginLeft: '15%', marginTop: '5%' }"
        class="salary-dialog"
      >
        <div class="salary-modal">
          <div class="salary-details">
            <div class="left-panel">
              <h3 class="text-xl font-semibold text-gray-800">급여 상세 정보</h3>
              <div class="info-item">
                <span>부서:</span>
                <span>{{ authStore.employeeData.deptName }}</span>
              </div>
              <div class="info-item">
                <span>팀:</span>
                <span>{{ authStore.employeeData.teamName }}</span>
              </div>
              <div class="info-item">
                <span>직무:</span>
                <span>{{ authStore.employeeData.jobName }}</span>
              </div>
              <div class="info-item">
                <span>직책:</span>
                <span>{{ authStore.employeeData.positionName }}</span>
              </div>
              <div class="info-item">
                <span>사원 이름:</span>
                <span>{{ authStore.employeeData.employeeName }}</span>
              </div>
              <div class="info-item">
                <span>총 근무시간:</span>
                <span>{{ selectedMonth?.totalWorkHours }} 시간</span>
              </div>
            </div>

            <div class="right-panel">
              <h3 class="text-xl font-semibold text-gray-800">공제액</h3>
              <div class="deduction-item" v-for="(deduction, index) in deductions" :key="index">
                <span>{{ deduction.name }}:</span>
                <span>{{ formatCurrency(deduction.amount) }}</span>
              </div>
              <div class="total-deductions font-semibold mt-4">
                <span>공제액 합계:</span>
                <span>{{ formatCurrency(totalDeductions) }}</span>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <Button label="닫기" class="p-button-text" @click="displayModal = false" />
          </div>
        </div>
      </Dialog>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { fetchDeductions } from '@/views/pages/salary/deductService';
import { fetchSalaryDataUpToCurrentMonth } from '@/views/pages/salary/salaryService';
import Button from 'primevue/button';
import Calendar from 'primevue/calendar';
import { onMounted, ref, watch } from 'vue';

const authStore = useAuthStore();

const selectedDate = ref(new Date());
const selectedYear = ref(selectedDate.value.getFullYear());
const displayModal = ref(false);
const selectedMonth = ref(null);
const deductions = ref([]);
const totalDeductions = ref(0);
const currentYear = new Date().getFullYear();
const yearRange = `${1900}:${currentYear}`;
const monthsList = ref([]);

// 화폐 포맷팅 함수
const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

// 선택한 연도에 따라 월별 데이터 가져오기
const updateSelectedYear = async (date) => {
  selectedYear.value = date.getFullYear();
  await loadMonthlyData();
};

// 월별 데이터 가져오기 함수
const loadMonthlyData = async () => {
  const salaryData = await fetchSalaryDataUpToCurrentMonth(authStore.loginUserId, selectedYear.value);
  // 월별 데이터에 대해 공제액 및 실지급액 계산
  monthsList.value = await Promise.all(salaryData.map(async (month) => {
    const deductionsData = await fetchDeductionsData(month); // 공제액 및 실지급액 계산
    return {
      ...month,
      ...deductionsData,
    };
  }));
};

// 급여 상세 모달 열기
const showSalaryModal = async (month) => {
  selectedMonth.value = month;
  await fetchDeductionsData(month); // 월 정보 전달
  displayModal.value = true;
};

const fetchDeductionsData = async (month) => {
  try {
    const fetchedDeductions = await fetchDeductions(month.id, selectedYear.value);

    // 공제 항목 추출
    const nationalPension = fetchedDeductions.find(d => d.deductName === '국민연금')?.deductionRate || 0;
    const healthInsuranceRate = fetchedDeductions.find(d => d.deductName === '건강보험')?.deductionRate || 0;
    const longTermCareRate = fetchedDeductions.find(d => d.deductName === '장기요양')?.deductionRate || 0;
    const employmentInsurance = fetchedDeductions.find(d => d.deductName === '고용보험')?.deductionRate || 0;
    const incomeTaxRate = fetchedDeductions.find(d => d.deductName === '소득세')?.deductionRate || 0;
    const localIncomeTaxRate = fetchedDeductions.find(d => d.deductName === '지방소득세')?.deductionRate || 0;

    // 공제 금액 계산
    const nationalPensionAmount = month.baseSalary * nationalPension;
    const healthInsuranceAmount = month.baseSalary * healthInsuranceRate;
    const longTermCareAmount = healthInsuranceAmount * longTermCareRate;
    const employmentInsuranceAmount = month.baseSalary * employmentInsurance;
    const incomeTaxAmount = month.baseSalary * incomeTaxRate;
    const localIncomeTaxAmount = incomeTaxAmount * localIncomeTaxRate;

    // 공제 항목 배열 구성
    deductions.value = [
      { name: '국민연금', amount: nationalPensionAmount },
      { name: '건강보험', amount: healthInsuranceAmount },
      { name: '장기요양', amount: longTermCareAmount },
      { name: '고용보험', amount: employmentInsuranceAmount },
      { name: '소득세', amount: incomeTaxAmount },
      { name: '지방소득세', amount: localIncomeTaxAmount },
    ];

    // 공제액 합계 계산
    totalDeductions.value = deductions.value.reduce((total, deduction) => total + deduction.amount, 0);

    // 총 지급액 계산 (기본급 + 보너스)
    const totalSalary = month.baseSalary + (month.bonus || 0);

    // 실지급액 계산
    const netPayment = totalSalary - totalDeductions.value;
    return { totalDeductions : totalDeductions.value, netPayment };
  } catch (error) {
    console.error('Error fetching deductions data:', error);
  }
};

const getMonthLabel = (dateString) => {
  const date = new Date(dateString); // 문자열 -> Date 객체
  const month = date.getMonth() + 1; // getMonth()는 0부터 시작하므로 1을 더함
  return `${month}월`;
};

// selectedMonth 변경 시 실지급액 계산
watch(selectedMonth, (newValue) => {
  if (newValue) {
    console.log('Selected Month:', newValue);
  }
});

// 페이지 로드 시 초기화
onMounted(async () => {
  await loadMonthlyData();
  await fetchDeductionsData();
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

.paid {
  background-color: #28a745; /* Green */
}

.pending {
  background-color: #dc3545; /* Red */
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
  background: #f9fafb; /* Light background */
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
  padding: 0.5rem 0;
  font-weight: bold;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}
</style>
