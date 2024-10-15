<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';
import router from '@/router';
import adminService from '../auth/service/authService';

const employeeId = ref('');
const password = ref('');
const authCode = ref('');
const isLoading = ref(false); // 로딩 상태
const authCodeTimer = ref(null); // 타이머 상태
const timeRemaining = ref(0); // 남은 시간

const handleAdminLogin = async () => {
  if (!employeeId.value || !password.value || !authCode.value) {
    alert('로그인 정보를 입력해주세요.');
    return;
  }

  if (timeRemaining.value <= 0) {
    alert('인증코드가 만료되었습니다. 다시 요청해주세요.');
    return;
  }

  try {
    const response = await adminService.adminLogin(employeeId.value, password.value, authCode.value);

    if (response.success) {
      alert('로그인 성공');
      router.push('/'); // 메인으로 이동
    }
  } catch (error) {
    alert('로그인 실패: ' + error.message);
  }
};

const requestAuthCode = async () => {
  if (!employeeId.value) {
    alert('사원 번호를 입력해주세요.');
    return;
  }

  isLoading.value = true; // 로딩 시작

  try {
    await axios.post('http://localhost:8080/mails/email', {
      employeeId: employeeId.value
    });

    alert('인증코드가 이메일로 전송되었습니다.');
    startAuthCodeTimer(); // 타이머 시작
  } catch (error) {
    alert('인증코드 요청 실패: ' + (error.response?.data?.message || '알 수 없는 오류'));
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

<template>
  <div class="relative flex items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-950">
    <div
      class="relative z-10 flex flex-col items-center justify-center bg-surface-0 dark:bg-surface-900 p-8 sm:p-20 rounded-lg shadow-lg">
      <div class="text-center mb-8">
        <div class="text-surface-900 dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
        <span class="text-muted-color font-medium">관리자 로그인</span>
      </div>
      <div>
        <label for="employeeId" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">사원
          번호</label>
        <InputText id="employeeId" type="text" placeholder="사원 번호를 입력해주세요." class="w-full md:w-[30rem] mb-4"
          v-model="employeeId" />

        <label for="adminPassword"
          class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">비밀번호</label>
        <Password id="adminPassword" v-model="password" placeholder="비밀번호를 입력해주세요." :toggleMask="true" class="mb-4"
          fluid :feedback="false"></Password>

        <div class="flex flex-row items-center justify-center">
          <label for="adminNo" class="block text-surface-900 dark:text-surface-0 text-xl font-medium">인증코드</label>
          <span class="font-medium no-underline text-right cursor-pointer hover:text-primary ml-auto"
            @click="requestAuthCode" :disabled="isLoading">{{ isLoading ? '발급 중...' : '인증코드 발급' }}</span>
        </div>
        <InputOtp class="mb-4 mt-2" v-model="authCode" :length="6"></InputOtp>

        <div v-if="timeRemaining > 0" class="text-xxl text-muted">남은 시간: {{ Math.floor(timeRemaining / 60) }}분 {{
          timeRemaining % 60 }}초</div>
        <div v-else-if="timeRemaining === 0" class="text-xxl text-red-500">사원 번호와 비밀번호를 입력 후 인증코드를 발급해주세요.</div>

        <div class="flex flex-col items-center">
          <Button label="Login" class="w-full mt-3" @click="handleAdminLogin"></Button>
          <span class="font-medium no-underline mt-3 text-right cursor-pointer hover:text-primary" @click="goToLogin">사원
            로그인</span>
        </div>
      </div>
    </div>
  </div>
</template>
