import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';
import fetchReissue from "./fetchReissue";
import { useRouter, useRoute } from 'vue-router';

const fetchAuthorizedPage = async (url) => {
  const authStore = useAuthStore();
  const router = useRouter();
  const route = useRoute();

  try {
    const response = await axios.get(url, {
      withCredentials: true,
      headers: {
        'Authorization': `Bearer ${authStore.accessToken}`,
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


export default fetchAuthorizedPage;
