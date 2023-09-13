<!-- 
  Base Component for Buttons 
-->
<template>
  <Interactable
    class="flex items-center justify-center px-2 py-1 text-center"
    @click="(e) => $emit('click', e)"
  >
    <slot></slot>
  </Interactable>
</template>

<script setup lang="ts">
import Interactable from './InteractableComponent.vue'

defineEmits(['click'])
</script>
