<template>
    <div class="card">
        <div class="education-detail">
            <Button label="목록 >" icon="pi pi-bars" @click="goBackToList" text />
            <div class="text-3xl font-bold">
                <span>[{{ categoryName }}]  {{ educationName }} </span>
            </div>

            <hr />

            <!-- education-info 테이블로 수정 -->
            <table class="education-info text-lg">
                <tr>
                    <th style="text-align: left;">교육 기간</th>
                    <td>
                        <template v-if="editMode">
                            <input type="date" v-model="educationStart" /> ~ 
                            <input type="date" v-model="educationEnd" />
                        </template>
                        <template v-else>
                            {{ formatDate(educationStart) }} ~ {{ formatDate(educationEnd) }}
                        </template>
                    </td>
                </tr>
                <tr>
                    <th style="text-align: left;">수료 기준</th>
                    <td>수강일 기준 80% 이상</td>
                </tr>
                <tr>
                    <th style="text-align: left;">수강 정원</th>
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
                    <th style="text-align: left;">교육 기관</th>
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
                    <th style="text-align: left; vertical-align: top;">교육 커리큘럼</th>
                    <td>
                        <div v-html="educationCurriculum" class="curriculum-content"></div>
                    </td>
                </tr>
            </table>

            <div class="button-group">
                <Button v-if="editMode" label="저장" icon="pi pi-save" @click="saveChanges" />
                <Button v-if="!editMode" label="수정" icon="pi pi-pencil" @click="gotoEducationUpdate()" />
                <Button v-if="!editMode" label="삭제" severity="danger" icon="pi pi-trash" @click="deleteEducation" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();

const educationName = ref('');
const categoryName = ref('');
const applicationStartDate = ref('');
const applicationEndDate = ref('');
const educationStart = ref('');
const educationEnd = ref('');
const participants = ref(0);
const institution = ref('');
const educationCurriculum = ref('');
const editMode = ref(false);
const educationId = ref(0);

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
        participants.value = education.participants;
        institution.value = education.institution;
        educationCurriculum.value = education.educationCurriculum;
    } catch (error) {
        console.error('교육 정보를 가져오는 데 오류가 발생했습니다:', error);
    }
};

onMounted(() => {
    const id = route.params.educationId;
    educationId.value = id;
    if (educationId) {
        fetchEducationDetails(id);
    } else {
        console.error('educationId가 정의되지 않았습니다.');
    }
});

const goBackToList = () => {
    router.push('/manage-education');
};

const gotoEducationUpdate = () => {
    router.push({ name: 'education-update', params: { id: educationId.value } });
};

async function fetchDelete(url) {
    const response = await fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            // 필요한 경우 인증 헤더 등을 추가하세요.
            // 'Authorization': `Bearer ${token}`
        }
    });

    // 응답이 성공적이지 않으면 오류를 던집니다.
    if (!response.ok) {
        const errorMessage = await response.text(); // 서버에서 반환한 오류 메시지
        console.error(`Error: ${response.status} ${response.statusText} - ${errorMessage}`); // 콘솔에 오류 출력
        throw new Error(`Error: ${response.status} ${response.statusText} - ${errorMessage}`);
    }

    return response;
}

const deleteEducation = async () => {
    try {
        // 삭제 확인 알림
        const result = await Swal.fire({
            title: '교육을 삭제하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '예',
            cancelButtonText: '아니오'
        });

        // 예를 선택한 경우 삭제 진행
        if (result.isConfirmed) {
            const response = await fetchDelete(`http://localhost:8080/api/v1/education-service/education/${educationId.value}`);

            // 삭제 성공 시 알림 표시
            if (response && (response.status === 200 || response.status === 204)) {
                await Swal.fire({
                    title: '교육이 삭제되었습니다.',
                    icon: 'success'
                });
                router.push('/manage-education');
                return true;
            } else {
                await Swal.fire({
                    title: '교육 삭제 실패',
                    text: '삭제 중 오류가 발생했습니다.',
                    icon: 'error'
                });
                return false;
            }
        } else {
            // 아니오를 선택한 경우
            await Swal.fire({
                title: '교육 삭제를 취소하였습니다.',
                icon: 'info'
            });
            return false;
        }
    } catch (error) {
        // 삭제 중 오류 발생 시
        await Swal.fire({
            title: '교육 삭제 실패',
            text: error.message, // 오류 메시지를 표시합니다.
            icon: 'error'
        });
        console.error('삭제 중 오류:', error); // 오류 로그 출력
        throw error;
    }
};

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

.button-group {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    padding: 10px 0;
}

.curriculum-content {
    text-align: left;
    display: inline-block;
    max-width: 100%;
    padding: 20px 0;
}
</style>
