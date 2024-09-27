<template>
    <div class="card">
        <h2 class="font-semibold text-xl mb-4">월 근태 현황</h2>
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
                    <div class="relative search-container">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters['global'].value" placeholder="검색어 입력" class="pl-8 search-input" />
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
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import axios from 'axios';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';

const attendanceRecords = ref([]);
const filteredAttendanceRecords = ref([]);
const filters = ref(null);

async function fetchAttendanceRecords() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/attendance'); // 수정된 경로
        if (Array.isArray(response.data)) {
            attendanceRecords.value = response.data.map((record) => ({
                attendanceId: record.attendanceId,
                employeeName: record.employeeId, // DTO의 employeeId 필드 사용
                attendanceDate: record.checkIn.split('T')[0], // 날짜 추출
                checkInTime: record.checkIn.split('T')[1], // 출근 시간 추출
                checkOutTime: record.checkOut.split('T')[1], // 퇴근 시간 추출
                totalHours: calculateTotalHours(record.checkIn, record.checkOut),
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
    const diffInHours = diffInMs / (1000 * 60 * 60);
    return `${diffInHours.toFixed(2)}시간`; // 소수점 두 자리까지 표시
}

function mapStatus(status) {
    switch (status) {
        case 'NORMAL':
            return '정상';
        case 'LATE':
            return '지각';
        case 'ABSENT':
            return '결석';
        default:
            return '알 수 없음';
    }
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
</style>
