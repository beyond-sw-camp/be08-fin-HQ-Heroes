<template>
    <div class="notice-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">공지사항 목록</label>
                <Button v-if="isAdmin()" label="공지사항 추가" icon="pi pi-plus" class="custom-button" @click="showWriteNoticePage" />
            </div>
            <div class="flex flex-row justify-between mb-2">
                <div>
                    <Dropdown class="mr-2" v-model="selectedCategory" :options="categories" optionLabel="categoryName" placeholder="카테고리 선택" @change="filterNotices" />
                </div>
                <div>
                    <InputGroup>
                        <InputGroupAddon>
                            <i class="pi pi-search"></i>
                        </InputGroupAddon>
                        <InputText placeholder="제목 및 작성자 검색" v-model="globalFilter" @input="filterNotices" />
                    </InputGroup>
                </div>
            </div>

            <DataTable
                :value="filteredNotices"
                style="table-layout: fixed"
                :globalFilterFields="['title', 'categoryName', 'employeeName']"
                paginator
                :rows="10"
                removableSort
                dataKey="id"
                @row-click="(event) => showNoticeDetail(event.data.noticeId)"
                :metaKeySelection="false"
                selectionMode="single"
                :rowHover="true"
            >
                <Column field="createdAt" header="날짜" sortable>
                    <template #body="slotProps">
                        {{ formatDateTime(slotProps.data.createdAt) }}
                    </template>
                </Column>
                <Column field="categoryName" header="카테고리" :sortable="true" sortField="category" />
                <Column field="title" header="제목" sortable />
                <Column field="content" header="내용" :style="{ width: '40%' }" sortable>
                    <template #body="slotProps">
                        <!-- 내용이 100자 초과 시 잘라내고 '...' 추가 -->
                        <span v-if="slotProps.data.content.length > 100" v-html="slotProps.data.content.slice(0, 100)"></span>
                        <span v-else v-html="slotProps.data.content"></span>
                    </template>
                </Column>
                <Column field="employeeName" header="작성자" sortable />

                <Column v-if="isAdmin()">
                    <template #body="slotProps">
                        <Button icon="pi pi-pencil" class="p-button p-button-sm p-button-warning mr-2" @click="goToNoticeUpdate(slotProps.data.noticeId)" />
                        <Button icon="pi pi-trash" class="p-button p-button-sm p-button-danger" @click.stop="confirmDeleteNotice(slotProps.data)" />
                    </template>
                </Column>
            </DataTable>
        </div>

        <Dialog v-model:visible="displayDeleteConfirmDialog" modal="true" header="삭제 확인" :style="{ width: '400px' }" :draggable="false" :closable="true">
            <p>정말로 이 공지사항을 삭제하시겠습니까?</p>
            <template #footer>
                <Button label="삭제" icon="pi pi-check" class="p-button-danger" @click="handleDeleteNotice" />
                <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeDeleteConfirmDialog" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import axios from 'axios';
import { format } from 'date-fns';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import InputText from 'primevue/inputtext';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchCategories } from './service/adminNoticeCategoryService';
import { deleteNotice, fetchNotices } from './service/adminNoticeService';

const authStore = useAuthStore();
const notices = ref([]);
const filteredNotices = ref([]);
const categories = ref([]);
const selectedCategory = ref(null);
const globalFilter = ref('');
const displayAddDialog = ref(false);
const displayDeleteConfirmDialog = ref(false);
const newNotice = ref({ title: '', employeeName: '', categoryName: '', createAt: '', updaterName: '', updateAt: '', content: '' });
const updaterDate = ref(''); // 수정 시간 관리를 위한 ref
const isEditMode = ref(false);
const selectedNotice = ref(null); // 선택된 공지사항
const router = useRouter();

// 관리자인지 확인
const isAdmin = () => {
    // return authStore.employeeData.isAdmin === 'ROLE_ADMIN';
    return true;
};

// 공지사항 데이터 필터링
const filterNotices = () => {
    filteredNotices.value = notices.value.filter((notice) => {
        const matchesCategory = !selectedCategory.value || selectedCategory.value.code === '전체' || notice.categoryId === selectedCategory.value.id; // category_id 기준

        const matchesGlobalFilter = notice.title.includes(globalFilter.value) || notice.employeeName.includes(globalFilter.value);

        return matchesCategory && matchesGlobalFilter;
    });
};

// 공지사항 목록
onMounted(async () => {
    try {
        notices.value = await fetchNotices();
        categories.value = await fetchCategories(); // 카테고리 데이터 가져오기
        filterNotices();
    } catch (error) {
        console.error('Error fetching notices:', error);
    }
});

// 날짜 포맷팅
const formatDateTime = (dateString) => {
    if (!dateString) return ''; // 유효성 검사: dateString이 없을 경우 빈 문자열 반환

    const date = new Date(dateString);

    // 잘못된 날짜 처리
    if (isNaN(date.getTime())) {
        console.error('Invalid date string:', dateString);
        return ''; // 잘못된 날짜일 경우 빈 문자열 반환
    }

    return format(date, 'MM월 dd일');
};

const updaterInterval = ref(null); // Interval을 저장할 변수


// 공지사항 삭제 확인
const confirmDeleteNotice = (notice) => {
    selectedNotice.value = notice;
    displayDeleteConfirmDialog.value = true;
};

// 공지사항 삭제
const handleDeleteNotice = async () => {
    try {
        await deleteNotice(selectedNotice.value.noticeId);
        notices.value = notices.value.filter((notice) => notice.noticeId !== selectedNotice.value.noticeId);
        filterNotices(); // 삭제 후 필터링

        closeDeleteConfirmDialog(); // 다이얼로그 닫기
    } catch (error) {
        console.error('Error deleting notice:', error);
    }
};

// 공지사항 삭제 취소
const closeDeleteConfirmDialog = () => {
    displayDeleteConfirmDialog.value = false;
    selectedNotice.value = null;
};


// 페이지 이동
const showWriteNoticePage = () => {
    // this.categories가 정의되어 있는지 확인
    if (categories.value) {
        // 카테고리를 로컬 스토리지에 저장
        localStorage.setItem('noticeCategories', JSON.stringify(categories.value));

        // 쿼리 문자열 없이 '/write-notice'로 이동
        router.replace({ path: '/write-notice', query: { fromPage: 'notice' } });
    } else {
        console.error('categories는 정의되지 않았습니다.');
    }
};

// 공지사항 상세보기
const showNoticeDetail = (noticeId) => {
    router.push({ path: `/notice/${noticeId}` });
};

const goToNoticeUpdate = (noticeId) => {
    router.push({ name: 'notice-update', params: { id: noticeId } });
};

// 컴포넌트가 제거되기 전에 인터벌 정리
onBeforeUnmount(() => {
    if (updaterInterval.value) {
        clearInterval(updaterInterval.value);
    }
    isAdmin();
});
</script>

<style scoped>
.card {
    width: 100%;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>
