<script setup>
import { ref } from 'vue';
import InputText from 'primevue/inputtext'; // Import InputText component
import Button from 'primevue/button'; // Import Button component
import Swal from 'sweetalert2'; // Alert library for feedback to the user

const employeeName = ref('');  // 성명
const employeeId = ref('');    // 사번

// 비밀번호 재발급 요청 함수
function sendResetPasswordRequest() {
    if (!employeeName.value || !employeeId.value) {
        Swal.fire('입력 오류', '성명과 사번을 모두 입력해주세요.', 'error');
        return;
    }

    // 여기서 실제 서버로 성명과 사번을 전송하는 API 요청을 처리해야 합니다.
    // 예시로 Swal을 사용해 성공적인 요청을 시뮬레이션합니다.
    Swal.fire('비밀번호 재발급 요청 완료', '입력한 정보로 비밀번호 재발급 요청이 완료되었습니다.', 'success');
}
</script>

<template>
    <div class="relative flex items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-950">
        <!-- 배경 설정 -->
        <div class="absolute inset-0" style="margin-left: 5%;">
            <!-- SVG 생략 -->
        </div>

        <!-- 비밀번호 재발급 폼 -->
        <div class="relative z-10 flex flex-col items-center justify-center bg-surface-0 dark:bg-surface-900 p-8 sm:p-20 rounded-lg shadow-lg">
            <div class="text-center mb-8">
                <div class="text-surface-900 dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
                <span class="text-muted-color font-medium">비밀번호 재발급</span>
            </div>

            <!-- 성명 입력 -->
            <div class="mb-6 w-full">
                <label for="employeeName" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">성명</label>
                <InputText id="employeeName" v-model="employeeName" class="w-full md:w-[30rem]" placeholder="성명을 입력해주세요." variant="filled" />
            </div>

            <!-- 사번 입력 -->
            <div class="mb-8 w-full">
                <label for="employeeId" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">사번</label>
                <InputText id="employeeId" v-model="employeeId" class="w-full md:w-[30rem]" placeholder="사번을 입력해주세요." variant="filled" />
            </div>

            <!-- 비밀번호 재발급 버튼 -->
            <Button label="비밀번호 재발급" class="w-full md:w-[30rem]" @click="sendResetPasswordRequest"></Button>
        </div>
    </div>
</template>

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
