<template>
    <div class="education-list-page">
        <div class="card">
            <div class="header">
                <h2>현재 진행 중인 교육 목록</h2>
                <div class="search-filter">
                    <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="교육 카테고리" @change="filterEducations" />
                    <Dropdown v-model="selectedInstructorName" :options="instructorNames" optionLabel="name" placeholder="강사" @change="filterEducations" />
                    <InputText v-model="globalFilter" placeholder="교육 검색 (이름, 코드)" @input="filterEducations" />
                </div>
                <Button label="교육 추가" icon="pi pi-plus" class="custom-button" @click="openAddEducationDialog" />
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
                <Column field="educationId" sortable header="교육 코드" />
                <Column field="educationName" sortable header="교육명" />
                <Column field="category" sortable header="카테고리" />
                <Column field="instructorName" sortable header="강사" />
                <Column field="educationStart" sortable header="시작일" :body="(rowData) => formatDate(rowData.educationStart)" />
                <Column field="educationEnd" sortable header="종료일" :body="(rowData) => formatDate(rowData.educationEnd)" />
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
                <div>
                    <label for="educationStart" class="block font-bold mb-3">시작일</label>
                    <InputText id="educationStart" v-model="newEducation.educationStart" required="true" placeholder="YYYY-MM-DD HH:mm" class="w-full" />
                </div>
                <div>
                    <label for="educationEnd" class="block font-bold mb-3">종료일</label>
                    <InputText id="educationEnd" v-model="newEducation.educationEnd" required="true" placeholder="YYYY-MM-DD HH:mm" class="w-full" />
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
                <!-- <Button label="닫기" icon="pi pi-times" text class="p-button-text" @click="closeDetailDialog" /> -->
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import axios from 'axios';
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

// 필수 입력값 검증
function validateEducationData() {
    return newEducation.value.educationName && newEducation.value.category && newEducation.value.instructorName &&
        newEducation.value.educationStart && newEducation.value.educationEnd &&
        newEducation.value.institution && newEducation.value.totalParticipants;
}

// 새로운 교육을 추가할 때 사용할 데이터
const newEducation = ref({
    educationName: '',
    category: '', 
    instructorName: '', 
    educationStart: '',
    educationEnd: '',
    institution: '',
    totalParticipants: 0
});

// 새로운 교육 추가 함수
async function addEducation() {
    if (!validateEducationData()) {
        console.error('필수 입력값이 누락되었습니다.');
        return;
    }

    console.log('전송할 데이터:', newEducation.value);

    // 입력된 날짜를 YYYY-MM-DDTHH:mm:ss 형식으로 변환
    const educationStart = new Date(newEducation.value.educationStart).toISOString().slice(0, 19);
    const educationEnd = new Date(newEducation.value.educationEnd).toISOString().slice(0, 19);

    try {
        const response = await axios.post('http://localhost:8080/api/v1/education-service/education', {
            ...newEducation.value,
            educationStart,
            educationEnd,
            totalParticipants: newEducation.value.totalParticipants // totalParticipants 필드 추가
        });

        educations.value.push(response.data);
        resetNewEducation();
        fetchEducations();
        displayAddDialog.value = false;
    } catch (error) {
        if (error.response) {
            // 서버가 응답을 반환했으나 상태 코드가 2xx가 아닐 경우
            console.error('서버 오류:', error.response.data);
        } else if (error.request) {
            // 요청은 했으나 응답이 없을 경우
            console.error('요청을 보냈으나 응답을 받지 못했습니다.');
        } else {
            // 오류를 발생시킨 요청 설정 중 오류
            console.error('오류 발생:', error.message);
        }
    }
}

// 교육 업데이트 함수
async function updateEducation() {
    if (!validateEducationData()) {
        console.error('필수 입력값이 누락되었습니다.');
        return;
    }
    
    console.log('수정할 데이터:', newEducation.value);
    
    // 입력된 날짜를 YYYY-MM-DDTHH:mm:ss 형식으로 변환
    const educationStart = new Date(newEducation.value.educationStart).toISOString().slice(0, 19);
    const educationEnd = new Date(newEducation.value.educationEnd).toISOString().slice(0, 19);
    
    try {
        const response = await axios.put(`http://localhost:8080/api/v1/education-service/education/${selectedEducation.value.educationId}`, {
            ...newEducation.value,
            educationStart,
            educationEnd,
        });
        const index = educations.value.findIndex(edu => edu.educationId === selectedEducation.value.educationId);
        educations.value.splice(index, 1, response.data);
        resetNewEducation();
        fetchEducations();
        displayAddDialog.value = false;
    } catch (error) {
        console.error('교육 수정 중 오류 발생:', error);
    }
}

// 교육 삭제 함수
async function deleteEducation() {
    if (!selectedEducation.value) return;

    try {
        await axios.delete(`http://localhost:8080/api/v1/education-service/education/${selectedEducation.value.educationId}`);
        educations.value = educations.value.filter(edu => edu.educationId !== selectedEducation.value.educationId);
        filteredEducations.value = [...educations.value];
        selectedEducation.value = null;
        closeDetailDialog();
    } catch (error) {
        console.error('교육 삭제 중 오류 발생:', error);
    }
}

// 교육 상세 정보 열기
function openEducationDetail(event) {
    selectedEducation.value = event.data;
    displayDetailDialog.value = true;
}

// 교육 상세 정보 닫기
function closeDetailDialog() {
    displayDetailDialog.value = false;
    selectedEducation.value = null;
}

// 다이얼로그 닫기
function closeDialog() {
    displayAddDialog.value = false;
}

// 새로운 교육 리셋
function resetNewEducation() {
    newEducation.value = {
        educationName: '',
        category: '', 
        instructorName: '', 
        educationStart: '',
        educationEnd: '',
        institution: '',
        totalParticipants: ''
    };
}

// 날짜 포맷팅
function formatDate(dateString) {
    if (!dateString) return '';
    const options = { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit', 
        hour: '2-digit', 
        minute: '2-digit', 
        hour12: false };
    return new Date(dateString).toLocaleString('ko-KR', options);
}

onBeforeMount(() => {
    fetchEducations();
});
</script>


<style scoped>
.education-list-page {
    margin: 20px;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.search-filter {
    display: flex;
    align-items: center;
    gap: 10px;
}

.custom-button {
    margin-left: 10px;
}
</style>
