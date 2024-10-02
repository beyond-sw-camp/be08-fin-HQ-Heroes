<template>
    <div class="vacation-page">
        <h2 class="title">휴가 신청</h2>
        <div class="col-span-12 xl:col-span-6">
            <div class="card">
                <div class="form-container">
                    <label for="type" class="label">휴가 종류:</label>
                    <select id="type" v-model="form.vacationType" class="select">
                        <option value="ANNUAL_LEAVE">연차</option>
                        <option value="MORNING_HALF_DAY">오전 반차</option>
                        <option value="AFTERNOON_HALF_DAY">오후 반차</option>
                        <option value="SICK_LEAVE">병가</option>
                        <option value="EVENT_LEAVE">경조</option>
                    </select>

                    <div class="time-section">
                        <div class="time-block">
                            <label for="startDate" class="label">시작 날짜</label>
                            <input type="date" id="startDate" v-model="form.vacationStartDate" class="input" />
                            <label for="startTime" class="label">시작 시간</label>
                            <input type="time" id="startTime" v-model="form.vacationStartTime" class="input" />
                        </div>
                        <div class="time-block">
                            <label for="endDate" class="label">종료 날짜</label>
                            <input type="date" id="endDate" v-model="form.vacationEndDate" class="input" />
                            <label for="endTime" class="label">종료 시간</label>
                            <input type="time" id="endTime" v-model="form.vacationEndTime" class="input" />
                        </div>
                    </div>

                    <div class="name-section">
                        <div class="name-block">
                            <label for="applicant" class="label">신청인</label>
                            <input type="text" id="applicant" v-model="form.employeeId" class="input" placeholder="신청인 ID" />
                        </div>
                        <div class="name-block">
                            <label for="approver" class="label">결재자</label>
                            <input type="text" id="approver" v-model="form.approverId" class="input" placeholder="결재자 ID" />
                        </div>
                    </div>
                </div>
                <div class="comment-section">
                    <label for="comment" class="label">사유:</label>
                    <textarea id="comment" v-model="form.comment" class="textarea" rows="4" placeholder="사유를 작성하세요."></textarea>
                </div>
                <div class="button-container">
                    <button @click="submitForm" class="submit-button">제출</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            form: {
                vacationType: 'ANNUAL_LEAVE',
                vacationStartDate: '', // 시작 날짜
                vacationStartTime: '', // 시작 시간
                vacationEndDate: '', // 종료 날짜
                vacationEndTime: '', // 종료 시간
                employeeId: '',
                approverId: '',
                comment: ''
            }
        };
    },
    methods: {
        async submitForm() {
            try {
                // 날짜와 시간을 조합하여 LocalDateTime 형식으로 변환
                const vacationStart = this.form.vacationStartDate + 'T' + this.form.vacationStartTime;
                const vacationEnd = this.form.vacationEndDate + 'T' + this.form.vacationEndTime;

                const requestBody = {
                    vacationType: this.form.vacationType,
                    vacationStart: vacationStart,
                    vacationEnd: vacationEnd,
                    employeeId: this.form.employeeId,
                    approverId: this.form.approverId,
                    comment: this.form.comment
                };

                const response = await axios.post('http://localhost:8080/api/v1/vacation/submit', requestBody);
                console.log('휴가 신청이 완료되었습니다:', response.data);

                alert('휴가 신청이 완료되었습니다.');
                this.resetForm();
            } catch (error) {
                console.error('휴가 신청 중 오류가 발생했습니다:', error);
                alert('휴가 신청 중 오류가 발생했습니다. 다시 시도해주세요.');
            }
        },
        resetForm() {
            this.form = {
                vacationType: 'ANNUAL_LEAVE',
                vacationStartDate: '',
                vacationStartTime: '',
                vacationEndDate: '',
                vacationEndTime: '',
                employeeId: '',
                approverId: '',
                comment: ''
            };
        }
    }
};
</script>

<style scoped>
/* 스타일 그대로 유지 */
.vacation-page {
    padding: 20px 40px;
    width: 100%;
    max-width: 100%;
    margin: 0 auto;
    background-color: #ffffff;
    border-radius: 10px;
}

.title {
    font-size: 24px;
    font-weight: bold;
}

.form-container {
    border: 1px solid #ddd;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
}

.label {
    display: block;
    margin-bottom: 10px;
    font-weight: bold;
}

.select,
.input,
.textarea {
    width: 100%;
    padding: 10px;
    margin-bottom: 20px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.time-section {
    display: flex;
    justify-content: space-between;
    gap: 20px;
}

.time-block {
    flex: 1;
}

.name-section {
    display: flex;
    justify-content: space-between;
    gap: 20px;
}

.name-block {
    flex: 1;
}

.comment-section {
    margin-top: 20px;
}

.button-container {
    display: flex;
    justify-content: flex-end;
}

.submit-button {
    background-color: #6366f1;
    color: white;
    border: none;
    border-radius: 5px;
    padding: 10px 15px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.submit-button:hover {
    background-color: #4f46e5;
}
</style>
