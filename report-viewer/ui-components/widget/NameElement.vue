<template>
  <div class="group relative flex items-center justify-center">
    <div class="break-anywhere">
      {{ displayedName }}
    </div>
    <div
      class="invisible absolute top-0 right-0 z-10 flex h-full cursor-pointer items-center p-2 delay-0 group-hover:visible group-hover:delay-100"
      @click="(e) => emit('changeAnonymous', e)"
    >
      <FontAwesomeIcon
        class="text-gray-500"
        :icon="isAnonymous ? ['fas', 'eye'] : ['fas', 'eye-slash']"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons'

library.add(faEye)
library.add(faEyeSlash)

defineProps({
  displayedName: {
    type: String,
    required: true
  },
  isAnonymous: {
    type: Boolean,
    required: false,
    default: false
  }
})

const emit = defineEmits(['changeAnonymous'])
</script>
