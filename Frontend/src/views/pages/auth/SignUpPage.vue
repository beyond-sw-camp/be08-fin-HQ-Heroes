<script setup>
import { ref, onMounted } from 'vue';
import apiService from '../auth/service/authService'; // register 메서드를 포함한 서비스
import Swal from 'sweetalert2';
import router from '@/router';
import axios from 'axios'; // axios 추가

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
const profileImageFile = ref(null); // 파일 저장을 위한 변수 추가
const departmentId = ref('');
const teamId = ref('');
const positionId = ref('');
const jobId = ref('');

const photoUrl = ref('https://via.placeholder.com/150'); // 초기 사진 URL

// 부서 및 팀 목록을 위한 ref
const departments = ref([]);
const teams = ref([]);
const jobs = ref([]);
const positions = ref([]);

// 부서 목록을 가져오는 함수
async function fetchDepartments() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/departments'); // 부서 API 호출
        departments.value = [...response.data]; // '전체 부서'에 deptId 추가
    } catch (error) {
        console.error('부서 데이터를 가져오는 중 오류 발생:', error);
        departments.value = [{ deptId: null, deptName: '전체 부서' }];
    }
}

// 선택된 부서 ID에 따라 팀 목록을 가져오는 함수
async function fetchTeams(deptId) {
    departmentId.value = deptId;
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/teams', {
            params: { deptId: deptId } // 부서 ID 전달
        });
        teams.value = [...response.data]; // DTO에서 teamName 사용
    } catch (error) {
        console.error('팀 데이터를 가져오는 중 오류 발생:', error);
        teams.value = [{ teamId: null, teamName: '전체 팀' }];
    }
}

async function selectedTeam(value) {
    teamId.value = value;
}

// 직무 목록을 가져오는 함수
async function fetchJobs() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/jobs'); // 직무 API 호출
        jobs.value = [...response.data]; // '전체 직무'에 jobId 추가
    } catch (error) {
        console.error('직무 데이터를 가져오는 중 오류 발생:', error);
        jobs.value = [{ jobId: null, jobName: '전체 직무' }];
    }
}

// 직무 목록을 가져오는 함수
async function fetchPositions() {
    try {
        const response = await axios.get('http://localhost:8080/api/v1/employee/positions'); // 직책 API 호출
        positions.value = [...response.data]; // '전체 직책'에 jobId 추가
    } catch (error) {
        console.error('직책 데이터를 가져오는 중 오류 발생:', error);
        positions.value = [{ positionId: null, positionName: '전체 직책' }];
    }
}

// 파일 업로드 핸들러 (FormData에 담기 위해 파일 객체 저장)
const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
        profileImageFile.value = file; // 파일 객체 저장
        const reader = new FileReader();
        reader.onload = (e) => {
            photoUrl.value = e.target.result; // 사진 미리보기 업데이트
        };
        reader.readAsDataURL(file);   
    } else {
        // 파일이 선택되지 않은 경우 기본 이미지로 초기화
        photoUrl.value = 'https://via.placeholder.com/150';
    }
};

// 회원가입 함수
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
      profileImageFile.value,
      departmentId.value,
      teamId.value,
      positionId.value,
      jobId.value
    );
    Swal.fire('회원가입 성공', '회원가입이 완료되었습니다.', 'success');
    router.push('/');
  } catch (error) {
    Swal.fire('회원가입 실패', '다시 시도해주세요.', 'error');
    console.error('회원가입 중 오류:', error);
  }
}

// 주소 검색 API 연동
const searchZipCode = () => {
    new daum.Postcode({
        oncomplete: function (data) {
            roadAddress.value = data.roadAddress; // 도로명 주소
            lotAddress.value = data.jibunAddress; // 지번 주소
            detailedAddress.value = ''; // 상세 주소는 빈 문자열로 설정
        }
    }).open();
};

// 컴포넌트가 마운트될 때 부서, 직무 및 직책 데이터를 가져옴
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
            <label for="birthDate">생년월일</label>
            <input type="date" id="birthDate" v-model="birthDate" class="form-control" />
        </div>

        <div class="form-group">
            <label for="phoneNumber">연락처</label>
            <input type="text" id="phoneNumber" v-model="phoneNumber" class="form-control" placeholder="연락처를 입력해주세요" />
        </div>

        <div class="form-group">
            <label for="profileImage">증명사진</label>

            <!-- 파일 업로드 입력 필드 -->
            <input type="file" id="profileImage" @change="handleFileUpload" accept="image/*" />

            <!-- 증명사진 미리보기 (사진이 없을 경우 기본 이미지로 표시) -->
            <img v-if="photoUrl" :src="photoUrl" alt="증명사진 미리보기" width="150" height="150" style="margin-top: 10px" />

            <!-- 기본 이미지가 표시되지 않을 경우 대비 -->
            <img v-else src="https://via.placeholder.com/150" alt="기본 이미지" width="150" height="150" style="margin-top: 10px" />
        </div>

        <!-- 직원주소 -->
        <div class="form-group">
            <div class="flex justify-between items-center">
                <label for="employeeAddress" class="mr-2">도로명 주소</label>
                <Button class="mb-5" @click="searchZipCode">우편번호 검색</Button>
            </div>

            <div class="address-group">
                <input type="text" id="employeeAddress" v-model="roadAddress" class="form-control address-input" disabled />
            </div>
        </div>

        <!-- 주소 -->
        <div class="form-group">
            <label for="address">지 번</label>
            <input type="text" id="address" v-model="lotAddress" class="form-control" disabled />
        </div>

        <!-- 상세 주소 -->
        <div class="form-group">
            <label for="detailedAddress">상세 주소</label>
            <input type="text" id="detailedAddress" v-model="detailedAddress" class="form-control detailed-address" />
        </div>

        <div class="form-group">
            <label for="role">역할</label>
            <select id="role" v-model="role" class="form-control">
                <option value="" disabled selected>역할을 선택하세요</option>
                <option value="ROLE_USER">USER</option>
                <option value="ROLE_ADMIN">ADMIN</option>
            </select>
        </div>

        <div class="form-group">
            <label for="departmentId">부서</label>
            <select id="departmentId" @change="fetchTeams($event.target.value)" class="form-control">
                <option value="" disabled selected>부서를 선택하세요</option>
                <option v-for="dept in departments" :key="dept.deptId" :value="dept.deptId">{{ dept.deptName }}</option>
            </select>
        </div>

        <div class="form-group">
            <label for="teamId">팀</label>
            <select id="teamId" @change="selectedTeam($event.target.value)" class="form-control">
                <option value="" disabled selected>팀을 선택하세요</option>
                <option v-for="team in teams" :key="team.teamId" :value="team.teamId">{{ team.teamName }}</option>
            </select>
        </div>

        <div class="form-group">
            <label for="jobId">직무</label>
            <select id="jobId" v-model="jobId" class="form-control">
                <option value="" disabled selected>직무를 선택하세요</option>
                <option v-for="job in jobs" :key="job.jobId" :value="job.jobId">{{ job.jobName }}</option>
            </select>
        </div>

        <div class="form-group">
            <label for="positionId">직책</label>
            <select id="positionId" v-model="positionId" class="form-control">
                <option value="" disabled selected>직책을 선택하세요</option>
                <option v-for="position in positions" :key="position.positionId" :value="position.positionId">{{ position.positionName }}</option>
            </select>
        </div>
        <button @click="registerEmployee" class="btn-submit">회원가입</button>
    </div>
</template>

<style scoped>
.form-container {
    width: 80vh;
    margin: auto;
    margin-top: 20px;
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
