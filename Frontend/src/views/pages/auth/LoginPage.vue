<template>
    <div class="flex lg:flex-row flex-col bg-surface-0 min-h-screen">
        <div class="flex-1 flex items-center justify-center">
            <div class="bg-surface-0 px-6 py-20 md:px-12 lg:px-20 min-h-screen flex items-center justify-center">
                <div class="bg-surface-0 p-6">
                    <div class="text-center mb-8">
                    <!-- FontAwesome 아이콘 사용 -->
                    <font-awesome-icon icon="user" class="mb-4 text-dark" style="font-size: 4rem" />

                    <div class="text-surface-900 text-3xl font-medium mb-4">HeRoes</div>
                    <span class="text-surface-600 font-semibold leading-normal">로그인</span>
                </div>

                    <form @submit.prevent="handleLogin" class="space-y-3">
                        <!-- 사원 코드 입력 -->
                        <div class="flex flex-1 gap-2 items-center">
                            <label for="employeeId" class="text-surface-900 font-semibold block">사원번호</label>
                            <InputText type="text" id="employeeId" v-model="employeeId" placeholder="사원 번호를 입력해주세요" class="flex-1" />
                        </div>

                        <!-- 비밀번호 입력 -->
                        <div class="flex flex-1 gap-2 items-center">
                            <label for="password" class="text-surface-900 font-semibold block">비밀번호</label>
                            <Password id="password" v-model="password" placeholder="비밀번호를 입력해주세요" :toggleMask="true" class="flex-1" fluid :feedback="false" />
                        </div>

                        <!-- 기억하기 체크박스 및 비밀번호 재발급 링크 -->
                        <div class="flex items-center justify-between">
                            <a @click="goToResetPassword" class="font-medium no-underline text-surface-600 cursor-pointer hover:text-primary">비밀번호 재발급</a>
                        </div>

                        <!-- 로그인 버튼 -->
                        <Button type="submit" label="로그인" icon="pi pi-user" class="w-full p-3 bg-primary text-white rounded-md hover:bg-primary-600" style="width: 20rem;" />

                        <!-- 회원가입 및 관리자 로그인 -->
                        <div class="flex flex-col items-center gap-2">
                            <a @click="goToSignUp" class="text-sm text-surface-600 font-medium leading-normal cursor-pointer hover:text-primary">회원가입</a>
                            <a @click="goToAdminLogin" class="text-sm text-surface-600 font-medium leading-normal cursor-pointer hover:text-primary">관리자 로그인</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="flex-1 overflow-hidden">
            <img src="https://fqjltiegiezfetthbags.supabase.co/storage/v1/render/image/public/block.images/blocks/hero/hero-1.png" alt="hero-1" class="h-screen w-screen object-cover lg:[clip-path:polygon(12%_0,100%_0%,100%_100%,0_100%)]" />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import authService, { getLoginEmployeeInfo } from '../auth/service/authService';
import { useAuthStore } from '../../../stores/authStore';

const employeeId = ref('');
const password = ref('');

const router = useRouter();
const authStore = useAuthStore();

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
            authStore.setEmployeeData(getLoginEmployeeInfo(employeeId));
        } else {
            alert('로그인 실패. 다시 시도해주세요.');
        }
    } catch (err) {
        alert(err.message);
    }
};
</script>
