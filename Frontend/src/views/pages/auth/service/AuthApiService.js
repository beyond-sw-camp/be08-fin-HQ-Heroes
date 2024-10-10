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

    try {
        const response = await axios.post(url, data, {
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

const fetchPut = async (url, employeeData, profileImageFile) => {
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
