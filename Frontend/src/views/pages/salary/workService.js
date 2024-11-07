import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import fetchReissue from '../auth/service/fetchReissue';

const API_URL = 'https://hq-heroes-api.com/api/v1';

export const fetchTotalWorkHours = async (year, month) => {
    const router = useRouter();
    const route = useRoute();

    try {
        const response = await axios.get(`${API_URL}/attendance/total-work-hours`, {
            params: { year, month },
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access')
            }
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

export const fetchTotalOverTime = async (employeeId, year, month) => {
    const router = useRouter();
    const route = useRoute();

    const date = new Date(year, month - 1);
    date.setMonth(date.getMonth() - 1);

    const prevYear = date.getFullYear();
    const prevMonth = date.getMonth() + 1;

    try {
        const response = await axios.get(`${API_URL}/overtime/total-overtime`, {
            params: { employeeId: employeeId, yearMonth: `${prevYear}-${String(prevMonth).padStart(2, '0')}` },
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access')
            }
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
