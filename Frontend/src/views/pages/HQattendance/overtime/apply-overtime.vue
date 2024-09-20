<template>
    <div class="vacation-page">
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
                <!-- 제출 버튼 -->
                <div class="button-container">
                    <button @click="submitForm" class="submit-button">제출</button>
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
                type: "휴가", // 기본 선택 값
                startDate: "",
                endDate: "",
                comment: "", // 코멘트 필드 추가
                remainingDays: 11, // 예시 데이터
            },
        };
    },
    methods: {
        submitForm() {
            // 폼 제출 로직 처리
            console.log("제출된 폼:", this.form);
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
    border: 1px solid #ddd; /* 경계선 추가 */
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
    margin-top: 20px; /* 코멘트 입력란 간격 추가 */
}

.button-container {
    display: flex;
    justify-content: flex-end; /* 버튼을 오른쪽으로 정렬 */
}

.submit-button {
    padding: 10px 20px;
    background-color: #4caf50;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}
</style>
