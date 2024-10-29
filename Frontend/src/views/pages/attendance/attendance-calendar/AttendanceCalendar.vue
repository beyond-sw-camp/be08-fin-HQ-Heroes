<template>
    <div class="demo-app">
        <!-- 사이드바 열고 닫기 버튼 -->
        <Button icon="pi pi-bars" label="필터 열기" @click="toggleSidebar" class="sidebar-toggle-btn" />

        <!-- 사이드바 (필터 메뉴) -->
        <Transition name="slide">
            <div v-if="isSidebarOpen" class="sidebar">
                <Button label="전체 보기" @click="setCategoryFilter(null)" class="filter-btn" />
                <Button label="월차" @click="setCategoryFilter('DAY_OFF')" class="filter-btn" />
                <Button label="반차" @click="setCategoryFilter('HALF_DAY_OFF')" class="filter-btn" />
                <Button label="병가" @click="setCategoryFilter('SICK_LEAVE')" class="filter-btn" />
                <Button label="경조" @click="setCategoryFilter('EVENT_LEAVE')" class="filter-btn" />
                <Button label="개인 일정만 보기" @click="togglePersonalView" class="filter-btn" />

                <!-- 사이드바 닫기 버튼 -->
                <Button icon="pi pi-times" label="닫기" @click="toggleSidebar" class="close-sidebar-btn" />
            </div>
        </Transition>

        <div class="demo-app-main">
            <FullCalendar ref="calendar" class="demo-app-calendar" :options="calendarOptions">
                <!-- 이벤트 콘텐츠 슬롯을 이용하여 시간과 제목 표시 -->
                <template v-slot:eventContent="arg">
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
                <Button label="저장" icon="pi pi-check" @click="saveEvent" />
                <Button label="닫기" icon="pi pi-times" text @click="closeModal" />
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
import { fetchGet, fetchPost, fetchPut } from '../../auth/service/AuthApiService';

export default {
    components: { FullCalendar },
    data() {
        return {
            isSidebarOpen: false, // 사이드바 열고 닫기 상태
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
                eventDrop: this.handleEventDrop,
                eventResize: this.handleEventResize,
                dayMaxEventRows: true,
                dayMaxEvents: 3,
                aspectRatio: 1.5,
                eventDidMount: (info) => {
                    info.el.style.backgroundColor = this.getEventColor(info.event.extendedProps.category);
                }
            },
            isModalOpen: false,
            isDetailModalOpen: false,
            selectedEvent: null,
            submitted: false,
            isPersonalView: false,
            categoryFilter: null,
            eventData: ref({
                title: '',
                start: '',
                end: '',
                category: ''
            })
        };
    },
    methods: {
        // 사이드바 열고 닫기 메서드
        toggleSidebar() {
            this.isSidebarOpen = !this.isSidebarOpen;
        },

        // 카테고리 필터를 설정하는 메서드
        setCategoryFilter(category) {
            this.categoryFilter = category;
            this.fetchAllEvents();
        },

        // 개인 일정 필터링 버튼을 클릭할 때 호출되는 메서드
        togglePersonalView() {
            this.isPersonalView = !this.isPersonalView;
            this.fetchAllEvents();
        },

        // 로그인한 사용자의 개인 일정 및 팀원들의 휴가 정보를 함께 가져와서 캘린더에 표시
        async fetchAllEvents() {
            try {
                const employeeId = window.localStorage.getItem('employeeId');

                // 개인 일정 및 휴가 가져오기
                const personalEvents = await this.fetchPersonalEvents(employeeId);

                // 모든 팀 휴가 가져오기 (API 한 번 호출)
                const { myVacations, teamVacations } = await this.fetchTeamAndPersonalVacations(employeeId);

                // 개인 일정과 본인 휴가를 합침
                let allEvents = [...personalEvents, ...myVacations];

                // 개인 일정만 보기 모드인지 여부에 따라 표시할 이벤트 결정
                if (!this.isPersonalView) {
                    allEvents = [...allEvents, ...teamVacations];
                }

                // 카테고리 필터가 설정되어 있다면 해당 카테고리에 맞는 이벤트만 필터링
                if (this.categoryFilter) {
                    allEvents = allEvents.filter((event) => event.extendedProps.category === this.categoryFilter);
                }

                // 캘린더 이벤트를 갱신
                this.calendarOptions.events = allEvents;
                this.$refs.calendar.getApi().rerenderEvents();
            } catch (error) {
                console.error('이벤트 데이터 로드 실패:', error);
            }
        },

        // 개인 일정 및 휴가 데이터를 가져오는 메서드
        async fetchPersonalEvents(employeeId) {
            const personalResponse = await fetchGet(`http://localhost:8080/api/v1/event/my-events?employeeId=${employeeId}`);
            return Array.isArray(personalResponse)
                ? personalResponse
                      .filter((event) => {
                          if (['월차', '반차', '병가', '경조'].includes(event.category)) {
                              return event.vacationStatus === 'APPROVED';
                          }
                          return true;
                      })
                      .map((event) => ({
                          id: event.eventId || Math.random().toString(),
                          title: `${event.title || '제목 없음'} - ${event.employeeName}`,
                          start: event.start,
                          end: event.end || null,
                          backgroundColor: this.getEventColor(event.category) || '#cccccc',
                          extendedProps: {
                              category: event.category || '기타',
                              comment: event.description || '설명 없음',
                              employeeName: event.employeeName || '직원 없음'
                          }
                      }))
                : [];
        },

        // 나의 휴가와 팀원의 휴가를 분리하여 가져오는 메서드
        async fetchTeamAndPersonalVacations(employeeId) {
            const teamResponse = await fetchGet(`http://localhost:8080/api/v1/vacation/team-vacations?employeeId=${employeeId}`);
            if (!Array.isArray(teamResponse)) return { myVacations: [], teamVacations: [] };

            const myVacations = [];
            const teamVacations = [];

            teamResponse.forEach((vacation) => {
                if (vacation.vacationStatus === 'APPROVED') {
                    const vacationEvent = {
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
                    };

                    // 본인의 휴가와 팀원의 휴가를 분리
                    if (vacation.employeeId === employeeId) {
                        myVacations.push(vacationEvent);
                    } else {
                        teamVacations.push(vacationEvent);
                    }
                }
            });

            return { myVacations, teamVacations };
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
                eventInfo.revert();
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
                eventInfo.revert();
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
                            this.fetchAllEvents();
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

        getEventColor(category) {
            switch (category) {
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
        this.fetchAllEvents();
    }
};
</script>

<style scoped>
.demo-app {
    position: relative;
    padding: 16px;
    border-radius: 12px;
    background-color: #ffffff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100vh;
    box-sizing: border-box;
}

.demo-app-main {
    flex: 1;
    overflow: hidden;
}

.demo-app-calendar {
    width: 100%;
    height: 100%;
}

.sidebar-toggle-btn {
    position: absolute;
    top: 19px;
    left: 210px;
}

.sidebar {
    position: absolute;
    top: 0;
    left: 0;
    width: 200px;
    height: 100%;
    background-color: #f4f4f4;
    padding: 16px;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.2);
    z-index: 10;
}

.filter-btn {
    display: block;
    width: 100%;
    margin-bottom: 8px;
}

.close-sidebar-btn {
    margin-top: 20px;
    width: 100%;
}

.slide-enter-active,
.slide-leave-active {
    transition: transform 0.3s ease;
}
.slide-enter {
    transform: translateX(-100%);
}
.slide-leave-to {
    transform: translateX(-100%);
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
