import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    employeeId: '',
    accessToken: '',
  }),
  actions: {
    setIsLoggedIn(status) {
      this.isLoggedIn = status;
    },
    setLoginUser(employeeId) {
      this.employeeId = employeeId;
    },
    setAccessToken(token) {
      this.accessToken = token;
    },
    logout() {
      this.isLoggedIn = false;
      this.employeeId = '';
      this.accessToken = '';
      window.localStorage.removeItem('access');
      window.localStorage.removeItem('employeeId');
    },
    initializeAuth() {
      const token = window.localStorage.getItem('access');
      const employeeId = window.localStorage.getItem('employeeId');
      if (token && employeeId) {
        this.accessToken = token;
        this.employeeId = employeeId;
        this.isLoggedIn = true;
      }
    },
  },
});
