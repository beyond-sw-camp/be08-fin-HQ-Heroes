<template>
  <div class="login-page flex items-center justify-center min-h-screen bg-gray-100">
    <div class="login-container bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
      <div class="text-center mb-8">
        <div class="text-surface-900 dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
        <span class="text-muted-color font-medium">로그인</span>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-4">
        <div class="flex flex-col gap-2">
          <label for="employeeId" class="font-medium text-gray-700">사원 코드</label>
          <InputText type="text" id="employeeId" v-model="employeeId" placeholder="사원 코드를 입력해주세요"
            class="p-3 bg-gray-100 border border-gray-300 rounded-md" />
        </div>

        <div class="flex flex-col gap-2">
          <label for="password" class="font-medium text-gray-700">비밀번호</label>
          <Password id="password" v-model="password" placeholder="비밀번호를 입력해주세요." :toggleMask="true" class="mb-4" fluid
            :feedback="false"></Password>
        </div>

        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <input type="checkbox" id="rememberMe" v-model="checked" class="cursor-pointer" />
            <label for="rememberMe" class="text-gray-700 cursor-pointer">내 정보 기억하기</label>
          </div>
          <a href="#" @click="goToResetPassword" class="text-sm text-gray-500 hover:text-primary">비밀번호 재발급</a>
        </div>

        <Button type="submit" class="w-full p-3 bg-blue-500 text-white rounded-md hover:bg-blue-600">
          로그인
        </Button>

        <div class="flex flex-col items-center gap-4 mt-4">
          <a @click="goToSignUp" class="text-sm text-gray-500 hover:text-primary cursor-pointer">회원가입</a>
          <a @click="goToAdminLogin" class="text-sm text-gray-500 hover:text-primary cursor-pointer">관리자 로그인</a>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'; // onMounted 추가
import { useRouter } from 'vue-router';
import authService from '../auth/service/authService';
import { useAuthStore } from '../../../stores/authStore';

const employeeId = ref('');
const password = ref('');
const checked = ref(false);

const router = useRouter();
const authStore = useAuthStore();

// 컴포넌트가 마운트될 때 initializeAuth 호출
onMounted(() => {
  authStore.initializeAuth();
});

const goToSignUp = () => router.push('/signup');
const goToResetPassword = () => router.push('/reset-password');
const goToAdminLogin = () => router.push('/admin-login');

const handleLogin = async () => {
  try {
    const response = await authService.login(employeeId.value, password.value);
    if (response.success) {
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
</style>
