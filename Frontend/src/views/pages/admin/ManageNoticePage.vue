<template>
    <div class="notice-list-page">
        <div class="card">
            <div class="flex flex-row justify-between mb-4">
                <label class="text-xl font-bold">공지사항 목록</label>
                <Button label="공지사항 추가" icon="pi pi-plus" class="custom-button" @click="openAddNoticeDialog" />
            </div>
            <div class="flex flex-row justify-between mb-4">
                <Dropdown 
                    class="mr-2" 
                    v-model="selectedCategory" 
                    :options="filteredCategories" 
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

            <DataTable
                :value="filteredNotices"
                :globalFilterFields="['title', 'category.name', 'author']"
                paginator
                :rows="10"
                :rowsPerPageOptions="[10, 20, 30, 50]"
                selectionMode="single"
                dataKey="id"
                style="min-height: 400px;"
                @selection-change="showNoticeContent"
            >
                <Column field="id" header="번호" sortable />
                <Column field="category.name" header="카테고리" sortable />
                <Column field="title" header="제목" sortable />
                <Column field="author" header="작성자" sortable />
                <Column field="date" header="날짜" sortable />
                
                <Column header="수정 / 삭제">
                    <template #body="slotProps">
                        <Button 
                            icon="pi pi-pencil"
                            class="p-button p-button-sm p-button-warning mr-2" 
                            @click.stop="editNotice(slotProps.data)"
                        />
                        <Button
                            icon="pi pi-trash" 
                            class="p-button p-button-sm p-button-danger" 
                            @click.stop="confirmDeleteNotice(slotProps.data)"
                        />
                    </template>
                </Column>
            </DataTable>
        </div>

        <Dialog 
            v-model:visible="displayAddDialog" 
            modal="true" 
            :header="isEditMode ? '공지사항 수정' : '공지사항 추가'" 
            :style="{ width: '450px' }" 
            :draggable="false" 
            :closable="true"
        >
            <div class="flex flex-col gap-6">
                <div>
                    <label for="noticeTitle" class="block font-bold mb-3">제목</label>
                    <InputText id="noticeTitle" v-model="newNotice.title" required="true" class="w-full" />
                </div>
                <div>
                    <label for="author" class="block font-bold mb-3">작성자</label>
                    <InputText id="author" v-model="newNotice.author" required="true" class="w-full" :readonly="isEditMode" />
                </div>
                <div>
                    <label for="category" class="block font-bold mb-3">카테고리</label>
                    <Dropdown 
                        v-model="newNotice.category" 
                        :options="isEditMode ? filteredEditCategories : filteredCategories" 
                        optionLabel="name" 
                        placeholder="카테고리 선택" 
                        class="w-full" 
                    />
                </div>
                <div>
                    <label for="date" class="block font-bold mb-3">날짜</label>
                    <InputText id="date" type="date" v-model="newNotice.date" class="w-full" />
                </div>
                <div>
                    <label for="content" class="block font-bold mb-3">내용</label>
                    <textarea id="content" v-model="newNotice.content" required="true" class="w-full p-2 border border-gray-300 rounded" rows="5"></textarea>
                </div>
            </div>

            <template #footer>
                <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeAddDialog" />
                <Button label="저장" icon="pi pi-check" class="p-button-primary" @click="saveNotice" />
            </template>
        </Dialog>

        <Dialog 
            v-model:visible="displayNoticeDialog" 
            modal="true" 
            header="공지사항 내용" 
            :style="{ width: '450px' }" 
            :draggable="false" 
            :closable="true"
        >
            <div>
                <h3>{{ selectedNotice.title }}</h3>
                <p><strong>작성자:</strong> {{ selectedNotice.author }}</p>
                <p><strong>카테고리:</strong> {{ selectedNotice.category.name }}</p>
                <p><strong>날짜:</strong> {{ selectedNotice.date }}</p>
                <p><strong>내용:</strong></p>
                <p>{{ selectedNotice.content }}</p>
            </div>
            <template #footer>
                <Button label="닫기" icon="pi pi-times" class="p-button-text" @click="closeNoticeDialog" />
            </template>
        </Dialog>

        <Dialog 
            v-model:visible="displayDeleteConfirmDialog" 
            modal="true" 
            header="삭제 확인" 
            :style="{ width: '400px' }" 
            :draggable="false" 
            :closable="true"
        >
            <p>정말로 이 공지사항을 삭제하시겠습니까?</p>
            <template #footer>
                <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeDeleteConfirmDialog" />
                <Button label="삭제" icon="pi pi-check" class="p-button-danger" @click="deleteNotice" />
            </template>
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
import { ref } from 'vue';

