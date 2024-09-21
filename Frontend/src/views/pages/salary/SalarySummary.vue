<template>
  <div class="salary-summary">
    <div 
      class="salary-card" 
      v-for="(salary, index) in filteredSalaries" 
      :key="index" 
      @click="selectSalary(index)"
    >
      <h3>{{ salary.period }} 급여</h3>
      <p>지급액: {{ salary.payment }}원</p>
      <p>공제액: {{ salary.deduction }}원</p>
    </div>
  </div>
</template>

<script>
export default {
  props: ['selectedYear'],
  data() {
    return {
      salaries: [
        { period: '2023년 1월', payment: 10000000, deduction: 500000, year: '2023' },
        { period: '2023년 2월', payment: 11000000, deduction: 600000, year: '2023' },
        { period: '2022년 1월', payment: 9000000, deduction: 400000, year: '2022' },
        { period: '2021년 1월', payment: 8500000, deduction: 350000, year: '2021' },
      ],
      selectedSalaryIndex: null,
    };
  },
  computed: {
    filteredSalaries() {
      return this.salaries.filter(salary => salary.year === this.selectedYear);
    },
  },
  methods: {
    selectSalary(index) {
      this.selectedSalaryIndex = index;
      this.$emit('salary-selected', this.filteredSalaries[index]);
    },
  },
};
</script>

<style scoped>
.salary-summary {
  display: flex;
  gap: 10px;
}

.salary-card {
  border: 1px solid #ddd;
  padding: 20px;
  cursor: pointer;
  width: 200px;
  text-align: center;
  background-color: #f9f9f9;
}

.salary-card:hover {
  background-color: #e6e6e6;
}
</style>
