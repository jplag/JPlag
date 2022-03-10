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

    private _filesOfFirstSubmission: Record<string, SubmissionFile>

    get filesOfFirstSubmission(): Record<string, SubmissionFile> {
        return this._filesOfFirstSubmission;
    }

    set filesOfFirstSubmission(value: Record<string, SubmissionFile>) {
        this._filesOfFirstSubmission = value;
    }

    private _filesOfSecondSubmission: Record<string, SubmissionFile>

    get filesOfSecondSubmission(): Record<string, SubmissionFile> {
        return this._filesOfSecondSubmission;
    }

    set filesOfSecondSubmission(value: Record<string, SubmissionFile>) {
        this._filesOfSecondSubmission = value;
    }

    private _colors: Array<string>

    get colors(): Array<string> {
        return this._colors;
    }

    set colors(value: Array<string>) {
        this._colors = value;
    }

    private _allMatches: Array<Match>

    get allMatches(): Array<Match> {
        return this._allMatches;
    }

    set allMatches(value: Array<Match>) {
        this._allMatches = value;
    }

    private _matchesInFirstSubmission: Record<string, Array<MatchInSingleFile>>

    get matchesInFirstSubmission(): Record<string, Array<MatchInSingleFile>> {
        return this._matchesInFirstSubmission;
    }

    set matchesInFirstSubmission(value: Record<string, Array<MatchInSingleFile>>) {
        this._matchesInFirstSubmission = value;
    }

    private _matchesInSecondSubmissions: Record<string, Array<MatchInSingleFile>>

    get matchesInSecondSubmissions(): Record<string, Array<MatchInSingleFile>> {
        return this._matchesInSecondSubmissions;
    }

    set matchesInSecondSubmissions(value: Record<string, Array<MatchInSingleFile>>) {
        this._matchesInSecondSubmissions = value;
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
}