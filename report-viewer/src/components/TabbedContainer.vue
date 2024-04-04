<template>
  <ContainerComponent class="flex flex-col overflow-hidden !p-0">
    <div class="flex w-full bg-container-secondary-light dark:bg-container-secondary-dark">
      <div
        v-for="index in Array(props.tabs.length).keys()"
        class="cursor-pointer border-r border-container-border-light dark:border-container-border-dark"
        @click="selectedTab = tabNames[index]"
        :key="index"
        :class="
          tabNames[index] == selectedTab
            ? 'border-b-0 bg-container-light dark:bg-container-dark'
            : 'border-b bg-container-secondary-light dark:bg-container-secondary-dark'
        "
      >
        <ToolTipComponent
          v-if="toolTips[index]"
          :direction="index < firstBottomTooltipIndex ? 'right' : 'bottom'"
        >
          <template #default>
            <p class="p-2 px-5">{{ tabNames[index] }}</p>
          </template>
          <template #tooltip>
            <p class="whitespace-pre text-sm">{{ toolTips[index] }}</p>
          </template>
        </ToolTipComponent>
        <p v-else class="p-2 px-5">{{ tabNames[index] }}</p>
      </div>
      <div
        class="flex-1 border-b border-container-border-light dark:border-container-border-dark"
      ></div>
    </div>
    <div class="flex-1 flex-col overflow-hidden p-2">
      <slot :name="selectedTab.replace(/\s/g, '-')"></slot>
    </div>
  </ContainerComponent>
</template>

<script setup lang="ts">
import { computed, ref, type Ref } from 'vue'
import ContainerComponent from './ContainerComponent.vue'
import type { ToolTipLabel } from '@/model/ui/ToolTip'
import ToolTipComponent from './ToolTipComponent.vue'

const props = defineProps({
  tabs: {
    type: Array<string | ToolTipLabel>,
    required: true
  },
  firstBottomTooltipIndex: {
    type: Number,
    required: false,
    default: 2
  }
})

const tabNames = computed(() =>
  props.tabs.map((tab) => (typeof tab === 'string' ? tab : tab.displayValue))
)
const toolTips = computed(() =>
  props.tabs.map((tab) => (typeof tab === 'string' ? null : tab.tooltip))
)

const _selectedTab: Ref<string | null> = ref(null)
const selectedTab = computed({
  set(value: string) {
    _selectedTab.value = value
  },
  get() {
    if (!_selectedTab.value) {
      return tabNames.value[0]
    }
    return _selectedTab.value
  }
})
</script>
