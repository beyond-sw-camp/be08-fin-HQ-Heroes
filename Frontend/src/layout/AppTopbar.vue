<script setup>
import { useLayout } from '@/layout/composables/layout';
import 'primeicons/primeicons.css';
import { ref, onMounted, onUnmounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import authService from '../views/pages/auth/service/authService';
import router from '@/router';

const { onMenuToggle } = useLayout();

const authStore = useAuthStore();
const remainingTime = ref('');  // 남은 시간 표시를 위한 변수

let intervalId = null;  // setInterval을 관리할 변수

// 로그아웃 처리
const handleLogout = async () => {
  try {
    await authService.logout();
    authStore.logout();
    clearInterval(intervalId);  // 로그아웃 시 인터벌 정리
    router.push('/');
  } catch (err) {
    alert(err.message);
  }
};

const calculateRemainingTime = () => {
  const tokenExpiry = authStore.accessTime;  // 토큰 만료 시간을 가져옴 (밀리초 단위로 가정)
  const now = Date.now();  // 현재 시간을 밀리초 단위로 가져옴
  const diff = Math.floor((tokenExpiry - now) / (1000 * 60));  // 남은 시간(분) 계산

  console.log(tokenExpiry);
  remainingTime.value = diff > 0 ? `${diff}분 남음` : '만료됨';
};


// 토큰 갱신 처리
const refreshAccessToken = async () => {
  try {
    const newToken = await authService.refreshToken();  // 토큰 재갱신
    authStore.setAccessToken(newToken);  // 새로운 토큰 저장
    calculateRemainingTime();  // 갱신 후 남은 시간 다시 계산
  } catch (err) {
    alert('토큰 갱신 실패: ' + err.message);
  }
};

// 남은 시간 업데이트 주기 설정
onMounted(() => {
  if (authStore.isLoggedIn) {
    calculateRemainingTime();
    intervalId = setInterval(calculateRemainingTime, 60000);  // 1분마다 남은 시간 계산
  }
});

onUnmounted(() => {
  clearInterval(intervalId);  // 컴포넌트가 파괴될 때 인터벌 제거
});

const goToLogin = () => router.push('/login');
const goToSignUp = () => router.push('/signup');
</script>


<template>
  <div class="layout-topbar">
    <div class="layout-topbar-logo-container">
      <button class="layout-menu-button layout-topbar-action" @click="onMenuToggle">
        <i class="pi pi-bars"></i>
      </button>
      <router-link to="/" class="layout-topbar-logo">
        <span>HeRoes</span>
      </router-link>
    </div>

    <div class="layout-topbar-actions">
      <div v-if="authStore.isLoggedIn" class="token-info">
        <!-- 남은 시간 표시 -->
        <span>{{ remainingTime }}</span>

        <!-- 토큰 갱신 버튼 -->
        <button @click="refreshAccessToken" class="layout-topbar-action" v-tooltip.bottom="{
          value: '재갱신',
          pt: {
            arrow: {
              style: {
                borderBottomColor: 'var(--p-noir)'
              }
            },
            text: '!bg-primary !text-primary-contrast !font-medium'
          }
        }">
          <i class="pi pi-refresh"></i>
          <span>재갱신</span>
        </button>

        <button v-if="authStore.isLoggedIn" type="button" class="layout-topbar-action" v-tooltip.bottom="{
          value: '로그아웃',
          pt: {
            arrow: {
              style: {
                borderBottomColor: 'var(--p-noir)'
              }
            },
            text: '!bg-primary !text-primary-contrast !font-medium'
          }
        }" @click="handleLogout">
          <i class="pi pi-sign-out"></i>
          <span>로그아웃</span>
        </button>
      </div>



      <button v-if="!authStore.isLoggedIn" type="button" class="layout-topbar-action" v-tooltip.bottom="{
        value: '로그인',
        pt: {
          arrow: {
            borderBottomColor: 'var(--p-noir)'
          },
          text: '!bg-primary !text-primary-contrast !font-medium'
        }
      }" @click="goToLogin">
        <i class="pi pi-sign-in"></i>
        <span>로그인</span>
      </button>

      <button v-if="!authStore.isLoggedIn" type="button" class="layout-topbar-action" v-tooltip.bottom="{
        value: '회원가입',
        pt: {
          arrow: {
            style: {
              borderBottomColor: 'var(--p-noir)'
            }
          },
          text: '!bg-primary !text-primary-contrast !font-medium'
        }
      }" @click="goToSignUp">
        <i class="pi pi-user-plus"></i>
        <span>회원가입</span>
      </button>
    </div>
  </div>
</template>
