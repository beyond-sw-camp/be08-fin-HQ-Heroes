import { defineStore } from 'pinia';

export const useNotificationStore = defineStore('notification', {
    state: () => ({
        unreadCount: 0 // 읽지 않은 알림 개수를 저장하는 상태
    }),
    actions: {
        setUnreadCount(count) {
            this.unreadCount = count; // 알림 개수 설정
        }
    }
});
