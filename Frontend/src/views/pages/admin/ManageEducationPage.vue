<template>
    <div class="education-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">교육 관리</label>
            </div>

            <!-- 필터 및 검색 섹션 -->
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <Dropdown v-model="selectedcategoryName" :options="categories" optionLabel="name" placeholder="카테고리를 선택하세요" @change="filterEducations" class="mr-2" />
                    <Calendar v-model="selectedDate" placeholder="날짜를 선택하세요" :showIcon="true" class="mr-2" @change="filterEducations" />
                    <div class="relative search-container">
                        <InputText v-model="globalFilter" placeholder="검색" class="pl-8 search-input" />
                        <i class="pi pi-search search-icon" />
                    </div>
                </div>
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="goToWriteNotice" />
            </div>

            <!-- 교육 목록 테이블 -->
            <DataTable
                :value="filteredEducations"
                paginator
                :rows="10"
                :globalFilterFields="['educationName', 'categoryName', 'institution']"
                dataKey="educationId"
                :metaKeySelection="false"
                selectionMode="single"
                removableSort
                @row-click="showEducationDetails"
            >
                <Column field="educationId" sortable header="No." />
                <Column field="categoryName" sortable header="카테고리" />
                <Column field="educationName" sortable header="교육 명" />
                <Column field="institution" sortable header="교육 기관" />
                <Column field="applicationStartDate" sortable header="신청 기간">
                    <template #body="{ data }">
                        {{ formatDate(data.applicationStartDate) }} ~ {{ formatDate(data.applicationEndDate) }}
                    </template>
                </Column>
                <Column field="educationStart" sortable header="교육 시작일">
                    <template #body="{ data }">
                        {{ formatDate(data.educationStart) }} ~ {{ formatDate(data.educationEnd) }}
                    </template>
                </Column>
            </DataTable>
        </div>

        <!-- 교육 상세 정보 모달 -->
        <!-- <Dialog v-model:visible="displayDetailDialog" modal="true" header="교육 상세 정보" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="true">
            <div v-if="selectedEducation">
                <p><strong>교육 코드:</strong> {{ selectedEducation.educationId }}</p>
                <p><strong>교육명:</strong> {{ selectedEducation.educationName }}</p>
                <p><strong>카테고리:</strong> {{ selectedEducation.categoryName }}</p>
                <p><strong>강사:</strong> {{ selectedEducation.instructorName }}</p>
                <p><strong>신청 기간:</strong> {{ formatDate(selectedEducation.applicationStartDate) }} ~ {{ formatDate(selectedEducation.applicationEndDate) }}</p>
                <p><strong>교육 기간:</strong> {{ formatDate(selectedEducation.educationStart) }} ~ {{ formatDate(selectedEducation.educationEnd) }}</p>
                <p><strong>기관명:</strong> {{ selectedEducation.institution }}</p>
                <p><strong>현재 인원:</strong> {{ selectedEducation.participant }}</p>
                <p><strong>총 인원:</strong> {{ selectedEducation.participants }}</p>
            </div>
            <template #footer>
                <Button label="수정" icon="pi pi-pencil" class="p-button-warning" @click="navigateToWriteNotice" />
                <Button label="삭제" icon="pi pi-trash" class="p-button-danger" @click="confirmDeleteEducation" />
            </template>
        </Dialog> -->
    </div>
</template>

<script setup>
import axios from 'axios';
import { useRouter } from 'vue-router';
import { ref, onBeforeMount } from 'vue';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import Calendar from 'primevue/calendar';

// 교육 목록 데이터
const educations = ref([]);
const filteredEducations = ref([]);
const selectedEducation = ref(null);
const displayDetailDialog = ref(false);

// 필터링을 위한 데이터
const selectedcategoryName = ref(null);
const selectedDate = ref(null);
const globalFilter = ref('');

// 카테고리 목록
const categories = ref([{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '인사' }]);

// Vue Router 설정
const router = useRouter();

// 필터링 함수
function filterEducations() {
    filteredEducations.value = educations.value.filter((education) => {
        const matchesCategoryName = selectedcategoryName.value?.name !== '전체' ? education.categoryName === selectedcategoryName.value.name : true;
        const matchesDate = selectedDate.value ? new Date(education.educationStart) <= new Date(selectedDate.value) && new Date(education.educationEnd) >= new Date(selectedDate.value) : true;
        const matchesGlobalFilter = globalFilter.value
            ? education.educationName.toLowerCase().includes(globalFilter.value.toLowerCase())
            : true;

        return matchesCategoryName && matchesDate && matchesGlobalFilter;
    });
}

// 교육 상세보기 함수
function showEducationDetails(event) {
    // 경로 이동
    router.push(`/manage-education/education-detail/${event.data.educationId}`);
}

// 교육 등록 페이지로 이동
function goToWriteNotice() {
    router.push('/write-notice');
}

// 교육 삭제 확인 함수
function confirmDeleteEducation() {
    const confirmDelete = window.confirm("해당 교육을 삭제하시겠습니까?");
    if (confirmDelete) {
        deleteEducation();
    }
}

// 교육 삭제 함수
function deleteEducation() {
    console.log("교육이 삭제되었습니다.", selectedEducation.value.educationId);
    // 삭제 후 모달 닫기 및 데이터 새로 고침 등의 작업 추가
    displayDetailDialog.value = false; // 모달 닫기
    fetchEducations(); // 목록 새로 고침
}

// 수정 페이지로 이동
function navigateToWriteNotice() {
    router.push('/write-notice');
}

// 날짜 포맷팅 함수
function formatDate(date) {
    if (!date) return ''; // 날짜가 없을 경우 빈 문자열 반환
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
}

// 컴포넌트가 마운트될 때 교육 목록 가져오기
onBeforeMount(() => {
    fetchEducations();
});

// 교육 목록 함수 (GET)
async function fetchEducations() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/education-service/education');
        educations.value = response.data;
        filteredEducations.value = educations.value;
    } catch (error) {
        console.error("교육 목록을 불러오지 못했습니다.", error);
    }
}

// 교육 등록 함수 (POST)
async function createEducation() {
    try {
        const response = await axios.post('http://localhost:8080/api/v1/education-service/education', educationData.value);
        console.log("교육 등록 성공:", response.data);
        fetchEducations(); // 교육 목록 새로 고침
        resetForm(); // 폼 초기화
    } catch (error) {
        console.error("교육 등록 실패:", error);
    }
}
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

.custom-button {
    margin-left: auto;
}

.time-section {
    display: flex;
    justify-content: space-between;
}

.time-block {
    flex: 1;
    margin-right: 10px;
}

.time-block:last-child {
    margin-right: 0;
}
</style>
