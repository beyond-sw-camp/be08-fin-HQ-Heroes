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
            <!-- 필터와 테이블 헤더 -->
            <template #header>
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                        <Dropdown v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="카테고리를 선택하세요" @change="filterByCategory" class="mr-2" />
                        <Dropdown v-model="selectedInstitution" :options="institutions" optionLabel="name" placeholder="기관을 선택하세요" @change="filterByInstitution" class="mr-2" />
                    </div>
                    <div class="relative search-container ml-auto">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="검색" class="pl-8 search-input" />
                    </div>
                </div>
            </template>

            <!-- 테이블 컬럼들 -->
            <Column field="certificationId" sortable header="번호" style="min-width: 2rem; text-align: left;" />
            <Column field="category" sortable header="카테고리" style="min-width: 6rem; text-align: left;">
                <template #body="{ data }">{{ data.category }}</template>
            </Column>
            <Column field="certificationName" sortable header="자격명" style="min-width: 20rem; text-align: left;" />
            <Column field="institution" sortable header="평가 기관" style="min-width: 8rem; text-align: left;" />
            <Column field="createdAt" sortable header="취득일" dataType="date" style="min-width: 8rem; text-align: left;">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.createdAt)) }}
                </template>
            </Column>
        </DataTable>
    </div>

    <!-- 자격증 상세 정보 모달 -->
    <Dialog v-model:visible="displayDialog" modal="true" header="자격증 상세" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="false">
        <div v-if="selectedCertification">
            <h3>자격증 정보</h3>
            <p><strong>자격명:</strong> {{ selectedCertification.certificationName }}</p>
            <p><strong>이름:</strong> {{ selectedCertification.name }}</p>
            <p><strong>평가 기관:</strong> {{ selectedCertification.institution }}</p>
            <p><strong>취득일:</strong> {{ formatDate(selectedCertification.createdAt) }}</p>
        </div>
        <template #footer>
            <Button label="닫기" icon="pi pi-times" class="p-button-text p-button-plain" @click="displayDialog = false" />
        </template>
    </Dialog>

        <!-- 자격증 추천 섹션 -->
        <div class="card">
        <h2 class="font-semibold text-xl my-6">회사가 추천하는 자격증</h2>
        <div class="grid grid-cols-12 gap-4">
            <div class="col-span-12 lg:col-span-6 xl:col-span-3" v-for="cert in recommendedCertifications" :key="cert.certificationId">
                <div class="card mb-1 border border-gray-300 p-4"> 
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">{{ cert.category }}</span>
                            <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ cert.name }}</div>
                        </div>
                        <div class="flex items-center justify-center" :class="cert.bgClass" style="width: 2.5rem; height: 2.5rem">
                            <i :class="cert.iconClass"></i>
                        </div>
                    </div>
                    <span class="text-muted-color">{{ currentDate }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import { ref, onBeforeMount } from 'vue';

const certifications = ref([
    { certificationId: 1, certificationName: '정보처리기사', category: 'IT', name: '홍길동', institution: '한국산업인력공단', createdAt: new Date('2022-04-15') },
    { certificationId: 2, certificationName: '컴퓨터활용능력 1급', category: '컴퓨터', name: '김영희', institution: '대한상공회의소', createdAt: new Date('2021-08-10') },
    { certificationId: 3, certificationName: '네트워크관리사', category: '네트워크', name: '이철수', institution: '한국정보통신협회', createdAt: new Date('2020-05-22') },
    { certificationId: 4, certificationName: '정보보안기사', category: '보안', name: '박민수', institution: '한국산업인력공단', createdAt: new Date('2023-02-19') },
    { certificationId: 5, certificationName: '데이터분석 전문가', category: '데이터', name: '최유리', institution: '한국데이터산업진흥원', createdAt: new Date('2019-12-03') }
]);

const filteredCertifications = ref([]);
const filters = ref(null);
const selectedCertification = ref(null);
const displayDialog = ref(false);
const selectedCategory = ref(null);
const selectedInstitution = ref(null);
const categories = ref([]);
const institutions = ref([]);

// 필터링
function filterByCategory() {
    filteredCertifications.value = certifications.value.filter(cert => {
        const matchesCategory = selectedCategory.value ? cert.category === selectedCategory.value.name : true;
        const matchesInstitution = selectedInstitution.value ? cert.institution === selectedInstitution.value.name : true;
        return matchesCategory && matchesInstitution;
    });
}

// 필터 초기화
function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        certificationName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        category: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        institution: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] }
    };
}

// 추천 자격증 목록 더미 데이터
const recommendedCertifications = ref([
    {
        certificationId: 1,
        name: '정보보안기사',
        category: '보안',
        iconClass: 'pi pi-lock text-green-500 !text-xl',
        bgClass: 'bg-green-100 dark:bg-green-400/10 rounded-border'
    },
    {
        certificationId: 2,
        name: '데이터 분석 전문가',
        category: '데이터',
        iconClass: 'pi pi-chart-line text-orange-500 !text-xl',
        bgClass: 'bg-orange-100 dark:bg-orange-400/10 rounded-border'
    },
    {
        certificationId: 3,
        name: '클라우드 컴퓨팅 자격증',
        category: '클라우드',
        iconClass: 'pi pi-cloud text-blue-500 !text-xl',
        bgClass: 'bg-blue-100 dark:bg-blue-400/10 rounded-border'
    },
    {
        certificationId: 4,
        name: 'AI 엔지니어 전문가',
        category: 'AI',
        iconClass: 'pi pi-cog text-purple-500 !text-xl',
        bgClass: 'bg-purple-100 dark:bg-purple-400/10 rounded-border'
    }
]);

// 자격증 세부 사항 보여주기
function showCertificationDetails(event) {
    selectedCertification.value = event.data;
    displayDialog.value = true;
}

// 카테고리 및 기관 목록 가져오기
async function fetchCategories() {
    categories.value = [{ name: '전체' }, { name: 'IT' }, { name: '네트워크' }, { name: '데이터' }];
}

async function fetchInstitutions() {
    institutions.value = [{ name: '전체' }, { name: '한국산업인력공단' }, { name: '대한상공회의소' }, { name: '한국정보통신협회' }];
}

// 날짜 포맷팅
function formatDate(date) {
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

// 초기화
onBeforeMount(() => {
    initFilters();
    fetchCategories();
    fetchInstitutions();
    filterByCategory();
});

</script>

<style scoped>
.search-container {
    position: relative;
}
.search-icon {
    position: absolute;
    left: 0.5rem;
    top: 50%;
    transform: translateY(-50%);
}
.search-input {
    padding-left: 2rem !important;
}
</style>
