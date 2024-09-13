export const AttendanceService = {
    getStudentsData() {
        return [
            {
                id: '1000',
                name: '김철수',
                class: 'Beyond 7기',
                attendanceStatus: '휴가'
            },
            {
                id: '1001',
                name: '이영희',
                class: 'Beyond 7기',
                attendanceStatus: '조퇴'
            },
            {
                id: '1002',
                name: '박민수',
                class: 'Beyond 8기',
                attendanceStatus: '병가'
            },
            {
                id: '1003',
                name: '최지우',
                class: 'Beyond 7기',
                attendanceStatus: '휴가'
            },
            {
                id: '1004',
                name: '장준혁',
                class: 'Beyond 7기',
                attendanceStatus: '병가'
            },
            {
                id: '1005',
                name: '한지민',
                class: 'Beyond 8기',
                attendanceStatus: '조퇴'
            },
            {
                id: '1006',
                name: '오민정',
                class: 'Beyond 8기',
                attendanceStatus: '휴가'
            },
            {
                id: '1007',
                name: '정재훈',
                class: 'Beyond 7기',
                attendanceStatus: '병가'
            },
            {
                id: '1008',
                name: '이수진',
                class: 'Beyond 8기',
                attendanceStatus: '휴가'
            },
            {
                id: '1009',
                name: '황준영',
                class: 'Beyond 7기',
                attendanceStatus: '조퇴'
            },
            {
                id: '1010',
                name: '고은비',
                class: 'Beyond 7기',
                attendanceStatus: '병가'
            },
            {
                id: '1011',
                name: '류승민',
                class: 'Beyond 8기',
                attendanceStatus: '휴가'
            },
            {
                id: '1012',
                name: '김나연',
                class: 'Beyond 7기',
                attendanceStatus: '조퇴'
            },
            {
                id: '1013',
                name: '정해인',
                class: 'Beyond 8기',
                attendanceStatus: '병가'
            },
            {
                id: '1014',
                name: '배수지',
                class: 'Beyond 7기',
                attendanceStatus: '휴가'
            },
            {
                id: '1015',
                name: '서강준',
                class: 'Beyond 8기',
                attendanceStatus: '조퇴'
            },
            {
                id: '1016',
                name: '김도연',
                class: 'Beyond 7기',
                attendanceStatus: '병가'
            },
            {
                id: '1017',
                name: '이지훈',
                class: 'Beyond 8기',
                attendanceStatus: '휴가'
            },
            {
                id: '1018',
                name: '윤아름',
                class: 'Beyond 7기',
                attendanceStatus: '조퇴'
            },
            {
                id: '1019',
                name: '박서준',
                class: 'Beyond 8기',
                attendanceStatus: '병가'
            }
        ];
    },

    getStudentsMini() {
        return Promise.resolve(this.getStudentsData().slice(0, 5));
    },

    getStudentsSmall() {
        return Promise.resolve(this.getStudentsData().slice(0, 10));
    },

    getStudents() {
        return Promise.resolve(this.getStudentsData());
    }
};
