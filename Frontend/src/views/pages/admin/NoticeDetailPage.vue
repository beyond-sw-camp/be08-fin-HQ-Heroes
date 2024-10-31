<template>
    <div class="card">
        <div class="notice-detail">
            <h2>[{{ categories.find(category => category.categoryId === editableNotice.categoryId)?.categoryName || '카테고리 없음' }}] {{ editableNotice.title }} </h2>
            <h4><b>작성자 |</b> {{ editableNotice.employeeName }} </h4>

            <hr />
            <table class="notice-info">
                <!-- <tr>
                    <th class="input-title">제 목</th>
                    <td>{{ editableNotice.title }}</td>
                </tr> -->
                <!-- <tr>
                    <th class="input-title">작성자</th>
                    <td>{{ editableNotice.employeeName }}</td>
                </tr> -->
                <tr>
                    <th class="input-title">카테고리</th>
                    <td>
                        <template v-if="editableNotice.categoryId">
                            {{ categories.find(category => category.categoryId === editableNotice.categoryId).categoryName }}
                        </template>
                    </td>
                </tr>
                <tr>
                    <th style="text-align: left; vertical-align: top;">내 용</th>
                    <td>
                        <div v-html="editableNotice.content" class="message-content"></div>
                    </td>
                </tr>
            </table>

            <div class="button-group">
                <Button label="목록" icon="pi pi-fw pi-book" @click="goBackToList" class="gray-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchCategories } from './service/adminNoticeCategoryService';
import { fetchNoticeById } from './service/adminNoticeService';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const editableNotice = ref({});
const isNewNotice = ref(!route.params.id);
const categories = ref([]);
const editor = ref(null);
const quillInstance = ref(null);

// 관리자인지 확인
const isAdmin = () => {
    return true;
};

const goBackToList = () => {
    router.push('/manage-notices');
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

.input-group {
    margin-bottom: 1.5rem;
}

.input-title {
    margin-top: 1rem;
    margin-bottom: 0.5rem;
    font-weight: bold;
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
    /* padding: 20px 0; */
}

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 0.5rem;
    margin-top: 1rem;
}
</style>
