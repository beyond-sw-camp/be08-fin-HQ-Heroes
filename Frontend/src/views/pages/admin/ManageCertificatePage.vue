<template>
    <div class="certification-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">자격증 관리</label>
            </div>
            <div class="flex flex-row justify-between mb-4">
                <div class="search-filter">
                    <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="자격증 카테고리" @change="filterCertifications" />
                    <Dropdown v-model="selectedIssuer" :options="issuers" optionLabel="name" placeholder="발급 기관" @change="filterCertifications" />
                    <InputText v-model="globalFilter" placeholder="자격증 검색 (이름, 코드)" @input="filterCertifications" />
                </div>
                <!-- '추가하기' 버튼 -->
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="goToWriteNotice" />
            </div>
            <DataTable
                :value="filteredCertifications"
                paginator
                :rows="10"
                :globalFilterFields="['certificationName', 'certificationCode']"
                dataKey="certificationId"
                @row-click="openCertificationDetail"
                :metaKeySelection="false"
                @rowSelect="onRowSelect"
                @rowUnselect="onRowUnselect"
                selectionMode="single"
                removableSort
            >
                <Column field="certificationCode" sortable header="자격증 코드" />
                <Column field="certificationName" sortable header="자격증명" />
                <Column field="category" sortable header="카테고리" />
                <Column field="issuer" sortable header="발급 기관" />
                <Column field="issueDate" sortable header="신청 날짜" />
                <Column field="expiryDate" sortable header="시험 날짜" />
            </DataTable>
        </div>

        <!-- 자격증 추가 모달 -->
        <Dialog v-model:visible="displayAddDialog" modal="true" header="새로운 자격증 추가" :style="{ width: '450px' }" :draggable="false" :closable="true">
            <div class="flex flex-col gap-6">
                <div>
                    <label for="certificationName" class="block font-bold mb-3">자격증명</label>
                    <InputText id="certificationName" v-model="newCertification.certificationName" required="true" class="w-full" />
                </div>
                <div>
                    <label for="certificationCode" class="block font-bold mb-3">자격증 코드</label>
                    <InputText id="certificationCode" v-model="newCertification.certificationCode" required="true" class="w-full" />
                </div>
                <div>
                    <label for="category" class="block font-bold mb-3">카테고리</label>
                    <Dropdown v-model="newCertification.category" :options="categories" optionLabel="name" placeholder="카테고리 선택" class="w-full" />
                </div>
                <div>
                    <label for="issuer" class="block font-bold mb-3">발급 기관</label>
                    <Dropdown v-model="newCertification.issuer" :options="issuers" optionLabel="name" placeholder="발급 기관 선택" class="w-full" />
                </div>
                <div>
                    <label for="issueDate" class="block font-bold mb-3">신청 날짜</label>
                    <InputText id="issueDate" type="datetime-local" v-model="newCertification.issueDate" required="true" class="w-full" />
                </div>
                <div>
                    <label for="expiryDate" class="block font-bold mb-3">시험 날짜</label>
                    <InputText id="expiryDate" type="datetime-local" v-model="newCertification.expiryDate" class="w-full" />
                </div>
            </div>

            <template #footer>
                <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeDialog" />
                <Button label="저장" icon="pi pi-check" class="p-button-primary" @click="addCertification" />
            </template>
        </Dialog>

        <!-- 자격증 상세 정보 모달 -->
        <Dialog v-model:visible="displayDetailDialog" modal="true" header="자격증 상세 정보" :style="{ width: '450px' }" :draggable="false" :closable="true">
            <div v-if="selectedCertification">
                <p><strong>자격증 코드:</strong> {{ selectedCertification.certificationCode }}</p>
                <p><strong>자격증명:</strong> {{ selectedCertification.certificationName }}</p>
                <p><strong>카테고리:</strong> {{ selectedCertification.category }}</p>
                <p><strong>발급 기관:</strong> {{ selectedCertification.issuer }}</p>
                <p><strong>신청 날짜:</strong> {{ formatDate(selectedCertification.issueDate) }}</p>
                <p><strong>시험 날짜:</strong> {{ formatDate(selectedCertification.expiryDate) }}</p>
            </div>

            <template #footer>
                <Button label="수정" icon="pi pi-pencil" class="p-button-warning" @click="openEditEducationDialog" />
                <Button label="삭제" icon="pi pi-trash" class="p-button-danger" @click="deleteEducation" />
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
import { useRouter } from 'vue-router';
import { onBeforeMount, ref } from 'vue';

