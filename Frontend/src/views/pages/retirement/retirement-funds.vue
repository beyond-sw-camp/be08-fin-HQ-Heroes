<template>
  <div class="severance-pay-container">
    <h2>퇴직금 조회</h2>
    <div class="input-field" v-if="!dataLoaded">
      <p>데이터를 로딩 중입니다...</p>
    </div>
    <div v-else>
      <div class="input-field">
        <label for="salary">월 평균 급여 :</label>
        <input v-model="salary" type="number" id="salary" placeholder="월 평균 급여를 입력하세요" :disabled="salaryDisabled"/>
      </div>
      <div class="input-field">
        <label for="years">근무 연수 :</label>
        <input v-model="years" type="number" id="years" placeholder="근무 연수를 입력하세요" :disabled="yearsDisabled"/>
      </div>
      <div class="input-field">
        <label for="bonus">연간 상여금 :</label>
        <input v-model="bonus" type="number" id="bonus" placeholder="연간 상여금을 입력하세요" :disabled="bonusDisabled"/>
      </div>
      <div class="flex flex-row flex-center justify-center">
        <Button class="bg-primary" style="width: 100%;" @click="calculateSeverancePay">
          퇴직금 계산
        </Button>
      </div>

      <div v-if="severancePay !== null" class="result">
        <h3>퇴직금 :</h3>
        <p>{{ formatCurrency(severancePay) }}</p>
      </div>

      <div v-if="errorMessage" class="error">
        <p>{{ errorMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import Button from 'primevue/button';
import { onMounted, ref } from 'vue';
import { fetchAverageSalary } from './service/retirementService';

const salary = ref(null);
const years = ref(null);
const bonus = ref(null);
const severancePay = ref(null);
const errorMessage = ref(null);
const dataLoaded = ref(false);
const salaryDisabled = ref(true);
const yearsDisabled = ref(true);
const bonusDisabled = ref(true);

const fetchData = async () => {
  const employeeId = "EMP-001";
  const retireData = await fetchAverageSalary(employeeId);

  if (retireData) {
    salary.value = retireData.payrollAvg || null;
    years.value = retireData.period || null;
    bonus.value = retireData.annualBonus || null;

    salaryDisabled.value = !retireData.payrollAvg;
    yearsDisabled.value = !retireData.period;
    bonusDisabled.value = !retireData.annualBonus;

    if (!salary.value) {
      errorMessage.value = "월 평균 급여를 입력해 주세요.";
    }
    if (!years.value) {
      errorMessage.value = "근무 연수를 입력해 주세요.";
    }
    if (!bonus.value) {
      errorMessage.value = "연간 상여금을 입력해 주세요.";
    }
  } else {
    errorMessage.value = "데이터를 가져오는 데 실패했습니다.";
  }

  dataLoaded.value = true;
};

const calculateSeverancePay = () => {
  const salaryValue = parseFloat(salary.value) || 0;
  const yearsValue = parseFloat(years.value) || 0;
  const bonusValue = parseFloat(bonus.value) || 0;

  severancePay.value = (salaryValue * yearsValue) + bonusValue;
  errorMessage.value = null;
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

// 컴포넌트가 마운트될 때 데이터 로드
onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.severance-pay-container {
  background-color: #ffffff;
  width: 100%;
  max-width: 100%;
  padding: 20px 40px;
  border-radius: 10px;
  margin: 0 auto;
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  font-size: 2rem;
  font-weight: bold;
}

.input-field {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 0.75rem;
  box-sizing: border-box;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.result {
  margin-top: 2rem;
  font-size: 1.2em;
  text-align: center;
}

.error {
  margin-top: 1rem;
  color: red;
  text-align: center;
}
</style>
