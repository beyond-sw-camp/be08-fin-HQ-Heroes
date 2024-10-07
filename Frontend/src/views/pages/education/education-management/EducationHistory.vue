<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4">교육 이력</div>
        <DataTable
            :value="filteredEmployees"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="employeeNo"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['courseName', 'category', 'instructorName', 'institute']"
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
            <!-- 강의 명 부분의 너비를 늘림 -->
            <Column field="educationName" sortable header="강의명" style="min-width: 20rem; text-align: left;">
                <template #body="{ data }">
                    <span @click="openEducationDetail(data)" style="cursor: pointer;">
                        {{ data.educationName }}
                    </span>
                </template>
            </Column>
            <Column field="educationEnd" sortable header="신청일" dataType="date" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.educationStart)) }}
                </template>
            </Column>
            <Column field="status" sortable header="이수 여부" style="min-width: 6rem">
                <template #body="{ data }">
                    {{ data.status }}
                </template>
            </Column>
        </DataTable>

        <!-- 모달 컴포넌트
        <Dialog v-model:visible="displayDialog" :header="selectedCourse?.courseName" modal>
            <div>
                <p><strong>카테고리:</strong> {{ selectedCourse?.category }}</p>
                <p><strong>강사명:</strong> {{ selectedCourse?.instructorName }}</p>
                <p><strong>교육기관:</strong> {{ selectedCourse?.institute }}</p>
                <p><strong>교육 시작일:</strong> {{ formatDate(new Date(selectedCourse?.startDate)) }}</p>
                <p><strong>교육 종료일:</strong> {{ formatDate(new Date(selectedCourse?.endDate)) }}</p>
                <p><strong>이수 여부:</strong> {{ selectedCourse?.status }}</p>
            </div>
            <template #footer>
                <Button label="닫기" icon="pi pi-times" @click="displayDialog = false" />
            </template>
        </Dialog> -->
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

const courses = ref([]);
const filteredEmployees = ref([]);
const filters = ref(null);
const selectedCourse = ref(null);
const displayDialog = ref(false);
const categories = ref([]);
const instructors = ref([]);
const selectedCategory = ref(null);
const selectedInstructor = ref(null);

async function fetchCourseList() {
    courses.value = [
    { courseId: 1, category: '어학', instructorName: '홍길동', educationName: '언어에 어려움을 겪고 있는 사람들을 위한 프로그램', institution: 'Hanwha Academy', educationStart: '2024-10-01', status: '이수' },
    { courseId: 2, category: '디자인', instructorName: '이순신', educationName: 'UI/UX 디자인 기초', institution: 'Design School', educationStart: '2024-11-02', status: '진행 중' },
    { courseId: 3, category: '관리', instructorName: '김유신', educationName: '프로젝트 관리 실무', institution: 'Management Center', educationStart: '2024-11-02', status: '이수' }
];

    filterByCategoryAndInstructor();
}

async function fetchCategories() {
    categories.value = [{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '관리' }];
}

async function fetchInstructors() {
    instructors.value = [{ name: '전체' }, { name: '홍길동' }, { name: '이순신' }, { name: '김유신' }];
}

function filterByCategoryAndInstructor() {
    filteredEmployees.value = courses.value.filter((course) => {
        const matchesCategory = selectedCategory.value && selectedCategory.value.name !== '전체' ? course.category === selectedCategory.value.name : true;
        const matchesInstructor = selectedInstructor.value && selectedInstructor.value.name !== '전체' ? course.instructorName === selectedInstructor.value.name : true;
        return matchesCategory && matchesInstructor;
    });
}

function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        courseName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        category: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        startDate: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }] }
    };
}

function showCourseDetails(event) {
    selectedCourse.value = event.data;
    displayDialog.value = true;
}

function formatDate(date) {
    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}

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
