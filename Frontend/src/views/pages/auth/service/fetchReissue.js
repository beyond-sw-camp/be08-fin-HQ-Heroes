import { useAuthStore } from '@/stores/authStore'; // Pinia store
import axios from 'axios';
import { useCookies } from 'vue3-cookies'; // 쿠키 관리 라이브러리

const fetchReissue = async () => {
    const { cookies } = useCookies();
    const authStore = useAuthStore();

    try {
        const response = await axios.post(
            'https://hq-heroes-api.com/reissue',
            {},
            {
                withCredentials: true
            }
        );

        if (response.status === 200) {
            // 토큰 재발급 성공
            const accessTime = Date.now();

            const newAccessToken = response.headers['access'];
            authStore.setAccessToken(newAccessToken); // Pinia에 access 토큰 저장
            window.localStorage.setItem('access', newAccessToken);
            window.localStorage.setItem('accessTime', accessTime);

            // 페이지 새로고침
            window.location.reload(); // 페이지를 새로고침하여 변경사항을 반영
            return true;
        } else {
            // 토큰 재발급 실패
            authStore.setAccessToken(null); // Pinia에서 access 토큰 제거
            window.localStorage.removeItem('access');
            cookies.set('refresh', null, { maxAge: 0 });
        }
    } catch (error) {
        console.error('토큰 재발급 에러: ', error);
    }

    return false;
};

export default fetchReissue;
