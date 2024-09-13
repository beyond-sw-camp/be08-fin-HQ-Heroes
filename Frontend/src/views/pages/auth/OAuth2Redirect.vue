<!-- src/components/OAuth2Redirect.vue -->
<template>
  <div>
    <!-- 로딩 상태 또는 리다이렉션 처리 중일 때 보여줄 내용 -->
    <p>처리 중...</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const OAuth2JwtHeaderFetch = async () => {
  try {
    const response = await fetch("http://localhost:31001/oauth2-jwt-header", {
      method: "POST",
      credentials: "include",
    });

    if (response.ok) {
      const accessToken = response.headers.get("access");
      window.localStorage.setItem("access", accessToken);

      const email = route.query.email || '';
      window.localStorage.setItem("email", email);

      authStore.setAccessToken(accessToken);
      authStore.setLoginUser(email);
      authStore.setIsLoggedIn(true);

      console.log('성공');
      router.replace('/');
    } else {
      alert('접근할 수 없는 페이지입니다.');
      router.replace('/');
    }
  } catch (error) {
    console.log("error: ", error);
    router.replace('/');
  }
};

onMounted(() => {
  OAuth2JwtHeaderFetch();
});
</script>
