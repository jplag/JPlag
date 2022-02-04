export function generateLineCodeLink(index: number, fileName: string, line: number): string {
    return String(index).concat(fileName).concat(String(line))
}

export function getFileExtension(file: File): string {
    if(file.name.includes(".")) {
        return file.name.split(".")[1]
    }
    return "";
}