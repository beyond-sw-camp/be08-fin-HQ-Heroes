<template>
    <div class="modal-container" v-if="isVisible">
        <div class="modal-overlay"></div>
        <div class="modal-content">
            <h1 class="main-title">직원 정보</h1>

            <div class="content-wrapper">
                <div class="left-column">
                    <div class="photo-upload-container">
                        <div class="header">
                            <h2>사진업로드(증명사진)</h2>
                        </div>
                        <div class="divider"></div>

                        <div class="form-group">
                            <img :src="photoUrl" alt="증명사진 미리보기" width="150" height="150" />
                            <div style="margin-left: 10px">
                                <p class="upload-instruction">증명사진을 첨부해 주세요.</p>
                                <input type="file" id="photoUpload" class="form-control" @change="handleFileUpload" style="border: none; background-color: transparent" />
                                <p class="usage-note">첨부하시는 사진은 직원 등록 및 직원 확인용으로 사용되며, 가로 150px, 세로 150px 사용을 권장합니다.</p>
                            </div>
                        </div>
                    </div>

                    <div class="department-info-container">
                        <div class="header">
                            <h2>부서 정보</h2>
                        </div>
                        <div class="divider"></div>

                        <!-- 부서 선택 -->
                        <div class="form-group">
                            <label for="deptName">부서명</label>
                            <select id="deptName" v-model="selectedDeptId" @change="fetchTeams(selectedDeptId)" class="form-control" :disabled="isReadonly" :class="{ 'readonly-input': isReadonly }">
                                <option v-for="dept in departments" :key="dept.deptId" :value="dept.deptId">{{ dept.deptName }}</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="teamName">팀명</label>
                            <select id="teamName" v-model="selectedTeamId" class="form-control" :disabled="isReadonly" :class="{ 'readonly-input': isReadonly }">
                                <option v-for="team in teams" :key="team.teamId" :value="team.teamId">{{ team.teamName }}</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="jobRoleName">직무</label>
                            <select id="jobRoleName" v-model="selectedJobRoleId" class="form-control" :disabled="isReadonly" :class="{ 'readonly-input': isReadonly }">
                                <option v-for="jobRole in jobs" :key="jobRole.jobRoleId" :value="jobRole.jobRoleId">{{ jobRole.jobRoleName }}</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="positionName">직책</label>
                            <select id="positionName" v-model="selectedPositionId" class="form-control" :disabled="isReadonly" :class="{ 'readonly-input': isReadonly }">
                                <option v-for="position in positions" :key="position.positionId" :value="position.positionId">{{ position.positionName }}</option>
                            </select>
                        </div>

                        <!-- 입사일 -->
                        <div class="form-group">
                            <label for="hireDate">입사일</label>
                            <input type="text" id="hireDate" v-model="hireDate" class="form-control readonly-input" readonly />
                        </div>

                        <div class="form-group">
                            <label for="status">재직상태</label>
                            <select id="status" v-model="employeeData.status" class="form-control">
                                <option value="ACTIVE">재직중</option>
                                <option value="INACTIVE">퇴사</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="employee-info-container">
                    <div class="header">
                        <h2>신상 정보</h2>
                    </div>
                    <div class="divider"></div>

                    <div class="form-group">
                        <label for="employeeId">사원번호</label>
                        <input type="text" id="employeeId" v-model="employeeData.employeeId" class="form-control readonly-input" readonly />
                    </div>

                    <div class="form-group">
                        <label for="name">성명</label>
                        <input type="text" id="name" v-model="employeeData.employeeName" class="form-control" :readonly="isReadonly" :class="{ 'readonly-input': isReadonly }" />
                    </div>

                    <div class="form-group">
                        <label for="dob">생년월일</label>
                        <input type="text" id="dob" v-model="employeeData.birthDate" class="form-control readonly-input" readonly />
                    </div>

                    <div class="form-group">
                        <label for="phone">연락처</label>
                        <input type="text" id="phone" v-model="employeeData.phoneNumber" class="form-control" :readonly="isReadonly" :class="{ 'readonly-input': isReadonly }" />
                    </div>

                    <div class="form-group">
                        <label for="email">이메일</label>
                        <input type="text" id="email" v-model="employeeData.email" class="form-control" :readonly="isReadonly" :class="{ 'readonly-input': isReadonly }" />
                    </div>

                    <div class="form-group">
                        <label for="employeeAddress">도로명 주소</label>
                        <div class="address-group">
                            <input type="text" id="employeeAddress" v-model="employeeData.roadAddress" class="form-control address-input" :readonly="isReadonly" :class="{ 'readonly-input': isReadonly }" />
                            <button @click="searchZipCode" class="btn-zipcode" style="margin-left: 10px">우편번호 검색</button>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="address">지 번</label>
                        <input type="text" id="address" v-model="employeeData.lotAddress" class="form-control" :readonly="isReadonly" :class="{ 'readonly-input': isReadonly }" />
                    </div>

                    <div class="form-group">
                        <label for="detailedAddress">상세 주소</label>
                        <input type="text" id="detailedAddress" v-model="employeeData.detailedAddress" class="form-control detailed-address" :readonly="isReadonly" :class="{ 'readonly-input': isReadonly }" />
                    </div>
                </div>
            </div>

            <div class="button-group">
                <button @click="enableEditing" class="btn-edit">수정</button>
                <button @click="handleClose" class="btn-close">닫기</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { adminUpdateEmployeeInfo } from '@/views/pages/auth/service/authService'; // 서비스 파일에서 메소드 가져오기
