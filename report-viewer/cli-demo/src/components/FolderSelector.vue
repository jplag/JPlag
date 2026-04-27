<template>
    <ContainerComponent>
        <div class="flex flex-col gap-2">
            <div class="flex items-center gap-2">
                <h1 class="text-xl">{{ header }} </h1>
                <ToolTipWrapper direction="right" :text="tooltip"/>
                <div class="flex-1"></div>
                <ButtonComponent @click="fileChooser()"><FontAwesomeIcon :icon="faPlus" /> Add Submission Folder</ButtonComponent>
                <ButtonComponent @click="fileChooser()"><FontAwesomeIcon :icon="faPlus" /> Add Single Submission</ButtonComponent>
            </div>
            <div>
                <div
                    v-for="l of list"
                    :key="l"
                >
                    <span>{{ l }}</span>
                    <FontAwesomeIcon class="float-right cursor-pointer" :icon="faTrash" @click="removeItem(l)" />
                </div>
            </div>
        </div>
    </ContainerComponent>
</template>

<script setup lang="ts">
import ButtonComponent from '@jplag/ui-components/base/ButtonComponent.vue';
import ContainerComponent from '@jplag/ui-components/base/ContainerComponent.vue';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faPlus, faTrash } from '@fortawesome/free-solid-svg-icons'
import { ref } from 'vue';
import { getFolder } from '../helper';
import ToolTipWrapper from './ToolTipWrapper.vue';

defineProps({
    header: {
        type: String,
        required: true
    },
    tooltip: {
        type: String,
        required: true
    }
})

const list = defineModel<string[]>({
    type: Array,
    default: () => []
})

function removeItem(item: string) {
    list.value = list.value.filter(i => i !== item);
}

async function fileChooser() {
    const name = await getFolder()
    if (name) {
        list.value.push(name)
    }
}

</script>