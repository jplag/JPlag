<template>
  <RouterLink :to="to"
    ><OptionComponent :selected="true" class="px-2! text-sm"
      ><span class="flex items-center gap-1"
        ><FontAwesomeIcon :icon="faArrowLeft" /> Overview</span
      ></OptionComponent
    ></RouterLink
  >
</template>

<script setup lang="ts">
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { RouterLink } from 'vue-router'
import { OptionComponent } from '@jplag/ui-components/widget'

defineProps({
  to: {
    type: String,
    required: false,
    default: '/overview/'
  }
})
</script>
