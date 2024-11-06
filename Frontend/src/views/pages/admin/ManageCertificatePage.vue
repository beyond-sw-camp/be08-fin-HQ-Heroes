<template>
    <div class="certification-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">자격증 관리</label>
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="showAddCertification" />
            </div>

            <!-- 필터 및 검색 섹션 -->
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <!-- 부서 선택 드롭다운 -->
                    <Dropdown v-model="selectedDepartment" :options="departments" optionLabel="deptName" placeholder="부서를 선택하세요" @change="filterCertifications" class="mr-2" />
                </div>
                <div class="relative search-container">
                    <InputText v-model="globalFilter" placeholder="검색어를 입력해주세요" @input="filterCertifications" class="pl-8 search-input" />
                    <i class="pi pi-search search-icon"></i>
                </div>
            </div>

            <!-- 자격증 목록 테이블 -->
            <DataTable
                :value="filteredCertifications"
                v-model:selection="selectedCertification"
                paginator
                dataKey="certificationId"
                :rows="10"
                :globalFilterFields="['certificationName', 'deptName', 'institution']"
                removableSort
                selectionMode="single"
                @row-click="showCertificationDetails"
            >
                <Column field="deptName" sortable header="부서 명" />
                <Column field="certificationName" sortable header="자격증 명" />
                <Column field="institution" sortable header="발급 기관" />
                <Column field="benefit" sortable header="혜택" />
                <Column class="w-16 !text-end">
                    <template #body="{ data }">
                        <div style="display: flex; gap: 0.5rem">
                            <Button icon="pi pi-pencil" @click.stop="showEditCertification(data)" severity="primary" rounded></Button>
                            <Button icon="pi pi-trash" @click.stop="confirmDeleteCertification(data)" severity="danger" rounded></Button>
                        </div>
                    </template>
                </Column>
            </DataTable>

            <!-- 자격증 조회 모달 -->
            <Dialog v-model:visible="isDetailDialogVisible" modal header="자격증 정보" :style="{ width: '30vw', borderRadius: '12px' }" :draggable="false" :closable="false">
                <template #header>
                    <div class="font-bold p-2" :style="{ fontSize: '1.5rem' }">{{ selectedCertification ? selectedCertification.certificationName : '정보 없음' }}</div>
                </template>
                <hr />
                <div class="p-4">
                    <table class="education-info">
                        <tr>
                            <td class="px-4 py-2"><strong>부서</strong></td>
                            <td class="px-4 py-2">{{ selectedCertification ? selectedCertification.deptName : 'N/A' }}</td>
                        </tr>
                        <tr>
                            <td class="px-4 py-2"><strong>자격증 명</strong></td>
                            <td class="px-4 py-2">{{ selectedCertification ? selectedCertification.certificationName : 'N/A' }}</td>
                        </tr>
                        <tr>
                            <td class="px-4 py-2"><strong>발급 기관</strong></td>
                            <td class="px-4 py-2">{{ selectedCertification ? selectedCertification.institution : 'N/A' }}</td>
                        </tr>
                        <tr>
                            <td class="px-4 py-2"><strong>혜택</strong></td>
                            <td class="px-4 py-2">{{ selectedCertification ? selectedCertification.benefit : 'N/A' }}</td>
                        </tr>
                    </table>
                </div>
                <template #footer>
                    <Button label="닫기" @click="closeDialog" />
                </template>
            </Dialog>
            <!-- 자격증 추가 모달 -->
            <Dialog v-model:visible="isAddDialogVisible" modal header="자격증 추가하기" :style="{ width: '30vw', borderRadius: '12px' }" :draggable="false" :closable="false">
                <div class="flex flex-col gap-6">
                    <div>
                        <Select v-model="selectedAddDepartment" :options="departments" optionLabel="deptName" placeholder="부서를 선택하세요" @change="changeSelectedAddDeptId(selectedAddDepartment.deptId)" />
                    </div>
                    <div>
                        <label for="certificationName" class="block font-bold mb-3">자격증 명</label>
                        <InputText id="certificationName" v-model="addCertificationData.certificationName" required class="w-full" placeholder="자격증 명을 입력해주세요." />
                    </div>
                    <div>
                        <label for="institution" class="block font-bold mb-3">기관 명</label>
                        <InputText id="institution" v-model="addCertificationData.institution" required class="w-full" placeholder="발급 기관을 입력해주세요" />
                    </div>
                    <div>
                        <label for="benefit" class="block font-bold mb-3">혜택</label>
                        <InputText id="benefit" v-model="addCertificationData.benefit" required class="w-full" placeholder="혜택을 입력해주세요." />
                    </div>
                </div>
                <template #footer>
                    <Button label="저장" class="p-button-primary" @click="saveCertification" />
                    <Button label="취소" text class="p-button-text" @click="isAddDialogVisible = false" />
                </template>
            </Dialog>

            <!-- 자격증 수정 모달 -->
            <Dialog v-model:visible="isEditDialogVisible" modal header="자격증 수정하기" :style="{ width: '30vw', borderRadius: '12px' }" :draggable="false" :closable="false">
                <div class="flex flex-col gap-6">
                    <div>
                        <Select v-model="selectedEditDepartment" :options="departments" optionLabel="deptName" placeholder="부서를 선택하세요" @change="changeSelectedEditDeptId(selectedEditDepartment.deptId)" />
                    </div>
                    <div>
                        <label for="certificationName" class="block font-bold mb-3">자격증 명</label>
                        <InputText id="certificationName" v-model="editCertificationData.certificationName" required class="w-full" placeholder="자격증 명을 입력해주세요." />
                    </div>
                    <div>
                        <label for="institution" class="block font-bold mb-3">기관 명</label>
                        <InputText id="institution" v-model="editCertificationData.institution" required class="w-full" placeholder="발급 기관을 입력해주세요" />
                    </div>
                    <div>
                        <label for="benefit" class="block font-bold mb-3">혜택</label>
                        <InputText id="benefit" v-model="editCertificationData.benefit" required class="w-full" placeholder="혜택을 입력해주세요." />
                    </div>
                </div>
                <template #footer>
                    <Button label="저장" class="p-button-primary" @click="editCertification" />
                    <Button label="취소" text class="p-button-text" @click="isEditDialogVisible = false" />
                </template>
            </Dialog>
        </div>
    </div>
