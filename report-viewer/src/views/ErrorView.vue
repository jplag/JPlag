<template>
  <div class="flex h-screen items-center text-center">
    <div class="w-screen">
      <div>
        <img
          class="mx-auto mt-8 h-auto w-60"
          src="@/assets/jplag-light-transparent.png"
          alt="JPlag Logo"
          v-if="store().uiState.useDarkMode"
        />
        <img
          class="mx-auto mt-8 h-auto w-60"
          src="@/assets/jplag-dark-transparent.png"
          alt="JPlag Logo"
          v-else
        />
      </div>
      <Container class="mx-auto mt-10 w-fit max-w-5xl space-y-5 p-5">
        <div class="space-y-2">
          <h3 class="text-2xl font-bold">There was an Error!</h3>
          <p class="text-xl">{{ message }}</p>
        </div>
        <Interactable class="mx-auto w-fit !border-accent-dark !bg-accent !bg-opacity-50">
          <RouterLink :to="to">{{ routerInfo }}</RouterLink>
        </Interactable>
      </Container>
    </div>
  </div>
</template>

<script setup lang="ts">
import Container from '@/components/ContainerComponent.vue'
import Interactable from '@/components/InteractableComponent.vue'
import { store } from '@/stores/store'

const message = history.state.message as string
const to = history.state.to as string
const routerInfo = history.state.routerInfo as string
</script>
