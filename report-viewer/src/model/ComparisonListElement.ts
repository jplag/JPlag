/**
 * Comparison model used by the Comparison Table in Overview. Only the needed attributes to display are included.
 * For full comparison model see Comparison.ts
 */
export type ComparisonListElement = {
    firstSubmissionId: string,
    secondSubmissionId: string,
    matchPercentage: number
}