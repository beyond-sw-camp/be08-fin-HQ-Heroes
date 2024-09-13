// src/stores/auth.js
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    userEmail: '',
    accessToken: '',
  }),
  actions: {
    setIsLoggedIn(status) {
      this.isLoggedIn = status;
    },
    setLoginUser(email) {
      this.userEmail = email;
    },
    setAccessToken(token) {
      this.accessToken = token;
    },
    logout() {
      this.isLoggedIn = false;
      this.userEmail = '';
      this.accessToken = '';
      window.localStorage.removeItem('access');
      window.localStorage.removeItem('email');
    },
    initializeAuth() {
      const token = window.localStorage.getItem('access');
      const email = window.localStorage.getItem('email');
      if (token && email) {
        this.accessToken = token;
        this.userEmail = email;
        this.isLoggedIn = true;
      }
    },
  },
});
