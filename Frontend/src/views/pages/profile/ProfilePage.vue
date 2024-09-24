<template>
    <div class="employee-info-container">
        <div class="header">
            <h2>직원 정보</h2>
            <span class="edit-icon" @click="enableEditing"><i class="pi pi-pencil"></i></span>
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
            <input type="text" id="detailedAddress" v-model="detailedAddress" class="form-control detailed-address" readonly />
        </div>
    </div>

    <!-- 부서 정보 틀 추가 -->
    <div class="department-info-container">
        <div class="header">
            <h2>부서 정보</h2>
            <span class="edit-icon" @click="enableDepartmentEditing"><i class="pi pi-pencil"></i></span>
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
    </div>

    <!-- 증명사진 업로드 틀 추가 -->
    <div class="photo-upload-container">
        <div class="header">
            <h2>증명사진</h2>
        </div>
        <div class="divider"></div>

        <!-- 사진 업로드 -->
        <div class="form-group">
            <label for="photoUpload">사진 업로드</label>
            <input type="file" id="photoUpload" class="form-control" @change="handleFileUpload" />
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import Swal from 'sweetalert2';

export default {
    setup() {
        const employeeId = ref('2023001');
        const name = ref('홍길동');
        const dob = ref('1990-01-01');
        const phone = ref('010-1234-5678');
        const email = ref('hong@example.com');
        const employeeAddress = ref('');
        const address = ref('');
        const detailedAddress = ref('');
        
        // 부서 정보 추가
        const departmentName = ref('인사부');
        const teamName = ref('인재채용팀');
        const position = ref('팀장');

        const searchZipCode = () => {
            new daum.Postcode({
                oncomplete: function(data) {
                    employeeAddress.value = data.roadAddress; // 도로명 주소
                    address.value = data.jibunAddress; // 지번 주소
                    detailedAddress.value = ''; // 상세 주소는 빈 문자열로 설정
                }
            }).open();
        };

        const enableEditing = () => {
            Swal.fire({
                title: '수정되었습니다.',      // Alert 제목
                icon: 'success'                       // Alert 타입
            });

            // 직원주소, 주소, 상세주소 필드의 readonly 속성을 제거
            document.getElementById('employeeAddress').removeAttribute('readonly');
            document.getElementById('address').removeAttribute('readonly');
            document.getElementById('detailedAddress').removeAttribute('readonly');
        };

        // 부서 정보 수정 함수 추가
        const enableDepartmentEditing = () => {
            Swal.fire({
                title: '부서 정보가 수정되었습니다.',      // Alert 제목
                icon: 'success'                       // Alert 타입
            });

            // 부서명, 팀명, 직급 필드의 readonly 속성을 제거
            document.getElementById('departmentName').removeAttribute('readonly');
            document.getElementById('teamName').removeAttribute('readonly');
            document.getElementById('position').removeAttribute('readonly');
        };

        // 파일 업로드 처리 함수
        const handleFileUpload = (event) => {
            const file = event.target.files[0];
            if (file) {
                // 파일 업로드 로직을 추가할 수 있습니다.
                console.log(`Uploaded file: ${file.name}`);
            }
        };

        return {
            employeeId,
            name,
            dob,
            phone,
            email,
            employeeAddress,
            address,
            detailedAddress,
            departmentName,
            teamName,
            position,
            searchZipCode,
            enableEditing,
            enableDepartmentEditing,
            handleFileUpload,
        };
    },
};
</script>

<style scoped>
.employee-info-container,
.department-info-container,
.photo-upload-container {
    width: 45%; /* 각 틀의 너비를 설정 */
    margin: 15px; /* 틀 사이의 간격을 설정 */
    padding: 20px;
    background-color: #ffffff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    display: inline-block; /* 틀을 나란히 배치 */
    vertical-align: top; /* 상단 정렬 */
}

.header {
    display: flex;
    justify-content: space-between; /* 제목과 아이콘을 양쪽 끝으로 정렬 */
    align-items: center; /* 수직 정렬 */
}

h2 {
    text-align: left; /* 좌측 정렬 */
    margin-bottom: 10px;
    font-weight: bold; /* 제목을 진하게 */
}

.divider {
    width: 100%;
    height: 2px;
    background-color: #ddd; /* 실선 색상 */
    margin-bottom: 20px;
}

.form-group {
    display: flex; /* 필드를 수평으로 정렬 */
    align-items: center;
    margin-bottom: 15px;
}

label {
    width: 20%; /* 레이블 너비를 설정 */
    font-weight: bold;
    margin-right: 10px;
}

.form-control {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #f9f9f9;
    cursor: not-allowed; /* 기본 커서 설정 */
}

.address-group {
    display: flex;
    align-items: center;
    width: 100%;
}

.address-input {
    width: 70%; /* 주소 입력 필드의 크기를 줄임 */
}

.btn-zipcode {
    margin-left: 10px;
    padding: 10px 15px;
    background-color: #007bff; /* primary 색상 */
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.edit-icon {
    cursor: pointer; /* 클릭 가능하게 */
}

.photo-upload-container .form-group {
    display: flex;
    align-items: center;
}

.photo-upload-container .form-control {
    width: auto; /* 사진 업로드 필드의 크기 설정 */
}
</style>
