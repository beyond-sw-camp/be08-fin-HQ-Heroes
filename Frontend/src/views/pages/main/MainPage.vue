<script setup>
import { useLayout } from '@/layout/composables/layout';
import { ProductService } from '@/service/ProductService';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import { onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { getNotices } from './service/noticeService'; // Import the getNotices function from noticeService.js

const router = useRouter();
const alarmDisplayDialog = ref(false);
const selectedMessage = ref(null);
const showMore = ref(false);

// Notice data
const announcements = ref([]); // Initialize with an empty array to hold fetched notice data

// Fetch notices when the component is mounted
onMounted(async () => {
  announcements.value = await getNotices(); // Fetch notices from the backend and populate the announcements array
});

// Modal functions for messages
function openModal(message) {
  selectedMessage.value = message;
  alarmDisplayDialog.value = true;
}

function closeModal() {
  alarmDisplayDialog.value = false;
  selectedMessage.value = null;
}

// Sample messages (you can replace this with dynamic data)
const messages = [
  { id: 1, sender: '홍길동', subject: '휴가 신청', content: '휴가 신청 관련 메세지 내용입니다.', status: '읽지 않음', sendTime: '2023-09-01 14:35' },
  { id: 2, sender: '김철수', subject: '조퇴 신청', content: '조퇴 신청 관련 메세지 내용입니다.', status: '읽지 않음', sendTime: '2023-09-02 09:12' },
  { id: 3, sender: '이영희', subject: '병가 신청', content: '병가 신청 관련 메세지 내용입니다.', status: '읽음', sendTime: '2023-09-03 16:45' },
  { id: 4, sender: '박수빈', subject: '지각 신청', content: '지각 신청 관련 메세지 내용입니다.', status: '읽음', sendTime: '2023-09-04 08:30' },
  { id: 5, sender: '최민호', subject: '출결 신청', content: '출결 신청 관련 메세지 내용입니다.', status: '읽지 않음', sendTime: '2023-09-05 11:20' }
];

// Modal functions for announcements
const displayDialog = ref(false);
const selectedAnnouncement = ref(null);

function openNoticeDetails(event) {
  selectedAnnouncement.value = event.data;
  displayDialog.value = true;
}

function closeDialog() {
  displayDialog.value = false;
  selectedAnnouncement.value = null;
}

// Category mapping object
const categoryTranslations = {
  GENERAL: '일반',
  HR: '인사',
  ATTENDANCE: '근태',
  VACATION: '휴가',
  EDUCATION: '교육'
};

// Function to translate category
const translateCategory = (category) => {
  return categoryTranslations[category] || category; // If no match, return the original category
};

// Layout and product data (assuming this data will be fetched or used)
const { getPrimary, getSurface, isDarkTheme } = useLayout();
const products = ref([]);
const display = ref(false);
const product = ref({});
const chartData = ref(null);
const chartOptions = ref(null);
const currentDate = ref(new Date().toLocaleString());

const updateDate = () => {
  currentDate.value = new Date().toLocaleString();
};

// Chart data and options
onMounted(() => {
  ProductService.getProductsSmall().then((data) => (products.value = data));
  chartData.value = setChartData();
  chartOptions.value = setChartOptions();
});

function setChartData() {
  const documentStyle = getComputedStyle(document.documentElement);

  return {
    labels: ['Q1', 'Q2', 'Q3', 'Q4'],
    datasets: [
      {
        type: 'bar',
        label: 'Subscriptions',
        backgroundColor: documentStyle.getPropertyValue('--p-primary-400'),
        data: [4000, 10000, 15000, 4000],
        barThickness: 32
      },
      {
        type: 'bar',
        label: 'Advertising',
        backgroundColor: documentStyle.getPropertyValue('--p-primary-300'),
        data: [2100, 8400, 2400, 7500],
        barThickness: 32
      },
      {
        type: 'bar',
        label: 'Affiliate',
        backgroundColor: documentStyle.getPropertyValue('--p-primary-200'),
        data: [4100, 5200, 3400, 7400],
        borderRadius: {
          topLeft: 8,
          topRight: 8
        },
        borderSkipped: true,
        barThickness: 32
      }
    ]
  };
}

function setChartOptions() {
  const documentStyle = getComputedStyle(document.documentElement);
  const borderColor = documentStyle.getPropertyValue('--surface-border');
  const textMutedColor = documentStyle.getPropertyValue('--text-color-secondary');

  return {
    maintainAspectRatio: false,
    aspectRatio: 0.8,
    scales: {
      x: {
        stacked: true,
        ticks: {
          color: textMutedColor
        },
        grid: {
          color: 'transparent',
          borderColor: 'transparent'
        }
      },
      y: {
        stacked: true,
        ticks: {
          color: textMutedColor
        },
        grid: {
          color: borderColor,
          borderColor: 'transparent',
          drawTicks: false
        }
      }
    }
  };
}

// Watch for theme changes and update the chart
watch([getPrimary, getSurface, isDarkTheme], () => {
  chartData.value = setChartData();
  chartOptions.value = setChartOptions();
});

// Helper function for formatting time
const formatDate = (date) => {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

// Attendance and salary data (can be fetched dynamically in the future)
const checkInTime = ref('');
const checkOutTime = ref('');
const salaryDday = ref('');

const calculateSalaryDday = () => {
  const today = new Date();
  const salaryDay = new Date(today.getFullYear(), today.getMonth() + 1, 25); // Assuming salary day is the 25th of every month
  const timeDifference = salaryDay - today;
  const dayDifference = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));
  salaryDday.value = dayDifference > 0 ? dayDifference : 0;
};

