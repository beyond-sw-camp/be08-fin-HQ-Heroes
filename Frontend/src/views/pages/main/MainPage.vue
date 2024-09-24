<script setup>
import { useLayout } from '@/layout/composables/layout';
import { ProductService } from '@/service/ProductService';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import { onMounted, ref, watch } from 'vue';

const alarmDisplayDialog = ref(false);
const selectedMessage = ref(null);
const showMore = ref(false);

// 샘플 메세지 데이터
const messages = [
    { id: 1, sender: '홍길동', subject: '휴가 신청', content: '휴가 신청 관련 메세지 내용입니다.', status: '읽지 않음', sendTime: '2023-09-01 14:35' },
    { id: 2, sender: '김철수', subject: '조퇴 신청', content: '조퇴 신청 관련 메세지 내용입니다.', status: '읽지 않음', sendTime: '2023-09-02 09:12' },
    { id: 3, sender: '이영희', subject: '병가 신청', content: '병가 신청 관련 메세지 내용입니다.', status: '읽음', sendTime: '2023-09-03 16:45' },
    { id: 4, sender: '박수빈', subject: '지각 신청', content: '지각 신청 관련 메세지 내용입니다.', status: '읽음', sendTime: '2023-09-04 08:30' },
    { id: 5, sender: '최민호', subject: '출결 신청', content: '출결 신청 관련 메세지 내용입니다.', status: '읽지 않음', sendTime: '2023-09-05 11:20' }
];

// 모달 열기 함수
function openModal(message) {
    selectedMessage.value = message;
    alarmDisplayDialog.value = true;
}

// 모달 닫기 함수
function closeModal() {
    alarmDisplayDialog.value = false;
    selectedMessage.value = null;
}

const { getPrimary, getSurface, isDarkTheme } = useLayout();

// const products = ref(null);
const products = ref([]);
const display = ref(false);
const product = ref({});
const chartData = ref(null);
const chartOptions = ref(null);
const currentDate = ref(new Date().toLocaleString());

const updateDate = () => {
    currentDate.value = new Date().toLocaleString();
};

// 공지사항 데이터 정의
const announcements = ref([
    {
        id: 1,
        type: '일반',
        title: '새로운 강의 안내',
        content: '새로운 Vue.js 강의가 다음 주부터 시작됩니다. 많은 참여 부탁드립니다.',
        date: '2023-09-01'
    },
    {
        id: 2,
        type: '긴급',
        title: '서버 점검 안내',
        content: '내일 오전 2시부터 4시까지 서버 점검이 있을 예정입니다. 서비스 이용에 참고 바랍니다.',
        date: '2023-09-02'
    },
    {
        id: 3,
        type: '일반',
        title: '서비스 업데이트 안내',
        content: '오는 9월 10일, 새로운 기능과 성능 개선이 포함된 서비스 업데이트가 예정되어 있습니다.',
        date: '2023-09-05'
    },
    {
        id: 4,
        type: '긴급',
        title: '보안 패치 적용 공지',
        content: '최근 발생한 보안 취약점으로 인해 긴급 보안 패치가 적용될 예정입니다. 적용 시간 동안 서비스 이용에 일시적인 불편이 있을 수 있습니다.',
        date: '2023-09-07'
    },
    {
        id: 5,
        type: '일반',
        title: '휴무일 안내',
        content: '추석 연휴로 인해 9월 28일부터 10월 2일까지 고객 지원 서비스가 운영되지 않습니다. 서비스 이용에 참고 바랍니다.',
        date: '2023-09-15'
    },
    {
        id: 6,
        type: '이벤트',
        title: '가을 이벤트 시작!',
        content: '9월 20일부터 10월 10일까지 특별한 가을 이벤트가 진행됩니다. 다양한 혜택을 놓치지 마세요!',
        date: '2023-09-18'
    },
    {
        id: 7,
        type: '공지',
        title: '고객센터 운영시간 변경 안내',
        content: '고객센터 운영 시간이 9월 30일부터 오전 9시~오후 6시로 변경됩니다. 서비스 이용에 불편 없으시길 바랍니다.',
        date: '2023-09-20'
    },
    {
        id: 8,
        type: '긴급',
        title: '서비스 장애 복구 안내',
        content: '금일 발생한 일시적인 서비스 장애가 복구되었습니다. 불편을 끼쳐드린 점 양해 부탁드립니다.',
        date: '2023-09-25'
    },
    {
        id: 9,
        type: '공지',
        title: '개인정보 처리방침 변경 안내',
        content: '개인정보 처리방침이 2023년 10월 1일부터 개정됩니다. 자세한 내용은 홈페이지를 참고해 주세요.',
        date: '2023-09-27'
    },
    {
        id: 10,
        type: '이벤트',
        title: '회원 대상 감사 이벤트!',
        content: '저희 서비스를 이용해 주시는 모든 회원분들을 위한 특별 감사 이벤트가 10월 1일부터 시작됩니다!',
        date: '2023-09-30'
    },
    {
        id: 11,
        type: '공지',
        title: '시스템 업그레이드 완료 안내',
        content: '금일 시스템 업그레이드가 성공적으로 완료되었습니다. 이제 더욱 향상된 서비스 속도를 경험하실 수 있습니다.',
        date: '2023-10-02'
    },
    {
        id: 12,
        type: '긴급',
        title: '정기 점검 일정 변경 안내',
        content: '예정되었던 정기 서버 점검 일정이 10월 5일로 변경되었습니다. 변경된 일정에 맞춰 서비스 이용에 불편이 없도록 협조 부탁드립니다.',
        date: '2023-10-03'
    }

    // 추가 공지사항 항목들...
]);