import Swal from 'sweetalert2';
import { computed, defineEmits, defineProps, ref, watch } from 'vue';
import { fetchGet } from '../auth/service/AuthApiService';

const props = defineProps({
    isVisible: {
        type: Boolean,
        required: true
    },
    close: {
        type: Function,
        required: true
    },
    employee: {
        type: Object,
        required: true // employee prop 추가
    }
});

const emit = defineEmits(['update:visible', 'closeModal']);

const handleClose = () => {
    emit('update:visible', false); // 부모 컴포넌트에 모달 닫기 이벤트 발송
    emit('closeModal'); // 부모 컴포넌트의 close 함수 호출
};

// 데이터 선언
const employeeData = ref({
    employeeId: '',
    deptId: '',
    teamId: '',
    jobRoleId: '',
    positionId: '',
    employeeName: '',
    teamName: '',
    deptName: '',
    jobRoleName: '',
    positionName: '',
    email: '',
    status: '',
    joinDate: '',
    birthDate: '',
    phoneNumber: '',
    roadAddress: '',
    lotAddress: '',
    detailedAddress: '',
    profileImageUrl: '',
    profileImage: []
});

// 부서, 팀, 직무, 직책 목록과 선택된 값들
const departments = ref([]);
const teams = ref([]);
const jobs = ref([]);
const positions = ref([]);

const selectedDeptId = ref(null);
const selectedTeamId = ref(null);
const selectedJobRoleId = ref(null);
const selectedPositionId = ref(null);
const hireDate = ref('');

// 프로필 이미지 처리
const photoUrl = ref('https://via.placeholder.com/150'); // 초기 이미지 URL
const profileImageFile = ref(null);

const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
        profileImageFile.value = file;
        const reader = new FileReader();
        reader.onload = (e) => {
            photoUrl.value = e.target.result;
        };
        reader.readAsDataURL(file);
    } else {
        photoUrl.value = 'https://via.placeholder.com/150';
    }
};

// 우편번호 검색
const searchZipCode = () => {
    new daum.Postcode({
        oncomplete: function (data) {
            employeeData.value.roadAddress = data.roadAddress;
            employeeData.value.lotAddress = data.jibunAddress;
            employeeData.value.detailedAddress = '';
        }
    }).open();
};

// 수정 버튼 클릭 시 처리
const enableEditing = () => {
    employeeData.value.deptId = selectedDeptId.value;
    employeeData.value.teamId = selectedTeamId.value;
    employeeData.value.jobRoleId = selectedJobRoleId.value;
    employeeData.value.positionId = selectedPositionId.value;

    const updatedEmployeeData = {
        ...employeeData.value,
        teamName: teams.value.find((team) => team.teamId === selectedTeamId.value)?.teamName || '',
        deptName: departments.value.find((dept) => dept.deptId === selectedDeptId.value)?.deptName || '',
        jobRoleName: jobs.value.find((jobRole) => jobRole.jobRoleId === selectedJobRoleId.value)?.jobRoleName || '',
        positionName: positions.value.find((position) => position.positionId === selectedPositionId.value)?.positionName || ''
    }; // employeeData를 복사하여 별도 객체로 생성

    console.log('updatedEmployeeData: ', updatedEmployeeData);
    adminUpdateEmployeeInfo(updatedEmployeeData, profileImageFile.value)
        .then(() => {
            Swal.fire({
                title: '수정되었습니다.',
                icon: 'success'
            }).then((result) => {
                if (result.isConfirmed) {
                    handleClose(); // 모달 닫기
                    window.location.reload();
                }
            });
        })
        .catch((error) => {
            Swal.fire({
                title: '수정 실패',
                text: '정보 수정 중 오류가 발생했습니다.',
                icon: 'error'
            });
        });
};

