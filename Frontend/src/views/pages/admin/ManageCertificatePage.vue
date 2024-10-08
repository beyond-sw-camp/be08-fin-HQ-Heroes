<template>
    <div class="certification-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">자격증 관리</label>
            </div>
            
            <!-- 필터 및 검색 섹션 -->
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="카테고리를 선택하세요" @change="filterCertifications" class="mr-2" />
                    <Dropdown v-model="selectedInstitution" :options="institutions" optionLabel="name" placeholder="발급기관을 선택하세요" @change="filterCertifications" class="mr-2" />
                    <div class="relative search-container">
                        <InputText v-model="globalFilter" placeholder="검색" class="pl-8 search-input" />
                        <i class="pi pi-search search-icon" />
                    </div>
                </div>
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="goToWriteNotice" />
            </div>

            <!-- 자격증 목록 -->
            <DataTable
                :value="filteredCertifications"
                paginator
                :rows="10"
                :globalFilterFields="['certificationName', 'category', 'institution']"
                dataKey="certificationCode"
                @row-click="showCertificationDetails"
                :metaKeySelection="false"
                selectionMode="single"
                removableSort
            >
                <Column field="certificationCode" sortable header="No." />
                <Column field="category" sortable header="카테고리" />
                <Column field="certificationName" sortable header="자격증 명" />
                <Column field="institution" sortable header="발급 기관" />
                <Column 
                    field="certificationApplyStart" 
                    sortable 
                    header="접수 기간" 
                    >
                    <template #body="{ data }">
                        {{ formatDate(new Date(data.certificationApplyStart)) }} ~ {{ formatDate(new Date(data.certificationApplyEnd)) }}
                    </template>     
                </Column>
                <Column 
                    field="certificationDate" 
                    sortable 
                    header="시험 일" 
                >
                    <template #body="{ data }">
                        {{ formatDate(new Date(data.certificationDate)) }}
                    </template>
                </Column>
            </DataTable>
        </div>

        <!-- 자격증 상세 정보 모달 -->
        <Dialog v-model:visible="displayDetailDialog" modal="true" header="자격증 상세" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="true">
            <div v-if="selectedCertification">
                <p><strong>자격증 코드:</strong> {{ selectedCertification.certificationCode }}</p>
                <p><strong>자격증명:</strong> {{ selectedCertification.certificationName }}</p>
                <p><strong>카테고리:</strong> {{ selectedCertification.category }}</p>
                <p><strong>발급 기관:</strong> {{ selectedCertification.institution }}</p>
                <p><strong>접수 기간:</strong> {{ formatDate(selectedCertification.certificationApplyStart) }} ~ {{ formatDate(selectedCertification.certificationApplyEnd) }}</p>
                <p><strong>시험 일:</strong> {{ formatDate(selectedCertification.certificationDate) }}</p>
                <p><strong>결과 발표:</strong> {{ formatDate(selectedCertification.resultDate) }}</p>
            </div>

            <template #footer>
                <Button label="수정" icon="pi pi-pencil" class="p-button-warning" @click="navigateToWriteNotice" />
                <Button label="삭제" icon="pi pi-trash" class="p-button-danger" @click="confirmDeleteCertification" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';

// 필터 객체 정의 및 초기화
const filters = ref({
    global: { value: '' },  // 'global' 필드를 미리 정의
});

// Vue Router 설정
const router = useRouter();

// 자격증 목록 관련 데이터
const certifications = ref([]);
const filteredCertifications = ref([]);
const selectedCertification = ref(null);
const displayDetailDialog = ref(false);
const selectedCategory = ref(null);
const selectedInstitution = ref(null);
const globalFilter = ref('');

// 카테고리와 발급 기관 리스트
const categories = ref([{ name: '전체' }, { name: 'IT' }, { name: '보안' }, { name: '경영' }]);
const institutions = ref([{ name: '전체' }, { name: '한국산업인력공단' }, { name: 'KISA' }, { name: 'HRD Korea' }]);

// 자격증 목록 가져오기
async function fetchCertifications() {
    certifications.value = [
        { certificationCode: 'A001', certificationName: '정보처리기사', category: 'IT', institution: '한국산업인력공단', certificationApplyStart: '2024-10-01', certificationApplyEnd: '2024-10-10', certificationDate: '2024-10-15', resultDate: '2024-11-15' },
        { certificationCode: 'B002', certificationName: '정보보안기사', category: '보안', institution: 'KISA', certificationApplyStart: '2024-10-05', certificationApplyEnd: '2024-10-12', certificationDate: '2024-10-18', resultDate: '2024-11-18' },
        { certificationCode: 'C003', certificationName: 'HRM 전문가', category: '경영', institution: 'HRD Korea', certificationApplyStart: '2024-09-20', certificationApplyEnd: '2024-09-28', certificationDate: '2024-10-03', resultDate: '2024-11-03' }
    ];
    filteredCertifications.value = certifications.value;
}

// 필터링
function filterCertifications() {
    filteredCertifications.value = certifications.value.filter(cert => {
        const matchesCategory = selectedCategory.value ? cert.category === selectedCategory.value.name : true;
        const matchesInstitution = selectedInstitution.value ? cert.institution === selectedInstitution.value.name : true;
        const matchesGlobalFilter = cert.certificationName.includes(globalFilter.value) || cert.category.includes(globalFilter.value) || cert.institution.includes(globalFilter.value);
        return matchesCategory && matchesInstitution && matchesGlobalFilter;
    });
}

// 수정 화면으로 이동하는 함수
function navigateToWriteNotice() {
    router.push('/write-notice');
}

// 자격증 삭제 확인 함수
function confirmDeleteCertification() {
    const confirmDelete = confirm("해당 자격증을 삭제하시겠습니까?");
    if (confirmDelete) {
        deleteCertification();
    }
}

// 실제 삭제 작업을 수행하는 함수 (구현 필요)
function deleteCertification() {
    // 삭제 로직 구현
    console.log("자격증 삭제됨: ", selectedCertification.value.certificationCode);
    // 모달 닫기
    displayDetailDialog.value = false;
}

// 자격증 세부 사항 보여주기
function showCertificationDetails(event) {
    selectedCertification.value = event.data;
    displayDetailDialog.value = true;
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

// 새로운 자격증 추가 페이지로 이동하는 함수
function goToWriteNotice() {
    router.push('/write-notice');
}

// 컴포넌트가 마운트되기 전에 자격증 목록 가져오기
onBeforeMount(() => {
    fetchCertifications();
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
</style>
