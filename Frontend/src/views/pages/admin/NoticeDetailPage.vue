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
                <!-- 수정됨 -->
                <div v-else v-html="editableNotice.content" class="message-content"></div>
            </div>

            <div class="button-group">
                <Button v-if="!isEditMode && isAdmin()" label="수정" icon="pi pi-pencil" class="p-button-primary" @click="toggleEditMode" />
                <Button v-if="isEditMode" label="저장" icon="pi pi-save" class="p-button-primary" @click="saveNotice" />
                <Button v-if="isEditMode" label="취소" icon="pi pi-times" class="p-button-secondary" @click="cancelEdit" />
                <Button v-if="isAdmin()" label="삭제" icon="pi pi-trash" class="p-button-danger" @click="showDeleteDialog" />
            </div>
        </div>

        <Dialog header="삭제 확인" v-model:visible="deleteDialogVisible" :modal="true" :style="{ width: '30vw' }" @hide="deleteDialogVisible = false">
            <p>정말로 이 공지사항을 삭제하시겠습니까?</p>
            <template #footer>
                <Button label="취소" icon="pi pi-times" @click="deleteDialogVisible = false" />
                <Button label="삭제" icon="pi pi-trash" @click="handleDeleteNotice" class="p-button-danger" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { format } from 'date-fns';
import Quill from 'quill';
import { nextTick, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchCategories } from './service/adminNoticeCategoryService';
import { deleteNotice, fetchNoticeById, updateNotice } from './service/adminNoticeService';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const editableNotice = ref({});
const isEditMode = ref(false);
const isNewNotice = ref(!route.params.id);
const categories = ref([]);
const editor = ref(null);
const quillInstance = ref(null);
const deleteDialogVisible = ref(false); // 삭제 확인 다이얼로그 상태
const originalNotice = ref({}); // 초기 상태를 저장할 변수 추가

// 관리자인지 확인
const isAdmin = () => {
    // return authStore.employeeData.isAdmin === 'ROLE_ADMIN';
    return true;
};

const formatDateTime = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return format(date, 'yyyy년 MM월 dd일 HH시 mm분 ss초');
};

// Quill 에디터 초기화
const initializeEditor = async () => {
    await nextTick();

    if (!editor.value) {
        console.error('Quill container를 찾을 수 없습니다.');
        return;
    }

    if (!quillInstance.value) {
        quillInstance.value = new Quill(editor.value, {
            theme: 'snow',
            modules: {
                toolbar: isEditMode.value ? [[{ header: [1, 2, false] }], ['bold', 'italic', 'underline'], ['link', 'image'], [{ list: 'ordered' }, { list: 'bullet' }]] : false
            }
        });

        // 툴바의 이미지 핸들러 설정
        quillInstance.value.getModule('toolbar').addHandler('image', imageHandler);

        // 처음 에디터를 초기화할 때 editableNotice.value.content를 설정
        quillInstance.value.root.innerHTML = editableNotice.value.content || '';
    }

    quillInstance.value.enable(isEditMode.value);
};

const imageHandler = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
        console.log('Input change event triggered');
        const file = input.files[0];
        if (!file) {
            console.error('No file selected.');
            return;
        }
        console.log('Selected file:', file);

        const resizedImage = await resizeImage(file, 700, 700);
        console.log('Resized image:', resizedImage);

        const formData = new FormData();
        formData.append('file', resizedImage);

        try {
            const response = await fetch('http://localhost:8080/api/v1/upload-image', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Error response:', errorText);
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            const imageUrl = data.imageUrl;
            console.log('Image URL from server:', imageUrl);

            if (quillInstance.value) {
                quillInstance.value.focus(); // 에디터에 포커스 맞추기
                console.log('Quill editor is initialized:', quillInstance.value);
                const range = quillInstance.value.getSelection();
                console.log('Current selection range:', range);

                if (range && range.index !== null) {
                    quillInstance.value.insertEmbed(range.index, 'image', imageUrl);
                    console.log('Image inserted at index:', range.index);
                    quillInstance.value.setSelection(range.index + 1, 0);
                } else {
                    const length = quillInstance.value.getLength();
                    quillInstance.value.insertEmbed(length - 1, 'image', imageUrl);
                    console.log('Image inserted at end of editor');
                    quillInstance.value.setSelection(length, 0);
                }
            } else {
                console.error('Quill instance is not initialized or getSelection is not available.');
            }
        } catch (error) {
            console.error('Image upload failed:', error);
        }
    };
};

