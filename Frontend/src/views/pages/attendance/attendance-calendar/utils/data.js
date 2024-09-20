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
        title: '병가',
        start: '2024-09-08',
        backgroundColor: '#ffe6cc',
        extendedProps: {
            category: '병가'
        }
    },
    {
        id: createEventId(),
        title: '반차',
        start: '2024-09-09T14:00:00',
        backgroundColor: '#ffcccc',
        extendedProps: {
            category: '반차'
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
