import { fetchGet } from '../auth/service/AuthApiService';

const API_URL = 'http://localhost:8080/api/v1/deduct-service/deductions';

// 모든 공제 항목 가져오기
export const fetchDeductions = async () => {
    return await fetchGet(`${API_URL}`);
};