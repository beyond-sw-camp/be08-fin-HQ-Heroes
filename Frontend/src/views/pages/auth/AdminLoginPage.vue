<script setup>
import { ref } from 'vue';
import axios from 'axios';
import router from '@/router';
import adminService from '../auth/service/authService';
const adminCode = ref('');
const password = ref('');
const otpCode = ref('');
const email = ref('');

const handleAdminLogin = async () => {
    if (!adminCode.value || !password.value || !otpCode.value) {
        alert('로그인 정보를 입력해주세요.');
        return;
    }

    try {
        const response = await adminService.adminLogin(adminCode.value, password.value, otpCode.value);

        if (response.success) {
            alert('로그인 성공');
            router.push('/'); // 관리자 대시보드로 이동
        }
    } catch (error) {
        alert('로그인 실패: ' + error.message);
    }
};


const requestOTP = async () => {
    if (!email.value) {
        alert('이메일을 입력해주세요.');
        return;
    }

    try {
        await axios.post('http://localhost:8080/mails/email', {
            email: email.value // Send email to get OTP
        });
        alert('OTP가 이메일로 전송되었습니다.');
    } catch (error) {
        alert('OTP 요청 실패: ' + (error.response?.data?.message || '알 수 없는 오류'));
    }
};

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
                    <label for="email" class="block text-surface-900 dark:text-surface-0 text-xl font-medium">이메일</label>
                    <InputText id="email" v-model="email" placeholder="이메일을 입력해주세요." class="mb-4 mt-2 w-full" />
                </div>

                <div class="flex flex-row items-center justify-center">
                    <label for="adminNo" class="block text-surface-900 dark:text-surface-0 text-xl font-medium">OTP</label>
                    <span class="font-medium no-underline text-right cursor-pointer hover:text-primary ml-auto" @click="requestOTP">OTP 발급</span>
                </div>
                <InputOtp class="mb-4 mt-2" v-model="otpCode" :length="6"></InputOtp>

                <div class="flex flex-col items-center">
                    <Button label="Login" class="w-full" @click="handleAdminLogin"></Button>
                    <span class="font-medium no-underline mt-3 text-right cursor-pointer hover:text-primary" @click="goToLogin">사원 로그인</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.pi-eye {
    transform: scale(1.6);
    margin-right: 1rem;
}

.pi-eye-slash {
    transform: scale(1.6);
    margin-right: 1rem;
}

.absolute {
    width: 100%;
    height: 100%;
    overflow: hidden;
}

.relative.z-10 {
    z-index: 10;
}
</style>
