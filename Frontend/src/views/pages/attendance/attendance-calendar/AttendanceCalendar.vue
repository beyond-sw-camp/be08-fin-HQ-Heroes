<template>
    <div class="demo-app">
        <div class="demo-app-main">
            <FullCalendar ref="calendar" class="demo-app-calendar" :options="calendarOptions">
                <template v-slot:eventContent="arg">
                    <b>{{ arg.timeText }}</b>
                    <i class="event-title">{{ translateVacationType(arg.event.title) }}</i>
                </template>
            </FullCalendar>
        </div>

        <!-- 이벤트 생성 모달 -->
        <Dialog v-model:visible="isModalOpen" :style="{ width: '450px' }" header="일정 생성" :modal="true">
            <div class="flex flex-col gap-6">
                <div>
                    <label for="title" class="block font-bold mb-3">일정 이름</label>
                    <InputText id="title" v-model.trim="eventData.title" required="true" autofocus :invalid="submitted && !eventData.title" fluid />
                    <small v-if="submitted && !eventData.title" class="text-red-500">일정 이름이 필요합니다.</small>
                </div>
                <div>
                    <label for="start" class="block font-bold mb-3">시작 날짜 및 시간</label>
                    <InputText type="datetime-local" id="start" v-model="eventData.start" required="true" fluid />
                </div>
                <div>
                    <label for="end" class="block font-bold mb-3">종료 날짜 및 시간</label>
                    <InputText type="datetime-local" id="end" v-model="eventData.end" fluid />
                </div>
                <div>
                    <label for="description" class="block font-bold mb-3">설명</label>
                    <InputText id="description" v-model="eventData.description" placeholder="일정 설명을 입력하세요" fluid />
                </div>
            </div>

            <template #footer>
                <Button label="닫기" icon="pi pi-times" text @click="closeModal" />
                <Button label="저장" icon="pi pi-check" @click="saveEvent" />
            </template>
        </Dialog>

        <!-- 이벤트 상세 정보 모달 -->
        <Dialog v-model:visible="isDetailModalOpen" :style="{ width: '450px' }" header="이벤트 상세 정보" :modal="true">
            <div class="flex flex-col gap-6">
                <div v-if="selectedEvent">
                    <p><strong>이름:</strong> {{ selectedEvent.title }}</p>
                    <p><strong>카테고리:</strong> {{ translateVacationType(selectedEvent.extendedProps.category) }}</p>
                    <p><strong>시작 날짜 및 시간:</strong> {{ formatDate(selectedEvent.start) }}</p>
                    <p><strong>종료 날짜 및 시간:</strong> {{ formatDate(selectedEvent.end) }}</p>
                </div>
            </div>
            <template #footer>
                <Button label="삭제" icon="pi pi-trash" severity="danger" @click="deleteEvent(selectedEvent)" />
                <Button label="닫기" icon="pi pi-times" text @click="closeDetailModal" />
            </template>
        </Dialog>
    </div>
</template>

<script>
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import FullCalendar from '@fullcalendar/vue3';
import { ref } from 'vue';
import { fetchGet, fetchPost, fetchPut } from '../../auth/service/AuthApiService'; // API 요청을 위한 유틸리티 함수

