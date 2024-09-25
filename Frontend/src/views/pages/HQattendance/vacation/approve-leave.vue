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
                            <InputText v-model="filters['global'].value" placeholder="Search" />
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
                <div>
                    <label for="emergencyContact" class="block font-bold mb-2">비상 연락처</label>
                    <p id="emergencyContact">{{ employee.emergencyContact }}</p>
                </div>
                <div>
                    <label for="leavePeriod" class="block font-bold mb-2">휴가 기간</label>
                    <p id="leavePeriod">{{ employee.leavePeriod }}</p>
                </div>
            </div>

            <template #footer>
                <Button label="닫기" icon="pi pi-times" class="close-button" @click="infoDialog = false" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { FilterMatchMode } from '@primevue/core/api';
import { useToast } from 'primevue/usetoast';
import { onMounted, ref } from 'vue';

onMounted(() => {
    employees.value = [
        { id: '1', name: '홍길동', department: '인사', leaveStatus: '연차', approver: '김철수', emergencyContact: '010-1234-5678', leavePeriod: '2024-09-01 ~ 2024-09-05' },
        { id: '2', name: '이몽룡', department: '경영', leaveStatus: '오후 반차', approver: '이영희', emergencyContact: '010-2345-6789', leavePeriod: '2024-09-03 ~ 2024-09-03' },
        { id: '3', name: '박지민', department: 'IT', leaveStatus: '병가', approver: '최유리', emergencyContact: '010-3456-7890', leavePeriod: '2024-09-10 ~ 2024-09-12' },
        { id: '4', name: '김유나', department: '재무', leaveStatus: '경조', approver: '정수현', emergencyContact: '010-4567-8901', leavePeriod: '2024-09-15 ~ 2024-09-16' },
        { id: '5', name: '이재훈', department: '마케팅', leaveStatus: '병가', approver: '박지민', emergencyContact: '010-5678-9012', leavePeriod: '2024-09-20 ~ 2024-09-25' },
        { id: '6', name: '하나', department: '인사', leaveStatus: '오후 반차', approver: '정우성', emergencyContact: '010-6789-0123', leavePeriod: '2024-09-28 ~ 2024-10-02' },
        { id: '7', name: '서준', department: '경영', leaveStatus: '연차', approver: '이영희', emergencyContact: '010-7890-1234', leavePeriod: '2024-09-18 ~ 2024-09-20' },
        { id: '8', name: '민서', department: 'IT', leaveStatus: '오전 반차', approver: '김철수', emergencyContact: '010-8901-2345', leavePeriod: '2024-10-01 ~ 2024-10-02' },
        { id: '9', name: '상현', department: '재무', leaveStatus: '병가', approver: '최유리', emergencyContact: '010-9012-3456', leavePeriod: '2024-10-10 ~ 2024-10-15' },
        { id: '10', name: '하람', department: '마케팅', leaveStatus: '연차', approver: '정수현', emergencyContact: '010-0123-4567', leavePeriod: '2024-10-05 ~ 2024-10-07' },
    ];
});

const toast = useToast();
const employees = ref([]);
const employee = ref({});
const infoDialog = ref(false);
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
            return 'danger';
        case '오후 반차':
            return 'warning';
        case '오전 반차':
            return 'warning';
        case '병가':
            return 'danger';
        case '경조':
            return 'info';
        default:
            return null;
    }
}
</script>

<style scoped>
.confirm-button {
    background-color: #28a745; /* 기본 초록색 */
    border: 1px solid #28a745;
    color: white !important;
}

.confirm-button:hover {
    background-color: #218838 !important; /* 짙은 초록색 */
    border: 1px solid #218838 !important; /* 짙은 초록색 테두리 */
}

.close-button {
    background-color: #dc3545; /* 빨간색 */
    border: 1px solid #dc3545;
    color: white !important;
}

.close-button:hover {
    background-color: #c82333 !important; /* 짙은 빨간색 */
    border: 1px solid #c82333 !important; /* 짙은 빨간색 테두리 */
}
</style>
