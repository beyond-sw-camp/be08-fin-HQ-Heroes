<template>
  <div class="notice-list-page">
    <div class="card">
      <div class="flex flex-row justify-between mb-4">
        <label class="text-xl font-bold">공지사항 목록</label>
        <Button label="공지사항 추가" icon="pi pi-plus" class="custom-button" @click="showWriteNoticePage" />
      </div>
      <div class="flex flex-row justify-between mb-4">
        <Dropdown class="mr-2" v-model="selectedCategory" :options="categories" optionLabel="name" placeholder="카테고리 선택"
          @change="filterNotices" />
        <InputGroup>
          <InputGroupAddon>
            <i class="pi pi-search"></i>
          </InputGroupAddon>
          <InputText placeholder="제목, 작성자" v-model="globalFilter" @input="filterNotices" />
        </InputGroup>
      </div>

      <DataTable :value="filteredNotices" :globalFilterFields="['title', 'categoryName', 'employeeName']" paginator
        :rows="10" removableSort :rowsPerPageOptions="[10, 20, 30, 50]" dataKey="id" style="min-height: 400px;"
        @row-click="(event) => showNoticeDetail(event.data.noticeId)" :metaKeySelection="false">
        <Column field="noticeId" header="번호" sortable />
        <Column field="categoryName" header="카테고리" :sortable="true" sortField="category" />
        <Column field="title" header="제목" sortable />
        <Column field="employeeName" header="작성자" sortable />
        <Column field="createdAt" header="날짜" sortable>
          <template #body="slotProps">
            {{ formatDateTime(slotProps.data.createdAt) }}
          </template>
        </Column>

        <Column header="수정 / 삭제">
          <template #body="slotProps">
            <Button icon="pi pi-pencil" class="p-button p-button-sm p-button-warning mr-2"
              @click.stop="editNotice(slotProps.data)" />
            <Button icon="pi pi-trash" class="p-button p-button-sm p-button-danger"
              @click.stop="confirmDeleteNotice(slotProps.data)" />
          </template>
        </Column>
      </DataTable>
    </div>

    <Dialog v-model:visible="displayAddDialog" modal="true" :header="'공지사항 수정'" :style="{ width: '450px' }"
      :draggable="false" :closable="true">
      <div class="flex flex-col gap-6">
        <div>
          <label for="noticeTitle" class="block font-bold mb-3">제목</label>
          <InputText id="noticeTitle" v-model="newNotice.title" required="true" class="w-full" />
        </div>
        <div>
          <label for="employeeName" class="block font-bold mb-3">작성자</label>
          <InputText id="employeeName" v-model="newNotice.employeeName" readonly class="w-full" />
        </div>
        <div>
          <label for="category" class="block font-bold mb-3">카테고리</label>
          <Dropdown v-model="newNotice.categoryName" :options="categories" optionLabel="name" placeholder="카테고리 선택"
            class="w-full" />
        </div>
        <div>
          <label for="date" class="block font-bold mb-3">작성 날짜 / 시간</label>
          <InputText id="date" type="text" v-model="newNotice.createdAt" class="w-full" readonly />
        </div>
        <div>
          <label for="updater" class="block font-bold mb-3">수정자</label>
          <InputText id="updater" v-model="newNotice.updaterName" readonly class="w-full" />
        </div>
        <div>
          <label for="updatedAt" class="block font-bold mb-3">수정 날짜 / 시간</label>
          <InputText id="updatedAt" type="text" v-model="updaterDate" class="w-full" readonly />
        </div>
        <div>
          <label for="content" class="block font-bold mb-3">내용</label>
          <textarea id="content" v-model="newNotice.content" required="true" class="w-full p-2 border border-gray-300 rounded"
            rows="5"></textarea>
        </div>
      </div>

      <template #footer>
        <Button label="취소" icon="pi pi-times" text class="p-button-text" @click="closeAddDialog" />
        <Button label="수정" icon="pi pi-check" class="p-button-primary" @click="saveNotice" />
      </template>
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
import { useAuthStore } from '@/stores/authStore';
import axios from 'axios';
import { format } from 'date-fns';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import InputText from 'primevue/inputtext';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchCategories } from './service/adminNoticeCategoryService';
import { deleteNotice, fetchNotices } from './service/adminNoticeService';

const authStore = useAuthStore();
const notices = ref([]);
const filteredNotices = ref([]);
const categories = ref([]);
const selectedCategory = ref(null);
const globalFilter = ref('');
const displayAddDialog = ref(false);
const displayDeleteConfirmDialog = ref(false);
const newNotice = ref({ title: '', employeeName: '', categoryName: '', createAt: '', updaterName: '', updateAt: '', content: '' });
const updaterDate = ref(''); // 수정 시간 관리를 위한 ref
const isEditMode = ref(false);
const selectedNotice = ref(null); // 선택된 공지사항
const router = useRouter();

