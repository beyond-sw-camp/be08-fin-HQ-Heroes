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
        :globalFilterFields="['title', 'category', 'employeeName']"
        paginator
        :rows="10"
        removableSort
        :rowsPerPageOptions="[10, 20, 30, 50]"
        selectionMode="single"
        dataKey="id"
        style="min-height: 400px;"
        @row-click="showNoticeContent"
        :metaKeySelection="false"
        @rowSelect="onRowSelect"
        @rowUnselect="onRowUnselect"
      >
        <Column field="noticeId" header="번호" sortable />
        <Column
          header="카테고리"
          :sortable="true"
          sortField="category"
        >
          <template #body="slotProps">
            {{ categoryMap[slotProps.data.category] }}
          </template>
        </Column>

        <Column field="title" header="제목" sortable />
        <Column field="employeeName" header="작성자" sortable />
        <Column field="createdAt" header="날짜" sortable>
          <template #body="slotProps">
            {{ formatDateTime(slotProps.data.createdAt) }}
          </template>
        </Column>

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

    <Dialog v-model:visible="displayAddDialog" modal="true" :header="isEditMode ? '공지사항 수정' : '공지사항 추가'"
      :style="{ width: '450px' }" :draggable="false" :closable="true">
      <div class="flex flex-col gap-6">
        <div>
          <label for="noticeTitle" class="block font-bold mb-3">제목</label>
          <InputText id="noticeTitle" v-model="newNotice.title" required="true" class="w-full" />
        </div>
        <div>
          <label for="employeeName" class="block font-bold mb-3">작성자</label>
          <InputText id="employeeName" v-model="newNotice.employeeName" required="true" class="w-full" :readonly="isEditMode" />
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

    <Dialog v-model:visible="displayNoticeDialog" modal="true" header="공지사항 내용" :style="{ width: '500px', maxWidth: '90vw' }">
      <div class="employee-details">
        <h3><strong>제목 : </strong> {{ selectedNotice.data.title }}</h3>
        <p><strong>작성자 : </strong> {{ selectedNotice.data.employeeName }}</p>
        <p><strong>카테고리 : </strong> {{ categoryMap[selectedNotice.data.category] }}</p>
        <p><strong>날짜 : </strong> {{ formatDateTime(selectedNotice.data.createdAt) }}</p>
        <p><strong>내용 : </strong> {{ selectedNotice.data.content }}</p>
      </div>
    </Dialog>

    <Dialog v-model:visible="displayDeleteConfirmDialog" modal="true" header="삭제 확인" :style="{ width: '400px' }"
      :draggable="false" :closable="true">
      <p>정말로 이 공지사항을 삭제하시겠습니까?</p>
      <template #footer>
        <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeDeleteConfirmDialog" />
        <Button label="삭제" icon="pi pi-check" class="p-button-danger" @click="handleDeleteNotice" />
      </template>
    </Dialog>
  </div>
</template>

<script setup>
import { format } from 'date-fns';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import InputText from 'primevue/inputtext';
import { onMounted, ref } from 'vue';
import { createNotice, deleteNotice, fetchNotices, updateNotice } from './service/adminNoticeService';

const notices = ref([]);
const filteredNotices = ref([]);
const selectedCategory = ref(null);
const globalFilter = ref('');
const displayAddDialog = ref(false);
const displayNoticeDialog = ref(false);
const displayDeleteConfirmDialog = ref(false);
const newNotice = ref({ title: '', employeeName: '', category: '', date: '', content: '' });
const selectedNotice = ref({});
const isEditMode = ref(false);
const categoryFormat = (category) => {
  return categoryMap(category);
};

// 카테고리 Enum 값 한글로 매핑
const categoryMap = {
  GENERAL: '일반',
  HR: '인사',
  ATTENDANCE: '근태',
  VACATION: '휴가',
  EDUCATION: '교육'
};

const filteredCategories = ref([
  { name: '전체', code: '전체' },
  { name: '일반', code: 'GENERAL' },
  { name: '인사', code: 'HR' },
  { name: '근태', code: 'ATTENDANCE' },
  { name: '휴가', code: 'VACATION' },
  { name: '교육', code: 'EDUCATION' }
]);

const filteredEditCategories = ref([
  { name: '일반', code: 'GENERAL' },
  { name: '인사', code: 'HR' },
  { name: '근태', code: 'ATTENDANCE' },
  { name: '휴가', code: 'VACATION' },
  { name: '교육', code: 'EDUCATION' }
]);

// 공지사항 데이터 필터링
const filterNotices = () => {
  filteredNotices.value = notices.value.filter(notice => {
    const matchesCategory = !selectedCategory.value || selectedCategory.value.code === '전체' || notice.category === selectedCategory.value.code;
    const matchesGlobalFilter = notice.title.includes(globalFilter.value) || notice.employeeName.includes(globalFilter.value);
    return matchesCategory && matchesGlobalFilter;
  });
};

// 공지사항 목록
onMounted(async () => {
  try {
    notices.value = await fetchNotices();
    filterNotices();
  } catch (error) {
    console.error('Error fetching notices:', error);
  }
});

// 날짜 포맷팅
const formatDateTime = (dateString) => {
  const date = new Date(dateString);
  return format(date, 'yyyy-MM-dd');
};

// 공지사항 추가/수정 저장
const saveNotice = async () => {
  try {
    if (isEditMode.value) {
      await updateNotice(selectedNotice.value.id, newNotice.value);
    } else {
      await createNotice(newNotice.value);
    }
    closeAddDialog();
    notices.value = await fetchNotices();
    filterNotices();
  } catch (error) {
    console.error('Error saving notice:', error);
  }
};

// 공지사항 삭제
const handleDeleteNotice = async () => {
  try {
    await deleteNotice(selectedNotice.value.id);
    closeDeleteConfirmDialog();
    notices.value = await fetchNotices();
    filterNotices();
  } catch (error) {
    console.error('Error deleting notice:', error);
  }
};

// 공지사항 내용 보기
const showNoticeContent = (notice) => {
  selectedNotice.value = notice;
  displayNoticeDialog.value = true;
};

const openAddNoticeDialog = () => {
  isEditMode.value = false;
  newNotice.value = { title: '', employeeName: '', category: '', date: '', content: '' };
  displayAddDialog.value = true;
};

// 공지사항 수정
const editNotice = (notice) => {
  isEditMode.value = true;
  newNotice.value = { ...notice }; // 기존 공지사항 데이터 로드
  displayAddDialog.value = true;
};

const confirmDeleteNotice = (notice) => {
  selectedNotice.value = notice;
  displayDeleteConfirmDialog.value = true;
};

const closeAddDialog = () => {
  displayAddDialog.value = false;
};

const closeDeleteConfirmDialog = () => {
  displayDeleteConfirmDialog.value = false;
};

</script>

<style scoped>
.employee-details {
    padding: 1.5rem;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.employee-details p {
    margin: 0.5rem 0;
    font-size: 1rem;
    line-height: 1.6;
    color: #333;
}

strong {
    color: #2c3e50;
}
</style>