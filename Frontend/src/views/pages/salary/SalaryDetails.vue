<template>
    <Dialog 
      header="급여 상세" 
      :visible="displayModal" 
      :style="{ width: '50vw' }"
      @hide="updateDisplayModal(false)"
    >
      <div class="salary-modal">
        <div class="modal-header">
          <h2>이달의 총 급여액</h2>
          <p>급여지급일: {{ selectedMonth.date }}</p>
          <div class="summary-details">
            <div>
              <span>총급여액: </span>{{ formatCurrency(selectedMonth.totalPayment) }} 원
            </div>
            <div>
              <span>공제액: </span>{{ formatCurrency(selectedMonth.totalDeductions) }} 원
            </div>
            <div>
              <span>실지급액: </span>{{ formatCurrency(selectedMonth.netPayment) }} 원
            </div>
          </div>
        </div>

        <div class="work-time">
          <h4>근무시간</h4>
          <p>근무시간 마감기간: {{ selectedMonth.date }}</p>
          <p>일반근로: {{ selectedMonth.normalWorkHours }} 시간</p>
          <p>연장근로: {{ selectedMonth.extraWorkHours }} 시간</p>
          <p>야간근로: {{ selectedMonth.nightWorkHours }} 시간</p>
        </div>

        <div class="salary-details-section">
          <div class="payment-info">
            <h4>지급내역</h4>
            <p>기준급: {{ formatCurrency(selectedMonth.baseSalary) }} 원</p>
            <p>총 지급액: {{ formatCurrency(selectedMonth.totalPayment) }} 원</p>
          </div>

          <div class="deduction-info">
            <h4>공제내역</h4>
            <p>국민연금: {{ formatCurrency(selectedMonth.nationalPension) }} 원</p>
            <p>건강보험: {{ formatCurrency(selectedMonth.healthInsurance) }} 원</p>
            <p>고용보험: {{ formatCurrency(selectedMonth.employmentInsurance) }} 원</p>
            <p>장기요양보험료: {{ formatCurrency(selectedMonth.longTermCareInsurance) }} 원</p>
            <p>소득세: {{ formatCurrency(selectedMonth.incomeTax) }} 원</p>
            <p>지방소득세: {{ formatCurrency(selectedMonth.localIncomeTax) }} 원</p>
          </div>
        </div>

        <Button label="급여명세서 보내기" class="p-button-primary" @click="sendPayStatement" />
      </div>
    </Dialog>
</template>

<script setup>
import { ref, defineProps } from 'vue';

const props = defineProps({
  displayModal: Boolean,
  selectedMonth: Object,
  salaryMonths: Array,
  formatCurrency: Function,
  sendPayStatement: Function
});

const updateDisplayModal = (value) => {
  emit('update:displayModal', value);
};
</script>

<style scoped>
.salary-modal {
  padding: 1.5rem;
}

.modal-header {
  background-color: #e6f7ff;
  padding: 1rem;
  margin-bottom: 1rem;
}

.summary-details {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
}

.work-time,
.salary-details-section {
  margin-top: 1rem;
}

.salary-details-section {
  display: flex;
  justify-content: space-between;
}

.payment-info, .deduction-info {
  width: 45%;
}

h4 {
  margin-bottom: 0.5rem;
}
</style>
