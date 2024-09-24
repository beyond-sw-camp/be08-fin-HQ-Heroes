<template>
    <div>
        <div class="card">
            <DataTable
                ref="dt"
                v-model:selection="selectedEmployees"
                :value="employees"
                dataKey="id"
                :paginator="true"
                :rows="10"
                :filters="filters"
                paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                :rowsPerPageOptions="[5, 10, 25]"
                currentPageReportTemplate="총 {totalRecords}명의 사원 중 {first} ~ {last}명 표시"
            >
                <template #header>
                    <div class="flex flex-wrap gap-2 items-center justify-between">
                        <h4 class="m-0">연차 관리</h4>
                        <IconField>
                            <InputIcon>
                                <i class="pi pi-search" />
                            </InputIcon>
                            <InputText v-model="filters['global'].value" placeholder="검색..." />
                        </IconField>
                    </div>
                </template>

                <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
                <Column field="name" header="이름" sortable style="min-width: 16rem"></Column>
                <Column field="department" header="부서" sortable style="min-width: 10rem"></Column>
                <Column field="leaveStatus" header="상태" sortable style="min-width: 12rem">
                    <template #body="slotProps">
                        <Tag :value="slotProps.data.leaveStatus" :severity="getStatusLabel(slotProps.data.leaveStatus)" />
                    </template>
                </Column>
                <Column field="approver" header="1차 결제자" sortable style="min-width: 12rem"></Column>
                <Column :exportable="false" style="min-width: 12rem">
                    <template #body="slotProps">
                        <Button label="확인하기" class="confirm-button mr-2" @click="showEmployeeInfo(slotProps.data)" />
                    </template>
                </Column>
            </DataTable>
        </div>

        <!-- 정보만 표시하는 다이얼로그 -->
        <Dialog v-model:visible="infoDialog" :style="{ width: '450px' }" header="사원 정보" :modal="true">
            <div class="flex flex-col gap-4">
                <div>
                    <label for="name" class="block font-bold mb-2">이름</label>
                    <p id="name">{{ employee.name }}</p>
                </div>
                <div>
                    <label for="department" class="block font-bold mb-2">부서</label>
                    <p id="department">{{ employee.department }}</p>
                </div>
                <div>
                    <label for="leaveStatus" class="block font-bold mb-2">상태</label>
                    <p id="leaveStatus">{{ employee.leaveStatus }}</p>
                </div>
                <div>
                    <label for="approver" class="block font-bold mb-2">1차 결제자</label>
                    <p id="approver">{{ employee.approver }}</p>
                </div>
            </div>

            <template #footer>
                <Button label="닫기" icon="pi pi-times" @click="infoDialog = false" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { FilterMatchMode } from '@primevue/core/api';
import { useToast } from 'primevue/usetoast';
import { onMounted, ref } from 'vue';

// EmployeeService 주석 처리
// import { EmployeeService } from './service/EmployeeService';

onMounted(() => {
    // 더미 데이터 추가 (나중에 실제 서비스로 교체)
    employees.value = [
        { id: '1', name: '홍길동', department: '인사', leaveStatus: '연차', approver: '김철수' },
        { id: '2', name: '이몽룡', department: '경영', leaveStatus: '오후 반차', approver: '이영희' },
        { id: '3', name: '박지민', department: 'IT', leaveStatus: '병가', approver: '최유리' },
        { id: '4', name: '김유나', department: '재무', leaveStatus: '경조', approver: '정수현' },
        { id: '5', name: '이재훈', department: '마케팅', leaveStatus: '가족돌봄', approver: '박지민' },
        { id: '6', name: '하나', department: '인사', leaveStatus: '난임 치료', approver: '정우성' },
        { id: '7', name: '서준', department: '경영', leaveStatus: '결혼 - 본인', approver: '이영희' },
        { id: '8', name: '민서', department: 'IT', leaveStatus: '결혼 - 자녀', approver: '김철수' },
        { id: '9', name: '상현', department: '재무', leaveStatus: '리프레시', approver: '최유리' },
        { id: '10', name: '하람', department: '마케팅', leaveStatus: '비상', approver: '정수현' },
    ];
});

const toast = useToast();
const employees = ref([]);
const employee = ref({});
const infoDialog = ref(false);  // 정보만 표시하는 다이얼로그
const filters = ref({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS }
});
const selectedEmployees = ref([]);

function showEmployeeInfo(emp) {
    employee.value = { ...emp };
    infoDialog.value = true;
}

function getStatusLabel(status) {
    switch (status) {
        case '연차':
            return 'danger';  // 초록색
        case '오후 반차':
            return 'warning';  // 노란색
        case '오전 반차':
            return 'warning';  // 노란색
        case '병가':
            return 'danger';  // 빨간색
        case '경조':
            return 'info';    // 파란색
        case '가족돌봄':
            return 'info';    // 파란색
        case '난임 치료':
            return 'info';    // 파란색
        case '결혼 - 본인':
            return 'success';  // 초록색
        case '결혼 - 자녀':
            return 'success';  // 초록색
        case '리프레시':
            return 'info';    // 파란색
        case '비상':
            return 'danger';  // 빨간색
        default:
            return null;
    }
}
</script>

<style scoped>
.confirm-button {
    background-color: #28a745; /* 초록색 */
    color: white;
}
.confirm-button:hover {
    background-color: white; /* 마우스 오버 시 하얀색 */
    color: #28a745; /* 글자색 초록색 유지 */
    border: 1px solid #28a745; /* 초록색 테두리 */
}
</style>
