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

      <DataTable
        :value="filteredNotices"
        :globalFilterFields="['title', 'categoryName', 'employeeName']"
        paginator
        :rows="10"
        removableSort
        :rowsPerPageOptions="[10, 20, 30, 50]"
        dataKey="id"
        style="min-height: 400px;"
        @row-click="(event) => showNoticeDetail(event.data.noticeId)"
        :metaKeySelection="false"
      >
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
          <InputText id="employeeName" v-model="newNotice.employeeName" readonly class="w-full" />
        </div>
        <div>
          <label for="category" class="block font-bold mb-3">카테고리</label>
          <Dropdown
            v-model="newNotice.categoryName"
            :options="categories"
            optionLabel="name"
            placeholder="카테고리 선택"
            class="w-full"
          />
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
          <InputText id="updatedAt" type="text" v-model="newNotice.updateAt" class="w-full" readonly />
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
import { format } from 'date-fns';
import Button from 'primevue/button';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dropdown from 'primevue/dropdown';
import InputGroup from 'primevue/inputgroup';
import InputGroupAddon from 'primevue/inputgroupaddon';
import InputText from 'primevue/inputtext';
import { onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router'; // 라우터 가져오기
import { fetchCategories } from './service/adminNoticeCategoryService';
import { createNotice, deleteNotice, fetchNotices, updateNotice } from './service/adminNoticeService';

const authStore = useAuthStore();
const notices = ref([]);
const filteredNotices = ref([]);
const categories = ref([]);
const selectedCategory = ref(null);
const globalFilter = ref('');
const displayAddDialog = ref(false);
const displayDeleteConfirmDialog = ref(false);
const newNotice = ref({ title: '', employeeName: '', categoryName: '', createAt: '', updaterName: '', updateAt: '', content: '' });
const isEditMode = ref(false);
const selectedNotice = ref(null); // 선택된 공지사항을 저장할 변수
const router = useRouter(); // 라우터 인스턴스 생성

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
    updaterDate: format(new Date(), 'yyyy-MM-dd HH:mm:ss'), // 현재 날짜 및 시간
    content: notice.content, // 내용
  };

  // 1초마다 updaterDate를 업데이트하는 interval 설정
  updaterInterval.value = setInterval(() => {
    newNotice.value.updaterDate = format(new Date(), 'yyyy-MM-dd HH:mm:ss');
  }, 1000);

  displayAddDialog.value = true; // 모달 대화상자 열기
};

// 컴포넌트 언마운트 시 interval 해제
onBeforeUnmount(() => {
  clearInterval(updaterInterval.value);
});


// 공지사항 삭제 확인 대화상자 열기
const confirmDeleteNotice = (notice) => {
  selectedNotice.value = notice; // 삭제할 공지사항 설정
  displayDeleteConfirmDialog.value = true; // 삭제 확인 대화상자 표시
};

// 공지사항 삭제 처리
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

// 모달 닫기
const closeAddDialog = () => {
  displayAddDialog.value = false;
  newNotice.value = { title: '', employeeName: '', categoryName: '', date: '', content: '' }; // 입력값 초기화
  isEditMode.value = false; // 수정 모드 초기화
};

// 상세 페이지로 이동
const showNoticeDetail = (noticeId) => {
  router.push({ name: 'NoticeDetailPage', params: { id: noticeId } });
};

</script>
