<template>
  <div class="severance-pay-container">
    <label class="title text-2xl font-bold text-gray-800">퇴직금 조회</label>
    <div class="employee-info">
      <div class="input-field">
        <label>부서 :</label>
        <p>{{ authStore.employeeData.deptName }}</p>
      </div>
      <div class="input-field">
        <label>팀 :</label>
        <p>{{ authStore.employeeData.teamName }}</p>
      </div>
      <div class="input-field">
        <label>직무 :</label>
        <p>{{ authStore.employeeData.jobRoleName }}</p>
      </div>
      <div class="input-field">
        <label>직책 :</label>
        <p>{{ authStore.employeeData.positionName }}</p>
      </div>
      <div class="input-field">
        <label>근무 년수 :</label>
        <p>{{ calculateAnnualLeave(authStore.employeeData.joinDate) }} 년</p>
      </div>
      <div class="input-field">
        <label>최근 3개월 평균 급여 :</label>
        <p>{{ formatCurrency(retireData) }}</p>
      </div>
    </div>

    <div class="result">
      <h3>예상 퇴직금</h3>
      <p class="severance-amount">{{ formatCurrency(severancePay) }}</p>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { onMounted, ref } from 'vue';
import { fetchSalarySum } from './service/retirementService';

const authStore = useAuthStore();

const retireData = ref(null);
const severancePay = ref(0);

const fetchSalary = async () => {
  try {
    const data = await fetchSalarySum(authStore.loginUserId); // 데이터를 가져와서 다른 변수에 저장
    retireData.value = data; // 반응형 데이터에 할당
    calculateSeverancePay();
  } catch (error) {
    console.error("Error fetching average salary");
  }
};

// 근무 년수 계산 함수
const calculateAnnualLeave = (date) => {
  const joinDate = new Date(date);
  if (isNaN(joinDate.getTime())) {
    return "입사일 정보가 유효하지 않습니다.";
  }

  const currentDate = new Date();
  let yearsOfService = currentDate.getFullYear() - joinDate.getFullYear();

  const monthDiff = currentDate.getMonth() - joinDate.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && currentDate.getDate() < joinDate.getDate())) {
    yearsOfService--;
  }

  if(yearsOfService == 0) {
    yearsOfService ++;
  }

  return yearsOfService;
};

// 예상 퇴직금 계산 함수
const calculateSeverancePay = () => {
  const yearsOfService = calculateAnnualLeave(authStore.employeeData.joinDate);
  severancePay.value = yearsOfService * retireData.value;
}

const formatCurrency = (value) =>
new Intl.NumberFormat('ko-KR', {
  style: 'currency',
  currency: 'KRW',
}).format(value || 0);

onMounted(async () => {
  await calculateAnnualLeave();
  await fetchSalary();
  await calculateSeverancePay();
});
</script>

<style scoped>
.severance-pay-container {
  background: #ffffff;
  width: 100%;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.5s ease-in-out;
}

.title {
  letter-spacing: 0.5px;
}

.employee-info {
  margin-top: 20px;
  border: 1px solid #f1f3f5;
  padding: 20px;
  border-radius: 12px;
  background-color: #f8fafc;
}

.input-field {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  padding: 8px 0;
  border-bottom: 1px solid #e9ecef;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.input-field:last-child {
  border-bottom: none;
}

.input-field:hover {
  background-color: #f1f5f9;
}

label {
  font-weight: 600;
  color: #495057;
}

.result {
  margin-top: 30px;
  text-align: center;
}

h3 {
  font-size: 1.6rem;
  color: #343a40;
  margin-bottom: 10px;
}

.severance-amount {
  font-size: 2.2rem;
  font-weight: 700;
  color: #6366f1;
}

.loading p {
  text-align: center;
  font-size: 1.2rem;
  color: #adb5bd;
}

@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
