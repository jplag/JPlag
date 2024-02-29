<template>
  <div class="group relative flex items-center justify-center">
    <div class="break-anywhere">
      {{ store().getDisplayName(id) }}
    </div>
    <div
      class="invisible absolute right-0 top-0 z-10 flex h-full cursor-pointer items-center p-2 delay-0 group-hover:visible group-hover:delay-100"
      @click="(event) => changeAnonymous(event)"
    >
      <FontAwesomeIcon
        class="text-gray-500"
        :icon="store().isAnonymous(props.id) ? ['fas', 'eye'] : ['fas', 'eye-slash']"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons'
import { store } from '@/stores/store'

library.add(faEye)
library.add(faEyeSlash)

const props = defineProps({
  id: {
    type: String,
    required: true
  }
})

function changeAnonymous(event: Event) {
  event.stopPropagation()
  event.preventDefault()
  if (store().isAnonymous(props.id)) {
    store().removeAnonymous([props.id])
  } else {
    store().addAnonymous([props.id])
  }
}
</script>
