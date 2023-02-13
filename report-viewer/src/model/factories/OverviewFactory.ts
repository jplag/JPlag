import { Overview } from "../Overview";
import { Metric } from "../Metric";
import { ComparisonListElement } from "../ComparisonListElement";
import { Cluster } from "@/model/Cluster";
import store from "@/store/store";
import { Version } from "../Version";
import versionJson from "../../version.json";

export class OverviewFactory {

  static reportViewerVersion: Version = versionJson["report_viewer_version"] !== undefined ? versionJson["report_viewer_version"] : {major: -1, minor: -1, patch: -1};

  static getOverview(json: Record<string, unknown>): Overview {
    const versionField = json.jplag_version as Record<string, number>;
    const jplagVersion: Version = {
      major: versionField.major,
      minor: versionField.minor,
      patch: versionField.patch
    }

    OverviewFactory.compareVersions(jplagVersion, this.reportViewerVersion);

    const submissionFolder = json.submission_folder_path as Array<string>;
    const baseCodeFolder = "";
    const language = json.language as string;
    const fileExtensions = json.file_extensions as Array<string>;
    const matchSensitivity = json.match_sensitivity as number;
    const jsonSubmissions = json.submission_id_to_display_name as Map<string, string>;
    const map = new Map<string, string>(Object.entries(jsonSubmissions));
    const dateOfExecution = json.date_of_execution as string;
    const duration = json.execution_time as number as number;
    const metrics = [] as Array<Metric>;
    const clusters = [] as Array<Cluster>;
    const totalComparisons= json.total_comparisons as number;
    (json.metrics as Array<unknown>).forEach((jsonMetric) => {
      const metric = jsonMetric as Record<string, unknown>;
      const comparisons = [] as Array<ComparisonListElement>;

      (metric.topComparisons as Array<Record<string, unknown>>).forEach(
        (jsonComparison,index) => {
          const comparison: ComparisonListElement = {
            id: index + 1 as number,
            firstSubmissionId: jsonComparison.first_submission as string,
            secondSubmissionId: jsonComparison.second_submission as string,
            similarity: jsonComparison.similarity as number,
          };
          comparisons.push(comparison);
        }
      );
      metrics.push({
        metricName: metric.name as string,
        metricThreshold: metric.threshold as number,
        distribution: metric.distribution as Array<number>,
        comparisons: comparisons,
        description: metric.description as string,
      });
    });
    store.commit("saveSubmissionNames", map);
    if (json.clusters) {
      (json.clusters as Array<unknown>).forEach((jsonCluster) => {
        const cluster = jsonCluster as Record<string, unknown>;
        const newCluster: Cluster = {
          averageSimilarity: cluster.average_similarity as number,
          strength: cluster.strength as number,
          members: cluster.members as Array<string>,
        };
        clusters.push(newCluster);
      });
    }
    
    OverviewFactory.saveSubmissionsToComparisonNameMap(json);
    return new Overview(
      submissionFolder,
      baseCodeFolder,
      language,
      fileExtensions,
      matchSensitivity,
      dateOfExecution,
      duration,
      metrics,
      clusters,
      totalComparisons,
      new Map()
    );
  }

  static compareVersions(jsonVersion: Version, reportViewerVersion: Version) {
    if(sessionStorage.getItem("versionAlert")===null) {

      if (reportViewerVersion.major === 0 && reportViewerVersion.minor === 0 && reportViewerVersion.patch === 0) {
        alert("The development version (0.0.0) of JPlag is used.");
      }

      if (jsonVersion.major !== reportViewerVersion.major ||
          jsonVersion.minor !== reportViewerVersion.minor ||
          jsonVersion.patch !== reportViewerVersion.patch) {
        if(reportViewerVersion.major === -1 && reportViewerVersion.minor === -1 && reportViewerVersion.patch === -1){
          console.warn("The report viewer's version cannot be read from version.json file. Please configure it correctly.");
        } else {
          console.warn("The result's version tag does not fit the report viewer's version. Trying to read it anyhow but be careful.");
          alert("The result's version(" + jsonVersion.major + "." + jsonVersion.minor + "." + jsonVersion.patch + ") tag does not fit the report viewer's version(" + reportViewerVersion.major + "." + reportViewerVersion.minor + "." + reportViewerVersion.patch + "). " +
              "Trying to read it anyhow but be careful.")
        }
      }

      sessionStorage.setItem("versionAlert","true");
    }
  }

  private static  saveSubmissionsToComparisonNameMap(json: Record<string, unknown>){
    const submissionIdsToComparisonName =
    json.submission_ids_to_comparison_file_name as Map<
      string,
      Map<string, string>
    >;
  const test: Array<Array<string | object>> = Object.entries(
    submissionIdsToComparisonName
  );
  const comparisonMap = new Map<string, Map<string, string>>(
  );
  for (const [key, value] of test) {
    comparisonMap.set(key as string, new Map(Object.entries(value as object)));
  }

  store.commit("saveComparisonFileLookup", comparisonMap);
  }
}
