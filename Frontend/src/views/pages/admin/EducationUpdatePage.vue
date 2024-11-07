<template>
    <div class="education-detail-page">
        <div class="card">
            <label class="text-xl font-bold" style="margin-bottom: 4rem">교육 수정</label>

            <!-- 교육기관 입력 -->
            <div class="input-group">
                <h3 class="input-title"><b>교육기관 *</b></h3>
                <input type="text" v-model="education.institution" class="message-input" placeholder="교육기관을 입력하세요" />
            </div>

            <!-- 교육명 입력 -->
            <div class="input-group">
                <h3 class="input-title"><b>교육명 *</b></h3>
                <input type="text" v-model="education.educationName" class="message-input" placeholder="교육명을 입력하세요" />
            </div>

            <!-- 강사명 입력 -->
            <div class="input-group">
                <h3 class="input-title"><b>강사명 *</b></h3>
                <input type="text" v-model="education.instructorName" class="message-input" placeholder="강사명을 입력하세요" />
            </div>

            <!-- 수강정원 입력 -->
            <div class="input-group">
                <h3 class="input-title"><b>수강정원 *</b></h3>
                <input type="number" v-model="education.participants" class="message-input" placeholder="수강정원을 입력하세요" min="1" />
            </div>

            <!-- 카테고리 선택 -->
            <div class="input-group">
                <h3 class="input-title"><b>카테고리</b></h3>
                <select v-model="education.categoryId" class="message-input">
                    <option value="" disabled selected>카테고리를 선택하세요</option>
                    <option v-for="category in categories" :key="category.id" :value="category.categoryId">
                        {{ category.categoryName }}
                    </option>
                </select>
            </div>

            <!-- 시작 날짜 및 종료 날짜 한 줄에 배치 -->
            <div class="date-section">
                <div class="date-block">
                    <label for="startDate" class="input-title"><b>시작 날짜 *</b></label>
                    <input type="date" v-model="education.educationStart" class="message-input" />
                </div>
                <div class="date-block">
                    <label for="endDate" class="input-title"><b>종료 날짜 *</b></label>
                    <input type="date" v-model="education.educationEnd" class="message-input" />
                </div>
            </div>

            <div class="input-group">
                <h3 class="input-title">내 용</h3>
                <div ref="editor" class="message-editor edit-mode"></div>
            </div>

            <div class="button-group">
                <Button label="수정" icon="pi pi-check" class="gray-button" @click="updateEducationContent" />
                <Button label="취소" icon="pi pi-times" class="p-button-danger" @click="cancelEdit" />
            </div>
        </div>
    </div>
</template>

<script setup>
import router from '@/router';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import Swal from 'sweetalert2';
import { nextTick, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { fetchGet, fetchPut } from '../auth/service/AuthApiService';

const route = useRoute();
const education = ref({
    institution: '',
    educationName: '',
    instructorName: '',
    participants: '',
    categoryName: '',
    categoryId: 0,
    educationStart: '',
    educationEnd: '',
    educationCurriculum: ''
});

const categories = ref([]);
const editor = ref(null);
let quillEditor = null;

const loadEducationData = async () => {
    const educationId = route.params.id;
    console.log('educationId', educationId);
    try {
        const result = await fetchGet(`https://hq-heroes-api.com/api/v1/education-service/education/${educationId}`);
        education.value = { ...result };
    } catch (error) {
        console.error('교육 데이터 조회 오류:', error);
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

        if (education.value.educationCurriculum) {
            quillEditor.root.innerHTML = education.value.educationCurriculum;
        }

        quillEditor.on('text-change', () => {
            education.value.educationCurriculum = quillEditor.root.innerHTML;
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

onMounted(async () => {
    await fetchCategories();
    await loadEducationData();
    await initializeEditor();
});

// 카테고리 목록 가져오기 함수
const fetchCategories = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/educationCategory-service/categories');
        categories.value = response;
    } catch (error) {
        console.error('카테고리 목록을 불러오지 못했습니다.', error);
    }
};

const updateEducationContent = async () => {
    try {
        const requestBody = { ...education.value };
        const result = await updateEducation(route.params.id, requestBody);

        if (result) {
            Swal.fire({
                icon: 'success',
                title: '교육 수정 완료',
                text: '교육이 성공적으로 수정되었습니다.',
                confirmButtonText: '확인'
            }).then(() => {
                router.push({ path: '/manage-education' });
            });
        }
    } catch (error) {
        console.error('교육 수정 중 오류 발생:', error);
        Swal.fire({
            icon: 'error',
            title: '오류 발생',
            text: '교육 수정 중 오류가 발생했습니다. 다시 시도해주세요.',
            confirmButtonText: '확인'
        });
    }
};

// 공지사항 수정
const updateEducation = async (educationId, updatedData) => {
    try {
        const response = await fetchPut(`https://hq-heroes-api.com/api/v1/education-service/education/${educationId}`, updatedData);
        return response;
    } catch (error) {
        throw error;
    }
};

const cancelEdit = () => {
    router.push({ path: '/manage-education' });
};
</script>

<style scoped>
.date-section {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 20px;
}

.date-block {
    flex: 1;
}

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
