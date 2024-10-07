<template>
    <div class="education-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">교육 관리</label>
            </div>
            <div class="flex flex-row justify-between mb-4">
                <div class="search-filter">
                    <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="교육 카테고리" @change="filterEducations" />
                    <Dropdown v-model="selectedInstructorName" :options="instructorNames" optionLabel="name" placeholder="강사" @change="filterEducations" />
                    <InputText v-model="globalFilter" placeholder="교육 검색 (이름, 코드)" @input="filterEducations" />
                </div>
                <Button label="교육 추가" icon="pi pi-plus" class="custom-button" @click="navigateToWriteNotice" />
            </div>
            <DataTable
                :value="filteredEducations"
                paginator
                :rows="10"
                :globalFilterFields="['educationName', 'educationId']"
                dataKey="educationId"
                @row-click="openEducationDetail"
                :metaKeySelection="false"
                @rowSelect="onRowSelect"
                @rowUnselect="onRowUnselect"
                selectionMode="single"
                removableSort
            >
                <Column field="educationId" sortable header="No." />
                <Column field="category" sortable header="카테고리" />
                <Column field="educationName" sortable header="교육명" />
                <Column field="instructorName" sortable header="강사" />
                <Column field="educationStart" sortable header="신청 기간" :body="(rowData) => formatDate(rowData.educationStart)" />
                <Column field="educationEnd" sortable header="수강 기간" :body="(rowData) => formatDate(rowData.educationEnd)" />
            </DataTable>
        </div>

        <!-- 교육 추가/수정 모달 -->
        <Dialog v-model:visible="displayAddDialog" modal="true" :header="isEditing ? '교육 수정' : '새로운 교육 추가'" :style="{ width: '450px' }" :draggable="false" :closable="true">
            <div class="flex flex-col gap-6">
                <div>
                    <label for="educationName" class="block font-bold mb-3">교육명</label>
                    <InputText id="educationName" v-model="newEducation.educationName" required="true" class="w-full" />
                </div>
                <div>
                    <label for="category" class="block font-bold mb-3">카테고리</label>
                    <InputText id="category" v-model="newEducation.category" required="true" placeholder="카테고리 입력" class="w-full" />
                </div>
                <div>
                    <label for="instructorName" class="block font-bold mb-3">강사</label>
                    <InputText id="instructorName" v-model="newEducation.instructorName" required="true" placeholder="강사 이름 입력" class="w-full" />
                </div>
                <div class="time-section">
                    <div class="time-block">
                        <label for="educationStart" class="block font-bold mb-3">신청 기간</label>
                        <input type="datetime-local" id="educationStart" v-model="newEducation.educationStart" required="true" class="w-full" />
                    </div>
                    <div class="time-block">
                        <label for="educationEnd" class="block font-bold mb-3">수강 기간</label>
                        <input type="datetime-local" id="educationEnd" v-model="newEducation.educationEnd" required="true" class="w-full" />
                    </div>
                </div>
                <div>
                    <label for="institution" class="block font-bold mb-3">기관명</label>
                    <InputText id="institution" v-model="newEducation.institution" required="true" class="w-full" />
                </div>
                <div>
                    <label for="totalParticipants" class="block font-bold mb-3">총 인원</label>
                    <InputText id="totalParticipants" v-model="newEducation.totalParticipants" required="true" class="w-full" />
                </div>
            </div>

            <template #footer>
                <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeDialog" />
                <Button label="저장" icon="pi pi-check" class="p-button-primary" @click="isEditing ? updateEducation() : addEducation()" />
            </template>
        </Dialog>

        <!-- 교육 상세 정보 모달 -->
        <Dialog v-model:visible="displayDetailDialog" modal="true" header="교육 상세 정보" :style="{ width: '450px' }" :draggable="false" :closable="true">
            <div v-if="selectedEducation">
                <p><strong>교육 코드:</strong> {{ selectedEducation.educationId }}</p>
                <p><strong>교육명:</strong> {{ selectedEducation.educationName }}</p>
                <p><strong>카테고리:</strong> {{ selectedEducation.category }}</p>
                <p><strong>강사:</strong> {{ selectedEducation.instructorName }}</p>
                <p><strong>시작일:</strong> {{ formatDate(selectedEducation.educationStart) }}</p>
                <p><strong>종료일:</strong> {{ formatDate(selectedEducation.educationEnd) }}</p>
                <p><strong>기관명:</strong> {{ selectedEducation.institution }}</p>
                <p><strong>총 인원:</strong> {{ selectedEducation.totalParticipants }}</p>
            </div>

            <template #footer>
                <Button label="수정" icon="pi pi-pencil" class="p-button-warning" @click="openEditEducationDialog" />
                <Button label="삭제" icon="pi pi-trash" class="p-button-danger" @click="deleteEducation" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import axios from 'axios';
