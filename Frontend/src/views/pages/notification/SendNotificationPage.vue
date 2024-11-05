<template>
    <div class="app-container">
        <div class="sidebar">
            <!-- 검색 필드 -->
            <div class="search-container">
                <i class="pi pi-search search-icon"></i>
                <input type="text" placeholder="검색어를 입력해주세요." v-model="searchQuery" class="search-input" @keyup.enter="onSearchEnter" />
            </div>

            <!-- 사원 목록 트리 구조 -->
            <div class="panelmenu-container">
                <div class="card">
                    <div class="font-semibold text-xl">사원 목록</div>
                    <Tree v-model:selectionKeys="selectedKeys" :value="filteredTreeData" :expandedKeys="expandedKeys" selectionMode="checkbox" class="w-full md:w-[20rem]">
                        <template #default="slotProps">
                            <div class="flex items-center">
                                <Avatar v-if="slotProps.node.key.startsWith('emp-') && !slotProps.node.profileImageUrl" label="X" size="normal" shape="circle" class="mr-2" style="background-color: #dee9fc; color: #1a2551" />
                                <Avatar v-else-if="slotProps.node.key.startsWith('emp-')" :image="slotProps.node.profileImageUrl" size="normal" shape="circle" class="mr-2" />
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
                    <h3 class="mb-2 text-xl"><b>발송자:</b> {{ authStore.employeeData.teamName }} {{ authStore.employeeData.employeeName }}</h3>
                    <hr class="divider" />

                    <!-- Notification Category Dropdown -->
                    <h3 class="mb-2 mt-5 text-xl"><b>카테고리</b></h3>
                    <Select v-model="selectedCategory" :options="categories" optionLabel="categoryName" placeholder="카테고리를 선택하세요" />

                    <h3 class="mb-2 mt-5 text-xl"><b>메시지</b></h3>
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
import { useAuthStore } from '@/stores/authStore';
import Avatar from 'primevue/avatar';
import Select from 'primevue/select';
import Tree from 'primevue/tree';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import { computed, onMounted, ref } from 'vue';
import { fetchGet, fetchPost } from '../auth/service/AuthApiService';

const authStore = useAuthStore();
const searchQuery = ref(''); // 검색어 상태
const message = ref('');
const editor = ref(null);
const employeeData = ref([]); // 전체 사원 데이터를 저장
const selectedKeys = ref({}); // 선택된 노드 상태
const expandedKeys = ref({}); // 트리의 확장 상태를 관리하는 ref
const categories = ref([]);
const selectedCategory = ref('');

import Swal from 'sweetalert2';
import { watch } from 'vue';

// 검색어가 변경될 때마다 트리를 확장
watch(searchQuery, (newQuery) => {
    if (newQuery.trim()) {
        expandMatchingNodes(filteredTreeData.value, newQuery.toLowerCase());
    } else {
        expandedKeys.value = {}; // 검색어가 없을 경우 트리 확장 초기화
    }
});

onMounted(() => {
    fetchEmployeeList();
    fetchCategories(); // Fetch notification categories

    const quillEditor = new Quill(editor.value, {
        theme: 'snow',
        modules: {
            toolbar: [[{ font: [] }, { size: [] }], ['bold', 'italic', 'underline', 'strike'], [{ color: [] }, { background: [] }], [{ list: 'ordered' }, { list: 'bullet' }], [{ align: [] }], ['link', 'blockquote'], ['clean']]
        }
    });

    quillEditor.on('text-change', () => {
        message.value = quillEditor.root.innerHTML;
    });
});

async function fetchEmployeeList() {
    try {
        const loggedInEmployeeId = window.localStorage.getItem('employeeId'); // 로그인한 사용자의 employeeId 가져오기
        const data = await fetchGet('https://hq-heroes-api.com/api/v1/employee/employees');

        // 로그인한 사용자를 제외한 데이터만 필터링
        const filteredData = data.filter((employee) => employee.employeeId !== loggedInEmployeeId);

        employeeData.value = convertToTreeModel(filteredData);
    } catch (error) {
        console.error('Error fetching employee data:', error);
    }
}

async function fetchCategories() {
    try {
        const response = await fetchGet('https://hq-heroes-api.com/api/v1/notification-service/categories');
        categories.value = response;
    } catch (error) {
        console.error('Error fetching categories:', error);
    }
}

const convertToTreeModel = (data) => {
    const departments = data.reduce((acc, employee) => {
        const dept = acc.find((d) => d.label === employee.deptName);
        if (!dept) {
            acc.push({
                key: `dept-${employee.deptName}`,
                label: employee.deptName,
                icon: 'pi pi-building',
                children: [
                    {
                        key: `team-${employee.teamName}`,
                        label: employee.teamName,
                        icon: 'pi pi-users',
                        children: []
                    }
                ]
            });
        }
        const deptIndex = acc.findIndex((d) => d.label === employee.deptName);
        const team = acc[deptIndex].children.find((t) => t.label === employee.teamName);
        if (!team) {
            acc[deptIndex].children.push({
                key: `team-${employee.teamName}`,
                label: employee.teamName,
                icon: 'pi pi-users',
                children: []
            });
        }
        const teamIndex = acc[deptIndex].children.findIndex((t) => t.label === employee.teamName);
        acc[deptIndex].children[teamIndex].children.push({
            key: `emp-${employee.employeeId}`,
            label: employee.employeeName,
            profileImageUrl: employee.profileImageUrl,
            employeeId: employee.employeeId,
            jobRoleName: employee.jobRoleName,
            positionName: employee.positionName,
            joinDate: employee.joinDate
        });
        return acc;
    }, []);
    return departments;
};

