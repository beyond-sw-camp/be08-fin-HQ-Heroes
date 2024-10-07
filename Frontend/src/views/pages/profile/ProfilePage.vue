<template>
    <div class="main-container">
        <h1 class="main-title">직원 정보</h1>

        <div class="content-wrapper">
            <div class="left-column">
                <!-- 증명사진 업로드 틀 -->
                <div class="photo-upload-container">
                    <div class="header">
                        <h2>사진업로드(증명사진)</h2>
                    </div>
                    <div class="divider"></div>

                    <!-- 사진 업로드 -->
                    <div class="form-group">
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

                    <!-- 부서명 -->
                    <div class="form-group">
                        <label for="departmentName">부서명</label>
                        <input type="text" id="departmentName" v-model="departmentName" class="form-control" readonly />
                    </div>

                    <!-- 팀명 -->
                    <div class="form-group">
                        <label for="teamName">팀명</label>
                        <input type="text" id="teamName" v-model="teamName" class="form-control" readonly />
                    </div>

                    <!-- 직급 -->
                    <div class="form-group">
                        <label for="position">직급</label>
                        <input type="text" id="position" v-model="position" class="form-control" readonly />
                    </div>

                    <!-- 입사일 -->
                    <div class="form-group">
                        <label for="hireDate">입사일</label>
                        <input type="text" id="hireDate" v-model="hireDate" class="form-control" readonly />
                    </div>
                </div>
            </div>

            <!-- 직원 정보 틀 -->
            <div class="employee-info-container">
                <div class="header">
                    <h2>신상 정보</h2>
                    <span class="edit-icon" @click="enableEditing"><i class="pi pi-pencil"></i></span>
                </div>
                <div class="divider"></div>

                <!-- 직원번호 -->
                <div class="form-group">
                    <label for="employeeId">직원번호</label>
                    <input type="text" id="employeeId" v-model="employeeData.employeeId" class="form-control" readonly />
                </div>

                <!-- 성명 -->
                <div class="form-group">
                    <label for="name">성명</label>
                    <input type="text" id="name" v-model="employeeData.employeeName" class="form-control" readonly />
                </div>

                <!-- 생년월일 -->
                <div class="form-group">
                    <label for="dob">생년월일</label>
                    <input type="text" id="dob" v-model="employeeData.birthDate" class="form-control" readonly />
                </div>

                <!-- 연락처 -->
                <div class="form-group">
                    <label for="phone">연락처</label>
                    <input type="text" id="phone" v-model="employeeData.phoneNumber" class="form-control" readonly />
                </div>

                <!-- 이메일 -->
                <div class="form-group">
                    <label for="email">이메일</label>
                    <input type="text" id="email" v-model="employeeData.email" class="form-control" readonly />
                </div>

                <!-- 직원주소 -->
                <div class="form-group">
                    <label for="employeeAddress">직원 주소</label>
                    <div class="address-group">
                        <input type="text" id="employeeAddress" v-model="employeeData.roadAddress" class="form-control address-input" readonly />
                        <button @click="searchZipCode" class="btn-zipcode">우편번호 검색</button>
                    </div>
                </div>

                <!-- 주소 -->
                <div class="form-group">
                    <label for="address">주소</label>
                    <input type="text" id="address" v-model="employeeData.lotAddress" class="form-control" readonly />
                </div>

                <!-- 상세 주소 -->
                <div class="form-group">
                    <label for="detailedAddress">상세 주소</label>
                    <input type="text" id="detailedAddress" v-model="employeeData.detailedAddress" class="form-control detailed-address" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import { getLoginEmployeeInfo } from '@/views/pages/auth/service/authService'; // 서비스 파일에서 메소드 가져오기

// 데이터 선언
const employeeData = ref({
    employeeId: '',
    employeeName: '',
    teamName: '',
    deptName: '',
    jobName: '',
    positionName: '',
    email: '',
    joinDate: '',
    birthDate: '',
    phoneNumber: '',
    roadAddress: '',
    lotAddress: '',
    detailedAddress: '',
    profileImageUrl: ''
});

// 부서 정보 선언
const departmentName = ref('');
const teamName = ref('');
const position = ref('');
const hireDate = ref('');

const photoUrl = ref('https://via.placeholder.com/150'); // 초기 이미지 URL

const router = useRouter();

// 파일 업로드 핸들러
const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            photoUrl.value = e.target.result;
        };
        reader.readAsDataURL(file);
    }
};

// 우편번호 검색 핸들러
const searchZipCode = () => {
    new daum.Postcode({
        oncomplete: function (data) {
            employeeAddress.value = data.roadAddress;
            address.value = data.jibunAddress;
            detailedAddress.value = '';
        }
    }).open();
};

// 수정 기능
const enableEditing = () => {
    Swal.fire({
        title: '수정되었습니다.',
        icon: 'success'
    });

    document.getElementById('employeeAddress').removeAttribute('readonly');
    document.getElementById('address').removeAttribute('readonly');
    document.getElementById('detailedAddress').removeAttribute('readonly');
};

onMounted(async () => {
    const employeeId = window.localStorage.getItem('employeeId'); // 로컬스토리지에서 ID 가져오기
    const data = await getLoginEmployeeInfo(employeeId); // 직원 정보 API 호출

    console.log(data); // 데이터 확인

    if (data) {
        employeeData.value = data;
        departmentName.value = data.deptName;
        teamName.value = data.teamName;
        position.value = data.positionName;
        hireDate.value = data.joinDate;
        photoUrl.value = data.profileImageUrl || 'https://via.placeholder.com/150'; // 프로필 이미지 설정
    }
});

</script>

<style scoped>
.main-container {
    width: 100%;
    padding: 20px;
    background-color: #fafafa; /* 하얀색에 가까운 색상으로 변경 */
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.main-title {
    font-weight: bold;
    font-size: large;
    margin-bottom: 20px;
    text-align: left; /* 제목을 왼쪽 상단에 배치 */
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
    transform: scale(1.05); /* 호버 시 살짝 커지는 효과 */
}

.edit-icon {
    cursor: pointer;
}

.photo-upload-container .form-control {
    width: auto;
}
</style>
