<template>
    <div class="app-container">
        <div class="main">
            <div class="compose-message-container">
                <div class="compose-message">
                    <h1 class="title">📢 <b>공지사항 작성</b></h1>

                    <div class="input-group">
                        <h3 class="input-title"><b>작성자 *</b></h3>
                        <input type="text" v-model="to" class="message-input readonly-input" readonly />
                    </div>

                    <div class="input-group">
                        <h3 class="input-title"><b>제 목 *</b></h3>
                        <input type="text" v-model="subject" class="message-input" placeholder="제목을 입력하세요" />
                    </div>

                    <div class="input-group">
                        <h3 class="input-title"><b>카테고리</b></h3>
                        <select v-model="selectedCategory" class="message-input">
                            <option value="" disabled selected>카테고리를 선택하세요</option>
                            <option v-for="category in categories" :key="category.categoryId" :value="category.categoryName">
                                {{ category.categoryName }}
                            </option>
                        </select>
                    </div>

                    <div class="input-group">
                        <h3 class="input-title"><b>내 용 *</b></h3>
                        <div ref="editor" class="message-editor"></div>
                    </div>

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
import { useAuthStore } from '@/stores/authStore';
import Quill from 'quill';
import 'quill/dist/quill.snow.css'; // Quill의 스타일
import Swal from 'sweetalert2';
import { onBeforeMount, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { fetchPost } from '../auth/service/AuthApiService';
import { fetchCategories } from './service/adminNoticeCategoryService';

const to = ref('');
const subject = ref('');
const selectedCategory = ref(''); // 선택된 카테고리
const categories = ref([]); // 카테고리 목록
const message = ref('');
const editor = ref(null); // Quill 에디터를 참조할 변수
const route = useRoute();
const employeeId = ref('');
let quillEditor = null; // quillEditor 변수 선언
const authStore = useAuthStore();

// Quill 에디터 초기화
onMounted(async () => {
    // 카테고리 데이터 로드
    const fetchedCategories = await fetchCategories();
    categories.value = [...fetchedCategories];

    // Quill 에디터 초기화
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

    // 에디터 내용이 변경될 때 message 값 업데이트
    quillEditor.on('text-change', () => {
        message.value = quillEditor.root.innerHTML;
    });

    // 작성자 정보 설정
    to.value = authStore.employeeData.employeeName;
    employeeId.value = authStore.loginUserId;
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
        const resizedImage = await resizeImage(file, 700, 700); // 원하는 크기로 조정

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

// 카테고리 로드
const loadCategories = async () => {
    try {
        const fetchedCategories = await fetchCategories();
        categories.value = [...fetchedCategories];
    } catch (error) {
        console.error('카테고리 조회 중 오류:', error);
    }
};

// 카테고리 설정
onBeforeMount(() => {
    loadCategories();
});

const sendMessage = async () => {
    try {
        console.log('selectedCategory = ', selectedCategory.value); // 추가: 선택된 카테고리 출력

        const requestBody = {
            employeeId: employeeId.value, // 로컬 스토리지에서 가져온 employeeId
            employeeName: to.value, // 작성자 이름
            title: subject.value, // 제목
            content: message.value, // Quill 에디터에서 작성한 HTML 내용
            categoryId: getCategoryById(selectedCategory.value), // 선택된 카테고리 ID
            updaterId: authStore.loginUserId, // 수정한 사람 ID
            updaterName: authStore.employeeData.employeeName // 수정한 사람 이름
        };

        console.log('requestData = ', requestBody); // 추가: 요청 데이터 출력

        // fetchPost 함수를 사용하여 데이터 전송
        const result = await fetchPost('https://hq-heroes-api.com/api/v1/notice-service/notice', requestBody);

        if (result) {
            // SweetAlert2를 사용하여 성공 알림 표시
            Swal.fire({
                icon: 'success',
                title: '공지사항 작성 완료',
                text: '공지사항이 성공적으로 작성되었습니다.',
                confirmButtonText: '확인'
            }).then(() => {
                // 확인 버튼 클릭 후 다른 페이지로 이동
                router.push({ path: '/manage-notices' }); // 원하는 페이지의 경로로 수정
            });
        }
    } catch (error) {
        console.error('공지사항 작성 중 오류 발생:', error);
        Swal.fire({
            icon: 'error',
            title: '오류 발생',
            text: '공지사항 작성 중 오류가 발생했습니다. 다시 시도해주세요.',
            confirmButtonText: '확인'
        });
    }
};

const getCategoryById = (categoryName) => {
    console.log('selectedCategory.value = ', categoryName); // 선택된 카테고리 이름 출력
    console.log('categories.value = ', categories.value); // 카테고리 목록 출력
    const category = categories.value.find((cat) => cat.categoryName === categoryName);
    console.log('category = ', category); // 찾은 카테고리 객체 출력
    return category ? category.categoryId : null; // categoryId 반환
};

// 페이지를 떠날 때 공지사항 목록으로 이동
onBeforeUnmount(() => {
    // router.push({ path: '/manage-notices' });
    if (quillEditor) {
        quillEditor.off('text-change');
        quillEditor = null; // 에디터 정리
    }
});
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
    padding: 20px; /* 패딩을 줄였습니다 */
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.title {
    font-size: 22px; /* 폰트 크기를 줄였습니다 */
    margin-bottom: 15px; /* 제목 하단 여백을 줄였습니다 */
}

.input-group {
    margin-bottom: 15px; /* 입력 그룹 간 간격을 줄였습니다 */
}

.input-title {
    margin-bottom: 6px; /* 입력 제목과 입력 필드 사이 간격을 줄였습니다 */
    font-size: 16px; /* 폰트 크기를 줄였습니다 */
    color: #333;
}

.message-input {
    padding: 10px;
    font-size: 14px; /* 폰트 크기를 줄였습니다 */
    border: 1px solid #ddd;
    border-radius: 5px;
    width: 100%;
    box-sizing: border-box;
}

.message-editor {
    flex-grow: 1;
    height: 400px; /* 에디터의 높이를 줄였습니다 */
    background-color: #ffffff;
    border: 1px solid #ddd;
    border-radius: 5px;
    padding: 8px; /* 내부 패딩을 줄였습니다 */
    font-size: 14px; /* 폰트 크기를 줄였습니다 */
    overflow-y: auto;
}

.button-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 15px; /* 위쪽 여백을 줄였습니다 */
}

.send-button {
    padding: 10px 25px; /* 버튼 패딩을 줄였습니다 */
    background-color: #6366f1;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 16px; /* 버튼 폰트 크기를 줄였습니다 */
    transition: background-color 0.3s;
}

.send-button:hover {
    background-color: #4f46e5;
}

.readonly-input[readonly] {
    background-color: #f0f0f0; /* 회색 배경 */
    cursor: not-allowed; /* 커서를 not-allowed로 변경 */
}
</style>
