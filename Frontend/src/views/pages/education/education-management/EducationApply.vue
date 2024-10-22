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
            :rowClass="rowClass"
            :selection="selectedCourse"
            @selection-change="onSelectionChange"
            showGridlines
            style="table-layout: fixed !important;"
            @row-click="openEducationDetail"
            class="data-table-custom"
        >
            <!-- 필터와 테이블 헤더 -->
            <template #header>
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                        <Dropdown v-model="selectedcategoryName" :options="categories" optionLabel="categoryName" placeholder="카테고리를 선택하세요" @change="filterEducations" class="mr-2" />
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
                    {{ data.educationName }}
                </template>
            </Column>
            <Column field="educationEnd" sortable header="수강 기간" dataType="date" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.educationStart)) }} ~ {{ formatDate(new Date(data.educationEnd)) }}
                </template>
            </Column>
            <Column field="status" sortable header="상태" style="min-width: 4rem; text-align: left;">
                <template #body="{ data }">
                    {{ data.status }}
                </template>
            </Column>
            <Column field="participants" sortable header="수강 인원" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ data.currentParticipant }} / {{ data.participants }} <!-- 신청 인원 표시 -->
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

const courses = ref([]);
const filteredEducations = ref([]);
const filters = ref(null);
const selectedCourse = ref(null);
const applyDialogVisible = ref(false);
const selectedcategoryName = ref(null);
const categories = ref([]);
const selectededucationStatus = ref(null);

const statusOptions = ref([
    { status: '전체' },
    { status: '신청 가능' },
    { status: '신청 마감' }
]);

async function fetchEducations() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/education-service/education');
        courses.value = response.data;

        // 현재 날짜 가져오기
        const currentDate = new Date();

        filteredEducations.value = courses.value.map(course => {
            // 시작 날짜가 현재 날짜 이후인 경우 상태를 '신청 마감'으로 설정
            const educationStartDate = new Date(course.educationStart);
            return {
                ...course,
                status: educationStartDate > currentDate ? '신청 가능' : '신청 마감'
            };
        });
    } catch (error) {
        console.error("신청 기간이 지났습니다.", error);
    }
}

async function fetchCategories() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/educationCategory-service/categories');
        categories.value = [{ categoryName: '전체' }, ...response.data];
    } catch (error) {
        console.error('카테고리 목록을 불러오지 못했습니다.', error);
    }
}

function filterEducations() {
    filteredEducations.value = courses.value.filter((education) => {
        const matchesCategoryName = selectedcategoryName.value?.categoryName && selectedcategoryName.value.categoryName !== '전체'
            ? education.categoryName === selectedcategoryName.value.categoryName 
            : true;

        // status 필터링 추가
        const matchesStatus = selectededucationStatus.value && selectededucationStatus.value.status !== '전체' 
            ? education.status === selectededucationStatus.value.status 
            : true;

        const matchesGlobalFilter = filters.value?.global?.value 
            ? education.educationName.toLowerCase().includes(filters.value.global.value.toLowerCase()) 
            : true;

        return matchesCategoryName && matchesStatus && matchesGlobalFilter;
    });
}

function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        educationName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        categoryName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
    };
}

function openEducationDetail(event) {
    const course = event.data;
    if (course.status === '신청 마감') {
        alert('신청 기간이 마감된 교육입니다.');
        return;
    }
    router.push({ path: `/education-apply/education-detail/${course.educationId}` });
}

function formatDate(date) {
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}
function onSelectionChange(e) {
    selectedCourse.value = e.value;
}

onBeforeMount(() => {
    initFilters();
    fetchCategories();
    fetchEducations();
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
</style>
