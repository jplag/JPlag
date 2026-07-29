<template>
  <div
    style="
      border-radius: 12px;
      background-color: var(--vp-c-bg-soft);
      display: flex;
      padding: 10px;
      padding-left: 20px;
      margin-bottom:10px;
    "
  >
    <img
      style="height: 60px; margin-right: 15px; margin-top: 10px"
      src="/img/paper-dark.svg"
      v-if="isDark"
    />
    <img
      style="height: 60px; margin-right: 15px; margin-top: 10px"
      src="/img/paper-light.svg"
      v-if="!isDark"
    />
    <p>
      {{ props.authors }}, "<a :href="paperURL" style="font-weight: bold">{{
        props.title
      }}</a
      >", <span v-if="props.linebreak"><br /></span>{{ props.reference
      }}<span v-if="doi"
        >, doi: <a :href="doiURL">{{ props.doi }}</a></span
      >.
    </p>
  </div>
</template>

<script setup>
import { useData } from "vitepress";
import { ref } from "vue";

const isDark = useData().isDark;
const props = defineProps(["authors", "title", "reference", "url", "doi", "linebreak"]);

const doiURL = ref("https://doi.org/" + props.doi);
const paperURL = props.url ? props.url : doiURL;
</script>
