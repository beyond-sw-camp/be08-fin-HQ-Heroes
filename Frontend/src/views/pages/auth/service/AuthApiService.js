import axios from 'axios';
import fetchReissue from "./fetchReissue";
import { useRouter, useRoute } from 'vue-router';

const fetchGet = async (url) => {
  const router = useRouter();
  const route = useRoute();

  try {
    const response = await axios.get(url, {
      withCredentials: true,
      headers: {
        'access': window.localStorage.getItem('access'),
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


const fetchPut = async (url, data) => {
  const router = useRouter();
  const route = useRoute();

  try {
    const response = await axios.put(url, data, {
      withCredentials: true,
      headers: {
        'access': window.localStorage.getItem('access'),
      }
    });

    if (response.status === 200) {
      return response.data;
    } else {
      const reissueSuccess = await fetchReissue();
      if (reissueSuccess) {
        return await fetchPut(url, data);
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

const fetchDelete = async (url) => {
  const router = useRouter();
  const route = useRoute();

  try {
    const response = await axios.delete(url, {
      withCredentials: true,
      headers: {
        'access': window.localStorage.getItem('access'),
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

export { fetchGet, fetchPost, fetchPut, fetchDelete };