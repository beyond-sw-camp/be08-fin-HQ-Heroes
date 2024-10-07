<template>
    <div class="card">
        <div class="education-detail">
            <h2>{{ educationName }}</h2>
            <hr />

            <h3>커리큘럼</h3>

            <div class="education-info">
                <p><strong>신청 기간 :</strong> {{ educationStart }} ~ {{ educationApplyEnd }}</p>
                <p><strong>교육 기간 :</strong> {{ educationStart }} ~ {{ educationEnd }}</p>
                <p><strong>수료 기준 :</strong> 수강일 기준 80% 이상</p>
                <p><strong>수강 정원 :</strong> 30명</p>
                <p><strong>강의 형식 :</strong> 오프라인</p>
            </div>
            <div class="button-group">
                <Button label="신청하기" icon="pi pi-check" @click="handleApplyClick" />
                <Button label="목록" icon="pi pi-fw pi-book" @click="goBackToList" class="gray-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

// 기본값을 추가하여 제대로 출력되는지 확인합니다.
const props = defineProps({
    educationName: {
        type: String,
        default: '[어학] 언어에 어려움을 겪고 있는 사람들을 위한 프로그램'
    },
    educationStart: {
        type: String,
        default: '2024년 10월 01일'
    },
    educationApplyEnd: {
        type: String,
        default: '2024년 10월 10일'
    },
    educationEnd: {
        type: String,
        default: '2024년 11월 21일'
    }
})

const router = useRouter()

// '목록' 버튼 클릭 시 목록 페이지로 이동
const goBackToList = () => {
    router.push('/education-apply')
}

// '신청하기' 버튼 클릭 시 alert 창 띄우기
const handleApplyClick = () => {
    alert("교육 신청이 완료되었습니다")
}
</script>

<style scoped>
.education-detail {
    max-width: 800px;
    margin: 0 auto;
}

h2 {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 10px;
}

hr {
    margin: 20px 0;
}

h3 {
    font-size: 18px;
    font-weight: bold;
    margin-top: 20px;
}

.education-info p {
    font-size: 16px;
    margin: 5px 0;
}

.education-info strong {
    font-weight: bold;
}

.gray-button {
    background-color: #ffffff;
    border: 1px solid #7d7d7d; /* Border 속성 추가 */
    color: #000000;
}

/* 버튼 사이 간격 추가 */
.button-group {
    align: left;
    display: flex;
    gap: 10px; /* 버튼 사이의 간격 설정 */
}
</style>
