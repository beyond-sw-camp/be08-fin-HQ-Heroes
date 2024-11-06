<template>
    <div class="card">
        <div class="salary-management">
            <div class="header">
                <label class="text-2xl font-bold text-gray-800">급여 명세서</label>
            </div>

            <div class="year-selection">
                <div class="calendar-group">
                    <Calendar v-model="selectedDate" :showIcon="true" :yearRange="yearRange" view="year" dateFormat="yy" @update:model-value="updateSelectedYear" placeholder="년도 선택" class="calendar" />
                </div>
                <div class="export">
                    <Calendar v-model="startDate" :showIcon="true" view="month" dateFormat="yy/mm" placeholder="시작" class="calendar" />
                    <Calendar v-model="endDate" :showIcon="true" view="month" dateFormat="yy/mm" placeholder="종료" class="calendar" />
                    <Button label="CSV 내보내기" class="p-button-primary" @click="exportSalaryDataToCSV" />
                </div>
            </div>

            <div class="salary-cards">
                <div class="grid grid-cols-12 gap-6">
                    <div v-if="monthsList.length === 0" class="col-span-12">
                        <p class="text-center text-gray-500">급여 데이터가 없습니다.</p>
                    </div>
                    <div v-for="month in monthsList" :key="month.id" class="col-span-12 lg:col-span-6 xl:col-span-4">
                        <div class="card p-4 custom-box relative transition-transform duration-200 hover:scale-105">
                            <div class="text-left">
                                <div class="flex items-center justify-between mb-4">
                                    <i :class="month.icon" class="text-gray-600 text-3xl"></i>
                                </div>
                                <span class="block text-muted-color font-medium text-lg">{{ getMonthLabel(new Date(month.salaryMonth).getMonth()) }} 급여</span>
                                <div class="text-surface-900 font-medium text-lg mt-2">
                                    <div>급여 합계 : {{ formatCurrency(month.preTaxTotal) }}</div>
                                    <div>공제액 합계 : {{ formatCurrency(calculateTotalDeductions(month)) }}</div>
                                    <div>실지급액 : {{ formatCurrency(month.postTaxTotal) }}</div>
                                </div>
                                <Button
                                    label="급여내역보기"
                                    class="p-button-secondary mt-4"
                                    style="width: 100%"
                                    @click="
                                        () => {
                                            showSalaryModal(month);
                                        }
                                    "
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <Dialog
                v-model:visible="displayModal"
                :style="{ width: '70vw', marginLeft: 'auto', marginRight: 'auto', marginTop: '1%' }"
                class="salary-dialog"
                @hide="closeSalaryModal"
                :draggable="false"
                pt:mask:class="backdrop-blur-sm"
                :closable="false"
            >
                <template #header>
                    <div class="modal-header">
                        <span class="header-text"> {{ authStore.employeeData.employeeName }} 님의 {{ monthNumber }}월 급여 내역 </span>
                    </div>
                    <button @click="closeSalaryModal" class="x-btn">X</button>
                </template>

                <div class="salary-modal">
                    <div class="salary-details">
                        <div class="left-panel">
                            <h3 class="text-xl font-semibold text-gray-800 mb-4">급여 상세 정보</h3>
                            <div class="info-item">
                                <span>근무시간 :</span>
                                <span>{{ selectedMonth?.workTime }} 시간</span>
                            </div>
                            <div class="info-item">
                                <span>시급 :</span>
                                <span>{{ formatCurrency(selectedMonth?.baseSalary) }}</span>
                            </div>
                            <div class="info-item">
                                <span>기본 급여 :</span>
                                <span>{{ formatCurrency(selectedMonth?.totalSalary) }}</span>
                            </div>
                            <div class="info-item">
                                <span>연장 근로 시간 :</span>
                                <span>{{ selectedMonth?.overTime || 0 }} 시간</span>
                            </div>
                            <div class="info-item" style="margin-bottom: 4rem">
                                <span>연장 근로 수당 :</span>
                                <span>{{ formatCurrency(selectedMonth?.overSalary) }} </span>
                            </div>
                            <div v-if="selectedMonth?.salaryMonth && (getMonthLabel(new Date(selectedMonth.salaryMonth).getMonth()) === '12월' || getMonthLabel(new Date(selectedMonth.salaryMonth).getMonth()) === '6월')" class="info-item">
                                <span>성과급 :</span>
                                <span>{{ formatCurrency(selectedMonth?.bonus) }}</span>
                            </div>
                            <div class="total-deductions font-semibold mt-4">
                                <span>급여 합계 :</span>
                                <span>{{ formatCurrency(selectedMonth?.preTaxTotal) }}</span>
                            </div>
                        </div>

                        <div class="right-panel">
                            <h3 class="text-xl font-semibold text-gray-800 mb-4">공제액</h3>
                            <div v-if="deductions.length">
                                <div v-for="deduction in deductions" :key="deduction.type" class="deduction-item">
                                    <span>{{ deduction.type }} :</span>
                                    <span>{{ formatCurrency(deduction.amount) }}</span>
                                </div>
                            </div>
                            <div v-else>
                                <p>급여 데이터가 없습니다.</p>
                            </div>
                            <div class="total-deductions font-semibold mt-4">
                                <span>공제액 합계 : </span>
                                <span>{{ formatCurrency(totalDeductions) }}</span>
                            </div>
                        </div>
                    </div>

                    <div class="total-info">
                        <div class="total-salary">
                            <div>급여 합계 :</div>
                            <div>{{ formatCurrency(selectedMonth?.preTaxTotal) }}</div>
                        </div>
                        <div class="deduction-amount">
                            <div>공제액 합계 :</div>
                            <div>{{ formatCurrency(totalDeductions) }}</div>
                        </div>
                        <div class="net-amount">
                            <div>실지급액 :</div>
                            <div>{{ formatCurrency(selectedMonth?.postTaxTotal) }}</div>
                        </div>
                    </div>
                </div>
            </Dialog>
        </div>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import Button from 'primevue/button';
