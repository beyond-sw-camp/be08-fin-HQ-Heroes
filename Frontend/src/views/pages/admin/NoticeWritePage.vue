<template>
    <div class="app-container">
        <!-- 메시지 작성 영역 -->
        <div class="main">
            <div class="compose-message-container">
                <div class="compose-message">
                    <h3><b>작성자</b></h3>
                    <input type="text" v-model="to" class="message-input" />

                    <h3><b>제 목</b></h3>
                    <input type="text" v-model="subject" class="message-input" />

                    <h3><b>카테고리</b></h3>
                    <select v-model="selectedCategory" class="message-input">
                        <option v-for="category in categories" :key="category.id" :value="category.name">
                            {{ category.categoryName }}
                        </option>
                    </select>

                    <h3><b>내 용</b></h3>
                    <br />
                    <!-- Quill 에디터 -->
                    <div ref="editor" class="message-editor"></div>
                    <div class="button-container">
                        <button @click="sendMessage" class="send-button" style="margin-top: 10px">작 성</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import Quill from 'quill';
import 'quill/dist/quill.snow.css'; // Quill의 스타일
import { onBeforeMount, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

export default {
    setup() {
        const to = ref('');
        const subject = ref('');
        const selectedCategory = ref(''); // 선택된 카테고리
        const categories = ref([]); // 카테고리 목록
        const message = ref('');
        const editor = ref(null); // Quill 에디터를 참조할 변수
        const route = useRoute();

        // Quill 에디터 초기화
        onMounted(() => {
            const quillEditor = new Quill(editor.value, {
                theme: 'snow', // Quill의 '' 테마 사용
                modules: {
                    toolbar: [
                        [{ font: [] }, { size: [] }], // 글꼴 및 글자 크기 선택
                        ['bold', 'italic', 'underline', 'strike'], // 텍스트 스타일
                        [{ color: [] }, { background: [] }], // 텍스트 색상 및 배경색 선택
                        [{ list: 'ordered' }, { list: 'bullet' }], // 목록
                        [{ align: [] }], // 정렬
                        ['link', 'image', 'blockquote'], // 링크, 이미지, 인용구
                        ['clean'] // 서식 제거
                    ]
                }
            });

            quillEditor.on('text-change', () => {
                message.value = quillEditor.root.innerHTML; // Quill 에디터 내용이 변경될 때 message 값 업데이트
            });
        });

        // fromPage 확인 및 카테고리 설정
        onBeforeMount(() => {
            const fromPage = route.query.fromPage;
            const incomingCategories = JSON.parse(route.query.categories || '[]'); // JSON 문자열을 배열로 변환

            if (fromPage === 'educationListPage') {
                console.log('교육 페이지에서 넘어옴', incomingCategories);
                categories.value = incomingCategories; // 받아온 카테고리를 설정
            } else {
                console.log('공지사항 페이지에서 넘어옴', incomingCategories);
                categories.value = incomingCategories; // 받아온 카테고리를 설정
            }
        });

        const sendMessage = () => {
            console.log(`Sending message to: ${to.value}, Subject: ${subject.value}, Category: ${selectedCategory.value}, Message: ${message.value}`);
            // 메시지를 전송하는 로직 추가
        };

        return { to, subject, selectedCategory, categories, message, editor, sendMessage };
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
    height: 100vh; /* 전체 화면 높이 */
    margin-bottom: 10%;
}

.main {
    width: 100%;
    padding: 0; /* 상단과 하단 여백을 없애기 위해 0으로 설정 */
    display: flex;
    flex-direction: column;
}

.compose-message-container {
    flex-grow: 1; /* 남은 공간을 채우도록 설정 */
    background-color: #ffffff;
    border: 1px solid #ddd;
    border-radius: 5px;
    padding: 20px;
    margin: 0; /* 상단 여백을 없애기 */
    display: flex;
    flex-direction: column;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 그림자 효과 추가 */
}

.message-input {
    margin: 15px 0; /* 입력 필드 아래쪽에 간격 추가 */
    padding: 10px; /* 내부 여백 */
    font-size: 16px; /* 폰트 크기 */
    border: 1px solid #ddd; /* 테두리 */
    border-radius: 5px; /* 모서리 둥글게 */
    width: 100%; /* 너비를 100%로 설정 */
}

.message-editor {
    flex-grow: 1;
    font-size: 16px;
    height: 350px; /* 에디터의 높이 설정 */
    overflow-y: auto; /* 세로 스크롤바 활성화 */
    border-radius: 0 0 5px 5px; /* 모서리 둥글게 */
}

.button-container {
    display: flex; /* Flexbox 사용 */
    justify-content: flex-end; /* 버튼을 오른쪽으로 정렬 */
}

.send-button {
    padding: 10px 20px; /* 버튼 안쪽 여백 */
    background-color: #6366f1; /* 버튼 배경색 */
    color: white; /* 글자색 */
    border: none; /* 테두리 없애기 */
    border-radius: 5px; /* 모서리 둥글게 */
    cursor: pointer; /* 커서 포인터로 변경 */
    font-size: 16px; /* 폰트 크기 */
    transition: background-color 0.2s; /* 배경색 변화 효과 */
}

.send-button:hover {
    background-color: #4f46e5; /* 호버 시 배경색 변화 */
}
</style>
