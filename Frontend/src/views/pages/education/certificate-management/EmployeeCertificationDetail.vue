<template>
    <Dialog 
        :visible="visible" 
        header="자격증 상세 정보" 
        modal 
        @hide="closeModal" 
        :closable="false"
        :style="{ width: '50vw', borderRadius: '12px' }" >

        <template #header>
            <div class="font-bold text-lg">{{ certificationDetail.certificationName }}</div>
        </template>

        <div class="p-4">
            <table class="table-auto w-full border-collapse">
                <tr>
                    <td class="px-4 py-2"><strong>자격증 명:</strong></td>
                    <td class="px-4 py-2">{{ certificationDetail.certificationName }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>취득 일:</strong></td>
                    <td class="px-4 py-2">{{ formatDate(certificationDetail.acquisitionDate) }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>발급 기관:</strong></td>
                    <td class="px-4 py-2">{{ certificationDetail.institution }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>혜택:</strong></td>
                    <td class="px-4 py-2">{{ certificationDetail.benefit }}</td>
                </tr>
            </table>
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
