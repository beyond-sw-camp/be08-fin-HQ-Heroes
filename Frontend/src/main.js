import '@/assets/styles.scss';
import '@/assets/tailwind.css';
import Aura from '@primevue/themes/aura';
import { createPinia } from 'pinia';
import 'primeicons/primeicons.css'; // PrimeIcons 스타일
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import ToastService from 'primevue/toastservice';
import 'quill/dist/quill.snow.css'; // Quill의 스타일 추가
import { createApp } from 'vue';
import { library } from '@fortawesome/fontawesome-svg-core'
import { faUser } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import App from './App.vue';
import router from './router';
const app = createApp(App);
const pinia = createPinia();

library.add(faUser)

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
app.component('font-awesome-icon', FontAwesomeIcon)
app.mount('#app');