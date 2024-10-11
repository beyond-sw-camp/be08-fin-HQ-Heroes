<template>
    <div class="certification-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">자격증 관리</label>
            </div>

            <!-- 필터 및 검색 섹션 -->
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <Dropdown v-model="selectedDeptName" :options="departments" optionLabel="name" placeholder="부서를 선택하세요" @change="filterCertifications" class="mr-2" />
                    <Dropdown v-model="selectedInstitution" :options="institutions" optionLabel="name" placeholder="발급기관을 선택하세요" @change="filterCertifications" class="mr-2" />
                    <div class="relative search-container">
                        <InputText v-model="globalFilter" placeholder="검색" class="pl-8 search-input" />
                        <i class="pi pi-search search-icon" />
                    </div>
                </div>
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="goToWriteNotice" />
            </div>

            <!-- 자격증 목록 -->
            <DataTable
                :value="filteredCertifications"
                paginator
                :rows="10"
                :globalFilterFields="['certificationName', 'deptName', 'institution']"
                dataKey="certificationId"
                @row-click="showCertificationDetails"
                :metaKeySelection="false"
                selectionMode="single"
                removableSort
            >
                <Column field="certificationId" sortable header="No." />
                <Column field="deptName" sortable header="부서 명" />
                <Column field="certificationName" sortable header="자격증 명" />
                <Column field="institution" sortable header="발급 기관" />
                <Column 
                    field="certificationApplyDate" 
                    sortable 
                    header="접수 기간" 
                >
                    <template #body="{ data }">
                        {{ formatDate(new Date(data.applicationStartDate)) }} ~ {{ formatDate(new Date(data.applicationEndDate)) }}
                    </template>     
                </Column>
                <Column 
                    field="examDate" 
                    sortable 
                    header="시험 일" 
                >
                    <template #body="{ data }">
                        {{ formatDate(new Date(data.examDate)) }}
                    </template>
                </Column>
            </DataTable>
        </div>

        <!-- 자격증 상세 정보 모달 -->
        <Dialog v-model:visible="displayDetailDialog" modal="true" header="자격증 상세" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="true">
            <div v-if="selectedCertification">
                <p><strong>자격증 코드:</strong> {{ selectedCertification.certificationId }}</p>
                <p><strong>자격증명:</strong> {{ selectedCertification.certificationName }}</p>
                <p><strong>부서:</strong> {{ selectedCertification.deptName }}</p>
                <p><strong>발급 기관:</strong> {{ selectedCertification.institution }}</p>
                <p><strong>접수 기간:</strong> {{ formatDate(selectedCertification.applicationStartDate) }} ~ {{ formatDate(selectedCertification.applicationEndDate) }}</p>
                <p><strong>시험 일:</strong> {{ formatDate(selectedCertification.examDate) }}</p>
                <p><strong>결과 발표:</strong> {{ formatDate(selectedCertification.resultDate) }}</p>
            </div>

            <template #footer>
                <Button label="수정" icon="pi pi-pencil" class="p-button-warning" @click="navigateToWriteNotice" />
                <Button label="삭제" icon="pi pi-trash" class="p-button-danger" @click="confirmDeleteCertification" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const certifications = ref([]);
const filteredCertifications = ref([]);
const selectedDeptName = ref(null);
const selectedInstitution = ref(null);
const globalFilter = ref('');
const departments = ref([]);  // 부서 데이터 리스트
const institutions = ref([]); // 발급기관 데이터 리스트
const displayDetailDialog = ref(false);
const selectedCertification = ref(null);
const router = useRouter();

// 자격증 목록을 가져오는 함수
async function fetchCertifications() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/certification-service/certification');
        certifications.value = response.data;
        filteredCertifications.value = certifications.value;
    } catch (error) {
        console.error("자격증 목록을 불러오지 못했습니다.", error);
    }
}

// 필터링 기능
const filterCertifications = () => {
    filteredCertifications.value = certifications.value.filter(certification => {
        return (
            (!selectedDeptName.value || certification.deptName === selecteDeptName.value) &&
            (!selectedInstitution.value || certification.institution === selectedInstitution.value) &&
            (certification.certificationName.includes(globalFilter.value) || 
                certification.deptName.includes(globalFilter.value) || 
                certification.institution.includes(globalFilter.value))
        );
    });
};

// 교육 상세보기 함수
function showCertificationDetails(event) {
    const certificationId = event.data.certificationId;
    // 경로 이동
    router.push(`/manage-certifications/certification-detail/${certificationId}`);
}

// '추가하기' 버튼 클릭 시 이동할 경로
const goToWriteNotice = () => {
    router.push('/write-notice');
};

// 페이지 진입 시 자격증 데이터 로드
onBeforeMount(() => {
    fetchCertifications();
});

// 날짜 포맷팅 함수
const formatDate = (date) => {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
    return date.toLocaleDateString('ko-KR', options);
};

</script>

<style scoped>
.search-container {
    position: relative;
}
.search-input {
    padding-left: 30px;
}
.search-icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
}
</style>
