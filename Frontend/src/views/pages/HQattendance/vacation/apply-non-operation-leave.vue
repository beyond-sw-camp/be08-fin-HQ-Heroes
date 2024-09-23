<template>
    <div class="vacation-page">
        <div class="col-span-12 xl:col-span-6">
            <div class="card">
                <div class="form-container">
                    <label for="type" class="label">결재 종류:</label>
                    <select id="type" v-model="form.type" class="select">
                        <!-- <option value="휴가">휴가</option> -->
                        <option value="가족돌봄">가족돌봄</option>
                        <option value="난임 치료">난임 치료</option>
                        <option value="결혼 - 본인">결혼 - 본인</option>
                        <option value="결혼 - 자녀">결혼 - 자녀</option>
                        <option value="리프레시">리프레시</option>
                        <option value="병가">병가</option>
                        <option value="비상">비상</option>
                    </select>

                    <div class="time-section">
                        <div class="time-block">
                            <label for="startDate" class="label">시작 일시</label>
                            <input type="date" id="startDate" v-model="form.startDate" class="input" />
                        </div>
                        <div class="time-block">
                            <label for="endDate" class="label">종료 일시</label>
                            <input type="date" id="endDate" v-model="form.endDate" class="input" />
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
                    <p><strong>결재 종류:</strong> {{ data.type }}</p>
                    <p><strong>시작 일시:</strong> {{ data.startDate }}</p>
                    <p><strong>종료 일시:</strong> {{ data.endDate }}</p>
                    <p><strong>사유:</strong> {{ data.comment }}</p>
                    <p><strong>제출 일시:</strong> {{ data.submittedAt }}</p>
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
                type: "휴가",
                startDate: "",
                endDate: "",
                comment: "",
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
                type: "휴가",
                startDate: "",
                endDate: "",
                comment: "",
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

.select, .input, .textarea {
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
</style>
