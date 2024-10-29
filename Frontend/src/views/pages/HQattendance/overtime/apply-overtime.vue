<template>
    <div class="overtime-page">
        <h2 class="title">연장 근로 신청</h2>
        <div class="col-span-12 xl:col-span-6">
            <div class="card">
                <div class="form-container">
                    <!-- 로그인된 사용자 이름을 출력 -->
                    <div class="logged-in-user">
                        <p>
                            로그인한 사용자: <strong>{{ employeeData.employeeName }}</strong>
                            <br />
                            이번 달 연장근로 시간: <strong>{{ totalOvertimeHours }}</strong>
                            <br />
                            잔여 연장근로 시간: <strong>{{ remainingOvertimeHours }}</strong>
                        </p>
                    </div>

                    <div class="time-section">
                        <div class="time-block">
                            <label for="startDate" class="label">시작 날짜</label>
                            <input type="date" id="startDate" v-model="form.overtimeStartDate" class="input" />
                        </div>
                        <div class="time-block">
                            <label for="endDate" class="label">종료 날짜</label>
                            <input type="date" id="endDate" v-model="form.overtimeEndDate" class="input" />
                        </div>
                    </div>

                    <!-- 쿼터 선택 필드 -->
                    <div class="time-section">
                        <div class="time-block">
                            <label for="startTime" class="label">시작 시간</label>
                            <input type="time" id="startTime" v-model="form.overtimeStartTime" class="input" />
                        </div>
                        <div class="time-block">
                            <label for="endTime" class="label">종료 시간</label>
                            <input type="time" id="endTime" v-model="form.overtimeEndTime" class="input" @input="checkOvertimeExceed" />
                            <!-- 잔여 시간을 초과하면 경고 표시 -->
                            <p v-if="overtimeExceed" class="error-message">잔여 시간을 초과했습니다!</p>
                        </div>
                    </div>

                    <div class="name-section">
                        <div class="name-block">
                            <label for="applicant" class="label">신청인</label>
                            <input type="text" id="applicant" v-model="form.employeeName" class="input" :placeholder="employeeData.employeeName" />
                        </div>
                        <div class="name-block">
                            <label for="approver" class="label">결재자</label>
                            <input type="text" id="approver" v-model="form.approverName" class="input" placeholder="결재자 이름" />
                        </div>
                    </div>
                </div>
                <div class="comment-section">
                    <label for="comment" class="label">사유:</label>
                    <textarea id="comment" v-model="form.comment" class="textarea" rows="4" placeholder="사유를 작성하세요."></textarea>
                </div>
                <div class="button-container">
                    <button @click="submitForm" class="submit-button" :disabled="overtimeExceed">제출</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService'; // 메서드 가져오기
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService';

const form = ref({
    overtimeStartDate: '', // 기본값: 오늘 날짜
    overtimeEndDate: '', // 기본값: 오늘 날짜
    overtimeStartTime: '18:00', // 시작 시간 기본값
    overtimeEndTime: '19:00', // 종료 시간 기본값
    employeeName: '', // 신청인 이름
    approverName: '', // 결재자 이름
    comment: ''
});

const employeeData = ref({
    employeeName: '',
    teamName: '',
    employeeId: ''
});

const totalOvertimeHours = ref(''); // 총 연장 근로 시간
const remainingOvertimeHours = ref(''); // 잔여 연장 근로 시간 (최대 10시간)
const overtimeExceed = ref(false); // 잔여 시간을 초과했는지 여부

const MAX_OVERTIME_HOURS = 10 * 60; // 최대 연장 근로 시간 (10시간 -> 600분)

// 오늘 날짜를 yyyy-mm-dd 형식으로 변환하는 함수
const getTodayDate = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
    const day = String(today.getDate()).padStart(2, '0'); // 일이 한 자리수일 경우 0을 채움
    return `${year}-${month}-${day}`;
};

// 잔여 시간을 초과했는지 체크하는 함수
const checkOvertimeExceed = () => {
    const startTimeInMinutes = calculateTimeInMinutes(form.value.overtimeStartTime);
    const endTimeInMinutes = calculateTimeInMinutes(form.value.overtimeEndTime);
    const overtimeMinutes = endTimeInMinutes - startTimeInMinutes;

    // 잔여 시간을 초과하는지 여부 확인
    overtimeExceed.value = overtimeMinutes > MAX_OVERTIME_HOURS - totalOvertimeHours.value;
};

// 로그인된 사용자 정보를 가져오는 함수
const loadEmployeeData = async () => {
    const employeeId = window.localStorage.getItem('employeeId');
    if (employeeId) {
        const data = await getLoginEmployeeInfo(employeeId);
        if (data) {
            employeeData.value = data;
            await loadTotalOvertimeHours(employeeId); // 연장근로 시간 조회
        }
    }
};

