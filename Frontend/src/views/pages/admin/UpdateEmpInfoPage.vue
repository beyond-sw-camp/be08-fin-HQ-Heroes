<template>
    <div class="employee-list-page">
        <div class="card">
            <div class="flex flex-row justify-between">
                <label class="text-xl font-bold" style="width: 10%">사원 목록</label>
                <div class="flex flex-row w-1rem col-4">
                    <Dropdown class="mr-2" v-model="selectedDepartment" :options="departments" optionLabel="name" placeholder="부서" @change="filterEmployees" />
                    <Dropdown class="mr-2" v-model="selectedTeam" :options="teams" optionLabel="name" placeholder="팀" @change="filterEmployees" />
                    <InputGroup>
                        <InputGroupAddon>
                            <i class="pi pi-search"></i>
                        </InputGroupAddon>
                        <InputText placeholder="사원 이름, 코드, 부서, 팀" v-model="globalFilter" @input="initFilters" />
                    </InputGroup>
                </div>
            </div>
            <DataTable
                :value="filteredEmployees"
                removableSort
                paginator
                :rows="10"
                :rowsPerPageOptions="[10, 20, 30, 50]"
                :globalFilterFields="['employeeName', 'employeeCode']"
                selectionMode="single"
                dataKey="employeeId"
                @row-click="showEditDialog"
                :metaKeySelection="false"
                @rowSelect="onRowSelect"
                @rowUnselect="onRowUnselect"
            >
                <Column field="employeeCode" sortable header="사원 코드" />
                <Column field="employeeName" sortable header="이름" />
                <Column field="department" sortable header="부서" />
                <Column field="teamName" sortable header="팀" />
                <Column field="position" sortable header="직책" />
                <Column field="hireDate" sortable header="입사일" />
            </DataTable>
        </div>
    </div>

    <Dialog header="직원 정보" :visible="displayEditDialog" style="width: 70%" :modal="true" :draggable="false">
        <div class="flex flex-col p-4 bg-white shadow-md rounded-lg" v-if="selectedEmployee">
            <div class="flex flex-row">
                <div class="flex flex-col w-1/2 p-3 border-r">
                    <div class="flex flex-row mb-4">
                        <div class="w-1/2 p-2">
                            <Image src="https://upload.wikimedia.org/wikipedia/ko/4/4a/%EC%8B%A0%EC%A7%B1%EA%B5%AC.png" alt="Profile Image" class="w-full h-auto rounded-lg shadow" preview/>
                        </div>
                        <div class="flex flex-col w-1/2 justify-center items-center">
                            <Button class="p-button p-component">사진 변경</Button>
                        </div>
                    </div>

                    <div class="flex flex-col gap-2">
                        <div class="flex flex-row items-center gap-2">
                            <label class="flex justify-end items-center text-gray-600" for="selectedEmployee.employeeCode" style="width: 30%">사원 코드</label>
                            <InputText id="selectedEmployee.employeeCode" v-model="selectedEmployee.employeeCode" class="flex-1" />
                        </div>
                        <div class="flex flex-row items-center gap-2">
                            <label class="flex justify-end items-center text-gray-600" for="selectedEmployee.employeeName" style="width: 30%">이름</label>
                            <InputText id="selectedEmployee.employeeName" v-model="selectedEmployee.employeeName" class="flex-1" />
                        </div>
                        <div class="flex flex-row items-center gap-2">
                            <label class="flex justify-end items-center text-gray-600" for="selectedEmployee.department" style="width: 30%">부서</label>
                            <InputText id="selectedEmployee.department" v-model="selectedEmployee.department" class="flex-1" />
                        </div>
                        <div class="flex flex-row items-center gap-2">
                            <label class="flex justify-end items-center text-gray-600" for="selectedEmployee.teamName" style="width: 30%">팀</label>
                            <InputText id="selectedEmployee.teamName" v-model="selectedEmployee.teamName" class="flex-1" />
                        </div>
                    </div>
                </div>

                <div class="flex flex-col w-1/2 p-3 gap-2">
                    <p><strong>사원 코드:</strong> {{ selectedEmployee.employeeCode }}</p>
                    <p><strong>이름:</strong> {{ selectedEmployee.employeeName }}</p>
                    <p><strong>부서:</strong> {{ selectedEmployee.department }}</p>
                    <p><strong>팀:</strong> {{ selectedEmployee.teamName }}</p>
                    <p><strong>직책:</strong> {{ selectedEmployee.position }}</p>
                    <p><strong>입사일:</strong> {{ selectedEmployee.hireDate | dateFormat }}</p>
                </div>
            </div>
            <div class="flex justify-end gap-2 mt-4">
                <Button class="p-button p-component p-button-success" @click="saveChanges">저장</Button>
                <Button class="p-button p-component p-button-danger" @click="hideDialog">취소</Button>
            </div>
        </div>
    </Dialog>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Dialog from 'primevue/dialog';

