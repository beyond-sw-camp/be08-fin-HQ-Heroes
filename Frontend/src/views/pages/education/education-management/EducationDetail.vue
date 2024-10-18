<template>
    <div class="card">
        <div class="education-detail">
            <h2>[ {{ categoryName }} ] {{ educationName }} </h2>

            <hr />

            <div class="education-info">
                <p>
                    <strong>신청 기간 :</strong>
                    <template v-if="editMode">
                        <input type="date" v-model="applicationStartDate" /> ~ 
                        <input type="date" v-model="applicationEndDate" />
                    </template>
                    <template v-else>
                        {{ formatDate(applicationStartDate) }} ~ {{ formatDate(applicationEndDate) }}
                    </template>
                </p>
                <p>
                    <strong>교육 기간 :</strong>
                    <template v-if="editMode">
                        <input type="date" v-model="educationStart" /> ~ 
                        <input type="date" v-model="educationEnd" />
                    </template>
                    <template v-else>
                        {{ formatDate(educationStart) }} ~ {{ formatDate(educationEnd) }}
                    </template>
                </p>
                <p><strong>수료 기준 :</strong> 수강일 기준 80% 이상</p>
                <p>
                    <strong>수강 정원 :</strong>
                    <template v-if="editMode">
                        <input v-model.number="participants" type="number" />
                    </template>
                    <template v-else>
                        {{ currentParticipants }} / {{ participants }}
                    </template>
                </p>
                <p><strong>강의 형식 :</strong> 오프라인</p>
                <p>
                    <strong>교육 기관 :</strong>
                    <template v-if="editMode">
                        <input v-model="institution" type="text" />
                    </template>
                    <template v-else>
                        {{ institution }}
                    </template>
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
import { fetchGet, fetchPost } from '../../auth/service/AuthApiService';

const route = useRoute();
const router = useRouter();

// 상태 정의
const educationName = ref('');
const categoryName = ref('');
const applicationStartDate = ref('');
const applicationEndDate = ref('');
const educationStart = ref('');
const educationEnd = ref('');
const participants = ref(0); // 기본값을 0으로 설정
const institution = ref(''); // institution 상태 추가
const editMode = ref(false);

// 현재 신청한 인원 수를 관리하는 상태 변수
const currentParticipants = ref(0); // 초기값은 0으로 설정

const fetchEducationDetails = async (courseId) => {
    try {
        const education = await fetchGet(`http://localhost:8080/api/v1/education-service/education/${courseId}`);
        educationName.value = education.educationName;
        categoryName.value = education.categoryName;
        applicationStartDate.value = education.applicationStartDate;
        applicationEndDate.value = education.applicationEndDate;
        educationStart.value = education.educationStart;
        educationEnd.value = education.educationEnd;
        participants.value = education.participants; // 참가자 수 할당
        institution.value = education.institution; // 교육 기관 할당
    } catch (error) {
        console.error('교육 정보를 가져오는 데 오류가 발생했습니다:', error);
    }
};

// 컴포넌트가 마운트될 때 courseId로 교육 정보를 가져옴
onMounted(() => {
    const courseId = route.params.courseId; // educationId -> courseId로 변경
    if (courseId) {
        fetchEducationDetails(courseId);
    } else {
        console.error('courseId가 정의되지 않았습니다.');
        alert("교육 ID가 정의되지 않았습니다. 올바른 링크를 사용해 주세요.");
    }
});

const handleApplyClick = async () => {
    const totalParticipants = participants.value; // 최대 수강 인원
    const newParticipantCount = currentParticipants.value + 1; // 새로 추가할 인원 수

    // 신청 인원 초과 여부 확인
    if (newParticipantCount > totalParticipants) {
        alert("신청 인원이 초과하였습니다");
        return; // 초과 시 함수 종료
    }

    // 신청할 데이터
    const data = {
        curriculumId: route.params.courseId, // curriculumId
    };

    try {
        const responseMessage = await fetchPost(`http://localhost:8080/api/v1/education-service/apply/${route.params.courseId}`, data);
        alert(responseMessage); // 신청 완료 메시지 표시
        currentParticipants.value = newParticipantCount; // 신청 인원 수 업데이트
        
        // 신청한 교육 정보를 history에 저장 후 /education-history로 리다이렉트
        router.push('/education-history');
    } catch (error) {
        console.error('신청 중 오류가 발생했습니다:', error);
        alert("신청 중 오류가 발생했습니다.");
    }
};

// '목록' 버튼 클릭 시 목록 페이지로 이동
const goBackToList = () => {
    router.push('/education-apply');
};

// 날짜 포맷팅 함수
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

.gray-button {
    background-color: #ffffff;
    border: 1px solid #7d7d7d;
    color: #000000;
}

.button-group {
    display: flex;
    justify-content: flex-end; /* 버튼을 오른쪽으로 정렬 */
    gap: 10px;
}

input {
    margin: 0 5px;
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 4px;
}
</style>
