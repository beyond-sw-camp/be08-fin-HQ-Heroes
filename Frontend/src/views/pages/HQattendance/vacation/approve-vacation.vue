<template>
    <div>
        <div class="card">
            <DataTable
                ref="dt"
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
                        <h4 class="m-0 title">휴가 관리</h4>
                        <IconField>
                            <InputIcon>
                                <i class="pi pi-search" />
                            </InputIcon>
                            <InputText v-model="filters['global'].value" placeholder="검색" />
                        </IconField>
                    </div>
                </template>

                <Column field="employeeName" header="이름" sortable style="min-width: 5rem"></Column>
                <Column field="vacationType" header="휴가 종류" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStart" header="시작일" sortable style="min-width: 5rem"></Column>
                <Column field="vacationEnd" header="종료일" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStartTime" header="시작 시간" sortable style="min-width: 5rem" v-if="selectedEmployee.vacationType !== '월차'"></Column>
                <Column field="vacationEndTime" header="종료 시간" sortable style="min-width: 5rem" v-if="selectedEmployee.vacationType !== '월차'"></Column>
                <Column field="approverName" header="결재자" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStatus" header="상태" sortable style="min-width: 5rem"></Column>

                <!-- 승인/반려 버튼 (관리자나 팀장만 표시) -->
                <Column v-if="isAdminOrTeamLead" header="승인/반려" style="min-width: 8rem">
                    <template #body="slotProps">
                        <Button label="승인" :disabled="isLoading" @click="approveVacation(slotProps.data.vacationId)" class="p-button-success" />
                        <Button label="반려" :disabled="isLoading" @click="rejectVacation(slotProps.data.vacationId)" class="p-button-danger" />
                    </template>
                </Column>
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
import { useToast } from 'primevue/usetoast';
import { computed, onMounted, ref } from 'vue';
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService'; // 수정된 fetch 함수 사용

const employees = ref([]);
const filters = ref({ global: { value: null } });
const selectedEmployee = ref({});
const infoDialog = ref(false);
const toast = useToast();
const isAdminOrTeamLead = ref(false); // 관리자 또는 팀장 여부 확인

// 관리자나 팀장일 경우 승인/반려된 항목을 제외하고 대기중인 휴가만 필터링
const filteredEmployees = computed(() => {
    if (isAdminOrTeamLead.value) {
        return employees.value.filter((employee) => employee.vacationStatus === '대기 중');
    }
    return employees.value; // 사원인 경우 모든 항목을 표시
});

onMounted(async () => {
    try {
        // 역할과 포지션 정보를 가져와서 관리자 또는 팀장 여부 확인
        const roleResponse = await fetchGet('http://localhost:8080/api/v1/employee/role-check');
        const { role, positionId } = roleResponse;

        if (role === 'ROLE_ADMIN' || (role === 'ROLE_USER' && positionId === 1)) {
            isAdminOrTeamLead.value = true; // 관리자나 팀장일 경우 승인/반려 버튼 보이도록 설정
        }

        // 휴가 목록 불러오기
        const response = await fetchGet('http://localhost:8080/api/v1/vacation/list');
        employees.value = response.map((record) => ({
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

const isLoading = ref(false); // 로딩 상태 변수

// 휴가 승인
async function approveVacation(vacationId) {
    if (isLoading.value) return; // 이미 로딩 중이면 중복 요청 방지
    isLoading.value = true; // 로딩 시작

    try {
        await fetchPost(`http://localhost:8080/api/v1/vacation/approve/${vacationId}`);
        employees.value = employees.value.map((emp) => (emp.vacationId === vacationId ? { ...emp, vacationStatus: '승인됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '휴가가 승인되었습니다.' });

        // 승인 후 새로고침
        window.location.reload(); // 페이지 새로고침
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '휴가 승인 실패.' });
    } finally {
        isLoading.value = false; // 로딩 완료
    }
}

// 휴가 반려
async function rejectVacation(vacationId) {
    if (isLoading.value) return; // 이미 로딩 중이면 중복 요청 방지
    isLoading.value = true; // 로딩 시작

    try {
        await fetchPost(`http://localhost:8080/api/v1/vacation/reject/${vacationId}`);
        employees.value = employees.value.map((emp) => (emp.vacationId === vacationId ? { ...emp, vacationStatus: '반려됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '휴가가 반려되었습니다.' });

        // 반려 후 새로고침
        window.location.reload(); // 페이지 새로고침
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '휴가 반려 실패.' });
    } finally {
        isLoading.value = false; // 로딩 완료
    }
}

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
