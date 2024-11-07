<script setup>
import Swal from 'sweetalert2';
import { onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import apiService from '../auth/service/authService';
import { fetchGet } from './service/AuthApiService';

// Form fields
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
const profileImageFile = ref(null);
const deptId = ref('');

// Options for dropdowns
const photoUrl = ref('https://via.placeholder.com/150');
const departments = ref([]);
const teams = ref([]);
const jobRoles = ref([]);
const positions = ref([]);
const isTeamSelectable = ref(false);
const selectedDepartment = ref(null);
const selectedTeam = ref(null);
const selectedJobRole = ref(null);
const selectedPosition = ref(null);
const router = useRouter();

// Error messages
const errors = ref({
    employeeName: '',
    email: '',
    password: '',
    birthDate: '',
    phoneNumber: '',
    roadAddress: '',
    detailedAddress: '',
    joinDate: '',
    role: '',
    selectedDepartment: '',
    selectedTeam: '',
    selectedJobRole: '',
    selectedPosition: ''
});

// Fetch departments, teams, jobRoles, and positions
async function fetchDepartments() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/departments');
        departments.value = [...response];
    } catch (error) {
        console.error('부서 데이터를 가져오는 중 오류 발생:', error);
    }
}

async function fetchTeams(selectedDeptId) {
    deptId.value = selectedDeptId;
    try {
        const response = await fetchGet(`https://hq-heroes-api.com/api/v1/employee/teams?deptId=${selectedDeptId}`);
        teams.value = response;
        isTeamSelectable.value = true;
    } catch (error) {
        console.error('팀 데이터를 가져오는 중 오류 발생:', error);
        isTeamSelectable.value = false;
        teams.value = []; // 오류 발생 시 빈 배열로 설정
    }
}

// 부서 및 팀에 따라 직원 목록 필터링
function filterByDepartmentAndTeam() {
    fetchTeams(selectedDepartment.value.deptId); // 선택한 부서 ID 전달
}

async function fetchJobRoles() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/jobs');
        jobRoles.value = [...response];
    } catch (error) {
        console.error('직무 데이터를 가져오는 중 오류 발생:', error);
    }
}

async function fetchPositions() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/employee/positions');
        positions.value = [...response];
    } catch (error) {
        console.error('직책 데이터를 가져오는 중 오류 발생:', error);
    }
}

// Handle file upload for profile image
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

// Register employee
async function registerEmployee() {
    if (!validateForm()) {
        return; // Stop if form is not valid
    }
    try {
        const response = await apiService.register(
            employeeName.value,
            email.value,
            password.value,
            role.value.value,
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
            selectedTeam.value?.teamId,
            selectedPosition.value?.positionId,
            selectedJobRole.value?.jobRoleId
        );
        await Swal.fire('사원 등록 성공', `사원 번호 : ${response.joinResult}`, 'success');
        router.push('/');
    } catch (error) {
        Swal.fire('사원 등록 실패', '다시 시도해주세요.', 'error');
        console.error('사원 등록 중 오류:', error);
    }
}

// Search for zip code
const searchZipCode = () => {
    new daum.Postcode({
        oncomplete: function (data) {
            roadAddress.value = data.roadAddress;
            lotAddress.value = data.jibunAddress;
            detailedAddress.value = '';
        }
    }).open();
};

