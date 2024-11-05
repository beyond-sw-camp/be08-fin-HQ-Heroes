import { fetchGet } from '../../auth/service/AuthApiService';

const API_URL = 'https://hq-heroes-api.com/api/v1/category-service';

// 모든 카테고리 조회
export const fetchCategories = async () => {
    try {
        const response = await fetchGet(`${API_URL}/categories`);
        return response;
    } catch (error) {
        throw error;
    }
};
