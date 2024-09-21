<script setup>
import { useLayout } from '@/layout/composables/layout';
import 'primeicons/primeicons.css';
import { ref } from 'vue';
import SignInDialog from '@/views/pages/auth/SignInDialog.vue';
import SignUpDialog from '@/views/pages/auth/SignUpDialog.vue';
import { useAuthStore } from '@/stores/authStore';
import authService from '@/service/authService';
import router from '@/router';

const { onMenuToggle, toggleDarkMode, isDarkTheme } = useLayout();

const displaySignInDialog = ref(false);
const displaySignUpDialog = ref(false);

const toggleSingInDialog = () => (displaySignInDialog.value = !displaySignInDialog.value);
const toggleSingUpDialog = () => (displaySignUpDialog.value = !displaySignUpDialog.value);

const authStore = useAuthStore();

const handleLogout = async () => {
    try {
        await authService.logout();
        authStore.logout();
        router.push('/');
    } catch (err) {
        alert(err.message);
    }
};

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
                <!-- 로고 및 기타 UI 코드 생략 -->
            </router-link>
        </div>

        <div class="layout-topbar-actions">
            <div class="layout-config-menu">
                <button type="button" class="layout-topbar-action" @click="toggleDarkMode">
                    <i :class="['pi', { 'pi-moon': isDarkTheme, 'pi-sun': !isDarkTheme }]"></i>
                </button>
            </div>

            <button
                class="layout-topbar-menu-button layout-topbar-action"
                v-styleclass="{ selector: '@next', enterFromClass: 'hidden', enterActiveClass: 'animate-scalein', leaveToClass: 'hidden', leaveActiveClass: 'animate-fadeout', hideOnOutsideClick: true }"
            >
                <i class="pi pi-ellipsis-v"></i>
            </button>

            <div class="layout-topbar-menu hidden lg:block">
                <div class="layout-topbar-menu-content">
                    <button
                        v-if="!authStore.isLoggedIn"
                        type="button"
                        class="layout-topbar-action"
                        v-tooltip.bottom="{
                            value: '로그인',
                            pt: {
                                arrow: {
                                    borderBottomColor: 'var(--p-noir)'
                                },
                                text: '!bg-primary !text-primary-contrast !font-medium'
                            }
                        }"
                        @click="goToLogin"
                    >
                        <i class="pi pi-sign-in"></i>
                        <span>로그인</span>
                    </button>

                    <button
                        v-if="authStore.isLoggedIn"
                        type="button"
                        class="layout-topbar-action"
                        v-tooltip.bottom="{
                            value: '로그아웃',
                            pt: {
                                arrow: {
                                    style: {
                                        borderBottomColor: 'var(--p-noir)'
                                    }
                                },
                                text: '!bg-primary !text-primary-contrast !font-medium'
                            }
                        }"
                        @click="handleLogout"
                    >
                        <i class="pi pi-sign-out"></i>
                        <span>로그아웃</span>
                    </button>

                    <button
                        v-if="!authStore.isLoggedIn"
                        type="button"
                        class="layout-topbar-action"
                        v-tooltip.bottom="{
                            value: '회원가입',
                            pt: {
                                arrow: {
                                    style: {
                                        borderBottomColor: 'var(--p-noir)'
                                    }
                                },
                                text: '!bg-primary !text-primary-contrast !font-medium'
                            }
                        }"
                        @click="goToSignUp"
                    >
                        <i class="pi pi-user-plus"></i>
                        <span>회원가입</span>
                    </button>
                </div>
            </div>
        </div>
    </div>
    <!-- 사용자 인증 Modal -->
    <SignInDialog :visible="displaySignInDialog" @update:visible="displaySignInDialog = $event" />
    <SignUpDialog :visible="displaySignUpDialog" @update:visible="displaySignUpDialog = $event" />
</template>
