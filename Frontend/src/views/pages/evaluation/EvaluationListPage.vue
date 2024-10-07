<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4"> {{authStore.employeesData.teamName}} 평가</div>
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


const employees = ref([]);
const filteredEmployees = ref([]);
const filters = ref(null);
const selectedEmployee = ref(null);
const displayDialog = ref(false);
const departments = ref([]);
const teams = ref([]);
const selectedDepartment = ref(null);
const selectedTeam = ref(null);
const authStore = useAuthStore();

async function fetchEmployeeList() {
    try {
        // 권한이 필요한 페이지에 접근할 때 fetchAuthorizedPage 사용
        const employeesData = await fetchGet('http://localhost:8080/api/v1/employee/employees', router.push, router.currentRoute.value);

        if (employeesData) {
            // JSON.parse 제거
            employees.value = employeesData;
        } else {
            employees.value = [];
        }

        filterByDepartmentAndTeam();
    } catch (error) {
        console.error('직원 데이터를 가져오는 중 오류 발생:', error);
        employees.value = [];
    }
}

async function fetchDepartments() {
    try {
        const departmentsData = await fetchGet('http://localhost:8080/api/v1/employee/departments', router.push, router.currentRoute.value);

        if (departmentsData) {
            // JSON.parse 제거
            departments.value = [{ deptId: null, deptName: '전체 부서' }, ...departmentsData];
        } else {
            departments.value = [{ deptId: null, deptName: '전체 부서' }];
        }
    } catch (error) {
        console.error('부서 데이터를 가져오는 중 오류 발생:', error);
        departments.value = [{ deptId: null, deptName: '전체 부서' }];
    }
}

async function fetchTeams(deptId) {
    try {
        const teamsData = await fetchGet(`http://localhost:8080/api/v1/employee/teams?deptId=${deptId}`, router.push, router.currentRoute.value);

        if (teamsData) {
            teams.value = [{ teamId: null, teamName: '전체 팀' }, ...teamsData]; // JSON.parse 제거
        } else {
            teams.value = [{ teamId: null, teamName: '전체 팀' }];
        }
    } catch (error) {
        console.error('팀 데이터를 가져오는 중 오류 발생:', error);
        teams.value = [{ teamId: null, teamName: '전체 팀' }];
    }
}

// 부서 및 팀에 따라 직원 목록 필터링
function filterByDepartmentAndTeam() {
    console.log('선택된 부서:', selectedDepartment.value); // 로그 추가
    console.log('부서 변경됨:', selectedDepartment.value); // 로그 추가

    if (selectedDepartment.value && selectedDepartment.value.deptId) {
        fetchTeams(selectedDepartment.value.deptId); // 선택한 부서 ID 전달
    } else {
        teams.value = [{ teamId: null, teamName: '전체 팀' }]; // 부서가 선택되지 않은 경우 기본 팀 추가
    }

    filteredEmployees.value = employees.value.filter((employee) => {
        const matchesDepartment = !selectedDepartment.value || selectedDepartment.value.deptName === '전체 부서' || employee.deptName === selectedDepartment.value.deptName;
        const matchesTeam = !selectedTeam.value || selectedTeam.value.teamName === '전체 팀' || employee.teamName === selectedTeam.value.teamName;
        return matchesDepartment && matchesTeam;
    });
}

// 필터 초기화
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
