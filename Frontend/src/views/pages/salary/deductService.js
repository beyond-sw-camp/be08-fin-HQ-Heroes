import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/deduct-service/deductions';

// 모든 공제 항목 가져오기
export const fetchDeductions = async () => {
    try {
      const response = await axios.get(`${API_URL}`); 
      return response.data;
    } catch (error) {
      console.error('Error fetching deductions:', error);
      throw error;
    }
};