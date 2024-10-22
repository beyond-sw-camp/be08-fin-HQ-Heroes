<template>
    <Dialog :visible="visible" header="자격증 상세 정보" modal @hide="closeModal" :closable="false">
        <template #header>
            <div class="font-bold text-lg">{{ certificationDetail.certificationName }}</div>
        </template>
        <div class="p-4">
            <p><strong>자격증 명:</strong> {{ certificationDetail.certificationName }}</p>
            <p><strong>접수 기간:</strong> {{ formatDate(certificationDetail.applicationStartDate) }} ~ {{ formatDate(certificationDetail.applicationEndDate) }}</p>
            <p><strong>시험 날짜:</strong> {{ formatDate(certificationDetail.examDate) }}</p>
            <p><strong>발급 기관:</strong> {{ certificationDetail.institution }}</p>
        </div>
        <template #footer>
            <Button label="닫기" icon="pi pi-times" @click="closeModal" />
        </template>
    </Dialog>
</template>

<script setup>
import { toRefs } from 'vue';

const props = defineProps({
    visible: Boolean, // 모달 가시성
    certificationDetail: Object // 전달된 자격증 정보
});

const emit = defineEmits(['update:visible']);
const { certificationDetail } = toRefs(props);

// 모달을 닫는 함수
function closeModal() {
    emit('update:visible', false); // 부모에게 모달을 닫으라는 이벤트 전송
}

// 날짜 포맷 함수
function formatDate(date) {
    const formattedDate = new Date(date);
    return `${formattedDate.getFullYear()}-${formattedDate.getMonth() + 1}-${formattedDate.getDate()}`;
}
</script>

<style scoped>
.p-dialog {
    max-width: 500px;
    width: 100%;
}
</style>
