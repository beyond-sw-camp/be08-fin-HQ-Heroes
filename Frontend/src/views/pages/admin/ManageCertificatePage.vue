<template>
    <div class="certification-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">자격증 관리</label>
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="goToWriteCertification" />
            </div>

            <!-- 자격증 목록 테이블 -->
            <DataTable
                :value="filteredCertifications"
                paginator
                :rows="10"
                :globalFilterFields="['certificationName', 'deptName', 'institution']"
                dataKey="certificationId"
                @row-click="showCertificationDetails"
                :metaKeySelection="false"
                selectionMode="single"
                removableSort
            >
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

                <Column field="certificationId" sortable header="No." />
                <Column field="deptName" sortable header="부서 명" />
                <Column field="certificationName" sortable header="자격증 명" />
                <Column field="institution" sortable header="발급 기관" />
                <Column field="applicationPeriod" sortable header="접수 기간">
                    <template #body="{ data }">
                        {{ formatDate(new Date(data.applicationStartDate)) }} ~ {{ formatDate(new Date(data.applicationEndDate)) }}
                    </template>
                </Column>
                <Column field="examDate" sortable header="시험 일">
                    <template #body="{ data }">
                        {{ formatDate(new Date(data.examDate)) }}
                    </template>
                </Column>
            </DataTable>
        </div>
    </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const certifications = ref([]);
const filteredCertifications = ref([]);
const selectedDepartment = ref(null);
const departments = ref([]);
const globalFilter = ref('');
const router = useRouter();

async function fetchCertifications() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/certification-service/certification');
        certifications.value = response.data;
        filteredCertifications.value = certifications.value;
    } catch (error) {
        console.error("자격증 목록을 불러오지 못했습니다.", error);
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
    filteredCertifications.value = certifications.value.filter(certification => {
        const matchesDept = selectedDepartment.value && selectedDepartment.value.deptName !== '전체 부서'
            ? certification.deptName === selectedDepartment.value.deptName
            : true;
        const matchesGlobalFilter = globalFilter.value
            ? certification.certificationName.toLowerCase().includes(globalFilter.value.toLowerCase())
            : true;

        return matchesDept && matchesGlobalFilter;
    });
}

// 자격증 상세보기 함수
function showCertificationDetails(event) {
    const certificationId = event.data.certificationId;
    router.push(`/manage-certifications/certification-detail/${certificationId}`);
}

// '추가하기' 버튼 클릭 시 이동할 경로
const goToWriteCertification = () => {
    router.push('/write-certification');
};

// 페이지 진입 시 데이터 로드
onBeforeMount(() => {
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
