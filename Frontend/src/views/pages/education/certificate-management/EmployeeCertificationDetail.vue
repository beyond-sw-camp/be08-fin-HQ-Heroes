<template>
    <Dialog 
        :visible="visible" 
        header="자격증 상세 정보" 
        modal 
        @hide="closeModal" 
        :closable="false"
        :style="{ width: '30vw', borderRadius: '12px' }" >

        <template #header>
            <div class="font-bold p-2" :style="{ fontSize: '1.5rem' }">{{ certificationDetail.certificationName }}</div>
        </template>
        <hr />
        <div class="p-4">
            <table class="certification-info">
                <tr>
                    <td class="px-4 py-2"><strong>자격증 명:</strong></td>
                    <td class="px-4 py-2 text-center">{{ certificationDetail.certificationName }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>발급 기관:</strong></td>
                    <td class="px-4 py-2 text-center">{{ certificationDetail.institution }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>취득 일:</strong></td>
                    <td class="px-4 py-2 text-center">{{ formatDate(certificationDetail.acquisitionDate) }}</td>
                </tr>
            </table>
        </div>

        <template #footer>
            <Button label="닫기" @click="closeModal" />
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
    return `${formattedDate.getFullYear()}-${String(formattedDate.getMonth() + 1).padStart(2, '0')}-${String(formattedDate.getDate()).padStart(2, '0')}`;
}

</script>

<style scoped>
.p-dialog {
    max-width: 500px;
    width: 100%;
}

.certification-info {
    width: 100%;
    border-collapse: collapse;
}

.certification-info th,
.certification-info td {
    padding: 8px;
    border-bottom: 1px solid #ddd;
    text-align: left;
}

</style>
