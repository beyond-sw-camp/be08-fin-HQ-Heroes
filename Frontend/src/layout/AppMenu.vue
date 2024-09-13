<script setup>
import { ref } from 'vue';

import AppMenuItem from './AppMenuItem.vue';

const model = ref([
    {
        label: 'Home',
        items: [{ label: 'Home', icon: 'pi pi-fw pi-home', to: '/' }]
    },
    {
        label: 'Pages',
        items: [
            { label: 'Attendance', icon: 'pi pi-fw pi-check-square', to: '/attendance' },
            { label: 'Student List', icon: 'pi pi-fw pi-list', to: '/studentlist' },
            { label: 'Calendar', icon: 'pi pi-fw pi-calendar', to: '/calendar' }
        ]
    },
    {
        label: 'Reference',
        items: [
            {
                label: 'Documentation',
                icon: 'pi pi-fw pi-book',
                url: 'https://primevue.org/setup/',
                target: '_blank'
            },
            {
                label: 'View Source',
                icon: 'pi pi-fw pi-github',
                url: 'https://github.com/beyond-sw-camp/be08-4th-1team',
                target: '_blank'
            }
        ]
    }
]);
</script>

<template>
    <ul class="layout-menu">
        <template v-for="(item, i) in model" :key="item">
            <app-menu-item v-if="!item.separator" :item="item" :index="i"></app-menu-item>
            <li v-if="item.separator" class="menu-separator"></li>
        </template>
    </ul>
</template>

<style lang="scss" scoped></style>
