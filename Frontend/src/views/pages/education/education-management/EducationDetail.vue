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
                <p>
                    <strong>교육 커리큘럼 :</strong>
                    <div v-html="educationCurriculum" class="curriculum-content"></div>
                </p>
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
const currentParticipant = ref(0);

const isApplied = ref(false); // 신청 여부 상태

// 로컬 스토리지에서 현재 참가자 수 가져오기
const loadCurrentParticipant = () => {
    const storedCount = localStorage.getItem(`currentParticipant${route.params.courseId}`);
    if (storedCount) {
        currentParticipant.value = parseInt(storedCount, 10);
    }
};

const fetchEducationDetails = async (courseId) => {
    try {
        const education = await fetchGet(`http://localhost:8080/api/v1/education-service/education/${courseId}`);
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
        alert("신청 인원이 초과하였습니다.");
        return;
    }

    const data = { curriculumId: route.params.courseId };

    try {
        // 교육 신청 요청
        const responseText = await fetchPost(`http://localhost:8080/api/v1/education-service/apply/${route.params.courseId}/${employeeId}`, data);
        
        alert("교육이 추가되었습니다"); // 성공 메시지 표시
        currentParticipant.value = newParticipantCount; // 새로 신청한 인원 수 업데이트
        localStorage.setItem(`currentParticipant${route.params.courseId}`, currentParticipant.value); // 로컬스토리지에 저장
        isApplied.value = true; // 신청 상태 업데이트
        router.push('/education-history'); // 신청 완료 후 "/education-history" 페이지로 이동
    } catch (error) {
        // 오류 메시지 검사
        if (error.message.includes("서버 오류: 409")) {
            alert("이미 신청한 교육입니다."); // 409 오류일 경우 메시지 변경
        } else {
            alert("신청 중 오류가 발생했습니다: " + error.message); // 그 외 오류 처리
        }
    }
};

// 취소 버튼 클릭 시 호출되는 함수
const handleCancelClick = async () => {
    const employeeId = window.localStorage.getItem('employeeId'); // 로컬스토리지에서 ID 가져오기
    const courseId = route.params.courseId; // 현재 코스 ID 가져오기
    
    const confirmCancel = confirm("교육을 취소하시겠습니까?"); // 취소 확인
    
    if (confirmCancel) {
        try {
            // 서버에 DELETE 요청 보내기
            const response = await fetch(`http://localhost:8080/api/v1/course-service/cancel/${courseId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (response.ok) {
                alert("신청이 취소되었습니다."); // 취소 메시지
                currentParticipant.value--; // 참가자 수 감소
                localStorage.setItem(`currentParticipant${courseId}`, currentParticipant.value); // 로컬스토리지 업데이트
                isApplied.value = false; // 신청 상태 업데이트
            } else {
                alert("취소에 실패했습니다. 다시 시도해 주세요.");
            }
        } catch (error) {
            console.error('취소 중 오류 발생:', error);
            alert("오류가 발생했습니다. 나중에 다시 시도해 주세요.");
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
