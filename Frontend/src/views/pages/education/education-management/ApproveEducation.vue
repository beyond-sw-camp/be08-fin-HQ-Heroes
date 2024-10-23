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
                        <h4 class="m-0 title">교육 / 자격증 관리</h4>
                        <div class="flex gap-2">
                            <Button type="button" label="교육" outlined :class="{ active: selectedEducation === '교육' }" @click="filterByEducation('교육')" />
                            <Button type="button" label="자격증" outlined :class="{ active: selectedEducation === '자격증' }" @click="filterByEducation('자격증')" />
                        </div>
                        <IconField>
                            <InputIcon>
                                <i class="pi pi-search" />
                            </InputIcon>
                            <InputText 
                                v-model="filters['global'].value" 
                                placeholder="검색" 
                            />
                        </IconField>
                    </div>
                </template>

                <Column field="applicantName" header="이름" sortable style="min-width: 5rem"></Column>
                <Column field="educationCategory" header="교육 카테고리" sortable style="min-width: 5rem"></Column>
                <Column field="educationName" header="교육 이름" sortable style="min-width: 5rem"></Column>
                <Column field="educationStart" header="교육 시작일" sortable style="min-width: 5rem"></Column>
                <Column field="educationEnd" header="교육 종료일" sortable style="min-width: 5rem"></Column>

                <Column field="status" header="상태" sortable style="min-width: 8rem">
                    <template #body="slotProps">
                        <Button label="이수" :disabled="isLoading" @click="approveCourse(slotProps.data.educationId)" class="p-button-success" />
                    </template>
                </Column>
            </DataTable>
        </div>
        
        <Dialog v-model:visible="infoDialog" :style="{ width: '450px' }" header="교육 정보" :modal="true">
            <div class="flex flex-col gap-4">
                <div v-for="(value, key) in selectedEmployee" :key="key">
                    <label :for="key" class="block font-bold mb-2">{{ formatLabel(key) }}</label>
                    <p :id="key">{{ value }}</p>
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
const isLoading = ref(false);
const selectedEducation = ref(null);

// 대기중인 휴가 상태만 필터링한 직원 목록
const filteredEmployees = computed(() => {
    const result = employees.value.filter((emp) => emp.status === '미이수');
    console.log('필터링된 직원 목록:', result); // 필터링된 직원 목록 로그
    return result;
});

onMounted(async () => {
    try {
        const roleResponse = await fetchGet('http://localhost:8080/api/v1/employee/role-check');
        const loggedInEmployeeName = roleResponse.employeeName;

        // 교육 목록 불러오기
        const response = await fetchGet('http://localhost:8080/api/v1/course-service/list');
        console.log('로딩된 교육 목록:', response);

        // approverName이 로그인한 사용자의 이름과 일치하는 항목만 필터링
        employees.value = response
            .filter((record) => record.approverName === loggedInEmployeeName)
            .map((record) => ({
                educationId: record.educationId,
                applicantName: record.applicantName,
                educationType: mapVacationType(record.educationType),
                educationStart: new Date(record.educationStart).toLocaleDateString(),
                educationEnd: new Date(record.educationEnd).toLocaleDateString(),
                status: mapStatus(record.status),
            }));

        console.log('각 직원의 상태:', employees.value.map(emp => ({ educationId: emp.educationId, status: emp.status })));

        console.log('최종 직원 목록:', employees.value);
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '데이터 로딩 중 문제가 발생했습니다.' });
        console.error('데이터 로딩 에러:', error);
    }
});

async function approveCourse(courseId) {
    if (isLoading.value) return;
    isLoading.value = true;

    try {
        await fetchPost(`http://localhost:8080/api/v1/course-service/complete/${courseId}`);
        employees.value = employees.value.map((emp) => (emp.educationId === courseId ? { ...emp, status: '승인됨' } : emp));
        toast.add({ severity: 'success', summary: 'Success', detail: '휴가가 승인되었습니다.' });
        window.location.reload();
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '휴가 승인 실패.' });
        console.error('휴가 승인 에러:', error); // 휴가 승인 에러 로그
    } finally {
        isLoading.value = false;
    }
}

// 휴가 상태를 한국어로 매핑하는 함수
function mapStatus(status) {
    switch (status) {
        case 'FAIL':
            return '미이수';
        case 'PASS':
            return '이수';
        default:
            return '알 수 없음';
    }
}
</script>

<style scoped>
.title {
    font-size: 1.5rem;
    font-weight: bold;
}

.card {
    padding: 2rem;
    margin: 2rem 0;
    background-color: #fff;
    box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.close-button {
    background-color: var(--primary-color);
    border: none;
    color: white;
}
</style>
