<template>
    <div class="card">
        <div class="flex flex-row justify-between mb-4">
            <label class="text-xl font-bold">교육/자격증 승인 관리</label>
        </div>
        <div class="flex flex-wrap gap-2 items-center justify-between mb-4">
            <div class="flex items-center gap-2">
                <Button label="교육" :class="['p-button-outlined', { 'active-button': showEducation }]" @click="loadEducationRequests" />
                <Button label="자격증" :class="['p-button-outlined', { 'active-button': !showEducation }]" @click="loadCertificationRequests" />
            </div>
            <div class="flex items-center search-input-container">
                <i class="pi pi-search search-icon" />
                <InputText v-model="filters['global'].value" placeholder="검색어를 입력해주세요" class="search-input" />
            </div>
        </div>

        <DataTable
            ref="dt"
            :value="filteredRequests"
            dataKey="requestId"
            :paginator="true"
            :rows="10"
            :filters="filters"
            paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport"
            currentPageReportTemplate=""
        >
            <!-- 교육 컬럼 -->
            <Column v-if="showEducation" field="categoryName" header="카테고리" sortable style="min-width: 5rem"></Column>
            <Column v-if="showEducation" field="educationName" header="교육 이름" sortable style="min-width: 5rem"></Column>
            <Column v-if="showEducation" field="educationStart" header="시작일" sortable style="min-width: 5rem"></Column>
            <Column v-if="showEducation" field="educationEnd" header="종료일" sortable style="min-width: 5rem"></Column>
            <Column v-if="showEducation" field="employeeName" header="신청자" sortable style="min-width: 5rem"></Column>

            <!-- 자격증 컬럼 -->
            <Column v-if="!showEducation" field="certificationName" header="자격증 이름" sortable style="min-width: 5rem"></Column>
            <Column v-if="!showEducation" field="institution" header="발급기관" sortable style="min-width: 5rem"></Column>
            <Column v-if="!showEducation" field="acquisitionDate" header="취득일" sortable style="min-width: 5rem"></Column>
            <Column v-if="!showEducation" field="employeeName" header="신청자" sortable style="min-width: 5rem"></Column>

            <!-- 승인/반려 버튼 -->
            <Column v-if="showEducation" field="courseStatus" header="이수 상태" sortable style="min-width: 5rem">
                <template #body="slotProps">
                    <Button label="이수" :disabled="isLoading || slotProps.data.courseStatus === '이수'" @click="completeCourse(slotProps.data)" class="p-button-info" />
                </template>
            </Column>
            <Column v-if="!showEducation" field="courseStatus" header="등록 상태" sortable style="min-width: 5rem">
                <template #body="slotProps">
                    <Button label="등록" :disabled="isLoading || slotProps.data.courseStatus === 'APPROVE'" @click="completeCourse(slotProps.data)" class="p-button-info" />
                </template>
            </Column>
        </DataTable>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { useToast } from 'primevue/usetoast';
import Swal from 'sweetalert2';
import { computed, onMounted, ref } from 'vue';
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService';

const authStore = useAuthStore();

const requests = ref([]);
const filters = ref({ global: { value: null } });
const isLoading = ref(false);
const toast = useToast();
const showEducation = ref(true); // 상태를 구분하기 위한 ref

const filteredRequests = computed(() => {
    // '이수' 및 'PASS' 상태인 요청을 항상 제외
    return requests.value.filter((request) => request.courseStatus !== '이수');
});

const loadEducationRequests = async () => {
    showEducation.value = true; // 교육 요청 표시
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/course-service/list');
        console.log('교육 요청 응답 데이터:', response); // 응답 데이터 로그 추가
        requests.value = response
            .map((record) => ({
                courseId: record.courseId,
                educationName: record.educationName,
                categoryName: record.categoryName,
                educationStart: record.startDate.split('T')[0],
                educationEnd: record.endDate.split('T')[0],
                employeeName: record.employeeName,
                courseStatus: mapStatus(record.courseStatus)
            }))
            .filter((request) => request.courseStatus !== '이수') // 'PASS' 상태 필터링
            .reverse(); // 역순으로 설정
    } catch (error) {
        console.error('API 요청 오류:', error); // 오류 로그 추가
        toast.add({ severity: 'error', summary: 'Error', detail: '데이터 로딩 중 문제가 발생했습니다.' });
    }
};

const loadCertificationRequests = async () => {
    showEducation.value = false; // 자격증 요청 표시
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee-certification/certification-list');
        requests.value = response
            .map((record) => ({
                registrationId: record.registrationId,
                certificationName: record.certificationName,
                institution: record.institution,
                acquisitionDate: record.acquisitionDate,
                employeeName: record.employeeName,
                employeeCertificationStatus: mapCertificationStatus(record.employeeCertificationStatus) // 요청 상태 추가
            }))
            .filter((request) => request.employeeCertificationStatus !== '승인') // 'PASS' 상태 필터링
            .reverse(); // 역순으로 설정
    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: '데이터 로딩 중 문제가 발생했습니다.' });
    }
};

onMounted(async () => {
    loadEducationRequests(); // 초기 로드 시 교육 요청 불러오기
});

const completeCourse = async (request) => {
    if (isLoading.value) return;
    isLoading.value = true;

    try {
        // `showEducation` 값에 따라 다른 URL 사용
        let url;
        if (showEducation.value) {
            // 교육 요청에 대한 이수 API
            url = `https://hq-heroes-api.com/api/v1/course-service/complete/${request.courseId}`;
        } else {
            // 자격증 요청에 대한 승인 API
            url = `https://hq-heroes-api.com/api/v1/employee-certification/complete/${request.registrationId}`;
        }

        await fetchPost(url);

        // 요청의 상태를 업데이트
        if (showEducation.value) {
            request.courseStatus = '이수';
            // 성공 메시지 표시
            await Swal.fire({
                title: '교육이 이수되었습니다.',
                icon: 'success'
            });
        } else {
            request.courseStatus = 'APPROVE';
            // 성공 메시지 표시
            await Swal.fire({
                title: '자격증이 승인되었습니다.',
                icon: 'success'
            });
        }

        // 목록에서 해당 요청 제거
        requests.value = requests.value.filter((req) => (showEducation.value ? req.courseId !== request.courseId : req.registrationId !== request.registrationId));
    } catch (error) {
        // 오류 메시지 표시
        await Swal.fire({
            title: '처리 실패',
            text: showEducation.value ? '교육 이수 처리 실패' : '자격증 승인 처리 실패',
            icon: 'error'
        });
    } finally {
        isLoading.value = false;
    }
};

function mapStatus(status) {
    switch (status) {
        case 'PASS':
            return '이수';
        case 'FAIL':
            return '미이수';
        default:
            return '알 수 없음';
    }
}

function mapCertificationStatus(certificationStatus) {
    switch (certificationStatus) {
        case 'APPROVE':
            return '승인';
        case 'DENIED':
            return '반려';
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

.search-input-container {
    position: relative;
}

.search-icon {
    position: absolute;
    left: 8px;
    color: #aaa;
}

.search-input {
    padding-left: 24px; /* 아이콘과 겹치지 않도록 여백 추가 */
}

/* 활성화된 버튼 스타일 */
.active-button {
    background-color: #007ad9; /* 원하는 색상으로 변경 */
    color: white;
    border-color: #007ad9;
}
</style>
