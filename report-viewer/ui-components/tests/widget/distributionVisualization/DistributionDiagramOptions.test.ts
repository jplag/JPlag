import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import DistributionDiagramOptions from '../../../widget/distributionVisualization/DistributionDiagramOptions.vue'
import BoxPlotDiagramOptions from '../../../widget/distributionVisualization/BoxPlotDiagramOptions.vue'
import { MetricJsonIdentifier } from '@jplag/model'

describe('Distribution diagram metric options', () => {
  const enabledMetrics = new Set([
    MetricJsonIdentifier.MAXIMUM_SIMILARITY,
    MetricJsonIdentifier.WEIGHTED_SIMILARITY
  ])
  const disabledMetrics = new Set([MetricJsonIdentifier.MAXIMUM_SIMILARITY])

  it('hides the weighted similarity option in the histogram without frequency analysis', () => {
    const wrapper = mount(DistributionDiagramOptions, {
      props: { secondaryMetrics: disabledMetrics }
    })

    expect(wrapper.text()).toContain('Average Similarity')
    expect(wrapper.text()).toContain('Maximum Similarity')
    expect(wrapper.text()).not.toContain('Weighted Avg Similarity')
  })

  it('hides the weighted similarity option in the boxplot without frequency analysis', () => {
    const wrapper = mount(BoxPlotDiagramOptions, {
      props: { secondaryMetrics: disabledMetrics }
    })

    expect(wrapper.text()).toContain('Average Similarity')
    expect(wrapper.text()).toContain('Maximum Similarity')
    expect(wrapper.text()).not.toContain('Weighted Avg Similarity')
  })

  it('offers the weighted similarity option in the histogram with frequency analysis', () => {
    const wrapper = mount(DistributionDiagramOptions, {
      props: { secondaryMetrics: enabledMetrics }
    })

    expect(wrapper.text()).toContain('Weighted Avg Similarity')
  })

  it('offers the weighted similarity option in the boxplot with frequency analysis', () => {
    const wrapper = mount(BoxPlotDiagramOptions, {
      props: { secondaryMetrics: enabledMetrics }
    })

    expect(wrapper.text()).toContain('Weighted Avg Similarity')
  })
})
