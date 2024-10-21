<template>
    <div class="vacation-page">
        <h2 class="title">휴가 신청</h2>
        <div class="col-span-12 xl:col-span-6">
            <div class="card">
                <div class="form-container">
                    <!-- 로그인된 사용자 이름을 출력 -->
                    <div class="logged-in-user">
                        <p>
                            로그인한 사용자: <strong>{{ employeeData.employeeName }}</strong>
                        </p>
                    </div>

                    <label for="type" class="label">휴가 종류:</label>
                    <select id="type" v-model="form.vacationType" class="select" @change="handleVacationTypeChange">
                        <option value="DAY_OFF">월차</option>
                        <option value="HALF_DAY_OFF">반차</option>
                        <option value="SICK_LEAVE">병가</option>
                        <option value="EVENT_LEAVE">경조</option>
                    </select>

                    <div class="time-section">
                        <div class="time-block">
                            <label for="startDate" class="label">시작 날짜</label>
                            <input type="date" id="startDate" v-model="form.vacationStartDate" class="input" />
                        </div>
                        <div class="time-block">
                            <label for="endDate" class="label">종료 날짜</label>
                            <input type="date" id="endDate" v-model="form.vacationEndDate" class="input" />
                        </div>
                    </div>

                    <!-- 휴가 종류가 연차가 아닌 경우에만 쿼터 선택 필드 표시 -->
                    <div class="time-section" v-if="form.vacationType !== 'DAY_OFF'">
                        <div class="time-block">
                            <label for="startTime" class="label">시작 시간 (쿼터)</label>
                            <select id="startTime" v-model="form.vacationStartTime" class="select">
                                <option value="09:00">1쿼터 (09:00 ~ 11:00)</option>
                                <option value="11:00">2쿼터 (11:00 ~ 13:00)</option>
                                <option value="14:00">3쿼터 (14:00 ~ 16:00)</option>
                                <option value="16:00">4쿼터 (16:00 ~ 18:00)</option>
                            </select>
                        </div>
                        <div class="time-block">
                            <label for="endTime" class="label">종료 시간 (쿼터)</label>
                            <select id="endTime" v-model="form.vacationEndTime" class="select">
                                <option value="11:00">1쿼터 (09:00 ~ 11:00)</option>
                                <option value="13:00">2쿼터 (11:00 ~ 13:00)</option>
                                <option value="16:00">3쿼터 (14:00 ~ 16:00)</option>
                                <option value="18:00">4쿼터 (16:00 ~ 18:00)</option>
                            </select>
                        </div>
                    </div>

                    <div class="name-section">
                        <div class="name-block">
                            <label for="applicant" class="label">신청인</label>
                            <!-- TreeSelect로 신청인 선택 -->
                            <TreeSelect v-model="form.employeeName" :options="employeeTreeData" @node-select="handleEmployeeChange" optionLabel="label" selectionMode="single" class="input" placeholder="신청인을 선택하세요">
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
                    <button @click="submitForm" class="submit-button">제출</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService';
import axios from 'axios';
import Avatar from 'primevue/avatar'; // Avatar 컴포넌트
import TreeSelect from 'primevue/treeselect'; // TreeSelect 컴포넌트
import { onMounted, ref } from 'vue';

const form = ref({
    vacationType: 'DAY_OFF', // 기본적으로 월차가 선택됨
    vacationStartDate: '', // 기본값: 오늘 날짜
    vacationEndDate: '', // 기본값: 오늘 날짜
    vacationStartTime: '09:00', // 시작 시간 기본값
    vacationEndTime: '18:00', // 종료 시간 기본값
    employeeName: '', // 신청인 이름
    approverName: '', // 결재자 이름
    approverId: '',
    comment: ''
});

const employeeData = ref({
    employeeName: '',
    teamName: '',
    employeeId: ''
});

const employeeTreeData = ref([]); // 트리 형식의 사원 목록 데이터
const approverTreeData = ref([]); // 트리 형식의 결재자 목록 데이터 (팀장만)

const loadEmployeeData = async () => {
    const employeeId = window.localStorage.getItem('employeeId');
    if (employeeId) {
        const data = await getLoginEmployeeInfo(employeeId);
        if (data) {
            employeeData.value = data;
            form.value.employeeName = data.employeeName; // 신청인 이름 설정
        }
    }
};

// 전체 사원 목록을 트리 구조로 변환하는 함수
const loadEmployeeTreeData = async () => {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/employees');
        // 로그인된 사용자의 부서명을 기준으로 필터링
        const filteredEmployees = response.data.filter(
            (employee) => employee.deptName === employeeData.value.deptName // 부서명이 같은 직원만 포함
        );
        employeeTreeData.value = convertToTreeModel(filteredEmployees); // 필터링된 데이터를 트리 구조로 변환
    } catch (error) {
        console.error('Error fetching employee data:', error);
    }
};

// 결재자 목록을 팀장으로만 필터링하는 함수
const loadApproverTreeData = async () => {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/employees');
        // 부서명이 같고 "팀장"인 경우만 필터링 (팀장만 포함)
        const filteredApprovers = response.data.filter(
            (employee) =>
                employee.deptName === employeeData.value.deptName && // 같은 부서
                employee.positionName === '팀장' // 팀장 직위만 포함
        );
        approverTreeData.value = convertToTreeModel(filteredApprovers); // 필터링된 팀장 목록을 트리 구조로 변환
    } catch (error) {
        console.error('Error fetching approver data:', error);
    }
};

// 사원 데이터를 트리 구조로 변환하는 함수
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

onMounted(() => {
    form.value.vacationStartDate = new Date().toISOString().split('T')[0];
    form.value.vacationEndDate = new Date().toISOString().split('T')[0];

    loadEmployeeData().then(() => {
        loadEmployeeTreeData(); // 신청자 목록 로드
        loadApproverTreeData(); // 결재자 목록 로드 (팀장만)
    });
});

const handleEmployeeChange = (selectedEmployee) => {
    form.value.employeeId = selectedEmployee.key.replace('emp-', ''); // 선택된 신청인의 ID 추출
    form.value.employeeName = selectedEmployee.label; // 선택된 신청인의 이름 설정
};

const handleApproverChange = (selectedApprover) => {
    form.value.approverId = selectedApprover.key.replace('emp-', ''); // 선택된 결재자의 ID 추출
    form.value.approverName = selectedApprover.label; // 선택된 결재자의 이름 설정
};

const submitForm = async () => {
    try {
        const requestBody = {
            employeeId: employeeData.value.employeeId,
            approverId: employeeData.value.approverId, // 결재자 ID 추가
            vacationType: form.value.vacationType,
            vacationStartDate: form.value.vacationStartDate,
            vacationStartTime: form.value.vacationStartTime || '00:00',
            vacationEndDate: form.value.vacationEndDate,
            vacationEndTime: form.value.vacationEndTime || '23:59',
            employeeName: form.value.employeeName,
            approverName: form.value.approverName,
            comment: form.value.comment
        };

        console.log(requestBody); // 전송 데이터 확인

        await axios.post('http://localhost:8080/api/v1/vacation/submit', requestBody, {
            headers: { 'Content-Type': 'application/json' }
        });
        alert('휴가 신청이 완료되었습니다.');
    } catch (error) {
        console.error('휴가 신청 중 오류가 발생했습니다:', error);
        alert('휴가 신청 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
};
</script>

<style scoped>
.vacation-page {
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

.logged-in-user {
    margin-bottom: 20px;
}
</style>
