<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <div class="grid-rows[auto_1fr] grid flex-1 gap-5 md:overflow-hidden print:flex-none">
      <ContainerComponent class="row-start-1 h-fit">
        <div class="grid grid-cols-[1fr_auto] grid-rows-[auto_1fr] gap-2">
          <h1 class="col-start-1 row-start-1 text-2xl">JPlag</h1>

          <InteractableComponent
            class="col-start-2 row-span-2 row-start-1 h-fit border-green-900! bg-green-700! text-white"
          >
            <div class="flex items-center gap-x-2 px-5">
              <FontAwesomeIcon :icon="faPlay" />
              Run
            </div>
          </InteractableComponent>

          <p class="col-start-1 row-start-2 text-sm">
            More information on each parameter can be found here:
            <a
              href="https://github.com/jplag/JPlag/wiki/1.-How-to-Use-JPlag"
              class="dark:text-link text-link-dark underline"
              >Parameter Description</a
            >
          </p>
        </div>
      </ContainerComponent>

      <div class="row-start-2 grid grid-cols-1 gap-3 overflow-auto">
        <ContainerComponent class="h-fit">
          <h2 class="text-xl">Comparison Options</h2>
          <div class="mt-3 grid grid-cols-1 gap-2 md:grid-cols-[auto_1fr]">
            <CliUiOption label="Language"></CliUiOption>
            <CliUiOption label="Minimum Token Match"></CliUiOption>
            <CliUiOption label="Normalize"></CliUiOption>
          </div>
        </ContainerComponent>

        <ContainerComponent class="h-fit">
          <h2 class="text-xl">Submission Directories</h2>

          <div class="mt-3 grid grid-cols-1 gap-2 md:grid-cols-[auto_1fr]">
            <CliUiOption label="Submission Directory"></CliUiOption>
            <CliUiOption label="Basecode Directory"></CliUiOption>
            <CliUiOption label="Old Submission Directories"></CliUiOption>
            <CliUiOption label="CSV File"></CliUiOption>
          </div>
        </ContainerComponent>

        <ContainerComponent class="h-fit">
          <h2 class="text-xl">Report Options</h2>

          <div class="mt-3 grid grid-cols-1 gap-2 md:grid-cols-[auto_1fr]">
            <CliUiOption label="Result File Name"></CliUiOption>
            <CliUiOption label="Shown Comparisons"></CliUiOption>
            <CliUiOption label="Similarity Treshold"></CliUiOption>
            <CliUiOption label="Overwrite Existing"></CliUiOption>
          </div>
        </ContainerComponent>

        <ContainerComponent class="h-fit">
          <div class="flex items-center gap-2">
            <SwitchComponent v-model="enableClustering" clas="col-start-1" />
            <span class="col-start-2">Enable Clustering</span>
          </div>

          <div v-if="enableClustering" class="mt-5 grid grid-cols-1 gap-2 md:grid-cols-[auto_1fr]">
            <CliUiOption label="Algorithm"></CliUiOption>
            <CliUiOption label="Metric"></CliUiOption>
          </div>
        </ContainerComponent>

        <ContainerComponent class="h-fit">
          <div class="flex items-center gap-2">
            <SwitchComponent v-model="enableMatchMerging" clas="col-start-1" />
            <span class="col-start-2">Enable Match Merging</span>
          </div>

          <div
            v-if="enableMatchMerging"
            class="mt-3 grid grid-cols-1 gap-2 md:grid-cols-[auto_1fr]"
          >
            <CliUiOption label="Max Gap Size"></CliUiOption>
            <CliUiOption label="Neighbour Length"></CliUiOption>
            <CliUiOption label="Required Merges"></CliUiOption>
          </div>
        </ContainerComponent>

        <ContainerComponent>
          <h2 class="cursor-pointer text-xl" @click="showAdvancedOptions = !showAdvancedOptions">
            <div class="flex items-center gap-x-2">
              <FontAwesomeIcon :icon="showAdvancedOptions ? faCaretDown : faCaretRight" />
              Advanced Options
            </div>
          </h2>

          <div v-if="showAdvancedOptions" class="mt-3 grid grid-cols-[auto_1fr] gap-2">
            <CliUiOption label="Exlcusion File"></CliUiOption>
            <CliUiOption label="File Extensions"></CliUiOption>
            <CliUiOption label="Subdirectory"></CliUiOption>
            <CliUiOption label="Debug"></CliUiOption>
            <CliUiOption label="Log Level"></CliUiOption>
          </div>
        </ContainerComponent>
      </div>
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import CliUiOption from '@/components/CliUiOption.vue'
import ContainerComponent from '@/components/ContainerComponent.vue'
import InteractableComponent from '@/components/InteractableComponent.vue'
import SwitchComponent from '@/components/SwitchComponent.vue'
import VersionRepositoryReference from '@/components/VersionRepositoryReference.vue'
import { faCaretDown, faCaretRight, faPlay } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { ref, type Ref } from 'vue'

const showAdvancedOptions = ref(false)
const enableClustering = ref(true)
const enableMatchMerging = ref(false)

interface Information {
  value: string
  error?: string
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function verifyNumber(
  information: Ref<Information>,
  isInt = true,
  min: number = 0,
  max: number = Infinity
) {
  const value = isInt ? parseInt(information.value.value) : parseFloat(information.value.value)

  if (isNaN(value)) {
    information.value.error = 'Entered value needs to be a number'
  } else if (value < min) {
    information.value.error = `Value must be greater than ${min}`
  } else if (value > max) {
    information.value.error = `Value must be less than ${max}`
  } else {
    information.value.error = undefined
  }
}
</script>
