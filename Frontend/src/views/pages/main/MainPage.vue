<template>
    <div class="grid grid-cols-12 gap-8">
        <!-- 출근시간 카드 -->
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">출근시간</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ checkInTime }}</div>
                    </div>
                    <div class="flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-check-circle text-blue-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ checkInTime }}</span>
            </div>
        </div>

        <!-- 퇴근시간 카드 -->
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">퇴근시간</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ checkOutTime }}</div>
                    </div>
                    <div class="flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-map text-orange-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ checkOutTime }}</span>
            </div>
        </div>

        <!-- 휴가 잔여 일수 카드 -->
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">휴가 잔여 일수</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ 3 }}일</div>
                    </div>
                    <div class="flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-folder-open text-cyan-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ currentDate }}</span>
            </div>
        </div>

        <!-- 급여 디데이 카드 -->
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">급여</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ salaryDday }}일 남음</div>
                    </div>
                    <div class="flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-envelope text-purple-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ currentDate }}</span>
            </div>
        </div>

        <!-- 공지사항 -->
        <div class="col-span-12 xl:col-span-6">
            <div class="card">
                <div class="font-semibold text-xl ml-4 mb-2">공지사항</div>
                <DataTable :value="announcements" :rows="5" :paginator="true" dataKey="noticeId" responsiveLayout="scroll" :rowHover="true" selectionMode="single" @row-click="(e) => showNoticeDetail(e.data.noticeId)" :metaKeySelection="false">
                    <Column field="categoryName" header="카테고리" style="width: 20%; text-align: left" headerStyle="text-align: center" />
                    <Column field="title" header="제목" style="width: 45%; text-align: left" headerStyle="text-align: center">
                        <template #body="slotProps">
                            <span>{{ slotProps.data.title }}</span>
                        </template>
                    </Column>
                    <Column field="employeeName" header="작성자" style="width: 15%; text-align: left" headerStyle="text-align: center" />
                    <Column style="width: 20%; text-align: left" header="시간" headerStyle="text-align: center">
                        <template #body="slotProps">
                            <span>{{ new Date(slotProps.data.createdAt).toLocaleDateString('ko-KR') }}</span>
                        </template>
                    </Column>
                </DataTable>
            </div>
        </div>

        <!-- 알림 -->
        <div class="col-span-12 xl:col-span-6">
            <div class="card">
                <div class="font-semibold text-xl ml-4 mb-2">받은 알림</div>
                <DataTable :value="notifications" :rows="5" sortField="createdAt" :sortOrder="-1" :paginator="true" responsiveLayout="scroll" selectionMode="single" @row-click="openNotificationModal">
                    <!-- 시간 컬럼 -->
                    <Column field="createdAt" style="width: 18%" header="시간" sortable>
                        <template #body="slotProps">
                            <span>{{ formatDate(slotProps.data.createdAt) }}</span>
                        </template>
                    </Column>

                    <!-- 보낸 사람 컬럼 -->
                    <Column field="senderName" header="보낸 사람" style="width: 20%" sortable>
                        <template #body="slotProps">
                            <span>{{ slotProps.data.senderName }}</span>
                        </template>
                    </Column>

                    <!-- 카테고리 -->
                    <Column field="categoryName" header="카테고리" style="width: 20%" sortable>
                        <template #body="slotProps">
                            <span>{{ slotProps.data.categoryName }}</span>
                        </template>
                    </Column>

                    <Column field="message" header="내용" style="width: 20%" sortable>
                        <template #body="slotProps">
                            <span>{{ getFirstText(slotProps.data.message) }}</span>
                        </template>
                    </Column>

                    <!-- 상태 -->
                    <Column field="status" header="상태" style="width: 20%" sortable>
                        <template #body="slotProps">
                            <span>{{ slotProps.data.status === 'READ' ? '읽음' : '안 읽음' }}</span>
                        </template>
                    </Column>
                </DataTable>

                <!-- 알림 상세 내용 -->
                <Dialog header="알림" v-model:visible="alarmDisplayDialog" modal maximizable :style="{ width: '30vw' }" :breakpoints="{ '1199px': '50vw', '575px': '90vw' }" :closable="false" closeOnEscape :blockScroll="true">
                    <template v-if="selectedNotification">
                        <div class="notification-details p-4 rounded-lg bg-white shadow">
                            <div class="flex items-center justify-between">
                                <!-- 보낸 사람 -->
                                <div class="notification-item mb-3">
                                    <p class="text-sm text-gray-500 font-medium mb-1">보낸 사람</p>
                                    <p class="text-lg font-semibold text-primary">{{ selectedNotification.senderName }}</p>
                                </div>

                                <!-- 카테고리 -->
                                <div class="notification-item mb-3">
                                    <p class="text-sm text-gray-500 font-medium mb-1">카테고리</p>
                                    <p class="text-lg">{{ selectedNotification.categoryName }}</p>
                                </div>

                                <!-- 보낸 시간 -->
                                <div class="notification-item mb-3">
                                    <p class="text-sm text-gray-500 font-medium mb-1">보낸 시간</p>
                                    <p class="text-lg">{{ new Date(selectedNotification.createdAt).toLocaleString('ko-KR') }}</p>
                                </div>
                            </div>

                            <!-- 내용 -->
                            <div class="notification-item">
                                <p class="text-sm text-gray-500 font-medium mb-1">내용</p>
                                <div v-html="selectedNotification.message" class="p-3 bg-gray-50 rounded-md border border-gray-300"></div>
                            </div>
                        </div>
                    </template>
                    <template #footer>
                        <Button label="닫기" class="p-button-danger" @click="closeNotificationModal" icon="pi pi-times" />
                    </template>
                </Dialog>
            </div>
        </div>
    </div>
