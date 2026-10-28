<template>
  <div class="grid grid-cols-2 grid-rows-[auto_auto_1fr] gap-5">
    <ContainerComponent>
      <div class="flex flex-col">
        <span class="font-bold">Submission Folders</span>
        <span class="text-gray px-5 text-xs"
          >The submissions in these folders will be compared to all other submissions</span
        >
        <div class="mt-2 flex w-full flex-col gap-2">
          <ToolTipComponent
            v-for="[i, s] in state.newSubmissions.entries()"
            :key="s"
            direction="right"
            :show-info-symbol="false"
            class="w-full"
          >
            <template #default>
              <ContainerComponent>
                <div class="flex items-center gap-1">
                  <span class="flex-1">{{ getDisplayName(s) }}</span>
                  <FontAwesomeIcon :icon="faTrash" @click="state.oldSubmissions.splice(i,1)" />
                </div>
              </ContainerComponent>
            </template>
            <template #tooltip>SubNam1, SubName2, ...</template>
          </ToolTipComponent>
          <ContainerComponent @click="addSubmission()">
            <FontAwesomeIcon :icon="faPlus" /> Add Submission Folder
          </ContainerComponent>
        </div>
      </div>
    </ContainerComponent>

    <ContainerComponent>
      <div class="flex flex-col">
        <span class="font-bold">Old Submission Folders</span>
        <span class="text-gray px-5 text-xs"
          >The submissions in these folders will not be compared with each other</span
        >
        <div class="mt-2 flex w-full flex-col gap-2">
          <ToolTipComponent
            v-for="[i,s] in state.oldSubmissions.entries()"
            :key="s"
            direction="left"
            :show-info-symbol="false"
            class="w-full"
          >
            <template #default>
              <ContainerComponent>
                <div class="flex items-center gap-1">
                  <span class="flex-1">{{ getDisplayName(s) }}</span>
                  <FontAwesomeIcon :icon="faTrash" @click="state.oldSubmissions.splice(i,1)" />
                </div>
              </ContainerComponent>
            </template>
            <template #tooltip>SubNam1, SubName2, ...</template>
          </ToolTipComponent>
          <ContainerComponent @click="addOldSubmission()">
            <FontAwesomeIcon :icon="faPlus" /> Add Submission Folder
          </ContainerComponent>
        </div>
      </div>
    </ContainerComponent>

    <ContainerComponent class="col-span-2">
      <div>
        <span class="font-bold">Submission Folders</span>
        <span class="text-gray px-5 text-xs"
          >This folder contains template code, that is excluded from comparisons</span
        >
        <div v-if="state.basecode" class="grid-rows[auto_auto] grid grid-cols-2">
          <span class="cols-span-2">{{ state.basecode }}</span>
          <ContainerComponent>
            <FontAwesomeIcon :icon="faRepeat" @click="setBaseCode" /> Change Basecode Folder
          </ContainerComponent>
          <ContainerComponent>
            <FontAwesomeIcon :icon="faTrash" @click="state.basecode = undefined" /> Remove Basecode Folder
          </ContainerComponent>
        </div>
        <ContainerComponent v-else @click="setBaseCode">
          <FontAwesomeIcon :icon="faPlus" /> Set Basecode Folder
        </ContainerComponent>
      </div>
    </ContainerComponent>
  </div>
</template>

<script setup lang="ts">
import { getFolderName } from '../model/FileDialog'
import { SubmissionsState } from '../model/Steps'
import { faPlus, faTrash, faRepeat } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { ContainerComponent } from '@jplag/ui-components/base'
import ToolTipComponent from '@jplag/ui-components/base/ToolTipComponent.vue'

const state = defineModel<SubmissionsState>({ required: true })

async function addSubmission() {
  const name = await getFolderName()
  if (!name) return
  state.value.newSubmissions.push(name)
}

async function addOldSubmission() {
  const name = await getFolderName()
  if (!name) return
  state.value.oldSubmissions.push(name)
}

async function setBaseCode() {
  const name = await getFolderName()
  if (!name) return
  state.value.basecode = name
}

function getDisplayName(s: string) {
  return s.split(/\\|\//).slice(-1)[0]
}
</script>