// 이미지 크기 조정 함수
const resizeImage = (file, maxWidth, maxHeight) => {
    return new Promise((resolve) => {
        const img = new Image();
        const reader = new FileReader();

        reader.onload = (event) => {
            img.src = event.target.result;
        };

        img.onload = () => {
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');

            let width = img.width;
            let height = img.height;

            // 크기 비율 유지하며 조정
            if (width > height) {
                if (width > maxWidth) {
                    height *= maxWidth / width;
                    width = maxWidth;
                }
            } else {
                if (height > maxHeight) {
                    width *= maxHeight / height;
                    height = maxHeight;
                }
            }

            canvas.width = width;
            canvas.height = height;
            ctx.drawImage(img, 0, 0, width, height);
            canvas.toBlob((blob) => {
                resolve(new File([blob], file.name, { type: file.type })); // 새로운 파일 반환
            }, file.type);
        };

        reader.readAsDataURL(file);
    });
};

// 수정 모드 토글 함수
const toggleEditMode = () => {
    isEditMode.value = !isEditMode.value;
    router.replace({ query: { edit: isEditMode.value ? 'true' : 'false' } }); // URL에 edit 파라미터 추가

    nextTick(() => {
        initializeEditor();
        const editorElement = editor.value;
        if (isEditMode.value) {
            editorElement.classList.remove('quill-editor-disabled');
        } else {
            editorElement.classList.add('quill-editor-disabled');
        }
    });
};

// 수정 취소
const cancelEdit = async () => {
    isEditMode.value = false;

    // 공지사항 내용을 원래 상태로 복원
    editableNotice.value = { ...originalNotice.value };

    // URL의 edit 파라미터를 업데이트하지 않고 특정 경로로 이동
    const noticeId = editableNotice.value.noticeId;
    router.replace({ path: `/manage-notices` });

    // Quill 에디터의 내용을 원래 상태로 복원 및 비활성화
    if (quillInstance.value) {
        quillInstance.value.root.innerHTML = originalNotice.value.content || '';
        quillInstance.value.enable(false); // Quill 비활성화
    }
};

// 공지사항 데이터 불러오기 및 Quill 에디터에 표시
const loadNotice = async () => {
    const noticeId = route.params.id;
    console.log('Fetching notice with ID:', noticeId); // 확인용 로그
    try {
        const result = await fetchNoticeById(noticeId);
        editableNotice.value = { ...result };
        originalNotice.value = { ...result }; // 원본 데이터를 저장해둠
        console.log('Loaded Notice:', editableNotice.value); // 확인용 로그
    } catch (error) {
        console.error('공지사항 조회 오류:', error);
    }
};

// 공지사항 저장
const saveNotice = async () => {
    // noticeId가 올바르게 있는지 콘솔 로그로 확인
    console.log('Notice ID:', editableNotice.value.noticeId);

    // Quill 에디터 내용 저장
    editableNotice.value.content = quillInstance.value.root.innerHTML;
    console.log('Saving notice with categoryId:', editableNotice.value.categoryId); // 확인용 로그

    console.log('Back-End 보내는 Data:', editableNotice.value);

    try {
        await updateNotice(editableNotice.value.noticeId, editableNotice.value); // noticeId 전달 확인
        router.push('/manage-notices');
    } catch (error) {
        console.error('공지사항 저장 오류:', error);
    }
};

const loadCategories = async () => {
    try {
        const result = await fetchCategories();
        categories.value = result;
        console.log('Loaded categories:', categories.value); // 카테고리 목록 확인용 로그
    } catch (error) {
        console.error('카테고리 조회 오류:', error);
    }
};

// 삭제 확인 다이얼로그 표시
const showDeleteDialog = () => {
    deleteDialogVisible.value = true;
};

// 공지사항 삭제
const handleDeleteNotice = async () => {
    try {
        await deleteNotice(editableNotice.value.noticeId);
        deleteDialogVisible.value = false; // 다이얼로그 닫기
        router.push('/manage-notices');
    } catch (error) {
        console.error('공지사항 삭제 오류:', error);
    }
};

// 컴포넌트가 마운트될 때 실행
onMounted(async () => {
    const editModeQuery = route.query.edit;
    isEditMode.value = editModeQuery === 'true'; // URL 파라미터에서 값 복구
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

.quill-editor-disabled {
    pointer-events: none; /* 퀼 에디터를 비활성화하여 클릭을 무시함 */
    opacity: 0.5; /* 비활성화된 상태에서 약간의 불투명도를 추가 */
}
</style>
