import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    loginUserId: '',
    accessToken: ''
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

    logout() {
      this.isLoggedIn = false;
      this.loginUserId = '';
      this.accessToken = '';
      window.localStorage.removeItem('access');
      window.localStorage.removeItem('loginUserId');
    },
    initializeAuth() {
      const token = window.localStorage.getItem('access');
      const loginUserId = window.localStorage.getItem('loginUserId');
      if (token && loginUserId) {
        this.accessToken = token;
        this.loginUserId = loginUserId;
        this.isLoggedIn = true;
      }
    },
  },
});

