<template>
    <div>
        <div class="card">
            <DataTable
                ref="dt"
                :value="filteredEmployees"
                dataKey="overtimeId"
                :paginator="true"
                :rows="10"
                :filters="filters"
                paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport"
                currentPageReportTemplate=""
            >
                <template #header>
                    <div class="flex flex-wrap gap-2 items-center justify-between">
                        <h4 class="m-0 title">연장 근로 결재</h4>
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

                <!-- 승인/반려 버튼 (항상 표시되도록 수정) -->
                <Column header="승인/반려" style="min-width: 8rem">
                    <template #body="slotProps">
                        <Button label="승인" :disabled="isLoading" @click="approveOvertime(slotProps.data.overtimeId)" class="p-button-success mr-2" />
                        <Button label="반려" :disabled="isLoading" @click="rejectOvertime(slotProps.data.overtimeId)" class="p-button-danger" />
                    </template>
                </Column>
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
import { computed, onMounted, ref } from 'vue';
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService';

const employees = ref([]);
const filters = ref({ global: { value: null } });
const selectedEmployee = ref({});
const infoDialog = ref(false);
const toast = useToast();

// 대기중인 연장 근로 상태만 필터링한 직원 목록
const filteredEmployees = computed(() => {
    return employees.value.filter((emp) => emp.overtimeStatus === '대기 중');
});

// 관리자나 팀장과 상관없이 모든 사원이 볼 수 있는 페이지
onMounted(async () => {
    try {
        // 로그인한 사용자의 employeeId와 이름 가져오기
        const roleResponse = await fetchGet('https://hq-heroes-api.com/api/v1/employee/role-check');
        const loggedInEmployeeName = roleResponse.employeeName; // 이름 가져오기

        // 연장 근로 목록 불러오기
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/overtime/list');

        // approverName이 로그인한 사용자의 이름과 일치하는 항목만 필터링
        employees.value = response
            .filter((record) => record.approverName === loggedInEmployeeName) // 승인자 이름과 로그인한 사용자 이름 비교
            .map((record) => ({
                overtimeId: record.overtimeId,
                employeeName: record.employeeName,
                overtimeStart: new Date(record.overtimeStartDate).toLocaleDateString(),
                overtimeStartTime: record.overtimeStartTime?.substring(0, 5),
                overtimeEnd: new Date(record.overtimeEndDate).toLocaleDateString(),
                overtimeEndTime: record.overtimeEndTime?.substring(0, 5),
                approverName: record.approverName,
                overtimeStatus: mapStatus(record.overtimeStatus)
            }));
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '데이터 로딩 중 문제가 발생했습니다.' });
    }
});

const isLoading = ref(false); // 로딩 상태 변수

// 연장 근로 승인
async function approveOvertime(overtimeId) {
    if (isLoading.value) return; // 중복 요청 방지
    isLoading.value = true;

    try {
        await fetchPost(`https://hq-heroes-api.com/api/v1/overtime/approve/${overtimeId}`);
        employees.value = employees.value.map((emp) => (emp.overtimeId === overtimeId ? { ...emp, overtimeStatus: '승인됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '연장 근로가 승인되었습니다.' });
        window.location.reload(); // 새로고침
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '연장 근로 승인 실패.' });
    } finally {
        isLoading.value = false;
    }
}

// 연장 근로 반려
async function rejectOvertime(overtimeId) {
    if (isLoading.value) return; // 중복 요청 방지
    isLoading.value = true;

    try {
        await fetchPost(`https://hq-heroes-api.com/api/v1/overtime/reject/${overtimeId}`);
        employees.value = employees.value.map((emp) => (emp.overtimeId === overtimeId ? { ...emp, overtimeStatus: '반려됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '연장 근로가 반려되었습니다.' });
        window.location.reload();
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '연장 근로 반려 실패.' });
    } finally {
        isLoading.value = false;
    }
}

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
</script>

<style scoped>
.title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
}

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
