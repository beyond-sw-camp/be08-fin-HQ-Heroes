import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import fetchReissue from './fetchReissue';

const fetchGet = async (url) => {
    const router = useRouter();
    const route = useRoute();

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
            } else {
                alert('관리자가 아닙니다.');
                router.push({ path: '/login', query: { redirect: route.path } });
            }
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
    }
    return null;
};

const fetchPost = async (url, data) => {
    const router = useRouter();
    const route = useRoute();
    let reissueAttempted = false;  // 재발급 시도 여부를 체크하는 플래그

    try {
        const response = await axios.post(url, data, {
            withCredentials: true,
            headers: {
                access: window.localStorage.getItem('access')
            }
        });

        if (response.status === 201) {
            return response.data;
        } else if (!reissueAttempted) {
            // 첫 재발급 시도
            const reissueSuccess = await fetchReissue();
            if (reissueSuccess) {
                reissueAttempted = true;  // 재발급을 시도한 상태로 변경
                return await fetchPost(url, data);  // 재귀 호출
            }
        }

        // 재발급 실패 또는 권한 없음
        alert('관리자가 아닙니다.');
        router.push({ path: '/login', query: { redirect: route.path } });

    } catch (error) {
        console.error('Error fetching authorized page:', error);
    }
    return null;
};


const fetchPut = async (url, employeeData, profileImageFile) => {
    const router = useRouter();
    const route = useRoute();
  try {
    const response = await axios.post(url, data, {
      withCredentials: true,  // 인증 토큰이 필요한 경우
      headers: {
        'access': window.localStorage.getItem('access'),  // 인증 헤더 확인
      }
    });

    if (response.status === 201) {
      return response.data;
    } else {
      throw new Error(`Unexpected status code: ${response.status}`);
    }
  } catch (error) {
    console.error('Error fetching authorized page:', error);
    throw error;  // 에러 핸들링 추가
  }
};


const fetchPutMain = async (url, data) => {
  const router = useRouter();
  const route = useRoute();

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
            return response.data;
        } else {
            const reissueSuccess = await fetchReissue();
            if (reissueSuccess) {
                return await fetchPut(url, employeeData, profileImageFile);
            } else {
                alert('관리자가 아닙니다.');
                router.push({ path: '/login', query: { redirect: route.path } });
            }
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
        throw error; // 에러를 다시 던져서 호출하는 곳에서 처리 가능
    }
};

const fetchDelete = async (url) => {
    const router = useRouter();
    const route = useRoute();

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
            } else {
                alert('관리자가 아닙니다.');
                router.push({ path: '/login', query: { redirect: route.path } });
            }
        }
    } catch (error) {
        console.error('Error fetching authorized page:', error);
    }
    return null;
};

export { fetchDelete, fetchGet, fetchPost, fetchPut };