import Calendar from 'primevue/calendar';
import { onMounted, ref } from 'vue';
import { fetchSalary } from './salaryService';
const authStore = useAuthStore();

const selectedDate = ref(new Date());
const selectedYear = ref(selectedDate.value.getFullYear());
const displayModal = ref(false);
const selectedMonth = ref({});
const deductions = ref([]);
const totalDeductions = ref(0);
const currentYear = new Date().getFullYear();
const yearRange = `${1900}:${currentYear}`;
const monthsList = ref([]);
const salaryDialogHeader = ref('급여 내역');
const startDate = ref(null); // 선택한 시작일
const endDate = ref(null); // 선택한 종료일
const monthNumber = ref(0); // monthNumber를 ref로 선언

const getMonthLabel = (monthIndex) => {
    const monthNames = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
    return monthNames[monthIndex];
};

// 화폐 포맷팅 함수
const formatCurrency = (value) => {
    return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

// 공제액 합계 계산 함수
const calculateTotalDeductions = (month) => {
    return month.nationalPension + month.healthInsurance + month.longTermCare + month.employmentInsurance + month.incomeTax + month.localIncomeTax;
};

// 선택한 연도에 따라 월별 데이터 가져오기
const updateSelectedYear = async (date) => {
    selectedYear.value = date.getFullYear();
    await loadMonthlyData();
};

// 월별 데이터 가져오기 함수
const loadMonthlyData = async () => {
    try {
        const salaryData = await fetchSalary(authStore.loginUserId);
        if (salaryData && salaryData.length > 0) {
            const filteredData = salaryData.filter((item) => {
                const salaryYear = new Date(item.salaryMonth).getFullYear();
                return salaryYear === selectedYear.value;
            });

            // 필터링된 데이터를 월별 내림차순으로 정렬
            monthsList.value = filteredData.sort((a, b) => new Date(b.salaryMonth) - new Date(a.salaryMonth));
        } else {
            monthsList.value = [];
        }
    } catch (error) {
        console.error('Error loading salary data:', error);
    }
};

// 급여 상세 모달 열기
const showSalaryModal = async (month) => {
    selectedMonth.value = month;

    // 전체 급여 기록 가져오기 (API 호출)
    const salaryData = await fetchSalary(authStore.loginUserId);
    const monthData = salaryData.find((item) => new Date(item.salaryMonth).getMonth() + 1 === new Date(month.salaryMonth).getMonth() + 1);

    if (!monthData) {
        console.error('해당 월의 급여 데이터를 찾을 수 없습니다.');
        return;
    }

    // 모달 헤더 설정
    monthNumber.value = new Date(month.salaryMonth).getMonth() + 1;

    // 월이 유효한지 확인
    if (isNaN(monthNumber)) {
        console.error('Invalid month value');
    } else {
        salaryDialogHeader.value = `${authStore.employeeData.employeeName} 님의 ${monthNumber}월 급여 내역`;
    }

    // 데이터 매핑
    selectedMonth.value.workTime = monthData.workTime;
    selectedMonth.value.baseSalary = monthData.baseSalary;
    selectedMonth.value.overTime = monthData.overTime;
    selectedMonth.value.overSalary = monthData.overSalary;
    selectedMonth.value.totalSalary = monthData.totalSalary;
    selectedMonth.value.bonus = monthData.bonus;

    // 공제 데이터 설정
    await fetchDeductionsData(month.salaryMonth);

    displayModal.value = true;
};

// 공제 데이터 가져오기 함수
const fetchDeductionsData = async (salaryMonth) => {
    deductions.value = [
        { type: '국민연금', amount: selectedMonth.value.nationalPension },
        { type: '건강보험', amount: selectedMonth.value.healthInsurance },
        { type: '장기요양보험', amount: selectedMonth.value.longTermCare },
        { type: '고용보험', amount: selectedMonth.value.employmentInsurance },
        { type: '소득세', amount: selectedMonth.value.incomeTax },
        { type: '지방소득세', amount: selectedMonth.value.localIncomeTax }
    ];

    totalDeductions.value = calculateTotalDeductions(selectedMonth.value); // 공제액 합계
};

// 모달 닫기 함수
const closeSalaryModal = () => {
    displayModal.value = false;
};

// CSV 내보내기 기능
const exportSalaryDataToCSV = async () => {
    if (startDate.value && endDate.value) {
        // 급여 데이터 필터링
        const filteredSalaries = monthsList.value.filter((month) => {
            const salaryDate = new Date(month.salaryMonth);
            return salaryDate >= new Date(startDate.value) && salaryDate <= new Date(endDate.value);
        });

        if (filteredSalaries.length > 0) {
            const csvContent = generateCSVContent(filteredSalaries);
            downloadCSV(csvContent);
        } else {
            console.warn('선택한 날짜 범위에 해당하는 급여 데이터가 없습니다.');
        }
    } else {
        console.warn('시작일과 종료일을 모두 선택해야 합니다.');
    }
};

// CSV 콘텐츠 생성 함수
const generateCSVContent = (salaries) => {
    const header = ['급여 월', '근무시간', '시급', '성과급', '연장근로시간', '연장근로수당', '급여합계', '국민연금', '건강보험', '장기요양보험', '고용보험', '소득세', '지방소득세', '공제액합계', '실지급액'];

    const rows = salaries.map((month) => [
        `${getMonthLabel(new Date(month.salaryMonth).getMonth())}`,
        month.workTime || 0,
        month.baseSalary || 0,
        month.bonus || 0,
        month.overTime || 0,
        month.overSalary || 0,
        month.totalSalary || 0,
        month.nationalPension || 0,
        month.healthInsurance || 0,
        month.longTermCare || 0,
        month.employmentInsurance || 0,
        month.incomeTax || 0,
        month.localIncomeTax || 0,
        calculateTotalDeductions(month) || 0,
        month.postTaxTotal || 0
    ]);

    const csvRows = [
        header.join(','), // 헤더 추가
        ...rows.map((row) => row.join(',')) // 데이터 행 추가
    ];

    return csvRows.join('\n');
};

// CSV 파일 다운로드 함수
const downloadCSV = (csvContent) => {
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `${authStore.employeeData.employeeName}님의_급여내역.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
};

// 컴포넌트 마운트 시 초기 데이터 로드
onMounted(async () => {
    await loadMonthlyData();
});
</script>

<style scoped>
.card {
    border-radius: 16px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.salary-management {
    padding: 1rem;
    animation: fadeIn 0.5s ease-in-out;
}

.header {
    margin-bottom: 2rem;
}

.year-selection {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.export {
    display: flex;
    gap: 0.5rem;
    align-items: center;
}

.salary-cards {
    margin-bottom: 2rem;
    min-height: 27rem;
}

.custom-box {
    border-radius: 1rem;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.2s;
}

.custom-box:hover {
    transform: translateY(-5px);
}

.status-badge {
    position: absolute;
    top: 1rem;
    right: 1rem;
    padding: 0.5rem 1rem;
    border-radius: 1rem;
    font-weight: bold;
    color: white;
}

.PAID {
    background-color: #6366f1;
}

.PENDING {
    background-color: #e0e7ff;
}

.salary-modal {
    display: flex;
    flex-direction: column;
}

.salary-details {
    display: flex;
    padding: 0rem 1rem;
}

.left-panel,
.right-panel {
    width: 50%;
    padding: 1rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    background: #f9fafb;
    margin: 0.5rem;
}

.info-item,
.deduction-item {
    display: flex;
    justify-content: space-between;
    padding: 0.75rem 0;
}

.total-deductions {
    display: flex;
    justify-content: space-between;
    padding: 1rem 0;
    font-weight: bold;
    font-size: 1.1rem;
    border-top: 2px solid #e5e7eb;
    margin-top: 1rem;
    min-height: 3rem;
}

.total-net-pay {
    font-size: 1.25rem;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    margin-right: 1.5rem;
}

/* 실지급액 css 부분 */
.total-info {
    width: 35rem;
    padding: 1rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    background: #f9fafb;
    margin-left: auto;
    margin-right: 1.5rem; /* 원하는 만큼 오른쪽 여백 추가 */
}

.total-info div {
    display: flex;
    justify-content: space-between;
    padding: 0.2rem 0; /* 항목 간 간격 */
    font-weight: bold; /* 글씨를 두껍게 설정 */
    color: black; /* 글씨 색상을 검은색으로 설정 */
    margin-bottom: 0.2rem; /* 항목 사이에 약간의 간격 추가 */
}

.total-info .total-salary div {
    color: #6366f1; /* X 버튼의 파란색 (예시) */
}

.total-info .deduction-amount div {
    color: #ff6b6b; /* 부드러운 빨간색 (예시) */
}

.total-info .net-amount div {
    color: black; /* 실지급액 색상, 원하시는 색상으로 변경 가능 */
}


/* header 부분 스타일 */
.modal-header {
    display: flex; /* Flexbox로 설정 */
    justify-content: space-between; /* 제목과 버튼 사이 공간 조정 */
    align-items: center; /* 수직 정렬 */
    font-size: 1.2rem; /* 글자 크기 조정 */
    font-weight: bold; /* 글씨를 진하게 설정 */
    margin-left: 2rem;
    white-space: nowrap; /* 줄 바꿈 방지 */
    padding-right: 2rem; /* 오른쪽 여백 추가 */
}

.x-btn {
    margin-left: auto; /* 자동 마진을 통해 오른쪽으로 밀기 */
    margin-right: 2rem; /* 오른쪽에서 3rem 띄우기 */
    background: none; /* 배경 제거 */
    border: none; /* 테두리 제거 */
    font-size: 1rem; /* 버튼 크기 */
    cursor: pointer; /* 커서 포인터 변경 */
}

@keyframes fadeIn {
    0% {
        opacity: 0;
        transform: translateY(-10px);
    }
    100% {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>
