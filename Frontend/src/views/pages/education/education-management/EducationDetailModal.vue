<template>
    <Dialog :visible="visible" modal @hide="closeModal" header="교육 상세 정보" :style="{ width: '50vw' }" :closable="false">
        <template #header>
            <div class="font-bold p-2" :style="{ fontSize: '1.5rem' }">{{ courseDetail ? courseDetail.educationName : '정보 없음' }}</div>
        </template>
        <hr />
        <div class="p-4">
            <table class="education-info">
                <tr>
                    <td class="px-4 py-2"><strong>카테고리</strong></td>
                    <td class="px-4 py-2">{{ courseDetail.categoryName }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>강사</strong></td>
                    <td class="px-4 py-2">{{ courseDetail.instructorName }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>교육 기관</strong></td>
                    <td class="px-4 py-2">{{ courseDetail.institution }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>교육 신청일</strong></td>
                    <td class="px-4 py-2">{{ formatDate(new Date(courseDetail.startDate)) }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>교육 종료일</strong></td>
                    <td class="px-4 py-2">{{ formatDate(new Date(courseDetail.endDate)) }}</td>
                </tr>
                <tr>
                    <td class="px-4 py-2"><strong>상태</strong></td>
                    <td class="px-4 py-2">{{ mapStatus(courseDetail.courseStatus) }}</td>
                </tr>
            </table>
        </div>

        <template #footer>
            <!-- courseStatus가 '이수'가 아니고 교육 시작일이 지나지 않았을 때만 취소하기 버튼을 보여줌 -->
            <Button v-if="mapStatus(courseDetail.courseStatus) !== '이수' && !isCourseStarted(courseDetail.startDate)" label="취소하기" severity="danger" class="p-button-outlined" @click="handleCancelClick" />
            <Button label="닫기" class="p-button-primary" @click="closeModal" />
        </template>
    </Dialog>
</template>

<script setup>
import Button from 'primevue/button';
import Swal from 'sweetalert2';
import { defineEmits, defineProps } from 'vue';
import { fetchDelete } from '../../auth/service/AuthApiService';

const props = defineProps({
    visible: Boolean,
    courseDetail: Object
});

const emit = defineEmits(['update:visible', 'refreshCourses']);

// 모달 닫기 함수
function closeModal() {
    emit('update:visible', false); // 부모 컴포넌트로 visible 상태 업데이트 이벤트 전송
}

// 날짜 포맷 함수
function formatDate(date) {
    const formattedDate = new Date(date);
    return `${formattedDate.getFullYear()}-${String(formattedDate.getMonth() + 1).padStart(2, '0')}-${String(formattedDate.getDate()).padStart(2, '0')}`;
}

// 상태를 텍스트로 변환하는 함수
const mapStatus = (status) => (status === 'PASS' ? '이수' : '미이수');

// 교육 시작 여부 확인 함수
const isCourseStarted = (startDate) => new Date(startDate) <= new Date();

// 취소 버튼 클릭 처리 함수
async function handleCancelClick() {
    closeModal(); // 상세 모달 창 닫기
    await confirmCancelEducation(); // 취소 확인
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
        await fetchDelete(`https://hq-heroes-api.com/api/v1/course-service/cancel/${courseId}`);

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

<style scoped>
.education-info {
    width: 100%;
    border-collapse: collapse;
}

.education-info th,
.education-info td {
    padding: 8px;
    border-bottom: 1px solid #ddd;
    text-align: left;
}
</style>
