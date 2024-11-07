<template>
    <div class="card" style="max-height: 44.1rem">
        <div class="font-semibold text-xl mb-4">평가 결과</div>

        <!-- DataTable for listing evaluations -->
        <DataTable :value="evaluations" :paginator="true" :rows="10" dataKey="evaluationId" :rowHover="true" selectionMode="single" @row-click="showEvaluationDetails" :metaKeySelection="false" sortField="year" :sortOrder="1">
            <!-- No Data Template -->
            <template #empty>No evaluations found.</template>

            <Column field="year" header="연도" :sortable="true" style="min-width: 8rem">
                <template #body="{ data }">
                    {{ new Date(data.updatedAt).getFullYear() }}
                </template>
            </Column>

            <Column field="halfTerm" header="반기" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ new Date(data.updatedAt).getMonth() + 1 > 6 ? '하반기' : '상반기' }}
                </template>
            </Column>

            <!-- 평가 등급 컬럼 -->
            <Column field="grade" header="평가 등급" style="min-width: 12rem">
                <template #body="{ data }">
                    {{ data.score >= 90 ? 'A' : data.score >= 80 ? 'B' : 'C' }}
                </template>
            </Column>

            <!-- 평가 점수 컬럼 -->
            <Column field="score" header="평가 점수" style="min-width: 8rem">
                <template #body="{ data }">{{ data.score }}</template>
            </Column>

            <!-- 평가자 컬럼 -->
            <Column field="evaluatorName" header="평가자" style="min-width: 10rem">
                <template #body="{ data }">{{ data.evaluatorName }}</template>
            </Column>

            <!-- 평가일 컬럼 -->
            <Column field="updatedAt" header="평가일" :sortable="true" dataType="date" style="min-width: 10rem">
                <template #body="{ data }">{{ formatDate(new Date(data.updatedAt)) }}</template>
            </Column>
        </DataTable>

        <Dialog header="평가 상세 정보" v-model:visible="displayDialog" modal :closable="false" :style="{ width: '30vw' }" :breakpoints="{ '1199px': '50vw', '575px': '90vw' }" closeOnEscape :blockScroll="true">
            <template v-if="selectedEvaluation">
                <div class="p-4 rounded-lg bg-white shadow">
                    <!-- 평가 정보 -->
                    <div class="flex items-center justify-start gap-5 mb-4">
                        <div>
                            <p class="text-sm text-gray-500 font-medium">연도</p>
                            <p class="text-lg font-semibold">{{ formatDate(new Date(selectedEvaluation.updatedAt)).split('-')[0] }}</p>
                        </div>
                        <div>
                            <p class="text-sm text-gray-500 font-medium">반기</p>
                            <p class="text-lg">{{ formatDate(new Date(selectedEvaluation.updatedAt)).split('-')[1] > 6 ? '하반기' : '상반기' }}</p>
                        </div>
                        <div>
                            <p class="text-sm text-gray-500 font-medium">평가 등급</p>
                            <p class="text-lg text-primary">{{ selectedEvaluation.score >= 90 ? 'A' : selectedEvaluation.score >= 80 ? 'B' : 'C' }}</p>
                        </div>
                        <div>
                            <p class="text-sm text-gray-500 font-medium">평가 점수</p>
                            <p class="text-lg font-semibold text-primary">{{ selectedEvaluation.score }}</p>
                        </div>
                        <div class="evaluation-item">
                            <p class="text-sm text-gray-500 font-medium">평가자</p>
                            <p class="text-lg">{{ selectedEvaluation.evaluatorName }}</p>
                        </div>
                    </div>

                    <!-- 기타 정보 -->
                    <div class="evaluation-item">
                        <p class="text-sm text-gray-500 font-medium">평가일</p>
                        <p class="text-lg">{{ formatDate(selectedEvaluation.updatedAt) }}</p>
                    </div>
                    <div class="evaluation-item mt-3">
                        <p class="text-sm text-gray-500 font-medium">Comment</p>
                        <div class="bg-gray-50 rounded-md">{{ selectedEvaluation.comments || '없음' }}</div>
                    </div>
                </div>
            </template>

            <template #footer>
                <Button label="닫기" icon="pi pi-times" class="p-button-danger" @click="displayDialog = false" />
            </template>
        </Dialog>
    </div>
</template>

<script setup>
import router from '@/router';
import { onBeforeMount, ref } from 'vue';
import { fetchGet } from '../auth/service/AuthApiService';

// State variables
const evaluations = ref([]); // 전체 평가 리스트
const selectedEvaluation = ref(null); // 선택된 평가 정보
const displayDialog = ref(false); // Dialog 표시 여부

// 사원 ID로 평가 목록 가져오기 (로그인된 사용자)
async function fetchEvaluationsByEmployeeId() {
    try {
        const evaluationData = await fetchGet('https://hq-heroes-api.com/api/v1/evaluation-service/evaluations/by-employeeId', router.push, router.currentRoute.value);

        evaluations.value = (evaluationData || []).sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
    } catch (error) {
        console.error('사원 ID로 평가 데이터를 가져오는 중 오류 발생:', error);
        evaluations.value = [];
    }
}

// 평가 상세 보기 함수
function showEvaluationDetails(event) {
    const evaluationId = event.data.evaluationId;
    fetchEvaluationById(evaluationId);
}

// 평가 ID로 평가 상세 정보 가져오기
async function fetchEvaluationById(evaluationId) {
    try {
        const evaluation = await fetchGet(`https://hq-heroes-api.com/api/v1/evaluation-service/evaluation/${evaluationId}`, router.push, router.currentRoute.value);
        if (evaluation) {
            selectedEvaluation.value = evaluation;
            displayDialog.value = true;
        }
    } catch (error) {
        console.error('평가 데이터를 가져오는 중 오류 발생:', error);
    }
}

function formatDate(date) {
    // 문자열 날짜나 null 값 확인 및 변환
    const parsedDate = typeof date === 'string' || typeof date === 'number' ? new Date(date) : date;

    // 유효한 날짜인지 확인
    if (isNaN(parsedDate)) {
        return ''; // 유효하지 않은 날짜일 경우 빈 문자열 반환
    }

    const year = String(parsedDate.getFullYear()).slice(2); // yy 형식으로 변경
    const month = String(parsedDate.getMonth() + 1).padStart(2, '0'); // 두 자리 형식
    const day = String(parsedDate.getDate()).padStart(2, '0'); // 두 자리 형식
    return `${year}-${month}-${day}`;
}

// 컴포넌트가 마운트될 때 평가 데이터 가져오기 (전체 목록)
onBeforeMount(() => {
    fetchEvaluationsByEmployeeId();
});
</script>
