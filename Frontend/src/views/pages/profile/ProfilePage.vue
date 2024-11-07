<template>
    <div class="main-container">
        <h1 class="main-title">사원 정보</h1>

        <!-- 비밀번호 수정 버튼과 연필 아이콘 -->
        <div class="icon-group">
            <!-- 비밀번호 수정 버튼 -->
            <button class="change-password-button" @click="visible = true">비밀번호 변경</button>

            <!-- 연필 아이콘 (편집 버튼) -->
            <!-- <span class="edit-icon" @click="enableEditing">
                <i class="pi pi-pencil"></i>
            </span> -->
            <button class="change-button" @click="enableEditing">수정</button>
        </div>

        <div class="content-wrapper">
            <div class="left-column">
                <!-- 증명사진 업로드 틀 -->
                <div class="photo-upload-container">
                    <div class="header">
                        <h2>사진업로드(증명사진)</h2>
                    </div>
                    <div class="divider"></div>
                    <div class="form-group-photo">
                        <img :src="photoUrl" alt="증명사진 미리보기" width="150" height="150" />
                        <div style="margin-left: 10px">
                            <p class="upload-instruction">증명사진을 첨부해 주세요.</p>
                            <input type="file" id="photoUpload" class="form-control" @change="handleFileUpload" style="border: none; background-color: transparent" />
                            <p class="usage-note">첨부하시는 사진은 직원 등록 및 직원 확인용으로 사용되며, 가로 150px, 세로 150px 사용을 권장합니다.</p>
                        </div>
                    </div>
                </div>

                <!-- 부서 정보 틀 -->
                <div class="department-info-container">
                    <div class="header">
                        <h2>부서 정보</h2>
                    </div>
                    <div class="divider"></div>
                    <div class="form-group">
                        <label for="departmentName">부서명</label>
                        <input type="text" id="departmentName" v-model="departmentName" class="form-control readonly-input" readonly />
                    </div>

                    <div class="form-group">
                        <label for="teamName">팀명</label>
                        <input type="text" id="teamName" v-model="teamName" class="form-control readonly-input" readonly />
                    </div>

                    <div class="form-group">
                        <label for="jobRoleName">직무</label>
                        <input type="text" id="jobRoleName" v-model="jobRoleName" class="form-control readonly-input" readonly />
                    </div>

                    <div class="form-group">
                        <label for="position">직책</label>
                        <input type="text" id="position" v-model="position" class="form-control readonly-input" readonly />
                    </div>

                    <div class="form-group">
                        <label for="hireDate">입사일</label>
                        <input type="text" id="hireDate" v-model="hireDate" class="form-control readonly-input" readonly />
                    </div>
                </div>
            </div>

            <!-- 직원 정보 틀 -->
            <div class="employee-info-container">
                <div class="header">
                    <h2>신상 정보</h2>
                </div>
                <div class="divider"></div>

                <!-- 직원번호 -->
                <div class="form-group">
                    <label for="employeeId">사원번호</label>
                    <input type="text" id="employeeId" v-model="employeeData.employeeId" class="form-control readonly-input" readonly />
                </div>

                <div class="form-group">
                    <label for="name">성명</label>
                    <input type="text" id="name" v-model="employeeData.employeeName" class="form-control readonly-input" readonly />
                </div>

                <div class="form-group">
                    <label for="dob">생년월일</label>
                    <input type="text" id="dob" v-model="employeeData.birthDate" class="form-control readonly-input" readonly />
                </div>

                <div class="form-group">
                    <label for="phone">연락처</label>
                    <input type="text" id="phone" v-model="employeeData.phoneNumber" class="form-control readonly-input" readonly />
                </div>

                <div class="form-group">
                    <label for="email">이메일</label>
                    <input type="text" id="email" v-model="employeeData.email" class="form-control readonly-input" readonly />
                </div>

                <div class="form-group">
                    <label for="employeeAddress">도로명 주소</label>
                    <div class="address-group">
                        <input type="text" id="employeeAddress" v-model="employeeData.roadAddress" class="form-control address-input readonly-input" readonly />
                        <button @click="searchZipCode" class="btn-zipcode" style="margin-left: 11px">우편번호 검색</button>
                    </div>
                </div>

                <div class="form-group">
                    <label for="address">지 번</label>
                    <input type="text" id="address" v-model="employeeData.lotAddress" class="form-control readonly-input" readonly />
                </div>

                <div class="form-group">
                    <label for="detailedAddress">상세 주소</label>
                    <input type="text" id="detailedAddress" v-model="employeeData.detailedAddress" class="form-control detailed-address" />
                </div>
            </div>
        </div>
    </div>
    <!-- 비밀번호 변경 Dialog -->
    <Dialog v-model:visible="visible" :closable="false" header="비밀번호 변경" modal style="width: 25rem">
        <div>
            <div class="form-group">
                <label for="currentPassword" class="form-label-inline ml-5 mb-1">현재 비밀번호</label>
                <input type="password" id="currentPassword" v-model="passwordUpdate.currentPassword" class="form-control-inline" />
                <small v-if="errors.currentPassword" class="error-text ml-5">{{ errors.currentPassword }}</small>
            </div>

            <div class="form-group">
                <label for="newPassword" class="form-label-inline ml-5 mb-1">새 비밀번호</label>
                <input type="password" id="newPassword" v-model="passwordUpdate.newPassword" class="form-control-inline" />
                <small v-if="errors.newPassword" class="error-text ml-5">{{ errors.newPassword }}</small>
            </div>

            <div class="flex items-center justify-center gap-2">
                <Button label="변경" severity="primary" raised @click="submitPasswordChange" />
                <Button label="취소" severity="secondary" raised @click="visible = false" />
            </div>
        </div>
    </Dialog>
