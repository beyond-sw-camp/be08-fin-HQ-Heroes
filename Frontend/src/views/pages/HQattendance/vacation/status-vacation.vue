<template>
    <div>
        <div class="card">
            <DataTable ref="dt" :value="employees" dataKey="vacationId" :paginator="true" :rows="10" :filters="filters" paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport" currentPageReportTemplate="">
                <template #header>
                    <div class="flex flex-wrap gap-2 items-center justify-between">
                        <h4 class="m-0 title">휴가 신청 현황</h4>
                        <IconField>
                            <InputIcon>
                                <i class="pi pi-search" />
                            </InputIcon>
                            <InputText v-model="filters['global'].value" placeholder="검색어를 입력해주세요 " />
                        </IconField>
                    </div>
                </template>

                <Column field="vacationType" header="휴가 종류" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStart" header="시작일" sortable style="min-width: 5rem"></Column>
                <Column field="vacationEnd" header="종료일" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStartTime" header="시작 시간" sortable style="min-width: 5rem"></Column>
                <Column field="vacationEndTime" header="종료 시간" sortable style="min-width: 5rem"></Column>
                <Column field="approverName" header="결재자" sortable style="min-width: 5rem"></Column>
                <Column field="vacationStatus" header="상태" sortable style="min-width: 5rem"></Column>

                <!-- 취소 버튼 추가 -->
                <Column header="휴가 취소" style="min-width: 5rem">
                    <template #body="slotProps">
                        <!-- 현재 날짜보다 시작일이 이후이면서 상태가 '반려됨', '취소됨', '취소 대기중'이 아닌 경우에만 취소 버튼 보이기 -->
                        <Button
                            v-if="
                                !isPastDate(slotProps.data.vacationStart) &&
                                slotProps.data.vacationStatus !== '반려됨' &&
                                slotProps.data.vacationStatus !== '취소됨' &&
                                slotProps.data.vacationStatus !== '취소 대기중' &&
                                slotProps.data.vacationStatus !== '취소 반려됨'
                            "
                            label="취소"
                            class="p-button-danger"
                            @click="confirmCancel(slotProps.data)"
                        />
                        <div v-else style="height: 2.5rem"></div>
                        <!-- 빈 div로 높이 맞추기 -->
                    </template>
                </Column>
            </DataTable>
        </div>

        <!-- 정보 모달 -->
        <Dialog v-model:visible="infoDialog" :style="{ width: '450px' }" header="휴가 정보" :modal="true">
            <div class="flex flex-col gap-4">
                <div>
                    <label for="employeeName" class="block font-bold mb-2">이름</label>
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
                <div>
                    <label for="vacationStartTime" class="block font-bold mb-2">시작 시간</label>
                    <p id="vacationStartTime">{{ selectedEmployee.vacationStartTime }}</p>
                </div>
                <div>
                    <label for="vacationEnd" class="block font-bold mb-2">종료일</label>
                    <p id="vacationEnd">{{ selectedEmployee.vacationEnd }}</p>
                </div>
                <div>
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

        <Dialog v-model:visible="cancelDialogVisible" :style="{ width: '450px' }" header="휴가 취소 요청" :modal="true">
            <div class="flex flex-col gap-4">
                <p><strong>휴가 종류:</strong> {{ selectedEmployee.vacationType }}</p>
                <p><strong>시작일:</strong> {{ selectedEmployee.vacationStart }}</p>
                <p><strong>종료일:</strong> {{ selectedEmployee.vacationEnd }}</p>
                <!-- 월차가 아닌 경우에만 시작 시간 표시 -->
                <p v-if="selectedEmployee.vacationType !== '월차'"><strong>시작 시간:</strong> {{ selectedEmployee.vacationStartTime }}</p>
                <!-- 월차가 아닌 경우에만 종료 시간 표시 -->
                <p v-if="selectedEmployee.vacationType !== '월차'"><strong>종료 시간:</strong> {{ selectedEmployee.vacationEndTime }}</p>
                <p><strong>결재자:</strong> {{ selectedEmployee.approverName }}</p>
            </div>
            <template #footer>
                <Button label="제출" icon="pi pi-check" class="p-button-success" @click="submitCancelRequest" />
                <Button label="닫기" icon="pi pi-times" class="p-button-secondary" @click="cancelDialogVisible = false" />
            </template>
        </Dialog>

        <!-- 대기 중인 휴가 취소 확인 모달 -->
        <Dialog v-model:visible="confirmDialogVisible" :style="{ width: '450px' }" header="휴가 취소" :modal="true">
            <div class="flex flex-col gap-4">
                <p>정말로 이 휴가를 취소하시겠습니까?</p>
                <p><strong>휴가 종류:</strong> {{ selectedEmployee.vacationType }}</p>
                <p><strong>시작일:</strong> {{ selectedEmployee.vacationStart }}</p>
                <p><strong>종료일:</strong> {{ selectedEmployee.vacationEnd }}</p>
            </div>
            <template #footer>
                <Button label="확인" icon="pi pi-check" class="p-button-success" @click="submitImmediateCancelRequest" />
                <Button label="취소" icon="pi pi-times" class="p-button-secondary" @click="confirmDialogVisible = false" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { useToast } from 'primevue/usetoast';