// 부서, 팀, 직무, 직책 데이터를 불러오는 함수
const fetchDepartments = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/departments');
        departments.value = response;
    } catch (error) {
        console.error('부서 데이터를 가져오는 중 오류 발생:', error);
    }
};

const fetchTeams = async (deptId) => {
    try {
        const response = await fetchGet(`https://hq-heroes-api.com/api/v1/employee/teams?deptId=${deptId}`);
        teams.value = response;
    } catch (error) {
        console.error('팀 데이터를 가져오는 중 오류 발생:', error);
    }
};

const fetchJobs = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/jobs');
        jobs.value = response;
    } catch (error) {
        console.error('직무 데이터를 가져오는 중 오류 발생:', error);
    }
};

const fetchPositions = async () => {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/positions');
        positions.value = response;
    } catch (error) {
        console.error('직책 데이터를 가져오는 중 오류 발생:', error);
    }
};

const isReadonly = computed(() => {
    return employeeData.value.status === 'INACTIVE';
});

watch(
    () => employeeData.value.status,
    (newStatus) => {
        isReadonly.value = newStatus === 'INACTIVE'; // 변경 시 바로 수정되지 않고 읽기 전용 상태만 업데이트
    }
);

// props.employee가 변경될 때마다 직원 정보 업데이트
watch(
    () => props.employee,
    async (newEmployee) => {
        if (newEmployee) {
            employeeData.value = newEmployee;
            photoUrl.value = newEmployee.profileImageUrl;

            // 부서, 팀, 직무, 직책 목록을 불러오고 선택된 값 설정
            await fetchDepartments();
            await fetchTeams(newEmployee.deptId);
            await fetchJobs();
            await fetchPositions();

            // 부모 컴포넌트에서 전달받은 값을 기본값으로 설정
            selectedDeptId.value = newEmployee.deptId;
            selectedTeamId.value = newEmployee.teamId;
            selectedJobRoleId.value = newEmployee.jobRoleId;
            selectedPositionId.value = newEmployee.positionId;
            hireDate.value = newEmployee.joinDate;
        }
    },
    { immediate: true }
);

// 모달이 열릴 때 데이터를 불러옴
watch(
    () => props.isVisible,
    (visible) => {
        if (visible) {
            fetchDepartments();
            fetchJobs();
            fetchPositions();
        }
    }
);

// 부서 선택 시 호출되는 함수
const handleDepartmentChange = async () => {
    await fetchTeams(selectedDeptId.value);
};
</script>

<style scoped>
.modal-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    padding: 15px; /* 조금 더 여유롭게 상하 좌우 여백을 설정 */
}

.modal-content {
    background-color: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    max-width: 1200px; /* 최대 너비를 1100px로 약간 확대 */
    width: 90%; /* 화면 너비의 90%로 설정 */
    max-height: 95vh; /* 화면 높이의 95%로 확대 */
    overflow-y: auto; /* 내용이 넘칠 경우만 스크롤 */
}

.main-title {
    font-weight: bold;
    font-size: large;
    margin-bottom: 20px;
    text-align: left; /* 제목을 왼쪽 상단에 배치 */
}

.readonly-input[readonly] {
    background-color: #f0f0f0; /* 회색 배경 */
    cursor: not-allowed; /* 커서를 not-allowed로 변경 */
}

select.readonly-input {
    appearance: none; /* 기본 드롭다운 화살표 제거 */
    -webkit-appearance: none; /* Safari 브라우저에서 기본 드롭다운 화살표 제거 */
    -moz-appearance: none; /* Firefox에서 기본 드롭다운 화살표 제거 */
    cursor: not-allowed; /* 커서를 비활성화로 변경 */
    background-color: #f0f0f0; /* 회색 배경 */
}

