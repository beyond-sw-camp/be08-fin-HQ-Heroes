<template>
    <div class="employee-list-page">
        <div class="card">
            <div class="header">
                <h2>사원 목록</h2>
                <div class="search-filter">
                    <Dropdown v-model="selectedDepartment" :options="departments" optionLabel="name" placeholder="부서" @change="filterEmployees" />
                    <Dropdown v-model="selectedTeam" :options="teams" optionLabel="name" placeholder="팀" @change="filterEmployees" />
                    <InputText v-model="globalFilter" placeholder="사원 검색 (이름, 사원 코드)" @input="filterEmployees" />
                </div>
            </div>
            <DataTable :value="filteredEmployees" paginator :rows="10" :globalFilterFields="['employeeName', 'employeeCode']" :filters="filters" dataKey="employeeId" @row-click="editEmployee">
                <Column field="employeeCode" header="사원 코드" />
                <Column field="employeeName" header="이름" />
                <Column field="department" header="부서" />
                <Column field="teamName" header="팀" />
                <Column field="position" header="직책" />
                <Column field="hireDate" header="입사일" />
            </DataTable>
        </div>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';

const employees = ref([]);
const filteredEmployees = ref([]);
const selectedEmployee = ref(null);
const displayEditDialog = ref(false);
const selectedDepartment = ref(null);
const selectedTeam = ref(null);
const globalFilter = ref('');

const departments = ref([{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '인사' }]);

const teams = ref([{ name: '전체' }, { name: '팀A' }, { name: '팀B' }, { name: '팀C' }]);

// 더미 데이터를 사용한 사원 목록
async function fetchEmployees() {
    employees.value = [
        { employeeId: 1, employeeCode: 'E001', employeeName: '김철수', department: '개발', teamName: '팀A', position: '개발자', hireDate: '2022-01-01' },
        { employeeId: 2, employeeCode: 'E002', employeeName: '박영희', department: '디자인', teamName: '팀B', position: '디자이너', hireDate: '2021-02-01' },
        { employeeId: 3, employeeCode: 'E003', employeeName: '이민호', department: '인사', teamName: '팀C', position: '팀장', hireDate: '2020-03-01' },
        { employeeId: 4, employeeCode: 'E004', employeeName: '최지우', department: '개발', teamName: '팀A', position: '프론트엔드 개발자', hireDate: '2019-04-01' },
        { employeeId: 5, employeeCode: 'E005', employeeName: '강호동', department: '인사', teamName: '팀C', position: 'HR 매니저', hireDate: '2018-05-01' },
        { employeeId: 6, employeeCode: 'E006', employeeName: '정해인', department: '디자인', teamName: '팀B', position: 'UI/UX 디자이너', hireDate: '2020-06-01' },
        { employeeId: 7, employeeCode: 'E007', employeeName: '김하늘', department: '개발', teamName: '팀A', position: '백엔드 개발자', hireDate: '2021-07-01' }
    ];
    filterEmployees();
}

function filterEmployees() {
    filteredEmployees.value = employees.value.filter((employee) => {
        const matchesDepartment = selectedDepartment.value?.name !== '전체' ? employee.department === selectedDepartment.value.name : true;
        const matchesTeam = selectedTeam.value?.name !== '전체' ? employee.teamName === selectedTeam.value.name : true;
        const matchesFilter = globalFilter.value ? employee.employeeName.includes(globalFilter.value) || employee.employeeCode.includes(globalFilter.value) : true;
        return matchesDepartment && matchesTeam && matchesFilter;
    });
}

function editEmployee(event) {
    selectedEmployee.value = event.data;
    displayEditDialog.value = true;
}

onBeforeMount(() => {
    fetchEmployees();
});
</script>

<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.search-filter {
    display: flex;
    gap: 10px;
}
</style>
