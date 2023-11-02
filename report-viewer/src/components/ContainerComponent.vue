<!-- 
  Base Component to seperate content into multiple chunks 
-->
<template>
  <div
    class="box-border rounded-md border-1 border-container-border-light bg-container-light p-2 shadow dark:border-container-border-dark dark:bg-container-dark dark:shadow-black print:border-2 print:shadow-none"
  >
    <slot></slot>
  </div>
</template>

<script setup lang="ts"></script>
