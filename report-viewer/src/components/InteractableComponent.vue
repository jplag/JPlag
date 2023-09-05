<!-- 
  Styled Base Component for all things a user can interact with 
-->
<template>
  <Container
    class="cursor-pointer border-interactable-border-light bg-interactable-light !shadow-none dark:border-interactable-border-dark dark:bg-interactable-dark"
  >
    <slot></slot>
  </Container>
</template>

<script setup lang="ts">
import Container from './ContainerComponent.vue'
</script>
