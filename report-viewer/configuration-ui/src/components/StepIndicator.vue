<template>
  <ContainerComponent
    class="p-1!"
    :class="{
      'border-accent!': state === 'active',
      'text-gray': state === 'future'
    }"
  >
    <div class="flex items-center gap-2">
      <FontAwesomeIcon
        :icon="state === 'done' ? faCircleCheck : faCircle"
        :class="{
          'text-accent': state === 'active'
        }"
      />
      <span>{{ title }}</span>
    </div>

    <p v-if="state === 'active' && description !== null">
      {{ description }}
    </p>
  </ContainerComponent>
</template>

<script setup lang="ts">
import { faCircle, faCircleCheck } from '@fortawesome/free-regular-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { ContainerComponent } from '@jplag/ui-components/base'
import { PropType } from 'vue'

defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: undefined
  },
  state: {
    type: String as PropType<'done' | 'active' | 'future'>,
    required: true
  }
})
</script>
