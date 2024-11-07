<template>
    <div class="card">
        <div class="education-detail">
            <h2>[ {{ categoryName }} ] {{ educationName }}</h2>
            <hr />
            <!-- education-info 테이블로 수정 -->
            <table class="education-info">
                <tr>
                    <th style="text-align: left">교육 일정</th>
                    <td>
                        <template v-if="editMode">
                            <input type="date" v-model="educationStart" /> ~
                            <input type="date" v-model="educationEnd" />
                        </template>
                        <template v-else> {{ formatDate(educationStart) }} ~ {{ formatDate(educationEnd) }} </template>
                    </td>
                </tr>
                <tr>
                    <th style="text-align: left">수료 기준</th>
                    <td>수강일 기준 80% 이상</td>
                </tr>
                <tr>
                    <th style="text-align: left">수강 정원</th>
                    <td>
                        <template v-if="editMode">
                            <input v-model.number="participants" type="number" />
                        </template>
                        <template v-else>
                            {{ participants }}
                        </template>
                    </td>
                </tr>
                <tr>
                    <th style="text-align: left">교육 기관</th>
                    <td>
                        <template v-if="editMode">
                            <input v-model="institution" type="text" />
                        </template>
                        <template v-else>
                            {{ institution }}
                        </template>
                    </td>
                </tr>
                <tr>
                    <th style="text-align: left; vertical-align: top">교육 커리큘럼</th>
                    <td>
                        <div v-html="educationCurriculum" class="curriculum-content"></div>
                    </td>
                </tr>
            </table>
            <div class="button-group">
                <Button label="신청하기" icon="pi pi-pencil" @click="handleApplyClick" />
                <Button label="목록" icon="pi pi-fw pi-book" @click="goBackToList" class="gray-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchGet, fetchPostThrowError } from '../../auth/service/AuthApiService';

const route = useRoute();
const router = useRouter();

const educationName = ref('');
const categoryName = ref('');
const educationStart = ref('');
const educationEnd = ref('');
const educationCurriculum = ref(''); // HTML 형식의 커리큘럼
const participants = ref(0);
const institution = ref('');
const currentParticipant = ref(0);

const isApplied = ref(false); // 신청 여부 상태

const fetchEducationDetails = async (courseId) => {
    try {
        const education = await fetchGet(`https://hq-heroes-api.com/api/v1/education-service/education/${courseId}`);
        educationName.value = education.educationName;
        categoryName.value = education.categoryName;
        educationStart.value = education.educationStart;
        educationEnd.value = education.educationEnd;
        institution.value = education.institution;
        educationCurriculum.value = education.educationCurriculum;
        participants.value = education.participants;
        currentParticipant.value = education.currentParticipant;
        isApplied.value = currentParticipant.value > 0; // 이미 신청한 경우 true로 설정
    } catch (error) {
        console.error('교육 정보를 가져오는 데 오류가 발생했습니다:', error);
    }
};

const handleApplyClick = async () => {
    const employeeId = window.localStorage.getItem('employeeId'); // 로컬스토리지에서 ID 가져오기

    const newParticipantCount = currentParticipant.value + 1;

    // 신청 인원이 초과하는지 확인
    if (newParticipantCount > participants.value) {
        await Swal.fire({
            title: '신청 인원이 초과하였습니다.',
            icon: 'warning'
        });
        return;
    }

    const data = { curriculumId: route.params.courseId };

    try {
        // 교육 신청 요청
        const result = await fetchPostThrowError(`https://hq-heroes-api.com/api/v1/education-service/apply/${route.params.courseId}/${employeeId}`, data);
        // 성공 메시지 표시

        console.log('result : ', result);
        if (result.message.includes('교육이 신청되었습니다.')) {
            await Swal.fire({
                title: '교육이 추가되었습니다',
                icon: 'success'
            });
            currentParticipant.value = newParticipantCount; // 새로 신청한 인원 수 업데이트
            isApplied.value = true; // 신청 상태 업데이트
            router.push('/education-history'); // 신청 완료 후 "/education-history" 페이지로 이동
        }
    } catch (error) {
        if (error.response.status === 409) {
            await Swal.fire({
                title: '이미 신청한 교육입니다.',
                icon: 'warning'
            });
        } else {
            await Swal.fire({
                title: '신청 중 오류가 발생했습니다.',
                text: error.message,
                icon: 'error'
            });
        }
    }
};

// 취소 버튼 클릭 시 호출되는 함수
const handleCancelClick = async () => {
    const employeeId = window.localStorage.getItem('employeeId'); // 로컬스토리지에서 ID 가져오기
    const courseId = route.params.courseId; // 현재 코스 ID 가져오기

    const confirmCancel = confirm('교육을 취소하시겠습니까?'); // 취소 확인

    if (confirmCancel) {
        try {
            // 서버에 DELETE 요청 보내기
            const response = await fetch(`https://hq-heroes-api.com/api/v1/course-service/cancel/${courseId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                alert('신청이 취소되었습니다.'); // 취소 메시지
                currentParticipant.value--; // 참가자 수 감소
                isApplied.value = false; // 신청 상태 업데이트
            } else {
                alert('취소에 실패했습니다. 다시 시도해 주세요.');
            }
        } catch (error) {
            console.error('취소 중 오류 발생:', error);
            alert('오류가 발생했습니다. 나중에 다시 시도해 주세요.');
        }
    }
};

const goBackToList = () => {
    router.push('/education-apply');
};

onMounted(() => {
    const courseId = route.params.courseId;
    if (courseId) {
        fetchEducationDetails(courseId);
    } else {
        console.error('courseId가 정의되지 않았습니다.');
        alert('교육 ID가 정의되지 않았습니다. 올바른 링크를 사용해 주세요.');
    }
});

// 날짜 포맷 함수
function formatDate(date) {
    const formattedDate = new Date(date);
    return `${formattedDate.getFullYear()}-${String(formattedDate.getMonth() + 1).padStart(2, '0')}-${String(formattedDate.getDate()).padStart(2, '0')}`;
}
</script>

<style scoped>
.education-detail {
    width: 100%; /* "card" 크기에 맞추기 위해 전체 너비로 설정 */
    margin: 0; /* 불필요한 외부 여백 제거 */
    padding: 0 5rem; /* 내부 여백만 설정하여 card 경계에 맞추기 */
}

h2 {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 10px;
}

hr {
    margin: 20px 0;
}

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

.curriculum-content {
    text-align: left;
    display: inline-block;
    max-width: 100%;
    padding: 20px 0;
}

.gray-button {
    background-color: #ffffff;
    border: 1px solid #7d7d7d;
    color: #000000;
}

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    padding: 10px 0;
}

input {
    margin: 0 5px;
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 4px;
}
</style>
