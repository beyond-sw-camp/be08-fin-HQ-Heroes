<script setup>
import { useLayout } from '@/layout/composables/layout';
import 'primeicons/primeicons.css';
import { ref, onMounted, onBeforeUnmount, computed, watch } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import authService from '../views/pages/auth/service/authService';
import router from '@/router';
import fetchReissue from '@/views/pages/auth/service/fetchReissue'; // 토큰 갱신 함수
import Button from 'primevue/button'; // PrimeVue 버튼 import
import Dialog from 'primevue/dialog';
import { getReceiveNotificationsByEmployeeId, getSendNotificationsByEmployeeId } from '@/views/pages/main/service/notificationService';

const { onMenuToggle } = useLayout();
const authStore = useAuthStore();
const remainingTime = ref(''); // 남은 시간 표시를 위한 변수
const showReissueDialog = ref(false);
const notificationDrawerVisible = ref(false);
const notifications = ref([]);
const selectedNotification = ref(null);
const selectNotificationsList = ref(false); // false = 받은 알림, true 보낸 알림

let timer = null;

// 로그아웃 처리
const handleLogout = async () => {
    try {
        await authService.logout(); // 서버 로그아웃 처리
        authStore.logout(); // Pinia 스토어에서 로그아웃 처리

        // 로그아웃 완료 후 로그인 화면으로 이동
        await router.push('/login');
    } catch (err) {
        console.error('로그아웃 오류: ', err.message);
        alert('로그아웃 중 오류가 발생했습니다.');
    }
};

// 남은 시간을 mm:ss 형식으로 변환하는 함수
const formatTime = (timeInMs) => {
    const minutes = Math.floor(timeInMs / (1000 * 60));
    const seconds = Math.floor((timeInMs % (1000 * 60)) / 1000);
    return `${String(minutes).padStart(2, '0')}분${String(seconds).padStart(2, '0')}초`;
};

// 타이머를 업데이트하는 함수
const updateRemainingTime = () => {
    const accessTime = parseInt(window.localStorage.getItem('accessTime'), 10);
    const currentTime = Date.now();
    const timeElapsed = currentTime - accessTime;
    const timeLeft = 30 * 60 * 1000 - timeElapsed; // 30분에서 경과된 시간 차감

    if (timeLeft > 0) {
        remainingTime.value = formatTime(timeLeft);
    } else {
        clearInterval(timer);
        remainingTime.value = '00분00초'; // 만료되면 00분00초 표시
        showReissueDialog.value = true; // 다이얼로그 표시
    }
};

const handleReissue = async () => {
    try {
        await fetchReissue(); // 토큰 재발급

        // 새로운 accessTime을 localStorage에 저장
        window.localStorage.setItem('accessTime', Date.now());

        // 타이머 업데이트
        updateRemainingTime();
        timer = setInterval(updateRemainingTime, 1000);

        // 다이얼로그 닫기
        showReissueDialog.value = false;
    } catch (error) {
        console.error('토큰 재갱신 실패:', error.message);
        alert('토큰 재갱신 중 오류가 발생했습니다.');
    }
};

const fetchNotifications = async () => {
    const employeeId = window.localStorage.getItem('employeeId');
    notifications.value = null;
    if (!employeeId) return;

    if (selectNotificationsList.value) {
        notifications.value = await getSendNotificationsByEmployeeId(employeeId);
    } else {
        notifications.value = await getReceiveNotificationsByEmployeeId(employeeId);
    }
};

const openNotificationDrawer = async () => {
    await fetchNotifications();
    notificationDrawerVisible.value = true;
};

watch(selectNotificationsList, async () => {
    await fetchNotifications();
});

const openNotificationModal = async (event) => {
    selectedNotification.value = event.data;
    alarmDisplayDialog.value = true;
};

const formatDate = (createdAt) => {
    const now = new Date();
    const notificationDate = new Date(createdAt);
    const timeDifference = now - notificationDate;
    const oneDayInMillis = 1000 * 60 * 60 * 24;

    if (timeDifference < oneDayInMillis) {
        return notificationDate.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
    } else {
        const daysAgo = Math.floor(timeDifference / oneDayInMillis);
        return `${daysAgo}일 전`;
    }
};

