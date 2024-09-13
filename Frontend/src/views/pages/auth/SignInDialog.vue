<template>
  <Dialog :visible="visible" pt:root:class="!border-0 !bg-transparent" pt:mask:class="backdrop-blur-md"
    :style="{ width: '20vw' }">
    <template #container="{ closeCallback }">
      <div class="flex flex-col px-8 py-6 gap-3 rounded-xl shadow-lg !bg-white">
        <h1 class="!text-gray-800 text-2xl font-semibold">로그인</h1>
        <Divider />

        <form @submit.prevent="handleSignIn">
          <div class="inline-flex flex-col gap-2  w-full">
            <label for="email" class="!text-gray-800 font-medium">이메일</label>
            <InputText v-model="email"
              class="!bg-gray-100 !border-0 !p-3 !text-gray-800 rounded-md focus:!ring-2 focus:!ring-gray-400"
              placeholder="Email">
            </InputText>
          </div>

          <div class="inline-flex flex-col gap-2 w-full mb-4">
            <label for="password" class="!text-gray-800 font-medium">비밀번호</label>
            <InputText v-model="password"
              class="!bg-gray-100 !border-0 !p-3 !text-gray-800 rounded-md focus:!ring-2 focus:!ring-gray-400"
              type="password" placeholder="Password"></InputText>
          </div>

          <div class="flex flex-col items-center gap-4">
            <div class="flex items-center gap-2 w-full">
              <Button type="submit" label="로그인" text
                class="!text-gray-800 !p-3 w-full !border !border-gray-300 hover:!bg-gray-200 rounded-md"></Button>
              <Button label="취소" @click="closeDialog" text
                class="!text-gray-800 !p-3 w-full !border !border-gray-300 hover:!bg-gray-200 rounded-md"></Button>
            </div>

            <div class="flex flex-col items-center gap-2">
              <a href="#" class="text-gray-400 text-sm hover:text-gray-800">회원가입</a>
              <a href="#" class="text-gray-400 text-sm hover:text-gray-800">비밀번호 찾기</a>
              <a @click="naverLogin" class="text-gray-400 text-sm hover:text-gray-800">네이버 로그인</a>
              <a @click="googleLogin" class="text-gray-400 text-sm hover:text-gray-800">구글 로그인</a>
            </div>
          </div>
        </form>
      </div>
    </template>
  </Dialog>
</template>

<script setup>
import { ref } from 'vue';
import authService from '@/service/authService';
import { useAuthStore } from '@/stores/authStore';

const email = ref('');
const password = ref('');

const props = defineProps({
  visible: Boolean,
});

const emit = defineEmits(['update:visible']);

const authStore = useAuthStore();

const closeDialog = () => {
  emit('update:visible', false);
};

const handleSignIn = async () => {
  try {
    const response = await authService.login(email.value, password.value);
    if (response.success) {

      authStore.setIsLoggedIn(true);
      authStore.setLoginUser(response.email);
      authStore.setAccessToken(window.localStorage.getItem('access'));

      console.log(authStore.isLoggedIn);
      console.log(authStore.userEmail);

      closeDialog();
      alert('로그인 성공!');
    } else {
      alert('로그인 실패. 다시 시도해주세요.');
    }
  } catch (err) {
    alert(err.message);
  }
};

const naverLogin = () => {
  authService.onNaverLogin();
};

const googleLogin = () => {
  authService.onGoogleLogin();
};
</script>
