<template>
    <div class="card">
        <div class="flex justify-end mb-4">
            <Button label="목록" icon="pi pi-bars" @click="goBackToList" outlined style="margin-right: 5rem" />
        </div>
        <div class="notice-detail">
            <div class="text-lg flex justify-between">
                <div class="font-bold text-3xl">
                    <span>
                        [{{ categories.find((category) => category.categoryId === editableNotice.categoryId)?.categoryName || '카테고리 없음' }}]
                        {{ editableNotice.title }}
                    </span>
                </div>
                <div>
                    <span class="font-bold">작성자</span>
                    <span class="cursor-auto ml-2 mr-2">|</span>
                    <span>{{ editableNotice.employeeName }}</span>
                </div>
            </div>

            <hr />
            <table class="notice-info text-lg">
                <tr>
                    <th style="text-align: left; width: 150px">카테고리</th>
                    <!-- 원하는 너비로 조정 -->
                    <td>
                        {{ categories.find((category) => category.categoryId === editableNotice.categoryId)?.categoryName || '카테고리 없음' }}
                    </td>
                </tr>
                <tr>
                    <th style="text-align: left; vertical-align: top; width: 150px">내 용</th>
                    <!-- 원하는 너비로 조정 -->
                    <td>
                        <div v-html="editableNotice.content" class="message-content"></div>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchCategories } from './service/adminNoticeCategoryService';
import { deleteNotice, fetchNoticeById } from './service/adminNoticeService';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const editableNotice = ref({});
const isNewNotice = ref(!route.params.id);
const categories = ref([]);
const editor = ref(null);
const quillInstance = ref(null);
const selectedNotice = ref(null); // selectedNotice 변수를 정의하여 초기화

const goBackToList = () => {
    router.push('/manage-notices');
};

const goToNoticeUpdate = (noticeId) => {
    router.push({ name: 'notice-update', params: { id: noticeId } });
};

// 공지사항 삭제 확인
const confirmDeleteNotice = (notice) => {
    selectedNotice.value = notice; // selectedNotice에 선택한 공지사항 저장
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
        Swal.fire('공지사항 삭제', '공지사항이 정상적으로 삭제되었습니다.', 'success'); // 삭제 완료 알림
        router.push('/manage-notices'); // 삭제 후 /manage-notices로 이동
    } catch (error) {
        console.error('Error deleting notice:', error);
        Swal.fire('공지사항 삭제 실패', '공지사항 삭제 중 오류가 발생했습니다.', 'error'); // 오류 알림
    }
    selectedNotice.value = null; // 삭제 후 selectedNotice 초기화
};

// 공지사항 데이터 불러오기 및 Quill 에디터에 표시
const loadNotice = async () => {
    const noticeId = route.params.id;
    try {
        const result = await fetchNoticeById(noticeId);
        editableNotice.value = { ...result };
    } catch (error) {
        console.error('공지사항 조회 오류:', error);
    }
};

const loadCategories = async () => {
    try {
        const result = await fetchCategories();
        categories.value = result;
    } catch (error) {
        console.error('카테고리 조회 오류:', error);
    }
};

// 컴포넌트가 마운트될 때 실행
onMounted(async () => {
    await loadCategories();
    if (!isNewNotice.value) await loadNotice();
});
</script>

<style scoped>
.notice-detail {
    width: 100%; /* "card" 크기에 맞추기 위해 전체 너비로 설정 */
    margin: 0; /* 불필요한 외부 여백 제거 */
    padding: 0 5rem; /* 내부 여백만 설정하여 card 경계에 맞추기 */
}

h2 {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 10px;
}

hr {
    margin: 20px 0;
}

.notice-info {
    width: 100%;
    border-collapse: collapse;
}

.notice-info th,
.notice-info td {
    padding: 8px;
    border-bottom: 1px solid #ddd;
    text-align: left;
}

.message-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 1rem;
}

.message-editor {
    height: 200px;
    border: 1px solid #ccc;
    border-radius: 4px;
    padding: 0.75rem;
    overflow-y: auto;
}

.message-editor.edit-mode {
    height: 600px; /* 수정 모드일 때 높이를 600px로 설정 */
}

.message-content {
    text-align: left;
    display: inline-block;
    max-width: 100%;
}

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    padding: 10px 0;
}
</style>
