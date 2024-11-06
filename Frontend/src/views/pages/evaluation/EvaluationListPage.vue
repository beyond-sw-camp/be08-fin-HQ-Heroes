<template>
    <div class="card" style="max-height: 44.1rem">
        <div class="flex items-center justify-center text-xl mb-4">
            <label class="font-semibold">{{ evaluationPeriod }} - {{ authStore.employeeData.teamName }} 팀원 평가</label>
        </div>
        <DataTable
            :value="filteredEmployees"
            :paginator="true"
            :rows="6"
            removableSort
            dataKey="employeeNo"
            :rowHover="true"
            v-model:filters="filters"
            filterDisplay="menu"
            :filters="filters"
            :globalFilterFields="['employeeName', 'deptName', 'jobRoleName', 'teamName', 'positionName', 'employeeId', 'joinDate']"
            :metaKeySelection="false"
        >
            <template #header>
                <div class="flex items-center justify-end mb-1">
                    <InputGroup style="width: 15rem">
                        <InputGroupAddon>
                            <i class="pi pi-search"></i>
                        </InputGroupAddon>
                        <InputText v-model="filters['global'].value" placeholder="검색어를 입력해주세요" />
                    </InputGroup>
                </div>
            </template>
            <template #empty> 검색 결과가 없습니다. </template>

            <Column field="employeeName" sortable header="이 름">
                <template #body="{ data }">
                    {{ data.employeeName }}
                </template>
            </Column>
            <Column field="jobRoleName" sortable header="직무">
                <template #body="{ data }">
                    {{ data.jobRoleName }}
                </template>
            </Column>
            <Column field="positionName" sortable header="직 책">
                <template #body="{ data }">
                    {{ data.positionName }}
                </template>
            </Column>
            <Column field="employeeId" sortable header="사 번">
                <template #body="{ data }">
                    {{ data.employeeId }}
                </template>
            </Column>
            <Column field="joinDate" sortable header="입사일" dataType="date">
                <template #body="{ data }">
                    {{ formatDate(new Date(data.joinDate)) }}
                </template>
            </Column>
            <Column class="w-40">
                <template #body="{ data }">
                    <Button v-if="canReevaluate(data.employeeId)" label="재평가" icon="pi pi-refresh" @click="showEmployeeDetails(data)" severity="warn" />
                    <Button v-else label="평가하기" icon="pi pi-pencil" @click="showEmployeeDetails(data)" severity="primary" />
                </template>
            </Column>
        </DataTable>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { computed, onBeforeMount, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { fetchGet } from '../auth/service/AuthApiService';

// 인증된 사용자 정보를 가져옴
const authStore = useAuthStore();
const router = useRouter();

// 상태 변수 선언
const employees = ref([]); // 전체 직원 목록
const filteredEmployees = ref([]); // 필터링된 직원 목록
const evaluations = ref([]); // 평가 목록
const filters = ref(null); // 필터 설정

// 상반기/하반기 텍스트 설정 (한국 시간 기준)
const evaluationPeriod = computed(() => {
    // 현재 한국 시간으로 Date 객체 생성
    const now = new Date();
    const koreanTime = new Date(now.setHours(now.getHours() + 9)); // 한국 시간으로 변환 (UTC+9)
    const year = now.getFullYear();
    const currentMonth = koreanTime.getMonth() + 1; // 월은 0부터 시작하므로 +1
    return currentMonth <= 6 ? year + '년 상반기' : year + '년 하반기';
});

// 로그인한 사용자의 팀명을 기준으로 팀원 필터링 함수
function filterEmployeesByTeam() {
    const teamName = authStore.employeeData?.teamName;
    if (!teamName) return; // teamName이 로드되지 않았을 때 함수 종료
    console.log('현재 로그인한 사용자의 팀:', teamName);

    // 직원 목록을 팀명 기준으로 필터링하여 filteredEmployees에 저장
    filteredEmployees.value = employees.value.filter((employee) => employee.teamName === teamName && employee.positionName === '팀원');
}

// 직원 목록 데이터 가져오기
async function fetchEmployeeList() {
    try {
        const employeesData = await fetchGet('https://hq-heroes-api.com/api/v1/employee/employees', router.push, router.currentRoute.value);
        console.log('가져온 직원 데이터:', employeesData);

        employees.value = employeesData || [];
        filterEmployeesByTeam(); // 팀에 따라 필터링된 직원 목록 설정
    } catch (error) {
        console.error('직원 데이터를 가져오는 중 오류 발생:', error);
        employees.value = [];
        filteredEmployees.value = [];
    }
}

// 평가 데이터를 가져오는 함수
async function fetchEvaluationsByEmployeeId() {
    try {
        const evaluationData = await fetchGet('https://hq-heroes-api.com/api/v1/evaluation-service/evaluations/by-evaluatorId', router.push, router.currentRoute.value);
        evaluations.value = evaluationData || [];
    } catch (error) {
        console.error('평가 데이터를 가져오는 중 오류 발생:', error);
        evaluations.value = [];
    }
}

// DataTable 기본 필터 설정 초기화
function initFilters() {
    filters.value = {
        global: { value: null, matchMode: 'contains' },
        employeeName: { operator: 'and', constraints: [{ value: null, matchMode: 'startsWith' }] },
        deptName: { operator: 'and', constraints: [{ value: null, matchMode: 'equals' }] },
        jobRoleName: { operator: 'and', constraints: [{ value: null, matchMode: 'equals' }] },
        teamName: { operator: 'and', constraints: [{ value: null, matchMode: 'equals' }] },
        positionName: { operator: 'and', constraints: [{ value: null, matchMode: 'equals' }] },
        employeeId: { operator: 'and', constraints: [{ value: null, matchMode: 'equals' }] },
        joinDate: { operator: 'and', constraints: [{ value: null, matchMode: 'dateIs' }] }
    };
}

// 평가 상태에 따라 조건부로 버튼 렌더링
function canReevaluate(employeeId) {
    return evaluations.value.some((evaluation) => evaluation.employeeId === employeeId && evaluation.evaluatorId === authStore.employeeData.employeeId);
}

// 직원 상세 보기 함수
function showEmployeeDetails(data) {
    router.push({ name: 'evaluationDetail', params: { employeeId: data.employeeId } });
}

function formatDate(date) {
    const year = String(date.getFullYear()).slice(2); // yy 형식으로 변경
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 두 자리 형식
    const day = String(date.getDate()).padStart(2, '0'); // 두 자리 형식
    return `${year}-${month}-${day}`;
}

// 로그인한 사용자의 팀명이 변경될 때마다 직원 목록을 필터링
watch(
    () => authStore.employeeData?.teamName,
    (newTeamName, oldTeamName) => {
        if (newTeamName && newTeamName !== oldTeamName) {
            fetchEmployeeList(); // teamName이 변경될 때 직원 목록을 새로 가져옴
        }
    },
    { immediate: true }
);

// 컴포넌트 마운트 시 데이터 가져오기
onBeforeMount(() => {
    fetchEvaluationsByEmployeeId();
    initFilters();
});
</script>

<style scoped lang="scss">
:deep(.p-datatable-frozen-tbody) {
    font-weight: bold;
}
</style>
