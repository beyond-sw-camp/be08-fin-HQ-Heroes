import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';

import Aura from '@primevue/themes/aura';
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import ToastService from 'primevue/toastservice';

import '@/assets/styles.scss';
import '@/assets/tailwind.css';

const app = createApp(App);
const pinia = createPinia();

// Indigo 색상을 직접적으로 설정
const indigoPalette = {
    50: '#EEF2FF',
    100: '#E0E7FF',
    200: '#C7D2FE',
    300: '#A5B4FC',
    400: '#818CF8',
    500: '#6366F1',  // Main Indigo 색상
    600: '#4F46E5',
    700: '#4338CA',
    800: '#3730A3',
    900: '#312E81',
};

// 기존 Aura 테마를 복사한 뒤 primary 색상을 indigo로 변경
const customTheme = {
    ...Aura,
    semantic: {
        ...Aura.semantic,
        primary: indigoPalette
    }
};

app.use(router);
app.use(PrimeVue, {
  theme: {
    preset: customTheme, // 변경된 테마 적용
    options: {
      darkModeSelector: '.app-dark'
    }
  }
});
app.use(ToastService);
app.use(ConfirmationService);
app.use(pinia);

app.mount('#app');
