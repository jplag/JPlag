<!-- 
  Base Component to seperate content into multiple chunks 
-->
<template>
  <div
    class="box-border rounded-md border-1 border-container-border-light bg-container-light p-2 shadow print:border-2 print:shadow-none dark:border-container-border-dark dark:bg-container-dark dark:shadow-black"
  >
    <slot></slot>
  </div>
</template>

<script setup lang="ts"></script>
