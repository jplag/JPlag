<!--
  Base Component to separate content into multiple chunks
-->
<template>
  <div
    class="border-container-border-light bg-container-light dark:border-container-border-dark dark:bg-container-dark box-border rounded-md border p-2 shadow-sm print:border-2 print:shadow-none"
  >
    <slot></slot>
  </div>
</template>

<script setup lang="ts"></script>