const employees = ref([]);
const filteredEmployees = ref([]);
const selectedEmployee = ref(null);
const displayEditDialog = ref(false);
const selectedDepartment = ref(null);
const selectedTeam = ref(null);
const globalFilter = ref('');

const departments = ref([{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '인사' }]);
const teams = ref([{ name: '전체' }, { name: '팀A' }, { name: '팀B' }, { name: '팀C' }]);

async function fetchEmployees() {
    employees.value = [
        { employeeId: 1, employeeCode: 'E001', employeeName: '김철수', department: '개발', teamName: '팀A', position: '개발자', hireDate: '2022-01-01' },
        { employeeId: 2, employeeCode: 'E002', employeeName: '박영희', department: '디자인', teamName: '팀B', position: '디자이너', hireDate: '2021-02-01' },
        { employeeId: 3, employeeCode: 'E003', employeeName: '이민호', department: '인사', teamName: '팀C', position: '팀장', hireDate: '2020-03-01' },
        { employeeId: 4, employeeCode: 'E004', employeeName: '최지우', department: '개발', teamName: '팀A', position: '프론트엔드 개발자', hireDate: '2019-04-01' },
        { employeeId: 5, employeeCode: 'E005', employeeName: '강호동', department: '인사', teamName: '팀C', position: 'HR 매니저', hireDate: '2018-05-01' },
        { employeeId: 6, employeeCode: 'E006', employeeName: '정해인', department: '디자인', teamName: '팀B', position: 'UI/UX 디자이너', hireDate: '2020-06-01' },
        { employeeId: 7, employeeCode: 'E007', employeeName: '김하늘', department: '개발', teamName: '팀A', position: '백엔드 개발자', hireDate: '2021-07-01' },
        { employeeId: 8, employeeCode: 'E008', employeeName: '윤아름', department: '개발', teamName: '팀A', position: '데브옵스 엔지니어', hireDate: '2022-02-15' },
        { employeeId: 9, employeeCode: 'E009', employeeName: '이수민', department: '디자인', teamName: '팀B', position: '그래픽 디자이너', hireDate: '2021-04-10' },
        { employeeId: 10, employeeCode: 'E010', employeeName: '정서윤', department: '인사', teamName: '팀C', position: '채용 담당자', hireDate: '2020-11-23' },
        { employeeId: 11, employeeCode: 'E011', employeeName: '최민수', department: '개발', teamName: '팀A', position: '풀스택 개발자', hireDate: '2019-08-05' },
        { employeeId: 12, employeeCode: 'E012', employeeName: '박민준', department: '디자인', teamName: '팀B', position: 'UX 리서처', hireDate: '2021-07-14' },
        { employeeId: 13, employeeCode: 'E013', employeeName: '김하린', department: '인사', teamName: '팀C', position: '교육 담당자', hireDate: '2019-03-20' },
        { employeeId: 14, employeeCode: 'E014', employeeName: '송지호', department: '개발', teamName: '팀A', position: '프론트엔드 개발자', hireDate: '2022-06-01' },
        { employeeId: 15, employeeCode: 'E015', employeeName: '백예린', department: '디자인', teamName: '팀B', position: 'UI 디자이너', hireDate: '2021-12-30' },
        { employeeId: 16, employeeCode: 'E016', employeeName: '최윤서', department: '인사', teamName: '팀C', position: 'HR 어드바이저', hireDate: '2020-09-12' },
        { employeeId: 17, employeeCode: 'E017', employeeName: '한지민', department: '개발', teamName: '팀A', position: '데이터 엔지니어', hireDate: '2022-10-05' }
    ];

    // 필터링된 데이터를 적용
    filteredEmployees.value = employees.value;
}

function filterEmployees() {
    filteredEmployees.value = employees.value.filter((employee) => {
        const globalSearchText = globalFilter.value?.toLowerCase() || '';

        const matchesGlobalFilter = globalSearchText
            ? employee.employeeName.toLowerCase().includes(globalSearchText) ||
              employee.employeeCode.toLowerCase().includes(globalSearchText) ||
              employee.department.toLowerCase().includes(globalSearchText) ||
              employee.teamName.toLowerCase().includes(globalSearchText)
            : true;

        const matchesDepartment = selectedDepartment.value?.name !== '전체' ? employee.department === selectedDepartment.value.name : true;
        const matchesTeam = selectedTeam.value?.name !== '전체' ? employee.teamName === selectedTeam.value.name : true;

        return matchesGlobalFilter && matchesDepartment && matchesTeam;
    });
}

function showEditDialog(event) {
    selectedEmployee.value = event.data;
    displayEditDialog.value = true;
}

function hideDialog() {
    displayEditDialog.value = false;
}

onBeforeMount(() => {
    fetchEmployees();
});
</script>
