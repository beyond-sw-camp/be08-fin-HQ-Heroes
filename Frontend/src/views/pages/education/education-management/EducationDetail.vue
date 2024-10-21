<template>
    <div class="card">
        <div class="education-detail">
            <h2>[ {{ categoryName }} ] {{ educationName }} </h2>
            <hr />
            <div class="education-info">
                <p>
                    <strong>교육 기간 :</strong>
                    {{ formatDate(educationStart) }} ~ {{ formatDate(educationEnd) }}
                </p>
                <p><strong>수료 기준 :</strong> 수강일 기준 80% 이상</p>
                <p><strong>수강 정원 :</strong>
                    {{ currentParticipant }} / {{ participants }}
                </p>
                <p><strong>강의 형식 :</strong> 오프라인</p>
                <p><strong>교육 기관 :</strong> {{ institution }}</p>
                <div>
                    <strong>교육 커리큘럼 :</strong>
                    <div v-html="educationCurriculum" class="curriculum-content"></div>
                </div>
            </div>
            <div class="button-group">
                <Button label="신청하기" icon="pi pi-pencil" @click="handleApplyClick" />
                <Button label="목록" icon="pi pi-fw pi-book" @click="goBackToList" class="gray-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { fetchGet } from '../../auth/service/AuthApiService';

async function fetchPost(url, data) {
    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            throw new Error(`서버 오류: ${response.status}`);
        }

        return response;
    } catch (error) {
        console.error("요청 중 오류 발생:", error);
        throw error;
    }
}

const route = useRoute();
const router = useRouter();

const educationName = ref('');
const categoryName = ref('');
const educationStart = ref('');
const educationEnd = ref('');
const educationCurriculum = ref(''); // HTML 형식의 커리큘럼
const participants = ref(0);
const institution = ref('');
const currentParticipant = ref('');

// 로컬 스토리지에서 현재 참가자 수 가져오기
const loadCurrentParticipant = () => {
    const storedCount = localStorage.getItem(`currentParticipant${route.params.courseId}`);
    if (storedCount) {
        currentParticipant.value = parseInt(storedCount, 10);
    }
};

const currentParticipants = ref([]); // 이미 신청한 사원 ID 목록

// 교육 정보를 가져오는 함수
const fetchEducationDetails = async (courseId) => {
    try {
        const education = await fetchGet(`http://localhost:8080/api/v1/education-service/education/${courseId}`);
        educationName.value = education.educationName;
        categoryName.value = education.categoryName;
        educationCurriculum.value = education.educationCurriculum;
        participants.value = education.participants;
        currentParticipant.value = education.currentParticipant;
        currentParticipants.value = education.currentParticipants || [];
    } catch (error) {
        console.error('교육 정보를 가져오는 데 오류가 발생했습니다:', error);
    }
};

// 신청하기 클릭 핸들러
const handleApplyClick = async () => {
    const employeeId = window.localStorage.getItem('employeeId'); // 로컬스토리지에서 ID 가져오기

    const newParticipantCount = currentParticipant.value + 1;

    // 신청 인원이 초과하는지 확인
    if (newParticipantCount > participants.value) {
        alert("신청 인원이 초과하였습니다.");
        return;
    }

    const data = { curriculumId: route.params.courseId };

    try {
        // 교육 신청 요청
        const response = await fetchPost(`http://localhost:8080/api/v1/education-service/apply/${route.params.courseId}/${employeeId}`, data);

        // JSON 응답 처리
        const responseData = await response.json(); 

        // 상태 코드에 따른 처리
        if (response.ok) {
            // 성공 시
            if (responseData.message === "교육이 신청되었습니다.") {
                currentParticipant.value = newParticipantCount; // 새로 신청한 인원 수 업데이트
                currentParticipants.value.push(employeeId); // 신청한 사원 ID를 배열에 추가
                alert(responseData.message); // 성공 메시지 출력
                router.push('/education-history'); // 신청 완료 후 "/education-history" 페이지로 이동
            }
        } else if (response.status === 409) {
            // 중복 신청 시 (409 상태)
            alert(responseData.message); // "이미 신청한 교육입니다" 메시지 출력
        } else {
            // 기타 에러 처리
            alert("서버 오류: " + responseData.message);
        }

    } catch (error) {
        console.error('신청 중 오류가 발생했습니다:', error);
        alert("신청 중 오류가 발생했습니다.");
    }
};

const goBackToList = () => {
    router.push('/education-apply');
};

onMounted(() => {
    const courseId = route.params.courseId;
    if (courseId) {
        fetchEducationDetails(courseId);
        loadCurrentParticipant(); // 로컬 스토리지에서 현재 참가자 수 로드
    } else {
        console.error('courseId가 정의되지 않았습니다.');
        alert("교육 ID가 정의되지 않았습니다. 올바른 링크를 사용해 주세요.");
    }
});

function formatDate(date) {
    if (!date) return '';
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
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

.education-info p {
    font-size: 16px;
    margin: 5px 0;
}

.education-info strong {
    font-weight: bold;
}

.curriculum-content {
    margin-top: 5px;
    /* 필요에 따라 스타일 추가 */
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
}

input {
    margin: 0 5px;
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 4px;
}
</style>
