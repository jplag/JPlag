export function generateLineCodeLink(index: number, fileName: string, line: number): string {
    return String(index).concat(fileName).concat(String(line))
}

export function getFileExtension(file: File): string {
    if (file.name.includes(".")) {
        const split = file.name.split(".")
        return split[split.length - 1]
    }
    return "";
}