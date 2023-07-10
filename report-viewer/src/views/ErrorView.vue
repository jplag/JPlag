<template>
  <div class="h-screen text-center flex items-center">
    <div class="w-screen">
      <div>
        <img
          class="mt-8 w-60 h-auto mx-auto"
          src="@/assets/jplag-light-transparent.png"
          alt="JPlag Logo"
          v-if="store().uiState.useDarkMode"
        />
        <img
          class="mt-8 w-60 h-auto mx-auto"
          src="@/assets/jplag-dark-transparent.png"
          alt="JPlag Logo"
          v-else
        />
      </div>
      <Container class="p-5 mt-10 w-fit max-w-5xl space-y-5 mx-auto">
        <div class="space-y-2">
          <h3 class="text-2xl font-bold">There was an Error!</h3>
          <p class="text-xl">{{ message }}</p>
        </div>
        <Interactable class="!bg-accent !bg-opacity-50 !border-accent-dark w-fit mx-auto">
          <RouterLink :to="to">{{ routerInfo }}</RouterLink>
        </Interactable>
      </Container>
    </div>
  </div>
</template>

<script setup lang="ts">
import Container from '@/components/ContainerComponent.vue'
import Interactable from '@/components/InteractableComponent.vue'
import store from '@/stores/store'

const message = history.state.message as string
const to = history.state.to as string
const routerInfo = history.state.routerInfo as string
</script>
