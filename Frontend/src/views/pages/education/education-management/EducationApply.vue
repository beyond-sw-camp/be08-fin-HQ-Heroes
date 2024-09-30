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
                    <div class="relative search-container ml-auto"> <!-- flex-grow 제거 및 ml-auto 추가 -->
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="Keyword Search" class="pl-8 search-input" />
                    </div>
                </div>
            </template>

            <!-- 테이블 컬럼들 -->
            <Column field="category" sortable header="카테고리" style="min-width: 8rem; text-align: center;">
                <template #body="{ data }">
                    {{ data.category }}
                </template>
            </Column>
            <Column field="instructorName" sortable header="강사명" style="min-width: 7rem; text-align: center;">
                <template #body="{ data }">
                    {{ data.instructorName }}
                </template>
            </Column>
            <Column field="educationName" sortable header="강의명" style="min-width: 12rem; text-align: center;">
                <template #body="{ data }">
                    {{ data.educationName }}
                </template>
            </Column>
            <Column field="institution" sortable header="교육기관" style="min-width: 12rem; text-align: center;">
                <template #body="{ data }">
                    {{ data.institution }}
                </template>
            </Column>
            <Column field="educationStart" sortable header="교육 시작일" dataType="date" style="min-width: 10rem; text-align: center;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.educationStart)) }}
                </template>
            </Column>
            <Column field="educationEnd" sortable header="교육 종료일" dataType="date" style="min-width: 10rem; text-align: center;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.educationEnd)) }}
                </template>
            </Column>
            <!-- 신청 인원 수 / 총 참여 인원 컬럼 추가 -->
            <Column field="totalParticipants" sortable header="신청인원 / 총참여인원" style="min-width: 11rem; white-space: nowrap !important; text-align: center;">
                <template #body="{ data }">
                    <span>{{ data.participants }} / {{ data.totalParticipants }}</span>
                </template>
            </Column>
            <!-- 신청 버튼 컬럼 추가 -->
            <Column field="apply" header="신청" style="min-width: 10rem; text-align: center;">
                <template #body="{ data }">
                    <Button label="신청하기" icon="pi pi-check" @click="openApplyModal(data)" />
                </template>
            </Column>
        </DataTable>

        <!-- 신청 모달 컴포넌트 -->
        <Dialog v-model:visible="applyDialogVisible" :header="selectedCourse?.educationName + ' 신청'" modal>
            <div>
                <p><strong>카테고리:</strong> {{ selectedCourse?.category }}</p>
                <p><strong>강사명:</strong> {{ selectedCourse?.instructorName }}</p>
                <p><strong>교육기관:</strong> {{ selectedCourse?.institution }}</p>
                <p><strong>교육 시작일:</strong> {{ formatDate(new Date(selectedCourse?.educationStart)) }}</p>
                <p><strong>교육 종료일:</strong> {{ formatDate(new Date(selectedCourse?.educationEnd)) }}</p>
                <p><strong>신청인원:</strong> {{ selectedCourse?.participants }} / {{ selectedCourse?.totalParticipants }}</p>
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
import axios from 'axios';

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

// 백엔드에서 교육 목록 가져오기
async function fetchCourseList() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/education-service/education');

        // response.data가 배열인지 확인한 후 courses에 할당
        if (Array.isArray(response.data)) {
            courses.value = response.data;
        } else if (response.data && Array.isArray(response.data.educations)) {
            // 만약 응답 데이터가 객체 내에 'educations' 배열을 포함하고 있다면 이렇게 처리
            courses.value = response.data.educations;
        } else {
            console.error("예상하지 못한 응답 데이터 형식입니다.");
        }

        filterByCategoryAndInstructor(); // 필터 적용
    } catch (error) {
        console.error("교육 목록을 불러오는 중 오류가 발생했습니다.", error);
    }
}

// 카테고리 및 강사 목록 가져오기
async function fetchCategories() {
    // API 연동을 위한 카테고리 로직이 필요하면 추가
    categories.value = [{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '관리' }];
}

async function fetchInstructors() {
    // API 연동을 위한 강사 목록 로직이 필요하면 추가
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

// 신청 모달 열기
function openApplyModal(course) {
    selectedCourse.value = course;
    applyDialogVisible.value = true;
}

// 강의 신청 처리 (API 연동)
async function applyForCourse() {
    if (!selectedCourse.value) {
        alert("신청할 교육 과정을 선택하세요.");
        return;
    }
    
    console.log("신청할 교육 과정 번호:", selectedCourse.value.courseId); // courseId 로그

    try {
        // 신청 API 호출
        const response = await axios.post(`http://localhost:8080/api/v1/education-service/education/${selectedCourse.value.courseId}/apply`);
        
        // 신청 완료 후 신청 인원 수 증가
        selectedCourse.value.participants += 1;

        // courses 배열에서도 신청 인원 수 업데이트
        const courseIndex = courses.value.findIndex(course => course.courseId === selectedCourse.value.courseId);
        if (courseIndex !== -1) {
            courses.value[courseIndex].participants += 1;
        }

        alert("신청이 완료되었습니다.");
        applyDialogVisible.value = false; // 모달 닫기
    } catch (error) {
        console.error("신청 처리 중 오류 발생:", error);
        alert("신청 처리 중 오류가 발생했습니다.");
    }
}

// 날짜 포맷팅 함수
function formatDate(date) {
    return new Intl.DateTimeFormat('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    }).format(date);
}

// 컴포넌트가 마운트될 때 데이터 가져오기
onBeforeMount(() => {
    fetchCourseList();
    fetchCategories();
    fetchInstructors();
    initFilters(); // 필터 초기화
});
</script>

<style scoped>
.search-container {
    position: relative;
}

.search-icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
}

.search-input {
    padding-left: 30px; /* 아이콘과 텍스트 간의 간격 조정 */
}
</style>
