<template>
  <div class="app-container">
    <div class="sidebar">
      <!-- 검색 필드 -->
      <div class="search-container">
        <i class="pi pi-search search-icon"></i>
        <input type="text" placeholder="Search users..." v-model="searchQuery" class="search-input" />
      </div>

      <!-- 사용자 목록 트리 구조 -->
      <div class="tree-container">
        <div class="card">
          <div class="font-semibold text-xl">사원 목록</div>
          <Tree :value="filteredTreeData" selectionMode="checkbox" v-model:selectionKeys="selectedTreeValue"></Tree>
        </div>
      </div>
    </div>

    <!-- 메시지 작성 영역 -->
    <div class="notification-container">
      <div class="compose-message-container">
        <div class="compose-message">
          <h3 class="mb-2"><b>발송자</b></h3>
          <input type="text" v-model="to" class="to-input" />
          <h3 class="mb-2 mt-5"><b>제목</b></h3>
          <input type="text" v-model="subject" class="message-input" />
          <h3 class="mb-2 mt-5"><b>메시지</b></h3>
          <!-- Quill 에디터 -->
          <div ref="editor" class="message-editor"></div>
          <div class="button-container">
            <button @click="sendMessage" class="send-button">알림 발송</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { NodeService } from '@/service/NodeService'; // Assuming this is your data service
import Quill from 'quill';
import 'quill/dist/quill.snow.css'; // Quill의 스타일

const treeValue = ref(null);
const selectedTreeValue = ref(null);
const searchQuery = ref('');
const to = ref('');
const subject = ref('');
const message = ref('');
const editor = ref(null); // Quill 에디터를 참조할 변수

// Load tree nodes data on mount
onMounted(() => {
  NodeService.getTreeNodes().then((data) => (treeValue.value = data));

  // Initialize Quill editor
  const quillEditor = new Quill(editor.value, {
    theme: 'snow', // Quill의 '' 테마 사용
    modules: {
      toolbar: [
        [{ font: [] }, { size: [] }], // 글꼴 및 글자 크기 선택
        ['bold', 'italic', 'underline', 'strike'], // 텍스트 스타일
        [{ color: [] }, { background: [] }], // 텍스트 색상 및 배경색 선택
        [{ list: 'ordered' }, { list: 'bullet' }], // 목록
        [{ align: [] }], // 정렬
        ['link', 'image', 'blockquote'], // 링크, 이미지, 인용구
        ['clean'] // 서식 제거
      ]
    }
  });

  quillEditor.on('text-change', () => {
    message.value = quillEditor.root.innerHTML; // Quill 에디터 내용이 변경될 때 message 값 업데이트
  });
});

const filteredTreeData = computed(() => {
  if (!searchQuery.value) return treeValue.value;

  const filterTree = (nodes, query) => {
    return nodes
      .map((node) => {
        if (node.children) {
          const filteredChildren = filterTree(node.children, query);
          if (filteredChildren.length || node.label.toLowerCase().includes(query)) {
            return { ...node, children: filteredChildren };
          }
        } else if (node.label.toLowerCase().includes(query)) {
          return node;
        }
        return null;
      })
      .filter((node) => node !== null);
  };

  return filterTree(treeValue.value, searchQuery.value.toLowerCase());
});

const sendMessage = () => {
  console.log(`Sending message to: ${to.value}, Subject: ${subject.value}, Message: ${message.value}`);
};
</script>

<style scoped>
.app-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 25%;
  height: 90vh;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.tree-container {
  flex-grow: 1;
  margin: 0;
}

.card {
  padding: 0;
  text-align: left;
}

.font-semibold.text-xl {
  padding: 10px;
}

.tree-container .pi-tree {
  text-align: left;
}

.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20%;
  padding-bottom: 10px;
}

.user-profile::after {
  content: '';
  display: block;
  width: 100%;
  height: 2px;
  background-color: #ddd;
  margin: 10px 0;
}

.search-container {
  position: relative;
}

.search-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
}

.search-input {
  padding: 10px 10px 10px 40px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  width: 100%;
}

.notification-container {
  width: 75%;
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  height: 90vh;
}

.compose-message-container {
  flex-grow: 1;
  background-color: #ffffff;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.to-input {
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 5px;
  width: 20%;
}

.message-input {
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 5px;
  width: 100%;
}

.message-editor {
  flex-grow: 1;
  font-size: 16px;
  height: 57vh;
  overflow-y: auto;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.button-container {
  display: flex;
  justify-content: flex-end;
}

.send-button {
  padding: 10px 20px;
  margin-top: 10px;
  background-color: #6366F1;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.2s;
}

.send-button:hover {
  background-color: #4f46e5;
}
</style>
