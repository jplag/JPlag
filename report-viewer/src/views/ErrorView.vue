<template>
  <div class="flex h-screen text-center">
    <div class="w-screen">
      <div>
        <img
          v-if="store().uiState.useDarkMode"
          class="mx-auto mt-32 h-auto w-60"
          src="@/assets/jplag-light-transparent.png"
          alt="JPlag Logo"
        />
        <img
          v-else
          class="mx-auto mt-32 h-auto w-60"
          src="@/assets/jplag-dark-transparent.png"
          alt="JPlag Logo"
        />
      </div>
      <Container class="mx-auto mt-10 w-fit max-w-5xl space-y-5 p-5">
        <div class="space-y-2">
          <h3 class="text-2xl font-bold">There was an Error!</h3>
          <p class="text-xl">{{ message }}</p>
        </div>
        <RouterLink
          :to="{
            name: to
          }"
        >
          <Interactable class="border-accent-dark! bg-accent/50! mx-auto mt-2 w-fit">
            {{ routerInfo }}
          </Interactable>
        </RouterLink>
      </Container>
    </div>
  </div>
</template>

<script setup lang="ts">
import Container from '@/components/ContainerComponent.vue'
import Interactable from '@/components/InteractableComponent.vue'
import { store } from '@/stores/store'
import { onErrorCaptured } from 'vue'

defineProps({
  message: {
    type: String,
    required: true
  },
  to: {
    type: String,
    required: false,
    default: 'FileUploadView'
  },
  routerInfo: {
    type: String,
    required: false,
    default: 'Back to file upload page'
  }
})

onErrorCaptured((error) => {
  console.error(error)
  alert(
    'An error occurred that could not be handled. Please check the console for more information.'
  )
  return false
})
</script>