</template>

<script setup>
import Dialog from 'primevue/dialog';
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchDelete, fetchGet, fetchPost, fetchPut } from '../auth/service/AuthApiService';

const certifications = ref([]);
const filteredCertifications = ref([]);
const selectedDepartment = ref(null);
const selectedAddDepartment = ref(null);
const selectedEditDepartment = ref(null);
const departments = ref([]);
const globalFilter = ref('');
const router = useRouter();
const isDetailDialogVisible = ref(false);
const selectedCertification = ref({
    certificationName: '',
    benefit: '',
    institution: '',
    deptId: 0,
    deptName: ''
});

const isAddDialogVisible = ref(false);
const addCertificationData = ref({
    certificationName: '',
    benefit: '',
    institution: '',
    deptId: 0,
    deptName: ''
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
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/certification-service/certification');
        certifications.value = response.reverse(); // 역순으로 정렬
        console.log(response);
        filteredCertifications.value = certifications.value;
    } catch (error) {
        console.error('자격증 목록을 불러오지 못했습니다.', error);
    }
}

async function fetchDepartments() {
    try {
        const departmentsData = await fetchGet('https://hq-heroes-api.com/api/v1/employee/departments');
        departments.value = [{ deptId: null, deptName: '전체 부서' }, ...departmentsData]; // departmentsData는 이미 JSON 형태라고 가정
    } catch (error) {
        console.error('부서 데이터를 가져오는 중 오류 발생:', error);
        departments.value = [{ deptId: null, deptName: '전체 부서' }];
    }
}

// 선택된 부서와 전역 검색어로 필터링
function filterCertifications() {
    filteredCertifications.value = certifications.value.filter((certification) => {
        // 부서 매칭
        const matchesDept = !selectedDepartment.value || selectedDepartment.value.deptName === '전체 부서' || certification.deptName === selectedDepartment.value.deptName;

        // 글로벌 필터 매칭
        const matchesGlobalFilter = globalFilter.value ? certification.certificationName.toLowerCase().includes(globalFilter.value.toLowerCase()) : true;

        return matchesDept && matchesGlobalFilter;
    });
}

// 자격증 조회 Dialog 띄우기
const showCertificationDetails = (event) => {
    const certification = event.data; // 클릭한 데이터가 있는지 확인
    console.log(certification); // 콘솔에 출력하여 데이터 확인
    if (certification) {
        selectedCertification.value = certification; // 선택한 자격증 정보를 설정
        isDetailDialogVisible.value = true; // 모달을 열기
    } else {
        closeDialog(); // 데이터가 없을 경우 모달 닫기 및 초기화
    }
};

