import { fetchGet } from '../../auth/service/AuthApiService';

const baseUrl = 'https://hq-heroes-api.com/api/v1/notification-service';

const getReceiveNotificationsByEmployeeId = async (employeeId) => {
    try {
        const url = `${baseUrl}/notifications/receiver/${employeeId}`;
        const response = await fetchGet(url);

        // receiveDelete가 false인 알림들을 리스트로 반환
        const filteredNotifications = response.filter((notification) => notification.receiveDelete === false);

        return filteredNotifications;
    } catch (error) {
        console.error('Error fetching notifications:', error);
        return [];
    }
};

const getSendNotificationsByEmployeeId = async (employeeId) => {
    try {
        const url = `${baseUrl}/notifications/sender/${employeeId}`;
        const response = await fetchGet(url);

        // sendDelete가 false인 알림들을 리스트로 반환
        const filteredNotifications = response.filter((notification) => notification.sendDelete === false);

        return filteredNotifications;
    } catch (error) {
        console.error('Error fetching notifications:', error);
        return [];
    }
};

const getNotificationById = async (notificationId) => {
    try {
        const url = `${baseUrl}/notification/${notificationId}`;
        const response = await fetchGet(url);
        return response;
    } catch (error) {
        console.error('Error fetching notification:', error);
        return null;
    }
};

export { getNotificationById, getReceiveNotificationsByEmployeeId, getSendNotificationsByEmployeeId };