const notices = ref([
    { id: 1, category: { name: '일반', code: '일반' }, title: '시스템 점검 안내', author: '관리자', date: '2024-09-30', content: '2024년 10월 1일에 시스템 점검이 진행됩니다.' },
    { id: 2, category: { name: '인사', code: '인사' }, title: '신입사원 소개', author: '인사팀', date: '2024-09-25', content: '신입사원 김영희씨를 소개합니다.' },
    { id: 3, category: { name: '휴가', code: '휴가' }, title: '휴가 정책 안내', author: '관리자', date: '2024-10-01', content: '휴가 정책이 변경됩니다. 자세한 사항은 인사팀에 문의하세요.' },
    { id: 4, category: { name: '일반', code: '일반' }, title: '보안 점검 공지', author: '보안팀', date: '2024-09-20', content: '10월 5일에 보안 점검이 진행됩니다. 모든 직원은 해당 일자에 주의해 주세요.' },
    { id: 5, category: { name: '인사', code: '인사' }, title: '인사발령 안내', author: '인사팀', date: '2024-09-15', content: '다음 주부터 인사 발령이 있습니다.' },
]);

const filteredNotices = ref([...notices.value]);
const selectedCategory = ref(null);
const globalFilter = ref('');
const displayAddDialog = ref(false);
const displayNoticeDialog = ref(false);
const displayDeleteConfirmDialog = ref(false);
const newNotice = ref({ title: '', author: '', category: '', date: '', content: '' });
const selectedNotice = ref({});
const isEditMode = ref(false);

const filteredCategories = ref([
    { name: '전체', code: '전체' },
    { name: '일반', code: '일반' },
    { name: '인사', code: '인사' },
    { name: '휴가', code: '휴가' }
]);

const filteredEditCategories = ref([
    { name: '일반', code: '일반' },
    { name: '인사', code: '인사' },
    { name: '휴가', code: '휴가' }
]);

const filterNotices = () => {
    filteredNotices.value = notices.value.filter(notice => {
        const matchesCategory = !selectedCategory.value || selectedCategory.value.code === '전체' || notice.category.code === selectedCategory.value.code;
        const matchesGlobalFilter = notice.title.includes(globalFilter.value) || notice.author.includes(globalFilter.value);
        return matchesCategory && matchesGlobalFilter;
    });
};

const openAddNoticeDialog = () => {
    isEditMode.value = false;
    newNotice.value = { title: '', author: '', category: '', date: '', content: '' };
    displayAddDialog.value = true;
};

const closeAddDialog = () => {
    displayAddDialog.value = false;
};

const saveNotice = () => {
    if (isEditMode.value) {
        Object.assign(selectedNotice.value, newNotice.value);
    } else {
        newNotice.value.id = notices.value.length + 1;
        notices.value.push({ ...newNotice.value, category: { name: newNotice.value.category, code: newNotice.value.category } });
    }
    closeAddDialog();
    filterNotices();
};

const editNotice = (notice) => {
    isEditMode.value = true;
    newNotice.value = { ...notice };
    displayAddDialog.value = true;
};

const confirmDeleteNotice = (notice) => {
    selectedNotice.value = notice;
    displayDeleteConfirmDialog.value = true;
};

const closeDeleteConfirmDialog = () => {
    displayDeleteConfirmDialog.value = false;
};

const deleteNotice = () => {
    notices.value = notices.value.filter(n => n.id !== selectedNotice.value.id);
    closeDeleteConfirmDialog();
    filterNotices();
};

const showNoticeContent = ({ value }) => {
    selectedNotice.value = value;
    displayNoticeDialog.value = true;
};

const closeNoticeDialog = () => {
    displayNoticeDialog.value = false;
};
</script>

<style scoped>
.notice-list-page {
    padding: 20px;
}
.custom-button {
    background-color: #007bff;
    color: #fff;
}
</style>
