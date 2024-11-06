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

<style scoped></style>