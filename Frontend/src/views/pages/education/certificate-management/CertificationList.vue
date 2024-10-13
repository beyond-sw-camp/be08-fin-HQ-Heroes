<template>
    <div class="card">
        <!-- 로그인된 사용자 이름을 출력 -->
        <div class="logged-in-user">
            <p>
                로그인한 사용자: <strong>{{ employeeData.employeeName }}</strong>
            </p>
        </div>
        <h2 class="font-semibold text-xl mb-4">자격증 목록</h2>
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

            <Column field="registrationId" sortable header="번호" style="min-width: 2rem; text-align: left;" />
            <Column field="certificationName" sortable header="자격명" style="min-width: 20rem; text-align: left;" />
            <Column field="institution" sortable header="발급 기관" style="min-width: 8rem; text-align: left;" />
            <Column field="acquisitionDate" sortable header="취득일" dataType="date" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(data.acquisitionDate) }}
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
                <label for="acquisitionDate" class="block font-bold mb-3">취득일</label>
                <Calendar id="acquisitionDate" v-model="newCertification.acquisitionDate" required="true" placeholder="YYYY-MM-DD" class="w-full small-calendar" />
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
            <div class="col-span-12 lg:col-span-6 xl:col-span-3" v-for="cert in recommendedCertifications" :key="cert.registrationId">
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
import { ref, onMounted } from 'vue';
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService';
import axios from 'axios';

const certifications = ref([]); 
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

const employeeData = ref({
    employeeName: '',
    employeeId: ''
});

// 추천 자격증 목록 가져오기
async function fetchRecommendedCertifications() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/certification-service/certification');
        recommendedCertifications.value = response.data;
    } catch (error) {
        console.error("추천 자격증 목록을 불러오지 못했습니다.", error);
    }
}

// 사원 자격증 목록 가져오기
async function fetchEmployeeCertifications() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee-certification/my-certification');
        certifications.value = response.data; 
        filteredCertifications.value = [...certifications.value];
    } catch (error) {
        console.error('사원 자격증 목록을 불러오지 못했습니다.', error);
    }
}

// 자격증 추가하기
async function saveCertification() {
    try {
        const requestBody = {
            certificationName: newCertification.value.certificationName,
            acquisitionDate: newCertification.value.acquisitionDate,
            institution: newCertification.value.institution,
        };

        // 백엔드로 POST 요청 보내기
        const response = await axios.post('http://localhost:8080/api/v1/employee-certification/my-certification', requestBody, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        await fetchEmployeeCertifications();
        addDialogVisible.value = false;
        newCertification.value = {};

        alert('자격증이 성공적으로 추가되었습니다.');
    } catch (error) {
        console.error('자격증 추가 중 오류가 발생했습니다:', error);
    }
}

// 자격증 세부 사항 보여주기
function showCertificationDetails(event) {
    selectedCertification.value = event.data;
    isEditing.value = true;
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
function formatDate(date) {
    return new Date(date).toLocaleDateString('ko-KR');
}

// 컴포넌트가 마운트된 후 실행될 코드
onMounted(async () => {
    const employeeId = window.localStorage.getItem('employeeId'); 
    const data = await getLoginEmployeeInfo(employeeId); 

    if (data) {
        employeeData.value = data;
    }

    await fetchEmployeeCertifications();
    await fetchRecommendedCertifications();
});
</script>


<style scoped>
.search-container {
    position: relative;
}
.search-icon {
    position: absolute;
    top: 50%;
    left: 0.5rem;
    transform: translateY(-50%);
    color: #999;
}
.search-input {
    padding-left: 2rem;
}
</style>