// 검색 필터링
const filteredTreeData = computed(() => {
    // 검색어가 없을 경우 전체 데이터를 반환하여 초기화
    if (!searchQuery.value.trim()) {
        expandedKeys.value = {}; // 트리 확장 상태 초기화
        return employeeData.value; // 전체 데이터를 반환
    }

    const filterTree = (items, query) => {
        const queryLower = query.toLowerCase();

        return items
            .map((item) => {
                // 자식 노드가 있는 경우(부서나 팀)
                if (item.children) {
                    const filteredChildren = filterTree(item.children, query);

                    // 부서나 팀 이름이 검색어와 일치하거나, 자식 중에 검색 결과가 있을 경우
                    if (item.label.toLowerCase().includes(queryLower) || filteredChildren.length) {
                        return {
                            ...item,
                            children: filteredChildren.length ? filteredChildren : item.children // 자식이 없으면 원래 자식 노드 그대로 반환
                        };
                    }
                }

                // 사원 검색: 사원 이름이 검색어와 일치하는 경우 해당 사원 반환
                if (item.label.toLowerCase().includes(queryLower)) {
                    return item;
                }
                return null;
            })
            .filter((item) => item !== null); // null인 경우 제거
    };

    const result = filterTree(employeeData.value, searchQuery.value.toLowerCase());

    // 검색 결과를 기반으로 상위 부서, 팀 노드를 추가로 확장하기
    if (searchQuery.value) {
        expandMatchingNodes(result, searchQuery.value.toLowerCase());
    }

    return result;
});

// 검색 결과에 따라 상위 노드를 확장
const expandMatchingNodes = (items, query) => {
    items.forEach((item) => {
        if (item.children) {
            const hasMatchingChildren = item.children.some((child) => child.label.toLowerCase().includes(query));

            // 자식 중에 검색어와 일치하는 항목이 있으면 부모 노드를 확장
            if (hasMatchingChildren || item.label.toLowerCase().includes(query)) {
                expandedKeys.value[item.key] = true; // 트리 노드 확장
            }
            // 자식들도 재귀적으로 검사
            expandMatchingNodes(item.children, query);
        }
    });
};

// 검색어 입력 후 Enter 시 호출
const onSearchEnter = () => {
    const filteredData = filteredTreeData.value;

    // 검색된 데이터에서 선택 항목을 찾아서 selectedKeys에 추가
    const selectMatchingNodes = (items) => {
        items.forEach((item) => {
            if (item.key.startsWith('emp-') || item.key.startsWith('team-') || item.key.startsWith('dept-')) {
                selectedKeys.value[item.key] = true; // 선택된 노드 업데이트
            }
            if (item.children) {
                selectMatchingNodes(item.children); // 자식 노드도 검색
            }
        });
    };

    selectMatchingNodes(filteredData);

    // 검색 결과에 따라 트리 노드를 확장
    expandMatchingNodes(filteredData, searchQuery.value.toLowerCase());
};

// 단일 알림 발송
const sendNotification = async (notificationPayload) => {
    try {
        const response = await fetchPost('https://hq-heroes-api.com/api/v1/notification-service/notification', notificationPayload);
        if (response) {
            console.log('Notification sent successfully:', response);
        } else {
            console.error('Failed to send notification.');
        }
    } catch (error) {
        console.error('Error sending notification:', error);
    }
};

// 알림 발송 시 진행률 표시
const sendMessage = async () => {
    try {
        const receiverIds = Object.keys(selectedKeys.value)
            .filter((key) => key.startsWith('emp-'))
            .map((key) => key.replace('emp-', ''));

        if (!selectedCategory.value) {
            alert('카테고리를 선택하세요.');
            return;
        }

        if (receiverIds.length === 0) {
            alert('직원을 선택하세요.');
            return;
        }

        const senderId = window.localStorage.getItem('employeeId');

        const payload = {
            senderId,
            receiverIds,
            categoryId: selectedCategory.value.notificationCategoryId,
            message: message.value,
            status: 'UNREAD' // 기본 상태 설정
        };

        await sendNotification(payload);

        await Swal.fire({
            title: '알림이 발송되었습니다.',
            icon: 'success'
        });
    } catch (error) {
        await Swal.fire({
            title: '알림이 발송이 실패하였습니다.',
            icon: 'error'
        });
    }
};
</script>

<style scoped>
.app-container {
    display: flex;
    height: 50rem;
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
    top: 44%;
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

.message-editor {
    flex-grow: 1;
    font-size: 16px;
    height: 28rem;
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
    background-color: #6366f1;
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
