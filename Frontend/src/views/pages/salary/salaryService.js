import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/salary-service';

// 연도별 급여 데이터 가져오기
export const fetchSalariesByEmployeeIdAndYear = async (employeeId, year) => {
  try {
    const response = await axios.get(`${API_URL}/histories`, {
      params: { employeeId, year },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching salary data by year:', error);
    throw error;
  }
};

// 해당 년도, 해당 월의 급여 데이터 가져오기
export const fetchSalaryByEmployeeIdAndMonth = async (employeeId, year, month) => {
  try {
    const response = await axios.get(`${API_URL}/histories/month`, {
      params: { employeeId, year, month },
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching salary data by month:', error);
    throw error;
  }
};

// 해당 년도, 해당 월의 성과급 데이터 가져오기
export const fetchBonusByEmployeeIdAndMonth = async (employeeId, year, month) => {
  try {
    const response = await axios.get(`${API_URL}/salaries/${employeeId}/year/${year}/month/${month}`, {
      params: { employeeId, year, month},
    });
    console.log(response);
    return response.data;
  } catch (error) {
    console.error('Error fetching salary data by year:', error);
    throw error;
  }
};