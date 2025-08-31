import { Logger } from '@jplag/logger'
import type { Cluster, ReportFormatCluster } from '@jplag/model'

export class ClusterFactory {
  public static getClusters(clusterFile: string): Cluster[] {
    Logger.label('ClusterFactory').info('Parse clusters from cluster file')
    return this.extractClusters(JSON.parse(clusterFile))
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
