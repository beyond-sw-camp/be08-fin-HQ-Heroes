import axios from 'axios';
import fetchReissue from './fetchReissue';

const fetchGet = async (url) => {
    try {
        const response = await axios.get(url, {
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access')
            }
        });

        if (response.status === 200) {
            return response.data;
        } else {
            const reissueSuccess = await fetchReissue();
            if (reissueSuccess) {
                return await fetchAuthorizedPage(url);
            }
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
    }
    return null;
};

const fetchPost = async (url, data) => {
    let reissueAttempted = false;

    try {
        const response = await axios.post(url, data, {
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access')
            }
        });

        if (response.status === 201 || response.status === 200) {
            return response.data;
        } else if (!reissueAttempted) {
            const reissueSuccess = await fetchReissue();
            if (reissueSuccess) {
                reissueAttempted = true;
                return await fetchPost(url, data); // 토큰 재발급 후 재요청
            }
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
        return null; // 오류 발생 시 null 반환
    }
    return null;
};

const fetchPostThrowError = async (url, data) => {
    let reissueAttempted = false;

    try {
        const response = await axios.post(url, data, {
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access')
            }
        });

        if (response.status === 201 || response.status === 200) {
            return response.data;
        } else if (!reissueAttempted) {
            const reissueSuccess = await fetchReissue();
            if (reissueSuccess) {
                reissueAttempted = true;
                return await fetchPost(url, data); // 토큰 재발급 후 재요청
            }
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
        throw error; // 오류 발생 시 예외를 던짐
    }
    throw new Error('Unexpected response status'); // 예상치 못한 응답 상태 처리
};


const fetchPut = async (url, data) => {
    try {
        const response = await axios.put(url, data, {
            withCredentials: true, // 인증 토큰이 필요한 경우
            headers: {
                access: window.localStorage.getItem('access') // 인증 헤더 확인
            }
        });

        if (response.status === 201 || response.status === 200) {
            return response.data;
        } else {
            throw new Error(`Unexpected status code: ${response.status}`);
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
        throw error; // 에러 핸들링 추가
    }
};

const fetchPutEmployee = async (url, employeeData, profileImageFile) => {
    const formData = new FormData();
    formData.append('employeeData', new Blob([JSON.stringify(employeeData)], { type: 'application/json' }));

    if (profileImageFile) {
        formData.append('profileImage', profileImageFile, `${employeeData.employeeId}_profile.png`);
    }

    try {
        const response = await axios.put(url, formData, {
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access'),
                'Content-Type': 'multipart/form-data'
            }
        });

        if (response.status === 200) {
            console.log(response.data);
            return response.data;
        } else {
            const reissueSuccess = await fetchReissue();
            if (reissueSuccess) {
                return await fetchPut(url, employeeData, profileImageFile);
            }
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
        throw error; // 에러를 다시 던져서 호출하는 곳에서 처리 가능
    }
};

const fetchDelete = async (url) => {
    try {
        const response = await axios.delete(url, {
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access')
            }
        });

        if (response.status === 200) {
            return response.data;
        } else {
            const reissueSuccess = await fetchReissue();
            if (reissueSuccess) {
                return await fetchDelete(url);
            }
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
    }
    return null;
};

export { fetchDelete, fetchGet, fetchPost, fetchPut, fetchPutEmployee, fetchPostThrowError };
