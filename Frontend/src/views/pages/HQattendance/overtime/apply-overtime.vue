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
                            잔여 연장근로 시간: <strong>{{ remainingOvertimeHours }}</strong>
                        </p>
                    </div>

                    <div class="time-section">
                        <div class="time-block">
                            <label for="startDate" class="label">시작 날짜</label>
                            <input type="date" id="startDate" v-model="form.overtimeStartDate" class="input" @input="checkDateInvalid" />
                        </div>
                        <div class="time-block">
                            <label for="endDate" class="label">
                                종료 날짜
                                <small v-if="isDateInvalid" class="text-red-500 ml-2">시작 날짜는 종료 날짜보다 이전이어야 합니다.</small>
                            </label>
                            <input type="date" id="endDate" v-model="form.overtimeEndDate" class="input" @input="checkDateInvalid" />
                        </div>
                    </div>

                    <!-- 쿼터 선택 필드 -->
                    <div class="time-section">
                        <div class="time-block">
                            <label for="startTime" class="label">시작 시간</label>
                            <!-- 시작 시간: 18:00부터 1시간 단위로 -->
                            <select id="startTime" v-model="form.overtimeStartTime" class="input">
                                <option value="18:00">1타임(18:00 ~ 19:00)</option>
                                <option value="19:00">2타임(19:00 ~ 20:00)</option>
                                <option value="20:00">3타임(20:00 ~ 21:00)</option>
                                <option value="21:00">4타임(21:00 ~ 22:00)</option>
                                <option value="22:00">5타임(22:00 ~ 23:00)</option>
                                <option value="23:00">6타임(23:00 ~ 00:00)</option>
                                <option value="00:00">7타임(00:00 ~ 01:00)</option>
                            </select>
                        </div>
                        <div class="time-block">
                            <label for="endTime" class="label">종료 시간</label>
                            <!-- 종료 시간: 19:00부터 1시간 단위로 -->
                            <select id="endTime" v-model="form.overtimeEndTime" class="input" @change="checkOvertimeExceed">
                                <option value="19:00">1타임(18:00 ~ 19:00)</option>
                                <option value="20:00">2타임(19:00 ~ 20:00)</option>
                                <option value="21:00">3타임(20:00 ~ 21:00)</option>
                                <option value="22:00">4타임(21:00 ~ 22:00)</option>
                                <option value="23:00">5타임(22:00 ~ 23:00)</option>
                                <option value="00:00">6타임(23:00 ~ 00:00)</option>
                                <option value="01:00">7타임(00:00 ~ 01:00)</option>
                            </select>
                        </div>
                    </div>

                    <div class="name-section">
                        <div class="name-block">
                            <label for="applicant" class="label">신청인</label>
                            <!-- TreeSelect로 신청인 선택 -->
                            <TreeSelect v-model="form.applicantName" :options="employeeTreeData" @node-select="handleEmployeeChange" optionLabel="label" selectionMode="single" class="input" :placeholder="form.applicantName || '신청인을 선택하세요'">
                                <template #default="slotProps">
                                    <div class="flex items-center">
                                        <Avatar v-if="slotProps.node.key.startsWith('emp-') && !slotProps.node.profileImageUrl" label="X" size="normal" shape="circle" class="mr-2" style="background-color: #dee9fc; color: #1a2551" />
                                        <Avatar v-else-if="slotProps.node.key.startsWith('emp-')" :image="slotProps.node.profileImageUrl" size="normal" shape="circle" class="mr-2" />
                                        <span>{{ slotProps.node.label }}</span>
                                    </div>
                                </template>
                            </TreeSelect>
                        </div>
                        <div class="name-block">
                            <label for="approver" class="label">결재자</label>
                            <!-- TreeSelect로 결재자 선택 -->
                            <TreeSelect v-model="form.approverName" :options="approverTreeData" @node-select="handleApproverChange" optionLabel="label" selectionMode="single" class="input" placeholder="결재자를 선택하세요">
                                <template #default="slotProps">
                                    <div class="flex items-center">
                                        <Avatar v-if="slotProps.node.key.startsWith('emp-') && !slotProps.node.profileImageUrl" label="X" size="normal" shape="circle" class="mr-2" style="background-color: #dee9fc; color: #1a2551" />
                                        <Avatar v-else-if="slotProps.node.key.startsWith('emp-')" :image="slotProps.node.profileImageUrl" size="normal" shape="circle" class="mr-2" />
                                        <span>{{ slotProps.node.label }}</span>
                                    </div>
                                </template>
                            </TreeSelect>
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
import Avatar from 'primevue/avatar';
import TreeSelect from 'primevue/treeselect';
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService';

const router = useRouter();

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

const employeeTreeData = ref([]);
const approverTreeData = ref([]);

const selectedApplicantName = ref(''); // 신청인의 이름
const selectedApplicantId = ref(''); // 신청인의 ID
const selectedApproverName = ref(''); // 결재자의 이름
const selectedApproverId = ref(''); // 결재자의 ID

