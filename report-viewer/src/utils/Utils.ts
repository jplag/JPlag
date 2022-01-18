export function convertToFilesByName(files: Array<Record<string, unknown>>): Record<string, unknown> {
    return files.reduce( (acc: Record<string, unknown>, val: Record<string, unknown>) => {
        if(!acc[val.file_name as string]) {
            acc[val.file_name as string] = {}
        }
        acc[val.file_name as string] = {
            lines: val.lines,
            collapsed: false
        }
        return acc;
    }, {})
}

export function groupMatchesByFileName(matches: Array<Record<string, unknown>>, index: number): Record<string, unknown> {
    return matches.reduce( ( acc: Record<string, Array<unknown> >, val: Record<string, unknown> ) => {
        const name = index === 1 ? val.first_file_name as string : val.second_file_name as string
        if(!acc[name]) {
            acc[name] = []
        }
        if( index === 1 ) {
            const newVal = {
                start : val.start_in_first,
                end: val.end_in_first,
                linked_panel: 2,
                linked_file: val.second_file_name,
                linked_line: val.start_in_second,
                color: val.color
            }
            acc[name].push(newVal)
        } else {
            const newVal = {
                start : val.start_in_second,
                end: val.end_in_second,
                linked_panel: 1,
                linked_file: val.first_file_name,
                linked_line: val.start_in_first,
                color: val.color
            }
            acc[name].push(newVal)
        }
        return acc;
    }, {})
}

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

export function generateColor(): string {
    let color = "#";
    for (let i = 0; i < 3; i++)
        color += ("0" + Math.floor(((1 + Math.random()) * Math.pow(16, 2)) / 2).toString(16)).slice(-2);
    return color;
}

export function generateColorsForMatches( num : number ): Array<unknown> {
    const colors = []
    const hueDelta = Math.trunc(360 / (num) )

    for( let i = 0; i < num; i++ ) {
        const hue = i * hueDelta

        colors.push(`hsla(${hue}, 80%, 50%, 0.5)`)
    }
    return colors
}