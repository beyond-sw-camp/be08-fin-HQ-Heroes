<script setup>
import { ref, onBeforeMount } from 'vue';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import InputText from 'primevue/inputtext';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import Textarea from 'primevue/textarea';
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';import { useRouter } from 'vue-router';

const router = useRouter();

// Vue Ref 및 Reactive 변수들
const evaluations = ref([]);
const filteredEvaluations = ref([]);
const filters1 = ref(null);
const loading1 = ref(true);
const selectedEvaluation = ref(null);
const displayDialog = ref(false);
const evaluationTypes = ref(['팀', '리더', '팀원']);
const selectedEvaluationType = ref(null);
const serviceLevel = ref(null);  // 서비스 레벨 평가 점수
const teamContribution = ref(null);  // 팀 목표 및 개인 과제 평가 점수
const scheduleAdherence = ref(null);  // 일정 계획 평가 점수
const comments = ref('');

// 평가 점수 범위 (S~C)
const scoreOptions = ref([
    { label: 'S (매우 우수)', value: 100 },
    { label: 'A+ (95점)', value: 95 },
    { label: 'A (우수)', value: 90 },
    { label: 'B+ (85점)', value: 85 },
    { label: 'B (보통)', value: 80 },
    { label: 'C+ (75점)', value: 75 },
    { label: 'C (개선 필요)', value: 70 },
]);

// 임시 평가 데이터
const tempEvaluations = [
    {
        evaluationId: 1,
        employeeId: 101,
        evaluatorId: 201,
        evaluatorName: '홍길동',
        evaluatorPosition: '대리',
        evaluationType: '팀',
        score: 0,
        comments: '',
        status: false,
        createdAt: new Date('2023-08-15')
    },
    {
        evaluationId: 2,
        employeeId: 102,
        evaluatorId: 202,
        evaluatorName: '이순신',
        evaluatorPosition: '팀장',
        evaluationType: '리더',
        score: 0,
        comments: '',
        status: false,
        createdAt: new Date('2023-07-20')
    }
];

// 평가 리스트 필터링 함수
function filterByEvaluationType(evaluationType) {
    if (selectedEvaluationType.value === evaluationType) {
        selectedEvaluationType.value = null;
    } else {
        selectedEvaluationType.value = evaluationType;
    }
    filteredEvaluations.value = selectedEvaluationType.value ? evaluations.value.filter((evaluation) => evaluation.evaluationType === selectedEvaluationType.value) : evaluations.value;
}

// 평가 기한 계산 함수
function calculateDueDate(createdAt) {
    const dueDate = new Date(createdAt);
    dueDate.setDate(dueDate.getDate() + 7);
    return dueDate;
}

// 평가 세부 사항 보여주기
function showEvaluationDetails(event) {
    selectedEvaluation.value = event.data;
    resetEvaluationForm(); // 모달 폼 초기화
    displayDialog.value = true;
}

// 모달 폼 초기화
function resetEvaluationForm() {
    serviceLevel.value = null;
    teamContribution.value = null;
    scheduleAdherence.value = null;
    comments.value = '';
}

// 평가 제출 시 처리 함수
function submitEvaluation() {
    console.log('평가 제출', {
        serviceLevel: serviceLevel.value,
        teamContribution: teamContribution.value,
        scheduleAdherence: scheduleAdherence.value,
        comments: comments.value
    });

    // 선택된 평가의 점수와 코멘트를 업데이트하고 평가 상태를 완료로 변경
    selectedEvaluation.value.score = (serviceLevel.value + teamContribution.value + scheduleAdherence.value) / 3;
    selectedEvaluation.value.comments = comments.value;
    selectedEvaluation.value.status = true;

    // 완료된 평가를 리스트에서 제거
    evaluations.value = evaluations.value.filter((e) => e.evaluationId !== selectedEvaluation.value.evaluationId);
    filteredEvaluations.value = evaluations.value;

    displayDialog.value = false; // 모달 닫기
}

