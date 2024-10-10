<template>
    <div>
        <div class="card">
            <DataTable
                ref="dt"
                v-model:selection="selectedEmployees"
                :value="employees"
                dataKey="vacationId"
                :paginator="true"
                :rows="10"
                :filters="filters"
                paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                :rowsPerPageOptions="[5, 10, 25]"
                currentPageReportTemplate="총 {totalRecords}명의 휴가 중 {first} ~ {last}명 표시"
            >
                <template #header>
                    <div class="flex flex-wrap gap-2 items-center justify-between">
                        <h4 class="m-0">휴가 관리</h4>
                        <IconField>
                            <InputIcon>
                                <i class="pi pi-search" />
                            </InputIcon>
                            <InputText v-model="filters['global'].value" placeholder="검색" />
                        </IconField>
                    </div>
                </template>

                <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
                <Column field="employeeName" header="이름" sortable style="min-width: 5rem"></Column>
                <Column field="vacationType" header="휴가 종류" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStart" header="시작일" sortable style="min-width: 5rem"></Column>
                <Column field="vacationEnd" header="종료일" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStartTime" header="시작 시간" sortable style="min-width: 5rem" v-if="selectedEmployee.vacationType !== '월차'"></Column>
                <Column field="vacationEndTime" header="종료 시간" sortable style="min-width: 5rem" v-if="selectedEmployee.vacationType !== '월차'"></Column>
                <Column field="approverName" header="결재자" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStatus" header="상태" sortable style="min-width: 5rem"></Column>
            </DataTable>
        </div>

        <Dialog v-model:visible="infoDialog" :style="{ width: '450px' }" header="휴가 정보" :modal="true">
            <div class="flex flex-col gap-4">
                <div>
                    <label for="employeeName" class="block font-bold mb-2">이름</label>
                    <p id="employeeName">{{ selectedEmployee.employeeName }}</p>
                </div>
                <div>
                    <label for="vacationType" class="block font-bold mb-2">휴가 종류</label>
                    <p id="vacationType">{{ selectedEmployee.vacationType }}</p>
                </div>
                <div>
                    <label for="vacationStart" class="block font-bold mb-2">시작일</label>
                    <p id="vacationStart">{{ selectedEmployee.vacationStart }}</p>
                </div>
                <div v-if="selectedEmployee.vacationType !== '월차'">
                    <label for="vacationStartTime" class="block font-bold mb-2">시작 시간</label>
                    <p id="vacationStartTime">{{ selectedEmployee.vacationStartTime }}</p>
                </div>
                <div>
                    <label for="vacationEnd" class="block font-bold mb-2">종료일</label>
                    <p id="vacationEnd">{{ selectedEmployee.vacationEnd }}</p>
                </div>
                <div v-if="selectedEmployee.vacationType !== '월차'">
                    <label for="vacationEndTime" class="block font-bold mb-2">종료 시간</label>
                    <p id="vacationEndTime">{{ selectedEmployee.vacationEndTime }}</p>
                </div>
                <div>
                    <label for="approverName" class="block font-bold mb-2">결재자</label>
                    <p id="approverName">{{ selectedEmployee.approverName }}</p>
                </div>
                <div>
                    <label for="vacationStatus" class="block font-bold mb-2">상태</label>
                    <p id="vacationStatus">{{ selectedEmployee.vacationStatus }}</p>
                </div>
            </div>
            <template #footer>
                <Button label="닫기" icon="pi pi-times" class="close-button" @click="infoDialog = false" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import axios from 'axios';
import { useToast } from 'primevue/usetoast';
import { onMounted, ref } from 'vue';

const employees = ref([]);
const filters = ref({ global: { value: null } });
const selectedEmployee = ref({});
const infoDialog = ref(false);
const toast = useToast();

onMounted(async () => {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/vacation/list');
        console.log(response.data); // 응답 데이터 출력
        employees.value = response.data.map((record) => ({
            vacationId: record.vacationId,
            employeeName: record.employeeName, // 신청인 이름
            vacationType: mapVacationType(record.vacationType), // 휴가 종류 (한국어로 매핑)
            vacationStart: new Date(record.vacationStartDate).toLocaleDateString(), // 시작일
            vacationStartTime: record.vacationStartTime.substring(0, 5), // 시작 시간 (시간과 분만 표시)
            vacationEnd: new Date(record.vacationEndDate).toLocaleDateString(), // 종료일
            vacationEndTime: record.vacationEndTime.substring(0, 5), // 종료 시간 (시간과 분만 표시)
            approverName: record.approverName, // 결재자 이름
            vacationStatus: mapStatus(record.vacationStatus) // 상태 매핑 (한국어로 매핑)
        }));
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '데이터 로딩 중 문제가 발생했습니다.' });
    }
});

// 휴가 상태를 한국어로 매핑하는 함수
function mapStatus(status) {
    switch (status) {
        case 'APPROVED':
            return '승인됨';
        case 'REJECTED':
            return '반려됨';
        case 'PENDING':
            return '대기 중';
        default:
            return '알 수 없음';
    }
}

// 휴가 종류를 한국어로 매핑하는 함수
function mapVacationType(vacationType) {
    switch (vacationType) {
        case 'DAY_OFF':
            return '월차';
        case 'HALF_DAY_OFF':
            return '반차';
        case 'SICK_LEAVE':
            return '병가';
        case 'EVENT_LEAVE':
            return '경조';
        default:
            return '기타';
    }
}

function showEmployeeInfo(employee) {
    selectedEmployee.value = employee;
    infoDialog.value = true;
}
</script>

<style scoped>
.custom-button {
    background-color: #6366f1;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.custom-button:hover {
    background-color: #4f46e5;
}

.close-button {
    background-color: #dc3545;
    border: 1px solid #dc3545;
    color: white !important;
}

.close-button:hover {
    background-color: #c82333 !important;
    border: 1px solid #c82333 !important;
}
</style>
