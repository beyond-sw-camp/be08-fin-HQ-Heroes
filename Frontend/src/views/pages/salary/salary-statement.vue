<template>
  <div class="salary-container">
    <salary-summary @salary-selected="updateSelectedSalary" />
    <salary-details :selectedSalary="selectedSalary" />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import SalaryDetails from './SalaryDetails.vue';
import SalarySummary from './SalarySummary.vue';

const selectedSalary = ref(null);

function updateSelectedSalary(salary) {
  selectedSalary.value = salary;
}
</script>

<style scoped>
.salary-container {
  background-color: #ffffff;
  width: 100%;
  max-width: 100%;
  padding: 20px 40px;
  border-radius: 10px;
  /* box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); */
  margin: 0 auto;
}
</style>
