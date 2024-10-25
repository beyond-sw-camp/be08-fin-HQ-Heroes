import { fetchGet } from '../auth/service/AuthApiService';

const API_URL = 'http://localhost:8080/api/v1/salary-service';

// 급여 기록 데이터 가져오기
export const fetchSalary = async (employeeId) => {
    return await fetchGet(`${API_URL}/histories/${employeeId}`);
};

// 성과급 데이터 가져오기
export const fetchBonus = async (employeeId) => {
    return await fetchGet(`${API_URL}/salaries/${employeeId}`);
};

// 급여 데이터 가져오기
export const fetchbaseSalary = async (employeeId) => {
    return await fetchGet(`${API_URL}/base-salary/${employeeId}`);
};