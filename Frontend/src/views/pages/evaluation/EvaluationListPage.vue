<template>
    <div class="card">
        <!-- employeeId를 제목에 표시 -->
        <div class="font-semibold text-xl mb-4">평가 - {{ authStore.employeeData.teamName }}</div>
        <DataTable
            :value="filteredEmployees"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="employeeNo"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['employeeName', 'deptName', 'jobName', 'teamName', 'positionName', 'employeeId', 'joinDate']"
            showGridlines
            @row-click="showEmployeeDetails"
            :metaKeySelection="false"
            @rowSelect="onRowSelect"
            @rowUnselect="onRowUnselect"
        >
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

        <EmployeeDetailModal :employee="selectedEmployee" :visible="displayDialog" @update:visible="displayDialog = $event" />
    </div>
</template>

<script setup>
import EmployeeDetailModal from '@/views/pages/employeeDetail/EmployeeDetailModal.vue';
import fetchGet from '../auth/service/AuthApiService';
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import { onBeforeMount, ref } from 'vue';
import router from '@/router';
import { useAuthStore } from '@/stores/authStore';

// authStore 가져오기
const authStore = useAuthStore();

const employees = ref([]);
const filteredEmployees = ref([]);
const filters = ref(null);
const selectedEmployee = ref(null);
const displayDialog = ref(false);
const departments = ref([]);
const teams = ref([]);

async function fetchEmployeeList() {
    try {
        const employeesData = await fetchGet('http://localhost:8080/api/v1/employee/employees', router.push, router.currentRoute.value);

        if (employeesData) { 
            employees.value = employeesData;
            filterEmployeesByTeam();
        } else {
            employees.value = [];
            filteredEmployees.value = [];
        }

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

async function fetchDepartments() {
    try {
        const departmentsData = await fetchGet('http://localhost:8080/api/v1/employee/departments', router.push, router.currentRoute.value);

        if (departmentsData) {
            departments.value = [{ deptId: null, deptName: '전체 부서' }, ...departmentsData];
        } else {
            departments.value = [{ deptId: null, deptName: '전체 부서' }];
        }
    } catch (error) {
        console.error('부서 데이터를 가져오는 중 오류 발생:', error);
        departments.value = [{ deptId: null, deptName: '전체 부서' }];
    }
}

// 직원 상세 보기 함수
function showEmployeeDetails(event) {
    selectedEmployee.value = event.data;
    displayDialog.value = true;
}

// 날짜 포맷팅 함수
function formatDate(date) {
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}

function onRowSelect(event) {
    console.log('선택된 직원:', event.data);
    selectedEmployee.value = event.data;
    displayDialog.value = true;
}

function onRowUnselect(event) {
    console.log('선택 해제된 직원:', event.data);
    selectedEmployee.value = null;
    displayDialog.value = false;
}

// 컴포넌트 마운트 시 데이터 가져오기
onBeforeMount(() => {
    fetchEmployeeList();
    fetchDepartments();
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
