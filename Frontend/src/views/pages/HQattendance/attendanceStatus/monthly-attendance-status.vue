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
                    <!-- 월 선택을 검색창 대신 왼쪽에 배치 -->
                    <Dropdown v-model="selectedMonth" :options="months" optionLabel="name" optionValue="value" placeholder="월 선택" class="ml-4" @change="filterByMonth" />

                    <!-- 검색 기능을 오른쪽으로 이동 -->
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
import { useAuthStore } from '@/stores/authStore'; // 인증된 사용자의 정보를 가져오기 위해 추가
import { fetchGet } from '@/views/pages/auth/service/AuthApiService'; // 직접 작성한 fetchGet 함수 가져오기
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown'; // PrimeVue Dropdown 컴포넌트 추가
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';

const attendanceRecords = ref([]);
const filteredAttendanceRecords = ref([]);
const filters = ref(null);
const authStore = useAuthStore(); // 인증된 사용자 정보 사용

const selectedMonth = ref(null); // 선택된 월을 저장하는 변수
const months = ref([
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
        // 인증된 사용자의 employeeId를 이용해 근태 기록 가져오기
        const employeeId = authStore.employeeData.employeeId;
        const response = await fetchGet(`http://localhost:8080/api/v1/attendance/my-attendance?employeeId=${employeeId}`); // fetchGet 사용

        if (Array.isArray(response)) {
            attendanceRecords.value = response.map((record) => ({
                attendanceId: record.attendanceId,
                employeeName: record.employeeName, // 서버에서 받은 직원 이름 사용
                attendanceDate: record.checkIn.split('T')[0], // 날짜 추출
                checkInTime: record.checkIn.split('T')[1].split('.')[0], // 출근 시간 추출, 초에서 소숫점 아래 버림
                checkOutTime: record.checkOut ? record.checkOut.split('T')[1].split('.')[0] : '-', // 퇴근 시간이 없을 경우 '-'
                totalHours: record.checkOut ? calculateTotalHours(record.checkIn, record.checkOut) : '-', // 퇴근 시간이 없을 경우 '-'
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
        case 'LEAVE_WORK': // 서버에서 반환하는 퇴근 상태값 추가
            return '퇴근';
        default:
            return '알 수 없음'; // 예상치 못한 상태에 대한 기본 처리
    }
}

// 선택한 월에 따라 필터링하는 함수
function filterByMonth() {
    if (selectedMonth.value !== null) {
        filteredAttendanceRecords.value = attendanceRecords.value.filter((record) => {
            const month = new Date(record.attendanceDate).getMonth() + 1; // JavaScript의 월은 0부터 시작하므로 +1
            return month === selectedMonth.value;
        });
    } else {
        filteredAttendanceRecords.value = attendanceRecords.value; // 월 선택이 해제되면 모든 기록 표시
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
