import { fetchGet } from '../../auth/service/AuthApiService'; // Use the existing fetchGet from AuthApiService.js

const baseUrl = 'http://localhost:8080/api/v1/notification-service'; // Replace with your actual backend URL

// Fetch notifications for a specific employee based on their ID
export const getNotificationsByEmployeeId = async (employeeId) => {
    try {
        const url = `${baseUrl}/notifications/receiver/${employeeId}`;
        const response = await fetchGet(url); // Using fetchGet instead of axios.get
        return response;
    } catch (error) {
        console.error('Error fetching notifications:', error);
        return [];
    }
};

// Fetch a single notification by its ID
export const getNotificationById = async (notificationId) => {
    try {
        const url = `${baseUrl}/notification/${notificationId}`;
        const response = await fetchGet(url); // Using fetchGet instead of axios.get
        return response;
    } catch (error) {
        console.error('Error fetching notification:', error);
        return null;
    }
};
