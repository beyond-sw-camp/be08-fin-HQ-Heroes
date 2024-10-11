<template>
  <div class="app-container">
    <div class="sidebar">
      <!-- 검색 필드 -->
      <div class="search-container">
        <i class="pi pi-search search-icon"></i>
        <input type="text" placeholder="Search employees..." v-model="searchQuery" class="search-input" />
      </div>

      <!-- 사원 목록 트리 구조 -->
      <div class="panelmenu-container">
        <div class="card">
          <div class="font-semibold text-xl">사원 목록</div>
          <Tree v-model:selectionKeys="selectedKeys" :value="filteredTreeData" selectionMode="checkbox"
            class="w-full md:w-[25rem]">
            <template #default="slotProps">
              <div class="flex items-center">
                <Avatar v-if="slotProps.node.key.startsWith('emp-') && !slotProps.node.profileImageUrl" label="X"
                  size="normal" shape="circle" class="mr-2" style="background-color: #dee9fc; color: #1a2551" />
                <Avatar v-else-if="slotProps.node.key.startsWith('emp-')" :image="slotProps.node.profileImageUrl"
                  size="normal" shape="circle" class="mr-2" />
                <span>{{ slotProps.node.label }}</span>
              </div>
            </template>
          </Tree>
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
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import Tree from 'primevue/tree';
import Avatar from 'primevue/avatar';
import { fetchGet } from '../auth/service/AuthApiService'; // Replace with your API service

// State variables
const searchQuery = ref('');
const to = ref('');
const subject = ref('');
const message = ref('');
const editor = ref(null);
const employeeData = ref([]);  // Stores employee data
const selectedKeys = ref({});  // Stores selected employee IDs as key-value pairs

// Fetch hierarchical employee data on component mount
onMounted(() => {
  fetchEmployeeList();

  // Initialize Quill editor for composing messages
  const quillEditor = new Quill(editor.value, {
    theme: 'snow',
    modules: {
      toolbar: [
        [{ font: [] }, { size: [] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ color: [] }, { background: [] }],
        [{ list: 'ordered' }, { list: 'bullet' }],
        [{ align: [] }],
        ['link', 'image', 'blockquote'],
        ['clean']
      ]
    }
  });

  quillEditor.on('text-change', () => {
    message.value = quillEditor.root.innerHTML;
  });
});

async function fetchEmployeeList() {
  try {
    const data = await fetchGet('http://localhost:8080/api/v1/employee/employees');
    employeeData.value = convertToTreeModel(data);
  } catch (error) {
    console.error('Error fetching employee data:', error);
  }
}

const convertToTreeModel = (data) => {
  const departments = data.reduce((acc, employee) => {
    const dept = acc.find(d => d.label === employee.deptName);
    if (!dept) {
      acc.push({
        key: `dept-${employee.deptName}`,
        label: employee.deptName,
        icon: 'pi pi-building',
        children: [{
          key: `team-${employee.teamName}`,
          label: employee.teamName,
          icon: 'pi pi-users',
          children: []
        }]
      });
    }
    const deptIndex = acc.findIndex(d => d.label === employee.deptName);
    const team = acc[deptIndex].children.find(t => t.label === employee.teamName);
    if (!team) {
      acc[deptIndex].children.push({
        key: `team-${employee.teamName}`,
        label: employee.teamName,
        icon: 'pi pi-users',
        children: []
      });
    }
    const teamIndex = acc[deptIndex].children.findIndex(t => t.label === employee.teamName);
    acc[deptIndex].children[teamIndex].children.push({
      key: `emp-${employee.employeeId}`,
      label: employee.employeeName,
      profileImageUrl: employee.profileImageUrl,
      employeeId: employee.employeeId,
      jobName: employee.jobName,
      positionName: employee.positionName,
      joinDate: employee.joinDate
    });
    return acc;
  }, []);
  return departments;
};

const filteredTreeData = computed(() => {
  if (!searchQuery.value) return employeeData.value;

  const filterTree = (items, query) => {
    return items
      .map(item => {
        if (item.children) {
          const filteredChildren = filterTree(item.children, query);
          if (filteredChildren.length || item.label.toLowerCase().includes(query)) {
            return { ...item, children: filteredChildren };
          }
        } else if (item.label.toLowerCase().includes(query)) {
          return item;
        }
        return null;
      })
      .filter(item => item !== null);
  };

  return filterTree(employeeData.value, searchQuery.value.toLowerCase());
});

const sendMessage = () => {
  const selectedEmployeeIds = Object.keys(selectedKeys.value)
    .filter(key => key.startsWith('emp-'))
    .map(key => key.replace('emp-', ''));

  if (selectedEmployeeIds.length === 0) {
    console.log("No employees selected");
    return;
  }

  console.log(`Sending message to employees: ${selectedEmployeeIds.join(', ')}`);
  console.log(`Subject: ${subject.value}`);
  console.log(`Message: ${message.value}`);
};
</script>



<style scoped>
.app-container {
  display: flex;
  height: 90vh;
}

.sidebar {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.panelmenu-container {
  flex-grow: 1;
}

.card {
  padding: 0;
  text-align: left;
}

.font-semibold.text-xl {
  padding: 10px;
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
