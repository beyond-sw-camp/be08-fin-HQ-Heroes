<template>
    <div class="modal-container" v-if="isVisible">
        <div class="modal-overlay"></div>
        <div class="modal-content">
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
                    </div>
                    <div class="divider"></div>

                    <!-- 직원번호 -->
                    <div class="form-group">
                        <label for="employeeId">직원번호</label>
                        <input type="text" id="employeeId" v-model="employeeId" class="form-control" readonly />
                    </div>

                    <!-- 성명 -->
                    <div class="form-group">
                        <label for="name">성명</label>
                        <input type="text" id="name" v-model="name" class="form-control" readonly />
                    </div>

                    <!-- 생년월일 -->
                    <div class="form-group">
                        <label for="dob">생년월일</label>
                        <input type="text" id="dob" v-model="dob" class="form-control" readonly />
                    </div>

                    <!-- 연락처 -->
                    <div class="form-group">
                        <label for="phone">연락처</label>
                        <input type="text" id="phone" v-model="phone" class="form-control" readonly />
                    </div>

                    <!-- 이메일 -->
                    <div class="form-group">
                        <label for="email">이메일</label>
                        <input type="text" id="email" v-model="email" class="form-control" readonly />
                    </div>

                    <!-- 직원주소 -->
                    <div class="form-group">
                        <label for="employeeAddress">직원 주소</label>
                        <div class="address-group">
                            <input type="text" id="employeeAddress" v-model="employeeAddress" class="form-control address-input" readonly />
                            <button @click="searchZipCode" class="btn-zipcode">우편번호 검색</button>
                        </div>
                    </div>

                    <!-- 주소 -->
                    <div class="form-group">
                        <label for="address">주소</label>
                        <input type="text" id="address" v-model="address" class="form-control" readonly />
                    </div>

                    <!-- 상세 주소 -->
                    <div class="form-group">
                        <label for="detailedAddress">상세 주소</label>
                        <input type="text" id="detailedAddress" v-model="detailedAddress" class="form-control detailed-address" />
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
import { ref, defineProps, defineEmits } from 'vue';
import Swal from 'sweetalert2';

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

const emit = defineEmits(['update:visible', 'closeModal']); // 올바른 위치에 정의

const handleClose = () => {
    emit('update:visible', false); // 부모 컴포넌트에 모달 닫기 이벤트 발송
    emit('closeModal'); // 부모 컴포넌트의 close 함수 호출
};

// 기본값 처리
const employeeId = ref(props.employee?.employeeId || '2023001');
const name = ref(props.employee?.name || '홍길동');
const dob = ref(props.employee?.dob || '1990-01-01');
const phone = ref(props.employee?.phone || '010-1234-5678');
const email = ref(props.employee?.email || 'hong@example.com');
const employeeAddress = ref(props.employee?.address || '');
const address = ref(props.employee?.address || '');
const detailedAddress = ref(props.employee?.detailedAddress || '');

// 부서 정보 추가
const departmentName = ref(props.employee?.departmentName || '인사부');
const teamName = ref(props.employee?.teamName || '인재채용팀');
const position = ref(props.employee?.position || '팀장');
const hireDate = ref(props.employee?.hireDate || '2020-05-01');
const photoUrl = ref(props.employee?.photoUrl || 'https://via.placeholder.com/150'); // 초기 이미지 URL

const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            photoUrl.value = e.target.result; // 이미지 URL 업데이트
        };
        reader.readAsDataURL(file); // 파일을 Data URL로 읽기
    }
};

const searchZipCode = () => {
    new daum.Postcode({
        oncomplete: function (data) {
            employeeAddress.value = data.roadAddress; // 도로명 주소
            address.value = data.jibunAddress; // 지번 주소
            detailedAddress.value = ''; // 상세 주소는 빈 문자열로 설정
        }
    }).open();
};

const enableEditing = () => {
    Swal.fire({
        title: '수정되었습니다.', // Alert 제목
        icon: 'success' // Alert 타입
    });

    // 직원주소, 주소, 상세주소 필드의 readonly 속성을 제거
    document.getElementById('employeeAddress').removeAttribute('readonly');
    document.getElementById('address').removeAttribute('readonly');
    document.getElementById('detailedAddress').removeAttribute('readonly');
};

</script>


<style scoped>
.modal-container {
    position: fixed; /* 모달을 화면에 고정 */
    top: 0; /* 화면의 상단에 위치 */
    left: 0; /* 화면의 왼쪽에 위치 */
    width: 100%; /* 전체 너비 */
    height: 100%; /* 전체 높이 */
    background-color: rgba(0, 0, 0, 0.5); /* 반투명 배경 */
    display: flex; /* 중앙 정렬을 위해 flex 사용 */
    justify-content: center; /* 수평 중앙 정렬 */
    align-items: center; /* 수직 중앙 정렬 */
    z-index: 1000; /* 다른 요소 위에 표시 */
}

.modal-content {
    background-color: white; 
    border-radius: 10px; 
    padding: 20px; 
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); 
    max-width: 1200px; /* 최대 너비를 1200px로 확장 */
    width: 90%; /* 화면의 90% 너비 */
}

.main-title {
    font-weight: bold;
    font-size: large;
    margin-bottom: 20px;
    text-align: left; /* 제목을 왼쪽 상단에 배치 */
}

/* 버튼 그룹 스타일 */
.button-group {
    display: flex; /* Flexbox로 버튼을 나란히 배치 */
    justify-content: flex-end; /* 오른쪽 정렬 */
    margin-top: 20px; /* 위쪽 여백 */
}

.btn-edit, .btn-close {
    background-color: #6366F1; /* 기본 배경색 */
    color: white; /* 글씨색 */
    border: none; /* 테두리 제거 */
    border-radius: 5px; /* 둥근 모서리 */
    padding: 10px 20px; /* 패딩 */
    cursor: pointer; /* 포인터 커서 */
    margin-left: 10px; /* 버튼 간격 */
    transition: background-color 0.3s ease; /* 배경색 부드러운 전환 */
}

.btn-edit:hover, .btn-close:hover {
    background-color: #4f46e5; /* 호버 시 배경색 */
}

input[type="file"]::-webkit-file-upload-button {
    background-color: #6366F1; /* 버튼 배경색 */
    color: white; /* 버튼 글씨색 */
    border: none; /* 버튼 테두리 없애기 */
    border-radius: 5px; /* 버튼 둥근 모서리 */
    padding: 7px; /* 버튼 패딩 */
    cursor: pointer; /* 포인터 커서 */
    margin-left: 0; /* 왼쪽 여백 제거 */
}

/* 마우스 호버 시 색상 변경 */
input[type="file"]:hover::-webkit-file-upload-button {
    background-color: #4f46e5; /* 호버 시 배경색 */
}

input[type="file"] {
    margin-left: 0; /* 파일 입력창의 왼쪽 여백 제거 */
    padding: 0; /* 파일 입력창의 패딩 제거 */
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
    background-color: #6366F1;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease, transform 0.3s ease; /* 부드러운 전환 효과 */
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

