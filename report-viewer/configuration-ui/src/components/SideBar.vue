<template>
  <div
    class="border-container-border box-border flex max-w-52 flex-col gap-2 border-r p-2 shadow-sm"
    :class="{
      'w-52': expanded
    }"
  >
    <div class="flex h-8 items-center gap-2 pl-1.25">
      <FontAwesomeIcon :icon="faBars" class="cursor-pointer" @click="expanded = !expanded" />
      <span v-if="expanded" class="text-2xl font-bold">JPlag</span>
    </div>
    <div class="flex flex-col gap-5">
      <StepIndicator
        v-for="[idx, step] in STEPS.entries()"
        :key="idx"
        :class="configState().canChooseStep(idx) ? 'cursor-pointer' : 'cursor-not-allowed'"
        :title="step.name"
        :description="step.description"
        :state="configState().getState(idx)"
        :validity="configState().getValidity(idx)"
        :expanded="expanded"
        @click="configState().setStep(idx)"
      />
    </div>

    <div v-if="expanded && configState().parsingAnswer !== null" class="border-gray text-gray border-y border-t mt-5">
      Submissions: {{ configState().parsingAnswer?.newSubmissions }} <br />
      Old Submissions: {{ configState().parsingAnswer?.oldSubmissions }}
    </div>
    <div class="flex-1"></div>
    <div v-if="expanded" class="text-gray flex flex-col gap-0 text-sm">
      <VersionInfoComponent /> <br />
      <a href="https://github.com/jplag/JPlag/" class="text-link underline"
        ><FontAwesomeIcon :icon="faGithub" /> GitHub</a
      >
    </div>
    <div v-else class="text-gray flex justify-center w-full">
      <a href="https://github.com/jplag/JPlag/"><FontAwesomeIcon :icon="faGithub" /></a>
      
    </div>
  </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { STEPS } from '../model/Steps'
import { configState } from '../state'
import StepIndicator from './StepIndicator.vue'
import { faBars } from '@fortawesome/free-solid-svg-icons'
import { ref } from 'vue'
import { faGithub } from '@fortawesome/free-brands-svg-icons'
import VersionInfoComponent from './VersionInfoComponent.vue'

const expanded = ref(true)
</script>
