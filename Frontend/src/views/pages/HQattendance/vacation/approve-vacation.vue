<template>
    <div>
        <div class="card">
            <DataTable
                ref="dt"
                :value="filteredEmployees"
                dataKey="vacationId"
                :paginator="true"
                :rows="10"
                :filters="filters"
                paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport"
                currentPageReportTemplate=""
            >
                <template #header>
                    <div class="flex flex-wrap gap-2 items-center justify-between">
                        <h4 class="m-0 title">휴가 결재</h4>
                        <IconField>
                            <InputIcon>
                                <i class="pi pi-search" />
                            </InputIcon>
                            <InputText v-model="filters['global'].value" placeholder="검색어를 입력해주세요" />
                        </IconField>
                    </div>
                </template>

                <Column field="applicantName" header="이름" sortable style="min-width: 5rem"></Column>
                <Column field="vacationType" header="휴가 종류" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStart" header="시작일" sortable style="min-width: 5rem"></Column>
                <Column field="vacationEnd" header="종료일" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStartTime" header="시작 시간" sortable style="min-width: 5rem" v-if="selectedEmployee.vacationType !== '월차'"></Column>
                <Column field="vacationEndTime" header="종료 시간" sortable style="min-width: 5rem" v-if="selectedEmployee.vacationType !== '월차'"></Column>
                <Column field="approverName" header="결재자" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStatus" header="상태" sortable style="min-width: 5rem"></Column>

                <!-- 승인/반려 버튼 (항상 표시되도록 수정) -->
                <Column header="승인/반려" style="min-width: 8rem">
                    <template #body="slotProps">
                        <div v-if="slotProps.data.vacationStatus === '대기 중'">
                            <Button label="승인" :disabled="isLoading" @click="approveVacation(slotProps.data.vacationId)" class="p-button-success mr-2" />
                            <Button label="반려" :disabled="isLoading" @click="rejectVacation(slotProps.data.vacationId)" class="p-button-danger" />
                        </div>
                        <div v-else-if="slotProps.data.vacationStatus === '취소 대기중'">
                            <Button label="취소 승인" :disabled="isLoading" @click="approveCancelVacation(slotProps.data.vacationId)" class="p-button-info mr-2" />
                            <Button label="취소 반려" :disabled="isLoading" @click="rejectCancelVacation(slotProps.data.vacationId)" class="p-button-warning" />
                        </div>
                        <div v-else>
                            <span>처리 완료</span>
                        </div>
                    </template>
                </Column>
            </DataTable>
        </div>

        <Dialog v-model:visible="infoDialog" :style="{ width: '450px' }" header="휴가 정보" :modal="true">
            <div class="flex flex-col gap-4">
                <div>
                    <label for="applicantName" class="block font-bold mb-2">이름</label>
                    <p id="applicantName">{{ selectedEmployee.applicantName }}</p>
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
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService';

const employees = ref([]);
const filters = ref({ global: { value: null } });
const selectedEmployee = ref({});
const infoDialog = ref(false);
const toast = useToast();

// 대기중인 휴가 상태만 필터링한 직원 목록
const filteredEmployees = computed(() => {
    return employees.value.filter((emp) => emp.vacationStatus === '대기 중' || emp.vacationStatus === '취소 대기중');
});

// 관리자나 팀장과 상관없이 모든 사원이 볼 수 있는 페이지
onMounted(async () => {
    try {
        // 로그인한 사용자의 employeeId와 이름 가져오기
        const roleResponse = await fetchGet('https://hq-heroes-api.com/api/v1/employee/role-check');
        const loggedInEmployeeName = roleResponse.employeeName; // 이름 가져오기

        // 휴가 목록 불러오기
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/vacation/list');

        // approverName이 로그인한 사용자의 이름과 일치하는 항목만 필터링
        employees.value = response
            .filter((record) => record.approverName === loggedInEmployeeName) // 승인자 이름과 로그인한 사용자 이름 비교
            .map((record) => ({
                vacationId: record.vacationId,
                applicantName: record.applicantName,
                vacationType: mapVacationType(record.vacationType),
                vacationStart: new Date(record.vacationStartDate).toLocaleDateString(),
                vacationStartTime: record.vacationStartTime?.substring(0, 5),
                vacationEnd: new Date(record.vacationEndDate).toLocaleDateString(),
                vacationEndTime: record.vacationEndTime?.substring(0, 5),
                approverName: record.approverName,
                vacationStatus: mapStatus(record.vacationStatus)
            }));
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '데이터 로딩 중 문제가 발생했습니다.' });
    }
});

const isLoading = ref(false); // 로딩 상태 변수

// 휴가 승인
async function approveVacation(vacationId) {
    if (isLoading.value) return; // 중복 요청 방지
    isLoading.value = true;

    try {
        await fetchPost(`https://hq-heroes-api.com/api/v1/vacation/approve/${vacationId}`);
        employees.value = employees.value.map((emp) => (emp.vacationId === vacationId ? { ...emp, vacationStatus: '승인됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '휴가가 승인되었습니다.' });
        window.location.reload(); // 새로고침
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '휴가 승인 실패.' });
    } finally {
        isLoading.value = false;
    }
}

// 휴가 반려
async function rejectVacation(vacationId) {
    if (isLoading.value) return; // 중복 요청 방지
    isLoading.value = true;

    try {
        await fetchPost(`https://hq-heroes-api.com/api/v1/vacation/reject/${vacationId}`);
        employees.value = employees.value.map((emp) => (emp.vacationId === vacationId ? { ...emp, vacationStatus: '반려됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '휴가가 반려되었습니다.' });
        window.location.reload();
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '휴가 반려 실패.' });
    } finally {
        isLoading.value = false;
    }
}

// 휴가 취소 승인
async function approveCancelVacation(vacationId) {
    if (isLoading.value) return; // 중복 요청 방지
    isLoading.value = true;

    try {
        await fetchPost(`https://hq-heroes-api.com/api/v1/vacation/approveCancel/${vacationId}`);
        employees.value = employees.value.map((emp) => (emp.vacationId === vacationId ? { ...emp, vacationStatus: '취소됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '휴가 취소가 승인되었습니다.' });
        window.location.reload(); // 새로고침
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '휴가 취소 승인 실패.' });
    } finally {
        isLoading.value = false;
    }
}

// 휴가 취소 반려
async function rejectCancelVacation(vacationId) {
    if (isLoading.value) return; // 중복 요청 방지
    isLoading.value = true;

    try {
        await fetchPost(`https://hq-heroes-api.com/api/v1/vacation/rejectCancel/${vacationId}`);
        employees.value = employees.value.map((emp) => (emp.vacationId === vacationId ? { ...emp, vacationStatus: '취소 반려됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '휴가 취소가 반려되었습니다.' });
        window.location.reload(); // 새로고침
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '휴가 취소 반려 실패.' });
    } finally {
        isLoading.value = false;
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
        case 'CANCEL':
            return '취소 대기중';
        case 'CANCEL_APPROVED':
            return '취소됨';
        case 'CANCEL_REJECTED':
            return '취소 반려됨';
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
