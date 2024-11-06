<template>
    <div class="education-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">교육 관리</label>
                <Button label="추가하기" icon="pi pi-plus" class="custom-button" @click="goToWriteNotice" />
            </div>

            <!-- 필터 및 검색 섹션 -->
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center">
                    <Dropdown v-model="selectedcategoryName" :options="categories" optionLabel="categoryName" placeholder="카테고리를 선택하세요" @change="filterEducations" class="mr-2" />
                </div>
                <div class="relative search-container">
                    <InputText v-model="globalFilter" placeholder="검색어를 입력해주세요" @input="filterEducations" class="pl-8 search-input" />
                    <i class="pi pi-search search-icon" />
                </div>
            </div>

            <!-- 교육 목록 테이블 -->
            <DataTable
                :value="filteredEducations"
                paginator
                :rows="10"
                :globalFilterFields="['educationName', 'categoryName', 'institution']"
                dataKey="educationId"
                :metaKeySelection="false"
                selectionMode="single"
                removableSort
                @row-select="showEducationDetails"
            >
                <Column field="categoryName" sortable header="카테고리" />
                <Column field="educationName" sortable header="교육 명" />
                <Column field="institution" sortable header="교육 기관" />
                <Column field="educationStart" sortable header="교육 기간">
                    <template #body="{ data }"> {{ formatDate(data.educationStart) }} ~ {{ formatDate(data.educationEnd) }} </template>
                </Column>
            </DataTable>
        </div>
    </div>
</template>

<script setup>
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGet } from '../auth/service/AuthApiService';

// 교육 목록 데이터
const educations = ref([]);
const filteredEducations = ref([]);

// 필터링을 위한 데이터
const selectedcategoryName = ref(null);
const selectedDate = ref(null);
const globalFilter = ref('');

// 카테고리 목록
const categories = ref([]);

// Vue Router 설정
const router = useRouter();

// 필터링 함수
function filterEducations() {
    filteredEducations.value = educations.value.filter((education) => {
        // 카테고리 이름 매칭
        const matchesCategoryName = !selectedcategoryName.value || selectedcategoryName.value.categoryName === '전체' || education.categoryName === selectedcategoryName.value.categoryName;

        // 글로벌 필터 매칭
        const matchesGlobalFilter = globalFilter.value ? education.educationName.toLowerCase().includes(globalFilter.value.toLowerCase()) : true;

        return matchesCategoryName && matchesGlobalFilter;
    });
}

// 교육 상세보기 함수
function showEducationDetails(event) {
    router.push(`/manage-education/education-detail/${event.data.educationId}`);
}

// 교육 등록 페이지로 이동

const goToWriteNotice = () => {
    router.replace({ path: '/write-education', query: { fromPage: 'educationListPage' } });
};

// 날짜 포맷 함수
function formatDate(date) {
    const formattedDate = new Date(date);
    return `${formattedDate.getFullYear()}-${String(formattedDate.getMonth() + 1).padStart(2, '0')}-${String(formattedDate.getDate()).padStart(2, '0')}`;
}

// 카테고리 목록 가져오기 함수
async function fetchCategories() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/educationCategory-service/categories');
        categories.value = [{ categoryName: '전체' }, ...response];
    } catch (error) {
        console.error('카테고리 목록을 불러오지 못했습니다.', error);
    }
}

// 컴포넌트가 마운트될 때 교육 목록 및 카테고리 가져오기
onBeforeMount(() => {
    fetchEducations();
    fetchCategories();
});

async function fetchEducations() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/education-service/education');
        educations.value = response.reverse(); // 데이터 역순 정렬
        filteredEducations.value = educations.value;
    } catch (error) {
        console.error('교육 목록을 불러오지 못했습니다.', error);
    }
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
    color: #aaa;
}

.custom-button {
    margin-left: auto;
}

.time-section {
    display: flex;
    justify-content: space-between;
}

.time-block {
    flex: 1;
    margin-right: 10px;
}

.time-block:last-child {
    margin-right: 0;
}
</style>
