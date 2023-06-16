<template>
  <Container
    class="cursor-pointer !shadow-none bg-interactable-light dark:bg-interactable-dark border-interactable-border-light dark:border-interactable-border-dark"
  >
    <slot></slot>
  </Container>
</template>

<script setup lang="ts">
import Container from './ContainerComponent.vue'
</script>
