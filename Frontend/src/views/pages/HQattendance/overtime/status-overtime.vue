<template>
    <div>
        <div class="card">
            <DataTable ref="dt" :value="employees" dataKey="overtimeId" :paginator="true" :rows="10" :filters="filters" paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport" currentPageReportTemplate="">
                <template #header>
                    <div class="flex flex-wrap gap-2 items-center justify-between">
                        <h4 class="m-0 title">연장 근로 신청 현황</h4>
                        <IconField>
                            <InputIcon>
                                <i class="pi pi-search" />
                            </InputIcon>
                            <InputText v-model="filters['global'].value" placeholder="검색어를 입력해주세요" />
                        </IconField>
                    </div>
                </template>

                <Column field="employeeName" header="이름" sortable style="min-width: 5rem"></Column>
                <Column field="overtimeStart" header="시작일" sortable style="min-width: 5rem"></Column>
                <Column field="overtimeEnd" header="종료일" sortable style="min-width: 5rem"></Column>
                <Column field="overtimeStartTime" header="시작 시간" sortable style="min-width: 5rem"></Column>
                <Column field="overtimeEndTime" header="종료 시간" sortable style="min-width: 5rem"></Column>
                <Column field="approverName" header="결재자" sortable style="min-width: 5rem"></Column>
                <Column field="overtimeStatus" header="상태" sortable style="min-width: 5rem"></Column>
            </DataTable>
        </div>

        <Dialog v-model:visible="infoDialog" :style="{ width: '450px' }" header="연장 근로 정보" :modal="true">
            <div class="flex flex-col gap-4">
                <div>
                    <label for="employeeName" class="block font-bold mb-2">이름</label>
                    <p id="employeeName">{{ selectedEmployee.employeeName }}</p>
                </div>
                <div>
                    <label for="overtimeStart" class="block font-bold mb-2">시작일</label>
                    <p id="overtimeStart">{{ selectedEmployee.overtimeStart }}</p>
                </div>
                <div>
                    <label for="overtimeStartTime" class="block font-bold mb-2">시작 시간</label>
                    <p id="overtimeStartTime">{{ selectedEmployee.overtimeStartTime }}</p>
                </div>
                <div>
                    <label for="overtimeEnd" class="block font-bold mb-2">종료일</label>
                    <p id="overtimeEnd">{{ selectedEmployee.overtimeEnd }}</p>
                </div>
                <div>
                    <label for="overtimeEndTime" class="block font-bold mb-2">종료 시간</label>
                    <p id="overtimeEndTime">{{ selectedEmployee.overtimeEndTime }}</p>
                </div>
                <div>
                    <label for="approverName" class="block font-bold mb-2">결재자</label>
                    <p id="approverName">{{ selectedEmployee.approverName }}</p>
                </div>
                <div>
                    <label for="overtimeStatus" class="block font-bold mb-2">상태</label>
                    <p id="overtimeStatus">{{ selectedEmployee.overtimeStatus }}</p>
                </div>
            </div>
            <template #footer>
                <Button label="닫기" icon="pi pi-times" class="close-button" @click="infoDialog = false" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { useToast } from 'primevue/usetoast';
import { onMounted, ref } from 'vue';
import { fetchGet } from '../../auth/service/AuthApiService'; // 수정된 fetch 함수 사용

const employees = ref([]);
const filters = ref({ global: { value: null } });
const selectedEmployee = ref({});
const infoDialog = ref(false);
const toast = useToast();

onMounted(async () => {
    try {
        const roleResponse = await fetchGet('https://hq-heroes-api.com/api/v1/employee/role-check');
        const loggedInEmployeeId = roleResponse.employeeId;

        // 연장 근로 목록 불러오기
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/overtime/list');
        employees.value = response
            .filter((record) => record.employeeId === loggedInEmployeeId)
            .map((record) => ({
                overtimeId: record.overtimeId,
                employeeName: record.employeeName,
                overtimeStart: record.overtimeStartDate.split('T')[0],
                overtimeStartTime: record.overtimeStartTime.substring(0, 5),
                overtimeEnd: record.overtimeEndDate.split('T')[0],
                overtimeEndTime: record.overtimeEndTime.substring(0, 5),
                approverName: record.approverName,
                overtimeStatus: mapStatus(record.overtimeStatus)
            }));
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '데이터 로딩 중 문제가 발생했습니다.' });
    }
});

// 연장 근로 상태를 한국어로 매핑하는 함수
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

function showEmployeeInfo(employee) {
    selectedEmployee.value = employee;
    infoDialog.value = true;
}
</script>

<style scoped>
.title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
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
