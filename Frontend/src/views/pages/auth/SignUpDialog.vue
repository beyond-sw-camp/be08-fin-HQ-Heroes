<template>
  <Dialog :visible="visible" pt:root:class="!border-0 !bg-transparent" pt:mask:class="backdrop-blur-md"
    :style="{ width: '25vw' }">
    <template #container="{ closeCallback }">
      <div class="flex flex-col px-8 py-6 gap-4 rounded-xl shadow-lg bg-white">
        <h1 class="text-gray-800 text-2xl font-semibold">회원가입</h1>
        <Divider />

        <form @submit.prevent="handleRegister">
          <div class="inline-flex flex-col gap-2 w-full">
            <label for="email" class="text-gray-700 font-medium">이메일</label>
            <InputText v-model="email" id="email"
              class="!bg-gray-100 !border-0 !p-3 !text-gray-800 rounded-md focus:!ring-2 focus:!ring-gray-400"
              placeholder="Email">
            </InputText>
          </div>

          <div class="inline-flex flex-col gap-2 w-full">
            <label for="email" class="text-gray-700 font-medium">이름</label>
            <InputText v-model="name" id="name"
              class="!bg-gray-100 !border-0 !p-3 !text-gray-800 rounded-md focus:!ring-2 focus:!ring-gray-400"
              placeholder="name">
            </InputText>
          </div>

          <div class="inline-flex flex-col gap-2 w-full">
            <label for="password" class="text-gray-700 font-medium">비밀번호</label>
            <InputText v-model="password" id="password"
              class="!bg-gray-100 !border-0 !p-3 !text-gray-800 rounded-md focus:!ring-2 focus:!ring-gray-400"
              type="password" placeholder="Password"></InputText>
          </div>

          <div class="inline-flex flex-col gap-2 w-full mb-4">
            <label for="confirmPassword" class="text-gray-700 font-medium">비밀번호 확인</label>
            <InputText v-model="confirmPassword" id="confirmPassword"
              class="!bg-gray-100 !border-0 !p-3 !text-gray-800 rounded-md focus:!ring-2 focus:!ring-gray-400"
              type="password" placeholder="Check the password"></InputText>
          </div>

          <div class="flex flex-col items-center gap-4">
            <div class="flex items-center gap-2 w-full">
              <Button type="submit" label="회원가입" text
                class="!p-3 w-full !text-gray-800 !border !border-gray-300 hover:!bg-gray-200 rounded-md"></Button>
              <Button label="취소" @click="closeDialog" text
                class="!p-3 w-full !text-gray-800 !border !border-gray-300 hover:!bg-gray-200 rounded-md"></Button>
            </div>

            <div class="flex flex-col items-center gap-2">
            </div>
          </div>
        </form>
      </div>
    </template>
  </Dialog>
</template>

<script setup>
import authService from '@/service/authService';
import { ref } from 'vue';
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  visible: Boolean,
});

const emit = defineEmits(['update:visible']);

const closeDialog = () => {
  emit('update:visible', false);
};

const email = ref('');
const name = ref('');
const password = ref('');
const confirmPassword = ref('');
const error = ref('');

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    error.value = '비밀번호가 일치하지 않습니다.';
    return;
  }

  try {
    const response = await authService.register(email.value, name.value, password.value);

    if (response.success) {
      closeDialog();
      alert('회원가입 성공!');
    } else {
      error.value = response.message;
    }
  } catch (err) {
    error.value = err.message;
  }
};
</script>