const totalOvertimeHours = ref(0); // 숫자형 초기값
const remainingOvertimeHours = ref(0); // 숫자형 초기값
const overtimeExceed = ref(false); // 잔여 시간을 초과했는지 여부

const isDateInvalid = ref(false); // 날짜 유효성 상태 변수

// 날짜 유효성 검사 함수
const checkDateInvalid = () => {
    const startDate = new Date(form.value.overtimeStartDate);
    const endDate = new Date(form.value.overtimeEndDate);
    isDateInvalid.value = startDate > endDate; // 시작 날짜가 종료 날짜보다 이후일 때 경고 표시
};

// 잔여 시간을 초과했는지 체크하는 함수
const checkOvertimeExceed = () => {
    // 시작 시간과 종료 시간에서 각 타임의 시간을 분 단위로 계산
    const startTimeInMinutes = calculateTimeInMinutes(form.value.overtimeStartTime);
    const endTimeInMinutes = calculateTimeInMinutes(form.value.overtimeEndTime);
    const selectedOvertimeMinutes = endTimeInMinutes - startTimeInMinutes;

    // 남은 연장 근로 시간을 분 단위로 변환하여 초과 여부 확인
    const remainingMinutes = parseInt(remainingOvertimeHours.value.split('시간')[0]) * 60; // 남은 시간을 분으로 계산

    // 초과 여부 확인
    if (selectedOvertimeMinutes > remainingMinutes) {
        overtimeExceed.value = true;

        // 초과 시 경고 알림
        Swal.fire({
            icon: 'error',
            title: '제출 불가',
            text: '선택한 시간이 잔여 연장 근로 시간을 초과했습니다.',
            confirmButtonText: '확인'
        });
    } else {
        overtimeExceed.value = false;
    }
};

// 로그인된 사용자 정보를 가져오는 함수
const loadEmployeeData = async () => {
    const employeeId = window.localStorage.getItem('employeeId');
    if (employeeId) {
        const data = await getLoginEmployeeInfo(employeeId);
        if (data) {
            employeeData.value = data;
            await loadTotalOvertimeHours(employeeId); // 연장근로 시간 조회
            await loadRemainingOvertimeHours(employeeId); // 잔여 연장근로 시간 조회 추가
        }
    }
};

// TreeSelect에 필요한 트리 모델 변환 함수
const convertToTreeModel = (data) => {
    const departments = data.reduce((acc, employee) => {
        const dept = acc.find((d) => d.label === employee.deptName);
        if (!dept) {
            acc.push({
                key: `dept-${employee.deptName}`,
                label: employee.deptName,
                icon: 'pi pi-building',
                children: []
            });
        }
        const deptIndex = acc.findIndex((d) => d.label === employee.deptName);
        const team = acc[deptIndex].children.find((t) => t.label === employee.teamName);
        if (!team) {
            acc[deptIndex].children.push({
                key: `team-${employee.teamName}`,
                label: employee.teamName,
                icon: 'pi pi-users',
                children: []
            });
        }
        const teamIndex = acc[deptIndex].children.findIndex((t) => t.label === employee.teamName);
        acc[deptIndex].children[teamIndex].children.push({
            key: `emp-${employee.employeeId}`,
            label: employee.employeeName,
            profileImageUrl: employee.profileImageUrl,
            jobName: employee.jobName,
            positionName: employee.positionName
        });
        return acc;
    }, []);
    return departments;
};

// 신청인 목록 로드
const loadEmployeeTreeData = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/employees');

        // 로그인된 사용자의 팀 이름으로 필터링
        const filteredEmployees = response.filter((employee) => employee.teamName === employeeData.value.teamName);

        // 필터링된 데이터를 트리 구조로 변환
        employeeTreeData.value = convertToTreeModel(filteredEmployees);
    } catch (error) {
        console.error('Error fetching employee data:', error);
    }
};

// 팀장만 포함된 결재자 목록 로드
const loadApproverTreeData = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/employees');
        if (response) {
            // 부서명이 같고 "팀장"인 경우만 필터링 (팀장만 포함)
            const filteredApprovers = response.filter(
                (employee) =>
                    employee.deptName === employeeData.value.deptName && // 같은 부서
                    employee.positionName === '팀장' // 팀장 직위만 포함
            );
            approverTreeData.value = convertToTreeModel(filteredApprovers); // 필터링된 팀장 목록을 트리 구조로 변환
        }
    } catch (error) {
        console.error('Error fetching approver data:', error);
    }
};

// 총 분을 시간과 분으로 변환하는 함수
const formatMinutesToHoursAndMinutes = (totalMinutes) => {
    if (isNaN(totalMinutes) || totalMinutes === undefined || totalMinutes === null) {
        totalMinutes = 0; // 유효하지 않은 값일 경우 0으로 설정
    }
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
    return `${hours}시간 ${minutes}분`;
};

// 총 시간을 분으로 계산하는 함수
const calculateTimeInMinutes = (time) => {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
};