// Vue Router 사용을 위한 선언
const router = useRouter();

// 자격증 목록 및 필터링 관련 데이터
const certifications = ref([]);
const filteredCertifications = ref([]);
const selectedCertification = ref(null);
const displayAddDialog = ref(false);
const displayDetailDialog = ref(false);
const selectedCategory = ref(null);
const selectedIssuer = ref(null);
const globalFilter = ref('');

// 카테고리와 발급 기관 리스트
const categories = ref([{ name: '전체' }, { name: 'IT' }, { name: '보안' }, { name: '경영' }]);
const issuers = ref([{ name: '전체' }, { name: '한국산업인력공단' }, { name: 'KISA' }, { name: 'HRD Korea' }]);

// 새로운 자격증을 추가할 때 사용할 데이터
const newCertification = ref({
    certificationName: '',
    certificationCode: '',
    category: null,
    issuer: null,
    issueDate: '',
    expiryDate: ''
});

// '추가하기' 버튼 클릭 시 호출되는 함수
function goToWriteNotice() {
    window.location.href = 'http://localhost:5173/write-notice'; // 새로운 URL로 이동
}

// 더미 데이터를 사용한 자격증 목록
async function fetchCertifications() {
    certifications.value = [
        { certificationId: 1, certificationCode: 'CERT001', certificationName: '정보처리기사', category: 'IT', issuer: '한국산업인력공단', issueDate: '2022-01-01', expiryDate: '2025-01-01' },
        { certificationId: 2, certificationCode: 'CERT002', certificationName: '정보보안기사', category: '보안', issuer: 'KISA', issueDate: '2021-06-01', expiryDate: '2024-06-01' },
        { certificationId: 3, certificationCode: 'CERT003', certificationName: '경영지도사', category: '경영', issuer: 'HRD Korea', issueDate: '2020-11-01', expiryDate: '2023-11-01' }
    ];
    filteredCertifications.value = [...certifications.value];
}

// 필터링 함수
function filterCertifications() {
    filteredCertifications.value = certifications.value.filter((certification) => {
        const matchesCategory = selectedCategory.value?.name !== '전체' ? certification.category === selectedCategory.value.name : true;
        const matchesIssuer = selectedIssuer.value?.name !== '전체' ? certification.issuer === selectedIssuer.value.name : true;
        const matchesFilter = globalFilter.value ? certification.certificationName.includes(globalFilter.value) || certification.certificationCode.includes(globalFilter.value) : true;
        return matchesCategory && matchesIssuer && matchesFilter;
    });
}

// 자격증 추가 모달 열기
function openAddCertificationDialog() {
    displayAddDialog.value = true;
}

// 새로운 자격증 추가 함수
function addCertification() {
    certifications.value.push({ ...newCertification.value, certificationId: certifications.value.length + 1 });
    displayAddDialog.value = false;
    filterCertifications();
    resetNewCertification();
}

// 새로운 자격증 초기화 함수
function resetNewCertification() {
    newCertification.value = {
        certificationName: '',
        certificationCode: '',
        category: null,
        issuer: null,
        issueDate: '',
        expiryDate: ''
    };
}

// 자격증 상세 정보 모달 열기
function openCertificationDetail(event) {
    selectedCertification.value = event.data;
    displayDetailDialog.value = true;
}

// 모달 닫기 함수
function closeDialog() {
    displayAddDialog.value = false;
    displayDetailDialog.value = false;
}

// 날짜 포맷팅 함수
function formatDate(date) {
    return new Date(date).toLocaleDateString('ko-KR');
}

onBeforeMount(() => {
    fetchCertifications();
});
</script>

<style scoped>
.custom-button {
    background-color: #28a745;
    color: white;
}
.custom-button:hover {
    background-color: #218838;
}
</style>
