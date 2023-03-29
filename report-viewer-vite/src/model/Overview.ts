import { Metric } from "./Metric";
import { Cluster } from "@/model/Cluster";

export class Overview {
  private readonly _submissionFolderPath: Array<string>;
  private readonly _baseCodeFolderPath: string;
  private readonly _language: string;
  private readonly _fileExtensions: Array<string>;
  private readonly _matchSensitivity: number;
  private readonly _submissionIdsToComparisonFileName: Map<string,Map<string,string>>;
  private readonly _dateOfExecution: string;
  private readonly _durationOfExecution: number;
  private readonly _metrics: Array<Metric>;
  private readonly _clusters: Array<Cluster>;
  private readonly _totalComparisons: number;

  constructor(
    submissionFolderPath: Array<string>,
    baseCodeFolderPath: string,
    language: string,
    fileExtensions: Array<string>,
    matchSensitivity: number,
    dateOfExecution: string,
    durationOfExecution: number,
    metrics: Array<Metric>,
    clusters: Array<Cluster>,
    totalComparisons: number,
    submissionIdsToComparisonFileName: Map<string,Map<string,string>>
  ) {
    this._submissionFolderPath = submissionFolderPath;
    this._baseCodeFolderPath = baseCodeFolderPath;
    this._language = language;
    this._fileExtensions = fileExtensions;
    this._matchSensitivity = matchSensitivity;
    this._dateOfExecution = dateOfExecution;
    this._durationOfExecution = durationOfExecution;
    this._metrics = metrics;
    this._clusters = clusters;
    this. _submissionIdsToComparisonFileName = submissionIdsToComparisonFileName;
    this._totalComparisons= totalComparisons;
  }
  get submissionIdsToComparisonFileName(): Map<string,Map<string,string>> {
    return this.submissionIdsToComparisonFileName;
  }

  get submissionFolderPath(): Array<string> {
    return this._submissionFolderPath;
  }

  get baseCodeFolderPath(): string {
    return this._baseCodeFolderPath;
  }

  get language(): string {
    return this._language;
  }

  get fileExtensions(): Array<string> {
    return this._fileExtensions;
  }

  get matchSensitivity(): number {
    return this._matchSensitivity;
  }


  get dateOfExecution(): string {
    return this._dateOfExecution;
  }

  get durationOfExecution(): number {
    return this._durationOfExecution;
  }

  get metrics(): Array<Metric> {
    return this._metrics;
  }

  get clusters(): Array<Cluster> {
    return this._clusters;
  }

  get totalComparisons(): number {
    return this._totalComparisons;
  }
}
