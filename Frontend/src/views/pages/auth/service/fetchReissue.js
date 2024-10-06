import axios from 'axios';
import { useAuthStore } from '@/stores/authStore'; // Pinia store
import { useCookies } from 'vue3-cookies'; // 쿠키 관리 라이브러리

const fetchReissue = async () => {
  const { cookies } = useCookies();
  const authStore = useAuthStore();

  try {
    const response = await axios.post("http://localhost:8080/reissue", {}, {
      withCredentials: true
    });

    if (response.status === 200) { // 토큰 재발급 성공
      const newAccessToken = response.headers['access'];
      authStore.setAccessToken(newAccessToken); // Pinia에 access 토큰 저장
      return true;
    } else { // 토큰 재발급 실패
      authStore.setAccessToken(null); // Pinia에서 access 토큰 제거
      cookies.remove('refresh'); // 쿠키에서 refresh 토큰 제거
    }
  } catch (error) {
    console.error('토큰 재발급 에러: ', error);
  }

  return false;
};

export default fetchReissue;
