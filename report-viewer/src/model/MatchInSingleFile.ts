/**
 * Describes a match in a single file.
 * @property start - Starting line of the match.
 * @property end - Ending line of the match.
 * @property linked_panel - The files container containing the file of the second submission to which this is matched.
 * @property linked_file - The file name containing the same match in the second submission.
 * @property linked_line - The start of the match in the second file.
 * @property color - Color of the match.
 */
export type MatchInSingleFile = {
  start: number
  end: number
  linked_panel: number
  linked_file: string
  linked_line: number
  color: string
}
