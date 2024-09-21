<script setup>
import router from '@/router';
import { ref } from 'vue';

const employeeName = ref('');
const email = ref('');
const password = ref('');
const joinDate = ref('');
const role = ref('EMPLOYEE'); // 기본값으로 EMPLOYEE 설정

function registerEmployee() {
    // 회원가입 폼 데이터를 서버로 보내 사용자 등록 처리
    alert("회원가입이 완료되었습니다.");
    router.push("/")
}
</script>

<template>
    <div class="relative flex items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-950">
        <div class="relative z-10 flex flex-col items-center justify-center bg-surface-0 dark:bg-surface-900 p-8 sm:p-20 rounded-lg shadow-lg">
            <div class="text-center mb-4">
                <div class="text-surface-900 dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
                <span class="text-muted-color font-medium">회원가입</span>
            </div>
            
            <div class="flex flex-col">
                <label for="employeeName" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">이름</label>
                <InputText id="employeeName" type="text" placeholder="이름을 입력해주세요." class="w-full md:w-[30rem] mb-4" v-model="employeeName" />

                <label for="email" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">이메일</label>
                <InputText id="email" type="email" placeholder="이메일을 입력해주세요." class="w-full md:w-[30rem] mb-4" v-model="email" />

                <label for="password" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">비밀번호</label>
                <Password id="password" v-model="password" placeholder="비밀번호를 입력해주세요." :toggleMask="true" class="md:w-[30rem] mb-4" fluid :feedback="false"></Password>

                <label for="joinDate" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">입사일</label>
                <InputText id="joinDate" type="date" class="w-full md:w-[30rem] mb-4" v-model="joinDate" />

                <label for="role" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">역할</label>
                <Dropdown id="role" :options="['EMPLOYEE', 'ADMIN']" v-model="role" class="w-full md:w-[30rem] mb-8" />

                <Button label="회원가입" class="w-full md:w-[30rem]" @click="registerEmployee"></Button>
            </div>
        </div>
    </div>
</template>