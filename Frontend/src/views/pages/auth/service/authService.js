import axios from 'axios';

const API_URL = 'http://localhost:8080';

// 회원가입 메서드
const register = async (employeeName, email, password, role, joinDate, birthDate, annualLeave, status, phoneNumber, roadAddress, lotAddress, detailedAddress, profileImageUrl, teamId, positionId, jobId) => {
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
  const credentials = new URLSearchParams();
  credentials.append('employeeId', employeeId); // 사번으로 로그인
  credentials.append('password', password);

  try {
      const response = await axios.post(`${API_URL}/login`, credentials, {
          headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
          },
          withCredentials: true
      });

      if (response.status === 200) {
          const { employeeId, accessToken } = response.data;  // 서버에서 받은 accessToken 포함
          window.localStorage.setItem('access', accessToken); // 토큰 저장
          window.localStorage.setItem('employeeId', employeeId); // employeeId 저장
          return { success: true, employeeId, accessToken };
      } else {
          throw new Error('로그인 실패');
      }
  } catch (error) {
      console.error('Login error:', error);
      throw new Error('로그인 실패. 다시 시도해주세요.');
  }
};

// 로그아웃 메서드 (변경 없음)
const logout = async () => {
    try {
        const response = await fetch(`${API_URL}/logout`, {
            method: 'POST',
            credentials: 'include'
        });
        if (response.ok) {
            alert('로그아웃 성공');
            window.localStorage.removeItem('access');
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