// 총 분을 시간과 분으로 변환하는 함수
const formatMinutesToHoursAndMinutes = (totalMinutes) => {
    const hours = Math.floor(totalMinutes / 60); // 총 분을 60으로 나누어 시간 계산
    const minutes = totalMinutes % 60; // 나머지 분 계산
    return `${hours}시간 ${minutes}분`;
};

// 총 시간을 분으로 계산하는 함수
const calculateTimeInMinutes = (time) => {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
};

// 백엔드에서 연장근로 시간을 가져오는 함수
const loadTotalOvertimeHours = async (employeeId) => {
    const yearMonth = new Date().toISOString().slice(0, 7); // 현재 연도와 월을 yyyy-MM 형식으로 추출
    try {
        const url = `http://localhost:8080/api/v1/overtime/total-overtime?employeeId=${employeeId}&yearMonth=${yearMonth}`;
        const response = await fetchGet(url);
        const totalMinutes = response.data; // 백엔드에서 받은 총 연장 근로 시간을 분 단위로 저장
        totalOvertimeHours.value = formatMinutesToHoursAndMinutes(totalMinutes); // 형식화된 연장 근로 시간
        remainingOvertimeHours.value = formatMinutesToHoursAndMinutes(MAX_OVERTIME_HOURS - totalMinutes); // 잔여 연장 근로 시간
    } catch (error) {
        console.error('연장근로 시간 조회 중 오류가 발생했습니다:', error);
    }
};

// 페이지 로드 시 데이터 초기화
onMounted(() => {
    form.value.overtimeStartDate = getTodayDate();
    form.value.overtimeEndDate = getTodayDate();
    loadEmployeeData(); // 로그인된 사용자 정보 로드
});

const submitForm = async () => {
    try {
        // overtimeStart와 overtimeEnd를 각각 Date와 Time으로 분리해서 보냄
        const requestBody = {
            employeeId: employeeData.value.employeeId, // 로그인된 사용자 ID 포함
            approverId: employeeData.value.approverId, // 결재자 ID 추가
            overtimeStartDate: form.value.overtimeStartDate, // 시작 날짜
            overtimeStartTime: form.value.overtimeStartTime || '00:00', // 시작 시간
            overtimeEndDate: form.value.overtimeEndDate, // 종료 날짜
            overtimeEndTime: form.value.overtimeEndTime || '23:59', // 종료 시간
            employeeName: form.value.employeeName, // 신청인 이름
            approverName: form.value.approverName, // 결재자 이름
            comment: form.value.comment // 사유
        };

        await fetchPost('http://localhost:8080/api/v1/overtime/submit', requestBody);

        Swal.fire({
            icon: 'success',
            title: '연장 근로 신청이 완료되었습니다.',
            showConfirmButton: false,
            timer: 1500
        });
    } catch (error) {
        console.error('연장 근로 신청 중 오류가 발생했습니다:', error);
        Swal.fire({
            icon: 'error',
            title: '오류',
            text: '연장 근로 신청 중 오류가 발생했습니다. 다시 시도해주세요.',
            confirmButtonText: '확인'
        });
    }
};
</script>

<style scoped>
/* 기존 스타일 유지 */
.overtime-page {
    padding: 20px 40px;
    width: 100%;
    max-width: 100%;
    margin: 0 auto;
    background-color: #ffffff;
    border-radius: 10px;
}

.title {
    font-size: 24px;
    font-weight: bold;
}

.form-container {
    border: 1px solid #ddd;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
}

.label {
    display: block;
    margin-bottom: 10px;
    font-weight: bold;
}

.select,
.input,
.textarea {
    width: 100%;
    padding: 10px;
    margin-bottom: 20px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.time-section {
    display: flex;
    justify-content: space-between;
    gap: 20px;
}

.time-block {
    flex: 1;
}

.name-section {
    display: flex;
    justify-content: space-between;
    gap: 20px;
}

.name-block {
    flex: 1;
}

.comment-section {
    margin-top: 20px;
}

.button-container {
    display: flex;
    justify-content: flex-end;
}

.submit-button {
    background-color: #6366f1;
    color: white;
    border: none;
    border-radius: 5px;
    padding: 10px 15px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.submit-button:hover {
    background-color: #4f46e5;
}

.submit-button:disabled {
    background-color: #b0b0ff;
    cursor: not-allowed;
}

.logged-in-user {
    margin-bottom: 20px;
}

.error-message {
    color: red;
}
</style>
