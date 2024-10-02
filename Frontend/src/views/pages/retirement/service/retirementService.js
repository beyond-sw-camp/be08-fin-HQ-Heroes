import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const fetchAverageSalary = async (employeeId) => {
  try {
    const response = await axios.get(`${API_URL}/api/v1/retire-service/retire`, { params: { employeeId } });
    return response.data;
  } catch (error) {
    console.error(`Error fetching average salary with ID ${employeeId}:`, error);
    return null;
  }
};
