import { Metric } from "./Metric";
import { Cluster } from "@/model/Cluster";

export class Overview {
  private readonly _submissionFolderPath: Array<string>;
  private readonly _baseCodeFolderPath: string;
  private readonly _language: string;
  private readonly _fileExtensions: Array<string>;
  private readonly _matchSensitivity: number;
  private readonly _submissionIds: Array<string>;
  private readonly _dateOfExecution: string;
  private readonly _durationOfExecution: number;
  private readonly _metrics: Array<Metric>;
  private readonly _clusters: Array<Cluster>;

  constructor(
    submissionFolderPath: Array<string>,
    baseCodeFolderPath: string,
    language: string,
    fileExtensions: Array<string>,
    matchSensitivity: number,
    submissionIds: Array<string>,
    dateOfExecution: string,
    durationOfExecution: number,
    metrics: Array<Metric>,
    clusters: Array<Cluster>
  ) {
    this._submissionFolderPath = submissionFolderPath;
    this._baseCodeFolderPath = baseCodeFolderPath;
    this._language = language;
    this._fileExtensions = fileExtensions;
    this._matchSensitivity = matchSensitivity;
    this._submissionIds = submissionIds;
    this._dateOfExecution = dateOfExecution;
    this._durationOfExecution = durationOfExecution;
    this._metrics = metrics;
    this._clusters = clusters;
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

  get submissionIds(): Array<string> {
    return this._submissionIds;
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
}
