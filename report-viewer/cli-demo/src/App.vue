<template>
  <div class="overflow-hidden">
    <div
      class="bg-background-light dark:bg-background-dark grid max-h-screen min-h-screen max-w-screen grid-rows-[auto_1fr] overflow-hidden text-black dark:text-amber-50 print:max-h-none print:w-full print:max-w-full print:overflow-visible"
      :style="{
        'grid-template-columns': tableExpanded ? 'auto 0px 1fr' : 'auto 1fr auto'
      }"
    >
      <div
        class="bg-container-light dark:bg-container-dark border-container-border-light dark:border-container-border-dark col-span-3 col-start-1 row-start-1 border-b"
      >
        <TopBar />
      </div>

      <div
        class="bg-container-light dark:bg-container-dark border-container-border-light dark:border-container-border-dark col-start-1 row-start-2 border-r"
      >
        <SideBar />
      </div>

      <div class="col-start-2 row-start-2 p-5 overflow-hidden max-h-full">
        <RouterView class="max-h-full! overflow-hidden! *:overflow-hidden *:max-h-full" />
      </div>

      <SideTable
        v-model:expanded="tableExpanded"
        :submissions="submissions"
        class="col-start-3 row-start-2"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import SideBar from './components/SideBar.vue'
import SideTable from './components/SideTable.vue'
import TopBar from './components/TopBar.vue'
import { RouterView } from 'vue-router'
import { LoadedSubmission } from './model/Submission'
import { ParserLanguage, SubmissionState } from '@jplag/model'
import { store } from './store'

const tableExpanded = ref(false)

function random4Letters() {
  const letters = 'abcdefghijklmnopqrstuvwxyz'
  let result = ''
  for (let i = 0; i < 4; i++) {
    result += letters[Math.floor(Math.random() * letters.length)]
  }
  return result
}

const ids = Array.from({ length: 120 }, () => 'u' + random4Letters())
const submissions = computed<LoadedSubmission[]>(() => {
  if (store().cliOptions.submissionDirectories.length + store().cliOptions.oldSubmissionDirectories.length <= 0) {
    return []
  }
  const simple = ids.map((id, i) => {
    const loc = Math.floor(Math.random() * 500) + 750
    return {
      id: i+1,
      name: id,
      encoding: 'utf-8',
      linesOfCode: loc,
      state: SubmissionState.UNPARSED
    }
  })
  if (store().cliOptions.language.length == 0) {
    return simple
  } else {
    return simple.map(s => ({
      ...s,
      language: [ParserLanguage.PYTHON, ParserLanguage.PYTHON],
      tokenCount: s.linesOfCode * 2,
      state: SubmissionState.VALID
    }))
  }
})
</script>
