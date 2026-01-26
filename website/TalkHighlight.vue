<template>
  <div
    style="
      border-radius: 12px;
      background-color: var(--vp-c-bg-soft);
      display: flex;
      padding: 5px;
      padding-left: 20px;
      margin-bottom:10px;
    "
  >
    <img
      style="height: 40px; margin-right: 15px; margin-top: 10px"
      src="/img/talk-dark.svg"
      v-if="isDark"
    />
    <img
      style="height: 40px; margin-right: 15px; margin-top: 10px"
      src="/img/talk-light.svg"
      v-if="!isDark"
    />
    <p>
      {{ props.authors }}, "<a :href="url" style="font-weight: bold">{{
        props.title
      }}</a
      >", {{ props.reference
      }}.
    </p>
  </div>
</template>

<script setup>
import { useData } from "vitepress";

const isDark = useData().isDark;
const props = defineProps(["authors", "title", "reference", "url"]);
</script>
