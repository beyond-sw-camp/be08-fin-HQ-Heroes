<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router'; // Vue Router 임포트
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import Swal from 'sweetalert2'; // 알림 라이브러리
import axios from 'axios'; // API 호출을 위한 axios

// 사용자가 입력한 성명과 사번
const employeeName = ref('');
const employeeId = ref('');

// Vue Router 사용
const router = useRouter();

// 비밀번호 재발급 요청 함수
async function sendResetPasswordRequest() {
  if (!employeeName.value || !employeeId.value) {
    Swal.fire('입력 오류', '성명과 사번을 모두 입력해주세요.', 'error');
    return;
  }

  try {
    // 서버로 데이터 전송
    const response = await axios.post('http://localhost:8080/mails/password', {
      name: employeeName.value,
      employeeId: employeeId.value
    });

    // 성공적으로 요청이 처리된 경우
    Swal.fire('비밀번호 재발급 요청 완료', response.data, 'success').then(() => {
      // 요청 완료 후 /login으로 이동
      router.push('/login');
    });
  } catch (error) {
    if (error.response) {
      // 서버 응답에서 오류 메시지 받기
      Swal.fire('오류', error.response.data, 'error');
    } else {
      // 서버 연결 실패 또는 기타 오류 처리
      Swal.fire('오류', '서버에 연결할 수 없습니다. 다시 시도해주세요.', 'error');
    }
  }
}
</script>

<template>
  <div class="relative flex items-center justify-center min-h-screen bg-surface-50 dark:bg-surface-950">
    <!-- 비밀번호 재발급 폼 -->
    <div
      class="relative z-10 flex flex-col items-center justify-center bg-surface-0 dark:bg-surface-900 p-8 sm:p-20 rounded-lg shadow-lg">
      <div class="text-center mb-8">
        <div class="text-surface-900 dark:text-surface-0 text-4xl font-medium mb-4">HeRoes</div>
        <span class="text-muted-color font-medium">비밀번호 재발급</span>
      </div>

      <!-- 성명 입력 -->
      <div class="mb-6 w-full">
        <label for="employeeName" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">성명</label>
        <InputText id="employeeName" v-model="employeeName" class="w-full md:w-[30rem]" placeholder="성명을 입력해주세요." />
      </div>

      <!-- 사번 입력 -->
      <div class="mb-8 w-full">
        <label for="employeeId" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">사번</label>
        <InputText id="employeeId" v-model="employeeId" class="w-full md:w-[30rem]" placeholder="사번을 입력해주세요." />
      </div>

      <!-- 비밀번호 재발급 버튼 -->
      <Button label="비밀번호 재발급" class="w-full md:w-[30rem]" @click="sendResetPasswordRequest"></Button>
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
