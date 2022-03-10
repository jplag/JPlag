/**
 * Generates the link for a line of code.
 * @param index Index of the files container where the line is.
 * @param fileName File in which the line is contained.
 * @param line Line number.
 */
export function generateLineCodeLink(index: number, fileName: string, line: number): string {
    return String(index).concat(fileName).concat(String(line))
}

/**
 * Get the extension of a given file.
 * @param file The file.
 */
export function getFileExtension(file: File): string {
    if (file.name.includes(".")) {
        const split = file.name.split(".")
        return split[split.length - 1]
    }
    return "";
}