<template>
  <div class="notice-detail-page">
    <div class="card">
      <label class="text-xl font-bold">공지사항 내용 {{ isEditMode ? '수정' : '보기' }}</label>

      <!-- 제목 -->
      <p><strong>제목 : </strong>
        <template v-if="isEditMode">
          <input v-model="editableNotice.title" class="input-field" />
        </template>
        <template v-else>
          {{ notice.title }}
        </template>
      </p>

      <!-- 카테고리 -->
      <p><strong>카테고리 : </strong>
        <template v-if="isEditMode">
          <select v-model="editableNotice.categoryName" class="input-field">
            <option value="GENERAL">일반</option>
            <option value="HR">인사</option>
            <option value="ATTENDANCE">출근</option>
            <option value="VACATION">휴가</option>
            <option value="EDUCATION">교육</option>
          </select>
        </template>
        <template v-else>
          {{ getCategoryLabel(notice.categoryName) }}
        </template>
      </p>

      <!-- 작성자 -->
      <p><strong>작성자 : </strong> {{ notice.employeeName }}</p>
      <p><strong>작성 날짜 : </strong> {{ formatDateTime(notice.createdAt) }}</p>

      <!-- 수정자 -->
      <p><strong>수정자 : </strong> {{ notice.updaterName }}</p>
      <p><strong>수정 시간 : </strong> {{ notice.updateAt ? formatDateTime(notice.updateAt) : '' }}</p>

      <!-- 내용 -->
      <p><strong>내용 : </strong></p>
      <div class="content-scroll">
        <template v-if="isEditMode">
          <textarea v-model="editableNotice.content" class="textarea-field"></textarea>
        </template>
        <template v-else>
          {{ notice.content }}
        </template>
      </div>

      <div class="button-group">
        <Button v-if="isEditMode" label="저장" icon="pi pi-check" class="p-button-success" @click="saveNotice" />
        <Button v-if="isEditMode" label="취소" icon="pi pi-times" class="p-button-secondary" @click="cancelEdit" />
        <Button v-if="!isEditMode" label="수정" icon="pi pi-pencil" class="p-button-warning" @click="enableEditMode" />
        <Button label="삭제" icon="pi pi-trash" class="p-button-danger" @click="handleDeleteNotice" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { deleteNotice, fetchNoticeById, updateNotice } from './service/adminNoticeService';

const route = useRoute();
const router = useRouter();
const notice = ref({});
const editableNotice = ref({}); // 수정 가능한 데이터
const originalNotice = ref({}); // 원본 공지사항 데이터
const isEditMode = ref(false); // 수정 모드 상태
const authStore = useAuthStore(); // 로그인 정보 가져오기

// 공지사항 ID로 데이터 가져오기
const loadNotice = async () => {
  const noticeId = route.params.id;
  try {
    const result = await fetchNoticeById(noticeId);
    notice.value = result;
    editableNotice.value = { ...result }; // 수정 가능 데이터 초기화
    originalNotice.value = { ...result }; // 원본 데이터 저장
  } catch (error) {
    console.error('Error fetching notice:', error);
  }
};

// 카테고리 라벨 변환 함수
const getCategoryLabel = (category) => {
  const categories = {
    'GENERAL': '일반',
    'HR': '인사',
    'ATTENDANCE': '출근',
    'VACATION': '휴가',
    'EDUCATION': '교육'
  };
  return categories[category] || category;
};

// 수정 모드 활성화
const enableEditMode = () => {
  isEditMode.value = true;
};

// 수정 저장 처리
const saveNotice = async () => {
  try {
    editableNotice.value.updaterName = authStore.employeeData.name; // authStore에서 이름 가져오기
    editableNotice.value.updateAt = new Date().toISOString();
    await updateNotice(editableNotice.value);
    notice.value = { ...editableNotice.value };
    originalNotice.value = { ...editableNotice.value }; // 수정 후 원본 데이터 갱신
    isEditMode.value = false;
  } catch (error) {
    console.error('Error updating notice:', error);
  }
};

// 수정 취소 처리
const cancelEdit = () => {
  editableNotice.value = { ...originalNotice.value }; // 원본 데이터로 복원
  isEditMode.value = false; // 수정 모드 해제
};

// 삭제 처리
const handleDeleteNotice = async () => {
  if (!notice.value.id) return;
  try {
    await deleteNotice(notice.value.id);
    router.push({ name: 'ManageNoticePage' });
  } catch (error) {
    console.error('Error deleting notice:', error);
  }
};

// 날짜 포맷팅 함수
const formatDateTime = (dateString) => {
  if (!dateString) return ''; // 유효하지 않은 경우 빈 문자열 반환
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }) + ' ' + date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
};

// 공지사항 로드
onMounted(loadNotice);
</script>

<style scoped>
.card {
  padding: 2rem;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.card p {
  margin-top : 2rem;
}

.input-field, .textarea-field {
  width: 100%;
  padding: 0.5rem;
  margin-top: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.content-scroll {
  max-height: 300px;
  overflow-y: auto;
  padding: 1rem 0;
}

.button-group {
  margin-top: 1rem;
  display: flex;
  justify-content: flex-end;
}
</style>
