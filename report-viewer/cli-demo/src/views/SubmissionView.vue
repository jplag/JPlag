<template>
    <div class="grid grid-cols-2 gap-5">
        <FolderSelector v-model="store().cliOptions.submissionDirectories" header="New Submissions" :tooltip="CliToolTip.ROOT_DIRS" />
        <FolderSelector v-model="store().cliOptions.oldSubmissionDirectories"  header="Old Submissions" :tooltip="CliToolTip.OLD"/>

        <ContainerComponent class="col-span-2">
        <div class="flex flex-col gap-2">
            <div class="flex items-center gap-2">
                <h1 class="text-xl">Basecode</h1>
                <ToolTipWrapper direction="right" :text="CliToolTip.BASE_CODE"/>
                <div class="flex-1"></div>
                <ButtonComponent @click="chooseBaseCode()"><FontAwesomeIcon :icon="faPlus" />{{ store().cliOptions.baseCodeSubmissionDirectory !== '' ? 'Change' : 'Set' }}</ButtonComponent>
            </div>
            <div>
                <div
                    v-if="store().cliOptions.baseCodeSubmissionDirectory !== ''"
                >
                    <span>{{ store().cliOptions.baseCodeSubmissionDirectory }}</span>
                    <FontAwesomeIcon class="float-right cursor-pointer" :icon="faTrash" @click="store().cliOptions.baseCodeSubmissionDirectory = ''" />
                </div>
            </div>
        </div>
    </ContainerComponent>
    </div>
</template>

<script setup lang="ts">
import { CliToolTip } from '../model/ToolTips';
import FolderSelector from '../components/FolderSelector.vue';
import ContainerComponent from '@jplag/ui-components/base/ContainerComponent.vue';
import { getFolder } from '../helper';
import { faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';
import ButtonComponent from '@jplag/ui-components/base/ButtonComponent.vue';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import ToolTipWrapper from '../components/ToolTipWrapper.vue';
import { store } from '../store';

async function chooseBaseCode() {
    const name = await getFolder()
    if (name) {
        store().cliOptions.baseCodeSubmissionDirectory = name
    }
}
</script>