import { useRouter } from 'vue-router'; // vue-router import 추가
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
const selectedInstructorName = ref(null);
const globalFilter = ref('');
const isEditing = ref(false);

// 카테고리와 강사 리스트
const categories = ref([{ name: '전체' }, { name: '개발' }, { name: '디자인' }, { name: '인사' }]);
const instructorNames = ref([{ name: '전체' }, { name: '홍길동' }, { name: '이순신' }, { name: '김영희' }]);

// Vue Router 사용 설정
const router = useRouter();

// API 호출을 통해 교육 목록 가져오기
async function fetchEducations() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/education-service/education');
        educations.value = response.data;
        filteredEducations.value = [...educations.value].map(education => ({
            ...education,
            educationStart: formatDate(education.educationStart),
            educationEnd: formatDate(education.educationEnd),
        }));
    } catch (error) {
        console.error('교육 목록을 가져오는 중 오류 발생:', error);
    }
}

// 필터링 함수
function filterEducations() {
    filteredEducations.value = educations.value.filter((education) => {
        const matchesCategory = selectedCategory.value?.name !== '전체' ? education.category === selectedCategory.value.name : true;
        const matchesInstructorName = selectedInstructorName.value?.name !== '전체' ? education.instructorName === selectedInstructorName.value.name : true;
        const matchesFilter = globalFilter.value ? education.educationName.includes(globalFilter.value) || education.educationId.includes(globalFilter.value) : true;
        return matchesCategory && matchesInstructorName && matchesFilter;
    }).map(education => ({
        ...education,
        educationStart: formatDate(education.educationStart),
        educationEnd: formatDate(education.educationEnd),
    }));
}

// "추가하기" 버튼 클릭 시 페이지 이동
function navigateToWriteNotice() {
    window.location.href = 'http://localhost:5173/write-notice'; // 새로운 URL로 이동
}

// 교육 추가 모달 열기
function openAddEducationDialog() {
    isEditing.value = false;
    resetNewEducation();
    displayAddDialog.value = true;
}

// 교육 수정 모달 열기
function openEditEducationDialog() {
    isEditing.value = true;
    Object.assign(newEducation.value, selectedEducation.value);
    displayAddDialog.value = true;
}

// 교육 삭제
async function deleteEducation() {
    if (selectedEducation.value) {
        try {
            await axios.delete(`http://localhost:8080/api/v1/education-service/education/${selectedEducation.value.educationId}`);
            fetchEducations();
            selectedEducation.value = null;
            displayDetailDialog.value = false;
        } catch (error) {
            console.error('교육 삭제 중 오류 발생:', error);
        }
    }
}

// 교육 상세보기 모달 열기
function openEducationDetail(event) {
    selectedEducation.value = event.data;
    displayDetailDialog.value = true;
}

// 데이터 리셋
function resetNewEducation() {
    newEducation.value = {
        educationName: '',
        category: '',
        instructorName: '',
        educationStart: '',
        educationEnd: '',
        institution: '',
        totalParticipants: '',
    };
}

// 날짜 포맷팅 함수
function formatDate(date) {
    return date ? new Date(date).toLocaleString('ko-KR', { timeZone: 'Asia/Seoul' }) : '';
}

// 컴포넌트 마운트 시 교육 목록 가져오기
onBeforeMount(fetchEducations);
</script>

<style scoped>
.education-list-page {
    padding: 20px;
}
.custom-button {
    margin-left: 10px;
}
.time-section {
    display: flex;
    justify-content: space-between;
}
.time-block {
    width: 48%;
}
</style>
