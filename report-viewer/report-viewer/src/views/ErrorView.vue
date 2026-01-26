<template>
  <div class="flex h-screen text-center">
    <div class="w-screen">
      <div>
        <img
          v-if="uiStore().useDarkMode"
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
      <ContainerComponent class="mx-auto mt-10 w-fit max-w-5xl space-y-5 p-5">
        <div class="space-y-2">
          <h3 class="text-2xl font-bold">There was an Error!</h3>
          <p class="text-xl">{{ message }}</p>
        </div>
        <RouterLink
          :to="{
            name: to
          }"
        >
          <InteractableComponent class="border-accent-dark! bg-accent/50! mx-auto mt-2 w-fit">
            {{ routerInfo }}
          </InteractableComponent>
        </RouterLink>
      </ContainerComponent>
    </div>
  </div>
</template>

<script setup lang="ts">
import { uiStore } from '@/stores/uiStore'
import { ContainerComponent, InteractableComponent } from '@jplag/ui-components/base'
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
