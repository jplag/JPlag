import type { Cluster, ReportFormatCluster } from '../Cluster'
import { BaseFactory } from './BaseFactory'

export class ClusterFactory extends BaseFactory {
  public static async getClusters(): Promise<Cluster[]> {
    return this.extractClusters(JSON.parse(await this.getFile('cluster.json')))
  }

  private static extractClusters(json: ReportFormatCluster[]): Cluster[] {
    return json.map((cluster, index) => {
      return {
        ...cluster,
        index
      }
    })
  }
}