import { onMounted, ref } from 'vue';
import { fetchDelete, fetchGet, fetchPost } from '../../auth/service/AuthApiService'; // 수정된 fetch 함수 사용

const employees = ref([]);
const filters = ref({ global: { value: null } });
const selectedEmployee = ref({});
const infoDialog = ref(false);
const cancelDialogVisible = ref(false);
const confirmDialogVisible = ref(false);
const cancelReason = ref('');
const toast = useToast();

onMounted(async () => {
    try {
        const roleResponse = await fetchGet('https://hq-heroes-api.com/api/v1/employee/role-check');
        const loggedInEmployeeId = roleResponse.employeeId;

        // 휴가 목록 불러오기
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/vacation/list');
        employees.value = response
            .filter((record) => record.applicantId === loggedInEmployeeId)
            .map((record) => ({
                vacationId: record.vacationId,
                applicantName: record.applicantName,
                vacationType: mapVacationType(record.vacationType),
                vacationStart: record.vacationStartDate.split('T')[0],
                vacationStartTime: record.vacationStartTime.substring(0, 5),
                vacationEnd: record.vacationEndDate.split('T')[0],
                vacationEndTime: record.vacationEndTime.substring(0, 5),
                approverName: record.approverName,
                vacationStatus: mapStatus(record.vacationStatus)
            }))
            .sort((a, b) => new Date(b.vacationStart) - new Date(a.vacationStart)); // 최신순으로 정렬
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

// 취소 모달 열기
function openCancelModal(employee) {
    selectedEmployee.value = employee;
    cancelDialogVisible.value = true;
}

// 함수 추가: 날짜가 현재 날짜보다 이전인지 확인
function isPastDate(date) {
    const today = new Date();
    const formattedDate = new Date(date);
    // 날짜 비교를 위해 시간 정보를 제거합니다.
    return formattedDate.setHours(0, 0, 0, 0) < today.setHours(0, 0, 0, 0);
}

// 취소 모달 또는 확인 모달 열기
function confirmCancel(employee) {
    selectedEmployee.value = employee;

    // 대기 중 상태인 경우 확인 모달을 띄움, 그 외에는 기존 취소 모달 띄움
    if (employee.vacationStatus === '대기 중') {
        confirmDialogVisible.value = true;
    } else {
        cancelDialogVisible.value = true;
    }
}

// 취소 요청 제출
async function submitCancelRequest() {
    try {
        const requestBody = {
            vacationId: selectedEmployee.value.vacationId,
            approverId: selectedEmployee.value.approverId // 결재자 ID 전달
        };
        console.log(requestBody);
        await fetchPost('https://hq-heroes-api.com/api/v1/vacation/cancel', requestBody);
        console.log(requestBody);

        toast.add({ severity: 'success', summary: 'Success', detail: '휴가 취소 요청이 제출되었습니다.' });

        // 모달 닫기 및 초기화
        cancelDialogVisible.value = false;
        cancelReason.value = '';

        window.location.reload();
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '휴가 취소 요청 실패.' });
    }
}

// 즉시 취소 요청 제출 (대기 중 상태용)
async function submitImmediateCancelRequest() {
    try {
        // vacationId를 URL에 쿼리 파라미터로 포함하여 삭제 요청
        const url = `https://hq-heroes-api.com/api/v1/vacation/delete?vacationId=${selectedEmployee.value.vacationId}`;
        const response = await fetchDelete(url);

        if (response) {
            toast.add({ severity: 'success', summary: 'Success', detail: '대기 중인 휴가가 취소되었습니다.' });
            confirmDialogVisible.value = false;

            // 삭제된 휴가를 employees 리스트에서 제거
            employees.value = employees.value.filter((employee) => employee.vacationId !== selectedEmployee.value.vacationId);
            window.location.reload();
        } else {
            toast.add({ severity: 'error', summary: 'Error', detail: '휴가 취소 요청 실패.' });
        }
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '대기 중인 휴가 취소 실패.' });
    }
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

.p-button-danger {
    background-color: #dc3545 !important;
    border-color: #dc3545 !important;
}

.full-width {
    width: 100%;
}

.bordered-textarea {
    border: 1px solid #ddd;
    padding: 10px;
    border-radius: 4px;
    resize: none; /* 사용자가 크기를 조절할 수 없도록 설정 (선택 사항) */
}

.bordered-textarea:focus {
    border-color: #6366f1; /* 포커스 시 테두리 색상 변경 */
    outline: none; /* 기본 아웃라인 제거 */
}

.p-button-success {
    background-color: #6366f1;
    border-color: #6366f1;
    color: white;
}

.p-button-secondary {
    background-color: #dc3545;
    border-color: #dc3545;
    color: white;
}
</style>
