<template>
  <CliViewCategory>
    <template #heading> Submission Files </template>

    <CliUiOption
      label="Submission Directory"
      :error="verifySubsmissionDirectories(submissionDirectories)"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.ROOT_DIRS"
    >
      <div class="flex flex-wrap gap-x-2 gap-y-1">
        <div
          v-for="[idx, dir] in submissionDirectories.entries()"
          :key="dir"
          class="border-interactable-border-light dark:border-interactable-border-dark bg-interactable-light dark:bg-interactable-dark flex items-center gap-x-2 rounded-full border px-2 text-xs"
        >
          {{ dir }}
          <span class="cursor-pointer" @click="submissionDirectories.splice(idx, 1)">X</span>
        </div>

        <button>
          <InteractableComponent class="py-0!" @click="addSubmissionDirectory()"
            >Add</InteractableComponent
          >
        </button>
      </div>
    </CliUiOption>
    <CliUiOption
      label="Basecode Directory"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.BASE_CODE"
    >
    </CliUiOption>
    <CliUiOption
      label="Old Submission Directories"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.OLD"
    >
      <div class="flex flex-wrap gap-x-2 gap-y-1">
        <div
          v-for="[idx, dir] in oldDirectories.entries()"
          :key="dir"
          class="border-interactable-border-light dark:border-interactable-border-dark bg-interactable-light dark:bg-interactable-dark flex items-center gap-x-2 rounded-full border px-2 text-xs"
        >
          {{ dir }}
          <span class="cursor-pointer" @click="oldDirectories.splice(idx, 1)">X</span>
        </div>

        <button>
          <InteractableComponent class="py-0!" @click="addOldDirectory()"
            >Add</InteractableComponent
          >
        </button>
      </div>
    </CliUiOption>
  </CliViewCategory>
</template>
<script setup lang="ts">
import { verifySubsmissionDirectories } from '@/model/verifier'
import { CliToolTip } from '../../model/CliToolTip'
import CliUiOption from '../CliUiOption.vue'
import CliViewCategory from '../CliViewCategory.vue'

defineProps({
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  }
})

const submissionDirectories = defineModel<string[]>('submissionDirectories', {
  default: () => []
})
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const baseCodeDirectory = defineModel<string>('baseCodeDirectory', {
  default: ''
})
const oldDirectories = defineModel<string[]>('oldDirectories', {
  default: () => []
})

function addSubmissionDirectory() {
  const dir = prompt('Enter submission directory path:')
  if (dir && dir.trim() !== '') {
    submissionDirectories.value.push(dir.trim())
  }
}
function addOldDirectory() {
  const dir = prompt('Enter old submission directory path:')
  if (dir && dir.trim() !== '') {
    oldDirectories.value.push(dir.trim())
  }
}
</script>
