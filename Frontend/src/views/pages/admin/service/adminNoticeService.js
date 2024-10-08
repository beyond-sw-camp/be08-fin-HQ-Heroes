import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/notice-service';

// 공지사항 등록
export const createNotice = async (noticeData) => {
  try {
    const response = await axios.post(`${API_URL}/notice`, noticeData);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// 공지사항 수정
export const updateNotice = async (noticeId, updatedData) => {
  try {
    const response = await axios.put(`${API_URL}/notice/${noticeId}`, updatedData);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// 공지사항 삭제
export const deleteNotice = async (noticeId) => {
  try {
    const response = await axios.delete(`${API_URL}/notice/${noticeId}`);
    return response.status === 200 || response.status === 204;
  } catch (error) {
    throw error;
  }
};

// 공지사항 조회
export const fetchNotices = async (category = null) => {
  try {
    const params = category ? { category: category.categoryName } : {};
    const response = await axios.get(`${API_URL}/notice`, { params });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// 특정 공지사항 조회
export const fetchNoticeById = async (id) => {
  const response = await fetch(`${API_URL}/notice/${id}`);
  if (!response.ok) {
    throw new Error('Error fetching notice');
  }
  return await response.json();
};