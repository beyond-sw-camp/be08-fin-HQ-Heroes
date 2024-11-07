<template>
    <div class="relative flex items-center justify-center bg-surface-0 min-h-screen overflow-hidden">
        <!-- 배경 이미지 영역 -->
        <div class="flex-1 hidden lg:flex items-center justify-center">
            <img src="/images/Heroesbackground.png" alt="background-image" class="h-screen w-screen object-cover lg:[clip-path:polygon(80%_100%,100%_0%,0%_0%,0%_100%)]" />
        </div>

        <!-- 로그인 폼 영역 -->
        <div class="flex-1 flex items-center justify-center transform scale-110">
            <div class="w-full max-w-md space-y-6">
                <!-- 타이틀 및 설명 -->
                <div class="text-center mb-4">
                    <h1 class="text-primary text-4xl font-bold mb-2">HeRoes</h1>
                    <p class="text-surface-600 text-lg font-semibold">관리자 로그인</p>
                </div>

                <!-- 입력 폼 -->
                <form @submit.prevent="handleAdminLogin" class="space-y-6">
                    <!-- 사원번호 입력 -->
                    <div class="flex flex-col">
                        <label for="employeeId" class="text-lg font-semibold text-surface-900">사원번호</label>
                        <InputText type="text" id="employeeId" v-model="employeeId" placeholder="사원 번호를 입력해주세요" class="w-full mt-1 border border-surface-200 rounded-md focus:border-primary" />
                    </div>

                    <!-- 비밀번호 입력 -->
                    <div class="flex flex-col">
                        <label for="adminPassword" class="text-lg font-semibold text-surface-900">비밀번호</label>
                        <Password id="adminPassword" v-model="password" placeholder="비밀번호를 입력해주세요" :toggleMask="true" class="w-full mt-1 border border-surface-200 rounded-md focus:border-primary" fluid :feedback="false" />
                    </div>

                    <div class="flex items-center justify-start mt-4 gap-5">
                        <div class="flex items-center justify-center">
                            <InputOtp v-model="authCode" :length="6"></InputOtp>
                        </div>
                        <!-- 인증 코드 발급 버튼 -->
                        <Button type="button" @click="requestAuthCode" :disabled="isLoading" class="text-primary font-medium no-underline px-4 py-2 rounded-lg hover:text-white transition-all duration-150" :class="{ 'cursor-not-allowed': isLoading }">
                            {{ isLoading ? '발급 중...' : '인증코드 발급' }}
                        </Button>
                    </div>

                    <!-- 인증 코드 입력 필드 -->
                    <div class="mt-4"></div>

                    <!-- 인증 코드 남은 시간 안내 -->
                    <div v-if="timeRemaining > 0" class="text-center text-sm text-gray-500 mt-1">남은 시간: {{ Math.floor(timeRemaining / 60) }}분 {{ timeRemaining % 60 }}초</div>
                    <div v-else-if="timeRemaining === 0" class="text-center text-sm text-red-500 mt-1">사원 번호를 입력 후 인증코드를 발급해주세요.</div>

                    <!-- 로그인 버튼 -->
                    <div class="mt-6">
                        <Button type="submit" icon="pi pi-user" label="로그인" class="w-full p-4 text-lg font-semibold bg-primary text-white rounded-lg hover:bg-primary-600 transition-all duration-200" />
                    </div>

                    <!-- 일반 사원 로그인 링크 -->
                    <div class="text-center mt-4">
                        <span class="text-sm font-medium text-surface-600 cursor-pointer hover:text-primary" @click="goToLogin">사원 로그인</span>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script setup>
import axios from 'axios';
import InputOtp from 'primevue/inputotp';
import Swal from 'sweetalert2';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import adminService from '../auth/service/authService';

const employeeId = ref('');
const password = ref('');
const authCode = ref('');
const isLoading = ref(false); // 로딩 상태
const authCodeTimer = ref(null); // 타이머 상태
const timeRemaining = ref(0); // 남은 시간
const router = useRouter();

const handleAdminLogin = async () => {
    if (!employeeId.value || !password.value || !authCode.value) {
        await Swal.fire({
            title: '로그인 정보를 모두 입력해주세요.',
            icon: 'warning',
            confirmButtonText: '확인'
        });
        return;
    }

    if (timeRemaining.value <= 0) {
        await Swal.fire({
            title: '인증코드가 만료되었습니다.',
            text: '다시 인증코드를 요청해주세요.',
            icon: 'warning',
            confirmButtonText: '확인'
        });
        return;
    }

    try {
        const response = await adminService.adminLogin(employeeId.value, password.value, authCode.value);

        if (response.success) {
            await Swal.fire({
                title: '로그인에 성공했습니다!',
                icon: 'success',
                confirmButtonText: '확인'
            });
            router.push('/');
        } else {
            await Swal.fire({
                title: '로그인 실패',
                text: '사원번호, 비밀번호, 또는 인증코드를 확인해주세요.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        }
    } catch (error) {
        await Swal.fire({
            title: '로그인 중 오류가 발생했습니다.',
            text: '다시 시도해 주세요.',
            icon: 'error',
            confirmButtonText: '확인'
        });
    }
};

const requestAuthCode = async () => {
    if (!employeeId.value) {
        await Swal.fire({
            title: '사원번호를 입력해주세요',
            text: '다시 시도해 주세요.',
            icon: 'warning',
            confirmButtonText: '확인'
        });
        return;
    }

    isLoading.value = true;

    try {
        await axios.post('https://hq-heroes-api.com/mails/email', {
            employeeId: employeeId.value
        });

        Swal.fire({
            icon: 'success',
            title: '인증코드가 이메일로 전송되었습니다.',
            showConfirmButton: false,
            timer: 1000
        });

        startAuthCodeTimer();
    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: '인증코드 요청 실패: ' + (error.response?.data?.message || '알 수 없는 오류')
        });
    } finally {
        isLoading.value = false;
    }
};

const startAuthCodeTimer = () => {
    timeRemaining.value = 180;

    if (authCodeTimer.value) {
        clearInterval(authCodeTimer.value);
    }

    authCodeTimer.value = setInterval(() => {
        if (timeRemaining.value > 0) {
            timeRemaining.value -= 1;
        } else {
            clearInterval(authCodeTimer.value);
        }
    }, 1000);
};

const goToLogin = () => router.push('/login');
</script>
