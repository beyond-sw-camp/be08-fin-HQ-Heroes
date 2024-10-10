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
  const router = useRouter();
  const route = useRoute();

  try {
    const response = await axios.post(url, data, {
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
        return await fetchPost(url, data);
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