// 백엔드에서 총 연장근로 시간을 가져오는 함수
const loadTotalOvertimeHours = async (employeeId) => {
    const yearMonth = new Date().toISOString().slice(0, 7); // 현재 연도와 월을 yyyy-MM 형식으로 추출
    try {
        const url = `https://hq-heroes-api.com/api/v1/overtime/total-overtime?employeeId=${employeeId}&yearMonth=${yearMonth}`;
        const responseData = await fetchGet(url);

        // 응답 데이터가 유효하지 않으면 오류 발생
        if (responseData === null || responseData === undefined) {
            console.error('응답 데이터가 없습니다:', responseData);
            throw new Error('Invalid total overtime response');
        }

        const totalMinutes = parseInt(responseData, 10);
        if (isNaN(totalMinutes)) {
            console.error('응답 데이터가 유효하지 않습니다:', responseData);
            throw new Error('Invalid total overtime minutes');
        }

        // 총 연장 근로 시간을 설정
        totalOvertimeHours.value = totalMinutes;
    } catch (error) {
        console.error('연장근로 시간 조회 중 오류가 발생했습니다:', error);
    }
};

// 잔여 연장 근로 시간을 백엔드에서 조회하는 함수
const loadRemainingOvertimeHours = async (employeeId) => {
    const yearMonth = new Date().toISOString().slice(0, 7);
    try {
        const url = `https://hq-heroes-api.com/api/v1/overtime/remaining-overtime?employeeId=${employeeId}&yearMonth=${yearMonth}`;
        const responseData = await fetchGet(url);

        // 응답 데이터가 유효하지 않으면 오류 발생
        if (responseData === null || responseData === undefined) {
            console.error('응답 데이터가 없습니다:', responseData);
            throw new Error('Invalid remaining overtime response');
        }

        const remainingMinutes = parseInt(responseData, 10);
        if (isNaN(remainingMinutes)) {
            console.error('응답 데이터가 유효하지 않습니다:', responseData);
            throw new Error('Invalid remaining overtime minutes');
        }

        remainingOvertimeHours.value = formatMinutesToHoursAndMinutes(remainingMinutes);
        checkOvertimeExceed();
    } catch (error) {
        console.error('잔여 연장근로 시간 조회 중 오류가 발생했습니다:', error);
    }
};

// 페이지 로드 시 데이터 초기화
onMounted(() => {
    form.value.overtimeStartDate = new Date().toISOString().split('T')[0];
    form.value.overtimeEndDate = new Date().toISOString().split('T')[0];
    loadEmployeeData().then(() => {
        form.value.applicantName = employeeData.value.employeeName; // 로그인된 사용자의 이름을 기본값으로 설정
        selectedApplicantName.value = employeeData.value.employeeName;
        selectedApplicantId.value = `emp-${employeeData.value.employeeId}`; // applicantId 설정

        loadEmployeeTreeData(); // 신청자 목록 로드
        loadApproverTreeData(); // 결재자 목록 로드 (팀장만)
    });
});

const handleEmployeeChange = (selectedEmployee) => {
    selectedApplicantId.value = selectedEmployee.key; // 선택된 신청인의 ID 추출
    selectedApplicantName.value = Object(selectedEmployee).label; // 선택된 신청인의 이름 설정
};

const handleApproverChange = (selectedApprover) => {
    selectedApproverId.value = selectedApprover.key; // 선택된 결재자의 ID 추출
    selectedApproverName.value = Object(selectedApprover).label; // 선택된 결재자의 이름 설정
};

const submitForm = async () => {
    try {
        // 잔여 시간을 초과한 경우 제출 중단
        checkOvertimeExceed();

        if (overtimeExceed.value) {
            return;
        }
        // 날짜 유효성 검사
        if (isDateInvalid.value) {
            Swal.fire({
                icon: 'error',
                title: '날짜 오류',
                text: '시작 날짜는 종료 날짜보다 이전이어야 합니다.',
                confirmButtonText: '확인'
            });
            return;
        }

        // `employeeId`, `approverId` 등은 문자열(`String`) 형태로 설정
        const requestBody = {
            employeeId: employeeData.value.employeeId,
            applicantId: selectedApplicantId.value.replace('emp-', ''),
            approverId: selectedApproverId.value.replace('emp-', ''), // 결재자 ID 추가
            employeeName: employeeData.value.employeeName,
            approverName: selectedApproverName.value,
            applicantName: selectedApplicantName.value,
            overtimeStartDate: form.value.overtimeStartDate,
            overtimeStartTime: form.value.overtimeStartTime || '00:00',
            overtimeEndDate: form.value.overtimeEndDate,
            overtimeEndTime: form.value.overtimeEndTime || '23:59',
            comment: form.value.comment
        };

        console.log('Sending request body:', requestBody); // 전송 데이터 확인

        await fetchPost('https://hq-heroes-api.com/api/v1/overtime/submit', requestBody);

        Swal.fire({
            icon: 'success',
            title: '연장 근로 신청이 완료되었습니다.',
            showConfirmButton: false,
            timer: 1500
        });

        await router.push('/status-overtime');
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
