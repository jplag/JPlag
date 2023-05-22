/**
 * Represents a cluster identified by the clustering algorithm.
 * A cluster is a group of similar submissions.
 * @property averageSimilarity - The average similarity of the submissions in the cluster.
 * @property strength - The strength of the cluster
 * @property members - The ids of the submissions in the cluster
 */
export type Cluster = {
  averageSimilarity: number
  strength: number
  members: Array<string>
}
