<template>
    <div class="notice-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">공지사항 목록</label>
                <Button v-if="isAdmin" label="추가하기" icon="pi pi-plus" class="custom-button" @click="showWriteNoticePage" />
            </div>
            <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                    <Dropdown class="mr-2" v-model="selectedCategory" :options="categories" optionLabel="categoryName" placeholder="카테고리 선택" @change="filterNotices" />
                </div>
                <div class="relative search-container">
                    <InputText placeholder="검색어를 입력해주세요" v-model="globalFilter" @input="filterNotices" class="search-input" />
                    <i class="pi pi-search search-icon" />
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
                <Column field="categoryName" header="카테고리" :sortable="true" sortField="categoryName" />
                <Column field="title" header="제목" sortable />
                <Column field="employeeName" header="작성자" sortable />

                <!-- 수정 및 삭제 -->
                <Column v-if="isAdmin" field="action" header="수정 / 삭제">
                    <template #body="slotProps">
                        <Button icon="pi pi-pencil" class="p-button p-button-sm p-button-warning mr-2" @click.stop="goToNoticeUpdate(slotProps.data.noticeId)" />
                        <Button icon="pi pi-trash" class="p-button p-button-sm p-button-danger" @click.stop="confirmDeleteNotice(slotProps.data)" />
                    </template>
                </Column>
            </DataTable>
        </div>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { format } from 'date-fns';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import Swal from 'sweetalert2';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGet } from '../auth/service/AuthApiService';
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
const isAdmin = ref(false);

// 관리자인지 확인
const roleCheck = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/role-check');
        console.log(response); // 응답 데이터 확인 // API 경로
        if (response.role === 'ROLE_ADMIN') {
            isAdmin.value = true;
        }
        console.log('isAdmin', isAdmin);
        console.log('.value', isAdmin.value);
    } catch (error) {
        console.error('Error fetching role and position:', error);
    }
    // return true;
};

// 공지사항 데이터 필터링
const filterNotices = () => {
    filteredNotices.value = notices.value.filter((notice) => {
        const matchesCategory =
            !selectedCategory.value ||
            selectedCategory.value.categoryName === '전체' || // 기본 '전체' 처리
            notice.categoryId === selectedCategory.value.categoryId;

        const matchesGlobalFilter = notice.title.includes(globalFilter.value) || notice.employeeName.includes(globalFilter.value) || notice.categoryName.includes(globalFilter.value);

        return matchesCategory && matchesGlobalFilter;
    });
};

// 페이지 마운트 시 데이터 초기화
onMounted(async () => {
    try {
        const fetchedNotices = await fetchNotices();
        const fetchedCategories = await fetchCategories(); // 카테고리 데이터 가져오기

        // 데이터가 유효한지 확인
        notices.value = Array.isArray(fetchedNotices) ? fetchedNotices : [];

        // '전체' 카테고리 추가 및 기본 선택
        categories.value = [{ id: null, categoryName: '전체' }, ...(Array.isArray(fetchedCategories) ? fetchedCategories : [])];

        selectedCategory.value = categories.value[0]; // 기본값을 '전체'로 설정
        filterNotices(); // 필터링
    } catch (error) {
        console.error('Error fetching notices or categories:', error);
        notices.value = []; // 예외 시 빈 배열로 초기화
        categories.value = [{ id: null, categoryName: '전체' }]; // 기본값 추가
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
    Swal.fire({
        title: '삭제 확인',
        text: '정말로 이 공지사항을 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then(async (result) => {
        if (result.isConfirmed) {
            await handleDeleteNotice();
        }
    });
};

// 공지사항 삭제
const handleDeleteNotice = async () => {
    try {
        await deleteNotice(selectedNotice.value.noticeId);
        notices.value = notices.value.filter((notice) => notice.noticeId !== selectedNotice.value.noticeId);
        filterNotices(); // 삭제 후 필터링
        Swal.fire('공지사항 삭제', '공지사항이 정상적으로 삭제되었습니다.', 'success'); // 삭제 완료 알림
    } catch (error) {
        console.error('Error deleting notice:', error);
        Swal.fire('공지사항 삭제 실패', '공지사항 삭제 중 오류가 발생했습니다.', 'error'); // 오류 알림
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

// 수정 관련 함수 주석 처리
const goToNoticeUpdate = (noticeId) => {
    router.push({ name: 'notice-update', params: { id: noticeId } });
};

onMounted(() => {
    roleCheck();
});

// 컴포넌트가 제거되기 전에 인터벌 정리
onBeforeUnmount(() => {
    if (updaterInterval.value) {
        clearInterval(updaterInterval.value);
    }
});
</script>

<style scoped>
.search-container {
    position: relative;
}

.search-input {
    padding-left: 40px; /* 아이콘과의 간격 조정 */
}

.search-icon {
    position: absolute;
    left: 12px; /* 아이콘 위치 조정 */
    top: 50%;
    transform: translateY(-50%);
    font-size: 1rem; /* 아이콘 크기 조정 (필요시 조정) */
    color: #aaa; /* 아이콘 색상 조정 (선택사항) */
}

.card {
    width: 100%;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.p-button:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.p-button:active {
    transform: scale(0.95);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}
</style>
