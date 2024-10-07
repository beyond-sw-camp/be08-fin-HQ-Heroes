import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        isLoggedIn: false,
        loginUserId: '',
        accessToken: '',
        employeeData: ref({
            employeeId: '',
            employeeName: '',
            teamName: '',
            deptName: '',
            jobName: '',
            positionName: '',
            joinDate: '',
            birthDate: '',
            phoneNumber: '',
            roadAddress: '',
            lotAddress: '',
            detailedAddress: '',
            profileImageUrl: ''
        })
    }),
    actions: {
        setIsLoggedIn(status) {
            this.isLoggedIn = status;
        },
        setLoginUser(loginUserId) {
            this.loginUserId = loginUserId;
        },
        setAccessToken(token) {
            this.accessToken = token;
        },
        setEmployeeData(employeeData){
          this.employeeData = employeeData;
        },
        logout() {
            this.isLoggedIn = false;
            this.loginUserId = '';
            this.accessToken = '';
            this.employeeData = null;
            window.localStorage.removeItem('access');
            window.localStorage.removeItem('employeeId'); // 'loginUserId'에서 'employeeId'로 수정
        },
        initializeAuth() {
            const token = window.localStorage.getItem('access');
            const loginUserId = window.localStorage.getItem('employeeId'); // 'loginUserId'에서 'employeeId'로 수정
            if (token && loginUserId) {
                this.accessToken = token;
                this.loginUserId = loginUserId;
                this.isLoggedIn = true;
            }
        }
    }
});
