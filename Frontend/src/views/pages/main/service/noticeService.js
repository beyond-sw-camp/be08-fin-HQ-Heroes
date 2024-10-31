import { fetchGet } from '../../auth/service/AuthApiService';

const API_URL = 'http://localhost:8080';

export const getNotices = async () => {
    try {
        const response = await fetchGet(`${API_URL}/api/v1/notice-service/notice`   );
        return response;
    } catch (error) {
        console.error('Error fetching notices:', error);
        return [];
    }
};
