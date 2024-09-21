<template>
    <div v-if="salary" class="salary-details">
      <h2>{{ salary.period }} 급여 상세</h2>
      <p><strong>지급액:</strong> {{ salary.payment }}원</p>
      <p><strong>공제액:</strong> {{ salary.deduction }}원</p>
      <p><strong>순급여:</strong> {{ netSalary }}원</p>
      <button @click="$emit('close-details')">닫기</button>
    </div>
  </template>
  
  <script>
  export default {
    props: ['salary'],
    computed: {
      netSalary() {
        return this.salary.payment - this.salary.deduction;
      },
    },
  };
  </script>
  
  <style scoped>
  .salary-details {
    border: 1px solid #ddd;
    padding: 20px;
    background-color: #f9f9f9;
    margin-top: 10px;
  }
  </style>
  