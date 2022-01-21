export function generateLineCodeLink(index: number, fileName: string, line: number): string {
    return String(index).concat(fileName).concat(String(line))
}

export function generateColoringArray(array: Array<Record<string, unknown>>, fileToConsider: number): Array<Record<string, unknown>> {
    switch (fileToConsider) {
        case 1:
            return array.map( m => {
                //Add connection to second panel via m.start_in_second
                return { start: m.start_in_first, end: m.end_in_first, link: m.start_in_second, color: m.color}
            })
        case 2:
            return array.map( m => {
                return { start: m.start_in_second, end: m.end_in_second, link: m.start_in_first, color: m.color}
            })
        default:
            return []
    }
}