<script setup>
import { ref } from 'vue';
import router from '@/router';

const adminCode = ref('');
const password = ref('');
const otpCode = ref('');

const handleAdminLogin = () => {
    if (!adminCode.value || !password.value || !otpCode.value) {
        alert("로그인 정보를 입력해주세요.");
        return;
    }

    if (otpCode.value !== '123456') {
        alert("잘못된 OTP 코드입니다.");
        return;
    }

    alert("로그인 성공");
    router.push('/');
};

const requestOTP = () =>{
  otpCode.value === '123456';
  alert("OTP : 123456")
}

const goToLogin = () => router.push('/login');
</script>

<template>
    <div class="relative flex items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-950">

        <div class="relative z-10 flex flex-col items-center justify-center bg-surface-0 dark:bg-surface-900 p-8 sm:p-20 rounded-lg shadow-lg">
            <div class="text-center mb-8">
                <div class="text-surface-900 dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
                <span class="text-muted-color font-medium">관리자 로그인</span>
            </div>
            <div>
                <label for="adminCode" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">관리자 코드</label>
                <InputText id="adminCode" type="text" placeholder="관리자 코드를 입력해주세요." class="w-full md:w-[30rem] mb-4" v-model="adminCode" />

                <label for="adminPassword" class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">비밀번호</label>
                <Password id="adminPassword" v-model="password" placeholder="비밀번호를 입력해주세요." :toggleMask="true" class="mb-4" fluid :feedback="false"></Password>
                <div class="flex flex-row items-center justify-center">
                    <label for="adminNo" class="block text-surface-900 dark:text-surface-0 text-xl font-medium">OTP</label>
                    <span class="font-medium no-underline text-right cursor-pointer text-primary ml-auto" @click="requestOTP">OTP 발급</span>
                </div>
                <InputOtp class="mb-4 mt-2" v-model="otpCode" :length="6"></InputOtp>
                <div class="flex flex-col items-center">
                    <Button label="Login" class="w-full" @click="handleAdminLogin"></Button>
                    <span class="font-medium no-underline mt-2 text-right cursor-pointer text-primary" @click="goToLogin">사원 로그인</span>
                </div>
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
