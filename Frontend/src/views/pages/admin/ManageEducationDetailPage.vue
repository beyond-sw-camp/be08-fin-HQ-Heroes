<template>
    <div class="card">
        <div class="education-detail">
            <div class="flex justify-end mb-4">
                <Button label="목록" icon="pi pi-bars" @click="goBackToList" outlined />
            </div>
            <div class="text-3xl font-bold">
                <span>[{{ categoryName }}] {{ educationName }} </span>
            </div>

            <hr />

            <!-- education-info 테이블로 수정 -->
            <table class="education-info text-lg">
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
                <Button v-if="editMode" label="저장" icon="pi pi-save" @click="saveChanges" />
                <Button v-if="!editMode" label="수정" icon="pi pi-pencil" @click="gotoEducationUpdate()" />
                <Button v-if="!editMode" label="삭제" severity="danger" icon="pi pi-trash" @click="deleteEducation" />
            </div>
        </div>
    </div>
</template>

<script setup>
import Swal from 'sweetalert2';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchDelete, fetchGet } from '../auth/service/AuthApiService';

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

const fetchEducationDetails = async (educationId) => {
    try {
        const education = await fetchGet(`https://hq-heroes-api.com/api/v1/education-service/education/${educationId}`);
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
            const response = await fetchDelete(`https://hq-heroes-api.com/api/v1/education-service/education/${educationId.value}`);

            console.log(response);
            // 삭제 성공 시 알림 표시
            if (response.message.includes('교육이 삭제되었습니다.')) {
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
