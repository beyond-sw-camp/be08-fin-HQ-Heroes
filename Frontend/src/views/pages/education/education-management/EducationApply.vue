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
            style="table-layout: fixed !important"
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
                        <InputText v-model="filters['global'].value" placeholder="검색어를 입력해주세요" class="pl-8 search-input" />
                    </div>
                </div>
            </template>

            <!-- 테이블 컬럼들 -->
            <Column field="categoryName" sortable header="카테고리" style="min-width: 6rem; text-align: left">
                <template #body="{ data }">
                    {{ data.categoryName }}
                </template>
            </Column>
            <Column field="educationName" sortable header="강의명" style="min-width: 20rem; text-align: left">
                <template #body="{ data }">
                    {{ data.educationName }}
                </template>
            </Column>
            <Column field="educationEnd" sortable header="수강 기간" dataType="date" style="min-width: 8rem; text-align: left">
                <template #body="{ data }"> {{ formatDate(new Date(data.educationStart)) }} ~ {{ formatDate(new Date(data.educationEnd)) }} </template>
            </Column>
            <Column field="status" sortable header="상태" style="min-width: 4rem; text-align: left">
                <template #body="{ data }">
                    {{ data.status }}
                </template>
            </Column>
            <Column field="participants" sortable header="수강 인원" style="min-width: 8rem; text-align: left">
                <template #body="{ data }">
                    {{ data.currentParticipant }} / {{ data.participants }}
                    <!-- 신청 인원 표시 -->
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
import Swal from 'sweetalert2';
import { onBeforeMount, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGet } from '../../auth/service/AuthApiService';

const router = useRouter();

const courses = ref([]);
const filteredEducations = ref([]);
const filters = ref(null);
const selectedCourse = ref(null);
const applyDialogVisible = ref(false);
const selectedcategoryName = ref(null);
const categories = ref([]);
const selectededucationStatus = ref(null);

const statusOptions = ref([{ status: '전체' }, { status: '신청 가능' }, { status: '신청 마감' }]);

async function fetchEducations() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/education-service/education');

        // API 응답을 console에 출력
        console.log('FetchEducations 응답:', response);

        if (response) {
            // response가 null이 아닐 때
            courses.value = response; // 응답 데이터가 배열로 올바르게 설정됨
        } else {
            courses.value = []; // 응답이 null일 경우 빈 배열로 초기화
        }

        // 현재 날짜 가져오기
        const currentDate = new Date();

        // courses.value가 배열인지 확인하고 map 호출
        if (Array.isArray(courses.value)) {
            filteredEducations.value = courses.value
                .map((course) => {
                    // 시작 날짜가 현재 날짜 이후인 경우 상태를 '신청 가능'으로 설정
                    const educationStartDate = new Date(course.educationStart);
                    return {
                        ...course,
                        status: educationStartDate > currentDate ? '신청 가능' : '신청 마감'
                    };
                })
                .reverse(); // 역순으로 정렬
        } else {
            console.warn('courses.value는 배열이 아닙니다:', courses.value);
            filteredEducations.value = []; // courses.value가 배열이 아닐 경우 빈 배열로 초기화
        }
    } catch (error) {
        console.error('신청 기간이 지났습니다.', error);
    }
}

async function fetchCategories() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/educationCategory-service/categories');

        // 응답 데이터의 구조를 콘솔에 출력하여 확인
        console.log('fetchCategories 응답:', response);

        // response가 배열인지 확인
        if (Array.isArray(response)) {
            categories.value = [{ categoryName: '전체' }, ...response];
        } else {
            console.error('응답이 예상한 형태가 아닙니다:', response);
            categories.value = [{ categoryName: '전체' }]; // 기본값 설정
        }
    } catch (error) {
        console.error('카테고리 목록을 불러오지 못했습니다.', error);
    }
}

function filterEducations() {
    const currentDate = new Date();

    // 각 교육의 상태를 업데이트합니다.
    courses.value.forEach((education) => {
        const educationStartDate = new Date(education.educationStart);
        education.status = educationStartDate > currentDate ? '신청 가능' : '신청 마감';
    });

    // 교육을 필터링하고 역순으로 정렬합니다.
    filteredEducations.value = courses.value
        .filter((education) => {
            const matchesCategoryName = selectedcategoryName.value?.categoryName && selectedcategoryName.value.categoryName !== '전체' ? education.categoryName === selectedcategoryName.value.categoryName : true;

            const matchesStatus = selectededucationStatus.value && selectededucationStatus.value.status !== '전체' ? education.status === selectededucationStatus.value.status : true;

            const matchesGlobalFilter = filters.value?.global?.value ? education.educationName.toLowerCase().includes(filters.value.global.value.toLowerCase()) : true;

            return matchesCategoryName && matchesStatus && matchesGlobalFilter;
        })
        .reverse(); // 필터링 후 역순으로 정렬합니다.
}

// openEducationDetail 함수가 중복으로 선언되지 않도록 확인 후, 필요 시 아래만 남겨두기
function openEducationDetail(event) {
    const course = event.data;
    if (course.status === '신청 마감') {
        Swal.fire({
            icon: 'warning',
            title: '알림',
            text: '신청 기간이 지났어요!'
        });
        return;
    }
    router.push({ path: `/education-apply/education-detail/${course.educationId}` });
}

function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        educationName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        categoryName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] }
    };
}

// 날짜 포맷 함수
function formatDate(date) {
    const formattedDate = new Date(date);
    return `${formattedDate.getFullYear()}-${String(formattedDate.getMonth() + 1).padStart(2, '0')}-${String(formattedDate.getDate()).padStart(2, '0')}`;
}

function onSelectionChange(e) {
    selectedCourse.value = e.value;
}

function rowClass(data) {
    return data.status !== '신청 가능' ? 'inactive-row' : '';
}

onBeforeMount(() => {
    initFilters();
    fetchCategories();
    fetchEducations();
});
</script>

<style scoped>
:deep(.inactive-row) {
    background-color: #f8d7da !important; /* 연한 빨간 배경 */
    color: #721c24 !important;
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
    color: #aaa;
}

.search-input {
    padding-left: 30px;
}
</style>
