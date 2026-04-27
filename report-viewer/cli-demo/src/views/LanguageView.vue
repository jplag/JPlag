<template>
    <div class="flex w-full gap-5">
        <div class="flex flex-col gap-2">
            <div
                v-for="l of langs"
                :key="l[0]"
                class="flex items-center gap-8"
            >
                <span>
                    <OptionComponent class="min-w-32" :selected="l[2]" @click="setLang(l[0])">{{ l[0] }}</OptionComponent>
                </span>
                <span class="float-right">{{ l[1] }} files found in submissions</span>
            </div>
        </div>

        <ContainerComponent class="flex-1">
            <div class="flex flex-col gap-2">
                <DropDownSelector :options="selectedLanguages" :value="selectedLanguage" />
                <div>
                    <h1 class="font-bold text-lg">Language Specific Settings</h1>
                    <div class="grid gap-3 grid-cols-[auto_300px_10px_1fr]">
                        <span>Minimum Match Length: </span>
                        <NumberInput v-model="store().cliOptions.minimumTokenMatch[selectedLanguage]" />
                        <ToolTipWrapper direction="top" :text="CliToolTip.MIN_TOKENS" />
                        <span></span>
                        <span>File Endings: </span>
                        <InputWrapper v-model="store().cliOptions.fileSuffixes[selectedLanguage]" type="text" />
                        <span></span>
                        <span></span>
                    </div>
                    <div>
                        
                    </div> 
                    <div>
                        
                    </div>
                </div>
            </div>
        </ContainerComponent>
    </div>    
</template>

<script setup lang="ts">
import { ParserLanguage } from '@jplag/model';
import { ContainerComponent } from '@jplag/ui-components/base'
import DropDownSelector from '@jplag/ui-components/base/DropDownSelector.vue';
import { OptionComponent } from '@jplag/ui-components/widget'
import { computed, ref } from 'vue';
import InputWrapper from '../components/InputWrapper.vue';
import NumberInput from '../components/NumberInput.vue';
import ToolTipWrapper from '../components/ToolTipWrapper.vue';
import { CliToolTip } from '../model/ToolTips';
import { store } from '../store';

type L = [ParserLanguage, number, boolean]
const langs = computed({
    get: () => {
        return Object.values(ParserLanguage).map(l => [l as ParserLanguage, getFileCount(l), store().cliOptions.language.includes(l)] as L)
    },
    set: (newLangs: L[]) => {
        store().cliOptions.language = newLangs.filter(l => l[2]).map(l => l[0])
    }
})
function getFileCount(l: ParserLanguage) {
    if (l == ParserLanguage.PYTHON) {
        return 431
    } else if (l == ParserLanguage.TEXT) {
        return 120
    }
    return 0
}
function setLang(l: ParserLanguage) {
    if (store().cliOptions.language.includes(l)) {
        store().cliOptions.language = store().cliOptions.language.filter(lang => lang != l)
    } else {
        store().cliOptions.language.push(l)
    }
}

const selectedLanguages = computed(() => langs.value.filter(l => l[2]).map(l => l[0]))
const selectedLanguage = ref(selectedLanguages.value[0])
</script>