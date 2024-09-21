<script setup>
import { ref } from 'vue';

const email = ref('');
</script>

<template>
    <div class="relative flex items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-950">
        <!-- 배경 설정 -->
        <div class="absolute inset-0" style="margin-left: 5%;">
            <svg xmlns="http://www.w3.org/2000/svg" width="600.67004" height="730.99672" viewBox="0 0 731.67004 625.99672">
                <!-- SVG 내용 생략 -->
            </svg>
        </div>

        <!-- 비밀번호 재발급 폼 -->
        <div class="relative z-10 flex flex-col items-center justify-center bg-surface-0 dark:bg-surface-900 p-8 sm:p-20 rounded-lg shadow-lg">
            <div class="text-center mb-8">
                <div class="text-surface-900 dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
                <span class="text-muted-color font-medium">비밀번호 재발급</span>
            </div>

            <div>
                <label for="email1" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">이메일</label>
                <InputText id="email1" type="text" placeholder="이메일을 입력해주세요." class="w-full md:w-[30rem] mb-8" v-model="email" />

                <Button label="비밀번호 재발급" class="w-full" @click="sendResetPasswordEmail"></Button>
            </div>
        </div>
    </div>
</template>

<script>
function sendResetPasswordEmail() {
    // 이메일을 입력받고 서버로 요청을 보내 비밀번호 재설정 메일 발송
    alert("비밀번호 재설정 링크가 이메일로 전송되었습니다.");
}
</script>

<style scoped>
/* 기본 스타일 */
.pi-eye {
    transform: scale(1.6);
    margin-right: 1rem;
}

.pi-eye-slash {
    transform: scale(1.6);
    margin-right: 1rem;
}

/* 배경으로 들어간 SVG의 크기를 전체화면에 맞춤 */
.absolute {
    width: 100%;
    height: 100%;
    overflow: hidden;
}

/* 로그인 폼을 배경 위에 오버레이 */
.relative.z-10 {
    z-index: 10;
}
</style>