// 공지사항 데이터 필터링
const filterNotices = () => {
  filteredNotices.value = notices.value.filter(notice => {
    const matchesCategory = !selectedCategory.value ||
      selectedCategory.value.code === '전체' ||
      notice.categoryId === selectedCategory.value.id; // category_id 기준

    const matchesGlobalFilter = notice.title.includes(globalFilter.value) ||
      notice.employeeName.includes(globalFilter.value);

    return matchesCategory && matchesGlobalFilter;
  });
};

// 공지사항 목록
onMounted(async () => {
  try {
    notices.value = await fetchNotices();
    categories.value = await fetchCategories(); // 카테고리 데이터 가져오기
    filterNotices();
  } catch (error) {
    console.error('Error fetching notices:', error);
  }
});

// 날짜 포맷팅
const formatDateTime = (dateString) => {
  if (!dateString) return ''; // 유효성 검사: dateString이 없을 경우 빈 문자열 반환

  const date = new Date(dateString);

  // 잘못된 날짜 처리
  if (isNaN(date.getTime())) {
    console.error('Invalid date string:', dateString);
    return ''; // 잘못된 날짜일 경우 빈 문자열 반환
  }

  return format(date, 'yyyy-MM-dd HH:mm:ss'); // HH:mm:ss로 수정
};

const updaterInterval = ref(null); // Interval을 저장할 변수

// 공지사항 수정
const editNotice = (notice) => {
  isEditMode.value = true; // 수정 모드로 전환
  selectedNotice.value = notice; // 선택된 공지사항 설정
  newNotice.value = {
    title: notice.title, // 제목
    employeeName: notice.employeeName, // 작성자
    categoryName: categories.value.find(category => category.id === notice.categoryId), // 카테고리 객체로 설정
    createdAt: formatDateTime(notice.createdAt), // 작성 날짜
    updaterName: authStore.employeeData.employeeName, // 수정자
    content: notice.content, // 내용
  };

  // 수정 시간 갱신
  updaterDate.value = format(new Date(), 'yyyy-MM-dd HH:mm:ss');
  
  // 1초마다 updaterDate를 업데이트하는 interval 설정
  updaterInterval.value = setInterval(() => {
    updaterDate.value = format(new Date(), 'yyyy-MM-dd HH:mm:ss');
  }, 1000);

  displayAddDialog.value = true;
};

// 다이얼로그 닫기
const closeAddDialog = () => {
  displayAddDialog.value = false;
  isEditMode.value = false;
  selectedNotice.value = null;

  // Interval 제거
  if (updaterInterval.value) {
    clearInterval(updaterInterval.value);
    updaterInterval.value = null;
  }
};

// 공지사항 삭제 확인
const confirmDeleteNotice = (notice) => {
  selectedNotice.value = notice;
  displayDeleteConfirmDialog.value = true;
};

// 공지사항 삭제
const handleDeleteNotice = async () => {
  try {
    await deleteNotice(selectedNotice.value.noticeId);
    notices.value = notices.value.filter(notice => notice.noticeId !== selectedNotice.value.noticeId);
    filterNotices(); // 삭제 후 필터링

    closeDeleteConfirmDialog(); // 다이얼로그 닫기
  } catch (error) {
    console.error('Error deleting notice:', error);
  }
};

// 공지사항 삭제 취소
const closeDeleteConfirmDialog = () => {
  displayDeleteConfirmDialog.value = false;
  selectedNotice.value = null;
};

// 공지사항 수정
const saveNotice = async () => {
  try {
    const updatedNotice = {
      title: newNotice.value.title,
      categoryId: newNotice.value.categoryName.id,
      updaterId: authStore.loginUserId,
      content: newNotice.value.content,
    };

    // noticeId를 URL에 명시적으로 포함
    const response = await axios.patch(`http://localhost:8080/api/v1/notice-service/notice/${selectedNotice.value.noticeId}`, updatedNotice);

    // 기존 로직 계속
    const index = notices.value.findIndex(n => n.noticeId === selectedNotice.value.noticeId);
    if (index !== -1) {
      notices.value.splice(index, 1, { ...selectedNotice.value, ...updatedNotice }); // 기존 공지사항을 업데이트된 공지사항으로 교체
    }

    closeAddDialog(); // 다이얼로그 닫기
  } catch (error) {
    console.error('Error updating notice:', error);
  }
};

// 페이지 이동
const showWriteNoticePage = () => {
  router.push({ path: '/write-notice' });
};

// 공지사항 상세보기
const showNoticeDetail = (noticeId) => {
  router.push({ path: `/notice/${noticeId}` });
};

// 컴포넌트가 제거되기 전에 인터벌 정리
onBeforeUnmount(() => {
  if (updaterInterval.value) {
    clearInterval(updaterInterval.value);
  }
});
</script>
