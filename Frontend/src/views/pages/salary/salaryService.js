import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/salary-service';

// 현재 년도까지 데이터 가져오기
export const fetchSalaryDataUpToCurrentMonth = async (employeeId, year) => {
  try {
    const response = await axios.get(`${API_URL}/salaries`, {
      params: { employeeId, year }
    });
    return response.data; // 각 월에 대한 데이터 반환
  } catch (error) {
    console.error('Error fetching salary data up to current month:', error);
    throw error;
  }
};

// 특정 월의 급여 데이터 가져오기
export const fetchSalaryDetailsForMonth = async (employeeId, year, month) => {
  try {
    const response = await axios.get(`${API_URL}/salaries`, {
      params: { employeeId, year, month }
    });
    return response.data; // 선택한 월의 급여 데이터 반환
  } catch (error) {
    console.error('Error fetching salary details for month:', error);
    throw error;
  }
};

