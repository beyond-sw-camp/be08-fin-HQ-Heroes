<template>
    <div class="app-container">
        <div class="main">
            <div class="compose-message-container">
                <div class="compose-message">
                    <h1 class="title">📘 <b>교육 작성</b></h1>

                    <!-- 교육기관 입력 -->
                    <div class="input-group">
                        <h3 class="input-title"><b>교육기관 *</b></h3>
                        <input type="text" v-model="institution" class="message-input" placeholder="교육기관을 입력하세요" />
                    </div>

                    <!-- 교육명 입력 -->
                    <div class="input-group">
                        <h3 class="input-title"><b>교육명 *</b></h3>
                        <input type="text" v-model="subject" class="message-input" placeholder="교육명을 입력하세요" />
                    </div>

                    <!-- 강사명 입력 -->
                    <div class="input-group">
                        <h3 class="input-title"><b>강사명 *</b></h3>
                        <input type="text" v-model="instructor" class="message-input" placeholder="강사명을 입력하세요" />
                    </div>

                    <!-- 수강정원 입력 -->
                    <div class="input-group">
                        <h3 class="input-title"><b>수강정원 *</b></h3>
                        <input type="number" v-model="capacity" class="message-input" placeholder="수강정원을 입력하세요" min="1" />
                    </div>

                    <!-- 카테고리 선택 -->
                    <div class="input-group">
                        <h3 class="input-title"><b>카테고리</b></h3>
                        <select v-model="selectedCategory" class="message-input">
                            <option value="" disabled selected>카테고리를 선택하세요</option>
                            <option v-for="category in categories" :key="category.id" :value="category.categoryName">
                                {{ category.categoryName }}
                            </option>
                        </select>
                    </div>

                    <!-- 시작 날짜 및 종료 날짜 한 줄에 배치 -->
                    <div class="date-section">
                        <div class="date-block">
                            <label for="startDate" class="input-title"><b>시작 날짜 *</b></label>
                            <input type="date" v-model="startDate" class="message-input" />
                        </div>
                        <div class="date-block">
                            <label for="endDate" class="input-title"><b>종료 날짜 *</b></label>
                            <small v-if="isDateInvalid" class="text-red-500 ml-2">시작 날짜는 종료 날짜보다 이전이어야 합니다.</small>
                            <input type="date" v-model="endDate" class="message-input" />
                        </div>
                    </div>

                    <!-- 내 용 입력 -->
                    <div class="input-group">
                        <h3 class="input-title"><b>내 용 *</b></h3>
                        <div ref="editor" class="message-editor"></div>
                    </div>

                    <!-- 작성 버튼 -->
                    <div class="button-container">
                        <button @click="sendMessage" class="send-button">💬 작 성</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import router from '@/router';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import Swal from 'sweetalert2';
import { onMounted, ref, watch } from 'vue';
import { fetchGet, fetchPost } from '../auth/service/AuthApiService'; // fetchPost 사용

const institution = ref(''); // 교육기관
const subject = ref(''); // 교육명
const instructor = ref(''); // 강사명
const capacity = ref(0); // 수강정원
const selectedCategory = ref(''); // 선택된 카테고리
const categories = ref([]); // 카테고리 목록
const message = ref(''); // Quill 에디터 내용
const editor = ref(null); // Quill 에디터 참조 변수
const startDate = ref(''); // 시작 날짜
const endDate = ref(''); // 종료 날짜
const isDateInvalid = ref(false);

let quillEditor = null; // quillEditor 변수

// Quill 에디터 초기화
onMounted(async () => {
    // 현재 날짜를 "YYYY-MM-DD" 형식으로 설정
    const today = new Date().toISOString().split('T')[0];
    startDate.value = today;
    await fetchCategories();

    quillEditor = new Quill(editor.value, {
        theme: 'snow',
        modules: {
            toolbar: {
                container: [[{ font: [] }, { size: [] }], ['bold', 'italic', 'underline', 'strike'], [{ color: [] }, { background: [] }], [{ list: 'ordered' }, { list: 'bullet' }], [{ align: [] }], ['link', 'image', 'blockquote'], ['clean']],
                handlers: {
                    image: imageHandler // 이미지 핸들러 추가
                }
            }
        }
    });

    quillEditor.on('text-change', () => {
        message.value = quillEditor.root.innerHTML; // Quill 에디터 내용 업데이트
    });
});

