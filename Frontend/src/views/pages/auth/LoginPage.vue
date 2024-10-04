<template>
    <div class="login-page flex items-center justify-center min-h-screen bg-gray-100">
      <div class="login-container bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h1 class="text-2xl font-bold mb-4 text-center text-gray-800">로그인</h1>
  
        <form @submit.prevent="handleSignIn" class="space-y-4">
          <div class="flex flex-col gap-2">
            <label for="employeeId" class="font-medium text-gray-700">사원 코드</label>
            <input
              type="text"
              id="employeeId"
              v-model="employeeId"
              placeholder="사원 코드를 입력해주세요"
              class="p-3 bg-gray-100 border border-gray-300 rounded-md"
            />
          </div>
  
          <div class="flex flex-col gap-2">
            <label for="password" class="font-medium text-gray-700">비밀번호</label>
            <input
              type="password"
              id="password"
              v-model="password"
              placeholder="비밀번호를 입력해주세요"
              class="p-3 bg-gray-100 border border-gray-300 rounded-md"
            />
          </div>
  
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <input type="checkbox" id="rememberMe" v-model="checked" class="cursor-pointer" />
              <label for="rememberMe" class="text-gray-700 cursor-pointer">내 정보 기억하기</label>
            </div>
            <a href="#" @click="goToResetPassword" class="text-sm text-gray-500 hover:text-gray-800">비밀번호 재발급</a>
          </div>
  
          <button type="submit" class="w-full p-3 bg-blue-500 text-white rounded-md hover:bg-blue-600">
            로그인
          </button>
  
          <div class="flex flex-col items-center gap-4 mt-4">
            <a @click="goToSignUp" class="text-sm text-gray-500 hover:text-gray-800">회원가입</a>
            <a @click="goToAdminLogin" class="text-sm text-gray-500 hover:text-gray-800">관리자 로그인</a>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import authService from '../auth/service/authService';
  import { useAuthStore } from '../../../stores/authStore';
  
  const employeeId = ref('');
  const password = ref('');
  const checked = ref(false);
  
  const router = useRouter();
  const authStore = useAuthStore();
  
  const goToSignUp = () => router.push('/signup');
  const goToResetPassword = () => router.push('/reset-password');
  const goToAdminLogin = () => router.push('/admin-login');
  
  const handleSignIn = async () => {
    try {
      const response = await authService.login(employeeId.value, password.value);
      if (response.success) {
        authStore.setIsLoggedIn(true);
        authStore.setLoginUser(response.employeeId);
        authStore.setAccessToken(response.accessToken);
  
        console.log('로그인 상태:', authStore.isLoggedIn);
        console.log('사원 코드:', authStore.employeeId);
  
        alert('로그인 성공!');
        router.push('/');
      } else {
        alert('로그인 실패. 다시 시도해주세요.');
      }
    } catch (err) {
      alert(err.message);
    }
  };
  </script>
  
  <style scoped>
  .login-page {
    background-color: #f9fafb;
  }
  
  .login-container {
    background-color: #fff;
    border-radius: 0.5rem;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  input[type="text"],
  input[type="password"] {
    width: 100%;
  }
  
  button {
    background-color: #3b82f6;
    transition: background-color 0.3s ease;
  }
  
  button:hover {
    background-color: #2563eb;
  }
  </style>
  