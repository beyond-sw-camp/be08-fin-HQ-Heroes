<template>
  <div class="card mb-0 mt-2 border dark:border-surface-700 bg-indigo-100 dark:bg-surface-700 rounded-lg">
    <div class="flex justify-between mb-0">
      <div>
        <span class="block text-muted-color font-medium mb-4">{{ employeeData.teamName }}
          {{ employeeData.employeeName }}님</span>
        <div class="text-surface-900 dark:text-surface-0 font-medium text-l">{{ currentDay }}</div>
        <div class="text-surface-900 dark:text-surface-0 font-medium text-">09 : 00 ~ 18 : 00</div>
      </div>
    </div>

    <button class="mt-3 font-bold py-2 px-4 rounded hover:bg-[#4f46e5]"
      :class="['bg-indigo-500 text-white', 'dark:bg-indigo-500 dark:text-white']">
      출근하기
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // 추가: router 사용
import fetchGet from '@/views/pages/auth/service/AuthApiService';

const currentDay = ref('');
const employeeData = ref({
  employeeId: '',
  employeeName: '',
  teamName: '',
  deptName: '',
  jobName: '',
  positionName: '',
  joinDate: '',
  birthDate: '',
  phoneNumber: '',
  roadAddress: '',
  lotAddress: '',
  detailedAddress: '',
  profileImageUrl: ''
});
const router = useRouter();

const getKoreanDate = () => {
  const date = new Date();
  date.setHours(date.getHours() + 9); // UTC+9 for KST
  const dayNames = ['일', '월', '화', '수', '목', '금', '토'];
  const day = dayNames[date.getDay()];
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const dayOfMonth = date.getDate();
  currentDay.value = `${year}/${month}/${dayOfMonth}(${day})`;
};

async function getLoginEmployeeInfo() {
  try {
    const employeeId = window.localStorage.getItem('employeeId');
    console.log(employeeId);
    const employeesData = await fetchGet(`http://localhost:8080/api/v1/employee/employees/${employeeId}`, router.push, router.currentRoute.value);

    if (employeesData) {
      employeeData.value = employeesData;
    } else {
      employeeData.value = {};
    }
  } catch (error) {
    console.error("직원 데이터를 가져오는 중 오류 발생:", error);
    employeeData.value = {};
  }
}

onMounted(() => {
  getKoreanDate();
  getLoginEmployeeInfo();
});
</script>

<style scoped>
.employee-details {
  margin-top: 10px;
  font-size: 14px;
  /* 추가: 글씨 크기 조정 */
}
</style>