/* 버튼 그룹 스타일 */
.button-group {
    display: flex;
    justify-content: flex-end;
    margin-top: 15px; /* 여백을 조금 추가 */
    padding-bottom: 15px; /* 아래쪽 여백을 조금 추가 */
}

.btn-edit,
.btn-close {
    background-color: #6366f1; /* 기본 배경색 */
    color: white; /* 글씨색 */
    border: none; /* 테두리 제거 */
    border-radius: 5px; /* 둥근 모서리 */
    padding: 10px 20px; /* 패딩 */
    cursor: pointer; /* 포인터 커서 */
    margin-left: 10px; /* 버튼 간격 */
    transition: background-color 0.3s ease; /* 배경색 부드러운 전환 */
}

.btn-edit:hover,
.btn-close:hover {
    background-color: #4f46e5; /* 호버 시 배경색 */
}

input[type='file']::-webkit-file-upload-button {
    background-color: #6366f1; /* 버튼 배경색 */
    color: white; /* 버튼 글씨색 */
    border: none; /* 버튼 테두리 없애기 */
    border-radius: 5px; /* 버튼 둥근 모서리 */
    padding: 7px; /* 버튼 패딩 */
    cursor: pointer; /* 포인터 커서 */
    margin-left: 0; /* 왼쪽 여백 제거 */
}

/* 마우스 호버 시 색상 변경 */
input[type='file']:hover::-webkit-file-upload-button {
    background-color: #4f46e5; /* 호버 시 배경색 */
}

input[type='file'] {
    margin-left: 0; /* 파일 입력창의 왼쪽 여백 제거 */
    padding: 0; /* 파일 입력창의 패딩 제거 */
}

/* 모든 input 필드의 기본 배경색을 통일 */
input.form-control {
    background-color: #fff; /* 흰색으로 설정 */
}

.content-wrapper {
    display: flex;
    flex-wrap: wrap; /* 작은 화면에서 요소가 겹치지 않도록 wrap 적용 */
    justify-content: space-between;
    gap: 20px; /* 열 사이 간격 추가 */
}

.left-column {
    display: flex;
    flex-direction: column;
    width: 48%; /* 두 열의 너비를 48%로 설정해서 공간 확보 */
    box-sizing: border-box; /* padding과 border를 포함한 크기 계산 */
}

.photo-upload-container img {
    width: 150px; /* 너비 고정 */
    height: 150px; /* 높이 고정 */
    object-fit: cover; /* 비율을 유지하면서 영역에 맞게 잘라내기 */
}

.photo-upload-container,
.department-info-container {
    background-color: #ffffff;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
    flex-grow: 1; /* 두 틀을 동일한 높이로 */
    box-sizing: border-box; /* padding과 border를 포함한 크기 계산 */
}

.employee-info-container {
    width: 50%; /* 두 열의 너비를 48%로 설정해서 공간 확보 */
    min-height: 300px; /* 원하는 최소 높이를 설정 */
    background-color: #ffffff;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    box-sizing: border-box; /* padding과 border를 포함한 크기 계산 */
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

h2 {
    margin-bottom: 10px;
    font-weight: bold;
}

#photoUpload {
    margin-top: 10px;
    margin-bottom: 10px;
}

.divider {
    width: 100%;
    height: 2px;
    background-color: #ddd;
    margin-bottom: 20px;
}

.form-group {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
}

label {
    width: 20%;
    font-weight: bold;
    margin-right: 10px;
}

.form-control {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #f9f9f9;
}

.address-group {
    display: flex;
    align-items: center;
    width: 100%;
}

.address-input {
    width: 70%;
}

.btn-zipcode {
    margin-left: 3px;
    padding: 10px 15px;
    background-color: #6366f1;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition:
        background-color 0.3s ease,
        transform 0.3s ease; /* 부드러운 전환 효과 */
}

.btn-zipcode:hover {
    background-color: #4e54d4; /* 호버 시 배경색 변경 */
}

.edit-icon {
    cursor: pointer;
}

.photo-upload-container .form-control {
    width: auto;
}

.employee-info-container .form-control.detailed-address {
    background-color: #fff; /* 상세 주소 필드 배경 색상 */
}

.employee-info-container .form-control[readonly] {
    background-color: #f0f0f0; /* 읽기 전용 필드의 배경색 */
}
</style>