// 모달을 닫을 때 선택된 항목 초기화
const closeDialog = () => {
    isDetailDialogVisible.value = false; // 모달 닫기
    selectedCertification.value = null; // 선택된 자격증 정보 초기화
};

// 자격증 추가 Dialog 띄우기
function showAddCertification() {
    isAddDialogVisible.value = true; // 모달 표시
}

// 자격증 추가 Dialog에 부서 띄우기
const changeSelectedAddDeptId = (deptId) => {
    addCertificationData.value.deptId = deptId;
};

// 자격증 수정 Dialog 띄우기
function showEditCertification(event) {
    editCertificationData.value = event; // 클릭된 행의 데이터
    selectedDepartment.value = { deptId: event.deptId, deptName: event.deptName }; // 선택한 부서 설정
    console.log(editCertificationData.value);
    isEditDialogVisible.value = true; // 모달 표시
}

//  자격증 수정 Dialog에 부서 띄우기
const changeSelectedEditDeptId = (deptId) => {
    editCertificationData.value.deptId = deptId;
};

// 자격증 추가하기
async function saveCertification() {
    try {
        const response = await fetchPost('https://hq-heroes-api.com/api/v1/certification-service/certification', addCertificationData.value);

        if (response) {
            // response가 null이 아닌 경우에만 성공 처리
            console.log(response);
            isAddDialogVisible.value = false;

            await Swal.fire('추가 완료', '자격증 추가가 완료되었습니다.', 'success');
            location.reload(); // 페이지 새로 고침
        } else {
            // 오류 알림
            Swal.fire('추가 실패', '자격증 추가 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');
        }
    } catch (error) {
        console.error('자격증 추가 중 오류:', error);
        Swal.fire('추가 실패', '자격증 추가 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');
    }
}

// 자격증 수정하기
async function editCertification() {
    const editRequestData = {
        certificationName: editCertificationData.value.certificationName,
        benefit: editCertificationData.value.benefit,
        institution: editCertificationData.value.institution,
        deptId: editCertificationData.value.deptId,
        deptName: editCertificationData.value.deptName
    };

    try {
        const response = await fetchPut(`https://hq-heroes-api.com/api/v1/certification-service/certification/${editCertificationData.value.certificationId}`, editRequestData);

        // 모달 닫기
        isEditDialogVisible.value = false;

        // 자격증 수정 완료 알림
        await Swal.fire('수정 완료', '자격증 수정이 완료되었습니다.', 'success');

        // 페이지 새로 고침
        location.reload(); // 즉시 페이지를 새로 고침합니다.
    } catch (error) {
        console.error('자격증 수정 중 오류:', error);
        // 오류 알림
        Swal.fire('수정 실패', '자격증 수정 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');
    }
}

// 교육 취소 확인 함수
async function confirmDeleteCertification(certification) {
    const result = await Swal.fire({
        title: '삭제 확인',
        text: '자격증을 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    });

    if (result.isConfirmed) {
        deleteCertification(certification); // 자격증 삭제
    } else {
        Swal.fire('취소됨', '자격증 삭제가 취소되었습니다.', 'info'); // 취소 알림
    }
}

// 자격증 삭제하기
async function deleteCertification(certification) {
    const certificationId = certification.certificationId; // 인자로 받은 certification 객체에서 certificationId 추출

    try {
        await fetchDelete(`https://hq-heroes-api.com/api/v1/certification-service/certification/${certificationId}`);

        // 삭제 완료 알림
        await Swal.fire({
            title: '자격증 삭제가 완료되었습니다.',
            icon: 'success'
        });

        // 페이지 새로 고침
        location.reload();
    } catch (error) {
        console.error('자격증 삭제 중 오류:', error);
        // 오류 알림
        Swal.fire('삭제 실패', '자격증 삭제 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');
    }
}

// 페이지 진입 시 데이터 로드
onMounted(() => {
    fetchCertifications();
    fetchDepartments();
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
    color: #aaa;
}

.education-info {
    width: 100%;
    border-collapse: collapse;
}

.education-info th,
.education-info td {
    padding: 8px;
    border-bottom: 1px solid #ddd;
    text-align: left;
}
</style>
