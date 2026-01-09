<template>
  <div>
    <div
      class="bg-background-light dark:bg-background-dark grid-cols-[auto_1fr] grid max-h-screen min-h-screen max-w-screen grid-rows-[auto_1fr] overflow-scroll text-black dark:text-amber-50 print:max-h-none print:w-full print:max-w-full print:overflow-visible"
    >
      <div class="col-start-1 row-start-1 col-span-2 bg-container-light dark:bg-container-dark border-container-border-light dark:border-container-border-dark border-b">
        <TopBar />
      </div>

      <div class="col-start-1 row-start-2 bg-container-light dark:bg-container-dark border-container-border-light dark:border-container-border-dark border-r">
        <SideBar />
      </div>

      <div class="col-start-2 row-start-2 p-5">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SideBar from './components/SideBar.vue';
import TopBar from './components/TopBar.vue';
import { RouterView } from 'vue-router';

</script>