</template>

<script setup>
import authservice, { getLoginEmployeeInfo, updateEmployeeInfo } from '@/views/pages/auth/service/authService';
import Dialog from 'primevue/dialog';
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchPost } from '../auth/service/AuthApiService';

// 데이터 선언
const employeeData = ref({
    employeeId: '',
    employeeName: '',
    teamName: '',
    deptName: '',
    jobRoleName: '',
    positionName: '',
    email: '',
    joinDate: '',
    birthDate: '',
    phoneNumber: '',
    roadAddress: '',
    lotAddress: '',
    detailedAddress: '',
    profileImageUrl: '',
    profileImage: []
});

// 부서 정보 선언
const departmentName = ref('');
const teamName = ref('');
const jobRoleName = ref('');
const position = ref('');
const hireDate = ref('');

const photoUrl = ref('https://via.placeholder.com/150'); // 초기 이미지 URL
const profileImageFile = ref(null);
const router = useRouter();

// 파일 업로드 핸들러
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

// 우편번호 검색 핸들러
const searchZipCode = () => {
    new daum.Postcode({
        oncomplete: function (data) {
            employeeData.value.roadAddress = data.roadAddress;
            employeeData.value.lotAddress = data.jibunAddress;
            employeeData.value.detailedAddress = '';
        }
    }).open();
};

