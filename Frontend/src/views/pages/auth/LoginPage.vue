<template>
    <div class="flex lg:flex-row flex-col bg-surface-0 min-h-screen overflow-hidden">
        <!-- 이미지 영역 -->
        <div class="flex-1 flex items-center justify-center">
            <div class="flex-1">
                <img src="/images/Heroesbackground.png" alt="background-image" class="h-screen w-screen object-cover lg:[clip-path:polygon(80%_100%,100%_0%,0%_0%,0%_100%)]" />
            </div>
        </div>

        <!-- 로그인 영역 -->
        <div class="flex-1 flex items-center justify-center transform scale-110 lg:transform-origin-left-center">
            <div class="relative flex flex-col items-center justify-center w-full max-w-md">
                <div class="text-center mb-4">
                    <div class="text-primary text-4xl font-bold mb-3">HeRoes</div>
                    <span class="text-surface-600 text-xl font-semibold">로그인</span>
                </div>

                <!-- 로그인 폼 -->
                <form @submit.prevent="handleLogin" class="space-y-5 w-full">
                    <!-- 사원 코드 입력 -->
                    <div class="flex flex-col">
                        <label for="employeeId" class="text-surface-900 font-semibold text-lg">사원번호</label>
                        <InputText type="text" id="employeeId" v-model="employeeId" placeholder="사원 번호를 입력해주세요" class="w-full border border-surface-200 rounded-md focus:border-primary" />
                    </div>

                    <!-- 비밀번호 입력 -->
                    <div class="flex flex-col">
                        <label for="password" class="text-surface-900 font-semibold text-lg">비밀번호</label>
                        <Password id="password" v-model="password" placeholder="비밀번호를 입력해주세요" :toggleMask="true" class="w-full border border-surface-200 rounded-md focus:border-primary" fluid :feedback="false" />
                    </div>

                    <!-- 비밀번호 재발급 링크 -->
                    <div class="flex items-center justify-end gap-1">
                        <a @click="goToResetPassword" class="text-sm text-surface-600 font-medium leading-normal cursor-pointer hover:text-primary">비밀번호 재발급</a>
                    </div>

                    <!-- 로그인 버튼 -->
                    <Button type="submit" label="로그인" icon="pi pi-user" class="w-full p-4 bg-primary text-white font-semibold rounded-lg hover:bg-primary-600 transition-all duration-200" />

                    <!-- 관리자 로그인 링크 -->
                    <div class="flex items-center justify-center mt-6">
                        <a @click="goToAdminLogin" class="text-sm text-surface-600 font-medium leading-normal cursor-pointer hover:text-primary">관리자 로그인</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script setup>
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../../stores/authStore';
import authService, { getLoginEmployeeInfo } from '../auth/service/authService';

const employeeId = ref('');
const password = ref('');

const router = useRouter();
const authStore = useAuthStore();

onMounted(() => {
    authStore.initializeAuth();
});

const goToResetPassword = () => router.push('/reset-password');
const goToAdminLogin = () => router.push('/admin-login');

const handleLogin = async () => {
    if (!employeeId.value || !password.value) {
        await Swal.fire({
            title: '사원번호와 비밀번호를 모두 입력해주세요.',
            icon: 'warning',
            confirmButtonText: '확인'
        });
        return;
    }

    try {
        const response = await authService.login(employeeId.value, password.value);
        if (response.success) {
            await Swal.fire({
                title: '로그인이 되었습니다.',
                icon: 'success',
                confirmButtonText: '확인'
            });
            router.push('/');
            const employeeData = await getLoginEmployeeInfo(employeeId.value);
            authStore.setEmployeeData(employeeData);
        } else {
            await Swal.fire({
                title: '로그인을 실패하였습니다.',
                text: '사원번호 또는 비밀번호가 잘못되었습니다.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        }
    } catch (err) {
        await Swal.fire({
            title: '로그인을 실패했습니다.',
            text: '다시 시도해 주세요.',
            icon: 'error',
            confirmButtonText: '확인'
        });
    }
};
</script>
