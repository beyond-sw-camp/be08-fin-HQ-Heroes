<template>
    <div class="vacation-page">
        <h2 class="title">연장 근무 신청</h2> <!-- 제목 추가 -->
        <div class="col-span-12 xl:col-span-6">
            <div class="card">
                <div class="form-container">
                    <div class="time-section">
                        <div class="time-block">
                            <label for="startDate" class="label">시작 일시</label>
                            <input type="datetime-local" id="startDate" v-model="form.startDate" class="input" />
                        </div>
                        <div class="time-block">
                            <label for="endDate" class="label">종료 일시</label>
                            <input type="datetime-local" id="endDate" v-model="form.endDate" class="input" />
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

        <div v-if="submittedData.length > 0" class="submitted-result">
            <div v-for="(data, index) in submittedData" :key="index" class="result-block">
                <div class="result-card">
                    <p><strong>시작 일시:</strong> {{ data.startDate }}</p>
                    <p><strong>종료 일시:</strong> {{ data.endDate }}</p>
                    <p><strong>사유:</strong> {{ data.comment }}</p>
                    <p class="submitted-at"><strong>제출 일시:</strong> {{ data.submittedAt }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            form: {
                startDate: "",
                endDate: "",
                comment: "",
                remainingDays: 11,
            },
            submittedData: [],
        };
    },
    methods: {
        submitForm() {
            const submittedAt = new Date().toLocaleString(); // 현재 날짜와 시간

            // 제출된 데이터를 배열의 앞에 추가
            this.submittedData.unshift({
                ...this.form,
                submittedAt, // 제출된 시간 추가
            });

            // 폼을 초기화
            this.resetForm();
        },
        resetForm() {
            this.form = {
                startDate: "",
                endDate: "",
                comment: "",
                remainingDays: 11,
            };
        },
    },
};
</script>

<style scoped>
.vacation-page {
    padding: 20px 40px;
    width: 100%;
    max-width: 100%;
    margin: 0 auto;
    background-color: #ffffff;
    border-radius: 10px;
}

.title {
    font-size: 24px; /* 제목 크기 */
    font-weight: bold; /* 제목 두께 */
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

.input, .textarea {
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

.comment-section {
    margin-top: 20px;
}

.button-container {
    display: flex;
    justify-content: flex-end;
}

.submit-button {
    padding: 10px 20px;
    background-color: #4caf50;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.submitted-result {
    margin-top: 30px;
}

.result-block {
    margin-bottom: 20px;
}

.result-card {
    padding: 20px;
    border: 1px solid #ddd;
    background-color: #f1f8f1;
    border-radius: 8px;
    box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
    margin-bottom: 10px;
}

.submit-button {
    background-color: #6366F1; /* 기본 배경색 */
    color: white; /* 글씨 색상 */
    border: none; /* 테두리 없애기 */
    border-radius: 5px; /* 둥근 모서리 */
    padding: 10px 15px; /* 패딩 */
    cursor: pointer; /* 포인터 커서 */
    transition: background-color 0.3s ease; /* 부드러운 전환 효과 */
}

.submit-button:hover {
    background-color: #4f46e5; /* 호버 시 배경색 변경 */
}

</style>
