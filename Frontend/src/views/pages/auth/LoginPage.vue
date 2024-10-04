<script setup>
import { ref } from 'vue';
import router from '@/router';

const employeeCode = ref('');
const password = ref('');
const checked = ref(false);

const goToResetPassword = () => router.push('/reset-password');
const goToSignUp = () => router.push('/signup');
const goToAdminLogin = () => router.push('/admin-login')

const handleLogin = () => {
    if (!employeeCode.value || !password.value) {
        alert("로그인 정보를 입력해주세요.");
        return;
    }

    alert("로그인 성공");
    router.push('/');
};

</script>

<template>
    <div class="relative flex items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-950">

        <div class="relative z-10 flex flex-col items-center justify-center bg-surface-0 dark:bg-surface-900 p-8 sm:p-20 rounded-lg shadow-lg">
            <div class="text-center mb-8">
                <div class="text-surface-900 dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
                <span class="text-muted-color font-medium">로그인</span>
            </div>

            <div>
                <label for="employeeCode" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">사원 코드</label>
                <InputText id="employeeCode" type="text" placeholder="사원 코드를 입력해주세요." class="w-full md:w-[30rem] mb-8" v-model="employeeCode" />

                <label for="password" class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">비밀번호</label>
                <Password id="password" v-model="password" placeholder="비밀번호를 입력해주세요." :toggleMask="true" class="mb-4" fluid :feedback="false"></Password>

                <div class="flex items-center justify-between mt-2 mb-8 gap-8">
                    <div class="flex items-center">
                        <Checkbox v-model="checked" id="rememberme1" binary class="mr-2"></Checkbox>
                        <label for="rememberme1">내 정보 기억하기</label>
                    </div>
                    <div class="flex flex-row items-end">
                        <span class="font-medium no-underline ml-2 text-right cursor-pointer text-primary" @click="goToSignUp">회원가입</span>
                        <span class="font-medium no-underline ml-2 text-right cursor-pointer text-primary" @click="goToResetPassword">비밀번호 재발급</span>
                    </div>
                </div>

                <div class="flex items-center flex-col">
                    <Button label="Login" class="w-full" @click="handleLogin"></Button>
                    <span class="font-medium no-underline mt-2 text-right cursor-pointer text-primary" @click="goToAdminLogin">관리자 로그인</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 기본 스타일 */
.pi-eye {
    transform: scale(1.6);
    margin-right: 1rem;
}

.pi-eye-slash {
    transform: scale(1.6);
    margin-right: 1rem;
}

/* 배경으로 들어간 SVG의 크기를 전체화면에 맞춤 */
.absolute {
    width: 100%;
    height: 100%;
    overflow: hidden;
}

/* 로그인 폼을 배경 위에 오버레이 */
.relative.z-10 {
    z-index: 10;
}
</style>
