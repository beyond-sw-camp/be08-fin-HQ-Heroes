<template>
    <div class="notice-detail-page">
        <div class="card">
            <label class="text-xl font-bold" style="margin-bottom: 4rem">공지사항 내용</label>

            <div class="input-group">
                <h3 class="input-title">제 목</h3>
                <input type="text" v-model="editableNotice.title" class="message-input" :readonly="!isEditMode" placeholder="제목을 입력하세요" />
            </div>

            <div class="input-group">
                <h3 class="input-title">작성자</h3>
                <input type="text" v-model="editableNotice.employeeName" class="message-input" :readonly="!isEditMode" />
            </div>

            <div class="input-group">
                <h3 class="input-title">카테고리</h3>
                <select v-model="editableNotice.categoryId" class="message-input" :disabled="!isEditMode">
                    <option v-for="category in categories" :key="category.categoryId" :value="category.categoryId">
                        {{ category.categoryName }}
                    </option>
                </select>
            </div>

            <div class="input-group">
                <h3 class="input-title">내 용</h3>
                <div v-if="isEditMode" ref="editor" class="message-editor edit-mode"></div>
                <div v-else v-html="editableNotice.content" class="message-content"></div>
            </div>

            <div class="button-group">
                <Button label="목록" icon="pi pi-fw pi-book" @click="goBackToList" class="gray-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import Quill from 'quill';
import { nextTick, onMounted, ref } from 'vue';
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

// Quill 에디터 초기화
const initializeEditor = async () => {
    await nextTick();

    if (!quillInstance.value) {
        quillInstance.value = new Quill(editor.value, {
            theme: 'snow',
            modules: {
                toolbar: isEditMode.value ? [[{ header: [1, 2, false] }], ['bold', 'italic', 'underline'], ['link', 'image'], [{ list: 'ordered' }, { list: 'bullet' }]] : false
            }
        });

        // isEditMode 상태에 따른 에디터 활성화/비활성화
        quillInstance.value.enable(isEditMode.value);

        // 에디터 내용을 불러오기
        if (editableNotice.value.content) {
            quillInstance.value.root.innerHTML = editableNotice.value.content;
        }
    }
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
    initializeEditor();
    isAdmin();
});
</script>

<style scoped>
.notice-detail-page {
    display: flex;
    justify-content: center;
}

.card {
    width: 100%;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
    min-height: 300px;
    padding: 1rem;
    border: 1px solid #ccc;
    border-radius: 4px;
    word-wrap: break-word;
}

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 0.5rem;
    margin-top: 1rem;
}
</style>
