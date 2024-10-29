<template>
    <div class="card">
        <div class="flex flex-row justify-between mb-4">
            <label class="text-xl font-bold">자격증 목록</label>
            <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="showAddOrUpdateDialog(null)" />
        </div>
        <DataTable
            :value="filteredCertifications"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="registrationId"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['certificationName', 'institution', 'name']"
            showGridlines
            style="table-layout: fixed;"
            @row-click="showEmployeeCertificationDetails" 
            @rowSelect="onRowSelect"
            @rowUnselect="onRowUnselect"
        >
            <template #header>
                <div class="flex items-center justify-between">
                    <div class="relative search-container ml-auto">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="globalFilter" placeholder="Keyword Search" class="pl-8 search-input" />
                    </div>
                </div>
            </template>

            <Column field="certificationName" sortable header="자격명" style="min-width: 20rem; text-align: left;" />
            <Column field="institution" sortable header="발급 기관" style="min-width: 8rem; text-align: left;" />
            <Column field="acquisitionDate" sortable header="취득일" dataType="date" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(data.acquisitionDate) }}
                </template>
            </Column>
        </DataTable>

        <!-- EmployeeCertificationDetail 모달 추가 -->
        <EmployeeCertificationDetail
            :visible="showDetailModal"
            :certificationDetail="selectedCertification"
            @update:visible="showDetailModal = $event"
        />
    </div>

    <!-- 자격증 추가 모달 -->
    <Dialog v-model:visible="addDialogVisible" modal="true" header="자격증 추가하기" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="false">
        <div class="flex flex-col gap-6">
            <div>
                <label for="certificationName" class="block font-bold mb-3">자격증 명</label>
                <InputText id="certificationName" v-model="newCertification.certificationName" required="true" class="w-full" />
            </div>
            <div>
                <label for="institution" class="block font-bold mb-3">기관명</label>
                <InputText id="institution" v-model="newCertification.institution" required="true" class="w-full" />
            </div>
            <div>
                <label for="acquisitionDate" class="block font-bold mb-3">취득일</label>
                <Calendar id="acquisitionDate" v-model="newCertification.acquisitionDate" required="true" placeholder="YYYY-MM-DD" class="w-full small-calendar" />
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
            <div class="col-span-12 lg:col-span-6 xl:col-span-3" v-for="cert in recommendedCertifications" :key="cert.registrationId">
                <div class="card mb-1 border border-gray-300 p-4" @click="showCertificationDetails(cert)">
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">{{ cert.deptName }}</span>
                            <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ cert.certificationName }}</div>
                        </div>
                        <div class="flex items-center justify-center" :class="cert.bgClass" style="width: 2.5rem; height: 2.5rem">
                            <i :class="cert.iconClass"></i>
                        </div>
                    </div>
                    <span class="text-muted-color">{{ cert.institution }}</span>
                </div>
            </div>
        </div>

        <!-- CertificationDetail 모달 컴포넌트 -->
        <CertificationDetail
            v-model:visible="isDialogVisible"
            :certification-detail="selectedCertification"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService';
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService';
import CertificationDetail from './CertificationDetail.vue';  // 모달 컴포넌트 가져오기
import EmployeeCertificationDetail from './EmployeeCertificationDetail.vue'; // 모달 컴포넌트 import


const certifications = ref([]); 
const filteredCertifications = ref([...certifications.value]); 
const filters = ref(null);
const displayDialog = ref(false);
const addDialogVisible = ref(false);
const isEditing = ref(false);
const selectedDept = ref(null);
const selectedInstitution = ref(null);
const institutions = ref([]); 
const recommendedCertifications = ref([]); 
const newCertification = ref({});
const currentEmployeeId = ref(null); // 현재 직원 ID를 저장할 ref 추가
const isModalOpen = ref(false);
const isDialogVisible = ref(false); // 모달 가시성
const selectedCertification = ref(null); // 선택된 자격증 정보
const showDetailModal = ref(false); // 모달 상태

// 인증서 클릭 시 호출되는 함수
function showEmployeeCertificationDetails(event) {
    selectedCertification.value = event.data; // 클릭된 행의 데이터
    showDetailModal.value = true; // 모달 표시
}

const employeeData = ref({
    employeeName: '',
    employeeId: ''
});

