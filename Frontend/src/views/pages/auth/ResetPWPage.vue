<script setup>
import axios from 'axios'; // API 호출을 위한 axios
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Swal from 'sweetalert2'; // 알림 라이브러리
import { ref } from 'vue';
import { useRouter } from 'vue-router'; // Vue Router 임포트

// 사용자가 입력한 성명과 사번
const employeeName = ref('');
const employeeId = ref('');
const isLoading = ref(false); // 비밀번호 발급 진행 상태

// Vue Router 사용
const router = useRouter();

// 비밀번호 재발급 요청 함수
const goToLogin = () => router.push('/login');

async function sendResetPasswordRequest() {
    if (!employeeName.value || !employeeId.value) {
        Swal.fire('입력 오류', '성명과 사번을 모두 입력해주세요.', 'error');
        return;
    }

    isLoading.value = true; // 비밀번호 발급 요청 시작

    try {
        const response = await axios.post('https://hq-heroes-api.com/mails/password', {
            name: employeeName.value,
            employeeId: employeeId.value
        });

        Swal.fire('비밀번호 재발급 완료', response.data, 'success', '확인').then(() => {
            router.push('/login');
        });
    } catch (error) {
        if (error.response) {
            Swal.fire('오류', error.response.data, 'error');
        } else {
            Swal.fire('오류', '서버에 연결할 수 없습니다. 다시 시도해주세요.', 'error');
        }
    } finally {
        isLoading.value = false; // 요청 완료 후 상태를 다시 false로 설정
    }
}
</script>

<template>
    <div class="relative flex items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-950">
        <!-- 비밀번호 재발급 폼 -->
        <div class="relative z-10 flex flex-col items-center justify-center bg-surface-0 dark:bg-surface-900 p-8 sm:p-20 rounded-lg shadow-lg">
            <div class="text-center mb-8">
                <div class="text-primary dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
                <span class="text-muted-color font-medium">비밀번호 재발급</span>
            </div>

            <!-- 성명 입력 -->
            <div class="mb-6 w-full">
                <label for="employeeName" class="block font-semibold text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">성명</label>
                <InputText id="employeeName" v-model="employeeName" class="w-full md:w-[30rem]" placeholder="성명을 입력해주세요." />
            </div>

            <!-- 사번 입력 -->
            <div class="mb-8 w-full">
                <label for="employeeId" class="block font-semibold text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">사원 번호</label>
                <InputText id="employeeId" v-model="employeeId" class="w-full md:w-[30rem]" placeholder="사원 번호를 입력해주세요." />
            </div>

            <!-- 비밀번호 재발급 버튼 -->
            <Button label="비밀번호 재발급" :loading="isLoading" class="w-full md:w-[30rem]" @click="sendResetPasswordRequest">
                <template v-if="isLoading">비밀번호 발급 중...</template>
                <template v-else>비밀번호 재발급</template>
            </Button>

            <!-- 일반 사원 로그인 링크 -->
            <div class="text-center mt-4">
                <span class="text-sm font-medium text-surface-600 cursor-pointer hover:text-primary" @click="goToLogin">사원 로그인</span>
            </div>
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
