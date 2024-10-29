<template>
    <div class="certification-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">자격증 관리</label>
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="showAddCertification" />
            </div>

            <!-- 자격증 목록 테이블 -->
            <DataTable :value="filteredCertifications" v-model:selection="selectedCertification" paginator dataKey="certificationId" :rows="10" :globalFilterFields="['certificationName', 'deptName', 'institution']" removableSort>
                <!-- 필터 및 검색 섹션 -->
                <template #header>
                    <div class="flex items-center justify-between mb-4">
                        <div class="flex items-center gap-2">
                            <!-- 부서 선택 드롭다운 -->
                            <Dropdown v-model="selectedDepartment" :options="departments" optionLabel="deptName" placeholder="부서를 선택하세요" @change="filterCertifications" class="mr-2" />
                        </div>
                        <div class="relative search-container">
                            <InputText v-model="globalFilter" placeholder="검색" class="pl-8 search-input" />
                            <i class="pi pi-search search-icon" />
                        </div>
                    </div>
                </template>

                <Column field="deptName" sortable header="부서 명" />
                <Column field="certificationName" sortable header="자격증 명" />
                <Column field="institution" sortable header="발급 기관" />
                <Column field="benefit" sortable header="혜택" />
                <Column class="w-16 !text-end">
                    <template #body="{ data }">
                        <Button icon="pi pi-search" @click="showCertificationDetails(data)" severity="info" rounded></Button>
                    </template>
                </Column>
                <Column class="w-16 !text-end">
                    <template #body="{ data }">
                        <Button icon="pi pi-pencil" @click="showEditCertification(data)" severity="primary" rounded></Button>
                    </template>
                </Column>
                <Column class="w-16 !text-end">
                    <template #body="{ data }">
                        <Button icon="pi pi-trash" @click="deleteCertification(data)" severity="danger" rounded></Button>
                    </template>
                </Column>
            </DataTable>
        </div>

        <!-- 자격증 조회 모달 -->
        <Dialog :visible="isDetailDialogVisible" modal header="자격증 정보" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="false">
            <div class="p-4">
                <p><strong>부서:</strong> {{ selectedCertification.deptName || 'N/A' }}</p>
                <p><strong>자격증 명:</strong> {{ selectedCertification.certificationName || 'N/A' }}</p>
                <p><strong>발급 기관:</strong> {{ selectedCertification.institution || 'N/A' }}</p>
                <p><strong>혜택:</strong> {{ selectedCertification.benefit || 'N/A' }}</p>
            </div>
            <template #footer>
                <Button label="닫기" icon="pi pi-times" @click="isDetailDialogVisible = false" />
            </template>
        </Dialog>

        <!-- 자격증 추가 모달 -->
        <Dialog v-model:visible="isAddDialogVisible" modal header="자격증 추가하기" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="false">
            <div class="flex flex-col gap-6">
                <div>
                    <Select v-model="selectedDepartment" :options="departments" id="deptId" optionLabel="deptName" placeholder="부서를 선택하세요" @change="changeSelectedAddDeptId(selectedDepartment.deptId)" />
                </div>
                <div>
                    <label for="certificationName" class="block font-bold mb-3">자격증 명</label>
                    <InputText id="certificationName" v-model="addCertificationData.certificationName" required="true" class="w-full" placeholder="자격증 명을 입력해주세요." />
                </div>
                <div>
                    <label for="institution" class="block font-bold mb-3">기관 명</label>
                    <InputText id="institution" v-model="addCertificationData.institution" required="true" class="w-full" placeholder="발급 기관을 입력해주세요" />
                </div>
                <div>
                    <label for="benefit" class="block font-bold mb-3">혜택</label>
                    <InputText id="benefit" v-model="addCertificationData.benefit" required="true" class="w-full" placeholder="혜택을 입력해주세요." />
                </div>
            </div>

            <template #footer>
                <Button label="저장" icon="pi pi-check" class="p-button-primary" @click="saveCertification" />
                <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="isAddDialogVisible = false" />
            </template>
        </Dialog>

        <!-- 자격증 수정 모달 -->
        <Dialog v-model:visible="isEditDialogVisible" modal header="자격증 수정하기" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="false">
            <div class="flex flex-col gap-6">
                <div>
                    <Select v-model="selectedDepartment" :options="departments" id="deptId" optionLabel="deptName" placeholder="부서를 선택하세요" @change="changeSelectedEditDeptId(selectedDepartment.deptId)" />
                </div>
                <div>
                    <label for="certificationName" class="block font-bold mb-3">자격증 명</label>
                    <InputText id="certificationName" v-model="editCertificationData.certificationName" required="true" class="w-full" placeholder="자격증 명을 입력해주세요." />
                </div>
                <div>
                    <label for="institution" class="block font-bold mb-3">기관 명</label>
                    <InputText id="institution" v-model="editCertificationData.institution" required="true" class="w-full" placeholder="발급 기관을 입력해주세요" />
                </div>
                <div>
                    <label for="benefit" class="block font-bold mb-3">혜택</label>
                    <InputText id="benefit" v-model="editCertificationData.benefit" required="true" class="w-full" placeholder="혜택을 입력해주세요." />
                </div>
            </div>

            <template #footer>
                <Button label="저장" icon="pi pi-check" class="p-button-primary" @click="editCertification" />
                <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="isEditDialogVisible = false" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { fetchPost, fetchPut, fetchDelete } from '../auth/service/AuthApiService';

