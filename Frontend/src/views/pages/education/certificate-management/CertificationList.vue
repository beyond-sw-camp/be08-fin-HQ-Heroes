<template>
    <div class="card">
        <h2 class="font-semibold text-xl mb-4">자격증 목록</h2>
        <DataTable
            :value="filteredCertifications"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="certificationId"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['certificationName', 'institution', 'name']"
            showGridlines
            style="table-layout: fixed !important;"
            @row-click="showCertificationDetails"
        >
            <template #header>
                <div class="flex items-center justify-between mb-4">
                    <div class="flex items-center gap-2">
                        <Dropdown v-model="selectedDept" :options="departments" optionLabel="name" placeholder="부서를 선택하세요" @change="filterCertifications" class="mr-2" />
                        <Dropdown v-model="selectedInstitution" :options="institutions" optionLabel="name" placeholder="발급기관을 선택하세요" @change="filterCertifications" class="mr-2" />
                        <div class="relative search-container">
                            <InputText v-model="globalFilter" placeholder="검색" class="pl-8 search-input" />
                            <i class="pi pi-search search-icon" />
                        </div>
                    </div>
                    <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="showAddOrUpdateDialog(null)" />
                </div>
            </template>

            <Column field="certificationId" sortable header="번호" style="min-width: 2rem; text-align: left;" />
            <Column field="certificationName" sortable header="자격명" style="min-width: 20rem; text-align: left;" />
            <Column field="institution" sortable header="발급 기관" style="min-width: 8rem; text-align: left;" />
            <Column field="createdAt" sortable header="취득일" dataType="date" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(data.createdAt) }}
                </template>
            </Column>
        </DataTable>
    </div>
    
    <!-- 자격증 추가 모달 -->
    <Dialog v-model:visible="addDialogVisible" modal="true" header="자격증 추가하기" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="false">
        <div class="flex flex-col gap-6">
            <div>
                <label for="certificationName" class="block font-bold mb-3">자격증 명</label>
                <InputText id="certificationName" v-model="newCertification.certificationName" required="true" class="w-full" />
            </div>
            <div>
                <label for="deptName" class="block font-bold mb-3">카테고리</label>
                <InputText id="deptName" v-model="newCertification.deptName" required="true" placeholder="카테고리 입력" class="w-full" />
            </div>
            <div>
                <label for="educationStart" class="block font-bold mb-3">취득일</label>
                <Calendar id="educationStart" v-model="newCertification.createdAt" required="true" placeholder="YYYY-MM-DD" class="w-full small-calendar" />
            </div>
            <div>
                <label for="institution" class="block font-bold mb-3">기관명</label>
                <InputText id="institution" v-model="newCertification.institution" required="true" class="w-full" />
            </div>
        </div>

        <template #footer>
            <Button label="저장" icon="pi pi-check" class="p-button-primary" @click="isEditing ? updateCertification() : saveCertification()" />
            <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeDialog" />
        </template>
    </Dialog>

    <div class="card">
        <h2 class="font-semibold text-xl my-6">회사가 추천하는 자격증</h2>
        <div class="grid grid-cols-12 gap-4">
            <div class="col-span-12 lg:col-span-6 xl:col-span-3" v-for="cert in recommendedCertifications" :key="cert.certificationId">
                <div class="card mb-1 border border-gray-300 p-4"> 
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">{{ cert.deptName }}</span>
                            <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ cert.certificationName }}</div>
                        </div>
                        <div class="flex items-center justify-center" :class="cert.bgClass" style="width: 2.5rem; height: 2.5rem">
                            <i :class="cert.iconClass"></i>
                        </div>
                    </div>
                    <span class="text-muted-color">{{ formatDate(cert.examDate) }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import axios from 'axios';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import Calendar from 'primevue/calendar';

// 상태 정의
const departments = ref([
    { name: 'IT' },
    { name: 'HR' },
    { name: 'Marketing' },
    { name: 'Sales' }
]);

const certifications = ref([
    { certificationId: 1, certificationName: '정보처리기사', deptName: 'IT', name: '홍길동', institution: '한국산업인력공단', createdAt: '2022-04-15' },
    { certificationId: 2, certificationName: '컴퓨터활용능력 1급', deptName: 'IT', name: '김영희', institution: '대한상공회의소', createdAt: '2021-08-10' },
    { certificationId: 3, certificationName: '네트워크관리사', deptName: '네트워크', name: '이철수', institution: '한국정보통신협회', createdAt: '2020-05-22' },
    { certificationId: 4, certificationName: '정보보안기사', deptName: '보안', name: '박민수', institution: '한국산업인력공단', createdAt: '2023-02-19' },
    { certificationId: 5, certificationName: '데이터분석 전문가', deptName: '데이터', name: '최유리', institution: '한국데이터산업진흥원', createdAt: '2019-12-03' }
]);

const filteredCertifications = ref([...certifications.value]);
const filters = ref(null);
const selectedCertification = ref(null);
const addDialogVisible = ref(false);
const isEditing = ref(false);
const selectedDept = ref(null);
const selectedInstitution = ref(null);
const institutions = ref([]);
const recommendedCertifications = ref([]);
const newCertification = ref({});

// 추천 자격증 목록 가져오기
async function fetchRecommendedCertifications() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/certification-service/certification');
        recommendedCertifications.value = response.data;
    } catch (error) {
        console.error("추천 자격증 목록을 불러오지 못했습니다.", error);
    }
}

// 컴포넌트가 마운트될 때 실행
onBeforeMount(() => {
    fetchRecommendedCertifications();
});

// 자격증 세부 사항 보여주기
function showCertificationDetails(event) {
    selectedCertification.value = event.data;
    isEditing.value = true;
}

// 자격증 추가하기 또는 수정하기 모달 보여주기
function showAddOrUpdateDialog(certification) {
    isEditing.value = false;
    newCertification.value = {}; // 초기화
    addDialogVisible.value = true;
}

// 자격증 추가하기
function saveCertification() {
    newCertification.value.certificationId = certifications.value.length + 1; // 새로운 ID 부여
    certifications.value.push({ ...newCertification.value });
    addDialogVisible.value = false; // 모달 닫기
    filterCertifications();
}

// 자격증 수정하기
function updateCertification() {
    const index = certifications.value.findIndex(cert => cert.certificationId === selectedCertification.value.certificationId);
    if (index !== -1) {
        certifications.value[index] = { ...newCertification.value };
    }
    addDialogVisible.value = false; // 모달 닫기
    filterCertifications();
}

// 필터링 함수
function filterCertifications() {
    filteredCertifications.value = certifications.value.filter(cert => {
        const matchesDept = selectedDept.value ? cert.deptName === selectedDept.value.name : true;
        const matchesInstitution = selectedInstitution.value ? cert.institution === selectedInstitution.value.name : true;
        return matchesDept && matchesInstitution;
    });
}

// 날짜 형식 지정
function formatDate(date) {
    return new Date(date).toLocaleDateString('ko-KR');
}

// 모달 닫기
function closeDialog() {
    addDialogVisible.value = false;
}

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

.small-calendar {
    font-size: 14px; /* 캘린더 글꼴 크기 조정 */
    width: 200px; /* 캘린더 폭 조정 */
}
</style>
