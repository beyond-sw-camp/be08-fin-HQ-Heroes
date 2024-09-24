<template>
    <div class="severance-pay-container">
        <h2>퇴직금 계산기</h2>
        <div class="input-field">
            <label for="salary">월 평균 급여 :</label>
            <input v-model="salary" type="number" id="salary" placeholder="월 평균 급여를 입력하세요" />
        </div>
        <div class="input-field">
            <label for="years">근무 연수 :</label>
            <input v-model="years" type="number" id="years" placeholder="근무 연수를 입력하세요" />
        </div>
        <div class="input-field">
            <label for="bonus">연간 상여금 :</label>
            <input v-model="bonus" type="number" id="bonus" placeholder="연간 상여금을 입력하세요" />
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
    </div>
</template>

<script setup>
import { ref } from 'vue';
import Button from 'primevue/button';

const salary = ref(null);
const years = ref(null);
const bonus = ref(null);
const severancePay = ref(null);

const calculateSeverancePay = () => {
const salaryValue = parseFloat(salary.value) || 0;
const yearsValue = parseFloat(years.value) || 0;
const bonusValue = parseFloat(bonus.value) || 0;

severancePay.value = (salaryValue * yearsValue) + bonusValue;
};

const formatCurrency = (value) => {
return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};
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
</style>
  