// Set sample attendance times
const setAttendanceTimes = () => {
  checkInTime.value = formatDate(new Date());
  checkOutTime.value = formatDate(new Date(new Date().setHours(new Date().getHours() + 9))); // 9 hours later
};

// 공지사항 목록 페이지로 이동하는 함수
const showNoticeDetail = () => {
    router.push({ name: 'manage-notices'});
};

// Execute on component mount
onMounted(() => {
  setAttendanceTimes();
  calculateSalaryDday();
  currentDate.value = new Date().toLocaleString();
});
</script>

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
          <div class="flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-border"
            style="width: 2.5rem; height: 2.5rem">
            <i class="pi pi-check-circle text-blue-500 !text-xl"></i>
          </div>
        </div>
        <span class="text-muted-color">{{ currentDate }}</span>
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
          <div class="flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-border"
            style="width: 2.5rem; height: 2.5rem">
            <i class="pi pi-map text-orange-500 !text-xl"></i>
          </div>
        </div>
        <span class="text-muted-color">{{ currentDate }}</span>
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
          <div class="flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-border"
            style="width: 2.5rem; height: 2.5rem">
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
          <div class="flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-border"
            style="width: 2.5rem; height: 2.5rem">
            <i class="pi pi-envelope text-purple-500 !text-xl"></i>
          </div>
        </div>
        <span class="text-muted-color">{{ currentDate }}</span>
      </div>
    </div>

    <!-- 공지사항 -->
    <div class="col-span-12 xl:col-span-6">
      <div class="card">
        <div class="font-semibold text-xl mb-4">공지사항</div>
        <DataTable :value="announcements" :rows="5" :paginator="true" dataKey="noticeId" responsiveLayout="scroll"
          :rowHover="true" selectionMode="single" @row-click="showNoticeDetail" :metaKeySelection="false">
          <!-- 카테고리 컬럼 -->
          <Column field="categoryName" header="카테고리" style="width: 20%; text-align: left"
            headerStyle="text-align: center" />
          <!-- 제목 컬럼 -->
          <Column field="title" header="제목" style="width: 45%; text-align: left" headerStyle="text-align: center">
            <template #body="slotProps">
              <span>{{ slotProps.data.title }}</span>
            </template>
          </Column>

          <!-- 작성자 컬럼 -->
          <Column field="employeeName" header="작성자" style="width: 15%; text-align: left"
            headerStyle="text-align: center" />

          <!-- 날짜 컬럼 -->
          <Column style="width: 20%; text-align: left" header="시간" headerStyle="text-align: center">
            <template #body="slotProps">
              <span>{{ new Date(slotProps.data.createdAt).toLocaleDateString('ko-KR') }}</span>
            </template>
          </Column>
        </DataTable>
      </div>

      <!-- 공지사항 Dialog -->
      <Dialog header="공지사항" v-model:visible="displayDialog" pt:mask:class="backdrop-blur-md"
        :breakpoints="{ '960px': '75vw' }" :style="{ width: '30vw' }" :modal="true">
        <template v-if="selectedAnnouncement">
          <p class="text-lg font-bold">{{ selectedAnnouncement.title }}</p>
          <p class="text-sm text-gray-500">{{ new Date(selectedAnnouncement.createdAt).toLocaleDateString('ko-KR') }}
          </p>
          <p class="mt-4 leading-normal">{{ selectedAnnouncement.content }}</p>
        </template>
        <template #footer>
          <Button label="Close" @click="closeDialog" />
        </template>
      </Dialog>
    </div>

    <!-- 메세지 -->
    <div class="col-span-12 xl:col-span-6">
      <div class="card">
        <div class="font-semibold text-xl mb-4">메세지</div>
        <DataTable :value="messages" :rows="5" :paginator="true" responsiveLayout="scroll">
          <!-- 보낸 시간 컬럼 -->
          <Column field="sendTime" header="보낸 시간" style="width: 20%">
            <template #body="slotProps">
              <span>{{ slotProps.data.sendTime }}</span>
            </template>
          </Column>

          <!-- 보낸 사람 컬럼 -->
          <Column field="sender" header="보낸 사람" style="width: 20%">
            <template #body="slotProps">
              <span>{{ slotProps.data.sender }}</span>
            </template>
          </Column>

          <!-- 제목 컬럼 -->
          <Column field="subject" header="제목" style="width: 40%">
            <template #body="slotProps">
              <Button type="button" class="p-button-link p-button-text" @click="openModal(slotProps.data)">
                {{ slotProps.data.subject }}
              </Button>
            </template>
          </Column>

          <!-- 열람 상태 컬럼 -->
          <Column field="status" header="열람 상태" style="width: 20%">
            <template #body="slotProps">
              <span>{{ slotProps.data.status }}</span>
            </template>
          </Column>
        </DataTable>

        <!-- 메세지 상세 모달 -->
        <Dialog header="메세지 상세 내용" v-model:visible="alarmDisplayDialog" :style="{ width: '30vw' }" modal>
          <template v-if="selectedMessage">
            <p><strong>보낸 사람:</strong> {{ selectedMessage.sender }}</p>
            <p><strong>보낸 시간:</strong> {{ selectedMessage.sendTime }}</p>
            <p><strong>제목:</strong> {{ selectedMessage.subject }}</p>
            <p><strong>내용:</strong> {{ selectedMessage.content }}</p>
          </template>
          <template #footer>
            <Button label="닫기" @click="closeModal" />
          </template>
        </Dialog>
      </div>
    </div>
  </div>
</template>