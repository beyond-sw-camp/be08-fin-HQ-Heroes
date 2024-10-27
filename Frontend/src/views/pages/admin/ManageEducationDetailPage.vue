<template>
    <div class="card">
        <div class="education-detail">
            <h2>[ {{ categoryName }} ] {{ educationName }} </h2>

            <hr />

            <div class="education-info">
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
                        {{ participants }}
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
                <div>
                    <strong>교육 커리큘럼 :</strong>
                    <div v-html="educationCurriculum" class="curriculum-content"></div> <!-- HTML 콘텐츠 렌더링 -->
                </div>
            </div>

            <div class="button-group">
                <Button v-if="editMode" label="저장" icon="pi pi-save" @click="saveChanges" />
                <Button v-else label="수정" icon="pi pi-pencil" @click="enableEditMode" />
                <Button label="목록" icon="pi pi-fw pi-book" @click="goBackToList" class="gray-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

// 라우터에서 educationId를 가져옴
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
const educationCurriculum = ref('');
const editMode = ref(false);

// API 호출하여 교육 정보를 가져오는 함수
const fetchGet = async (url) => {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error('네트워크 응답이 좋지 않습니다.');
    }
    return response.json();
};

const fetchEducationDetails = async (educationId) => {
    try {
        const education = await fetchGet(`http://localhost:8080/api/v1/education-service/education/${educationId}`);
        educationName.value = education.educationName;
        categoryName.value = education.categoryName;
        applicationStartDate.value = education.applicationStartDate;
        applicationEndDate.value = education.applicationEndDate;
        educationStart.value = education.educationStart;
        educationEnd.value = education.educationEnd;
        participants.value = education.participants; // 참가자 수 할당
        institution.value = education.institution; // 교육 기관 할당
        educationCurriculum.value = education.educationCurriculum;
    } catch (error) {
        console.error('교육 정보를 가져오는 데 오류가 발생했습니다:', error);
    }
};

// 컴포넌트가 마운트될 때 educationId로 교육 정보를 가져옴
onMounted(() => {
    const educationId = route.params.educationId;
    if (educationId) {
        fetchEducationDetails(educationId);
    } else {
        console.error('educationId가 정의되지 않았습니다.');
    }
});

// 수정 모드를 활성화하는 함수
// function enableEditMode() {
//     router.push('/write-notice');
// }

const enableEditMode = (educationId) => {
    router.push({ name: 'education-update', params: { id: educationId } });
};

// 변경 사항을 저장하는 함수
const fetchPut = async (url, data) => {
    const response = await fetch(url, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    if (!response.ok) {
        throw new Error('네트워크 응답이 좋지 않습니다.');
    }
    return response.json();
};

const saveChanges = async () => {
    try {
        const educationId = route.params.educationId;
        const updatedEducation = {
            educationName: educationName.value,
            applicationStartDate: applicationStartDate.value,
            applicationEndDate: applicationEndDate.value,
            educationStart: educationStart.value,
            educationEnd: educationEnd.value,
            participants: participants.value, // participants를 제대로 포함
            institution: institution.value, // institution 추가
            educationCurriculum: educationCurriculum.value
        };

        // 백엔드에 PUT 요청으로 수정된 내용을 저장
        const response = await fetchPut(`http://localhost:8080/api/v1/education-service/education/${educationId}`, updatedEducation);
        
        if (response) {
            // 저장 후 수정 모드 해제
            editMode.value = false; 
            // 변경된 값을 다시 가져와서 반영
            fetchEducationDetails(educationId);
        }
    } catch (error) {
        console.error('변경 사항을 저장하는 데 오류가 발생했습니다:', error);
    }
};

// '목록' 버튼 클릭 시 목록 페이지로 이동
const goBackToList = () => {
    router.push('/manage-education');
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

h3 {
    font-size: 18px;
    font-weight: bold;
    margin-top: 20px;
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
