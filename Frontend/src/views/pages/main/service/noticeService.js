import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const getNotices = async (category = null) => {
    try {
        const response = await axios.get(`${API_URL}/api/v1/notice-service/notice`, {
            params: { category }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching notices:', error);
        return [];
    }
};
