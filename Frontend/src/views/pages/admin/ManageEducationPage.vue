<template>
    <div class="education-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">교육 관리</label>
            </div>

            <!-- 필터 및 검색 섹션 -->
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="카테고리를 선택하세요" @change="filterEducations" class="mr-2" />
                    <Calendar v-model="selectedDate" placeholder="날짜를 선택하세요" :showIcon="true" class="mr-2" @change="filterEducations" />
                    <div class="relative search-container">
                        <InputText v-model="globalFilter" placeholder="검색" class="pl-8 search-input" />
                        <i class="pi pi-search search-icon" />
                    </div>
                </div>
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="goToWriteNotice" />
            </div>

            <!-- 교육 목록 테이블 -->
            <DataTable
                :value="filteredEducations"
                paginator
                :rows="10"
                :globalFilterFields="['educationName', 'category', 'institution']"
                dataKey="educationId"
                @row-click="showEducationDetails"
                :metaKeySelection="false"
                selectionMode="single"
                removableSort
            >
                <Column field="educationId" sortable header="No." />
                <Column field="category" sortable header="카테고리" />
                <Column field="educationName" sortable header="교육 명" />
                <Column field="institution" sortable header="발급 기관" />
                <Column 
                    field="educationApplyStart" 
                    sortable 
                    header="신청 기간" 
                >
                    <template #body="{ data }">
                        {{ formatDate(data.educationApplyStart) }} ~ {{ formatDate(data.educationApplyEnd) }}
                    </template>     
                </Column>
                <Column 
                    field="educationDate" 
                    sortable 
                    header="교육 기간" 
                >
                    <template #body="{ data }">
                        {{ formatDate(data.educationStart) }} ~ {{ formatDate(data.educationEnd) }}
                    </template>
                </Column>
            </DataTable>
        </div>
        <!-- 교육 상세 정보 모달 -->
        <Dialog v-model:visible="displayDetailDialog" modal="true" header="교육 상세 정보" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="true">
            <div v-if="selectedEducation">
                <p><strong>교육 코드:</strong> {{ selectedEducation.educationId }}</p>
                <p><strong>교육명:</strong> {{ selectedEducation.educationName }}</p>
                <p><strong>카테고리:</strong> {{ selectedEducation.category }}</p>
                <p><strong>강사:</strong> {{ selectedEducation.instructorName }}</p>
                <p><strong>신청 기간:</strong> {{ formatDate(selectedEducation.educationApplyStart) }} ~ {{ formatDate(selectedEducation.educationApplyEnd) }}</p>
                <p><strong>교육 기간:</strong> {{ formatDate(selectedEducation.educationStart) }} ~ {{ formatDate(selectedEducation.educationEnd) }}</p>
                <p><strong>기관명:</strong> {{ selectedEducation.institution }}</p>
                <p><strong>현재 인원:</strong> {{ selectedEducation.participant }}</p>
                <p><strong>총 인원:</strong> {{ selectedEducation.totalParticipants }}</p>
            </div>
            <template #footer>
                <Button label="수정" icon="pi pi-pencil" class="p-button-warning" @click="navigateToWriteNotice" />
                <Button label="삭제" icon="pi pi-trash" class="p-button-danger" @click="confirmDeleteEducation" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import axios from 'axios';
import { useRouter } from 'vue-router';
import { ref, onBeforeMount } from 'vue';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import Calendar from 'primevue/calendar';

// 필터 및 검색 관련 데이터
const educations = ref([]);
const filteredEducations = ref([]);
const selectedEducation = ref(null);
const displayDetailDialog = ref(false);
const selectedCategory = ref(null);
const selectedDate = ref(null);
const globalFilter = ref('');

// 카테고리 목록
const categories = ref([{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '인사' }]);

// Vue Router 설정
const router = useRouter();

// 필터링 함수
function filterEducations() {
    filteredEducations.value = educations.value.filter((education) => {
        const matchesCategory = selectedCategory.value?.name !== '전체' ? education.category === selectedCategory.value.name : true;
        const matchesDate = selectedDate.value ? new Date(education.educationStart) <= selectedDate.value && new Date(education.educationEnd) >= selectedDate.value : true;
        const matchesGlobalFilter = globalFilter.value
            ? education.educationName.toLowerCase().includes(globalFilter.value.toLowerCase())
            : true;

        return matchesCategory && matchesDate && matchesGlobalFilter;
    });
}

// 교육 상세보기 함수
function showEducationDetails(event) {
    selectedEducation.value = event.data;
    displayDetailDialog.value = true;
}

// 교육 추가 화면으로 이동
function goToWriteNotice() {
    router.push('/write-notice');
}

// 교육 삭제 확인 함수
function confirmDeleteEducation() {
    const confirmDelete = window.confirm("해당 교육을 삭제하시겠습니까?");
    if (confirmDelete) {
        deleteEducation();
    }
}

// 교육 삭제 함수
function deleteEducation() {
    // 여기에서 실제 삭제 작업을 수행하세요.
    console.log("교육이 삭제되었습니다.", selectedEducation.educationId);
    // 삭제 후 모달 닫기 및 데이터 새로 고침 등의 작업 추가
}

// 수정 화면으로 이동하는 함수 이름 변경
function navigateToWriteNotice() {
    router.push('/write-notice');
}

// 교육 목록 가져오는 함수
function fetchEducations() {
    educations.value = [
        {
            educationId: '1',
            educationName: 'Vue.js 기초',
            category: '개발',
            instructorName: '홍길동',
            educationApplyStart: '2024-10-01',
            educationApplyEnd: '2024-10-10',
            educationStart: '2024-10-15',
            educationEnd: '2024-10-20',
            institution: '코드스쿨',
            participant: 10,
            totalParticipants: 30
        },
        {
            educationId: '2',
            educationName: 'UI/UX 디자인',
            category: '디자인',
            instructorName: '이순신',
            educationApplyStart: '2024-10-05',
            educationApplyEnd: '2024-10-12',
            educationStart: '2024-10-20',
            educationEnd: '2024-11-02',
            institution: '디자인 아카데미',
            participant: 10,
            totalParticipants: 25
        },
        {
            educationId: '3',
            educationName: '인사 관리',
            category: '인사',
            instructorName: '김영희',
            educationApplyStart: '2024-10-03',
            educationApplyEnd: '2024-10-08',
            educationStart: '2024-10-12',
            educationEnd: '2024-10-23',
            institution: 'HR 센터',
            participant: 10,
            totalParticipants: 20
        }
    ]; // 더미 데이터를 할당
    filteredEducations.value = educations.value; // 필터링 목록에 추가
}

// 날짜 포맷팅 함수
function formatDate(date) {
    if (!date) return ''; // 날짜가 없을 경우 빈 문자열 반환
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
}

// 컴포넌트가 마운트될 때 교육 목록 가져오기
onBeforeMount(() => {
    fetchEducations(); // fetchEducations 호출
});
</script>

<style scoped>
.search-container {
    position: relative;
}

.search-input {
    padding-left: 30px;
}

.search-icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
}

.custom-button {
    margin-left: auto;
}

.time-section {
    display: flex;
    justify-content: space-between;
}

.time-block {
    flex: 1;
    margin-right: 10px;
}

.time-block:last-child {
    margin-right: 0;
}
</style>
