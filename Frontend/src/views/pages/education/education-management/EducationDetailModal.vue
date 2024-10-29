<template>
    <Dialog :visible="visible" modal @hide="closeModal" header="교육 상세 정보" :style="{ width: '50vw' }" :closable="false">
        <template #header>
            <div class="font-bold text-lg">{{ courseDetail ? courseDetail.educationName : '정보 없음' }}</div>
        </template>
        <div class="p-4">
            <p><strong>카테고리:</strong> {{ courseDetail.categoryName }}</p>
            <p><strong>강사:</strong> {{ courseDetail.instructorName }}</p>
            <p><strong>교육 기관:</strong> {{ courseDetail.institution }}</p>
            <p><strong>교육 신청일:</strong> {{ formatDate(new Date(courseDetail.startDate)) }}</p>
            <p><strong>교육 종료일:</strong> {{ formatDate(new Date(courseDetail.endDate)) }}</p>
            <p><strong>상태:</strong> {{ mapStatus(courseDetail.status) }}</p>
        </div>

        <template #footer>
            <Button label="취소하기" icon="pi pi-times" class="p-button-text" @click="handleCancelClick" />
            <Button label="닫기" icon="pi pi-check" class="p-button-primary" @click="closeModal" />
        </template>
    </Dialog>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import Button from 'primevue/button';
import { fetchDelete } from '../../auth/service/AuthApiService';
import Swal from 'sweetalert2';

const props = defineProps({
    visible: Boolean,
    courseDetail: Object
});

const emit = defineEmits(['update:visible', 'refreshCourses']);

// 모달 닫기 함수
function closeModal() {
    emit('update:visible', false);  // 부모 컴포넌트로 visible 상태 업데이트 이벤트 전송
}

// 날짜 포맷 함수
const formatDate = (date) => `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;

// 상태를 텍스트로 변환하는 함수
const mapStatus = (status) => status === 'PASS' ? '이수' : '미이수';

// 취소 버튼 클릭 처리 함수
async function handleCancelClick() {
    closeModal();  // 상세 모달 창 닫기
    await confirmCancelEducation();  // 취소 확인
}

// 교육 취소 확인 및 실행 함수
async function confirmCancelEducation() {
    const result = await Swal.fire({
        title: '교육을 취소하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '예',
        cancelButtonText: '아니오'
    });

    if (result.isConfirmed) {
        await cancelEducation(); // 교육 취소 후 성공 메시지 표시
    }
}

// 교육 취소하기 함수
async function cancelEducation() {
    const courseId = props.courseDetail.courseId;
    try {
        await fetchDelete(`http://localhost:8080/api/v1/course-service/cancel/${courseId}`);
        
        await Swal.fire({
            title: '교육 취소가 완료되었습니다.',
            icon: 'success'
        });

        emit('refreshCourses'); // 부모 컴포넌트에 업데이트 요청
    } catch (error) {
        await Swal.fire({
            title: '교육 취소 실패',
            text: '교육 취소 중 오류가 발생했습니다.',
            icon: 'error'
        });
        console.error('교육 취소에 실패했습니다.', error);
    }
}

</script>
