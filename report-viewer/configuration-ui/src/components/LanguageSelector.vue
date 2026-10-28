<template>
  <ContainerComponent :class="{'border-accent!' : selected}">
    <div class="flex">
      <div class="w-1/3">
        <label class="flex cursor-pointer items-center gap-2">
        <input v-model="selected" type="checkbox" class="h-5 w-5 accent-accent" />
        <span class="select-none">{{ name }}</span>
        <span v-if="count > 0" class="text-gray">({{ count }} submissions)</span>
      </label>
      </div>
      
      
      <div v-if="selected" class="grid grid-cols-[auto_1fr] gap-1">
        <span>Minimum Token Match:</span>
        <NumberInput />

        <span>File Endings:</span>
        <NumberInput />

        <span>Normalize:</span>
        <div>
          <input type="checkbox" class="accent-accent">
        </div>
      </div>
    </div>
  </ContainerComponent>
</template>

<script setup lang="ts">
import NumberInput from './NumberInput.vue';
import { ContainerComponent } from '@jplag/ui-components/base'

defineProps({
  name: {
    type: String,
    required: true
  },
  count: {
    type: Number,
    default: 0
  }
})


const selected = defineModel<boolean>('selected', { required: true })
//const options = defineModel<LanguageInterface>('options', {required: true})
</script>
