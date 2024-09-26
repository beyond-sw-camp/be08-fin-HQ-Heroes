<template>
    <div class="notice-list-page">
        <div class="card">
            <div class="flex flex-row justify-between">
                <label class="text-xl font-bold">공지사항 목록</label>
                <div class="flex flex-row w-1rem col-4">
                    <Dropdown 
                        class="mr-2" 
                        v-model="selectedCategory" 
                        :options="categories" 
                        optionLabel="name" 
                        placeholder="카테고리 선택" 
                        @change="filterNotices" 
                    />
                    <InputGroup>
                        <InputGroupAddon>
                            <i class="pi pi-search"></i>
                        </InputGroupAddon>
                        <InputText 
                            placeholder="제목, 작성자" 
                            v-model="globalFilter" 
                            @input="filterNotices" 
                        />
                    </InputGroup>
                </div>
            </div>
            <DataTable
                :value="filteredNotices"
                :globalFilterFields="['title', 'category', 'author']"
                paginator
                :rows="10"
                :rowsPerPageOptions="[10, 20, 30, 50]"
                selectionMode="single"
                dataKey="id"
                style="min-height: 400px;"
                @row-click="showNoticeContent"
            >
                <Column field="id" header="번호" />
                <Column field="category" header="카테고리" />
                <Column field="title" header="제목" />
                <Column field="author" header="작성자" />
                <Column field="date" header="날짜" />
            </DataTable>
        </div>

        <Dialog 
            header="공지사항 내용" 
            :visible="displayNoticeDialog" 
            style="width: 70%" 
            :modal="true" 
            :draggable="false"
            :closeOnEscape="true"
            @hide="hideNoticeContent"
        >
            <div v-if="selectedNotice">
                <h3>{{ selectedNotice.title }}</h3>
                <p><strong>작성자:</strong> {{ selectedNotice.author }}</p>
                <p><strong>카테고리:</strong> {{ selectedNotice.category }}</p>
                <p><strong>날짜:</strong> {{ selectedNotice.date }}</p>
                <p>{{ selectedNotice.content }}</p>
            </div>
            <div class="flex justify-end gap-2 mt-4">
                <Button 
                    class="p-button p-component p-button-danger" 
                    @click="hideNoticeContent"
                >
                    닫기
                </Button>
            </div>
        </Dialog>
    </div>
</template>

<script setup>
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import InputText from 'primevue/inputtext';
import { onBeforeMount, ref } from 'vue';

const notices = ref([]);
const filteredNotices = ref([]);
const selectedCategory = ref(null);
const globalFilter = ref('');
const displayNoticeDialog = ref(false);
const selectedNotice = ref(null);

const categories = ref([
    { name: '전체', code: null },
    { name: '일반', code: 'general' },
    { name: '인사', code: 'hr' },
    { name: '휴가', code: 'vacation' }
]);

async function fetchNotices() {
    notices.value = [
        { id: 1, category: '일반', title: '2024년 가을 학기 수업 일정 안내', author: '홍길동', date: '2024-09-01', content: '2024년 가을 학기 수업 일정이 업데이트 되었습니다. 자세한 내용은 학사 공지를 확인해 주시기 바랍니다.' },
        { id: 2, category: '인사', title: '인사부 직원 변경 안내', author: '김유신', date: '2024-09-05', content: '인사부의 직원이 변경되었습니다. 자세한 사항은 인사부에 문의해 주시기 바랍니다.' },
        { id: 3, category: '휴가', title: '2024년 연말 휴가 계획 안내', author: '이순신', date: '2024-09-10', content: '연말 휴가 계획을 세우시기 바랍니다. 신청 마감일은 10월 15일입니다.' },
        { id: 4, category: '일반', title: '신입 사원 교육 일정 안내', author: '박지민', date: '2024-09-15', content: '신입 사원을 위한 교육 일정이 확정되었습니다. 참석 여부를 회신해 주시기 바랍니다.' },
        { id: 5, category: '인사', title: '복리후생 제도 개편 안내', author: '최태웅', date: '2024-09-20', content: '복리후생 제도가 개편되었습니다. 세부 내용은 내부 공지를 통해 확인해 주시기 바랍니다.' },
        { id: 6, category: '휴가', title: '여름 휴가 신청 마감 안내', author: '이혜선', date: '2024-09-22', content: '여름 휴가 신청 마감일이 다가오고 있습니다. 미리 신청해 주시기 바랍니다.' },
        { id: 7, category: '일반', title: '사내 행사 일정 안내', author: '김영호', date: '2024-09-24', content: '사내 행사 일정이 확정되었습니다. 모두 참석해 주시기 바랍니다.' },
        { id: 8, category: '인사', title: '승진 인사 발표', author: '이수진', date: '2024-09-25', content: '최근 승진 인사 발표가 있었습니다. 자세한 내용은 인사부 공지를 확인해 주세요.' },
        { id: 9, category: '휴가', title: '공식 휴일 안내', author: '이재명', date: '2024-09-26', content: '오는 10월 3일은 공식 휴일입니다. 참고해 주시기 바랍니다.' },
        { id: 10, category: '일반', title: '사무실 정리 및 청소 안내', author: '최민수', date: '2024-09-27', content: '사무실 정리 및 청소가 예정되어 있습니다. 협조 부탁드립니다.' },
    ];
    filteredNotices.value = notices.value; // 필터링된 공지사항 초기화
}

function filterNotices() {
    const globalSearchText = globalFilter.value.toLowerCase();
    
    // 공지사항 필터링
    filteredNotices.value = notices.value.filter(notice => {
        const matchesTitle = notice.title.toLowerCase().includes(globalSearchText); // 제목 필터링
        const matchesAuthor = notice.author.toLowerCase().includes(globalSearchText); // 작성자 필터링
        const matchesCategory = selectedCategory.value 
            ? (selectedCategory.value.code === null || notice.category === selectedCategory.value.name) // 카테고리 필터링
            : true;
        return (matchesTitle || matchesAuthor) && matchesCategory; // 제목, 작성자, 카테고리 일치 여부 확인
    });
}

function showNoticeContent(event) {
    selectedNotice.value = event.data; // 공지사항 저장
    displayNoticeDialog.value = true;
}

function hideNoticeContent() {
    displayNoticeDialog.value = false;
    selectedNotice.value = null; // 공지사항 초기화
}

onBeforeMount(fetchNotices); // 컴포넌트 마운트 전에 공지사항 가져오기
</script>
