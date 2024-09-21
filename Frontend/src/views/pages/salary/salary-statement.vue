<template>
    <div class="container">
      <header>
        <h1>급여관리</h1>
        <div class="year-selector">
          <label for="year">년도 선택:</label>
          <select v-model="selectedYear" id="year">
            <option v-for="year in years" :key="year">{{ year }}</option>
          </select>
        </div>
      </header>

      <salary-summary 
        :selectedYear="selectedYear"
        @salary-selected="updateSelectedSalary" 
      />
      <salary-details 
        :selectedSalary="selectedSalary"
      />
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
        selectedYear: new Date().getFullYear(),
        years: this.generateYears(1900),
      };
    },
    methods: {
      updateSelectedSalary(salary) {
        this.selectedSalary = salary;
      },
      generateYears(startYear) {
        const currentYear = new Date().getFullYear();
        const years = [];
        for (let year = startYear; year <= currentYear; year++) {
          years.push(year);
        }
        return years;
      },
    },
  };
  </script>
  
  <style scoped>
  .container {
    padding: 20px;
  }
  header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .year-selector {
    display: flex;
    align-items: center;
  }
  
  select {
    margin-left: 10px;
    padding: 5px;
  }
  </style>
  