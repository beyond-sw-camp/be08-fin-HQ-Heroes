<script setup>
import { AttendanceService } from './service/AttendanceService';
import { FilterMatchMode } from '@primevue/core/api';
import { useToast } from 'primevue/usetoast';
import { onMounted, ref } from 'vue';

onMounted(() => {
    AttendanceService.getStudents().then((data) => (students.value = data));
});

const toast = useToast();
const dt = ref();
const students = ref([]);
const approveStudentDialog = ref(false);  // 승인 다이얼로그 변수
const deleteStudentDialog = ref(false);
const deleteStudentsDialog = ref(false);
const student = ref({});
const selectedStudents = ref([]);
const filters = ref({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS }
});
const submitted = ref(false);
const attendanceStatuses = ref([
    { label: '휴가', value: '휴가' },
    { label: '조퇴', value: '조퇴' },
    { label: '병가', value: '병가' }
]);

function openNew() {
    student.value = {};
    submitted.value = false;
    approveStudentDialog.value = true;
}

function hideDialog() {
    approveStudentDialog.value = false;
    submitted.value = false;
}

function approveStudent() {
    submitted.value = true;

    if (student?.value.name?.trim()) {
        if (student.value.id) {
            student.value.attendanceStatus = student.value.attendanceStatus.value ? student.value.attendanceStatus.value : student.value.attendanceStatus;
            students.value[findIndexById(student.value.id)] = student.value;
            toast.add({ severity: 'success', summary: 'Successful', detail: 'Student Approved', life: 3000 });
        } else {
            student.value.id = createId();
            students.value.push(student.value);
            toast.add({ severity: 'success', summary: 'Successful', detail: 'Student Approved', life: 3000 });
        }

        approveStudentDialog.value = false;
        student.value = {};
    }
}

function editStudent(stu) {
    student.value = { ...stu };
    approveStudentDialog.value = true;
}

function confirmDeleteStudent(stu) {
    student.value = stu;
    deleteStudentDialog.value = true;
}

function deleteStudent() {
    students.value = students.value.filter((val) => val.id !== student.value.id);
    deleteStudentDialog.value = false;
    student.value = {};
    toast.add({ severity: 'success', summary: 'Successful', detail: 'Student Deleted', life: 3000 });
}

function confirmApproveSelected() {
    selectedStudents.value.forEach(stu => {
        stu.attendanceStatus = 'Approved';  // 승인 상태로 변경
    });
    toast.add({ severity: 'success', summary: 'Approved', detail: 'Selected Students Approved', life: 3000 });
    selectedStudents.value = [];
}

function findIndexById(id) {
    return students.value.findIndex((student) => student.id === id);
}