// 날짜 포맷팅 함수
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}/${month}/${day}`;
}

// 초기 필터 설정
function initFilters1() {
    filters1.value = {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        employeeId: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        evaluatorId: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        evaluationType: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] },
        score: { operator: FilterOperator.AND, constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }] }
    };
}

// 컴포넌트 마운트 전 임시 데이터 설정
onBeforeMount(() => {
    evaluations.value = tempEvaluations;
    filteredEvaluations.value = evaluations.value || [];
    loading1.value = false;
    initFilters1();
});

// 평가자를 클릭하면 해당 평가자의 상세 페이지로 이동
function onRowClick(event) {
    const employeeId = event.data.employeeId;
    router.push({ name: 'evaluationDetail', params: { employeeId } });
}
</script>

<template>
    <div class="card">
        <div class="font-semibold text-xl mb-4">전체 평가 목록</div>
        <DataTable
            :value="filteredEvaluations"
            removableSort
            :paginator="true"
            :rows="10"
            dataKey="evaluationId"
            :rowHover="true"
            :loading="loading1"
            selectionMode="single"
            @row-click="onRowClick"
            :metaKeySelection="false"
            @rowSelect="onRowSelect"
            @rowUnselect="onRowUnselect"
        >
            <template #header>
                <div class="flex justify-between items-center">
                    <div class="flex gap-2">
                        <Button v-for="evaluationType in evaluationTypes" :key="evaluationType" type="button" :label="evaluationType" outlined @click="filterByEvaluationType(evaluationType)" />
                    </div>
                    <div class="relative search-container">
                        <i class="pi pi-search search-icon" />
                        <InputText v-model="filters1['global'].value" placeholder="검색" class="pl-8" />
                    </div>
                </div>
            </template>
            <Column field="evaluationId" sortable header="평가 번호" />
            <Column field="evaluatorName" sortable header="평가자 이름" />
            <Column field="evaluatorPosition" sortable header="평가자 직급" />
            <Column field="evaluationType" sortable header="평가 유형" />
            <Column field="status" header="평가 상태" sortable :body="(data) => (data.status ? '완료' : '미완료')" />
            <Column field="createdAt" header="평가 기한" sortable :body="(data) => calculateDueDate(data.createdAt)" />
        </DataTable>

        <Dialog v-model:visible="displayDialog" modal="true" header="평가 상세" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="true" :dismissableMask="true">
            <div class="p-fluid p-d-flex p-flex-column p-ai-center p-py-3">

                <!-- 서비스 레벨 평가 -->
                <div class="p-field p-col-12 p-md-8 p-d-flex p-flex-column p-ai-center mb-3">
                    <label for="serviceLevel" class="p-text-bold" style="font-size: 1.2rem">서비스 레벨 평가</label>
                    <Dropdown v-model="serviceLevel" :options="scoreOptions" optionLabel="label" placeholder="점수를 선택하세요" />
                </div>

                <!-- 팀 목표 및 개인 과제 평가 -->
                <div class="p-field p-col-12 p-md-8 p-d-flex p-flex-column p-ai-center mb-3">
                    <label for="teamContribution" class="p-text-bold" style="font-size: 1.2rem">팀 목표 및 개인 과제 평가</label>
                    <Dropdown v-model="teamContribution" :options="scoreOptions" optionLabel="label" placeholder="점수를 선택하세요" />
                </div>

                <!-- 일정 계획 평가 -->
                <div class="p-field p-col-12 p-md-8 p-d-flex p-flex-column p-ai-center mb-3">
                    <label for="scheduleAdherence" class="p-text-bold" style="font-size: 1.2rem">일정 계획 평가</label>
                    <Dropdown v-model="scheduleAdherence" :options="scoreOptions" optionLabel="label" placeholder="점수를 선택하세요" />
                </div>

                <!-- 코멘트 -->
                <div class="p-field p-col-12 p-md-8 p-d-flex p-flex-column p-ai-center mb-3">
                    <label for="comments" class="p-text-bold" style="font-size: 1.2rem">코멘트</label>
                    <Textarea id="comments" v-model="comments" rows="5" autoResize placeholder="코멘트를 입력하세요" class="p-mt-1" style="width: 100%; height: 150px" />
                </div>

            </div>

            <div class="p-d-flex p-jc-center p-mt-3">
                <Button label="제출" icon="pi pi-check" class="p-button-rounded p-button-primary p-mr-2" @click="submitEvaluation" />
                <Button label="취소" icon="pi pi-times" class="p-button-text p-button-plain" @click="displayDialog = false" />
            </div>
        </Dialog>
    </div>
</template>