const certifications = ref([]);
const filteredCertifications = ref([]);
const selectedDepartment = ref(null);
const departments = ref([]);
const globalFilter = ref('');
const router = useRouter();
const isDetailDialogVisible = ref(false);
const selectedCertification = ref({
    certificationName: '',
    benefit: '',
    institution: '',
    deptId: 0
});

const isAddDialogVisible = ref(false);
const addCertificationData = ref({
    certificationName: '',
    benefit: '',
    institution: '',
    deptId: 0
});

const isEditDialogVisible = ref(false);
const editCertificationData = ref({
    certificationId: 0,
    certificationName: '',
    benefit: '',
    institution: '',
    deptId: 0,
    deptName: ''
});

const delelteCertificationData = ref({
    certificationId: 0,
    certificationName: '',
    benefit: '',
    institution: '',
    deptId: 0,
    deptName: ''
});

async function fetchCertifications() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/certification-service/certification');
        certifications.value = response.data.reverse(); // 역순으로 정렬
        console.log(response);
        filteredCertifications.value = certifications.value;
    } catch (error) {
        console.error('자격증 목록을 불러오지 못했습니다.', error);
    }
}

async function fetchDepartments() {
    try {
        const departmentsData = await axios.get('http://localhost:8080/api/v1/employee/departments');
        departments.value = [{ deptId: null, deptName: '전체 부서' }, ...departmentsData.data];
    } catch (error) {
        console.error('부서 데이터를 가져오는 중 오류 발생:', error);
        departments.value = [{ deptId: null, deptName: '전체 부서' }];
    }
}

// 선택된 부서와 전역 검색어로 필터링
function filterCertifications() {
    filteredCertifications.value = certifications.value.filter((certification) => {
        const matchesDept = selectedDepartment.value && selectedDepartment.value.deptName !== '전체 부서' ? certification.deptName === selectedDepartment.value.deptName : true;
        const matchesGlobalFilter = globalFilter.value ? certification.certificationName.toLowerCase().includes(globalFilter.value.toLowerCase()) : true;

        return matchesDept && matchesGlobalFilter;
    });
}

// 자격증 조회 Dialog 띄우기
function showCertificationDetails(event) {
    console.log(event);
    selectedCertification.value = event; // 클릭된 행의 데이터
    isDetailDialogVisible.value = true; // 모달 표시
}

// 자격증 추가 Dialog 띄우기
function showAddCertification() {
    isAddDialogVisible.value = true; // 모달 표시
}

const changeSelectedAddDeptId = (deptId) => {
    addCertificationData.value.deptId = deptId;
};

const changeSelectedEditDeptId = (deptId) => {
    editCertificationData.value.deptId = deptId;
};

// 자격증 수정 Dialog 띄우기
function showEditCertification(event) {
    editCertificationData.value = event; // 클릭된 행의 데이터
    console.log(editCertificationData.value);
    isEditDialogVisible.value = true; // 모달 표시
}

// 자격증 추가하기
async function saveCertification() {
    try {
        const response = await fetchPost('http://localhost:8080/api/v1/certification-service/certification', addCertificationData.value);

        alert(response.certificationName + '이 추가되었습니다.');
        isAddDialogVisible.value = false;
    } catch (error) {
        console.error('자격증 추가 중 오류:', error);
        alert('자격증 추가 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
}

// 자격증 수정하기
async function editCertification() {
    const editRequestData = {
        certificationName: editCertificationData.value.certificationName,
        benefit: editCertificationData.value.benefit,
        institution: editCertificationData.value.institution,
        deptId: editCertificationData.value.deptId
    };

    try {
        const response = await fetchPut(`http://localhost:8080/api/v1/certification-service/certification/${editCertificationData.value.certificationId}`, editRequestData);

        alert(response.certificationName + '이 수정되었습니다.');
        isEditDialogVisible.value = false;
    } catch (error) {
        console.error('자격증 추가 중 오류:', error);
        alert('자격증 추가 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
}

// 자격증 삭제하기
async function deleteCertification(event) {

    delelteCertificationData.value = event;
    const certificationId = delelteCertificationData.value.certificationId;

    try {
        await fetchDelete(`http://localhost:8080/api/v1/certification-service/certification/${certificationId}`);

        alert(delelteCertificationData.value.certificationName + '이 삭제되었습니다.');
        router.go(0);
    } catch (error) {
        console.error('자격증 추가 중 오류:', error);
        alert('자격증 추가 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
}

// 페이지 진입 시 데이터 로드
onMounted(() => {
    fetchCertifications();
    fetchDepartments();
});

// 날짜 포맷팅 함수
const formatDate = (date) => {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
    return date.toLocaleDateString('ko-KR', options);
};
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
