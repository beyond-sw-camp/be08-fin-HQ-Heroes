import { fetchGet } from '../auth/service/AuthApiService';

const API_URL = 'https://hq-heroes-api.com/api/v1/salary-service';

// 급여 기록 데이터 가져오기
export const fetchSalary = async (employeeId) => {
    return await fetchGet(`${API_URL}/histories/${employeeId}`);
};
