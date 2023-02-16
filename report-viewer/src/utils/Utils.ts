/**
 * Generates the link for a line of code.
 * @param index Index of the files container where the line is.
 * @param fileName File in which the line is contained.
 * @param line Line number.
 */
export function generateLineCodeLink(index: number, fileName: string, line: number): string {
    return String(index).concat(fileName).concat(String(line))
}
