<template>
    <div class="card">
        <div class="flex flex-row justify-between mb-4">
            <label class="text-xl font-bold">자격증 목록</label>
            <Button label="등록하기" icon="pi pi-plus" class="custom-button" @click="showAddOrUpdateDialog(null)" />
        </div>

        <div class="grid grid-cols-12 gap-4">
            <div class="col-span-12 lg:col-span-6 xl:col-span-3" v-for="cert in filteredCertifications" :key="cert.registrationId" @click="showEmployeeCertificationDetails(cert)">
                <div class="card mb-1 border border-gray-300 p-4 cursor-pointer">
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">{{ cert.institution }}</span>
                            <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ cert.certificationName }}</div>
                        </div>
                        <div class="flex items-center justify-center" :class="cert.bgClass" style="width: 2.5rem; height: 2.5rem">
                            <i :class="cert.iconClass"></i>
                        </div>
                    </div>
                    <span class="text-muted-color">취득일: {{ formatDate(cert.acquisitionDate) }}</span>
                </div>
            </div>

            <EmployeeCertificationDetail :visible="showDetailModal" :certificationDetail="selectedCertification" @update:visible="showDetailModal = $event" />
        </div>
    </div>

    <Dialog v-model:visible="addDialogVisible" modal="true" header="자격증 추가하기" :style="{ width: '30vw', borderRadius: '12px' }" :draggable="false" :closable="false">
        <div class="flex flex-col gap-6">
            <div>
                <label for="certificationName" class="block font-bold mb-3">자격증 명</label>
                <InputText id="certificationName" v-model="newCertification.certificationName" required class="w-full" />
            </div>
            <div>
                <label for="institution" class="block font-bold mb-3">기관명</label>
                <InputText id="institution" v-model="newCertification.institution" required class="w-full" />
            </div>
            <div>
                <label for="acquisitionDate" class="block font-bold mb-3">취득일</label>
                <Calendar id="acquisitionDate" v-model="newCertification.acquisitionDate" required placeholder="YYYY-MM-DD" class="w-full small-calendar" />
            </div>
        </div>

        <template #footer>
            <Button label="저장" icon="pi pi-check" class="p-button-primary" @click="isEditing ? updateCertification() : saveCertification()" />
            <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeDialog" />
        </template>
    </Dialog>

    <div class="card">
        <h2 class="font-semibold text-xl my-6">추천 자격증</h2>
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

        <CertificationDetail v-model:visible="isDialogVisible" :certification-detail="selectedCertification" />
    </div>
</template>

<script setup>
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService';
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService';
import CertificationDetail from './CertificationDetail.vue';
import EmployeeCertificationDetail from './EmployeeCertificationDetail.vue';

const certifications = ref([]);
const filteredCertifications = ref([...certifications.value]);
const addDialogVisible = ref(false);
const isEditing = ref(false);
const currentEmployeeId = ref(null);
const showDetailModal = ref(false);
const selectedCertification = ref(null);
const newCertification = ref({});
const isDialogVisible = ref(false); // 모달 가시성
const recommendedCertifications = ref([]);
const employeeData = ref({});

// 자격증 클릭 시 호출되는 함수
function showEmployeeCertificationDetails(cert) {
    selectedCertification.value = cert;
    showDetailModal.value = true;
}

// 추천 자격증 목록 가져오기
async function fetchRecommendedCertifications() {
    if (!employeeData.value.deptName) {
        console.error('부서명이 없습니다.');
        return;
    }

    try {
        const allCertifications = await fetchGet(`https://hq-heroes-api.com/api/v1/certification-service/by-department?deptName=${employeeData.value.deptName}`);
        const employeeCertificationsData = await fetchGet('https://hq-heroes-api.com/api/v1/employee-certification/my-certification/by-employeeId');

        const employeeCertificationNames = new Set(employeeCertificationsData.map((cert) => cert.certificationName));

        recommendedCertifications.value = allCertifications.filter((cert) => cert.deptName === employeeData.value.deptName && !employeeCertificationNames.has(cert.certificationName));
    } catch (error) {
        console.error('추천 자격증 목록을 불러오는 중 오류 발생:', error);
    }
}

// 사원 자격증 목록 가져오기
async function fetchEmployeeCertifications() {
    try {
        const data = await fetchGet('https://hq-heroes-api.com/api/v1/employee-certification/my-certification/by-employeeId');
        certifications.value = data.filter((cert) => cert.employeeCertificationStatus === 'APPROVE');
        filteredCertifications.value = [...certifications.value];
    } catch (error) {
        console.error('사원 자격증 목록을 불러오지 못했습니다.', error);
    }
}

// 자격증 추가하기
async function saveCertification() {
    try {
        const requestBody = {
            ...newCertification.value,
            employeeId: currentEmployeeId.value
        };
        const { registrationId } = await fetchPost('https://hq-heroes-api.com/api/v1/employee-certification/my-certification', requestBody);

        if (registrationId) {
            await fetchEmployeeCertifications();
            addDialogVisible.value = false;
            newCertification.value = {};

            // 성공 메시지 표시
            await Swal.fire({
                title: '자격증이 성공적으로 추가되었습니다.',
                icon: 'success'
            });
        } else {
            throw new Error('등록 실패');
        }
    } catch (error) {
        console.error('자격증 추가 중 오류:', error);

        // 오류 메시지 표시
        await Swal.fire({
            title: '자격증 추가 중 오류가 발생했습니다.',
            text: '다시 시도해주세요.',
            icon: 'error'
        });
    }
}

// 자격증 클릭 시 상세 정보를 모달로 전달하는 함수
const showCertificationDetails = (cert) => {
    selectedCertification.value = cert; // 선택된 자격증 정보 설정
    isDialogVisible.value = true; // 모달 표시
};

// 날짜 포맷 함수
function formatDate(date) {
    const formattedDate = new Date(date);
    return `${formattedDate.getFullYear()}-${String(formattedDate.getMonth() + 1).padStart(2, '0')}-${String(formattedDate.getDate()).padStart(2, '0')}`;
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

// 컴포넌트가 마운트된 후 실행될 코드
onMounted(async () => {
    const employeeId = window.localStorage.getItem('employeeId');
    const data = await getLoginEmployeeInfo(employeeId);

    if (data) {
        employeeData.value = data;
        currentEmployeeId.value = data.employeeId;
    }

    await fetchEmployeeCertifications();
    await fetchRecommendedCertifications();
});
</script>

<style scoped>
.small-calendar {
    max-width: 250px;
}
</style>
