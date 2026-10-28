<template>
  <div class="flex flex-col gap-2">
    <LanguageSelector v-for="[idx, lang] in languageList.entries()" :key="lang" v-model:selected="bools[idx]" :name="lang" :options="{minTokenMatch: 9, normalize: false, fileEndings: ['.something']}" :count="configState().parsingAnswer?.detectedLanguages[lang] ?? 0"   />
  </div>
</template>

<script setup lang="ts">
import LanguageSelector from '../components/LanguageSelector.vue';
import { LanguageState } from '../model/Steps';
import { ParserLanguage } from '@jplag/model';
import { ref, watch } from 'vue';
import { configState } from '../state.js';

const languageList = [
  ParserLanguage.JAVA,
  ParserLanguage.PYTHON,
  ParserLanguage.C,
  ParserLanguage.CPP,
  ParserLanguage.C_SHARP,
  ParserLanguage.EMF_METAMODEL_DYNAMIC,
  ParserLanguage.EMF_MODEL,
  ParserLanguage.EMF_METAMODEL
]

const model = defineModel<LanguageState>({required: true})
const bools = ref(Array.from({length: languageList.length}, (_, i) => model.value?.selectedLanguages.includes(languageList[i])))

watch(() => bools, () => {
  model.value.selectedLanguages = []
  for (let i = 0; i <= languageList.length; i++) {
    if (bools.value[i]) model.value.selectedLanguages.push(languageList[i])
  }
}, {deep: true})


</script>
