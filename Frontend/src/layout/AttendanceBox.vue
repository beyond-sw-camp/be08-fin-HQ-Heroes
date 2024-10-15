<template>
    <div class="card mb-0 mt-2 border dark:border-surface-700 bg-indigo-100 dark:bg-surface-700 rounded-lg">
        <div class="flex justify-between mb-0">
            <div>
                <!-- authStore.employeeData로 직접 접근 -->
                <span class="block text-muted-color font-medium mb-4">{{ authStore.employeeData.teamName }} {{ authStore.employeeData.employeeName }}님</span>
                <div class="text-surface-900 dark:text-surface-0 font-medium text-l">{{ currentDay }}</div>
                <div class="text-surface-900 dark:text-surface-0 font-medium text-">{{ currentTime }}</div>
            </div>
        </div>

        <button class="mt-3 font-bold py-2 px-4 rounded hover:bg-[#4f46e5]" :class="[isCheckedIn ? 'bg-red-500 hover:bg-red-600' : 'bg-indigo-500 hover:bg-[#4f46e5]', 'text-white', 'dark:bg-indigo-500 dark:text-white']" @click="handleAttendance">
            {{ isCheckedIn ? '퇴근하기' : '출근하기' }}
        </button>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService';
import { onMounted, ref } from 'vue';
import { fetchGet, fetchPost } from '../views/pages/auth/service/AuthApiService'; // fetchPost, fetchGet 메서드 가져오기

const authStore = useAuthStore();
const currentDay = ref('');
const currentTime = ref('');
const isCheckedIn = ref(false); // 출근 여부를 나타내는 상태

const getKoreanDate = () => {
    const date = new Date();
    const dayNames = ['일', '월', '화', '수', '목', '금', '토'];
    const day = dayNames[date.getDay()];
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const dayOfMonth = date.getDate();
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
        if (!isCheckedIn.value) {
            // 출근 처리
            const response = await fetchPost('http://localhost:8080/api/v1/attendance/check-in', { checkInTime: attendanceTime });
            if (response && response.attendanceId) {
                isCheckedIn.value = true; // 출근 상태로 변경
            } else {
                console.error('출근 처리 실패');
            }
        } else {
            // 퇴근 처리
            const response = await fetchPost('http://localhost:8080/api/v1/attendance/check-out', { checkOutTime: attendanceTime });
            if (response) {
                isCheckedIn.value = false; // 퇴근 상태로 변경
            } else {
                console.error('퇴근 처리 실패');
            }
        }
    } catch (error) {
        console.error('오류 발생', error);
    }
};

// 컴포넌트가 로드될 때 출근 여부 상태 확인
onMounted(async () => {
    getKoreanDate();

    // 매초마다 시간을 업데이트하는 인터벌 설정
    updateCurrentTime();
    setInterval(updateCurrentTime, 1000);

    // 서버에서 출근 상태 확인
    try {
        const response = await fetchGet('http://localhost:8080/api/v1/attendance/status');
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
</style>
