<template>
    <div class="card">
        <h2 class="font-semibold text-xl mb-4">월 근태 현황</h2>
        <DataTable :value="attendanceRecords" removableSort :paginator="true" :rows="10" dataKey="attendanceId" :rowHover="true">
            <Column field="employeeName" sortable header="직원 이름" />
            <Column field="attendanceDate" sortable header="출근 날짜" />
            <Column field="checkInTime" sortable header="출근 시간" />
            <Column field="checkOutTime" sortable header="퇴근 시간" />
            <Column field="totalHours" sortable header="총 근무 시간" />
            <Column field="status" sortable header="상태" />
        </DataTable>

        <!-- 데이터가 없을 경우 표시할 메시지 -->
        <p v-if="attendanceRecords.length === 0">근태 기록이 없습니다.</p>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';

// 근태 기록 리스트 (예시 데이터)
const attendanceRecords = ref([
    {
        attendanceId: 1,
        employeeName: '홍길동',
        attendanceDate: '2023-09-01',
        checkInTime: '09:00',
        checkOutTime: '17:00',
        totalHours: '8시간',
        status: '정상'
    },
    {
        attendanceId: 2,
        employeeName: '이순신',
        attendanceDate: '2023-09-02',
        checkInTime: '09:30',
        checkOutTime: '17:30',
        totalHours: '8시간',
        status: '정상'
    }
]);
</script>

<style scoped>
.card {
    padding: 16px;
    border-radius: 12px;
    background-color: #ffffff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
