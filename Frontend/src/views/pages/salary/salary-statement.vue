<template>
  <div>
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
</style>


