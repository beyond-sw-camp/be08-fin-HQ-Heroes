import axios from 'axios';

const API_URL = 'http://120.50.90.75:31001';

const register = async (email, name, password) => {
  const formData = new FormData();
  formData.append('email', email);
  formData.append('name', name);
  formData.append('password', password);
  try {
    const response = await axios.post(`${API_URL}/join`, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });

    return response.data;
  } catch (error) {
    throw new Error('회원가입 실패. 다시 시도해주세요.');
  }
};

const login = async (username, password) => {
  const credentials = new URLSearchParams();
  credentials.append('username', username);
  credentials.append('password', password);

  try {
    const response = await axios.post(`${API_URL}/login`, credentials, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      withCredentials: true,
    });

    if (response.status === 200) {
      const { email } = response.data;

      return { success: true, email };
    } else {
      throw new Error('로그인 실패');
    }
  } catch (error) {
    console.error('Login error:', error);
    throw new Error('로그인 실패. 다시 시도해주세요.');
  }
};

const logout = async () => {
  try {
    const response = await fetch(`${API_URL}/logout`, {
      method: "POST",
      credentials: "include",
    });
    if (response.ok) {
      alert("로그아웃 성공");
      window.localStorage.removeItem("access");
    } else {
      alert("로그아웃 실패");
    }
  } catch (error) {
    console.error("로그아웃 중 오류 발생: ", error);
  }
};

const onNaverLogin = () => {
  window.location.href = `${API_URL}/oauth2/authorization/naver`;
};

const onGoogleLogin = () => {
  window.location.href = `${API_URL}/oauth2/authorization/google`;
};

export default {
  register,
  login,
  logout,
  onNaverLogin,
  onGoogleLogin,
};
