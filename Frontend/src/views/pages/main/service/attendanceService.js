import { fetchGet } from '../../auth/service/AuthApiService'; // Use the existing fetchGet from AuthApiService.js

const baseUrl = 'https://hq-heroes-api.com/api/v1/attendance'; // Replace with your actual backend URL

// 특정 employeeId의 출근 및 퇴근 시간을 가져오는 함수
export const getAttendanceTimes = async (employeeId) => {
    try {
        const url = `${baseUrl}/latest/${employeeId}`;
        const response = await fetchGet(url);
        if (response) {
            return {
                checkIn: response.checkIn,
                checkOut: response.checkOut
            };
        } else {
            console.error('No attendance data found for employee.');
            return null;
        }
    } catch (error) {
        console.error('Error fetching attendance times:', error);
        return null;
    }
};
