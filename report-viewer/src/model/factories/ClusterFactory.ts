import type { Cluster } from '../Cluster'
import { BaseFactory } from './BaseFactory'

export class ClusterFactory extends BaseFactory {
  public static async getClusters(): Promise<Cluster[]> {
    return this.extractClusters(JSON.parse(await this.getFile('cluster.json')))
  }

  private static extractClusters(json: Record<string, unknown>[]): Cluster[] {
    const clusters = [] as Cluster[]
    for (const [index, jsonCluster] of json.entries()) {
      clusters.push({
        index,
        averageSimilarity: jsonCluster.average_similarity as number,
        strength: jsonCluster.strength as number,
        members: jsonCluster.members as Array<string>
      })
    }
    return clusters
  }
}
