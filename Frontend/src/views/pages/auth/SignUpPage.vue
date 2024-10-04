<script setup>
import { ref } from 'vue';
import apiService from '../auth/service/authService'; // register 메서드를 포함한 서비스
import Swal from 'sweetalert2';
import router from '@/router';

const employeeName = ref('');
const email = ref('');
const password = ref('');
const joinDate = ref('');
const role = ref('ROLE_EMPLOYEE'); // 기본값 ROLE_EMPLOYEE
const birthDate = ref('');
const annualLeave = ref(0);
const status = ref('ACTIVE');
const phoneNumber = ref('');
const roadAddress = ref('');
const lotAddress = ref('');
const detailedAddress = ref('');
const profileImageUrl = ref('');
const teamId = ref('');
const positionId = ref('');
const jobId = ref('');

const photoUrl = ref('https://via.placeholder.com/150'); // 초기 사진 URL

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      profileImageUrl.value = e.target.result;
      photoUrl.value = e.target.result; // 사진 미리보기 업데이트
    };
    reader.readAsDataURL(file);
  }
};

async function registerEmployee() {
  try {
    await apiService.register(
      employeeName.value,
      email.value,
      password.value,
      role.value,
      joinDate.value,
      birthDate.value,
      annualLeave.value,
      status.value,
      phoneNumber.value,
      roadAddress.value,
      lotAddress.value,
      detailedAddress.value,
      profileImageUrl.value,
      teamId.value,
      positionId.value,
      jobId.value
    );
    Swal.fire('회원가입 성공', '회원가입이 완료되었습니다.', 'success');
    router.push('/');  // 성공 후 홈으로 리디렉션
  } catch (error) {
    Swal.fire('회원가입 실패', '다시 시도해주세요.', 'error');
    console.error('회원가입 중 오류:', error);
  }
}
</script>

<template>
  <div class="form-container">
    <h1 class="main-title">회원가입</h1>
    
    <div class="form-group">
      <label for="employeeName">이름</label>
      <input type="text" id="employeeName" v-model="employeeName" class="form-control" placeholder="이름을 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="email">이메일</label>
      <input type="email" id="email" v-model="email" class="form-control" placeholder="이메일을 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="password">비밀번호</label>
      <input type="password" id="password" v-model="password" class="form-control" placeholder="비밀번호를 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="joinDate">입사일</label>
      <input type="date" id="joinDate" v-model="joinDate" class="form-control" />
    </div>

    <div class="form-group">
      <label for="role">역할</label>
      <select id="role" v-model="role" class="form-control">
        <option value="ROLE_USER">USER</option>
        <option value="ROLE_ADMIN">ADMIN</option>
      </select>
    </div>

    <div class="form-group">
      <label for="birthDate">생년월일</label>
      <input type="date" id="birthDate" v-model="birthDate" class="form-control" />
    </div>

    <div class="form-group">
      <label for="phoneNumber">연락처</label>
      <input type="text" id="phoneNumber" v-model="phoneNumber" class="form-control" placeholder="연락처를 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="roadAddress">도로명 주소</label>
      <input type="text" id="roadAddress" v-model="roadAddress" class="form-control" placeholder="도로명 주소를 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="lotAddress">지 번</label>
      <input type="text" id="lotAddress" v-model="lotAddress" class="form-control" placeholder="지 번을 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="detailedAddress">상세 주소</label>
      <input type="text" id="detailedAddress" v-model="detailedAddress" class="form-control" placeholder="상세 주소를 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="profileImageUrl">증명사진</label>
      <input type="file" id="profileImageUrl" @change="handleFileUpload" />
      <img :src="photoUrl" alt="증명사진 미리보기" width="150" height="150" />
    </div>

    <div class="form-group">
      <label for="teamId">팀 ID</label>
      <input type="number" id="teamId" v-model="teamId" class="form-control" placeholder="팀 ID를 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="positionId">직위 ID</label>
      <input type="number" id="positionId" v-model="positionId" class="form-control" placeholder="직위 ID를 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="jobId">직무 ID</label>
      <input type="number" id="jobId" v-model="jobId" class="form-control" placeholder="직무 ID를 입력해주세요" />
    </div>

    <button @click="registerEmployee" class="btn-submit">회원가입</button>
  </div>
</template>

<style scoped>
.form-container {
  max-width: 600px;
  margin: auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.main-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.btn-submit {
  width: 100%;
  padding: 10px;
  background-color: #6366F1;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-submit:hover {
  background-color: #4f46e5;
}
</style>
