import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    loginUserId: '',
    accessToken: '',
    accessTime: 0,
    employeeData: {
      employeeId: '',
      employeeName: '',
      teamName: '',
      deptName: '',
      jobRoleName: '',
      positionName: '',
      email: '',
      joinDate: '',
      birthDate: '',
      phoneNumber: '',
      roadAddress: '',
      lotAddress: '',
      detailedAddress: '',
      profileImageUrl: ''
    }
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
    setAccessTime(time) {
      this.accessTime = time;
    },
    setEmployeeData(employeeData) {
      this.employeeData = employeeData;
    },
    logout() {
      this.isLoggedIn = false;
      this.loginUserId = '';
      this.accessToken = '';
      this.accessTime = 0;
      window.localStorage.removeItem('access');
      window.localStorage.removeItem('accessTime');
      window.localStorage.removeItem('employeeId');
    },
    initializeAuth() {
      const token = window.localStorage.getItem('access');
      const loginUserId = window.localStorage.getItem('employeeId');
      const accessTime = window.localStorage.getItem('accessTime');
      if (token && loginUserId && accessTime) {
        this.accessToken = token;
        this.loginUserId = loginUserId;
        this.accessTime = accessTime;
        this.isLoggedIn = true;
      }
    }
  }
});
