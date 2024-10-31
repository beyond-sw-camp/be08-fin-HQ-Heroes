<template>
    <div class="flex lg:flex-row flex-col bg-surface-0 min-h-screen">
        <div class="flex-1 flex items-center justify-center">
            <div class="flex-1">
                <img src="https://media.discordapp.net/attachments/1279791869991059579/1301515878369857592/architecture-1048092_640.jpg?ex=6724c29e&is=6723711e&hm=aec119ec64cd78a25dde5094d49032c3e3326f5dbc9f942d2dce967e5283fcb8&=&format=webp&width=800&height=600" alt="hero-1" class="h-screen w-screen object-cover lg:[clip-path:polygon(80%_100%,100%_0%,0%_0%,0%_100%)]" />
            </div>
        </div>
        <div class="flex-1 flex items-center justify-center">
            <div class="relative flex flex-col items-center justify-center min-h-screen">
                <div class="text-center mb-4">
                    <div class="text-surface-900 text-3xl font-medium mb-4">HeRoes</div>
                    <span class="text-surface-600 font-semibold leading-normal">로그인</span>
                </div>

                <form @submit.prevent="handleLogin" class="space-y-4">
                    <!-- 사원 코드 입력 -->
                    <div class="flex gap-2 items-center">
                        <label for="employeeId" class="text-surface-900 font-semibold block">사원번호</label>
                        <InputText type="text" id="employeeId" v-model="employeeId" placeholder="사원 번호를 입력해주세요" class="flex-1" />
                    </div>

                    <!-- 비밀번호 입력 -->
                    <div class="flex gap-2 items-center">
                        <label for="password" class="text-surface-900 font-semibold block">비밀번호</label>
                        <Password id="password" v-model="password" placeholder="비밀번호를 입력해주세요" :toggleMask="true" class="flex-1" fluid :feedback="false" />
                    </div>

                    <!-- 비밀번호 재발급 링크 -->
                    <div class="flex items-center justify-end gap-2">
                        <a @click="goToSignUp" class="text-sm text-surface-600 font-medium leading-normal cursor-pointer hover:text-primary">회원가입</a>
                        <span class="cursor-auto">/</span>
                        <a @click="goToResetPassword" class="text-sm font-medium no-underline text-surface-600 cursor-pointer hover:text-primary">비밀번호 재발급</a>
                    </div>

                    <!-- 로그인 버튼 -->
                    <Button type="submit" label="로그인" icon="pi pi-user" class="w-full p-3 bg-primary text-white rounded-md hover:bg-primary-600" style="width: 20rem" />

                    <!-- 회원가입 및 관리자 로그인 -->
                    <div class="flex flex-col items-center">
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

const goToSignUp = () => router.push('/signup');
const goToResetPassword = () => router.push('/reset-password');
const goToAdminLogin = () => router.push('/admin-login');

const handleLogin = async () => {
    // 입력값이 비어있는지 확인
    if (!employeeId.value || !password.value) {
        await Swal.fire({
            title: '사원번호와 비밀번호를 모두 입력해주세요.',
            icon: 'warning',
            confirmButtonText: '확인'
        });
        return; // 함수 종료
    }

    try {
        const response = await authService.login(employeeId.value, password.value);
        if (response.success) {
            await Swal.fire({
                title: '로그인이 되었습니다.',
                icon: 'success', // 성공 메시지에 적합한 아이콘 설정
                confirmButtonText: '확인'
            });
            router.push('/');
            const employeeData = await getLoginEmployeeInfo(employeeId.value);
            authStore.setEmployeeData(employeeData); // 로그인한 사용자 정보 설정
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
