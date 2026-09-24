/**
 * Stores the identified clusters, with information about the matches between the submissions.
 * @property averageSimilarity - The average similarity of the submissions in the cluster.
 * @property strength - The strength of the cluster
 * @property members - The ids of the submissions in the cluster, and the matches between them
 */
type ClusterListElement = {
  averageSimilarity: number
  strength: number
  members: ClusterListElementMember
}
/**
 * The ids of the submissions in the cluster, and the matches between them
 */
type ClusterListElementMember = Map<string, Array<{ matchedWith: string; similarity: number }>>
export type { ClusterListElement, ClusterListElementMember }
