<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4">교육 신청</div>
        <DataTable
            :value="filteredEmployees"
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
                        <Dropdown v-model="selectedCategoryName" :options="categories" optionLabel="name" placeholder="카테고리를 선택하세요" @change="filterByCategoryNameAndInstructor" class="mr-2" />
                        <Dropdown v-model="selectedInstructor" :options="instructors" optionLabel="name" placeholder="강사명을 선택하세요" @change="filterByCategoryNameAndInstructor" class="mr-2" />
                    </div>
                    <div class="relative search-container ml-auto">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="Keyword Search" class="pl-8 search-input" />
                    </div>
                </div>
            </template>

            <!-- 테이블 컬럼들 -->
            <!-- No. 부분의 너비를 줄임 -->
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
            <Column field="educationStart" sortable header="신청 기간" dataType="date" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.applicationStartDate)) }} ~ {{ formatDate(new Date(data.applicationEndDate)) }}
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
import { useRouter } from 'vue-router'; // useRouter 추가
import axios from 'axios'; // axios 추가

const router = useRouter(); // 라우터 객체 가져오기

// 데이터 및 상태 관리
const courses = ref([]); // 초기값을 빈 배열로 설정
const filteredEmployees = ref([]);
const filters = ref(null);
const selectedCourse = ref(null);
const applyDialogVisible = ref(false); // 신청 모달 가시성 상태
const categories = ref([]);
const instructors = ref([]);
const selectedCategoryName = ref(null);
const selectedInstructor = ref(null);

// 카테고리 및 강사 목록 가져오기
async function fetchCategories() {
    categories.value = [{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '관리' }];
}

async function fetchInstructors() {
    instructors.value = [{ name: '전체' }, { name: '홍길동' }, { name: '이순신' }, { name: '김유신' }];
}

// 교육 목록 함수 (GET)
async function fetchEducations() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/education-service/education');
        courses.value = response.data; // 가져온 데이터를 courses에 저장
        filteredEmployees.value = courses.value; // 필터링된 데이터를 설정
    } catch (error) {
        console.error("교육 목록을 불러오지 못했습니다.", error);
    }
}

// 필터링
function filterByCategoryNameAndInstructor() {
    filteredEmployees.value = courses.value.filter((course) => {
        const matchesCategoryName = selectedCategoryName.value && selectedCategoryName.value.name !== '전체' ? course.categoryName === selectedCategoryName.value.name : true;
        const matchesInstructor = selectedInstructor.value && selectedInstructor.value.name !== '전체' ? course.instructorName === selectedInstructor.value.name : true;
        return matchesCategoryName && matchesInstructor;
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

// 신청 모달 열기
function openApplyModal(course) {
    if (!course.educationId) {
        console.error('educationId가 존재하지 않습니다:', course);
        return;
    }
    selectedCourse.value = course;
    applyDialogVisible.value = true;
}

// 초기화
onBeforeMount(() => {
    initFilters();
    fetchCategories();
    fetchInstructors();
    fetchEducations(); // 교육 목록 가져오기
    filterByCategoryNameAndInstructor(); // 필터 적용
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
