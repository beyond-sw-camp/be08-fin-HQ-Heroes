<template>
    <div class="demo-app">
        <div class="demo-app-main">
            <FullCalendar ref="calendar" class="demo-app-calendar" :options="calendarOptions">
                <!-- 이벤트 제목을 한글로 변환하여 표시하고 글자는 검정색 유지 -->
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
import { fetchGet } from '../../auth/service/AuthApiService'; // API 요청을 위한 유틸리티 함수

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
                events: [], // 승인된 휴가 일정이 여기에 표시됩니다.
                editable: true,
                selectable: true,
                dateClick: this.handleDateClick, // 날짜 클릭 시 이벤트 추가
                eventClick: this.handleEventClick
            },
            isModalOpen: false,
            isDetailModalOpen: false,
            selectedEvent: null,
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
                console.log(response); // 응답 데이터 확인

                if (response) {
                    this.calendarOptions = {
                        ...this.calendarOptions,
                        events: response.map((vacation) => {
                            const startDateTime = `${vacation.vacationStartDate}T${vacation.vacationStartTime}`;
                            const endDateTime = vacation.vacationEndDate && vacation.vacationEndTime ? `${vacation.vacationEndDate}T${vacation.vacationEndTime}` : null;

                            return {
                                id: vacation.vacationId,
                                title: `${this.translateVacationType(vacation.vacationType)} - ${vacation.employeeName}`,
                                start: startDateTime,
                                end: endDateTime,
                                backgroundColor: this.getEventColor(vacation.vacationType),
                                extendedProps: {
                                    category: vacation.vacationType,
                                    comment: vacation.comment,
                                    vacationStatus: vacation.vacationStatus
                                }
                            };
                        })
                    };
                    this.$refs.calendar.getApi().rerenderEvents(); // 캘린더 리렌더링
                }
            } catch (error) {
                console.error('휴가 데이터 로드 실패:', error);
            }
        },
        handleDateClick(info) {
            // 날짜 클릭 시 모달 열고, 클릭한 날짜를 기본 값으로 설정
            this.eventData.start = info.dateStr;
            this.isModalOpen = true;
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
        handleEventClick(eventInfo) {
            this.selectedEvent = eventInfo.event;
            this.isDetailModalOpen = true;
        },
        saveEvent() {
            if (this.eventData.title.trim()) {
                fetchPost('http://localhost:8080/api/v1/event/create', {
                    title: this.eventData.title,
                    start: this.eventData.start,
                    end: this.eventData.end,
                    description: this.eventData.description, // 카테고리 대신 설명 필드로 대체
                    employee: { employeeId: '현재 로그인된 사용자의 ID' } // 로그인된 사용자 정보
                })
                    .then((data) => {
                        if (data) {
                            console.log('이벤트가 성공적으로 생성되었습니다.');
                            this.isModalOpen = false;
                            this.resetEventData();
                            this.fetchApprovedVacationEvents(); // 새로 생성된 이벤트를 로드
                        }
                    })
                    .catch((error) => {
                        console.error('이벤트 생성 중 오류가 발생했습니다:', error);
                    });
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
        this.fetchApprovedVacationEvents(); // 컴포넌트 마운트 시 승인된 휴가 데이터 로드
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
    color: black !important; /* 글자색을 검정으로 고정 */
}

.fc-event {
    border-radius: 5px;
    padding: 4px;
    background-color: var(--background-color); /* 배경색 적용 */
}

.fc-time {
    font-weight: bold;
}

.fc-title {
    font-size: 14px;
    color: black !important; /* 글자 색을 검정으로 고정 */
}
</style>
