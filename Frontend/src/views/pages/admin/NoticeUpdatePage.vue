<template>
    <div class="notice-detail-page">
        <div class="card">
            <label class="text-xl font-bold" style="margin-bottom: 4rem">공지사항 수정</label>

            <div class="input-group">
                <h3 class="input-title">제 목</h3>
                <input type="text" v-model="editableNotice.title" class="message-input" placeholder="제목을 입력하세요" />
            </div>

            <div class="input-group">
                <h3 class="input-title">작성자</h3>
                <input type="text" v-model="editableNotice.employeeName" class="message-input" />
            </div>

            <div class="input-group">
                <h3 class="input-title">카테고리</h3>
                <select v-model="editableNotice.categoryId" class="message-input">
                    <option v-for="category in categories" :key="category.categoryId" :value="category.categoryId">
                        {{ category.categoryName }}
                    </option>
                </select>
            </div>

            <div class="input-group">
                <h3 class="input-title">내 용</h3>
                <div ref="editor" class="message-editor edit-mode"></div>
            </div>

            <div class="button-group">
                <Button label="수정" icon="pi pi-check" class="gray-button" @click="updateNoticeContent" />
                <Button label="취소" icon="pi pi-times" class="p-button-danger" @click="cancelEdit" />
            </div>
        </div>
    </div>
</template>

<script setup>
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import Swal from 'sweetalert2';
import { nextTick, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchCategories } from './service/adminNoticeCategoryService';
import { fetchNoticeById, updateNotice } from './service/adminNoticeService';
const route = useRoute();
const router = useRouter();

const editableNotice = ref({
    title: '',
    employeeId: '',
    categoryId: '',
    content: ''
});
const categories = ref([]);

const editor = ref(null);
let quillEditor = null;

const loadNotice = async () => {
    const noticeId = route.params.id;
    console.log(noticeId);
    try {
        const result = await fetchNoticeById(noticeId);
        editableNotice.value = { ...result };
    } catch (error) {
        console.error('공지사항 조회 오류:', error);
    }
};

const initializeEditor = async () => {
    await nextTick();

    if (editor.value) {
        quillEditor = new Quill(editor.value, {
            theme: 'snow',
            modules: {
                toolbar: {
                    container: [[{ font: [] }, { size: [] }], ['bold', 'italic', 'underline', 'strike'], [{ color: [] }, { background: [] }], [{ list: 'ordered' }, { list: 'bullet' }], [{ align: [] }], ['link', 'image', 'blockquote'], ['clean']],
                    handlers: {
                        image: imageHandler
                    }
                }
            }
        });

        quillEditor.enable(true);

        if (editableNotice.value.content) {
            quillEditor.root.innerHTML = editableNotice.value.content;
        }

        quillEditor.on('text-change', () => {
            editableNotice.value.content = quillEditor.root.innerHTML;
        });
    }
};

const imageHandler = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
        const file = input.files[0];
        const resizedImage = await resizeImage(file, 700, 700);

        const formData = new FormData();
        formData.append('file', resizedImage);

        try {
            const response = await fetch('https://hq-heroes-api.com/api/v1/upload-image', {
                method: 'POST',
                body: formData
            });
            const data = await response.json();
            const imageUrl = data.imageUrl;

            if (quillEditor) {
                const range = quillEditor.getSelection();
                quillEditor.insertEmbed(range.index, 'image', imageUrl);
            }
        } catch (error) {
            console.error('이미지 업로드 실패:', error);
        }
    };
};

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
                resolve(new File([blob], file.name, { type: file.type }));
            }, file.type);
        };

        reader.readAsDataURL(file);
    });
};

const updateNoticeContent = async () => {
    try {
        const requestBody = {
            title: editableNotice.value.title,
            content: quillEditor.root.innerHTML,
            employeeId: editableNotice.value.employeeId,
            categoryId: editableNotice.value.categoryId
        };

        const result = await updateNotice(route.params.id, requestBody);

        if (result) {
            Swal.fire({
                icon: 'success',
                title: '공지사항 수정 완료',
                text: '공지사항이 성공적으로 수정되었습니다.',
                confirmButtonText: '확인'
            }).then(() => {
                router.push({ path: '/manage-notices' });
            });
        }
    } catch (error) {
        console.error('공지사항 수정 중 오류 발생:', error);
        Swal.fire({
            icon: 'error',
            title: '오류 발생',
            text: '공지사항 수정 중 오류가 발생했습니다. 다시 시도해주세요.',
            confirmButtonText: '확인'
        });
    }
};

const cancelEdit = () => {
    router.push({ path: '/manage-notices' });
};

onMounted(async () => {
    const fetchedCategories = await fetchCategories(); // 카테고리 데이터 가져오기
    categories.value = [{ id: null, categoryName: '전체' }, ...fetchedCategories];

    await loadNotice();
    await initializeEditor();
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
    height: 600px;
}

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 0.5rem;
    margin-top: 1rem;
}

.quill-editor-disabled {
    pointer-events: none;
    opacity: 0.5;
}
</style>
