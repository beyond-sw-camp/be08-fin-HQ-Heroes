<template>
    <div class="education-list-page">
        <div class="card">
            <div class="header">
                <h2>현재 진행 중인 교육 목록</h2>
                <div class="search-filter">
                    <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="교육 카테고리" @change="filterEducations" />
                    <Dropdown v-model="selectedInstructor" :options="instructors" optionLabel="name" placeholder="강사" @change="filterEducations" />
                    <InputText v-model="globalFilter" placeholder="교육 검색 (이름, 코드)" @input="filterEducations" />
                </div>
                <Button label="교육 추가" icon="pi pi-plus" class="p-button-success" @click="openAddEducationDialog" />
            </div>
            <DataTable
                :value="filteredEducations"
                paginator
                :rows="10"
                :globalFilterFields="['educationName', 'educationCode']"
                dataKey="educationId"
                @row-click="openEducationDetail"
                :metaKeySelection="false"
                @rowSelect="onRowSelect"
                @rowUnselect="onRowUnselect"
                selectionMode="single"
                removableSort
            >
                <Column field="educationCode" sortable header="교육 코드" />
                <Column field="educationName" sortable header="교육명" />
                <Column field="category" sortable header="카테고리" />
                <Column field="instructor" sortable header="강사" />
                <Column field="startDate" sortable header="시작일" />
                <Column field="endDate" sortable header="종료일" />
            </DataTable>
        </div>

        <!-- 교육 추가 모달 -->
        <Dialog v-model:visible="displayAddDialog" modal="true" header="새로운 교육 추가" :style="{ width: '450px' }" :draggable="false" :closable="true">
            <div class="flex flex-col gap-6">
                <div>
                    <label for="educationName" class="block font-bold mb-3">교육명</label>
                    <InputText id="educationName" v-model="newEducation.educationName" required="true" class="w-full" />
                </div>
                <div>
                    <label for="educationCode" class="block font-bold mb-3">교육 코드</label>
                    <InputText id="educationCode" v-model="newEducation.educationCode" required="true" class="w-full" />
                </div>
                <div>
                    <label for="category" class="block font-bold mb-3">카테고리</label>
                    <Dropdown v-model="newEducation.category" :options="categories" optionLabel="name" placeholder="카테고리 선택" class="w-full" />
                </div>
                <div>
                    <label for="instructor" class="block font-bold mb-3">강사</label>
                    <Dropdown v-model="newEducation.instructor" :options="instructors" optionLabel="name" placeholder="강사 선택" class="w-full" />
                </div>
                <div>
                    <label for="startDate" class="block font-bold mb-3">시작일</label>
                    <InputText id="startDate" type="datetime-local" v-model="newEducation.startDate" required="true" class="w-full" />
                </div>
                <div>
                    <label for="endDate" class="block font-bold mb-3">종료일</label>
                    <InputText id="endDate" type="datetime-local" v-model="newEducation.endDate" class="w-full" />
                </div>
            </div>

            <template #footer>
                <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeDialog" />
                <Button label="저장" icon="pi pi-check" class="p-button-primary" @click="addEducation" />
            </template>
        </Dialog>

        <!-- 교육 상세 정보 모달 -->
        <Dialog v-model:visible="displayDetailDialog" modal="true" header="교육 상세 정보" :style="{ width: '450px' }" :draggable="false" :closable="true">
            <div v-if="selectedEducation">
                <p><strong>교육 코드:</strong> {{ selectedEducation.educationCode }}</p>
                <p><strong>교육명:</strong> {{ selectedEducation.educationName }}</p>
                <p><strong>카테고리:</strong> {{ selectedEducation.category }}</p>
                <p><strong>강사:</strong> {{ selectedEducation.instructor }}</p>
                <p><strong>시작일:</strong> {{ formatDate(selectedEducation.startDate) }}</p>
                <p><strong>종료일:</strong> {{ formatDate(selectedEducation.endDate) }}</p>
            </div>

            <template #footer>
                <Button label="닫기" icon="pi pi-times" text class="p-button-text" @click="closeDetailDialog" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';

// 교육 목록 및 필터링 관련 데이터
const educations = ref([]);
const filteredEducations = ref([]);
const selectedEducation = ref(null);
const displayAddDialog = ref(false);
const displayDetailDialog = ref(false);
const selectedCategory = ref(null);
const selectedInstructor = ref(null);
const globalFilter = ref('');

// 카테고리와 강사 리스트
const categories = ref([{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '인사' }]);
const instructors = ref([{ name: '전체' }, { name: '홍길동' }, { name: '이순신' }, { name: '김영희' }]);

// 새로운 교육을 추가할 때 사용할 데이터
const newEducation = ref({
    educationName: '',
    educationCode: '',
    category: null,
    instructor: null,
    startDate: '',
    endDate: ''
});

// 더미 데이터를 사용한 교육 목록
async function fetchEducations() {
    educations.value = [
        { educationId: 1, educationCode: 'EDU001', educationName: '프론트엔드 개발', category: '개발', instructor: '홍길동', startDate: '2022-01-01', endDate: '2022-02-01' },
        { educationId: 2, educationCode: 'EDU002', educationName: 'UI/UX 디자인', category: '디자인', instructor: '이순신', startDate: '2021-03-01', endDate: '2021-04-01' },
        { educationId: 3, educationCode: 'EDU003', educationName: 'HR 관리', category: '인사', instructor: '김영희', startDate: '2020-05-01', endDate: '2020-06-01' }
    ];
    // 더미 데이터로 초기 필터링된 데이터를 설정
    filteredEducations.value = [...educations.value];
}

// 필터링 함수
function filterEducations() {
    filteredEducations.value = educations.value.filter((education) => {
        const matchesCategory = selectedCategory.value?.name !== '전체' ? education.category === selectedCategory.value.name : true;
        const matchesInstructor = selectedInstructor.value?.name !== '전체' ? education.instructor === selectedInstructor.value.name : true;
        const matchesFilter = globalFilter.value ? education.educationName.includes(globalFilter.value) || education.educationCode.includes(globalFilter.value) : true;
        return matchesCategory && matchesInstructor && matchesFilter;
    });
}

// 교육 추가 모달 열기
function openAddEducationDialog() {
    displayAddDialog.value = true;
}

// 새로운 교육 추가 함수
function addEducation() {
    educations.value.push({ ...newEducation.value, educationId: educations.value.length + 1 });
    displayAddDialog.value = false;
    filterEducations();
    resetNewEducation();
}

// 새로운 교육 초기화 함수
function resetNewEducation() {
    newEducation.value = {
        educationName: '',
        educationCode: '',
        category: null,
        instructor: null,
        startDate: '',
        endDate: ''
    };
}

// 모달 닫기 함수
function closeDialog() {
    displayAddDialog.value = false;
}

// 교육 상세 정보 모달 열기
function openEducationDetail(event) {
    selectedEducation.value = event.data;
    displayDetailDialog.value = true;
}

// 교육 상세 모달 닫기
function closeDetailDialog() {
    displayDetailDialog.value = false;
}

// 날짜 형식 변환 함수
function formatDate(date) {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

onBeforeMount(() => {
    fetchEducations();
});
</script>

<style scoped>
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.search-filter {
    display: flex;
    gap: 10px;
}
.flex {
    display: flex;
}
.flex-col {
    flex-direction: column;
}
.gap-6 {
    gap: 1.5rem;
}
.w-full {
    width: 100%;
}
.p-button-primary {
    background-color: #007bff;
    border-color: #007bff;
}
.p-button-text {
    color: #007bff;
}
</style>
