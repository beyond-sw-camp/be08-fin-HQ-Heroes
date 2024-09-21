<template>
    <div class="salary-details" v-if="selectedSalary">
      <h3>{{ selectedSalary.period }} 급여 세부내역</h3>
      <p>지급액: {{ selectedSalary.payment }}원</p>
      <p>공제액: {{ selectedSalary.deduction }}원</p>
    </div>
  </template>
  
  <script>
  export default {
    props: ['selectedSalary'],
  };
  </script>
  