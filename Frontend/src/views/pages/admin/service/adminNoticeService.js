import axios from 'axios';

const API_URL = 'http://localhost:8080';

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

// 공지사항 조회
export const fetchNotices = async (category = null) => {
  try {
    const params = { category }; // 카테고리 params에 추가
    const response = await axios.get(`${API_URL}/api/v1/notice-service/notice`, { params });
    return response.data;
  } catch (error) {
    console.error('Failed to fetch notices: ', error);
    throw error;
  }
};
