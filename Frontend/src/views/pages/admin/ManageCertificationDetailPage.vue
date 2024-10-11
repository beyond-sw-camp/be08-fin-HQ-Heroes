<template>
    <div class="card">
        <div class="certification-detail">
            <h2>[ {{ deptName }} ] {{ certificationName }} </h2>

            <hr />

            <div class="certification-info">
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
                    <strong>시험 일 :</strong>
                    <template v-if="editMode">
                        <input type="date" v-model="examDate" />
                    </template>
                    <template v-else>
                        {{ formatDate(examDate) }}
                    </template>
                </p>
                <p>
                    <strong>인증 기관 :</strong>
                    <template v-if="editMode">
                        <input v-model="institution" type="text" />
                    </template>
                    <template v-else>
                        {{ institution }}
                    </template>
                </p>
                <p>
                    <strong>혜택 :</strong>
                    <template v-if="editMode">
                        <input v-model="benefit" type="text" />
                    </template>
                    <template v-else>
                        {{ benefit }}
                    </template>
                </p>
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
import axios from 'axios';

// 라우터에서 certificationId를 가져옴
const route = useRoute();
const router = useRouter();

// 상태 정의
const certificationName = ref('');
const deptName = ref('');
const applicationStartDate = ref('');
const applicationEndDate = ref('');
const examDate = ref('');
const benefit = ref('');
const institution = ref(''); // institution 상태 추가
const editMode = ref(false);

// API 호출하여 인증 정보를 가져오는 함수
const fetchCertificationDetails = async (certificationId) => {
    try {
        const response = await axios.get(`http://localhost:8080/api/v1/certification-service/certification/${certificationId}`);
        const certification = response.data;
        deptName.value = certification.deptName;
        certificationName.value = certification.certificationName;
        applicationStartDate.value = certification.applicationStartDate;
        applicationEndDate.value = certification.applicationEndDate;
        examDate.value = certification.examDate;
        benefit.value = certification.benefit;
        institution.value = certification.institution; // 인증 기관 할당
    } catch (error) {
        console.error('인증 정보를 가져오는 데 오류가 발생했습니다:', error);
    }
};

// 컴포넌트가 마운트될 때 certificationId로 인증 정보를 가져옴
onMounted(() => {
    const certificationId = route.params.certificationId;
    if (certificationId) {
        fetchCertificationDetails(certificationId);
    } else {
        console.error('certificationId가 정의되지 않았습니다.');
    }
});

// 수정 모드를 활성화하는 함수
function enableEditMode() {
    router.push('/write-notice');
}

// '목록' 버튼 클릭 시 목록 페이지로 이동
const goBackToList = () => {
    router.push('/manage-certifications');
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
.certification-detail {
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

.certification-info p {
    font-size: 16px;
    margin: 5px 0;
}

.certification-info strong {
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
