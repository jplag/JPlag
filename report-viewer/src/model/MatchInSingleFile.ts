/**
 * Describes a match in a single file.
 */
export type MatchInSingleFile = {
    start: number,
    end: number,
    /**
     * The files container containing the file of the second submission to which this is matched.
     */
    linked_panel: number,
    /**
     * The file name containing the same match in the second submission.
     */
    linked_file: string,
    /**
     * The start of the match in the second file.
     */
    linked_line: number,
    color: string
}