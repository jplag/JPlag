import type { Cluster, ReportFormatCluster } from '@jplag/model'

export class ClusterFactory {
  public static getClusters(clusterFile: string): Cluster[] {
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
