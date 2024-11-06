<script setup>
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';

const authStore = useAuthStore();

onMounted(() => {
  authStore.initializeAuth();
});
</script>

<template>
  <router-view />
</template>

<!-- 젠킨스 테스트 21 -->
<style scoped></style>
