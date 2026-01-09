<template>
    <div class="px-4 py-2 flex gap-10">
        <h1 class="text-xl">JPlag</h1>
        <nav>
  <ul class="flex gap-6">
    <li
      v-for="item in items"
      :key="item.label"
      class="relative"
      @mouseenter="open = item.label"
      @mouseleave="open = null"
    >
      <button>
        {{ item.label }}
        <FontAwesomeIcon class="ml-1" :icon="faChevronDown" />
      </button>

      <ul
        v-if="open === item.label"
        class="absolute w-full translate-y-2 top-full left-0 bg-container-light px-2 last:pb-2 border border-container-border-light rounded-b-md mt-1 border-t-0"
      >
        <li
          v-for="child in item.children"
          :key="child"
        >
          {{ child }}
        </li>
      </ul>
    </li>
  </ul>
</nav>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faChevronDown } from '@fortawesome/free-solid-svg-icons'

const open = ref<string|null>(null);

const items = [
  {
    label: "Config",
    children: ["Load", "Save"],
  },
  {
    label: "Session",
    children: ["Load", "Save"],
  },
  {
    label: "Report",
    children: ["Load", "Save"],
  },
];
</script>