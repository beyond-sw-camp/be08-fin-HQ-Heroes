import { useAuthStore } from '@/stores/authStore';
import { fetchGet, fetchPutEmployee } from '@/views/pages/auth/service/AuthApiService';
import axios from 'axios';
import { useCookies } from 'vue3-cookies';

const API_URL = 'https://hq-heroes-api.com';
const { cookies } = useCookies(); // Initialize the cookies

// 회원가입 메서드
const register = async (employeeName, email, password, role, joinDate, birthDate, annualLeave, status, phoneNumber, roadAddress, lotAddress, detailedAddress, profileImage, deptId, teamId, positionId, jobRoleId) => {
    const formData = new FormData();
    formData.append('employeeName', employeeName);
    formData.append('password', password);
    formData.append('email', email);
    formData.append('role', role);
    formData.append('joinDate', joinDate);
    formData.append('birthDate', birthDate);
    formData.append('annualLeave', annualLeave);
    formData.append('status', status);
    formData.append('phoneNumber', phoneNumber);
    formData.append('roadAddress', roadAddress);
    formData.append('lotAddress', lotAddress);
    formData.append('detailedAddress', detailedAddress);
    formData.append('profileImage', profileImage);
    formData.append('deptId', deptId);
    formData.append('teamId', teamId);
    formData.append('positionId', positionId);
    formData.append('jobRoleId', jobRoleId);

    try {
        const response = await axios.post(`${API_URL}/join`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data' // multipart/form-data 설정
            }
        });
        console.log(response.data);
        return response.data;
    } catch (error) {
        throw new Error('회원가입 실패. 다시 시도해주세요.');
    }
};

// 관리자 로그인
const adminLogin = async (employeeId, password, authCode) => {
    const credentials = new URLSearchParams({
        username: employeeId,
        password: password,
        authCode: authCode // 관리자용 OTP 코드
    });

    try {
        const response = await axios.post(`${API_URL}/login`, credentials, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            withCredentials: true
        });

        // 로그인 성공 시 처리
        if (response.status === 200) {
            const { employeeId } = response.data;
            const accessToken = response.headers['access'];
            const authStore = useAuthStore();
            const accessTime = Date.now();

            authStore.setAccessToken(accessToken);
            authStore.setLoginUser(employeeId);
            authStore.setAccessTime(accessTime);

            window.localStorage.setItem('access', accessToken);
            window.localStorage.setItem('employeeId', employeeId);
            window.localStorage.setItem('accessTime', accessTime);

            authStore.setIsLoggedIn(true);
            console.log('관리자 로그인 성공:', authStore);
            return { success: true, employeeId, accessToken };
        } else {
            throw new Error('로그인 실패: 상태 코드가 200이 아닙니다.');
        }
    } catch (error) {
        console.error('Admin login error:', error.response?.data?.message || error.message);
        throw new Error('관리자 로그인 실패. 다시 시도해주세요.');
    }
};

// 로그인
const login = async (employeeId, password) => {
    const credentials = new URLSearchParams({
        username: employeeId,
        password: password
    });

    try {
        const response = await axios.post(`${API_URL}/login`, credentials, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            withCredentials: true
        });

        // 로그인 성공
        if (response.status === 200) {
            const { employeeId } = response.data;
            const accessToken = response.headers['access'];
            const authStore = useAuthStore();
            const accessTime = Date.now();

            authStore.setAccessToken(accessToken);
            authStore.setLoginUser(employeeId);
            authStore.setAccessTime(accessTime);

            window.localStorage.setItem('access', accessToken);
            window.localStorage.setItem('employeeId', employeeId);
            window.localStorage.setItem('accessTime', accessTime);

            authStore.setIsLoggedIn(true);
            console.log('로그인 성공:', authStore);
            return { success: true, employeeId, accessToken };
        } else {
            // 로그인 실패
            throw new Error('로그인 실패: 상태 코드가 200이 아닙니다.');
        }
    } catch (error) {
        console.error('Login error:', error);
        throw new Error('로그인 실패. 다시 시도해주세요.');
    }
};

// 로그아웃 메서드
const logout = async () => {
    const authStore = useAuthStore();
    try {
        const response = await fetch(`${API_URL}/logout`, {
            method: 'POST',
            credentials: 'include'
        });
        if (response.ok) {
            console.log('로그아웃');
            authStore.logout();
        } else {
            alert('로그아웃 실패');
        }
    } catch (error) {
        console.error('로그아웃 중 오류 발생: ', error);
    }
};

// 로그인 된 회원정보 불러오는 메서드
export const getLoginEmployeeInfo = async (employeeId) => {
    try {
        // fetchGet 메서드 사용
        const employeeData = await fetchGet(`${API_URL}/api/v1/employee/employees/${employeeId}`);

        if (employeeData) {
            return employeeData;
        } else {
            throw new Error('직원 데이터를 불러오는 데 실패했습니다.');
        }
    } catch (error) {
        console.error('직원 데이터를 가져오는 중 오류 발생:', error);
        return null;
    }
};

export const updateEmployeeInfo = async (employeeData, profileImageFile) => {
    try {
        const response = await fetchPutEmployee(`${API_URL}/api/v1/employee/update`, employeeData, profileImageFile);
        return response;
    } catch (error) {
        console.error('직원 정보 업데이트 중 오류 발생:', error);
        throw error; // 호출하는 곳에서 에러 처리 가능
    }
};

export const adminUpdateEmployeeInfo = async (employeeData, profileImageFile) => {
    try {
        const response = await fetchPutEmployee(`${API_URL}/api/v1/admin/employee/update`, employeeData, profileImageFile);
        return response;
    } catch (error) {
        console.error('직원 정보 업데이트 중 오류 발생:', error);
        throw error; // 호출하는 곳에서 에러 처리 가능
    }
};

const isTokenExpired = () => {
    const authStore = useAuthStore();
    const currentTime = Date.now();
    const thirtyMinutes = 30 * 60 * 1000;

    if (currentTime - authStore.accessTime >= thirtyMinutes) {
        return true;
    }
    return false;
};

const handleTokenExpiration = async () => {
    const authStore = useAuthStore();

    if (isTokenExpired()) {
        const reissued = await fetchReissue();
        if (!reissued) {
            authStore.logout();
            alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        }
    }
};

export default {
    register,
    login,
    adminLogin,
    logout,
    getLoginEmployeeInfo,
    updateEmployeeInfo,
    handleTokenExpiration
};
