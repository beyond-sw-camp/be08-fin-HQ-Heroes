<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4">교육 신청</div>
        <DataTable
            :value="filteredEmployees"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="courseNo"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['courseName', 'category', 'instructorName', 'institute']"
            showGridlines
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
            <Column field="category" sortable header="카테고리" style="min-width: 8rem">
                <template #body="{ data }">
                    {{ data.category }}
                </template>
            </Column>
            <Column field="instructorName" sortable header="강사명" style="min-width: 7rem">
                <template #body="{ data }">
                    {{ data.instructorName }}
                </template>
            </Column>
            <Column field="courseName" sortable header="강의명" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.courseName }}
                </template>
            </Column>
            <Column field="institute" sortable header="교육기관" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.institute }}
                </template>
            </Column>
            <Column field="startDate" sortable header="교육 시작일" dataType="date" style="min-width: 10rem">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.startDate)) }}
                </template>
            </Column>
            <Column field="endDate" sortable header="교육 종료일" dataType="date" style="min-width: 10rem">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.endDate)) }}
                </template>
            </Column>
            <!-- 신청 인원 수 / 총 인원 수 컬럼 추가 -->
            <Column field="participants" sortable header="신청인원 / 총인원" style="min-width: 11rem">
                <template #body="{ data }"> {{ data.participants }} / {{ data.totalSeats }} </template>
            </Column>
            <!-- 신청 버튼 컬럼 추가 -->
            <Column field="apply" header="신청" style="min-width: 10rem">
                <template #body="{ data }">
                    <Button label="신청하기" icon="pi pi-check" @click="openApplyModal(data)" />
                </template>
            </Column>
        </DataTable>

        <!-- 신청 모달 컴포넌트 -->
        <Dialog v-model:visible="applyDialogVisible" :header="selectedCourse?.courseName + ' 신청'" modal>
            <div>
                <p><strong>카테고리:</strong> {{ selectedCourse?.category }}</p>
                <p><strong>강사명:</strong> {{ selectedCourse?.instructorName }}</p>
                <p><strong>교육기관:</strong> {{ selectedCourse?.institute }}</p>
                <p><strong>교육 시작일:</strong> {{ formatDate(new Date(selectedCourse?.startDate)) }}</p>
                <p><strong>교육 종료일:</strong> {{ formatDate(new Date(selectedCourse?.endDate)) }}</p>
                <p><strong>신청인원:</strong> {{ selectedCourse?.participants }} / {{ selectedCourse?.totalSeats }}</p>
            </div>
            <template #footer>
                <Button label="신청" icon="pi pi-check" @click="applyForCourse" />
                <Button label="취소" icon="pi pi-times" class="ml-2" @click="applyDialogVisible = false" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';

// 데이터 및 상태 관리
const courses = ref([]);
const filteredEmployees = ref([]);
const filters = ref(null);
const selectedCourse = ref(null);
const applyDialogVisible = ref(false); // 신청 모달 가시성 상태
const categories = ref([]);
const instructors = ref([]);
const selectedCategory = ref(null);
const selectedInstructor = ref(null);

async function fetchCourseList() {
    courses.value = [
        { courseNo: 1, courseName: 'Java 개발', category: '개발', instructorName: '홍길동', institute: 'ABC Academy', startDate: '2022-01-01', endDate: '2022-03-01', participants: 5, totalSeats: 10 },
        { courseNo: 2, courseName: '디자인 패턴', category: '디자인', instructorName: '이순신', institute: 'XYZ Design Center', startDate: '2021-05-01', endDate: '2021-08-01', participants: 8, totalSeats: 15 },
        { courseNo: 3, courseName: '프로젝트 관리', category: '관리', instructorName: '김유신', institute: 'PQR Management School', startDate: '2020-06-01', endDate: '2020-12-01', participants: 12, totalSeats: 20 }
    ];

    filterByCategoryAndInstructor();
}

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
        courseName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        category: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        startDate: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
    };
}

// 신청 모달 열기
function openApplyModal(course) {
    selectedCourse.value = course;
    applyDialogVisible.value = true;
}

// 강의 신청 처리 (예시)
function applyForCourse() {
    // 신청 로직 구현 (API 호출 등)
    alert(`신청 완료: ${selectedCourse.value.courseName}`);
    applyDialogVisible.value = false;
}

// 날짜 형식 포맷팅
function formatDate(date) {
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}

// 마운트 시 데이터 및 필터 초기화
onBeforeMount(() => {
    fetchCourseList();
    fetchCategories();
    fetchInstructors();
    initFilters();
});
</script>

<style scoped lang="scss">
:deep(.p-datatable-frozen-tbody) {
    font-weight: bold;
}

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
    padding-left: 2.5rem;
}
</style>
