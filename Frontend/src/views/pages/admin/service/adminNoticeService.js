import { fetchDelete, fetchGet, fetchPost, fetchPut } from '../../auth/service/AuthApiService';

const API_URL = 'https://hq-heroes-api.com/api/v1/notice-service';

// 공지사항 등록
export const createNotice = async (noticeData) => {
    try {
        return await fetchPost(`${API_URL}/notice`, noticeData);
    } catch (error) {
        console.log('공지사항 등록 실패 ', error);
    }
};

// 공지사항 수정
export const updateNotice = async (noticeId, updatedData) => {
    try {
        return await fetchPut(`${API_URL}/notice/${noticeId}`, updatedData);
    } catch (error) {
        console.log('공지사항 수정 실패 ', error);
    }
};

// 공지사항 삭제
export const deleteNotice = async (noticeId) => {
    try {
        return await fetchDelete(`${API_URL}/notice/${noticeId}`);
    } catch (error) {
        console.log('공지사항 삭제 실패 ', error);
    }
};

// 공지사항 목록 조회
export const fetchNotices = async () => {
    try {
        return await fetchGet(`${API_URL}/notice`);
    } catch (error) {
        console.log('공지사항 목록 조회 실패 ', error);
    }
};

// 특정 공지사항 조회
export const fetchNoticeById = async (id) => {
    try {
        return await fetchGet(`${API_URL}/notice/${id}`);
    } catch (error) {
        console.log(`${id}번 공지사항 조회 실패 `, error);
    }
};
