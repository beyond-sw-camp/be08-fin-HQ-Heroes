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
            <Column field="category" sortable header="카테고리" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.category }}
                </template>
            </Column>
            <Column field="instructorName" sortable header="강사명" style="min-width: 12rem">
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
            <Column field="status" sortable header="이수 여부" style="min-width: 10rem">
                <template #body="{ data }">
                    {{ data.status }}
                </template>
            </Column>
        </DataTable>

        <!-- 모달 컴포넌트 -->
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
        { courseNo: 1, courseName: 'Java 개발', category: '개발', instructorName: '홍길동', institute: 'ABC Academy', startDate: '2022-01-01', endDate: '2022-03-01', status: '이수' },
        { courseNo: 2, courseName: '디자인 패턴', category: '디자인', instructorName: '이순신', institute: 'XYZ Design Center', startDate: '2021-05-01', endDate: '2021-08-01', status: '미이수' },
        { courseNo: 3, courseName: '프로젝트 관리', category: '관리', instructorName: '김유신', institute: 'PQR Management School', startDate: '2020-06-01', endDate: '2020-12-01', status: '이수' }
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
