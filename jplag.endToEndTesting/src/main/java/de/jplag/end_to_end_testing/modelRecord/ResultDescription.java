// package de.jplag.end_to_end_testing.modelRecord;
//
// import java.util.Map;
//
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonProperty;
// import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//
// import de.jplag.JPlagComparison;
// import de.jplag.options.LanguageOption;
//
// @JsonDeserialize(as = ResultDescription.class)
// public record ResultDescription(@JsonIgnore LanguageOption languageOption,
// @JsonProperty("options") Options options,
// @JsonProperty("tests") Map<String, ExpectedResult> identifierToResultMap,
// @JsonIgnore JPlagComparison jplagComparison) {
//
// public ResultDescription(LanguageOption languageOption, Options options, Map<String, ExpectedResult>
// identifierToResultMap, JPlagComparison jplagComparison){
// this.identifierToResultMap = identifierToResultMap;
// this.options = options;
// this.languageOption = languageOption;
// this.jplagComparison = jplagComparison;
// }
//
// public ResultDescription(Options options, Map<String, ExpectedResult> identifierToResultMap)
// {
// this(null , options , identifierToResultMap , null);
// }
//
// @JsonIgnore
// public Map<String, ExpectedResult> getIdentifierResultMap()
// {
// return identifierToResultMap;
// }
// @JsonIgnore
// public ExpectedResult getExpectedResultByIdentifier(String identifier)
// {
// return identifierToResultMap.get(identifier);
// }
//
// @JsonIgnore
// public Options getOptions()
// {
// return options;
// }
// @JsonIgnore
// public LanguageOption getLanguageOption()
// {
// return languageOption;
// }
//
// public void putIdenfifierToResultMap(String identifier, ExpectedResult expectedResult) {
// identifierToResultMap.put(identifier, expectedResult);
// }
//
// @JsonDeserialize(as = Options.class)
// public record Options(@JsonProperty("minimum_token_match") int minimumTokenMatch)
// {
// public Options(int minimumTokenMatch)
// {
// this.minimumTokenMatch = minimumTokenMatch;
// }
//
// @JsonIgnore
// public int getMinimumTokenMatch() {
// return minimumTokenMatch;
// }
//
// @JsonIgnore
// @Override
// public boolean equals(Object options) {
// if (options instanceof Options) {
// return minimumTokenMatch == ((Options) options).getMinimumTokenMatch();
// } else {
// return false;
// }
//
// }
//
// @JsonIgnore
// @Override
// public String toString() {
// return "Options [minimumTokenMatch=" + minimumTokenMatch+ "]";
// }
// }
//
// @JsonDeserialize(as = ExpectedResult.class)
// public record ExpectedResult(@JsonProperty("minimal_similarity")float resultSimilarityMinimum,
// @JsonProperty("maximum_similarity") float resultSimilarityMaximum,
// @JsonProperty("matched_token_number") int resultMatchedTokenNumber,
// @JsonIgnore JPlagComparison jplagComparison)
// {
// public ExpectedResult(float resultSimilarityMinimum, float resultSimilarityMaximum,
// int resultMatchedTokenNumber , JPlagComparison jplagComparison)
// {
// this.resultSimilarityMinimum = resultSimilarityMaximum;
// this.resultSimilarityMaximum = resultSimilarityMaximum;
// this.resultMatchedTokenNumber = resultMatchedTokenNumber;
// this.jplagComparison = jplagComparison;
// }
//
// public ExpectedResult(JPlagComparison jplagComparison)
// {
// this(jplagComparison.minimalSimilarity() , jplagComparison.maximalSimilarity() ,
// jplagComparison.getNumberOfMatchedTokens());
// }
//
// public ExpectedResult(float resultSimilarityMinimum, float resultSimilarityMaximum,
// int resultMatchedTokenNumber) {
// this(resultSimilarityMinimum, resultSimilarityMaximum, resultMatchedTokenNumber, null);
// }
//
// @JsonIgnore
// public float getResultSimilarityMinimum() {
// return resultSimilarityMinimum;
// }
//
// @JsonIgnore
// public float getResultSimilarityMaximum() {
// return resultSimilarityMaximum;
// }
//
// @JsonIgnore
// public int getResultMatchedTokenNumber() {
// return resultMatchedTokenNumber;
// }
// }
// }