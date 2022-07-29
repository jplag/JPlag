type ClusterListElement = {
  averageSimilarity: number;
  strength: number;
  members: ClusterListElementMember;
};
type ClusterListElementMember = Map<
  string,
  Array<{ matchedWith: string; percentage: number }>
>;
export { ClusterListElement, ClusterListElementMember };
