<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4">신청한 교육 목록</div>
        <DataTable
            :value="filteredCourses"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="courseId"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['educationName', 'categoryName', 'instructorName', 'institute']"
            showGridlines
            @row-click="showCourseDetails"
            :metaKeySelection="false"
            @rowSelect="onRowSelect"
            @rowUnselect="onRowUnselect"
        >
            <template #header>
                <div class="flex justify-between items-center">
                    <div class="flex items-center gap-2">
                        <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="카테고리를 선택하세요" @change="filterByCategoryAndInstructor" class="mr-2" />
                        <Dropdown v-model="selectedInstructor" :options="instructors" optionLabel="name" placeholder="강사명을 선택하세요" @change="filterByCategoryAndInstructor" class="mr-2" />
                    </div>
                    <div class="relative search-container">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="Keyword Search" class="pl-8 search-input" />
                    </div>
                </div>
            </template>
            <template #empty> No courses found. </template>

            <!-- 테이블 컬럼들 -->
            <Column field="categoryName" sortable header="카테고리" style="min-width: 6rem; text-align: left;">
                <template #body="{ data }">
                    {{ data.categoryName }}
                </template>
            </Column>
            <Column field="educationName" sortable header="강의명" style="min-width: 20rem; text-align: left;">
                <template #body="{ data }">
                        {{ data.educationName }}
                </template>
            </Column>
            <Column field="startDate" sortable header="교육 신청일" dataType="date" style="min-width: 10rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.startDate)) }}
                </template>
            </Column>
            <Column field="endDate" sortable header="교육 종료일" dataType="date" style="min-width: 10rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.endDate)) }}
                </template>
            </Column>
            <Column field="status" sortable header="이수 여부" style="min-width: 6rem">
                <template #body="{ data }">
                    {{ mapStatus(data.status) }}
                </template>
            </Column>
        </DataTable>

        <!-- 모달창 컴포넌트 추가 -->
        <EducationDetailModal
            v-model:visible="isDialogVisible"
            :courseDetail="selectedCourse"
            @refreshCourses="fetchCourseList"
        />
    </div>
</template>

<script setup>
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import { fetchGet } from '../../auth/service/AuthApiService';
import { onBeforeMount, ref, onMounted } from 'vue';
import EducationDetailModal from '../education-management/EducationDetailModal.vue';  // 모달 컴포넌트 가져오기

const courseList = ref([]);  // 전체 교육 목록
const filteredCourses = ref([]);  // 필터링된 교육 목록
const filters = ref(null);  // 필터 설정
const selectedCourse = ref(null);  // 선택된 강의
const categories = ref([]);  // 카테고리 목록
const instructors = ref([]);  // 강사 목록
const selectedCategory = ref(null);  // 선택된 카테고리
const selectedInstructor = ref(null);  // 선택된 강사
const isDialogVisible = ref(false); // 모달 가시성

const employeeData = ref({
    employeeName: '',
    employeeId: ''
});

// API 호출해서 교육 목록 가져오기
async function fetchCourseList() {
    try {
        const response = await fetchGet('http://localhost:8080/api/v1/course-service/my-courses');
        
        if (Array.isArray(response)) {
            courseList.value = response.reverse();  // API로부터 받은 데이터를 courseList에 저장하고 반전
            filteredCourses.value = [...courseList.value];  // filteredCourses 업데이트
        } else {
            console.error('응답 데이터가 배열이 아닙니다.', response);
        }
    } catch (error) {
        console.error('교육 목록을 불러오지 못했습니다.', error);
    }
}

// 필터 초기화
function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        educationName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        categoryName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        startDate: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
    };
}

// 날짜 포맷팅
function formatDate(date) {
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}

// 상태에 따라 이수 여부를 매핑
function mapStatus(status) {
    switch (status) {
        case 'PASS':
            return '이수';
        case 'FAIL':
            return '미이수';
        default:
            return '알 수 없음';
    }
};

// 교육 클릭 시 상세 정보를 모달로 전달하는 함수
const showCourseDetails = (course) => {
    selectedCourse.value = course;  // 선택된 교육 정보 설정
    isDialogVisible.value = true;  // 모달 표시
};

function onRowSelect(event) {
    selectedCourse.value = event.data;
    isDialogVisible.value = true; // 모달 표시
}

function onRowUnselect() {
    selectedCourse.value = null;
    isDialogVisible.value = false; // 모달 닫기
}

onBeforeMount(() => {
    initFilters();
});

// 컴포넌트가 마운트된 후 실행될 코드
onMounted(async () => {
    const employeeId = window.localStorage.getItem('employeeId'); 
    const data = await getLoginEmployeeInfo(employeeId); 

    if (data) {
        employeeData.value = data;
    }

    await fetchCourseList();
});
</script>

<style scoped>
.search-container {
    display: flex;
    align-items: center;
    position: relative;
}

.search-icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
    color: #888;
}

.search-input {
    padding-left: 30px;
}

.font-semibold {
    font-weight: 600;
}
</style>
