/**
 * Comparison model used by the Comparison Table in Overview. Only the needed attributes to display are included.
 * For full comparison model see Comparison.ts
 * @see Comparison
 * @property id - Id of the comparison
 * @property firstSubmissionId - Id of the first submission
 * @property secondSubmissionId - Id of the second submission
 * @property similarity - Similarity of the two submissions
 */
export type ComparisonListElement = {
  id: number
  firstSubmissionId: string
  secondSubmissionId: string
  similarity: number
}
