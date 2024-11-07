<template>
    <div class="demo-app">
        <!-- 사이드바 열고 닫기 버튼 -->
        <Button icon="pi pi-bars" label="필터 열기" @click="toggleSidebar" class="sidebar-toggle-btn" />

        <!-- 사이드바 (필터 메뉴) -->
        <Transition name="slide">
            <div v-if="isSidebarOpen" class="sidebar">
                <h3 class="sidebar-header">필터 옵션</h3>
                <Button label="전체 보기" :class="['filter-btn', 'all-btn', { active: activeFilter === null }]" @click="setCategoryFilter(null)" style="border-color: gray" />
                <Button label="월차" :class="['filter-btn', 'day-off-btn', { active: activeFilter === 'DAY_OFF' }]" @click="setCategoryFilter('DAY_OFF')" />
                <Button label="반차" :class="['filter-btn', 'half-day-btn', { active: activeFilter === 'HALF_DAY_OFF' }]" @click="setCategoryFilter('HALF_DAY_OFF')" />
                <Button label="병가" :class="['filter-btn', 'sick-leave-btn', { active: activeFilter === 'SICK_LEAVE' }]" @click="setCategoryFilter('SICK_LEAVE')" />
                <Button label="경조" :class="['filter-btn', 'event-leave-btn', { active: activeFilter === 'EVENT_LEAVE' }]" @click="setCategoryFilter('EVENT_LEAVE')" />
                <Button :label="isPersonalView ? '전체 일정 보기' : '개인 일정만 보기'" @click="togglePersonalView" class="filter-btn personal-btn" />

                <!-- 사이드바 닫기 버튼 -->
                <Button icon="pi pi-times" label="닫기" @click="toggleSidebar" class="close-sidebar-btn" />
            </div>
        </Transition>

        <div class="demo-app-main">
            <FullCalendar ref="calendar" class="demo-app-calendar" :options="calendarOptions">
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
import Swal from 'sweetalert2';
import { ref } from 'vue';
import { fetchDelete, fetchGet, fetchPost, fetchPut } from '../../auth/service/AuthApiService';

