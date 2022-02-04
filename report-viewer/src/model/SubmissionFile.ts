export type SubmissionFile = {
    /**
     * Code lines of the file.
     */
    lines: Array<string>;
    /**
     * Indicates whether the file is displayed or not.
     */
    collapsed: boolean;
}