<!-- 
  Styled Base Component for all things a user can interact with 
-->
<template>
  <Container class="border-interactable-border bg-interactable cursor-pointer shadow-none!">
    <slot></slot>
  </Container>
</template>

<script setup lang="ts">
import Container from './ContainerComponent.vue'
</script>