export default {
    components: { FullCalendar },
    data() {
        return {
            activeFilter: null,
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
            this.activeFilter = category;
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
                this.$refs.calendar.getApi().refetchEvents();
            } catch (error) {
                console.error('이벤트 데이터 로드 실패:', error);
            }
        },

        // 개인 일정 및 휴가 데이터를 가져오는 메서드
        // 개인 일정 및 휴가 데이터를 가져오는 메서드
        async fetchPersonalEvents(employeeId) {
            const personalResponse = await fetchGet(`https://hq-heroes-api.com/api/v1/event/my-events?employeeId=${employeeId}`);
            return Array.isArray(personalResponse)
                ? personalResponse
                      .filter((event) => {
                          if (['월차', '반차', '병가', '경조'].includes(event.category)) {
                              return ['APPROVED', 'CANCEL', 'CANCEL_REJECTED'].includes(event.vacationStatus);
                          }
                          return true;
                      })
                      .map((event) => ({
                          id: event.id, // 이벤트 ID 추가
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
            const teamResponse = await fetchGet(`https://hq-heroes-api.com/api/v1/vacation/team-vacations?employeeId=${employeeId}`);
            if (!Array.isArray(teamResponse)) return { myVacations: [], teamVacations: [] };

            const myVacations = [];
            const teamVacations = [];

            teamResponse.forEach((vacation) => {
                if (vacation.vacationStatus === 'APPROVED' || vacation.vacationStatus === 'CANCEL' || vacation.vacationStatus === 'CANCEL_REJECTED') {
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
                // 기존 시간 유지
                const originalStart = eventInfo.oldEvent.start;
                const originalEnd = eventInfo.oldEvent.end;

                // 새로운 시작 시간에 한국 시간대 적용
                const newStart = new Date(start);
                newStart.setHours(originalStart.getHours() + 9, originalStart.getMinutes(), originalStart.getSeconds());

                const newEnd = end ? new Date(end) : null;
                if (newEnd && originalEnd) {
                    newEnd.setHours(originalEnd.getHours() + 9, originalEnd.getMinutes(), originalEnd.getSeconds());
                }

                // 서버에 업데이트 요청
                await fetchPut(`https://hq-heroes-api.com/api/v1/event/update/${id}`, {
                    start: newStart.toISOString(),
                    end: newEnd ? newEnd.toISOString() : null
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
                // 기존 시간 유지
                const originalStart = eventInfo.oldEvent.start;
                const originalEnd = eventInfo.oldEvent.end;

                // 새로운 종료 시간에 한국 시간대 적용
                const newStart = new Date(start);
                newStart.setHours(originalStart.getHours() + 9, originalStart.getMinutes(), originalStart.getSeconds());

                const newEnd = new Date(end);
                newEnd.setHours(originalEnd.getHours() + 9, originalEnd.getMinutes(), originalEnd.getSeconds());

                // 서버에 업데이트 요청
                await fetchPut(`https://hq-heroes-api.com/api/v1/event/update/${id}`, {
                    start: newStart.toISOString(),
                    end: newEnd.toISOString()
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
                fetchPost('https://hq-heroes-api.com/api/v1/event/create', {
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

        async deleteEvent(event) {
            const eventId = event.id || this.selectedEvent.id; // event 객체에서 id를 가져오거나 selectedEvent에서 가져오기

            if (eventId) {
                // 모달을 먼저 닫기
                this.isDetailModalOpen = false;

                // 삭제 확인 창 표시
                const result = await Swal.fire({
                    title: '정말 일정을 삭제하시겠습니까?',
                    text: '이 작업은 되돌릴 수 없습니다.',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: '삭제',
                    cancelButtonText: '취소'
                });

                // 사용자가 "삭제"를 확인한 경우에만 진행
                if (result.isConfirmed) {
                    try {
                        // 서버에 삭제 요청
                        await fetchDelete(`https://hq-heroes-api.com/api/v1/event/delete/${eventId}`);

                        // 성공적으로 삭제되면 이벤트를 화면에서 제거
                        event.remove();
                        this.$toast.add({ severity: 'success', summary: '성공', detail: '이벤트가 삭제되었습니다.' });
                        window.location.reload(); // 새로고침
                    } catch (error) {
                        console.error('이벤트 삭제 중 오류가 발생했습니다:', error);

                        Swal.fire({
                            icon: 'error',
                            title: '삭제 오류',
                            html: `${error.response?.data || '삭제 중 오류가 발생했습니다.'}<br>다시 시도해 주세요.`,
                            confirmButtonText: '확인'
                        });

                        this.$toast.add({ severity: 'error', summary: '오류', detail: '이벤트 삭제에 실패했습니다.' });
                    } finally {
                        // 모달 닫기
                        this.isDetailModalOpen = false;
                    }
                } else {
                    // 사용자가 삭제를 취소한 경우 모달을 다시 열기
                    this.isDetailModalOpen = true;
                }
            } else {
                console.error('이벤트 정보가 유효하지 않습니다.');
            }
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
    height: auto;
    max-height: 60%;
    overflow-y: auto;
    background-color: #f9f9f9;
    padding: 16px;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.2);
    border-radius: 10px; /* 모서리 둥글게 */
    z-index: 10;
}

.sidebar-header {
    font-size: 1.2rem;
    font-weight: bold;
    margin-bottom: 12px;
    color: #333;
}

.filter-btn {
    display: block;
    width: 100%;
    margin-bottom: 8px;
    font-weight: bold;
    color: black;
    border: none;
    padding: 10px;
    border-radius: 4px;
    background-color: white; /* 기본 색상 */
    border-color: gray;
}

/* 카테고리별 버튼 색상 */
.all-btn {
    background-color: white;
    border-color: gray;
}

.filter-btn.active.day-off-btn {
    background-color: #ffcccc;
    border-color: gray;
}

.filter-btn.active.half-day-btn {
    background-color: #ffeb99;
    border-color: gray;
}

.filter-btn.active.sick-leave-btn {
    background-color: #ccffcc;
    border-color: gray;
}

.filter-btn.active.event-leave-btn {
    background-color: #ccccff;
    border-color: gray;
}

.filter-btn.day-off-btn:hover {
    background-color: #ffe6e6; /* 살짝 연한 핑크색 */
    color: black;
    border-color: gray;
}

.filter-btn.half-day-btn:hover {
    background-color: #fff5cc; /* 살짝 연한 노란색 */
    color: black;
    border-color: gray;
}

.filter-btn.sick-leave-btn:hover {
    background-color: #e6ffe6;
    color: black; /* 살짝 연한 연두색 */
    border-color: gray;
}

.filter-btn.event-leave-btn:hover {
    background-color: #e6e6ff; /* 살짝 연한 보라색 */
    color: black;
    border-color: gray;
}

.personal-btn {
    background-color: white;
    border-color: gray;
}

.close-sidebar-btn {
    margin-top: 20px;
    width: 100%;
    color: #fff;
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
