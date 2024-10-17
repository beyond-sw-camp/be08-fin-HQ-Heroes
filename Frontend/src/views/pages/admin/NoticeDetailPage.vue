<template>
  <div class="notice-detail-page">
    <div class="card">
      <label class="text-xl font-bold" style="margin-bottom: 4rem;">공지사항 내용</label>

      <div class="input-group">
        <h3 class="input-title">제 목</h3>
        <input
          type="text"
          v-model="editableNotice.title"
          class="message-input"
          :readonly="!isEditMode"
          placeholder="제목을 입력하세요"
        />
      </div>

      <div class="input-group">
        <h3 class="input-title">작성시간</h3>
        <input
          type="text"
          :value="formatDateTime(editableNotice.createdAt)"
          class="message-input"
          readonly
        />
      </div>

      <div class="input-group">
        <h3 class="input-title">작성자</h3>
        <input
          type="text"
          v-model="editableNotice.employeeName"
          class="message-input"
          :readonly="!isEditMode"
        />
      </div>

      <div class="input-group">
        <h3 class="input-title">카테고리</h3>
        <select v-model="editableNotice.categoryName" class="message-input" :disabled="!isEditMode">
          <option v-for="category in categories" :key="category.categoryId" :value="category.categoryName">
            {{ category.categoryName }}
          </option>
        </select>
      </div>

      <div class="input-group">
        <h3 class="input-title">수정자</h3>
        <input
          type="text"
          :value="!isEditMode ? editableNotice.updaterName : authStore.employeeData.employeeName"
          class="message-input"
          readonly
        />
      </div>

      <div class="input-group">
        <h3 class="input-title">수정시간</h3>
        <input
          type="text"
          :value="!isEditMode ? formatDateTime(editableNotice.updateAt) : formatDateTime(new Date())"
          class="message-input"
          readonly
        />
      </div>

      <div class="input-group">
        <h3 class="input-title">내 용</h3>
        <div v-if="isEditMode" ref="editor" class="message-editor"></div>
        <div v-else v-html="editableNotice.content" class="message-content"></div>
      </div>

      <div class="button-group">
        <Button v-if="!isEditMode" label="수정" icon="pi pi-pencil" class="p-button-primary" @click="enableEditMode" />
        <Button v-if="isEditMode" label="저장" icon="pi pi-save" class="p-button-primary" @click="saveNotice" />
        <Button v-if="isEditMode" label="취소" icon="pi pi-times" class="p-button-secondary" @click="cancelEdit" />
        <Button label="삭제" icon="pi pi-trash" class="p-button-danger" @click="showDeleteDialog" />
      </div>
    </div>

    <Dialog
      header="삭제 확인"
      v-model:visible="deleteDialogVisible"
      :modal="true"
      :style="{ width: '30vw' }"
      @hide="deleteDialogVisible = false"
    >
      <p>정말로 이 공지사항을 삭제하시겠습니까?</p>
      <template #footer>
        <Button label="취소" icon="pi pi-times" @click="deleteDialogVisible = false" />
        <Button label="삭제" icon="pi pi-trash" @click="handleDeleteNotice" class="p-button-danger" />
      </template>
    </Dialog>

  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { format } from 'date-fns';
import Quill from 'quill';
import { nextTick, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchCategories } from './service/adminNoticeCategoryService';
import { deleteNotice, fetchNoticeById, updateNotice } from './service/adminNoticeService';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const editableNotice = ref({});
const isEditMode = ref(false);
const isNewNotice = ref(!route.params.id);
const categories = ref([]);
const editor = ref(null);
const quillInstance = ref(null);
const deleteDialogVisible = ref(false); // 삭제 확인 다이얼로그 상태

const formatDateTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return format(date, 'yyyy년 MM월 dd일 HH시 mm분 ss초');
};

// Quill 에디터 초기화 함수
const initializeEditor = () => {
  if (!editor.value) return; // editor가 존재하는지 확인

  quillInstance.value = new Quill(editor.value, {
    theme: 'snow',
    readOnly: !isEditMode.value,
    modules: {
      toolbar: isEditMode.value
        ? [
            [{ header: [1, 2, false] }],
            ['bold', 'italic', 'underline'],
            ['link', 'image'],
            [{ list: 'ordered' }, { list: 'bullet' }],
          ]
        : false, // 툴바가 비활성화될 때는 false로 설정
    },
  });
  // 에디터 초기화 후, 기존 내용 설정
  if (editableNotice.value.content) {
    quillInstance.value.setContents(quillInstance.value.clipboard.convert(editableNotice.value.content));
  }

  if (!isEditMode.value && quillInstance.value) {
    quillInstance.value.disable(); // 비활성화 상태로 설정
    quillInstance.value.enable(false); // 툴바 비활성화
  }
};

// 수정 모드 활성화
const enableEditMode = () => {
  isEditMode.value = true;
  nextTick(() => {
    initializeEditor();
    quillInstance.value.enable(true);
  });
};

// 공지사항 데이터 불러오기 및 Quill 에디터에 표시
const loadNotice = async () => {
  const noticeId = route.params.id;
  try {
    const result = await fetchNoticeById(noticeId);
    editableNotice.value = { ...result };
    if (quillInstance.value) {
      // 에디터에 기존 데이터 설정
      nextTick(() => {
        quillInstance.value.setContents(quillInstance.value.clipboard.convert(result.content));
      });
    }
  } catch (error) {
    console.error('공지사항 조회 오류:', error);
  }
};

// 공지사항 저장
const saveNotice = async () => {
  editableNotice.value.content = quillInstance.value.root.innerHTML; // 에디터 내용 저장
  try {
    if (isNewNotice.value) {
      await createNotice(editableNotice.value);
    } else {
      await updateNotice(editableNotice.value);
    }
    router.push('/manage-notices');
  } catch (error) {
    console.error('공지사항 저장 오류:', error);
  }
};

const loadCategories = async () => {
  try {
    const result = await fetchCategories();
    categories.value = result;
  } catch (error) {
    console.error('카테고리 조회 오류:', error);
  }
};

// 수정 취소
const cancelEdit = () => {
  isEditMode.value = false; // 수정 모드 비활성화
  if (quillInstance.value) {
    quillInstance.value.disable(); // 에디터 비활성화
    console.log(editableNotice.content);
    quillInstance.value.enable(false); // 툴바 비활성화
  }
  loadNotice(); // 기존 데이터로 복원
};

// 삭제 확인 다이얼로그 표시
const showDeleteDialog = () => {
  deleteDialogVisible.value = true;
};

// 공지사항 삭제
const handleDeleteNotice = async () => {
  try {
    await deleteNotice(editableNotice.value.noticeId);
    deleteDialogVisible.value = false; // 다이얼로그 닫기
    router.push('/manage-notices');
  } catch (error) {
    console.error('공지사항 삭제 오류:', error);
  }
};

// 컴포넌트가 마운트될 때 실행
onMounted(async () => {
  await loadCategories();
  if (!isNewNotice.value) await loadNotice(); // 데이터 로드
  initializeEditor(); // 에디터 초기화
});
</script>

<style scoped>
.notice-detail-page {
  display: flex;
  justify-content: center;
}

.card {
  width: 100%;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.input-group {
  margin-bottom: 1.5rem;
}

.input-title {
  margin-top: 1rem;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.message-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.message-editor {
  height: 200px;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 0.75rem;
  overflow-y: auto;
}

.message-content {
  min-height: 300px;
  padding: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  word-wrap: break-word;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1rem;
}
</style>
