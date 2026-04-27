<template>
    <div>
        <ContainerComponent>
            <div class="flex flex-col gap-5">
                <h1 class="font-bold text-lg">Clustering</h1>
                <div class="flex gap-3">
                        <span>Enable Clustering</span>
                    <SwitchComponent v-model="store().cliOptions.clusteringOptions.enabled" />
                    <ToolTipWrapper direction="right" :text="CliToolTip.CLUSTERING" />
                    <span></span>
                    
                </div>

                <h1 class="font-bold text-lg">Match Merging</h1>
                <div class="grid gap-3 grid-cols-[auto_300px_10px_1fr]">
                    <div class="flex gap-3 col-span-4">
                        <span>Enable Match Merging</span>
                    <SwitchComponent v-model="store().cliOptions.mergingOptions.enabled" />
                    <ToolTipWrapper direction="right" :text="CliToolTip.MATCH_MERGING" />
                    <span></span>
                    </div>
                    

                    <template v-if="store().cliOptions.mergingOptions.enabled">
                        <span>Maximum Gap Size</span>
                        <NumberInput v-model="store().cliOptions.mergingOptions.maximumGapSize" />
                        <ToolTipWrapper direction="right" :text="CliToolTip.SMM_GAP_SIZE" />
                        <span></span>

                        <span>Neighbour Length</span>
                        <NumberInput v-model="store().cliOptions.mergingOptions.minimumNeighborLength" />
                        <ToolTipWrapper direction="right" :text="CliToolTip.SMM_NEIGHBOUR_LENGTH" />
                        <span></span>

                        <span>Required Merges</span>
                        <NumberInput v-model="store().cliOptions.mergingOptions.minimumRequiredMerges" />
                        <ToolTipWrapper direction="right" :text="CliToolTip.SMM_REQUIERED_MERGES" />
                        <span></span>
                    </template>
                </div>
            </div>
            
        </ContainerComponent>
    </div>
</template>

<script setup lang="ts">
import SwitchComponent from '../components/SwitchComponent.vue';
import ToolTipWrapper from '../components/ToolTipWrapper.vue';
import { CliToolTip } from '../model/ToolTips';
import NumberInput from '../components/NumberInput.vue';
import ContainerComponent from '@jplag/ui-components/base/ContainerComponent.vue';
import { store } from '../store';
</script>