// 수정 기능
const enableEditing = () => {
    document.getElementById('employeeAddress').removeAttribute('readonly');
    document.getElementById('address').removeAttribute('readonly');
    document.getElementById('detailedAddress').removeAttribute('readonly');

    updateEmployeeInfo(employeeData.value, profileImageFile.value)
        .then(() => {
            Swal.fire({
                title: '수정되었습니다.',
                icon: 'success'
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.reload(); // 확인 버튼 클릭 시 새로고침
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

const visible = ref(false); // Dialog 표시 여부
const passwordUpdate = ref({
    currentPassword: '',
    newPassword: ''
});

// 에러 메시지 객체
const errors = ref({
    currentPassword: '',
    newPassword: ''
});

// 비밀번호 변경 처리 함수
const submitPasswordChange = async () => {
    if (!validatePasswords()) {
        return; // 유효성 검사 실패 시 함수 종료
    }

    try {
        const response = await fetchPost('https://hq-heroes-api.com/api/v1/employee/update-password', passwordUpdate.value);
        console.log('response', response);

        if (response.includes('비밀번호가 변경되었습니다.')) {
            console.log('response', response.status);
            visible.value = false; // Dialog 닫기
            Swal.fire('성공', '비밀번호가 변경되었습니다.', 'success');
            await authservice.logout();
            router.push('/login');
        } else {
            alert('현재 비밀번호가 일치하지 않습니다.');
        }
    } catch (error) {
        alert('비밀번호 변경 중 문제가 발생했습니다.');
    }
};

// 유효성 검사 함수
const validatePasswords = () => {
    let isValid = true;

    // 현재 비밀번호와 새로운 비밀번호가 동일한지 확인
    if (passwordUpdate.value.currentPassword === passwordUpdate.value.newPassword) {
        errors.value.newPassword = '새로운 비밀번호는 현재 비밀번호와 같을 수 없습니다.';
        isValid = false;
    } else {
        errors.value.newPassword = ''; // 이전 에러 메시지 초기화
    }

    // 새로운 비밀번호가 최소 8자 이상이고, 숫자 및 특수문자가 포함되어 있는지 확인
    const passwordPattern = /^(?=.*[0-9])(?=.*[!@#$%^&*])/;
    if (passwordUpdate.value.newPassword.length < 8 || !passwordPattern.test(passwordUpdate.value.newPassword)) {
        errors.value.newPassword = '새로운 비밀번호는 8자 이상, 숫자와 특수문자를 포함해야 합니다.';
        isValid = false;
    }

    // 현재 비밀번호가 비어있는지 확인
    if (!passwordUpdate.value.currentPassword) {
        errors.value.currentPassword = '현재 비밀번호를 입력해주세요.';
        isValid = false;
    } else {
        errors.value.currentPassword = ''; // 이전 에러 메시지 초기화
    }

    return isValid;
};

onMounted(async () => {
    const employeeId = window.localStorage.getItem('employeeId'); // 로컬스토리지에서 ID 가져오기
    const data = await getLoginEmployeeInfo(employeeId); // 직원 정보 API 호출

    console.log(data); // 데이터 확인

    if (data) {
        employeeData.value = data;
        departmentName.value = data.deptName;
        teamName.value = data.teamName;
        jobRoleName.value = data.jobRoleName;
        position.value = data.positionName;
        hireDate.value = data.joinDate;
        photoUrl.value = data.profileImageUrl; // 프로필 이미지 설정
    }
});
</script>

<style scoped>
.main-container {
    position: relative; /* 상대적 위치 설정 */
    width: 100%;
    padding: 20px;
    background-color: #fafafa;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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

.content-wrapper {
    display: flex;
    justify-content: space-between;
}

.left-column {
    display: flex;
    flex-direction: column;
    width: 48%; /* 직원 정보 틀과 동일한 너비 */
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
    width: 51%;
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

/* Form 그룹 전체 디자인 */
.form-group {
    display: flex;
    width: auto;
    flex-direction: column;
    margin-bottom: 10px;
}

/* Label과 Input을 같은 줄에 배치 */
.form-label-inline {
    width: 30%; /* label의 넓이 조정 */
    display: inline-block;
    font-weight: bold;
}

.form-control-inline {
    padding: 8px;
    margin-left: 5%;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 1rem;
}

/* 에러 메시지 디자인 */
.error-text {
    color: red;
    font-size: 0.9em;
    margin-top: 5px;
}

/* Dialog 푸터 버튼 스타일 */
.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
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
    transform: scale(1.05); /* 호버 시 살짝 커지는 효과 */
}

.icon-group {
    position: absolute;
    top: 20px; /* 상단 여백 */
    right: 30px; /* 우측 여백 */
    display: flex;
    align-items: center;
}

.edit-icon {
    font-size: 1.5em;
    cursor: pointer;
    color: #6366f1;
}

.edit-icon:hover {
    color: #4f46e5; /* 호버 시 색상 */
}
.change-password-button {
    background-color: #6366f1;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 5px;
    margin-right: 10px; /* 연필 아이콘과 간격 */
    cursor: pointer;
    font-size: 1rem;
    transition:
        background-color 0.3s ease,
        transform 0.3s ease;
}

.change-password-button:hover {
    background-color: #4f46e5;
}

.change-button {
    background-color: #6366f1;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 5px;
    margin-right: 10px; /* 연필 아이콘과 간격 */
    cursor: pointer;
    font-size: 1rem;
    transition:
        background-color 0.3s ease,
        transform 0.3s ease;
}

.change-button:hover {
    background-color: #4f46e5;
}

.photo-upload-container .form-control {
    width: auto;
}

.form-group-photo {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
}
</style>
