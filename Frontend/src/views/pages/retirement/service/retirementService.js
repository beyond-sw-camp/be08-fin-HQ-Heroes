import { fetchGet } from "../../auth/service/AuthApiService";

const API_URL = 'http://localhost:8080/api/v1/salary-service/histories';

// 최근 3개월 급여 합계 가져오기
export const fetchSalarySum = async (employeeId) => {
    return await fetchGet(`${API_URL}/last-three-months/sum/${employeeId}`) / 3;
};