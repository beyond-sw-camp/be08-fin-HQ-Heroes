<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4">교육 신청</div>
        <DataTable
            :value="filteredEmployees"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="courseId"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['educationName', 'category', 'instructorName', 'institution']"
            showGridlines
            style="table-layout: fixed !important;"
        >
            <!-- 필터와 테이블 헤더 -->
            <template #header>
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                        <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="카테고리를 선택하세요" @change="filterByCategoryAndInstructor" class="mr-2" />
                        <Dropdown v-model="selectedInstructor" :options="instructors" optionLabel="name" placeholder="강사명을 선택하세요" @change="filterByCategoryAndInstructor" class="mr-2" />
                    </div>
                    <div class="relative search-container ml-auto">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="Keyword Search" class="pl-8 search-input" />
                    </div>
                </div>
            </template>

            <!-- 테이블 컬럼들 -->
            <!-- No. 부분의 너비를 줄임 -->
            <Column field="courseId" sortable header="No." style="min-width: 2rem; text-align: left;">
                <template #body="{ data }">
                    {{ data.courseId }}
                </template>
            </Column>
            <Column field="category" sortable header="카테고리" style="min-width: 6rem; text-align: left;">
                <template #body="{ data }">
                    {{ data.category }}
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
                    {{ formatDate(new Date(data.educationApplyStart)) }} ~ {{ formatDate(new Date(data.educationApplyEnd)) }}
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

const router = useRouter(); // 라우터 객체 가져오기

// 데이터 및 상태 관리
const courses = ref([
    { courseId: 1, category: '어학', instructorName: '홍길동', educationName: '언어에 어려움을 겪고 있는 사람들을 위한 프로그램', institution: 'Hanwha Academy', educationApplyStart: '2024-10-01', educationApplyEnd: '2024-10-10', educationStart: '2024-11-02', educationEnd: '2024-11-20', participants: 20, totalParticipants: 30 },
    { courseId: 2, category: '디자인', instructorName: '이순신', educationName: 'UI/UX 디자인 기초', institution: 'Design School',educationApplyStart: '2024-10-01', educationApplyEnd: '2024-10-10', educationStart: '2024-11-02', educationEnd: '2024-11-20', participants: 10, totalParticipants: 20 },
    { courseId: 3, category: '관리', instructorName: '김유신', educationName: '프로젝트 관리 실무', institution: 'Management Center', educationApplyStart: '2024-10-01', educationApplyEnd: '2024-10-10', educationStart: '2024-11-02', educationEnd: '2024-11-20', participants: 15, totalParticipants: 25 }
]);

const filteredEmployees = ref([]);
const filters = ref(null);
const selectedCourse = ref(null);
const applyDialogVisible = ref(false); // 신청 모달 가시성 상태
const categories = ref([]);
const instructors = ref([]);
const selectedCategory = ref(null);
const selectedInstructor = ref(null);

// 카테고리 및 강사 목록 가져오기
async function fetchCategories() {
    categories.value = [{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '관리' }];
}

async function fetchInstructors() {
    instructors.value = [{ name: '전체' }, { name: '홍길동' }, { name: '이순신' }, { name: '김유신' }];
}

// 필터링
function filterByCategoryAndInstructor() {
    filteredEmployees.value = courses.value.filter((course) => {
        const matchesCategory = selectedCategory.value && selectedCategory.value.name !== '전체' ? course.category === selectedCategory.value.name : true;
        const matchesInstructor = selectedInstructor.value && selectedInstructor.value.name !== '전체' ? course.instructorName === selectedInstructor.value.name : true;
        return matchesCategory && matchesInstructor;
    });
}

// 필터 초기화
function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        educationName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        category: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        educationStart: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
    };
}

function openEducationDetail(course) {
    if (!course.courseId) {
        console.error('courseId가 존재하지 않습니다:', course);
        return;
    }
    // 라우터 네비게이션 (courseId를 파라미터로 전달)
    router.push({ name: 'educationDetail', params: { courseId: course.courseId } });
}

// 신청 모달 열기
function openApplyModal(course) {
    if (!course.courseId) {
        console.error('courseId가 존재하지 않습니다:', course);
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
    filterByCategoryAndInstructor();
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
