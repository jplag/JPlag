Testing Maintainability
--
Observing the number of affected artefacts when adding new information to the
report to be displayed.
Adding the number of tokens in a match, which has to be displayed in the MatchesTable in the ComparisonView.
1. Adding int tokens to Match.java [JPlag]
2. Edited convertMatchToReportMatch in ReportObejctFactory to get # token from de.jplag.Match
	and save it in Match DTO [JPlag]
3. Added tokens: number to Match.ts [report-viewer]
4. Edited mapMatch in ComparisonFactory.ts to get # tokens from the json file [report-viewer]
5. Edited MatchTable.vue to display the tokens number in the ComparisonView