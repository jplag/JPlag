package de.jplag.end_to_end_testing.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.jplag.JPlagComparison;
import de.jplag.end_to_end_testing.helper.JPlagTestSuiteHelper;
import de.jplag.options.JPlagOptions;
import de.jplag.options.LanguageOption;

public class ResultDescription {
	@JsonIgnore
	private LanguageOption languageOption;
	@JsonProperty("options")
	Options options;
	@JsonProperty("tests")
	Map<String, ExpectedResult> identifierToResultMap;
	
	public ResultDescription(Options options, Map<String, ExpectedResult> identifierToResultMap)
	{
		this.options = options;
		this.identifierToResultMap = identifierToResultMap;
	}
	
	public ResultDescription(Options options , JPlagComparison jPlagComparison , LanguageOption languageOption)
	{
		this.languageOption = languageOption;
		this.options = options;
		identifierToResultMap = new HashMap<>();
		identifierToResultMap.put(JPlagTestSuiteHelper.getTestIdentifier(jPlagComparison), new ExpectedResult(jPlagComparison));
	}
	
	/**
	 * empty constructor in case the serialization contains an empty object to
	 * prevent throwing exceptions. this constructor was necessary for serialization
	 * with the Jackson parse extension
	 */
	public ResultDescription()
	{
		// For Serialization
	}
	
	@JsonIgnore
	public Map<String, ExpectedResult> getIdentifierResultMap()
	{
		return identifierToResultMap;
	}
	@JsonIgnore
	public ExpectedResult getExpectedResultByIdentifier(String identifier)
	{
		return identifierToResultMap.get(identifier);
	}
	
	@JsonIgnore
	public Options getOptions()
	{
		return options;		
	}
	@JsonIgnore
	public LanguageOption getLanguageOption()
	{
		return languageOption;
	}
	
	public void putIdenfifierToResultMap(String identifier, ExpectedResult expectedResult) {
		identifierToResultMap.put(identifier, expectedResult);
	}
	
}
