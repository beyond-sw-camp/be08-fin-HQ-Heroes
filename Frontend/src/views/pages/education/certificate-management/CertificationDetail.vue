<template>
    <Dialog :visible="visible" modal @hide="closeModal" :closable="false" :style="{ width: '30vw', borderRadius: '12px' }">
        <template #header>
            <div 
                class="font-bold p-2" 
                :style="{ fontSize: '1.5rem', display: 'flex', justifyContent: 'center', alignItems: 'center' }">
                {{ certificationDetail ? certificationDetail.deptName : '정보 없음' }}
            </div>
        </template>
        <hr />
        <div class="p-4">
            <table class="certification-info">
                <tr>
                    <td class="px-4 py-2"><strong>부서:</strong></td>
                    <td class="px-4 py-2">{{ certificationDetail.deptName || 'N/A' }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>자격증 명:</strong></td>
                    <td class="px-4 py-2">{{ certificationDetail.certificationName }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>발급 기관:</strong></td>
                    <td class="px-4 py-2">{{ certificationDetail.institution }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>혜택:</strong></td>
                    <td class="px-4 py-2">{{ certificationDetail.benefit || 'N/A' }}</td>
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
</script>

<style scoped>
.p-dialog {
    max-width: 500px;
    width: 100%;
}

.tr {
    font-size: large;
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
