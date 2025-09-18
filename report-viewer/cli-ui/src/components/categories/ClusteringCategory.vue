<template>
  <CliViewCategory :class="enableClustering ? 'h-full' : 'h-fit'" :show-content="enableClustering">
    <template #heading>
      <span class="flex items-center gap-2">
        <SwitchComponent v-model="enableClustering" clas="col-start-1" />
        <span class="col-start-2">Clustering</span>

        <ToolTipComponent direction="left">
          <template #tooltip>
            <p class="max-w-32 text-sm font-normal whitespace-pre-wrap">
              {{ CliToolTip.CLUSTERING }}
            </p>
          </template>
        </ToolTipComponent>
      </span>
    </template>

    <CliUiOption
      label="Algorithm"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.CLUSTER_ALGORITHM"
    >
      <OptionsSelectorComponent
        :labels="ClusteringAlgorithmList"
        @selection-changed="(i) => (clusteringAlgorithm = ClusteringAlgorithmList[i])"
      />
    </CliUiOption>
    <CliUiOption
      label="Metric"
      :scroll-offset-y="scrollOffsetY"
      :tooltip="CliToolTip.CLUSTER_METRIC"
    >
      <MetricSelector
        :metrics="ClusterMetricList"
        @selection-changed="(m) => (clusterMetric = m)"
      />
    </CliUiOption>
  </CliViewCategory>
</template>

<script setup lang="ts">
import { CliToolTip } from '../../model/CliToolTip'
import CliUiOption from '../CliUiOption.vue'
import CliViewCategory from '../CliViewCategory.vue'
import { MetricSelector, OptionsSelectorComponent } from '@jplag/ui-components/widget'
import SwitchComponent from '../SwitchComponent.vue'
import { MetricJsonIdentifier } from '@jplag/model'

defineProps({
  scrollOffsetY: {
    type: Number,
    required: false,
    default: 0
  }
})

const enableClustering = defineModel<boolean>('enableClustering', {
  default: true
})
const clusteringAlgorithm = defineModel<string>('clusteringAlgorithm', {
  default: 'SPECTRAL'
})
const clusterMetric = defineModel<MetricJsonIdentifier>('clusterMetric', {
  default: MetricJsonIdentifier.AVERAGE_SIMILARITY
})

const ClusteringAlgorithmList = ['SPECTRAL', 'AGGLOMERATIVE']
const ClusterMetricList: MetricJsonIdentifier[] = [
  MetricJsonIdentifier.AVERAGE_SIMILARITY,
  MetricJsonIdentifier.MAXIMUM_SIMILARITY
]
</script>
