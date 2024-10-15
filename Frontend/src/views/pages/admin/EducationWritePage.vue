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
                            <option v-for="category in categories" :key="category.id" :value="category.name">
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

<script>
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import { onBeforeMount, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

export default {
    setup() {
        const institution = ref(''); // 교육기관
        const subject = ref(''); // 교육명
        const instructor = ref(''); // 강사명
        const capacity = ref(0); // 수강정원
        const selectedCategory = ref(''); // 선택된 카테고리
        const startDate = ref(''); // 시작 날짜
        const endDate = ref(''); // 종료 날짜
        const categories = ref([]); // 카테고리 목록
        const message = ref(''); // Quill 에디터 내용
        const editor = ref(null); // Quill 에디터 참조 변수
        const route = useRoute();

        // Quill 에디터 초기화
        onMounted(() => {
            const quillEditor = new Quill(editor.value, {
                theme: 'snow',
                modules: {
                    toolbar: [[{ font: [] }, { size: [] }], ['bold', 'italic', 'underline', 'strike'], [{ color: [] }, { background: [] }], [{ list: 'ordered' }, { list: 'bullet' }], [{ align: [] }], ['link', 'image', 'blockquote'], ['clean']]
                }
            });

            quillEditor.on('text-change', () => {
                message.value = quillEditor.root.innerHTML; // Quill 에디터 내용 업데이트
            });
        });

        // 카테고리 로드
        const loadCategoriesFromStorage = () => {
            let storedCategories = JSON.parse(localStorage.getItem('educationCategories')) || [];
            categories.value = storedCategories; // 로컬 스토리지에서 카테고리 로드
        };

        onBeforeMount(() => {
            loadCategoriesFromStorage(); // 카테고리 로드
        });

        // 메시지 전송 로직
        const sendMessage = () => {
            console.log(
                `교육기관: ${institution.value}, 교육명: ${subject.value}, 강사명: ${instructor.value}, 수강정원: ${capacity.value}, 카테고리: ${selectedCategory.value}, 시작 날짜: ${startDate.value}, 종료 날짜: ${endDate.value}, 내용: ${message.value}`
            );
            // 실제 메시지 전송 로직 추가
        };

        return {
            institution,
            subject,
            instructor,
            capacity,
            selectedCategory,
            categories,
            startDate,
            endDate,
            message,
            editor,
            sendMessage
        };
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
</style>
