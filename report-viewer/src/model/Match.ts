/**
 * Match between two files of two submissions.
 */
export type Match = {
    firstFile: string,
    secondFile: string,
    startInFirst: number,
    endInFirst: number,
    startInSecond: number,
    endInSecond: number,
    tokens: number,
    color: string
}