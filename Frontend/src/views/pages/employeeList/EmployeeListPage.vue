<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4">전체 사원 목록</div>
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
            <template #header>
                <div class="flex justify-between items-center">
                    <div class="flex items-center gap-2">
                        <Dropdown v-model="selectedDepartment" :options="departments" optionLabel="deptName" placeholder="부서를 선택하세요" @change="filterByDepartmentAndTeam" class="mr-2" />
                        <Dropdown v-model="selectedTeam" :options="teams" optionLabel="teamName" placeholder="팀을 선택하세요" @change="filterByDepartmentAndTeam" class="mr-2" />
                    </div>
                    <div class="relative search-container">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="Keyword Search" class="pl-8 search-input" />
                    </div>
                </div>
            </template>
            <template #empty> No employees found. </template>
            <Column field="deptName" sortable header="부 서" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.deptName }}
                </template>
            </Column>
            <Column field="teamName" sortable header="팀" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.teamName }}
                </template>
            </Column>
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
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import axios from 'axios';
import { onBeforeMount, ref } from 'vue';

const employees = ref([]);
const filteredEmployees = ref([]);
const filters = ref(null);
const selectedEmployee = ref(null);
const displayDialog = ref(false);
const departments = ref([]);
const teams = ref([]);
const selectedDepartment = ref(null);
const selectedTeam = ref(null);

async function fetchEmployeeList() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/employees'); // API 호출
        if (Array.isArray(response.data)) {
            employees.value = response.data;
        } else {
            employees.value = [];
        }
    } catch (error) {
        console.error("직원 데이터를 가져오는 중 오류 발생:", error);
        employees.value = [];
    }

    filterByDepartmentAndTeam();
}

async function fetchDepartments() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/departments'); // 부서 API 호출
        console.log("부서 응답:", response.data);
        if (Array.isArray(response.data)) {
            departments.value = [{ deptName: '전체 부서' }, ...response.data]; // '전체' 추가
        } else {
            departments.value = [{ deptName: '전체 부서' }];
        }
    } catch (error) {
        console.error("부서 데이터를 가져오는 중 오류 발생:", error);
        departments.value = [{ deptName: '전체 부서' }];
    }
}

async function fetchTeams() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/teams'); // 팀 API 호출
        console.log("팀 응답:", response.data);
        if (Array.isArray(response.data)) {
            teams.value = [{ teamName: '전체 팀' }, ...response.data]; // '전체' 추가
        } else {
            teams.value = [{ teamName: '전체 팀' }];
        }
    } catch (error) {
        console.error("팀 데이터를 가져오는 중 오류 발생:", error);
        teams.value = [{ teamName: '전체 팀' }];
    }
}

function filterByDepartmentAndTeam() {
    console.log("선택된 부서:", selectedDepartment.value); // 추가된 로그
    filteredEmployees.value = employees.value.filter((employee) => {
        const matchesDepartment = !selectedDepartment.value || selectedDepartment.value.deptName === '전체 부서' || employee.deptName === selectedDepartment.value.deptName;
        const matchesTeam = !selectedTeam.value || selectedTeam.value.teamName === '전체 팀' || employee.teamName === selectedTeam.value.teamName;
        return matchesDepartment && matchesTeam;
    });
}


function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        employeeName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        deptName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        jobName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        teamName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        positionName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        employeeId: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        joinDate: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
    };
}

function showEmployeeDetails(event) {
    selectedEmployee.value = event.data;
    displayDialog.value = true;
}

function formatDate(date) {
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}

onBeforeMount(() => {
    fetchEmployeeList();
    fetchDepartments();
    fetchTeams();
    initFilters();
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
