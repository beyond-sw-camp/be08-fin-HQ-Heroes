<template>
  <div class="card">
    <div class="font-semibold text-xl mb-4">평가 - {{ authStore.employeeData.teamName }}</div>
    <DataTable :value="filteredEmployees" :paginator="true" :rows="10" removableSort dataKey="employeeNo"
      :rowHover="true" selectionMode="single"
      :globalFilterFields="['employeeName', 'deptName', 'jobName', 'teamName', 'positionName', 'employeeId', 'joinDate']"
      showGridlines @row-click="showEmployeeDetails" :metaKeySelection="false">
      <template #empty> No employees found. </template>

      <Column field="employeeName" sortable header="이 름" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.employeeName }}
        </template>
      </Column>
      <Column field="jobName" sortable header="직무" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.jobName }}
        </template>
      </Column>
      <Column field="positionName" sortable header="직 책" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.positionName }}
        </template>
      </Column>
      <Column field="employeeId" sortable header="사 번" style="min-width: 12rem">
        <template #body="{ data }">
          {{ data.employeeId }}
        </template>
      </Column>
      <Column field="joinDate" sortable header="입사일" dataType="date" style="min-width: 10rem">
        <template #body="{ data }">
          {{ formatDate(new Date(data.joinDate)) }}
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script setup>
import { ref, onBeforeMount, watch } from 'vue';
import router from '@/router';
import { useAuthStore } from '@/stores/authStore';
import { fetchGet } from '../auth/service/AuthApiService';

// authStore 가져오기
const authStore = useAuthStore();

const employees = ref([]);
const filteredEmployees = ref([]);

// 직원 목록 가져오기
async function fetchEmployeeList() {
  try {
    const employeesData = await fetchGet('http://localhost:8080/api/v1/employee/employees', router.push, router.currentRoute.value);
    employees.value = employeesData || [];
    filterEmployeesByTeam();  // 팀별로 필터링
  } catch (error) {
    console.error('직원 데이터를 가져오는 중 오류 발생:', error);
    employees.value = [];
    filteredEmployees.value = [];
  }
}

// 팀 이름으로 직원 목록 필터링 함수
function filterEmployeesByTeam() {
  const teamName = authStore.employeeData.teamName;
  filteredEmployees.value = employees.value.filter(employee => employee.teamName === teamName && employee.positionName === '사원');
}

// 직원 상세 보기 함수
function showEmployeeDetails(event) {
  const employeeId = event.data.employeeId;
  router.push({ name: 'evaluationDetail', params: { employeeId } });  // 직원 정보를 문자열로 변환하여 전달
}

// 날짜 포맷팅 함수
function formatDate(date) {
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}

// 팀 이름이 변경되거나 있을 때 필터링을 다시 적용
watch(() => authStore.employeeData.teamName, () => {
  if (employees.value.length > 0) {
    filterEmployeesByTeam();
  }
});

// 컴포넌트가 마운트될 때 데이터 가져오기
onBeforeMount(() => {
  fetchEmployeeList();
});

</script>


<style scoped lang="scss">
:deep(.p-datatable-frozen-tbody) {
  font-weight: bold;
}

.search-container {
  display: flex;
  align-items: center;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #888;
}

.search-input {
  padding-left: 2.5rem;
}
</style>