// 이미지 핸들러 함수
const imageHandler = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
        const file = input.files[0];

        // 이미지 크기 조정
        const resizedImage = await resizeImage(file, 700, 700); // 원하는 크기로 조정 (800x800 예시)

        const formData = new FormData();
        formData.append('file', resizedImage);

        try {
            // 이미지 업로드 API 호출
            const response = await fetch('https://hq-heroes-api.com/api/v1/upload-image', {
                method: 'POST',
                body: formData
            });

            const data = await response.json();
            const imageUrl = data.imageUrl; // 서버에서 반환된 이미지 URL

            // quillEditor가 정의되었는지 확인
            if (quillEditor) {
                const range = quillEditor.getSelection();
                quillEditor.insertEmbed(range.index, 'image', imageUrl); // 에디터에 이미지 URL 삽입
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

// 카테고리 이름을 ID로 변환
const getCategoryById = (categoryName) => {
    // 카테고리 목록에서 categoryName이 일치하는 항목 찾기
    const category = categories.value.find((cat) => cat.categoryName === categoryName);

    if (!category) {
        console.error('카테고리 ID를 찾을 수 없습니다.');
    }

    // categoryId를 반환
    return category ? category.categoryId : null;
};

// 카테고리 목록 가져오기 함수
const fetchCategories = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/educationCategory-service/categories');
        categories.value = response;
    } catch (error) {
        console.error('카테고리 목록을 불러오지 못했습니다.', error);
    }
};

// 날짜 변경 시 유효성 검사
watch([startDate, endDate], ([newStartDate, newEndDate]) => {
    isDateInvalid.value = newStartDate && newEndDate && newStartDate > newEndDate;
});

// 메시지 전송 로직
const sendMessage = async () => {
    if (isDateInvalid.value) {
        Swal.fire({
            icon: 'error',
            title: '유효성 오류',
            text: '종료 날짜는 시작 날짜보다 이후여야 합니다.',
            confirmButtonText: '확인'
        });
        return;
    }

    try {
        const requestBody = {
            educationName: subject.value,
            instructorName: instructor.value,
            institution: institution.value,
            educationStart: startDate.value,
            educationEnd: endDate.value,
            categoryId: getCategoryById(selectedCategory.value),
            educationCurriculum: message.value,
            participants: capacity.value
        };

        const result = await fetchPost('https://hq-heroes-api.com/api/v1/education-service/education', requestBody);

        if (result) {
            await Swal.fire({
                icon: 'success',
                title: '교육 작성 완료',
                text: '교육 정보가 성공적으로 작성되었습니다.',
                confirmButtonText: '확인'
            });
            router.push({ path: '/manage-education' });
        }
    } catch (error) {
        console.error('API 요청 실패:', error);
        Swal.fire({
            icon: 'error',
            title: '오류 발생',
            text: '교육 정보 작성 중 문제가 발생했습니다.',
            confirmButtonText: '확인'
        });
    }
};
</script>

<style scoped>
html,
body {
    margin: 0;
    padding: 0;
    height: 100%;
}

.app-container {
    display: flex;
    height: 100vh;
    margin-bottom: 10%;
}

.main {
    width: 100%;
    padding: 0;
    display: flex;
    flex-direction: column;
}

.compose-message-container {
    flex-grow: 1;
    background-color: #f9fafb;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 30px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.title {
    font-size: 24px;
    margin-bottom: 20px;
}

.input-group {
    margin-bottom: 20px;
}

.input-title {
    margin-bottom: 8px;
    font-size: 16px;
    color: #333;
}

.message-input {
    padding: 8px;
    font-size: 14px;
    border: 1px solid #ddd;
    border-radius: 5px;
    width: 100%;
    box-sizing: border-box;
}

/* 시작 날짜와 종료 날짜를 한 줄에 배치 */
.date-section {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 20px;
}

.date-block {
    flex: 1;
}

.message-editor {
    flex-grow: 1;
    height: 300px;
    background-color: #ffffff;
    border: 1px solid #ddd;
    border-radius: 5px;
    padding: 10px;
    font-size: 14px;
    overflow-y: auto;
}

.button-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}

.send-button {
    padding: 12px 30px;
    background-color: #6366f1;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 16px;
    transition: background-color 0.3s;
}

.send-button:hover {
    background-color: #4f46e5;
}

.text-red-500 {
    color: red;
}
.ml-2 {
    margin-left: 0.5rem;
}
</style>
