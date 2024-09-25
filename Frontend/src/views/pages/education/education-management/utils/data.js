import { createEventId } from './event-utils';

export const INITIAL_EVENTS = [
    {
        id: createEventId(),
        title: '비즈니스 특강',
        start: '2024-09-02',
        end: '2024-09-04',
        backgroundColor: '#ffcccc',
        extendedProps: {
            category: '직무'
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
        title: '휴가',
        start: '2024-09-10',
        end: '2024-09-12',
        backgroundColor: '#ffcccc',
        extendedProps: {
            category: '휴가'
        }
    }
];
