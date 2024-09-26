import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/notice-service';

// 공지사항 등록
export const createNotice = async (noticeData) => {
  try {
    const response = await axios.post(`${API_URL}/notice`, noticeData);
    return response.data;
  } catch (error) {
    console.error('Failed to create notice:', error);
    throw error;
  }
};

// 공지사항 수정
export const updateNotice = async (noticeId, updatedData) => {
  try {
    const response = await axios.put(`${API_URL}/notice/${noticeId}`, updatedData);
    return response.data;
  } catch (error) {
    console.error(`Failed to update notice with ID ${noticeId}:`, error);
    throw error;
  }
};

// 공지사항 삭제
export const deleteNotice = async (noticeId) => {
  try {
    const response = await axios.delete(`${API_URL}/notice/${noticeId}`);
    return response.status === 200;
  } catch (error) {
    console.error(`Failed to delete notice with ID ${noticeId}:`, error);
    throw error;
  }
};
