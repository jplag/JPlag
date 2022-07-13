import { Comparison } from "../Comparison";
import { Match } from "../Match";
import { SubmissionFile } from "../SubmissionFile";
import { MatchInSingleFile } from "../MatchInSingleFile";

export class ComparisonFactory {
  static getComparison(json: Record<string, unknown>): Comparison {
    const filesOfFirst = this.convertToFilesByName(
      json.files_of_first_submission as Array<Record<string, unknown>>
    );
    const filesOfSecond = this.convertToFilesByName(
      json.files_of_second_submission as Array<Record<string, unknown>>
    );

    const matches = json.matches as Array<Record<string, unknown>>;
    const colors = this.generateColorsForMatches(matches.length);
    const coloredMatches = matches.map((match, index) =>
      this.mapMatch(match, colors[index])
    );

    const matchesInFirst = this.groupMatchesByFileName(coloredMatches, 1);
    const matchesInSecond = this.groupMatchesByFileName(coloredMatches, 2);

    const comparison = new Comparison(
      json.first_submission_id as string,
      json.second_submission_id as string,
      json.match_percentage as number
    );
    comparison.filesOfFirstSubmission = filesOfFirst;
    comparison.filesOfSecondSubmission = filesOfSecond;
    comparison.colors = colors;
    comparison.allMatches = coloredMatches;
    comparison.matchesInFirstSubmission = matchesInFirst;
    comparison.matchesInSecondSubmissions = matchesInSecond;

    return comparison;
  }

  private static convertToFilesByName(
    files: Array<Record<string, unknown>>
  ): Map<string, SubmissionFile> {
    const map = new Map<string, SubmissionFile>();
    files.forEach((val) => {
      if (!map.get(val.file_name as string)) {
        map.set(val.file_name as string, {
          lines: [],
          collapsed: false,
        });
      }
      map.set(val.file_name as string, {
        lines: val.lines as Array<string>,
        collapsed: false,
      });
    });
    return map;
  }

  private static groupMatchesByFileName(
    matches: Array<Match>,
    index: number
  ): Map<string, Array<MatchInSingleFile>> {
    const acc = new Map<string, Array<MatchInSingleFile>>();
    matches.forEach((val) => {
      const name =
        index === 1 ? (val.firstFile as string) : (val.secondFile as string);
      if (!acc.get(name)) {
        acc.set(name, []);
      }
      if (index === 1) {
        const newVal: MatchInSingleFile = {
          start: val.startInFirst as number,
          end: val.endInFirst as number,
          linked_panel: 2,
          linked_file: val.secondFile as string,
          linked_line: val.startInSecond as number,
          color: val.color as string,
        };
        acc.get(name)?.push(newVal);
      } else {
        const newVal: MatchInSingleFile = {
          start: val.startInSecond as number,
          end: val.endInSecond as number,
          linked_panel: 1,
          linked_file: val.firstFile as string,
          linked_line: val.startInFirst as number,
          color: val.color as string,
        };
        acc.get(name)?.push(newVal);
      }
    });
    return acc;
  }

  private static generateColorsForMatches(num: number): Array<string> {
    const colors = [];
    const hueDelta = Math.trunc(360 / num);

    for (let i = 0; i < num; i++) {
      const hue = i * hueDelta;

      colors.push(`hsla(${hue}, 80%, 50%, 0.3)`);
    }
    return colors;
  }

  private static mapMatch(
    match: Record<string, unknown>,
    color: string
  ): Match {
    return {
      firstFile: match.first_file_name as string,
      secondFile: match.second_file_name as string,
      startInFirst: match.start_in_first as number,
      endInFirst: match.end_in_first as number,
      startInSecond: match.start_in_second as number,
      endInSecond: match.end_in_second as number,
      tokens: match.tokens as number,
      color: color,
    };
  }
}
