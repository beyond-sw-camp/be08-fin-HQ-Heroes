<script setup>
import { useLayout } from '@/layout/composables/layout';
import { ProductService } from '@/service/ProductService';
import { onMounted, ref, watch, onUnmounted } from 'vue';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';

const alarmDisplayDialog = ref(false);
const selectedNotification = ref(null);
const showMore = ref(false);

// 승인 버튼 클릭 처리
function approve() {
    console.log(`승인: ${selectedNotification.value?.text}`);
    closeModal();
}

// 거부 버튼 클릭 처리
function reject() {
    console.log(`거부: ${selectedNotification.value?.text}`);
    closeModal();
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
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">연차</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl"></div>
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
                        <span class="block text-muted-color font-medium mb-4">가족돌봄</span>
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
                        <span class="block text-muted-color font-medium mb-4">난임 치료</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">0명</div>
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
                        <span class="block text-muted-color font-medium mb-4">결혼 - 본인</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">0명</div>
                    </div>
                    <div class="flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-envelope text-purple-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ currentDate }}</span>
            </div>
        </div>
        <div class="col-span-12 lg:col-span-6 xl:col-span-3">
            <div class="card mb-0">
                <div class="flex justify-between mb-4">
                    <div>
                        <span class="block text-muted-color font-medium mb-4">결혼 - 자녀</span>
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
                        <span class="block text-muted-color font-medium mb-4">리프레시</span>
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
                        <span class="block text-muted-color font-medium mb-4">병가</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">0명</div>
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
                        <span class="block text-muted-color font-medium mb-4">비상</span>
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">0명</div>
                    </div>
                    <div class="flex items-center justify-center bg-purple-100 dark:bg-purple-400/10 rounded-border" style="width: 2.5rem; height: 2.5rem">
                        <i class="pi pi-envelope text-purple-500 !text-xl"></i>
                    </div>
                </div>
                <span class="text-muted-color">{{ currentDate }}</span>
            </div>
        </div>
        <!-- 알림 승인/거부 모달 -->
        <Dialog header="승인/거부" pt:mask:class="backdrop-blur-md" v-model:visible="alarmDisplayDialog" :style="{ width: '30vw' }" modal @hide="removeBlurEffect">
            <p>{{ selectedNotification?.text }}</p>
            <div class="flex justify-end gap-2 mt-4">
                <Button label="승인" icon="pi pi-check" @click="approve" class="p-button-success" />
                <Button label="거부" icon="pi pi-times" @click="reject" class="p-button-danger" />
            </div>
        </Dialog>
    </div> 
</template>