// Validate the entire form before submission
function validateForm() {
    let isValid = true;

    if (!employeeName.value) {
        errors.value.employeeName = '이름을 입력해주세요.';
        isValid = false;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email.value || !emailPattern.test(email.value)) {
        errors.value.email = '유효한 이메일 주소를 입력해주세요.';
        isValid = false;
    }

    const passwordPattern = /^(?=.*[!@#$%^&*(),.?":{}|<>])(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*(),.?":{}|<>]{8,}$/;
    if (!passwordPattern.test(password.value)) {
        errors.value.password = '비밀번호는 8자 이상, 숫자와 특수문자를 포함해야 합니다.';
        isValid = false;
    }

    if (!birthDate.value) {
        errors.value.birthDate = '생년월일을 선택해주세요.';
        isValid = false;
    }

    const phonePattern = /^[0-9]{10,11}$/;
    if (!phonePattern.test(phoneNumber.value)) {
        errors.value.phoneNumber = '유효한 전화번호를 입력해주세요.';
        isValid = false;
    }

    if (!roadAddress.value) {
        errors.value.roadAddress = '도로명 주소를 입력해주세요.';
        isValid = false;
    }

    if (!detailedAddress.value) {
        errors.value.detailedAddress = '상세 주소를 입력해주세요.';
        isValid = false;
    }

    if (!joinDate.value) {
        errors.value.joinDate = '입사일을 선택해주세요.';
        isValid = false;
    }

    if (!role.value) {
        errors.value.role = '역할을 선택해주세요.';
        isValid = false;
    }

    if (!selectedDepartment.value) {
        errors.value.selectedDepartment = '부서를 선택해주세요.';
        isValid = false;
    }

    if (isTeamSelectable.value && !selectedTeam.value) {
        errors.value.selectedTeam = '팀을 선택해주세요.';
        isValid = false;
    }

    if (!selectedJobRole.value) {
        errors.value.selectedJobRole = '직무를 선택해주세요.';
        isValid = false;
    }

    if (!selectedPosition.value) {
        errors.value.selectedPosition = '직책을 선택해주세요.';
        isValid = false;
    }

    return isValid;
}

// Watchers for real-time validation
watch(employeeName, (newValue) => {
    errors.value.employeeName = !newValue ? '이름을 입력해주세요.' : '';
});

watch(email, (newValue) => {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    errors.value.email = !newValue || !emailPattern.test(newValue) ? '유효한 이메일 주소를 입력해주세요.' : '';
});

watch(password, (newValue) => {
    const passwordPattern = /^(?=.*[!@#$%^&*(),.?":{}|<>])(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*(),.?":{}|<>]{8,}$/;
    errors.value.password = !passwordPattern.test(newValue) ? '비밀번호는 8자 이상, 숫자와 특수문자를 포함해야 합니다.' : '';
});

watch(birthDate, (newValue) => {
    errors.value.birthDate = !newValue ? '생년월일을 선택해주세요.' : '';
});

watch(phoneNumber, (newValue) => {
    const phonePattern = /^[0-9]{10,11}$/;
    errors.value.phoneNumber = !phonePattern.test(newValue) ? '유효한 전화번호를 입력해주세요.' : '';
});

watch(roadAddress, (newValue) => {
    errors.value.roadAddress = !newValue ? '도로명 주소를 입력해주세요.' : '';
});

watch(detailedAddress, (newValue) => {
    errors.value.detailedAddress = !newValue ? '상세 주소를 입력해주세요.' : '';
});

watch(joinDate, (newValue) => {
    errors.value.joinDate = !newValue ? '입사일을 선택해주세요.' : '';
});

watch(role, (newValue) => {
    errors.value.role = !newValue ? '역할을 선택해주세요.' : '';
});

watch(selectedDepartment, (newValue) => {
    errors.value.selectedDepartment = !newValue ? '부서를 선택해주세요.' : '';
});

watch(selectedTeam, (newValue) => {
    errors.value.selectedTeam = isTeamSelectable.value && !newValue ? '팀을 선택해주세요.' : '';
});

watch(selectedJobRole, (newValue) => {
    errors.value.selectedJobRole = !newValue ? '직무를 선택해주세요.' : '';
});

watch(selectedPosition, (newValue) => {
    errors.value.selectedPosition = !newValue ? '직책을 선택해주세요.' : '';
});

// Lifecycle hook
onMounted(() => {
    fetchDepartments();
    fetchJobRoles();
    fetchPositions();
});
</script>

<template>
    <div class="form-container">
        <h1 class="main-title">사원 등록</h1>

        <div class="form-group">
            <label for="employeeName">이름</label>
            <InputText id="employeeName" v-model="employeeName" class="form-control" placeholder="이름을 입력해주세요" />
            <small v-if="errors.employeeName" class="error-text">{{ errors.employeeName }}</small>
        </div>

        <div class="form-group">
            <label for="email">이메일</label>
            <InputText id="email" v-model="email" class="form-control" placeholder="이메일을 입력해주세요" />
            <small v-if="errors.email" class="error-text">{{ errors.email }}</small>
        </div>

        <div class="form-group">
            <label for="password">비밀번호</label>
            <InputText id="password" type="password" v-model="password" class="form-control" placeholder="비밀번호를 입력해주세요" />
            <small v-if="errors.password" class="error-text">{{ errors.password }}</small>
        </div>

        <div class="form-group">
            <label for="birthDate">생년월일</label>
            <DatePicker id="birthDate" v-model="birthDate" placeholder="생년월일을 선택하세요" />
            <small v-if="errors.birthDate" class="error-text">{{ errors.birthDate }}</small>
        </div>

        <div class="form-group">
            <label for="phoneNumber">전화번호</label>
            <InputText id="phoneNumber" v-model="phoneNumber" class="form-control" placeholder="전화번호를 입력해주세요" />
            <small v-if="errors.phoneNumber" class="error-text">{{ errors.phoneNumber }}</small>
        </div>

        <div class="form-group">
            <div class="flex justify-between items-center mb-5">
                <label class="text-xl">주소</label>
                <Button label="주소 검색" @click="searchZipCode" class="address-search-button" />
            </div>

            <label for="roadAddress">도로명 주소</label>
            <InputText id="roadAddress" v-model="roadAddress" class="form-control" placeholder="도로명 주소" />
            <small v-if="errors.roadAddress" class="error-text">{{ errors.roadAddress }}</small>
        </div>

        <div class="form-group">
            <label for="lotAddress">지번 주소</label>
            <InputText id="lotAddress" v-model="lotAddress" class="form-control" placeholder="지번 주소" />
        </div>

        <div class="form-group">
            <label for="detailedAddress">상세 주소</label>
            <InputText id="detailedAddress" v-model="detailedAddress" class="form-control" placeholder="상세 주소" />
            <small v-if="errors.detailedAddress" class="error-text">{{ errors.detailedAddress }}</small>
        </div>

        <div class="form-group">
            <label for="profileImage">프로필 이미지</label>
            <input type="file" @change="handleFileUpload" class="form-control" />
            <img :src="photoUrl" alt="Profile Image Preview" v-if="photoUrl" class="profile-image-preview" />
        </div>

        <div class="form-group">
            <label for="joinDate">입사일</label>
            <DatePicker id="joinDate" v-model="joinDate" placeholder="입사일을 선택하세요" />
            <small v-if="errors.joinDate" class="error-text">{{ errors.joinDate }}</small>
        </div>

        <hr class="divider mb-2" />

        <!-- 부서와 팀을 한 row에 배치 -->
        <div class="form-row">
            <div class="form-group">
                <label for="selectedDepartment">부서</label>
                <Select v-model="selectedDepartment" :options="departments" id="deptId" optionLabel="deptName" placeholder="부서를 선택하세요" @change="fetchTeams(selectedDepartment.deptId)" />
                <small v-if="errors.selectedDepartment" class="error-text">{{ errors.selectedDepartment }}</small>
            </div>

            <div class="form-group ml-4">
                <label for="selectedTeam">팀</label>
                <Select v-model="selectedTeam" :options="teams" id="teamId" optionLabel="teamName" placeholder="팀을 선택하세요" @change="filterByDepartmentAndTeam" :disabled="!isTeamSelectable" />
                <small v-if="errors.selectedTeam" class="error-text">{{ errors.selectedTeam }}</small>
            </div>
        </div>
        <hr class="divider mb-2" />

        <!-- 직무와 직책을 한 row에 배치 -->
        <div class="form-row">
            <div class="form-group">
                <label for="selectedJobRole">직무</label>
                <Select id="jobRole" v-model="selectedJobRole" :options="jobRoles" optionLabel="jobRoleName" placeholder="직무를 선택하세요" />
                <small v-if="errors.selectedJobRole" class="error-text">{{ errors.selectedJobRole }}</small>
            </div>

            <div class="form-group ml-4">
                <label for="selectedPosition">직책</label>
                <Select id="position" v-model="selectedPosition" :options="positions" optionLabel="positionName" placeholder="직책을 선택하세요" />
                <small v-if="errors.selectedPosition" class="error-text">{{ errors.selectedPosition }}</small>
            </div>
        </div>
        <hr class="divider mb-2" />

        <div class="form-group">
            <label for="role">역할</label>
            <Select
                id="role"
                v-model="role"
                :options="[
                    { label: '사원', value: 'ROLE_USER' },
                    { label: '관리자', value: 'ROLE_ADMIN' }
                ]"
                optionLabel="label"
                placeholder="역할을 선택하세요"
            />
            <small v-if="errors.role" class="error-text">{{ errors.role }}</small>
        </div>

        <div class="flex justify-end">
            <Button label="사원 등록" @click="registerEmployee" />
        </div>
    </div>
</template>

<style scoped>
.form-container {
    width: 55rem;
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

.form-row {
    display: flex;
    gap: 1rem;
    width: 100%;
    flex-wrap: nowrap;
}

.form-group {
    flex: 1;
    margin-bottom: 10px;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: bold;
}

/* 이미지 미리보기 */
.profile-image-preview {
    display: block;
    width: 150px;
    height: 150px;
    border-radius: 50%;
    margin: 10px auto;
    object-fit: cover;
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

.error-text {
    color: red;
    font-size: 0.9em;
    margin-top: 5px;
}
</style>
