class TopComparison {
    comparisonName: string;
    submission1: string;
    submission2: string
    matchPercentage: string;

    constructor(comparisonName: string, matchPercentage: number) {
        this.comparisonName = comparisonName
        const split: string[]  = comparisonName.split("-")
        this.submission1 = split[0]
        this.submission2 = split[1]
        this.matchPercentage = matchPercentage.toFixed(2)
    }

}

export default TopComparison;