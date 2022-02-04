import {Match} from "./Match";
import {SubmissionFile} from "./SubmissionFile"
import {MatchInSingleFile} from "./MatchInSingleFile";

/**
 * Comparison model used by the ComparisonView
 */
export class Comparison {
    private readonly _firstSubmissionId: string
    private readonly _secondSubmissionId: string
    private readonly _match_percentage: number
    private _filesOfFirstSubmission: Record<string, SubmissionFile>
    private _filesOfSecondSubmission: Record<string, SubmissionFile>
    private _colors: Array<string>
    private _allMatches: Array<Match>
    private _matchesInFirstSubmission: Record<string, Array<MatchInSingleFile>>
    private _matchesInSecondSubmissions: Record<string, Array<MatchInSingleFile>>


    constructor(firstSubmissionId: string, secondSubmissionId: string, match_percentage: number) {
        this._firstSubmissionId = firstSubmissionId;
        this._secondSubmissionId = secondSubmissionId;
        this._match_percentage = match_percentage;
        this._filesOfFirstSubmission = {}
        this._filesOfSecondSubmission = {}
        this._colors = []
        this._allMatches = []
        this._matchesInFirstSubmission = {}
        this._matchesInSecondSubmissions = {}
    }


    get firstSubmissionId(): string {
        return this._firstSubmissionId;
    }

    get secondSubmissionId(): string {
        return this._secondSubmissionId;
    }

    get match_percentage(): number {
        return this._match_percentage;
    }

    get filesOfFirstSubmission(): Record<string, SubmissionFile> {
        return this._filesOfFirstSubmission;
    }

    set filesOfFirstSubmission(value: Record<string, SubmissionFile>) {
        this._filesOfFirstSubmission = value;
    }

    get filesOfSecondSubmission(): Record<string, SubmissionFile> {
        return this._filesOfSecondSubmission;
    }

    set filesOfSecondSubmission(value: Record<string, SubmissionFile>) {
        this._filesOfSecondSubmission = value;
    }

    get colors(): Array<string> {
        return this._colors;
    }

    set colors(value: Array<string>) {
        this._colors = value;
    }

    get allMatches(): Array<Match> {
        return this._allMatches;
    }

    set allMatches(value: Array<Match>) {
        this._allMatches = value;
    }

    get matchesInFirstSubmission(): Record<string, Array<MatchInSingleFile>> {
        return this._matchesInFirstSubmission;
    }

    set matchesInFirstSubmission(value: Record<string, Array<MatchInSingleFile>>) {
        this._matchesInFirstSubmission = value;
    }

    get matchesInSecondSubmissions(): Record<string, Array<MatchInSingleFile>> {
        return this._matchesInSecondSubmissions;
    }

    set matchesInSecondSubmissions(value: Record<string, Array<MatchInSingleFile>>) {
        this._matchesInSecondSubmissions = value;
    }
}