// 추천 자격증 목록 가져오기
async function fetchRecommendedCertifications() {
    try {
        if (!employeeData.value.deptName) {
            console.error("부서명이 없습니다.");
            return;
        }

        // 부서에 따른 자격증 목록 가져오기
        const allCertifications = await fetchGet(`http://localhost:8080/api/v1/certification-service/by-department?deptName=${employeeData.value.deptName}`);

        // 사원 자격증 목록 가져오기
        const employeeCertificationsData = await fetchGet('http://localhost:8080/api/v1/employee-certification/my-certification/by-employeeId');
        
        // 사원 자격증 이름 목록 만들기
        const employeeCertificationNames = new Set(employeeCertificationsData.map(cert => cert.certificationName));

        if (allCertifications && Array.isArray(allCertifications)) {
            // 추천 자격증 목록 필터링 (사원 자격증 이름이 아닌 것만 포함)
            recommendedCertifications.value = allCertifications.filter(cert => 
                cert.deptName === employeeData.value.deptName && !employeeCertificationNames.has(cert.certificationName)
            );
        } else {
            console.error("자격증 목록을 불러오는 데 실패했습니다.", allCertifications);
        }
    } catch (error) {
        console.error("추천 자격증 목록을 불러오는 중 오류 발생:", error);
    }
}

// 사원 자격증 목록 가져오기
async function fetchEmployeeCertifications() {
    try {
        const data = await fetchGet('http://localhost:8080/api/v1/employee-certification/my-certification/by-employeeId');

        if (data && Array.isArray(data)) {
            // APPROVED 상태의 자격증만 필터링하고, registrationId 순서대로 정렬 (최신순)
            certifications.value = data
                .filter(cert => cert.employeeCertificationStatus === 'APPROVE') // APPROVED 상태만 선택
                .sort((a, b) => b.registrationId - a.registrationId); // 최신순 정렬

            filteredCertifications.value = [...certifications.value];
        } else {
            console.error('응답 데이터가 배열이 아닙니다.', data);
        }
    } catch (error) {
        console.error('사원 자격증 목록을 불러오지 못했습니다.', error);
    }
}

// 자격증 추가하기
async function saveCertification() {
    try {
        const requestBody = {
            ...newCertification.value, // newCertification에서 필요한 필드를 바로 가져오기
            employeeId: currentEmployeeId.value
        };

        console.log('Sending request body:', requestBody); 

        const { registrationId } = await fetchPost('http://localhost:8080/api/v1/employee-certification/my-certification', requestBody);

        if (registrationId) {
            await fetchEmployeeCertifications();
            addDialogVisible.value = false;
            newCertification.value = {};
            alert('자격증이 성공적으로 추가되었습니다.');
        } else {
            throw new Error('등록 실패');
        }
    } catch (error) {
        console.error('자격증 추가 중 오류:', error);
        alert('자격증 추가 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
}

function onRowSelect(event) {
    selectedCertification.value = event.data;
    console.log('선택된 자격증:', selectedCertification.value); // 로그 추가
    displayDialog.value = true;
}

function onRowUnselect(event) {
    console.log('선택 해제된 자격증:', event.data);
    selectedCertification.value = null;
    displayDialog.value = false;
}

// 자격증 클릭 시 상세 정보를 모달로 전달하는 함수
const showCertificationDetails = (cert) => {
    selectedCertification.value = cert; // 선택된 자격증 정보 설정
    isDialogVisible.value = true; // 모달 표시
};

// 날짜 포맷 함수
function formatDate(date) {
    const formattedDate = new Date(date);
    return `${formattedDate.getFullYear()}-${formattedDate.getMonth() + 1}-${formattedDate.getDate()}`;
}

// 자격증 추가하기 또는 수정하기 모달 보여주기
function showAddOrUpdateDialog(certification) {
    isEditing.value = false;
    newCertification.value = {}; 

    if (certification) {
        isEditing.value = true;
        newCertification.value = { ...certification };
    }

    addDialogVisible.value = true;
}

// 모달 닫기
function closeDialog() {
    addDialogVisible.value = false;
    newCertification.value = {};
}

// 필터링 기능 추가
function filterCertifications() {
    filteredCertifications.value = certifications.value.filter(certification => {
        return (
            (!selectedDept.value || certification.deptName === selectedDept.value.name) &&
            (!selectedInstitution.value || certification.institution === selectedInstitution.value.name)
        );
    });
}

// 날짜 형식 포맷팅 함수
// function formatDate(date) {
//     return new Date(date).toLocaleDateString('ko-KR');
// }

// 컴포넌트가 마운트된 후 실행될 코드
onMounted(async () => {
    const employeeId = window.localStorage.getItem('employeeId'); 
    const data = await getLoginEmployeeInfo(employeeId); 

    if (data) {
        employeeData.value = data;
        currentEmployeeId.value = data.employeeId; // currentEmployeeId에 직원 ID 할당
    }

    await fetchEmployeeCertifications();
    await fetchRecommendedCertifications();
});
</script>

<style scoped>
.search-container {
    display: flex;
    align-items: center;
    position: relative;
}

.search-icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
    color: #888;
}

.search-input {
    padding-left: 30px;
}

.custom-button {
    align-items: right;
}

</style>
