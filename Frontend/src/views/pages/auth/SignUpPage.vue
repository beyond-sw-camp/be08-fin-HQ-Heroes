<script setup>
import { ref, onMounted } from 'vue';
import apiService from '../auth/service/authService';
import Swal from 'sweetalert2';
import router from '@/router';
import axios from 'axios';

const employeeName = ref('');
const email = ref('');
const password = ref('');
const joinDate = ref('');
const role = ref('');
const birthDate = ref('');
const annualLeave = ref(0);
const status = ref('ACTIVE');
const phoneNumber = ref('');
const roadAddress = ref('');
const lotAddress = ref('');
const detailedAddress = ref('');
const profileImage = ref('');
const profileImageFile = ref(null);
const deptId = ref('');
const teamId = ref('');
const positionId = ref('');
const jobId = ref('');

const photoUrl = ref('https://via.placeholder.com/150');
const departments = ref([]);
const teams = ref([]);
const jobs = ref([]);
const positions = ref([]);
const isTeamSelectable = ref(false);
const selectedDepartment = ref(null);
const selectedTeam = ref(null);
const selectedJob = ref(null);
const selectedPosition = ref(null);

async function fetchDepartments() {
  try {
    const response = await axios.get('http://localhost:8080/api/v1/employee/departments');
    departments.value = [...response.data];
  } catch (error) {
    console.error('부서 데이터를 가져오는 중 오류 발생:', error);
    departments.value = [{ deptId: null }];
  }
}

async function fetchTeams(selectedDeptId) {
  deptId.value = selectedDeptId;
  try {
    const response = await axios.get('http://localhost:8080/api/v1/employee/teams', {
      params: { deptId: selectedDeptId }
    });
    teams.value = [...response.data];
    isTeamSelectable.value = true;
  } catch (error) {
    console.error('팀 데이터를 가져오는 중 오류 발생:', error);
    teams.value = [{ teamId: null }];
    isTeamSelectable.value = false;
  }
}

async function fetchJobs() {
  try {
    const response = await axios.get('http://localhost:8080/api/v1/employee/jobs');
    jobs.value = [...response.data];
  } catch (error) {
    console.error('직무 데이터를 가져오는 중 오류 발생:', error);
    jobs.value = [{ jobId: null, jobName: '전체 직무' }];
  }
}

async function fetchPositions() {
  try {
    const response = await axios.get('http://localhost:8080/api/v1/employee/positions');
    positions.value = [...response.data];
  } catch (error) {
    console.error('직책 데이터를 가져오는 중 오류 발생:', error);
    positions.value = [{ positionId: null, positionName: '전체 직책' }];
  }
}

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

async function registerEmployee() {
  try {
    await apiService.register(
      employeeName.value,
      email.value,
      password.value,
      role.value,
      new Date(joinDate.value).toISOString().split('T')[0],
      new Date(birthDate.value).toISOString().split('T')[0],
      annualLeave.value,
      status.value,
      phoneNumber.value,
      roadAddress.value,
      lotAddress.value,
      detailedAddress.value,
      profileImageFile.value,
      deptId.value,
      selectedTeam.value.teamId,
      selectedPosition.value.positionId,
      selectedJob.value.jobId
    );
    Swal.fire('회원가입 성공', '회원가입이 완료되었습니다.', 'success');
    router.push('/');
  } catch (error) {
    Swal.fire('회원가입 실패', '다시 시도해주세요.', 'error');
    console.error('회원가입 중 오류:', error);
  }
}

const searchZipCode = () => {
  new daum.Postcode({
    oncomplete: function (data) {
      roadAddress.value = data.roadAddress;
      lotAddress.value = data.jibunAddress;
      detailedAddress.value = '';
    }
  }).open();
};

onMounted(() => {
  fetchDepartments();
  fetchJobs();
  fetchPositions();
});
</script>

<template>
  <div class="form-container">
    <h1 class="main-title">회원가입</h1>

    <div class="form-group">
      <label for="employeeName">이름</label>
      <InputText id="employeeName" v-model="employeeName" class="form-control" placeholder="이름을 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="email">이메일</label>
      <InputText id="email" v-model="email" class="form-control" placeholder="이메일을 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="password">비밀번호</label>
      <InputText id="password" type="password" v-model="password" class="form-control" placeholder="비밀번호를 입력해주세요" />
    </div>

    <div class="form-group">
      <label for="birthDate">생년월일</label>
      <DatePicker id="birthDate" v-model="birthDate" placeholder="생년월일을 선택하세요" />
    </div>

    <div class="form-group">
      <label for="phoneNumber">전화번호</label>
      <InputText id="phoneNumber" v-model="phoneNumber" class="form-control" placeholder="전화번호를 입력해주세요" />
    </div>

    <div class="form-group">

      <div class="flex justify-between items-center mb-5">
        <label class="text-xl">주소</label>
        <Button label="주소 검색" @click="searchZipCode" class="address-search-button" />
      </div>

      <label for="roadAddress">도로명 주소</label>
      <InputText id="roadAddress" v-model="roadAddress" class="form-control" placeholder="도로명 주소" />
    </div>

    <div class="form-group">
      <label for="lotAddress">지번 주소</label>
      <InputText id="lotAddress" v-model="lotAddress" class="form-control" placeholder="지번 주소" />
    </div>

    <div class="form-group">
      <label for="detailedAddress">상세 주소</label>
      <InputText id="detailedAddress" v-model="detailedAddress" class="form-control" placeholder="상세 주소" />
    </div>

    <div class="form-group">
      <label for="profileImage">프로필 이미지</label>
      <InputText class="mb-5" type="file" @change="handleFileUpload" />
      <img :src="photoUrl" alt="Profile Image Preview" v-if="photoUrl" class="profile-image-preview" />
    </div>

    <div class="form-group">
      <label for="joinDate">입사일</label>
      <DatePicker id="joinDate" v-model="joinDate" placeholder="입사일을 선택하세요" />
    </div>

    <div class="form-group">
      <label for="selectedDepartment">부서</label>
      <Select v-model="selectedDepartment" :options="departments" id="deptId" optionLabel="deptName"
        placeholder="부서를 선택하세요" @change="fetchTeams(selectedDepartment.deptId)" />
    </div>

    <div class="form-group">
      <label for="selectedTeam">팀</label>
      <Select v-model="selectedTeam" :options="teams" id="teamId" optionLabel="teamName" placeholder="팀을 선택하세요"
        :disabled="!isTeamSelectable" />
    </div>

    <div class="form-group">
      <label for="selectedJob">직무</label>
      <Select id="job" v-model="selectedJob" :options="jobs" optionLabel="jobName" placeholder="직무를 선택하세요" />
    </div>

    <div class="form-group">
      <label for="selectedPosition">직책</label>
      <Select id="position" v-model="selectedPosition" :options="positions" optionLabel="positionName"
        placeholder="직책을 선택하세요" />
    </div>

    <div class="form-group">
      <label for="role">역할</label>
      <Select id="role" v-model="role" :options="['ROLE_USER', 'ROLE_ADMIN']" placeholder="역할을 선택하세요" />
    </div>

    <div class="flex justify-end">
      <Button label="회원가입" @click="registerEmployee" />
    </div>
  </div>
</template>


<style scoped>
.form-container {
  width: 80vh;
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
  background-color: #6366f1;
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
