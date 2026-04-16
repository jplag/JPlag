<template>
    <div class="flex w-full gap-5">
        <div class="flex flex-col gap-2">
            <div
                v-for="l of langs"
                :key="l[0]"
                class="flex items-center gap-8"
            >
                <span>
                    <OptionComponent class="min-w-32" :selected="l[2]" @click="l[2] = !l[2]">{{ l[0] }}</OptionComponent>
                </span>
                <span class="float-right">{{ l[1] }} files in submission</span>
            </div>
        </div>

        <ContainerComponent class="flex-1">
            <div>
                <DropDownSelector :options="selectedLanguages" />
                <div>
                    Settings
                    <div>
                        <span>Minimum Match Length</span>
                        <input type="number" class="border rounded-md" />
                    </div> 
                </div>
            </div>
        </ContainerComponent>
    </div>    
</template>

<script setup lang="ts">
import { Language, ParserLanguage } from '@jplag/model';
import { ContainerComponent } from '@jplag/ui-components/base'
import DropDownSelector from '@jplag/ui-components/base/DropDownSelector.vue';
import { OptionComponent } from '@jplag/ui-components/widget'
import { computed, ref } from 'vue';

type L = [ParserLanguage, number, boolean]
const langs = ref<L[]>(Object.values(ParserLanguage).map(l => [l as ParserLanguage, 0, false] as L))
langs.value[0][1] = 756
langs.value[1][1] = 89
langs.value[2][1] = 20
langs.value[3][1] = 2
for (const l of langs.value) {
    if (l[1] > 0) l[2] = true
}

const selectedLanguages = computed(() => langs.value.filter(l => l[2]).map(l => l[0]))
function getDefaultTokenMatch(l: Language)

</script>