// Dialog와 선택된 공지사항을 위한 상태 관리
const displayDialog = ref(false);
const selectedAnnouncement = ref(null);

// Dialog 열기 함수
function openDialog(announcement) {
    selectedAnnouncement.value = announcement;
    displayDialog.value = true;
}

// Dialog 닫기 함수
function closeDialog() {
    displayDialog.value = false;
    selectedAnnouncement.value = null;
}

const items = ref([
    { label: 'Add New', icon: 'pi pi-fw pi-plus' },
    { label: 'Remove', icon: 'pi pi-fw pi-trash' }
]);

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

// const formatCurrency = (value) => {
//     return value.toLocaleString('en-US', { style: 'currency', currency: 'USD' });
// };

// Function to format the title (example: make it uppercase)
const formatTitle = (title) => {
    // Example of formatting: convert title to uppercase
    return title ? title.toUpperCase() : '';
};

watch([getPrimary, getSurface, isDarkTheme], () => {
    chartData.value = setChartData();
    chartOptions.value = setChartOptions();
});
</script>

<template>
    <div class="grid grid-cols-12 gap-8">
        <!-- 기존 카드들 유지 -->
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">출근시간</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">30명</div>
                    </div>
                    <div class="flex items-center justify-center bg-blue-100 dark:bg-blue-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-check-circle text-blue-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ currentDate }}</span>
            </div>
        </div>

        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">퇴근시간</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">1명</div>
                    </div>
                    <div class="flex items-center justify-center bg-orange-100 dark:bg-orange-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-map text-orange-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ currentDate }}</span>
            </div>
        </div>

        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">휴가 잔여 일수</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">3일</div>
                    </div>
                    <div class="flex items-center justify-center bg-cyan-100 dark:bg-cyan-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-folder-open text-cyan-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ currentDate }}</span>
            </div>
        </div>

        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">급여</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">0명</div>
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
                <div class="font-semibold text-xl mb-4">공지사항</div>
                <DataTable :value="announcements" :rows="5" :paginator="true" responsiveLayout="scroll">
                    <!-- Type 컬럼 -->
                    <Column field="type" header="Type" style="width: 20%">
                        <template #body="slotProps">
                            <span>{{ slotProps.data.type }}</span>
                        </template>
                    </Column>

                    <!-- Title 컬럼 -->
                    <Column field="title" header="Title" style="width: 50%">
                        <template #body="slotProps">
                            <span>{{ slotProps.data.title }}</span>
                        </template>
                    </Column>

                    <!-- View 버튼 컬럼 -->
                    <Column style="width: 15%" header="View">
                        <template #body="slotProps">
                            <Button icon="pi pi-search" type="button" class="p-button-text" @click="openDialog(slotProps.data)" />
                        </template>
                    </Column>
                </DataTable>

                <!-- 공지사항 Dialog -->
                <Dialog header="공지사항" v-model:visible="displayDialog" pt:mask:class="backdrop-blur-md" :breakpoints="{ '960px': '75vw' }" :style="{ width: '30vw' }" :modal="true">
                    <template v-if="selectedAnnouncement">
                        <p class="text-lg font-bold">{{ selectedAnnouncement.title }}</p>
                        <p class="text-sm text-gray-500">{{ selectedAnnouncement.date }}</p>
                        <p class="mt-4 leading-normal">{{ selectedAnnouncement.content }}</p>
                    </template>
                    <template #footer>
                        <Button label="Close" @click="closeDialog" />
                    </template>
                </Dialog>
            </div>
        </div>

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
