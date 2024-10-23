<template>
    <Dialog :visible="visible" modal @hide="closeModal" header="교육 상세 정보" :style="{ width: '50vw' }" :closable="false">
        <template #header>
            <div class="font-bold text-lg">{{ courseDetail ? courseDetail.educationName : '정보 없음' }}</div>
        </template>
        <div class="p-4">
            <p><strong>카테고리:</strong> {{ courseDetail.categoryName }}</p>
            <p><strong>강사:</strong> {{ courseDetail.instructorName }}</p>
            <p><strong>교육 기관:</strong> {{ courseDetail.institute }}</p>
            <p><strong>교육 신청일:</strong> {{ formatDate(new Date(courseDetail.startDate)) }}</p>
            <p><strong>교육 종료일:</strong> {{ formatDate(new Date(courseDetail.endDate)) }}</p>
            <p><strong>상태:</strong> {{ mapStatus(courseDetail.status) }}</p>
            <p><strong>교육 커리큘럼:</strong> {{ courseDetail.educationCurriculum }}</p>
        </div>

        <!-- 취소하기 및 닫기 버튼 추가 -->
        <template #footer>
            <Button label="취소하기" icon="pi pi-times" class="p-button-text" @click="confirmCancelEducation" />
            <Button label="닫기" icon="pi pi-check" class="p-button-primary" @click="closeModal" />
        </template>
    </Dialog>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import Button from 'primevue/button';  // PrimeVue의 Button 컴포넌트 임포트
import { fetchDelete } from '../../auth/service/AuthApiService';  // DELETE 요청을 위한 함수 임포트

const props = defineProps({
    visible: Boolean,
    courseDetail: Object  // 'selectedCourse' 대신 'courseDetail' 사용
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

// 교육 취소 확인 함수
function confirmCancelEducation() {
    const confirmCancel = confirm("교육을 취소하시겠습니까?"); // 확인 대화상자
    if (confirmCancel) {
        cancelEducation(); // 교육 취소
    }
}

// 교육 취소하기 함수
async function cancelEducation() {
    const courseId = props.courseDetail.courseId; // courseId 가져오기
    try {
        await fetchDelete(`http://localhost:8080/api/v1/course-service/cancel/${courseId}`); // API 호출
        alert("교육 취소가 완료되었습니다."); // 성공 메시지
        closeModal(); // 모달 닫기
        emit('refreshCourses'); // 부모 컴포넌트에 업데이트 요청
    } catch (error) {
        alert("교육 취소가 실패하였습니다."); // 실패 메시지
        console.error('교육 취소에 실패했습니다.', error);
    }
}
</script>
