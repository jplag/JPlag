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
import { ref } from 'vue'
import SideBar from './components/SideBar.vue'
import SideTable from './components/SideTable.vue'
import TopBar from './components/TopBar.vue'
import { RouterView } from 'vue-router'
import { LoadedSubmission } from './model/Submission'
import { ParserLanguage, SubmissionState } from '@jplag/model'

const tableExpanded = ref(false)

function random4Letters() {
  const letters = 'abcdefghijklmnopqrstuvwxyz'
  let result = ''
  for (let i = 0; i < 4; i++) {
    result += letters[Math.floor(Math.random() * letters.length)]
  }
  return result
}

const submissions: LoadedSubmission[] = Array.from({ length: 1200 }, (_, i) => ({
  id: i + 1,
  name: 'u' + random4Letters(),
  encoding: 'utf-8',
  linesOfCode: Math.floor(Math.random() * 3000),
  languages: [
    ParserLanguage.JAVA,
    ...[ParserLanguage.PYTHON, ParserLanguage.CPP].filter(() => Math.random() > 0.4)
  ],
  tokenCount: Math.floor(Math.random() * 10000),
  //state: SubmissionState.UNPARSED,
  state: [
    SubmissionState.VALID,
    SubmissionState.VALID,
    SubmissionState.VALID,
    SubmissionState.CANNOT_PARSE,
    SubmissionState.NOTHING_TO_PARSE,
    SubmissionState.TOO_SMALL
  ][Math.floor(Math.random() * 6)]
}))
</script>
