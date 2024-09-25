<template>
    <div class="app-container">
        <div class="sidebar">
            <!-- 상단 프로필 영역 -->
            <div class="user-profile">
                <h4 class="user-name">John Doe</h4>
                <p class="user-department">HR Department</p>
            </div>

            <!-- 검색 필드 -->
            <div class="search-container">
                <i class="pi pi-search search-icon"></i>
                <input type="text" placeholder="Search users..." v-model="searchQuery" class="search-input" />
            </div>

            <!-- 사용자 목록 -->
            <div class="profile" v-for="user in filteredUsers" :key="user.id">
                <img :src="user.image" alt="user photo" class="user-photo"/>
                <div class="user-info">
                    <h5>{{ user.name }}</h5>
                    <p>{{ user.lastMessage }}</p>
                </div>
            </div>
        </div>

        <!-- 메시지 작성 영역 -->
        <div class="main">
            <div class="compose-message-container">
                <div class="compose-message">
                    <h3><b>To</b></h3>
                    <input type="text" v-model="to" class="message-input" />
                    <h3><b>Subject</b></h3>
                    <input type="text" v-model="subject" class="message-input" />
                    <h3><b>Message</b></h3>
                    <textarea v-model="message" placeholder="Type your message here..." class="message-textarea"></textarea>
                    <div class="button-container">
                        <button @click="sendMessage" class="send-button">Send Message</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, computed } from 'vue';

export default {
    setup() {
        const users = ref([
            { id: 1, name: '홍길동', image: 'path/to/image.jpg', lastMessage: 'Last message preview' },
            { id: 2, name: '이순신', image: 'path/to/image.jpg', lastMessage: 'Last message preview' },
            { id: 3, name: '김유신', image: 'path/to/image.jpg', lastMessage: 'Last message preview' },
            // Other users
        ]);
        
        const searchQuery = ref('');
        const to = ref('');
        const subject = ref('');
        const message = ref('');

        const filteredUsers = computed(() => {
            return users.value.filter(user => 
                user.name.toLowerCase().includes(searchQuery.value.toLowerCase())
            );
        });

        const sendMessage = () => {
            console.log(`Sending message to: ${to.value}, Subject: ${subject.value}, Message: ${message.value}`);
            // Add logic to send the message
        };

        return { users, searchQuery, filteredUsers, to, subject, message, sendMessage };
    }
};
</script>

<style scoped>
.app-container {
    display: flex;
    height: 100vh;
}

.sidebar {
    width: 25%; /* 사이드바 폭 조정 */
    background-color: #ffffff;
    padding: 20px;
    margin: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    overflow-y: auto;
    display: flex;
    flex-direction: column;
}

.user-profile {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 20%; /* 유지 */
    padding-bottom: 10px; /* 프로필 아래쪽 패딩 */
}

.user-profile::after {
    content: '';
    display: block;
    width: 100%;
    height: 2px; /* 선의 두께 */
    background-color: #ddd; /* 선의 색상 */
    margin: 10px 0; /* 프로필과 선 사이의 간격 */
}

.search-container {
    position: relative; /* 아이콘 위치 조정을 위한 relative */
}

.search-icon {
    position: absolute;
    left: 10px; /* 아이콘 위치 조정 */
    top: 50%;
    transform: translateY(-50%);
    color: #aaa; /* 아이콘 색상 */
}

.search-input {
    padding: 10px 10px 10px 40px; /* 왼쪽 패딩을 아이콘의 너비에 맞게 조정 */
    margin-bottom: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    width: 100%; /* 너비를 100%로 설정 */
}

.profile {
    display: flex;
    align-items: center;
    padding: 10px;
    margin: 5px 0; /* 각 사용자 목록 사이의 간격 */
    background-color: #f9f9f9; /* 박스 배경색 */
    border: 1px solid #ddd; /* 테두리 */
    border-radius: 5px; /* 모서리 둥글게 */
    transition: background-color 0.2s; /* 호버 시 배경색 변화 효과 */
}

.profile:hover {
    background-color: #f0f0f0; /* 호버 시 배경색 변화 */
}

.user-photo {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 10px;
}

.user-info h5 {
    margin: 0;
    color: #333;
}

.user-info p {
    margin: 0;
    color: #666;
    font-size: 0.9em;
}

.main {
    width: 75%;
    padding: 20px;
    display: flex;
    flex-direction: column;
    height: 100vh; /* 높이를 전체 화면으로 맞춤 */
}

.compose-message-container {
    flex-grow: 1; /* 나머지 영역을 채우도록 설정 */
    background-color: #ffffff;
    border: 1px solid #ddd;
    border-radius: 5px;
    padding: 20px;
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

.message-textarea {
    flex-grow: 1; /* 메시지 입력 영역도 나머지 공간을 채우도록 설정 */
    margin: 15px 0;
    padding: 10px;
    font-size: 16px;
    border: 1px solid #ddd;
    border-radius: 5px;
    width: 100%;
    resize: none;
}

.button-container {
    display: flex; /* Flexbox 사용 */
    justify-content: flex-end; /* 버튼을 오른쪽으로 정렬 */
}

.send-button {
    padding: 10px 20px; /* 버튼 안쪽 여백 */
    background-color: #6366F1; /* 버튼 배경색 */
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
