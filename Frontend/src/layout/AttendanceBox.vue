<template>
    <div class="card mb-0 mt-2 border bg-indigo-100 rounded-lg">
        <div class="flex justify-between mb-0 items-center">
            <!-- 왼쪽: 이름 및 팀 정보 -->
            <div class="flex flex-col">
                <span class="block text-surface-900 font-bold">{{ authStore.employeeData.teamName }}</span>
                <span class="block text-surface-900 font-bold">{{ authStore.employeeData.employeeName }}님</span>
                <div class="text-muted-color font-medium text-base">{{ currentDay }}</div>
                <div class="text-muted-color font-medium text-sm">{{ currentTime }}</div>
            </div>

            <!-- 프로필 이미지 및 배지 -->
            <div class="ml-4">
                <Avatar class="p-overlay-badge custom-avatar" :image="authStore.employeeData.profileImageUrl" size="custom" />
            </div>
        </div>

        <!-- 출근/퇴근 버튼 -->
        <button class="mt-3 font-bold py-2 px-4 rounded hover:bg-[#4f46e5]" :class="[isCheckedIn ? 'bg-red-500 hover:bg-red-600' : 'bg-indigo-500 hover:bg-[#4f46e5]', 'text-white', 'dark:bg-indigo-500 dark:text-white']" @click="handleAttendance">
            {{ isCheckedIn ? '퇴근하기' : '출근하기' }}
        </button>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { useNotificationStore } from '@/stores/notificationStore';
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService';
import Avatar from 'primevue/avatar'; // PrimeVue Avatar 가져오기
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGet, fetchPost } from '../views/pages/auth/service/AuthApiService';
import authService from '../views/pages/auth/service/authService'; // fetchPost, fetchGet 메서드 가져오기

const router = useRouter(); // useRouter 훅 호출
const authStore = useAuthStore();
const notificationStore = useNotificationStore();
const currentDay = ref('');
const currentTime = ref('');
const isCheckedIn = ref(false); // 출근 여부를 나타내는 상태

const getKoreanDate = () => {
    const date = new Date();
    const dayNames = ['일', '월', '화', '수', '목', '금', '토'];
    const day = dayNames[date.getDay()];
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const dayOfMonth = String(date.getDate()).padStart(2, '0');
    currentDay.value = `${year}/${month}/${dayOfMonth}(${day})`;
};

// 대한민국 현재 시간을 업데이트하는 함수
const updateCurrentTime = () => {
    const date = new Date();
    currentTime.value = date.toLocaleTimeString('ko-KR', { hour12: false });
};

// 출근/퇴근 처리
const handleAttendance = async () => {
    const attendanceTime = new Date(); // 현재 시간을 저장하기 위한 변수

    try {
        const result = await Swal.fire({
            title: isCheckedIn.value ? '정말 퇴근 처리 하시겠습니까?' : '정말 출근 처리 하시겠습니까?',
            text: isCheckedIn.value ? '로그아웃 됩니다.' : '출근 시간으로 기록됩니다.',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '확인',
            cancelButtonText: '취소'
        });

        if (result.isConfirmed) {
            if (!isCheckedIn.value) {
                // 출근 처리
                const response = await fetchPost('https://hq-heroes-api.com/api/v1/attendance/check-in', { checkInTime: attendanceTime });
                if (response && response.attendanceId) {
                    isCheckedIn.value = true; // 출근 상태로 변경
                    window.location.reload(); // 페이지를 다시 로드
                } else {
                    console.error('출근 처리 실패');
                }
            } else {
                // 퇴근 처리
                const response = await fetchPost('https://hq-heroes-api.com/api/v1/attendance/check-out', { checkOutTime: attendanceTime });
                await handleLogout();
            }
        } else {
            // 사용자가 취소를 선택한 경우, 아무 작업도 수행하지 않습니다.
            console.log('사용자가 출근/퇴근 처리를 취소했습니다.');
        }
    } catch (error) {
        console.error('오류 발생', error);
        Swal.fire({
            icon: 'error',
            title: '처리 오류',
            text: '출근/퇴근 처리 중 오류가 발생했습니다. 다시 시도해주세요.',
            confirmButtonText: '확인'
        });
    }
};

const handleLogout = async () => {
    try {
        isCheckedIn.value = false;
        await authService.logout(); // 서버 로그아웃 처리
        // authStore.logout(); // Pinia 스토어에서 로그아웃 처리

        // 로그아웃 완료 후 로그인 화면으로 이동
        await router.push('/login');
    } catch (err) {
        console.error('로그아웃 오류: ', err.message);
        alert('로그아웃 중 오류가 발생했습니다.');
    }
};

// 컴포넌트가 로드될 때 출근 여부 및 알림 상태 확인
onMounted(async () => {
    getKoreanDate();

    // 매초마다 시간을 업데이트하는 인터벌 설정
    updateCurrentTime();
    setInterval(updateCurrentTime, 1000);

    // 서버에서 출근 상태 확인
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/attendance/status');
        if (response && response.isCheckedIn) {
            isCheckedIn.value = true; // 서버 응답에 따라 출근 상태 설정
        } else {
            isCheckedIn.value = false;
        }
    } catch (error) {
        console.error('출근 상태 확인 실패', error);
    }

    // 사용자 정보 가져오기
    const employeeId = window.localStorage.getItem('employeeId');
    const employeeData = await getLoginEmployeeInfo(employeeId);
    if (employeeData) {
        authStore.setEmployeeData(employeeData); // store에 직접 저장
    }
});
</script>

<style scoped>
.employee-details {
    margin-top: 10px;
    font-size: 14px;
}

.custom-avatar {
    width: 60px; /* 원하는 크기로 조정 */
    height: 70px; /* 원하는 크기로 조정 */
}
</style>
