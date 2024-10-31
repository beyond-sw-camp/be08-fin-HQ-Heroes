<template>
    <div class="relative flex items-center justify-center bg-surface-0">
        <div class="flex-1 flex items-center justify-center">
            <div class="flex-1">
                <img src="https://media.discordapp.net/attachments/1279791869991059579/1301515878369857592/architecture-1048092_640.jpg?ex=6724c29e&is=6723711e&hm=aec119ec64cd78a25dde5094d49032c3e3326f5dbc9f942d2dce967e5283fcb8&=&format=webp&width=800&height=600" alt="hero-1" class="h-screen w-screen object-cover lg:[clip-path:polygon(80%_100%,100%_0%,0%_0%,0%_100%)]" />
            </div>
        </div>
        <div class="flex-1 flex items-center justify-center">
            <div class="relative flex flex-col items-center justify-center min-h-screen">
                <div class="text-center mb-8">
                    <div class="text-red-600 text-3xl font-medium mb-4">HeRoes</div>
                    <span class="text-red-600 font-semibold leading-normal">관리자 로그인</span>
                </div>

                <div class="flex flex-col gap-3">
                    <div class="flex flex-1 gap-2 items-center">
                        <label for="employeeId" class="text-surface-900 font-semibold block">사원번호</label>
                        <InputText type="text" id="employeeId" v-model="employeeId" placeholder="사원 번호를 입력해주세요" class="flex-1" />
                    </div>
                    <div class="flex flex-1 gap-2 items-center">
                        <label for="adminPassword" class="text-surface-900 font-semibold block">비밀번호</label>
                        <Password id="adminPassword" v-model="password" placeholder="비밀번호를 입력해주세요" :toggleMask="true" class="flex-1" fluid :feedback="false" />
                    </div>
                    <div class="flex flex-row items-center justify-center">
                        <label for="adminNo" class="text-surface-900 font-semibold text-base block">인증코드</label>
                        <span class="font-medium no-underline text-sm text-right cursor-pointer hover:text-primary ml-auto" @click="requestAuthCode" :disabled="isLoading">{{ isLoading ? '발급 중...' : '인증코드 발급' }}</span>
                    </div>

                    <div v-if="timeRemaining > 0" class="flex items-center justify-center">
                        <InputOtp v-model="authCode" :length="6"></InputOtp>
                    </div>

                    <div v-if="timeRemaining > 0" class="text-xxl text-muted">남은 시간: {{ Math.floor(timeRemaining / 60) }}분 {{ timeRemaining % 60 }}초</div>
                    <div v-else-if="timeRemaining === 0" class="text-sm text-red-500">사원 번호를 입력 후 인증코드를 발급해주세요.</div>

                    <div class="flex flex-col items-center">
                        <Button icon="pi pi-user" label="로그인" class="w-full" @click="handleAdminLogin" style="width: 20rem"></Button>
                        <span class="font-medium no-underline mt-3 text-sm cursor-pointer hover:text-primary" @click="goToLogin">사원 로그인</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import router from '@/router';
import axios from 'axios';
import Swal from 'sweetalert2';
import { ref } from 'vue';
import adminService from '../auth/service/authService';

const employeeId = ref('');
const password = ref('');
const authCode = ref('');
const isLoading = ref(false); // 로딩 상태
const authCodeTimer = ref(null); // 타이머 상태
const timeRemaining = ref(0); // 남은 시간

const handleAdminLogin = async () => {
    // 필수 입력값이 모두 입력되었는지 확인
    if (!employeeId.value || !password.value || !authCode.value) {
        await Swal.fire({
            title: '로그인 정보를 모두 입력해주세요.',
            icon: 'warning',
            confirmButtonText: '확인'
        });
        return; // 함수 종료
    }

    // 인증코드가 만료되었는지 확인
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

        // 로그인 성공 시 알림
        if (response.success) {
            await Swal.fire({
                title: '로그인에 성공했습니다!',
                icon: 'success',
                confirmButtonText: '확인'
            });
            router.push('/'); // 메인 페이지로 이동
        } else {
            // 로그인 실패 시 알림
            await Swal.fire({
                title: '로그인 실패',
                text: '사원번호, 비밀번호, 또는 인증코드를 확인해주세요.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        }
    } catch (error) {
        // 예외 발생 시 알림
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

    isLoading.value = true; // 로딩 시작

    try {
        await axios.post('http://localhost:8080/mails/email', {
            employeeId: employeeId.value
        });

        Swal.fire({
            icon: 'success',
            title: '인증코드가 이메일로 전송되었습니다.',
            showConfirmButton: false,
            timer: 1000
        });

        startAuthCodeTimer(); // 타이머 시작
    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: '인증코드 요청 실패: ' + (error.response?.data?.message || '알 수 없는 오류')
        });
    } finally {
        isLoading.value = false; // 로딩 종료
    }
};

const startAuthCodeTimer = () => {
    timeRemaining.value = 180; // 3분 (180초)

    if (authCodeTimer.value) {
        clearInterval(authCodeTimer.value); // 기존 타이머 초기화
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