</template>

<script setup>
import router from '@/router';
import { useAuthStore } from '@/stores/authStore';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import { onMounted, ref } from 'vue';
import { fetchPut } from '../auth/service/AuthApiService';
import { getAttendanceTimes } from './service/attendanceService';
import { getNotices } from './service/noticeService';
import { getReceiveNotificationsByEmployeeId } from './service/notificationService';

const displayDialog = ref(false);
const alarmDisplayDialog = ref(false);
const announcements = ref([]);
const notifications = ref([]);
const selectedAnnouncement = ref(null);
const selectedNotification = ref(null);
const checkInTime = ref('출근전');
const checkOutTime = ref('퇴근전');
const salaryDday = ref('');
const currentDate = ref(new Date().toLocaleDateString());
const authStore = useAuthStore();

onMounted(async () => {
    announcements.value = await getNotices();
    const employeeId = window.localStorage.getItem('employeeId');
    notifications.value = await getReceiveNotificationsByEmployeeId(employeeId);
    await setAttendanceTimes(employeeId);
    calculateSalaryDday();
    currentDate.value = new Date().toLocaleString();
});

const setAttendanceTimes = async (employeeId) => {
    const attendanceTimes = await getAttendanceTimes(employeeId);
    if (attendanceTimes) {
        checkInTime.value = attendanceTimes.checkIn ? formatTime(new Date(attendanceTimes.checkIn)) : '출근 전';
        checkOutTime.value = attendanceTimes.checkOut ? formatTime(new Date(attendanceTimes.checkOut)) : '퇴근 전';
    }
};

const calculateSalaryDday = () => {
    const today = new Date();
    const nextSalaryDate = new Date(today.getFullYear(), today.getMonth(), 25);
    if (today.getDate() > 25) {
        nextSalaryDate.setMonth(today.getMonth() + 1);
    }
    const daysRemaining = Math.ceil((nextSalaryDate - today) / (1000 * 60 * 60 * 24));
    salaryDday.value = daysRemaining;
};

const formatTime = (date) => {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

const showNoticeDetail = (noticeId) => {
    router.push({ name: 'notice-detail', params: { id: noticeId } });
};

function closeDialog() {
    displayDialog.value = false;
    selectedAnnouncement.value = null;
}

const openNotificationModal = async (event) => {
    selectedNotification.value = event.data;
    const notificationId = event.data.notificationId;
    const employeeId = authStore.employeeData.employeeId;

    try {
        // 백엔드로 알림 상태 변경 요청
        const url = `http://localhost:8080/api/v1/notification-service/notification/${notificationId}/read?employeeId=${employeeId}`;
        await fetchPut(url, {});

        // 성공적으로 업데이트된 경우 UI에 반영
        console.log('알림 상태가 READ로 변경되었습니다.');

        // 상태 변경 후, 해당 알림의 상태를 'READ'로 변경
        const notificationIndex = notifications.value.findIndex((n) => n.notificationId === notificationId);
        if (notificationIndex !== -1) {
            notifications.value[notificationIndex].status = 'READ';
        }
    } catch (error) {
        console.error('알림 상태 업데이트 중 오류 발생:', error);
    }

    alarmDisplayDialog.value = true;
};

function closeNotificationModal() {
    alarmDisplayDialog.value = false;
    selectedNotification.value = null;
}

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

const getFirstText = (htmlString) => {
    // HTML 문자열을 DOM으로 파싱
    const parser = new DOMParser();
    const doc = parser.parseFromString(htmlString, 'text/html');
    // 첫 번째 텍스트 노드를 찾아 반환
    const firstText = doc.body.textContent || '';
    // 첫 번째 텍스트를 반환하고, 너무 길 경우 생략 처리
    return firstText.length > 7 ? firstText.slice(0, 7) + '…' : firstText;
};

// Function to safely render HTML content in the notification modal
const renderHtml = (htmlContent) => {
    return htmlContent;
};
</script>

<style scoped>
.card {
    padding: 1rem;
    border-radius: 0.5rem;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 1rem;
}

.font-semibold {
    font-weight: 600;
}

.text-xl {
    font-size: 1.25rem;
}

.text-muted-color {
    color: #6b7280;
}

.rounded-border {
    border-radius: 0.5rem;
}

.text-surface-900 {
    color: #1f2937;
}
</style>
