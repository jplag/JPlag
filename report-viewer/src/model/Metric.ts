import {ComparisonListElement} from "./ComparisonListElement";

export type Metric = {
    metricName : string,
    metricThreshold : number,
    distribution : Array<number>,
    comparisons : Array<ComparisonListElement>
}