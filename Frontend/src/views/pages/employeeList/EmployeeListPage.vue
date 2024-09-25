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
            :globalFilterFields="['employeeName', 'department', 'address', 'email']"
            showGridlines
            @row-click="showEmployeeDetails"
            :metaKeySelection="false"
            @rowSelect="onRowSelect"
            @rowUnselect="onRowUnselect"
        >
            <template #header>
                <div class="flex justify-between items-center">
                    <div class="flex items-center gap-2">
                        <Dropdown v-model="selectedDepartment" :options="departments" optionLabel="name" placeholder="부서를 선택하세요" @change="filterByDepartmentAndTeam" class="mr-2" />
                        <Dropdown v-model="selectedTeam" :options="teams" optionLabel="name" placeholder="팀을 선택하세요" @change="filterByDepartmentAndTeam" class="mr-2" />
                    </div>
                    <div class="relative search-container">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="Keyword Search" class="pl-8 search-input" />
                    </div>
                </div>
            </template>
            <template #empty> No employees found. </template>
            <Column field="department" sortable header="부 서" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.department }}
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
            <Column field="position" sortable header="직 책" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.position }}
                </template>
            </Column>
            <Column field="employeeId" sortable header="사 번" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.employeeId }}
                </template>
            </Column>
            <Column field="hireDate" sortable header="입사일" dataType="date" style="min-width: 10rem">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.hireDate)) }}
                </template>
            </Column>
            <Column field="status" sortable header="상 태" style="min-width: 10rem">
                <template #body="{ data }">
                    {{ data.status }}
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
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';

// Vue Ref 및 Reactive 변수들
const employees = ref([]);
const filteredEmployees = ref([]);
const filters = ref(null);
const selectedEmployee = ref(null);
const displayDialog = ref(false);
const departments = ref([]);
const teams = ref([]);
const selectedDepartment = ref(null);
const selectedTeam = ref(null);

// API 호출하여 직원 데이터 가져오기
async function fetchEmployeeList() {
    employees.value = [
        { employeeNo: 1, employeeName: '홍길동', department: '개발', teamName: '팀A', position: '개발자', employeeId: 'E001', hireDate: '2022-01-01', status: '근무중' },
        { employeeNo: 2, employeeName: '이순신', department: '개발', teamName: '팀B', position: '팀장', employeeId: 'E002', hireDate: '2021-02-01', status: '퇴근' },
        { employeeNo: 3, employeeName: '김유신', department: '디자인', teamName: '팀C', position: '디자이너', employeeId: 'E003', hireDate: '2020-03-01', status: '휴가' }
    ];


    filterByDepartmentAndTeam(); // 필터링 함수 호출
}

async function fetchDepartments() {
    departments.value = [
        { name: '전체' }, // 전체 옵션 추가
        { name: '개발' },
        { name: '디자인' },
        { name: '인사' }
    ];
}

async function fetchTeams() {
    teams.value = [
        { name: '전체' }, // 전체 옵션 추가
        { name: '팀A' },
        { name: '팀B' },
        { name: '팀C' }
    ];
}

function filterByDepartmentAndTeam() {
    filteredEmployees.value = employees.value.filter((employee) => {
        const matchesDepartment = selectedDepartment.value && selectedDepartment.value.name !== '전체' ? employee.department === selectedDepartment.value.name : true;
        const matchesTeam = selectedTeam.value && selectedTeam.value.name !== '전체' ? employee.teamName === selectedTeam.value.name : true;
        return matchesDepartment && matchesTeam;
    });
}

function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        employeeName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        department: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        hireDate: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
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
