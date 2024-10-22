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

      <!-- <div class="input-group">
        <h3 class="input-title">수정자</h3>
        <input
          type="text"
          :value="!isEditMode ? editableNotice.updaterName : authStore.employeeData.employeeName"
          class="message-input"
          readonly
        />
      </div> -->

      <div class="input-group">
        <h3 class="input-title">내 용</h3>
        <div v-if="isEditMode" ref="editor" class="message-editor edit-mode"></div> <!-- 수정됨 -->
        <div v-else v-html="editableNotice.content" class="message-content"></div>
      </div>

      <div class="button-group">
        <Button v-if="!isEditMode && isAdmin()" label="수정" icon="pi pi-pencil" class="p-button-primary" @click="toggleEditMode" />
        <Button v-if="isEditMode" label="저장" icon="pi pi-save" class="p-button-primary" @click="saveNotice" />
        <Button v-if="isEditMode" label="취소" icon="pi pi-times" class="p-button-secondary" @click="cancelEdit" />
        <Button v-if="isAdmin()" label="삭제" icon="pi pi-trash" class="p-button-danger" @click="showDeleteDialog" />
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
const originalNotice = ref({}); // 초기 상태를 저장할 변수 추가

// 관리자인지 확인
const isAdmin = () => {
    // return authStore.employeeData.isAdmin === 'ROLE_ADMIN';
  return true;
};

const formatDateTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return format(date, 'yyyy년 MM월 dd일 HH시 mm분 ss초');
};

// Quill 에디터 초기화
const initializeEditor = async () => {
  await nextTick();

  if (!editor.value) {
    console.error('Quill container를 찾을 수 없습니다.');
    return;
  }

  if (!quillInstance.value) {
    quillInstance.value = new Quill(editor.value, {
      theme: 'snow',
      modules: {
        toolbar: isEditMode.value ? [
          [{ header: [1, 2, false] }],
          ['bold', 'italic', 'underline'],
          ['link', 'image'],
          [{ list: 'ordered' }, { list: 'bullet' }]
        ] : false,
      },
    });
  }

  quillInstance.value.enable(isEditMode.value);
  quillInstance.value.root.innerHTML = editableNotice.value.content || '';
};

// 수정 모드 토글 함수
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value;
  router.replace({ query: { edit: isEditMode.value ? 'true' : 'false' } }); // URL에 edit 파라미터 추가

  nextTick(() => {
    initializeEditor();
    const editorElement = editor.value;
    if (isEditMode.value) {
      editorElement.classList.remove('quill-editor-disabled');
    } else {
      editorElement.classList.add('quill-editor-disabled');
    }
  });
};

// 수정 취소
const cancelEdit = async () => {
  isEditMode.value = false;

  // 공지사항 내용을 원래 상태로 복원
  editableNotice.value = { ...originalNotice.value };

  // URL의 edit 파라미터를 업데이트하지 않고 특정 경로로 이동
  const noticeId = editableNotice.value.noticeId;
  router.replace({ path: `/manage-notices` });

  // Quill 에디터의 내용을 원래 상태로 복원 및 비활성화
  if (quillInstance.value) {
    quillInstance.value.root.innerHTML = originalNotice.value.content || '';
    quillInstance.value.enable(false); // Quill 비활성화
  }
};

// 공지사항 데이터 불러오기 및 Quill 에디터에 표시
const loadNotice = async () => {
  const noticeId = route.params.id;
  try {
    const result = await fetchNoticeById(noticeId);
    editableNotice.value = { ...result };
    originalNotice.value = { ...result }; // 원본 데이터를 저장해둠
    if (quillInstance.value) {
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
    await updateNotice(editableNotice.value);
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
  const editModeQuery = route.query.edit;
  isEditMode.value = editModeQuery === 'true'; // URL 파라미터에서 값 복구
  await loadCategories();
  if (!isNewNotice.value) await loadNotice();
  initializeEditor();
  isAdmin();
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

.message-editor.edit-mode {
  height: 600px; /* 수정 모드일 때 높이를 600px로 설정 */
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

.quill-editor-disabled {
  pointer-events: none; /* 퀼 에디터를 비활성화하여 클릭을 무시함 */
  opacity: 0.5; /* 비활성화된 상태에서 약간의 불투명도를 추가 */
}

</style>