const toggleButtonStyle = computed(() => {
    return selectNotificationsList.value
        ? {
              backgroundColor: '#000',
              color: '#fff',
              border: '2px solid #fff',
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              cursor: 'pointer'
          }
        : {
              backgroundColor: '#fff',
              color: '#000',
              border: '2px solid gray',
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              cursor: 'pointer'
          };
});

// 페이지가 로드될 때 타이머 시작
onMounted(async () => {
    timer = setInterval(updateRemainingTime, 1000); // 1초마다 업데이트
    updateRemainingTime(); // 첫 화면 로드 시 바로 호출
    const employeeId = window.localStorage.getItem('employeeId');
    if (!selectNotificationsList) {
        notifications.value = await getReceiveNotificationsByEmployeeId(employeeId);
    } else {
        notifications.value = await getSendNotificationsByEmployeeId(employeeId);
    }
});

// 페이지가 닫힐 때 타이머 정리
onBeforeUnmount(() => {
    clearInterval(timer);
});

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
                <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 537.64 563.26">
                    <path
                        id="uuid-fb6bba6d-324d-4625-a98c-3d990729dcd8-114-164-127-213"
                        d="m294.36,308.7c1.69,8.48,7.72,13.98,13.47,12.28,5.75-1.7,9.04-9.96,7.35-18.44-.63-3.4-2.11-6.52-4.32-9.07l-7.63-35.8-17.84,5.88,9.42,34.67c-1.01,3.51-1.16,7.11-.43,10.48,0,0,0,0,0,0Z"
                        fill="#f8a8ab"
                    />
                    <rect x="254.14" y="514.38" width="20.94" height="29.71" transform="translate(529.23 1058.47) rotate(-180)" fill="#f8a8ab" />
                    <path
                        d="m272.77,561.11c-3.58.32-21.5,1.74-22.4-2.37-.82-3.77.39-7.71.56-8.25,1.72-17.14,2.36-17.33,2.75-17.44.61-.18,2.39.67,5.28,2.53l.18.12.04.21c.05.27,1.33,6.56,7.4,5.59,4.16-.66,5.51-1.58,5.94-2.03-.35-.16-.79-.44-1.1-.92-.45-.7-.53-1.6-.23-2.68.78-2.85,3.12-7.06,3.22-7.23l.27-.48,23.8,16.06,14.7,4.2c1.11.32,2,1.11,2.45,2.17h0c.62,1.48.24,3.2-.96,4.28-2.67,2.4-7.97,6.51-13.54,7.02-1.48.14-3.44.19-5.64.19-9.19,0-22.61-.95-22.71-.97Z"
                        fill="#2f2e43"
                    />
                    <rect x="196.13" y="514.38" width="20.94" height="29.71" transform="translate(413.21 1058.47) rotate(-180)" fill="#f8a8ab" />
                    <path
                        d="m214.76,561.11c-3.58.32-21.5,1.74-22.4-2.37-.82-3.77.39-7.71.56-8.25,1.72-17.14,2.36-17.33,2.75-17.44.61-.18,2.39.67,5.28,2.53l.18.12.04.21c.05.27,1.33,6.56,7.4,5.59,4.16-.66,5.51-1.58,5.94-2.03-.35-.16-.79-.44-1.1-.92-.45-.7-.53-1.6-.23-2.68.78-2.85,3.12-7.06,3.22-7.23l.27-.48,23.8,16.06,14.7,4.2c1.11.32,2,1.11,2.45,2.17h0c.62,1.48.24,3.2-.96,4.28-2.67,2.4-7.97,6.51-13.54,7.02-1.48.14-3.44.19-5.64.19-9.19,0-22.61-.95-22.71-.97Z"
                        fill="#2f2e43"
                    />
                    <polygon points="213.11 100.28 245.58 110.95 245.58 64.21 216.12 64.21 213.11 100.28" fill="#f8a8ab" />
                    <circle cx="241.56" cy="44.8" r="32.35" fill="#f8a8ab" />
                    <path
                        d="m233.32,47.33l4.46,5.41,8.07-14.12s10.3.53,10.3-7.11c0-7.64,9.45-7.86,9.45-7.86,0,0,13.37-23.35-14.33-17.2,0,0-19.21-13.16-28.77-1.91,0,0-29.3,14.75-20.91,40.44l13.93,26.48,3.16-5.99s-1.91-25.16,14.65-18.15Z"
                        fill="#2f2e43"
                    />
                    <path d="m0,562.07c0,.66.53,1.19,1.19,1.19h535.26c.66,0,1.19-.53,1.19-1.19s-.53-1.19-1.19-1.19H1.19c-.66,0-1.19.53-1.19,1.19Z" fill="#484565" />
                    <path d="m328.8,349.01l-61.13-19.65c-7.54-2.42-15.64,1.63-18.21,9.13l-27.62,80.54c-2.82,8.22,2.16,17.06,10.65,18.92l70.26,15.37c7.72,1.69,15.38-3.1,17.24-10.78l18.49-76.26c1.79-7.4-2.43-14.94-9.68-17.27Z" fill="#e2e3e4" />
                    <path d="m322.6,366l-3.94-.7c2.52-14.32,6-52.5-8.05-57.18-6.81-2.27-13.67,1.7-20.38,11.81-5.27,7.95-8.35,16.75-8.38,16.84l-3.78-1.31c.54-1.55,13.39-37.94,33.8-31.14,20.17,6.72,11.12,59.43,10.72,61.67Z" fill="#2f2e43" />
                    <polygon points="276.25 254.19 166.98 254.19 193.72 529.88 224.72 527.88 226.25 329.34 245.76 416.47 251.47 526.38 279.72 527.88 283 402.85 276.25 254.19" fill="#2f2e43" />
                    <polygon points="211.34 83.19 248.34 92.19 279.97 228.88 276.25 254.19 183.6 269.87 166.98 254.19 211.34 83.19" fill="#6c63ff" />
                    <polygon points="211.28 76.35 198.63 90.54 172.97 96.7 129.22 291.22 213.52 305.5 237 107.39 211.28 76.35" fill="#2f2e43" />
                    <polygon points="248.32 83.44 241.72 106.96 272.27 317.88 288.03 317.88 280.76 111.48 259.36 94.48 248.32 83.44" fill="#2f2e43" />
                    <path d="m268.07,108.87l12.69,2.61s5.56,2.04,7.58,17.84c2.01,15.8,2.37,68.86,2.37,68.86l21.58,85.6-24.51,8.08-11.54-46.06-8.18-136.94Z" fill="#2f2e43" />
                    <polygon points="246.9 97.89 240.76 109.15 253.04 241.23 238.71 254.54 225.4 242.26 235.13 109.66 220.95 97.89 225.91 94.3 231.03 100.44 241.27 98.4 245.36 92.25 246.9 97.89" fill="#2f2e43" />
                    <path
                        id="uuid-4155b336-10d9-4b14-826d-07551e167be9-115-165-128-214"
                        d="m186.59,275.51c5.15,6.95,12.95,9.34,17.42,5.35,4.47-3.99,3.92-12.86-1.23-19.82-2.02-2.81-4.69-4.99-7.79-6.35l-22.2-29.11-13.62,12.94,23.33,27.32c.59,3.61,1.99,6.92,4.09,9.66,0,0,0,0,0,0Z"
                        fill="#f8a8ab"
                    />
                    <path d="m183.11,99.89l-10.14-3.19s-13.74,3.41-18.29,20.57l-22.21,83.6,37.67,66.12,20.13-24.44-18.96-60.7,11.81-81.96Z" fill="#2f2e43" />
                </svg>
                <span>HeRoes</span>
            </router-link>
        </div>

        <div class="layout-topbar-actions">
            <div v-if="authStore.isLoggedIn" class="token-info">
                <!-- 남은 시간 표시 -->
                <div class="flex-row">
                    <span class="mr-1 row">남은 시간</span>
                    <span>{{ remainingTime }}</span>

                    <!-- PrimeVue 버튼을 사용하여 재갱신 버튼 생성 -->
                    <Button icon="pi pi-refresh" severity="info" outlined rounded @click="handleReissue" class="ml-2" />

                    <!-- PrimeVue 로그아웃 버튼 -->
                    <Button icon="pi pi-sign-out" severity="danger" outlined rounded @click="handleLogout" class="ml-2" />

                    <!-- 알림 Drawer 띄우는 버튼 -->
                    <Button class="ml-2" rounded icon="pi pi-bell" @click="openNotificationDrawer" />
                </div>
            </div>

            <div v-else>
                <!-- PrimeVue 로그인 버튼 -->
                <Button icon="pi pi-sign-in" label="로그인" severity="primary" outlined rounded @click="goToLogin" class="ml-2" />

                <!-- PrimeVue 회원가입 버튼 -->
                <Button icon="pi pi-user-plus" label="회원가입" severity="success" outlined rounded @click="goToSignUp" class="ml-2" />
            </div>
        </div>
    </div>

    <!-- 재갱신 요청 다이얼로그 -->
    <Dialog
        v-model:visible="showReissueDialog"
        :modal="true"
        :closable="false"
        pt:mask:class="backdrop-blur-sm bg-black/50"
        header="시간 만료"
        :style="{ width: '25rem', borderRadius: '10px', boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)' }"
        class="custom-dialog"
    >
        <p class="text-gray-900 text-center mb-1">접근 시간이 만료되었습니다.</p>
        <p class="text-gray-700 text-sm text-center mb-4">시간을 갱신해 주세요.</p>

        <div class="flex justify-center">
            <Button icon="pi pi-refresh" label="시간 갱신" @click="handleReissue" class="w-full py-2 px-4 font-bold rounded-lg text-white" />
        </div>
    </Dialog>

    <Drawer v-model:visible="notificationDrawerVisible" position="right" style="width: 40%">
        <template #header>
            <div class="flex items-center gap-2">
                <Avatar :image="authStore.employeeData.profileImageUrl" shape="circle" />
                <span class="font-bold">{{ authStore.employeeData.teamName }} {{ authStore.employeeData.employeeName }}</span>
            </div>
        </template>

        <div class="flex items-center justify-start gap-3 mb-4">
            <ToggleButton unstyled v-model="selectNotificationsList" onLabel="보낸 알림" offLabel="받은 알림" :style="toggleButtonStyle" />
        </div>
        <div class="col ml-4">
            <span class="font-bold">알림</span>
        </div>

        <DataTable :value="notifications" :rows="9" :paginator="true" responsiveLayout="scroll" selectionMode="single" @row-click="openNotificationModal" removableSort>
            <!-- 시간 컬럼 -->
            <Column style="width: 25%" header="시간" sortable>
                <template #body="slotProps">
                    <span>{{ formatDate(slotProps.data.createdAt) }}</span>
                </template>
            </Column>

            <!-- 보낸 사람 컬럼 -->
            <Column field="senderName" header="보낸 사람" style="width: 25%" sortable>
                <template #body="slotProps">
                    <span>{{ slotProps.data.senderName }}</span>
                </template>
            </Column>

            <!-- 카테고리 -->
            <Column field="categoryName" header="카테고리" style="width: 25%" sortable>
                <template #body="slotProps">
                    <span>{{ slotProps.data.categoryName }}</span>
                </template>
            </Column>

            <!-- 상태 -->
            <Column field="notificationStatus" header="상태" style="width: 20%" sortable>
                <template #body="slotProps">
                    <span>{{ slotProps.data.status === 'READ' ? '읽음' : '안 읽음' }}</span>
                </template>
            </Column>
        </DataTable>

        <template #footer>
            <div class="flex items-center gap-2">
                <Button label="Close" icon="pi pi-times" class="flex-auto" outlined @click="notificationDrawerVisible = false" />
            </div>
        </template>
    </Drawer>
</template>
