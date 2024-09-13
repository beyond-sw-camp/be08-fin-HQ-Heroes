import { createEventId } from './event-utils';

export const INITIAL_EVENTS = [
    {
        id: createEventId(),
        title: '휴가',
        start: '2024-09-02',
        end: '2024-09-04',
        backgroundColor: '#ffcccc',
        extendedProps: {
            category: '휴가'
        }
    },
    {
        id: createEventId(),
        title: '회의',
        start: '2024-09-05T10:00:00',
        backgroundColor: '#ccffcc',
        extendedProps: {
            category: '회의'
        }
    },
    {
        id: createEventId(),
        title: '출근',
        start: '2024-09-06T09:00:00',
        backgroundColor: '#cce6ff',
        extendedProps: {
            category: '출근'
        }
    },
    {
        id: createEventId(),
        title: '프로젝트 마감',
        start: '2024-09-07',
        backgroundColor: '#ffe6cc',
        extendedProps: {
            category: '마감'
        }
    },
    {
        id: createEventId(),
        title: '병가',
        start: '2024-09-08',
        backgroundColor: '#ffe6cc',
        extendedProps: {
            category: '병가'
        }
    },
    {
        id: createEventId(),
        title: '조퇴',
        start: '2024-09-09T14:00:00',
        backgroundColor: '#ffcccc',
        extendedProps: {
            category: '조퇴'
        }
    },
    {
        id: createEventId(),
        title: '휴가',
        start: '2024-09-10',
        end: '2024-09-12',
        backgroundColor: '#ffcccc',
        extendedProps: {
            category: '휴가'
        }
    },
    {
        id: createEventId(),
        title: '회의',
        start: '2024-09-13T15:00:00',
        backgroundColor: '#ccffcc',
        extendedProps: {
            category: '회의'
        }
    },
    {
        id: createEventId(),
        title: '출근',
        start: '2024-09-14T09:00:00',
        backgroundColor: '#cce6ff',
        extendedProps: {
            category: '출근'
        }
    },
    {
        id: createEventId(),
        title: '프로젝트 리뷰',
        start: '2024-09-15',
        backgroundColor: '#ffe6cc',
        extendedProps: {
            category: '리뷰'
        }
    }
];