function createId() {
    let id = '';
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for (let i = 0; i < 5; i++) {
        id += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return id;
}

function exportCSV() {
    dt.value.exportCSV();
}

function confirmDeleteSelected() {
    deleteStudentsDialog.value = true;
}

function deleteSelectedStudents() {
    students.value = students.value.filter((val) => !selectedStudents.value.includes(val));
    deleteStudentsDialog.value = false;
    selectedStudents.value = null;
    toast.add({ severity: 'success', summary: 'Successful', detail: 'Students Deleted', life: 3000 });
}

function getStatusLabel(status) {
    switch (status) {
        case '휴가':
            return 'info';

        case '조퇴':
            return 'warning';

        case '병가':
            return 'danger';

        case 'Approved':
            return 'success';

        default:
            return null;
    }
}
</script>

<template>
    <div>
        <div class="card">
            <Toolbar class="mb-6">
                <template #start>
                    <Button label="New" icon="pi pi-plus" severity="secondary" class="mr-2" @click="openNew" />
                    <Button label="Approve" icon="pi pi-check" severity="success" @click="confirmApproveSelected" :disabled="!selectedStudents || !selectedStudents.length" />
                    <Button label="Delete" icon="pi pi-trash" severity="danger" @click="confirmDeleteSelected" :disabled="!selectedStudents || !selectedStudents.length" />
                </template>

                <template #end>
                    <Button label="Export" icon="pi pi-upload" severity="secondary" @click="exportCSV($event)" />
                </template>
            </Toolbar>

            <DataTable
                ref="dt"
                v-model:selection="selectedStudents"
                :value="students"
                dataKey="id"
                :paginator="true"
                :rows="10"
                :filters="filters"
                paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                :rowsPerPageOptions="[5, 10, 25]"
                currentPageReportTemplate="Showing {first} to {last} of {totalRecords} students"
            >
                <template #header>
                    <div class="flex flex-wrap gap-2 items-center justify-between">
                        <h4 class="m-0">Manage Attendance</h4>
                        <IconField>
                            <InputIcon>
                                <i class="pi pi-search" />
                            </InputIcon>
                            <InputText v-model="filters['global'].value" placeholder="Search..." />
                        </IconField>
                    </div>
                </template>

                <Column selectionMode="multiple" style="width: 3rem" :exportable="false"></Column>
                <Column field="name" header="Name" sortable style="min-width: 16rem"></Column>
                <Column field="class" header="Class" sortable style="min-width: 10rem"></Column>
                <Column field="attendanceStatus" header="Attendance" sortable style="min-width: 12rem">
                    <template #body="slotProps">
                        <Tag :value="slotProps.data.attendanceStatus" :severity="getStatusLabel(slotProps.data.attendanceStatus)" />
                    </template>
                </Column>
                <Column :exportable="false" style="min-width: 12rem">
                    <template #body="slotProps">
                        <Button icon="pi pi-check" outlined rounded class="mr-2" @click="editStudent(slotProps.data)" />
                        <Button icon="pi pi-trash" outlined rounded severity="danger" @click="confirmDeleteStudent(slotProps.data)" />
                    </template>
                </Column>
            </DataTable>
        </div>

        <Dialog v-model:visible="approveStudentDialog" :style="{ width: '450px' }" header="Approve Student" :modal="true">
            <div class="flex flex-col gap-6">
                <div>
                    <label for="name" class="block font-bold mb-3">Name</label>
                    <InputText id="name" v-model.trim="student.name" required="true" autofocus :invalid="submitted && !student.name" fluid />
                    <small v-if="submitted && !student.name" class="text-red-500">Name is required.</small>
                </div>
                <div>
                    <label for="class" class="block font-bold mb-3">Class</label>
                    <InputText id="class" v-model="student.class" required="true" fluid />
                </div>
                <div>
                    <label for="attendanceStatus" class="block font-bold mb-3">Attendance Status</label>
                    <Select id="attendanceStatus" v-model="student.attendanceStatus" :options="attendanceStatuses" optionLabel="label" placeholder="Select a Status" fluid></Select>
                </div>
            </div>

            <template #footer>
                <Button label="Cancel" icon="pi pi-times" text @click="hideDialog" />
                <Button label="Approve" icon="pi pi-check" severity="success" @click="approveStudent" />
            </template>
        </Dialog>

        <Dialog v-model:visible="deleteStudentDialog" :style="{ width: '450px' }" header="Confirm" :modal="true">
            <div class="flex items-center gap-4">
                <i class="pi pi-exclamation-triangle !text-3xl" />
                <span v-if="student"
                    >Are you sure you want to delete <b>{{ student.name }}</b
                    >?</span
                >
            </div>
            <template #footer>
                <Button label="No" icon="pi pi-times" text @click="deleteStudentDialog = false" />
                <Button label="Yes" icon="pi pi-check" @click="deleteStudent" />
            </template>
        </Dialog>

        <Dialog v-model:visible="deleteStudentsDialog" :style="{ width: '450px' }" header="Confirm" :modal="true">
            <div class="flex items-center gap-4">
                <i class="pi pi-exclamation-triangle !text-3xl" />
                <span v-if="student">Are you sure you want to delete the selected students?</span>
            </div>
            <template #footer>
                <Button label="No" icon="pi pi-times" text @click="deleteStudentsDialog = false" />
                <Button label="Yes" icon="pi pi-check" text @click="deleteSelectedStudents" />
            </template>
        </Dialog>
    </div>
</template>