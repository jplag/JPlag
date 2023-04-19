<template>
  <div class="container">
    <img alt="JPlag" src="@/assets/logo-nobg.png" />
    <error-message :message="message" />
    <error-router :to="to" :routerInfo="routerInfo" />
  </div>
</template>

<script setup lang="ts">
import ErrorMessage from '@/components/ErrorMessage.vue'
import ErrorRouter from '@/components/ErrorRouter.vue'

const message = history.state.message as string
const to = history.state.to as string
const routerInfo = history.state.routerInfo as string
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: var(--primary-color-light);
}
</style>
