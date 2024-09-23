<template>
    <div class="card">
        <h2 class="font-semibold text-xl mb-4">자격증 목록</h2>
        <DataTable :value="certifications" :paginator="true" :rows="10" dataKey="certificationId" :rowHover="true" @row-click="showCertificationDetails">
            <Column field="certificationId" header="번호" />
            <Column field="certificationName" header="자격명" />
            <Column field="name" header="이름" />
            <Column field="evaluatorPosition" header="직급" />
            <Column field="evaluationType" header="평가 기관" />
            <Column field="createdAt" header="취득일" :body="(data) => formatDate(data.createdAt)" />
        </DataTable>

        <!-- 자격증 추천 섹션 -->
        <h2 class="font-semibold text-xl my-6">회사가 추천하는 자격증</h2>
        <div class="grid grid-cols-12 gap-4">
            <div class="col-span-12 lg:col-span-6 xl:col-span-3" v-for="cert in recommendedCertifications" :key="cert.certificationId">
                <div class="card mb-0">
                    <div class="flex justify-between mb-4">
                        <div>
                            <span class="block text-muted-color font-medium mb-4">{{ cert.category }}</span>
                            <div class="text-surface-900 dark:text-surface-0 font-medium text-xl">{{ cert.name }}</div>
                        </div>
                        <div class="flex items-center justify-center" :class="cert.bgClass" style="width: 2.5rem; height: 2.5rem">
                            <i :class="cert.iconClass"></i>
                        </div>
                    </div>
                    <span class="text-muted-color">{{ currentDate }}</span>
                </div>
            </div>
        </div>
    </div>

    <!-- 자격증 상세 정보 모달 -->
    <Dialog v-model:visible="displayDialog" modal="true" header="자격증 상세" :style="{ width: '50vw', borderRadius: '12px' }" :draggable="false" :closable="true">
        <div v-if="selectedCertification">
            <h3>자격증 정보</h3>
            <p><strong>자격명:</strong> {{ selectedCertification.certificationName }}</p>
            <p><strong>이름:</strong> {{ selectedCertification.name }}</p>
            <p><strong>직급:</strong> {{ selectedCertification.evaluatorPosition }}</p>
            <p><strong>평가 기관:</strong> {{ selectedCertification.evaluationType }}</p>
            <p><strong>취득일:</strong> {{ formatDate(selectedCertification.createdAt) }}</p>
        </div>
        <template #footer>
            <Button label="닫기" icon="pi pi-times" class="p-button-text p-button-plain" @click="displayDialog = false" />
        </template>
    </Dialog>
</template>

<script setup>
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import { ref } from 'vue';

// 자격증 목록 데이터
const certifications = ref([
    {
        certificationId: 1,
        certificationName: '정보처리기사',
        name: '홍길동',
        evaluatorPosition: '대리',
        evaluationType: '한국산업인력공단',
        createdAt: new Date('2022-05-15')
    },
    {
        certificationId: 2,
        certificationName: '기술사',
        name: '이순신',
        evaluatorPosition: '팀장',
        evaluationType: '한국기술자격검정원',
        createdAt: new Date('2021-10-10')
    }
]);

// 추천 자격증 목록
const recommendedCertifications = ref([
    {
        certificationId: 1,
        name: '정보보안기사',
        category: '보안',
        iconClass: 'pi pi-lock text-green-500 !text-xl',
        bgClass: 'bg-green-100 dark:bg-green-400/10 rounded-border'
    },
    {
        certificationId: 2,
        name: '데이터 분석 전문가',
        category: '데이터',
        iconClass: 'pi pi-chart-line text-orange-500 !text-xl',
        bgClass: 'bg-orange-100 dark:bg-orange-400/10 rounded-border'
    },
    {
        certificationId: 3,
        name: '클라우드 컴퓨팅 자격증',
        category: '클라우드',
        iconClass: 'pi pi-cloud text-blue-500 !text-xl',
        bgClass: 'bg-blue-100 dark:bg-blue-400/10 rounded-border'
    },
    {
        certificationId: 4,
        name: 'AI 엔지니어 전문가',
        category: 'AI',
        iconClass: 'pi pi-cog text-purple-500 !text-xl',
        bgClass: 'bg-purple-100 dark:bg-purple-400/10 rounded-border'
    }
]);

// 선택된 자격증 상세 정보
const selectedCertification = ref(null);
const displayDialog = ref(false);

// 자격증 세부 사항 보여주기
function showCertificationDetails(event) {
    selectedCertification.value = event.data;
    displayDialog.value = true;
}

// 날짜 포맷팅 함수 (yyyy/mm/dd 형식)
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}/${month}/${day}`;
}

// 현재 날짜
const currentDate = formatDate(new Date());
</script>
