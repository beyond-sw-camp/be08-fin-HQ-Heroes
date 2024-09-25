<template>
    <div class="notice-management severance-pay-container">
        <div class="header">
            <h2 class="font-semibold text-xl mb-4">공지사항 페이지</h2>
            <Button label="공지사항 추가" @click="showAddNoticeModal" class="p-button-primary" />
        </div>
        <div class="notice-list">
            <div v-if="notices.length">
                <div v-for="notice in paginatedNotices" :key="notice.id" class="notice-card" @click="viewNotice(notice)">
                    <Card>
                        <template #header>
                            <div class="notice-header">
                                <h3>{{ notice.title }}</h3>
                                <div class="notice-actions">
                                    <Button label="수정" @click.stop="editNotice(notice)" class="p-button-secondary" />
                                    <Button label="삭제" @click.stop="confirmDelete(notice.id)" class="p-button-danger" />
                                </div>
                            </div>
                            <p class="notice-date">{{ notice.date }}</p>
                        </template>
                        <div class="notice-content">
                            <p>{{ notice.content }}</p>
                        </div>
                    </Card>
                </div>

                <!-- Pagination Controls -->
                <div class="pagination">
                    <Button 
                        label="이전" 
                        :disabled="currentPage === 1" 
                        @click="prevPage" 
                        class="p-button-secondary" 
                    />
                    <span>페이지 {{ currentPage }} / {{ totalPages }}</span>
                    <Button 
                        label="다음" 
                        :disabled="currentPage === totalPages" 
                        @click="nextPage" 
                        class="p-button-secondary" 
                    />
                </div>
            </div>
            <div v-else>
                <p class="no-notices">등록된 공지사항이 없습니다.</p>
            </div>
        </div>

        <!-- Modal for viewing notice details -->
        <Dialog 
            header="공지사항 상세" 
            :visible="viewingModal" 
            :style="{ width: '50vw' }" 
            @update:visible="onUpdateViewingModal"
        >
            <div class="modal-content">
                <h3>{{ selectedNotice.title }}</h3>
                <p class="notice-date">{{ selectedNotice.date }}</p>
                <p>{{ selectedNotice.content }}</p>
                <Button label="닫기" @click="viewingModal = false" class="p-button-secondary" />
            </div>
        </Dialog>

        <Dialog 
            :header="isEditing ? '공지사항 수정' : '공지사항 등록'" 
            :visible="displayModal" 
            :style="{ width: '50vw' }" 
            @update:visible="onUpdateVisible" 
        >
            <div class="modal-content">
                <InputText v-model="selectedNotice.title" placeholder="제목" class="input-field" />
                <InputTextarea v-model="selectedNotice.content" placeholder="내용" rows="5" class="input-field" />
                <Button label="저장" @click="saveNotice" class="p-button-primary" />
            </div>
        </Dialog>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import Card from 'primevue/card';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import InputTextarea from 'primevue/inputtext';
import Dialog from 'primevue/dialog';

const notices = ref([
    { id: 1, title: '인사 정책 변경 안내', content: '2024년부터 시행되는 새로운 인사 정책에 대한 안내입니다. 모든 직원은 필독해 주시기 바랍니다.', date: '2024-09-01' },
    { id: 2, title: '2024 연례 성과 평가 일정', content: '2024년 연례 성과 평가가 10월 1일부터 10월 15일까지 진행됩니다. 자세한 사항은 인사팀에 문의해 주세요.', date: '2024-09-15' },
    { id: 3, title: '신입사원 오리엔테이션', content: '신입사원을 위한 오리엔테이션이 2024년 9월 30일에 진행됩니다. 모든 신입사원은 참석 필수입니다.', date: '2024-09-20' },
    { id: 4, title: '재택근무 정책 안내', content: '재택근무 정책이 변경되었습니다. 모든 직원은 변경 사항을 숙지하시기 바랍니다.', date: '2024-09-22' },
    { id: 5, title: '2024년 복리후생 혜택 안내', content: '2024년 복리후생 혜택에 대한 자세한 안내를 드립니다. 모든 직원은 혜택을 잘 활용하시기 바랍니다.', date: '2024-09-23' },
    { id: 6, title: '직원 교육 프로그램 모집', content: '신규 직원 교육 프로그램에 참여할 직원을 모집합니다. 관심 있는 직원은 인사팀에 신청해 주세요.', date: '2024-09-24' },
]);

const displayModal = ref(false);
const viewingModal = ref(false);
const selectedNotice = ref({ title: '', content: '', date: '' });
const currentPage = ref(1);
const itemsPerPage = 4;
const isEditing = ref(false);

const totalPages = computed(() => Math.ceil(notices.value.length / itemsPerPage));
const paginatedNotices = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    return notices.value.slice(start, start + itemsPerPage);
});

const showAddNoticeModal = () => {
    selectedNotice.value = { title: '', content: '', date: new Date().toISOString().split('T')[0] };
    isEditing.value = false;
    displayModal.value = true;
};

const editNotice = (notice) => {
    selectedNotice.value = { ...notice };
    isEditing.value = true;
    displayModal.value = true;
};

const viewNotice = (notice) => {
    selectedNotice.value = { ...notice };
    viewingModal.value = true;
};

const confirmDelete = (id) => {
    if (confirm('정말 삭제하시겠습니까?')) {
        deleteNotice(id);
    }
};

const deleteNotice = (id) => {
    notices.value = notices.value.filter(notice => notice.id !== id);
};

const saveNotice = () => {
    if (isEditing.value) {
        const index = notices.value.findIndex(n => n.id === selectedNotice.value.id);
        if (index !== -1) {
            notices.value[index] = { ...selectedNotice.value };
        }
    } else {
        selectedNotice.value.id = Date.now();
        notices.value.push({ ...selectedNotice.value });
    }
    displayModal.value = false;
};

const onUpdateVisible = (value) => {
    displayModal.value = value;
};

const onUpdateViewingModal = (value) => {
    viewingModal.value = value;
};

const prevPage = () => {
    if (currentPage.value > 1) {
        currentPage.value--;
    }
};

const nextPage = () => {
    if (currentPage.value < totalPages.value) {
        currentPage.value++;
    }
};
</script>

<style scoped>
.notice-management {
    padding: 2rem;
}

.severance-pay-container {
    background-color: #ffffff;
    width: 100%;
    max-width: 100%;
    padding: 20px 40px;
    border-radius: 10px;
    margin: 0 auto;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.notice-list {
    margin-top: 2rem;
}

.notice-card {
    margin-bottom: 2rem;
    transition: transform 0.2s;
    cursor: pointer;
}

.notice-card:hover {
    transform: translateY(-5px);
}

.notice-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-left: 1rem;
}

.notice-actions {
    display: flex;
    gap: 0.5rem;
    margin-top: 1rem;
    margin-right: 1rem;
}

.notice-date {
    font-size: 0.9rem;
    color: #777;
    margin-left: 1rem;
}

.notice-content {
    padding: 1rem 0;
}

.no-notices {
    text-align: center;
    color: #777;
}

.p-button-danger {
    margin-left: 0.5rem;
}

.modal-content {
    padding: 1.5rem;
}

.input-field {
    width: 100%;
    margin-bottom: 1rem;
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 1rem;
    gap: 1rem;
}
</style>
