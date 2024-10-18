import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import fetchReissue from '../auth/service/fetchReissue';

const API_URL = 'http://localhost:8080/api/v1/attendance';

export const fetchTotalWorkHours = async (year, month) => {
    const router = useRouter();
    const route = useRoute();

    try {
        const response = await axios.get(`${API_URL}/total-work-hours`, {
            params: { year, month },
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access'),
            },
        });

        if (response.status === 200) {
            return response.data;
        } else {
            const reissueSuccess = await fetchReissue();
            if (reissueSuccess) {
                return await fetchTotalWorkHours(year, month);
            } else {
                alert('관리자가 아닙니다.');
                router.push({ path: '/login', query: { redirect: route.path } });
            }
        }
    } catch (error) {
        console.error('총 근무 시간 조회 오류:', error);
        return 0;
    }
};
