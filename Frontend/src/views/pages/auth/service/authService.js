import { useAuthStore } from '@/stores/authStore';
import axios from 'axios';
import { useCookies } from 'vue3-cookies';

const API_URL = 'http://localhost:8080';
const { cookies } = useCookies(); // Initialize the cookies

// 회원가입 메서드
const register = async (employeeName, email, password, role, joinDate, birthDate, annualLeave, status, phoneNumber, roadAddress, lotAddress, detailedAddress, profileImageUrl, deptId, teamId, positionId, jobId) => {
  const formData = new FormData();
  formData.append('employeeName', employeeName);
  formData.append('password', password);
  formData.append('email', email);
  formData.append('role', role);
  formData.append('joinDate', joinDate);
  formData.append('birthDate', birthDate);
  formData.append('annualLeave', annualLeave);
  formData.append('status', status);
  formData.append('phoneNumber', phoneNumber);
  formData.append('roadAddress', roadAddress);
  formData.append('lotAddress', lotAddress);
  formData.append('detailedAddress', detailedAddress);
  formData.append('profileImageUrl', profileImageUrl);
  formData.append('deptId', deptId);
  formData.append('teamId', teamId);
  formData.append('positionId', positionId);
  formData.append('jobId', jobId);

  try {
    const response = await axios.post(`${API_URL}/join`, formData);
    console.log(response)
    return response.data;
  } catch (error) {
    throw new Error('회원가입 실패. 다시 시도해주세요.');
  }
};

const login = async (employeeId, password) => {
  const credentials = new URLSearchParams({
    username: employeeId,
    password: password
  });

  const authStore = useAuthStore();

  try {
    const response = await axios.post(`${API_URL}/login`, credentials, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      withCredentials: true
    });

    if (response.status === 200) {
      const { employeeId, access } = response.data; // 'access'를 사용
      const accessToken = response.headers['access']; // 헤더에서 access 토큰을 추출

      window.localStorage.setItem('access', accessToken);
      window.localStorage.setItem('employeeId', employeeId);

      cookies.set('access', accessToken); // 쿠키 등록

      authStore.setIsLoggedIn(true);
      authStore.setLoginUser(employeeId);
      authStore.setAccessToken(accessToken);

      console.log('로그인 성공:', authStore);
      return { success: true, employeeId, accessToken };
    } else {
      throw new Error('로그인 실패: 상태 코드가 200이 아닙니다.');
    }
  } catch (error) {
    console.error('Login error:', error);
    throw new Error('로그인 실패. 다시 시도해주세요.');
  }
};

// 로그아웃 메서드
const logout = async () => {
  try {
    const response = await fetch(`${API_URL}/logout`, {
      method: 'POST',
      credentials: 'include'
    });
    if (response.ok) {
      alert('로그아웃 성공');
      window.localStorage.removeItem('access');
      cookies.remove('access'); // 쿠키에서 access 토큰 제거
    } else {
      alert('로그아웃 실패');
    }
  } catch (error) {
    console.error('로그아웃 중 오류 발생: ', error);
  }
};

export default {
  register,
  login,
  logout
};
