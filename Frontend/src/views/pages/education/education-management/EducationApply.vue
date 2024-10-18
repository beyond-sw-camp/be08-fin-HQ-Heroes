<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4">교육 신청</div>
        <DataTable
            :value="filteredEducations"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="educationId"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['educationName', 'categoryName', 'instructorName', 'institution']"
            showGridlines
            style="table-layout: fixed !important;"
        >
            <!-- 필터와 테이블 헤더 -->
            <template #header>
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                        <Dropdown v-model="selectedcategoryName" :options="categories" optionLabel="categoryName" placeholder="카테고리를 선택하세요" @change="filterEducations" class="mr-2" />
                        <Calendar v-model="selectedDate" placeholder="날짜를 선택하세요" :showIcon="true" class="mr-2" @change="filterEducations" />
                    </div>
                    <div class="relative search-container ml-auto">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="Keyword Search" class="pl-8 search-input" />
                    </div>
                </div>
            </template>

            <!-- 테이블 컬럼들 -->
            <Column field="educationId" sortable header="No." style="min-width: 2rem; text-align: left;">
                <template #body="{ data }">
                    {{ data.educationId }}
                </template>
            </Column>
            <Column field="categoryName" sortable header="카테고리" style="min-width: 6rem; text-align: left;">
                <template #body="{ data }">
                    {{ data.categoryName }}
                </template>
            </Column>
            <Column field="educationName" sortable header="강의명" style="min-width: 20rem; text-align: left;">
                <template #body="{ data }">
                    <span @click="openEducationDetail(data)" style="cursor: pointer;">
                        {{ data.educationName }}
                    </span>
                </template>
            </Column>
            <Column field="educationEnd" sortable header="수강 기간" dataType="date" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.educationStart)) }} ~ {{ formatDate(new Date(data.educationEnd)) }}
                </template>
            </Column>
        </DataTable>
    </div>
</template>

<script setup>
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

// 데이터 및 상태 관리
const courses = ref([]); // 초기값을 빈 배열로 설정
const filteredEducations = ref([]); // 변수명 통일
const filters = ref(null);
const selectedCourse = ref(null);
const applyDialogVisible = ref(false); // 신청 모달 가시성 상태
const selectedcategoryName = ref(null);
const selectedDate = ref(null);

// 카테고리 목록
const categories = ref([]);

// 교육 목록 함수 (GET)
async function fetchEducations() {
    try {
        // 백엔드에서 API 경로가 정확한지 확인하세요
        const response = await axios.get('http://localhost:8080/api/v1/education-service/education'); 
        courses.value = response.data; // 가져온 데이터를 courses에 저장
        filteredEducations.value = courses.value; // 필터링된 데이터를 설정
    } catch (error) {
        console.error("교육 목록을 불러오지 못했습니다.", error);
        // 에러가 발생한 URL과 상태 코드 출력
        if (error.response) {
            console.error("Status code:", error.response.status);
            console.error("Error data:", error.response.data);
        }
    }
}


// 카테고리 목록 가져오기 함수
async function fetchCategories() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/educationCategory-service/categories');
        categories.value = [{ categoryName: '전체' }, ...response.data]; // '전체' 카테고리를 추가
    } catch (error) {
        console.error('카테고리 목록을 불러오지 못했습니다.', error);
    }
}

// 필터링 함수
function filterEducations() {
    filteredEducations.value = educations.value.filter((education) => {
        const matchesCategoryName = selectedcategoryName.value?.categoryName !== '전체' ? education.categoryName === selectedcategoryName.value.categoryName : true;
        const matchesDate = selectedDate.value ? new Date(education.educationStart) <= new Date(selectedDate.value) && new Date(education.educationEnd) >= new Date(selectedDate.value) : true;
        const matchesGlobalFilter = globalFilter.value ? education.educationName.toLowerCase().includes(globalFilter.value.toLowerCase()) : true;

        return matchesCategoryName && matchesDate && matchesGlobalFilter;
    });
}

// 필터 초기화
function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        educationName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        categoryName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        educationStart: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
    };
}

function openEducationDetail(course) {
    if (!course.educationId) {
        console.error('educationId 존재하지 않습니다:', course);
        return;
    }
    // 라우터 네비게이션 (educationId 파라미터로 전달)
    router.push({ path: `/education-apply/education-detail/${course.educationId}` });
}

// 초기화
onBeforeMount(() => {
    initFilters();
    fetchCategories();
    fetchEducations(); // 교육 목록 가져오기
});

// 날짜 형식 변환
function formatDate(date) {
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}
</script>

<style scoped>
.search-container {
    position: relative;
}
.search-icon {
    position: absolute;
    left: 0.5rem;
    top: 50%;
    transform: translateY(-50%);
}
.search-input {
    padding-left: 2rem !important;
}
</style>
