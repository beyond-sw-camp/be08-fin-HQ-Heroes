<template>
    <div>
      <salary-summary @salary-selected="updateSelectedSalary" />
      <salary-details :selectedSalary="selectedSalary" />
    </div>
  </template>
  
  <script>
  import SalaryDetails from './SalaryDetails.vue';
import SalarySummary from './SalarySummary.vue';
  
  export default {
    components: {
      SalarySummary,
      SalaryDetails,
    },
    data() {
      return {
        selectedSalary: null,
      };
    },
    methods: {
      updateSelectedSalary(salary) {
        this.selectedSalary = salary;
      },
    },
  };
  </script>
  