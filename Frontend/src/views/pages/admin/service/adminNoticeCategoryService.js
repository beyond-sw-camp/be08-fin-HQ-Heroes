import { fetchGet } from '../../auth/service/AuthApiService';

const API_URL = 'http://localhost:8080/api/v1/category-service';

// 모든 카테고리 조회
export const fetchCategories = async () => {
    try {
        const response = await fetchGet(`${API_URL}/categories`);
        return response;
    } catch (error) {
        throw error;
    }
};
