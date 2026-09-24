import { describe, it, expect } from 'vitest'
import validClusters from './assets/ValidCluster.json?raw'
import { ClusterFactory } from '../../src'

describe('Test JSON to Cluster list', () => {
  it('Test Valid JSON', () => {
    const result = ClusterFactory.getClusters(validClusters)

    expect(result.length).toBe(2)
    expect(result[0].members.length).toBe(3)
    expect(result[0].averageSimilarity).toBe(0.85)
    expect(result[0].index).toBe(0)

    expect(result[1].members.length).toBe(3)
    expect(result[1].averageSimilarity).toBe(0.75)
    expect(result[1].index).toBe(1)
  })
})