export default {
    components: { FullCalendar },
    data() {
        return {
            calendarOptions: {
                plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
                headerToolbar: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                },
                initialView: 'dayGridMonth',
                events: [],
                editable: true,
                selectable: true,
                dateClick: this.handleDateClick,
                eventClick: this.handleEventClick,
                eventDrop: this.handleEventDrop, // 일정 이동 이벤트 핸들러
                eventResize: this.handleEventResize // 일정 크기 조정 이벤트 핸들러
            },
            isModalOpen: false,
            isDetailModalOpen: false,
            selectedEvent: null,
            submitted: false,
            eventData: ref({
                title: '',
                start: '',
                end: '',
                category: ''
            }),
            categories: ref([
                { label: '휴가', value: 'DAY_OFF' },
                { label: '출근', value: 'HALF_DAY_OFF' },
                { label: '병가', value: 'SICK_LEAVE' },
                { label: '조퇴', value: 'EVENT_LEAVE' }
            ])
        };
    },
    methods: {
        async fetchApprovedVacationEvents() {
            try {
                const response = await fetchGet('http://localhost:8080/api/v1/vacation/my-vacations');
                if (response) {
                    const vacationEvents = response.map((vacation) => ({
                        id: vacation.vacationId,
                        title: `${this.translateVacationType(vacation.vacationType)} - ${vacation.employeeName}`,
                        start: `${vacation.vacationStartDate}T${vacation.vacationStartTime}`,
                        end: vacation.vacationEndDate ? `${vacation.vacationEndDate}T${vacation.vacationEndTime}` : null,
                        backgroundColor: this.getEventColor(vacation.vacationType),
                        extendedProps: {
                            category: vacation.vacationType,
                            comment: vacation.comment,
                            vacationStatus: vacation.vacationStatus
                        }
                    }));
                    this.calendarOptions.events = [...vacationEvents];
                    this.$refs.calendar.getApi().rerenderEvents();
                }
            } catch (error) {
                console.error('휴가 데이터 로드 실패:', error);
            }
        },

        async fetchPersonalEvents() {
            try {
                const employeeId = window.localStorage.getItem('employeeId');
                const response = await fetchGet(`http://localhost:8080/api/v1/event/my-events?employeeId=${employeeId}`);
                console.log('개인 일정 데이터:', response);

                if (Array.isArray(response)) {
                    const personalEvents = response.map((event) => ({
                        id: event.eventId || Math.random().toString(),
                        title: event.title || '제목 없음',
                        start: event.start,
                        end: event.end || null,
                        backgroundColor: this.getEventColor(event.category) || '#cccccc',
                        extendedProps: {
                            category: event.category || '기타',
                            comment: event.description || '설명 없음',
                            employeeName: event.employeeName || '직원 없음',
                            employeeId: event.employeeId || 'ID 없음'
                        }
                    }));

                    this.calendarOptions.events = [...(this.calendarOptions.events || []), ...personalEvents];
                    this.calendarOptions = { ...this.calendarOptions };
                } else {
                    console.error('응답이 배열 형식이 아닙니다:', response);
                }
            } catch (error) {
                console.error('개인 일정 데이터 로드 실패:', error);
            }
        },

        handleDateClick(info) {
            this.eventData.start = info.dateStr + 'T00:00';
            this.eventData.end = info.dateStr + 'T23:59';
            this.isModalOpen = true;
            this.submitted = false;
        },

        handleEventClick(eventInfo) {
            this.selectedEvent = eventInfo.event;
            this.isDetailModalOpen = true;
        },

        async handleEventDrop(eventInfo) {
            const { id, start, end } = eventInfo.event;

            try {
                await fetchPut(`http://localhost:8080/api/v1/event/update/${id}`, {
                    start: start.toISOString(),
                    end: end ? end.toISOString() : null
                });
                console.log('일정이 성공적으로 업데이트되었습니다.');
            } catch (error) {
                console.error('일정 이동 중 오류가 발생했습니다:', error);
                eventInfo.revert(); // 에러 발생 시 이동을 되돌림
            }
        },

        async handleEventResize(eventInfo) {
            const { id, start, end } = eventInfo.event;

            try {
                await fetchPut(`http://localhost:8080/api/v1/event/update/${id}`, {
                    start: start.toISOString(),
                    end: end.toISOString()
                });
                console.log('일정 크기 조정이 성공적으로 업데이트되었습니다.');
            } catch (error) {
                console.error('일정 크기 조정 중 오류가 발생했습니다:', error);
                eventInfo.revert(); // 에러 발생 시 크기 조정을 되돌림
            }
        },

        saveEvent() {
            this.submitted = true;
            if (this.eventData.title.trim()) {
                const employeeId = window.localStorage.getItem('employeeId');
                fetchPost('http://localhost:8080/api/v1/event/create', {
                    title: this.eventData.title,
                    start: this.eventData.start,
                    end: this.eventData.end,
                    description: this.eventData.description,
                    employeeId
                })
                    .then((data) => {
                        if (data) {
                            this.isModalOpen = false;
                            this.resetEventData();
                            this.fetchApprovedVacationEvents();
                            this.fetchPersonalEvents();
                        }
                    })
                    .catch((error) => {
                        console.error('이벤트 생성 중 오류가 발생했습니다:', error);
                    });
            } else {
                alert('일정 이름을 입력하세요.');
            }
        },

        deleteEvent(event) {
            if (event) {
                event.remove();
            }
            this.isDetailModalOpen = false;
        },

        closeModal() {
            this.isModalOpen = false;
            this.resetEventData();
        },

        closeDetailModal() {
            this.isDetailModalOpen = false;
        },

        translateVacationType(vacationType) {
            switch (vacationType) {
                case 'DAY_OFF':
                    return '월차';
                case 'HALF_DAY_OFF':
                    return '반차';
                case 'SICK_LEAVE':
                    return '병가';
                case 'EVENT_LEAVE':
                    return '경조';
                default:
                    return vacationType;
            }
        },

        getEventColor(vacationType) {
            switch (vacationType) {
                case 'DAY_OFF':
                    return '#ffcccc';
                case 'HALF_DAY_OFF':
                    return '#ffeb99';
                case 'SICK_LEAVE':
                    return '#ccffcc';
                case 'EVENT_LEAVE':
                    return '#ccccff';
                default:
                    return '#cccccc';
            }
        },

        formatDate(date) {
            return new Date(date).toLocaleString('ko-KR', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        },

        resetEventData() {
            this.eventData.title = '';
            this.eventData.start = '';
            this.eventData.end = '';
            this.eventData.category = '';
        }
    },

    mounted() {
        this.fetchApprovedVacationEvents();
        this.fetchPersonalEvents();
    }
};
</script>

<style scoped>
.demo-app {
    padding: 16px;
    border-radius: 12px;
    background-color: #ffffff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.event-title {
    color: black !important;
}

.fc-event {
    border-radius: 5px;
    padding: 4px;
    background-color: var(--background-color);
}

.fc-time {
    font-weight: bold;
}

.fc-title {
    font-size: 14px;
    color: black !important;
}
</style>
