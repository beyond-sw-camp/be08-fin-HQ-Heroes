<template>
    <div class="education-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">교육 관리</label>
            </div>

            <!-- 필터 및 검색 섹션 -->
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <Dropdown v-model="selectedcategoryName" :options="categories" optionLabel="categoryName" placeholder="카테고리를 선택하세요" @change="filterEducations" class="mr-2" />
                    <Calendar v-model="selectedDate" placeholder="날짜를 선택하세요" :showIcon="true" class="mr-2" @change="filterEducations" />
                    <div class="relative search-container">
                        <InputText v-model="globalFilter" placeholder="검색" class="pl-8 search-input" />
                        <i class="pi pi-search search-icon" />
                    </div>
                </div>
                <Button label="교육 추가" icon="pi pi-plus" class="custom-button" @click="goToWriteNotice" />
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
                    <template #body="{ data }"> {{ formatDate(data.applicationStartDate) }} ~ {{ formatDate(data.applicationEndDate) }} </template>
                </Column>
                <Column field="educationStart" sortable header="교육 시작일">
                    <template #body="{ data }"> {{ formatDate(data.educationStart) }} ~ {{ formatDate(data.educationEnd) }} </template>
                </Column>
            </DataTable>
        </div>
    </div>
</template>

<script setup>
import Button from 'primevue/button';
import Calendar from 'primevue/calendar';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGet } from '../auth/service/AuthApiService';

// 교육 목록 데이터
const educations = ref([]);
const filteredEducations = ref([]);

// 필터링을 위한 데이터
const selectedcategoryName = ref(null);
const selectedDate = ref(null);
const globalFilter = ref('');

// 카테고리 목록
const categories = ref([]);

// Vue Router 설정
const router = useRouter();

// 필터링 함수
function filterEducations() {
    filteredEducations.value = educations.value.filter((education) => {
        const matchesCategoryName = selectedcategoryName.value?.categoryName !== '전체' ? education.categoryName === selectedcategoryName.value.categoryName : true;
        const matchesDate = selectedDate.value ? new Date(education.educationStart) <= new Date(selectedDate.value) && new Date(education.educationEnd) >= new Date(selectedDate.value) : true;
        const matchesGlobalFilter = globalFilter.value ? education.educationName.toLowerCase().includes(globalFilter.value.toLowerCase()) : true;

        return matchesCategoryName && matchesDate && matchesGlobalFilter;
    });
}

// 교육 상세보기 함수
function showEducationDetails(event) {
    // 경로 이동
    router.push(`/manage-education/education-detail/${event.data.educationId}`);
}

// 교육 등록 페이지로 이동
const goToWriteNotice = () => {
    if (categories.value.length > 0) {
        // 카테고리가 비어있지 않은지 확인
        const filteredCategories = categories.value.filter((category) => category.categoryName !== '전체'); // '전체' 제외

        // 카테고리를 로컬 스토리지에 저장
        localStorage.setItem('educationCategories', JSON.stringify(filteredCategories));

        // 쿼리 문자열 없이 '/write-notice'로 이동
        router.replace({ path: '/write-education', query: { fromPage: 'educationListPage' } });
    } else {
        console.error('categories는 정의되지 않았습니다.');
    }
};

// 날짜 포맷팅 함수
function formatDate(date) {
    if (!date) return ''; // 날짜가 없을 경우 빈 문자열 반환
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
}

// 카테고리 목록 가져오기 함수
async function fetchCategories() {
    try {
        const response = await fetchGet('http://localhost:8080/api/v1/educationCategory-service/categories');
        categories.value = [{ categoryName: '전체' }, ...response]; // '전체' 카테고리를 추가
        console.log(categories.value);
    } catch (error) {
        console.error('카테고리 목록을 불러오지 못했습니다.', error);
    }
}

// 컴포넌트가 마운트될 때 교육 목록 및 카테고리 가져오기
onBeforeMount(() => {
    fetchEducations();
    fetchCategories(); // 카테고리 목록 가져오기
});

// 교육 목록 함수 (GET)
async function fetchEducations() {
    try {
        const response = await fetchGet('http://localhost:8080/api/v1/education-service/education');
        educations.value = response;
        filteredEducations.value = educations.value;
    } catch (error) {
        console.error('교육 목록을 불러오지 못했습니다.', error);
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
