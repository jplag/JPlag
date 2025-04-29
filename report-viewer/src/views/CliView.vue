<template>
  <div class="flex flex-col gap-1 md:overflow-hidden">
    <div class="flex-1 md:overflow-hidden print:flex-none">
      <div class="flex max-h-full flex-col gap-5 md:overflow-hidden">
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

        <div class="grid flex-1 grid-cols-1 gap-3 overflow-auto md:grid-cols-2">
          <CliViewCategory class="h-full min-h-fit md:col-start-1 md:row-start-1">
            <template #heading> Comparison Options </template>

            <CliUiOption label="Language"></CliUiOption>
            <CliUiOption label="Minimum Token Match"></CliUiOption>
            <CliUiOption label="Normalize"></CliUiOption>
          </CliViewCategory>

          <CliViewCategory class="h-full min-h-fit md:col-start-2 md:row-start-1">
            <template #heading> Report Options </template>

            <CliUiOption label="Result File Name"></CliUiOption>
            <CliUiOption label="Shown Comparisons"></CliUiOption>
            <CliUiOption label="Similarity Treshold"></CliUiOption>
            <CliUiOption label="Overwrite Existing"></CliUiOption>
            <CliUiOption label="CSV File"></CliUiOption>
          </CliViewCategory>

          <CliViewCategory class="h-fit md:col-span-2 md:row-start-2">
            <template #heading> Submission Files </template>

            <CliUiOption label="Submission Directory"></CliUiOption>
            <CliUiOption label="Basecode Directory"></CliUiOption>
            <CliUiOption label="Old Submission Directories"></CliUiOption>
          </CliViewCategory>

          <CliViewCategory
            class="min-h-fit md:col-start-1 md:row-start-3"
            :class="enableClustering ? 'h-full' : 'h-fit'"
            :show-content="enableClustering"
          >
            <template #heading>
              <span class="flex items-center gap-2">
                <SwitchComponent v-model="enableClustering" clas="col-start-1" />
                <span class="col-start-2">Clustering</span>
              </span>
            </template>

            <CliUiOption label="Algorithm"></CliUiOption>
            <CliUiOption label="Metric"></CliUiOption>
          </CliViewCategory>

          <CliViewCategory
            class="min-h-fit md:col-start-2 md:row-start-3"
            :class="enableMatchMerging ? 'h-full' : 'h-fit'"
            :show-content="enableMatchMerging"
          >
            <template #heading>
              <span class="flex items-center gap-2">
                <SwitchComponent v-model="enableMatchMerging" clas="col-start-1" />
                <span class="col-start-2">Match Merging</span>
              </span>
            </template>

            <CliUiOption label="Max Gap Size"></CliUiOption>
            <CliUiOption label="Neighbour Length"></CliUiOption>
            <CliUiOption label="Required Merges"></CliUiOption>
          </CliViewCategory>

          <CliViewCategory
            class="h-fit md:col-span-2 md:row-start-4"
            :show-content="showAdvancedOptions"
          >
            <template #heading>
              <span class="cursor-pointer" @click="showAdvancedOptions = !showAdvancedOptions">
                <div class="flex items-center gap-x-2">
                  <FontAwesomeIcon :icon="showAdvancedOptions ? faCaretDown : faCaretRight" />
                  Advanced Options
                </div>
              </span>
            </template>

            <CliUiOption label="Exlcusion File"></CliUiOption>
            <CliUiOption label="File Extensions"></CliUiOption>
            <CliUiOption label="Subdirectory"></CliUiOption>
            <CliUiOption label="Debug"></CliUiOption>
            <CliUiOption label="Log Level"></CliUiOption>
          </CliViewCategory>
        </div>
      </div>
    </div>

    <VersionRepositoryReference />
  </div>
</template>

<script setup lang="ts">
import CliUiOption from '@/components/CliUiOption.vue'
import CliViewCategory from '@/components/CliViewCategory.vue'
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
