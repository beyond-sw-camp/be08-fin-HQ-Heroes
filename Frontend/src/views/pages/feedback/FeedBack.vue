<script setup>
import { ref, defineEmits, defineProps } from 'vue';
import { Button } from 'primevue/button';  // PrimeVue 버튼 사용 예시

// 피드백 아이템 정의
const feedbackItems = ref([
    { id: 1, question: "의사소통 능력은 어떻게 평가하시나요?", rating: null, comment: "" },
    { id: 2, question: "리더십 수준은 어떻게 평가하시나요?", rating: null, comment: "" },
    // 필요에 따라 더 많은 항목 추가
]);

// 평가 선택 함수
function selectRating(itemId, rating) {
    const item = feedbackItems.value.find(item => item.id === itemId);
    item.rating = rating;
}

// 피드백 제출 함수
function submitFeedback() {
    console.log('Submitted feedback:', feedbackItems.value);
    alert('피드백이 성공적으로 제출되었습니다!');
}

// smooth scroll 함수
function smoothScroll(id) {
    document.body.click();
    document.querySelector(id).scrollIntoView({
        behavior: 'smooth'
    });
}
</script>

<template>
    <div class="bg-surface-0 dark:bg-surface-900">
        <div class="landing-wrapper overflow-hidden">
            <div class="py-6 px-6 mx-0 md:mx-12 lg:mx-20 lg:px-20 flex items-center justify-between relative lg:static">
                <a class="flex items-center" href="#">
                    <span class="text-surface-900 dark:text-surface-0 font-medium text-2xl leading-normal mr-20">다면 평가</span>
                </a>
                <Button class="lg:!hidden" text severity="secondary" rounded>
                    <i class="pi pi-bars !text-2xl"></i>
                </Button>
            </div>

            <div id="feedback-form" class="py-6 px-6 lg:px-20">
                <div class="text-center mb-6">
                    <h1 class="text-surface-900 dark:text-surface-0 font-bold text-4xl">360° 다면 평가 양식</h1>
                    <p class="text-muted-color text-2xl">1부터 5까지의 척도를 사용하여 피드백을 제공해 주세요.</p>
                </div>
                
                <div class="feedback-items">
                    <div v-for="item in feedbackItems" :key="item.id" class="feedback-item mb-6">
                        <div class="text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">{{ item.question }}</div>
                        <div class="rating-options flex justify-between max-w-md mx-auto">
                            <div v-for="score in [1, 2, 3, 4, 5]" :key="score" @click="selectRating(item.id, score)" class="rating-option cursor-pointer px-4 py-2 border rounded">
                                {{ score }}
                            </div>
                        </div>
                        <textarea v-model="item.comment" placeholder="여기에 추가 의견을 입력하세요" class="mt-4 w-full p-2 border rounded"></textarea>
                    </div>
                </div>

                <button @click="submitFeedback" class="bg-primary text-white font-bold py-2 px-4 rounded hover:bg-primary-dark transition-colors">
                    피드백 제출
                </button>
            </div>
        </div>
    </div>
</template>
