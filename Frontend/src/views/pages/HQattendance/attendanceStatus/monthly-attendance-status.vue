<template>
    <div class="card">
        <div class="flex justify-between items-center mb-4">
            <h2 class="font-semibold text-xl">월 근태 현황</h2>
            <!-- 엑셀 내보내기 버튼을 오른쪽으로 이동 -->
            <Button label="CSV 내보내기" icon="pi pi-file-excel" class="p-button-success" @click="exportToExcel" />
        </div>
        <DataTable
            :value="filteredAttendanceRecords"
            :paginator="true"
            :rows="10"
            removableSort
            dataKey="attendanceId"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            selectionMode="single"
            :globalFilterFields="['employeeName', 'attendanceDate', 'checkInTime', 'checkOutTime', 'totalHours', 'status']"
            showGridlines
        >
            <template #header>
                <div class="flex justify-between items-center">
                    <!-- 선택된 월에 따라 필터링 -->
                    <Dropdown v-model="selectedMonth" :options="months" optionLabel="name" optionValue="value" placeholder="월 선택" class="ml-4" @change="filterByMonth" />

                    <!-- 검색 기능을 오른쪽으로 이동 -->
                    <div class="relative search-container">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="검색어를 입력해주세요" class="pl-8 search-input" />
                    </div>
                </div>
            </template>
            <template #empty> 근태 기록이 없습니다. </template>

            <Column field="employeeName" header="직원 이름" style="min-width: 12rem" />
            <Column field="attendanceDate" sortable header="출근 날짜" style="min-width: 12rem" />
            <Column field="checkInTime" sortable header="출근 시간" style="min-width: 12rem" />
            <Column field="checkOutTime" sortable header="퇴근 시간" style="min-width: 12rem" />
            <Column field="totalHours" sortable header="총 근무 시간" style="min-width: 12rem" />
            <Column field="status" sortable header="상태" style="min-width: 12rem" />
        </DataTable>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { fetchGet } from '@/views/pages/auth/service/AuthApiService';
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';
import * as XLSX from 'xlsx';

const attendanceRecords = ref([]);
const filteredAttendanceRecords = ref([]);
const filters = ref(null);
const authStore = useAuthStore();

const selectedMonth = ref(null);
const months = ref([
    { name: '전체', value: null },
    { name: '1월', value: 1 },
    { name: '2월', value: 2 },
    { name: '3월', value: 3 },
    { name: '4월', value: 4 },
    { name: '5월', value: 5 },
    { name: '6월', value: 6 },
    { name: '7월', value: 7 },
    { name: '8월', value: 8 },
    { name: '9월', value: 9 },
    { name: '10월', value: 10 },
    { name: '11월', value: 11 },
    { name: '12월', value: 12 }
]);

async function fetchAttendanceRecords() {
    try {
        const employeeId = authStore.employeeData.employeeId;
        const response = await fetchGet(`https://hq-heroes-api.com/api/v1/attendance/my-attendance?employeeId=${employeeId}`);

        if (Array.isArray(response)) {
            attendanceRecords.value = response.map((record) => ({
                attendanceId: record.attendanceId,
                employeeName: record.employeeName,
                attendanceDate: record.checkIn.split('T')[0],
                checkInTime: record.checkIn.split('T')[1].split('.')[0],
                checkOutTime: record.checkOut ? record.checkOut.split('T')[1].split('.')[0] : '-',
                totalHours: record.checkOut ? calculateTotalHours(record.checkIn, record.checkOut) : '-',
                status: mapStatus(record.status)
            }));
        } else {
            attendanceRecords.value = [];
        }
    } catch (error) {
        console.error('근태 데이터를 가져오는 중 오류 발생:', error);
        attendanceRecords.value = [];
    }

    filteredAttendanceRecords.value = attendanceRecords.value;
}

function initFilters() {
    filters.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        employeeName: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }] },
        attendanceDate: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        checkInTime: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        checkOutTime: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        totalHours: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        status: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] }
    };
}

function calculateTotalHours(checkIn, checkOut) {
    const checkInTime = new Date(checkIn);
    const checkOutTime = new Date(checkOut);
    const diffInMs = checkOutTime - checkInTime;

    const hours = Math.floor(diffInMs / (1000 * 60 * 60));
    const minutes = Math.floor((diffInMs % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diffInMs % (1000 * 60)) / 1000);

    return `${hours}시간 ${minutes}분 ${seconds}초`;
}

function mapStatus(status) {
    switch (status) {
        case 'NORMAL':
            return '출근';
        case 'LATE':
            return '지각';
        case 'ABSENT':
            return '결석';
        case 'LEAVE_WORK':
            return '퇴근';
        default:
            return '알 수 없음';
    }
}

function filterByMonth() {
    if (selectedMonth.value !== null) {
        filteredAttendanceRecords.value = attendanceRecords.value.filter((record) => {
            const month = new Date(record.attendanceDate).getMonth() + 1;
            return month === selectedMonth.value;
        });
    } else {
        filteredAttendanceRecords.value = attendanceRecords.value;
    }
}

function exportToExcel() {
    const worksheet = XLSX.utils.json_to_sheet(filteredAttendanceRecords.value);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Attendance');
    XLSX.writeFile(workbook, 'attendance_records.xlsx');
}

onBeforeMount(() => {
    fetchAttendanceRecords();
    initFilters();
});
</script>

<style scoped>
.card {
    padding: 16px;
    border-radius: 12px;
    background-color: #ffffff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-container {
    display: flex;
    align-items: center;
    position: relative;
}

.search-icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
    color: #888;
}

.search-input {
    padding-left: 2.5rem;
}

.p-button-success {
    background-color: #6366f1;
    border-color: #6366f1;
    color: white;
}

.p-button-success:hover {
    background-color: #4f46e5 !important; /* 조금 더 짙은 파란색 */
    border-color: #4f46e5 !important;
}
</style>
